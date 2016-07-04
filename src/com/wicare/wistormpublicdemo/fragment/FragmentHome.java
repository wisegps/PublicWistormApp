package com.wicare.wistormpublicdemo.fragment;

import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.ui.WCircleProView;
import com.wicare.wistormpublicdemo.R;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.ui.SwitchImageView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
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
	private View view;
	private MyApplication app;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){
		view = inflater.inflate(R.layout.home, container, false);  
		
		app =  (MyApplication)getActivity().getApplication();
		
		circleProView = (WCircleProView)view.findViewById(R.id.circle_view);
		tvAirScore    = (TextView)view.findViewById(R.id.tv_air_score);
		
		SwitchImageView ivAirPower   = (SwitchImageView)view.findViewById(R.id.iv_air_power);
		SwitchImageView ivAirAuto    = (SwitchImageView)view.findViewById(R.id.iv_air_auto);
		SwitchImageView ivAirSetting = (SwitchImageView)view.findViewById(R.id.iv_air_setting);
		SwitchImageView ivAirLevel   = (SwitchImageView)view.findViewById(R.id.iv_air_level);
        return view;  
    }  
	
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	
	@Override
	public void onResume() {
		super.onResume();
		
	}	

	@Override
	public void onPause() {
		super.onPause();
	}

//	/**
//	 * 刷新数据 requestAir
//	 */
//	public void refreshAir() {
//
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (isResumed) {
//					httpAir.requestAir(url);
//					SystemClock.sleep(8000);
//				}
//			}
//		}).start();
//	}
}
