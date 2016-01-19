package com.wicare.wistormpublicdemo;

import java.util.regex.Pattern;

import org.json.JSONObject;

import com.wicare.wistorm.http.HttpThread;
import com.wicare.wistorm.toolkit.WCarBrandSelector;
import com.wicare.wistormpublicdemo.app.Constant;
import com.wicare.wistormpublicdemo.app.Msg;
import com.wicare.wistormpublicdemo.xutil.ActivityCollector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu 
 * 
 * 注册页面
 *
 */
public class RegisterActivity extends Activity {
	
	static final String TAG = "RegisterActivity";
	/*帐号输入框*/
	private EditText etAccount;
	/*协议*/
	private TextView tvAgreement;
	/*帐号*/
	private String account;
	/*判断是否是手机号*/
	boolean isPhone = true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ActivityCollector.addActivity(this);//添加当前活动进行管理
		ImageView ivBack = (ImageView) findViewById(R.id.iv_top_back);
		ivBack.setVisibility(View.VISIBLE);
		ivBack.setOnClickListener(onClickListener);
		TextView tvTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTitle.setText("注册");
		findViewById(R.id.btn_register).setOnClickListener(onClickListener);
		etAccount = (EditText)findViewById(R.id.et_account_num);
		tvAgreement = (TextView)findViewById(R.id.tv_agreement);
		setAgreement();
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
			case R.id.btn_register:
				register();
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
            
            case Msg.ACCOUNT_IS_EXIST:
            	Log.d(TAG, "====是否存在==="+msg.obj.toString());
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
				Toast.makeText(RegisterActivity.this, "该账号已注册，请登录", Toast.LENGTH_SHORT).show();
			}else{
				AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
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
						Intent i_identify = new Intent(RegisterActivity.this,IdentifyCodeAcitvity.class);
						i_identify.putExtra("account", account);
						i_identify.putExtra("isPhone", isPhone);
						i_identify.putExtra("mark", 0);
						Log.d(TAG, "====是account存在===" + account);
						startActivity(i_identify);
					}
				});
				dialog.setNegativeButton("取消", null);
				dialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 注册 判断帐号是否存在
	 */
	private void register() {
		account = etAccount.getText().toString().trim();
		String url = Constant.BaseUrl + "exists?query_type=6&value=" + account;
		if (account.equals("")) {
			Toast.makeText(RegisterActivity.this, "请填写手机号码或邮箱", Toast.LENGTH_SHORT).show();
		} else if (account.length() == 11 && isNumeric(account)) {
			isPhone = true;
			//开启线程获取服务器数据
			new HttpThread.getDataThread(mHandler, url, Msg.ACCOUNT_IS_EXIST).start();
		} else if (isEmail(account)) {
			isPhone = false;
			//开启线程获取服务器数据
			new HttpThread.getDataThread(mHandler, url, Msg.ACCOUNT_IS_EXIST).start();
		} else {
			Toast.makeText(RegisterActivity.this, "您输入的账号不正确",
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
	
	/**
	 * 设置协议
	 */
	private void setAgreement() {
		SpannableString sp = new SpannableString("点击上面的注册按钮，即表示同意《叭叭软件许可及服务条款》");
		sp.setSpan(new URLSpan("http://api.bibibaba.cn/help/fwtk"), 16, 27,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvAgreement.setText(sp);
		tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	
}
