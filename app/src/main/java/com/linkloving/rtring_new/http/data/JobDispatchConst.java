/**
 * JobDispatchConst.java
 * @author Jason Lu
 * @date 2014-2-27
 * @version 1.0
 */
package com.linkloving.rtring_new.http.data;

/**
 * @author Jason
 * 
 * 作业调度常量表 
 */
public interface JobDispatchConst
{
	/** 注册相关作业调度id */
	int LOGIC_REGISTER = 1;
	
	/** 运动数据相关调度id */
	int REPORT_BASE = 11;
	
	/** 运动数据相关调度id */
	int REPORT_DAY_SYNOPIC = 12;
	
	/** 用户头像上传调度id */
	int AVATAR_UPLOAD = 24;
	
	/** 社交功能调度id */
	int SNS_BASE = 3;
	
	/** 第三方登录调度id */
	int THIRD_PARTY_LOGIC_BASE = 4;
	
	/** 用户签名调度id */
	int USER_SIGNATURE = 5;
	
	/** 蓝牙固件调度id */
	int FIRMWARE_BASE = 6;
	
	/** 推送消息调度id */
	int USER_MESSAGE_PUSH_BASE = 7;
}
