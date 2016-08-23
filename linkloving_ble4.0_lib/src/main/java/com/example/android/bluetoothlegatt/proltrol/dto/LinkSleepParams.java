package com.example.android.bluetoothlegatt.proltrol.dto;

public class LinkSleepParams {
	public int sleepLevel;
	public int sleepCount;
	public LinkSleepParams(){}
	public int getSleepCount() {
		return sleepCount;
	}
	public void setSleepCount(int sleepCount) {
		this.sleepCount = sleepCount;
	}
	public int getSleepLevel() {
		return sleepLevel;
	}
	public void setSleepLevel(int sleepLevel) {
		this.sleepLevel = sleepLevel;
	}
}
