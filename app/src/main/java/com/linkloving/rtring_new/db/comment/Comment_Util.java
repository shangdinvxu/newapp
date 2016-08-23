package com.linkloving.rtring_new.db.comment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.linkloving.rtring_new.logic.UI.friend.chatbean.CommentChat;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.TimeZoneHelper;

import java.util.ArrayList;

/**
 * Created by DC on 2016/4/19.
 */
public class Comment_Util {
    private final static String TAG = Comment_Util.class.getSimpleName();
    static CommentDataBase mOpenHelper;
    Context context;

    public Comment_Util(Context context) {
        this.context = context;
        mOpenHelper=CommentDataBase.getInstance(context);
    }

    //插入数据  插入参数 user_id to_user_id comments time
    public static void InsertData(int user_id, int to_user_id, String comments,String localtime,String tag){

        String dateTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, localtime);
        ContentValues cv = new ContentValues();
        cv.put(CommentDataBase.COLUMN_USER_ID, user_id);
		cv.put(CommentDataBase.COLUMN_TO_USER_ID, to_user_id);
        cv.put(CommentDataBase.COLUMN_Comment, comments);
        cv.put(CommentDataBase.COLUMN_TIME, dateTimeUTC);
        cv.put(CommentDataBase.COLUMN_TAG, tag);
        // 获取
        mOpenHelper.getWritableDatabase().insert(CommentDataBase.TABLE_NAME, null, cv);
        MyLog.e(TAG,"=====插入数据成功===="+user_id+"，to_user_id="+to_user_id+"，comments="+comments);
        mOpenHelper.getWritableDatabase().close();
    }
    public static ArrayList<CommentChat> Query(int user_id,int to_user_id){
        ArrayList<CommentChat> list=new ArrayList<CommentChat>();
        Cursor mCursor = null;
        //查询sql语句
        String where = CommentDataBase.COLUMN_USER_ID+"='"+user_id+"' and "+CommentDataBase.COLUMN_TO_USER_ID+"='"+to_user_id+"'";//+"'"+" order by "+CommentDataBase.COLUMN_TIME+" asc"
        String[] strings=new String[]{CommentDataBase.COLUMN_USER_ID,
                CommentDataBase.COLUMN_TAG,
                CommentDataBase.COLUMN_Comment,
                CommentDataBase.COLUMN_TIME,
                CommentDataBase.COLUMN_TO_USER_ID };
//查询数据
        mCursor =mOpenHelper.getWritableDatabase().query(CommentDataBase.TABLE_NAME,strings,where,null,null,null,null);
        mCursor.moveToFirst();
        while(!mCursor.isAfterLast())
        {
            CommentChat commentChat= new CommentChat();
            //转换时间
            commentChat.setComment_create_time((mCursor.getString(mCursor.getColumnIndex(CommentDataBase.COLUMN_TIME))));
            commentChat.setComments((mCursor.getString(mCursor.getColumnIndex(CommentDataBase.COLUMN_Comment))));
            commentChat.setTo_user_id((mCursor.getInt(mCursor.getColumnIndex(CommentDataBase.COLUMN_TO_USER_ID))));
            commentChat.setUser_id((mCursor.getInt(mCursor.getColumnIndex(CommentDataBase.COLUMN_USER_ID))));
            commentChat.setTag((mCursor.getString(mCursor.getColumnIndex(CommentDataBase.COLUMN_TAG))));
            list.add(commentChat);
            mCursor.moveToNext();
        }
        mCursor.close();
        return list;
    }

}
