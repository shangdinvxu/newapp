package com.example.android.bluetoothlegatt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * ʱ��ת��������
 *
 * @author Administrator
 *
 */
public class TimeUtil
{

	public static final String TIMEZONE_OFFSET = "timezoneOffset";

	/**
	 * �������ת��Ϊ�ַ��� ����ʽΪ"00:00:00"
	 *
	 * @param secondCount
	 * @return
	 */
	public static String formatTimeFromSecondCount(int secondCount)
	{
		String str = "";
		if (secondCount < 60)
		{
			String s;
			if (secondCount < 10)
			{
				s = "0" + secondCount;
			}
			else
			{
				s = secondCount + "";
			}
			str = "00:00:" + s;
		}
		else if (secondCount < 3600)
		{
			String m, s;
			if (secondCount / 60 < 10)
			{
				m = "0" + secondCount / 60;
			}
			else
			{
				m = secondCount / 60 + "";
			}
			if (secondCount % 60 < 10)
			{
				s = "0" + secondCount % 60;
			}
			else
			{
				s = secondCount % 60 + "";
			}
			str = "00:" + m + ":" + s;
		}
		else
		{
			String h, m, s;
			if (secondCount / 3600 < 10)
			{
				h = "0" + secondCount / 3600;
			}
			else
			{
				h = secondCount / 3600 + "";
			}
			if (((secondCount % 3600) / 60) < 10)
			{
				m = "0" + (secondCount % 3600) / 60;
			}
			else
			{
				m = (secondCount % 3600) / 60 + "";
			}
			if (secondCount % 60 < 10)
			{
				s = "0" + secondCount % 60;
			}
			else
			{
				s = secondCount % 60 + "";
			}
			str = h + ":" + m + ":" + s;
		}
		return str;
	}

	/**
	 * ��ʽ�������� Ϊ ʱ�䴮
	 *
	 * @param minuteCount
	 * @return
	 */
	public static String formatTimeFromMinuteCount(int minuteCount)
	{
		if (minuteCount == 0)
		{
			return "00:00";
		}
		StringBuilder sb = new StringBuilder();
		int hours = minuteCount / 60;
		int minutes = minuteCount % 60;
		if (hours < 10)
		{
			sb.append("0");
		}
		sb.append(hours);
		sb.append(":");
		if (minutes < 10)
		{
			sb.append("0");
		}
		sb.append(minutes);
		return sb.toString();
	}

	/**
	 * �ж����������Ƿ�Ϊͬһ��
	 *
	 * @param date1
	 *            ����һ
	 * @param date2
	 *            ���ڶ�
	 * @return ͬһ��true������ͬһ��false
	 */
	public static boolean isSameDate(Date date1, Date date2)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
		{
			result = true;
		}
		return result;
	}

	public static boolean isToday(Date targetDate)
	{
		return isSameDate(new Date(), targetDate);
	}

	public static boolean isYesterday(Date currentDate, Date targetDate)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(currentDate);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(targetDate);

		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& (c1.get(Calendar.DAY_OF_MONTH) - 1) == c2.get(Calendar.DAY_OF_MONTH))
		{
			result = true;
		}
		return result;
	}

	public static boolean isTheDayBeforeYesterday(Date currentDate, Date targetDate)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(currentDate);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(targetDate);

		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& (c1.get(Calendar.DAY_OF_MONTH) - 2) == c2.get(Calendar.DAY_OF_MONTH))
		{
			result = true;
		}
		return result;
	}

	/**
	 * �ж��Ƿ�ͬһ��
	 *
	 * @param date1
	 * @param date2
	 * @return ͬһ��true������ͬһ��false
	 */
	public static boolean isSameWeek(Date date1, Date date2)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR))
		{
			result = true;
		}
		return result;
	}

	/**
	 * �ж��Ƿ�ͬһ����
	 *
	 * @param date1
	 * @param date2
	 */
	public static boolean isSameMonth(Date date1, Date date2)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
		{
			result = true;
		}
		return result;
	}

	public static Date parseDateByYMDFromTime(long time)
	{
		if (time > 0l)
		{
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date(time);
			String str = sf.format(date);
			try
			{
				return sf.parse(str);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		return new Date();
	}

	public static Date afterDate(Date time, int n)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.add(Calendar.DATE, n);
		return c.getTime();
	}

	public static Date afterDateByWeek(Date time, int n)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.add(Calendar.WEEK_OF_YEAR, n);
		return c.getTime();
	}

	public static Date afterDateByMonth(Date time, int n)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.add(Calendar.MONTH, n);
		return c.getTime();
	}

	public static String formatDateByYYMMDD(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByMMDD(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByD(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByYYYYMMDD(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByHM(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		return simpleDateFormat.format(time);
	}

	public static Date parseDateByHM(String time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		try
		{
			return simpleDateFormat.parse(time);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseDateByYMDHM(String time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
		try
		{
			return simpleDateFormat.parse(time);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseDateByYMDHMS(String time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		try
		{
			return simpleDateFormat.parse(time);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return new Date();
	}

	public static String formatDateByYMDHM(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByYMDHMLong(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByYMDHMS(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(time);
	}

	public static String formatTimeByHM(long time)
	{
		return formatDateByHM(new Date(time));
	}

	public static Date parseDateByYMD(String time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
		try
		{
			return simpleDateFormat.parse(time);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return new Date();
	}

	public static String formatDateByYMDHMSLong(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(time);
	}

	public static boolean isWeekend(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if (day == 1 || day == 7)
		{
			return true;
		}
		return false;
	}

	public static String parseDateByYMDH00(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		return simpleDateFormat.format(time);
	}

	// TODO:??
	public static List<String> getDateInCurrentWeek()
	{
		List<String> list = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		list.add(formatDateByYYMMDD(calendar.getTime()));
		while (day != Calendar.SUNDAY)
		{
			calendar.add(Calendar.DAY_OF_WEEK, -1);
			list.add(formatDateByYYMMDD(calendar.getTime()));
			// calendar = getDateBefore(calendar.getTime(), 1);
			day = calendar.get(Calendar.DAY_OF_WEEK);
		}
		return list;
	}

	/**
	 * ��ȡָ������00:00ʱ�����ֵ
	 *
	 * @param timeInMillis
	 *            ָ������ʱ�����ֵ
	 * @return
	 */
	public static long getBeginOfDay(long timeInMillis)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	/**
	 * ��ȡָ������24:00����ֵ
	 *
	 * @param timeInMillis
	 *            ָ������ʱ�����ֵ
	 * @return
	 */
	public static long getEndOfDay(long timeInMillis)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	/**
	 * ��ȡ��ǰ�������ܼ�(����0,��һ1...)
	 */
	public static int getCurrentDayOfWeek()
	{
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_WEEK) - 1;// ����0 ��һ1 ....
	}

	/**
	 * ��ȡСʱ
	 *
	 * @param minuteCount
	 *            ������
	 * @return
	 */
	public static int getHourFromMinuteCount(int minuteCount)
	{
		return (int) (minuteCount / 60);
	}

	/**
	 * ��ȡ����
	 *
	 * @param minuteCount
	 *            ������
	 * @return
	 */
	public static int getMinuteFromMinuteCount(int minuteCount)
	{
		return (int) (minuteCount % 60);
	}

	/**
	 * ����������ʽ��Ϊ"1h1m"
	 *
	 * @param minuteCount
	 *            ������
	 * @return
	 */
	public static String formatMinuteCountByHM(int minuteCount)
	{
		int h = (int) (minuteCount / 60);
		int m = (int) (minuteCount % 60);
		StringBuilder builder = new StringBuilder();
		if (h > 0)
		{
			builder.append(h).append("h");
		}
		if (m > 0)
		{
			builder.append(m).append("m");
		}
		if (h == 0 && m == 0)
		{
			builder.append(0);
		}
		return builder.toString();
	}

	/**
	 * ����������
	 *
	 * @param dayIndex
	 *            �����1970�������
	 * @param hourIndex
	 *            ���������Сʱ��(0-23)
	 * @return
	 */
	public static Date parseDateFromDayIndexAndHourIndex(int dayIndex, int hourIndex)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.add(Calendar.DATE, dayIndex);
		calendar.set(Calendar.HOUR_OF_DAY, hourIndex);
		long resultTime = calendar.getTimeInMillis();
		return new Date(resultTime);
	}

	/**
	 * ����������ֵ
	 *
	 * @param dayIndex
	 *            �����1970�������
	 * @param hourIndex
	 *            ���������Сʱ��(0-23)
	 * @return
	 */
	public static long parseTimeFromDayIndexAndHourIndex(int dayIndex, int hourIndex)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);// -TimeZone.getDefault().getRawOffset());
		calendar.add(Calendar.DATE, dayIndex);
		calendar.set(Calendar.HOUR_OF_DAY, hourIndex);
		long resultTime = calendar.getTimeInMillis();
		return resultTime;
	}

	/**
	 * ����������ֵ
	 *
	 * @param dayIndex
	 *            �����1970�������
	 * @param seconds30Count
	 *            ���������30�����
	 * @return
	 */
	public static long parseTimeFromDayIndexAndSeconds30Count(int dayIndex, int seconds30Count)
	{
		Calendar calendar = Calendar.getInstance();
		// calendar.setTimeInMillis(0);
		calendar.setTimeInMillis(-TimeZone.getDefault().getRawOffset());
		calendar.add(Calendar.DATE, dayIndex);
		calendar.set(Calendar.SECOND, seconds30Count * 30);
		long resultTime = calendar.getTimeInMillis();
		return resultTime;
	}

	/**
	 * �����ս���������
	 *
	 * @param birthdayTime
	 * @return
	 */
	public static int parseAgeFromBirthdayTime(long birthdayTime)
	{
		Calendar cal = Calendar.getInstance();
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTimeInMillis(birthdayTime);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth)
		{
			if (monthNow == monthBirth)
			{
				if (dayOfMonthNow < dayOfMonthBirth)
				{
					age--;
				}
				else
				{
					// do nothing
				}
			}
			else
			{
				// monthNow>monthBirth
				age--;
			}
		}
		else
		{
			// monthNow<monthBirth
			// do nothing
		}

		return age;
	}

	/** һ����Ȼ�յĺ����� */
	public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;

	/**
	 * ��ȡָ��ʱ����1970.01.01��������ֵ
	 */
	public static int getDayIndexFrom1970(long ms)
	{
		return (int) ((ms + TimeZone.getDefault().getRawOffset()) / ONE_DAY_MILLIS);
	}

	/**
	 * ��ȡ��1970.01.01��ָ��������ֵ��ʱ��
	 */
	public static long getTimeByDayIndex(int dayIndex)
	{
		return dayIndex * ONE_DAY_MILLIS;
	}

	/**
	 * ��ȡ��ǰʱ��(������)
	 *
	 * @return
	 */
	public static long getCurrentTime()
	{
		return Calendar.getInstance().getTimeInMillis() + TimeZone.getDefault().getRawOffset();
	}

	/**
	 * ��ȡ�����1970�������
	 *
	 * @return
	 */
	public static int getCurrentDay()
	{
		return (int) (getCurrentTime() / ONE_DAY_MILLIS);
	}

	/**
	 * ��˯��30��Ƭ����ת��Ϊ˯��Сʱ��
	 *
	 * @param seconds30Count
	 * @return
	 */
	public static float getHourCountFromSeconds30Count(int seconds30Count)
	{
		return (float) (seconds30Count / 2 * 10 / 60) / 10;
	}

	/**
	 * �������1970�������ת��Ϊ����
	 *
	 * @param dayIndex
	 * @return
	 */
	public static Date parseDateFromDayIndex(int dayIndex)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.add(Calendar.DATE, dayIndex);
		return calendar.getTime();
	}

	/**
	 * ��ȡָ��ʱ���Ӧ��Сʱ����
	 *
	 * @param ms
	 *            ���Ժ���ֵ
	 * @return
	 */
	public static int getHourIndexFromTime(long ms)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(ms);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * ��ȡָ��ʱ������Ӧ���30��ʱ��Ƭ����
	 *
	 * @param ms
	 *            ���Ժ���ֵ
	 * @return
	 */
	public static int getSeconds30CountFromTime(long ms)
	{
		return (int) (((ms + TimeZone.getDefault().getRawOffset()) % (24 * 3600 * 1000)) / (30 * 1000));
	}

	/**
	 * ���������1970���������������Ժ���ֵ+1��<br>
	 * ��ע���������û�������ͬ��
	 *
	 * @param dayIndex
	 * @return
	 */
	public static long getBelongDayFromDayIndex(int dayIndex)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(-TimeZone.getDefault().getRawOffset());
		calendar.add(Calendar.DATE, dayIndex);
		calendar.add(Calendar.SECOND, 1);
		return calendar.getTimeInMillis();
	}

	/**
	 * �����������������ں���ֵ����������1970�������<br>
	 * ��ע���������û�������ͬ��
	 *
	 * @param belongDay
	 * @return
	 */
	public static int getDayIndexFromBelongDay(long belongDay)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(-TimeZone.getDefault().getRawOffset());
		long baseTime = calendar.getTimeInMillis();
		int dayIndex = (int) ((belongDay - baseTime) / ONE_DAY_MILLIS);
		return dayIndex;
	}

	/**
	 * ����Сʱ���ͷ����������30��ʱ��Ƭ�εĸ���
	 *
	 * @param hour
	 * @param mins
	 * @return
	 */
	public static int getSeconds30FromHourAndMinute(int hour, int mins)
	{
		return hour * 120 + mins * 2;
	}

	/**
	 * ��30��ʱ��Ƭ�εĸ���ת���ɾ��Ժ���ֵ
	 *
	 * @param seconds30Count
	 * @return
	 */
	public static long getTimeFromSeconds30Count(int seconds30Count)
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.SECOND, seconds30Count * 30);
		return c.getTimeInMillis();
	}

	/**
	 * ��ȡ���ܵ�����<br>
	 *
	 * @return <Key:[1-7],Value:Date>
	 */
	public static Map<Integer, Date> getThisWeekDates()
	{
		Map<Integer, Date> datesMap = new HashMap<Integer, Date>();
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);// ����1,��һ2...
		// ת��Ϊ����һΪ�ܵ�һ��
		if (day == 1)
		{
			day = 7;
		}
		else
		{
			day -= 1;
		}
		Date currentDate = new Date();
		for (int i = 1; i <= 7; i++)
		{
			Date date = afterDate(currentDate, i - day);
			datesMap.put(i, date);
		}
		return datesMap;
	}

	/**
	 * ��ȡ��ǰʱ���Ӧ��ʱ��Ƭ��ʱ��ƬΪ30��/����
	 *
	 * @return
	 */
	public static int getTicksOfDay()
	{
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		return hour * 60 * 2 + minute * 2;
	}

	/**
	 * ��ȡͼ��ʱ��Ƭ��Ӧ������ʱ��ƬΪ30��/����
	 *
	 * @param ticks
	 * @return
	 */
	public static int parseSecFromTicks(int ticks)
	{
		return ticks / 2;
	}

}
