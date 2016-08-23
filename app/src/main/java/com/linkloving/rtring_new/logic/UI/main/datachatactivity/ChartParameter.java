package com.linkloving.rtring_new.logic.UI.main.datachatactivity;

/**
 * 图表参数
 * @author Administrator
 *
 */
public class ChartParameter {
	private float xScale;
	private float yScale;
	private int width;//屏幕的宽度
	private int height;//屏幕宽度的一半
	private int chartHeight;
	
	public ChartParameter(float xScale,	float yScale, int width, int height){
		this.xScale = xScale;
		this.yScale = yScale;
		this.width = width;
		this.height = height;
		chartHeight = height;
	}
	
	public float getXScale(){
		return xScale;
	}
	
	public void setXScale(float xScale){
		this.xScale = xScale;
	}
	
	public float getYScale(){
		return yScale;
	}
	
	public void setYScale(float yScale){
		this.yScale = yScale;
	}
	
	public int getWidth(){
		return width;
	}
	
	public void setWidth(int width){
		this.width = width;
	}
	
	public int getHeight(){
		return height;
	}
	public void setHeight(int height){
		this.height = height;
	}
	public int getChartHeight(){
		return this.chartHeight;
	}
}
