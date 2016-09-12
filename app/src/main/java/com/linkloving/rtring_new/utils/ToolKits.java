package com.linkloving.rtring_new.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.db.summary.DaySynopicTable;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.notify.NotificationCollectorService;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.utils.sportUtils.SportDataHelper;
import com.linkloving.utils.TimeZoneHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ToolKits
{
private final static String TAG = ToolKits.class.getSimpleName();

	public final static int UNIT_YING = 111;
	public final static int UNIT_GONG = 112;
	
	public final static String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
	public final static String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
	public final static String DATE_FORMAT_MM_DD = "MM-dd";
	
    public final static String DATE_FORMAT_HH_MM = "HH:mm";

    
    public final static double UNIT_METER_TO_MILES = 0.0006214;
    
    public final static double UNIT_LBS_TO_KG =  0.4535924;
	public final static double UNIT_KG_TO_LBS =  2.2046226;
    
   
    public final static double UNIT_INCHES_TO_CM = 2.54;

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public static float stringtofloat(String parsedString)
	{
		NumberFormat nf = NumberFormat.getInstance(Locale.forLanguageTag("es"));
		Number parsedNumber = null;
		try {
			parsedNumber = nf.parse(parsedString);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		float parsedValueFloat = parsedNumber.floatValue();
		return parsedValueFloat;
	}
	
	public static String convertTimeWithPartten(long time, String partten)
	{
		long utctime = time - TimeZoneHelper.getTimeZoneOffsetMills();
		Date date = new Date(utctime);
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		if(Integer.parseInt(sdf.format(date))>12){
			SimpleDateFormat sdf_after = new SimpleDateFormat("hh:mm");
			return sdf_after.format(date)+" PM";
		}else{ 
			// 早晨  
			SimpleDateFormat sdf_morning = new SimpleDateFormat("hh:mm");
			return sdf_morning.format(date)+" AM";
		}
	}

	/** 磅转KG */
	public static float calcLBS2KG(float lbs)
	{
		float weight = (float)(lbs * UNIT_LBS_TO_KG);
		BigDecimal   b   =   new   BigDecimal(weight);
		float   f1   =   b.setScale(1,   BigDecimal.ROUND_HALF_UP).floatValue();
		return f1;
	}

	/** KG转磅 */
	public static float calcKG2LB(float kg)
	{
		float weight = (float)(kg * UNIT_KG_TO_LBS);
		BigDecimal   b   =   new   BigDecimal(weight);
		float   f1   =   b.setScale(1,   BigDecimal.ROUND_HALF_UP).floatValue();
		return f1;
	}

	public static int calcInches2CM(int inches)
	{
		return (int)Math.rint(inches * UNIT_INCHES_TO_CM);
	}


	public static boolean isJSONNullObj(String nullContent)
	{
		return "null".equals(nullContent);
	}

	public static String int2String(int temp)
	{
		return (temp > 9 || temp < 0) ? "" + temp : "0" + temp;
	}
	public static int calculateCalories(float distance, int seconds, int weight){
		if (seconds ==0) {
			return 0;
		}

		float speed = (float) (distance * 3.6 / seconds);
		float p = 1;
		if (speed > 19.308) {
			p= 0.13f;
		}
		else if (speed > 17.699) {
			p= 0.113f;
		}
		else if (speed > 16.09) {
			p= 0.1f;
		}
		else if (speed > 14.481) {
			p= 0.094f;
		}
		else if (speed > 12.872) {
			p= 0.089f;
		}
		else if (speed > 11.263) {
			p= 0.085f;
		}
		else if (speed > 9.654) {
			p = 0.079f;
		}
		else
		{
			return (int) ((distance * weight * 1.099) / 1000);
		}

		return (int) ((seconds * weight * p) / (0.453 *60));
	}


	public static void showCommonTosat(Context context, boolean showRightOrWrong, String showString, int toastLength)
	{
		// 获取LayoutInflater对象，该对象能把XML文件转换为与之一直的View对象
		LayoutInflater inflater = LayoutInflater.from(context);
		// 根据指定的布局文件创建一个具有层级关系的View对象
		// 第二个参数为View对象的根节点，即LinearLayout的ID
		View layout = inflater.inflate(R.layout.common_toast, null);

		// 查找ImageView控件
		// 注意是在layout中查找
		TextView text = (TextView) layout.findViewById(R.id.common_toast_text);
		text.setText(showString);

		Drawable sexImg;
		int resId = showRightOrWrong ? R.mipmap.common_toast_image_right : R.mipmap.common_toast_image_wrong;
		sexImg = context.getResources().getDrawable(resId);
		sexImg.setBounds(0, 0, sexImg.getMinimumWidth(), sexImg.getMinimumHeight());
		text.setCompoundDrawables(null, sexImg, null, null);

		Toast toast = new Toast(context);
		// 设置Toast的位置
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(toastLength);
		// 让Toast显示为我们自定义的样子
		toast.setView(layout);
		toast.show();

	}

	/**
	 * 需要权限:android.permission.GET_TASKS
	 *
	 * @param context
	 * @return
	 */
	public static boolean isApplicationBroughtToBackground(Context context) {
		ActivityManager activityManager =(ActivityManager) context.getSystemService(
				Context.ACTIVITY_SERVICE);
		String packageName =context.getPackageName();
		List<ActivityManager.RunningAppProcessInfo>appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 获取画图后显示的文字的长度
	 * 
	 * @param pFont
	 * @param text
	 * @return
	 */
	public static float getTextLength(TextPaint pFont, String text)
	{
		float textLength = pFont.measureText(text);
		return textLength;
	}
//	public static Bitmap getBlueboothPowerLevel(float level, Activity activity)
//	{
//		return getBlueboothPowerLevel(level, activity, BitmapFactory.decodeResource(activity.getResources(), R.drawable.fullscreen_bt),
//				BitmapFactory.decodeResource(activity.getResources(), R.drawable.fullscreen_bt_bg));
//	}

	/**
	 * 此方法是丁维写的关于合成蓝牙电量的图标实用方法.
	 *
	 * @param level
	 *            0~1.00f的浮点值表示蓝牙电量
	 * @param activity
	 * @return
	 */
	public static Bitmap getBlueboothPowerLevel(float level, Activity activity, Bitmap full, Bitmap fullbg)
	{
		if (full == null || fullbg == null || activity == null)
			return null;
		Bitmap output = Bitmap.createBitmap(full.getWidth(), full.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Rect rect = new Rect(0, 0, (int) (full.getWidth() * level), full.getHeight());
		final RectF rectF = new RectF(rect);

		final Rect rectbg = new Rect(0, 0, fullbg.getWidth(), fullbg.getHeight());
		final RectF rectFbg = new RectF(rectbg);
		canvas.drawBitmap(fullbg, rectbg, rectFbg, null);
		canvas.drawBitmap(full, rect, rectF, null);

		// 及时回 收内存
		if (full != null && !full.isRecycled())
			full.recycle();
		if (fullbg != null && !fullbg.isRecycled())
			fullbg.recycle();

		return output;
	}

	/**
	 * 网络是否可传输数据.
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		return MyApplication.getInstance(context).isLocalDeviceNetworkOk();
	}

	/**
	 * 计算BMI指数
	 */
	public static double getBMI(float weight, int height)
	{
//		DecimalFormat r = new DecimalFormat();
//		r.applyPattern("#0.0");// 保留小数位数，不足会补零
		//西班牙语闪退
		BigDecimal   b   =   new   BigDecimal((weight * UNIT_LBS_TO_KG) / ((height * UNIT_INCHES_TO_CM / 100.0) * (height * UNIT_INCHES_TO_CM / 100.0)));  
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
//		return new Double(r.format((weight * UNIT_LBS_TO_KG) / ((height * UNIT_INCHES_TO_CM / 100.0) * (height * UNIT_INCHES_TO_CM / 100.0)))).doubleValue();
	}

	/**
	 * 计算BMI指数描述
	 */
	public static String getBMIDesc(Context context,double bmi)
	{
		if (bmi < 18.5)
			return getStringbyId(context, R.string.body_info_slim);
		if (bmi >= 18.5 && bmi <= 24.9)
			return getStringbyId(context, R.string.body_info_normal);
		if (bmi > 25 && bmi <= 29.9 )
			return getStringbyId(context, R.string.body_info_overweight);
		if (bmi >= 30.0 && bmi<=34.9)
			return getStringbyId(context, R.string.body_info_mildly_obese);
		if (bmi >= 35 && bmi<=39.9)
			return getStringbyId(context, R.string.body_info_moderately_obese);
		if (bmi >= 40.0)
			return getStringbyId(context, R.string.body_info_severe_obesity);
		return getStringbyId(context, R.string.body_info_normal);
	}
	
	public static double CMChangetoINRate(int cm)
	{
		BigDecimal   b   =   new   BigDecimal(cm * 0.3937008);
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static Integer INRateChangetoCM(int cm)
		{
			return (int) (cm / 0.3937008);
		}
	
	public static Integer LBRateChangetoKG(int cm)
	{
		return (int) (cm / 2.2046226);
	}
	/**
	 * @param m 米
	 * @return 公制/英制
	 */
	public static double MChangetoMIRate (int m)
	{
		BigDecimal   b   =   new   BigDecimal(m * 0.0006214);
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 经纬度
	 */
	public static String getLong(double sjiachun)
	{
		BigDecimal db = new BigDecimal(sjiachun);
		String ii = db.toPlainString();
		return ii;
	}
	
	/**
	 * 加载企业用户文件
	 */
//	public static void loadEntFileImage(Activity context, ImageView view, String user_id, String fileName, int width, int height)
//	{
//		AsyncBitmapLoader asyncLoader = new AsyncBitmapLoader(EntHelper.getEntFileSavedDir(context) + "/");
//		Bitmap bitmap = null;
//		if(!CommonUtils.isStringEmpty(fileName, true))
//		{
//			if(bitmap != null && !bitmap.isRecycled()){
//				bitmap.recycle();
//			}
//			//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调
//			bitmap = asyncLoader.loadBitmap(view
//					// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
//					// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
//					// URL要一定能取的到头像数据就对了
//					, EntHelper.getEntDownloadURL(context, user_id, fileName)
//					, fileName//, rowData.getUserAvatarFileName()
//					, new ImageCallBack()
//					{
//						@Override
//						public void imageLoad(ImageView imageView, Bitmap bitmap)
//						{
////						Log.w(GoodDetailActivity.class.getSimpleName(), "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
//							if(imageView != null){
//								imageView.setImageBitmap(bitmap);
//							}
//							// ## 非常奇怪的一个问题：当网络下载的图片完成时会回调至此，但图片数据明
//							// ## 明有了却不能刷新显示之，目前为了它能显示就低效地notifyDataSetChanged
//							// ## 一下吧，以后看看什么方法可以单独刷新（否则每一次都得刷新所有可见区），
//							// ## 有可能是android的listview机制性问题
//						}
//					}
//					// 指定生成的Bitmap对象所描述的图片的大小(更小的尺寸将成倍地减小内存消耗)
//					, width, height// 此头像用于好友列表中的好友头像，大小参考：@drawable/head_bg_hole.png
//			);
//
//			if(bitmap != null && view != null){
//				view.setImageBitmap(bitmap);
//			}
//		}
//		else
//		{
//			if(bitmap != null && !bitmap.isRecycled()){
//				bitmap.recycle();
//			}
//			if(view!=null){
//				Resources res = context.getResources();
//				bitmap = BitmapFactory.decodeResource(res, R.drawable.main_fragment_little_logo);
//			    view.setImageBitmap(bitmap);
//			}
//
//		}
//	}
	
	/**
	 * 分享功能
	 */
	public static void shareContent(Context context, String activityTitle, String msgTitle, String text, String imgPath)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (CommonUtils.isStringEmpty(imgPath))
		{
			intent.setType("text/plain"); // 纯文本
		}
		else
		{
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile())
			{
				intent.setType("image/*");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		context.startActivity(Intent.createChooser(intent, activityTitle));

	}
	
	
	public static String getStringbyId(Context context,int id)
	{
		return context.getResources().getString(id);
	}

	public static void getScreenHot(View v, String filePath)
	{
		View view = v.getRootView();
		view.setDrawingCacheEnabled(true);
		try
		{
			view.buildDrawingCache();
			Bitmap bitmap = view.getDrawingCache();
			if (bitmap != null)
			{
			
					FileOutputStream out = new FileOutputStream(filePath);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.close();
			}
			else
			{
				System.out.println("bitmap is NULL!");
			}
		}
		catch (Exception e)
		{
			Log.w(TAG, e.getMessage(), e);
		}
		view.setDrawingCacheEnabled(false);
	}

	/**
	 * 获取两个date相差的天数
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getindexfromDate(Date start,Date end){
		long t = 0;
		try {
			t = (end.getTime() - start.getTime()) / (3600 * 24 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int)t;
	}

	/**
	 * 获取根据当前时间与index天的日期
	 * @param date
	 * @param index 需要累加的天数
	 * @return 如果传入2016-05-01  今天是2016-05-04  则返回 集合(2016-05-02,2016-05-03,2016-05-04)
	 */
	public static List<String> getlistDate(Date date, int index)
	{
		SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);
		List<String> list = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		for(int i=0;i<index;i++){
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			date = calendar.getTime();
			list.add(sdf.format(date));
		}
		return list;
	}

	/**
	 * 获取根据当前时间与index天的日期
	 * index 为负数时则为过去的天数，例：-1.则表示 date时间的以前一天，反之为后一天，0则返回date
	 * @param date
	 * @param index
	 * @return
	 */
	public static Date getDayFromDate(Date date, int index)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, index);
		date = calendar.getTime();
		return date;
	}
	
	public static Drawable getRepetDrawable(Context context,int res)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),res);
    	BitmapDrawable drawable = new BitmapDrawable(bitmap);
    	drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
    	drawable.setDither(true);
    	return drawable;
	}

	public static String inttoStringMoney(int i){
//		float   scale  =   (float) (i/100.0);
//		DecimalFormat fnum  =   new  DecimalFormat("#0.00");
//		String   dd=fnum.format(scale);
		BigDecimal   b   =   new   BigDecimal(i/100.0);
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue()+"";
//		return dd;
	}
	
	//隐藏虚拟键盘
    public static void HideKeyboard(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
        if ( imm.isActive( ) )
        {     
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );   
        }    
    }
    
  //显示虚拟键盘
    public static void ShowKeyboard(View v)
    {
          InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
         imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

	//获取设备唯一ID
	public static String getAndroidUID()
	{
		String m_szDevIDShort = "35" + //we make this look like a valid IMEI
				Build.BOARD.length()%10 +
				Build.BRAND.length()%10 +
				Build.CPU_ABI.length()%10 +
				Build.DEVICE.length()%10 +
				Build.DISPLAY.length()%10 +
				Build.HOST.length()%10 +
				Build.ID.length()%10 +
				Build.MANUFACTURER.length()%10 +
				Build.MODEL.length()%10 +
				Build.PRODUCT.length()%10 +
				Build.TAGS.length()%10 +
				Build.TYPE.length()%10 +
				Build.USER.length()%10 ; //13 digits
		return m_szDevIDShort;

	}



	public static final void showResultDialog(Context context, String msg, String title)
	{
		if (msg == null)
			return;
		String rmsg = msg.replace(",", "\n");
		Log.d("Util", rmsg);
		new AlertDialog.Builder(context).setTitle(title).setMessage(rmsg).setNegativeButton("知道了", null).create().show();
	}

	/**
	 * 获取未度数字符串，大于99时显示为99+
	 * @param num
	 * @return
	 */
    public static String getUnreadString(int num)
    {
    	String tmp;
		if(num > 99)
		{
			tmp = "99+";
		}
		else
		{
			tmp = num + "";
		}
		return tmp;
    }
    
    
	// string类型转换为long类型
 	// strTime要转换的String类型的时间
 	// formatType时间格式
 	// strTime的时间格式和formatType的时间格式必须相同
 	public static long stringToLong(String strTime, String formatType)
 			throws ParseException {
 		Date date = stringToDate(strTime, formatType); // String类型转成date类型
 		if (date == null) {
 			return 0;
 		} else {
 			long currentTime = date.getTime(); // date类型转成long类型
 			return currentTime;
 		}
 	}
 	// string类型转换为date类型
  	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
  	// HH时mm分ss秒，
  	// strTime的时间格式必须要与formatType的时间格式相同
  	public static Date stringToDate(String strTime, String formatType) throws ParseException {
  		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
  		Date date = null;
  		try {
			date = formatter.parse(strTime);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
  		return date;
  	}


	/**
	 * 比较两个日期之间的大小
	 * @param date1
	 * @param date2
	 * @return 前者大于后者返回true 反之false
	 */
	public static boolean compareDate(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(date1);
		c2.setTime(date2);
		int result = c1.compareTo(c2);
		if (result >= 0)
			return true;//return 1;
		else
			return false; //return 0;
	}
	//当前周,周一的日期
	public static   Date getMondayOfThisWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0)
			day_of_week = 7;
		c.add(Calendar.DATE, -day_of_week + 1);
		return c.getTime();
	}

	//当前周周日的日期
	public static Date getSundayofThisWeek(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		MyLog.i(TAG, "day_of_week=" + day_of_week);
		if (day_of_week ==0)
			day_of_week=7;
		c.add(Calendar.DATE, 7 - day_of_week);
		return c.getTime();
	}

	//获取一个星期所有日期的集合
	public static ArrayList<String> getAllWeekDay(Date date){
		SimpleDateFormat sim=new SimpleDateFormat(com.linkloving.rtring_new.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD);
		ArrayList<String> arrayList=new ArrayList<>();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		for(int i=1;i<=7;i++){
			int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
			if (day_of_week == 0)
				day_of_week = 7;
			c.add(Calendar.DATE, -day_of_week + i);
			arrayList.add(sim.format(c.getTime()));
		}
		MyLog.i(TAG, "这一周的所有时间=" + arrayList.toString());
		return arrayList;
	}
	//获取一个月的所有日期集合
	public static ArrayList<String> getAllMonthDay(Date date){
		SimpleDateFormat sim=new SimpleDateFormat(com.linkloving.rtring_new.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD);
		ArrayList<String> arrayList=new ArrayList<>();
		//获取当前月第一天：
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int days=c.getActualMaximum(Calendar.DAY_OF_MONTH);//这个月有多少天
		c.add(Calendar.MONTH, 0);
		//设置为1号,当前日期既为本月第一天
		for(int i=1;i<=days;i++){
		c.set(Calendar.DAY_OF_MONTH, i);
			arrayList.add(sim.format(c.getTime()));
		}
		MyLog.i(TAG, arrayList.toString());
		return arrayList;
	}
	//当前月份的第一天
	public static Date getFirstofMonth(Date date){
		//获取当前月第一天：
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 0);
		//设置为1号,当前日期既为本月第一天
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}
	//当前月份的最后一天
	public static Date getLastofMonth(Date date){
		//获取当前月第一天：
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return c.getTime();
	}
	/**
	 * 得到两个日期相差的天数
	 */
	public static int getBetweenDay(Date date1, Date date2) {
		Calendar d1 = new GregorianCalendar();
		d1.setTime(date1);
		Calendar d2 = new GregorianCalendar();
		d2.setTime(date2);
		int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		System.out.println("days=" + days);
		int y2 = d2.get(Calendar.YEAR);
		if (d1.get(Calendar.YEAR) != y2) {
//      d1 = (Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
				d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		}
		return days;
	}

	//获得这个周的汇总数据
	public static ArrayList<DaySynopic> getFindWeekData(Activity activity, Date date, UserEntity userEntity)  {
		//根据传进来的日期来计算出这个星期的起始日期到结束日期
		ArrayList<DaySynopic> srsForReturn = new ArrayList<DaySynopic>();
		MyLog.e(TAG, "传进来的时间:" + new SimpleDateFormat(com.linkloving.rtring_new.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date));
		String startDateString = new SimpleDateFormat(com.linkloving.rtring_new.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getMondayOfThisWeek(date));
		String endDateString = new SimpleDateFormat(com.linkloving.rtring_new.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getSundayofThisWeek(date));
		MyLog.e(TAG, "这周的时间范围:" + startDateString + "~~~" + endDateString);
		ArrayList<DaySynopic> mDaySynopicArrayList = DaySynopicTable.findDaySynopicRange(activity, userEntity.getUser_id() + "", startDateString, endDateString, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
		ArrayList<DaySynopic> temp = new ArrayList<>();
		//如果查出来的数据没有一周的数据,只能去日数据去计算这一个星期的数据
		//线查出该段时间内的所有明细数据,然后去汇总,然后再去查汇总数据的数据,
		if (mDaySynopicArrayList == null || mDaySynopicArrayList.size() < 7) {
			SimpleDateFormat DATE_PATTERN_sdfYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
			Date s = null;
			Date end=null;
			try {
				s = DATE_PATTERN_sdfYYMMDD.parse(startDateString);
				end = DATE_PATTERN_sdfYYMMDD.parse(endDateString);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}

			GregorianCalendar gs = new GregorianCalendar();
			gs.setTime(s);
			int dayDelta = (int) ((end.getTime() - s.getTime()) / 1000 / 60 / 60 / 24);
			MyLog.i(TAG,"加入的数据dayDelta："+dayDelta);
			if (dayDelta > 0) {
				for (int i = 0; i < dayDelta; i++) {
					String startD = DATE_PATTERN_sdfYYMMDD.format(gs.getTime());
					MyLog.i(TAG,"加入的数据shijian："+startD);
					if (mDaySynopicArrayList.size() > 0) {
						for (int j = 0; j < mDaySynopicArrayList.size(); j++) {

							DaySynopic ds = mDaySynopicArrayList.get(j);
							if (startD.equals(ds.getData_date())) {
								srsForReturn.add(ds);
								break;
							}
							// 匹配到最后一个，依然没有匹配上，则放入一个空对象
							if (j == mDaySynopicArrayList.size() - 1){
								if (!ToolKits.compareDate(stringToDate(startD, ToolKits.DATE_FORMAT_YYYY_MM_DD), new Date())) {
									DaySynopic daySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(activity, startD, startD);
									if (daySynopic != null) {
										srsForReturn.add(daySynopic);
										if (daySynopic.getTime_zone() == null) {

										} else {
											temp.add(daySynopic);//加入到集合,等会保存
										}
									}
								}
							}
						}
					}
					else {
						if (!ToolKits.compareDate(stringToDate(startD, ToolKits.DATE_FORMAT_YYYY_MM_DD), new Date())) {
							DaySynopic daySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(activity, startD, startD);
							if (daySynopic != null) {
								srsForReturn.add(daySynopic);
								if (daySynopic.getTime_zone() == null) {

								} else {
									temp.add(daySynopic);//加入到集合,等会保存
								}
							}
						}
					}
					// 下一日时间准备
					gs.add(GregorianCalendar.DAY_OF_MONTH, 1);
				}
			}
			//保存汇总的数据
			DaySynopicTable.saveToSqliteAsync(activity, temp, userEntity.getUser_id() + "");
			return srsForReturn;
			} else {
				return mDaySynopicArrayList;
			}
		}
	//获得这个月的汇总数据
	public static  ArrayList<DaySynopic> getFindMonthData(Activity activity,Date date,UserEntity userEntity){
		ArrayList<DaySynopic> srsForReturn = new ArrayList<DaySynopic>();
		String startDateString=new SimpleDateFormat(com.linkloving.rtring_new.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getFirstofMonth(date));//获取这个月的第一天
		String endDateString=new SimpleDateFormat(com.linkloving.rtring_new.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getLastofMonth(date));//获取这个月的最后一天
		MyLog.i(TAG, "这月的时间范围:" + startDateString + "~~~" + endDateString);
		ArrayList<DaySynopic> mDaySynopicArrayList= DaySynopicTable.findDaySynopicRange(activity, userEntity.getUser_id() + "",
				startDateString, endDateString, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
		ArrayList<DaySynopic> temp=new ArrayList<>();
		//道理和星期一样,如果size小于这个月天数的话,就要去明细数据查询
		int monthDays=0;//计算这个月的天数
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		monthDays=c.getActualMaximum(Calendar.DAY_OF_MONTH);

		if(mDaySynopicArrayList!=null||mDaySynopicArrayList.size()<monthDays){
			//查询明细数据,去汇总
			SimpleDateFormat DATE_PATTERN_sdfYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
			Date s = null;
			Date end=null;
			try {
				s = DATE_PATTERN_sdfYYMMDD.parse(startDateString);
				end = DATE_PATTERN_sdfYYMMDD.parse(endDateString);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}

			GregorianCalendar gs = new GregorianCalendar();
			gs.setTime(s);
			int dayDelta = (int) ((end.getTime() - s.getTime()) / 1000 / 60 / 60 / 24);
			MyLog.i(TAG, "加入的数据dayDelta：" + dayDelta);
			if (dayDelta > 0) {
				for (int i = 0; i < dayDelta; i++) {
					String startD = DATE_PATTERN_sdfYYMMDD.format(gs.getTime());
					MyLog.i(TAG,"加入的数据shijian："+startD);
					if (mDaySynopicArrayList.size() > 0) {
						for (int j = 0; j < mDaySynopicArrayList.size(); j++) {

							DaySynopic ds = mDaySynopicArrayList.get(j);
							if (startD.equals(ds.getData_date())) {
								srsForReturn.add(ds);
								break;
							}
							// 匹配到最后一个，依然没有匹配上，则放入一个空对象
							if (j == mDaySynopicArrayList.size() - 1){
								if (!ToolKits.compareDate(stringToDate(startD, ToolKits.DATE_FORMAT_YYYY_MM_DD), new Date())) {
									DaySynopic daySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(activity, startD, startD);
									if (daySynopic != null) {
										srsForReturn.add(daySynopic);
										if (daySynopic.getTime_zone() == null) {

										} else {
											temp.add(daySynopic);//加入到集合,等会保存
										}
									}
								}
							}
						}
					}
					else {
						if (!ToolKits.compareDate(stringToDate(startD, ToolKits.DATE_FORMAT_YYYY_MM_DD), new Date())) {
							DaySynopic daySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(activity, startD, startD);
							if (daySynopic != null) {
								srsForReturn.add(daySynopic);
								if (daySynopic.getTime_zone() == null) {
								} else {
									temp.add(daySynopic);//加入到集合,等会保存
								}
							}
						}
					}
					// 下一日时间准备
					gs.add(GregorianCalendar.DAY_OF_MONTH, 1);
				}
			}
			//保存汇总的数据
			DaySynopicTable.saveToSqliteAsync(activity, temp, userEntity.getUser_id() + "");
			return srsForReturn;
		}else {
			return mDaySynopicArrayList;
		}
	}
	/**
	 * 获得指定日期的前一天
	 *
	 * @param specifiedDay
	 * @return
	 * @throws Exception
	 */
	public static String getSpecifiedDayBefore(String specifiedDay) {//可以用new Date().toLocalString()传递参数
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - 1);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c
				.getTime());
		return dayBefore;
	}

	/**
	 * 获得指定日期的后一天
	 *
	 * @param specifiedDay
	 * @return
	 */
	public static String getSpecifiedDayAfter(String specifiedDay) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
				.format(c.getTime());
		return dayAfter;
	}

	public static int makeShort(byte b1, byte b2) {
		return (int) (((b1 & 0xFF) << 8) | (b2 & 0xFF));
	}

	//判断是否获取了消息通知权限
	public static boolean isEnabled(Context context) {
		String pkgName = context.getPackageName();
		final String flat = Settings.Secure.getString(context.getContentResolver(), NotificationCollectorService.ENABLED_NOTIFICATION_LISTENERS);
		if (!TextUtils.isEmpty(flat)) {
			final String[] names = flat.split(":");
			for (int i = 0; i < names.length; i++) {
				final ComponentName cn = ComponentName.unflattenFromString(names[i]);
				if (cn != null) {
					if (TextUtils.equals(pkgName, cn.getPackageName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
