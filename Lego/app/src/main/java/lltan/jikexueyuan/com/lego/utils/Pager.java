package lltan.jikexueyuan.com.lego.utils;

import android.content.Context;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lltan.jikexueyuan.com.lego.bean.Page;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.http.SimpleCallback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by legoer on 2016/5/4.
 */
public class Pager {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFRESH = 1;
    private static final int STATE_MORE = 2;

    private int state = STATE_NORMAL;

    private static Builder builder;
    private OkHttpHelper httpHelper;

    public Pager() {
        httpHelper = OkHttpHelper.getInstance();
        initRefreshLayout();
    }

    public  static Builder newBuilder(){
        builder = new Builder();
        return builder;
    }


    public void request(){

        requestData();
    }

    public void  putParam(String key,Object value){
        builder.params.put(key,value);

    }


    private void initRefreshLayout() {
        builder.mRefresh.setLoadMore(builder.canLoadMore);
        builder.mRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (builder.pageIndex < builder.pageSize) {
                    loadMore();
                } else {
                    Toast.makeText(builder.mContext, "没有更多数据", Toast.LENGTH_LONG).show();
                    materialRefreshLayout.finishRefreshLoadMore();
                    materialRefreshLayout.setLoadMore(false);
                }
            }
        });
    }

    private void loadMore() {
        builder.pageIndex += 1;
        state = STATE_MORE;
        requestData();
    }

    private void refresh() {
        builder.pageIndex = 1;
        state = STATE_REFRESH;
        requestData();
    }

    private void requestData() {
        String url = buildUrl();

        httpHelper.getData(url, new RequestCallBack(builder.mContext));
    }

    private String buildUrl() {
        return builder.mUrl +"?"+buildUrlParams();
    }

    private String buildUrlParams() {
        HashMap<String, Object> map = builder.params;

        map.put("curPage",builder.pageIndex);
        map.put("pageSize",builder.pageSize);

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0,s.length()-1);
        }
        return s;
    }

    private <T> void showData(List<T> datas, int totalPage, int totalCount) {
        if(datas ==null|| datas.size()<=0){
            Toast.makeText(builder.mContext,"加载不到数据",Toast.LENGTH_LONG).show();
            return;
        }

        if(STATE_NORMAL==state){

            if(builder.mOnPageListener !=null){
                builder.mOnPageListener.load(datas,totalPage,totalCount);
            }
        }

        else  if(STATE_REFRESH==state)   {
            builder.mRefresh.finishRefresh();
            if(builder.mOnPageListener !=null){
                builder.mOnPageListener.refresh(datas,totalPage,totalCount);
            }

        }
        else  if(STATE_MORE == state){

            builder.mRefresh.finishRefreshLoadMore();
            if(builder.mOnPageListener !=null){
                builder.mOnPageListener.loadMore(datas,totalPage,totalCount);
            }

        }
    }

    class  RequestCallBack<T> extends SimpleCallback<Page<T>> {

        public RequestCallBack(Context context) {
            super(context);

            super.mType = builder.mType;
        }

        @Override
        public void onFailure(Request request, Exception e) {

//            dismissDialog();
            Toast.makeText(builder.mContext,"请求出错："+e.getMessage(),Toast.LENGTH_LONG).show();

            if(STATE_REFRESH==state)   {
                builder.mRefresh.finishRefresh();
            }
            else  if(STATE_MORE == state){

                builder.mRefresh.finishRefreshLoadMore();
            }
        }

        @Override
        public void onSuccess(Response response, Page<T> page) {


            builder.pageIndex = page.getCurrentPage();
            builder.pageSize = page.getPageSize();
            builder.totalPage = page.getTotalPage();
            showData(page.getList(), page.getTotalPage(), page.getTotalCount());
        }

        @Override
        public void onError(Response response, int code, Exception e) {

            Toast.makeText(builder.mContext,"加载数据失败",Toast.LENGTH_LONG).show();

            if(STATE_REFRESH == state)   {
                builder.mRefresh.finishRefresh();
            }
            else  if(STATE_MORE == state){
                builder.mRefresh.finishRefreshLoadMore();
            }
        }

    }


    public interface OnPageListener<T>{
        void load(List<T> datas, int totalPage, int totalCount);

        void refresh(List<T> datas, int totalPage, int totalCount);

        void loadMore(List<T> datas, int totalPage, int totalCount);
    }

    public static class Builder {

        private MaterialRefreshLayout mRefresh;
        private Boolean canLoadMore;
        private Context mContext;
        private Type mType;
        private String mUrl;
        private OnPageListener mOnPageListener;

        private int totalPage = 1;
        private int pageIndex = 1;
        private int pageSize = 10;

        private HashMap<String,Object> params = new HashMap<>(5);

        public Pager build(Context context, Type type){

            this.mType = type;
            this.mContext =context;

            valid();
            return new Pager();

        }


        private void valid(){


            if(this.mContext==null)
                throw  new RuntimeException("content can't be null");

            if(this.mUrl==null || "".equals(this.mUrl))
                throw  new RuntimeException("url can't be  null");

            if(this.mRefresh==null)
                throw  new RuntimeException("MaterialRefreshLayout can't be  null");
        }


        public Builder setRefresh(MaterialRefreshLayout mRefresh) {
            this.mRefresh = mRefresh;
            return builder;
        }

        public Builder setLoadMore(Boolean loadMore) {
            this.canLoadMore = loadMore;
            return builder;
        }

        public Builder setUrl(String mUrl) {
            builder.mUrl = mUrl;
            return builder;
        }

        public Builder setOnPageListener(OnPageListener mOnPageListener) {
            this.mOnPageListener = mOnPageListener;
            return builder;
        }

        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return builder;
        }

        public Builder setParams(String key,Object value) {
            params.put(key,value);
            return builder;
        }
    }
}
