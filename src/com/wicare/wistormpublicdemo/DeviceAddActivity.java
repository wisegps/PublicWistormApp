package com.wicare.wistormpublicdemo;

import java.util.HashMap;
import org.json.JSONObject;

import xutil.L;
import xutil.T;

import com.android.volley.VolleyError;
import com.wicare.wistorm.api.WDeviceApi;
import com.wicare.wistorm.api.WVehicleApi;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.toolkit.WZxingActivity;
import com.wicare.wistorm.ui.WLoading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import application.MyApplication;

/**
 * @author Wu
 * 
 * 绑定设备页面   流程 使用序列号查询设备信息 然后更新设备信息  cust_id 绑定到客户id 下，最后更新车辆信息  绑定到车辆下
 *
 */
public class DeviceAddActivity extends Activity{
	
	static final String TAG = "DeviceAddActivity";
	private final int REQUEST_ZXING_CODE = 0 ;
	public static final int BIND_RESULT_CODE   = 2 ;
	/*序列号*/
	private EditText et_serial;
	/*硬件版本*/
	private EditText et_hardware_version;
	/*软件版本*/
	private EditText et_software_version;
	/*点击扫描二维码获取序列号*/
	private ImageView iv_serial;
	/*净化器接口位置提示*/
	private TextView tv_prompt;
	/*提交信息*/
	private TextView tv_finish;
	/*加载框*/
	private WLoading mWLoading = null;
	
	private MyApplication app;
	
	private String device_id = null; //设备id
	private String obj_id = null; // 车辆ID
	
	private Context mContext;
	private WDeviceApi deviceApi;
	private WVehicleApi vehicleApi;
	
	private boolean isCheckDevice = false; // 是否查询了设备信息
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device);
		app = (MyApplication)getApplication();
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("绑定设备");
		
		Intent intent = getIntent();
		obj_id = intent.getStringExtra("obj_id");
		
		iv_serial = (ImageView)findViewById(R.id.iv_serial);
		iv_serial.setOnClickListener(onClickListener);
		
		et_serial = (EditText)findViewById(R.id.et_serial);
		ImageView iv_search = (ImageView)findViewById(R.id.iv_search);
		iv_search.setOnClickListener(onClickListener );
		
		et_hardware_version = (EditText)findViewById(R.id.et_hardware_version);
		et_software_version = (EditText)findViewById(R.id.et_software_version);
		tv_finish   = (TextView)findViewById(R.id.tv_finish);
		tv_finish.setOnClickListener(onClickListener);
		
		et_hardware_version.setEnabled(false);
		et_software_version.setEnabled(false);
		
		tv_prompt = (TextView) findViewById(R.id.tv_prompt);
		SpannableString sp = new SpannableString( "请扫描终端的二维码或者输入对应的序列号进行绑定(常用OBD安装位置)");
		sp.setSpan(new URLSpan("http://api.bibibaba.cn/help/obd"), 24, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_prompt.setText(sp);
		tv_prompt.setMovementMethod(LinkMovementMethod.getInstance());
		//二维码扫描
		startActivityForResult(new Intent(DeviceAddActivity.this, WZxingActivity.class), REQUEST_ZXING_CODE);
		init();
	}
	
	
	/**
	 * 初始化
	 */
	private void init(){
		deviceApi = new WDeviceApi(mContext);
		vehicleApi = new WVehicleApi(mContext);
		BaseVolley.init(mContext);
	}
	
	
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
				
			case R.id.iv_serial://二维码扫描
				startActivityForResult(new Intent(DeviceAddActivity.this, WZxingActivity.class), REQUEST_ZXING_CODE);
				break;
				
			case R.id.tv_finish://提交信息
				
				
				if(isCheckDevice){
					if(cust_id.equals("0")){
						bindDevice();
					}else{
						T.showShort(DeviceAddActivity.this, "该设备已经被绑定");
					}
				}else{
					T.showShort(DeviceAddActivity.this, "请先查询设备信息");
				}
				
				
				break;
			case R.id.iv_search:
				getDeviceData(et_serial.getText().toString().trim());
				break;
			}
		}
	};

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 取得返回信息
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			et_serial.setText(scanResult);
			getDeviceData(scanResult);
		}
	};
	
	
	
	/**
	 * 获取设备信息
	 */
	private void getDeviceData(String serial){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token",app.access_token);
		params.put("serial",serial);
		
		String fields = "device_id,serial,hardware_version,software_version,status,sim,cust_id";
		
		deviceApi.get(params, fields, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				L.d(TAG, "获取设备返回信息：" + response);
				parseDeviceData(response);
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	
	/**
	 * 解析返回的设备数据
	 * 
	 * @param strJson
	 */
	String cust_id;
	private void parseDeviceData(String strJson){
		try {
			if (strJson.contains("htm")) {
				et_serial.setError("序列号不存在");
			} else {
				JSONObject jsonObject = new JSONObject(strJson);
				String status = jsonObject.getString("status");
				cust_id = jsonObject.getString("cust_id");
				//返回字段"status"说明（ 0：未出库 1：已出库 2: 确认收货 3：已激活 ）
				if (!status.equals("3")) {
					T.showShort(mContext, "终端尚未激活");
				} 
				if(!cust_id.equals("0")){
					et_serial.setError("设备已经被绑定");
				}
				isCheckDevice = true;
				device_id = jsonObject.getString("device_id");
				et_hardware_version.setText(jsonObject.getString("hardware_version"));
				et_software_version.setText(jsonObject.getString("software_version"));	
			}	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 绑定设备
	 */
	private void bindDevice() {
		String serial = et_serial.getText().toString().trim();
		if (serial.equals("") || serial == null) {
			et_serial.setError("序列号不能为空");
			return;
		}else {	
			startProgressDialog();
			L.d(TAG, "字段信息 ：" + obj_id + "=========" + device_id + "-----" + app.cust_id);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", app.access_token);
			params.put("_device_id",  device_id);//更改条件就是 obj_id 前面加下划线"_",后面参数不用修改
			params.put("cust_id", app.cust_id);
			
			deviceApi.update(params, "", new OnSuccess() {
				
				@Override
				protected void onSuccess(String response) {
					// TODO Auto-generated method stub
					L.e(TAG, "更新设备信息 ："+ response);
					updataVehicle();
				}
			}, new OnFailure() {
				
				@Override
				protected void onFailure(VolleyError error) {
					// TODO Auto-generated method stub
					stopProgressDialog();
				}
			});	
		}
	}
	
	/**
	 * 更新device_id 到 车辆表中   绑定
	 */
	private void updataVehicle(){
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token",app.access_token);
		params.put("_obj_id",  obj_id);//更改条件就是 obj_id 前面加下划线"_",后面参数不用修改
		params.put("cust_id", app.cust_id);
		params.put("device_id", device_id);
		
		vehicleApi.update(params, new OnSuccess() {
			
			@Override
			protected void onSuccess(String response) {
				// TODO Auto-generated method stub
				L.d(TAG, "绑定设备返回信息 ：" + response);
				parseBindDevice(response);
			}
		}, new OnFailure() {
			
			@Override
			protected void onFailure(VolleyError error) {
				// TODO Auto-generated method stub
				T.showShort(mContext, "绑定设备失败！");
				stopProgressDialog();
			}
		});
	}
	
	
	
	
	/**
	 * 解析绑定设备
	 * 
	 * @param strJson {"status_code":0}
	 */
	private void parseBindDevice(String strJson){
		try {
			
			JSONObject obj = new JSONObject(strJson);
			
			L.d(TAG, "错误信息 ：" + obj.toString());
			
			String status =  obj.getString("status_code");
			
			if("0".equals(status)){
				stopProgressDialog();
				setResult(BIND_RESULT_CODE);
				finish();
			}else{
				T.showShort(mContext, "绑定设备失败！");
				stopProgressDialog();
			}
		} catch (Exception e) {
			// TODO: handle exception
			stopProgressDialog();
		}
	}
	

	/**
	 * 开始显示加载
	 */
	private void startProgressDialog() {
		if (mWLoading == null) {
			mWLoading = WLoading.createDialog(this,WLoading.LARGE_TYPE);
			mWLoading.setMessage("绑定设备中...");
		}
		mWLoading.show();
	}

	/**
	 * 关闭加载提示
	 */
	private void stopProgressDialog() {
		if (mWLoading != null) {
			mWLoading.dismiss();
			mWLoading = null;
		}
	}

}
