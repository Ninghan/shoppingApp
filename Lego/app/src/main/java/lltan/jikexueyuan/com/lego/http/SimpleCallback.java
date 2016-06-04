package lltan.jikexueyuan.com.lego.http;

import android.content.Context;
import lltan.jikexueyuan.com.lego.EasyGoApplication;
import lltan.jikexueyuan.com.lego.R;
import lltan.jikexueyuan.com.lego.utils.ToastUtils;
import okhttp3.Request;
import okhttp3.Response;


public abstract class SimpleCallback<T> extends BaseCallback<T> {

    protected Context mContext;

    public SimpleCallback(Context context){

        mContext = context;

    }

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
    public void onTokenError(Response response, int code) {
        ToastUtils.show(mContext, mContext.getString(R.string.token_error));

//        Intent intent = new Intent();
//        intent.setClass(mContext, MainActivity.class);
//        mContext.startActivity(intent);

        EasyGoApplication.getInstance().clearUser();

    }


}
