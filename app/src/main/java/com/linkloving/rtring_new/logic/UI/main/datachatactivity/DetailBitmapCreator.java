package com.linkloving.rtring_new.logic.UI.main.datachatactivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.utils.sportUtils.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 图表图片生成类
 * @author Administrator
 *
 */
public class DetailBitmapCreator {
	private final String TAG = DetailBitmapCreator.class.getSimpleName();
	private ChartParameter chartParameter;
	private int beginTime;

	String time;

	public DetailBitmapCreator(Context context){
	}
	public void initChartParameter(ChartParameter chartParameter){
		this.chartParameter = chartParameter;
	}

	public Bitmap drawDetailChart(List<BRDetailData> detailData,int dayindexNow)
	{
		MyLog.i(TAG,"转换后的数据"+detailData.size());
		beginTime = dayindexNow;
		Bitmap chartBitmap = Bitmap.createBitmap(chartParameter.getWidth(),chartParameter.getHeight(),Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(chartBitmap);
		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.rgb(255, 255, 255));
		canvas.drawLine(0, chartParameter.getChartHeight() - 30, chartParameter.getWidth(), chartParameter.getHeight() - 30, rectPaint);
		//在上面所画出来的横线下面加上坐标轴
		rectPaint.setStyle(Style.FILL);
		if(detailData != null && detailData.size() > 0){
			List<AnylyzeResult> anylyzeResult = new SleepDataAnylyze().anylyzeDetailData(detailData);
            for(AnylyzeResult result : anylyzeResult){
				MyLog.i(TAG,"转换后的数据"+result.toString());
				//只画睡眠
				if(result.isSleep()){
					drawSleepChart(detailData, canvas, result.getBeginIndex(), result.getEndIndex());
				}
			}			
		}
		drawX(rectPaint, canvas);
		return chartBitmap;
	}

	/**
	 * 画X坐标
	 * @param xPaint 画笔
	 * @param canvas 画布
     */
	private void drawX(Paint xPaint,Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		//现在准备将那条直线分为六段,从前一天的下午六点,到今天的晚上的零点
		//还要在直线上画出两个像素的坐标线
		String [] x=new String[]{"6pm","0am","6am","12am","6pm","12am"};
		xPaint.setTextSize(20);
		//画X轴   起始端点的X坐标  起始端点的Y坐标。终止端点的X坐标 终止端点的Y坐标。
		canvas.drawLine(	0, chartParameter.getChartHeight() - 30,
				chartParameter.getWidth(), chartParameter.getHeight() - 30, xPaint);
		//xPointIndex 第N个X坐标点
		int xPointIndex=0;
		//xPoint x坐标点的位置
		for(int xPoint=0;xPoint <= chartParameter.getWidth();){
			/* public void drawText(@NonNull String text, float x, float y, @NonNull Paint paint) {*/
			if(xPointIndex<x.length){
				xPaint.setTextAlign(Paint.Align.CENTER);
				if(xPointIndex==0){
					xPaint.setTextAlign(Paint.Align.LEFT);
				}
				if(xPointIndex==5){
					xPaint.setTextAlign(Paint.Align.RIGHT);
				}
				//画X轴上的坐标[6pm]
				canvas.drawText(x[xPointIndex],xPoint,chartParameter.getHeight()-5,xPaint);
				//画X轴上的坐标突出点 高度5
				xPaint.setStrokeWidth((float) 1.5);
				canvas.drawLine(xPoint,chartParameter.getChartHeight() - 30,xPoint,chartParameter.getHeight() - 35,xPaint);
				xPoint=xPoint + chartParameter.getWidth()/5;
				xPointIndex++;
			}else {
				break;
			}
		}
	}

	//使用原始数据的画法,
	/** @deprecated */
	public Bitmap drawDetailChart1(List<SportRecord> detailData,String time)
	{
		this.time=time;
		Bitmap chartBitmap = Bitmap.createBitmap(chartParameter.getWidth(),chartParameter.getHeight(),Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(chartBitmap);
		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.rgb(255, 255, 255));
		rectPaint.setStyle(Style.FILL);
		canvas.drawLine(0, chartParameter.getChartHeight() - 30, chartParameter.getWidth(), chartParameter.getHeight() - 30, rectPaint);
		if(detailData != null && detailData.size() > 0){
			List<AnylyzeResult> anylyzeResult = new SleepDataAnylyze().anylyzeSportRecord(detailData);
			for(AnylyzeResult result : anylyzeResult){
				MyLog.i(TAG,"转换后的数据"+result.toString());
				if(result.isSleep()){
					drawSleepChart1(detailData, canvas, result.getBeginIndex(), result.getEndIndex());
				}else{

				}
			}
		}
		drawX(rectPaint, canvas);
		return chartBitmap;
	}

	/** @deprecated */
	private void drawSleepChart1(List<SportRecord> detailData,Canvas canvas, int beginIndex, int endIndex){
		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.rgb(0x0f, 0xb4, 0xc9));	//#0FB4C9
		rectPaint.setStyle(Style.FILL);
		for(int i = beginIndex; i<= endIndex; i ++){
			SportRecord data = detailData.get(i);
			Paint paint = new Paint();
			paint.setColor(GetSleepColor());
			paint.setStyle(Style.FILL);
			Path path = new Path();
			//将起笔点移到该位置
			path.moveTo(getBeginX(data), chartParameter.getChartHeight()-30);
			//从上一个点绘制线段到这个点
			path.lineTo(getBeginX(data),GetLigntSleepY());

			path.lineTo(getEndX(data),GetLigntSleepY());

			path.lineTo(getEndX(data),chartParameter.getChartHeight()-30);

			path.moveTo(getBeginX(data),chartParameter.getChartHeight()-30);

			path.close();

			canvas.drawPath(path, paint);
		}
	}

	private void drawSleepChart(List<BRDetailData> detailData,Canvas canvas, int beginIndex, int endIndex){

//		setBeginTime(0);
		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.rgb(0x0f, 0xb4, 0xc9));	//#0FB4C9
		rectPaint.setStyle(Style.FILL);
//		canvas.drawRect(getBeginX(detailData.get(beginIndex)), chartParameter.getChartHeight(), getEndX(detailData.get(endIndex)), chartParameter.getHeight(), rectPaint);
		for(int i = beginIndex; i<= endIndex; i ++){
			BRDetailData data = detailData.get(i);
			Paint paint = new Paint();
			paint.setColor(GetSleepColor());
			paint.setStyle(Style.FILL);
			Path path = new Path();
			//画一个矩形
			path.moveTo(getBeginX(data), chartParameter.getChartHeight()-30);
			if(data.getState()==BRDetailData.STATE_SLEEP_LIGHT){
				path.lineTo(getBeginX(data),GetLigntSleepY());
				path.lineTo(getEndX(data),GetLigntSleepY());
			}else if(data.getState()==BRDetailData.STATE_SLEEP_DEEP){
				path.lineTo(getBeginX(data),GetDeepSleepY());
				path.lineTo(getEndX(data),GetDeepSleepY());
			}
			path.lineTo(getEndX(data),chartParameter.getChartHeight()-30);
			path.moveTo(getBeginX(data),chartParameter.getChartHeight()-30);

	        path.close(); 
	        canvas.drawPath(path, paint);
		}    	
	}
	/** @deprecated */
	private float getBeginX(SportRecord  data)
	{
		//现在是一个屏幕,平均每一秒占几个像素,七点是utc时间的昨天六点

		MyLog.i("getBeginX获取到的高度:" +getS(data.getStart_time())*chartParameter.getXScale());

		return getS(data.getStart_time())*chartParameter.getXScale();
	}
	/** @deprecated */
	private float getEndX(SportRecord  data){
		MyLog.i("getEndX获取到的高度:" +(getS(data.getStart_time())+Integer.parseInt(data.getDuration()))*chartParameter.getXScale());
		return (getS(data.getStart_time())+Integer.parseInt(data.getDuration()))*chartParameter.getXScale();
	}

	//计算当前数据的时间距离图表时间是多少秒
	/** @deprecated */
	private int getS(String s)  {
	String timenow=TimeUtils.getsleepStartTimeUTC(time);//查询的起始时间,也就是昨天下午六点
		//计算该时间和传进来的时间相差多少秒
		Date	d1= null;
		Date d2=null;
		try {
			d1 = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).parse(s);
			d2=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).parse(timenow);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return (int) (d1.getTime()/1000-d2.getTime()/1000);
	}



	private float getBeginX(BRDetailData  data)
	{
		if(beginTime==data.getDayIndex()){
			//和现在是同一天的时间,要加时间片加到六个小时的......
							//每个时间片（30s）所占的像素  * 这个数据开始的时间片 + 1/5屏幕
			float width =chartParameter.getXScale()  * (data.getBegin())+chartParameter.getWidth() / 5;
			MyLog.i("getBeginX获取到的高度:" +width);
			return width;
		}else {
			//如果是前一天的,开始的数据要时间片向前减掉18个小时的时间片（2160）
			float width =chartParameter.getXScale() * (data.getBegin()-2160);
			MyLog.i("如果是前一天的getBeginX获取到的高度:" +width);
			return width;
		}
	}
	
	private float getEndX(BRDetailData  data)
	{
		if(beginTime==data.getDayIndex()){
			//原始数据的开始时间 是当天
			float width =  chartParameter.getXScale()  * (data.getBegin() + data.getDuration())+chartParameter.getWidth()/5;
			MyLog.i("getEndX获取到的高度:" +width);
			return width;

		}else {
			//如果是前一天的,要时间片向前减掉18个小时
			float width =  chartParameter.getXScale()* (data.getBegin() + data.getDuration()-2160);
			MyLog.i("getEndX获取到的高度:" +width);
			return width;
		}
	}

	private float getSportY(BRDetailData  data){
		if(data.getDuration()  > 0 &&( data.getState() == BRDetailData.STATE_WALKING || data.getState() == BRDetailData.STATE_RUNNING)){
			float height =  data.getSteps()/data.getDuration()* chartParameter.getYScale();
			return  (chartParameter.getChartHeight() - height);
		}else{
			return chartParameter.getChartHeight();
		}
	}
	
	/*private float GetSleepY(int state){
		if(state == BRDetailData.STATE_SLEEP_ACTIVE){
			return chartParameter.getChartHeight() - 18 * chartParameter.getYScale();
		}else if (state == BRDetailData.STATE_SLEEP_LIGHT){
			return chartParameter.getChartHeight() - 36 * chartParameter.getYScale();
		}else{
			return chartParameter.getChartHeight() - 60 * chartParameter.getYScale();
		}
	}*/

	private float GetLigntSleepY(){
		return chartParameter.getChartHeight()-70*chartParameter.getYScale();
	}

	private float GetDeepSleepY(){
		return chartParameter.getChartHeight()-60*chartParameter.getYScale();
	}
	
	private int GetSportColor(BRDetailData  data){	
		int average = 0;
		if(data.getDuration() > 0)
		{
			average = data.getSteps()/data.getDuration();
		}
		if(average > 40){
			return Color.rgb(0xF3, 0x2D, 0x0C); //#F32D0C
		}else if(average > 20){
			return Color.rgb(0xF5, 0xB2, 0x27); //#F5B227
		}else{
			return Color.rgb(0xF7, 0xCA, 0x3F); //#F7CA3F
		}
	}
	
/*	private int GetSleepColor(int state){
		if(state == BRDetailData.STATE_SLEEP_ACTIVE){
			return Color.rgb(0xFF, 0xB6, 0x30); //#FFB630
		}else if (state == BRDetailData.STATE_SLEEP_LIGHT){
			return Color.rgb(0x30, 0xC3, 0xF9); //#30C3F9
		}else{
			return Color.rgb(0x08, 0x7B, 0xC4); //#087BC4
		}
	}*/

	private int GetSleepColor(){
		return Color.rgb(0x30, 0xC3, 0xF9); //#30C3F9
	}

	public void setBeginTime(int beginTime) {
		this.beginTime = beginTime;
	}
}
