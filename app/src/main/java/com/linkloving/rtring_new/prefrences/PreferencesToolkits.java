package com.linkloving.rtring_new.prefrences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.android.bluetoothlegatt.proltrol.dto.LLTradeRecord;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.google.gson.Gson;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.logic.UI.launch.LoginInfo;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.devicebean.LocalInfoVO;
import com.linkloving.rtring_new.prefrences.devicebean.ModelInfo;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Preferences辅助类.
 * 
 * @author Jack Jiang, 2014-04-01
 * @since 2.5
 */
public class PreferencesToolkits
{
	private final static String TAG = PreferencesToolkits.class.getSimpleName();
	
	/** 存储用户最近登陆用户名的key标识常量（本Shared Preferences目前只在LoginActivity内有效果哦） */
	public final static String SHARED_PREFERENCES_KEY_LOGIN$NAME = "name";
	/** 
	 * 存储常量的Shared Preferences标识常量（根据Android的Shared Preferences原理，如果不指名名字，则
	 * 用的是对应Activity的包名加类名作为名称，那么其它activity就很难取到罗）  */
	public final static String SHARED_PREFERENCES_SHAREDPREFERENCES = "__sharedpreferences__";
	
	public final static String KEY_LOCAL_VO = "__deviceinfo__";

	public final static String KEY_DEVICE_INFO = "__deviceSetting__";

	/** 
	 * 存储用户登陆成功后服务端端返回的完整个人信息的Shared Preferences标识常量（根据Android的
	 * Shared Preferences原理，如果不指名名字，则用的是对应Activity的包名加类名作为名称，那么其它activity就很难取到罗）  */
	public final static String KEY_LOGIN_USER_INFO = "__userinfo__";
	/** 
	 * 存储用户登陆登陆信息（包括登陆名、密码）的Shared Preferences标识常量（根据Android的
	 * Shared Preferences原理，如果不指名名字，则用的是对应Activity的包名加类名作为名称，那么其它activity就很难取到罗）  */
//	public final static String KEY_LOGIN_LOGIN_INFO2 = "__logininfo__";
	
	public final static String KEY_LOGIN_LOGIN_INFO_Last = "__logininfo5__";

	public final static String KEY_LOGIN_LOGIN_INFO_Lastphone = "__logininfophone__";

	/**
	 * 是否上传到google健康
	 */
	public final static String KEY_GOOGLE_FIT = "__googlefit__";
	/**
	 * 用户设置信息（久坐提醒，闹钟，运动目标）
	 */
	public final static String KEY_USER_SETTING_INFO = "__usersetting__";

	/**
	 * 单位换算
	 */
	public final static String KEY_USER_Unit_INFO = "__usersettingUnit__";

	/** 
	 * 存储“是否在主界面上显示可下拉刷新的提示”的key标识常量（对应的Shared Preferences标识常量
	 * 是 {@link #SHARED_PREFERENCES_SHAREDPREFERENCES} ） */
	public final static String KEY_NEED_SHOW_FRAGMENT_TIP = "__need_show_fragment_tip__";
	
	/** 
	 * 存储“是否在主界面上显示升级OAD的提示”的key标识常量（对应的Shared Preferences标识常量
	 * 是 {@link #SHARED_PREFERENCES_SHAREDPREFERENCES} ） */
	public final static String KEY_NEED_SHOW_OAD = "__need_show_oad__";
	
	/** 
	 * 存储“单位”的key标识常量（对应的Shared Preferences标识常量
	 * 是 {@link #SHARED_PREFERENCES_SHAREDPREFERENCES} ） */
	public final static String KEY_UNIT_TYPE = "__unit_type__";

	public static String SHAREDPREFERENCES_NAME = "first_pref";

	public final static String KEY_LNT_USERNAME = "__lntusername__";

	/**
	 * 用户目标设置====预存金额
	 */
	public final static String KEY_GOAL_MONEY = "__goal_money__";
	/**
	 * 用户目标设置====步数
	 */
	public final static String KEY_GOAL_STEP = "__goal_step__";
	/**
	 * 用户目标设置====里程
	 */
	public final static String KEY_GOAL_DISTANCE = "__goal_dis__";
	/**
	 * 用户目标设置====卡路里
	 */
	public final static String KEY_GOAL_CAL = "__goal_cal__";
	/**
	 * 用户目标设置====锻炼
	 */
	public final static String KEY_GOAL_RUN = "__goal_run__";
	/**
	 * 用户目标设置====睡眠
	 */
	public final static String KEY_GOAL_SLEEP = "__goal_sleep__";
	/**
	 * 用户目标设置====体重
	 */
	public final static String KEY_GOAL_WEIGHT = "__goal_weight__";


	public static SharedPreferences getAppDefaultSharedPreferences(Context context, boolean canWrite) throws Exception 
	{
		if(context == null)
		{
			throw new Exception("context 为空");
		}
		return context.getSharedPreferences(
				SHARED_PREFERENCES_SHAREDPREFERENCES, canWrite?Context.MODE_WORLD_WRITEABLE:Context.MODE_WORLD_READABLE);
	}
	
	/**
	 * @param activity 参考示例
	 * @return
	 */
	public static void save_googlefit(Context activity,boolean up2google)
	{
			SharedPreferences savedevice;
			try 
			{
				savedevice = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=savedevice.edit();
				namePref.putBoolean(KEY_GOOGLE_FIT, up2google);
				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, "KEY_GOOGLE_FIT保存到本地失败");
			}
	}

	/**
	 *
	 * @param context 参考示例
	 * @return
     */
	public static boolean get_googlefit(Context context){
		 SharedPreferences Setting;
			try
			{
				Setting = getAppDefaultSharedPreferences(context, false);
				boolean jstr = Setting.getBoolean(KEY_GOOGLE_FIT, false);
	            return jstr;
			} catch (Exception e) {
				e.printStackTrace();
			}
		    //所有异常返回null
		    return false;
		    
		  }

	/**
	 * 存储登陆信息.邮箱的
	 *
	 * @param activity
	 * @return
	 */
	public static void addLoginInfo(Context activity, LoginInfo loginInfo,boolean fromPhone)
	{
		List<LoginInfo> history = getLoginInfo(activity,fromPhone);
		//密码是空的就说明本地没有,需要存进去
		if(CommonUtils.isStringEmpty(getLoginPswByLoginName(activity, loginInfo.getUseName(),fromPhone)))
		{
			if(history == null) 
				history = new ArrayList<LoginInfo>();
			history.add(loginInfo);
			//		this.currentMySenceName = currentMySenceName;
			SharedPreferences nameSetting;
			try
			{
				nameSetting = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=nameSetting.edit();
				//将数据保存到sp
				if(fromPhone){
					namePref.putString(KEY_LOGIN_LOGIN_INFO_Lastphone,JSON.toJSONString(history));//new Gson().toJson(history)

				}else {
					namePref.putString(KEY_LOGIN_LOGIN_INFO_Last,JSON.toJSONString(history));//new Gson().toJson(history)
				}

				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}

		}
		//如果和本地的密码不一样
		else if(!getLoginPswByLoginName(activity, (String)loginInfo.getUseName(),fromPhone).equals(loginInfo.getPwd()))
		{
			updateLoginInfo(activity, loginInfo,fromPhone);
		}
	}





	public static void updateLoginInfo(Context activity, LoginInfo loginInfo,boolean isfromPhone)
	{
		removeLoginInfo(activity, loginInfo.getUseName(),isfromPhone);

		addLoginInfo(activity, loginInfo.getUseName(), (String) loginInfo.getPwd(),isfromPhone);
	}


	/**
	 * 存储登陆信息.
	 *
	 * @param activity
	 * @return
	 */
	public static void addLoginInfo(Context activity, String loginName, String loginPsw,boolean isfromPhone)
	{
		List<LoginInfo> history =  getLoginInfo(activity,isfromPhone);

		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setUseName(loginName);
		loginInfo.setPwd(loginPsw);
		if(CommonUtils.isStringEmpty(getLoginPswByLoginName(activity, loginName,isfromPhone)))
		{
			if(history == null)
				history = new ArrayList<LoginInfo>();
			history.add(loginInfo);
			SharedPreferences nameSetting;
			try
			{
				nameSetting = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=nameSetting.edit();
				if(isfromPhone){
					namePref.putString(KEY_LOGIN_LOGIN_INFO_Lastphone, JSON.toJSONString(history) );
				}else {
					namePref.putString(KEY_LOGIN_LOGIN_INFO_Last, JSON.toJSONString(history) );
				}
				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	/**
	 * 获取登陆信息
	 *
	 * @param activity
	 * @return
	 */
	public static List<LoginInfo> getLoginInfo(Context activity,boolean isfromPhone)
	{
		//取出最近使用过的角色名
		SharedPreferences nameSetting;
		try
		{
			nameSetting = getAppDefaultSharedPreferences(activity, false);
			String jstr=" ";

			if(isfromPhone){
				//取出使用手机登录的用户
				jstr = nameSetting.getString(KEY_LOGIN_LOGIN_INFO_Lastphone, null);

			}else{
				jstr = nameSetting.getString(KEY_LOGIN_LOGIN_INFO_Last, null);

			}
			return CommonUtils.isStringEmpty(jstr) ? null : JSON.parseArray(jstr, LoginInfo.class);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}

	}
	//根据用户名查找密码
	public static String getLoginPswByLoginName(Context activity, String loginName,boolean isfromPhone)
	{
		List<LoginInfo> infos = getLoginInfo(activity,isfromPhone);
		String psw = "";
		if(infos != null)
		{
			for (LoginInfo info : infos)
			{
				if(info.getUseName().equalsIgnoreCase(loginName))
					return (String) info.getPwd();
			}
		}
		return psw;
	}

	//获取本地的用户名
	public static String[] getLoginNames(Context activity,boolean isfromPhone)
	{
		List<LoginInfo> infos = getLoginInfo(activity,isfromPhone);
		String[] names = null;
		if(infos != null)
		{
			names = new String[infos.size()];
			for(int i = 0; i < infos.size(); i++)
			{
				names[i] = infos.get(i).getUseName();
			}
		}
		return names;
	}

	public static void removeLoginInfo(Context activity, String loginName,boolean isfromPhone)
	{
		List<LoginInfo> history = getLoginInfo(activity,isfromPhone);
		if(history != null)
		{
			history = new ArrayList<LoginInfo>();
			for (LoginInfo loginInfo2 : history)
			{
				if(loginInfo2.getUseName().equalsIgnoreCase(loginName))
				{
					history.remove(loginInfo2);
					break;
				}
			}
			SharedPreferences nameSetting;
			try
			{
				nameSetting = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=nameSetting.edit();
				if(isfromPhone){
					namePref.putString(KEY_LOGIN_LOGIN_INFO_Lastphone, JSON.toJSONString(history));
				}else {
					namePref.putString(KEY_LOGIN_LOGIN_INFO_Last, JSON.toJSONString(history));
				}


				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}

		}
	}

	/**
	 * 保存UserEntity到本地
	 * @param context
	 * @param userInfo
     */
	public static void setLocalUserInfo(Context context, UserEntity userInfo)
	{
		String jString =JSON.toJSONString(userInfo);
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString(KEY_LOGIN_USER_INFO, jString);
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}

	}
	//获得用户信息：本地获得和网络获得。
	public static UserEntity getLocalUserInfo(Context context)
	{
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, false);

			String jString = sharedPreferences.getString(KEY_LOGIN_USER_INFO, null);
			//null
			MyLog.e(TAG,"getLocalUserInfo jString是："+jString);
			if (CommonUtils.isStringEmptyPrefer(jString))
			{
				MyLog.i(TAG,"jString是空");
				return new UserEntity();
			}
			else{
				MyLog.i(TAG,"jString不是空"+jString);
				return new Gson().fromJson(jString, UserEntity.class);
			}
			}

		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
			return new UserEntity();
		}

	}

	public static void setAppStartFitst(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor Pref=preferences.edit();
		Pref.putBoolean("isFirstIn",false);
		Pref.commit();
	}


	/**
	 * 用于免登陆时的用户信息读取方法.
	 * <p>
	 * 与 {@link @getLocalUserInfo(Context)}唯一的不同在于当Preference中不存在时返回null而非空
	 * UserEntity对象.
	 *
	 * @param context
	 * @return
	 */

	public static UserEntity getLocalUserInfoForLaunch(Context context)
	{
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, false);
			String jString = sharedPreferences.getString(KEY_LOGIN_USER_INFO, null);
			if (CommonUtils.isStringEmpty(jString) || jString.equals("{}"))
				return null;
			else
				return new Gson().fromJson(jString, UserEntity.class);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}

	}

	/**
	 * 获取localvo
	 * @param context
	 * @return LocalInfoVO
     */
	public static LocalInfoVO getLocalDeviceInfo(Context context)
	{
		try
		{
			String id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id()+""; //获得用户ID
			SharedPreferences sharedPreferences = getAppDefaultSharedPreferences(context, false);
			String jString = sharedPreferences.getString(KEY_LOCAL_VO, null);//获得设备信息
			if (CommonUtils.isStringEmptyPrefer(jString) ) //判断是否为空字符串。
			{
				MyLog.e(TAG,"getLocalDeviceInfo:"+jString);
				return new LocalInfoVO();
			}
			else
			{
				LocalInfoVO vo = JSON.parseObject(jString, LocalInfoVO.class);
				if(vo.userId != null && vo.userId.equals(id))
				{
					return  vo;
				}
				return new LocalInfoVO();
			}
		}
		catch (Exception e)
		{
			MyLog.e(PreferencesToolkits.class.getSimpleName(), e.getMessage());
			return new LocalInfoVO();
		}

	}

	/**
	 * 获取localvo
	 * @param context
	 * @return LocalInfoVO
	 */
	public static void setLocalDeviceInfoVo(Context context,LocalInfoVO vo)
	{
		setLocalDeviceInfo(context, vo);
	}

	/**
	 * 将LPDeviceInfo 转换成 应用层的 LocalInfoVO（电量.同步时间）
	 * @param context
	 * @param deviceInfo
     */
	public static void updateLocalDeviceInfo(Context context,LPDeviceInfo deviceInfo)
	{
		LocalInfoVO localInfoVO = getLocalDeviceInfo(context);
		if(localInfoVO.recoderStatus == -1)
		{
			LocalInfoVO temp =  new LocalInfoVO();
			temp.updateByDeviceInfo(context, deviceInfo);
			temp.syncTime = new Date().getTime();
			setLocalDeviceInfo(context, temp);
		}
		else
		{
			localInfoVO.updateByDeviceInfo(context, deviceInfo);
			localInfoVO.syncTime = new Date().getTime();
			setLocalDeviceInfo(context, localInfoVO);
		}
	}
	/**
	 * 将LPDeviceInfo 转换成 应用层的 LocalInfoVO（电量.同步时间）
	 * @param context
	 * @param syncTime
	 */
	public static void updateLocalDeviceInfo(Context context,long syncTime)
	{
		LocalInfoVO localInfoVO = getLocalDeviceInfo(context);
		if(localInfoVO == null)
		{
			LocalInfoVO temp =  new LocalInfoVO();
			temp.syncTime = syncTime;
			setLocalDeviceInfo(context, temp);
		}
		else
		{
			localInfoVO	.syncTime = syncTime;
			setLocalDeviceInfo(context, localInfoVO);
		}
	}


	/**
	 * 设置本地设备信息
	 * @param context
	 * @param localInfoVO
     */
	private static void setLocalDeviceInfo(Context context, LocalInfoVO localInfoVO)
	{
		String id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id()+"";
		localInfoVO.userId = id;
		String jString = JSON.toJSONString(localInfoVO);
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString(KEY_LOCAL_VO, jString);
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}

	}

	/*存储单位设置信息*/
	public static void setLocalSettingUnitInfo(Context context, int unit)
	{
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putInt(KEY_USER_Unit_INFO, unit);
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}

	}
	/*获取单位设置信息*/
	public static int getLocalSettingUnitInfo(Context context)
	{
		SharedPreferences sharedPreferences;
		try
		{
			MyLog.e(TAG,"存储单位默认为公制");
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			int jString = sharedPreferences.getInt(KEY_USER_Unit_INFO, ToolKits.UNIT_GONG);//获得设备信息
			if (jString<0) //判断是否为空字符串。
			{
				return ToolKits.UNIT_GONG;
			}else
				return jString;
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
		return ToolKits.UNIT_GONG;
	}


	/**
	 * 存储目标信息
	 * @param context
	 * @param type 目标类型
     * @param i    目标值
     */
	public static void setGoalInfo(Context context, String type,String i)
	{
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			MyLog.e(TAG,"存储单位默认为公制："+i);
			edit.putString(type, i);
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	}

	/*获取单位设置信息*/
	public static String getGoalInfo(Context context,String type)
	{
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			String jString = "50";
			if(type.equals(KEY_GOAL_MONEY)){
				jString = sharedPreferences.getString(type, "100");//目标值
			}
			else if(type.equals(KEY_GOAL_STEP)){
				jString = sharedPreferences.getString(type, "10000");//目标值
			}
			else if(type.equals(KEY_GOAL_DISTANCE)){
				jString = sharedPreferences.getString(type, "8500");//目标值
			}
			else if(type.equals(KEY_GOAL_CAL)){
				jString = sharedPreferences.getString(type, "2800");//目标值
			}
			else if(type.equals(KEY_GOAL_RUN)){
				jString = sharedPreferences.getString(type, "30");//目标值
			}
			else if(type.equals(KEY_GOAL_SLEEP)){
				jString = sharedPreferences.getString(type, "8.0");//目标值
			}
			else if(type.equals(KEY_GOAL_WEIGHT)){
				jString = sharedPreferences.getString(type, "55.0");//目标值
			}
			return jString;
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
		return "50";
	}
//存储聊天记录
public static void saveComment(Context context, String strComment){
	SharedPreferences sharedPreferences;
	try
	{
		sharedPreferences = getAppDefaultSharedPreferences(context, true);
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putString("strComment",strComment);
		edit.commit();
	}
	catch (Exception e)
	{
		Log.e(TAG, e.getMessage());
	}
}
	public static String getComment(Context context){
		SharedPreferences sharedPreferences;
		String jString;
		try {
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			jString=sharedPreferences.getString("strComment","fail");
			return  jString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  null;
	}


	//存储更新时间
	public static void setUpdateTime(Context context, String updatetime){
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString("updatetime",updatetime);
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	}
	//取出更新时间

	public static String getUpdateTime(Context context){
		SharedPreferences sharedPreferences;
		String jString;
		try {
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			jString=sharedPreferences.getString("updatetime","fail");
			return  jString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  null;
	}

	//存储更新时间
	public static void setOADUpdateTime(Context context){
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putLong(KEY_NEED_SHOW_OAD,System.currentTimeMillis()/1000);
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	}
	//取出更新时间

	public static long getOADUpdateTime(Context context){
		SharedPreferences sharedPreferences;
		long jString;
		try {
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			jString=sharedPreferences.getLong(KEY_NEED_SHOW_OAD,0);
			return  jString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  0;
	}

	/**
	 * 报存本地modelName的信息
	 * @param context
	 * @param modelName
	 * @return
	 */
	public static void saveInfoBymodelName(Context context, String modelName, ModelInfo modelInfo){
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString(modelName,JSON.toJSONString(modelInfo));
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * 获取本地modelName的信息
	 * @param context
	 * @param modelName
     * @return
     */
	public static ModelInfo getInfoBymodelName(Context context,String modelName){
		SharedPreferences sharedPreferences;
		ModelInfo modelInfo = null;
		String jString;
		try {
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			jString=sharedPreferences.getString(modelName,null);
			MyLog.e(TAG,"取出的ModelInfo："+jString);
			if(!CommonUtils.isStringEmptyPrefer(jString)){
				modelInfo = JSONObject.parseObject(jString, ModelInfo.class);
			}
			return  modelInfo;
		} catch (Exception e) {
			MyLog.e(TAG,"取出的ModelInfo：",e);
			e.printStackTrace();
		}
		return  modelInfo;
	}



	/**
	 * 保存岭南通用户名
	 * @param context
	 * @param username
	 * @return
	 */
	public static void saveLNTusername(Context context, String username){
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString(KEY_LNT_USERNAME,username);
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * 获取本地岭南通用户名
	 * @param context
	 * @return
	 */
	public static String getLNTusername(Context context){
		SharedPreferences sharedPreferences;
		String jString = null;
		try {
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			jString=sharedPreferences.getString(KEY_LNT_USERNAME,null);
			return  jString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  jString;
	}

	/**
	 * 保存岭南通用户名
	 * @param context
	 * @param list
	 * @return
	 */
	public static void saveQianbaoList(Context context, String cardNumber, List<LLTradeRecord> list){
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString(cardNumber,JSON.toJSONString(list));
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * 获取本地岭南通用户名
	 * @param context
	 * @return
	 */
	public static List<LLTradeRecord> getQianbaoList(Context context, String cardNumber){
		SharedPreferences sharedPreferences;
		List<LLTradeRecord> list = new LinkedList<>();
		String jString = null;
		try {
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			jString=sharedPreferences.getString(cardNumber,null);
			if(!CommonUtils.isStringEmpty(jString)){
				list = JSON.parseArray(jString, LLTradeRecord.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  list;
	}

}
