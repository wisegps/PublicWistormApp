package com.wicare.wistormpublicdemo;

import android.os.Bundle;

import com.wicare.wistorm.toolkit.WAboutApp;

/**
 * @author Wu 关于页面
 *
 */
public class AboutApp extends WAboutApp{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAppLogo(R.drawable.ws_ico_baba);
		setAppCopyright("Copyright @ 2015-2016 Wicare");
		setAppVersion("叭叭 V 1.0.1");
	}

}
