package com.wicare.wistormpublicdemo.app;

import android.os.Environment;

/**
 * @author Wu
 * 
 * 常量
 */
public class Constant {
	
	/** 共用 URL */
	public static String BaseUrl = "http://api.bibibaba.cn/";
	
	/** 图片路径存储地址 **/
	public static String BasePath = Environment.getExternalStorageDirectory().getPath() + "/wistorm/";
	
	/** 存放用户头像 **/
	public static String userIconPath = BasePath + "userIcon/";
	
	/**
	 * SharedPreferences数据共享名称
	 */
	public static final String sharedPreferencesName = "userData";
	/** 存放用户帐号 **/
	public static final String sp_account = "sp_account";
	/** 存放用户密码 **/
	public static final String sp_pwd = "sp_pwd";
	
	/** 存放个人信息 **/
	public static final String sp_customer = "sp_customer";
	
	/** 登录广播，首页获取车辆用到 */
	public static String Wicare_Login = "com.wicare.wistormpublicdemo.login";
	/** 退出登录广播，首页获取车辆用到 */
	public static String Wicare_Login_Out = "com.wicare.wistormpublicdemo.login_out";

}
