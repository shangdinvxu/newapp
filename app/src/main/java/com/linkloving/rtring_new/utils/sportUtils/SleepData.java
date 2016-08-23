package com.linkloving.rtring_new.utils.sportUtils;

import java.util.ArrayList;

/**
 * Created by leo.wang on 2016/4/11.
 */
public class SleepData {
    String sleepBegintime;//睡觉的开始时间
    String sleepEndtime;//睡觉的结束时间
    int awake;//睡觉醒来的次数
    int duration;//该段睡眠的时间
    ArrayList<SleepAwake> sleepAwake;//睡觉醒来的一些数据,醒来的时间,和结束时间
    public int getAwake() {
        return awake;
    }

    public void setAwake(int awake) {
        this.awake = awake;
    }

    public ArrayList<SleepAwake> getSleepAwake() {
        return sleepAwake;
    }

    public void setSleepAwake(ArrayList<SleepAwake> sleepAwake) {
        this.sleepAwake = sleepAwake;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSleepBegintime() {
        return sleepBegintime;
    }

    public void setSleepBegintime(String sleepBegintime) {
        this.sleepBegintime = sleepBegintime;
    }

    public String getSleepEndtime() {
        return sleepEndtime;
    }

    public void setSleepEndtime(String sleepEndtime) {
        this.sleepEndtime = sleepEndtime;
    }

    @Override
    public String toString() {
        return "SleepData{" +
                "awake=" + awake +
                ", sleepBegintime='" + sleepBegintime + '\'' +
                ", sleepEndtime='" + sleepEndtime + '\'' +
                ", duration=" + duration +
                ", sleepAwake=" + sleepAwake +
                '}';
    }
}
