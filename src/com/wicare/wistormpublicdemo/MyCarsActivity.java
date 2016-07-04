package com.wicare.wistormpublicdemo;

import java.io.File;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.wicare.wistorm.api.WVehicleApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.ui.WAlertDialog;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.HandlerMsg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.model.CarData;
import com.wicare.wistormpublicdemo.ui.SlidingView;
import com.wicare.wistormpublicdemo.xutil.L;
import com.wicare.wistormpublicdemo.xutil.NetThread;
import com.wicare.wistormpublicdemo.xutil.T;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu
 * 
 * 添加车辆 （这里作为公用版本 ，这里设置只能添加一辆车）
 */
public class MyCarsActivity extends Activity{
	
	final String TAG = "MyCarsActivity";
	
	private static final int ADD_CAR_CODE = 2;
	
	/*是否刷新*/
	boolean isRefresh = false;
	/*车辆列表*/
	private ListView lv_my_cars;
	/*APP*/
	private MyApplication app;
	/*适配器*/
	private CarAdapter carAdapter;
	
	private Context mContext;
	private WVehicleApi vehicleApi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_cars);
		app = (MyApplication)getApplication();
		mContext = MyCarsActivity.this;
		
		carAdapter = new CarAdapter();
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("我的爱车");
		
		lv_my_cars = (ListView)findViewById(R.id.lv_my_cars);
		LayoutInflater mLayoutInflater = LayoutInflater.from(this);
		View foot_view = mLayoutInflater.inflate(R.layout.item_add_my_car, null);
		lv_my_cars.addFooterView(foot_view);
		lv_my_cars.setAdapter(carAdapter);
		lv_my_cars.setOnItemClickListener(onItemClickListener);
		
		init();
		getVehileList();
	}
	/**
	 * wistorm api接口网络请求初始化
	 */
	private void init(){
		vehicleApi = new WVehicleApi();
		BaseVolley.init(mContext);
	}
	
	/**
	 * 列表点击事件
	 */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			if (position == app.carDatas.size()) {
				Intent i_addcar = new Intent(MyCarsActivity.this, CarAddActivity.class);
				startActivityForResult(i_addcar , ADD_CAR_CODE);
			} 
		}
	};
	
	/**
	 * 点击事件监听
	 */
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.iv_top_back://返回
				finish();
				break;
			}
		}
	};
	
	
	/**
	 * 获取车辆信息列表
	 */
	private void getVehileList(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", app.access_token);
//		params.put("seller_id", seller_id);//商户ID  
		params.put("cust_id", app.cust_id);//不是商户的时候 用 cust_id
		params.put("sorts", "obj_id");
		params.put("limit", "-1");
		String fields = "nick_name,cust_name,car_series,car_brand_id,cust_id,device_id";
		vehicleApi.list(params, fields, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				L.d(TAG, "我的车辆列表信息：" + response);
				parseVehicleList(response);
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	/**
	 * @param strJson 
	 */
	private void parseVehicleList(String strJson){
		app.carDatas.clear();
		try {
			JSONObject jsonObject =  new JSONObject(strJson);
			JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
			
			for(int i=0;i<jsonArray.length();i++){
				L.d(TAG, "第  " + i + " 辆车信息：" + jsonArray.get(i).toString());
				CarData cardata = new CarData();
//				第  0 辆车信息：{"nick_name":"timm","cust_name":"Dancan","car_series":"奥迪A4","car_brand_id":9,"device_id":0,"obj_id":2884,"cust_id":1219,"mobile":"13537687553"}
				JSONObject object = new JSONObject(jsonArray.get(i).toString());
				cardata.setCar_brand_id(object.getString("car_brand_id"));
				cardata.setCar_serial(object.getString("car_series"));
				cardata.setNick_name(object.getString("nick_name"));
				cardata.setObj_id(object.getString("obj_id"));
				if(object.has("device_id")){
					cardata.setDevice_id(object.getString("device_id"));
				}else{
					cardata.setDevice_id("0");
				}
				
				
				
				
				app.carDatas.add(cardata);
			}
			carAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			L.d(TAG, "错误信息： " + e.toString());
		}
	}
	
	
	
	
	
	/**删除车辆，或添加修改终端后，用这个标记删除内存里的数据，不用从网络上获取**/
	int index;
	
	
	/**
	 * @author Wu
	 * 
	 * 适配器
	 */
	class CarAdapter extends BaseAdapter {
		
		LayoutInflater layoutInflater = LayoutInflater.from(MyCarsActivity.this);

		@Override
		public int getCount() {
			return app.carDatas.size();
		}

		@Override
		public Object getItem(int arg0) {
			return app.carDatas.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.item_my_cars, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_serial = (TextView) convertView.findViewById(R.id.tv_serial);
				holder.tv_update = (TextView) convertView.findViewById(R.id.tv_update);
				holder.tv_remove = (TextView) convertView.findViewById(R.id.tv_remove);
				holder.tv_del = (TextView) convertView.findViewById(R.id.tv_del);
				holder.bt_bind = (Button) convertView.findViewById(R.id.bt_bind);
				holder.sv = (SlidingView) convertView.findViewById(R.id.sv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final CarData carData = app.carDatas.get(position);
			if (new File(Constant.VehicleLogoPath + carData.getCar_brand_id() + ".png").exists()) {
				Bitmap image = BitmapFactory.decodeFile(Constant.VehicleLogoPath + carData.getCar_brand_id() + ".png");
				holder.iv_icon.setImageBitmap(image);
			} else {
				holder.iv_icon.setImageResource(R.drawable.icon_car_moren);
			}
			holder.tv_name.setText(carData.getNick_name());
			holder.tv_serial.setText(carData.getCar_serial());
			holder.tv_del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteVehicle(position);
				}
			});
			holder.sv.ScorllRestFast();
			if (carData.getDevice_id().equals("0")) {
				holder.tv_update.setVisibility(View.GONE);
				holder.tv_remove.setVisibility(View.GONE);
				holder.bt_bind.setVisibility(View.VISIBLE);
				holder.bt_bind.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, DeviceAddActivity.class);
						intent.putExtra("obj_id", carData.getObj_id());
						startActivityForResult(intent, 2);
					}
				});
			} else {
				holder.bt_bind.setVisibility(View.GONE);
				holder.tv_update.setVisibility(View.GONE);
				holder.tv_remove.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView tv_name, tv_serial, tv_update, tv_del, tv_remove;
			ImageView iv_icon;
			Button bt_bind;
			SlidingView sv;
		}
	}
	
	
	
	/**删除车辆确认**/
	private void deleteVehicle(final int position) {
		WAlertDialog.Builder dialog = new WAlertDialog.Builder(mContext);
		dialog.setMessage("确定删除该车辆 ?");
		dialog.setMessageColor(Color.RED);
		dialog.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("access_token", app.access_token);
				params.put("obj_id", app.carDatas.get(position).getObj_id());
				vehicleApi.delete(params, new OnSuccess() {
					
					@Override
					protected void onSuccess(String response) {
						// TODO Auto-generated method stub
						L.i(TAG, "删除车辆 返回信息：" + response);
						try {
							JSONObject obj = new JSONObject(response);
							if("0".equalsIgnoreCase(obj.getString("status_code"))){
								T.showShort(mContext, "删除车辆信息成功");
								app.carDatas.remove(position);
								carAdapter.notifyDataSetChanged();
							}else{
								T.showShort(mContext, "删除车辆信息失败");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new OnFailure() { 
					
					@Override
					protected void onFailure(VolleyError error) {
						
					}
				});
			}
		});
		dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.create().show();
	}

    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(resultCode == CarAddActivity.FINISH_ADD_CAR_REQUEST_CODE){
    		getVehileList();//刷新车辆列表
    	}else if(resultCode == DeviceAddActivity.BIND_RESULT_CODE){
			T.showShort(mContext, "绑定设备成功！");
    	}
    	
    }
    
    
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    
    
	private void onBack() {
		System.out.println("isRefresh = " + isRefresh);
		if (isRefresh) {
			// 发广播
			Intent intent = new Intent(Constant.Wicare_Refresh_Car);
			sendBroadcast(intent);
		}
		finish();
	}
	
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		carAdapter.notifyDataSetChanged();//刷新车辆列表
//	}
	
}
