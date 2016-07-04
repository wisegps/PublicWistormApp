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