package model;

/**
 * 车辆信息(车牌，车标)
 */
public class CarData{

	/* 昵称 */
	private String nick_name;
	/* 车系列 */
	private String car_serial;
	/* 车品牌id */
	private String car_brand_id;
	/* 设备id */
	private String device_id;
	/* 车辆id */
	private String obj_id;
	
	
	/* 净化器速度   */
	private String air_speed;
	/* 净化器模式   */
	private String air_mode;
	/* 净化器开关   */
	private String air_switch;
	/* 净化器设备的纬度   */
	private String lat;
	/* 净化器设备的经度   */
	private String lon;
	/* 车辆内的空气质量值   */
	private String air;
	/* 电压   小于12时候不能正常工作   */
	private String battery;
	
	
	
	public String getAir_speed() {
		return air_speed;
	}
	public void setAir_speed(String air_speed) {
		this.air_speed = air_speed;
	}
	public String getAir_mode() {
		return air_mode;
	}
	public void setAir_mode(String air_mode) {
		this.air_mode = air_mode;
	}
	public String getAir_switch() {
		return air_switch;
	}
	public void setAir_switch(String air_switch) {
		this.air_switch = air_switch;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getAir() {
		return air;
	}
	public void setAir(String air) {
		this.air = air;
	}
	public String getBattery() {
		return battery;
	}
	public void setBattery(String battery) {
		this.battery = battery;
	}
	public String getObj_id() {
		return obj_id;
	}
	public void setObj_id(String obj_id) {
		this.obj_id = obj_id;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public String getCar_brand_id() {
		return car_brand_id;
	}
	public void setCar_brand_id(String car_brand_id) {
		this.car_brand_id = car_brand_id;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public String getCar_serial() {
		return car_serial;
	}
	public void setCar_serial(String car_serial) {
		this.car_serial = car_serial;
	}


}