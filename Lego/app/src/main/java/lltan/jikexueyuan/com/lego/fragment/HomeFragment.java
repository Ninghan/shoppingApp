package lltan.jikexueyuan.com.lego.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import lltan.jikexueyuan.com.lego.Contants;
import lltan.jikexueyuan.com.lego.R;
import lltan.jikexueyuan.com.lego.WareDisplayActivity;
import lltan.jikexueyuan.com.lego.adapter.Banner;
import lltan.jikexueyuan.com.lego.adapter.Campaign;
import lltan.jikexueyuan.com.lego.adapter.HomeAdapter;
import lltan.jikexueyuan.com.lego.adapter.HomeCampaign;
import lltan.jikexueyuan.com.lego.adapter.decoration.CardViewtemDecortion;
import lltan.jikexueyuan.com.lego.adapter.layoutmanager.FullyLinearLayoutManager;
import lltan.jikexueyuan.com.lego.http.BaseCallback;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.http.SpotsCallBack;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by legoer on 2016/3/27.
 */
public class HomeFragment extends BaseFragment {

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.slider)
    private SliderLayout mSliderLayout;
    @ViewInject(R.id.custom_indicator)
    private PagerIndicator custom_indicator;

    private List<Banner> mBanners;
    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();
    private HomeAdapter homeAdapter;
    private static final String TAG = "HomeActivity";

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void init() {
        requestImages();
        initRecyclerView();
    }

    private void initRecyclerView() {

        httpHelper.getData(Contants.API.CAMPAIGN_HOME, new SpotsCallBack<List<HomeCampaign>>(getActivity()) {

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

        homeAdapter = new HomeAdapter(homeCampaigns,getActivity());
        homeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Campaign campaign) {
                Intent intent = new Intent(getActivity(), WareDisplayActivity.class);
                intent.putExtra(Contants.COMPAINGAIN_ID,campaign.getId());

                startActivity(intent);

            }
        });

        mRecyclerView.setLayoutManager(new FullyLinearLayoutManager(this.getActivity()));

        mRecyclerView.setAdapter(homeAdapter);
        mRecyclerView.addItemDecoration(new CardViewtemDecortion());
    }

    private void requestImages() {
        final String url = "http://112.124.22.238:8081/course_api/banner/query?type=1";
        httpHelper.getData(url, new BaseCallback<List<Banner>>() {

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
                initSlider();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });

    }

    private void initSlider() {
        if(mBanners !=null){

            for (Banner banner : mBanners){


                DefaultSliderView defaultSliderView = new DefaultSliderView(this.getActivity());
                defaultSliderView.image(banner.getImgUrl());
                defaultSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                mSliderLayout.addSlider(defaultSliderView);

            }
        }



        mSliderLayout.setCustomIndicator(custom_indicator);
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setDuration(5000);


    }
}
