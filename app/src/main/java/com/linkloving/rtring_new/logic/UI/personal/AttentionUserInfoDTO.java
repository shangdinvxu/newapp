package com.linkloving.rtring_new.logic.UI.personal;

public class AttentionUserInfoDTO {
	private String nickname,longitude,latitude,what_s_up,user_avatar_file_name;
	private Integer user_sex;
	private boolean isAttention;
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getWhat_s_up() {
		return what_s_up;
	}
	public void setWhat_s_up(String what_s_up) {
		this.what_s_up = what_s_up;
	}
	public Integer getUser_sex() {
		return user_sex;
	}
	public void setUser_sex(Integer user_sex) {
		this.user_sex = user_sex;
	}
	public boolean getIsAttention() {
		return isAttention;
	}
	public void setIsAttention(Boolean isAttention) {
		this.isAttention = isAttention;
	}
	public String getUser_avatar_file_name() {
		return user_avatar_file_name;
	}
	public void setUser_avatar_file_name(String user_avatar_file_name) {
		this.user_avatar_file_name = user_avatar_file_name;
	}
	public void setAttention(boolean isAttention) {
		this.isAttention = isAttention;
	}



}
