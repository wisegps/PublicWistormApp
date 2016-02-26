/**
 * 
 */
package com.wicare.wistormpublicdemo.xutil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.model.ActiveGpsData;
import com.wicare.wistormpublicdemo.model.GpsData;

/**
 * 汽车数据
 * 
 */
public class HttpCarInfo {
	
	static String TAG = "HttpCarInfo";
	
	private Handler uiHandler;
	private Handler workHandler;
	private HandlerThread handlerThread = null;
	private RequestQueue mQueue;
	private MyApplication app;
	private String deviceId;

	private Context context;

	public HttpCarInfo(Context context, Handler uiHandler) {
		super();
		this.context = context;
		this.uiHandler = uiHandler;
		handlerThread = new HandlerThread("HttpCarInfo");
		handlerThread.start();
		Looper looper = handlerThread.getLooper();
		workHandler = new Handler(looper, handleCallBack);
		app = (MyApplication) ((Activity) context).getApplication();
		mQueue = HttpUtil.getRequestQueue(context);
	}

	/**
	 * 工作子线程回调函数： 主线程把网络请求数据发送到该工作子线程，子线程解析完毕，发送通知到ui主线程跟新界面
	 */
	private Callback handleCallBack = new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			Bundle data = null;
			String response = msg.obj.toString();
			switch (msg.what) {
			
			case Msg.GET_CAR_GPS_DATA:
				data = parseGps(response);
				break;
			}
			// 通知ui线程更新数据
			Message m = uiHandler.obtainMessage();
			m.what = msg.what;
			m.setData(data);
			uiHandler.sendMessage(m);
			return false;
		}

	};

	private ErrorListener errorListener = new ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
		}
	};

	/**
	 * 获取gps requestGps
	 */
	public void requestGps(String device_id) {
		String gpsUrl = Constant.BaseUrl + "device/" + device_id 
				+ "?auth_code=" + app.auth_code + "&update_time=2014-01-01%2019:06:43";
		Listener<String> listener = new Response.Listener<String>() {
			public void onResponse(String response) {
				// 返回数据，发送到工作子线程去解析
				Message msg = workHandler.obtainMessage();
				msg.what = Msg.GET_CAR_GPS_DATA;
				msg.obj = response;
				workHandler.sendMessage(msg);
			}
		};
		@SuppressWarnings("rawtypes")
		Request request = new StringRequest(gpsUrl, listener, errorListener);
		mQueue.add(request);
	}

	/**
	 * 解析gps信息
	 */
	private Bundle parseGps(String response) {
		try {
			Bundle bundle = new Bundle();
			Gson gson = new Gson();
			ActiveGpsData activeGpsData = gson.fromJson(response,
					ActiveGpsData.class);
			if (activeGpsData == null) {
				return null;
			}
			GpsData gpsData = activeGpsData.getActive_gps_data();
			if (gpsData != null) {
				bundle.putDouble("lat", gpsData.getLat());
				bundle.putDouble("lon", gpsData.getLon());
				String rcv_time = changeTimeZone(gpsData
						.getRcv_time().substring(0, 19).replace("T", " "));
				bundle.putString("rcv_time", rcv_time);
			}
			if (activeGpsData.getParams() != null) {
				bundle.putInt("sensitivity", activeGpsData.getParams()
						.getSensitivity());
			}
			return bundle;
		} catch (Exception e) {

		}
		return null;
	}
	
	/**
	 * 解决时区问题
	 * 
	 * @param Date
	 * @return
	 */
	public static String changeTimeZone(String Date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar nowDate = Calendar.getInstance();
			nowDate.setTime(sdf.parse(Date));
			nowDate.add(Calendar.HOUR_OF_DAY, 8);
			String Date1 = sdf.format(nowDate.getTime());
			return Date1;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
