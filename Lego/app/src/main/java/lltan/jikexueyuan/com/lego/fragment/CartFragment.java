package lltan.jikexueyuan.com.lego.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

import lltan.jikexueyuan.com.lego.Contants;
import lltan.jikexueyuan.com.lego.CreateOrderActivity;
import lltan.jikexueyuan.com.lego.R;
import lltan.jikexueyuan.com.lego.adapter.CartAdapter;
import lltan.jikexueyuan.com.lego.adapter.decoration.DividerItemDecoration;
import lltan.jikexueyuan.com.lego.bean.Page;
import lltan.jikexueyuan.com.lego.bean.ShoppingCart;
import lltan.jikexueyuan.com.lego.bean.Wares;
import lltan.jikexueyuan.com.lego.http.BaseCallback;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.utils.CartProvider;
import lltan.jikexueyuan.com.lego.widget.EasyGoToolBar;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Administrator on 2016/3/27.
 */
public class CartFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG="CartFragment";

    private static final int ACTION_EDIT = 0;
    private static final int ACTION_COMPLETE = 1;

    @ViewInject(R.id.ez_toolbar)
    private EasyGoToolBar mEzGoToolBar;
    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.checkbox_all)
    private CheckBox mCheckBoxAll;
    @ViewInject(R.id.btn_order)
    private Button mButtonOrder;
    @ViewInject(R.id.btn_del)
    private Button mButtonDel;
    @ViewInject(R.id.txt_total)
    private TextView mTextTotalPrice;
    @ViewInject(R.id.txt_go)
    private TextView mTextGo;
    @ViewInject(R.id.bottomControls)
    private RelativeLayout mBottomControls;

    private CartProvider cartProvider;
    private CartAdapter cartAdapter;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart,container,false);
    }

    @Override
    public void init() {
        initView();
    }


    private void initView() {

        mCheckBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartAdapter != null){
                    cartAdapter.setCheckAll(mCheckBoxAll.isChecked());
                }
            }
        });

        cartProvider = new CartProvider(this.getActivity());
        changToolbar();
        showData();


    }

    @OnClick(R.id.btn_del)
    public void delCart(View view){
        if(cartAdapter != null){
            cartAdapter.delCart();
        }
    }

    @OnClick(R.id.btn_order)
    public void toOrder(View view){
        Intent intent = new Intent(getContext(), CreateOrderActivity.class);

        startActivity(intent);
    }

    private void changToolbar() {
        mEzGoToolBar.hideSearchView();
        mEzGoToolBar.showTitleView();
        mEzGoToolBar.setTitle(R.string.cart);
        mEzGoToolBar.getmRightButton().setVisibility(View.VISIBLE);
        mEzGoToolBar.getmRightButton().setText(R.string.cart_edit);
        mEzGoToolBar.getmRightButton().setOnClickListener(this);
        mEzGoToolBar.getmRightButton().setTag(ACTION_EDIT);
    }

    private void showDelControl(){
        mEzGoToolBar.getmRightButton().setText(R.string.cart_complete);
        mButtonOrder.setVisibility(View.GONE);
        mButtonDel.setVisibility(View.VISIBLE);
        if(cartAdapter != null){
            cartAdapter.setNumberAddSubVisible(true);
        }
        mEzGoToolBar.getmRightButton().setTag(ACTION_COMPLETE);

//        mAdapter.checkAll_None(false);
//        mCheckBoxAll.setChecked(false);
    }


    private void hideDelControl(){
        mEzGoToolBar.getmRightButton().setText(R.string.cart_edit);
        mButtonOrder.setVisibility(View.VISIBLE);
        mButtonDel.setVisibility(View.GONE);
        if(cartAdapter != null){

            cartAdapter.setNumberAddSubVisible(false);
        }
        mEzGoToolBar.getmRightButton().setTag(ACTION_EDIT);

//        mAdapter.checkAll_None(true);
//        mAdapter.showTotalPrice();

//        mCheckBoxAll.setChecked(false);
    }

    private void showData() {

        List<ShoppingCart> shoppingDatas = cartProvider.getAll();
        if(shoppingDatas != null){
            cartAdapter = new CartAdapter(shoppingDatas,this.getActivity(),mTextTotalPrice);
            showBottomControls();
            mTextGo.setVisibility(View.GONE);
            mRecyclerView.setAdapter(cartAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(),DividerItemDecoration.VERTICAL_LIST));
        }else {
            hideBottomControls();
            mTextGo.setVisibility(View.VISIBLE);
            mTextGo.setTextColor(getResources().getColor(R.color.gray));
            mTextGo.setText("购物车已饥渴难耐\n快去挑几件宝贝吧");

        }

    }


    private void showBottomControls(){
        mBottomControls.setVisibility(View.VISIBLE);
    }


    private void hideBottomControls(){
        mBottomControls.setVisibility(View.GONE);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        int action = (int) v.getTag();
        if(action == ACTION_EDIT){
            showDelControl();
        }else if(action == ACTION_COMPLETE){
            hideDelControl();
        }
    }

    public void refData() {
//        List<ShoppingCart> carts = cartProvider.getAll();
        showData();
//        if(cartAdapter != null){
//            cartAdapter.clear();
//            cartAdapter.addData(carts);
//            cartAdapter.showTotalPrice();
//        }
        changToolbar();
        hideDelControl();
    }
}
