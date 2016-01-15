package com.wicare.wistormpublicdemo;

import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Wu
 * 
 * 忘记密码页面
 */
public class ForgotPasswordActivity extends Activity {

	static final String TAG = "ForgotPasswordActivity";
	private EditText etAccount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		ActivityCollector.addActivity(this);//添加当前活动进行管理
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("忘记密码");
		findViewById(R.id.btn_forgot_pwd).setOnClickListener(onClickListener);
		etAccount = (EditText)findViewById(R.id.et_account_num);
		
		
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
			case R.id.btn_forgot_pwd:
				
				break;
			}
		}
	};
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	
}
