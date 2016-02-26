/*package com.wicare.wistormpublicdemo;

import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

*//**
 * @author Wu 消息提醒中心
 *
 *//*
public class ReminderActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reminder);
		
		ActivityCollector.addActivity(this);//添加activity进去活动管理
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("消息中心");
	}
	
	
	*//**
	 * 点击事件监听
	 *//*
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {			
			case R.id.iv_top_back://返回
				finish();
				break;
			}
		}
	};
	
	
	 (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	};

}
*/