package lltan.jikexueyuan.com.lego.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import lltan.jikexueyuan.com.lego.R;
import lltan.jikexueyuan.com.lego.bean.Wares;

/**
 * Created by legoer on 2016/4/19.
 */
public class HotWaresAdapter extends RecyclerView.Adapter<HotWaresAdapter.HotViewHolder> {
    private List<Wares> mWares;
    private LayoutInflater mInflater;
    private Context mContext;

    public HotWaresAdapter(Context context, List<Wares> wares) {
        this.mWares = wares;
        this.mContext = context;
    }

    public interface OnItemClickListener{
        public void onButtonClick(View view, Wares wares);
        public void onLayoutClick(View view, Wares wares);

    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public HotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.template_hot_wares,null,false);
        return new HotViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final HotViewHolder holder, int position) {
        Wares ware = mWares.get(position);
        holder.simpleDraweeView.setImageURI(Uri.parse(ware.getImgUrl()));
        holder.textTitle.setTextColor(mContext.getResources().getColor(R.color.gray));
        holder.textPrice.setTextColor(mContext.getResources().getColor(R.color.crimson));
        holder.textTitle.setText(ware.getName());
        holder.textPrice.setText("￥" + ware.getPrice());
    }

    @Override
    public int getItemCount() {
        return mWares.size();
    }

    public List<Wares> getDatas(){
        return mWares;
    }

    public Wares getItem(int position) {
        if (position >= mWares.size()) return null;
        return mWares.get(position);
    }

    public void addWares(List<Wares> datas){
       addWares(0,datas);
    }
    public void addWares(int position, List<Wares> datas){
        if(datas != null && datas.size() > 0){

            mWares.addAll(datas);
            notifyItemRangeChanged(position,mWares.size());
        }
    }

    public void clearWares(){
        mWares.clear();
        notifyItemRangeRemoved(0,mWares.size());
    }

    class HotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SimpleDraweeView simpleDraweeView;
        TextView textTitle;
        TextView textPrice;
        Button btnBuy;
        RelativeLayout mWareLayout;

        public HotViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.drawee_view);
            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            textPrice = (TextView) itemView.findViewById(R.id.textPrice);
            btnBuy = (Button) itemView.findViewById(R.id.btn_add);
            mWareLayout = (RelativeLayout) itemView.findViewById(R.id.ware_layout);

            btnBuy.setOnClickListener(this);
            mWareLayout.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            Wares ware = mWares.get(getLayoutPosition());
            switch (v.getId()){
                case R.id.btn_add:
                    if(mOnItemClickListener != null){
                        mOnItemClickListener.onButtonClick(v, ware);
                    }
                    break;
                case R.id.ware_layout:
                    if(mOnItemClickListener != null){
                        mOnItemClickListener.onLayoutClick(v, ware);
                    }
                    break;
                default:
                    break;

            }
        }
    }
}
