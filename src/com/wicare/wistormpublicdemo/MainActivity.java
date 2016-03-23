package com.wicare.wistormpublicdemo;

import java.util.HashMap;
import java.util.Iterator;

import com.baidu.mapapi.SDKInitializer;
import com.wicare.wistorm.ui.WTabBar;
import com.wicare.wistorm.ui.WTabBar.OnTabChangedListener;
import com.wicare.wistormpublicdemo.R;
import com.wicare.wistormpublicdemo.app.HandlerMsg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.fragment.FragmentHome;
import com.wicare.wistormpublicdemo.fragment.FragmentMap;
import com.wicare.wistormpublicdemo.fragment.FragmentMore;
import com.wicare.wistormpublicdemo.model.CarData;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;
import com.wicare.wistormpublicdemo.xutil.HttpCarInfo;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity{
	
	static final String TAG = "MainActivity";
	private HashMap<String, Fragment> fragments;
	private FragmentManager fragmentManager;
	private TextView titleName;//标题 name
	//导航栏未选中图标
	private int[] imgNormal = { 
		R.drawable.ic_home_normal,
		R.drawable.ic_map_normal,
		R.drawable.ic_more_normal };
	//导航栏选择图标
	private int[] imgPress = { 
		R.drawable.ic_home_press,
		R.drawable.ic_map_press,
		R.drawable.ic_more_press };
	//导航栏的名字
	private String[] itemNames = {"首页","定位","更多"};
	
	private Handler uiHander = null;//ui更新
	private HttpCarInfo httpCarInfo;
	private CarData carData;
	private MyApplication app;
	private String device_id;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext()); 
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);  ////////////////////////////
        fragmentManager = getSupportFragmentManager();
        fragments = new HashMap<String, Fragment>();
        WTabBar ll = (WTabBar) findViewById(R.id.tabbarLayout);
        titleName = (TextView) findViewById(R.id.tv_top_title);
        ll.setOnTabChangedListener(onTabChangedListener);
        ll.setTabText(itemNames); //设置导航栏的名字
        ll.setTabImage(imgNormal, imgPress);//设置导航栏的图标   
        showFragment("home");
        titleName.setText(itemNames[0]);
        ActivityCollector.addActivity(this);//添加当前活动进行管理
        
        app = (MyApplication)getApplication();
		uiHander = new Handler(handleCallBack);
		httpCarInfo = new HttpCarInfo(MainActivity.this, uiHander);
		carData = app.carDatas.get(0);
		device_id = carData.getDevice_id();   
    }
    
    

    /**
	 * Handler 回调
	 */
	public Handler.Callback handleCallBack = new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
		
			Bundle bundle = msg.getData();
			switch (msg.what) {
			
			case HandlerMsg.GET_CAR_GPS_DATA:
				app.lon = bundle.getDouble("lon");
				app.lat = bundle.getDouble("lat");
				break;
			}
			return true;
		}
	};
	
    /**
     *  导航栏选项卡切换
     */
    OnTabChangedListener onTabChangedListener = new OnTabChangedListener() {
		
		@Override
		public void onTabClick(int index) {

			switch (index) {
			case 0:
				showFragment("home");
				break;
			case 1:
				showFragment("map");
				break;
			case 2:
				showFragment("more");
				break;
			}
			titleName.setText(itemNames[index]);
		}
	};
	
	
	
	/**
	 * @param name 显示某个fragment
	 */
	private void showFragment(String name){
		FragmentTransaction t;
		
		Fragment fragment = fragments.get(name);
		if (fragment == null) {
			if (name.equals("home")) {// 首页
				fragment = new FragmentHome();
			} else if (name.equals("map")) {// 定位
				fragment = new FragmentMap();
			}else if (name.equals("more")) {// 设置
				fragment = new FragmentMore();
				OnFinishListener finishListener = new OnFinishListener() {
					@Override
					public void onFinish() {
						finish();
					}
				};
				((FragmentMore) fragment).setOnFinishListener(finishListener);
			}
			fragments.put(name, fragment);
			Fragment last = fragmentManager.findFragmentByTag(name);
			t = fragmentManager.beginTransaction();
			if (last != null && last.isAdded()) {
				Log.i("MainActivity", "删除一个fragment");
				t.remove(last).commit();
			}
			t = fragmentManager.beginTransaction();
			t.add(R.id.main_content, fragment, name).commit();
		}

		// 先隐藏所有fragment
		Iterator<String> keys = fragments.keySet().iterator();
		while (keys.hasNext()) {
			t = fragmentManager.beginTransaction();
			String key = (String) keys.next();
			Fragment value = fragments.get(key);
			if (value != null) {
				t.hide(value);
				t.commit();
			}
		}
		t = fragmentManager.beginTransaction();
		// 然后显示所要显示的fragment
		t.show(fragment);
		t.commit();
	}
	
	
	@Override
	public void finish() {
		super.finish();
		// 删除所有fragment
		FragmentTransaction trans = null;
		String name[] = { "home", "map", "more" };
		for (int i = 0; i < name.length; i++) {
			Fragment last = fragmentManager.findFragmentByTag(name[i]);
			if (last != null) {
				trans = fragmentManager.beginTransaction();
				trans.remove(last).commit();
			}
		}
	}
	
	/**
	 * @author Wu
	 */
	public interface OnFinishListener {
		public abstract void onFinish();
	}
	
	
	boolean isResume = false;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isResume = true;
		refreshGpsData();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isResume = false;
		ActivityCollector.removeActivity(this);
	}
	
	/**
	 * 刷新数据 refreshGpsData
	 */
	public void refreshGpsData() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(isResume){
					if (device_id == null || device_id.equals("")) {
						continue;
					}else{
						httpCarInfo.requestGps(device_id);
					}
					SystemClock.sleep(10000);
				}
			}
		}).start();
	}

}
