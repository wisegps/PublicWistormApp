package fragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.wicare.wistormpublicdemo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import application.MyApplication;

/**
 * @author Wu  地图定位
 *
 */
public class FragmentMap extends Fragment{

	private static final String TAG = "FragmentMap";
	private MapView mMapView = null; 
	private BaiduMap mBaiduMap = null;
	private MyApplication app;
	// 手机定位
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();	
		
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){  
		app = (MyApplication)getActivity().getApplication();
		View view = inflater.inflate(R.layout.map, container, false); 
		mMapView = (MapView) view.findViewById(R.id.bmapView);
        return view;  
    }  
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14));
		// 定位初始化
		mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(20000);//10秒更新一次位置
		mLocClient.setLocOption(option);
		mLocClient.start();		
	}
	
	
	/**
	 * @author WU 手机位置图标
	 *
	 */
	Marker phoneMark;
	private class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null  || mMapView == null)
				return;
			if(phoneMark != null) {
				phoneMark.remove();
			}
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());	
			Log.e(TAG, "手机位置：" + latLng);
			// 构建Marker图标
			BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_my_phone);
			// 构建MarkerOption，用于在地图上添加Marker
			OverlayOptions option = new MarkerOptions().anchor(0.5f, 1.0f)
					.position(latLng).icon(bitmap);
			// 在地图上添加Marker，并显示
			phoneMark = (Marker)(mBaiduMap.addOverlay(option));
			//显示手机位置的界面
			MapStatus mapStatus = new MapStatus.Builder().target(latLng)
					.build();
			MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
					.newMapStatus(mapStatus);
			mBaiduMap.setMapStatus(mapStatusUpdate);
			setCarLocationMark();
		}
	}
	
	
	LatLng carLatLng;
	Marker carMarker = null;
	/**
	 * 显示 当前车辆位子图标
	 */
	private void setCarLocationMark() {
		try {			
			carLatLng = new LatLng(app.lat, app.lon);
			if (carMarker != null) {
				carMarker.remove();
			}
			// 构建Marker图标
			BitmapDescriptor bitmap = BitmapDescriptorFactory .fromResource(R.drawable.ic_car_mark);
			// 构建MarkerOption，用于在地图上添加Marker
			OverlayOptions option = new MarkerOptions().anchor(0.5f, 1.0f)
					.position(carLatLng).icon(bitmap);
			// 在地图上添加Marker，并显示
			carMarker = (Marker) (mBaiduMap.addOverlay(option));
			MapStatus mapStatus = new MapStatus.Builder().target(
					carLatLng).build();
//			MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
//					.newMapStatus(mapStatus);
//			mBaiduMap.setMapStatus(mapStatusUpdate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "---map-->onResume()");
		mMapView.onResume(); 
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "---map-->onDestroy()");
		mLocClient.unRegisterLocationListener(myListener);
		mMapView.onDestroy(); 
	}

}
