package com.wicare.wistormpublicdemo.xutil;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.model.Air;

public class HttpAir {
	
	
	public static final String TAG = "HttpAir";
	
	private Context context;
	private RequestQueue mQueue;
	private Handler uiHandler;
	private Handler workHandler;
	private HandlerThread handlerThread = null;
	
	
	public HttpAir(Context context, Handler uiHandler){
		super();
		this.context = context;
		this.uiHandler = uiHandler;
		
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
				
				Log.e(TAG, "---hTTP air handleCallBack---" + msg.obj.toString());
				// 解析后提交ui线程更新数据
//				Air air = paseAir(msg.obj.toString());
//				Message m = uiHandler.obtainMessage();
//				m.what = msg.what;
//				m.obj = air;
//				uiHandler.sendMessage(m);
				break;
			}
			return false;
		}

	};

	
	
	
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
	
	
	
	
	
	
}
