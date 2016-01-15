package com.wicare.wistormpublicdemo;

import com.wicare.wistorm.toolkit.WCitySelector;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu 设置中心页面
 *
 */
public class SettingActivity extends Activity {
	
	private static final int REQUEST_CODE = 0;
	private TextView tvCity;//城市
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_setting);
		ActivityCollector.addActivity(this);//添加activity进去活动管理
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("设置中心");
		
		tvCity = (TextView) findViewById(R.id.tv_city);
		
		RelativeLayout rl_account =  (RelativeLayout) findViewById(R.id.rl_my_account);
		rl_account.setOnClickListener(onClickListener);
		
		RelativeLayout rl_LocationCity =  (RelativeLayout) findViewById(R.id.rl_location_city);
		rl_LocationCity.setOnClickListener(onClickListener);
		RelativeLayout rl_about =  (RelativeLayout) findViewById(R.id.rl_about);
		rl_about.setOnClickListener(onClickListener);
		
	}
	
	
	/**
	 * 点击事件监听
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_my_account:
				Toast.makeText(SettingActivity.this, "我的账户", Toast.LENGTH_SHORT).show();
				break;

			case R.id.rl_location_city:
				Intent i_SwitchCity = new Intent(SettingActivity.this,WCitySelector.class);
				startActivityForResult(i_SwitchCity, REQUEST_CODE);
				break;
				
			case R.id.rl_about:
				Intent i_about = new Intent(SettingActivity.this,AboutApp.class);
				startActivity(i_about);
				break;
				
			case R.id.iv_top_back://返回
				finish();
				break;
			}
		}
	};
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case 0:
			try{
				tvCity.setText(data.getExtras().getString("cityName"));
            }catch (Exception e) {
                e.printStackTrace();
            }
			break;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	};

}
