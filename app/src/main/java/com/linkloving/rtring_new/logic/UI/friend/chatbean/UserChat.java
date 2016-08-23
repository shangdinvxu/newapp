package com.linkloving.rtring_new.logic.UI.friend.chatbean;

public class UserChat
{
	private Integer user_id, to_user_id, chat_status;

	private String chat_id, chat_content, chat_time;

	public String getChat_id()
	{
		return chat_id;
	}

	public void setChat_id(String chat_id)
	{
		this.chat_id = chat_id;
	}

	public Integer getUser_id()
	{
		return user_id;
	}

	public void setUser_id(Integer user_id)
	{
		this.user_id = user_id;
	}

	public Integer getTo_user_id()
	{
		return to_user_id;
	}

	public void setTo_user_id(Integer to_user_id)
	{
		this.to_user_id = to_user_id;
	}

	public Integer getChat_status()
	{
		return chat_status;
	}

	public void setChat_status(Integer chat_status)
	{
		this.chat_status = chat_status;
	}

	public String getChat_content()
	{
		return chat_content;
	}

	public void setChat_content(String chat_content)
	{
		this.chat_content = chat_content;
	}

	public String getChat_time()
	{
		return chat_time;
	}

	public void setChat_time(String chat_time)
	{
		this.chat_time = chat_time;
	}

}
