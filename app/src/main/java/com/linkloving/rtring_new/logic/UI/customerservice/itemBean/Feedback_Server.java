package com.linkloving.rtring_new.logic.UI.customerservice.itemBean;
	/*
	 * 解析服务器自己定义的json
	 */

public class Feedback_Server {
//"id":"114","user_id":"35991",
//	"create_time":"2016-01-12 13:16:21",
//	"feedback_content":"uuuuu",
//	"feedback_result":"测试回答",
//	"feedback_state":"2"
	public static final String STATE_OK = "2";
	public static final String STATE_NO_RES = "0";
	
	private String id;
	private String user_id;
	private String create_time;
	private String feedback_content;
	private String feedback_result;
	private String feedback_state;
	
	public Feedback_Server(String id, String user_id, String create_time,String feedback_content, String feedback_result,String feedback_state) {
		this.id = id;
		this.user_id = user_id;
		this.create_time = create_time;
		this.feedback_content = feedback_content;
		this.feedback_result = feedback_result;
		this.feedback_state = feedback_state;
	}
	
	
	public Feedback_Server() {
		
	}



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getFeedback_content() {
		return feedback_content;
	}
	public void setFeedback_content(String feedback_content) {
		this.feedback_content = feedback_content;
	}
	public String getFeedback_result() {
		return feedback_result;
	}
	public void setFeedback_result(String feedback_result) {
		this.feedback_result = feedback_result;
	}
	public String getFeedback_state() {
		return feedback_state;
	}
	public void setFeedback_state(String feedback_state) {
		this.feedback_state = feedback_state;
	}
	
	
}
