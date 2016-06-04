package lltan.jikexueyuan.com.lego.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import lltan.jikexueyuan.com.lego.Contants;
import lltan.jikexueyuan.com.lego.R;
import lltan.jikexueyuan.com.lego.WareDetailActivity;
import lltan.jikexueyuan.com.lego.adapter.HotWaresAdapter;
import lltan.jikexueyuan.com.lego.adapter.decoration.DividerItemDecoration;
import lltan.jikexueyuan.com.lego.bean.Page;
import lltan.jikexueyuan.com.lego.bean.Wares;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.http.SpotsCallBack;
import lltan.jikexueyuan.com.lego.utils.CartProvider;
import lltan.jikexueyuan.com.lego.utils.ToastUtils;
import okhttp3.Response;


/**
 * Created by legoer on 2016/3/27.
 */
public class HotFragment extends BaseFragment {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFRESH = 1;
    private static final int STATE_MORE = 2;
    private int state = STATE_NORMAL;
    private int totalPage = 1;

    private HotWaresAdapter hotWaresAdapter;

    @ViewInject(R.id.refresh_view)
    private MaterialRefreshLayout refreshView;
    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;


    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();
    private int curPage = 1;
    private int pageSize = 10;
    private List<Wares> mDatas;

    private CartProvider provider;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot,container,false);
    }

    @Override
    public void init() {
        initRefreshView();
        initRecyclerView();
        provider = new CartProvider(getContext());
    }

    private void initRefreshView() {
        if(refreshView != null){
            refreshView.setLoadMore(true);
            refreshView.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    refreshData();
                }

                @Override
                public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    if(curPage < totalPage){

                        refreshLoadMoreData();
                    }else {
                        materialRefreshLayout.finishRefreshLoadMore();
                    }
                }
            });
        }
    }

    private void refreshLoadMoreData() {
        if(curPage < totalPage){
            curPage = curPage + 1;
            state = STATE_MORE;
            initRecyclerView();
        }
    }

    private void refreshData(){

        curPage = 1;

        state = STATE_REFRESH;
        initRecyclerView();
    }

    private void initRecyclerView() {
        String url = Contants.API.WARES_HOT+"?curPage="+curPage+"&pageSize="+pageSize;
        okHttpHelper.getData(url, new SpotsCallBack<Page<Wares>>(this.getActivity()) {


            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {
                mDatas = waresPage.getList();
                curPage = waresPage.getCurrentPage();
                pageSize = waresPage.getPageSize();
                totalPage = waresPage.getTotalPage();
                initData();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });


    }

    private void initData() {
        switch (state){
            case STATE_NORMAL:
                initNormalData();
                break;
            case STATE_REFRESH:
                initRefreshData();
                break;
            case STATE_MORE:
                initRefreshMoreData();
                break;
        }


    }

    private void initNormalData(){
        hotWaresAdapter = new HotWaresAdapter(this.getActivity(),mDatas);
        hotWaresAdapter.setOnItemClickListener(new HotWaresAdapter.OnItemClickListener() {
            @Override
            public void onButtonClick(View view, Wares wares) {
                provider.put(wares);

                ToastUtils.show(getActivity(),"已经添加到购物车");
            }

            @Override
            public void onLayoutClick(View view, Wares wares) {
                Intent intent = new Intent(getActivity(), WareDetailActivity.class);

                intent.putExtra(Contants.WARE, wares);
                startActivity(intent);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setAdapter(hotWaresAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(),DividerItemDecoration.VERTICAL_LIST));
    }
    private void initRefreshData(){
        hotWaresAdapter.clearWares();
        hotWaresAdapter.addWares(mDatas);
        mRecyclerView.scrollToPosition(0);
        refreshView.finishRefresh();
        state = STATE_NORMAL;
    }

    private void initRefreshMoreData(){
        hotWaresAdapter.addWares(hotWaresAdapter.getDatas().size(),mDatas);
        mRecyclerView.scrollToPosition(hotWaresAdapter.getDatas().size());
        refreshView.finishRefreshLoadMore();
        state = STATE_NORMAL;
    }
}
