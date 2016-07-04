package com.wicare.wistormpublicdemo.app;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.wicare.wistormpublicdemo.model.CarData;

import android.app.Application;

public class MyApplication extends Application{
	

	public String access_token;
	
	/** auth_code **/
	public String auth_code;
	/** 用户类别 **/
	public int cust_type = 0;
	/** cust_id **/
	public String cust_id;
	/** 用户名称 **/
	public String cust_name = "";
	/** 当前定位城市 */
	public String City = "深圳";
	
	public double lat;
	public double lon;
	
	/** 车辆信息 **/
	public List<CarData> carDatas = new ArrayList<CarData>();
	/** 是否登陆  **/
	public boolean isLogin = false;
	
	
	public String Token = "";
	
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(this);
	}
	
	

}
