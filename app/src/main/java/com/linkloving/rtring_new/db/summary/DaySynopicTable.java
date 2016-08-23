package com.linkloving.rtring_new.db.summary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.db.basic.TableRoot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * 日数据概览表历史记录的辅助操作实现类.
 * 
 */
public class DaySynopicTable extends TableRoot
{
	private final static String TAG = DaySynopicTable.class.getSimpleName();
	
	/** 存放于sqlLite数据库中的表格字段名：自增id（主键）（默认ident列，无需插入数据）*/
	private static final String COLUMN_KEY_ID="_record_id";
	private static final String COLUMN_KEY_ACOUNT$UID="_user_id";
	
	public static final String COLUMN_DATA_DATE="data_date";
	public static final String COLUMN_DATA_DATE2="data_date2";
	public static final String COLUMN_TIMEZONE="summary_timezone";
	public static final String COLUMN_RUN_DURATION="run_duration";
	public static final String COLUMN_RUN_STEP="run_step";
	public static final String COLUMN_RUN_DISTANCE="run_distance";
	public static final String COLUMN_WORK_STEP="work_step";
	public static final String COLUMN_WORK_DISTANCE="work_distance";
	public static final String COLUMN_WORK_DURATION="work_duration";

	public static final String COLUMN_SLEEP_MINUTE="sleep_minute";
	public static final String COLUMN_DEEP_SLEEP_MIUTE="deep_sleep_miute";
	public static final String COLUMN_CALORIE="_calorie_";
	public static final String COULUM_GO_TO_BED_TIME="gotobed_time";
	public static final String COLUMN_GET_UP_TIME="getup_time";

	/** 存放于sqlLite数据库中的表名 */
	public static final String TABLE_NAME="rt_day_synopic"; //日数据浏览表
	
	/** 建表语句 */
	public static final String DB_CREATE=" CREATE TABLE "
										+TABLE_NAME+" ( "	
										+COLUMN_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
										+COLUMN_KEY_ACOUNT$UID+" INTEGER,"
										+COLUMN_DATA_DATE+" TEXT UNIQUE,"
										+COLUMN_DATA_DATE2+" TEXT,"
										+COLUMN_TIMEZONE+" INTEGER,"
										+COLUMN_RUN_DURATION+" INTEGER,"
										+COLUMN_RUN_STEP+" INTEGER,"
										+COLUMN_RUN_DISTANCE+" INTEGER,"
										+COLUMN_WORK_DURATION+" INTEGER,"
										+COLUMN_WORK_STEP+" INTEGER," 
										+COLUMN_WORK_DISTANCE+" INTEGER," 
										+COLUMN_SLEEP_MINUTE+" INTEGER," 
										+COLUMN_DEEP_SLEEP_MIUTE+" INTEGER,"
										+COLUMN_CALORIE+" INTEGER,"
										+COULUM_GO_TO_BED_TIME+" INTEGER,"
										+COLUMN_GET_UP_TIME+" "
										+")";
	//singleton
	private static DaySynopicTable instance;
	
	private DaySynopicTable(Context context)
	{
		super(context);
	}
	
	public static DaySynopicTable getInstance(Context context)
	{
		if(instance==null)
			// FIX BUG: 以下context不能直接传Activity及其子类的句柄，否则将发生内存泄漏！
			// Application为全局唯一，所以不存在不释放的问题！
			instance=new DaySynopicTable(MyApplication.getInstance(context));
		return instance;		
	}

	//---------------------------------------------------------------------------------------------------
	public int getID()
	{
		return 1;
	}
	public String getTableDesc()
	{
		return "首页”消息“缓存";
	}
	public String getTableName()
	{
		return TABLE_NAME;
	}
	
	/**
	 * 从本地sqlLite的产品信息表中查询所需数据.
	 * 
	 * @param userId 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param condition 查询条件
	 * @return 游标结果集
	 */
	private Cursor queryDaySynopicImpl(String userId, String condition)
	{
		return query(new String[]{
				 COLUMN_KEY_ID
				,COLUMN_DATA_DATE
				,COLUMN_DATA_DATE2
				,COLUMN_TIMEZONE
				,COLUMN_RUN_DURATION
				,COLUMN_RUN_STEP
				,COLUMN_RUN_DISTANCE
				,COLUMN_WORK_DURATION
				,COLUMN_WORK_STEP
				,COLUMN_WORK_DISTANCE
				,COLUMN_SLEEP_MINUTE
				,COLUMN_DEEP_SLEEP_MIUTE
				,COLUMN_GET_UP_TIME
				,COULUM_GO_TO_BED_TIME} , COLUMN_KEY_ACOUNT$UID+"='"+userId+"'"+(condition == null?"":" and "+condition)+" order by "+COLUMN_DATA_DATE+" asc");
	}
	/**
	 * 返回ArrayList<DaySynopic>记录.
	 * @param userId 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @return
	 */
	private ArrayList<DaySynopic> findDaySynopic(String userId, String condition)
	{
		ArrayList<DaySynopic> cpList= new ArrayList<DaySynopic>();
		//条件是
		Cursor mCursor = queryDaySynopicImpl(userId, condition);//" 1=1 order by _update_time asc");// 取出的结果顺序的哦）
		mCursor.moveToFirst();
		while(!mCursor.isAfterLast())
		{
			try
			{
				DaySynopic cp = new DaySynopic();
				cp.setRecord_id(mCursor.getString(mCursor.getColumnIndex(COLUMN_KEY_ID))); // 此record_id是本地sqlite生成的，用于update时
				cp.setData_date(mCursor.getString(mCursor.getColumnIndex(COLUMN_DATA_DATE)));
				cp.setData_date2(mCursor.getString(mCursor.getColumnIndex(COLUMN_DATA_DATE2)));
				cp.setTime_zone(mCursor.getString(mCursor.getColumnIndex(COLUMN_TIMEZONE)));
				cp.setRun_duration((mCursor.getString(mCursor.getColumnIndex(COLUMN_RUN_DURATION))));
				cp.setRun_step(mCursor.getString(mCursor.getColumnIndex(COLUMN_RUN_STEP)));
				cp.setRun_distance(mCursor.getString(mCursor.getColumnIndex(COLUMN_RUN_DISTANCE)));
				cp.setWork_duration(mCursor.getString(mCursor.getColumnIndex(COLUMN_WORK_DURATION)));
				cp.setWork_step(mCursor.getString(mCursor.getColumnIndex(COLUMN_WORK_STEP)));
				cp.setWork_distance(mCursor.getString(mCursor.getColumnIndex(COLUMN_WORK_DISTANCE)));
				cp.setSleepMinute(mCursor.getString(mCursor.getColumnIndex(COLUMN_SLEEP_MINUTE)));
				cp.setDeepSleepMiute(mCursor.getString(mCursor.getColumnIndex(COLUMN_DEEP_SLEEP_MIUTE)));
				cp.setGetupTime(mCursor.getString(mCursor.getColumnIndex(COLUMN_GET_UP_TIME)));
				cp.setGotoBedTime(mCursor.getString(mCursor.getColumnIndex(COULUM_GO_TO_BED_TIME)));
				//还需要增加卡路里
				cpList.add(cp);
			}
			catch (Exception e)
			{
				Log.e(TAG, e.getMessage(), e);
			}
			
			mCursor.moveToNext();
		}
		mCursor.close();
		return cpList;
	}
	
	/**
	 * 返回指定日期范围内的记录.
	 * <p>
	 * 如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999间的数据.<br>
	 * @param userId 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param startDate
	 * @param endDate
	 * @param timezone 所需要查询数据的时区
	 * @return
	 */
	private ArrayList<DaySynopic> findDaySynopicRange(String userId, String startDate, String endDate,String timezone)
	{
		// 时间范围内的数据
		String where = "("+COLUMN_DATA_DATE+" between '"+startDate+"' and '"+endDate+"')";
		
		// 查找此期间内的运动原始数据
		return findDaySynopic(userId, where);
	}
	
	/**
	 * 插入一行临时聊天的首页消息数据.
	 * 
	 * @param user_id 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param amd
	 * @return
	 */
	private long insertDaySynopic(String user_id, DaySynopic amd)
	{
		if(amd != null)
		{
			return insertDaySynopic(user_id
						,amd.getData_date()
						,amd.getData_date2()
						,amd.getTime_zone()
						,amd.getRun_duration()
						,amd.getRun_step()
						,amd.getRun_distance()
						,amd.getWork_duration()
						,amd.getWork_step()
						,amd.getWork_distance()
						,amd.getSleepMinute()
						,amd.getDeepSleepMiute()
						,amd.getGetupTime()
						,amd.getGotoBedTime()
					);
		}
		return -1;
	}
	/**
	 * 插入一行数据到表中.
	 */
	private long insertDaySynopic( String user_id
				, String data_date
				, String data_date2
				, String time_zone
				, String run_duaration
				, String run_step
				, String run_distance
				, String work_duaration
				, String work_step
				, String work_distance
				, String sleep_minute
				, String deep_sleep_miute
				, String getup_time
				,String gotobed_time
			)
	{
		ContentValues initialValues=new ContentValues();
		initialValues.put(COLUMN_KEY_ACOUNT$UID, user_id);
		initialValues.put(COLUMN_DATA_DATE, data_date);
		initialValues.put(COLUMN_DATA_DATE2, data_date2);
		initialValues.put(COLUMN_TIMEZONE, time_zone);
		initialValues.put(COLUMN_RUN_DURATION, run_duaration);
		initialValues.put(COLUMN_RUN_STEP, run_step);
		initialValues.put(COLUMN_RUN_DISTANCE, run_distance);
		initialValues.put(COLUMN_WORK_DURATION, work_duaration);
		initialValues.put(COLUMN_WORK_STEP, work_step);
		initialValues.put(COLUMN_WORK_DISTANCE, work_distance);
		initialValues.put(COLUMN_SLEEP_MINUTE, sleep_minute);
		initialValues.put(COLUMN_DEEP_SLEEP_MIUTE, deep_sleep_miute);
		initialValues.put(COLUMN_GET_UP_TIME,getup_time);
		initialValues.put(COULUM_GO_TO_BED_TIME,gotobed_time);
		return super.insert(TABLE_NAME, null, initialValues);//
	}
	
	/**
	 * <p>
	 * 将指定2维护vector表示的多行数据插入到本表中.<br><br>
	 * 本方法中在插入开始前开始事务，在数据插入后提交数据以提高性能，否则性
	 * 能将相差N个数量级。要取消事务方式提交只需要把insertRow(..)前后有有关事务操作语句删除即可.<br><br>
	 * 另：本方法也同时更新字段 {@link #updateTime}1次，以便刷新最近更新时间，用于其它地方显示之用.
	 * </p>
	 * @see #insertRow(Vector)
	 */
	//*** 2011-11-17　BY　JS，生产环境下，1600条左右的产品信息，在读取完后插入到SQLLite的过程中如果不用事务
	//*** ，则从登陆开始耗时在1分30秒以上(测试表明1600行左右的产品的插入耗时就整整要1分钟30秒左右)，
	//*** 用了事务以后，从登陆开始耗时只有8秒左右(测试表明1600行左右的产品的插入耗时只需要1~2秒，太棒了！！！)
	private void insertDaySynopics(List<DaySynopic> dss, String userId)
	{
		SQLiteDatabase dbImpl = db.getDb(true);
		//* 手动设置开始事务[事务操作语句 1/3]
		dbImpl.beginTransaction();        
		
		// 更新最近刷新时间
		updateTime = new Date();

		//* 再插入数据
		for(DaySynopic ds : dss)
			insertDaySynopic(userId, ds);
		
		//* 设置事务处理成功，不设置会自动回滚不提交[事务操作语句 2/3]
		dbImpl.setTransactionSuccessful(); 
		//* 事务结束，处理完成 [事务操作语句 3/3]
		dbImpl.endTransaction();        
	}

	public static long deleteDaySynopic(Context context, String userId)
	{
		long sum  = 0;
		DaySynopicTable chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = DaySynopicTable.getInstance(context);
			chatMessageTable.open();

			// 保存消息到本地数据库
			sum = chatMessageTable.deleteDaySynopicWithRange(userId);
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}

		return sum;
	}

	/**
	 * 删除数据.
	 * @param userId 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param userId 消息发送者的uid
	 * @return
	 */
	private long deleteDaySynopicWithRange(String userId)
	{
		return super.delete(TABLE_NAME, COLUMN_KEY_ACOUNT$UID+"="+userId+"");
	}
	/**  =============查询============
	 * 返回指定日期范围内的记录.
	 * <p>
	 * 如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999间的数据.<br>
	 * @param userId 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param startDate
	 * @param endDate
	 * @param timezone 时区  480
	 * @return
	 */
	public static ArrayList<DaySynopic> findDaySynopicRange(Context context, String userId, String startDate, String endDate,String timezone)
	{
		ArrayList<DaySynopic> ret = new ArrayList<DaySynopic>();
		DaySynopicTable chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = DaySynopicTable.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			ret = chatMessageTable.findDaySynopicRange(userId, startDate, endDate,timezone);
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		
		return ret;
	}

	/** =============存============
	 * 报存DaySynopic汇总运动数据
	 * @param context
	 * @param userId
	 * @return true表示可存成功，否则保存失败
	 */
	public static boolean saveToSqlite(Context context, List<DaySynopic> dss, String userId)
	{
		DaySynopicTable chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = DaySynopicTable.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			chatMessageTable.insertDaySynopics(dss, userId);
			return true;
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		return false;
	}
	
	/**
	 * 子线程报存汇总运动数据
	 */
	public static void saveToSqliteAsync(final Context context, final List<DaySynopic> dss,final  String userId)
	{
		new AsyncTask<Object, Object, Object>(){
			@Override
			protected Object doInBackground(Object... params)
			{
				DaySynopicTable.saveToSqlite(context, dss, userId);
				return null;
			}
		}.execute();
	}
	
	
}
