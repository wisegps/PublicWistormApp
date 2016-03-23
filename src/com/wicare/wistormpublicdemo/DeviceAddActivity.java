package com.wicare.wistormpublicdemo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.wicare.wistorm.http.HttpThread;
import com.wicare.wistorm.toolkit.WZxingActivity;
import com.wicare.wistorm.ui.WLoading;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.HandlerMsg;
import com.wicare.wistormpublicdemo.app.MyApplication;
import com.wicare.wistormpublicdemo.model.CarData;
import com.wicare.wistormpublicdemo.xutil.NetThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu
 * 
 * 绑定设备页面
 *
 */
public class DeviceAddActivity extends Activity{
	
	static final String TAG = "DeviceAddActivity";
	
	/*序列号*/
	private EditText et_serial;
	/*sim卡*/
	private EditText et_sim;
	/*硬件版本*/
	private EditText et_hardware_version;
	/*软件版本*/
	private EditText et_software_version;
//	/*服务截止*/
//	private EditText et_end_time;
	/*点击扫描二维码获取序列号*/
	private ImageView iv_serial;
	/*净化器接口位置提示*/
	private TextView tv_prompt;
	/*提交信息*/
	private TextView tv_finish;
	/*加载框*/
	private WLoading mWLoading = null;
	
	private String device_id;
	
	MyApplication app;
	
	private int car_id;
//	private String car_series_id;
//	private String car_series;
	
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
		car_id = intent.getIntExtra("car_id", 0);
//		car_series_id = intent.getStringExtra("car_series_id");
//		car_series = intent.getStringExtra("car_series");
		
		iv_serial = (ImageView)findViewById(R.id.iv_serial);
		iv_serial.setOnClickListener(onClickListener);
		
		et_serial = (EditText)findViewById(R.id.et_serial);
		et_serial.setOnFocusChangeListener(onFocusChangeListener);
		et_sim = (EditText)findViewById(R.id.et_sim);
		et_sim.setOnFocusChangeListener(onFocusChangeListener);
		
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
		startActivityForResult(new Intent(DeviceAddActivity.this, WZxingActivity.class), 0);
		
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
				startActivityForResult(new Intent(DeviceAddActivity.this, WZxingActivity.class), 0);
				break;
				
			case R.id.tv_finish://提交信息
				addDevice();
				break;
			}
		}
	};
	
	
	/**
	 * EditText 监听
	 */
	OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				switch (v.getId()) {
				case R.id.et_serial:
					checkSerial();
					break;
				case R.id.et_sim:
					String sim = et_sim.getText().toString().trim();
					if (sim.length() != 11) {
						et_sim.setError("sim格式不对");
					}
					break;
				}
			}
		}
	};
	
	
	
	/**
     * Handler 处理消息
     */
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
    	
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            
            case HandlerMsg.CHECK_SERIAL:
            	Log.d(TAG, "====检查序列号是否符合返回的信息===" + msg.obj.toString());
            	jsonSerial(msg.obj.toString());
            	break;
            	
            case HandlerMsg.ADD_DEVICE:
            	Log.d(TAG, "====添加设备返回的信息===" + msg.obj.toString());
            	jsonAddSerial(msg.obj.toString());
            	break;
            	
            case HandlerMsg.UPDATA_SIM:
            	Log.d(TAG, "====添加SIM返回的信息===" + msg.obj.toString());
            	jsonUpdataSIM(msg.obj.toString());
            	break;
            	
            case HandlerMsg.UPDATA_USER:
            	Log.d(TAG, "====添加设备到用户返回的信息===" + msg.obj.toString());
            	jsonUpdataUser(msg.obj.toString());
            	break;
            	
            case HandlerMsg.UPDATA_CAR:
            	stopProgressDialog();
            	updateVariableCarData();//更新数据	
				Intent intent = new Intent();
				setResult(1, intent);
				finish();
				Intent intent1 = new Intent(Constant.Wicare_Refresh_Car);
				sendBroadcast(intent1);
            	break;
            }
        }
    };	
	

    /**
	 * 更新内存里的数据
	 */
	private void updateVariableCarData() {
		for (CarData carData : app.carDatas) {
			if (carData.getObj_id() == Integer.valueOf(car_id)) {
				carData.setDevice_id(device_id);
				carData.setSerial(et_serial.getText().toString().trim());
				break;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 取得返回信息
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			et_serial.setText(scanResult);
			checkSerial();
		}
	};
	
	
	/**
	 * 添加设备
	 */
	private void addDevice() {
		String serial = et_serial.getText().toString().trim();
		String sim = et_sim.getText().toString().trim();
		if (serial.equals("")) {
			et_serial.setError("序列号不能为空");
		} else if (sim.length() != 11) {
			et_sim.setError("sim格式不对");
		} else {		
			String url = Constant.BaseUrl + "device/serial/" + serial + "?auth_code=" + app.auth_code;
			new HttpThread.getDataThread(mHandler, url, HandlerMsg.ADD_DEVICE).start();
			saveDataIn();
		}
	}
	
	/**
	 * @param strJson 解析添加设备返回的信息
	 */
	private void jsonAddSerial(String strJson) {

		try {
			if (strJson.equals("")) {
				et_serial.setError("序列号不存在");
				saveDataOver();
			} else {
				JSONObject jsonObject = new JSONObject(strJson);
				int custID = jsonObject.getInt("cust_id");
				if (custID == Integer.valueOf(app.cust_id) || custID == 0) {
					String sim = et_sim.getText().toString().trim();
					device_id = jsonObject.getString("device_id");
					String url = Constant.BaseUrl + "device/" + device_id + "/sim?auth_code=" + app.auth_code;
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("sim", sim));
					
					new NetThread.putDataThread(mHandler, url, params, HandlerMsg.UPDATA_SIM).start();
				} else {
					Toast.makeText(DeviceAddActivity.this, "该终端已被其他用户绑定，无法再次绑定", Toast.LENGTH_LONG).show();
					saveDataOver();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param strJson
	 */
	private void jsonUpdataSIM(String strJson){
		try {
			String status_code = new JSONObject(strJson) .getString("status_code");
			if (status_code.equals("0")) {
				String url_sim = Constant.BaseUrl + "device/" + device_id + "/customer?auth_code="
						+ app.auth_code;
				List<NameValuePair> paramSim = new ArrayList<NameValuePair>();
				paramSim.add(new BasicNameValuePair("cust_id", app.cust_id));
				new NetThread.putDataThread(mHandler, url_sim, paramSim, HandlerMsg.UPDATA_USER).start();
			} else {
				saveDataOver();
				Toast.makeText(DeviceAddActivity.this, "绑定终端失败", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			saveDataOver();
			Toast.makeText(DeviceAddActivity.this, "绑定终端失败", Toast.LENGTH_SHORT).show();
		}

	}
	
	
	/**
	 * @param strJson
	 */
	private void jsonUpdataUser(String strJson){
		try {
			String status_code = new JSONObject(strJson) .getString("status_code");
			if (status_code.equals("0")) {
				// 绑定车辆
				final String url = Constant.BaseUrl + "vehicle/" + car_id + "/device?auth_code=" + app.auth_code;
				
				final List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("device_id", device_id));
		
				new NetThread.putDataThread(mHandler, url, params, HandlerMsg.UPDATA_CAR).start();

			} else {
				saveDataOver();
				Toast.makeText(DeviceAddActivity.this, "绑定终端失败", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			saveDataOver();
			Toast.makeText(DeviceAddActivity.this, "绑定终端失败", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	
	
	/**
	 * 检查序列号是否存在
	 */
	private void checkSerial() {
		String serial = et_serial.getText().toString().trim();
		String url = Constant.BaseUrl + "device/serial/" + serial
				+ "?auth_code=" + app.auth_code;
		new HttpThread.getDataThread(mHandler, url, HandlerMsg.CHECK_SERIAL).start();
	}
	
	/**
	 * @param strJson 
	 */
	private void jsonSerial(String strJson) {
		try {
			if (strJson.equals("")) {
				et_serial.setError("序列号不存在");
			} else {
				JSONObject jsonObject = new JSONObject(strJson);
				String status = jsonObject.getString("status");
				//返回字段"status"说明（ 0：未出库 1：已出库 2: 确认收货 3：已激活 ）
				if (status.equals("3")) {
					et_sim.setText(jsonObject.getString("sim"));
				} else{
					et_serial.setError("终端尚未激活");
				}
				et_hardware_version.setText(jsonObject.getString("hardware_version"));
				et_software_version.setText(jsonObject.getString("software_version"));	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void saveDataIn() {
		et_serial.setEnabled(false);
		et_sim.setEnabled(false);
		iv_serial.setEnabled(false);
		tv_finish.setEnabled(false);
		startProgressDialog();
	}

	private void saveDataOver() {
		et_serial.setEnabled(true);
		et_sim.setEnabled(true);
		iv_serial.setEnabled(true);
		tv_finish.setEnabled(true);
		stopProgressDialog();
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
