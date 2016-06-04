package lltan.jikexueyuan.com.lego.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import lltan.jikexueyuan.com.lego.EasyGoApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpHelper {


    public static final int TOKEN_MISSING=401;// token 丢失
    public static final int TOKEN_ERROR=402; // token 错误
    public static final int TOKEN_EXPIRE=403; // token 过期



    public static final String TAG="OkHttpHelper";

//        private volatile  static  OkHttpHelper mInstance;
    private OkHttpClient mHttpClient;
    private Gson mGson;

    private Handler mHandler;


    private OkHttpHelper(){

        mHttpClient = new OkHttpClient();
//            mHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
//            mHttpClient.setReadTimeout(10,TimeUnit.SECONDS);
//            mHttpClient.setWriteTimeout(30,TimeUnit.SECONDS);

        mGson = new Gson();

        mHandler = new Handler(Looper.getMainLooper());

    };

//        public static  OkHttpHelper getInstance(){
//            if(mInstance == null){
//                synchronized (OkHttpHelper.class){
//                    if(mInstance == null){
//                        mInstance = new OkHttpHelper();
//                    }
//                }
//            }
//            return  mInstance;
//        }

    public static OkHttpHelper getInstance(){
        return OkHttpHolder.mInstance;
    }

    private static class OkHttpHolder{
        private static final OkHttpHelper mInstance = new OkHttpHelper();
    }




    public void getData(String url,Map<String,Object> param,BaseCallback callback){


        Request request = buildGetRequest(url,param);

        request(request,callback);

    }

    public void getData(String url,BaseCallback callback){

        getData(url,null,callback);
    }


    public void postData(String url,Map<String,Object> param, BaseCallback callback){

        Request request = buildPostRequest(url,param);
        request(request,callback);
    }





    public  void request(final Request request,final  BaseCallback callback){

        callback.onBeforeRequest(request);

        mHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                callbackFailure(callback, request, e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

//                    callback.onResponse(response);
                callbackResponse(callback,response);

                if(response.isSuccessful()) {

                    String resultStr = response.body().string();

                    Log.d(TAG, "result=" + resultStr);


                    if (callback.mType == String.class){
                        callbackSuccess(callback,response,resultStr);
                    }
                    else {
                        try {

                            Object obj = mGson.fromJson(resultStr, callback.mType);
                            callbackSuccess(callback,response,obj);
                        }
                        catch (com.google.gson.JsonParseException e){ // Json解析的错误
                            callback.onError(response,response.code(),e);
                        }
                    }
                }
                else if(response.code() == TOKEN_ERROR||response.code() == TOKEN_EXPIRE ||response.code() == TOKEN_MISSING ){

                    callbackTokenError(callback,response);
                }


                else {
                    callbackError(callback,response,null);
                }

            }
        });


    }


    private void callbackTokenError(final  BaseCallback callback , final Response response ){

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onTokenError(response,response.code());
            }
        });
    }

        private void callbackSuccess(final  BaseCallback callback , final Response response, final Object obj ){

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(response, obj);
                }
            });
        }


        private void callbackError(final  BaseCallback callback , final Response response, final Exception e ){

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(response,response.code(),e);
                }
            });
        }



    private void callbackFailure(final  BaseCallback callback , final Request request, final IOException e ){

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request,e);
            }
        });
    }


    private void callbackResponse(final  BaseCallback callback , final Response response ){

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }



    private  Request buildPostRequest(String url,Map<String,Object> params){

            return  buildRequest(url, HttpMethodType.POST,params);
        }

        private  Request buildGetRequest(String url,Map<String,Object> param){

            return  buildRequest(url, HttpMethodType.GET,param);
        }

        private  Request buildRequest(String url,HttpMethodType methodType,Map<String,Object> params){


            Request.Builder builder = new Request.Builder()
                    .url(url);

            if (methodType == HttpMethodType.POST){
                RequestBody body = builderFormData(params);
                builder.post(body);
            }
            else if(methodType == HttpMethodType.GET){

                url = buildUrlParams(url,params);
                builder.url(url);

                builder.get();
            }


            return builder.build();
        }



    private   String buildUrlParams(String url ,Map<String,Object> params) {

        if(params == null)
            params = new HashMap<>(1);

        String token = EasyGoApplication.getInstance().getToken();
        if(!TextUtils.isEmpty(token))
            params.put("token",token);


        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + (entry.getValue()==null?"":entry.getValue().toString()));
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }

        if(url.indexOf("?")>0){
            url = url +"&"+s;
        }else{
            url = url +"?"+s;
        }

        return url;
    }

        private RequestBody builderFormData(Map<String,Object> params){


            FormBody.Builder builder = new FormBody.Builder();

            if(params !=null){



                for (Map.Entry<String,Object> entry :params.entrySet() ){

                    builder.add(entry.getKey(),entry.getValue()==null?"":entry.getValue().toString());
                }

                String token = EasyGoApplication.getInstance().getToken();
                if(!TextUtils.isEmpty(token))
                    builder.add("token", token);
            }

            return  builder.build();

        }



        enum  HttpMethodType{

            GET,
            POST,

        }



}
