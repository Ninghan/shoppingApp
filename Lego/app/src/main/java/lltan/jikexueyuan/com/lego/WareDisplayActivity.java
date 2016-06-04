package lltan.jikexueyuan.com.lego;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import lltan.jikexueyuan.com.lego.adapter.BaseAdapter;
import lltan.jikexueyuan.com.lego.adapter.WaresAdapter;
import lltan.jikexueyuan.com.lego.adapter.decoration.DividerItemDecoration;
import lltan.jikexueyuan.com.lego.bean.Page;
import lltan.jikexueyuan.com.lego.bean.Wares;
import lltan.jikexueyuan.com.lego.utils.Pager;
import lltan.jikexueyuan.com.lego.widget.EasyGoToolBar;

/**
 * Created by legoer on 2016/5/26.
 */
public class WareDisplayActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener, Pager.OnPageListener<Wares> {


    public static final String TAG = "WareDisplayActivity";
    public static final int ACTION_LIST = 1;
    public static final int ACTION_GRID = 2;
    public static final int TAG_DEFALUT = 0;
    public static final int TAG_SALE = 1;
    public static final int TAG_PRICE = 2;

    @ViewInject(R.id.toolbar)
    private EasyGoToolBar mToolbar;

    @ViewInject(R.id.tab_layout)
    private TabLayout mTabLayout;


    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefresh;

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;

    private int orderBy = 0;
    private long campaignId = 1;
    private WaresAdapter mWaresAdapter;
    private Pager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ware_display);

        com.lidroid.xutils.ViewUtils.inject(this);

        initToolbar();

        campaignId=getIntent().getLongExtra(Contants.COMPAINGAIN_ID,1);
        Log.d(TAG, "=" + campaignId);

        initTab();

        getData();
    }

    private void initToolbar() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WareDisplayActivity.this.finish();
            }
        });

        mToolbar.setRightImageIcon(R.drawable.icon_grid_32);
        mToolbar.getmRightImage().setTag(ACTION_GRID);

        mToolbar.setRightImageOnClickListener(this);

    }

    private void initTab() {
        createTab(R.string.default1,TAG_DEFALUT);
        createTab(R.string.sale,TAG_SALE);
        createTab(R.string.price,TAG_PRICE);

        mTabLayout.setOnTabSelectedListener(this);
    }

    private void createTab(int id,int tag){
        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setText(getResources().getText(id));
        tab.setTag(tag);
        mTabLayout.addTab(tab);
    }

    private void getData() {
        pager = Pager.newBuilder()
                .setUrl(Contants.API.WARES_CAMPAIN_LIST)
                .setParams("campaignId",campaignId)
                .setParams("orderBy",orderBy)
                .setRefresh(mRefresh)
                .setLoadMore(true)
                .setOnPageListener(this)
                .build(this, new TypeToken<Page<Wares>>(){}.getType());

        pager.request();
    }

    @Override
    public void onClick(View v) {
        int action = (int) v.getTag();
        if(action == ACTION_LIST){
            mToolbar.setRightImageIcon(R.drawable.icon_grid_32);
            mToolbar.getmRightImage().setTag(ACTION_GRID);


            mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
            if(mWaresAdapter != null){
                mWaresAdapter.resetLayout(R.layout.template_grid_wares);
                mRecyclerView.setAdapter(mWaresAdapter);
            }
        }else if(action == ACTION_GRID){
            mToolbar.setRightImageIcon(R.drawable.icon_list_32);
            mToolbar.getmRightImage().setTag(ACTION_LIST);

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            if(mWaresAdapter != null){
                mWaresAdapter.resetLayout(R.layout.template_list_wares);
                mRecyclerView.setAdapter(mWaresAdapter);
            }
        }
    }

    @Override
    public void load(List<Wares> datas, int totalPage, int totalCount) {
        if(mWaresAdapter == null){
            mWaresAdapter = new WaresAdapter(this,datas);
            mWaresAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Wares wares = mWaresAdapter.getItem(position);

                    Intent intent = new Intent(WareDisplayActivity.this, WareDetailActivity.class);

                    intent.putExtra(Contants.WARE,wares);
                    startActivity(intent);
                }
            });
            mRecyclerView.setAdapter(mWaresAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }else {
            mWaresAdapter.refreshData(datas);
        }
    }

    @Override
    public void refresh(List<Wares> datas, int totalPage, int totalCount) {
        mWaresAdapter.refreshData(datas);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Wares> datas, int totalPage, int totalCount) {
        mWaresAdapter.loadMoreData(datas);
//        mRecyclerView.scrollToPosition(datas.);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        orderBy = (int) tab.getTag();
        pager.putParam("orderBy",orderBy);
        pager.request();

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
