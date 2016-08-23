package com.linkloving.rtring_new.logic.UI.friend.fragment.commentBean;

/**
 * Created by Linkloving on 2016/3/31.
 */
public class CommentMsgEntity {
    //名字
    private String name;
    //日期
    private String date;
    //聊天内容
    private String text;
    private String user_id;
    //是否为自己提问
    private boolean isQuestion = true;
    public CommentMsgEntity(String name, String date, String text, boolean isQuestion,String user_id) {
        this.name = name;
        this.date = date;
        this.text = text;
        this.isQuestion = isQuestion;
        this.user_id=user_id;
        toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getMsgType() {
        return isQuestion;
    }

    public void setMsgType(boolean isQuestion) {
        this.isQuestion = isQuestion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    @Override
    public String toString() {

        // TODO Auto-generated method stub
        return "反馈内容:"+text;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
