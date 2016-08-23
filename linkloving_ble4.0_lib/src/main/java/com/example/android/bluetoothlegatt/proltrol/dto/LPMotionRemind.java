package com.example.android.bluetoothlegatt.proltrol.dto;

public class LPMotionRemind {
	public static final int ACTION_1 = 0x10;
	public static final int ACTION_2 = 0x11;
	
	private int startTime;           // 检测区间起始时间
	private int endTime;             // 检测区间结束时间
	private int timeWindow;          // 每个检测窗长度
	private int level;               // 在每个检测窗内需要达到的运动次数，达不到就会提醒
	private int action = ACTION_1;   // 运动提醒震动方式
	
	public LPMotionRemind() {
		super();
	}
	public LPMotionRemind(int startTime, int endTime, int timeWindow, int level, int action) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.timeWindow = timeWindow;
		this.level = level;
		this.action = action;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	public int getTimeWindow() {
		return timeWindow;
	}
	public void setTimeWindow(int timeWindow) {
		this.timeWindow = timeWindow;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
}
