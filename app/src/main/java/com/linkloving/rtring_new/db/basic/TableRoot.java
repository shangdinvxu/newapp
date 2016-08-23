package com.linkloving.rtring_new.db.basic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 本系统中sqlLite表对象根类，本系统中的表操作对象推荐继承本类后再实现自已的逻辑.<br>
 * 
 * 本类中提供了一个字段用于标识当前数据行数并自行维护之，另提供一字段表示本表最近一次数据插入时间.
 * 
 * @author Jack Jiang, 2013-03-18
 * @since 2.5
 */
public abstract class TableRoot
{
	/** 用于缓存列表查看面板中：表示缓存对应的表常量值，在 DataBaseHelper中被定义 */
	public static String IDENT_TABLE_ID="tableId";
	/** 用于缓存列表查看面板中：表示缓存对应的数据库表名，在 DataBaseHelper中被定义 */
	public static String IDENT_TABLE_NAME="tableName";
	/** 用于缓存列表查看面板中：表示缓存对应的表显示名，在 DataBaseHelper中被定义 */
	public static String IDENT_TABLE_SHOWNAME="showName";
	/** 用于缓存列表查看面板中：表示缓存对应的表数据行数，在 DataBaseHelper中被定义 */
	public static String IDENT_TABLE_DATA_COUNT="dataCount";
	/** 用于缓存列表查看面板中：表示缓存对应的表数据最近更新时间，在 DataBaseHelper中被定义 */
	public static String IDENT_TABLE_UPDATE_TIME="updateTime";
	
	/** 主要用于缓存列表查看面板中：本字段表示子类实现的表当前数据行数 */
	protected long count=0;
	/** 主要用于缓存列表查看面板中：本字段表示子类实现的表数据最近更新时间 */
	protected Date updateTime;
	
	/** 操作子类实现表的context的实例引用 */
	protected Context context;
	/** 子类实现表对应的数据数操作辅助对象实例引用 */
	protected MyDataBase db;
	
	public TableRoot(Context context)
	{
		this.context = context;
	}
	
	/**
	 * 用于插入一行数据到本表中。
	 * 本方法应由子类实现，默认什么也不做，返回-1值。
	 * 
	 * @param rowValues 要插入本表的数据行vector，每个单元的意义及如何使用由子类自行决定
	 * @return 参见：SQLiteDatabase{@link #insert(String, String, ContentValues)}
	 */
	public long insertRow(Vector rowValues)
	{
		return -1;
	}
	
	/**
	 * <p>
	 * 将指定2维护vector表示的多行数据插入到本表中.<br><br>
	 * 
	 * 本方法中在插入开始前开始事务，在数据插入后提交数据以提高性能，否则性
	 * 能将相差N个数量级。要取消事务方式提交只需要把insertRow(..)前后有有关事务操作语句删除即可.<br><br>
	 * 
	 * 另：本方法也同时更新字段 {@link #updateTime}1次，以便刷新最近更新时间，用于其它地方显示之用.
	 * </p>
	 * 
	 * @param vector
	 * @see #insertRow(Vector)
	 */
	//*** 2011-11-17　BY　JS，生产环境下，1600条左右的产品信息，在读取完后插入到SQLLite的过程中如果不用事务
	//*** ，则从登陆开始耗时在1分30秒以上(测试表明1600行左右的产品的插入耗时就整整要1分钟30秒左右)，
	//*** 用了事务以后，从登陆开始耗时只有8秒左右(测试表明1600行左右的产品的插入耗时只需要1~2秒，太棒了！！！)
	public void insertRows(Vector<Vector> vector)
	{
		SQLiteDatabase dbImpl = db.getDb(true);
		//* 手动设置开始事务[事务操作语句 1/3]
		dbImpl.beginTransaction();        
		
		//更新最近刷新时间
		updateTime = new Date();
		
		//数据插入操作循环
		for(Vector rowVec:vector)
			insertRow(rowVec);
		
		//* 设置事务处理成功，不设置会自动回滚不提交[事务操作语句 2/3]
		dbImpl.setTransactionSuccessful(); 
		//* 事务结束，处理完成 [事务操作语句 3/3]
		dbImpl.endTransaction();        
	}
	
	/**
	 * 用于缓存列表查看面板中：返回本表的基本信息，便于查看和管理sqlLite缓存。
	 * 
	 * @return 返回的是key、value键值对，键由 {@link #IDENT_TABLE_ID}等定义
	 * @see #getID()
	 * @see #getTableName()
	 * @see #getTableDesc()
	 */
	public Map<String,String> getTableInfo()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HashMap<String,String> tableMap= new HashMap<>();
		tableMap.put(IDENT_TABLE_ID, getID()+"");//DataBaseHelper.UPDATE_TABLE_SCKH);
		tableMap.put(IDENT_TABLE_NAME, getTableName());//TABLE_NAME);
		tableMap.put(IDENT_TABLE_SHOWNAME, getTableDesc());//"商超客户信息数据缓存");
		tableMap.put(IDENT_TABLE_DATA_COUNT, count+"");
		tableMap.put(IDENT_TABLE_UPDATE_TIME, sdf.format(updateTime));
		return tableMap;		
	}
	
	/** 
	 * 返回用于标识本表的唯一id常量值，常量值在 DataBaseHelper中被定义。
	 * 子类需要实现之.
	 * 
	 * @see #getTableInfo() 
	 */
	public abstract int getID();
	/** 
	 * 返回本表的描述信息。
	 * 子类需要实现之.
	 * 
	 * @see #getTableInfo() 
	 */
	public abstract String getTableDesc();
	/** 
	 * 返回本表对应的sqlLite数据库中的表名。
	 * 子类需要实现之.
	 * 
	 * @see #getTableInfo()
	 */
	public abstract String getTableName();
	
	/**
	 * 删除本表中的所有数据，并置 {@link #count}=0表示当前表数据为0行。
	 * 
	 * @return 参考：SQLiteDatabase.{@link #@delete(String, String, String[])} >0
	 */
	public boolean deleteDatas()
	{
		count = 0;
		return db.getDb(true).delete(getTableName(), null, null)>0;
	}
	
	/**
	 * 初始化数据库操作辅助类，在操作本类中的所有数据库操作方法前必须确保首先调用本方法至少一次。
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException
	{
//		if(db == null)
			db = MyDataBase.open(context);
	}
	
	/**
	 * 释放数据库操作辅助类所占用资源，在操作本类中的所有数据库操作方法完成后请显示调用本方法1次，以
	 * 释放资源，一般情况下它将与 {@link #open()}方法成对出现 。
	 * 
	 * @throws SQLException
	 */
	public void close()
	{
		if(db != null)
			db.close();
//		db = null;
	}
	
	/**
	 * 查询本表中的数据.
	 * 
	 * @param fieldNames 要查询的字段名
	 * @param filterSQL 过滤条件
	 * @return
	 * @see SQLiteDatabase #query(String, String[], String, String[], String, String, String, String)
	 */
	public Cursor query(String[] fieldNames, String filterSQL)
	{
		return db.getDb(false).query(getTableName()
				, fieldNames, filterSQL, null, null, null, null);
	}
	
	/**
	 * 插入1行数据到本表中，并更新 {@link #count}使其+1，表示当前表数据行增加1.
	 * 
	 * @param table
	 * @param nullColumnHack
	 * @param values
	 * @return
	 * @see SQLiteDatabase#insert(String, String, ContentValues)
	 */
	public long insert(String table, String nullColumnHack, ContentValues values)
	{
		count++;
		return db.getDb(true).insertWithOnConflict(table, nullColumnHack, values,5);  //{"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};
//		return db.getDb(true).insert(table, nullColumnHack, values);
	}
	
	/**
	 * 删除表数据，并更新 {@link #count}使其+1，表示当前表数据行增加1.
	 * 
	 * @param table
	 * @param where
	 * @return
	 * @see SQLiteDatabase#delete(String, String, String[])
	 */
	public long delete(String table, String where)
	{
		long efeectRow = db.getDb(true).delete(table, where, null);
		count = count - efeectRow;
		return efeectRow;
	}
	
	/**
	 * 更新表数据.
	 * 
	 * @param table
	 * @param values
	 * @param whereClause
	 * @return the number of rows affected
	 * @see SQLiteDatabase#update(String, ContentValues, String, String[])
	 */
	public long update(String table, ContentValues values, String whereClause)
	{
		long efeectRow = db.getDb(true).update(table, values, whereClause, null);
		return efeectRow;
	}

}
