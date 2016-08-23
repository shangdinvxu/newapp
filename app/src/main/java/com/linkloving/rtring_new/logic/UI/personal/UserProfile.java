package com.linkloving.rtring_new.logic.UI.personal;

/**
 * Created by leo.wang on 2016/3/30.
 */
public class UserProfile {

    private String user_id;
    private String nickname;
    private String user_sex;
    private String userAvatar;
    private String what_s_up;
    private String max_distance;
    private String distance;
    private String data_date;
    private String attention;
    private String longitude;
    private String latitude;
    private boolean virtual;

    public boolean isVirtual()
    {
        return this.virtual;
    }

    public void setVirtual(boolean virtual)
    {
        this.virtual = virtual;
    }

    public String getUser_id()
    {
        return this.user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getNickname()
    {
        return this.nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getDistance()
    {
        return this.distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getUserAvatar()
    {
        return this.userAvatar;
    }

    public void setUserAvatar(String userAvatar)
    {
        this.userAvatar = userAvatar;
    }

    public String getWhat_s_up()
    {
        return this.what_s_up;
    }

    public void setWhat_s_up(String what_s_up)
    {
        this.what_s_up = what_s_up;
    }

    public String getMax_distance()
    {
        return this.max_distance;
    }

    public void setMax_distance(String max_distance)
    {
        this.max_distance = max_distance;
    }

    public String getData_date()
    {
        return this.data_date;
    }

    public void setData_date(String data_date)
    {
        this.data_date = data_date;
    }

    public String getAttention()
    {
        return this.attention;
    }

    public void setAttention(String attention)
    {
        this.attention = attention;
    }

    public String getLongitude()
    {
        return this.longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude()
    {
        return this.latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getUser_sex()
    {
        return this.user_sex;
    }

    public void setUser_sex(String user_sex)
    {
        this.user_sex = user_sex;
    }
}
