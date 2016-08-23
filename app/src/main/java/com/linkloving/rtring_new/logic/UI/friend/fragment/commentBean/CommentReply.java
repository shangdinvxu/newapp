package com.linkloving.rtring_new.logic.UI.friend.fragment.commentBean;

/**
 * Created by Linkloving on 2016/3/31.
 */
public class CommentReply {
    public static final String STATE_NO_RES = "0";
    private String comment_id;
    private String user_id;
    private String user_time;
    private String comment_user_id;
    private String comment_user_avatar;
    private String comment_nickname;
    private String comment_time;
    private String comment_content;
    private String reply_user_id;
    private String reply_nickname;

    public String getComment_id()
    {
        return this.comment_id;
    }

    public void setComment_id(String comment_id)
    {
        this.comment_id = comment_id;
    }

    public String getUser_id()
    {
        return this.user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getUser_time()
    {
        return this.user_time;
    }

    public void setUser_time(String user_time)
    {
        this.user_time = user_time;
    }

    public String getComment_user_id()
    {
        return this.comment_user_id;
    }

    public void setComment_user_id(String comment_user_id)
    {
        this.comment_user_id = comment_user_id;
    }

    public String getComment_user_avatar()
    {
        return this.comment_user_avatar;
    }

    public void setComment_user_avatar(String comment_user_avatar)
    {
        this.comment_user_avatar = comment_user_avatar;
    }

    public String getComment_nickname()
    {
        return this.comment_nickname;
    }

    public void setComment_nickname(String comment_nickname)
    {
        this.comment_nickname = comment_nickname;
    }

    public String getComment_time()
    {
        return this.comment_time;
    }

    public void setComment_time(String comment_time)
    {
        this.comment_time = comment_time;
    }

    public String getComment_content()
    {
        return this.comment_content;
    }

    public void setComment_content(String comment_content)
    {
        this.comment_content = comment_content;
    }

    public String getReply_user_id()
    {
        return this.reply_user_id;
    }

    public void setReply_user_id(String reply_user_id)
    {
        this.reply_user_id = reply_user_id;
    }

    public String getReply_nickname()
    {
        return this.reply_nickname;
    }

    public void setReply_nickname(String reply_nickname)
    {
        this.reply_nickname = reply_nickname;
    }
}
