/*package com.wicare.wistormpublicdemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wicare.wistorm.ui.pickerview.WTimePopupWindow;
import com.wicare.wistorm.ui.pickerview.WTimePopupWindow.OnTimeSelectListener;
import com.wicare.wistorm.ui.pickerview.WTimePopupWindow.Type;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.HandlerMsg;
import com.wicare.wistormpublicdemo.ui.AirTimersListAdapter;
import com.wicare.wistormpublicdemo.ui.AirTimersListAdapter.OnDeleteTimerListener;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

*//**
 * @author Wu 空气净化器设置页面
 *
 *//*
public class AirSettingActivity extends Activity {
	
	private static String TAG = "AirSettingActivity";
	private Switch switchMode, switchTimer;//模式切换
	private String device_id = "";//设备ID
	
	private HttpAir httpAir;
	private Handler uiHandler = null; 
	private String url = "";
	
	private List<String> listTimesData;
	private ListView lvTimers;//定时器列表
	private AirTimersListAdapter timersAdapter;//定时器列表适配器
	
	private WTimePopupWindow pwTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_air_setting);
		ActivityCollector.addActivity(this);//添加当前活动进行管理
		
		url = getIntent().getStringExtra("air_url");
		device_id = getIntent().getStringExtra("device_id");
		
		uiHandler = new Handler(handleCallBack);
		httpAir = new HttpAir(this, uiHandler);
		httpAir.requestAir(url);
		
		
		TextView tvTitle = (TextView)findViewById(R.id.tv_top_title);
		tvTitle.setText("净化器设置");
		
		pwTime = new WTimePopupWindow(AirSettingActivity.this, Type.HOURS_MINS);
        pwTime.setOnTimeSelectListener(new OnTimeSelectListener() {
			
			@Override
			public void onTimeSelect(Date date) {
				// TODO Auto-generated method stub
				listTimesData.add(getTime(date));
				timersAdapter.notifyDataSetChanged();
			}
		});
		
		导航栏
		ImageView ivBack = (ImageView)findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);

		净化器模式开关
		switchMode  = (Switch) findViewById(R.id.switchMode);
		switchMode.setOnTouchListener(onTouchListener);
		定时开启净化器
		switchTimer = (Switch) findViewById(R.id.switchTimer);
		switchTimer.setOnTouchListener(onTouchListener);
		
		listTimesData = new ArrayList<String>();
		timersAdapter = new AirTimersListAdapter(this);
		timersAdapter.setDatas(listTimesData);
		
		定时器列表      ------ （  最多只能设置五个定时器  ！！！！！！！！！）
		lvTimers = (ListView)findViewById(R.id.lv_timers);
        lvTimers.addFooterView(LayoutInflater.from(this).inflate(R.layout.item_list_timers_foot_add, null));
        lvTimers.setAdapter(timersAdapter);
        lvTimers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == listTimesData.size()){
					if(listTimesData.size()>4){
						Toast.makeText(AirSettingActivity.this, 
								getResources().getString(R.string.max_air_timers), Toast.LENGTH_SHORT).show();
						return;					
						}
					pwTime.showAtLocation(view, Gravity.BOTTOM, 0, 0, new Date());
				}
			}
		});
        
        
        timersAdapter.setOnDeleteTimerListener(new OnDeleteTimerListener() {
			
			@Override
			public void onDeleteTimer(int position) {
				// TODO Auto-generated method stub
				listTimesData.remove(position);
				timersAdapter.notifyDataSetChanged();
			}
		});
        
        
        
        
	}
	
	
	*//**
	 * Handler 回调
	 *//*
	public Handler.Callback handleCallBack = new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
		
			Bundle bundle = msg.getData();
			switch (msg.what) {
			case HandlerMsg.GET_CAR_AIR:
				Log.e(TAG, "----------"+msg.obj);
				initValue((Air) msg.obj);
				break;
			}
			return true;
		}
	};
	
	*//**
	 * 点击事件
	 *//*
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.iv_top_back:
//				setMode();
				
				for(int i=0;i<timersAdapter.getListTimersEnable().size();i++){
					Log.i("AirSettingActivity", timersAdapter.getListTimersEnable().get(i));
				}
				
				
//				AirSettingActivity.this.finish();
				break;
			}
		}
	};
	
	
	*//**
	 * 触摸事件
	 *//*
	private OnTouchListener onTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.switchMode:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					switchTimer.setChecked(false);
					lvTimers.setVisibility(View.GONE);
				}
				break;
			case R.id.switchTimer:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					switchMode.setChecked(false);
					if (switchTimer.isChecked()) {
						lvTimers.setVisibility(View.GONE);
					} else {
						lvTimers.setVisibility(View.VISIBLE);
					}
				}
				break;
			}
			return false;
		}
	};
	
	
	
	*//**
	 * 初始化界面
	 * 
	 * @param air
	 *//*
	public void initValue(Air air) {
		int mode = air.getAir_mode();
		switchMode.setChecked(false);
		switchTimer.setChecked(false);
		if (mode == Constant.AIR_MODE_SMART) {
			switchMode.setChecked(true);
		} else if (mode == Constant.AIR_MODE_TIMER) {
			switchTimer.setChecked(true);
//			time = air.getAir_time();		
//			tvAirTimer.setText(air.getAir_time());
			int duration = air.getAir_duration();
			Log.i("AirSettingActivity", duration+"");
			lvTimers.setVisibility(View.VISIBLE);
			
		}
//		Log.e(TAG, device_id + "----------" + time + "---------" + mode);
	}


	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
//			setMode();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	*//**
	 * 设置模式
	 *//*
	public void setMode() {
		int mode = Constant.AIR_MODE_MANUL;
		if (switchMode.isChecked()) {
			mode = Constant.AIR_MODE_SMART;
		}

		if (switchTimer.isChecked()) {
			mode = Constant.AIR_MODE_TIMER;
		}

//		int duration = Integer.parseInt(tvdurationTime.getText().toString());
//		httpAir.setMode(device_id, mode, time, duration);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	
	*//**
	 * @param date
	 * @return
	 *//*
	public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }
}
*/