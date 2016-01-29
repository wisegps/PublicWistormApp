package com.wicare.wistormpublicdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wicare.wistormpublicdemo.MainActivity.OnFinishListener;
import com.wicare.wistormpublicdemo.R;
import com.wicare.wistormpublicdemo.ReminderActivity;
import com.wicare.wistormpublicdemo.SettingActivity;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

/**
 * @author Wu 更多页面
 *
 */
public class FragmentMore extends Fragment{

	static final String TAG = "FragmentMore";
	private OnFinishListener onFinishListener;
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){  
		
		View view = inflater.inflate(R.layout.more, container, false); 
	/*	RelativeLayout rl_myMessage  = (RelativeLayout)view.findViewById(R.id.rl_my_msg);
		rl_myMessage.setOnClickListener(onClickListener);*/
		RelativeLayout rl_mySetting  = (RelativeLayout)view.findViewById(R.id.rl_my_seting);
		rl_mySetting.setOnClickListener(onClickListener);
		RelativeLayout rl_exitSystem = (RelativeLayout)view.findViewById(R.id.rl_exit_system);
		rl_exitSystem.setOnClickListener(onClickListener);
        return view; 
    }  
	
	
	
	
	/**
	 * 点击事件监听
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			/*case R.id.rl_my_msg://我的信息
				Intent i_reminder = new Intent(getActivity(),ReminderActivity.class);
				startActivity(i_reminder);
				break;*/

			case R.id.rl_my_seting://设置中心
				Intent i_setting = new Intent(getActivity(),SettingActivity.class);
				startActivity(i_setting);
				break;
				
			case R.id.rl_exit_system://退出
				ActivityCollector.finishAll();
				if(onFinishListener != null){
					onFinishListener.onFinish();
				}
				break;
			}
		}
	};
	
	
	
	/**
	 * @param onFinishListener
	 */
	public void setOnFinishListener(OnFinishListener onFinishListener){
		this.onFinishListener = onFinishListener;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "---more-->onResume()");
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "---more-->onDestroy()");
	}
}
