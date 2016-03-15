package com.wicare.wistormpublicdemo;

import java.util.HashMap;

import com.wicare.wistorm.toolkit.DensityUtil;
import com.wicare.wistorm.ui.WTimeSelector;
import com.wicare.wistorm.ui.WTimeSelector.OnTimeChangedListener;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.model.Air;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;
import com.wicare.wistormpublicdemo.xutil.HttpAir;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

/**
 * @author Wu 空气净化器设置页面
 *
 */
public class AirSettingActivity extends Activity {
	
	private static String TAG = "AirSettingActivity";
	private TextView tvAirTimer;//定时时间
	private TextView tvdurationTime;//开启时间
	private Switch switchMode, switchTimer;//开关
	
	private LinearLayout llytDuration,llytSetDuration;
	private ImageView imgRight;
	private String time = "00:00";
	private ImageView imgDuration[] = new ImageView[5];
	private int imgDurationId[] = new int[] { R.id.iv_duration_30,
			R.id.iv_duration_60, R.id.iv_duration_90, R.id.iv_duration_100,
			R.id.iv_duration_120 };
	private HashMap<Integer, Integer> mapDurationId = new HashMap<Integer, Integer>();
	private HttpAir httpAir;
	private Handler uiHandler = null; 
	private String url = "";
	private String device_id = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_air_setting);
		ActivityCollector.addActivity(this);//添加当前活动进行管理
		
		url = getIntent().getStringExtra("air_url");
		device_id = getIntent().getStringExtra("device_id");
		
		uiHandler = new Handler(handleCallBack);
		httpAir = new HttpAir(this, uiHandler);
		httpAir.requestAir(url);
		
		
		TextView tvTitle = (TextView)findViewById(R.id.tv_top_title);
		tvTitle.setText("净化器设置");
		/*返回按键*/
		ImageView ivBack = (ImageView)findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		/*定时时间设置*/
		RelativeLayout rlAirTimer = (RelativeLayout)findViewById(R.id.rlyt_air_timer);
		rlAirTimer.setOnClickListener(onClickListener);
		/*定时时间显示*/
		tvAirTimer = (TextView)findViewById(R.id.tv_air_timer);
		/*净化器开启的时长*/
		tvdurationTime = (TextView)findViewById(R.id.tv_duration);
		/*净化器模式开关*/
		switchMode  = (Switch) findViewById(R.id.switchMode);
		switchMode.setOnTouchListener(onTouchListener);
		/*定时开启净化器*/
		switchTimer = (Switch) findViewById(R.id.switchTimer);
		switchTimer.setOnTouchListener(onTouchListener);
		
		llytDuration = (LinearLayout)findViewById(R.id.llytDuration);
		/*设置开启时长点击事件*/
		llytSetDuration = (LinearLayout)findViewById(R.id.llytSetDuration);
		llytSetDuration.setOnClickListener(onClickListener);
		imgRight = (ImageView) findViewById(R.id.imgRight);
		for (int i = 0; i < 5; i++) {
			int id = imgDurationId[i];
			imgDuration[i] = (ImageView) findViewById(id);
			imgDuration[i].setOnTouchListener(onTouchListener);
		}
		initDurationLayout();
	}
	
	
	/**
	 * Handler 回调
	 */
	public Handler.Callback handleCallBack = new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
		
			Bundle bundle = msg.getData();
			switch (msg.what) {
			case Msg.GET_CAR_AIR:
				Log.e(TAG, "----------"+msg.obj);
				initValue((Air) msg.obj);
				break;
//			case Msg.SET_AIR_MODEL:
//				Log.d(TAG, "response " + msg.obj.toString());
//				break;
			}
			return true;
		}
	};
	
	/**
	 * 点击事件
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.iv_top_back:
				setMode();
				AirSettingActivity.this.finish();
				break;

			case R.id.rlyt_air_timer:
				WTimeSelector mTimeSelector = new WTimeSelector(AirSettingActivity.this);
				mTimeSelector.setTime();
				mTimeSelector.setOnTimeChangedListener(new OnTimeChangedListener() {
					
					@Override
					public void onTimeChanged(String hour, String minute) {
						// TODO Auto-generated method stub
						time = hour + ":" + minute;
						tvAirTimer.setText(time);
					}
				});
				break;
				
			case R.id.llytSetDuration:
				if (llytDuration.getVisibility() == View.VISIBLE) {
					llytDuration.setVisibility(View.GONE);
					imgRight.invalidate();

				} else {
					llytDuration.setVisibility(View.VISIBLE);
					RotateAnimation ra = new RotateAnimation(0, 90, 0.5f, 0.5f);
					ra.setDuration(100);
					ra.setFillAfter(true);
					imgRight.startAnimation(ra);
				}
				
				break;
			}
		}
	};
	
	
	/**
	 * 触摸事件
	 */
	private OnTouchListener onTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (v.getId()) {
			
			case R.id.switchMode:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					switchTimer.setChecked(false);
					llytDuration.setVisibility(View.GONE);
				}
				break;

			case R.id.switchTimer:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					switchMode.setChecked(false);
					if (switchTimer.isChecked()) {
						llytDuration.setVisibility(View.GONE);
					} else {
						llytDuration.setVisibility(View.VISIBLE);
					}
				}
				break;
			case R.id.iv_duration_30:
			case R.id.iv_duration_60:
			case R.id.iv_duration_90:
			case R.id.iv_duration_100:
			case R.id.iv_duration_120:
				onChange(v.getId());
				break;
			}
			return false;
		}
	};
	
	
	
	/**
	 * 初始化界面
	 * 
	 * @param air
	 */
	public void initValue(Air air) {
		int mode = air.getAir_mode();
		switchMode.setChecked(false);
		switchTimer.setChecked(false);
		if (mode == Constant.AIR_MODE_SMART) {
			switchMode.setChecked(true);
		} else if (mode == Constant.AIR_MODE_TIMER) {
			switchTimer.setChecked(true);
			time = air.getAir_time();
			
			tvAirTimer.setText(air.getAir_time());
			mapDurationId.put(0, R.id.iv_duration_30);
			mapDurationId.put(30, R.id.iv_duration_30);
			mapDurationId.put(60, R.id.iv_duration_60);
			mapDurationId.put(90, R.id.iv_duration_90);
			mapDurationId.put(100, R.id.iv_duration_100);
			mapDurationId.put(120, R.id.iv_duration_120);

			int duration = air.getAir_duration();
			Log.i("AirSettingActivity", duration+"");
			int id = mapDurationId.get(duration);
			onChange(id);
			llytDuration.setVisibility(View.VISIBLE);
			
		}
		
		Log.e(TAG, device_id + "----------" + time + "---------" + mode);
	}

	
	/**
	 * 设置下面时间设置布局，
	 */
	public void initDurationLayout() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
		int padding = (int) DensityUtil.dip2px(this, 20); // llytDuration padding*2

		View viewLine = findViewById(R.id.viewAirLine);

		int width = (mScreenWidth - padding) / 5 * 4;
		FrameLayout.LayoutParams params = (LayoutParams) viewLine
				.getLayoutParams();
		params.width = width;
		viewLine.setLayoutParams(params);
		viewLine.invalidate();
		llytDuration.setVisibility(View.INVISIBLE);
		llytDuration.setVisibility(View.GONE);

	}
	
	/**
	 * @param id 
	 */
	private void onChange(int id) {
		int values[] = { 30, 60, 90, 100, 120 };
		int index = 0;
		for (int i = 0; i < 5; i++) {
			ImageView img = (ImageView) findViewById(imgDurationId[i]);
			if (imgDurationId[i] == id) {
				index = i;
				img.setImageResource(R.drawable.ico_switch_thum);
			} else {
				img.setImageResource(R.drawable.ico_circle_gray);
			}

		}
		tvdurationTime.setText(values[index] + "");
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			setMode();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	/**
	 * 设置模式
	 */
	public void setMode() {
		int mode = Constant.AIR_MODE_MANUL;
		if (switchMode.isChecked()) {
			mode = Constant.AIR_MODE_SMART;
		}

		if (switchTimer.isChecked()) {
			mode = Constant.AIR_MODE_TIMER;
		}

		int duration = Integer.parseInt(tvdurationTime.getText().toString());
		httpAir.setMode(device_id, mode, time, duration);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
}
