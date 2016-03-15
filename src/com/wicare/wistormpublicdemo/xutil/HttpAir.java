package com.wicare.wistormpublicdemo.xutil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.app.Activity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.model.Air;

public class HttpAir {
	
	public static final String TAG = "HttpAir";
	
	private Context context;
	private RequestQueue mQueue;
	private Handler uiHandler;
	private Handler workHandler;
	private HandlerThread handlerThread = null;
	
	public static int COMMAND_SWITCH = 0x4043; // 开关指令
	public static int COMMAND_AIR_MODE = 0x4044; // 设置净化模式指令
	public static int COMMAND_AIR_SPEED = 0x4045; // 设置净化速度指令
	
	private MyApplication app;
	
	
	public HttpAir(Context context, Handler uiHandler){
		super();
		this.context = context;
		this.uiHandler = uiHandler;
		
		app = (MyApplication) ((Activity) context).getApplication();
		mQueue = HttpUtil.getRequestQueue(context);
		
		handlerThread = new HandlerThread("HttpAir");
		handlerThread.start();
		Looper looper = handlerThread.getLooper();
		workHandler = new Handler(looper, handleCallBack);
		
	}
	
	
	
	/**
	 * 工作子线程回调函数：
	 * 主线程把网络请求数据发送到该工作子线程，子线程解析完毕，发送通知到ui主线程跟新界面
	 */
	public Handler.Callback handleCallBack = new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			
			case Msg.GET_CAR_AIR:
				
				Log.d(TAG, "---hTTP air handleCallBack---" + msg.obj.toString());
				// 解析后提交ui线程更新数据
				Air air = paseAir(msg.obj.toString());
				Message m = uiHandler.obtainMessage();
				m.what = msg.what;
				m.obj = air;
				uiHandler.sendMessage(m);
				break;
				
			case Msg.GET_OBD_DATA:
				// 解析后提交ui线程更新数据
				Bundle bundle = parse(msg.obj.toString());
				Message m1 = uiHandler.obtainMessage();
				m1.what = msg.what;
				m1.setData(bundle);
				uiHandler.sendMessage(m1);
				break;
				
//			case Msg.SET_AIR_MODEL:
//				Message m2 = uiHandler.obtainMessage();
//				m2.what = msg.what;
//				m2.obj  = 
//				
//				uiHandler.sendMessage(m1);
//				break;
			}
			
			
			return false;
		}

	};

	
	/**
	 * 
	 * 解析返回字符串
	 * 
	 * @param response
	 * @return
	 */
	private Bundle parse(String response) {
		Bundle budle = new Bundle();
		try {
			Boolean isStart = false;
			JSONObject gpsData = new JSONObject(response) .getJSONObject("active_gps_data");
			/*
			 * 空气净化
			 */
			int air = gpsData.optInt("air");
			budle.putInt("air", air);
			String battery = $(gpsData, "battery");
			budle.putString("battery", battery);
			
			JSONObject jsonParams = new JSONObject(response) .getJSONObject("params");
			int vSwitch =jsonParams.optInt("switch");
			budle.putInt("switch", vSwitch);
			int air_mode = jsonParams.optInt("air_mode");
			budle.putInt("air_mode", air_mode);
			int airDuration = jsonParams.optInt("air_duration");
			budle.putInt("airDuration", airDuration);
			String air_time = jsonParams.optString("air_time");
			budle.putString("air_time", air_time);
			Log.e("HttpGetData", "airDuration " + airDuration);
		} catch (JSONException e) {
			Log.i("HttpGetData", "exception " + e.getMessage());
			e.printStackTrace();
		}
		return budle;
	}
	
	// 把json对象为空判断 转化为int
	private String $(JSONObject json, String key) {
		Object obj = null;
		try {
			obj = json.get(key);
		} catch (JSONException e) {
			Log.i("HttpGetData", "exception1 " + e.getMessage());
			e.printStackTrace();
			return "--";
		}
		if (obj.equals(null)) {
			return "--";
		} else {
			Double value = Double.parseDouble(obj.toString());
			int i = value.intValue();
			Log.i("HttpGetData", "intValue " + key + " :" + i);
			return value.intValue() + "";
		}
	}
	
	/**
	 * 请求空气质量指数,提交工作线程解析
	 */
	public void requestAir(String url) {

		Log.e("HttpAir", url.toString());
		Listener<String> listener = new Response.Listener<String>() {
			public void onResponse(String response) {
				//返回数据，发送到工作子线程去解析
				Message msg = workHandler.obtainMessage();
				msg.what = Msg.GET_CAR_AIR;
				msg.obj = response;
				workHandler.sendMessage(msg);
			}
		};

		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		};
		Request request = new StringRequest(url, listener, errorListener);
		mQueue.add(request);
	}
	
	
	/**
	 * @param response
	 * @return
	 */
	private Air paseAir(String response) {
		Air mAir = new Air();
		int airSwitch = 0, airDuration = 0, airMode = 0,airValue = 0;
		String airTime = "";
		try {
			JSONObject obj = new JSONObject(response);
			JSONObject data = obj.optJSONObject("active_gps_data");
			JSONObject params = obj.optJSONObject("params");
			airValue = data.optInt("air");
			airSwitch = params.optInt("switch");
			airMode = params.optInt("air_mode");
			airTime = params.optString("air_time");
			airDuration = params.optInt("air_duration");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		mAir.setAir(airValue);
		mAir.set_switch(airSwitch);
		mAir.setAir_mode(airMode);
		mAir.setAir_time(airTime);
		mAir.setAir_duration(airDuration);
		return mAir;
	}
	
	
	/**
	 * 根据url发送get请求 返回json字符串,并解析
	 * 
	 * @param url
	 */
	public void requestObdData(String brand,String deviceId) {
		if( app.carDatas == null){
			return;
		}
		try {
			brand = URLEncoder.encode(brand, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = Constant.BaseUrl + "device/" + deviceId 
				+ "?auth_code="+ app.auth_code + "&brand" + brand;
		
		Listener<String> listener = new Response.Listener<String>() {
			public void onResponse(String response) {

				Log.i("HttpGetData", "response " + response);
				Message msg = workHandler.obtainMessage();
				msg.what = Msg.GET_OBD_DATA;
				msg.obj = response;
				workHandler.sendMessage(msg);
			}
		};

		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		};
		Request request = new StringRequest(url, listener, errorListener);
		request.setShouldCache(false);
		mQueue.add(request);
	}
	
	
	/**
	 * 发送postt请求 返回json字符串,并解析(发送指令控制净化器)
	 * 
	 * @param url
	 */
	public void request(String deviceId, final int command, String params) {

		String url = Constant.BaseUrl + "command?auth_code=" + app.auth_code;
		String data = "{device_id:" + deviceId + ",cmd_type:" + command
				+ ",params:" + params + "}";

		Log.d("FragmentElectricCarInfo", data);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Request request = new JsonObjectRequest(Method.POST, url, jsonObject,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
//						Message message = uiHandler.obtainMessage();
//						message.obj = response;
//						message.what = Msg.SET_AIR_MODEL;
//						uiHandler.sendMessage(message);
//						uiHandler.sendEmptyMessage(Msg.SET_AIR_MODEL);			
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
//						uiHandler.sendEmptyMessage(Msg.Set_Air_Response);
					}
				});
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		mQueue.add(request);
	}

	
	/**
	 * @param deviceId  设备ID
	 * @param power
	 */
	public void setPower(String deviceId, boolean power) {
		int command = COMMAND_SWITCH;
		int value = power ? 1 : 0;
		// value = 1;
		String params = "{switch: " + value + "}";
		request(deviceId, command, params);
	}

	public void setMode(String deviceId, int mode, String time, int duration) {
		int command = COMMAND_AIR_MODE;
		String params = "{air_mode: " + mode + "}";
		if (mode == Constant.AIR_MODE_TIMER) {
			params = "{air_mode: " + mode + ",air_time:'" + time
					+ "',air_duration:" + duration + "}";
		}
		request(deviceId, command, params);
	}
}
