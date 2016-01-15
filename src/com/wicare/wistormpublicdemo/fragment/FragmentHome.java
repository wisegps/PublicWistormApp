package com.wicare.wistormpublicdemo.fragment;

import com.wicare.wistorm.ui.WCircleProView;
import com.wicare.wistormpublicdemo.AirLevelActivity;
import com.wicare.wistormpublicdemo.AirSettingActivity;
import com.wicare.wistormpublicdemo.R;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.ui.SwitchImageView;
import com.wicare.wistormpublicdemo.xutil.HttpAir;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu  首页净化器
 *
 */
public class FragmentHome extends Fragment{
	
	static final String TAG = "FragmentHome";
	
	String url = "http://api.bibibaba.cn/device/1613/active_gps_data?auth_code=9e9e4747bfe62c7abee177b983d9745d";
	
	private WCircleProView circleProView;
	private TextView tvAirScore;//车内空气质量指数值
	private Handler uiHander = null;//ui更新
	private HttpAir httpAir;
	private boolean isResumed = false;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.home, container, false);  
		
		httpAir = new HttpAir(this.getActivity(), uiHander);
		uiHander = new Handler(handleCallBack);
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
	
	
	
	/**
	 * Handler 回调
	 */
	public Handler.Callback handleCallBack = new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
		
			switch (msg.what) {
			
			case Msg.GET_CAR_AIR:
				Log.e(TAG, "---home handleCallBack---");
				break;
			}
			return true;
		}
	};
	
	
	
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
				Toast.makeText(getActivity(), "净化器开关", Toast.LENGTH_SHORT).show();
				break;

			case R.id.iv_air_auto:
				Toast.makeText(getActivity(), "净化器自动", Toast.LENGTH_SHORT).show();		
				break;
				
			case R.id.iv_air_setting:
				Intent i_airSetting = new Intent(FragmentHome.this.getActivity(), AirSettingActivity.class);
				FragmentHome.this.startActivity(i_airSetting);
				break;
				
			case R.id.iv_air_level:
				Toast.makeText(getActivity(), "净化器leavl", Toast.LENGTH_SHORT).show();
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
//		Log.d(TAG, "---home-->onResume()");
		
		
		Toast.makeText(getActivity(), "---home-->onResume()", Toast.LENGTH_SHORT).show();
		
	}
	

	@Override
	public void onPause() {
		super.onPause();
		isResumed = false;
		Toast.makeText(getActivity(), "---home-->onPause()", Toast.LENGTH_SHORT).show();
//		Log.d(TAG, "---home-->onPause()");
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
					SystemClock.sleep(10000);
				}
			}
		}).start();
	}
	

}
