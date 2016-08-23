package com.linkloving.rtring_new.logic.dto;



public class UserDeviceRecord {
	private Integer record_id;

	private Integer user_id;

	private Integer device_id;

	private String start_time;
	
	private Integer state;

	private Long step;

	private Long duration;

	private Long distance;

	private Integer utc_time;

	public Integer getRecord_id() {
		return record_id;
	}

	public void setRecord_id(Integer record_id) {
		this.record_id = record_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getDevice_id() {
		return device_id;
	}

	public void setDevice_id(Integer device_id) {
		this.device_id = device_id;
	}

	public String getStart_time() {

		return start_time;
	}

	public void setStart_time(String start_time) {

		this.start_time = start_time;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getStep() {
		return step;
	}

	public void setStep(Long step) {
		this.step = step;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Long getDistance() {
		return distance;
	}

	public void setDistance(Long distance) {
		this.distance = distance;
	}

	public Integer getUtc_time() {
		return utc_time;
	}

	public void setUtc_time(Integer utc_time) {
		this.utc_time = utc_time;
	}
	

}