package com.wicare.wistormpublicdemo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wicare.wistorm.http.HttpThread;
import com.wicare.wistorm.toolkit.SystemTools;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.HandlerMsg;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;
import com.wicare.wistormpublicdemo.xutil.NetThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu
 *
 * 填写验证码页面
 */
public class IdentifyCodeAcitvity extends Activity {
	
	static final String TAG = "IdentifyCodeAcitvity";
	/*是否是手机号*/
	private boolean isPhone = true;
	/*帐号*/
	private String account;
	/*验证码*/
	private EditText et_identify_code;
	/*第一次输入密码*/
	private EditText et_pwd_first;
	/*第二次输入密码*/
	private EditText et_pwd_again;
	/*后台返回的验证码*/
	private String identify_code = null;
	/**
	 * 0 注册 ， 1 修改密码，
	 */
	private int mark = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identify_code);
		ActivityCollector.addActivity(this);//添加当前活动进行管理
		Button btnIdentify = (Button)findViewById(R.id.btn_identify);
		btnIdentify.setOnClickListener(onClickListener);
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("填写验证码");
		TextView tv_account_num = (TextView)findViewById(R.id.tv_account_number);
		et_identify_code = (EditText)findViewById(R.id.et_identify);
		et_pwd_first     = (EditText)findViewById(R.id.et_login_pwd);
		et_pwd_again     = (EditText)findViewById(R.id.et_login_pwd_again);
		Intent intent = getIntent();
		isPhone = intent.getBooleanExtra("isPhone", true);
		account = intent.getStringExtra("account");
		mark    = intent.getIntExtra("mark", 0);
		if(mark == 0){
			btnIdentify.setText("下一步");
		}else if(mark == 1){
			btnIdentify.setText("重置密码");
		}
		tv_account_num.setText(account);
		/*获取后台验证码*/
		getIdentifyCode();
		
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
			case R.id.btn_identify:
				submit ();
				break;
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
            
            case HandlerMsg.GET_IDENTIFY_CODE:
            	Log.d(TAG, "====获取后台===" + msg.obj.toString());
            	jsonIdentifyCode(msg.obj.toString());
            	break;
            case HandlerMsg.RESRT_ACCOUNT_PWD:
            	Log.d(TAG, "====获取后台===" + msg.obj.toString());
            	jsonReset(msg.obj.toString());
            	break;
            }
        }
    };	
    
	
    /**
     * 填写好验证码后提交
     */
    private void submit (){
    	String strIdentifyCode  = et_identify_code.getText().toString().trim();
    	String strPasswordFirst = et_pwd_first.getText().toString().trim();
    	String strPasswordAgain = et_pwd_again.getText().toString().trim();
    	
    	if(!strPasswordFirst.equals(strPasswordAgain)){
    		Toast.makeText(IdentifyCodeAcitvity.this, "2次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(!strIdentifyCode.equals(identify_code)){
    		Toast.makeText(IdentifyCodeAcitvity.this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	if(mark == 0){
    		Intent intent = new Intent(IdentifyCodeAcitvity.this,RegisterInfoActivity.class);
        	intent.putExtra("password", strPasswordFirst);
    		intent.putExtra("account" , account);
    		intent.putExtra("isPhone" , isPhone);
        	startActivity(intent);
    	}else if(mark ==1){
    		// TODO 重置密码
			String url = Constant.BaseUrl
					+ "customer/password/reset?account=" + account
					+ "&password=" + SystemTools.getM5DEndo(strPasswordFirst);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//开启线程获取服务器数据
			new NetThread.putDataThread(mHandler, url, params, HandlerMsg.RESRT_ACCOUNT_PWD).start();
    	}	
    }
    
    
    /**
     * @param strJson 解析获取到的后台验证码
     */
    private void jsonIdentifyCode(String strJson){
    	try {
			JSONObject jsonObject = new JSONObject(strJson);
			identify_code = jsonObject.getString("valid_code");
			Log.d(TAG, "====解析到的验证码是===" + identify_code);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    
	/**
	 * 获取后台的验证码
	 */
	private void getIdentifyCode() {
		String url;
		if (isPhone) {
			url = Constant.BaseUrl + "valid_code?mobile=" + account + "&type=1";
		} else {
			url = Constant.BaseUrl + "valid_code/email?email=" + account + "&type=1";
		}
		//开启线程获取服务器数据
		new HttpThread.getDataThread(mHandler, url, HandlerMsg.GET_IDENTIFY_CODE).start();
	}
	
	
	/**
	 * @param strJson
	 */
	private void jsonReset(String strJson) {
		try {
			JSONObject jsonObject = new JSONObject(strJson);
			if (jsonObject.getString("status_code").equals("0")) {
				setResult(2);
				finish();
				Toast.makeText(IdentifyCodeAcitvity.this, "修改成功", Toast.LENGTH_SHORT) .show();
			} else {
				Toast.makeText(IdentifyCodeAcitvity.this, "修改失败", Toast.LENGTH_SHORT) .show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

}
