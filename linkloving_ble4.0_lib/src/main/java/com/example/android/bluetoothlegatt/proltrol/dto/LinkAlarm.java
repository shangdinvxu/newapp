package com.example.android.bluetoothlegatt.proltrol.dto;

public class LinkAlarm {
	/** 马达震动方式 */
	public final static int ACTION_1 = 0x10;
	public final static int ACTION_2 = 0x11;

	/** 周几的mask */
	public final static int WEEK_0 = 0x01;  // 周日，bit0置1
	public final static int WEEK_1 = 0x02;  // 周一，bit1置1
	public final static int WEEK_2 = 0x04;
	public final static int WEEK_3 = 0x08;
	public final static int WEEK_4 = 0x10;
	public final static int WEEK_5 = 0x20;
	public final static int WEEK_6 = 0x40;  // 周六，bit6置1
	
	/**
	 *  设置的话，闹铃震一次后就会自动删除
	 *  不设置，闹铃按照设置的时间震动，下一周，遇到相应的时间点还会震动 
	 * */
	public final static int ONCE = 0x80;    // bit7置1
	
	public final static int WORK_DAY = (WEEK_1 | WEEK_2 | WEEK_3 | WEEK_4 | WEEK_5);
	public final static int WEEKEND_DAY = (WEEK_0 | WEEK_6);
	public final static int EVERY_DAY = (WEEK_0 | WEEK_1 | WEEK_2 | WEEK_3 | WEEK_4 | WEEK_5 | WEEK_6);
	
	/**
	 * 目前机芯支持的最多闹铃个数，合法的闹铃索引就是0 ~ DEVICE_ALARM_LENGTH-1
	 */
	public final static int DEVICE_ALARM_LENGTH = 7;

	
	int time;        // 闹铃时间，
	int frequency;   // 周几和ONCE的位或
	int index;       // 闹钟索引
	int action = ACTION_2;  // 闹钟震动方式

	public LinkAlarm() {}
	public LinkAlarm(int time,int frequence) {
		this.time = time;
		this.frequency = frequence;
	}
	
	public LinkAlarm(int index, int time, int frequence) {
		this.index = index;
		this.time = time;
		this.frequency = frequence;
		action = ACTION_2;
	}

	@Override
	public String toString() {
		return "LPAlarm [time=" + time + ", frequency=" + frequency + ", index=" + index + ", action=" + action + "]";
	}
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
	
}
