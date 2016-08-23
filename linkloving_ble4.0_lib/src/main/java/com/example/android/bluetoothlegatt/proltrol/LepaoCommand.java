package com.example.android.bluetoothlegatt.proltrol;

public interface LepaoCommand {
//	static final byte COMMAND_IS_STATUS = (byte) 0xA0;      // 返回的是一个状态
//	static final byte COMMAND_IS_DATA = (byte) 0xA1;        // 返回的是一段数据
	
	static final byte COMMAND_HEAD = (byte) 0xAF;
	static final byte COMMAND_UP_16 = 0x16;                 // 加速
	static final byte COMMAND_DEVICEINFO = 0x30;            // 获取modelname等
	
	static final byte COMMAND_GET_CHARGED = 0x01;           // 获取电量
//	static final byte COMMAND_GET_SPORT_DATA = 0xa0;        // 获取运动数据长度（参数为0） 或者 获取运动数据（两个参数，short偏移，byte长度）
	static final byte COMMAND_CLEAR = 0x03;                 // 设置手环时间，同时清空手环的运动数据
	static final byte COMMAND_GET_TIME = 0x04;              // 获取手环被设置的时间
	static final byte COMMAND_SET_TASK = 0x05;              // 设置任务量
	static final byte COMMAND_SET_TIME_OFFEST = 0x06;       // 设置手环时间 （特殊时区）
	static final byte COMMAND_REGISTER = 0x07;              // 注册信息，激活设备
	static final byte COMMAND_GET_USER_INFO = 0x08;         // 获取用户信息
	static final byte COMMAND_SET_STEP = 0x09;              // 设置运动步数（oad）
//	static final byte COMMAND_MIC_OK = 0x0B;                // 告诉设备MIC是OK的
	static final byte COMMAND_SetOledOnTimeZone = 0x11;     // 设置抬手
	static final byte COMMAND_SETNOTIFICATION = 0x12;       // 设置消息提醒
	static final byte COMMAND_GET_ALL_USER_INFO = 0x13;     // 获取所有用户信息
	static final byte COMMAND_GET_SPORT_RECORDER  = 0x02;   // 获取运动数据记录
	static final byte COMMAND_GET_TODAY_SPORT = 0x15;       // 获取今天的运动数据
	
	
	static final byte COMMAND_SET_ALL_DECICE_INFO = 0x16;   //设置所有的设备信息
	static final byte COMMAND_SET_POWER_DECICE = 0x18;      //设置省电模式
	static final byte COMMAND_BAND_RING = 0x19;             //设置手环震动
	static final byte COMMAND_ANCS_PUSH = 0x20;             //消息提醒
	
	static final byte COMMAND_SET_ALARM = 0x0C;             // 设置闹钟
	static final byte COMMAND_GET_ALARM = 0x0D;             // 获取可用闹钟个数(参数 0xFF) 或者 闹钟信息(参数 闹钟索引)
	static final byte COMMAND_GET_VERSION = 0x0E;           // 获取设备版本号，是一个String
	static final byte COMMAND_SET_MOTION_REMIND = 0x0F;     // 运动提醒（久坐提醒）
	static final byte COMMAND_REQUEST_BOUND = 0x10;         // 请求绑定
	//static final byte COMMAND_SLEEP_PARAMS = 0x11;        // 设置睡眠参数
	static final byte COMMAND_GET_RAW_DATA = 0x1F;          // 获取原始数据
	static final byte COMMAND_GET_CARD_NUMBER = (byte) 0x85;// 获取设置设备卡号
	static final byte COMMAND_SET_NAME = 0x5C;              // 设置设备名称
	static final byte COMMAND_GET_DEVICE_ID = 0x5F;         // 设备ID，8byte
	
	//*********************卡片********************//
	static final byte COMMAND_CONTROL_CARD = (byte) 0x90;   // 开卡(上电)  关卡(断电)
	static final byte COMMAND_AID_CARD = (byte) 0x91;       //AID
	
	static final byte COMMAND_FORMAT_DEVICE = 0x5E;         // 初始化设备(解除绑定)
	
	static final byte COMMAND_FACTORY_TEST = 0x00;          // 工厂测试命令
}
