package com.wicare.wistormpublicdemo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wicare.wistorm.toolkit.WCarBrandSelector;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.HandlerMsg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.model.CarData;
import com.wicare.wistormpublicdemo.xutil.NetThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
	static final int FINISH_ADD_CAR_REQUEST_CODE = 3;
	
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
	
	CarData carNewData = new CarData();
	
	MyApplication app;
	
    int car_id = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_cars);
		app = (MyApplication)getApplication();
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("我的爱车");
		//填写信息完成提交
		findViewById(R.id.tv_finish).setOnClickListener(onClickListener);
		findViewById(R.id.btn_choose).setOnClickListener(onClickListener);
		
		etNickName    = (EditText)findViewById(R.id.et_nick_name);
		tvCarModels   = (TextView)findViewById(R.id.tv_car_models);
		tvCarModels.setOnClickListener(onClickListener);
		etObjName     = (EditText)findViewById(R.id.et_obj_name);
		tvCarProvince = (TextView)findViewById(R.id.tv_car_province);
		
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
     * Handler 处理消息
     */
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
    	
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            
            case HandlerMsg.ADD_NEW_CAR:
            	Log.d(TAG, "====提交车辆之后返回的信息===" + msg.obj.toString());
            	jsonAddCar(msg.obj.toString());
            	break;
            }
        }
    };	
    
    


	/**
	 * @param strJson
	 */
	private void jsonAddCar(String strJson) {
		try {
			JSONObject jsonObject = new JSONObject(strJson);
			if (jsonObject.getString("status_code").equals("0")) {
				car_id = jsonObject.getInt("obj_id");
				carNewData.setObj_id(car_id);
				app.carDatas.add(carNewData);
				Toast.makeText(CarAddActivity.this, "车辆添加成功", Toast.LENGTH_SHORT).show();
				setResult(FINISH_ADD_CAR_REQUEST_CODE);
				finish();
			} else {
				Toast.makeText(CarAddActivity.this, "添加失败", Toast.LENGTH_SHORT) .show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(CarAddActivity.this, "添加失败", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	
	
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
		String url = Constant.BaseUrl + "vehicle/simple?auth_code=" + app.auth_code;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		carNewData.setObj_name(obj_name);
		carNewData.setNick_name(nick_name);
		carNewData.setCar_brand(car_brand);
		carNewData.setCar_series(car_series);
		carNewData.setCar_type(car_type);
		carNewData.setCar_brand_id(car_brand_id);
		carNewData.setCar_series_id(car_series_id);
		carNewData.setCar_type_id(car_type_id);
		params.add(new BasicNameValuePair("cust_id", app.cust_id));
		params.add(new BasicNameValuePair("obj_name", obj_name));
		params.add(new BasicNameValuePair("nick_name", nick_name));
		params.add(new BasicNameValuePair("car_brand", car_brand));
		params.add(new BasicNameValuePair("car_series", car_series));
		params.add(new BasicNameValuePair("car_type", car_type));
		params.add(new BasicNameValuePair("car_brand_id", car_brand_id));
		params.add(new BasicNameValuePair("car_series_id", car_series_id));
		params.add(new BasicNameValuePair("car_type_id", car_type_id));
		
		new NetThread.postDataThread(mHandler, url, params, HandlerMsg.ADD_NEW_CAR).start();

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
		    tvCarModels.setText(car_series + "--" + car_type);
			break;
		}
	};
	

}
