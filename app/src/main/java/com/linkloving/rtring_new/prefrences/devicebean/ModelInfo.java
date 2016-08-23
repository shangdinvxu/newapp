package com.linkloving.rtring_new.prefrences.devicebean;

/**
 * 设备管理信息
 * Created by zkx on 2016/5/31.
 */
public class ModelInfo {
    /**
     *  金融协议 待定
     */
    private int protocol;
    /**
     *  电池类型 0:外接电源型  1:纽扣电池     2:充电电池
     */
    private int battery;
    /**
     *  是否有马达 0:无  1:有
     */
    private int motor;
    /**
     *  显示类型 0:无  1:LED 2:OLED
     */
    private int dispaly;
    /**
     *  是否有抬手显屏 0:无  1:有
     */
    private int hand;
    /**
     *  金融卡 0:无  1:可查看记录但不可充值  2:可查看记录可充值
     */
    private int fiscard;
    /**
     *  消息提醒 0:无  1:提醒  2:提醒并推送消息至设备
     */
    private int ancs;
    /**
     *  是否有勿扰模式 0:无  1:有
     */
    private int no_disturb;
    /**
     *  是否有节能模式 0:无  1:有
     */
    private int power_waste;
    /**
     *  是否含有睡眠算法 0:无  1:有
     */
    private int sleep;
    /**
     *  是否支持坐的计算 0:无  1:有
     */
    private int sit;
    /**
     *  心率 0:无  1:静态心率  2:动态心率
     */
    private int heart_rate;

    public int getAncs() {
        return ancs;
    }

    public void setAncs(int ancs) {
        this.ancs = ancs;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getDispaly() {
        return dispaly;
    }

    public void setDispaly(int dispaly) {
        this.dispaly = dispaly;
    }

    public int getFiscard() {
        return fiscard;
    }

    public void setFiscard(int fiscard) {
        this.fiscard = fiscard;
    }

    public int getHand() {
        return hand;
    }

    public void setHand(int hand) {
        this.hand = hand;
    }

    public int getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }

    public int getMotor() {
        return motor;
    }

    public void setMotor(int motor) {
        this.motor = motor;
    }

    public int getNo_disturb() {
        return no_disturb;
    }

    public void setNo_disturb(int no_disturb) {
        this.no_disturb = no_disturb;
    }

    public int getPower_waste() {
        return power_waste;
    }

    public void setPower_waste(int power_waste) {
        this.power_waste = power_waste;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getSit() {
        return sit;
    }

    public void setSit(int sit) {
        this.sit = sit;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    @Override
    public String toString() {
        return "ModelInfo{" +
                "ancs=" + ancs +
                ", protocol=" + protocol +
                ", battery=" + battery +
                ", motor=" + motor +
                ", dispaly=" + dispaly +
                ", hand=" + hand +
                ", fiscard=" + fiscard +
                ", no_disturb=" + no_disturb +
                ", power_waste=" + power_waste +
                ", sleep=" + sleep +
                ", sit=" + sit +
                ", heart_rate=" + heart_rate +
                '}';
    }
}
