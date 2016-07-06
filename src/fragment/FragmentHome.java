package fragment;

import java.util.HashMap;
import java.util.List;

import model.CarData;
import model.ParseCarJson;

import org.json.JSONException;
import org.json.JSONObject;

import widget.MarqueeTextView;
import widget.SwitchImageView;
import xutil.L;
import xutil.T;

import com.android.volley.VolleyError;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.navisdk.comapi.mapcontrol.MapParams.Const;
import com.wicare.wistorm.api.WAirApi;
import com.wicare.wistorm.api.WDeviceApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.ui.WCircleProView;
import com.wicare.wistormpublicdemo.R;

import de.greenrobot.event.EventBus;

import eventbrocast.UpdataHomeFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import application.Constant;
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
	private MarqueeTextView tv_location;//车辆的位置
	private SwitchImageView ivAirPower;
	private SwitchImageView ivAirAuto;
	private SwitchImageView ivAirLevel;
	
	private MyApplication app;
	private Context mContext;
	private String device_id;//设备终端ID 
	private float battery;//电压
	private int speed_level;//速度等级
	private WDeviceApi deviceApi;
	private WAirApi airApi;
	private CarData cardata;
	private GeoCoder mGeoCoder = null;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.home, container, false);  
		mContext = getActivity();
		app =  (MyApplication)getActivity().getApplication();
		EventBus.getDefault().register(this);  
		circleProView = (WCircleProView)view.findViewById(R.id.circle_view);
		tvAirScore    = (TextView)view.findViewById(R.id.tv_air_score);
		tv_location = (MarqueeTextView)view.findViewById(R.id.tv_location);
		ivAirPower   = (SwitchImageView)view.findViewById(R.id.iv_air_power);
		ivAirAuto    = (SwitchImageView)view.findViewById(R.id.iv_air_auto);
//		SwitchImageView ivAirSetting = (SwitchImageView)view.findViewById(R.id.iv_air_setting);
		ivAirLevel   = (SwitchImageView)view.findViewById(R.id.iv_air_level);
		ivAirAuto.setOnClickListener(onClickListener);
		ivAirPower.setOnClickListener(onClickListener);
		ivAirLevel.setOnClickListener(onClickListener);
		
		circleProView.enableInsideScaleRing(true);
		init();
        return view;  
    }  
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(listener);
		device_id = app.carDatas.get(0).getDevice_id();
		getDeviceData(device_id);
	}
	
	/**
	 * wistorm api接口网络请求初始化
	 */
	private void init(){
		deviceApi = new WDeviceApi();
		airApi = new WAirApi();
		BaseVolley.init(mContext);
	}

	/**
	 * onclick
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			
			case R.id.iv_air_power:
				if(battery<12.0){
					T.showLong(mContext, "电压过低");
				}else{
					if(ivAirPower.isChecked()){
						ctrlAirSwitch(Constant.AIR_POWER_OFF);
					}else{
						ctrlAirSwitch(Constant.AIR_POWER_ON);
					}
				}
				break;
			case R.id.iv_air_auto:
				if(ivAirAuto.isChecked()){
					ctrlAirAuto(Constant.AIR_NORMAL_MODEL);
				}else{
					ctrlAirAuto(Constant.AIR_SMART_MODEL);
				}
				break;
			case R.id.iv_air_level:
				Log.d(TAG, "控制净化器 ：" + ivAirLevel.isChecked() + " " + speed_level);
				if(ivAirLevel.isChecked()){
					if(speed_level == 1){
						ctrlAirSpeed(Constant.MIDDLE_SPEED);//启动中速
					}else if(speed_level == 2){
						ctrlAirSpeed(Constant.HIGHT_SPEED);//启动高速速
					}else {
						ctrlAirSpeed(Constant.LOW_SPEED);//启动低速
					}
				}
				break;
			}
		}
	};
	
	
	
	/**
	 * @param command 控制净化器速度
	 */
	private void ctrlAirSpeed(String command){
		Log.e(TAG, "控制净化器速度指令 ：" + command);
		airApi.setAirSpeed(app.access_token, device_id, command, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.e(TAG, "控制净化器速度返回信息 ：" + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						getDeviceData(device_id);
						T.showLong(mContext, "设置速度成功");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	/**
	 * 控制手动和自动模式
	 */
	private  void ctrlAirAuto(String command){
		airApi.setAirModel(app.access_token, device_id, command, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.d(TAG, "净化器moshi返回信息 ：" + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						getDeviceData(device_id);
						T.showLong(mContext, "设置模式成功");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				Log.d(TAG, "moshi连接错误返回信息 ：" + error.toString());
				T.showLong(mContext, "连接超时");
			}
		});
	}
	
	
	/**
	 * 控制净化器开关
	 */
	private void ctrlAirSwitch(String command){
		airApi.setAirSwitch(app.access_token, device_id, command, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				Log.d(TAG, "关闭净化器返回信息 ：" + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					if("0".equals(jsonObject.getString("status_code"))){
						getDeviceData(device_id);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				Log.d(TAG, "关闭连接错误返回信息 ：" + error.toString());
				T.showLong(mContext, "连接超时");
			}
		});
	}
	
	
	/**
	 * @param event 
	 */
	public void onEventMainThread(UpdataHomeFragment event){
		device_id = event.getMsg();
		Log.d(TAG, "收到更新信息");
		getDeviceData(device_id);
	}
	
	
	/**
	 * 获取设备信息
	 */
	private void getDeviceData(String device_id){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token",app.access_token);
		params.put("device_id",device_id);
		String fields = "params,active_gps_data,active_obd_data";
		deviceApi.get(params, fields, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				parseDeviceData(response);
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				L.d(TAG, "Error 连接失败");
			}
		});
	}
	
	
	
	/**
	 * 解析返回的设备数据
	 * 
	 * @param strJson
	 */
	private void parseDeviceData(String strJson){
		try {
			
			cardata = ParseCarJson.getData(strJson);
			
			refreshUIStatus();
			L.d(TAG, "test:" + cardata.getAir() + "\n" + cardata.getAir_mode()
					+ "\n" + cardata.getAir_speed() + "\n" + cardata.getAir_switch() + "\n" + cardata.getBattery()
					+ "\n" + cardata.getLat() + "\n" + cardata.getLon());
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 刷新界面状态
	 */
	private void refreshUIStatus(){
		String air_mode  = cardata.getAir_mode();
		String air_speed = cardata.getAir_speed();
		String air_switch= cardata.getAir_switch();
		float  battery = Float.valueOf(cardata.getBattery());
		String air = cardata.getAir();
		
		Double lat = Double.valueOf(cardata.getLat());
		Double lon = Double.valueOf(cardata.getLon());
		app.lat = lat;
		app.lon = lon;
		LatLng latLng = new LatLng(lat, lon);
		
		
		if("1".equals(air_switch)){
			ivAirPower.setChecked(true);
			ivAirLevel.setChecked(true);
			circleProView.enableScaleRingPointRun(true);
		}else{
			ivAirPower.setChecked(false);
			ivAirLevel.setChecked(false);
			circleProView.enableScaleRingPointRun(false);
		}
		if("0".equals(air_mode)){
			ivAirAuto.setChecked(true);
		}else{
			ivAirAuto.setChecked(false);
		}
		if("1".equals(air_speed)){
			speed_level = 1;
		}else if("2".equals(air_speed)){
			speed_level = 2;
		}else{
			speed_level = 3;
		}
		this.battery = battery;
		tvAirScore.setText(air);
		mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
	}

	
	/**
	 * 百度地图反解析
	 */
	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			} else {
				final String address = result.getAddress();
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						tv_location.setText(address);
					}
				});
			}
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {

		}
	};

	boolean isResume = false;
	@Override
	public void onResume() {
		super.onResume();
		isResume = true;
		refreshTimer();
	}	

	@Override
	public void onPause() {
		super.onPause();
		isResume = false;
	}

	
	/**
	 * 刷新数据 requestAir
	 */
	public void refreshTimer() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isResume) {
					getDeviceData(device_id);
					SystemClock.sleep(10000);
				}
			}
		}).start();
	}
}
