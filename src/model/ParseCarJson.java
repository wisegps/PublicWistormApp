package model;

import org.json.JSONException;
import org.json.JSONObject;

import xutil.L;

public class ParseCarJson {

	String TAG = "JSON_CAR";
	public static CarData getData(String strJson){
		
		CarData cardata = new CarData();
		try {
			JSONObject jsonObject = new JSONObject(strJson);
			JSONObject paramsObject  = new JSONObject(jsonObject.getString("params"));
			cardata.setAir_speed(paramsObject.getString("air_speed"));
			cardata.setAir_mode(paramsObject.getString("air_mode"));
			cardata.setAir_switch(paramsObject.getString("switch"));
			JSONObject  gpsdataObject = new JSONObject(jsonObject.getString("active_gps_data"));
			cardata.setLat(gpsdataObject.getString("lat"));
			cardata.setLon(gpsdataObject.getString("lon"));
			cardata.setAir(gpsdataObject.getString("air"));
			cardata.setBattery(gpsdataObject.getString("battery"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			L.d("TAG", "JSON 解析错误");
		}
		return cardata;
	}
	
	
	
}
