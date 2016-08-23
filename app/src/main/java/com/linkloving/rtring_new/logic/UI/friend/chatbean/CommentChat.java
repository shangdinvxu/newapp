package com.linkloving.rtring_new.logic.UI.friend.chatbean;

/**
 * Created by DC on 2016/4/19.
 */
public class CommentChat {
    private int user_id;
    private int to_user_id;
    private String comments;
    private String comment_create_time;
    private String tag;
public CommentChat(){

}
    public CommentChat(int user_id, int to_user_id, String comments, String comment_create_time, String tag) {
        this.user_id = user_id;
        this.to_user_id = to_user_id;
        this.comments = comments;
        this.comment_create_time = comment_create_time;
        this.tag = tag;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getComment_create_time() {
        return comment_create_time;
    }

    public void setComment_create_time(String comment_create_time) {
        this.comment_create_time = comment_create_time;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(int to_user_id) {
        this.to_user_id = to_user_id;
    }

    @Override
    public String toString() {
        return "CommentChat{" +
                "user_id=" + user_id +
                ", to_user_id=" + to_user_id +
                ", comments='" + comments + '\'' +
                ", comment_create_time='" + comment_create_time + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
