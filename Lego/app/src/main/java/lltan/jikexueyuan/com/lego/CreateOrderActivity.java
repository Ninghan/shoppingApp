package lltan.jikexueyuan.com.lego;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pingplusplus.android.PaymentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lltan.jikexueyuan.com.lego.adapter.WareOrderAdapter;
import lltan.jikexueyuan.com.lego.adapter.decoration.DividerItemDecoration;
import lltan.jikexueyuan.com.lego.adapter.layoutmanager.FullyLinearLayoutManager;
import lltan.jikexueyuan.com.lego.bean.Charge;
import lltan.jikexueyuan.com.lego.bean.Page;
import lltan.jikexueyuan.com.lego.bean.ShoppingCart;
import lltan.jikexueyuan.com.lego.bean.Wares;
import lltan.jikexueyuan.com.lego.http.BaseCallback;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.http.SpotsCallBack;
import lltan.jikexueyuan.com.lego.msg.BaseRespMsg;
import lltan.jikexueyuan.com.lego.msg.CreateOrderRespMsg;
import lltan.jikexueyuan.com.lego.utils.CartProvider;
import lltan.jikexueyuan.com.lego.utils.JSONUtil;
import lltan.jikexueyuan.com.lego.widget.EasyGoToolBar;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by legoer on 2016/5/25.
 */
public class CreateOrderActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 银联支付渠道
     */
    private static final String CHANNEL_UPACP = "upacp";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 百度支付渠道
     */
    private static final String CHANNEL_BFB = "bfb";
    /**
     * 京东支付渠道
     */
    private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";

    @ViewInject(R.id.toolbar)
    private EasyGoToolBar mToolbar;

    @ViewInject(R.id.txt_name)
    private TextView mName;

    @ViewInject(R.id.txt_address)
    private TextView mAddress;


    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;


    @ViewInject(R.id.rl_alipay)
    private RelativeLayout mLayoutAlipay;

    @ViewInject(R.id.rl_wechat)
    private RelativeLayout mLayoutWechat;

    @ViewInject(R.id.rl_bd)
    private RelativeLayout mLayoutBd;


    @ViewInject(R.id.rb_alipay)
    private RadioButton mRbAlipay;

    @ViewInject(R.id.rb_webchat)
    private RadioButton mRbWechat;

    @ViewInject(R.id.rb_bd)
    private RadioButton mRbBd;

    @ViewInject(R.id.btn_createOrder)
    private Button mBtnCreateOrder;

    @ViewInject(R.id.txt_total)
    private TextView mTxtTotal;


    private CartProvider cartProvider;

    private WareOrderAdapter mAdapter;

    private int curPage = 1;
    private int pageSize = 10;
    private List<Wares> mDatas;



    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();

    private String orderNum;
    private String payChannel = CHANNEL_ALIPAY;
    private float amount = 0;


    private HashMap<String,RadioButton> channels = new HashMap<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        ViewUtils.inject(this);

        cartProvider = new CartProvider(this);
        getWareDatas();
        initToolbar();

        init();
    }

    private void showData() {

        mAdapter = new WareOrderAdapter(this,cartProvider.getAll());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new FullyLinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));


        amount = mAdapter.getTotalPrice();
        if(amount > 0){
            mTxtTotal.setText("合计： ￥"+ amount);
        }


    }

    private void getWareDatas() {
        String url = Contants.API.WARES_HOT+"?curPage="+curPage+"&pageSize="+pageSize;
        okHttpHelper.getData(url, new BaseCallback<Page<Wares>>() {


            @Override
            public void onBeforeRequest(Request request) {

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {
                mDatas = waresPage.getList();
                curPage = waresPage.getCurrentPage();
                pageSize = waresPage.getPageSize();
                getCartDatas();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });

    }

    private void getCartDatas() {
        if(mDatas != null){
            for(Wares data:mDatas){
//                cartProvider.put(data);

            }
        }
        showData();

    }

    private void initToolbar() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        channels.put(CHANNEL_ALIPAY,mRbAlipay);
        channels.put(CHANNEL_WECHAT,mRbWechat);
        channels.put(CHANNEL_BFB,mRbBd);

        mLayoutAlipay.setOnClickListener(this);
        mLayoutWechat.setOnClickListener(this);
        mLayoutBd.setOnClickListener(this);
        mRbAlipay.setChecked(true);
        mRbWechat.setChecked(false);
        mRbBd.setChecked(false);
    }


    @Override
    public void onClick(View v) {
        selectPayChannel(v.getTag().toString());
    }

    /**
     *
     * @param payChannel 是支付方式
     */
    private void selectPayChannel(String payChannel) {
        this.payChannel = payChannel;
        for(String key:channels.keySet()){
            RadioButton radioButton = channels.get(key);
            if(key.equals(payChannel)){
                Boolean isChecked = radioButton.isChecked();
                radioButton.setChecked(! isChecked);
            }else {
                radioButton.setChecked(false);
            }
        }
    }

    @OnClick(R.id.btn_createOrder)
    public void createNewOrder(View view){
        postNewOrder();
    }

    private void postNewOrder() {
        final List<ShoppingCart> carts = mAdapter.getDatas();
        List<WareItem> wareItems = new ArrayList<>(carts.size());
        for(ShoppingCart cart:carts){
            WareItem wareItem = new WareItem(cart.getId(),cart.getPrice().intValue());
            wareItems.add(wareItem);
        }

        String item_json = JSONUtil.toJSON(wareItems);

        Map<String,Object> params = new HashMap<>(5);
        params.put("user_id",EasyGoApplication.getInstance().getUser());
        params.put("item_json",item_json);
        params.put("pay_channel",payChannel);
        params.put("amount",(int)amount+"");
        params.put("addr_id",1+"");

        mBtnCreateOrder.setEnabled(true);

        okHttpHelper.postData(Contants.API.ORDER_CREATE, params, new SpotsCallBack<CreateOrderRespMsg>(this) {
            @Override
            public void onSuccess(Response response, CreateOrderRespMsg createOrderRespMsg) {
                mBtnCreateOrder.setEnabled(true);
                orderNum = createOrderRespMsg.getData().getOrderNum();
                Charge charge = createOrderRespMsg.getData().getCharge();

                openPaymentActivity(JSONUtil.toJSON(charge));
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                mBtnCreateOrder.setEnabled(true);
            }
        });
    }

    private void openPaymentActivity(String charge) {
        Intent intent = new Intent();
        String packageName = getPackageName();
        ComponentName componentName = new ComponentName(packageName,packageName + ".wxapi.WXPayEntryActivity");
        intent.setComponent(componentName);
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
        startActivityForResult(intent, Contants.REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Contants.REQUEST_CODE_PAYMENT){
            if(resultCode == Activity.RESULT_OK){
                String result = data.getExtras().getString("pay_result");

                if (result.equals("success"))
                    changeOrderStatus(1);
                else if (result.equals("fail"))
                    changeOrderStatus(-1);
                else if (result.equals("cancel"))
                    changeOrderStatus(-2);
                else
                    changeOrderStatus(0);
            }
        }
    }

    private void changeOrderStatus(final int status) {
        Map<String ,Object> params = new HashMap<>(5);
        params.put("order_num",orderNum);
        params.put("status",status+"");


        okHttpHelper.postData(Contants.API.ORDER_COMPLEPE, params, new SpotsCallBack<BaseRespMsg>(this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg o) {

                toPayResultActivity(status);
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                toPayResultActivity(-1);
            }
        });
    }

    private void toPayResultActivity(int status) {
        Intent intent = new Intent(this,PayResultActivity.class);
        intent.putExtra("status", status);

        startActivity(intent);
        this.finish();
    }

    @OnClick(R.id.rl_addr)
    public void addAddress(View view){
        Intent intent = new Intent(this,AddressListActivity.class);
        startActivity(intent);
        this.finish();
    }

    class WareItem{
        public Long wareId;
        public int amount;

        public WareItem(Long wareId, int amount) {
            this.wareId = wareId;
            this.amount = amount;
        }

        public Long getWareId() {
            return wareId;
        }

        public void setWareId(Long wareId) {
            this.wareId = wareId;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
