package com.wicare.wistormpublicdemo.app;

import android.app.Application;

public class MyApplication extends Application{
	
	/** auth_code **/
	public String auth_code;
	/** 用户类别 **/
	public int cust_type = 0;
	/** cust_id **/
	public String cust_id;
	/** 用户名称 **/
	public String cust_name = "";

	/**
	 * 当前位置
	 */
	public String Adress = "";
	/**
	 * 当前定位城市
	 */
	public String City = "深圳";
	public String Province = "广东省";

}
