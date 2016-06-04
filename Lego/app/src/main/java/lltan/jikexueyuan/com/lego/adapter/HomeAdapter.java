package lltan.jikexueyuan.com.lego.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lltan.jikexueyuan.com.lego.R;

/**
 * Created by legoer on 2016/3/30.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{

    private List<HomeCampaign> mdata;
    private Context mContext;

    private static final String TAG = "HomeAdapter";
    private static int VIEW_TYPE_R = 0;
    private static int VIEW_TYPE_L = 1;
    private static int VIEW_TYPE_X = 2;
    private List<Integer> imgViewList = new ArrayList<Integer>();

    public HomeAdapter(List<HomeCampaign> datas, Context context) {
        this.mdata = datas;
        this.mContext = context;
    }

    /**
     * Called when a view has been clicked.
     *
     */

    public interface OnItemClickListener{
        public void onItemClick(View view, Campaign campaign);

    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        MyViewHolder myViewHolder;
        View views;
        if(getItemViewType(i) == VIEW_TYPE_R){
            views = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home,parent,false);
            imgViewList.clear();

            imgViewList.add(R.id.imgview_big);
            imgViewList.add(R.id.imgview_small_top);
            imgViewList.add(R.id.imgview_small_bottom);
        } else if (getItemViewType(i) == VIEW_TYPE_X){
            views = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_r,parent,false);

            imgViewList.clear();
            imgViewList.add(R.id.imgview_big);
            imgViewList.add(R.id.imgview_small_bottom);
            imgViewList.add(R.id.imgview_small_top);
            imgViewList.add(R.id.imgView_right_bottom);

        }else {
            views = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_l,parent,false);

            imgViewList.clear();
            imgViewList.add(R.id.imgview_big);
            imgViewList.add(R.id.imgview_small_top);
            imgViewList.add(R.id.imgview_small_bottom);
        }

        myViewHolder = new MyViewHolder(views,imgViewList);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder mViewHolder, int i) {
        HomeCampaign homeCampaign = mdata.get(i);

        mViewHolder.textView.setText(homeCampaign.getTitle());

        for(int j = 0;j < mViewHolder.imgViews.size();j++){
            Picasso.with(mContext).load(homeCampaign.getCp(j).getImgUrl()).into(mViewHolder.imgViews.get(j));
        }
//        mViewHolder.imgViews.clear();
//        Picasso.with(mContext).load(homeCampaign.getCpTwo().getImgUrl()).into(mViewHolder.imgViews.get(1));
//        Picasso.with(mContext).load(homeCampaign.getCpThree().getImgUrl()).into(mViewHolder.imgViews.get(2));
//        Picasso.with(mContext).load(homeCampaign.getCpThree().getImgUrl()).into(mViewHolder.imgViews.get(0));

    }


    @Override
    public int getItemCount() {
        return mdata.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position % 3 == 0){
            return VIEW_TYPE_R;
        }else if(position % 3 == 1){
            return VIEW_TYPE_L;
        }else{
            return VIEW_TYPE_X;
        }
    }

    public void removeItem(int position){
        mdata.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        private List<ImageView> imgViews = new ArrayList<ImageView>();

        public MyViewHolder(View itemView,List<Integer> imgLists) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.text_title);

            imgViews.clear();
            for(int imgList:imgLists ){
                ImageView imageView = (ImageView) itemView.findViewById(imgList);
                imageView.setOnClickListener(this);
                imgViews.add(imageView);
            }
        }

        @Override
        public void onClick(View v) {
            HomeCampaign homeCampaign = mdata.get(getLayoutPosition());
            if(mOnItemClickListener != null){
                switch (v.getId()){
                    case R.id.imgview_big:
                        mOnItemClickListener.onItemClick(v,homeCampaign.getCpOne());
                        break;
                    case R.id.imgview_small_top:
                        mOnItemClickListener.onItemClick(v,homeCampaign.getCpTwo());
                        break;
                    case R.id.imgview_small_bottom:
                        mOnItemClickListener.onItemClick(v,homeCampaign.getCpThree());
                        break;
                }
            }

        }
    }


}
