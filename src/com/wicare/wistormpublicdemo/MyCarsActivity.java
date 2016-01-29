package com.wicare.wistormpublicdemo;

import java.io.File;

import org.json.JSONObject;

import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.model.CarData;
import com.wicare.wistormpublicdemo.ui.SlidingView;
import com.wicare.wistormpublicdemo.xutil.NetThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	/*帐号*/
	private String sp_account;
	/*APP*/
	private MyApplication app;
	/*适配器*/
	private CarAdapter carAdapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_cars);
		app = (MyApplication)getApplication();
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
		
		SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
		sp_account = preferences.getString(Constant.sp_account, "");
		
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
	
	
	/**删除车辆，或添加修改终端后，用这个标记删除内存里的数据，不用从网络上获取**/
	int index;
	
	
	/**
	 * @author Wu
	 * 
	 * 适配器
	 */
	class CarAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater = LayoutInflater.from(MyCarsActivity.this);

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
			holder.tv_serial.setText(carData.getCar_series());
			holder.tv_del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 删除车辆
					index = position;
					deleteCar(position);
				}
			});
			holder.sv.ScorllRestFast();
			if (carData.getDevice_id() == null || carData.getDevice_id().equals("") || carData.getDevice_id().equals("0")) {
				holder.tv_update.setVisibility(View.GONE);
				holder.tv_remove.setVisibility(View.GONE);
				holder.bt_bind.setVisibility(View.VISIBLE);
				holder.bt_bind.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MyCarsActivity.this, DeviceAddActivity.class);
						intent.putExtra("car_id", carData.getObj_id());
						intent.putExtra("car_series_id", carData.getCar_series_id());
						intent.putExtra("car_series", carData.getCar_series());
						intent.putExtra("isBind", true);
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
	private void deleteCar(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyCarsActivity.this);
		builder.setTitle("提示");
		builder.setMessage("确定删除该车辆？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String url = Constant.BaseUrl + "vehicle/" + app.carDatas.get(position).getObj_id() 
							+ "?auth_code=" + app.auth_code;
				new Thread(new NetThread.DeleteThread(mHandler, url, Msg.DELET_CAR)).start();
			}
		}).setNegativeButton("否", null);
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	
	
	
	
	/**
     * Handler 处理消息
     */
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
    	
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            
            case Msg.DELET_CAR:
            	Log.d(TAG, "====删除车辆之后返回的信息===" + msg.obj.toString());
            	jsonDelete(msg.obj.toString());
            	break;
            }
        }
    };	
    
    
    
    /**
     * @param str 解析删除车辆之后返回信息处理
     */
    private void jsonDelete(String str) {
		try {
			JSONObject jsonObject = new JSONObject(str);
			if (jsonObject.getString("status_code").equals("0")) {
				Toast.makeText(MyCarsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
				app.carDatas.remove(index);
				carAdapter.notifyDataSetChanged();
				isRefresh = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(resultCode == 1){
    		carAdapter.notifyDataSetChanged();//刷新车辆列表
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
	
	
	@Override
	protected void onResume() {
		super.onResume();
		carAdapter.notifyDataSetChanged();//刷新车辆列表
	}
	
}
