package lltan.jikexueyuan.com.lego;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lltan.jikexueyuan.com.lego.city.XmlParserHandler;
import lltan.jikexueyuan.com.lego.city.model.CityModel;
import lltan.jikexueyuan.com.lego.city.model.DistrictModel;
import lltan.jikexueyuan.com.lego.city.model.ProvinceModel;
import lltan.jikexueyuan.com.lego.http.OkHttpHelper;
import lltan.jikexueyuan.com.lego.http.SpotsCallBack;
import lltan.jikexueyuan.com.lego.msg.BaseRespMsg;
import lltan.jikexueyuan.com.lego.widget.ClearEditText;
import lltan.jikexueyuan.com.lego.widget.EasyGoToolBar;
import okhttp3.Response;

/**
 * Created by legoer on 2016/5/19.
 */
public class AddressAddActivity extends BaseActivity{

    private OptionsPickerView mCityPikerView; //https://github.com/saiwu-bigkoo/Android-PickerView


    @ViewInject(R.id.txt_address)
    private TextView mTxtAddress;

    @ViewInject(R.id.edittxt_consignee)
    private ClearEditText mEditConsignee;

    @ViewInject(R.id.edittxt_phone)
    private ClearEditText mEditPhone;

    @ViewInject(R.id.edittxt_add)
    private ClearEditText mEditAddr;

    @ViewInject(R.id.toolbar)
    private EasyGoToolBar mToolBar;


    private List<ProvinceModel> mProvinces;
    private ArrayList<ArrayList<String>> mCities = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<ArrayList<String>>> mDistricts = new ArrayList<ArrayList<ArrayList<String>>>();


    private OkHttpHelper mHttpHelper=OkHttpHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);

        ViewUtils.inject(this);

        initToolbar();
        initData();
    }

    private void initToolbar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressAddActivity.this.finish();
            }
        });
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddress();
            }
        });
    }

    private void createAddress() {
        String consignee = mEditConsignee.getText().toString();
        String phone = mEditPhone.getText().toString();
        String address = mTxtAddress.getText().toString() + mEditAddr.getText().toString();


        Map<String,Object> params = new HashMap<>(5);
        params.put("user_id", EasyGoApplication.getInstance().getUser().getId());
        params.put("consignee", consignee);
        params.put("phone", phone);
        params.put("addr", address);
        params.put("zip_code", "000000");

        mHttpHelper.postData(Contants.API.ADDRESS_CREATE, params, new SpotsCallBack<BaseRespMsg>(this) {


            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                if (baseRespMsg.getStatus() == BaseRespMsg.STATUS_SUCCESS) {
                    setResult(RESULT_OK);
                    finish();

                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

    private void initData() {
        initProviceDatas();

        mCityPikerView = new OptionsPickerView(this);

        try {

            mCityPikerView.setPicker((ArrayList)mProvinces,mCities,mDistricts,true);
            mCityPikerView.setTitle("选择城市");
            mCityPikerView.setCyclic(false, false, false);
            mCityPikerView.setSelectOptions(0,0,0);
            mCityPikerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3) {
                    String address = mProvinces.get(options1).getName() + " "
                            + mCities.get(options1).get(options2)
                            + mDistricts.get(options1).get(options2).get(options3);
                    mTxtAddress.setText(address);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initProviceDatas() {
        AssetManager asset = getAssets();

        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input,handler);
            input.close();
            mProvinces = handler.getDataList();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if(mProvinces != null){
            for(ProvinceModel provinceModel:mProvinces){
                List<CityModel> cities = provinceModel.getCityList();
                ArrayList<String> cityStrs = new ArrayList<String>();

                ArrayList<ArrayList<String>> dts = new ArrayList<ArrayList<String>>();//地区List,这行代码可以优化
                for(CityModel c : cities){
                    cityStrs.add(c.getName());//把城市名称放入cityStrs


                    List<DistrictModel> districts = c.getDistrictList();
                    ArrayList<String> districtStrs = new ArrayList<String>();
                    for(DistrictModel d:districts){
                        districtStrs.add(d.getName());
                    }
                    dts.add(districtStrs);



                }
                mDistricts.add(dts);
                mCities.add(cityStrs);
            }
        }

    }

    @OnClick(R.id.ll_city_picker)
    public void showCityPickerView(View view){
        mCityPikerView.show();
    }
}
