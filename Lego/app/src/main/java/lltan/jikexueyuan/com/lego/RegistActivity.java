package lltan.jikexueyuan.com.lego;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import lltan.jikexueyuan.com.lego.utils.ManifestUtil;
import lltan.jikexueyuan.com.lego.utils.ToastUtils;
import lltan.jikexueyuan.com.lego.widget.ClearEditText;
import lltan.jikexueyuan.com.lego.widget.EasyGoToolBar;

/**
 * Created by legoer on 2016/5/11.
 */
public class RegistActivity extends AppCompatActivity {

    private static final String TAG = "RegActivity";

    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";

    @ViewInject(R.id.toolbar)
    private EasyGoToolBar mToolbar;

    @ViewInject(R.id.txtCountryCode)
    private TextView mCountryCode;

    @ViewInject(R.id.edittxt_phone)
    private ClearEditText mPhone;

    @ViewInject(R.id.edittxt_pwd)
    private ClearEditText mPwd;

    @ViewInject(R.id.txtCountry)
    private TextView mCountry;

    private SMSEventHandle evenHanlder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reg);
        ViewUtils.inject(this);

        initToolbar();

        SMSSDK.initSDK(this, ManifestUtil.getMetaDataValue(this, "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(this, "mob_sms_appSecrect"));

        evenHanlder = new SMSEventHandle();
        SMSSDK.registerEventHandler(evenHanlder);

        String[] country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        if (country != null) {

            mCountryCode.setText("+"+country[1]);

            mCountry.setText(country[0]);

        }

    }

    private void initToolbar() {
        if (mToolbar == null){
            return;
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistActivity.this.finish();
            }
        });
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SMSSDK.getVerificationCode();

                getCode();

            }
        });
    }

    private void getCode() {
        if(mPhone==null || mPwd == null){
            return;
        }
        String phone = mPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mCountryCode.getText().toString().trim();
//        String country = mCountry.getText().toString().trim();
        String pwd = mPwd.getText().toString().trim();

        checkPhoneNumber(phone, code, pwd);
        SMSSDK.getVerificationCode(code, phone);
    }

    private void checkPhoneNumber(String phone, String code, String pwd) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        if(TextUtils.isEmpty(phone)){
            ToastUtils.show(RegistActivity.this, "请输入手机号");
            Log.w(TAG,"手机号码为空");
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            ToastUtils.show(RegistActivity.this,"密码不能为空");
            Log.w(TAG, "密码为空");
            return;
        }

        if (code == "86") {
            if(phone.length() != 11) {
                ToastUtils.show(this,"手机号码长度不对");
                return;
            }

        }

        String rule = "^1(3|4|5|7|8)\\d{9}$";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);
        if(! m.matches()){
            ToastUtils.show(this,"手机号码格式不正确");
            return;
        }


    }

    class SMSEventHandle extends EventHandler {
        @Override
        public void afterEvent(final int event, final int result, final Object data) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                    SMSSDK.getSupportedCountries();
                    Log.d(TAG," " + result);

                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {

                            onCountryListGot((ArrayList<HashMap<String, Object>>) data);

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            // 请求验证码后，跳转到验证码填写页面

                            afterVerificationCodeRequested((Boolean) data);

                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                        }
                    } else {

                        // 根据服务器返回的网络错误，给toast提示
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                ToastUtils.show(RegistActivity.this, des);
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }
                    }
                }
            });
        }
    }

    /** 请求验证码后，跳转到验证码填写页面 */
    private void afterVerificationCodeRequested(boolean smart) {


        if(mPhone == null || mPwd == null || mCountryCode == null){
            return;
        }

        String phone = mPhone.getText().toString().trim().replaceAll("\\s*", "");
        String code = mCountryCode.getText().toString().trim();
        String pwd = mPwd.getText().toString().trim();

        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        Intent intent = new Intent(this,RegSecondActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("pwd",pwd);
        intent.putExtra("countryCode",code);

        startActivity(intent);
    }

    private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
        // 解析国家列表
        for (HashMap<String, Object> country : countries) {
            String code = (String) country.get("zone");
            String rule = (String) country.get("rule");
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }

            Log.d(TAG, "code=" + code + "rule=" + rule);


        }
    }

    private String[] getCurrentCountry() {
        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
        }

        if (country == null) {
            Log.w("SMSSDK", "no country found by MCC: " + mcc);
            country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        }
        return country;
    }

    private String getMCC() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }

        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        SMSSDK.unregisterEventHandler(evenHanlder);

    }
}
