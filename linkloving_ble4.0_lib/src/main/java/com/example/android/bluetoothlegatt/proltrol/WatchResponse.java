package com.example.android.bluetoothlegatt.proltrol;

import android.util.Log;

import com.example.android.bluetoothlegatt.proltrol.dto.LLTradeRecord;
import com.example.android.bluetoothlegatt.proltrol.dto.LLXianJinCard;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportRecorder;
import com.example.android.bluetoothlegatt.utils.OwnLog;

import java.util.ArrayList;
import java.util.List;

public class WatchResponse {

	 private static final String TAG = WatchResponse.class.getSimpleName();
		// 4 个头部字节
		// 中间最多传输192 byte
		// 2 个做CheckSum
        // public static final int  DATA_LEN = 4 + 192 + 2;
		private static final int DATA_BASE = 4;
		
		
		/*-------------获取设备所有信息系列开始--------------*/
		
		private static final int CHARGE = DATA_BASE + 1;
		private static final int CHARGE_10 = CHARGE + 1;
		private static final int CHARGE_100 =  CHARGE_10+ 1;
		/**时间系列*/
		private static final int TIME_STAMP_BASE = CHARGE_100 + 1;
		private static final int TIME_STAMP_H = TIME_STAMP_BASE + 1;
		private static final int TIME_STAMP_M1 = TIME_STAMP_H + 1;
		private static final int TIME_STAMP_M2 = TIME_STAMP_M1 + 1;
		private static final int TIME_STAMP_L = TIME_STAMP_M2 + 1;
		/**版本系列*/
//		private static final  int Version_BASE = TIME_STAMP_L + 1;
		private static final  int Version_M1 = TIME_STAMP_L + 1;
		private static final  int Version_M2 = Version_M1 + 1;
		/**当日完成步数系列*/
		private static final int STEP_DAY_ALL_BASE1 = Version_M2 + 1;
		private static final int STEP_DAY_ALL_BASE2 = STEP_DAY_ALL_BASE1 + 1;
		private static final int STEP_DAY_ALL1 = STEP_DAY_ALL_BASE2 + 1;
		private static final int STEP_DAY_ALL2 = STEP_DAY_ALL1 +1;
		private static final int STEP_DAY_ALL3 = STEP_DAY_ALL2 +1;
		private static final int STEP_DAY_ALL4 = STEP_DAY_ALL3+1;
		/**用户信息系列*/
		private static final int USER_PACKAGEHEADER = STEP_DAY_ALL4 + 1;
		private static final int USER_GENDER = USER_PACKAGEHEADER + 1;
		private static final int USER_AGE = USER_GENDER + 1;
		private static final int USER_HEIGHT = USER_AGE + 1;
		private static final int USER_WEIGHT = USER_HEIGHT + 1;
		private static final int USER_DEVICEACTIVED = USER_WEIGHT + 1;
		private static final int USER_ID1 = USER_DEVICEACTIVED + 1;
		private static final int USER_ID2 = USER_ID1 + 1;
		private static final int USER_ID_BASE1 = USER_ID2 + 1;
		private static final int USER_ID_BASE2 = USER_ID_BASE1 + 1;
		private static final int USER_ID3 = USER_ID_BASE2 + 1;
		private static final int USER_ID4 = USER_ID3 + 1;
		/**设备信息系列*/
		private static final int DEVICE_PACKAGEHEADER = USER_ID4 + 1;
		private static final int DEVICE_CUSTOMER1 = DEVICE_PACKAGEHEADER+1;
		private static final int DEVICE_CUSTOMER2 = DEVICE_CUSTOMER1 +1;
		private static final int DEVICE_CUSTOMER3 = DEVICE_CUSTOMER2 +1;
		private static final int DEVICE_TIME1 = DEVICE_CUSTOMER3 +1;
		private static final int DEVICE_TIME2 = DEVICE_TIME1 +1;
		private static final int DEVICE_TIME3 = DEVICE_TIME2 +1;
		private static final int DEVICE_TIME4 = DEVICE_TIME3 +1;
		private static final int DEVICE_MAC_IP1 = DEVICE_TIME4 +1;
		private static final int DEVICE_MAC_IP2 = DEVICE_MAC_IP1 +1;
		private static final int DEVICE_MAC_IP3 = DEVICE_MAC_IP2 +1;
		private static final int DEVICE_MAC_IP4 = DEVICE_MAC_IP3 +1;
		private static final int DEVICE_MAC_IP5 = DEVICE_MAC_IP4 +1;
		private static final int DEVICE_MAC_IP6 = DEVICE_MAC_IP5 +1;
		/*-------------获取设备所有信息系列结束--------------*/
		
		public static final boolean CHECKPIN_OK = true;
		
		private  byte[]  data;
		private  int     len;
		/**
		 * 
		 * @param d        recv 的 byte数据
		 * @param offset   复制的起始位置
		 * @param l        recv 的 byte数据的长度
		 * @throws LPException
		 */
		public  WatchResponse( byte[] d, int offset, int l ) throws LPException {
			if(d == null || d[0] != LepaoCommand.COMMAND_HEAD  ||  l <= 0 || !LPUtil.unsignedEqual( d[1], l ) ) {
				LPException  ex = new LPException( LPErrCode.LP_ILLEGAL_DATA );
				ex.addMsg( "len", String.valueOf(l));
				if( l > 0 ) {
					ex.addMsg( "data", LPUtil.ubytesToString( d, offset, l ) );
				}
				throw  ex;
			}
			//if( d[2] == LepaoCommand.COMMAND_IS_STATUS && d[4] != 0x00 )
//			if(d[4] != 0x00 && d[4] != 0x01 && d[4] != 0x02 && d[4] != 0x03)
//			{
//				int  errCode = d[4];
//				LPException  ex = new LPException( errCode );
//				ex.addMsg( "device error", LPErrCode.getMessage( errCode ) );
//				throw  ex;
//			}
			len = l;	
			data = new byte[len];
			System.arraycopy( d, offset, data, 0, len );
		}
		
		public  byte[]  getData() {
			return  data;
		}
		
		 public static String getString(String s1,String s2,int l){
			  StringBuilder sb=new StringBuilder();
			  sb.append(s1).insert(l, s2); 
			  return sb.toString();
		  }
		
		   /**   历史记录数据
	     * @throws LPException */
		public  List<LPSportRecorder> toSportRecorder() throws LPException
		{
			checkResponseOK(LepaoCommand.COMMAND_GET_SPORT_RECORDER,"toSportRecorder");
			List<LPSportRecorder> recorders = new ArrayList<LPSportRecorder>();
			for(int i = 11;i < len-2;i += 5)
			{
				if(i + 12 > len)
					continue;
				// 根据序设备中对数据的定义，此原始数据读取出来后*4才是真正的步数、距离等
				LPSportRecorder sportRecorder = new LPSportRecorder();
				sportRecorder.numbers = LPUtil.makeShort(data[6], data[5]) ;    //运动记录条数
				sportRecorder.inittime= LPUtil.makeInt(data[10],data[9], data[8], data[7]);  //设置到设备里的初始化时间
				OwnLog.i(TAG, "读出来的运动步数:"+sportRecorder.steps);
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
			if(data.length <= 40)  //这个以后再定
				throw new LPException(LPErrCode.LP_ILLEGAL_DATA);
			checkResponseOK(cmd,cmdName);
			LPDeviceInfo info = new LPDeviceInfo();
			info.charge = ( data[CHARGE] & 0xFF);
			info.charge10 = ( data[CHARGE_10] & 0xFF);
			info.charge100 = ( data[CHARGE_100] & 0xFF);
			info.timeStamp = LPUtil.makeInt(data[TIME_STAMP_L],data[TIME_STAMP_M2],data[TIME_STAMP_M1],data[TIME_STAMP_H]);
			/**------------version-----------**/
			StringBuilder sb=new StringBuilder(); 
			for(int i=Version_M2;i>=Version_M1;i--){
				String hex = Integer.toHexString(data[i] & 0xFF); 
				if (hex.length() == 1) { 
				hex = '0' + hex;
				}
				sb.append(hex);
			}
			info.version_byte[0]=data[Version_M1];
			info.version_byte[1]=data[Version_M2];
			info.version=sb.toString();
			OwnLog.i(TAG, "版本号是："+info.version);
			/**------------version-----------**/
			info.stepDayTotals =  LPUtil.makeInt(data[STEP_DAY_ALL4],data[STEP_DAY_ALL3],data[STEP_DAY_ALL2],data[STEP_DAY_ALL1]);
			info.userPackageHeader =( data[USER_PACKAGEHEADER] & 0xFF );
			info.userGender =( data[USER_GENDER] & 0xFF );
			info.userAge = ( data[USER_AGE] & 0xFF );
			info.userHeight = ( data[USER_HEIGHT] & 0xFF );
			info.userWeight = ( data[USER_WEIGHT] & 0xFF );
			info.recoderStatus = ( data[USER_DEVICEACTIVED] & 0xFF );
			info.userId =  LPUtil.makeInt(data[USER_ID4] , data[USER_ID3] , data[USER_ID2] , data[USER_ID1]);
			info.devicePackageHeader = ( data[DEVICE_PACKAGEHEADER] & 0xFF );
			return info;
		}
		
		/** 设备信息详细版*/
		public LPDeviceInfo toModelName(byte cmd,String cmdName) throws LPException
		{
			checkResponseOK(cmd,cmdName);
			LPDeviceInfo info = new LPDeviceInfo();
			Log.i(TAG, "...................获取到的modelname:"+(char)(data[17])+(char)(data[18])+(char)(data[19])+(char)(data[20])+(char)(data[21])+(char)(data[22])+".........................");
			if((""+(char)(data[17])+(char)(data[18])+(char)(data[19])+(char)(data[20])+(char)(data[21])).startsWith("LW100")){
				info.modelName = "LW100";
			}else{
				info.modelName = ""+(char)(data[17])+(char)(data[18])+(char)(data[19])+(char)(data[20])+(char)(data[21])+(char)(data[22]);
			}
			return info;
		}
		
		public String toCardNumber(byte cmd,String cmdName) throws LPException{
			checkResponseOK(cmd,cmdName);
			String hex = null;
			for(int i=4;i<17;i++){
				hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
			}
			return hex;
		}
		
		
		public  List< LPSportData >  toLPSportDataList(int devicetime) throws LPException {
//			if( data[2] != LepaoCommand.COMMAND_IS_DATA || data[3] != LepaoCommand.COMMAND_GET_SPORT_DATA ) {
//			if( data[2] != LepaoCommand.COMMAND_GET_SPORT_DATA ) {
//				LPException  ex = new LPException( LPErrCode.LP_CONVERT_ERR );
//				ex.addMsg( "type", "SportData" );
//				ex.addMsg( "len", String.valueOf(len) );
//				if( len > 0 ) {
//					ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
//				}
//				throw  ex;
//			}
			//String dataSrc = LPUtil.ubytesToString( data, 0, LPResponseNew.this.len );
			int len = LPUtil.makeShort(data[6], data[5]);
			OwnLog.i(TAG, ".........................剩余原始数据记录条数:"+len+".........................");
			int timeStemp = LPUtil.makeInt(data[10], data[9], data[8], data[7]);
			List<LPSportData> list = new ArrayList<LPSportData>();
			timeStemp = timeStemp+devicetime; //应该加上 utc - 0x13 返回的timestmp
			byte[] data_sport = new byte[data.length-13];
			System.arraycopy(data, 11, data_sport, 0, data.length-13);
			for(int i=0;i<data_sport.length-2;i+=6){
				LPSportData sportData = new LPSportData();
				System.out.println("-----------------原始数据部署--------------------------");
				sportData.setRefTime(LPUtil.makeShort((byte)(data_sport[i+1]), (byte)data_sport[i]));  //关于timebase的偏移值
				OwnLog.d(TAG, "sportData.setRefTime:"+LPUtil.makeShort((byte)(data_sport[i+1]),(byte)data_sport[i]));
				sportData.setState(data_sport[i+2] & 0xFF);
				sportData.setSteps(data_sport[i+3] & 0xFF);
				if(sportData.getState()==LPSportData.STATE_IDLE){
					sportData.setDistance((data_sport[i+4]&0xFF)*16);
				}else {
					sportData.setDistance((data_sport[i+4]&0xFF));
				}
				sportData.setDuration( LPUtil.makeShort((byte)(data_sport[i+7]),(byte) data_sport[i+6]) - sportData.getRefTime());
				OwnLog.i(TAG, "sportData.setDuration:"+LPUtil.makeShort((byte)(data_sport[i+7]), (byte)data_sport[i+6])+"---->sportData.getRefTime():"+sportData.getRefTime());
				sportData.setDataLen(len);
				sportData.setRevLen(this.len);
				OwnLog.i(TAG, ".........................收到数据的长度:"+this.len+".........................");
				sportData.setTimeStemp(timeStemp);  
                OwnLog.i(TAG, sportData.toString());
				int tmp = sportData.getDuration();
				if(tmp < 0 )
				{
					sportData.state = 0;
					sportData.duration = 31;
//					sportData.stepsPart =  LPUtil.makeShort((byte)(data[i+6]&0x3F), (byte)data[i+7]);
//					sportData.distancePart = sportData.getRefTime();
				}
				list.add(sportData);
			}
			
//			for( int i = 11; i < data.length-2-2; i += 6 ) {
//				LPSportData sportData = new LPSportData();
			//	sportData.srcData = dataSrc ;
//				if(((data[i+4] & 0xFF) & 0x00) == 0x00)  
//			 if((data[i+4]&0x00) == 0x00)
//				{
//					System.out.println("-----------------原始数据部署--------------------------");
//					sportData.setRefTime(LPUtil.makeShort(data[i+1], data[i]));
//					sportData.setState(data[i+2]&0xFF);
//					sportData.setSteps(data[i+3]&0xFF);
//					sportData.setDistance(data[i+4]&0xFF);
//					sportData.setDuration( LPUtil.makeShort((byte) (data[i+7]), data[i+6]) - sportData.getRefTime());
//					sportData.setDataLen(len);
//					sportData.setRevLen(this.len);
//					OwnLog.d(TAG, ".........................收到数据的长度:"+this.len+".........................");
//					sportData.setTimeStemp(timeStemp);
//					
//					if( sportData.getState() == LPSportData.STATE_IDLE) {
//						sportData.setSteps(  data[i+3] & 0xFF );                //14
//						sportData.setDistance( ( data[i+4] & 0xFF ) * 16 );     //15
////						sportData.setStepsPart( (data[i+5] & 0x0F)*4);          //16
////						sportData.setDistancePart( ( data[i+6] & 0xFF ) * 4 );
//					}
//					else {
//						sportData.setDistance( LPUtil.makeShort( data[i+2], data[i+3] ) );
//						sportData.setSteps( LPUtil.makeShort( (byte) (data[i+4]&0x0F), data[i+5] ) );
//						sportData.setDistancePart( -1 );
//						sportData.setStepsPart( -1 );
//					}				
//				}
//				else
//				{
//					sportData.setRefTime(LPUtil.makeShort((byte)(data[i]&0x3F), data[i+1]));
//					sportData.setState( ( (data[i]&0xFF) >> 6 ) & 0x03 ); 
//					System.out.println("-----------------原始数据部署--------------------------");
//					
//					sportData.setDuration( LPUtil.makeShort((byte) (data[i+6]&0x3F), data[i+7]) - sportData.getRefTime());
//					sportData.setDataLen(len);
//					sportData.setRevLen(this.len);
//					OwnLog.d(TAG, ".........................收到数据的长度:"+this.len+".........................");
//					sportData.setTimeStemp(timeStemp);
//					
//					if( sportData.getState() == LPSportData.STATE_IDLE) {
//						sportData.setSteps(  data[i+2] & 0xFF );
//						sportData.setDistance( ( data[i+3] & 0xFF ) * 16 );
//						sportData.setStepsPart( data[i+4] & 0xFF );
//						sportData.setDistancePart( ( data[i+5] & 0xFF ) * 16 );
//					}
//					else {
//						sportData.setDistance( LPUtil.makeShort( data[i+2], data[i+3] ) );
//						sportData.setSteps( LPUtil.makeShort( data[i+4], data[i+5] ) );
//						sportData.setDistancePart( -1 );
//						sportData.setStepsPart( -1 );
//					}
//				}
//				OwnLog.d(TAG, sportData.toString());
//				
//				int tmp = sportData.getDuration();
//				
//				if(tmp < 0 || tmp > 14430)
//				{
//					sportData.state = 0;
//					sportData.duration = 31;
//					sportData.stepsPart =  LPUtil.makeShort((byte) (data[i+6]&0x3F), data[i+7]);
//					sportData.distancePart = sportData.getRefTime();
//				}
//				
//				list.add(sportData);
//			}
			
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
		
		public boolean toOpenSmartCardOK( byte cmd ,String cmdName)throws LPException
		{
			checkResponseOK(cmd, cmdName);
			if(!(data[4] == 0x00))
			{
				LPException  ex = new LPException( LPErrCode.LP_HARDWARE_ERR );
				ex.addMsg( "type", "OpenSmartCard" );
				ex.addMsg( "len", String.valueOf(len) );
				if( len > 0 ) {
					ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
				}
				throw  ex;
			}
			return true;
		}
		
		public boolean toCloseSmartCardOK( byte cmd ,String cmdName)throws LPException
		{
			checkResponseOK(cmd, cmdName);
			if(!((data[4] == 0x01) && (data[5] == 0x00)))
			{
				LPException  ex = new LPException( LPErrCode.LP_HARDWARE_ERR );
				ex.addMsg( "type", "CloseSmartCard" );
				ex.addMsg( "len", String.valueOf(len) );
				if( len > 0 ) {
					ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
				}
				throw  ex;
			}
			return true;
		}
		
		public boolean toAIDSmartCardOK( byte cmd ,LPDeviceInfo deviceInfo,String cmdName)throws LPException
		{//91,0,61,1D
			
			String hex2= Integer.toHexString((data[5] & 0xFF));
			OwnLog.e(TAG, "ssssssssssssssss"+hex2);
			if(deviceInfo.customer.equals(LPDeviceInfo.ZHENGYUAN) ){
				
				 if(!( data[3] == cmd && data[4] == 0x00  && data[5] == (byte)0x90) )
					{
						return false;
					}
				 
			}else{
				
				 if(!( data[3] == cmd && data[4] == 0x00  && data[5] == (byte)0x61) )
					{
					 	return false;
					}
			}
			
			return true;
		}
		
		public boolean tocheckAID_stepOK( byte cmd ,String cmdName)throws LPException
		{
			
			if(!( data[3] == cmd && data[4] == 0x00  && data[5] == (byte)0x61) )
			{
				LPException  ex = new LPException( LPErrCode.LP_HARDWARE_ERR );
				ex.addMsg( "type", "tocheckAID_stepOK" );
				ex.addMsg( "len", String.valueOf(len));
				if( len > 0 ) {
					ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
				}
				throw  ex;
			}
			return true;
		}
		
		public boolean tocheckPINOK( byte cmd , String cmdName )throws LPException
		{//91,0,61,1D
			
			String hex2= Integer.toHexString((data[5] & 0xFF));
			if(!( data[3] == cmd && data[4] == 0x00  && data[5] == (byte)0x90 && data[6] == (byte)0x00) )
			{
				LPException  ex = new LPException( LPErrCode.LP_HARDWARE_ERR );
				ex.addMsg( "type", "tocheckPINOK" );
				ex.addMsg( "len", String.valueOf(len));
				if( len > 0 ) {
					ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
				}
				throw  ex;
			}
			return true;
		}
		
		public boolean toClose7816CardOK( byte cmd ,String cmdName)throws LPException
		{
			checkResponseOK(cmd, cmdName);
			if(!( data[3] == cmd && data[4] == 0x00  && data[5] == (byte)0x90 && data[6] == (byte)0x00) )
			{
				return false;
			}
			return true;
		}
		
		public Integer toReadCardBanlance( byte cmd ,String cmdName,LPDeviceInfo deviceInfo)throws LPException
		{
			int balance;
			if(!(data[3] == cmd && data[4] == 0x00  && data[5] == (byte)0x90  && data[6] == 0x00)) //91,0,90,0
			{
				LPException  ex = new LPException( LPErrCode.LP_HARDWARE_ERR );
				ex.addMsg( "type", "toReadCardBanlance" );
				ex.addMsg( "len", String.valueOf(len) );
				if( len > 0 ) {
					ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
				}
				throw  ex ;
			}
			
			if(deviceInfo.customer.equals(LPDeviceInfo.HENGBAO_XIANJIN) 
					||deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_XJ) ||deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_JIEJI) ||deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_DAIJI)){
				StringBuffer sb = new StringBuffer();
				for(int i=10;i<16;i++){
					String hex = Integer.toHexString((data[i] & 0xFF));
					if (hex.length() == 1) { 
						hex = '0' + hex;
						}
					sb.append(hex);
				}
				balance=Integer.parseInt(sb.toString());
				
			}
			else if(deviceInfo.customer.equals(LPDeviceInfo.ZHENGYUAN))
			{
				balance=LPUtil.makeInt((byte)0x00,data[7],data[8],data[9]);
				OwnLog.e(TAG, "余额:"+balance);
				OwnLog.e(TAG, "data[7]:"+(data[7] & 0x80));
				 if ((data[7] & 0x80 ) ==128) {
					 balance |= 0xff000000;
					 balance = ~ balance ;
					 balance += 1;
					 balance = -balance;
				  }

			}
			else if(deviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA))
			{
				StringBuffer sb = new StringBuffer();
				for( int i=7 ; i < 21 ; i++ ){
					String hex = Integer.toHexString((data[i] & 0xFF));
					if (hex.length() == 1) { 
						hex = '0' + hex;
						}
					sb.append(hex);
				}
				balance=Integer.parseInt(sb.toString());
//				balance=0;
			}
			else if(deviceInfo.customer.equals(LPDeviceInfo.YUNNAN_FUDIAN))
			{
				StringBuffer sb = new StringBuffer();
				for( int i=10 ; i < 16 ; i++ ){
					String hex = Integer.toHexString((data[i] & 0xFF));
					if (hex.length() == 1) { 
						hex = '0' + hex;
						}
					sb.append(hex);
				}
				balance=Integer.parseInt(sb.toString());
//				balance=0;
			}
			else
			{
				balance=LPUtil.makeInt(data[7],data[8],data[9],data[10]);
			}
			return balance;
		}
		
		private void checkResponseOK(byte cmd,String cmdName) throws LPException
		{
			if( data[3] != cmd) {
				LPException  ex = new LPException( LPErrCode.LP_CONVERT_ERR );
				ex.addMsg( "type",cmdName );
				ex.addMsg( "len", String.valueOf(len) );
				if( len > 0 ) {
					ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
				}
				throw  ex;
			}
		}
		public LLTradeRecord toLLTradeRecord( byte cmd ,String cmdName,LPDeviceInfo deviceInfo)throws LPException
		{ 
			//  91,0,90,0,6A,83 
			if(data.length < 32)
				throw new LPException(LPErrCode.LP_ILLEGAL_DATA);
			checkResponseOK(cmd,cmdName);
			
			LLTradeRecord llTradeRecord = new LLTradeRecord();
			if(!(data[3] == cmd && data[4] == 0x00) ){
				llTradeRecord.setVaild(false);
				llTradeRecord.setHasRecord(false);
				OwnLog.e(TAG, "非法字符");
				return llTradeRecord;
			}
			else
			{
				String hex1 = Integer.toHexString((data[5] & 0xFF));
				String hex2= Integer.toHexString((data[6] & 0xFF));
				OwnLog.e(TAG, hex1+"  "+hex2);
                if(data[5] == (byte)0x6A && data[6] == (byte)0x83 ){
                	llTradeRecord.setHasRecord(false);
                	llTradeRecord.setVaild(false);
                	OwnLog.e(TAG, "no记录");
                	return llTradeRecord;
				}	
                llTradeRecord.setHasRecord(true);
				llTradeRecord.setVaild(true);
			}
			
			
			StringBuffer sb = new StringBuffer();
			if(deviceInfo.customer==LPDeviceInfo.YANGCHENG){
				
				int amount = LPUtil.makeInt(data[12], data[13], data[14], data[15]);
				if(amount<10){
					llTradeRecord.setTradeAmount("0.0"+amount%100);
				}else if(amount>=10 && amount<100){
					llTradeRecord.setTradeAmount("0."+ amount%100);
				}else{
					String amount_=amount+"";
					llTradeRecord.setTradeAmount(getString(amount_, ".", amount_.length()-2));
				}
	            sb.setLength(0);
	            
	            llTradeRecord.setTradeType(data[16]==(byte)0x02?"in":"out");
	            
	            StringBuffer sb1 = new StringBuffer();
	            
			    for(int i = 25;i < 29;i++)
			    {
			    	int d = data[i]&0xFF;
			    	String tmp;
			    	if(d < 10)
			    	{
			    		tmp = "0"+Integer.toHexString(d);
			    	}
			    	else
			    	{
			    		tmp = Integer.toHexString(d);
					}
			    	sb1.append(tmp);
			    }
	            
	            llTradeRecord.setTradeTime(sb1.toString());
	            
	            
			}else{
				//终端机编号
				for(int i=17;i<23;i++){
					String hex = Integer.toHexString((data[i] & 0xFF));
					if (hex.length() == 1) { 
						hex = '0' + hex;
						}
					sb.append(hex);
				}
				llTradeRecord.setTerminalNum(sb.toString());
				sb.setLength(0);
				//交易金额
				int amount = LPUtil.makeInt(data[12], data[13], data[14], data[15]);
				if(amount<10){
					llTradeRecord.setTradeAmount("0.0"+amount%100);
				}else if(amount>=10 && amount<100){
					llTradeRecord.setTradeAmount("0."+ amount%100);
				}else{
					String amount_=amount+"";
					llTradeRecord.setTradeAmount(getString(amount_, ".", amount_.length()-2));
				}
	            sb.setLength(0);
	            
	            if(deviceInfo.customer.equals(LPDeviceInfo.HENGBAO_QIANBAO)){
	            	llTradeRecord.setTradeType(data[16]==(byte)0x02?"in":"out");
	            }else{
	            	//交易类型
	            	llTradeRecord.setTradeType(data[16]==(byte)0x01?"in":"out");
	            }
	            
	          //联机或脱机交易序号
				for(int i=7;i<9;i++){
					String hex = Integer.toHexString((data[i] & 0xFF));
					if (hex.length() == 1) { 
						hex = '0' + hex;
						}
					sb.append(hex);
				}
				llTradeRecord.setTradeNum(sb.toString());
				sb.setLength(0);
				
			    StringBuffer sb1 = new StringBuffer();
			    for(int i = 23;i < 30;i++)
			    {
			    	int d = data[i]&0xFF;
			    	String tmp;
			    	if(d < 10)
			    	{
			    		tmp = "0"+Integer.toHexString(d);
			    	}
			    	else
			    	{
			    		tmp = Integer.toHexString(d);
					}
			    	sb1.append(tmp);
			    }
				//交易时间戳
			    if(sb1.toString().equals("00000000000000")){
			    	llTradeRecord.setHasRecord(false);
					llTradeRecord.setVaild(false);
			    }
				llTradeRecord.setTradeTime(sb1.toString());
			}
			
			return llTradeRecord;
		}
		
		public LLXianJinCard XIANJIN_quancunTradeRecord( byte cmd ,String cmdName)throws LPException
		{ 
			//  91,0,90,0,6A,83 
			if(data.length < 32)
				throw new LPException(LPErrCode.LP_ILLEGAL_DATA);
			checkResponseOK(cmd,cmdName);
			
			LLXianJinCard xianjin = new LLXianJinCard();
			
			
			if(!(data[3] == cmd && data[4] == 0x00) ){
				xianjin.setVaild(false);
				xianjin.setHasRecord(false);
				OwnLog.e(TAG, "非法字符");
				return xianjin;
			}
			else{
				
				String hex1 = Integer.toHexString((data[5] & 0xFF));
				String hex2= Integer.toHexString((data[6] & 0xFF));
				OwnLog.e(TAG, hex1+"  "+hex2);
				
                if(data[5] == (byte)0x6A && data[6] == (byte)0x83 ){
                	xianjin.setHasRecord(false);
                	xianjin.setVaild(false);
                	OwnLog.e(TAG, "无纪律");
                	return xianjin;
				}	
                xianjin.setHasRecord(true);
                xianjin.setVaild(true);
			}
			//7位开始  15 9 25 0 0 0 0 0 0 0 0 10 0 0 0 0 0 0 1 56 1 56 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 f 
			StringBuffer sb = new StringBuffer();
			//Put Data 修改前的值:
			for(int i=9;i<15;i++)
			{
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) 
				{ 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			long putdata_before = Long.parseLong(sb.toString());
			sb.setLength(0);
			
			//Put Data 修改后的值:
			for(int i=15;i<21;i++)
			{
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) 
				{ 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			long putdata_after = Long.parseLong(sb.toString());
			sb.setLength(0);
			
			if(putdata_before > putdata_after){
				//圈提	
				//**交易类型*/
				xianjin.setTred_type_1("quanti");
				
				long amount = putdata_before-putdata_after;
				if(amount<10){
					xianjin.setXianjinAmount_6("0.0"+ amount%100 );
				}else if(amount>=10 && amount<100){
					xianjin.setXianjinAmount_6("0."+ amount%100 );
				}else{
					String amount_=amount+"";
					xianjin.setXianjinAmount_6(getString(amount_, ".", amount_.length()-2));
				}
				sb.setLength(0);
			}
			
			else{
				//**交易类型*/	
				xianjin.setTred_type_1("in");
				
				long amount = putdata_after-putdata_before;
//				OwnLog.e(TAG, amount+"");
				if(amount<10){
					xianjin.setXianjinAmount_6("0.0"+ amount%100 );
				}else if(amount>=10 && amount<100){
					xianjin.setXianjinAmount_6("0."+ amount%100 );
				}else{
					String amount_=amount+"";
					xianjin.setXianjinAmount_6(getString(amount_, ".", amount_.length()-2));
				}
				
				sb.setLength(0);
			}
			
			//日期
			for(int i=21;i<24;i++)
			{
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) 
				{ 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setData_3(sb.toString());
			sb.setLength(0);
			
			//交易时间
			for(int i=24;i<27;i++){
				
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
				
			}
			xianjin.setTime_3(sb.toString());
			OwnLog.e(TAG, "时间_:"+xianjin.getTime_3());
			
			sb.setLength(0);
//			
//			///**商户名称*/
//			for(int i=29;i<=48;i++){
//				String hex = Integer.toHexString((data[i] & 0xFF));
//				if (hex.length() == 1) { 
//					hex = '0' + hex;
//					}
//				sb.append(hex);
//			}
//			xianjin.setStore_name_20(sb.toString());
//			sb.setLength(0);
//			
//			///**计数器*/
//			for(int i=50;i<=51;i++){
//				String hex = Integer.toHexString((data[i] & 0xFF));
//				if (hex.length() == 1) { 
//					hex = '0' + hex;
//					}
//				sb.append(hex);
//			}
//			xianjin.setTred_count_2(sb.toString());
//			sb.setLength(0);
			return xianjin;
		}
		
		public LLXianJinCard XIANJIN_toLLTradeRecord( byte cmd ,String cmdName)throws LPException
		{ 
			//  91,0,90,0,6A,83 
			if(data.length < 32)
				throw new LPException(LPErrCode.LP_ILLEGAL_DATA);
			checkResponseOK(cmd,cmdName);
			
			LLXianJinCard xianjin = new LLXianJinCard();
			
			
			if(!(data[3] == cmd && data[4] == 0x00) ){
				xianjin.setVaild(false);
				xianjin.setHasRecord(false);
				OwnLog.e(TAG, "非法字符");
				return xianjin;
			}
			else{
				String hex1 = Integer.toHexString((data[5] & 0xFF));
				String hex2= Integer.toHexString((data[6] & 0xFF));
				OwnLog.e(TAG, hex1+"  "+hex2);
                if(data[5] == (byte)0x6A && data[6] == (byte)0x83 ){
                	xianjin.setHasRecord(false);
                	xianjin.setVaild(false);
                	OwnLog.e(TAG, "无纪律");
                	return xianjin;
				}	
                xianjin.setHasRecord(true);
                xianjin.setVaild(true);
			}
			//7位开始  15 9 25 0 0 0 0 0 0 0 0 10 0 0 0 0 0 0 1 56 1 56 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 f 
			StringBuffer sb = new StringBuffer();
			//交易日期
			for(int i=7;i<=9;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) 
				{ 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setData_3(sb.toString());
			sb.setLength(0);
			
			//交易时间
			for(int i=10;i<=12;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setTime_3(sb.toString());
			sb.setLength(0);
			
			//交易金额
			for(int i=13;i<=18;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			int amount = Integer.parseInt(sb.toString());
			if(amount<10){
				xianjin.setXianjinAmount_6("0.0"+ amount%100 );
			}else if(amount>=10 && amount<100){
				xianjin.setXianjinAmount_6("0."+ amount%100 );
			}else{
				String amount_=amount+"";
				xianjin.setXianjinAmount_6(getString(amount_, ".", amount_.length()-2));
			}
			sb.setLength(0);
			
			//其他金额
			for(int i=19;i<=24;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setXianjinAmount_other_6(sb.toString());
			sb.setLength(0);
			
			///**终端国家代码*/
			for(int i=25;i<=26;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
				}
				sb.append(hex);
			}
			xianjin.setCountry_2(sb.toString());
			sb.setLength(0);
			
			///**交易货币代码*/
			for(int i=27;i<=28;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setTredmoney_2(sb.toString());
			sb.setLength(0);
			
			///**商户名称*/
			for(int i=29;i<=48;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setStore_name_20(sb.toString());
			sb.setLength(0);
			
			{
			///**交易类型*/
			String hex = Integer.toHexString((data[49] & 0xFF));
			xianjin.setTred_type_1(hex.equals("1")?"out":"in");
			
			}
			
			sb.setLength(0);
			
			///**计数器*/
			for(int i=50;i<=51;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setTred_count_2(sb.toString());
			sb.setLength(0);
			
			
			return xianjin;
		}
		
		public LLXianJinCard HUBEU_XIANJIN_toLLTradeRecord( byte cmd ,String cmdName)throws LPException
		{ 
			//  91,0,90,0,6A,83 
			if(data.length < 32)
				throw new LPException(LPErrCode.LP_ILLEGAL_DATA);
			checkResponseOK(cmd,cmdName);
			
			LLXianJinCard xianjin = new LLXianJinCard();
			
			
			if(!(data[3] == cmd && data[4] == 0x00) ){
				xianjin.setVaild(false);
				xianjin.setHasRecord(false);
				OwnLog.e(TAG, "非法字符");
				return xianjin;
			}
			else
			{
				String hex1 = Integer.toHexString((data[5] & 0xFF));
				String hex2= Integer.toHexString((data[6] & 0xFF));
				OwnLog.e(TAG, hex1+"  "+hex2);
                if(data[5] == (byte)0x6A && data[6] == (byte)0x83 ){
                	
                	xianjin.setHasRecord(false);
                	xianjin.setVaild(false);
                	OwnLog.e(TAG, "无纪律");
                	return xianjin;
				}	
                xianjin.setHasRecord(true);
                xianjin.setVaild(true);
			}
			//7位开始  15 9 25 0 0 0 0 0 0 0 0 10 0 0 0 0 0 0 1 56 1 56 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 f 
			StringBuffer sb = new StringBuffer();
			//交易日期
			for(int i=7;i<=9;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) 
				{ 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setData_3(sb.toString());
			sb.setLength(0);
			
			//交易时间
			for(int i=10;i<=12;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setTime_3(sb.toString());
			sb.setLength(0);
			
			//交易金额
			for(int i=13;i<=18;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			int amount = Integer.parseInt(sb.toString());
			if(amount<10){
				xianjin.setXianjinAmount_6("0.0"+ amount%100 );
			}else if(amount>=10 && amount<100){
				xianjin.setXianjinAmount_6("0."+ amount%100 );
			}else{
				String amount_=amount+"";
				xianjin.setXianjinAmount_6(getString(amount_, ".", amount_.length()-2));
			}
			sb.setLength(0);
			
			//其他金额
			for(int i=19;i<=24;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setXianjinAmount_other_6(sb.toString());
			sb.setLength(0);
			
			///**终端国家代码*/
			for(int i=25;i<=26;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
				}
				sb.append(hex);
			}
			xianjin.setCountry_2(sb.toString());
			sb.setLength(0);
			
			///**交易货币代码*/
			for(int i=27;i<=28;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setTredmoney_2(sb.toString());
			sb.setLength(0);
			
			///**商户名称*/
			for(int i=29;i<=48;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setStore_name_20(sb.toString());
			sb.setLength(0);
			{
				///**交易类型*/
				String hex = Integer.toHexString((data[49] & 0xFF));
				xianjin.setTred_type_1(hex.equals("63")?"in":"out");
			}
			
			sb.setLength(0);
			
			///**计数器*/
			for(int i=50;i<=51;i++){
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
				sb.append(hex);
			}
			xianjin.setTred_count_2(sb.toString());
			sb.setLength(0);
			
			
			return xianjin;
		}
		
		public String toReadSchoolID( byte cmd ,String cmdName)throws LPException
		{
			if(!(data[3] == cmd && data[4] == 0x00  && data[5] == (byte)0x90  && data[6] == 0x00)) //91,0,90,0
			{
				LPException  ex = new LPException( LPErrCode.LP_HARDWARE_ERR );
				ex.addMsg( "type", "toReadSchoolID" );
				ex.addMsg( "len", String.valueOf(len) );
				if( len > 0 ) {
					ex.addMsg( "data", LPUtil.ubytesToString( data, 0, len ) );
				}
				throw  ex ;
			}
				int id = LPUtil.makeInt((byte)0x00,data[7], data[8], data[9]);
				
				
			return id+"";
		}
		
		public String toDeviceID() throws LPException
		{ //af 17 12 5f 0 52 4 0 0 60 2a c9 56 0 0 0 0 0 0 0 0 3 36 
			if (data[3] != LepaoCommand.COMMAND_GET_DEVICE_ID)
			{
				LPException ex = new LPException(LPErrCode.LP_CONVERT_ERR);
				ex.addMsg("type", "DeviceVersion");
				ex.addMsg("len", String.valueOf(len));
				if (len > 0)
				{
					ex.addMsg("data", LPUtil.ubytesToString(data, 0, len));
				}
				throw ex;
			}
			StringBuffer sb = new StringBuffer();
			for (int i = 6; i < 9; i++)
			{
				String hex = Integer.toHexString((data[i] & 0xFF));
				if (hex.length() == 1) 
					hex = '0' + hex;
				sb.append(hex);
			}
			return sb.toString();
		}
		
		
		
}
