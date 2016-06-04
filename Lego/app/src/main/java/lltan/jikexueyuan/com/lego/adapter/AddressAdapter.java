package lltan.jikexueyuan.com.lego.adapter;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import lltan.jikexueyuan.com.lego.R;
import lltan.jikexueyuan.com.lego.bean.Address;


/**
 * Created by Administrator on 2016/5/21.
 */
public class AddressAdapter extends SimpleAdapter<Address> {

    private AddressListener listener;
    public AddressAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public AddressAdapter(Context context, List<Address> datas, AddressListener listener) {
        super(context, R.layout.template_address, datas);
        this.listener = listener;
    }

    @Override
    protected void convert(BaseViewHolder viewHoder, final Address item) {
        viewHoder.getTextView(R.id.txt_name).setText(item.getConsignee());
        viewHoder.getTextView(R.id.txt_phone).setText(replacePhoneNum(item.getPhone()));
        viewHoder.getTextView(R.id.txt_address).setText(item.getAddr());

        final CheckBox checkBox = viewHoder.getCheckBox(R.id.cb_is_defualt);

        final boolean isDefault = item.getIsDefault();
        checkBox.setChecked(isDefault);

        if(isDefault){
            checkBox.setText(R.string.default_address);
        }else {
            checkBox.setClickable(true);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked && listener != null){
                        item.setIsDefault(true);
                        listener.setDefault(item);
                    }
                }
            });
        }
    }

    private String replacePhoneNum(String phone) {
        return phone.substring(0,phone.length()-(phone.substring(3)).length())+"****"+phone.substring(7);
    }

    public interface AddressListener{
        public void setDefault(Address address);
    }
}
