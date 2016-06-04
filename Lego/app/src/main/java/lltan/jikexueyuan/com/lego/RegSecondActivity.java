package lltan.jikexueyuan.com.lego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import lltan.jikexueyuan.com.lego.bean.User;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.http.SpotsCallBack;
import lltan.jikexueyuan.com.lego.msg.LoginRespMsg;
import lltan.jikexueyuan.com.lego.utils.DESUtil;
import lltan.jikexueyuan.com.lego.utils.ManifestUtil;
import lltan.jikexueyuan.com.lego.utils.ToastUtils;
import lltan.jikexueyuan.com.lego.widget.ClearEditText;
import lltan.jikexueyuan.com.lego.widget.CountTimerView;
import lltan.jikexueyuan.com.lego.widget.EasyGoToolBar;
import okhttp3.Response;

/**
 * Created by legoer on 2016/5/13.
 */
public class RegSecondActivity extends AppCompatActivity {

    @ViewInject(R.id.toolbar)
    private EasyGoToolBar mToolbar;
    @ViewInject(R.id.txtTip)
    private TextView mTip;
    @ViewInject(R.id.edittxt_code)
    private ClearEditText mCode;
    @ViewInject(R.id.btn_reSend)
    private Button mResend;

    private String phone;
    private String pwd;
    private String countryCode;


    private CountTimerView countTimerView;
    private  SMSEvenHanlder evenHanlder;



    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsecond);
        ViewUtils.inject(this);

        initToolbal();

        phone = getIntent().getStringExtra("phone");
        pwd = getIntent().getStringExtra("pwd");
        countryCode = getIntent().getStringExtra("countryCode");

        String formatedPhone = "+" + countryCode + " " + splitPhoneNum(phone);



        String text = getString(R.string.smssdk_send_mobile_detail)+formatedPhone;
        mTip.setText(Html.fromHtml(text));



        countTimerView = new CountTimerView(mResend);
        countTimerView.start();




        SMSSDK.initSDK(this, ManifestUtil.getMetaDataValue(this, "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(this, "mob_sms_appSecrect"));

        evenHanlder = new SMSEvenHanlder();
        SMSSDK.registerEventHandler(evenHanlder);

    }

    private String splitPhoneNum(String phone) {
        StringBuilder builder = new StringBuilder(phone);
        builder.reverse();
        for (int i = 4, len = builder.length(); i < len; i += 5) {
            builder.insert(i, ' ');
        }
        builder.reverse();
        return builder.toString();
    }

    private void initToolbal() {
        if(mToolbar == null){
            return;
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegSecondActivity.this.finish();
            }
        });

        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
            }
        });
    }

    private void submitCode() {
        String vCode = mCode.getText().toString().trim();

        if (TextUtils.isEmpty(vCode)) {
            ToastUtils.show(this, R.string.smssdk_resend_identify_code);
            return;
        }
        SMSSDK.submitVerificationCode(countryCode, phone, vCode);

    }

    private void doReg(){

        Map<String,Object> params = new HashMap<>(2);
        params.put("phone",phone);
        params.put("password", DESUtil.encode(Contants.DES_KEY, pwd));

        okHttpHelper.postData(Contants.API.REG, params, new SpotsCallBack<LoginRespMsg<User>>(this) {


            @Override
            public void onSuccess(Response response, LoginRespMsg<User> userLoginRespMsg) {

//                if (dialog != null && dialog.isShowing())
//                    dialog.dismiss();

                if (userLoginRespMsg.getStatus() == LoginRespMsg.STATUS_ERROR) {
                    ToastUtils.show(RegSecondActivity.this, "注册失败:" + userLoginRespMsg.getMessage());
                    return;
                }
                EasyGoApplication application = EasyGoApplication.getInstance();
                application.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());


                startActivity(new Intent(RegSecondActivity.this, MainActivity.class));
                finish();

            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            @Override
            public void onTokenError(Response response, int code) {
                super.onTokenError(response, code);


            }
        });
    }

    class SMSEvenHanlder extends EventHandler {


        @Override
        public void afterEvent(final int event, final int result,
                               final Object data) {



            runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                    if(dialog !=null && dialog.isShowing())
//                        dialog.dismiss();

                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                            countTimerView.onFinish();
//                              HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
//                              String country = (String) phoneMap.get("country");
//                              String phone = (String) phoneMap.get("phone");

//                            ToastUtils.show(RegSecondActivity.this,"验证成功："+phone+",country:"+country);


                            doReg();
//                            dialog.setMessage("正在提交注册信息");
//                            dialog.show();
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
//                                ToastUtils.show(RegActivity.this, des);
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
}
