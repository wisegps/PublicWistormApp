package com.wicare.wistormpublicdemo;

import java.util.HashMap;

import model.CarData;

import org.json.JSONArray;
import org.json.JSONObject;

import xutil.L;
import xutil.T;

import com.android.volley.VolleyError;
import com.wicare.wistorm.api.WUserApi;
import com.wicare.wistorm.api.WVehicleApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.toolkit.SystemTools;
import com.wicare.wistorm.ui.WInputField;

import de.greenrobot.event.EventBus;
import eventbrocast.UpdataCarListEvent;
import eventbrocast.UpdataHomeFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import application.Constant;
import application.MyApplication;

public class LoginActivity extends Activity{
	
	private static final String TAG = "LoginActivity";
	private WInputField edAccount;
	private WInputField edPassword;
	private Button btnLogin;
	private TextView tvUpdataPassword;
	private TextView tvRegister;
	
	private String account;
	private String password;
	private String loginMsg = "";
	public WUserApi userApi;
	public MyApplication application;
	
	public SharedPreferences pref;
	public SharedPreferences.Editor editor;
	
	private Context mContext;
	private WVehicleApi vehicleApi;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		mContext = LoginActivity.this;
		
		editor = getSharedPreferences(Constant.SP_USER_DATA, MODE_PRIVATE).edit();
    	pref = getSharedPreferences(Constant.SP_USER_DATA, MODE_PRIVATE); 

		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("登陆");
    	
		application = (MyApplication)getApplication();
		edAccount  = (WInputField)findViewById(R.id.ed_account);
		edPassword = (WInputField)findViewById(R.id.ed_password);
		btnLogin   = (Button)findViewById(R.id.btn_login);
		tvUpdataPassword = (TextView)findViewById(R.id.tv_password);
		tvRegister = (TextView)findViewById(R.id.tv_register);		
		btnLogin.setOnClickListener(onClickListener);
		tvUpdataPassword.setOnClickListener(onClickListener);
		tvRegister.setOnClickListener(onClickListener);
		edAccount.setText(pref.getString(Constant.SP_ACCOUNT,""));
		init();
		Intent intent = getIntent();
		loginMsg = intent.getStringExtra("loginMsg");
	}
	
	
	/**
	 * wistorm api接口网络请求初始化
	 */
	private void init(){
		vehicleApi = new WVehicleApi();
		userApi = new WUserApi();
		BaseVolley.init(mContext);
	}
	
	
	/**
	 * OnClickListener
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btn_login:
				if(SystemTools.isNetworkAvailable(LoginActivity.this)){
					login();
				}else{
					AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
					dialog.setTitle("提示");
					dialog.setMessage("当前网络未连接");
					dialog.setPositiveButton("去打开", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivity(new Intent("android.settings.WIFI_SETTINGS"));
						}
					});
					dialog.setNegativeButton("取消", null);
					dialog.show();
				}
				break;
			case R.id.tv_password:
				Intent intent_password = new Intent(LoginActivity.this,UpdataPasswordActivity.class);
				startActivity(intent_password);
				break;
				
			case R.id.tv_register:
				Intent intent_register = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivity(intent_register);
				break;
			}
		}
	};

	
	
	/**
	 * login
	 */
	private void login(){
		account  = edAccount.getText() .toString().trim();
		password = edPassword.getText().toString().trim();
		if("".equals(account) || "".equals(password)){
			T.showShort(LoginActivity.this, "账号或者密码不能为空");
			return;
		}
		editor.putString(Constant.SP_ACCOUNT, account);
		editor.putString(Constant.SP_PWD, password);
    	editor.commit();
    	userApi.login(account, password, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
//				{"status_code":0,"cust_id":1219,"cust_name":"Dancan","access_token":"f1b3afaf9bbedfcb0ca3f0465a1d2e7e157c1ea55ad8d2dbcaa7083d125d360c20e75f99257980957f3220a23c5df2b6","valid_time":"2016-07-01T11:01:14.677Z"}
				parseLogin(response);
			}
		},new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * @param strJson
	 */
	private void parseLogin(String strJson){
		L.d(TAG,"=======" + strJson);	
		try {
			JSONObject object = new JSONObject(strJson);			
			application.access_token = object.getString("access_token");
			application.cust_id      = object.getString("cust_id");
			application.cust_name    = object.getString("cust_name");
			
			L.d(TAG,application.cust_id     + "\n" 
					+ application.cust_name + "\n" 
					+ application.access_token);
			
			getVehileList();//获取车辆信息
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取车辆信息列表
	 */
	private void getVehileList(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", application.access_token);
//		params.put("seller_id", seller_id);//商户ID  
		params.put("cust_id", application.cust_id);//不是商户的时候 用 cust_id
		params.put("sorts", "obj_id");
		params.put("limit", "-1");
		String fields = "nick_name,cust_name,car_series,car_brand_id,cust_id,device_id";
		vehicleApi.list(params, fields, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				L.d(TAG, "我的车辆列表信息：" + response);
				parseVehicleList(response);
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	/**
	 * @param strJson 
	 */
	private void parseVehicleList(String strJson){
		application.carDatas.clear();
		try {
			JSONObject jsonObject =  new JSONObject(strJson);
			JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
			
			for(int i=0;i<jsonArray.length();i++){
				L.d(TAG, "第  " + i + " 辆车信息：" + jsonArray.get(i).toString());
				CarData cardata = new CarData();
//				第  0 辆车信息：{"nick_name":"timm","cust_name":"Dancan","car_series":"奥迪A4","car_brand_id":9,"device_id":0,"obj_id":2884,"cust_id":1219,"mobile":"13537687553"}
				JSONObject object = new JSONObject(jsonArray.get(i).toString());
				cardata.setCar_brand_id(object.getString("car_brand_id"));
				cardata.setCar_serial(object.getString("car_series"));
				cardata.setNick_name(object.getString("nick_name"));
				cardata.setObj_id(object.getString("obj_id"));
				if(object.has("device_id")){
					cardata.setDevice_id(object.getString("device_id"));
				}else{
					cardata.setDevice_id("0");
				}
				application.carDatas.add(cardata);
			}
			
			L.d(TAG, "test信息： " + application.carDatas.size());
			application.isLogin = true;
			if("signIn".equals(loginMsg)){
				finish();
				EventBus.getDefault().post(new UpdataCarListEvent("login_again")); 
				EventBus.getDefault().post(new UpdataHomeFragment(application.carDatas.get(0).getDevice_id()));  
			}else {
				Intent intent_main = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent_main);
				LoginActivity.this.finish();
			}	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			L.d(TAG, "错误信息： " + e.toString());
		}
	}
	
	
}
