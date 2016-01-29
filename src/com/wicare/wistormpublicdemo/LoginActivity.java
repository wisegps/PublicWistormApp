package com.wicare.wistormpublicdemo;

import org.json.JSONObject;

import com.wicare.wistorm.http.HttpThread;
import com.wicare.wistorm.toolkit.SystemTools;
import com.wicare.wistorm.ui.WLoading;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.model.JsonData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu
 * 
 *  登陆页面
 */
public class LoginActivity extends Activity {

	static final String TAG = "LoginActivity";
	/*登陆*/
	private Button btnLogin;
	/*帐号输入*/
	private EditText etAccount;
	/*密码输入*/
	private EditText etPassword;
	/*警告*/
	private TextView etWarming;
	/*帐号*/
	private String account;
	/*密码*/
	private String password;
	/*加载框*/
	private WLoading mWLoading = null;
	
	private String loginMsg = "";
	
	private MyApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		app = (MyApplication)getApplication();
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("登陆");
		findViewById(R.id.tv_register).setOnClickListener(onClickListener);
		findViewById(R.id.tv_forgot_password).setOnClickListener(onClickListener);
		btnLogin = (Button)findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(onClickListener);
		etAccount  = (EditText)findViewById(R.id.et_account);
		etPassword = (EditText)findViewById(R.id.et_pwd);
		etWarming  = (TextView)findViewById(R.id.tv_warming);
		SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
		etAccount.setText(preferences.getString(Constant.sp_account, ""));
		
		Intent intent = getIntent();
		loginMsg = intent.getStringExtra("loginMsg");
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
			case R.id.tv_register://注册
				Intent i_register = new Intent(LoginActivity.this,RegisterActivity.class);
				startActivity(i_register);
				break;	
			case R.id.tv_forgot_password://忘记密码
				Intent i_forgot_pwd = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
				startActivity(i_forgot_pwd);
				break;
			case R.id.btn_login://登陆
				accountLogin();
				etWarming.setVisibility(View.GONE);
				
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
            
            case Msg.ACCOUNT_LOGIN:
            	Log.d(TAG, "====登陆信息===" + msg.obj.toString());
            	jsonLogin(msg.obj.toString());
            	break;
            	
            case Msg.GET_CUSTOMER:
            	Log.d(TAG, "====用户信息信息===" + msg.obj.toString());
            	jsonCustomer(msg.obj.toString());
            	break;
            	
            case Msg.GET_CAR_DATA:
            	Log.d(TAG, "====车辆信息===" + msg.obj.toString());
            	jsonCarData(msg.obj.toString());
            	break;
            }
        }
    };	
    
    
    
    /**
     * @param strJson  解析登陆信息
     */
    private void jsonLogin(String strJson){
    	try {
    		JSONObject jsonObject = new JSONObject(strJson);
    		if(jsonObject.getString("status_code").equals("0")){
    			//用户ID
    			app.cust_id   = jsonObject.getString("cust_id");
    			//授权码
    			app.auth_code = jsonObject.getString("auth_code");
    			//用户昵称
    			app.cust_name = jsonObject.getString("cust_name");
				SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString(Constant.sp_account, account);
				editor.putString(Constant.sp_pwd, SystemTools.getM5DEndo(password));
				editor.commit();
				getCustomer();
    		}else{
    			etWarming.setVisibility(View.VISIBLE);
    			stopProgressDialog();
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}	
    }
	

	/**
	 * @param str 解析个人信息
	 */
	private void jsonCustomer(String str) {
		SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
		// 按sp_customer+id的格式保存，可以保存多个登录的信息
		Editor editor = preferences.edit();
		editor.putString(Constant.sp_customer + app.cust_id, str);
		editor.commit();
		try {
			JSONObject jsonObject = new JSONObject(str);
			if (jsonObject.opt("status_code") == null) {
				int cust_type = jsonObject.getInt("cust_type");
				//用户类型 
				app.cust_type = cust_type;
				getCarData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    
    /** 获取用户信息 **/
	private void getCustomer() {
		String url = Constant.BaseUrl + "customer/" + app.cust_id + "?auth_code=" + app.auth_code;
		new HttpThread.getDataThread(mHandler, url, Msg.GET_CUSTOMER).start();
	}
	
	
	/** 获取车辆信息 **/
	private void getCarData() {
		String url = Constant.BaseUrl + "customer/" + app.cust_id + "/vehicle?auth_code=" + app.auth_code;
		Log.i("LoginActivity", " 获取车辆信息: "+url);
		new HttpThread.getDataThread(mHandler, url, Msg.GET_CAR_DATA).start();
	}
	
	/** 账号密码登录 **/
	private void accountLogin() {
		if (SystemTools.isNetworkAvailable(LoginActivity.this)) {
			account  = etAccount.getText().toString().trim();
			password = etPassword.getText().toString().trim();
			if (account.equals("") || password.equals("")) {
				Toast.makeText(LoginActivity.this, "请输入账号或者密码", Toast.LENGTH_SHORT).show();
				return;
			}
			String url = Constant.BaseUrl + "user_login?account=" + account + "&password=" + SystemTools.getM5DEndo(password);
			Log.i("LoginActivity", url);
			new HttpThread.getDataThread(mHandler, url, Msg.ACCOUNT_LOGIN).start();
			startProgressDialog();//提示框
		} else {
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
	}
	
	
	/** 解析车辆信息 **/
	private void jsonCarData(String str) {
		app.carDatas.clear();
		app.carDatas.addAll(JsonData.jsonCarInfo(str));
		// 发出登录广播信号
		Intent intent = new Intent(Constant.Wicare_Login);
		sendBroadcast(intent);
		app.isLogin = true;
		stopProgressDialog();
		if("signIn".equals(loginMsg)){
			finish();
		}else {
			Intent intent_main = new Intent(LoginActivity.this,MainActivity.class);
			startActivity(intent_main);
			LoginActivity.this.finish();
		}	
	}
	
	/**
	 * 开始显示加载
	 */
	private void startProgressDialog() {
		if (mWLoading == null) {
			mWLoading = WLoading.createDialog(this);
			mWLoading.setMessage("登陆中...");
		}
		mWLoading.show();
	}

	/**
	 * 关闭加载提示
	 */
	private void stopProgressDialog() {
		if (mWLoading != null) {
			mWLoading.dismiss();
			mWLoading = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
