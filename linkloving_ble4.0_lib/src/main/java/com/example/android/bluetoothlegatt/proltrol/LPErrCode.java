package com.example.android.bluetoothlegatt.proltrol;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class LPErrCode {
	public final static int LP_OK = 0x00;
	public final static int LP_INIT_ERR = -1;         // 初始化错误
	public final static int LP_ILLEGAL_CMD = -2;      // 非法命令
	public final static int LP_ILLEGAL_OP = -3;       // 非法操作
	public final static int LP_ILLEGAL_DATA = -4;     // 非法数据
	public final static int LP_CONVERT_ERR = -5;      // 转换类型出错
	public final static int LP_RESPONSE_ERR = -6;     // 回复类型错误（数据、状态混乱）
	public final static int LP_HARDWARE_ERR = -7;     // 获取硬件资源错误
	public final static int LP_INTERRUPT = -10;       // 录音过程被打断，例如突然拔出机芯
	public final static int LP_CANT_PARSE = -11;      // 无法解析机芯返回波形
	public final static int LP_PHONE_CHECKSUM_ERR = -12;  // 手机接收到的信号，没有通过CheckSum验证
	
	// 以下都是从机芯接收到信号并成功解析出来，但是机芯端反回了错误码
	public final static int LP_DEVICE_CHECKSUM_ERR = 0x11;          // 机芯那边没有通过CheckSum验证
	public final static int LP_DEVICE_TIMEOUT = 0x12;               // 机芯解析波形时，在一段时间内没有等到数据
	public final static int LP_DEVICE_BIT_ERROR = 0x13;             // 机芯解析波形时，发现bit错误
	public final static int LP_DEVICE_ILLEGAL_PARAM = 0x14;         // 机芯发现非法参数
	public final static int LP_DEVICE_CMD_NOT_SUPPORT = 0x15;       // 机芯不支持此命令
	public final static int LP_DEVICE_CMD_DATA_OUT_OF_RANGE = 0x16; // 机芯发现请求命令溢出
	public final static int LP_DEVICE_IS_ACTIVE = 0x1D;             // 机芯发现已经激活了
	public final static int LP_DEVICE_IS_NOT_ACTIVE = 0x1E;         // 机芯发现还没有激活，但是命令必须在激活后才能发送
	public final static int LP_DEVICE_ILLEGAL = 0x1F;               // 机芯发现非法命令
	
	private static Map<Integer, String> messageMap = new HashMap< Integer, String >();
	static {
		messageMap.put(LP_INIT_ERR, "INIT_ERR");
		messageMap.put(LP_ILLEGAL_CMD, "ILLEGAL_CMD");
		messageMap.put(LP_ILLEGAL_OP, "ILLEGAL_OP");
		messageMap.put(LP_ILLEGAL_DATA, "ILLEGAL_DATA");
		messageMap.put(LP_CONVERT_ERR, "CONVERT_ERR");
		messageMap.put(LP_RESPONSE_ERR, "RESPONSE_ERR");
		messageMap.put(LP_HARDWARE_ERR, "HARDWARE_ERR");
		messageMap.put(LP_INTERRUPT, "INTERRUPT");
		messageMap.put(LP_CANT_PARSE, "CANT_PARSE");
		messageMap.put(LP_PHONE_CHECKSUM_ERR, "PHONE_CHECKSUM_ERR");
		
		messageMap.put(LP_DEVICE_CHECKSUM_ERR, "DEVICE_CHECKSUM_ERROR");
		messageMap.put(LP_DEVICE_TIMEOUT, "DEVICE_TIMEOUT");
		messageMap.put(LP_DEVICE_BIT_ERROR, "DEVICE_BIT_ERROR");
		messageMap.put(LP_DEVICE_ILLEGAL_PARAM, "DEVICE_ILLEGAL_PARAM");
		messageMap.put(LP_DEVICE_CMD_NOT_SUPPORT, "DEVICE_CMD_NOT_SUPPORT");
		messageMap.put(LP_DEVICE_CMD_DATA_OUT_OF_RANGE, "DEVICE_CMD_DATA_OUT_OF_RANGE");
		messageMap.put(LP_DEVICE_IS_ACTIVE, "DEVICE_IS_ACTIVE");
		messageMap.put(LP_DEVICE_IS_NOT_ACTIVE, "DEVICE_IS_NOT_ACTIVE");
		messageMap.put(LP_DEVICE_ILLEGAL, "DEVICE_ILLEGAL");
	}

	public static String getMessage(int key){
		String  ans = messageMap.get(key);
		return ans == null ? ("Unknown Error " + key) : ans;
	}
}
