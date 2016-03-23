package com.wicare.wistormpublicdemo.fragment;

import com.wicare.wistorm.api.WAirApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.Msg;
import com.wicare.wistorm.ui.WCircleProView;
import com.wicare.wistormpublicdemo.AirLevelActivity;
import com.wicare.wistormpublicdemo.AirSettingActivity;
import com.wicare.wistormpublicdemo.R;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.HandlerMsg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.model.Air;
import com.wicare.wistormpublicdemo.model.CarData;
import com.wicare.wistormpublicdemo.ui.SwitchImageView;
import com.wicare.wistormpublicdemo.xutil.HttpAir;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Wu  首页净化器
 *
 */
public class FragmentHome extends Fragment{
	
	static final String TAG = "FragmentHome";
	
	public final static int POWER_ON = 1;
	public final static int POWER_OFF = 0;
	
	private WCircleProView circleProView;
	private TextView tvAirScore;//车内空气质量指数值
	private Handler uiHander = null;//ui更新
	private HttpAir httpAir;
	private WAirApi airApi;
	private boolean isResumed = false;
	private View view;
	private int air_speed_count = 1;
	private MyApplication app;
	private CarData carData;
	private String url;
//	private float battery;
	private boolean isFirst = true;
	//设备ID
	private String device_id = "";
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){
		view = inflater.inflate(R.layout.home, container, false);  
		
		uiHander = new Handler(handleCallBack);
		airApi   = new WAirApi(uiHander);
		BaseVolley.init(getActivity());
		
		
		httpAir = new HttpAir(this.getActivity(), uiHander);
		
		app = (MyApplication)getActivity().getApplication();
		carData = app.carDatas.get(0);
		device_id = carData.getDevice_id();//获得设备ID
		circleProView = (WCircleProView)view.findViewById(R.id.circle_view);
		tvAirScore    = (TextView)view.findViewById(R.id.tv_air_score);
		circleProView.setOnClickListener(onClickListener);
		
		SwitchImageView ivAirPower   = (SwitchImageView)view.findViewById(R.id.iv_air_power);
		SwitchImageView ivAirAuto    = (SwitchImageView)view.findViewById(R.id.iv_air_auto);
		SwitchImageView ivAirSetting = (SwitchImageView)view.findViewById(R.id.iv_air_setting);
		SwitchImageView ivAirLevel   = (SwitchImageView)view.findViewById(R.id.iv_air_level);
		
		ivAirPower.setOnClickListener(onClickListener);
		ivAirAuto.setOnClickListener(onClickListener);
		ivAirSetting.setOnClickListener(onClickListener);
		ivAirLevel.setOnClickListener(onClickListener);
        return view;  
    }  
	
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		url = Constant.BaseUrl + "device/" + carData.getDevice_id() +
				"/active_gps_data?auth_code=" + app.auth_code;
		httpAir.requestObdData(carData.getCar_brand(), carData.getDevice_id());	
	}
	
	
	/**
	 * Handler 回调
	 */
	public Handler.Callback handleCallBack = new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
		
			Bundle bundle = msg.getData();
			switch (msg.what) {
			case HandlerMsg.GET_OBD_DATA:
				initValue(bundle);
				break;
			case HandlerMsg.GET_CAR_AIR:
				refreshValue((Air) msg.obj);
				break;
				
			case Msg.SET_AIR_SPEED_COMMAND:
				Bundle bundle_air_speed = msg.getData();
				String status_code = bundle_air_speed.getString("status_code");
//				Log.i(TAG, "设置速度返回信息status_code：" + status_code + "==" + air_speed_count);
				if("0".equals(status_code)){
					air_speed_count ++;
					if(air_speed_count > 3){
						air_speed_count = 1;
					}
				}
				
				break;
			}
			return true;
		}
	};
	
	
	/**
	 * 设置空气质量信息
	 * 
	 * @param bundle
	 */
	public void initValue(Bundle bundle) {

//		String battery = bundle.getString("battery");
//		try {
//			this.battery = Float.parseFloat(battery);
//		} catch (Exception e) {
//			this.battery = 0;
//		}
		Air mAir = new Air();
		int airValue = bundle.getInt("air");
		int airSwitch = bundle.getInt("switch");
		int airMode = bundle.getInt("air_mode");
		String airTime = bundle.getString("air_time");
		int airDuration = bundle.getInt("airDuration");
		int airSpeed    = bundle.getInt("air_speed");
		mAir.setAir(airValue);
		mAir.set_switch(airSwitch);
		mAir.setAir_mode(airMode);
		mAir.setAir_duration(airDuration);
		mAir.setAir_time(airTime);
		mAir.setAirSpeed(airSpeed);
		refreshValue(mAir);
	}
	
	/**
	 * 刷新空气质量信息
	 * 
	 * @param bundle
	 */
	public void refreshValue(Air air) {
		
		Log.d("FragmentHomeAir", "refreshValue");
		View v = view;
		SwitchImageView ivAirPower = (SwitchImageView) v
				.findViewById(R.id.iv_air_power);
		SwitchImageView ivAirAuto = (SwitchImageView) v
				.findViewById(R.id.iv_air_auto);
		SwitchImageView ivAirLevel = (SwitchImageView) v
				.findViewById(R.id.iv_air_level);
		SwitchImageView ivAirSetting = (SwitchImageView) v
				.findViewById(R.id.iv_air_setting);
		
		int air_speed = air.getAirSpeed();
		String str_air_speed = "";
		if(air_speed ==1){
			str_air_speed = "低速";
		}else if(air_speed ==2){
			str_air_speed = "中速";
		}else{
			str_air_speed = "高速";
		}
		if(isFirst){
			isFirst = false;
			if(air_speed > 2){
				air_speed_count = 1;
			}else{
				air_speed_count =  air_speed + 1;
			}	
		}
		Log.i("FragmentHomeAir", "速度: " + str_air_speed + "  现在速度：" + air_speed  + "  下一次速度：" + air_speed_count);
		/* 开关控制 */
		int vSwitch = air.get_switch();
		boolean isChecked = (vSwitch == POWER_ON) ? true : false;
		ivAirPower.setChecked(isChecked);
		int vAirMode = air.air_mode;
		isChecked = (vAirMode == Constant.AIR_MODE_MANUL) ? false : true;
		ivAirAuto.setChecked(isChecked);
		ivAirSetting.setChecked(isChecked);

	}
	
	/**
	 * 点击事件
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.circle_view:
				Intent i_airLevel = new Intent(FragmentHome.this.getActivity(), AirLevelActivity.class);
				FragmentHome.this.startActivity(i_airLevel);
				break;			
			case R.id.iv_air_power:
				SwitchImageView ivPower = (SwitchImageView) v;
				boolean isChecked = ivPower.isChecked();
				ivPower.setChecked(!isChecked);
				
				if(!ivPower.isChecked()){
					airApi.setAirCommand(app.Token, device_id, Msg.AIR_POWER_OFF, Msg.SWITCH_COMMAND_MODEL);
				}else{
					airApi.setAirCommand(app.Token, device_id, Msg.AIR_POWER_ON , Msg.SWITCH_COMMAND_MODEL);
				}
				break;
			case R.id.iv_air_auto:
				SwitchImageView ivAuto = (SwitchImageView) v;
				ivAuto.setChecked(!ivAuto.isChecked());
				String air_mode = Msg.AIR_NORMAL_MODEL;
				if (ivAuto.isChecked()) {
					air_mode = Msg.AIR_SMART_MODEL;
				}
				airApi.setAirCommand(app.Token, device_id, air_mode , Msg.MODEL_SET_COMMAND_MODEL);
				break;			
			case R.id.iv_air_setting:
				Intent i_airSetting = new Intent(FragmentHome.this.getActivity(), AirSettingActivity.class);
				i_airSetting.putExtra("air_url", url);
				i_airSetting.putExtra("device_id", carData.getDevice_id());
				FragmentHome.this.startActivity(i_airSetting);
				break;				
			case R.id.iv_air_level:
				Log.i("FragmentHomeAir", "ddddddd: " + air_speed_count);
				if(air_speed_count == 1){
					airApi.setAirCommand(app.Token, device_id, Msg.LOW_SPEED, Msg.SPEED_COMMAND_MODEL);
				}
				if(air_speed_count == 2){
					airApi.setAirCommand(app.Token, device_id, Msg.MIDDLE_SPEED, Msg.SPEED_COMMAND_MODEL);
				}
				if(air_speed_count == 3){
					airApi.setAirCommand(app.Token, device_id, Msg.HIGHT_SPEED, Msg.SPEED_COMMAND_MODEL);
				}
				break;			
			default:
				break;
			}
		}
	};
	
	
	@Override
	public void onResume() {
		super.onResume();
		isResumed = true;
		refreshAir();//更新数据
		
	}	

	@Override
	public void onPause() {
		super.onPause();
		isResumed = false;
	}

	/**
	 * 刷新数据 requestAir
	 */
	public void refreshAir() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isResumed) {
					httpAir.requestAir(url);
					SystemClock.sleep(8000);
				}
			}
		}).start();
	}
}
