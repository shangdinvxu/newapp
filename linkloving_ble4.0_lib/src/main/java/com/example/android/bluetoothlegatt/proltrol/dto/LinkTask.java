package com.example.android.bluetoothlegatt.proltrol.dto;

/**
 *   1step == 0.4米 == 0.038卡
 */
public class LinkTask {
	int step;           // 设置的任务总量
	int stepDayTotals;  // 已经完成的任务量
	
	public  LinkTask() {}
	
	public LinkTask(int step,int stepDayTotals) {
		this.step = step;
		this.stepDayTotals = stepDayTotals;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public int getStepDayTotals() {
		return stepDayTotals;
	}
	public void setStepDayTotals(int stepDayTotals) {
		this.stepDayTotals = stepDayTotals;
	}
}
