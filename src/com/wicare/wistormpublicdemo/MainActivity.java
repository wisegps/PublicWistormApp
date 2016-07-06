package com.wicare.wistormpublicdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import widget.CustomerPopupWindow;
import widget.CustomerPopupWindow.OnItemClickListener;
import xutil.ActivityCollector;
import xutil.T;

import com.wicare.wistorm.ui.WTabBar;
import com.wicare.wistorm.ui.WTabBar.OnTabChangedListener;
import com.wicare.wistormpublicdemo.R;

import de.greenrobot.event.EventBus;
import eventbrocast.UpdataCarListEvent;
import eventbrocast.UpdataHomeFragment;
import fragment.FragmentHome;
import fragment.FragmentMap;
import fragment.FragmentMore;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import application.MyApplication;


/**
 * @author W
 *
 * 主页 https://github.com/wisegps/PublicWistormApp.git
 *
 */
public class MainActivity extends FragmentActivity{
	
	static final String TAG = "MainActivity";
	private HashMap<String, Fragment> fragments;
	private FragmentManager fragmentManager;
	private TextView titleName;//标题 name
	private ImageView iv_arrow;
	private LinearLayout ll_selector;
	//导航栏未选中图标
	private int[] imgNormal = { R.drawable.ic_home_normal,R.drawable.ic_map_normal,R.drawable.ic_more_normal };
	//导航栏选择图标
	private int[] imgPress = {R.drawable.ic_home_press,R.drawable.ic_map_press,R.drawable.ic_more_press };
	//导航栏的名字
	private String[] itemNames = {"首页","定位","更多"};
	
	private MyApplication app;
	private Context mContext;
	private List<String> items;//车辆的名字列表集合
	private String carName;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        app = (MyApplication)getApplication();
        //注册EventBus  
        EventBus.getDefault().register(this);  
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        fragmentManager = getSupportFragmentManager();
        fragments = new HashMap<String, Fragment>();
        WTabBar ll = (WTabBar) findViewById(R.id.tabbarLayout);
        titleName = (TextView) findViewById(R.id.tv_top_title);
        ll.setOnTabChangedListener(onTabChangedListener);
        ll.setTabText(itemNames); //设置导航栏的名字
        ll.setTabImage(imgNormal, imgPress);//设置导航栏的图标   
        showFragment("home");
        titleName.setText(itemNames[0]);
        iv_arrow = (ImageView)findViewById(R.id.iv_bottom_arrow);
        iv_arrow.setVisibility(View.VISIBLE);
        ll_selector = (LinearLayout)findViewById(R.id.ll_select);
        ll_selector.setOnClickListener(onClickListener);
        ActivityCollector.addActivity(this);//添加当前活动进行管理
        items = new ArrayList<String>();
        if(app.carDatas.size()==0){
        	carName = "NaN";
        	T.showLong(mContext, "您没有添加车辆，请先创建车辆");
        }else{
        	getCarList();
        	carName = items.get(0);
    		titleName.setText(carName);
        }
    }
	
    /**
     *  导航栏选项卡切换
     */
    OnTabChangedListener onTabChangedListener = new OnTabChangedListener() {
		
		@Override
		public void onTabClick(int index) {

			switch (index) {
			case 0:
				showFragment("home");
				if(items.size()==0){
					titleName.setText("NaN");
				}else{
					titleName.setText(carName);
				}
				iv_arrow.setVisibility(View.VISIBLE);
				break;
			case 1:
				showFragment("map");
				titleName.setText(itemNames[index]);
				iv_arrow.setVisibility(View.GONE);
				break;
			case 2:
				showFragment("more");
				titleName.setText(itemNames[index]);
				iv_arrow.setVisibility(View.GONE);
				break;
			}
		}
	};
	
	/**
	 * click
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			if(items.size()>1){
				iv_arrow.setImageResource(R.drawable.ic_bottom_arrow);
				final CustomerPopupWindow popView = new CustomerPopupWindow(MainActivity.this);
				popView.initView(v,400,LayoutParams.WRAP_CONTENT);
				popView.setData(items);
				popView.SetOnItemClickListener(new OnItemClickListener() {

					@Override
					public void OnItemClick(int index) {
						// TODO Auto-generated method stub
						popView.dismiss();
						iv_arrow.setImageResource(R.drawable.ic_top_arrow);
						titleName.setText(items.get(index));
						String device_id = app.carDatas.get(index).getDevice_id();
						if("0".equals(device_id)){
							T.showLong(mContext, "该车辆尚未绑定设备");
						}else{
							EventBus.getDefault().post(new UpdataHomeFragment(device_id));
						}
					}
				});	
			}else{
				T.showLong(mContext, "您没有添加车辆，请先创建车辆");
			}
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
	
	
	//SecondEvent接收函数三  
	public void onEventAsync(UpdataCarListEvent event){  
	    Log.d(TAG, "更新数据信息：" + event.getMsg());  
	    getCarList();
	    if("logout_updata".equals(event.getMsg())){
	    	carName= "NaN";
	    }else if("login_again".equals(event.getMsg())){
	    	if(items.size()>0){
	    		carName= items.get(0);
	    	}else{
	    		carName= "NaN";
	    	}
	    }
	}  
	
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
		EventBus.getDefault().unregister(this);
	}
	
	
	/**
	 * 获取车辆列表
	 */
	private void getCarList(){
		items.clear();
		Log.d(TAG, "清除items信息：" + items.toString());  
		for(int i=0;i<app.carDatas.size();i++){
			items.add(app.carDatas.get(i).getNick_name());
		}
	}

}
