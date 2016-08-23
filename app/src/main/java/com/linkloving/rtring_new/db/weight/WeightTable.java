package com.linkloving.rtring_new.db.weight;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.db.basic.TableRoot;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 记录用户体重的数据库表
 * Created by zkx on 2016/5/3.
 */
public class WeightTable extends TableRoot {

    private final static String TAG = WeightTable.class.getSimpleName();

    /** 存放于sqlLite数据库中的表格字段名：自增id（主键）（默认ident列，无需插入数据）*/
    private static final String COLUMN_KEY_ID="_id";
    private static final String COLUMN_KEY_ACOUNT$UID="_user_id";  //uerId
    public static final String COLUMN_TIME="time";                 //时间
    public static final String COLUMN_WEIGHT="weight";             //体重

    /** 存放于sqlLite数据库中的表名 */
    public static final String TABLE_NAME="user_weight";

    /** 建表语句 */
    public static final String DB_CREATE="CREATE TABLE "  //IF NOT EXISTS
            +TABLE_NAME+" ( "
            +COLUMN_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,"
            +COLUMN_KEY_ACOUNT$UID+" INTEGER,"
            +COLUMN_TIME+" TEXT UNIQUE," //数据开始时间
            +COLUMN_WEIGHT+" TEXT"       //用户的体重
            +")";

    //singleton
    private static WeightTable instance;

    public WeightTable(Context context) {
        super(context);
    }

    public static WeightTable getInstance(Context context)
    {
        if(instance==null)
            // FIX BUG: 以下context不能直接传Activity及其子类的句柄，否则将发生内存泄漏！
            // Application为全局唯一，所以不存在不释放的问题！
            instance = new WeightTable(MyApplication.getInstance(context));
        return instance;
    }
    /**************************插入***************************/

    /**
     * 将消息保存到本地数据库中作为历史聊天消息保存下来.
     *
     * @param context
     * @param dss
     * @param userId
     * @param userId
     */
    public static void saveToSqliteAsync(final Context context, final List<UserWeight> dss , final  String userId)
    {
        new AsyncTask<Object, Object, Boolean>(){
            @Override
            protected Boolean doInBackground(Object... params)
            {
                return WeightTable.saveWeightToSqlite(context, dss, userId);
            }
        }.execute();
    }
    /**
     * 增
     * @param context
     * @param dss
     * @param userId
     * @return true表示可存成功，否则保存失败
     */
    private static boolean saveWeightToSqlite(Context context,List<UserWeight> dss, String userId)
    {
        WeightTable weightTable = null;
        try
        {
            // 初始化表操作对象
            weightTable = WeightTable.getInstance(context);
            weightTable.open();
            // 保存消息到本地数据库
            weightTable.insertWeightsDay(dss,userId);
            return true;
        }
        catch (Exception e)
        {
            MyLog.e(TAG,e.getMessage(), e);
        }
        finally
        {
            try{
                if(weightTable != null)
                    weightTable.close();
                }
                catch (Exception e){
                }
        }
        return false;
    }

    /**
     * 插入一行数据到表中.
     * @param user_id userId
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    private long insertWeightDay(String user_id , UserWeight userWeight )
    {
        ContentValues initialValues=new ContentValues();
        initialValues.put(COLUMN_KEY_ACOUNT$UID, user_id);
        initialValues.put(COLUMN_TIME, userWeight.getTime());
        initialValues.put(COLUMN_WEIGHT, userWeight.getWeight());
        return super.insert(TABLE_NAME, null, initialValues);//
    }

    /**
     * @param dss
     * @see #insertRow(Vector)
     */
    public void insertWeightsDay(List<UserWeight> dss, String userId)
    {
        SQLiteDatabase dbImpl = db.getDb(true);
        //* 手动设置开始事务[事务操作语句 1/3]
        dbImpl.beginTransaction();
        // 更新最近刷新时间
        //* 再插入数据
        for(UserWeight ds : dss)
            insertWeightDay(userId, ds);
        //* 设置事务处理成功，不设置会自动回滚不提交[事务操作语句 2/3]
        dbImpl.setTransactionSuccessful();
        //* 事务结束，处理完成 [事务操作语句 3/3]
        dbImpl.endTransaction();
    }

    /**************************查询***************************/
    /**UI层直接调用
     * 查询一天的体重
     * @param context
     * @param userId
     * @param data 查询时间  如果是null的时候 默认查询最近的一条
     * @return 正常是会返回一个UserWeight 如果是null代表这天没有数据
     */
    public static UserWeight queryWeightByDay(Context context, String userId, String data)
    {
        ArrayList<UserWeight> srs = new ArrayList<UserWeight>();
        WeightTable weightTable = null;
        UserWeight userWeight = null;
        try
        {
            // 初始化表操作对象
            weightTable = WeightTable.getInstance(context);
            weightTable.open();
            try
            {
                if(data==null){  //查询数据库最近的一条
                    String where = null;
                    MyLog.e(TAG,"where:"+where);
                    // 查找此期间内的运动原始数据
                    srs = weightTable.findHistoryChartsql(userId, where);
                }else{
                    // 时间范围内的数据
                    String where = COLUMN_TIME+"='"+data+"' ";
                    MyLog.e(TAG,"where:"+where);
                    // 查找此期间内的运动原始数据
                    srs = weightTable.findHistoryChartsql(userId, where);
                }

                if(srs!=null && srs.size()>0){
                    //理论只会查出一条 然后取第一条
                    userWeight = srs.get(0);
                }
                return userWeight;
            }
            catch (Exception e)
            {
                MyLog.e(TAG, "findHistoryChartSport时日期格式转换出错，", e);
            }
        }
        catch (Exception e)
        {
            MyLog.e(TAG, "初始化表操作对象",e);
        }
        finally
        {
            try{
                if(weightTable != null)
                    weightTable.close();
            }
            catch (Exception e){
            }
        }
        return userWeight;
    }

    /**************************查询***************************/
    /**UI层直接调用
     * 查询一段时间的体重
     * @param context
     * @param userId
     * @param startdata 开始时间
     * @param enddata   结束时间
     * @return ArrayList<UserWeight> 如果size是0就是没有数据
     */
    public static ArrayList<UserWeight> queryWeights(Context context, String userId, String startdata,String enddata)
    {
        ArrayList<UserWeight> srs = new ArrayList<UserWeight>();
        WeightTable weightTable = null;
        try
        {
            // 初始化表操作对象
            weightTable = WeightTable.getInstance(context);
            weightTable.open();
            try
            {
                // 时间范围内的数据
                String where = COLUMN_TIME+">='"+startdata+"' and "+COLUMN_TIME+"<='"+enddata+"'";
                MyLog.e(TAG,"where:"+where);
                // 查找此期间内的运动原始数据
                srs = weightTable.findHistoryChartsql(userId, where);
                return srs;
            }
            catch (Exception e)
            {
                MyLog.e(TAG, "findHistoryChartSport时日期格式转换出错，", e);
            }
        }
        catch (Exception e)
        {
            MyLog.e(TAG, "初始化表操作对象",e);
        }
        finally
        {
            try{
                if(weightTable != null)
                    weightTable.close();
            }
            catch (Exception e){
            }
        }
        return srs;
    }

    /**
     * sql层查询一段时间的具体方法
     * 返回ArrayList<UserWeight>记录.
     * @param userId 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
     * @param condition
     * @return
     */
    private synchronized ArrayList<UserWeight> findHistoryChartsql(String userId,String condition)
    {
        ArrayList<UserWeight> cpList= new ArrayList<UserWeight>();
        Cursor mCursor = null;
        try
        {
            //条件是
            mCursor = queryHistoryImpl(userId, condition);//" 1=1 order by _update_time asc");// 取出的结果顺序的哦）
            mCursor.moveToFirst();
            while(!mCursor.isAfterLast())
            {
                try
                {
                    UserWeight userWeight = new UserWeight();
                    userWeight.setRecordId((mCursor.getString(mCursor.getColumnIndex(COLUMN_KEY_ID))));
                    userWeight.setUserId((mCursor.getString(mCursor.getColumnIndex(COLUMN_KEY_ACOUNT$UID)))); // 此record_id是本地sqlite生成的，用于update时
                    userWeight.setTime((mCursor.getString(mCursor.getColumnIndex(COLUMN_TIME))));
                    userWeight.setWeight((mCursor.getString(mCursor.getColumnIndex(COLUMN_WEIGHT))));
                    cpList.add(userWeight);
                }
                catch (OutOfMemoryError e)
                {
                    Log.e(TAG, "【首页findHistory(内)】频繁切换日期时导致内存溢出！", e);
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.getMessage(), e);
                }
                mCursor.moveToNext();
            }
            mCursor.close();
        }
        catch (OutOfMemoryError e){
//			WidgetUtils.showToast(context, "** 您的操作过于频繁，数据载入失败，请重试！", ToastType.WARN);
            MyLog.e(TAG, "【首页findHistory(外)】频繁切换日期时导致内存溢出！", e);
        }
        catch (Exception e)
        {
            MyLog.e(TAG, e.getMessage(), e);
        }
        finally
        {
            if(mCursor != null && !mCursor.isClosed())
            {
                mCursor.close();
            }
        }
        return cpList;
    }

    /**
     * 从本地sqlLite的产品信息表中查询所需数据.
     *
     * @param userId 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
     * @param condition 查询条件
     * @return 游标结果集
     */
    private Cursor queryHistoryImpl(String userId, String condition)
    {
        String where;
        if(condition==null){  //如果是null的时候 只查最近的一条
            where =COLUMN_KEY_ACOUNT$UID+"='"+userId+"'"+" order by "+COLUMN_TIME+" desc limit 0,1";
        }else{
            where =COLUMN_KEY_ACOUNT$UID+"='"+userId+"'"+(condition == null?"":" and "+condition)+" order by "+COLUMN_TIME+" asc";
        }
        MyLog.d(TAG, "正在查询明细数据：条件> where="+where+")");
        return query(new String[]{
                COLUMN_KEY_ID
                ,COLUMN_KEY_ACOUNT$UID
                ,COLUMN_TIME
                ,COLUMN_WEIGHT
        }, where);
    }

    @Override
    public int getID() {
        return 3;
    }

    @Override
    public String getTableDesc() {
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
