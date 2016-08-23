/**
 * UserChat.java
 * @author DC
 * @date 2016-4-11
 */
package com.linkloving.rtring_new.logic.UI.friend.chatbean;

public class UserChatDTO
{
	private Integer unread, user_id, to_user_id;
	private String nickname, user_avatar_file_name, last_time, last_content;

	public Integer getUnread()
	{
		return unread;
	}

	public void setUnread(Integer unread)
	{
		this.unread = unread;
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

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public String getUser_avatar_file_name()
	{
		return user_avatar_file_name;
	}

	public void setUser_avatar_file_name(String user_avatar_file_name)
	{
		this.user_avatar_file_name = user_avatar_file_name;
	}

	public String getLast_time()
	{
		return last_time;
	}

	public void setLast_time(String last_time)
	{
		this.last_time = last_time;
	}

	public String getLast_content()
	{
		return last_content;
	}

	public void setLast_content(String last_content)
	{
		this.last_content = last_content;
	}

	@Override
	public String toString() {
		return "UserChatDTO{" +
				"unread=" + unread +
				", user_id=" + user_id +
				", to_user_id=" + to_user_id +
				", nickname='" + nickname + '\'' +
				", user_avatar_file_name='" + user_avatar_file_name + '\'' +
				", last_time='" + last_time + '\'' +
				", last_content='" + last_content + '\'' +
				'}';
	}
}
