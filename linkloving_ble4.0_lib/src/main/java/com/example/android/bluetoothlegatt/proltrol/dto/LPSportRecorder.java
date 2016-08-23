package com.example.android.bluetoothlegatt.proltrol.dto;

public class LPSportRecorder
{
	/** 时间长度 */
	public int duration;
	
	/** 步数*/
	public int steps;
	
	/** 幅度 */
	public int distance;
	
	/** 时间长度 */
	public int runDuration;
	
	/** 步数*/
	public int runSteps;
	
	/** 幅度 */
	public int runDistance;
	
	/**条数*/
	public int numbers;
	
	/**设备初始时间*/
	public int inittime;
	
	
	public int getInittime() {
		return inittime;
	}
	public void setInittime(int inittime) {
		this.inittime = inittime;
	}
	public int getNumbers() {
		return numbers;
	}
	public void setNumbers(int numbers) {
		this.numbers = numbers;
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
	public int getRunDuration() {
		return runDuration;
	}
	public void setRunDuration(int runDuration) {
		this.runDuration = runDuration;
	}
	public int getRunSteps() {
		return runSteps;
	}
	public void setRunSteps(int runSteps) {
		this.runSteps = runSteps;
	}
	public int getRunDistance() {
		return runDistance;
	}
	public void setRunDistance(int runDistance) {
		this.runDistance = runDistance;
	}
	
	@Override
	public String toString()
	{
		return "LPSportRecorder[duration=" + duration + ",steps=" + steps
				+ "distance=" + distance + ",runDuration=" + runDuration
				+ ",runSteps" + runSteps + ",runDistance=" + runDistance+"]";
	}
}
