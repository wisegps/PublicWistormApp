package application;

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
	
	public final static String AIR_POWER_ON  = "{switch:1}";
	public final static String AIR_POWER_OFF = "{switch:0}";
	
	
	public final static String AIR_NORMAL_MODEL = "{air_mode:1}";
	public final static String AIR_SMART_MODEL  = "{air_mode:0}";
	public final static String AIR_TIMER_MODEL  = "{air_mode:2}";
	
	
	public final static String LOW_SPEED    = "{air_speed:1}";
	public final static String MIDDLE_SPEED = "{air_speed:2}";
	public final static String HIGHT_SPEED  = "{air_speed:3}";
	
}
