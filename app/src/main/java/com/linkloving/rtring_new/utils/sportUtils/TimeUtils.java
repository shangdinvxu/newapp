package com.linkloving.rtring_new.utils.sportUtils;

import android.util.Log;

import com.linkloving.band.sleep.SleepDataHelper;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.TimeZoneHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by zkx on 2016/3/16.
 */
public class TimeUtils {
    private final static String TAG = TimeUtils.class.getSimpleName();
    /**
     * 将起始时间转为UTC时间
     * @param startDateTime
     * @return
     */
    public static String getstartDateTimeUTC(String startDateTime ,boolean Issleep) {
//		Log.e(TAG, "转换前的时间:"+startDateTime);
        String startTimeUTC ="";
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(startDateTime));
            gc.add(GregorianCalendar.DAY_OF_MONTH, -1);
            if(Issleep){
                // 日期切换组件的今天-1即是andy认为的睡眠时间的“今天”
                gc.set(GregorianCalendar.HOUR_OF_DAY, -12);
            }
            String startDate = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
            startTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, startDate+" 23:59:59.999"); //将当地时间的0点 换成utc时间的去查询  比如北京时间（+8）的05-02 00：00就是utc时间的05-01 16：00
        } catch (ParseException e) {
            MyLog.e(TAG, e.getMessage(), e);
        }
        return startTimeUTC;
    }

    /**
     * 将结束时间转为UTC时间
     * @param endDateTime
     * @param Issleep 是否是查询睡眠数据
     * @return
     */
    public static String getendDateTimeUTC(String endDateTime,boolean Issleep) {
        String endTimeUTC ="";
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(endDateTime));
            gc.add(GregorianCalendar.DAY_OF_MONTH, 1);// 结束日期也相应加1以便配合服务端的SQL查询
            if(Issleep) {
                endTimeUTC = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime())+" 00:00:00.000";
            } else  {
//               endTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, gc.getTime()+" 00:00:00.000");
                endTimeUTC = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime())+" 00:00:00.000";
            }
            MyLog.i(TAG,"endTimeUTC---->"+endTimeUTC);

//		    endTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(  //tip:传入时 starttime 是 当前时间-1   5.3日  7点  endDateLocal是当前天数+1 5.6日 7点
//					ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, endDate+" 09:00:00.000", endDateTime+" 00:00:00.000" // 2014-08-07 : 按andy要求改成了统计到当天的24点
//						);

        } catch (ParseException e) {
            Log.w(TAG, e.getMessage(), e);
        }
        return endTimeUTC;
    }

    /**
     * 将开始时间转为减去12小时的UTC时间（方便查询睡眠数据）
     * @param startDateTime
     * @return
     */
    public static String getsleepStartTimeUTC(String startDateTime) {
        String startTimeUTC ="";
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(startDateTime));
            gc.add(GregorianCalendar.DAY_OF_MONTH, -1);
            String startDate = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
            startTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, startDate+" 17:59:59.999");
        } catch (ParseException e) {
            MyLog.e(TAG, e.getMessage(), e);
        }
        return startTimeUTC;
    }
    /**
     * 将结束时间转为减去12小时的UTC时间（方便查询睡眠数据）
     * @param endDateTime
     * @return
     */
    public static String getsleepEndTimeUTC(String endDateTime ) {
        String startTimeUTC ="";
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(endDateTime));
            String startDate = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
            startTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, startDate+" 23:59:59.999");
        } catch (ParseException e) {
            MyLog.e(TAG, e.getMessage(), e);
        }
        return startTimeUTC;
    }

    /**
     * 把一天的某一刻转成结束时间 如：
     * 2016-03-23 09:38:59 转成 2016-03-23 23:59:59
     * @param starttime
     * @return
     */
    public static String praseTime(String starttime) {
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(starttime));
            starttime = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime())+" 23:59:59";
        } catch (ParseException e) {
            MyLog.e(TAG, e.getMessage(), e);
        }
        return starttime;
    }

    /**
     * 通过index获取想要的天数
     * @param index   -1是昨天 0是今天 1是明天
     * @return
     */
    public static String getstartDateTime(int index,Date date)
    {
        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, index);//取当前日期的前一天.
//        cal.add(Calendar.DAY_OF_MONTH, +1);//取当前日期的后一天.
        //通过格式化输出日期
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return format.format(cal.getTime());
    }

    public static String convertTimeWithPartten(int time)
    {
        if(time==0){
            return null;
        }
        else
        {
            long time_ = time * 1000l;
            String timestr = ToolKits.convertTimeWithPartten(time_, ToolKits.DATE_FORMAT_HH_MM);
            long utctime = time_ - TimeZoneHelper.getTimeZoneOffsetMinute()* 60 * 1000l;
            Date date = new Date(utctime);
            SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_HH_MM);
            return sdf.format(date);
        }
    }


    public static String formatTimeHHMM(long time)
    {
        if(time==0){
            return null;
        }
        else
        {
            long time_ = time * 1000l;
            Date data = new Date(time_);
            return new SimpleDateFormat(ToolKits.DATE_FORMAT_HH_MM).format(data);
        }
    }

    /**
     * 返回睡眠查询日期的按日分隔集合.
     * <p>
     * 因为目前多日睡眠数据的查询，在本服务端是按每日来查的，所以此方法的目的是将日期范围拆分成每日.
     *
     * @param startDate
     *            查询日期起（如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-01“）
     * @param endDate
     *            查询日期止（如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-02“）
     * @return 返回的是一个2维数据，每个数据里是当日查询的日期，如果2014-06-01日的睡眠数据，则本数据为["2014-06-01",
     *         "2014-06-02"]
     * @throws Exception
     */
    public static String[][] spliteDateRangeToEveryDayForQuerySleep(String startDate, String endDate) throws Exception
    {
        String[][] ret = null;
        SimpleDateFormat DATE_PATTERN_sdfYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
        // SimpleDateFormat sdfYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
        Date s = DATE_PATTERN_sdfYYMMDD.parse(startDate);
        Date e = DATE_PATTERN_sdfYYMMDD.parse(endDate);
        System.out.println(TAG+"--->startDate:"+startDate+"=="+s);
        System.out.println(TAG+"--->endDate:"+endDate+"=="+e);
        GregorianCalendar gs = new GregorianCalendar();
        gs.setTime(s);
        gs.add(GregorianCalendar.DAY_OF_MONTH, -1);

        GregorianCalendar ge = new GregorianCalendar();
        ge.setTime(s);
        ge.add(GregorianCalendar.DAY_OF_MONTH, 1);

        // 所查询的日期范围共几日
        int dayDelta = (int) ((e.getTime() - s.getTime()) / 1000 / 60 / 60 / 24);
        System.out.println("总共相差的天数="+dayDelta);
        if (dayDelta > 0)
        {
            ret = new String[dayDelta][2];
            for (int i = 0; i < dayDelta; i++)
            {
                String startD = DATE_PATTERN_sdfYYMMDD.format(gs.getTime());
                String endD = DATE_PATTERN_sdfYYMMDD.format(ge.getTime());

                // 第i日
                ret[i][0] = startD;
                ret[i][1] = endD;

                // 下一日时间准备
                gs.add(GregorianCalendar.DAY_OF_MONTH, 1);
                ge.add(GregorianCalendar.DAY_OF_MONTH, 1);
            }
        }

        return ret;
    }

    /**
     * @param time 传进来的时间               2016-05-20 19:20:30
     * @param inpattern 传进来的时间的格式    yyyy-MM-dd HH:mm:ss
     * @param outpattern 传出去的时间的格式   yyyy-MM-dd  ===>2016-05-21
     * @return
     */
    public static String getLocalTimeFromUTC( String time,String inpattern,String outpattern)// throws
    {
        try
        {
            // 先准备好UTC标准时区的时间格式化对象
            SimpleDateFormat sdf2 = new SimpleDateFormat(inpattern);
            sdf2.setTimeZone(TimeZone.getTimeZone("GMT"));

            // 再准备好本地时间的时间格式化对象
            SimpleDateFormat sdf3 = new SimpleDateFormat(outpattern);
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

    public static long stringToLong(String strTime, String formatType) {
        Date date = stringToDate(strTime, formatType);
        if(date == null) {
            return 0L;
        } else {
            long currentTime = date.getTime() / 1000L;
            return currentTime;
        }
    }

    public static Date stringToDate(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;

        try {
            date = formatter.parse(strTime);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return date;
    }

}
