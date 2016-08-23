package com.example.android.bluetoothlegatt.proltrol;

import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.example.android.bluetoothlegatt.proltrol.dto.LPUser;

import java.util.List;

public interface LepaoProtocol
{
	
	/**  获取设备ID*/
//	public  String getDeviceId() throws BLException,LPException;
	
	/** 获取历史记录*/
//	 List<LPSportRecorder> getSportRecorder() throws BLException ,LPException;
	
	/**................................. 以下是最新版指令.............................*/
	/** 获取所有用户信息*/
	 LPDeviceInfo getAllDeviceInfoNew() throws BLException ,LPException;
	
	/**请求绑定*/
	  int requestbound() throws BLException,LPException;
	
	/**注册信息，激活设备*/
	  int registerNew(LPUser userInfo) throws BLException,LPException;
	
	/**设置运动数据*/
	  int resetSportDataNew(int step) throws BLException,LPException;
	
	/**获取运动数据*/
	  List<LPSportData> getSportDataNew(int offset, int length,int devicetime) throws BLException,LPException;
	

	/** 初始化设备*/
	 boolean formatDevice() throws BLException,LPException;
	
	/** 设置闹钟    */
	 int setClock(LPDeviceInfo deviceInfo)  throws BLException,LPException;
	
	/** 设置久坐提醒    */
	 int setLongSitRemind(LPDeviceInfo deviceInfo)  throws BLException,LPException;
	
	/** 设置运动目标   */
	 LPDeviceInfo setSportTarget(LPDeviceInfo deviceInfo)  throws BLException,LPException;
	
	/** 设置抬手显示   */
	 int setHandUp(LPDeviceInfo deviceInfo)  throws BLException,LPException;
	
	/** 设置消息提醒   */
	 int setNotification(byte[] data)  throws BLException,LPException;
	
	/**
	 * ** 设置除电话外其他提醒*
	 * @param type             ANCS_AppID
	 * @param status           EventID 0：Add状态    1：Modified状态   2：Removed状态
	 * @param notificationUID  消息的序列号
	 * @param title            标题
	 * @param text             内容
	 * @throws BLException
	 * @throws LPException
	 */
	public void ANCS_Other(byte type,byte status,byte notificationUID,byte[] title,byte[] text)  throws BLException,LPException;
	
	/**
	 * ** 设置除电话外其他提醒*
	 * @param Category_status             ANCS_CategoryID  接电话 和 挂电话
	 * @param notificationUID    消息的序列号
	 * @param title
	 * @param text
	 * @throws BLException
	 * @throws LPException
	 */
	public void ANCS_PHONE(byte Category_status,byte EventID_status,byte notificationUID,byte[] title,byte[] text)  throws BLException,LPException;
	
	/** 获取OAD头文件反馈*/
	public int getOADHeadBack(byte[] data) throws BLException,LPException;
	
	
	public int sendOADAll(byte[] data) throws BLException,LPException;
	
	/** 获取Flash头文件反馈*/
	public int getFlashHeadBack(byte[] data) throws BLException,LPException;
	
	/** 获取Flash文件反馈*/
	public int getFlashBodyBack(byte[] data) throws BLException,LPException;
	

	public boolean setDeviceIdNew(String str) throws BLException,LPException;

	/**设置设备时间*/  //测试
	public int setDeviceTime() throws BLException,LPException;

}
