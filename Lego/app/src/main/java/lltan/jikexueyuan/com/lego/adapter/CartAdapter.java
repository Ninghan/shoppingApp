package lltan.jikexueyuan.com.lego.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Iterator;
import java.util.List;

import lltan.jikexueyuan.com.lego.R;
import lltan.jikexueyuan.com.lego.bean.ShoppingCart;
import lltan.jikexueyuan.com.lego.utils.CartProvider;
import lltan.jikexueyuan.com.lego.widget.NumberAddSub;

/**
 * Created by legoer on 2016/4/23.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private List<ShoppingCart> mDatas;
    private LayoutInflater mInflater;
    private Context mContext;
    private View mView;
    private CartProvider cartProvider;
    private TextView mTextTotalPrice;

    private OnItemClickListener mOnItemClickListener;

    public CartAdapter(List<ShoppingCart> mDatas, Context mContext, TextView textView) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        this.cartProvider = new CartProvider(mContext);
        this.mTextTotalPrice = textView;
        showTotalPrice();
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(mContext);
        mView = mInflater.inflate(R.layout.template_cart,parent,false);
        return new CartViewHolder(mView);
    }


    @Override
    public void onBindViewHolder(final CartViewHolder holder, int position) {
        final ShoppingCart data = mDatas.get(position);
//        cartProvider.update(data);
        holder.mSimpleDraweeView.setImageURI(Uri.parse(data.getImgUrl()));
        holder.mTitle.setTextColor(mContext.getResources().getColor(R.color.gray));
        holder.mPrice.setTextColor(mContext.getResources().getColor(R.color.colorPrice));
        holder.mTitle.setText(data.getName());
        holder.mPrice.setText("￥" + data.getPrice().toString());
        if(data.isNumberAddSubVisible()){
            showNumberAddSub(holder);
        }else {
            hideNumberAddSub(holder);
        }
        holder.mNumberAddSub.setValue(data.getCount());
        holder.mTextCount.setText("×" + data.getCount());
        holder.mCheckBox.setChecked(data.isChecked());
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v.findViewById(v.getId());
                if(checkBox.isChecked()){
                    data.setIsChecked(true);
                }else {
                    data.setIsChecked(false);
                }
                showTotalPrice();
            }
        });



        holder.mNumberAddSub.setOnButtonClickListener(new NumberAddSub.OnButtonClickListener() {
            @Override
            public void onButtonClick(View view, int value) {
                data.setCount(value);
                boolean b = data.isChecked();
                ShoppingCart temp = data;
                temp.setIsNumberAddSubVisible(false);
                temp.setIsChecked(false);
                cartProvider.update(temp);
                data.setIsChecked(b);
                showTotalPrice();
            }
        });

        showTotalPrice();
    }

    @Override
    public int getItemCount() {
        if(mDatas != null){

            return mDatas.size();
        }else {
            return 0;
        }
    }

    public void showTotalPrice() {
        float sum = 0;
        if(mDatas != null && mDatas.size() > 0){
            for(ShoppingCart data : mDatas){
                if(data.isChecked()){
                    sum += data.getTotalPrice();
                }
            }
        }
        mTextTotalPrice.setText(Html.fromHtml("<font color='#ffffff'>合计</font> <font color='#eb4f38'>￥" + sum + "</font>"), TextView.BufferType.SPANNABLE);

    }


    public void setCheckAll(Boolean isChecked){
        if(mDatas != null && mDatas.size() > 0){
            int i=0;
            for (ShoppingCart cart :mDatas){
                cart.setIsChecked(isChecked);
                notifyItemChanged(i);
                i++;
            }

            showTotalPrice();
        }
    }

    public void delCart(){
        if(mDatas != null && mDatas.size() > 0){
            Boolean isChecked = false;
            for(Iterator iterator = mDatas.iterator();iterator.hasNext();){
                ShoppingCart cart = (ShoppingCart) iterator.next();
                if(cart.isChecked()){
                    int position = mDatas.indexOf(cart);
                    cartProvider.delete(cart);
                    iterator.remove();
                    notifyItemRemoved(position);
                    isChecked = true;
                }
            }
            if(!isChecked){
                Toast.makeText(mContext,"您还没有选择宝贝哦！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void clear(){
//        int itemCount = datas.size();
//        datas.clear();
//        this.notifyItemRangeRemoved(0,itemCount);

        if(mDatas==null || mDatas.size()<=0)
            return;

        for (Iterator it=mDatas.iterator();it.hasNext();){

            ShoppingCart t = (ShoppingCart) it.next();
            int position = mDatas.indexOf(t);
            it.remove();
            notifyItemRemoved(position);
        }
    }

    public void addData(List<ShoppingCart> datas){

        addData(0,datas);
    }

    public void addData(int position,List<ShoppingCart> list){

        if(list !=null && list.size()>0) {

            for (ShoppingCart t:list) {
                mDatas.add(position, t);
                notifyItemInserted(position);
            }

        }
    }

    /*
    ** 设置NumberAddSub控件可见与隐藏
     */
    public void setNumberAddSubVisible(Boolean isVisible){
        if(mDatas != null && mDatas.size() > 0){
            int i=0;
            for (ShoppingCart cart :mDatas){
                cart.setIsNumberAddSubVisible(isVisible);
                notifyItemChanged(i);
                i++;
            }
            showTotalPrice();
        }
    }

    private void hideNumberAddSub(CartViewHolder holder) {
        holder.mNumberAddSub.setVisibility(View.GONE);
        holder.mTextCount.setVisibility(View.VISIBLE);
    }

    private void showNumberAddSub(CartViewHolder holder) {
        holder.mNumberAddSub.setVisibility(View.VISIBLE);
        holder.mTextCount.setVisibility(View.GONE);
    }

    class CartViewHolder extends RecyclerView.ViewHolder{

        private CheckBox mCheckBox;
        private SimpleDraweeView mSimpleDraweeView;
        private TextView mTitle;
        private TextView mPrice;
        private NumberAddSub mNumberAddSub;
        private TextView mTextCount;

        public CartViewHolder(View itemView) {
            super(itemView);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.drawee_view);
            mTitle = (TextView) itemView.findViewById(R.id.text_title);
            mPrice = (TextView) itemView.findViewById(R.id.text_price);
            mNumberAddSub = (NumberAddSub) itemView.findViewById(R.id.num_control);
            mTextCount = (TextView) itemView.findViewById(R.id.text_count);

        }

    }

    interface OnItemClickListener{
        public void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }


}
