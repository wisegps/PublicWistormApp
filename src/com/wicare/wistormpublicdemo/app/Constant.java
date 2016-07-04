package com.wicare.wistormpublicdemo.app;

import android.os.Environment;

/**
 * @author Wu
 * 
 * 常量
 */
public class Constant {
	
	public static boolean IS_DEBUG = true;
	
	/** 共用 URL */
	public static String BaseUrl = "http://api.bibibaba.cn/";
	
	/** 图片路径存储地址 **/
	public static String BasePath = Environment.getExternalStorageDirectory().getPath() + "/wistorm/";
	
	/** 存放用户头像 **/
	public static String userIconPath = BasePath + "userIcon/";
	
	/** 车品牌logo **/
	
	//注意 这里的车辆logo图片路径一定要和 Wistorm 中的图片设置一致
	public static String VehicleLogoPath = BasePath + "vehicleLogo/";
	
	
	/**
	 * SharedPreferences数据共享名称
	 */
	public static final String SP_USER_DATA = "userData";
	/** 存放用户帐号 **/
	public static final String SP_ACCOUNT   = "sp_account";
	/** 存放用户密码 **/
	public static final String SP_PWD       = "sp_pwd";
	
	/** 登录广播，首页获取车辆用到 */
	public static String Wicare_Login = "com.wicare.wistormpublicdemo.login";
	/** 退出登录广播，首页获取车辆用到 */
	public static String Wicare_Login_Out = "com.wicare.wistormpublicdemo.login_out";
	/** 车辆更改 */
	public static String Wicare_Refresh_Car = "com.wicare.wistormpublicdemo.refresh_car";
	
	//空气净化器设置运行模式,手工，智能，定时
	public static final int AIR_MODE_MANUL = 0;
	public static final int AIR_MODE_SMART = 1;
	public static final int AIR_MODE_TIMER = 2;
	
}
