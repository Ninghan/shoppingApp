package lltan.jikexueyuan.com.lego.adapter;

import android.content.Context;
import android.net.Uri;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import lltan.jikexueyuan.com.lego.R;
import lltan.jikexueyuan.com.lego.bean.ShoppingCart;


/**
 * Created by legoer on 2016/5/14.
 */
public class WareOrderAdapter extends SimpleAdapter<ShoppingCart> {

    public WareOrderAdapter(Context context, List<ShoppingCart> datas) {
        super(context, R.layout.template_order_wares, datas);

    }

    @Override
    protected void convert(BaseViewHolder viewHoder, ShoppingCart item) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) viewHoder.getView(R.id.drawee_view);
        TextView textTitle = viewHoder.getTextView(R.id.text_title);
        TextView textPrice = viewHoder.getTextView(R.id.text_price);
        TextView textCount = viewHoder.getTextView(R.id.text_count);

        simpleDraweeView.setImageURI(Uri.parse(item.getImgUrl()));
        textTitle.setText(item.getName());
        textPrice.setText("￥" + item.getPrice());
        textCount.setText("×" + item.getCount());
    }

    public float getTotalPrice() {
        float sum = 0;
        if (!isNull())
            return sum;

        for (ShoppingCart cart :
                datas) {

            sum += cart.getCount() * cart.getPrice();
        }

        return sum;
    }

    private boolean isNull() {

        return (datas != null && datas.size() > 0);
    }

}
