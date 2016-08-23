package com.linkloving.rtring_new.db.basic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.linkloving.rtring_new.db.sport.UserDeviceRecord;
import com.linkloving.rtring_new.db.summary.DaySynopicTable;
import com.linkloving.rtring_new.db.weight.WeightTable;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

/**
 * 本系统的数据库操作核心实现类.
 *
 *
 *
 */
public class MyDataBase
{
	public final static String TAG = MyDataBase.class.getSimpleName();

	/** 数据库（文件）名 */
	private static final String DB_NAME = "rtring.db";
	/**
	 * 当前版本号，此版本将决定你的数据库是否会被重建（与手机上当前的版本比较）——
	 * 如果本版本号高于你手机上的正在使用中的数据库版本号高则会被重构，否则不会。 所以如要更新手机上数据库结构则需要修改此版本号（递增）
	 */
	// ver 1 -> 2014-06-11 by JS：第1个发布版本
	// ver 2 -> 2014-06-17 by JS：增加了表UserDeviceRecord
	// ver 5 -> 2014-06-21 by JS：在表rt_day_synopic中增加了字段sleep_minute、deep_sleep_miute,user_device_record中steps_part、distance_part
	private static final int DB_VERSION = 7;

	// 此实例并非单例，它在需要时被实例化，并在close时置本对象为null
	private static MyDataBase sInstance = null;

	/**
	 * SQLiteDatabase Open Helper
	 */
	private DatabaseHelper mOpenHelper = null;

	/** 当前正在使用数据库的计数器. 调用{@link #open(Context)}时本计数器+1，
	 * 调用{@link #close()}时-1且如果此值<=0时才真正地调用{@link DatabaseHelper#close()}从而释放资源 */
	private static int current = 0;

	/**
	 * 构造函数：创建一个DatabaseHelper给成员变量
	 * @param context
	 */
	private MyDataBase(Context context)
	{
		mOpenHelper = new DatabaseHelper(context);
	}

	/**
	 * Get Database且计数器+1。
	 * <b>重要说明：</b>open方法的调用推荐与{@link #close()}方法成对调用，从而保证资源的及时释放.
	 *
	 * @param context
	 * @return
	 */
	public static synchronized MyDataBase open(Context context)
	{
		if (null == sInstance)
			sInstance = new MyDataBase(context);

		// 计数器+1
		current += 1;

		return sInstance;
	}
	/**
	 * Close Database且计数器-1.
	 * <p>
	 * 当且仅当{@link #current}<=0时才真正地调用{@link DatabaseHelper#close()}从而释放资源.
	 * <p>
	 * 为什么有{@link #current}计数器的存在？因为在KChat中，比如app启动时，因各种数据的载入的保存
	 * 很可能是在异步线程中执行的，很难保证顺序执行，如果此时有调用者调用close释放了db则将影响其
	 * 它正在进行中的db操作。而经改进后，则不存在这个问题：只在当所有使用者都释放使用权后，最后
	 * 一个使用者使用完成时才会真正的close，这属于安全的释放db资源！
	 */
	public void close()
	{
		// 计数器-1
		current -= 1;

		if(current <= 0)
		{
			try
			{
				if (null != sInstance)
				{
					mOpenHelper.close();
					sInstance = null;
				}
			}
			catch (Exception e)
			{
				Log.w(TAG, e);
			}
			finally
			{
				// 恢复计数器默认状态
				current = 0;
			}
		}
	}

	/**
	 * Get SQLiteDatabase Open Helper
	 *
	 * @return
	 */
	public SQLiteOpenHelper getSQLiteOpenHelper()
	{
		return mOpenHelper;
	}

	/**
	 * Get Database Connection
	 *
	 * @param writeable
	 * @return
	 */
	public SQLiteDatabase getDb(boolean writeable)
	{
		if (writeable)
			return mOpenHelper.getWritableDatabase();
		else
			return mOpenHelper.getReadableDatabase();
	}

	/**
	 * 本方法实现数据库表及其它结构的建立.<br>
	 * <b>数据库需要的表则其建表语句应该放在本方法里，有多少表就要多少个建表语句.</b>
	 *
	 * {@inheritDoc}
	 */
	public static void createAllTables(SQLiteDatabase db)
	{
		MyLog.d(TAG, "Creat UserDeviceRecord sql:"+UserDeviceRecord.DB_CREATE);
		MyLog.d(TAG, "Creat DaySynopicTable sql:"+DaySynopicTable.DB_CREATE);
		db.execSQL(UserDeviceRecord.DB_CREATE);
		db.execSQL(DaySynopicTable.DB_CREATE);
		db.execSQL(WeightTable.DB_CREATE);
	}

	/**
	 * <p>
	 * 本方法实现数据库表及其它结构的删除.<br>
	 * <b>数据库需要的表则其删除语句应该放在本方法里，有多少表就要多少个删除语句.</b> <br>
	 * <br>
	 * 数据库升级时为确保数据库表结构的完整，本方法是简单的先尝试删除存在的表，然后再把所有表重建一次.
	 * 这样带来的后果是历史数据将丢失，所幸本系统的历史数据没有意义，因为每登陆1次都要要至少确保自动重置缓存1次
	 * 以尽量确保数据是最新的(缓存数据当然无法完全保证数据是最新的，只是在性能和数据的最新性方面作出的权衡选择).
	 * </p>
	 *
	 * {@inheritDoc}
	 */
	public static void dropAllTables(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		MyLog.d(TAG, "开始删除之前的数据库表结构.");
		db.execSQL("DROP TABLE IF EXISTS " + UserDeviceRecord.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DaySynopicTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + WeightTable.TABLE_NAME);
	}

	/**
	 * SQLiteOpenHelper
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		/**构造函数：创建一个SQLiteOpenHelper**/
		public DatabaseHelper(Context context)
		{
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			Log.d(TAG, "Create Database.");
			// create tables
			createAllTables(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.d(TAG, "Upgrade Database.");
			// DROP TABLE
			dropAllTables(db, oldVersion, newVersion); //先删除后创建
			//
			onCreate(db);
		}
	}
}
