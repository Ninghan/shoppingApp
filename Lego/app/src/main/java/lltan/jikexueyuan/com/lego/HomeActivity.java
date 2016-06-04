package lltan.jikexueyuan.com.lego;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import lltan.jikexueyuan.com.lego.adapter.Banner;
import lltan.jikexueyuan.com.lego.adapter.Campaign;
import lltan.jikexueyuan.com.lego.adapter.HomeAdapter;
import lltan.jikexueyuan.com.lego.adapter.HomeCampaign;
import lltan.jikexueyuan.com.lego.adapter.decoration.CardViewtemDecortion;
import lltan.jikexueyuan.com.lego.http.BaseCallback;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.http.SpotsCallBack;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by legoer on 2016/5/22.
 */
public class HomeActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private List<HomeCampaign> mDatas;
    private List<Banner> mBanners;
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private HomeAdapter homeAdapter;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        init();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);


    }

    private void init() {
        requestImages();
        initRecyclerView();
    }

    private void initRecyclerView() {

        httpHelper.getData(Contants.API.CAMPAIGN_HOME, new SpotsCallBack<List<HomeCampaign>>(this) {

            @Override
            public void onSuccess(Response response, List<HomeCampaign> homeCampaigns) {
                initData(homeCampaigns);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }

    private void initData(final List<HomeCampaign> homeCampaigns) {

        homeAdapter = new HomeAdapter(homeCampaigns,this);
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Campaign campaign) {
                Toast.makeText(view.getContext(), campaign.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(homeAdapter);
        mRecyclerView.addItemDecoration(new CardViewtemDecortion());
    }

    private void requestImages() {
        final String url = "http://112.124.22.238:8081/course_api/banner/query?type=1";
        httpHelper.getData(url, new BaseCallback<List<Banner>>(){

            @Override
            public void onBeforeRequest(Request request) {

            }

            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onSuccess(Response response, List<Banner> banners) {
                mBanners = banners;
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });

    }
}
