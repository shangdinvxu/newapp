package com.linkloving.rtring_new.logic.UI.launch.dto;

import java.io.Serializable;

public class UserRegisterDTO implements Serializable {
		private static final long serialVersionUID = -2215869556732684810L;
		private int user_id;
		private String user_mail;
		private String user_mobile;
		private String user_psw;
		private String user_status;
		private String register_time;
		private String register_os_type;
		private String register_ip;
		private String nickname;
		private Integer register_type;
		private Integer user_type;
		
		public String getUser_mobile() {
			return user_mobile;
		}
		public void setUser_mobile(String user_mobile) {
			this.user_mobile = user_mobile;
		}
		public String getUser_psw() {
			return user_psw;
		}
		public void setUser_psw(String user_psw) {
			this.user_psw = user_psw;
		}
		public String getUser_status() {
			return user_status;
		}
		public void setUser_status(String user_status) {
			this.user_status = user_status;
		}
		public String getRegister_time() {
			return register_time;
		}
		public void setRegister_time(String register_time) {
			this.register_time = register_time;
		}

		public String getRegister_os_type() {
			return register_os_type;
		}
		public void setRegister_os_type(String register_os_type) {
			this.register_os_type = register_os_type;
		}
		public String getRegister_ip() {
			return register_ip;
		}
		public void setRegister_ip(String register_ip) {
			this.register_ip = register_ip;
		}
		public String getNickname() {
			return nickname;
		}
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		public int getUser_id() {
			return user_id;
		}
		public void setUser_id(int user_id) {
			this.user_id = user_id;
		}
		public Integer getUser_type() {
			return user_type;
		}
		public void setUser_type(Integer user_type) {
			this.user_type = user_type;
		}
		public String getUser_mail() {
			return user_mail;
		}
		public void setUser_mail(String user_mail) {
			this.user_mail = user_mail;
		}
		public int getRegister_type() {
			return register_type;
		}
		public void setRegister_type(int register_type) {
			this.register_type = register_type;
		}
		
		
		
		
}
