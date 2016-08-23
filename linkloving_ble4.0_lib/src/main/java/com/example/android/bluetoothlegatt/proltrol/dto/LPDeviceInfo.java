package com.example.android.bluetoothlegatt.proltrol.dto;



public class LPDeviceInfo {

	public static final int STATUS_UNKNOW = -2;
	public static final int STATUS_FAILED = -1;
	public static final int STATUS_OK = 0;
	
    public static final int INDEX_UNACTIVATION = STATUS_OK + 1;
    public static final int INDEX_TIME_ERROR = INDEX_UNACTIVATION + 1;
    public static final int INDEX_USER_INFO = INDEX_TIME_ERROR + 1;
    public static final int INDEX_TASK = INDEX_USER_INFO + 1;
    public static final int INDEX_REMIND = INDEX_TASK + 1;
    public static final int INDEX_CLOCK = INDEX_REMIND + 1;
    public static final int INDEX_USER_ID_ERROR = INDEX_CLOCK + 1;

    /******************各地市名卡******************/
    public static final String UN_KNOW_="un_know";
    public static final String SUZHOU_="215020";
    public static final String LIUZHOU_5="531";
    public static final String LIUZHOU_4="431";
    
    public static final String DATANG_BEIJING="0311";
    public static final String DATANG_ZHENJIANG="3030";
    public static final String DATANG_JIAOTONG="2220";
    public static final String DATANG_HULIAN="xxx0";
    public static final String DATANG_TUOCHENG="7190";
    
    public static final String HENGBAO_1="8612";
    public static final String HENGBAO_2="6228";
    public static final String HENGBAO_QIANBAO="hengbao_qianbao";
    public static final String HENGBAO_XIANJIN="hengbao_xianjin";
    
    public static final String YANGCHENG="yangcheng";
    public static final String MIANYANG_XJ="mianyang_xianjing";
    public static final String MIANYANG_JIEJI="mianyang_jieji";
    public static final String MIANYANG_DAIJI="mianyang_daiji";
    public static final String ZHENGYUAN="zhengyuan";
    
    public static final String LINGNANTONG="510";
    
    public static final String HUBEI_SHUMA="62585";
    public static final String YUNNAN_FUDIAN="6251";
    
	/** 激活码*/
	public static final int ACTIVATION = 1;
	
	/** 硬件版本*/
	public String version;
	public byte[] version_byte=new byte[2];
	public String modelName;

	/**
	 * 电量
	 */
	public int charge10;
	public int charge100;
	public int charge;

	/**
	 * 时间
	 */
	//	public int year; // 年，设定机芯时的时间
	//	public int month; // 月，设定机芯时的时间
	//	public int day; // 日，设定机芯时的时间

	public int timeStamp;
	public int dayOfWeek; // 周几，周日是0，周一是1……，和Calendar返回的值相差1
	public int times; // 当天0:00到现在的时间(30秒为单位)，例如12:01就是1442
	public int dayIndex; // 天数索引（基于0），现在是距离设定机芯时间的第几天

	/**
	 * 激活状态
	 */
	public int recoderStatus = -1;
	/**
	 * 设备状态（省电模式）
	 */
	public int deviceStatus = 0;
	/**
	 * 用户信息
	 */
	public int userPackageHeader = -1;
	public int userGender = -1;
	public int userAge = -1;
	public int userHeight = -1;
	public int userWeight = -1;
	public int userId = -1;

	/**
	 * 设备信息
	 */
	public int devicePackageHeader = -1;
	public String customer = "";
	public int devicetime = -1;
	public int devicemac = -1;
	/**
	 * task
	 */
	public int step; // 设置的任务总量
	
	//老版本信息
	public int stepDayTotals; // 已经完成的任务量
	public int distenceDayTotals;
	
	/**
	 * 走路信息
	 */
	public int dayWalkSteps;
	public int dayWalkDistance;
	public int dayWalkTime;// 时间片，使用时需要*30才是秒
	
	/**
	 * 跑步信息
	 */
	public int dayRunSteps;
	public int dayRunDistance;
	public int dayRunTime;// 时间片，使用时需要*30才是秒
	

	/**
	 * 久坐提醒
	 */
	public int startTime1_H; // 检测区间起始时间(上午小时)
	public int endTime1_H; // 检测区间结束时间(上午小时)
	public int startTime1_M; // 检测区间起始时间(上午分钟)
	public int endTime1_M; // 检测区间结束时间(上午分钟)
	public int startTime2_H; // 检测区间起始时间(下午小时)
	public int endTime2_H; // 检测区间结束时间(下午小时)
	public int startTime2_M; // 检测区间起始时间(下午分钟)
	public int endTime2_M; // 检测区间结束时间(下午分钟)
	public int longsit_step;
	public int timeWindow; // 每个检测窗长度
	public int level; // 在每个检测窗内需要达到的运动次数，达不到就会提醒
	
	/**
	 * 抬手显屏
	 */
	public int handup_startTime1_H=0; // 检测区间起始时间(上午小时)
	public int handup_startTime1_M=0; // 检测区间结束时间(上午分钟)
	public int handup_endTime1_H=0; // 检测区间起始时间(下午小时)
	public int handup_endTime1_M=0; // 检测区间结束时间(下午分钟)
	
	public int handup_startTime2_H=0; // 检测区间起始时间(上午小时)
	public int handup_startTime2_M=0 ;// 检测区间结束时间(上午分钟)
	public int handup_endTime2_H=0 ; // 检测区间起始时间(下午小时)
	public int handup_endTime2_M=0 ; // 检测区间结束时间(下午分钟)
	
	public int handup_timeWindow; // 每个检测窗长度

	/**
	 * 数据长度
	 */
	public int dataLen;

	/**
	 * 闹钟
	 */
	public int alarmTime1_H; // 闹铃时间，
	public int alarmTime1_M;
	public int frequency1; // 周几和ONCE的位或

	public int alarmTime2_H; // 闹铃时间，
	public int alarmTime2_M;
	public int frequency2; // 周几和ONCE的位或

	public int alarmTime3_H; // 闹铃时间，
	public int alarmTime3_M;
	public int frequency3; // 周几和ONCE的位或

	//	public int alarmTime4; // 闹铃时间，
	//	public int frequency4; // 周几和ONCE的位或
	//
	//	public int alarmTime5; // 闹铃时间，
	//	public int frequency5; // 周几和ONCE的位或


	public long time;// 用户信息最后更新的时间
	
	public int osType;// 手机系统ios/android 1/2
	
	public String userNickname;
	
	public String deviceId;
// charge10 = 10  charge100=245 , charge=245
	public double getPercent() {
		return charge >= charge100 ? 1.0
				: charge >= charge10 ? ((charge - charge10) * 0.9
						/ (charge100 - charge10) + 0.1) : 0.1 * charge
						/ charge10;
	}




	public int checkStatus()
	{
		//		if(userAge >= 0 && userGender >=0 && userWeight >= 0 && userHeight >= 0 && userId >= 0)
		//		{
		if(recoderStatus == ACTIVATION)
		{
			return STATUS_OK;	
		}
		else 
		{
			return STATUS_FAILED;
		}
		//		}

		//return STATUS_FAILED;
	}
	
	public int checkUserId(LPDeviceInfo info)
	{
		if(userId == info.userId)
		{
			return STATUS_OK;
		}
		return STATUS_FAILED;
	}

	public int CheckUserInfo(LPDeviceInfo info)
	{
		if (userAge == info.userAge && userGender == info.userGender
				&& userHeight == info.userHeight 
				&& userWeight == info.userWeight) {
			return STATUS_OK;
		}
		return STATUS_FAILED;
	}

	public int checkTask(LPDeviceInfo info)
	{
		if(info.step == step)
		{
			return STATUS_OK;
		}
		return STATUS_FAILED;
	}

	public int checkMotionRemind(LPDeviceInfo info)
	{
		//
		 //&& info.level == level
		//&& info.timeWindow == timeWindow
		if(info.startTime1_H == startTime1_H && info.endTime1_H == endTime1_H &&  info.startTime1_M == startTime1_M && info.endTime1_M == endTime1_M &&
				info.startTime2_H == startTime2_H && info.endTime2_H == endTime2_H &&  info.startTime2_M == startTime2_M && info.endTime2_M == endTime2_M	
				)
		{
			return STATUS_OK;
		}
		return STATUS_FAILED;
	}

	public int checkClock(LPDeviceInfo info)
	{
		if(info.alarmTime1_H == alarmTime1_H && info.alarmTime1_M == alarmTime1_M && info.frequency1 == frequency1
				&& info.alarmTime2_H == alarmTime2_H && info.alarmTime2_M == alarmTime2_M && info.frequency2 == frequency2
				&& info.alarmTime3_H == alarmTime3_H && info.alarmTime3_M == alarmTime3_M && info.frequency3 == frequency3)
			//				&& info.alarmTime4 == alarmTime4 && info.frequency4 == frequency4
			//				&& info.alarmTime5 == alarmTime5 && info.frequency5 == frequency5)
		{
			return STATUS_OK;
		}
		return STATUS_FAILED;
	}
	
	public int checkSaveInfo(LPDeviceInfo info)
	{
		return checkAllInfo(info, false, true);
	}
	
	public int checkLargeInfo(LPDeviceInfo info)
	{
		return checkAllInfo(info, true, false);
	}
	
	public int checkLittleInfo(LPDeviceInfo info)
	{
		return checkAllInfo(info, false, false);
	}
	
	public  int checkAllInfo(LPDeviceInfo info,boolean isLargeInfo,boolean isSave)
	{
		if(isLargeInfo && checkStatus() != STATUS_OK)
		{
		     return INDEX_UNACTIVATION;
		}
		
		if(isLargeInfo && checkUserId(info) != STATUS_OK)
		{
			// 注释掉就可以解决绑定被别人绑过的手环！
			//return INDEX_USER_ID_ERROR;
		}
		
		if((!isSave) && (dayIndex == 0xFF))
		{
			return INDEX_TIME_ERROR;
		}
		
		if(isLargeInfo && CheckUserInfo(info) != STATUS_OK)
		{
			return INDEX_USER_INFO;
		}
		
		if(checkTask(info) != STATUS_OK)
		{
			return INDEX_TASK;
		}
		
		if(checkMotionRemind(info) != STATUS_OK)
		{
			return INDEX_REMIND;
		}
		
		if(checkClock(info) != STATUS_OK)
		{
			return INDEX_CLOCK;
		}
		
		return STATUS_OK;
		
	}

	@Override
	public String toString() {
		return "LPDeviceInfo [userNickname="+userNickname+",charge10="+charge10 + ", charge100="
				+ charge100 + ", charge=" + charge +",timeStamp="+timeStamp+",version="+version
			    + ", stepDayTotals="+ stepDayTotals +",userPackageHeader="+userPackageHeader+", userGender="
				+ userGender + ", userAge=" + userAge + ", userHeight="
				+ userHeight + ", userWeight=" + userWeight +",recoderStatus="+recoderStatus+ ", userId="
				+ userId + ",devicePackageHeader="+devicePackageHeader+ "]";
	}
	
//	// 闹钟
//	public void setAlarms(List<AlarmClock> alarmClocks) {
//
//		if (alarmClocks != null && alarmClocks.size() == 3){
//
//			AlarmClock alarmClock = null;
//
//			// 闹钟处于开启状态才上传到设备中
//			alarmClock = alarmClocks.get(0);
//			alarmTime1 = alarmClock.getAlertTime();
//			if (alarmClock.getState() == 1 ){
//				frequency1 = alarmClock.getFrequency();
//			}
//
//			alarmClock = alarmClocks.get(1);
//			alarmTime2 = alarmClock.getAlertTime();
//			if (alarmClocks.get(1).getState() == 1 ){
//				frequency2 = alarmClock.getFrequency();
//
//			}
//
//			alarmClock = alarmClocks.get(2);
//			alarmTime3 = alarmClock.getAlertTime();
//			if (alarmClocks.get(2).getState() == 1 ){
//				frequency3 = alarmClock.getFrequency();
//			}
//
//			//alarmClock = alarmClocks.get(3);
//			//if (alarmClocks.get(3).getState() == 1 ){
//			//				alarmTime4 = 0;
//			//				frequency4 = 0;
//			//			//}
//			//			
//			//			//alarmClock = alarmClocks.get(4);
//			//			//if (alarmClocks.get(4).getState() == 1 ){
//			//				alarmTime5 = 0;
//			//				frequency5 = 0;
//			//}
//
//		}
//	}
//
//	public List<AlarmClock> getAlarmClocks(){
//		List<AlarmClock> clocks = new ArrayList<AlarmClock>();
//
//		clocks.add(new AlarmClock(alarmTime1, frequency1));
//		clocks.add(new AlarmClock(alarmTime2, frequency2));
//		clocks.add(new AlarmClock(alarmTime3, frequency3));
//		
//		return clocks;
//	}
//
//
//	// 久坐提醒
//	public void setLongsit(LongSitInfo longSitInfo) {
//
//		// 存在并开启了久坐提醒
//		if (longSitInfo != null ){
//			startTime = (int) longSitInfo.getStartTime();
//			endTime = (int) longSitInfo.getEndTime();
//			if (longSitInfo.getState() == 1){
//				timeWindow = longSitInfo.getDuration() * 2;
//				level = 50;
//			}
//		}
//	}
//
//	public LongSitInfo getLongsit(){
//		return new LongSitInfo(userId, startTime, endTime, timeWindow/2);
//	}
//	
	

}
