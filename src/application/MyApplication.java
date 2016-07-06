package application;

import java.util.ArrayList;
import java.util.List;

import model.CarData;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class MyApplication extends Application{
	

	public String access_token;
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

	
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(this);
	}
	
	

}
