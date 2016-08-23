package com.linkloving.rtring_new.utils.sportUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期切换实现类.
 * @author Jack Jiang, 2014-05-14
 * @version 1.0
 */
public class DateSwitcher
{
	/** 日期格式 */
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	/** 日期基准类 */
	protected GregorianCalendar base = null;
	/** 日期切换类型 */
	protected int type = PeriodSwitchType.day;	

	public DateSwitcher(int type)
	{
		this.type = type;

		init();
	}
	
	public DateSwitcher setBaseTime(Date d)
	{
		if(d != null)
			base.setTime(d);
		System.out.println("测试的时间是："+d);
		return this;
	}

	protected void init()
	{
		switch(this.type)
		{
		case PeriodSwitchType.day:
			// 日类型时，默认时间为前一天
			base = new GregorianCalendar();
			base.add(GregorianCalendar.DAY_OF_MONTH, -1);
			break;
			
		case PeriodSwitchType.week:
			// 周类型时：默认时间为本周一
			base = new GregorianCalendar();
////			base.setFirstDayOfWeek(Calendar.MONDAY);
//			base.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
//			base.set(Calendar.DAY_OF_MONTH, base.getFirstDayOfWeek()); // Monday
			base.add(Calendar.DAY_OF_MONTH, -1); //解决周日会出现 并到下一周的情况    
			base.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
			break;
			
		case PeriodSwitchType.month:
			// 月类型时：默认时间为本月1日
			base = new GregorianCalendar();
			base.set(GregorianCalendar.DAY_OF_MONTH, 1);
			break;
			case PeriodSwitchType.year:
			// 月类型时：默认时间为本年的第一天
			base = new GregorianCalendar();
			base.set(GregorianCalendar.DAY_OF_YEAR, 1);
			break;
			
		}
		
		
		
	}

	/**
	 * 起始日期.
	 * 
	 * @return
	 */
	public String getStartDateStr()
	{
		return sdf.format(getStartDate());
	}
	public Date getStartDate()
	{
		return base.getTime();
	}
	
	/**
	 * 结束日期.
	 * <p>
	 * 类型为日时，结束日期为开始日的下1日；<br>
	 * 类型为周时，结束日期为基准日一周日；<br>
	 * 类型为月时，结束日期为基准日的下月1日。
	 * 
	 * @return
	 */
	public String getEndDateStr()
	{
		return sdf.format(getEndDate());
	}
	public Date getEndDate()
	{
		GregorianCalendar end = new GregorianCalendar();
		switch(this.type)
		{
			case PeriodSwitchType.day:
			{
				end.setTime(base.getTime());
				end.add(GregorianCalendar.DAY_OF_MONTH, 1);
				break;
			}
			case PeriodSwitchType.week:
			{
				end.setTime(base.getTime());
				end.add(Calendar.DAY_OF_MONTH, 7); // 周日
				break;
			}
			case PeriodSwitchType.month:
			{
				end.setTime(base.getTime());
				end.add(Calendar.MONTH, 1);
				break;
			}
			
			case PeriodSwitchType.year:
			{
				end.setTime(base.getTime());
				end.add(Calendar.YEAR, 1);
				break;
			}
			
			
		}
		return end.getTime();
	}
	
	/**
	 * 返回SQL中可以使用的"BWTWEEN .. AND .."字符串.
	 * <p>
	 * "BWTWEEN date1 AND date2" 在SQL中的解读为：">= date1 且 < date2" .
	 * 
	 * @return
	 */
	public String getSQLBetweenAnd()
	{
		return "BETWEEN '"+getStartDateStr()+"' AND '"+getEndDateStr()+"'";
	}

	/**
	 * 切换到下一个日期.
	 * 
	 * @param add true表示向更新的日期切换，否则切换到更老的日期
	 * @return true表示切换成功，false表示没有切换成功（是否成功意味着是否允许切换到新日期）
	 */
	private boolean next(boolean add)
	{
		if(add)
		{     
			if(!switchToNextCheck()) 
				return false;
		}
		else
		{
			if(!switchToPreviousCheck()) 
				return false;
		}
		
		switch(this.type)
		{
			case PeriodSwitchType.day:
			{
				base.add(GregorianCalendar.DAY_OF_MONTH, add ? 1 : -1); //-------
				System.out.println("按了返回后base的时间是："+base.getTime());
				break;
			}
			case PeriodSwitchType.week:
			{
				base.add(GregorianCalendar.DAY_OF_MONTH, add ? 7 : -7);
				break;
			}
			
			case PeriodSwitchType.month:
			{
				base.add(GregorianCalendar.MONTH,  add ? 1 : -1);
				break;
			}
			
			case PeriodSwitchType.year:
			{
				base.add(GregorianCalendar.YEAR,  add ? 1 : -1);
				break;
			}
			
		}
		return true;
	}

	/**
	 * 滚动切换到下一个时间类型区间。
	 * 
	 * @return
	 */
	public boolean next()
	{
		return next(true);
	}

	/**
	 * 滚动切换到上一个时间类型区间
	 * 
	 * @return
	 */
	public boolean previous()
	{
		return next(false);
	}
	
	/**
	 * 向更新的日期切换检查.
	 * 
	 * @return true表示检查通过，允许切换到新日期，否则不允许切换
	 */
	protected boolean switchToNextCheck()
	{
		return true;
	}
	
	/**
	 * 向更老的日期切换检查.
	 * 
	 * @return true表示检查通过，允许切换到新日期，否则不允许切换
	 */
	protected boolean switchToPreviousCheck()
	{
		return true;
	}
	
	public interface PeriodSwitchType
	{
		int ERROR = -99;
		/** 日报表 */
		int day = 0;
		/** 周报表 */
		int week = 1;
		/** 月报表 */
		int month = 2;
		/**年报表*/
		int year=3;
		/** 全网排名 */
		int all = 98;
		/** 企业排名 */
		int ent = 99;
	}
}

