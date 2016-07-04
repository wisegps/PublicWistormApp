package fragment;

import widget.SwitchImageView;
import xutil.T;

import com.wicare.wistorm.ui.WCircleProView;
import com.wicare.wistormpublicdemo.MainActivity;
import com.wicare.wistormpublicdemo.R;

import de.greenrobot.event.EventBus;

import eventbrocast.UpdataHomeFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import application.MyApplication;

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
	
	
	private String device_id;//设备终端ID 
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){
		view = inflater.inflate(R.layout.home, container, false);  
		
		app =  (MyApplication)getActivity().getApplication();
		EventBus.getDefault().register(this);  
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
	
	
	
	
	
	/**
	 * @param event 
	 */
	public void onEventMainThread(UpdataHomeFragment event){
		
		device_id = event.getMsg();
		T.showLong(getActivity(), device_id);
		Log.d(TAG, "收到更新信息");
		
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
