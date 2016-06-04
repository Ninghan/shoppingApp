package lltan.jikexueyuan.com.lego.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import lltan.jikexueyuan.com.lego.R;


/**
 * Created by Administrator on 2016/4/23.
 */
public class NumberAddSub extends LinearLayout implements View.OnClickListener {

    public static final int DEFUALT_MAX=1000;

    private LayoutInflater mInflater;
    private TextView mTxtNumber;
    private Button mNumAdd;
    private Button mNumSub;

    private  int value;
    private int minValue;
    private int maxValue=DEFUALT_MAX;

    private OnButtonClickListener onButtonClickListener;

    public NumberAddSub(Context context) {
        this(context, null);
    }

    public NumberAddSub(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberAddSub(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberAddSub, defStyleAttr,0);
        int value = a.getInt(R.styleable.NumberAddSub_value,0);
        setValue(value);
        int maxValue = a.getInt(R.styleable.NumberAddSub_maxValue,0);
        if(maxValue != 0){
            setMaxValue(maxValue);
        }
        int minValue = a.getInt(R.styleable.NumberAddSub_minValue, 0);
        if(minValue != 0){
            setMinValue(minValue);
        }

        Drawable editBackground = a.getDrawable(R.styleable.NumberAddSub_editBackground);
        if(editBackground != null){
            setEditBackground(editBackground);
        }
        Drawable btnAddBackground = a.getDrawable(R.styleable.NumberAddSub_buttonAddBackgroud);
        if(btnAddBackground != null){
            setBtnAddBackground(btnAddBackground);
        }
        Drawable btnSubBackground = a.getDrawable(R.styleable.NumberAddSub_buttonSubBackgroud);
        if(btnAddBackground != null){
            setBtnSubBackground(btnSubBackground);
        }

        a.recycle();

    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.number_add_sub, this, true);
        mNumAdd = (Button) view.findViewById(R.id.btn_add);
        mNumSub = (Button) view.findViewById(R.id.btn_sub);
        mTxtNumber = (TextView) view.findViewById(R.id.etxt_num);

        mTxtNumber.setInputType(InputType.TYPE_NULL);
        mTxtNumber.setKeyListener(null);
        mNumAdd.setOnClickListener(this);
        mNumSub.setOnClickListener(this);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                numAdd();
                if(onButtonClickListener != null){
                    onButtonClickListener.onButtonClick(v,this.value);
                }
                break;
            case R.id.btn_sub:
                numSub();
                if(onButtonClickListener != null){
                    onButtonClickListener.onButtonClick(v, this.value);
                }
                break;
        }

    }

    private void numSub() {
        getValue();
        if(this.value > this.minValue){

            this.value = this.value - 1;
        }
        mTxtNumber.setText(" " + this.value);
    }

    private void numAdd() {
        getValue();
        if (this.value < this.maxValue) {

            this.value = this.value + 1;
        }
        mTxtNumber.setText(" " + this.value);
    }


    public void setValue(int value) {
        mTxtNumber.setTextColor(getResources().getColor(R.color.black));
        mTxtNumber.setText(" " + value);
        this.value = value;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBtnSubBackground(Drawable btnSubBackground) {
        mNumSub.setBackground(btnSubBackground);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBtnAddBackground(Drawable btnAddBackground) {
        mNumAdd.setBackground(btnAddBackground);
    }

    public void setEditTextBackground(int drawableId){

        setEditBackground(getResources().getDrawable(drawableId));

    }

    public void setEditBackground(Drawable editBackground) {
        mTxtNumber.setBackground(editBackground);
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener){
        this.onButtonClickListener = onButtonClickListener;
    }

    public int getValue() {
        String value = mTxtNumber.getText().toString();
        if(value != null && value.equals(" ")){
            this.value = Integer.parseInt(value);
        }

        return this.value;
    }

    public interface OnButtonClickListener{
        public void onButtonClick(View view, int value);
    }

}
