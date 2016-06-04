package lltan.jikexueyuan.com.lego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lltan.jikexueyuan.com.lego.adapter.AddressAdapter;
import lltan.jikexueyuan.com.lego.adapter.decoration.DividerItemDecoration;
import lltan.jikexueyuan.com.lego.bean.Address;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.http.SpotsCallBack;
import lltan.jikexueyuan.com.lego.msg.BaseRespMsg;
import lltan.jikexueyuan.com.lego.widget.EasyGoToolBar;
import okhttp3.Response;

/**
 * Created by legoer on 2016/5/21.
 */
public class AddressListActivity extends BaseActivity {

    @ViewInject(R.id.toolbar)
    private EasyGoToolBar mToolbar;

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.btn_addAddress)
    private Button mAddAddress;

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();

    private AddressAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ViewUtils.inject(this);

        initToolbar();
    }

    private void initToolbar() {

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mToolbar.setRightImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddActivity();
            }
        });
    }

    private void toAddActivity() {
        Intent intent = new Intent(AddressListActivity.this,AddressAddActivity.class);
        startActivityForResult(intent, Contants.REQUEST_CODE);
    }

    @OnClick(R.id.btn_addAddress)
    public void addNewAddress(View view){
        toAddActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initAddress();
    }

    private void initAddress() {
        Map<String,Object> params = new HashMap<>(1);
        params.put("user_id",EasyGoApplication.getInstance().getUser());
        okHttpHelper.getData(Contants.API.ADDRESS_LIST, params, new SpotsCallBack<List<Address>>(this) {
            @Override
            public void onSuccess(Response response, List<Address> addresses) {
                showAddressList(addresses);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

    private void showAddressList(List<Address> addresses) {
        Collections.sort(addresses);
        if(mAdapter == null){
            mAdapter = new AddressAdapter(this, addresses, new AddressAdapter.AddressListener() {
                @Override
                public void setDefault(Address address) {
                    updateAddress(address);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        }else {
            mAdapter.refreshData(addresses);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void updateAddress(Address address) {
        Map<String,Object> params = new HashMap<>(1);
        params.put("id",address.getId());
        params.put("consignee",address.getConsignee());
        params.put("phone",address.getPhone());
        params.put("addr",address.getAddr());
        params.put("zip_code",address.getZipCode());
        params.put("is_default",address.getIsDefault());

        okHttpHelper.postData(Contants.API.ADDRESS_UPDATE, params, new SpotsCallBack<BaseRespMsg>(this) {

            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg.getStatus() == BaseRespMsg.STATUS_SUCCESS) {

                    initAddress();
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }
}
