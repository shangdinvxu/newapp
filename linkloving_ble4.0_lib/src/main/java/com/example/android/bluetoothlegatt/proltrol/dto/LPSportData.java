package com.example.android.bluetoothlegatt.proltrol.dto;

import java.util.ArrayList;
import java.util.List;


/**
 * @author guanxin@hoolai.com
 * 手环传回的原始数据
 */
public class LPSportData {
	
	public static final int STATE_IDLE = 0x00;
	public static final int STATE_WALKING = 0x01;
	public static final int STATE_RUNNING = 0x02;

//	/** 离上次同步时间的相对日期数 */
//	public int dayIndex;
//	/** 是否进入睡眠情况 */
//	public boolean isSleep;
	/**   idle 0 , walk 1 running 2;*/
	public int state;
//	/** 开始*/
//	public int begin;
	/** 时间长度 */
	public int duration;
	/** 步数*/
	public int steps;
	/** 幅度 */
	public int distance;
	
	/**
	 * 剩余数据长度
	 */
	public int dataLen;
	
	/**
	 * 接收数据长度
	 */
	public int revLen;
	
	public int timeStemp;
	
	/***
	 * 相对于timeStemp的30s个数
	 */
	public int refTime;
	
	
	/** IDLE状态下最后5分钟，每个30s里产生动作次数，此值代表10个30s中最大的那次 */
	public int stepsPart;
	

	/** IDLE状态下最后5分钟的时间段里，每个30s里产生动作幅度的平均值，此值记录的是10次里最大的那次 */
	public int distancePart;
	
//	/**	原始数据 */
//	public String srcData;
	
	@Override
	public String toString() {
		return  "SportData["+"state=" + state + ", duration=" + duration + ", steps="
				+ steps + ", distance=" + distance +",stepsPart="+stepsPart+",distancePart="+distancePart+ "]";
	}
	
	public LPSportData(int dayIndex, boolean isSleep, int state, int begin,
			int duration, int steps, int distance, int stepsPart,
			int distancePart) {
		super();
		this.state = state;
		this.duration = duration;
		this.steps = steps;
		this.distance = distance;
		this.stepsPart = stepsPart;
		this.distancePart = distancePart;
	}
	
	
	public LPSportData() {
	}
	
//	public int getDayIndex() {
//		return dayIndex;
//	}
//	public void setDayIndex(int dayIndex) {
//		this.dayIndex = dayIndex;
//	}
	
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getSteps() {
		return steps;
	}
	public void setSteps(int steps) {
		this.steps = steps;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getStepsPart() {
		return stepsPart;
	}
	public void setStepsPart(int stepsPart) {
		this.stepsPart = stepsPart;
	}
	public int getDistancePart() {
		return distancePart;
	}
	public void setDistancePart(int distancePart) {
		this.distancePart = distancePart;
	}

	public int getDataLen() {
		return dataLen;
	}

	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}

	public int getTimeStemp() {
		return timeStemp;
	}

	public void setTimeStemp(int timeStemp) {
		this.timeStemp = timeStemp;
	}

	public int getRefTime() {
		return refTime;
	}

	public void setRefTime(int refTime) {
		this.refTime = refTime;
	}

	public int getRevLen() {
		return revLen;
	}

	public void setRevLen(int revLen) {
		this.revLen = revLen;
	}
	
	
}
