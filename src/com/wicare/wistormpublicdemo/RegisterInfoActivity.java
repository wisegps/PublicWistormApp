package com.wicare.wistormpublicdemo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wicare.wistorm.http.HttpThread;
import com.wicare.wistorm.toolkit.SystemTools;
import com.wicare.wistorm.toolkit.WCarBrandSelector;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;
import com.wicare.wistormpublicdemo.xutil.NetThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu
 * 
 * 注册信息填写页面
 */
public class RegisterInfoActivity extends Activity {

	static final String TAG = "RegisterInfoActivity";
	static final int CAR_SELECTOR_RESULT_CODE = 1;
	
	private Button btnEnterSystem;
	/*昵称输入*/
	private EditText etPetName;
	/*生日选择*/
	private Spinner spBirth;
	/*汽车品牌*/
	private TextView tvCarModel;
	/*汽车品牌的 布局*/
	private LinearLayout llCarModel;
	/*性别*/
	private String sex = "0";
	/*汽车品牌*/
	private String carBrank = "";
	/*品牌ID*/
	private String carBrankId = "";
	/*汽车系列*/
	private String carSeries = "";
	/*系列ID*/
	private String carSeriesId = "";
	
	private String cust_type = "0";
	
	private boolean isPhone = true;
	
	/*密码*/
	private String password = "";
	/*帐号*/
	private String account  = "";
	
	private String cust_name= "";
	
	private MyApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_info);
		ActivityCollector.addActivity(this);//添加当前活动进行管理
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("最后一步");
		app = (MyApplication)getApplication();
		etPetName = (EditText) findViewById(R.id.et_pet_name);
		spBirth   = (Spinner) findViewById(R.id.s_birth);
		llCarModel = (LinearLayout) findViewById(R.id.ll_car_model); 
		tvCarModel = (TextView)findViewById(R.id.tv_car_model);
		tvCarModel.setOnClickListener(onClickListener);
		btnEnterSystem = (Button)findViewById(R.id.btn_enter_sys);
		btnEnterSystem.setOnClickListener(onClickListener);
		RadioGroup rg_identity = (RadioGroup) findViewById(R.id.rg_identity);
		rg_identity.setOnCheckedChangeListener(onCheckedChangeListener);
		RadioGroup rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
		rg_sex.setOnCheckedChangeListener(onCheckedChangeListener);
		
		Intent intent = getIntent();
		isPhone = intent.getBooleanExtra("isPhone", true);
		password = intent.getStringExtra("password");
		account = intent.getStringExtra("account");
		
		Toast.makeText(RegisterInfoActivity.this, isPhone + "=" + password + "=" + account, Toast.LENGTH_SHORT).show();
		getYear();
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
			case R.id.tv_car_model://汽车选择
				Intent i = new Intent(RegisterInfoActivity.this,WCarBrandSelector.class);
				startActivityForResult(i, CAR_SELECTOR_RESULT_CODE);
				break;
			case R.id.btn_enter_sys:
				save();
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
            
            case Msg.NAME_IS_EXIST:
            	submitRegisterInfo(msg.obj.toString());
            	break;	
            case Msg.SUBMIT_REGISTER_INFO:
            	Log.d(TAG, "====获取后台===" + msg.obj.toString());
            	jsonSubmitRegisterInfo(msg.obj.toString());
            	break;
            case Msg.GET_CUSTOMER_INFO:
            	Log.d(TAG, "====获取后台===" + msg.obj.toString());
            	jsonCustomer(msg.obj.toString());
            	break;
            }
        }
    };	
	


	/**
	 * 判断昵称是否存在
	 */
	private void save() {
		cust_name = etPetName.getText().toString().trim();
		if (cust_name.equals("")) {
			Toast.makeText(RegisterInfoActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		String url = Constant.BaseUrl + "exists?query_type=5&value=" + cust_name;
		//开启线程获取服务器数据
		new HttpThread.getDataThread(mHandler, url, Msg.NAME_IS_EXIST).start();
	}
	
	
	
	/**
	 * @param str 提交注册信息
	 */
	private void submitRegisterInfo(String str) {
		try {
			JSONObject json = new JSONObject(str);
			if (!json.getBoolean("exist")) {
				String url = Constant.BaseUrl + "customer/register?auth_code=127a154df2d7850c4232542b4faa2c3d";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				if (isPhone) {
					params.add(new BasicNameValuePair("mobile", account));
					params.add(new BasicNameValuePair("email", ""));
				} else {
					params.add(new BasicNameValuePair("mobile", ""));
					params.add(new BasicNameValuePair("email", account));
				}
				params.add(new BasicNameValuePair("password", SystemTools.getM5DEndo(password)));
				params.add(new BasicNameValuePair("cust_type", cust_type));
				params.add(new BasicNameValuePair("sex", sex));
				params.add(new BasicNameValuePair("birth", getBirth()));
				params.add(new BasicNameValuePair("province", app.Province));
				params.add(new BasicNameValuePair("city", app.City));
				params.add(new BasicNameValuePair("car_brand", carBrank));
				params.add(new BasicNameValuePair("car_series", carSeries));
				params.add(new BasicNameValuePair("service_type", "0"));
				params.add(new BasicNameValuePair("cust_name", cust_name));
				params.add(new BasicNameValuePair("qq_login_id", ""));
				params.add(new BasicNameValuePair("sina_login_id", ""));
				params.add(new BasicNameValuePair("logo", ""));
				params.add(new BasicNameValuePair("remark", ""));
				new NetThread.postDataThread(mHandler, url, params, Msg.SUBMIT_REGISTER_INFO).start();
			} else {
				Toast.makeText(RegisterInfoActivity.this, "昵称已存在", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param str 解析提交注册返回的信息  判断是否注册成功
	 */
	private void jsonSubmitRegisterInfo(String str) {
		try {
			JSONObject jsonObject = new JSONObject(str);
			String status_code = jsonObject.getString("status_code");
			if (status_code.equals("0")) {
				// TODO 注册成功，把数据处理好
				app.cust_id = jsonObject.getString("cust_id");
				app.auth_code = "127a154df2d7850c4232542b4faa2c3d";
				// 存储账号密码
				SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString(Constant.sp_account, account);
				editor.putString(Constant.sp_pwd, SystemTools.getM5DEndo(password));
				editor.commit();
				String url = Constant.BaseUrl + "customer/" + app.cust_id + "?auth_code=" + app.auth_code;
				//开启线程获取服务器数据
				new HttpThread.getDataThread(mHandler, url, Msg.GET_CUSTOMER_INFO).start();
//				Intent intent = new Intent(Constant.Wicare_Login);
//				sendBroadcast(intent);
			} else {
				Toast.makeText(RegisterInfoActivity.this, "注册失败，请重试", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param str
	 */
	private void jsonCustomer(String str) {
		SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(Constant.sp_customer + app.cust_id, str);
		editor.commit();
    	Toast.makeText(RegisterInfoActivity.this,"恭喜你注册成功", Toast.LENGTH_SHORT).show();
		ActivityCollector.finishAll();
	}
	
	

	
	/**
	 * 检查是否
	 */
	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (group.getCheckedRadioButtonId()) {
			case R.id.rb_no_car:
				llCarModel.setVisibility(View.GONE);
				cust_type = "0";
				break;
			case R.id.rb_car:
				llCarModel.setVisibility(View.VISIBLE);
				cust_type = "1";
				break;
			}
		}
	};
	
	/**
	 * 检查是否
	 */
	OnCheckedChangeListener onSexChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (group.getCheckedRadioButtonId()) {
			case R.id.rb_woman:
				sex = "1";
				break;
			case R.id.rb_man:
				sex = "0";
				break;
			}
		}
	};
	
	
	
	/**
	 * 获取年份数据并适配
	 */
	private void getYear() {
		Time time = new Time();
		time.setToNow();
		int year = time.year;
		List<Integer> years = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			years.add(year - i);
		}
		ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(RegisterInfoActivity.this, android.R.layout.simple_spinner_item, years);
		arrayAdapter.setDropDownViewResource(R.layout.item_spiner_drop_down);
		spBirth.setAdapter(arrayAdapter);
	}

	/**
	 * @return 生日时间
	 */
	private String getBirth() {
		Time time = new Time();
		time.setToNow();
		int year = time.year;
		return (year - spBirth.getSelectedItemPosition()) + "-01-01";
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		   case CAR_SELECTOR_RESULT_CODE:
		    Bundle car_msg = data.getExtras(); 
		    carBrank   = car_msg.getString("brank"); //汽车品牌
		    carSeries  = car_msg.getString("series");//汽车型号
		    carBrankId  = car_msg.getString("brankId"); //汽车品牌id
		    carSeriesId = car_msg.getString("seriesId");//汽车型号ID
		    tvCarModel.setText("  " + carBrank + "  " + carSeries);
		    break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	
}
