package lltan.jikexueyuan.com.lego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

import lltan.jikexueyuan.com.lego.bean.User;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.http.SpotsCallBack;
import lltan.jikexueyuan.com.lego.msg.LoginRespMsg;
import lltan.jikexueyuan.com.lego.utils.DESUtil;
import lltan.jikexueyuan.com.lego.utils.ToastUtils;
import lltan.jikexueyuan.com.lego.widget.ClearEditText;
import lltan.jikexueyuan.com.lego.widget.EasyGoToolBar;
import okhttp3.Response;

/**
 * Created by legoer on 2016/5/26.
 */
public class LoginActivity extends AppCompatActivity {
    private static final int STATE_EMPTY = 0;
    private static final int STATE_HASVALUE = 1;

    @ViewInject(R.id.toolbar)
    private EasyGoToolBar mToolbar;
    @ViewInject(R.id.etxt_phone)
    private ClearEditText mPhone;
    @ViewInject(R.id.etxt_pwd)
    private ClearEditText mPwd;
    @ViewInject(R.id.btn_login)
    private Button mLogin;
    @ViewInject(R.id.txt_toReg)
    private TextView mToReg;
    @ViewInject(R.id.txt_toGetPwd)
    private TextView mToGetPwd;

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);

        initToolbar();
        mPhone.setTag(STATE_EMPTY);
        mPwd.setTag(STATE_EMPTY);

        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPhone.getText().length() > 0) {
                    mPhone.setTag(STATE_HASVALUE);
                } else {
                    mPhone.setTag(STATE_EMPTY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int state1 = (int) mPhone.getTag();
                int state2 = (int) mPwd.getTag();
                if (state1 == STATE_HASVALUE && state2 == STATE_HASVALUE) {
                    mLogin.setEnabled(true);
                } else {
                    mLogin.setEnabled(false);
                }
            }
        });

        mPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mPwd.getText().length() > 0){
                    mPwd.setTag(STATE_HASVALUE);
                }else {
                    mPwd.setTag(STATE_EMPTY);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                int state1 = (int) mPhone.getTag();
                int state2 = (int) mPwd.getTag();
                if(state1 == STATE_HASVALUE && state2 == STATE_HASVALUE){
                    mLogin.setEnabled(true);
                }else {
                    mLogin.setEnabled(false);
                }
            }
        });

        mToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    @OnClick(R.id.btn_login)
    public void login(View view){
        String phone = mPhone.getText().toString().trim();
        String pwd = mPwd.getText().toString().trim();
        Map<String,Object> params = new HashMap<>(2);
        params.put("phone",phone);
        params.put("password", DESUtil.encode(Contants.DES_KEY, pwd));

        okHttpHelper.postData(Contants.API.LOGIN, params, new SpotsCallBack<LoginRespMsg<User>>(this) {


            @Override
            public void onSuccess(Response response, LoginRespMsg<User> userLoginRespMsg) {


                EasyGoApplication application = EasyGoApplication.getInstance();
                application.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());

                if (application.getIntent() == null) {
                    setResult(RESULT_OK);
                    ToastUtils.show(LoginActivity.this, "账户或者密码错误");
//                    finish();
                } else {

                    application.jumpToTargetActivity(LoginActivity.this);
                    finish();

                }


            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

}
