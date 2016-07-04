package com.wicare.wistormpublicdemo;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.wicare.wistorm.api.WVehicleApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.toolkit.WCarBrandSelector;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.xutil.L;
import com.wicare.wistormpublicdemo.xutil.T;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu
 *
 * 添加车辆
 */
public class CarAddActivity extends Activity{
	
	final String TAG = "CarAddActivity";
	
	static final int ADD_CAR_REQUEST_CODE = 2;
	static final int ADD_CAR_RESULT_CODE = 2;
	static final int SELECTOR_CAR_RESULT_CODE = 1;
	static final int SELECTOR_CAR_REQUEST_CODE = 1;
	public static final int FINISH_ADD_CAR_REQUEST_CODE = 3;
	
	/*车辆昵称输入*/
	private EditText etNickName;
	/*车辆牌子型号选择*/
	private TextView tvCarModels;
	/*车牌*/
	private EditText etObjName;
	/*车票省份缩写*/
	private TextView tvCarProvince;
	/*车牌省份*/
	private String carProvince;
	/*汽车品牌*/
	private String car_brand = "";
	/*汽车品牌ID*/
	private String car_brand_id = "";
	/*品牌系列*/
	private String car_series = "";
	/*品牌系列ID*/
	private String car_series_id = "";
	/*系列中的款型*/
	private String car_type = "";
	/*系列中的款型id*/
	private String car_type_id = "";
	
	MyApplication app;
    
    private Context mCotext;
    private WVehicleApi vehicleApi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_cars);
		app = (MyApplication)getApplication();
		mCotext = CarAddActivity.this;
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("添加车辆");
		//填写信息完成提交
		findViewById(R.id.tv_finish).setOnClickListener(onClickListener);
		findViewById(R.id.btn_choose).setOnClickListener(onClickListener);
		
		etNickName    = (EditText)findViewById(R.id.et_nick_name);
		tvCarModels   = (TextView)findViewById(R.id.tv_car_models);
		tvCarModels.setOnClickListener(onClickListener);
		etObjName     = (EditText)findViewById(R.id.et_obj_name);
		tvCarProvince = (TextView)findViewById(R.id.tv_car_province);
		init();
	}
	
	
	/**
	 * wistorm api接口网络请求初始化
	 */
	private void init(){
		vehicleApi = new WVehicleApi();
		BaseVolley.init(mCotext);
	}
	
	
	/**
	 * 点击事件监听
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.iv_top_back://返回
				finish();
				break;
				
			case R.id.tv_finish://完成提交车辆数据
				addCarSubmit();
				break;
				
			case R.id.btn_choose://车牌省份缩写
				Intent i_province = new Intent(CarAddActivity.this,ShortProvinceActivity.class);
				startActivityForResult(i_province, ADD_CAR_REQUEST_CODE);
				break;
				
			case R.id.tv_car_models://车型型号选择
				Intent i_selector_car = new Intent(CarAddActivity.this,WCarBrandSelector.class);
				startActivityForResult(i_selector_car, SELECTOR_CAR_REQUEST_CODE);
				break;
			}
		}
	};
	
	
	/**
	 * 提交添加新车辆的信息
	 */
	private void addCarSubmit() {
		String nick_name = etNickName.getText().toString().trim();
		if (nick_name.equals("")) {
			Toast.makeText(CarAddActivity.this, "车辆名称不能为空", Toast.LENGTH_SHORT) .show();
			return;
		}
		if (car_brand == null || car_brand.equals("")) {
			Toast.makeText(CarAddActivity.this, "车型不能为空", Toast.LENGTH_SHORT) .show();
			return;
		}
		String obj_name = tvCarModels.getText().toString() + etObjName.getText().toString().trim();
		if (obj_name.length() == 1) {
			obj_name = "";
		}
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", app.access_token);
		params.put("cust_id", app.cust_id);
//		params.put("device_id", "0");//默认 0  没有绑定设备
		params.put("car_brand_id", car_brand_id);
		params.put("car_brand", car_brand);
		params.put("car_series_id", car_series_id);
		params.put("car_series", car_series);
		params.put("car_type_id", car_type_id);
		params.put("car_type", car_type);
		params.put("obj_name", obj_name);
		params.put("nick_name", nick_name);
		
		vehicleApi.create(params, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				L.d(TAG, "创建车辆返回信息：" + response);
				try {
					JSONObject obj = new JSONObject(response);
					
					if("0".equals(obj.getString("status_code"))){
						T.showShort(mCotext, "创建车辆成功！");
						setResult(FINISH_ADD_CAR_REQUEST_CODE);
						finish();
					}else if("8".equals(obj.getString("status_code")))
						T.showShort(mCotext, "该车辆已被添加过");
					else{
						T.showShort(mCotext, "创建车辆失败！");
					}
				} catch (JSONException e){
					e.printStackTrace();
				}
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				T.showShort(mCotext, "创建车辆失败！");
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case ADD_CAR_RESULT_CODE:
			carProvince = data.getStringExtra("province");
			tvCarProvince.setText(carProvince);
			break;

		case SELECTOR_CAR_RESULT_CODE:
			Bundle car_msg = data.getExtras(); 
			car_brand     = car_msg.getString("brank"); //汽车品牌
			car_brand_id  = car_msg.getString("brankId"); //汽车品牌id	
			car_series  = car_msg.getString("series");//汽车型号
		    car_series_id = car_msg.getString("seriesId");//汽车型号ID
		    car_type    = car_msg.getString("type");  //汽车款式
		    car_type_id   = car_msg.getString("typeId");  //汽车款式ID		
		    tvCarModels.setText(car_series + " " + car_type);
			break;
		}
	};
	

}
