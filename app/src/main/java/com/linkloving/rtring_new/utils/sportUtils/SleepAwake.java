package com.linkloving.rtring_new.utils.sportUtils;

/**
 * Created by leo.wang on 2016/4/11.
 */
public class SleepAwake {
    String begintime;//睡觉醒来的开始时间
    String endtime;//睡觉醒来的结束时间
    int duration;//该段醒来的时间

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    @Override
    public String toString() {
        return "SleepAwake{" +
                "begintime='" + begintime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", duration=" + duration +
                '}';
    }
}
