package com.example.android.bluetoothlegatt.proltrol;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportRecorder;

public class LPResponseNew 
{
	
    private static final String TAG = LPResponseNew.class.getSimpleName();
	// 4 个头部字节
	// 中间最多传输192 byte
	// 2 个做CheckSum
//	public static final int  DATA_LEN = 4 + 192 + 2;
	
	private static final int DATA_BASE = 5;
	
	private static final  int Version_H = DATA_BASE;
	private static final  int Version_M1 = Version_H + 1;
	private static final  int Version_M2 = Version_M1 + 1;;
	private static final  int Version_M3 = Version_M2 + 1; ;
	private static final  int Version_M4 = Version_M3 + 1;
	private static final  int Version_M5 = Version_M4 + 1;
	private static final  int Version_L = Version_M5 + 1;
	
	private static final int CHARGE_BASE = Version_L + 1;
	private static final int CHARGE = CHARGE_BASE;
	private static final int  CHARGE_100 = CHARGE+1;
	private static final int CHARGE_10 = CHARGE_100+1;
	
	private static final int USER_BASE = CHARGE_10 + 1;
	private static final int USER_GENDER = USER_BASE;
	private static final int USER_AGE = USER_GENDER +1;
	private static final int USER_HEIGHT = USER_AGE + 1;
	private static final int USER_WEIGHT = USER_HEIGHT +1;
	private static final int USER_ID_H = USER_WEIGHT+1;
	private static final int USER_ID_M1 = USER_ID_H +1;
	private static final int USER_ID_M2 = USER_ID_M1 +1;
	private static final int USER_ID_L = USER_ID_M2 + 1;
	private static final int USER_STATUS = USER_ID_L+1;
	
	private static final int TIME_BASE = USER_STATUS + 1;
	private static final int TIME_STAMP_H = TIME_BASE;
	private static final int TIME_STAMP_M1 = TIME_STAMP_H + 1;
	private static final int TIME_STAMP_M2 = TIME_STAMP_M1 + 1;
	private static final int TIME_STAMP_L = TIME_STAMP_M2+1;
	private static final int TIME_DAY_INDEX = TIME_STAMP_L + 1;
	private static final int TIME_WEEK = TIME_DAY_INDEX +1;
	private static final int TIME_TIMES_H = TIME_WEEK +1;
	private static final int TIME_TIMES_L = TIME_TIMES_H+1;
	
	private static final int TASK_BASE = TIME_TIMES_L + 1;
	private static final int TASK_STEP_H = TASK_BASE;
	private static final int TASK_STEP_M1 = TASK_STEP_H +1;
	private static final int TASK_STEP_L = TASK_STEP_M1 +1;
	
	private static final int DAY_TOTAL_WALK_BASE = TASK_STEP_L + 1;
	private static final int DAY_TOTAL_WALK_STEP_H =  DAY_TOTAL_WALK_BASE;
	private static final int DAY_TOTAL_WALK_STEP_M1 = DAY_TOTAL_WALK_STEP_H + 1;
	private static final int DAY_TOTAL_WALK_STEP_L = DAY_TOTAL_WALK_STEP_M1 + 1;
	private static final int DAY_TOTAL_WALK_DISTANCE_H = DAY_TOTAL_WALK_STEP_L + 1;
	private static final int DAY_TOTAL_WALK_DISTANCE_M1 = DAY_TOTAL_WALK_DISTANCE_H + 1;
	private static final int DAY_TOTAL_WALK_DISTANCE_L = DAY_TOTAL_WALK_DISTANCE_M1 + 1;
	private static final int DAY_TOTAL_WALK_TIME_H = DAY_TOTAL_WALK_DISTANCE_L + 1;
	private static final int DAY_TOTAL_WALK_TIME_L = DAY_TOTAL_WALK_TIME_H + 1;
	
	private static final int DAY_TOTAL_RUN_BASE = DAY_TOTAL_WALK_TIME_L + 1;
	private static final int DAY_TOTAL_RUN_STEP_H = DAY_TOTAL_RUN_BASE;
	private static final int DAY_TOTAL_RUN_STEP_M1 = DAY_TOTAL_RUN_STEP_H + 1;
	private static final int DAY_TOTAL_RUN_STEP_L = DAY_TOTAL_RUN_STEP_M1 + 1;
	private static final int DAY_TOTAL_RUN_DISTANCE_H = DAY_TOTAL_RUN_STEP_L + 1;
	private static final int DAY_TOTAL_RUN_DISTANCE_M1 = DAY_TOTAL_RUN_DISTANCE_H + 1;
	private static final int DAY_TOTAL_RUN_DISTANCE_L = DAY_TOTAL_RUN_DISTANCE_M1 + 1;
	private static final int DAY_TOTAL_RUN_TIME_H = DAY_TOTAL_RUN_DISTANCE_L + 1;
	private static final int DAY_TOTAL_RUN_TIME_L = DAY_TOTAL_RUN_TIME_H + 1;
	
	private static final int REMIND_BASE = DAY_TOTAL_RUN_TIME_L + 1;
	private static final int REMIND_START_TIME_H = REMIND_BASE;
	private static final int REMIND_START_TIME_L = REMIND_START_TIME_H+1;
	private static final int REMIND_END_TIME_H = REMIND_START_TIME_L +1;
	private static final int REMIND_END_TIME_L = REMIND_END_TIME_H + 1;
	private static final int REMIND_TIME_WINDOW = REMIND_END_TIME_L +1;
	private static final int REMIND_LEVEL_H = REMIND_TIME_WINDOW +1;
	private static final int REMIND_LEVEL_L = REMIND_LEVEL_H + 1;
	
	private static final int CLOCK_BASE = REMIND_LEVEL_L + 1;
	private static final int CLOCK_ALARM_TIME_H1 = CLOCK_BASE;
	private static final int CLOCK_ALARM_TIME_L1 = CLOCK_ALARM_TIME_H1 + 1;
	private static final int CLOCK_FREQ1 = CLOCK_ALARM_TIME_L1 +1;
	private static final int CLOCK_ALARM_TIME_H2 = CLOCK_FREQ1 + 1;
	private static final int CLOCK_ALARM_TIME_L2 = CLOCK_ALARM_TIME_H2 + 1;
	private static final int CLOCK_FREQ2 = CLOCK_ALARM_TIME_L2 +1;
	private static final int CLOCK_ALARM_TIME_H3 = CLOCK_FREQ2 + 1;
	private static final int CLOCK_ALARM_TIME_L3 = CLOCK_ALARM_TIME_H3 + 1;
	private static final int CLOCK_FREQ3 = CLOCK_ALARM_TIME_L3 +1;
//	private static final int CLOCK_ALARM_TIME_H4 = CLOCK_FREQ3 + 1;
//	private static final int CLOCK_ALARM_TIME_L4 = CLOCK_ALARM_TIME_H4 + 1;
//	private static final int CLOCK_FREQ4 = CLOCK_ALARM_TIME_L4 +1;
//	private static final int CLOCK_ALARM_TIME_H5 = CLOCK_FREQ4 + 1;
//	private static final int CLOCK_ALARM_TIME_L5 = CLOCK_ALARM_TIME_H5 + 1;
//	private static final int CLOCK_FREQ5 = CLOCK_ALARM_TIME_L5 +1;
	
	private static final int DATA_LEN_BASE = CLOCK_FREQ3 + 1;
	private static final int DATA_LEN_H = DATA_LEN_BASE;
	private static final int DATA_LEN_L = DATA_LEN_H +1;
	
	
	private static final int TASK_BASE_0x02 = DATA_BASE;
	private static final int TASK_STEP_H_0x02 = TASK_BASE_0x02;
	private static final int TASK_STEP_M1_0x02 = TASK_STEP_H_0x02 +1;
	private static final int TASK_STEP_L_0x02 = TASK_STEP_M1_0x02 +1;
	
	private static final int REMIND_BASE_0x02  = TASK_STEP_L_0x02 + 1;
	private static final int REMIND_START_TIME_H_0x02  = REMIND_BASE_0x02;
	private static final int REMIND_START_TIME_L_0x02  = REMIND_START_TIME_H_0x02+1;
	private static final int REMIND_END_TIME_H_0x02  = REMIND_START_TIME_L_0x02 +1;
	private static final int REMIND_END_TIME_L_0x02  = REMIND_END_TIME_H_0x02 + 1;
	private static final int REMIND_TIME_WINDOW_0x02  = REMIND_END_TIME_L_0x02 +1;
	private static final int REMIND_LEVEL_H_0x02  = REMIND_TIME_WINDOW_0x02 +1;
	private static final int REMIND_LEVEL_L_0x02  = REMIND_LEVEL_H_0x02 + 1;
	
	private static final int CLOCK_BASE_0x02  = REMIND_LEVEL_L_0x02 + 1;
	private static final int CLOCK_ALARM_TIME_H1_0x02  = CLOCK_BASE_0x02;
	private static final int CLOCK_ALARM_TIME_L1_0x02  = CLOCK_ALARM_TIME_H1_0x02 + 1;
	private static final int CLOCK_FREQ1_0x02  = CLOCK_ALARM_TIME_L1_0x02 +1;
	private static final int CLOCK_ALARM_TIME_H2_0x02  = CLOCK_FREQ1_0x02 + 1;
	private static final int CLOCK_ALARM_TIME_L2_0x02  = CLOCK_ALARM_TIME_H2_0x02 + 1;
	private static final int CLOCK_FREQ2_0x02  = CLOCK_ALARM_TIME_L2_0x02 +1;
	private static final int CLOCK_ALARM_TIME_H3_0x02  = CLOCK_FREQ2_0x02 + 1;
	private static final int CLOCK_ALARM_TIME_L3_0x02  = CLOCK_ALARM_TIME_H3_0x02 + 1;
	private static final int CLOCK_FREQ3_0x02  = CLOCK_ALARM_TIME_L3_0x02 +1;
	
	private static final int DATA_LEN_BASE_0x02  = CLOCK_FREQ3_0x02 + 1;
	private static final int DATA_LEN_H_0x02  = DATA_LEN_BASE_0x02;
	private static final int DATA_LEN_L_0x02  = DATA_LEN_H_0x02 +1;
	
	private static final int TIME_STAMP_0X02 = DATA_LEN_L_0x02 + 1;
    private static final int TIME_CAL_DAY_H_0x02 = TIME_STAMP_0X02 + 1;
    private static final int TIME_CAL_DAY_L_0x02 = TIME_CAL_DAY_H_0x02 + 1;
	
	
	
	private  byte[]  data;
	private  int     len;
	public  LPResponseNew( byte[] d, int offset, int l ) throws LPException {
		if(d == null || d[0] != LepaoCommand.COMMAND_HEAD  ||  l <= 0 || !LPUtil.unsignedEqual( d[1], l ) ) {
			LPException  ex = new LPException( LPErrCode.LP_ILLEGAL_DATA );
			ex.addMsg( "len", String.valueOf(l) );
			if( l > 0 ) {
				ex.addMsg( "data", LPUtil.ubytesToString( d, offset, l ) );
			}
			throw  ex;
		}
		//if( d[2] == LepaoCommand.COMMAND_IS_STATUS && d[4] != 0x00 )
		if(d[4] != 0x00 && d[4] != 0x01 && d[4] != 0x02 && d[4] != 0x03)
		{
			int  errCode = d[4];
			LPException  ex = new LPException( errCode );
			ex.addMsg( "device error", LPErrCode.getMessage( errCode ) );
			throw  ex;
		}
		len = l;	
		data = new byte[len];
		System.arraycopy( d, offset, data, 0, len );
	}
	
	public  byte[]  getData() {
		return  data;
	}
	
	public  boolean  toOK( byte cmd ,String cmdName) throws LPException {
//		if( data[2] == LepaoCommand.COMMAND_IS_STATUS &&
//				data[3] == cmd &&
//				data[4] == 0x00 &&
//				data[5] == 0x00 ) {
//		if( data[2] == cmd &&
//		data[4] == 0x00)
//		{
//			return  true;
//		}
////		if( data[2] != LepaoCommand.COMMAND_IS_STATUS || data[3] != cmd ) {
//		if( data[2] != cmd ) {
//			LPException  ex = new LPException( LPErrCode.LP_RESPONSE_ERR );
//			ex.addMsg( "len", String.valueOf(len) );
//			if( len > 0 ) {
//				ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
//			}
//			throw  ex;
//		}
//		if( data[4] != 0x00 ) {
//			int  errCode = data[4];
//			LPException  ex = new LPException( errCode );
//			ex.addMsg( "device error", LPErrCode.getMessage( errCode ) );
//			throw  ex;
//		}
//		
//		return  false;
		checkResponseOK(cmd, cmdName);
		return true;
	}
	
	public  List< LPSportData >  toLPSportDataList() throws LPException {
//		if( data[2] != LepaoCommand.COMMAND_IS_DATA || data[3] != LepaoCommand.COMMAND_GET_SPORT_DATA ) {
//		if( data[2] != LepaoCommand.COMMAND_GET_SPORT_DATA ) {
//			LPException  ex = new LPException( LPErrCode.LP_CONVERT_ERR );
//			ex.addMsg( "type", "SportData" );
//			ex.addMsg( "len", String.valueOf(len) );
//			if( len > 0 ) {
//				ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
//			}
//			throw  ex;
//		}
		
		//String dataSrc = LPUtil.ubytesToString( data, 0, LPResponseNew.this.len );
		int len = LPUtil.makeShort(data[5], data[6]);
		Log.d(TAG, ".........................剩余原始数据记录条数:"+len+".........................");
		int timeStemp = LPUtil.makeInt(data[7], data[8], data[9], data[10]);
		List<LPSportData> list = new ArrayList<LPSportData>();
		for( int i = 11; i < this.len-2; i += 6 ) {
			LPSportData sportData = new LPSportData();
		//	sportData.srcData = dataSrc ;
			if(((data[i+4] & 0xFF)&0xC0) == 0xC0)  // if((data[i+4]&0xC0) == 0xC0)
			{
				sportData.setRefTime(LPUtil.makeShort(data[i], data[i+1]));
				
				sportData.setState( ( (data[i+4]&0xFF) >> 4 ) & 0x03 );
				System.out.println("-----------------原始数据部署--------------------------");
				
				sportData.setDuration( LPUtil.makeShort((byte) (data[i+6]), data[i+7]) - sportData.getRefTime());
				sportData.setDataLen(len);
				sportData.setRevLen(this.len);
				Log.d(TAG, ".........................收到数据的长度:"+this.len+".........................");
				sportData.setTimeStemp(timeStemp);
				
				if( sportData.getState() == LPSportData.STATE_IDLE) {
					sportData.setSteps(  data[i+2] & 0xFF );
					sportData.setDistance( ( data[i+3] & 0xFF ) * 16 );
					sportData.setStepsPart( (data[i+4] & 0x0F)*4);
					sportData.setDistancePart( ( data[i+5] & 0xFF ) * 4 );
				}
				else {
					sportData.setDistance( LPUtil.makeShort( data[i+2], data[i+3] ) );
					sportData.setSteps( LPUtil.makeShort( (byte) (data[i+4]&0x0F), data[i+5] ) );
					sportData.setDistancePart( -1 );
					sportData.setStepsPart( -1 );
				}				
			}
			else
			{
				sportData.setRefTime(LPUtil.makeShort((byte)(data[i]&0x3F), data[i+1]));
				sportData.setState( ( (data[i]&0xFF) >> 6 ) & 0x03 ); 
				System.out.println("-----------------原始数据部署--------------------------");
				
				sportData.setDuration( LPUtil.makeShort((byte) (data[i+6]&0x3F), data[i+7]) - sportData.getRefTime());
				sportData.setDataLen(len);
				sportData.setRevLen(this.len);
				Log.d(TAG, ".........................收到数据的长度:"+this.len+".........................");
				sportData.setTimeStemp(timeStemp);
				
				if( sportData.getState() == LPSportData.STATE_IDLE) {
					sportData.setSteps(  data[i+2] & 0xFF );
					sportData.setDistance( ( data[i+3] & 0xFF ) * 16 );
					sportData.setStepsPart( data[i+4] & 0xFF );
					sportData.setDistancePart( ( data[i+5] & 0xFF ) * 16 );
				}
				else {
					sportData.setDistance( LPUtil.makeShort( data[i+2], data[i+3] ) );
					sportData.setSteps( LPUtil.makeShort( data[i+4], data[i+5] ) );
					sportData.setDistancePart( -1 );
					sportData.setStepsPart( -1 );
				}
			}
			Log.d(TAG, sportData.toString());
			
			int tmp = sportData.getDuration();
			
			if(tmp < 0 || tmp > 14430)
			{
				sportData.state = 0;
				sportData.duration = 31;
				sportData.stepsPart =  LPUtil.makeShort((byte) (data[i+6]&0x3F), data[i+7]);
				sportData.distancePart = sportData.getRefTime();
			}
			
			list.add(sportData);
		}
		
		if(list.size() > 0)
		{
			LPSportData last = list.get(list.size() -1);
			if(last.state == 0 && last.duration == 31)
			{
				last.duration = 32;
			}
		}
		
		return  list;
	}
	
	public  String  toDeviceID() throws LPException {
//		if( len != 14 || data[2] != LepaoCommand.COMMAND_IS_DATA || data[3] != LepaoCommand.COMMAND_GET_DEVICE_ID ) {
//		if( len != 17 || data[2] != LepaoCommand.COMMAND_GET_DEVICE_ID ) {
		if( len != 17){
			LPException  ex = new LPException( LPErrCode.LP_CONVERT_ERR );
			ex.addMsg( "type", "DeviceVersion" );
			ex.addMsg( "len", String.valueOf(len) );
			if( len > 0 ) {
				ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
			}
			throw  ex;
		}
		byte[] bytes = new byte[12];
		
//		return  LPUtil.makeLong( data[4], data[5], data[6], data[7],
//				data[8], data[9], data[10], data[11] );
		for (int i = 0; i < bytes.length; i++)
		{
               bytes[i] = data[i+5];			
		}
		return LPUtil.makeDeviceID(bytes);
	}
	
	/** 设备信息精简版
	 * @throws LPException */
	public LPDeviceInfo toDeviceInfoSimplify(byte cmd,String cmdName) throws LPException
	{
		checkResponseOK(cmd,cmdName);
		LPDeviceInfo info = new LPDeviceInfo();
		info.step =  LPUtil.makeInt((byte)0, data[TASK_STEP_H_0x02], data[TASK_STEP_M1_0x02], data[TASK_STEP_L_0x02] ) ;
		
		info.dataLen = LPUtil.makeShort(data[DATA_LEN_H_0x02], data[DATA_LEN_L_0x02]);
		
//		info.startTime1_H = LPUtil.makeShort(data[REMIND_START_TIME_H_0x02], data[REMIND_START_TIME_L_0x02]);
//		info.endTime  = LPUtil.makeShort(data[REMIND_END_TIME_H_0x02], data[REMIND_END_TIME_L_0x02]);
		info.timeWindow = data[REMIND_TIME_WINDOW_0x02] & 0xFF;
		info.level = LPUtil.makeShort(data[REMIND_LEVEL_H_0x02], data[REMIND_LEVEL_L_0x02]);
		
//		info.alarmTime1 = LPUtil.makeShort(data[CLOCK_ALARM_TIME_H1_0x02], data[CLOCK_ALARM_TIME_L1_0x02]);
//		info.frequency1 = data[CLOCK_FREQ1_0x02] & 0xFF;
//		
//		info.alarmTime2 = LPUtil.makeShort(data[CLOCK_ALARM_TIME_H2_0x02], data[CLOCK_ALARM_TIME_L2_0x02]);
//		info.frequency2 = data[CLOCK_FREQ2_0x02] & 0xFF;
//		
//		info.alarmTime3 = LPUtil.makeShort(data[CLOCK_ALARM_TIME_H3_0x02], data[CLOCK_ALARM_TIME_L3_0x02]);
//		info.frequency3 = data[CLOCK_FREQ3_0x02] & 0xFF;
		
		info.dayIndex = data[TIME_STAMP_0X02] & 0xFF;
		info.times = LPUtil.makeShort(data[TIME_CAL_DAY_H_0x02], data[TIME_CAL_DAY_L_0x02]);
		
		return info;
	}
	
    /**   历史记录数据
     * @throws LPException */
	public  List<LPSportRecorder> toSportRecorder() throws LPException
	{
		checkResponseOK(LepaoCommand.COMMAND_GET_SPORT_RECORDER,"toSportRecorder");
		List<LPSportRecorder> recorders = new ArrayList<LPSportRecorder>();
		for(int i = 5;i < len;i += 12)
		{
			if(i + 12 > len)
				continue;
			// 根据序设备中对数据的定义，此原始数据读取出来后*4才是真正的步数、距离等
			LPSportRecorder sportRecorder = new LPSportRecorder();
			sportRecorder.steps = LPUtil.makeShort(data[i], data[i+1]) * 4;
			sportRecorder.distance = LPUtil.makeShort(data[i+2], data[i+3]) * 4;
			sportRecorder.duration = LPUtil.makeShort(data[i+4], data[i+5])*30;
			sportRecorder.runSteps = LPUtil.makeShort(data[i+6], data[i+7]) * 4;
			sportRecorder.runDistance = LPUtil.makeShort(data[i+8], data[i+9]) * 4;
			sportRecorder.runDuration = LPUtil.makeShort(data[i+10], data[i+11])*30;
			recorders.add(sportRecorder);
		}
		return recorders;
	}
	
	/** 设备信息详细版*/
	public LPDeviceInfo toDeviceInfo(byte cmd,String cmdName) throws LPException
	{
		if(data.length <= 60)
			throw new LPException(LPErrCode.LP_ILLEGAL_DATA);
		checkResponseOK(cmd,cmdName);
		LPDeviceInfo info = new LPDeviceInfo();
		info.charge = ( data[CHARGE] & 0xFF );
		info.charge100 = ( data[CHARGE_100] & 0xFF );
		info.charge10 = ( data[CHARGE_10] & 0xFF );
		
		char[] vChar = new char[7];
		vChar[0] = (char) (data[Version_H]&0xFF);
		vChar[1] = (char) (data[Version_M1]&0xFF);
		vChar[2] = (char) (data[Version_M2]&0xFF);
		vChar[3] = (char) (data[Version_M3]&0xFF);
		vChar[4] = (char) (data[Version_M4]&0xFF);
		vChar[5] = (char) (data[Version_M5]&0xFF);
		vChar[6] = (char) (data[Version_L]&0xFF);
		info.version = new String(vChar);
		
		info.timeStamp = LPUtil.makeInt(data[TIME_STAMP_H], data[TIME_STAMP_M1], data[TIME_STAMP_M2], data[TIME_STAMP_L]);
		info.dayIndex = ( data[TIME_DAY_INDEX] & 0xFF );
		info.dayOfWeek = ( data[TIME_WEEK] & 0xFF );
		info.times = LPUtil.makeShort( data[TIME_TIMES_H], data[TIME_TIMES_L] );
		
		info.recoderStatus = (data[USER_STATUS] & 0xFF);
		info.userAge = (data[USER_AGE] & 0xFF);
		info.userGender =  (data[USER_GENDER] & 0xFF);
		info.userHeight =  (data[USER_HEIGHT] & 0xFF);
		info.userWeight =  (data[USER_WEIGHT] & 0xFF);
		info.userId = LPUtil.makeInt(data[USER_ID_H], data[USER_ID_M1], data[USER_ID_M2], data[USER_ID_L]);
		
		info.step =  LPUtil.makeInt((byte)0, data[TASK_STEP_H], data[TASK_STEP_M1], data[TASK_STEP_L] ) ;
		info.dayWalkSteps = LPUtil.makeInt((byte)0, data[DAY_TOTAL_WALK_STEP_H], data[DAY_TOTAL_WALK_STEP_M1],  data[DAY_TOTAL_WALK_STEP_L]);
		info.dayWalkTime = LPUtil.makeShort(data[DAY_TOTAL_WALK_TIME_H], data[DAY_TOTAL_WALK_TIME_L]);
		info.dayWalkDistance = LPUtil.makeInt((byte)0, data[DAY_TOTAL_WALK_DISTANCE_H], data[DAY_TOTAL_WALK_DISTANCE_M1],  data[DAY_TOTAL_WALK_DISTANCE_L]);
		info.dayRunSteps = LPUtil.makeInt((byte)0, data[DAY_TOTAL_RUN_STEP_H], data[DAY_TOTAL_RUN_STEP_M1],  data[DAY_TOTAL_RUN_STEP_L]);
		info.dayRunTime =  LPUtil.makeShort(data[DAY_TOTAL_RUN_TIME_H], data[DAY_TOTAL_RUN_TIME_L]);
		info.dayRunDistance = LPUtil.makeInt((byte)0, data[DAY_TOTAL_RUN_DISTANCE_H], data[DAY_TOTAL_RUN_DISTANCE_M1],  data[DAY_TOTAL_RUN_DISTANCE_L]);
		
		info.dataLen = LPUtil.makeShort(data[DATA_LEN_H], data[DATA_LEN_L]);
		
//		info.startTime = LPUtil.makeShort(data[REMIND_START_TIME_H], data[REMIND_START_TIME_L]);
//		info.endTime  = LPUtil.makeShort(data[REMIND_END_TIME_H], data[REMIND_END_TIME_L]);
		info.timeWindow = data[REMIND_TIME_WINDOW] & 0xFF;
		info.level = LPUtil.makeShort(data[REMIND_LEVEL_H], data[REMIND_LEVEL_L]);
		
//		info.alarmTime1 = LPUtil.makeShort(data[CLOCK_ALARM_TIME_H1], data[CLOCK_ALARM_TIME_L1]);
//		info.frequency1 = data[CLOCK_FREQ1] & 0xFF;
//		
//		info.alarmTime2 = LPUtil.makeShort(data[CLOCK_ALARM_TIME_H2], data[CLOCK_ALARM_TIME_L2]);
//		info.frequency2 = data[CLOCK_FREQ2] & 0xFF;
//		
//		info.alarmTime3 = LPUtil.makeShort(data[CLOCK_ALARM_TIME_H3], data[CLOCK_ALARM_TIME_L3]);
//		info.frequency3 = data[CLOCK_FREQ3] & 0xFF;
		
		return info;
	}
	
	
	private void checkResponseOK(byte cmd,String cmdName) throws LPException
	{
		if( data[2] != cmd) {
			LPException  ex = new LPException( LPErrCode.LP_CONVERT_ERR );
			ex.addMsg( "type",cmdName );
			ex.addMsg( "len", String.valueOf(len) );
			if( len > 0 ) {
				ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
			}
			throw  ex;
		}
	}

}
