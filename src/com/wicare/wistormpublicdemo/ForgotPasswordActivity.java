package com.wicare.wistormpublicdemo;

import java.util.regex.Pattern;

import org.json.JSONObject;

import com.wicare.wistorm.http.HttpThread;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.HandlerMsg;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

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

/**
 * @author Wu
 * 
 * 忘记密码页面
 */
public class ForgotPasswordActivity extends Activity {

	static final String TAG = "ForgotPasswordActivity";
	/*帐号输入*/
	private EditText etAccount;
	/*帐号*/
	private String account;
	/*判断是否是手机号*/
	boolean isPhone = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		ActivityCollector.addActivity(this);//添加当前活动进行管理
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("忘记密码");
		findViewById(R.id.btn_forgot_pwd).setOnClickListener(onClickListener);
		etAccount = (EditText)findViewById(R.id.et_account_num);
		
		
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
			case R.id.btn_forgot_pwd:
				isExist();
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
            
            case HandlerMsg.ACCOUNT_IS_EXIST:
            	Log.d(TAG, "====是否存在===" + msg.obj.toString());
            	jsonIsExist(msg.obj.toString());
            	break;
            }
        } 
    };	
	
    /**
	 * @param strJson Json 数据
	 */
	private void jsonIsExist(String strJson){
		try {
			JSONObject jsonObject = new JSONObject(strJson);
			boolean isExist = jsonObject.getBoolean("exist");
			if(isExist){
				AlertDialog.Builder dialog = new AlertDialog.Builder(ForgotPasswordActivity.this);
				if(isPhone){
					dialog.setTitle("确认手机帐号");
					dialog.setMessage("我们已将验证码短信发送到你的手机，请尽快查收\n" + account);
				}else{
					dialog.setTitle("确认手机帐号");
					dialog.setMessage("我们已将验证码短信发送到你的邮箱，请尽快查收\n" + account);
				}
				dialog.setPositiveButton("好",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent i_identify = new Intent(ForgotPasswordActivity.this,IdentifyCodeAcitvity.class);
						i_identify.putExtra("account", account);
						i_identify.putExtra("isPhone", isPhone);
						i_identify.putExtra("mark", 1);
						Log.d(TAG, "====是account存在===" + account);
						startActivityForResult(i_identify, 2);
					}
				});
				dialog.setNegativeButton("取消", null);
				dialog.show();
			}else{
				if(isPhone){
					Toast.makeText(ForgotPasswordActivity.this, "你的手机没有注册，请重新检查", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(ForgotPasswordActivity.this, "你的邮箱没有注册，请重新检查", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
    
	/**
	 * 注册 判断帐号是否存在
	 */
	private void isExist() {
		account = etAccount.getText().toString().trim();
		String url = Constant.BaseUrl + "exists?query_type=6&value=" + account;
		if (account.equals("")) {
			Toast.makeText(ForgotPasswordActivity.this, "请填写手机号码或邮箱", Toast.LENGTH_SHORT).show();
		} else if (account.length() == 11 && isNumeric(account)) {
			isPhone = true;
			//开启线程获取服务器数据
			new HttpThread.getDataThread(mHandler, url, HandlerMsg.ACCOUNT_IS_EXIST).start();
		} else if (isEmail(account)) {
			isPhone = false;
			//开启线程获取服务器数据
			new HttpThread.getDataThread(mHandler, url, HandlerMsg.ACCOUNT_IS_EXIST).start();
		} else {
			Toast.makeText(ForgotPasswordActivity.this, "您输入的账号不正确",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * @param str 判断是否是数字手机号
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * @param str 判断是否是邮箱
	 * @return
	 */
	public static boolean isEmail(String str) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		return pattern.matcher(str).matches();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 2:
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	
}
