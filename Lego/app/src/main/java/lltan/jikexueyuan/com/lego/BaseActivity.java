package lltan.jikexueyuan.com.lego;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import lltan.jikexueyuan.com.lego.bean.User;


/**
 * Created by Administrator on 2016/5/19.
 */
public class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getSimpleName();

    public void startActivity(Intent intent,boolean isNeedLogin){


        if(isNeedLogin){

            User user =EasyGoApplication.getInstance().getUser();
            if(user !=null){
                super.startActivity(intent);
            }
            else{

                EasyGoApplication.getInstance().putIntent(intent);
//                Intent loginIntent = new Intent(this
//                        , LoginActivity.class);
                super.startActivity(intent);

            }

        }
        else{
            super.startActivity(intent);
        }

    }
}
