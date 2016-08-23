package com.example.android.bluetoothlegatt.proltrol;

import android.bluetooth.BluetoothGattCharacteristic;

import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.proltrol.dto.LLTradeRecord;
import com.example.android.bluetoothlegatt.proltrol.dto.LLXianJinCard;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.example.android.bluetoothlegatt.proltrol.dto.LPUser;
import com.example.android.bluetoothlegatt.utils.OwnLog;
import com.example.android.bluetoothlegatt.utils.TimeZoneHelper;
import com.example.android.bluetoothlegatt.wapper.BLEWapper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class LepaoProtocalImpl implements LepaoProtocol {
	private static String TAG = LepaoProtocalImpl.class.getSimpleName();

	/** 从服务端拿到的UTC时间（单位：毫秒）（此时间已加上了从本地发起请求到服务端返回的网络及处理耗时 */
	// 设置到设备中的UTC时间目的是为了此时间独立于设备存在，从而最大程度保证当用户手机时间错乱时不影响数据的时间
	private long utcFromServerForBound = 0;
	private long utcFromServerAtLocalTimeBound = 0;

	// ------->OAD<------
	// BLE
	private BluetoothGattCharacteristic mCharBlock = null;
	private BLEWapper mBleWapper;

	private boolean mServiceOk = false;
	
	public static int OAD_percent=0;
	public int OAD_count=0;

	private boolean continue_oad=false;  //可以继续OAD的
	public long oad_start;

	public static int getOAD_percent() {
		return OAD_percent;
	}
	

	public static void setOAD_percent(int oAD_percent) {
		OAD_percent = oAD_percent;
	}

	public LepaoProtocalImpl() {

	}

	private int retryTimes = 0;
	//最大重新发送次数
	private final int MAX_RETRY_TIMES = 1;

	private byte seq = 1;

	public static int makeShort(byte b1, byte b2) {
		return (int) (((b1 & 0xFF) << 8) | (b2 & 0xFF));
	}

	public static byte[] intto2byte(int a) {
		byte[] m = new byte[2];
		m[0] = (byte) ((0xff & a));
		m[1] = (byte) (0xff & (a >> 8));
		return m;
	}
	//OAD用
	private synchronized byte[] sendCommandNew(byte[] data, int status,boolean isoad) throws BLException, LPException {
		retryTimes = 0;
		byte[] resp = null;
		if (isoad) {
			mBleWapper = BLEWapper.getInstence();
			mCharBlock = mBleWapper.mOad_BLOCK_REQUEST;
			if (status == BLEWapper.OAD_HEAD) {
				resp = mBleWapper.writeCharacteristic(mCharBlock, data, status);
			} else if (status == BLEWapper.OAD_ALL) {
				resp = mBleWapper.writeCharacteristic(mCharBlock, data, status);
			}
			return resp;
		} else {
			
			try {
				resp = sendOnce(data, status);
				retryTimes = 0;
				return resp;
			} catch (BLException e) {
				OwnLog.e("KILL", e.getMessage());
				throw e;
			} catch (LPException e) {
				OwnLog.e("KILL", e.getMessage());
				throw e;
			}
				
		}
	}
	//OAD用
	private byte[] sendOnce(byte[] data, int status)throws LPException, BLException {
		try {
			byte[] resp = BLEWapper.getInstence().send(data, status);
			retryTimes = 0;
			return resp;
		} catch (BLException e) {
			if (retryTimes++ >= 1)
				throw e;
			OwnLog.e(TAG, "sendOnceNew retry:" + retryTimes);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return sendOnce( data, status) ;
		}
	}
	//OAD用
	private synchronized byte[] sendOADCommand(byte[] data, int status,boolean isoad) throws BLException, LPException {
		retryTimes = 0;
		byte[] resp = null;
		if (isoad) {
			mBleWapper = BLEWapper.getInstence();
			mCharBlock = mBleWapper.mOad_BLOCK_REQUEST;
			if (status == BLEWapper.OAD_HEAD) {
				OwnLog.i(TAG, ".................BLEWapper.OAD_HEAD................");
				resp = mBleWapper.writeCharacteristic(mCharBlock, data, status);
			} else if (status == BLEWapper.OAD_ALL) {
				resp = mBleWapper.writeCharacteristic(mCharBlock, data, status);
			}
			return resp;
		}
		return resp; 
	}

	/**
	 * 现用的发送方法
	 * */
	private synchronized WatchResponse sendData2BLE(WatchRequset req) throws BLException, LPException {
		try {
			WatchResponse resp = sendData2BLEImpl(req);
			retryTimes = 0;
			return resp;
		} catch (BLException e) {
			OwnLog.e("KILL", e.getMessage());
			throw e;
		} catch (LPException e) {
			OwnLog.e("KILL", e.getMessage());
			throw e;
		}
	}

	private WatchResponse sendData2BLEImpl(WatchRequset req) throws LPException, BLException {
		try {
			byte[] resp = BLEWapper.getInstence().send(req.getData(),0);
			retryTimes = 0;
			return new WatchResponse(resp, 0, resp.length);
		} catch (LPException e) {
			if(retryTimes++>MAX_RETRY_TIMES) throw e;
			OwnLog.e(TAG, "retry:"+retryTimes);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return sendData2BLEImpl( req);
		}catch (BLException e) {
			if(retryTimes++>MAX_RETRY_TIMES) throw e;
			OwnLog.e(TAG, "retry:"+retryTimes);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return sendData2BLEImpl(req);
		}
	}

	// 获取设备信息
	@Override
	public LPDeviceInfo getAllDeviceInfoNew() throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		byte ff = (byte) 0xff;
		byte _00=(byte) 0x00;
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_GET_ALL_USER_INFO).appendByte(ff).makeCheckSum();
		LPUtil.printData(req.getData(), " getAllDeviceInfoNew");
		WatchResponse resp = this.sendData2BLE(req);
		if(resp.getData()[4]==0){
			return resp.toDeviceInfo(LepaoCommand.COMMAND_GET_ALL_USER_INFO,"getAllDeviceInfoNew");
		}else{
			return null;
		}
	}
	
	//获取 modelname
	public LPDeviceInfo getModelName() throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_DEVICEINFO).makeCheckSum();
		LPUtil.printData(req.getData(), " getModelName");
		WatchResponse resp = this.sendData2BLE(req);
		LPUtil.printData(resp.getData(), " getModelName接收");
		if(resp.getData()[4]==0){
			return resp.toModelName(LepaoCommand.COMMAND_DEVICEINFO,"getDeviceInfo");
		}else{
			LPDeviceInfo lpDeviceInfo = new LPDeviceInfo();
			return lpDeviceInfo;
		}
	}

	// 设置用户信息
	@Override
	public int registerNew(LPUser userInfo) throws BLException,
			LPException {
		// 注册之前先清空数据，这个交给上层App控制
		// clearSportData();
		// 后28位有效
		int userId = userInfo.userId;
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_REGISTER)
		        .appendByte((byte) 0)
				.appendByte((byte) userInfo.sex)
				.appendByte((byte) userInfo.age)
				.appendByte((byte) userInfo.height)
				.appendByte((byte) userInfo.weight)
				.appendByte((byte) 1)
				.appendInt(userId)
				.makeCheckSum();
//		OwnLog.i(TAG, "性别："+userInfo.sex+"---年龄："+userInfo.age);
//		OwnLog.i(TAG, "身高："+userInfo.height+"---体重："+userInfo.weight);
//		OwnLog.i(TAG, "id："+userId);
		WatchResponse resp = this.sendData2BLE(req);
		LPUtil.printData(req.getData(), "registerNew");
		if(resp.getData()[4]==1)
			return BLEProvider.INDEX_REGIESTER_INFO_NEW;
		return BLEProvider.INDEX_REGIESTER_INFO_NEW_FAIL;
	}

	// 设置设备时间
	@Override
	public int setDeviceTime() throws BLException, LPException {
		
		int zoneOffset  = TimeZoneHelper.getTimeZoneOffsetMinute();
		int zone = zoneOffset / 60; // 时区偏移值
		// 最大程度保证要设到设备里的时间是服务端的UTC（避免手机未被正确设置时间）     
//		long timestampWillBeSetToDevice = getServerUTCTimestampAuto();  
		long timestampWillBeSetToDevice = System.currentTimeMillis();
//		c.setTimeInMillis(Long.valueOf(timestampWillBeSetToDevice / 1000 * 1000));
//		OwnLog.i(TAG,"要设置到设备里的时间long值是" + (int)timestampWillBeSetToDevice + "--->"+ c.getTime());
		WatchRequset req = new WatchRequset();
		     req.appendByte(seq++)
				.appendByte(LepaoCommand.COMMAND_CLEAR)
				.appendInt((int) (timestampWillBeSetToDevice > 0 ? timestampWillBeSetToDevice / 1000 : (System.currentTimeMillis() / 1000)))
//				.appendInt((int)(System.currentTimeMillis() / 1000))
				.appendByte((byte) zone).makeCheckSum();
		LPUtil.printData(req.getData(), "setdevicetime");
		WatchResponse resp = this.sendData2BLE(req);
		if(resp.getData()[4]==1)
			return BLEProvider.INDEX_SET_DEVICE_TIME ;
		return BLEProvider.INDEX_SET_DEVICE_TIME_FAIL;
	}
	
	// 设置设备时间（包含特殊时区）
	public int setDeviceoffsetTime() throws BLException, LPException {
		int zoneOffset  = TimeZoneHelper.getTimeZoneOffsetMinute();
		int zone = zoneOffset / 6; // 时区偏移值
		// 最大程度保证要设到设备里的时间是服务端的UTC（避免手机未被正确设置时间）     
		long timestampWillBeSetToDevice = System.currentTimeMillis();
		WatchRequset req = new WatchRequset();
		     req.appendByte(seq++)
				.appendByte(LepaoCommand.COMMAND_SET_TIME_OFFEST)
				.appendInt((int) (timestampWillBeSetToDevice > 0 ? timestampWillBeSetToDevice / 1000 : (System.currentTimeMillis() / 1000)))
				.appendByte((byte) zone).makeCheckSum();
		LPUtil.printData(req.getData(), "settimeoffset");
		WatchResponse resp = this.sendData2BLE(req);
		if(resp.getData()[5]==0){
			return BLEProvider.INDEX_SET_DEVICE_TIME ;
		}else{
			return setDeviceTime();
		}
	}

	@Override
	public int resetSportDataNew(int step) throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++)
				.appendByte(LepaoCommand.COMMAND_SET_STEP)
				.appendInt(step)
				.makeCheckSum();
		LPUtil.printData(req.getData(), "重置步数");
		WatchResponse resp = this.sendData2BLE(req);
		if(resp.getData()[4]==1)
			return BLEProvider.INDEX_SEND_STEP;
		return BLEProvider.INDEX_SEND_STEP_FAIL;
	}

	// 获取运动数据
	@Override
	public List<LPSportData> getSportDataNew(int offset, int length,int detial)
			throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_GET_SPORT_RECORDER).appendByte((byte) offset)
		.appendByte((byte) length).makeCheckSum();
		LPUtil.printData(req.getData(), " sendSportDataNew");
		WatchResponse resp = this.sendData2BLE(req);
		LPUtil.printData(resp.getData(), " getSportDataNew");
		if(resp.getData()[4]==0){
			return resp.toLPSportDataList(detial);
		}else{
			List<LPSportData> list = new ArrayList<LPSportData>(); 
			return list;
		}
	}

	// test
	@Override
	public boolean formatDevice() throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		byte format =0;
		byte format_test =1;
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_FORMAT_DEVICE).appendByte(format).makeCheckSum();
		LPUtil.printData(req.getData(), "UnbondDevice");
		byte[] data = this.sendCommandNew(req.getData(), BLEWapper.NOT_OAD,false);
		seq=0x01;
		if(data[4]==1){
			return true;
		}else{
			return false;
		}
	}

	// test
	@Override
	public boolean setDeviceIdNew(String str) throws BLException, LPException {
		return mServiceOk;
	}
	
	// 获取OAD头文件的反馈
	public byte[] getOADHeadBack_new(byte[] data) throws BLException, LPException {
		byte[] resp = this.sendOADCommand(data, BLEWapper.OAD_HEAD, true);
		LPUtil.printData(data, "getOADHeadBack_new");
		if (resp[0] == 0x10) {
			return resp;
		} else {
			return resp;
		} 
	}

	// 获取OAD头文件的反馈
	@Override
	public int getOADHeadBack(byte[] data) throws BLException, LPException {
		OwnLog.i(TAG, ".................getOADHeadBack(oad_head)................");
		byte[] resp = this.sendOADCommand(data, BLEWapper.OAD_HEAD, true);
		LPUtil.printData(data, "OADHEAD");
		if (resp[0] == 0x10) {
			OwnLog.i(TAG, "我接受的BLEProvider.INDEX_SEND_OAD_HEAD");
			return BLEProvider.INDEX_SEND_OAD_HEAD;
		} else {
			OwnLog.i(TAG, "我接受的INDEX_SEND_OAD_HEAD_FAIL+");
			return BLEProvider.INDEX_SEND_OAD_HEAD_FAIL;
		}
		
	}

	private static byte[] oadmakeCheckSum(int i,int sum1, byte[] data1) {
		data1[i] =  (byte)( sum1 & 0x00FF );
		data1[i+1] =  (byte)( (sum1 >> 8) & 0x00FF );
		data1[i+2] =(byte)( (sum1 >> 16) & 0x00FF );
 		data1[i+3] =(byte)( (sum1 >> 24) & 0x00FF );
		return data1;
	}
	private static byte[] oadlen_16Sum(int i,int sum, byte[] data1) {
		data1[i] = (byte)( sum & 0x00FF );
 		data1[i+1] = (byte)( (sum >> 8) & 0x00FF );
		return data1;
	}
     /**
       * zkx
       * OAD 传输指令
       * 
     */
	@Override
	public int sendOADAll(byte[] data) throws BLException, LPException {
		
		boolean oad_succ = false;
		boolean can_oad = true;
		
		int len = data.length+16;
		byte[] oad_all = new byte[len];
		System.arraycopy(data, 0, oad_all, 0, data.length);
		int count = (data.length +15)/16 ;     //拼接的最大次数
		OwnLog.i(TAG, "发送的总次数:"+count);
		OAD_count=count;                        
		int OAD_error = 0;                    //超时次数
		int OAD_error_count = 3;              //最大超时次数
		int index = 0;                        //init index
		byte[] oad_block = new byte[20];      //每次发送的20个byte（block块）
		oad_block[0] = 0x10;                  //组装20个byte的第一位
		byte[] oad_block_head = new byte[2];  //将index转为OAD文件的头
		byte[] rec_data = new byte[3];        //接收到的OAD反馈  （0x10）
		
//		long start = System.currentTimeMillis();
		
		for(int j=index;j<count;){
			byte[] resp = null;
			oad_start = System.currentTimeMillis();
			for(int k = index;k<index+4;k++){
//				OwnLog.e(TAG, "接下来要发第:"+k+"个!");
					if((oad_all.length-k*16)>=16)
					    System.arraycopy(oad_all, k*16, oad_block, 3, 16);
					else
					{
//						OwnLog.i(TAG, "count的值："+count+"  以及 oad_all.length:"+oad_all.length);
//						OwnLog.i(TAG, "k的值："+k+"  以及 k*16:"+k*16);
						oad_block= new byte[oad_all.length - k*16 + 3];
						oad_block[0] = 0x10;
						System.arraycopy(oad_all, k*16, oad_block, 3, oad_all.length-k*16);
					}
					oad_block_head = intto2byte(k);
					oad_block[1] = (byte) (oad_block_head[0] & 0x00FF);
					oad_block[2] = (byte) (oad_block_head[1] & 0x00FF);
					
					setOAD_percent(k);
					resp = this.sendOADCommand(oad_block, BLEWapper.OAD_ALL, true); //fa song
					if(resp==null)
						return BLEProvider.INDEX_SEND_OAD_HEAD_FAIL;  //0810
					try {
						new Thread().sleep(25); 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if (resp != null && k!=count-1) {  //将接受到的反馈byte 给 rec_data
						rec_data = resp;
						if(System.currentTimeMillis() - oad_start >= 2500){
							
							OAD_error++;
							
						}else{
							
							OAD_error=0;
						}
					}
					
					if(k==count-1){
						oad_succ=true;
						break;
					}
			}
			
			if(oad_succ){   //OAD完成
//				OwnLog.i(TAG, "发送完成耗时:"+(System.currentTimeMillis()-start)/1000);
				setOAD_percent(OAD_percent);
				return BLEProvider.INDEX_SEND_OAD;
			}
			if(resp==null || resp.length<3)  //设备无返回信息
			{
				OwnLog.e(TAG, "无响应或者返回错误信息!"+resp.length);
				
				return BLEProvider.INDEX_SEND_OAD_HEAD_FAIL;  //0810
			}
				
			
			if(OAD_error>=OAD_error_count){
				OwnLog.e(TAG, "发送超时次数够大!");
				return BLEProvider.INDEX_SEND_OAD_HEAD_FAIL;  //0810
			}
				
			//再将返回的byte拼接成index(int)
			index = makeShort(rec_data[2], rec_data[1]);  
//			OwnLog.e(TAG, "收到请求要发第:"+index+"个!");
			
//			if(index<=j)
//				OwnLog.i(TAG, "重新发OAD块了:"+index);
		}
		
		
		return BLEProvider.INDEX_SEND_OAD;
	}
	
	 /**
     * zkx
     * OAD 传输指令
     * 
   */
	public byte[] sendOADback(byte[] data) throws BLException, LPException {
		
		boolean oad_succ = false;
		boolean can_oad = true;
		
		int len = data.length+16;
		byte[] oad_all = new byte[len];
		System.arraycopy(data, 0, oad_all, 0, data.length);
		int count = (data.length +15)/16 ;     //拼接的最大次数
		OwnLog.i(TAG, "发送的总次数:"+count);
		OAD_count=count;                        
		int OAD_error = 0;                    //超时次数
		int OAD_error_count = 3;              //最大超时次数
		int index = 0;                        //init index
		byte[] oad_block = new byte[20];      //每次发送的20个byte（block块）
		oad_block[0] = 0x10;                  //组装20个byte的第一位
		byte[] oad_block_head = new byte[2];  //将index转为OAD文件的头
		byte[] rec_data = new byte[3];        //接收到的OAD反馈  （0x10）
		
//		long start = System.currentTimeMillis();
		
		for(int j=index;j<count;){
			byte[] resp = null;
			oad_start = System.currentTimeMillis();
			for(int k = index;k<index+4;k++){
//				OwnLog.e(TAG, "接下来要发第:"+k+"个!");
					if((oad_all.length-k*16)>=16)
					    System.arraycopy(oad_all, k*16, oad_block, 3, 16);
					else
					{
//						OwnLog.i(TAG, "count的值："+count+"  以及 oad_all.length:"+oad_all.length);
//						OwnLog.i(TAG, "k的值："+k+"  以及 k*16:"+k*16);
						oad_block= new byte[oad_all.length - k*16 + 3];
						oad_block[0] = 0x10;
						System.arraycopy(oad_all, k*16, oad_block, 3, oad_all.length-k*16);
					}
					oad_block_head = intto2byte(k);
					oad_block[1] = (byte) (oad_block_head[0] & 0x00FF);
					oad_block[2] = (byte) (oad_block_head[1] & 0x00FF);
					
					setOAD_percent(k);
					resp = this.sendOADCommand(oad_block, BLEWapper.OAD_ALL, true); //fa song
					if(resp==null)
						return resp;  //0810
					try {
						new Thread().sleep(25); 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if (resp != null && k!=count-1) {  //将接受到的反馈byte 给 rec_data
						rec_data = resp;
						if(System.currentTimeMillis() - oad_start >= 2500){
							
							OAD_error++;
							
						}else{
							
							OAD_error=0;
						}
					}
					
					if(k==count-1){
						oad_succ=true;
						break;
					}
			}
			
			if(oad_succ){   //OAD完成
//				OwnLog.i(TAG, "发送完成耗时:"+(System.currentTimeMillis()-start)/1000);
				setOAD_percent(OAD_percent);
				return new byte[]{(byte) (0xff)};
			}
			if(resp==null || resp.length<3)  //设备无返回信息
			{
				OwnLog.e(TAG, "无响应或者返回错误信息!"+resp.length);
				
				return resp;
			}
				
			
			if(OAD_error>=OAD_error_count){
				OwnLog.e(TAG, "发送超时次数够大!");
				return resp;
			}
				
			//再将返回的byte拼接成index(int)
			index = makeShort(rec_data[2], rec_data[1]);  
//			OwnLog.e(TAG, "收到请求要发第:"+index+"个!");
			
//			if(index<=j)
//				OwnLog.i(TAG, "重新发OAD块了:"+index);
		}
		
		return new byte[]{(byte) (0xff)};
	}

    //闹钟
	@Override
	public int setClock(LPDeviceInfo deviceInfo) throws BLException,
			LPException {
		// COMMAND_SET_ALARM
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_SET_ALARM)
				.appendByte((byte) deviceInfo.alarmTime1_H)
				.appendByte((byte) deviceInfo.alarmTime1_M)
				.appendByte((byte) deviceInfo.frequency1)
				.appendByte((byte) deviceInfo.alarmTime2_H)
				.appendByte((byte) deviceInfo.alarmTime2_M)
				.appendByte((byte) deviceInfo.frequency2)
				.appendByte((byte) deviceInfo.alarmTime3_H)
				.appendByte((byte) deviceInfo.alarmTime3_M)
				.appendByte((byte) deviceInfo.frequency3).makeCheckSum();
		WatchResponse resp = this.sendData2BLE(req);
		LPUtil.printData(req.getData(), "setClock");
		if(resp.getData()[3]==LepaoCommand.COMMAND_SET_ALARM && resp.getData()[4]==1)
			return BLEProvider.INDEX_SET_DEVICE_CLOCK;
		return  BLEProvider.INDEX_SET_DEVICE_CLOCK_FAIL;
	}

	@Override
	public int setLongSitRemind(LPDeviceInfo deviceInfo)
			throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++)
				.appendByte(LepaoCommand.COMMAND_SET_MOTION_REMIND)
				.appendByte((byte) 60)
				.appendByte((byte) deviceInfo.longsit_step)
				.appendByte((byte) deviceInfo.startTime1_H)
				.appendByte((byte) deviceInfo.startTime1_M)
				.appendByte((byte) deviceInfo.endTime1_H)
				.appendByte((byte) deviceInfo.endTime1_M)
				.appendByte((byte) deviceInfo.startTime2_H)
				.appendByte((byte) deviceInfo.startTime2_M)
				.appendByte((byte) deviceInfo.endTime2_H)
				.appendByte((byte) deviceInfo.endTime2_M).makeCheckSum();
		WatchResponse resp = this.sendData2BLE(req);
		LPUtil.printData(req.getData(), "setLongSitRemind");
		if(resp.getData()[3]==LepaoCommand.COMMAND_SET_MOTION_REMIND && resp.getData()[4]==1)
			return BLEProvider.INDEX_SET_DEVICE_LONGSIT;
		return BLEProvider.INDEX_SET_DEVICE_LONGSIT_FAIL;
	}

	/** 运动目标 */
	@Override
	public LPDeviceInfo setSportTarget(LPDeviceInfo deviceInfo)
			throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_SET_TASK)
				.appendInt(deviceInfo.step).makeCheckSum();
		WatchResponse resp = this.sendData2BLE(req);
		LPUtil.printData(req.getData(), "setSportTarget");
		return null;
	}
	
	/** 省电模式*/
	public int setPower(LPDeviceInfo deviceInfo)throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_SET_POWER_DECICE)
				.appendByte((byte)deviceInfo.deviceStatus).makeCheckSum();
		LPUtil.printData(req.getData(), "setPower");
		WatchResponse resp = this.sendData2BLE(req);
		if(resp.getData()[3]==LepaoCommand.COMMAND_SET_POWER_DECICE && resp.getData()[4]==1)
			return BLEProvider.INDEX_POWER;
		return BLEProvider.INDEX_POWER_FAIL;
	}
	
	/** 获取卡号*/
	public String get_cardnum()throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_GET_CARD_NUMBER).makeCheckSum();
		LPUtil.printData(req.getData(), "get_cardnum");
		WatchResponse res = this.sendData2BLE(req);
		byte[] resp = res.getData();
		LPUtil.printData(resp, "get_cardnum接受");
		if(resp[4]==0 && resp.length > 7 ){
			String hex = null;
			StringBuffer sb = new StringBuffer();
			for(int i=5;i<resp.length-2;i++){
				hex = Integer.toHexString((resp[i] & 0xFF));
				if (hex.length() == 1) { 
					hex = '0' + hex;
					}
					sb.append(hex);
			}
			String index_all = sb.toString();
			String index_end = index_all.substring(index_all.length()-1, index_all.length());
			if(Integer.parseInt(index_end, 16)>=0x0a && Integer.parseInt(index_end, 16)<=0x0F){
				 index_all = index_all.substring(0,index_all.length()-1);
			}
			else if(index_all.startsWith(LPDeviceInfo.YUNNAN_FUDIAN) || index_all.startsWith(LPDeviceInfo.HUBEI_SHUMA) ||index_all.startsWith(LPDeviceInfo.SUZHOU_) ||index_all.startsWith(LPDeviceInfo.LIUZHOU_4) || index_all.startsWith(LPDeviceInfo.LIUZHOU_5)){//柳州  只取16位
				index_all = index_all.substring(0,16);
			}else if(index_all.startsWith(LPDeviceInfo.LINGNANTONG)){
				index_all = index_all.substring(0,index_all.length()-4);
			}
			return index_all;
		}
		else if(resp[4]==1 && resp[5]==1){
			//非支付
			return "NO_PAY";
		}
		return (BLEProvider.INDEX_GET_CARD_NUMBER_FAIL+"");
	}
	
	/** 设置名称*/
	public int set_name(LPDeviceInfo deviceInfo)throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		byte name_len = 6;
		byte[] name = null;
		try {
			name = LPUtil.stringtobyte(deviceInfo.userNickname,10);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_SET_NAME).appendByte(name_len);
		for(int i=0;i<name.length;i++){
			req.appendByte(name[i]);
		}
		if(name.length < 6){
			for(int i=0;i<6-name.length;i++){
				req.appendByte((byte)0);
			}
		}
		req.makeCheckSum();
		LPUtil.printData(req.getData(), "set_name");
		WatchResponse resp = this.sendData2BLE(req);
		if(resp.getData()[4]==1)
			return BLEProvider.INDEX_SET_NAME;
		
		return BLEProvider.INDEX_SET_NAME_FAIL;
	}
	
	 //手环震动
	public void setBandRing() throws BLException,LPException {
		// COMMAND_SET_ALARM
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_BAND_RING).makeCheckSum();
		this.sendData2BLE(req);
		LPUtil.printData(req.getData(), "setBandRing");
	}

	/** 抬手显示 */
	@Override
	public int setHandUp(LPDeviceInfo deviceInfo) throws BLException,LPException {
		// COMMAND_SetOledOnTimeZone
		WatchRequset req = new WatchRequset();
	    if(deviceInfo.handup_startTime2_H != 0){
	    	req.appendByte(seq++)
				.appendByte(LepaoCommand.COMMAND_SetOledOnTimeZone)
				.appendByte((byte) deviceInfo.handup_startTime1_H)
				.appendByte((byte) deviceInfo.handup_startTime1_M)
				.appendByte((byte) deviceInfo.handup_endTime1_H)
				.appendByte((byte) deviceInfo.handup_endTime1_M)
				.appendByte((byte) deviceInfo.handup_startTime2_H)
				.appendByte((byte) deviceInfo.handup_startTime2_M)
				.appendByte((byte) deviceInfo.handup_endTime2_H)
				.appendByte((byte) deviceInfo.handup_endTime2_M)
				.makeCheckSum();  
		}
	    else
	    {
	    	 req.appendByte(seq++)
				.appendByte(LepaoCommand.COMMAND_SetOledOnTimeZone)
				.appendByte((byte) deviceInfo.handup_startTime1_H)
				.appendByte((byte) deviceInfo.handup_startTime1_M)
				.appendByte((byte) deviceInfo.handup_endTime1_H)
				.appendByte((byte) deviceInfo.handup_endTime1_M).makeCheckSum();  
		}
		WatchResponse resp = this.sendData2BLE(req);
		LPUtil.printData(req.getData(), "setHandUp");
		if(resp.getData()[4]==1)
			return BLEProvider.INDEX_SET_HAND_UP;
		return BLEProvider.INDEX_SET_HAND_UP_FAIL;
	}
	
	/**消息提醒*/
	@Override
	public int setNotification(byte[] data) throws BLException,LPException {
		WatchRequset req = new WatchRequset();
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_SETNOTIFICATION).appendByte(data[0]).appendByte(data[1]).makeCheckSum();
		LPUtil.printData(req.getData(), "setNotification");
		WatchResponse resp = this.sendData2BLE(req);
		if(resp.getData()[4]==1)
			return BLEProvider.INDEX_SEND_NOTIFICATION;
		return BLEProvider.INDEX_SEND_NOTIFICATION_FAIL;
	}
	
	/**keepstate*/
	public void keepstate() throws BLException,LPException {
		WatchRequset req = new WatchRequset();
		byte keepstate = 0;
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_GET_DEVICE_ID).appendByte(keepstate).makeCheckSum();
		LPUtil.printData(req.getData(), "keepstate");
		this.sendData2BLE(req);
	}
	
	/**获取设备ID*/
	public String getDeviceId() throws BLException,LPException {
		WatchRequset req = new WatchRequset();
		byte keepstate = 0;
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_GET_DEVICE_ID).appendByte(keepstate).makeCheckSum();
		LPUtil.printData(req.getData(), "getDeviceId()");
		WatchResponse resp = this.sendData2BLE(req);
		return resp.toDeviceID();
		
	}

	/** 请求绑定 */
	@Override
	public int requestbound() throws BLException, LPException {
		WatchRequset req = new WatchRequset();
		byte[] resp;
		byte peidui=0;  //手表
		byte test=1;    //手环
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_REQUEST_BOUND).appendByte(peidui).makeCheckSum();
		LPUtil.printData(req.getData(), "REQUEST_BOUND");
		WatchResponse res = this.sendData2BLE(req);
		resp = res.getData();
		OwnLog.i(TAG, "resp------>"+resp.length);
		LPUtil.printData(resp, "接收到的绑定信息");
		StringBuffer sb = new StringBuffer();
		byte[] jianrong = new byte[18];
		sb.append("revice RAW Data[");
		if(resp.length==20){
			System.arraycopy(resp, 0, jianrong, 0, 18);
			for (byte b : jianrong) {
				sb.append(Integer.toHexString((b & 0xFF)));
			}
			sb.append("]");
		}
		OwnLog.i(TAG, "resp---content--->"+sb.toString());
		String mss = "revice RAW Data[af14"+(seq-1)+"1010000000000000]";
		if(mss.equals(sb.toString())){
			synchronized (LepaoProtocalImpl.class) {
				try {
					LepaoProtocalImpl.class.wait(30000);
				} catch (InterruptedException e) {
					return BLEProvider.INDEX_BOUND_FAIL;
				}
			}
	    	return BLEProvider.INDEX_BOUND_SUCCESS;
		}else{
			if( resp[5] == 1)
			{
				return BLEProvider.INDEX_BOUND_SUCCESS;
				
		    } else if(resp[5]==0) {  //继续发绑定命令
		    	OwnLog.i(TAG, "继续发绑定命令");
				return BLEProvider.INDEX_BOUND_GOON;
				
		    }else if(resp[5]== 0xff){
			//绑定失败
			    return BLEProvider.INDEX_BOUND_FAIL;
		    }
		}
		    return BLEProvider.INDEX_BOUND_FAIL;
	 };
	 
	 public int requestbound_fit() throws BLException, LPException {
			WatchRequset req = new WatchRequset();
			byte[] resp;
			byte peidui=2;  //手表
			req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_REQUEST_BOUND).appendByte(peidui).makeCheckSum();
			LPUtil.printData(req.getData(), "REQUEST_BOUND");
		 	WatchResponse res = this.sendData2BLE(req);
			resp = res.getData();
			OwnLog.i(TAG, "resp------>"+resp.length);
			LPUtil.printData(resp, "接收到的绑定信息(首次)");
				if( resp[5] == 1)
				{
					return BLEProvider.INDEX_BOUND_GOON;
					
			    }
				else if(resp[5] == 0) {  //继续发绑定命令
					
					return BLEProvider.INDEX_BOUND_NOCHARGE;
			    }
			    return BLEProvider.INDEX_BOUND_FAIL;
		 };
		 
		 public int requestbound_recy() throws BLException, LPException {
				WatchRequset req = new WatchRequset();
				byte[] resp;
				byte peidui=3;  //手表
				req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_REQUEST_BOUND).appendByte(peidui).makeCheckSum();
				LPUtil.printData(req.getData(), "REQUEST_BOUND");
			 	WatchResponse res = this.sendData2BLE(req);
				resp = res.getData();
				OwnLog.i(TAG, "resp------>"+resp.length);
				LPUtil.printData(resp, "接收到的绑定信息(循环)");
					if( resp[5] == 1)
					{
						return BLEProvider.INDEX_BOUND_SUCCESS;
				    }
					else if(resp[5]==0) {  //继续发绑定命令
				    	OwnLog.i(TAG, "继续发绑定命令");
						return BLEProvider.INDEX_BOUND_GOON;
						
				    }else if(resp[5]== 0xff){
					//绑定失败
					    return BLEProvider.INDEX_BOUND_FAIL;
				    }
				    return BLEProvider.INDEX_BOUND_FAIL;
			 };
	 
	 
	 
	
	

	@Override
	public int getFlashHeadBack(byte[] data) throws BLException, LPException {
		byte[] flash_head = new byte[17];
		int sum=0;
		for( int i = 0; i < data.length; ++i ){
			sum += ( data[i] & 0xFF );
		}
//		byte[] new_date=
//		flash_head[0]=0x20;
		flash_head[0]=0x10;
		flash_head=oadmakeCheckSum(1,sum,flash_head);              //checksum
		flash_head[5]=0;
		flash_head[6]=0;
		flash_head=oadlen_16Sum(7,(data.length+15)/16,flash_head);       //长度
		flash_head=oadmakeCheckSum(9 ,(140)*4096,flash_head);         //ASC12地址
//		flash_head=oadmakeCheckSum(9 ,128*4096,flash_head);              //uti12地址
//		flash_head=oadmakeCheckSum(9 ,141*4096,flash_head);            //hzk12地址 
		
		flash_head[13]=0;
		flash_head[14]=0;
		flash_head[15]=0;
		flash_head[16]=0;
		LPUtil.printData(flash_head, " FlashDeviceHead");
		byte[] resp = this.sendCommandNew(flash_head, BLEWapper.OAD_HEAD, true);
		if (resp[0] == 0x10) {
			OwnLog.i(TAG, "我接受的BLEProvider.INDEX_SEND_FLASH_HEAD");
			return BLEProvider.INDEX_SEND_FLASH_HEAD;

		} else {
			OwnLog.i(TAG, "我接受的INDEX_SEND_FLASH_HEAD_FAIL+" + resp.length);
			return BLEProvider.INDEX_SEND_FLASH_HEAD_FAIL;
		}
	}

	@Override
	public int getFlashBodyBack(byte[] data) throws BLException, LPException {
		int len = data.length ;
		byte[] oad_all = new byte[len+66];
		System.arraycopy(data, 0, oad_all, 0, len);
		for(int i =data.length;i<oad_all.length;i++){
			oad_all[i]=0;
		}
		int count = (data.length+15)/16;
		OwnLog.i(TAG, "count:"+count);
		byte[] new_data = { 0x00, 0x00 };
		int index = new_data[0];
		byte[] oad_block = new byte[20];
		oad_block[0] = 0x10;
		byte[] oad_block_head = new byte[2];
		byte[] rec_data = new byte[3];
		for(int j=index;j<count;){
			for(int k = index;k<index+4;k++){
				System.arraycopy(oad_all, k*16, oad_block, 3, 16);
				oad_block_head = intto2byte(k);
				oad_block[1] = (byte) (oad_block_head[0] & 0x00FF);
				oad_block[2] = (byte) (oad_block_head[1] & 0x00FF);
				LPUtil.printData(oad_block, "Flash");
				byte[] resp = this.sendCommandNew(oad_block, BLEWapper.OAD_ALL, true);
				if (resp != null) {
					rec_data = resp;
				}
				if(k==count){
					break;
				}
			}
			if(rec_data[0]==0x10){
				index = makeShort(rec_data[2], rec_data[1]);
			}else if(rec_data[0]==0x12 && rec_data[1]==0x0f){
				break;
			}
			OwnLog.i(TAG, "接下来要传的数据头：" + index);
		}
		OwnLog.i(TAG, "Flash成功！");
		return BLEProvider.INDEX_SEND_OAD;
	}
	
	// **********************for 一卡通相关的代码 START
	public boolean openSmartCard() throws LPException, BLException 
	{
		WatchRequset req = new WatchRequset();  
		byte open =1;
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_CONTROL_CARD).appendByte(open).makeCheckSum();
		LPUtil.printData(req.getData()," openSmartCard");
		WatchResponse resp = this.sendData2BLE(req);
		return resp.toOpenSmartCardOK(LepaoCommand.COMMAND_CONTROL_CARD, "openSmartCard");
	}
	
	/*********************************城市AID*****************************/
	public boolean AIDSmartCard(LPDeviceInfo deviceInfo) throws LPException, BLException 
	{
		WatchRequset req = new WatchRequset();  
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD);  
		if(deviceInfo.customer.equals(LPDeviceInfo.SUZHOU_)){              
				req.appendByte(Card_Command.SUZHOU_AID);
		}
		
		/***************************    大唐电信部分START    *******************************/
		else if(deviceInfo.customer.equals(LPDeviceInfo.DATANG_ZHENJIANG)){
				req.appendByte(Card_Command.DATANG_ZHENJIANG_AID);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.DATANG_JIAOTONG)){ 
			 	req.appendByte(Card_Command.DATANG_JIAOTONG_AID);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.DATANG_BEIJING)){ 
			 	req.appendByte(Card_Command.DATANG_BEIJING_AID);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.DATANG_HULIAN)){ 
				req.appendByte(Card_Command.DATANG_HULIAN_AID);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.DATANG_TUOCHENG)){ 
			req.appendByte(Card_Command.DATANG_ZHENJIANG_AID);
		}
		/***************************    大唐电信部分OVER    *******************************/
		
		
		else if(deviceInfo.customer.equals(LPDeviceInfo.LIUZHOU_4) || deviceInfo.customer.equals(LPDeviceInfo.LIUZHOU_5)){
				req.appendByte(Card_Command.LIUZHOU_AID);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.HENGBAO_QIANBAO) ){
				req.appendByte(Card_Command.HENGBAO_QIANBAO_AID);			
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.HENGBAO_XIANJIN) ){
		 		req.appendByte(Card_Command.HENGBAO_XIANJIN_AID);			
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.YANGCHENG) ){ 
				req.appendByte(Card_Command.YANGCHENG_AID);				
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_XJ) ){
			 	req.appendByte(Card_Command.MIANYANG_XJ_AID);		
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_JIEJI) ){
			 	req.appendByte(Card_Command.MIANYANG_JIEJI_AID);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_DAIJI) ){
			 	req.appendByte(Card_Command.MIANYANG_DAIJI_AID);	
			 	
		}else if(deviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA) ){
		 	req.appendByte(Card_Command.HUBEISHIXUN_AID);	
		 	
		}else if(deviceInfo.customer.equals(LPDeviceInfo.YUNNAN_FUDIAN) ){
		 	req.appendByte(Card_Command.YUNNANFUDIAN_AID);		
		}
		
		else if(deviceInfo.customer.equals(LPDeviceInfo.ZHENGYUAN) ){
				req.appendByte(Card_Command.ZHENGYUAN_AID);		
		}	
		req.makeCheckSum();
		LPUtil.printData(req.getData()," AIDSmartCard");
		WatchResponse resp = this.sendData2BLE(req);
		return resp.toAIDSmartCardOK(LepaoCommand.COMMAND_AID_CARD,deviceInfo, "AIDSmartCard");
	}	
	
	// AID 后续操作1  
	public boolean AID_step1() throws LPException, BLException 
	{
		WatchRequset req = new WatchRequset();  
				req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD).appendByte(Card_Command.AID_STEP1).makeCheckSum();
		LPUtil.printData(req.getData()," AID_step1");
		WatchResponse resp = this.sendData2BLE(req);
		return resp.tocheckAID_stepOK(LepaoCommand.COMMAND_AID_CARD, "AID_step1");
	}
	
	// AID 后续操作2 
	public boolean AID_step2() throws LPException, BLException 
	{
		WatchRequset req = new WatchRequset();  
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD).appendByte(Card_Command.AID_STEP2).makeCheckSum();
		LPUtil.printData(req.getData()," AID_step2");
		WatchResponse resp = this.sendData2BLE(req);
		return resp.tocheckAID_stepOK(LepaoCommand.COMMAND_AID_CARD, "AID_step2");
	}
	
	//检验PIN
	public boolean checkPin(LPDeviceInfo deviceInfo) throws LPException, BLException 
	{
		if(deviceInfo.customer.equals(LPDeviceInfo.DATANG_ZHENJIANG) ||deviceInfo.customer.equals(LPDeviceInfo.DATANG_JIAOTONG)||deviceInfo.customer.equals(LPDeviceInfo.DATANG_HULIAN) || deviceInfo.customer.equals(LPDeviceInfo.DATANG_BEIJING) || deviceInfo.customer.equals(LPDeviceInfo.YANGCHENG) 
				||deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_XJ) || deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_JIEJI) || deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_DAIJI) || deviceInfo.customer.equals(LPDeviceInfo.DATANG_TUOCHENG))
		{
			return WatchResponse.CHECKPIN_OK;
			
		}else if(deviceInfo.customer.equals(LPDeviceInfo.LIUZHOU_4) || deviceInfo.customer.equals(LPDeviceInfo.LIUZHOU_5))
		{
			return WatchResponse.CHECKPIN_OK;
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.HENGBAO_QIANBAO) || deviceInfo.customer.equals(LPDeviceInfo.HENGBAO_XIANJIN))
		{
			return WatchResponse.CHECKPIN_OK;
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.ZHENGYUAN))
		{
			return WatchResponse.CHECKPIN_OK;
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA))
		{
			return WatchResponse.CHECKPIN_OK;
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.YUNNAN_FUDIAN))
		{
			return WatchResponse.CHECKPIN_OK;
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.SUZHOU_))
		{
			WatchRequset req = new WatchRequset();  
			req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD).appendByte(Card_Command.SUZHOU_CHECKPIN).makeCheckSum();
			LPUtil.printData(req.getData()," checkPin");
			WatchResponse resp = this.sendData2BLE(req);
			return resp.tocheckPINOK(LepaoCommand.COMMAND_AID_CARD, "checkPin");
		}
		return false;
	}
	
	
	
	//钱包 读余额  0x00,0x5C,0x00,0x01,0x00,0x04  
	public Integer readCardBalance(LPDeviceInfo deviceInfo) throws LPException, BLException 
	{
		WatchRequset req = new WatchRequset();  
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD);
		if(deviceInfo.customer.equals(LPDeviceInfo.SUZHOU_)){
				req.appendByte(Card_Command.SUZHOU_BALANCE);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.LIUZHOU_4) || deviceInfo.customer.equals(LPDeviceInfo.LIUZHOU_5)){  // 0x80,0x5C,0x00,0x02,0x00,0x04
			 	req.appendByte(Card_Command.LIUZHOU_BALANCE);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.DATANG_HULIAN) || deviceInfo.customer.equals(LPDeviceInfo.DATANG_BEIJING)
				|| deviceInfo.customer.equals(LPDeviceInfo.DATANG_ZHENJIANG) || deviceInfo.customer.equals(LPDeviceInfo.DATANG_JIAOTONG)
				|| deviceInfo.customer.equals(LPDeviceInfo.DATANG_TUOCHENG)){  //0x00,0x5C,0x00,0x02,0x00,0x04
				req.appendByte(Card_Command.DATANG_BEIJING_BALANCE);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.HENGBAO_QIANBAO)){  
			 	req.appendByte(Card_Command.HENGBAO_QIANBAO_BALANCE);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.HENGBAO_XIANJIN)){  
			 	req.appendByte(Card_Command.HENGBAO_XIANJIN_BALANCE);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.YANGCHENG)){  
			 	req.appendByte(Card_Command.YANGCHENG_BALANCE);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_XJ)||deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_DAIJI) 
				||deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_JIEJI) ){  
			 	req.appendByte(Card_Command.MIANYANG_BALANCE);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.ZHENGYUAN)){ //HUBEI_CARD
			 	req.appendByte(Card_Command.ZHENGYUAN_BALANCE);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA)){  
		 	req.appendByte(Card_Command.HUBEI_CARD);
		}
		else if(deviceInfo.customer.equals(LPDeviceInfo.YUNNAN_FUDIAN)){  
		 	req.appendByte(Card_Command.YUNNAN_FUDIAN_CARD);
		}
		req.makeCheckSum();
		LPUtil.printData(req.getData()," readCardBalance");  //OwnLog
		WatchResponse resp = this.sendData2BLE(req);
		return resp.toReadCardBanlance(LepaoCommand.COMMAND_AID_CARD, "readCardBalance",deviceInfo);
	}
	
	//钱包 交易记录
	public LLTradeRecord getSmartCardTradeRecord(int index,LPDeviceInfo deviceInfo) throws LPException, BLException
    {  //0,B2,01,5C,0,17
		 WatchRequset  req = new WatchRequset();
         req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD);
         if(deviceInfo.customer.equals(LPDeviceInfo.SUZHOU_) || deviceInfo.customer.equals(LPDeviceInfo.YANGCHENG)){
        	 req.appendByte((byte) 0x00)
        	 	.appendByte((byte) 0xB2)
        	 	.appendByte((byte) index)
        	 	.appendByte((byte) 0xC4)
        	 	.appendByte((byte) 0x00)
        	 	.appendByte((byte) 0x17); 
         }else if(deviceInfo.customer.equals(LPDeviceInfo.LIUZHOU_4) || deviceInfo.customer.equals(LPDeviceInfo.LIUZHOU_5)){  // 0x80,0x5C,0x00,0x02,0x00,0x04
        	 req.appendByte((byte) 0x00)
     	 		.appendByte((byte) 0xB2)
     	 		.appendByte((byte) index)
     	 		.appendByte((byte) 0xC4)
     	 		.appendByte((byte) 0x00)
     	 		.appendByte((byte) 0x17); 
		} else if(deviceInfo.customer.equals(LPDeviceInfo.DATANG_BEIJING) || deviceInfo.customer.equals(LPDeviceInfo.DATANG_HULIAN)
				|| deviceInfo.customer.equals(LPDeviceInfo.DATANG_JIAOTONG) || deviceInfo.customer.equals(LPDeviceInfo.DATANG_ZHENJIANG) || deviceInfo.customer.equals(LPDeviceInfo.DATANG_TUOCHENG) ){
        	 //暂未实现
        	 req.appendByte((byte) 0x00)
             	.appendByte((byte) 0xB2)
             	.appendByte((byte) index)
             	.appendByte((byte) 0xC4)
             	.appendByte((byte) 0x00)
             	.appendByte((byte) 0x17)
             	;
         }else if(deviceInfo.customer.equals(LPDeviceInfo.HENGBAO_QIANBAO)){
        	 req.appendByte((byte) 0x00)
        	 	.appendByte((byte) 0xB2)
        	 	.appendByte((byte) index)
        	 	.appendByte((byte) 0xC4)
        	 	.appendByte((byte) 0x00)
        	 	.appendByte((byte) 0x17);
         }
//         else if(deviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA)){
//        	 //00B2015C00
//        	 req.appendByte((byte) 0x00)
//        	 	.appendByte((byte) 0xB2)
//        	 	.appendByte((byte) index)
//        	 	.appendByte((byte) 0xC4)
//        	 	.appendByte((byte) 0x00)
//        	 	.appendByte((byte) 0x17);
//         }
         req.makeCheckSum();
         LPUtil.printData(req.getData()," getSmartCardTradeRecord");
		WatchResponse resp = this.sendData2BLE(req);
         return  resp.toLLTradeRecord(LepaoCommand.COMMAND_AID_CARD, "getSmartCardTradeRecord",deviceInfo);
    }
	
	//恒宝企业专用  (现 加入湖北数码视讯 )
	//现金 交易记录(消费)
	public LLXianJinCard XIANJIN_getSmartCardTradeRecord(int index,LPDeviceInfo deviceInfo) throws LPException, BLException
    {  //0,B2,01,5C,0,17
				 WatchRequset  req = new WatchRequset();
				 req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD);
				 
				 if(deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_XJ) || deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_JIEJI) || deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_DAIJI)){
					 req.appendByte((byte) 0x00)
			        	.appendByte((byte) 0xB2)
			        	.appendByte((byte) index)
			        	.appendByte((byte) 0x5C)
			        	.appendByte((byte) 0x00)
			        	.appendByte((byte) 0x2D);
				 }else if(deviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA)){
					 //00B2 01 5C00
					 req.appendByte((byte) 0x00)
				    	.appendByte((byte) 0xB2)
				    	.appendByte((byte) index)
				    	.appendByte((byte) 0x5C)
				    	.appendByte((byte) 0x00)
				    	.appendByte((byte) 0x2D);
				 }
				 else if(deviceInfo.customer.equals(LPDeviceInfo.YUNNAN_FUDIAN)){
					 //00B2 01 5C00
					 req.appendByte((byte) 0x00)
				    	.appendByte((byte) 0xB2)
				    	.appendByte((byte) index)
				    	.appendByte((byte) 0x5C)
				    	.appendByte((byte) 0x00)
				    	.appendByte((byte) 0x2D);
				 }
				 else
				 {   //00B2 015C 00
			         req.appendByte((byte) 0x00)
				    	.appendByte((byte) 0xB2)
				    	.appendByte((byte) index)
				    	.appendByte((byte) 0x5C)
				    	.appendByte((byte) 0x00)
				    	.appendByte((byte) 0x2D);
			     }
				 req.makeCheckSum();
				 LPUtil.printData(req.getData()," XIANJIN_getSmartCardTradeRecord");
					WatchResponse resp = this.sendData2BLE(req);
				 if(deviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA) || deviceInfo.customer.equals(LPDeviceInfo.YUNNAN_FUDIAN)){
					 return  resp.HUBEU_XIANJIN_toLLTradeRecord(LepaoCommand.COMMAND_AID_CARD, "XIANJIN_getSmartCardTradeRecord");
				 }
			     return  resp.XIANJIN_toLLTradeRecord(LepaoCommand.COMMAND_AID_CARD, "XIANJIN_getSmartCardTradeRecord");
			     
			     
	}		
	//恒宝企业专用
	//现金 交易记录(圈存)
	public LLXianJinCard XIANJIN_getQuancunTradeRecord(int index,LPDeviceInfo deviceInfo) throws LPException, BLException
    {  //0,B2,01,5C,0,17
				 WatchRequset  req = new WatchRequset();
				 req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD);
//				 if(deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_XJ) || deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_JIEJI) || deviceInfo.customer.equals(LPDeviceInfo.MIANYANG_DAIJI)){
				 {   //00B2 015C 00
			         req.appendByte((byte) 0x00)
				    	.appendByte((byte) 0xB2)
				    	.appendByte((byte) index)
				    	.appendByte((byte) 0x64)
				    	.appendByte((byte) 0x00)
				    	.appendByte((byte) 0x2C);
			     }
				 req.makeCheckSum();
				 LPUtil.printData(req.getData()," XIANJIN_getQuancunTradeRecord");
				 WatchResponse resp = this.sendData2BLE(req);
			     return  resp.XIANJIN_quancunTradeRecord(LepaoCommand.COMMAND_AID_CARD, "XIANJIN_getQuancunTradeRecord");
	}
	//恒宝企业专用
	//学校代号
	public String School_ID() throws LPException, BLException
    {  
				 WatchRequset  req = new WatchRequset();
				 req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD);//00A6 0000 03
				 {   //00 B2 01 5C 00
					 //00 A6 00 00 03
			         req.appendByte((byte) 0x00)
				    	.appendByte((byte) 0xA6)
				    	.appendByte((byte) 0X00)
				    	.appendByte((byte) 0x00)
				    	.appendByte((byte) 0x00)
				    	.appendByte((byte) 0x03);
			     }
				 req.makeCheckSum();
				 LPUtil.printData(req.getData()," School_ID");
		WatchResponse resp = this.sendData2BLE(req);
			     return  resp.toReadSchoolID(LepaoCommand.COMMAND_AID_CARD, "School_ID");
	}	
	/**
	 * 关闭非接通道
	 * @return
	 * @throws LPException
	 * @throws BLException
	 */
	public boolean close7816Card() throws LPException, BLException 
	{
		WatchRequset req = new WatchRequset();  
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD).appendByte(Card_Command.YUNNAN_FUDIAN_CLOSE).makeCheckSum();
		if(openSmartCard()){
			LPUtil.printData(req.getData()," close7816Card");
			WatchResponse resp = this.sendData2BLE(req);
			return resp.toClose7816CardOK(LepaoCommand.COMMAND_AID_CARD, "close7816Card");
		}else{
			return false;
		}
	}
	
	/**
	 * 关闭非接通道
	 * @return
	 * @throws LPException
	 * @throws BLException
	 */
	public boolean open7816Card() throws LPException, BLException 
	{
		WatchRequset req = new WatchRequset();  
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_AID_CARD).appendByte(Card_Command.YUNNAN_FUDIAN_OPEN).makeCheckSum();
		if(openSmartCard()){
			LPUtil.printData(req.getData()," open7816Card");
			WatchResponse resp = this.sendData2BLE(req);
			return resp.toClose7816CardOK(LepaoCommand.COMMAND_AID_CARD, "open7816Card");
		}else{
			return false;
		}
		
	}
	
	public boolean closeSmartCard() throws LPException, BLException 
	{
		WatchRequset req = new WatchRequset();  
		byte close =0;
		req.appendByte(seq++).appendByte(LepaoCommand.COMMAND_CONTROL_CARD).appendByte(close).makeCheckSum();
		LPUtil.printData(req.getData()," closeSmartCard");
		WatchResponse resp = this.sendData2BLE(req);
		OwnLog.e(TAG, "关卡");
		return resp.toCloseSmartCardOK(LepaoCommand.COMMAND_CONTROL_CARD, "closeSmartCard");
		
	}
	
	
		public byte[] test_senddata1(byte[] data) throws BLException,LPException {
			WatchRequset req = new WatchRequset();
			byte[] resp = null;
			req.appendByte(seq++);
			for(int i=0 ; i < data.length ; i++){
				req.appendByte(data[i]);
			}
			req.makeCheckSum();
			LPUtil.printData(req.getData(), "==senddata==");
			resp = this.sendCommandNew(req.getData(), BLEWapper.NOT_OAD, false);
			LPUtil.printData(resp, "==recievedata==");
			byte[] resp_ = new byte[resp.length-5];
			//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
			System.arraycopy(resp, 3, resp_, 0, resp_.length);  
			return resp_;
//			return resp;
		}
	
	//测试透传
	public byte[] test_senddata(byte[] data) throws BLException,LPException {
		WatchRequset req = new WatchRequset(data.length);
		byte[] resp = null;
		req.appendByte(seq++);
		for(int i=0;i<data.length;i++){
			req.appendByte(data[i]);
		}
		req.makeCheckSum();
		LPUtil.printData(req.getData(), " test_senddata");
		resp = this.sendCommandNew(req.getData(), BLEWapper.NOT_OAD, false);
		byte[] resp_ = new byte[resp.length-5];
		//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
		System.arraycopy(resp, 3, resp_, 0, resp_.length);  
		return resp_;
	}
	
	//测试透传  yangcheng
	public byte[] senddata(byte[] data) throws BLException,LPException {
		
		WatchRequset req  = creat_data(data);
		
		byte[] resp = this.sendCommandNew(req.getData(), BLEWapper.NOT_OAD, false);
		
		//-----------去掉前3个（af.seq.len）   后2个checksum
		byte[] resp_data = new byte[resp.length-5];
		//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
		System.arraycopy(resp, 3, resp_data, 0, resp_data.length);
		//-----------去掉前3个（af.seq.len）   后2个checksum
		
		//出现异常 重新组装并且发送
		if(resp_data.length == 4 && resp_data[2] ==(byte)0x6C && resp_data[1]==(byte)0x00)
		{
			data[4] = resp_data[3];//sw2
			req = creat_data(data);
			
			byte[] resp_new = this.sendCommandNew(req.getData(), BLEWapper.NOT_OAD, false);
			
			//-----------去掉前3个（af.seq.len）   后2个checksum
			byte[] resp_new_data = new byte[resp_new.length-5];
			//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
			System.arraycopy(resp_new, 3, resp_new_data, 0, resp_new_data.length);
			//-----------去掉前3个（af.seq.len）   后2个checksum
			
			return resp_new_data;
		}
		return resp_data;
	}
	
	private WatchRequset creat_data(byte[] data) throws LPException, BLException {
		WatchRequset req = new WatchRequset(data.length+1); //加入了le的长度
		req.appendByte(seq++);
		req.appendByte(LepaoCommand.COMMAND_AID_CARD);
		//出来
		byte[] new_data = new byte[data.length+1];  //加入了le的长度
		if(data.length==5){
			//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
			System.arraycopy(data, 0, new_data, 0, 4); 
			new_data[4] =(byte)0x00;
			new_data[5] = data[4];
			
		}else if(data.length>5){
			//放入前5个
			System.arraycopy(data, 0, new_data, 0, 5); 
			new_data[5] = (byte) 0x00;
			System.arraycopy(data, 5, new_data, 6, data.length-5); 
		}
			
		for(int i=0;i<new_data.length;i++){
			req.appendByte(new_data[i]);
		}
		req.makeCheckSum();
		
		return req;
		
	}
	
	
	@Override
	public void ANCS_Other(byte type,byte sta,byte notificationUID,byte[] title, byte[] text) 
			throws BLException,LPException {
		WatchRequset req = new WatchRequset();  //测试QQ
		//title 24byte message 84byte 
		req.appendByte(seq++)
		.appendByte(LepaoCommand.COMMAND_ANCS_PUSH)
		//从这开始是  categoryID AppID EventID notificationUID 组装部分
		.appendByte(ANCSCommand.CategoryIDOther)             //除了电话以外
		.appendByte(type)                                     //APPID
		.appendByte(sta)                                      //状态：添加 0
		.appendByte(notificationUID);                         //初始是1   类似seq 
//		.appendByte(ANCSCommand.NotificationAttributeIDTitle)                                  //
//		.appendByte((byte) title.length);
//		for(int i=0;i<title.length;i++){
//			req.appendByte(title[i]);
//		}
		req.appendByte(ANCSCommand.NotificationAttributeIDMessage)//
		.appendByte((byte) text.length);
		for(int i=0;i<text.length;i++){
			req.appendByte(text[i]);
		}
		req.appendByte((byte) 0xff);
		req.makeCheckSum();
		LPUtil.printData(req.getData(), " 消息提醒");
		WatchResponse resp = this.sendData2BLE(req);
	}


	@Override
	public void ANCS_PHONE(byte Category_status,byte EventID_status, byte notificationUID,
			byte[] title, byte[] text) throws BLException, LPException {
		WatchRequset req = new WatchRequset(); 
		//title 24byte message 84byte 
		req.appendByte(seq++)
		.appendByte(LepaoCommand.COMMAND_ANCS_PUSH)
		//从这开始是  categoryID AppID EventID notificationUID 组装部分
		.appendByte(Category_status)                                  //接入电话 或者 
		.appendByte(ANCSCommand.ANCS_APPNameID_Phone)        //PhoneID
		.appendByte(EventID_status)                          //添加一个状态
		.appendByte(notificationUID)                         //初始是1   类似seq 
		.appendByte(ANCSCommand.NotificationAttributeIDTitle)
		.appendByte((byte) title.length);
		for(int i=0;i<title.length;i++){
			req.appendByte(title[i]);
		}
		req.appendByte(ANCSCommand.NotificationAttributeIDMessage)//
		.appendByte((byte) text.length);
		for(int i=0;i<text.length;i++){
			req.appendByte(text[i]);
		}
		req.appendByte((byte) 0xff);
		req.makeCheckSum();
		LPUtil.printData(req.getData(), " 来电提醒");
		WatchResponse resp = this.sendData2BLE(req);
	}
	//短信消息提醒
	public void ANCS_MSG(byte type,byte sta,byte notificationUID,byte[] title, byte[] text) 
			throws BLException,LPException {
		WatchRequset req = new WatchRequset();  
		//title 24byte message 84byte 
		req.appendByte(seq++)
		.appendByte(LepaoCommand.COMMAND_ANCS_PUSH)
		//从这开始是  categoryID AppID EventID notificationUID 组装部分
		.appendByte(ANCSCommand.CategoryIDSocial)             //短信
		.appendByte(type)                                    //APPID  2
		.appendByte(sta)                                      //状态：添加  0
		.appendByte(notificationUID)                          //初始是1   类似seq 
		.appendByte(ANCSCommand.NotificationAttributeIDTitle)                                  //
		.appendByte((byte) title.length);
		for(int i=0;i<title.length;i++){
			req.appendByte(title[i]);
		}
		req.appendByte(ANCSCommand.NotificationAttributeIDMessage)//
		.appendByte((byte) text.length);
		for(int i=0;i<text.length;i++){
			req.appendByte(text[i]);
		}
		req.appendByte((byte) 0xff);
		req.makeCheckSum();
		LPUtil.printData(req.getData(), " 消息提醒");
		WatchResponse resp = this.sendData2BLE(req);
	}
	
	//移除来电消息提醒
	public void ANCS_remove_incall(byte CategoryID,byte appid,byte notificationUID) 
			throws BLException,LPException {
		WatchRequset req = new WatchRequset();  
		req.appendByte(seq++)
		.appendByte(LepaoCommand.COMMAND_ANCS_PUSH)
		.appendByte(CategoryID)             //来电
		.appendByte(appid)         //APPID    2
		.appendByte(ANCSCommand.EventIDNotificationRemoved)         //状态：添加       0
		.appendByte(notificationUID);                               //初始是1   类似seq 
		req.appendByte((byte) 0xff);
		req.makeCheckSum();
		LPUtil.printData(req.getData(), " 移除来电消息提醒");
		WatchResponse resp = this.sendData2BLE(req);
	}
	
	//未接来电提醒
		public void ANCS_MISSCALL(byte notificationUID,byte[] title, byte[] text) 
				throws BLException,LPException {
			WatchRequset req = new WatchRequset();  
			req.appendByte(seq++)
			.appendByte(LepaoCommand.COMMAND_ANCS_PUSH)
			//从这开始是  categoryID AppID EventID notificationUID 组装部分
			.appendByte(ANCSCommand.CategoryIDMissedCall)             //
			.appendByte(ANCSCommand.ANCS_APPNameID_Phone)             //
			.appendByte(ANCSCommand.EventIDNotificationAdded)         //状态：添加  0
			.appendByte(notificationUID)                              //初始是1   类似seq 
			.appendByte(ANCSCommand.NotificationAttributeIDTitle)     //
			.appendByte((byte) title.length);
			for(int i=0;i<title.length;i++){
				req.appendByte(title[i]);
			}
			req.appendByte(ANCSCommand.NotificationAttributeIDMessage)//
			.appendByte((byte) text.length);
			for(int i=0;i<text.length;i++){
				req.appendByte(text[i]);
			}
			req.appendByte((byte) 0xff);
			req.makeCheckSum();
			LPUtil.printData(req.getData(), " 未接来电提醒");
			WatchResponse resp = this.sendData2BLE(req);
		}
}
