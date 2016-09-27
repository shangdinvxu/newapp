package com.linkloving.rtring_new.prefrences.devicebean;

/**
 * Created by zkx on 2016/3/8.
 * //还需要初始化
 */
public class DeviceSetting {
    /**用户id*/
    private String user_id = "";
    /**闹钟1  06:30-0-0 时间 频率 开关*/
    private String alarm_one = "06:30-0-0";
    /**闹钟2*/
    private String alarm_two = "07:30-0-0";
    /**闹钟3*/
    private String alarm_three = "08:30-0-0";
    /**久坐提醒的时间*/
    private String longsit_time = "09:00-11:30-14:00-20:00";
    /**久坐提醒的步数*/
    private String longsit_step = "60";
    /**久坐提醒的开关*/
    private String longsit_vaild = "1";
    /**勿扰的时间*/
    private String disturb_time ="22:00-07:00";
    /**勿扰的开关*/
    private String disturb_vaild = "1";
    /**消息提醒*/
    private int ANCS_value=31;
    /**省电管理*/
    private int electricity_value=0;
    /**用户邮箱*/
    private String user_mail;
    /**目标*/
    private String goal;

    /**目标更新*/
    private long goal_update;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAlarm_one() {
        return alarm_one;
    }

    public void setAlarm_one(String alarm_one) {
        this.alarm_one = alarm_one;
    }

    public String getAlarm_two() {
        return alarm_two;
    }

    public void setAlarm_two(String alarm_two) {
        this.alarm_two = alarm_two;
    }

    public String getAlarm_three() {
        return alarm_three;
    }

    public void setAlarm_three(String alarm_three) {
        this.alarm_three = alarm_three;
    }

    public String getLongsit_time() {
        return longsit_time;
    }

    public void setLongsit_time(String longsit_time) {
        this.longsit_time = longsit_time;
    }

    public String getLongsit_step() {
        return longsit_step;
    }

    public void setLongsit_step(String longsit_step) {
        this.longsit_step = longsit_step;
    }

    public String getLongsit_vaild() {
        return longsit_vaild;
    }

    public void setLongsit_vaild(String longsit_vaild) {
        this.longsit_vaild = longsit_vaild;
    }

    public String getDisturb_vaild() {
        return disturb_vaild;
    }

    public void setDisturb_vaild(String disturb_vaild) {
        this.disturb_vaild = disturb_vaild;
    }

    public int getANCS_value() {
        return ANCS_value;
    }

    public void setANCS_value(int ANCS_value) {
        this.ANCS_value = ANCS_value;
    }

    public String getDisturb_time() {
        return disturb_time;
    }

    public void setDisturb_time(String disturb_time) {
        this.disturb_time = disturb_time;
    }

    public int getElectricity_value() {
        return electricity_value;
    }

    public void setElectricity_value(int electricity_value) {
        this.electricity_value = electricity_value;
    }

    public String getUser_mail() {
        return user_mail;
    }

    public void setUser_mail(String user_mail) {
        this.user_mail = user_mail;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public long getGoal_update() {
        return goal_update;
    }

    public void setGoal_update(long goal_update) {
        this.goal_update = goal_update;
    }
}
