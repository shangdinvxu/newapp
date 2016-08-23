package com.linkloving.rtring_new.logic.UI.launch.register;

import java.io.Serializable;

/**
 * Created by Linkloving on 2016/3/23.
 */
public class UserRegisterDTO implements Serializable {
    private static final long serialVersionUID = -2205869556732684810L;
    public static final int UPDATE_PSW_ERROR_OLD_NOT_EQ_LOGIN_PSW = -1;
    private String user_id;
    private String user_mail;
    private String nickname;
    private String user_psw;
    private String user_height;
    private String user_weight;
    private String user_sex;
    private String birthdate;
    private String user_type;
    private String os_name;
    private String channel;
    private String thirdparty_access_token;
    private String thirdparty_expire_time;

    public String getUser_id()
    {
        return this.user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getUser_mail()
    {
        return this.user_mail;
    }

    public void setUser_mail(String user_mail)
    {
        this.user_mail = user_mail;
    }

    public String getNickname()
    {
        return this.nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getUser_psw()
    {
        return this.user_psw;
    }

    public void setUser_psw(String user_psw)
    {
        this.user_psw = user_psw;
    }

    public String getUser_height()
    {
        return this.user_height;
    }

    public void setUser_height(String user_height)
    {
        this.user_height = user_height;
    }

    public String getUser_weight()
    {
        return this.user_weight;
    }

    public void setUser_weight(String user_weight)
    {
        this.user_weight = user_weight;
    }

    public String getUser_sex()
    {
        return this.user_sex;
    }

    public void setUser_sex(String user_sex)
    {
        this.user_sex = user_sex;
    }

    public String getBirthdate()
    {
        return this.birthdate;
    }

    public void setBirthdate(String birthdate)
    {
        this.birthdate = birthdate;
    }

    public String getUser_type()
    {
        return this.user_type;
    }

    public void setUser_type(String user_type)
    {
        this.user_type = user_type;
    }

    public String getOs_name()
    {
        return this.os_name;
    }

    public void setOs_name(String os_name)
    {
        this.os_name = os_name;
    }

    public String getThirdparty_access_token()
    {
        return this.thirdparty_access_token;
    }

    public void setThirdparty_access_token(String thirdparty_access_token)
    {
        this.thirdparty_access_token = thirdparty_access_token;
    }

    public String getThirdparty_expire_time()
    {
        return this.thirdparty_expire_time;
    }

    public void setThirdparty_expire_time(String thirdparty_expire_time)
    {
        this.thirdparty_expire_time = thirdparty_expire_time;
    }

    public String getChannel()
    {
        return this.channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }
}
