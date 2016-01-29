package com.wicare.wistormpublicdemo;

import com.wicare.wistorm.toolkit.WCitySelector;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Wu 设置中心页面
 *
 */
public class SettingActivity extends Activity {
	
	private static final int REQUEST_CODE = 0;
	// 城市
	private TextView tvCity;
	// login_out
	private Button btnLoginOut;
	
	private TextView tv_sign;
	
	MyApplication app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		app = (MyApplication)getApplication();
		ActivityCollector.addActivity(this);//添加activity进去活动管理
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("设置中心");
		tvCity = (TextView) findViewById(R.id.tv_city);
		
		RelativeLayout rl_account =  (RelativeLayout) findViewById(R.id.rl_my_account);
		rl_account.setOnClickListener(onClickListener);
		
		RelativeLayout rl_my_car  =  (RelativeLayout) findViewById(R.id.rl_my_car);
		rl_my_car.setOnClickListener(onClickListener);
		
		RelativeLayout rl_LocationCity =  (RelativeLayout) findViewById(R.id.rl_location_city);
		rl_LocationCity.setOnClickListener(onClickListener);
		
		RelativeLayout rl_about =  (RelativeLayout) findViewById(R.id.rl_about);
		rl_about.setOnClickListener(onClickListener);
		
		btnLoginOut = (Button)findViewById(R.id.btn_exit);
		btnLoginOut.setOnClickListener(onClickListener);
		
		tv_sign = (TextView)findViewById(R.id.tv_sign_in);
		
		mHandler.sendEmptyMessage(Msg.LOGIN_UPDATA_UI);
	}
	
	
	
	/**
     * Handler 处理消息
     */
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
    	
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            
            case Msg.LOGIN_UPDATA_UI:
            	if(app.isLogin){
            		btnLoginOut.setVisibility(View.VISIBLE);
            		tv_sign.setText(app.cust_name);
            	}else{
            		btnLoginOut.setVisibility(View.GONE);
            		tv_sign.setText("登陆/注册");
            	}
            	break;
            }
        }
    };	
	

    
	/**
	 * 点击事件监听  
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_my_account://帐号登陆/注册 
				if(!app.isLogin){
					Intent i_login = new Intent(SettingActivity.this,LoginActivity.class);
					i_login.putExtra("loginMsg", "signIn");
					startActivity(i_login);
				}
				break;

			case R.id.rl_location_city:
				Intent i_SwitchCity = new Intent(SettingActivity.this,WCitySelector.class);
				startActivityForResult(i_SwitchCity, REQUEST_CODE);
				break;
				
			case R.id.rl_about:
				Intent i_about = new Intent(SettingActivity.this,AboutApp.class);
				startActivity(i_about);
				break;
				
			case R.id.rl_my_car:
				Intent i_add_cars = new Intent(SettingActivity.this,MyCarsActivity.class);
				startActivity(i_add_cars);
				break;
			
			case R.id.iv_top_back://返回
				finish();
				break;
				
			case R.id.btn_exit://退出登陆
				loginOut();
				break;
			}
		}
	};
	
	
	/**
	 * 退出登陆
	 */
	private void loginOut(){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString(Constant.sp_pwd, "");
				editor.putString(Constant.sp_account, "");
				editor.commit();
				app.cust_id = null;
				app.auth_code = null;
				app.carDatas.clear();
				Intent intent = new Intent(Constant.Wicare_Login_Out);
				sendBroadcast(intent);
				app.isLogin = false;
				mHandler.sendEmptyMessage(Msg.LOGIN_UPDATA_UI);
			}		
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			try{
				String city  = data.getExtras().getString("cityName");
				tvCity.setText(city);
				app.City = city;
            }catch (Exception e) {
                e.printStackTrace();
            }
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(Msg.LOGIN_UPDATA_UI);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	};
	
	

}
