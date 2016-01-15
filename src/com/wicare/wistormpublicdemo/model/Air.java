package com.wicare.wistormpublicdemo.model;

import java.io.Serializable;

/**
 * @author Wu 
 * 
 * 净化器空气质量参数
 */
public class Air implements Serializable{

	/**
	 * 开启时长
	 */
	public int air_duration;
	/**
	 * 模式
	 */
	public int air_mode;
	/**
	 * 开关
	 */
	public int _switch;
	/**
	 * 空气质量指数
	 */
	public int air;
	/**
	 * 定时时间
	 */
	public String air_time;
	
	
	public int getAir_duration() {
		return air_duration;
	}
	public void setAir_duration(int air_duration) {
		this.air_duration = air_duration;
	}
	
	
	public int getAir_mode() {
		return air_mode;
	}
	public void setAir_mode(int air_mode) {
		this.air_mode = air_mode;
	}
	
	
	public int get_switch() {
		return _switch;
	}
	public void set_switch(int _switch) {
		this._switch = _switch;
	}
	
	
	public int getAir() {
		return air;
	}
	public void setAir(int air) {
		this.air = air;
	}
	
	public String getAir_time() {
		return air_time;
	}
	public void setAir_time(String air_time) {
		this.air_time = air_time;
	}
}
