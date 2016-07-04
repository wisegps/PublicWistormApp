package xutil;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * @author Wu Activity管理
 *
 */
public class ActivityCollector {
	
	public static List<Activity> activities = new ArrayList<Activity>();
	
	/**
	 * @param activity 添加一个活动
	 */
	public static void addActivity(Activity activity){
		activities.add(activity);
	}

	/**
	 * @param activity 移除一个活动
	 */
	public static void removeActivity(Activity activity){
		activities.remove(activity);
	}
	
	/**
	 * finish 所有活动
	 */
	public static void finishAll(){
		for(Activity activity:activities){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}
}
