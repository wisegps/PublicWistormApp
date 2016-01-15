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

}
