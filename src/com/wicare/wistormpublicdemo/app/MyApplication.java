package com.wicare.wistormpublicdemo.app;

import java.util.ArrayList;
import java.util.List;

import com.wicare.wistormpublicdemo.model.CarData;

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
	/** 当前位置 */
	public String Adress = "";
	/** 当前定位城市 */
	public String City = "深圳";
	/** 当前省份 */
	public String Province = "广东省";
	/** 车辆信息 **/
	public List<CarData> carDatas = new ArrayList<CarData>();

}
