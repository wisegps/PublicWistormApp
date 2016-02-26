/*package com.wicare.wistormpublicdemo;

import java.util.regex.Pattern;

import org.json.JSONObject;

import com.wicare.wistorm.http.HttpThread;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.Msg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

*//**
 * @author WU
 *
 * 更新终端信息
 *//*
public class UpdataDeviceActivity extends Activity {

	static final String TAG = "UpdataDeviceActivity";
	private static final int REQUEST_CODE = 2;
	
	public static final int  UPDATA_RESULT_CODE = 10;
	帐号输入框
	private EditText etAccount;
	帐号
	private String account;
	判断是否是手机号
	boolean isPhone = true;
	修改终端
	int mark = 1; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_register);
		
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("修改终端");
		Button btnUpdataDevice = (Button) findViewById(R.id.btn_register);
		btnUpdataDevice.setOnClickListener(onClickListener);
		btnUpdataDevice.setText("验证身份");
		etAccount = (EditText)findViewById(R.id.et_account_num);
		Intent intent = getIntent();
		etAccount.setText(intent.getStringExtra("account"));
		etAccount.setEnabled(false);
		
		
		
	}
	
	
	*//**
	 * 点击事件监听
	 *//*
	OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				
			case R.id.iv_top_back://返回
				finish();
				break;
			case R.id.btn_register://修改终端验证身份
				
				checkAccount();
				break;
			}
		}
	};
	
	
	*//**
     * Handler 处理消息
     *//*
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
    	
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            
            case Msg.ACCOUNT_IS_EXIST:
            	Log.d(TAG, "====是否存在==="+msg.obj.toString());
            	jsonIsAccountExist(msg.obj.toString());
            	break;
            }
        }
    };	
    
	
	*//**
	 *  判断帐号是否存在
	 *//*
	private void checkAccount() {
		account = etAccount.getText().toString().trim();
		String url = Constant.BaseUrl + "exists?query_type=6&value=" + account;
		if (account.equals("")) {
			Toast.makeText(UpdataDeviceActivity.this, "请填写手机号码或邮箱", Toast.LENGTH_SHORT).show();
		} else if (account.length() == 11 && isNumeric(account)) {
			isPhone = true;
			//开启线程获取服务器数据
			new HttpThread.getDataThread(mHandler, url, Msg.ACCOUNT_IS_EXIST).start();
		} else if (isEmail(account)) {
			isPhone = false;
			//开启线程获取服务器数据
			new HttpThread.getDataThread(mHandler, url, Msg.ACCOUNT_IS_EXIST).start();
		} else {
			Toast.makeText(UpdataDeviceActivity.this, "您输入的账号不正确",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	*//**
	 * @param strJson Json 数据
	 *//*
	private void jsonIsAccountExist(String strJson){
		try {
			JSONObject jsonObject = new JSONObject(strJson);
			boolean isExist = jsonObject.getBoolean("exist");
			if(!isExist){
				Toast.makeText(UpdataDeviceActivity.this, "该账号已注册，请登录", Toast.LENGTH_SHORT).show();
			}else{
				AlertDialog.Builder dialog = new AlertDialog.Builder(UpdataDeviceActivity.this);
				if (isPhone) {
					dialog.setTitle("确认");
					dialog.setMessage("我们将发送验证码短信到您的手机，请尽快查收\n" + account);
				} else {
					dialog.setTitle("确认");
					dialog.setMessage("我们将发送验证码到您的邮箱，请尽快查收\n" + account);
				}
				dialog.setPositiveButton("好",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent i_identify = new Intent(UpdataDeviceActivity.this,IdentifyCodeAcitvity.class);
								i_identify.putExtra("account", account);
								i_identify.putExtra("isPhone", isPhone);
								i_identify.putExtra("device_update", true);
								i_identify.putExtra("mark", 1);//mark == 1 修改终端
								startActivityForResult(i_identify, 2);
							}
						}).setNegativeButton("取消", null).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	*//**
	 * @param str 判断是否是数字手机号
	 * @return
	 *//*
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	*//**
	 * @param str 判断是否是邮箱
	 * @return
	 *//*
	public static boolean isEmail(String str) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		return pattern.matcher(str).matches();
	}
	
}
*/