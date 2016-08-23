package com.example.android.bluetoothlegatt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeZoneHelper
{
	/**
	 * ��������UTC��׼����ʱ��ʱ��ƫ�ƣ���λ�����ӣ���
	 *
	 * @return
	 */
	public static int getTimeZoneOffsetMinute()
	{
		// TimeZone tz = TimeZone.getDefault();
		// return tz.getRawOffset() / 1000 / 60;

		//2015-05-19 [LZ] ������ʱ����UTCʱ����ʱ��������в��죬�������ͳһ
		String date = __getUTC0FromLocalTime("yyyy-MM-dd HH:mm:ss", TimeUtil.getCurrentTime());
		long a = TimeUtil.parseDateByYMDHMS(date).getTime();
		String date1 = __getLocalTimeFromUTC0("yyyy-MM-dd HH:mm:ss", date);
		long b = TimeUtil.parseDateByYMDHMS(date1).getTime();
		return (int) ((b - a) / 60 / 1000);
	}

	// ����
	public static int getTimeZoneOffsetSecond()
	{
		return getTimeZoneOffsetMinute() * 60;
	}

	// ������
	public static long getTimeZoneOffsetMills()
	{
		return getTimeZoneOffsetMinute() * 60 * 1000l;
	}

	public static String __getUTC0FromLocalTime(String pattern, long utcTimestamp)
	{
		// String startDate = new SimpleDateFormat(pattern).format(new
		// Date(utcTimestamp));
		// return startDate;
		return getUTC0FromLocalTime(pattern, utcTimestamp);
	}

	public static String __getUTC0FromLocalTime(String pattern, Date date)
	{
		// String startDate = new SimpleDateFormat(pattern).format(date);
		// return startDate;
		return __getUTC0FromLocalTime(pattern, date.getTime());
	}

	public static String __getUTC0FromLocalTime(String pattern, String date)
	{
		// return date;
		return getUTC0FromLocalTime(pattern, date);
	}

	public static String __getLocalTimeFromUTC0(String pattern, String time)
	{
		return getLocalTimeFromUTC0(pattern, time);
	}
	
	/**
	 * 返回UTC标准时区表示的当前本地时间字符串.
	 *
	 * @param pattern
	 *            时间样式
	 * @return UTC标准时区表示的本地时间字符串
	 */
	public static String getUTC0FromLocalTime(String pattern, long tm)
	{
		SimpleDateFormat sdf1 = new SimpleDateFormat(pattern);
		sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf1.format(new Date(tm));
	}
	
	/**
	 * 将一个UTC标准时区表示的时间字符串转换成本地时区的时间字符串.
	 *
	 * @param pattern
	 *            时间样式
	 * @param time
	 *            UTC标准时区表示的时间字符串
	 * @return 本地时区表示的时间字符串
	 * @throws Exception
	 */
	public static String getLocalTimeFromUTC0(String pattern, String time)// throws
	// Exception
	{
		try
		{
			// 先准备好UTC标准时区的时间格式化对象
			SimpleDateFormat sdf2 = new SimpleDateFormat(pattern);
			sdf2.setTimeZone(TimeZone.getTimeZone("GMT"));

			// 再准备好本地时间的时间格式化对象
			SimpleDateFormat sdf3 = new SimpleDateFormat(pattern);
			sdf3.setTimeZone(TimeZone.getDefault());

			// UTC标准零时区对应的Date对象（从原始UTC标准时区表示的时间字符串解析过来的）
			Date datetimeInUTC = sdf2.parse(time);
			// 将UTC标准时区时间转换成本地时区时间字符串
			String datetimeInLocal = sdf3.format(datetimeInUTC);

			return datetimeInLocal;
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
		return time;
	}
	

	/**
	 * 将一个本地时区表示的时间字符串转换成UTC标准时区的时间字符串.
	 *
	 * @param pattern
	 *            时间样式
	 * @param timeOfLocal
	 *            本地时区表示的时间字符串
	 * @return UTC标准时区表示的时间字符串
	 * @throws Exception
	 */
	public static String getUTC0FromLocalTime(String pattern, String timeOfLocal)
	{
		try
		{
			// 准备好本地时间的时间格式化对象
			SimpleDateFormat sdf2 = new SimpleDateFormat(pattern);
			sdf2.setTimeZone(TimeZone.getDefault());

			// 准备好UTC标准时区的时间格式化对象
			SimpleDateFormat sdf3 = new SimpleDateFormat(pattern);
			sdf3.setTimeZone(TimeZone.getTimeZone("GMT"));

			// 本地时区对应的Date对象（从原始本地时区表示的时间字符串解析过来的）
			Date datetimeInLocal = sdf2.parse(timeOfLocal);
			// 将本地时区时间转换成UTC标准时区时间字符串
			String datetimeInUTC = sdf3.format(datetimeInLocal);
			return datetimeInUTC;
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
		return timeOfLocal;
	}
}
