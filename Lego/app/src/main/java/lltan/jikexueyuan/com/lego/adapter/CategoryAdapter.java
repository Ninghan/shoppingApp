package lltan.jikexueyuan.com.lego.adapter;

import android.content.Context;

import java.util.List;

import lltan.jikexueyuan.com.lego.R;
import lltan.jikexueyuan.com.lego.bean.Category;


/**
 * Created by legoer
 */
public class CategoryAdapter extends SimpleAdapter<Category> {


    public CategoryAdapter(Context context, List<Category> datas) {
        super(context, R.layout.template_single_text, datas);
    }

    @Override
    protected void convert(BaseViewHolder viewHoder, Category item) {


        viewHoder.getTextView(R.id.textView).setText(item.getName());
//        viewHoder.getButton(R.id.btn_select).setText(item.getName());

    }
}
