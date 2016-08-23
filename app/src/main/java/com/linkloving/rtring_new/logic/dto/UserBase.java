/**
 * UserEntity.java
 * @author Jason Lu
 * @date 2016-3-29
 * @version 1.0
 */
package com.linkloving.rtring_new.logic.dto;

import java.io.Serializable;
import java.util.Observer;

/**
 * @author Lz
 *
 */
public class UserBase implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -8760727274257939834L;
	protected transient Observer propertyChangedObserver = null;

	private int user_id, user_type, user_status, user_sex, user_height, user_weight, play_calory, channel;

	private String user_mail, user_mobile, nickname, user_psw, user_avatar_file_name, birthdate, longitude, latitude, register_ip, register_time, latest_login_time,
			latest_login_ip, thirdparty_access_token, thirdparty_expire_time, use_os_type, register_os_type, what_s_up;
//	private String user_mail="", user_mobile="", nickname="", user_psw="", user_avatar_file_name="", birthdate="", longitude="", latitude="", register_ip="", register_time="", latest_login_time="",
//			latest_login_ip="", access_token="", expire_time="", use_os_type="", register_os_type="", what_s_up="";



	public UserBase(String birthdate)
	{
		this.birthdate= birthdate;
	}
	public void setPropertyChangedObserver(Observer propertyChangedObserver)
	{
		this.propertyChangedObserver = propertyChangedObserver;
	}

	public int getUser_id()
	{
		return user_id;
	}

	public void setUser_id(int user_id)
	{
		this.user_id = user_id;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_id");
	}

	public int getUser_type()
	{
		return user_type;
	}

	public void setUser_type(int user_type)
	{
		this.user_type = user_type;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_type");
	}

	public int getUser_status()
	{
		return user_status;
	}

	public void setUser_status(int user_status)
	{
		this.user_status = user_status;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_status");
	}

	public int getUser_sex()
	{
		return user_sex;
	}

	public void setUser_sex(int user_sex)
	{
		this.user_sex = user_sex;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_sex");
	}

	public int getUser_height()
	{
		return user_height;
	}

	public void setUser_height(int user_height)
	{
		this.user_height = user_height;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_height");
	}

	public int getUser_weight()
	{
		return user_weight;
	}

	public void setUser_weight(int user_weight)
	{
		this.user_weight = user_weight;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_weight");
	}

	public int getPlay_calory()
	{
		return play_calory;
	}

	public void setPlay_calory(int play_calory)
	{
		this.play_calory = play_calory;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "play_calory");
	}

	public int getChannel()
	{
		return channel;
	}

	public void setChannel(int channel)
	{
		this.channel = channel;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "channel");
	}



	public String getUse_os_type() {
		return use_os_type;
	}

	public void setUse_os_type(String use_os_type) {
		this.use_os_type = use_os_type;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "use_os_type");
	}

	public String getRegister_os_type() {
		return register_os_type;
	}

	public void setRegister_os_type(String register_os_type) {
		this.register_os_type = register_os_type;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "register_os_type");
	}

	public String getUser_mail()
	{
		return user_mail;
	}

	public void setUser_mail(String user_mail)
	{
		this.user_mail = user_mail;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_mail");
	}

	public String getUser_mobile()
	{
		return user_mobile;
	}

	public void setUser_mobile(String user_mobile)
	{
		this.user_mobile = user_mobile;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_mobile");
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "nickname");
	}

	public String getUser_psw()
	{
		return user_psw;
	}

	public void setUser_psw(String user_psw)
	{
		this.user_psw = user_psw;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_psw");
	}

	public String getUser_avatar_file_name()
	{
		return user_avatar_file_name;
	}

	public void setUser_avatar_file_name(String user_avatar_file_name)
	{
		this.user_avatar_file_name = user_avatar_file_name;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "user_avatar_file_name");
	}

	public String getBirthdate()
	{
		return birthdate;
	}

	public void setBirthdate(String birthdate)
	{
		this.birthdate = birthdate;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "birthdate");
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "longitude");
	}

	public String getLatitude()
	{
		return latitude;
	}

	public void setLatitude(String latitude)
	{
		this.latitude = latitude;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "latitude");
	}

	public String getRegister_ip()
	{
		return register_ip;
	}

	public void setRegister_ip(String register_ip)
	{
		this.register_ip = register_ip;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "register_ip");
	}

	public String getRegister_time()
	{
		return register_time;
	}

	public void setRegister_time(String register_time)
	{
		this.register_time = register_time;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "register_time");
	}

	public String getLatest_login_time()
	{
		return latest_login_time;
	}

	public void setLatest_login_time(String latest_login_time)
	{
		this.latest_login_time = latest_login_time;
	}

	public String getLatest_login_ip()
	{
		return latest_login_ip;
	}

	public void setLatest_login_ip(String latest_login_ip)
	{
		this.latest_login_ip = latest_login_ip;
	}

	public String getThirdparty_access_token() {
		return thirdparty_access_token;
	}

	public void setThirdparty_access_token(String thirdparty_access_token) {
		this.thirdparty_access_token = thirdparty_access_token;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "thirdparty_access_token");
	}

	public String getThirdparty_expire_time() {
		return thirdparty_expire_time;
	}

	public void setThirdparty_expire_time(String thirdparty_expire_time) {
		this.thirdparty_expire_time = thirdparty_expire_time;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "thirdparty_expire_time");
	}

	public String getWhat_s_up() {
		return what_s_up;
	}

	public void setWhat_s_up(String what_s_up) {
		this.what_s_up = what_s_up;
		if (this.propertyChangedObserver != null)
			this.propertyChangedObserver.update(null, "what_s_up");
	}

	@Override
	public String toString() {
		return "UserBase{" +
				"propertyChangedObserver=" + propertyChangedObserver +
				", user_id=" + user_id +
				", user_type=" + user_type +
				", user_status=" + user_status +
				", user_sex=" + user_sex +
				", user_height=" + user_height +
				", user_weight=" + user_weight +
				", play_calory=" + play_calory +
				", channel=" + channel +
				", user_mail='" + user_mail + '\'' +
				", user_mobile='" + user_mobile + '\'' +
				", nickname='" + nickname + '\'' +
				", user_psw='" + user_psw + '\'' +
				", user_avatar_file_name='" + user_avatar_file_name + '\'' +
				", birthdate='" + birthdate + '\'' +
				", longitude='" + longitude + '\'' +
				", latitude='" + latitude + '\'' +
				", register_ip='" + register_ip + '\'' +
				", register_time='" + register_time + '\'' +
				", latest_login_time='" + latest_login_time + '\'' +
				", latest_login_ip='" + latest_login_ip + '\'' +
				", thirdparty_access_token='" + thirdparty_access_token + '\'' +
				", thirdparty_expire_time='" + thirdparty_expire_time + '\'' +
				", use_os_type='" + use_os_type + '\'' +
				", register_os_type='" + register_os_type + '\'' +
				", what_s_up='" + what_s_up + '\'' +
				'}';
	}
}
