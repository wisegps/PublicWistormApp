package com.wicare.wistormpublicdemo;

import android.os.Bundle;

import com.wicare.wistorm.toolkit.WAboutApp;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

/**
 * @author Wu 关于页面
 *
 */
public class AboutApp extends WAboutApp{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActivityCollector.addActivity(this);//添加当前活动进行管理
		
		setAppLogo(R.drawable.ws_ico_baba);
		setAppCopyright("Copyright @ 2015-2016 Wicare");
		setAppVersion("叭叭 V 1.0.1");
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

}
