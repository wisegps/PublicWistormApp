package com.wicare.wistormpublicdemo;


import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Wu
 * 
 *  登陆页面
 */
public class LoginActivity extends Activity {

	static final String TAG = "LoginActivity";
	private Button btnLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
//		ActivityCollector.addActivity(this);//添加当前活动进行管理
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("登陆");
		findViewById(R.id.tv_register).setOnClickListener(onClickListener);
		findViewById(R.id.tv_forgot_password).setOnClickListener(onClickListener);
		btnLogin = (Button)findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(onClickListener);
		
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
				
				break;
				
			}
		}
	};
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		ActivityCollector.removeActivity(this);
	}
}
