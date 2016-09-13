package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.android.bluetoothlegatt.exception.BLENotBounded;
import com.example.android.bluetoothlegatt.exception.BLENotEnabledException;
import com.example.android.bluetoothlegatt.exception.BLENotSupportException;
import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.proltrol.ANCSCommand;
import com.example.android.bluetoothlegatt.proltrol.LPException;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.example.android.bluetoothlegatt.proltrol.dto.LLTradeRecord;
import com.example.android.bluetoothlegatt.proltrol.dto.LLXianJinCard;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.example.android.bluetoothlegatt.proltrol.dto.LPUser;
import com.example.android.bluetoothlegatt.utils.OwnLog;
import com.example.android.bluetoothlegatt.wapper.BLEWapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;

public  class BLEProvider
{
	public static String TAG = BLEProvider.class.getSimpleName();
	int count=0;

	public static String BOUND_KEY = "bound";
	
	public static final int STATE_DISCONNECTED = 0;
	public static final int STATE_CONNECTED = STATE_DISCONNECTED + 1;
	public static final int STATE_DISCOVERED = STATE_CONNECTED + 1;
	public static final int STATE_CONNECTED_FAILED = STATE_DISCOVERED + 1;
	public static final int STATE_CONNECTING = STATE_CONNECTED_FAILED + 1;

	public static final int MSG_BLE_NOTSUPPORT = 0x10;
	public static final int MSG_BLE_NOT_ENABLED = MSG_BLE_NOTSUPPORT+1;
//	public static final int MSG_BLE_INIT_FAILED = MSG_BLE_NOT_ENABLED+1;
	
//	public static final int MSG_BLE_NOT_SCAN = MSG_BLE_NOT_ENABLED + 1;
	public static final int MSG_BLE_SCAN_TIME_OUT = MSG_BLE_NOT_ENABLED + 1;//MSG_BLE_NOT_SCAN + 1;
	public static final int MSG_BLE_NOT_CONNECT = MSG_BLE_SCAN_TIME_OUT + 1;
	public static final int MSG_BLE_CONNECT_FAILED = MSG_BLE_NOT_CONNECT + 1;
	public static final int MSG_BLE_CONNECT_LOST = MSG_BLE_CONNECT_FAILED + 1;
	public static final int MSG_BLE_CONNECT_SUCCESS = MSG_BLE_CONNECT_LOST + 1;
	public static final int MSG_BLE_DATA = MSG_BLE_CONNECT_SUCCESS + 1;
	// 本轮运动数据完全读取完成的消息（此消息不带任何数据，仅用于通知）
	public static final int MSG_BLE_DATA_END = MSG_BLE_DATA + 1;
	public static final int MSG_BLE_SPORT_DATA_PROESS = MSG_BLE_DATA_END + 1; 
	public static final int MSG_DATA_ERROR = MSG_BLE_SPORT_DATA_PROESS + 1;
	public static final int MSG_USER_ERROR = MSG_DATA_ERROR + 1;
	public static final int MSG_BOUND_DEVICE_FAILED = MSG_USER_ERROR + 1;
	public static final int MSG_BLE_ENERGY = MSG_BOUND_DEVICE_FAILED + 1;
	public static final int MSG_BLE_CONNECTING = MSG_BLE_ENERGY + 1;
	public static final int MSG_BLE_RSSI = MSG_BLE_CONNECTING + 1;
	public static final int MSG_BLE_CONNECTED = MSG_BLE_RSSI + 1;
	public static final int MSG_BLE_DEVICE = MSG_BLE_CONNECTED + 1;
	
	/** for 一卡通相关的指令代码 START **/
	public static final int INDEX_OPEN_SMC = MSG_BLE_DEVICE + 1;
	public static final int INDEX_AID_SMC = INDEX_OPEN_SMC + 1; 
	public static final int INDEX_AID_SMC_YC1 = INDEX_AID_SMC + 1; 
	public static final int INDEX_AID_SMC_YC2 = INDEX_AID_SMC_YC1 + 1; 
	public static final int INDEX_GET_SMC_BALANCE = INDEX_AID_SMC_YC2 + 1; 
	public static final int MSG_GET_SMC_TRADE_RECORD = INDEX_GET_SMC_BALANCE + 1;
	public static final int MSG_GET_SMC_TRADE_RECORD_ASYNC = MSG_GET_SMC_TRADE_RECORD + 1;
	public static final int INDEX_SET_SMC_TRANSFER = MSG_GET_SMC_TRADE_RECORD_ASYNC + 1;
	public static final int INDEX_CLOSE_SMC = INDEX_SET_SMC_TRANSFER + 1;
	public static final int INDEX_DEBUG_SMC = INDEX_CLOSE_SMC + 1;
	public static final int INDEX_AID_XIANJIN_SMC = INDEX_DEBUG_SMC + 1; 
	public static final int INDEX_XIANJIN_BALANCE = INDEX_AID_XIANJIN_SMC + 1; 
	public static final int INDEX_XIANJIN_NUMBER = INDEX_XIANJIN_BALANCE + 1; 
	public static final int INDEX_XIANJIN_RECORD = INDEX_XIANJIN_NUMBER + 1;
	public static final int MSG_GET__XIANJIN_TRADE_RECORD_ASYNC = INDEX_XIANJIN_RECORD + 1;	
	public static final int INDEX_SCHOOL_ID = MSG_GET__XIANJIN_TRADE_RECORD_ASYNC + 1;	
	public static final int INDEX_ClOSE_FEIJIE = INDEX_SCHOOL_ID + 1;	
	public static final int INDEX_OPEN_FEIJIE = INDEX_ClOSE_FEIJIE + 1;	
	/** for 一卡通相关的指令代码 END **/
	
	
	
	/** @deprecated */
	public static final int INDEX_GAT_ALL_INFO = 0x100;    //10:256
	/** @deprecated */
	public static final int INDEX_GET_TODAY_SPORT_DATA = INDEX_GAT_ALL_INFO + 1;
	/** 指令代码：获取日历史数据（ 1日1条，最多5条 ） */
	/** @deprecated */
	public static final int INDEX_GET_SPORT_RECORDER = INDEX_GET_TODAY_SPORT_DATA + 1;
	/** @deprecated */
	public static final int INDEX_SYNC_SPORT_DATA = INDEX_GET_SPORT_RECORDER + 1;
	/** @deprecated */
	public static final int INDEX_FACTORY_TEST = INDEX_SYNC_SPORT_DATA + 1;
	/** 设置device_id，本指令目前用于出厂测试程序中 */
	public static final int INDEX_SET_DEVICEID_NEW = INDEX_FACTORY_TEST + 1;
	/** 情求绑定的指令 */
	public static final int INDEX_REQUEST_BOUND = INDEX_SET_DEVICEID_NEW + 1;
	
	/** 情求绑定的指令_美国手环 */
	public static final int INDEX_REQUEST_BOUND_FIT = INDEX_REQUEST_BOUND + 1;
	/** 情求绑定的指令_美国手环(循环) */
	public static final int INDEX_REQUEST_BOUND_RECY = INDEX_REQUEST_BOUND_FIT + 1;
	//public static final int INDEX_GET_ENERGY = INDEX_SYNC_SPORT_DATA + 1;
	
	/** 指令代码：与设备的全流程同步指令（包括绑定检查、用户id绑定、设备设置信息是否与服务端一致等，并把实时数据和设备信息返回） */
	public static final int INDEX_SYNC_ALL_DEVICE_INFO = 0x1000;
	/** 指令代码：仅获取运动原始数据（1日N条） */
	public static final int INDEX_SYNC_SPORT_DATA_NEW = INDEX_SYNC_ALL_DEVICE_INFO + 1;
	/** 指令代码：单独设置设备中的配置信息指令 */
	/** @deprecated */
	public static final int INDEX_SETTING_ALL_DEVICE_INFO = INDEX_SYNC_SPORT_DATA_NEW + 1;
	/** 指令代码：绑定失败指令 */
	public static final int INDEX_BOUND_FAIL = INDEX_SETTING_ALL_DEVICE_INFO + 1;
	/** 指令代码：绑定失败(没充电)指令 */
	public static final int INDEX_BOUND_NOCHARGE = INDEX_BOUND_FAIL + 1;
	/** 指令代码：继续绑定指令 */
	public static final int INDEX_BOUND_GOON = INDEX_BOUND_NOCHARGE + 1;
	/** 指令代码：绑定成功指令 */
	public static final int INDEX_BOUND_SUCCESS = INDEX_BOUND_GOON + 1;
	/** 指令代码：解绑指令 */
	public static final int INDEX_UNBOUND_DEVICE = INDEX_BOUND_SUCCESS + 1;
	/** 指令代码：单独执行的0x13指令 */
	public static final int INDEX_GAT_ALL_INFO_NEW = INDEX_UNBOUND_DEVICE + 1;
	/** 指令代码：设备注册指令（不推荐单独使用） */
	public static final int INDEX_REGIESTER_INFO_NEW = INDEX_GAT_ALL_INFO_NEW + 1;
	/** 指令代码：设备注册指令fail（不推荐单独使用） */
	public static final int INDEX_REGIESTER_INFO_NEW_FAIL = INDEX_REGIESTER_INFO_NEW + 1;
	/** 指令代码：发送OAD头指令 */
	public static final int INDEX_SEND_OAD_HEAD = INDEX_REGIESTER_INFO_NEW_FAIL + 1;
	/** 指令代码：发送OAD头指令失败 */
	public static final int INDEX_SEND_OAD_HEAD_FAIL = INDEX_SEND_OAD_HEAD + 1;
	/** 指令代码：发送OAD指令 */
	public static final int INDEX_SEND_OAD= INDEX_SEND_OAD_HEAD_FAIL + 1;
	/** 指令代码：发送OAD完成数指令 */
	public static final int INDEX_SEND_OAD_PERCENT= INDEX_SEND_OAD + 1;
	/** 指令代码：发送OAD头指令(带返回类型的) */
	public static final int INDEX_SEND_OAD_HEAD_BACK= INDEX_SEND_OAD_PERCENT + 1;
	/** 指令代码：发送OAD指令   (带返回类型的) */
	public static final int INDEX_SEND_OAD_BACK= INDEX_SEND_OAD_HEAD_BACK + 1;
	/** 指令代码：发送设置手表时间指令 */
	public static final int INDEX_SET_DEVICE_TIME= INDEX_SEND_OAD_BACK + 1;
	/** 指令代码：发送设置手表时间指令FAIL */
	public static final int INDEX_SET_DEVICE_TIME_FAIL= INDEX_SET_DEVICE_TIME + 1;
	/** 指令代码：发送设置手表时间指令(新的) */
	public static final int INDEX_SET_DEVICE_TIME_NEW= INDEX_SET_DEVICE_TIME_FAIL + 1;
	/** 指令代码：发送设置手表时间指令FAIL(新的) */
	public static final int INDEX_SET_DEVICE_TIME_NEW_FAIL= INDEX_SET_DEVICE_TIME_NEW + 1;
	/** 指令代码：发送设置手表闹钟指令 */
	public static final int INDEX_SET_DEVICE_CLOCK= INDEX_SET_DEVICE_TIME_NEW_FAIL + 1;
	/** 指令代码：发送设置手表闹钟失败指令 */
	public static final int INDEX_SET_DEVICE_CLOCK_FAIL= INDEX_SET_DEVICE_CLOCK + 1;
	/** 指令代码：发送设置久坐提醒指令 */
	public static final int INDEX_SET_DEVICE_LONGSIT= INDEX_SET_DEVICE_CLOCK_FAIL + 1;
	/** 指令代码：发送设置久坐提醒失败指令 */
	public static final int INDEX_SET_DEVICE_LONGSIT_FAIL= INDEX_SET_DEVICE_LONGSIT + 1;
	/** 指令代码：发送设置抬手显示指令 */
	public static final int INDEX_SET_HAND_UP= INDEX_SET_DEVICE_LONGSIT_FAIL + 1;
	/** 指令代码：发送设置抬手显示失败指令 */
	public static final int INDEX_SET_HAND_UP_FAIL= INDEX_SET_HAND_UP + 1;
	/** 指令代码：发送设置省电模式指令 */
	public static final int INDEX_POWER= INDEX_SET_HAND_UP_FAIL + 1; 
	/** 指令代码：发送设置省电模式FAIL指令 */
	public static final int INDEX_POWER_FAIL= INDEX_POWER + 1;
	/** 指令代码：发送设置运动目标指令 */
	public static final int INDEX_STEP_TARGET= INDEX_POWER_FAIL + 1;  
	/** 指令代码：发送Flash头指令 */
	public static final int INDEX_SEND_FLASH_HEAD= INDEX_STEP_TARGET + 1;
	/** 指令代码：发送Flash头失败指令 */
	public static final int INDEX_SEND_FLASH_HEAD_FAIL= INDEX_SEND_FLASH_HEAD + 1;
	/** 指令代码：发送FlashBody指令 */
	public static final int INDEX_SEND_FLASH_BODY= INDEX_SEND_FLASH_HEAD_FAIL + 1;
	/** 指令代码：发送Flash头失败指令 */
	public static final int INDEX_SEND_FLASH_BODY_FAIL= INDEX_SEND_FLASH_BODY + 1;
	/** 指令代码：发送消息提醒指令 */
	public static final int INDEX_SEND_NOTIFICATION= INDEX_SEND_FLASH_BODY_FAIL + 1;
	/** 指令代码：发送消息提醒失败指令 */
	public static final int INDEX_SEND_NOTIFICATION_FAIL= INDEX_SEND_NOTIFICATION + 1;
	/** 指令代码：发送保持连接 */
	public static final int INDEX_SEND_0X5F= INDEX_SEND_NOTIFICATION_FAIL + 1;
	/** 指令代码：发送QQ消息提醒指令 */
	public static final int INDEX_SEND_QQ_NOTIFICATION= INDEX_SEND_0X5F + 1;
	/** 指令代码：发送微信消息提醒指令 */
	public static final int INDEX_SEND_WX_NOTIFICATION= INDEX_SEND_QQ_NOTIFICATION + 1;
	/** 指令代码：发送短信信消息提醒指令 */
	public static final int INDEX_SEND_MSG_NOTIFICATION= INDEX_SEND_WX_NOTIFICATION + 1;
	/** 指令代码：发送连爱消息提醒指令 */
	public static final int INDEX_SEND_LINK_NOTIFICATION= INDEX_SEND_MSG_NOTIFICATION + 1;
	/** 指令代码：发送来电消息提醒指令 */
	public static final int INDEX_SEND_PHONE_NOTIFICATION= INDEX_SEND_LINK_NOTIFICATION + 1;
	/** 指令代码：发送移除来电消息提醒指令 */
	public static final int INDEX_SEND_REMOVE_PHONE_NOTIFICATION= INDEX_SEND_PHONE_NOTIFICATION + 1;
	/** 指令代码：发送未接来电消息提醒指令 */
	public static final int INDEX_SEND_MISS_PHONE_NOTIFICATION= INDEX_SEND_REMOVE_PHONE_NOTIFICATION + 1;
	/** 指令代码：发送重置步数指令 */
	public static final int INDEX_SEND_STEP= INDEX_SEND_MISS_PHONE_NOTIFICATION + 1;
	/** 指令代码：发送重置步数FAIL指令 */
	public static final int INDEX_SEND_STEP_FAIL= INDEX_SEND_STEP + 1;
	/** 指令代码：获取卡号 */
	public static final int INDEX_GET_CARD_NUMBER= INDEX_SEND_STEP_FAIL + 1;
	/** 指令代码：获取卡号失败*/
	public static final int INDEX_GET_CARD_NUMBER_FAIL= INDEX_GET_CARD_NUMBER + 1;
	/** 指令代码：获取卡号不支持支付功能*/
	public static final int INDEX_GET_CARD_NUMBER_NOPAY= INDEX_GET_CARD_NUMBER_FAIL + 1;
	/** 指令代码：设置名称 */
	public static final int INDEX_SET_NAME= INDEX_GET_CARD_NUMBER_NOPAY + 1;
	/** 指令代码：设置名称失败 */
	public static final int INDEX_SET_NAME_FAIL= INDEX_SET_NAME + 1;
	/** 指令代码：发送让手环震动 */
	public static final int INDEX_SEND_BAND_RING= INDEX_SET_NAME_FAIL + 1;
	/** 指令代码：获取设备ID */
	public static final int INDEX_GET_DEVICEID= INDEX_SEND_BAND_RING + 1;
	/** 指令代码：透传 */
	public static final int INDEX_SEND_DATA= INDEX_GET_DEVICEID + 1;
	/** 指令代码：透传() */
	public static final int INDEX_SEND_DATA_CARD= INDEX_SEND_DATA + 1;
	/** 指令代码：获取modelName */
	public static final int INDEX_GET_MODEL= INDEX_SEND_DATA_CARD + 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;

	private Context mContext;
	//
	private String currentDeviceMac = null;
	private BluetoothDevice mBluetoothDevice = null;
	//正在扫描的临时判断
	private boolean isScaning = false;
	private boolean HAS_REGISTER_RECIVER = false;
	private int state = STATE_DISCONNECTED;

	public void setProviderHandler(BLEHandler handler)
	{
		this.mHandler = handler;
	}
	public BLEHandler getProviderHandler()
	{
		return this.mHandler;
	}
	/**
	 * @see BLEHandler#setBleProviderObserver(BLEHandler.IBLEProviderObserver)
	 */
	public void setBleProviderObserver(BLEHandler.BLEProviderObserverAdapter bleProviderObserver) {
		OwnLog.e(TAG, "setBleProviderObserver了");
		if(mHandler != null){
			this.mHandler.setBleProviderObserver(bleProviderObserver);
		}else{
			OwnLog.e(TAG, "setBleProviderObserver mHandler == null");
		}

	}
	/**
	 * @see BLEHandler#setBleProviderObserver(BLEHandler.IBLEProviderObserver)
	 */
	public BLEHandler.IBLEProviderObserver getBleProviderObserver()
	{
		if(mHandler != null)
			this.mHandler.getBleProviderObserver();
		return null;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
		if(obsFor_BleConnect!=null)
			obsFor_BleConnect.update(null, state);
		
	}
	/**0x13获取的时间*/
	private int timestemp;
	/**服务器获取的时间*/
	private long servertime;
	
	private static BLEProvider instence = null;
	private BLEWapper mBlEWapper = null;
    private LepaoProtocalImpl mLepaoProtocalImpl = null;
    private ConcurrentLinkedQueue<ProessThread> threadQueue = new ConcurrentLinkedQueue<ProessThread>();
    private ConcurrentLinkedQueue<ProessThread> deviceAddressQueue = new ConcurrentLinkedQueue<ProessThread>();
     
    private BLEHandler mHandler;
    private Runnable mScaningRunnable = null;
    
    private Observer obsFor_BleConnect;
    
    /** 设置蓝牙状态观察者 */
	public void setObsFor_BleConnect(Observer obsForSportDatasUploadSucess)
	{
		this.obsFor_BleConnect = obsForSportDatasUploadSucess;
	}
    
	
    public Observer getObsFor_BleConnect() {
		return obsFor_BleConnect;
	}


	
	public BLEProvider(Context context)
	{
		 HAS_REGISTER_RECIVER = true;
		 mContext = context;
		 mBlEWapper = BLEWapper.getInstence();
		 context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		 mLepaoProtocalImpl = new LepaoProtocalImpl();
	}
	
	public  static BLEProvider getInstence() {
		if (instence == null) {
			instence = new BLEProvider();
		}
		return instence;
	}
	
	private BLEProvider() {
	
	}
	

	public long getServertime() {
		return servertime;
	}
	public void setServertime(long servertime1) {
		this.servertime = servertime1;
	}
	public int getTimestemp() {
		return timestemp;
	}
	public void setTimestemp(int timestemp) {
		this.timestemp = timestemp;
	}
	public void release()
	{
		if(isScaning){
			this.stopScan(0);
		}
		mBluetoothDevice = null;
		resetDefaultState();
		mBlEWapper.release();
	}
	
	private boolean init(Context context)
	{
		try
		{
			mBlEWapper.init(context);
		}
		catch (BLENotSupportException e)
		{
			mHandler.sendEmptyMessage(MSG_BLE_NOTSUPPORT);
			return false;
		}
		catch (BLENotEnabledException e)
		{
			mHandler.sendEmptyMessage(MSG_BLE_NOT_ENABLED);
			return false;
		} catch (BLENotBounded e) {
			e.printStackTrace();
		}
		return true;
	}

	
    public boolean connect(){
    	try {
    		
			return mBlEWapper.connect(this.currentDeviceMac);
			
		} catch (BLException e) {
			e.printStackTrace();
		}
		return false;
    }
	
	public void disConnect()
	{
		if(state == STATE_CONNECTED || state == STATE_DISCOVERED)
//			mBlEWapper.release();
			mBlEWapper.disconnect();
			
	}
	
	
	public void scanForConnnecteAndDiscovery()//BLEHandler handler) 
	{
		if(this.currentDeviceMac == null)
		{
			OwnLog.i(TAG, "currentDeviceMac is null！扫描和连接无法继续！！");
			return;
		}
		
		if(isScaning)
		{
			OwnLog.i(TAG, "is scaning!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			return;
		}
		
		if(state == STATE_CONNECTED || state == STATE_DISCOVERED
			||  state == STATE_CONNECTING)
		{
			OwnLog.i(TAG, "当前state正处于"+state+"时，本次scanForConnnecteAndDiscovery将被忽略！");
			return;
		}
		
		if(!init(mContext))//, handler))
		{
			OwnLog.i(TAG, "init failed!!!!!!!!!!!!!!!!!!");
			return;
		}
		// * 【1】先看看在已连接连表里是否已存在这个设备（可能是在之前诸如切换账号时没有成功关闭掉此设备）
		// * ，如果已存在就直接连接之，否则如果不直接连接，若按普通流程（即反正描后再连接）是行不通的，
		// * 因为处于已连接状态下的设备此时是再也的扫描不到的
		List<BluetoothDevice> connectedDevices = mBlEWapper.getConnectedDevices();
		
		OwnLog.i(TAG, "【BLE_DEBUG】首先看看已连接列表，connectedDevices="+connectedDevices);
		boolean find = false;
		if(connectedDevices != null)
		{
			OwnLog.i(TAG, "【BLE_DEBUG】已连接列表中设备数="+connectedDevices.size());
			if(this.getCurrentDeviceMac() != null)
			{
				for(final BluetoothDevice bd : connectedDevices)
				{
					if(bd != null)
					{
						OwnLog.i(TAG, "【BLE_DEBUG】当前已连接的设备：mac="+bd.getAddress()+",type="+bd.getType()+",bondState="+bd.getBondState()+",uuids="+Arrays.toString(bd.getUuids()));
						String connectedMac = bd.getAddress();
						if(connectedMac != null && this.getCurrentDeviceMac().toUpperCase().substring(6).equals(connectedMac.toUpperCase().substring(6)))
						{
							OwnLog.i(TAG, "【BLE_DEBUG】OK, 设备：mac="+bd.getAddress()+"，就是我们要连接的设备，直接连接无需扫描了！");
							// FIXME:放在主线程中执行是为了兼容3星Note2（升级到Android4.3），
							// 据网络资料此设备如果不放在主线程里连接，则无法连接
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								@Override
								public void run() 
								{
									try
									{
										connect(bd);
									}
									catch (BLException e)
									{
										mHandler.sendEmptyMessage(MSG_BLE_CONNECT_FAILED);
									}
								}
							});
							
							// 在已连接的连表中找到了设备，直接返回，不需要再扫描了！！！！！！！！！！！
							find = true;
							return;
						}
					}
				}
			}
			else
			{
				OwnLog.i(TAG, "要连接的设备mac为null，处理无法继续！！！");
			}
		}

		// * 【2】该设备并不存在于已连接列表，则从头开启扫描和连接流程
		if(!find)
		{
		 	if(mHandler != null)
        	    mHandler.sendEmptyMessage(MSG_BLE_CONNECTING);
		 	
			try 
			{
				isScaning = true;
				stopScan(SCAN_PERIOD);
				OwnLog.e(TAG, "===============start scan===============");
//				if (Build.VERSION.SDK_INT < 21){
					mBlEWapper.scan(mHandler, mLeScanCallback);
//				}
//				else{
//					mBlEWapper.scanAPI21(mHandler, mScanCallback);
//				}
			}
			catch (BLException e) 
			{
				OwnLog.i(TAG, "scan error!!!!!!!!!!!!!!!!!!!!!!!!!!!"+ e.getMessage());
				
			}
		}
	}
	
    /**
     * 连接指定的设备.
     *
     * @param device
     * @throws Exception
     */
    private void connect(BluetoothDevice device) throws BLException
    {
    	OwnLog.e(TAG, "our device address.........................." + device.getAddress());
 	   mBluetoothDevice = device;
 	   if(!mBlEWapper.connect(device.getAddress()))
 	   {
 		   mHandler.sendEmptyMessage(MSG_BLE_CONNECT_FAILED);
 	   }
    }

    /**
     * 连接指定的MAC.
     * @param mac
     */
    public void connect_mac(final String mac)
    {
		if(isScaning)
		{
			OwnLog.i(TAG, "is scaning!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			return;
		}
		
		if(state == STATE_CONNECTED || state == STATE_DISCOVERED ||  state == STATE_CONNECTING)
		{
			OwnLog.i(TAG, "当前state正处于"+state+"时，本次scanForConnnecteAndDiscovery将被忽略！");
			return;
		}
		
		if(!init(mContext))//, handler))
		{
			OwnLog.i(TAG, "init failed!!!!!!!!!!!!!!!!!!");
			return;
		}
		OwnLog.e(TAG, "connect device address.........................." + mac);
 	   	//状态改为正在连接
		setState(STATE_CONNECTING);
	 	if(mHandler != null)
	 		mHandler.sendEmptyMessage(MSG_BLE_CONNECTING);
	 	// * 【1】先看看在已连接连表里是否已存在这个设备（可能是在之前诸如切换账号时没有成功关闭掉此设备）
		// * ，如果已存在就直接连接之，否则如果不直接连接，若按普通流程（即反正描后再连接）是行不通的，
		// * 因为处于已连接状态下的设备此时是再也的扫描不到的
		List<BluetoothDevice> connectedDevices = mBlEWapper.getConnectedDevices();
		OwnLog.i(TAG, "【BLE_DEBUG】首先看看已连接列表，connectedDevices="+connectedDevices);
		boolean find = false;
		if(connectedDevices != null)
		{
			OwnLog.i(TAG, "【BLE_DEBUG】已连接列表中设备数="+connectedDevices.size());
			if(this.getCurrentDeviceMac() != null)
			{
				for(final BluetoothDevice bd : connectedDevices)
				{
					if(bd != null)
					{
						OwnLog.i(TAG, "【BLE_DEBUG】当前已连接的设备：mac="+bd.getAddress()+",type="+bd.getType()+",bondState="+bd.getBondState()+",uuids="+Arrays.toString(bd.getUuids()));
						String connectedMac = bd.getAddress();
						if(connectedMac != null && mac.equals(connectedMac.toUpperCase()))
						{
							OwnLog.i(TAG, "【BLE_DEBUG】OK, 设备：mac="+bd.getAddress()+"，就是我们要连接的设备，直接连接无需扫描了！");
							// FIXME:放在主线程中执行是为了兼容3星Note2（升级到Android4.3），
							// 据网络资料此设备如果不放在主线程里连接，则无法连接
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								@Override
								public void run() 
								{
									try
									{
										connect(bd);
									}
									catch (BLException e)
									{
										mHandler.sendEmptyMessage(MSG_BLE_CONNECT_FAILED);
									}
								}
							});
							
							// 在已连接的连表中找到了设备，直接返回，不需要再扫描了！！！！！！！！！！！
							find = true;
							return;
						}
					}
				}
			}
			else
			{
				OwnLog.i(TAG, "要连接的设备mac为null，处理无法继续！！！");
			}
		}
		if(!find){
			// FIXME:放在主线程中执行是为了兼容3星Note2（升级到Android4.3），
			// 据网络资料此设备如果不放在主线程里连接，则无法连接
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() 
				{
					try
					{
						if(!mBlEWapper.connect(mac))
					 	   {
					 		   mHandler.sendEmptyMessage(MSG_BLE_CONNECT_FAILED);
					 	   }
					}
					catch (BLException e)
					{
						mHandler.sendEmptyMessage(MSG_BLE_CONNECT_FAILED);
					}
				}
			});
		}
 	   
    }
    
    /**
     * 连接指定的MAC.(callback)
     * @param mac
     * @throws Exception
     */
    public void connectMacCallback(String mac) throws BLException
    {
		if(isScaning)
		{
			OwnLog.i(TAG, "is scaning!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			return;
		}
		
		if(state == STATE_CONNECTED || state == STATE_DISCOVERED ||  state == STATE_CONNECTING)
		{
			OwnLog.i(TAG, "当前state正处于"+state+"时，本次scanForConnnecteAndDiscovery将被忽略！");
			return;
		}
		
		if(!init(mContext))//, handler))
		{
			OwnLog.i(TAG, "init failed!!!!!!!!!!!!!!!!!!");
			return;
		}
		OwnLog.e(TAG, "准备去连接的  device address.........................." + mac);
 	   currentDeviceMac = mac; 
 	   if(!mBlEWapper.connect(mac))
 	   {
 		   mHandler.sendEmptyMessage(MSG_BLE_CONNECT_FAILED);
 	   }
 	   
    }

	private void stopScan(final long delay)
	{
		OwnLog.e(TAG, "停止掃描  delay.........................." +delay);
		mHandler.postDelayed(mScaningRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					if (mBluetoothDevice == null && delay > 0 ) {
						mHandler.sendEmptyMessage(MSG_BLE_SCAN_TIME_OUT);
					}
					//断开扫描API判断
//					if (Build.VERSION.SDK_INT < 21){
						mBlEWapper.stopScan();
//					}
//					else{
//						mBlEWapper.stopScannAPI21();
//					}
				} 
				catch (BLException e)
				{
					OwnLog.e(TAG, e.getMessage(), e);
				}
				finally	
				{
					isScaning = false;
				}
			}
		}, delay);
	}


	//wapper类 连接的广播接收者
	// Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            int status_from_BLEWapper=intent.getIntExtra(BLEWapper.EXTRA_STATUS, 0);
            boolean notsendbroad = intent.getBooleanExtra(BLEWapper.EXTRA_DATA, true);
            //设备已经连接
            if (BLEWapper.ACTION_GATT_CONNECTED.equals(action))
            {
            	setState(STATE_CONNECTED);
            	OwnLog.d(TAG, ".....................connected(接收到连接成功的广播)......................");
//            	stopScan(0); // 既然连上了，就无条件尝试关掉扫描（再扫描就没有意义了）  修改于0916
            }
            //连接已经断开
            else if (BLEWapper.ACTION_GATT_DISCONNECTED.equals(action)) 
            {
            	setState(STATE_DISCONNECTED);
            	if(mHandler != null)
            	  mHandler.sendEmptyMessage(MSG_BLE_CONNECT_LOST);
            	mBlEWapper.close();
            	OwnLog.e(TAG, ".....................disconnected................................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
            //服务被发现
            else if (BLEWapper.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) 
            {
            	if(status_from_BLEWapper==BluetoothGatt.GATT_SUCCESS){
            		setState(STATE_DISCOVERED);
            		OwnLog.i(TAG, "..........discovered....rec.."+(count++)+"..."+notsendbroad);
      				if(mHandler != null && !notsendbroad){
      				   mHandler.sendEmptyMessage(MSG_BLE_CONNECT_SUCCESS);
      				 OwnLog.i(TAG, ".....................discovered................................");
      				}
                	  
            	} else {
            		OwnLog.e(TAG, "Service discovery failed");
      				return;
      			}
            } 
            //连接失败
            else if (BLEWapper.ACTION_GATT_CONNECTED_FAILED.equals(action))
            {
            	setState(STATE_CONNECTED_FAILED);
            	if(mHandler != null)
            	    mHandler.sendEmptyMessage(MSG_BLE_CONNECT_FAILED);
            	OwnLog.e(TAG, ".....................connect failed................................");
			}
            //可能  是正在连接吧
            else if (BLEWapper.ACTION_GATT_CONNECTING.equals(action))
            {
            	setState(STATE_CONNECTING);
			 	if(mHandler != null)
            	    mHandler.sendEmptyMessage(MSG_BLE_CONNECTING);
			 	OwnLog.e(TAG, ".....................connecting................................");
			}
//            else  if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
//            	Log.e(TAG, ".....................配对状态发生改变................................");
////            	if(intent.getIntExtra(name, defaultValue))
//            	try {
//					createBond(BluetoothDevice.class, mBluetoothDevice);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//            	
//			}
            else  if (BLEWapper.ACTION_DATA_NOTIFY.equals(action)){
				
			}
        }


    };

	// Device scan callback. 21api以下的扫描回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
 	   //防止重复连接
 	   private boolean isRuning = false;
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) 
        {
     	   if(getCurrentDeviceMac() == null)
     	   {
     		   OwnLog.e(TAG, "getCurrentDeviceMac() is NULL!!!!!!!!!!!!!!!!!!!!!!!!");
     		   return;
     	   }
     	   else
     	   {
     		   String scanMac = device.getAddress().substring(device.getAddress().length()-11,device.getAddress().length()).toUpperCase();
     		   String myMac = getCurrentDeviceMac().substring(getCurrentDeviceMac().length()-11,getCurrentDeviceMac().length()).toUpperCase();
     		   /**判断是否是手环设备 23 24*/ 
     		  if(( ((scanRecord[5] == (byte)0xE1) && (scanRecord[6] == (byte)0xFE)) || ((scanRecord[23] == (byte)0xE1) && (scanRecord[24] == (byte)0xFE)) ) && scanMac.equals(myMac))  //"78:A5:04:84:48:B4"))
     		   {
     			   OwnLog.e(TAG, "【来自扫描】4.3 device address.........................." + device.getAddress());
//     			   try
//     			   {
					   stopScan(0);
// 				   }
//     			   catch (BLException e1)
// 				   {
//     				   OwnLog.e(TAG, e1.getMessage());
// 				   }
     			   if(state == STATE_CONNECTED  || state == STATE_DISCOVERED || state == STATE_CONNECTING || isRuning)
     			   {
     				   return;
     			   }
     			   // FIXME:放在主线程中执行是为了兼容3星Note2（升级到Android4.3），
     			   // 据网络资料此设备如果不放在主线程里连接，则无法连接
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
 					@Override
 					public void run() {
 						 try
 		    			   {
 							   Message msg =  mHandler.obtainMessage(MSG_BLE_RSSI, rssi);
 							   msg.sendToTarget();
 							   isRuning = true;
 							   mBluetoothDevice = device;
 							   connect(device);
 		    			   } catch (BLException e) {
 		    				   mHandler.sendEmptyMessage(MSG_BLE_CONNECT_FAILED);
 		    			   }
 						 isRuning = false;
 					}
 				   });
     		   }
     	   }
        }
    };
	//21api以上的扫描回调
//	@SuppressLint("NewApi")
//	private ScanCallback mScanCallback = new ScanCallback() {
//		//防止重复连接
//		private boolean isRuning = false;
//		@Override
//		public void onScanResult(int callbackType, final ScanResult result) {
//			Log.i("callbackType", String.valueOf(callbackType));
//			Log.i("result", result.toString());
//			final BluetoothDevice device = result.getDevice();
//			if(getCurrentDeviceMac() == null)
//			{
//				OwnLog.e(TAG, "getCurrentDeviceMac() is NULL!!!!!!!!!!!!!!!!!!!!!!!!");
//				return;
//			}
//			else
//			{
//				String scanMac = device.getAddress().substring(device.getAddress().length()-11,device.getAddress().length()).toUpperCase();
//				String myMac = getCurrentDeviceMac().substring(getCurrentDeviceMac().length()-11,getCurrentDeviceMac().length()).toUpperCase();
//				if(scanMac.equals(myMac))  //"78:A5:04:84:48:B4"))
//				{
//					OwnLog.e(TAG, "【来自扫描】5.0 device address.........................." + device.getAddress());
////					try
////					{
//						stopScan(0);
////					}
////					catch (BLException e1)
////					{
////						OwnLog.e(TAG, e1.getMessage());
////					}
//					if(state == STATE_CONNECTED  || state == STATE_DISCOVERED || state == STATE_CONNECTING || isRuning)
//					{
//						return;
//					}
//					// FIXME:放在主线程中执行是为了兼容3星Note2（升级到Android4.3），
//					// 据网络资料此设备如果不放在主线程里连接，则无法连接
//					new Handler(Looper.getMainLooper()).post(new Runnable() {
//						@Override
//						public void run() {
//							try
//							{
//								Message msg =  mHandler.obtainMessage(MSG_BLE_RSSI,result.getRssi());
//								msg.sendToTarget();
//								isRuning = true;
//								mBluetoothDevice = device;
//								connect(device);
//							} catch (BLException e) {
//								mHandler.sendEmptyMessage(MSG_BLE_CONNECT_FAILED);
//							}
//							isRuning = false;
//						}
//					});
//				}
//			}
//		}
//
//		@Override
//		public void onBatchScanResults(List<ScanResult> results) {
//			for (ScanResult sr : results) {
//				Log.i("ScanResult - Results", sr.toString());
//			}
//		}
//
//		@Override
//		public void onScanFailed(int errorCode) {
//			Log.e("Scan Failed", "Error Code: " + errorCode);
//		}
//	};
    
    

   //......................................................从这个开始就是应用层发送设备给蓝牙层的数据过滤......................................................//
    
   public void unBoundDevice(Context context)
   {
	   OwnLog.i(TAG, "..................unBoundDevice Thread........................");
	   runIndexProess(context, INDEX_UNBOUND_DEVICE);
   }
   
   //0x13  获取设备信息
   public void getAllDeviceInfoNew(Context context)
   {
	   OwnLog.i(TAG, "..................getAllDeviceInfoNew Thread........................");
	   runIndexProess(context, INDEX_GAT_ALL_INFO_NEW);
   }
   
   //0x30  获取ModelName
   public void getModelName(Context context)
   {
	  OwnLog.i(TAG, "..................getModelName Thread........................");
	   runIndexProess(context, INDEX_GET_MODEL);
	   
   }
   //0x02  获取运动数据
   public void getSportDataNew(Context context)
   {
	  OwnLog.i(TAG, "..................getSportDataNew Thread........................");
	   runIndexProess(context, INDEX_SYNC_SPORT_DATA_NEW);
   }
   //设置身体信息
   public void regiesterNew(Context context,LPDeviceInfo deviceInfo)
   {
	  OwnLog.i(TAG, "..................regiesterNew Thread........................");
	   runIndexProess(context, INDEX_REGIESTER_INFO_NEW,deviceInfo);
   }
   
//   public void syncALLDeviceInfo(Context context,LPDeviceInfo deviceInfo)
//   {
//	  OwnLog.i(TAG, "..................syncALLDeviceInfo Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//	   runIndexProess(context, INDEX_SYNC_ALL_DEVICE_INFO,deviceInfo);
//   }
   

   /* 设置时间*/
   public void SetDeviceTime(Context context)
   {
	  OwnLog.i(TAG, "..................SetDeviceTime Thread........................");
	   runIndexProess(context, INDEX_SET_DEVICE_TIME);
   }
   /* 设置闹钟*/
   public void SetClock(Context context,LPDeviceInfo deviceInfo)
   {
	  OwnLog.i(TAG, "..................SetClock Thread........................");
	   runIndexProess(context, INDEX_SET_DEVICE_CLOCK,deviceInfo);
   }
   /* 设置久坐提醒*/
   public void SetLongSit(Context context,LPDeviceInfo deviceInfo)
   {
	  OwnLog.i(TAG, "..................SetLongSit Thread........................");
	   runIndexProess(context, INDEX_SET_DEVICE_LONGSIT,deviceInfo);
   }
   /* 设置抬手显示*/
   public void SetHandUp(Context context,LPDeviceInfo deviceInfo)
   {
	  OwnLog.i(TAG, "..................SetHandUp Thread........................");
	   runIndexProess(context, INDEX_SET_HAND_UP,deviceInfo);
   }
   /* 设置省电模式*/
   public void SetPower(Context context,LPDeviceInfo deviceInfo)
   {
	  OwnLog.i(TAG, "..................SetPower Thread........................");
	   runIndexProess(context, INDEX_POWER,deviceInfo);
   }
   
   /* 设置保持状态*///INDEX_SEND_0X5f
   public void keepstate(Context context)
   {
	  OwnLog.i(TAG, "..................keepstate Thread........................");
	   runIndexProess(context, INDEX_SEND_0X5F);
   }
   
   /* 获取卡号*/
   public void get_cardnum(Context context)
   {
	  OwnLog.i(TAG, "..................get_cardnum Thread........................");
	   runIndexProess(context, INDEX_GET_CARD_NUMBER);
   }
   /* 设置名称*/
   public void set_name(Context context,LPDeviceInfo deviceInfo)
   {
	  OwnLog.i(TAG, "..................set_name Thread........................");
	   runIndexProess(context, INDEX_SET_NAME,deviceInfo);
   }
   /* 设置手环震动*/
   public void SetBandRing(Context context)
   {
	  OwnLog.i(TAG, "..................SetBandRing Thread........................");
	   runIndexProess(context, INDEX_SEND_BAND_RING);
   }
   /* 开卡*/
   public void openSmartCard(Context context)
   {
	   runIndexProess(context, INDEX_OPEN_SMC);
   }
   /* AID*/
   public void AIDSmartCard(Context context,LPDeviceInfo deviceInfo)
   {
	   runIndexProess(context, INDEX_AID_SMC,deviceInfo);
   }
   /* 检验PIN*/
   public void PINSmartCard(Context context,LPDeviceInfo deviceInfo)
   {
	   runIndexProess(context, INDEX_SET_SMC_TRANSFER,deviceInfo);
   }
   
   /* 羊城step1*/
   public void yangChengStep1(Context context)
   {
	   runIndexProess(context, INDEX_AID_SMC_YC1);
   }
   
   /* 羊城step2*/
   public void yangChengStep2(Context context)
   {
	   runIndexProess(context, INDEX_AID_SMC_YC2);
   }
   
   /* 读取学校代号*/
   public void getSchoolID(Context context)
   {
	   runIndexProess(context, INDEX_SCHOOL_ID);
   }
   
   /* 读取余额*/
   public void readCardBalance(Context context,LPDeviceInfo deviceInfo)
   {
	   runIndexProess(context, INDEX_GET_SMC_BALANCE,deviceInfo);
   }
   /* 读取交易记录*/
   public void getSmartCardTradeRecord(Context contex,LPDeviceInfo deviceInfo)
   {
	   runIndexProess(contex , MSG_GET_SMC_TRADE_RECORD , deviceInfo);
   }
   
   public void readCardRecord_XJ(Context context,LPDeviceInfo deviceInfo)
   {
		runIndexProess(context, INDEX_XIANJIN_RECORD,deviceInfo);
   }
   
   
   public void close7816Card(Context context)
   {
	   runIndexProess(context, INDEX_ClOSE_FEIJIE);
   }
   
   public void open7816Card(Context context)
   {
	   runIndexProess(context, INDEX_OPEN_FEIJIE);
   }
   
   /* 关卡*/
   public void closeSmartCard(Context context)
   {
	   runIndexProess(context, INDEX_CLOSE_SMC);
   }
   //OAD头
   public void OADDeviceHead(Context context,final byte[] data)
   {
	  OwnLog.i(TAG, "..................setOADDeviceHead Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_SEND_OAD_HEAD,data);
   
   }
   //整个OAD H60-L01
   public void OADDevice(final Context context,final byte[] data)
   {
	   if(android.os.Build.MODEL.equals("H60-L01")){
		   new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					  OwnLog.i(TAG, "..................OADDevice Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					   runIndexProess(context, INDEX_SEND_OAD, data);
					}
			   }); 
	   }
	   else{
		  OwnLog.i(TAG, "..................OADDevice Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		   runIndexProess(context, INDEX_SEND_OAD, data);
	   }
	  
   }
   
   ////新的带回调的OAD接口  OAD头
   public void OADDeviceHeadback(Context context,final byte[] data)
   {
	  OwnLog.i(TAG, "..................setOADDeviceHead Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_SEND_OAD_HEAD_BACK,data);
   
   }
   //新的带回调的OAD接口
   public void OADDeviceback(final Context context,final byte[] data)
   {
	   if(android.os.Build.MODEL.equals("H60-L01")){
		   new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					  OwnLog.i(TAG, "..................OADDevice Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					   runIndexProess(context, INDEX_SEND_OAD_BACK, data);
					}
			   }); 
	   }
	   else{
		  OwnLog.i(TAG, "..................OADDevice Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		   runIndexProess(context, INDEX_SEND_OAD_BACK, data);
	   }
	  
   }
   
   //绑定设备  
   public void requestbound(Context context)
   {
	  OwnLog.i(TAG, "..................INDEX_REQUEST_BOUND Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_REQUEST_BOUND);
   }
  //绑定设备 (开始) _美国
   public void requestbound_fit(Context context)
   {
	  OwnLog.i(TAG, "..................INDEX_REQUEST_BOUND_FIT Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_REQUEST_BOUND_FIT);
   }
   //绑定设备 (开始) _美国
   public void requestbound_recy(Context context)
   {
	  OwnLog.i(TAG, "..................INDEX_REQUEST_BOUND_RECY Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_REQUEST_BOUND_RECY);
   }
   
   //运动目标
   public void setTarget(Context context,LPDeviceInfo deviceInfo)
   {
	  OwnLog.i(TAG, "..................INDEX_STEP_TARGET Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_STEP_TARGET,deviceInfo);
   }  
  //消息提醒  开关
   public void setNotification(Context context,byte[] data)
   {
	  OwnLog.i(TAG, "..................setNotification Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_SEND_NOTIFICATION,data);
   }
   
   //消息提醒  (qq)
   public void setNotification_qq(Context context,byte notificationUID,byte[] title,byte[] text)
   {
	  OwnLog.i(TAG, "..................INDEX_SEND_QQ_NOTIFICATION Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context,notificationUID, INDEX_SEND_QQ_NOTIFICATION,title,text);
   }
   //消息提醒  (短信)
   public void setNotification_MSG(Context context,byte notificationUID,byte[] title,byte[] text)
   {
	  OwnLog.i(TAG, "..................INDEX_SEND_MSG_NOTIFICATION Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context,notificationUID, INDEX_SEND_MSG_NOTIFICATION,title,text);
   }
   //消息提醒  (WX)
   public void setNotification_WX(Context context,byte notificationUID,byte[] title,byte[] text)
   {
	  OwnLog.i(TAG, "..................INDEX_SEND_WX_NOTIFICATION Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context,notificationUID, INDEX_SEND_WX_NOTIFICATION,title,text);
   }
   //消息提醒  (连爱)
   public void setNotification_LINK(Context context,byte notificationUID,byte[] title,byte[] text)
   {
	  OwnLog.i(TAG, "..................INDEX_SEND_LINK_NOTIFICATION Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context,notificationUID, INDEX_SEND_LINK_NOTIFICATION,title,text);
   }
   //消息提醒  (电话)
   public void setNotification_PHONE(Context context,byte notificationUID,byte[] title,byte[] text)
   {
	  OwnLog.i(TAG, "..................INDEX_SEND_PHONE_NOTIFICATION Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context,notificationUID, INDEX_SEND_PHONE_NOTIFICATION,title,text);
   }
   //消息提醒  (移除来电)
   public void setNotification_remove_incall(Context context,byte notificationUID,byte[] title,byte[] text)
   {
	  OwnLog.i(TAG, "..................setNotification_remove_incall Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context,notificationUID, INDEX_SEND_REMOVE_PHONE_NOTIFICATION,title,text);
   }
   //消息提醒  (未接来电)
   public void setNotification_misscall(Context context,byte notificationUID,byte[] title,byte[] text)
   {
	  OwnLog.i(TAG, "..................setNotification_misscall Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context,notificationUID, INDEX_SEND_MISS_PHONE_NOTIFICATION,title,text);
   }
   
   //发送命令（透传）
   public void send_data2ble(Context context,byte[] data)
   {
	  OwnLog.i(TAG, "..................INDEX_SEND_DATA Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context , INDEX_SEND_DATA , data);
   }
   
 //发送命令（透传）
   public void send_data2ble_card(Context context,byte[] data)
   {
	  OwnLog.i(TAG, "..................INDEX_SEND_DATA_CARD Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context , INDEX_SEND_DATA_CARD , data);
   }
   
   //flash
   public void flashhead(Context context,byte[] data)
   {
	  OwnLog.i(TAG, "..................flashhead Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_SEND_FLASH_HEAD,data);
   }
   //flash body
   public void flashbody(Context context,byte[] data)
   {
	  OwnLog.i(TAG, "..................flashbody Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_SEND_FLASH_BODY,data);
   }
   
   //重置步数
   public void resetStep(Context context,LPDeviceInfo deviceInfo)
   {
	  OwnLog.i(TAG, "..................INDEX_SEND_STEP Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_SEND_STEP,deviceInfo);
   }
   
   //获取设备ID
   public void getdeviceId(Context context)
   {
	  OwnLog.i(TAG, "..................INDEX_GET_DEVICEID Thread........................"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	   runIndexProess(context, INDEX_GET_DEVICEID);
   }
   
   public void runIndexProess(Context context,int index,LPDeviceInfo deviceInfo)
   {
	   	   
	   synchronized(threadQueue)
	   {
		   ProessThread proessThread = new ProessThread(context, index,deviceInfo);
		   
		   boolean needStart = threadQueue.isEmpty();
		   
		   threadQueue.add(proessThread);
		   if(needStart)
			   proessThread.start();
	   }
   }
   
   public void runIndexProess(Context context,int index)
   {
	 
	   synchronized(threadQueue)
	   {
		   ProessThread proessThread = new ProessThread(context, index);
		   if(threadQueue.isEmpty())
		   {
			   proessThread.start();
		   }
		   threadQueue.add(proessThread);
		  OwnLog.i(TAG, "============threadQueue============"+threadQueue.size());
		   
	   }
   }
   public void runIndexProess(Context context,byte notificationUID,int index,byte[] title,byte[] text)
   {
	 
	   synchronized(threadQueue)
	   {
		   ProessThread proessThread = new ProessThread(context,notificationUID, index,title,text);
		   if(threadQueue.isEmpty())
		   {
			   proessThread.start();
		   }
		   threadQueue.add(proessThread);
		   
	   }
   }
   
   public void runIndexProess(Context context,int index,byte[] date)
   {
	   synchronized(deviceAddressQueue)
	   {
		   ProessThread proessThread = new ProessThread(context, index ,date);
		   if(deviceAddressQueue.isEmpty())
		   {
			   proessThread.start();
		   }else{
			   deviceAddressQueue.clear();
			   proessThread.start();
		   }
		   deviceAddressQueue.add(proessThread);
	   }
   }
   //////////////////*********1112修改**************///////////////////
   public void clearProess()
   {
	   synchronized(deviceAddressQueue)
	   {
		   if(deviceAddressQueue != null && !deviceAddressQueue.isEmpty())
		   {
			   deviceAddressQueue.clear();
		   }
	   }
	   
	   synchronized(threadQueue){
		   if(threadQueue != null && !threadQueue.isEmpty())
			{
				threadQueue.clear();
			}
	   }
   }
   //////////////////*********1112修改**************/////////////////// 
   
   public void runProessAgain()
   {
	   if(threadQueue.size() > 0)
	   {
		   ProessThread  currentThread = threadQueue.element();
		   if(currentThread.getDeviceInfo() == null)
		      new ProessThread(currentThread.getContext(),currentThread.getIndex()).start();
		   else
			   new ProessThread(currentThread.getContext(),currentThread.getIndex(), currentThread.getDeviceInfo()).start();
	   }
   }
   
   private void getSportDataNew() throws BLException, LPException
   {
//	   	  INDEX_SYNC_SPORT_DATA_NEW
	      //////////////////*********1112修改**************///////////////////
	      // 如果取到的手环时间判断为重置后的 则把同步上来的数据时间加上时间差
	      int detail=0;
	      if(getTimestemp()<365*24*3600){
	    	  detail = (int)(getServertime()/1000) -getTimestemp();
	      }else{
	    	  detail = 0;
	      }
	      //////////////////*********1112修改**************///////////////////
	      float len = 0;
	      int temp = 0;
	      Message msg;
	      List<LPSportData> tmpSportDataList = null;
	      while(true)
	      {
	    	  if(tmpSportDataList == null)
	    	  { 
	    		  tmpSportDataList = mLepaoProtocalImpl.getSportDataNew(0xfF, 0x7F,detail);
	    		  if(!tmpSportDataList.isEmpty())
	    		  {
	    			  // 同步方式的保存运动数据
	    			  saveSportSync2DB(tmpSportDataList);
	    			  
	    			  len = tmpSportDataList.get(0).getDataLen()*6;
		    		  temp = tmpSportDataList.get(0).getRevLen();
		    		  // 异步方式的数据获取通知(通知界面)
	    			  msg =  mHandler.obtainMessage();
					  msg.what = MSG_BLE_DATA;
					  msg.arg1 = MSG_BLE_SPORT_DATA_PROESS;
					  msg.obj =  tmpSportDataList.get(0).getDataLen();
			    	  msg.sendToTarget();
	    		  }
	    		  else
	    		  {
	    			  // 异步方式的数据获取通知（本次实际上是空数据哦，也通知一下）
	    			  msg =  mHandler.obtainMessage();
					  msg.what = MSG_BLE_DATA;
					  msg.arg1 = MSG_BLE_SPORT_DATA_PROESS;
					  msg.obj = 0;
			    	  msg.sendToTarget();
	    			  
	    			  msg =  mHandler.obtainMessage(MSG_BLE_DATA_END);
	    			  msg.sendToTarget();
	    			  return;
				  }
	    	  }
	    	  else
	    	  {
	    		if(tmpSportDataList.isEmpty())
	    			
	    			return;
	    		  
	    		int lastRecordItems =	tmpSportDataList.get(0).getDataLen() - (tmpSportDataList.get(0).getRevLen() - 13) / 6 ;
	    		if(lastRecordItems<=0)
	    			lastRecordItems =0;
//				tmpSportDataList = mLepaoProtocalImpl.getSportDataNew(tmpSportDataList.get(0).getDataLen() >= 0 ? tmpSportDataList.get(0).getDataLen(): 0, lastRecordItems);
	    		tmpSportDataList = mLepaoProtocalImpl.getSportDataNew(lastRecordItems, 0,detail);
				
	    		  if(tmpSportDataList.size() > 0)
		          {
	    			  OwnLog.d(TAG, "..................dataLen..........................."+tmpSportDataList.get(0).getDataLen());
					  OwnLog.d(TAG, "..................recvLen.........................."+(tmpSportDataList.get(0).getRevLen() - 13)/ 6);
//					  msg =  mHandler.obtainMessage(MSG_BLE_DATA,cmd,0,tmpSportDataList);
//					  msg.sendToTarget();
					  // 同步方式的保存数据
					  saveSportSync2DB(tmpSportDataList);
					  
					  msg =  mHandler.obtainMessage();
					  msg.what = MSG_BLE_DATA;
					  msg.arg1 = MSG_BLE_SPORT_DATA_PROESS;
					  msg.obj = tmpSportDataList.get(0).getDataLen();
			    	  msg.sendToTarget();
	    		  }
	    		  else
	    		  {
	    			  msg =  mHandler.obtainMessage();
					  msg.what = MSG_BLE_DATA;
					  msg.arg1 = MSG_BLE_SPORT_DATA_PROESS;
					  msg.obj = 0;
			    	  msg.sendToTarget();
	    			  
	    			  msg =  mHandler.obtainMessage(MSG_BLE_DATA_END);
	    			  msg.sendToTarget();
	    			  mLepaoProtocalImpl.setDeviceTime();
	    			  break;
	    		  }
			  } 
	    	 OwnLog.d(TAG, "sport len......................."+len);
	    	 OwnLog.d(TAG, "current len......................"+temp);
	      }
   }
   
   private List<LLTradeRecord> getTradeRecordList(BLEHandler handler,LPDeviceInfo serverDeviceInfo) throws LPException, BLException
   {
	   List<LLTradeRecord> tmp = new LinkedList<>();
	   for(int i = 1; i < 11;i++)
	   {
		   LLTradeRecord llTradeRecord = mLepaoProtocalImpl.getSmartCardTradeRecord(i,serverDeviceInfo);
		   OwnLog.e(TAG, "getTradeTimelong:"+llTradeRecord.getTradeTimelong());
		   OwnLog.e(TAG, "serverDeviceInfo:"+serverDeviceInfo.time);
		   if(llTradeRecord.getTradeTimelong() == serverDeviceInfo.time){
			   OwnLog.e(TAG, "已查询到上次查询的时间了");
			   return tmp;
		   }
		   else if(llTradeRecord.isVaild() && llTradeRecord.isHasRecord())
		   {
			   Message msg = handler.obtainMessage(MSG_GET_SMC_TRADE_RECORD_ASYNC,llTradeRecord);
			   msg.sendToTarget();
			   tmp.add(llTradeRecord);
		   }
		   else if(!llTradeRecord.isVaild()){
			  OwnLog.e(TAG, "非法");
			   return tmp;
		   }
		   else if(!llTradeRecord.isHasRecord()){
			  OwnLog.e(TAG, "无记录");
			   return tmp;
		   }
	   }
	   Message msg = handler.obtainMessage(MSG_GET_SMC_TRADE_RECORD,tmp);
	   msg.sendToTarget();
	   return tmp;
   }
   
   private List<LLXianJinCard> getXianJinRecordList(BLEHandler handler,LPDeviceInfo serverDeviceInfo) throws LPException, BLException
   {
	   
	   List<LLXianJinCard> tmp = new ArrayList<LLXianJinCard>();
	   
	   if(serverDeviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA)){
		   //消费
		   for(int i = 1; i < 11;i++)
		   {
			   LLXianJinCard llXianJinCard_1 = mLepaoProtocalImpl.XIANJIN_getSmartCardTradeRecord(i,serverDeviceInfo);
			  if(!llXianJinCard_1.isVaild()){
				  OwnLog.e(TAG, "非法");
				   break;
			   }
			   else if(!llXianJinCard_1.isHasRecord()){
				  OwnLog.e(TAG, "无记录");
				   break;
			   }
			   else if(llXianJinCard_1.getXianjinAmount_6().equals("0.00") && llXianJinCard_1.getXianjinAmount_other_6().equals("000000000000") )
			   {
				   tmp.remove(llXianJinCard_1);
			   }
			   else if(llXianJinCard_1.isVaild() && llXianJinCard_1.isHasRecord())
			   {
				   Message msg = handler.obtainMessage(MSG_GET__XIANJIN_TRADE_RECORD_ASYNC,llXianJinCard_1);
				   msg.sendToTarget();
				   tmp.add(llXianJinCard_1);
			   }
		   }
		   
	   	}else{ //数码视讯以外
		   //消费
		   for(int i = 1; i < 11;i++)
		   {
			   LLXianJinCard llXianJinCard_1 = mLepaoProtocalImpl.XIANJIN_getSmartCardTradeRecord(i,serverDeviceInfo);
			  if(!llXianJinCard_1.isVaild()){
				  OwnLog.e(TAG, "非法");
				   break;
			   }
			   else if(!llXianJinCard_1.isHasRecord()){
				  OwnLog.e(TAG, "无记录");
				   break;
			   }
			   else if(llXianJinCard_1.getXianjinAmount_6().equals("0.00") && llXianJinCard_1.getXianjinAmount_other_6().equals("000000000000") )
			   {
				   tmp.remove(llXianJinCard_1);
			   }
			   else if(llXianJinCard_1.isVaild() && llXianJinCard_1.isHasRecord())
			   {
				   Message msg = handler.obtainMessage(MSG_GET__XIANJIN_TRADE_RECORD_ASYNC,llXianJinCard_1);
				   msg.sendToTarget();
				   tmp.add(llXianJinCard_1);
			   }
		   }
		   //圈存
		   for(int j = 1; j < 11;j++)
		   {
			   LLXianJinCard llXianJinCard_2 = mLepaoProtocalImpl.XIANJIN_getQuancunTradeRecord(j,serverDeviceInfo);
			   if(llXianJinCard_2.isVaild() && llXianJinCard_2.isHasRecord())
			   {
				   Message msg = handler.obtainMessage(MSG_GET__XIANJIN_TRADE_RECORD_ASYNC,llXianJinCard_2);
				  OwnLog.e(TAG, "日期："+llXianJinCard_2.getData_3());
				  OwnLog.e(TAG, "时间："+llXianJinCard_2.getTime_3());
				   msg.sendToTarget();
				   tmp.add(llXianJinCard_2);
			   }
			   else if(!llXianJinCard_2.isVaild()){
				  OwnLog.e(TAG, "非法");
				   return tmp;
			   }
			   else if(!llXianJinCard_2.isHasRecord()){
				  OwnLog.e(TAG, "无记录");
				   return tmp;
			   }
		   }
	   }
	  
	   return tmp;
   }
   
   
   public void onActivityResultProess(int requestCode, int resultCode, Intent data)
   {
	    // User chose not to enable Bluetooth.
//		if (requestCode == BLEHandler.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//			return;
//		}
//		if(requestCode == BLEHandler.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK)
//		{
//			Log.d(TAG, ".....................re................................");
//			if(mHandler != null)
//				scanForConnnecteAndDiscovery();//mHandler);
//			else 
//				Log.e(TAG, "mHandler is null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//		}
   }
   
   
   /**
    * 自定义线程
    * @author Administrator
    *
    */
   private class ProessThread extends Thread
   {
	   private Context mContext;
	   //private BLEHandler mHandler;
	   private int index;
	   
	   private LPDeviceInfo serverDeviceInfo = null;
	   
	   private byte[] oad_data;
	   private byte notificationUID;
	   private byte[] title;
	   private byte[] text;
	   
	   public int getIndex()
	   {
		   return index;
	   }
	   
	   public LPDeviceInfo getDeviceInfo()
	   {
		   return serverDeviceInfo;
	   }
	   
	   public Context getContext()
	   {
		   return mContext;
	   }
	   
	   public ProessThread(Context context,int index) 
	   {
		   mContext = context;  
		   this.index = index;
	   }
	   
	   public ProessThread(Context context,int index,byte[] data) 
	   {
		   mContext = context; 
		   this.index = index;
		   this.oad_data=data;
	   }

	   public ProessThread(Context context,byte notificationUID,int index,byte[] title,byte[] text) 
	   {
		   mContext = context;  
		   this.index = index;
		   this.notificationUID=notificationUID;
		   this.title=title;
		   this.text=text;
	   }
	   
	   public ProessThread(Context context,int index,final LPDeviceInfo deviceInfo) 
	   {
		   mContext = context;  
		   this.index = index;
		   serverDeviceInfo = deviceInfo;
	   }
	   
	   @Override
	   public void run()
	   {
		   super.run();
		    if(mHandler == null)
		    {
		    	 OwnLog.e(TAG, "init failed mHandler is null!!!!!!!!!!!!!!!!!!");
		         return;
		    }
		    
		    if(!init(mContext))//, mHandler))
	    	{
			    OwnLog.e(TAG, "init failed!!!!!!!!!!!!!!!!!!");
	    		return;
	    	}
		   
	    	if(state != STATE_DISCOVERED)
	    	{
	    		Log.e(TAG, "not connect!!!!!!!!!!!!!!!!!!");
	    		mHandler.sendEmptyMessage(MSG_BLE_NOT_CONNECT);
	    		return;
	        }
	    	
			try 
			{
				Message msg;
				switch (index)
				{
				    case INDEX_SYNC_SPORT_DATA_NEW:
				    	Log.d(TAG, ".................INDEX_SYNC_SPORT_DATA_NEW................");
				    	getSportDataNew();
				    	break;
				    case INDEX_UNBOUND_DEVICE:  //执行解绑
				    	Log.e(TAG, ".................INDEX_UNBOUND_DEVICE................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.obj = mLepaoProtocalImpl.formatDevice();
				    	msg.arg1 = INDEX_UNBOUND_DEVICE;
				    	msg.sendToTarget();
//					try {
//						Log.e(TAG, "与"+getmBluetoothDevice().getAddress()+"解除配对");
//						Log.e(TAG, "解除配对:"+removeBond(getmBluetoothDevice().getClass(),getmBluetoothDevice()));
//					} catch (Exception e) {
//						Log.e(TAG, "解除配对失败");
//					}
				    	break;
//				    case INDEX_GET_ENERGY:
//				    	msg = mHandler.obtainMessage();
//				    	msg.obj = mLepaoProtocalImpl.getCharged();
//				    	msg.sendToTarget();
//				    	break;
				    case INDEX_GAT_ALL_INFO_NEW:
				    	Log.i(TAG, ".................INDEX_GAT_ALL_INFO_NEW................");
				    	msg = mHandler.obtainMessage();
						msg.what = MSG_BLE_DATA;
						msg.arg1 = INDEX_GAT_ALL_INFO_NEW;
						msg.obj = mLepaoProtocalImpl.getAllDeviceInfoNew();
			    		msg.sendToTarget();
						break;
				    case INDEX_GET_MODEL:
				    	Log.d(TAG, ".................INDEX_GET_MODEL................");
				    	msg = mHandler.obtainMessage();
						msg.what = MSG_BLE_DATA;
						msg.arg1 = INDEX_GET_MODEL;
						msg.obj = mLepaoProtocalImpl.getModelName();
			    		msg.sendToTarget();
						break;
				    case INDEX_REGIESTER_INFO_NEW:
				    	Log.d(TAG, ".................INDEX_REGIESTER_INFO_NEW................");
				    	msg = mHandler.obtainMessage();
						msg.what = MSG_BLE_DATA;
						msg.arg1 = INDEX_REGIESTER_INFO_NEW;
					    LPUser lpUser = new LPUser();
					    lpUser.setAge(serverDeviceInfo.userAge);
					    lpUser.setHeight(serverDeviceInfo.userHeight);
					    lpUser.setSex(serverDeviceInfo.userGender);
					    lpUser.setUserId(serverDeviceInfo.userId);
					    lpUser.setWeight(serverDeviceInfo.userWeight);
					    lpUser.setRecoderStatus(1);
					    lpUser.setUserId(serverDeviceInfo.userId);
						msg.obj = mLepaoProtocalImpl.registerNew(lpUser);
						msg.sendToTarget();
				    	break;
				    case INDEX_SET_DEVICEID_NEW:
				    	Log.d(TAG, ".................INDEX_SET_DEVICEID_NEW................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SET_DEVICEID_NEW;
				    	msg.obj = mLepaoProtocalImpl.setDeviceIdNew(serverDeviceInfo.deviceId);
				    	msg.sendToTarget();
				    	break;
				    // 请求绑定
				    case INDEX_REQUEST_BOUND:
				    	Log.d(TAG, ".................INDEX_REQUEST_BOUND................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_REQUEST_BOUND;
				    	msg.obj = mLepaoProtocalImpl.requestbound();
				    	Log.d(TAG, ".................INDEX_REQUEST_BOUND................"+msg.obj);
				    	msg.sendToTarget();
				    	break;
				    //  发送设置手表时间指令 
				    case INDEX_SET_DEVICE_TIME:
				    	Log.d(TAG, ".................INDEX_SET_DEVICE_TIME................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SET_DEVICE_TIME;
//				    	msg = mHandler.obtainMessage(INDEX_SET_DEVICE_TIME);
				    	msg.obj = mLepaoProtocalImpl.setDeviceoffsetTime();
				    	msg.sendToTarget();
				    	break;
				    //  发送设置闹钟指令
				    case INDEX_SET_DEVICE_CLOCK:
				    	Log.d(TAG, ".................INDEX_SET_DEVICE_CLOCK................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SET_DEVICE_CLOCK;
				    	msg.obj = mLepaoProtocalImpl.setClock(serverDeviceInfo);
				    	msg.sendToTarget();
				    	break;
				    // 发送设置久坐提醒指令  
				    case INDEX_SET_DEVICE_LONGSIT:  //
				    	Log.d(TAG, ".................INDEX_SET_DEVICE_LONGSIT................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SET_DEVICE_LONGSIT;
				    	msg.obj = mLepaoProtocalImpl.setLongSitRemind(serverDeviceInfo);
				    	msg.sendToTarget();
				    	break;
					// 发送运动目标提醒指令
					case INDEX_STEP_TARGET:
						Log.d(TAG, ".................INDEX_STEP_TARGET................");
						msg = mHandler.obtainMessage();
						msg.what = MSG_BLE_DATA;
						msg.arg1 = INDEX_STEP_TARGET;
						msg.obj = mLepaoProtocalImpl.setSportTarget(serverDeviceInfo);
						msg.sendToTarget();
						break;
					// 发送省电模式指令
				    case INDEX_POWER:
				    	Log.d(TAG, ".................INDEX_POWER................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_POWER;
				    	msg.obj = mLepaoProtocalImpl.setPower(serverDeviceInfo);
				    	msg.sendToTarget();
				    	break;
				    	// 请求绑定_美国
				    case INDEX_REQUEST_BOUND_FIT:
				    	Log.d(TAG, ".................INDEX_REQUEST_BOUND_FIT................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_REQUEST_BOUND;
				    	msg.obj = mLepaoProtocalImpl.requestbound_fit();
				    	msg.sendToTarget();
				    	break;
				    // 请求绑定_美国(循环)
				    case INDEX_REQUEST_BOUND_RECY:
				    	Log.d(TAG, ".................INDEX_REQUEST_BOUND_RECY................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_REQUEST_BOUND;
				    	msg.obj = mLepaoProtocalImpl.requestbound_recy();
				    	msg.sendToTarget();
				    	break;
				    // 发送设置抬手显示指令     
				    case INDEX_SET_HAND_UP:  //
				    	Log.d(TAG, ".................INDEX_SET_HAND_UP................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SET_HAND_UP;
				    	msg.obj = mLepaoProtocalImpl.setHandUp(serverDeviceInfo);
				    	msg.sendToTarget();
				    	break;
				    case INDEX_GET_CARD_NUMBER:
				    	Log.d(TAG, ".................INDEX_GETCARDNUMBER................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_GET_CARD_NUMBER;
				    	msg.obj = mLepaoProtocalImpl.get_cardnum();
				    	msg.sendToTarget();
				    	break;
				    case INDEX_SET_NAME:  //
				    	Log.d(TAG, ".................INDEX_SET_NAME................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SET_NAME;
				    	msg.obj = mLepaoProtocalImpl.set_name(serverDeviceInfo);
				    	msg.sendToTarget();
				    	break;
				    case INDEX_SEND_BAND_RING:
				    	Log.d(TAG, ".................INDEX_SEND_BAND_RING................");
				    	mLepaoProtocalImpl.setBandRing();
				    	break;
				     // 发送消息提醒指令  
				    case INDEX_SEND_NOTIFICATION:  
				    	Log.d(TAG, ".................INDEX_SEND_NOTIFICATION................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SEND_NOTIFICATION;
				    	msg.obj = mLepaoProtocalImpl.setNotification(oad_data);
				    	msg.sendToTarget();
				    	break;
				    /**for 一卡通相关的代码 START**/
				    case INDEX_OPEN_SMC:
				    	msg = mHandler.obtainMessage(INDEX_OPEN_SMC,mLepaoProtocalImpl.openSmartCard());
				    	msg.sendToTarget();
				    	break;
				    case INDEX_AID_SMC:
				    	msg = mHandler.obtainMessage(INDEX_AID_SMC,mLepaoProtocalImpl.AIDSmartCard(serverDeviceInfo));
				    	msg.sendToTarget();
				    	break;
				    	
				    case INDEX_AID_SMC_YC1:
				    	msg = mHandler.obtainMessage(INDEX_AID_SMC_YC1,mLepaoProtocalImpl.AID_step1());
				    	msg.sendToTarget();
				    	break;
				    	
				    case INDEX_AID_SMC_YC2:
				    	msg = mHandler.obtainMessage(INDEX_AID_SMC_YC2,mLepaoProtocalImpl.AID_step2());
				    	msg.sendToTarget();
				    	break;
				    	
				    case INDEX_SCHOOL_ID:
				    	msg = mHandler.obtainMessage(INDEX_SCHOOL_ID,mLepaoProtocalImpl.School_ID());
				    	msg.sendToTarget();
				    	break;
				    	
				    case INDEX_SET_SMC_TRANSFER:
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SET_SMC_TRANSFER;
				    	msg.obj = mLepaoProtocalImpl.checkPin(serverDeviceInfo);
				    	msg.sendToTarget();
				    	break;
				    case INDEX_GET_SMC_BALANCE:
				    	msg = mHandler.obtainMessage(INDEX_GET_SMC_BALANCE,mLepaoProtocalImpl.readCardBalance(serverDeviceInfo));
				    	msg.sendToTarget();
				    	break;
				    case MSG_GET_SMC_TRADE_RECORD:
				    	msg = mHandler.obtainMessage(MSG_GET_SMC_TRADE_RECORD,getTradeRecordList(mHandler,serverDeviceInfo));
				    	msg.sendToTarget();
				    	break;
				    case INDEX_XIANJIN_RECORD:
				    	msg = mHandler.obtainMessage(INDEX_XIANJIN_RECORD,getXianJinRecordList(mHandler,serverDeviceInfo));
				    	msg.sendToTarget();
				    	break;
				    	
				    case INDEX_OPEN_FEIJIE:
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_OPEN_FEIJIE;
				    	msg.obj = mLepaoProtocalImpl.open7816Card();
				    	msg.sendToTarget();
				    	
				    case INDEX_ClOSE_FEIJIE:
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_ClOSE_FEIJIE;
				    	msg.obj = mLepaoProtocalImpl.close7816Card();
				    	msg.sendToTarget();
				    	
				    case INDEX_CLOSE_SMC:
				    	mLepaoProtocalImpl.closeSmartCard();
				    	break;
				    /** for 一卡通相关的代码 END   **/
				    // 发送消息提醒指令 (QQ)
				    case INDEX_SEND_QQ_NOTIFICATION:  
				    	Log.d(TAG, ".................INDEX_SEND_QQ_NOTIFICATION................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SEND_QQ_NOTIFICATION;
				    	mLepaoProtocalImpl.ANCS_Other((byte)4,(byte) 0, notificationUID, title,text);
				    	msg.sendToTarget();
				    	break;
				    	// 发送消息提醒指令 (短信)
				    case INDEX_SEND_MSG_NOTIFICATION:  
				    	Log.d(TAG, ".................INDEX_SEND_MSG_NOTIFICATION................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SEND_MSG_NOTIFICATION;
				    	mLepaoProtocalImpl.ANCS_MSG((byte)2,(byte) 0, notificationUID, title,text);
				    	msg.sendToTarget();
				    	break;
				    	// 发送消息提醒指令 (WX)
				    case INDEX_SEND_WX_NOTIFICATION:  
				    	Log.d(TAG, ".................INDEX_SEND_WX_NOTIFICATION................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SEND_WX_NOTIFICATION;
				    	mLepaoProtocalImpl.ANCS_Other((byte)3,(byte) 0, notificationUID, title,text);
				    	msg.sendToTarget();
				    	break;
				     	// 发送消息提醒指令 (连爱)
				    case INDEX_SEND_LINK_NOTIFICATION:  
				    	Log.d(TAG, ".................INDEX_SEND_LINK_NOTIFICATION................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SEND_LINK_NOTIFICATION;
				    	mLepaoProtocalImpl.ANCS_Other((byte)0,(byte) 0, notificationUID, title,text);
				    	msg.sendToTarget();
				    	break;
				    	// 发送消息提醒指令 (电话)
				    case INDEX_SEND_PHONE_NOTIFICATION:  
				    	Log.d(TAG, ".................INDEX_SEND_PHONE_NOTIFICATION................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SEND_PHONE_NOTIFICATION;
				    	mLepaoProtocalImpl.ANCS_PHONE((byte)1,(byte) 0, notificationUID, title,text);
				    	msg.sendToTarget();
				    	break;
				    	// 发送移除来电指令 (电话)
				    case INDEX_SEND_REMOVE_PHONE_NOTIFICATION:  
				    	Log.d(TAG, ".................INDEX_SEND_REMOVE_PHONE_NOTIFICATION................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SEND_REMOVE_PHONE_NOTIFICATION;
				    	mLepaoProtocalImpl.ANCS_remove_incall(ANCSCommand.CategoryIDIncomingCall,ANCSCommand.ANCS_APPNameID_Phone, notificationUID);
				    	msg.sendToTarget();
				    	break;
				    	// 发送未接来电指令 (电话)
				    case INDEX_SEND_MISS_PHONE_NOTIFICATION:  
				    	Log.d(TAG, ".................INDEX_SEND_MISS_PHONE_NOTIFICATION................");
				    	msg = mHandler.obtainMessage();
				    	msg.what = MSG_BLE_DATA;
				    	msg.arg1 = INDEX_SEND_MISS_PHONE_NOTIFICATION;
				    	mLepaoProtocalImpl.ANCS_MISSCALL(notificationUID,title,text);
				    	msg.sendToTarget();
				    	break;
				    case INDEX_SEND_OAD_HEAD:  //getFlasshHeadBack
				    	byte[] oad_head = new byte[17];
				    	//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
						System.arraycopy(oad_data, 0, oad_head, 1, 16);  
						Log.i(TAG, ".................INDEX_SEND_OAD_HEAD................");
						oad_head[0]=0x11;
				    		msg = mHandler.obtainMessage();
					    	msg.what = MSG_BLE_DATA;
					    	msg.arg1 = INDEX_SEND_OAD_HEAD;
					    	msg.obj =mLepaoProtocalImpl.getOADHeadBack(oad_head);
					    	Log.i(TAG, ".................mLepaoProtocalImpl.getOADHeadBack(oad_head);................");
					    	msg.sendToTarget();
				    	break;
				    case INDEX_SEND_OAD:
				    	Log.d(TAG, ".................INDEX_SEND_OAD................"+oad_data.length);
				    	byte[] oad_all = new byte[oad_data.length-16];
				    	System.arraycopy(oad_data, 16, oad_all, 0, oad_data.length-16);
				    	//返回一个可以继续OAD的
				    		msg = mHandler.obtainMessage();
					    	msg.what = MSG_BLE_DATA;
					    	msg.arg1 = INDEX_SEND_OAD;
					    	msg.obj = mLepaoProtocalImpl.sendOADAll(oad_all);
					    	msg.sendToTarget();
				    	break;
				    	
				    	
				    	
				    case INDEX_SEND_OAD_HEAD_BACK:  //getFlashHeadBack
				    	byte[] oad_head_back = new byte[17];
				    	//[1]:源数组； [2]:源数组要复制的起始位置； [3]:目的数组； [4]:目的数组放置的起始位置； [5]:复制的长度。 注意：[1] and [3]都必须是同类型或者可以进行转换类型的数组
						System.arraycopy(oad_data, 0, oad_head_back, 1, 16);  
						Log.i(TAG, ".................INDEX_SEND_OAD_HEAD_BACK................");
						oad_head_back[0]=0x11;
				    		msg = mHandler.obtainMessage();
					    	msg.what = MSG_BLE_DATA;
					    	msg.arg1 = INDEX_SEND_OAD_HEAD_BACK;
					    	msg.obj =mLepaoProtocalImpl.getOADHeadBack_new(oad_head_back);
					    	msg.sendToTarget();
				    	break;
				    case INDEX_SEND_OAD_BACK:
				    	Log.d(TAG, ".................INDEX_SEND_OAD_BACK................"+oad_data.length);
				    	byte[] oad_all_back = new byte[oad_data.length-16];
				    	System.arraycopy(oad_data, 16, oad_all_back, 0, oad_data.length-16);
				    	//返回一个可以继续OAD的
				    		msg = mHandler.obtainMessage();
					    	msg.what = MSG_BLE_DATA;
					    	msg.arg1 = INDEX_SEND_OAD;
					    	msg.obj = mLepaoProtocalImpl.sendOADback(oad_all_back);
					    	msg.sendToTarget();
				    	break;
				    case INDEX_SEND_FLASH_HEAD:
				    	   OwnLog.d(TAG, ".................INDEX_SEND_FLASH_HEAD................"+oad_data.length);
				    	    int flash_head_back = mLepaoProtocalImpl.getFlashHeadBack(oad_data);
				    		msg = mHandler.obtainMessage();
					    	msg.what = MSG_BLE_DATA;
					    	msg.arg1 = INDEX_SEND_FLASH_HEAD;
					    	msg.obj = flash_head_back;
					    	msg.sendToTarget();
				    	break;
				    case INDEX_SEND_FLASH_BODY:
				    	Log.d(TAG, ".................INDEX_SEND_FLASH_BODY................"+oad_data.length);
				    	int flash_body_back = mLepaoProtocalImpl.getFlashBodyBack(oad_data);
				    	//返回一个成功
				    		msg = mHandler.obtainMessage();
					    	msg.what = MSG_BLE_DATA;
					    	msg.arg1 = INDEX_SEND_FLASH_BODY;
					    	msg.obj = flash_body_back;
					    	msg.sendToTarget();
				    	break;
				    case INDEX_SEND_OAD_PERCENT:
				    	Log.d(TAG, ".................INDEX_SEND_OAD_PERCENT................");
				    	double cmd11 =mLepaoProtocalImpl.OAD_percent;
				    	break;
				    case INDEX_SEND_STEP:
				    	Log.d(TAG, ".................INDEX_SEND_STEP................");
				        msg = mHandler.obtainMessage();
			    	    msg.what = MSG_BLE_DATA;
			    	    msg.arg1 = INDEX_SEND_STEP;
			    	    msg.obj = mLepaoProtocalImpl.resetSportDataNew(serverDeviceInfo.stepDayTotals);;
			    	    msg.sendToTarget();
				        break;
				    case INDEX_SEND_0X5F:
					   OwnLog.d(TAG, ".................INDEX_SEND_0X5F................");
					    mLepaoProtocalImpl.keepstate();
					    break;    
				    case INDEX_SEND_DATA:
					   OwnLog.d(TAG, ".................INDEX_SEND_DATA................");
				        msg = mHandler.obtainMessage();
			    	    msg.what = MSG_BLE_DATA;
			    	    msg.arg1 = INDEX_SEND_DATA;
			    	    msg.obj = mLepaoProtocalImpl.test_senddata(oad_data);
			    	    msg.sendToTarget();
					    break;   
				    case INDEX_SEND_DATA_CARD:
					    OwnLog.d(TAG, ".................INDEX_SEND_DATA_CARD................");
				        msg = mHandler.obtainMessage();
			    	    msg.what = MSG_BLE_DATA;
			    	    msg.arg1 = INDEX_SEND_DATA_CARD;
			    	    msg.obj = mLepaoProtocalImpl.senddata(oad_data);
			    	    msg.sendToTarget();
					    break; 
				    case INDEX_GET_DEVICEID:
					   OwnLog.d(TAG, ".................INDEX_GET_DEVICEID................");
				        msg = mHandler.obtainMessage();
			    	    msg.what = MSG_BLE_DATA;
			    	    msg.arg1 = INDEX_GET_DEVICEID;
			    	    msg.obj = mLepaoProtocalImpl.getDeviceId();
			    	    msg.sendToTarget();
					    break;  
				    default:
					    break;
					    
				}
			}
			catch (BLException e) 
			{
//				Log.e(TAG, "send data error!!!!!!!!!!!!!!!!!! 直接断开蓝牙");
				mHandler.sendEmptyMessage(MSG_DATA_ERROR);
//				release();
				Log.e(TAG, e.getMessage());
				clearProess();
				
				return;
				
			}
			catch (LPException e)
			{
//				Log.e(TAG, "resp data error!!!!!!!!!!!!!!!!!! 直接断开蓝牙");
				mHandler.sendEmptyMessage(MSG_DATA_ERROR);
//				release();
				Log.e(TAG, e.getMessage());
//				/connectNextDevice();
				clearProess();
				
				return;
			}
			
			synchronized (threadQueue)
			{
				if(threadQueue != null && !threadQueue.isEmpty())
				{
					threadQueue.remove();
					if(threadQueue != null && !threadQueue.isEmpty())
					{
						threadQueue.element().start();
					}
				}
			}
	   }
   }
   
   public void resetDefaultState()
   {
	   this.state = STATE_DISCONNECTED;
   }

   public String getCurrentDeviceMac() {
	   return currentDeviceMac;
   }

   public void setCurrentDeviceMac(String currentDeviceMac) {
	   this.currentDeviceMac = currentDeviceMac;
   }
   
   public boolean isConnectedAndDiscovered()
   {
	   return state == STATE_DISCOVERED;
   }
   
   public boolean isConnecting()
   {
	   return state == STATE_CONNECTING;
   }

   public BluetoothDevice getmBluetoothDevice() {
	   return mBluetoothDevice;
   }


   public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
	   this.mBluetoothDevice = mBluetoothDevice;
   }

   public LepaoProtocalImpl getmLepaoProtocalImpl()
   {
	   return mLepaoProtocalImpl;
   }

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intentFilter.addAction(BLEWapper.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BLEWapper.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BLEWapper.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BLEWapper.ACTION_GATT_CONNECTED_FAILED);
		intentFilter.addAction(BLEWapper.ACTION_GATT_CONNECTING);
		return intentFilter;
	}
   /**
    * 每组运动数据读完后的同步保存方法。每次运动数据读取时，因数据量大，而硬件处理能力有限，是
    * 分批次发过来的，应用层每取一次返回的可能是若干条运动数据，取N次直到设备中数据全部读到为止。
    * <p>
    * 子类可重写本方法以便实现此功能。
    * 
    * @param originalSportDatas
    */
   protected void saveSportSync2DB(List<LPSportData> originalSportDatas)
   {
	   // default do nothing
   }
   

   public void unregisterReciver()
   {
	   mContext.unregisterReceiver(mGattUpdateReceiver);
	   HAS_REGISTER_RECIVER = false;
   }
   
   public boolean getBLEReciever()
   {
		return HAS_REGISTER_RECIVER;
   }
   
//   @Override
//   public IBinder onBind(Intent intent) {
//	    return null;
//   }
   
//   @Override
//   public void onCreate() {
//	   super.onCreate();
//	  OwnLog.i(TAG, "BLEProvider的服务启动了！");
//	   }
   


}
