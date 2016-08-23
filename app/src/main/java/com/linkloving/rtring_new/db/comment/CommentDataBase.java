package com.linkloving.rtring_new.db.comment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DC on 2016/4/19.
 */
public class CommentDataBase extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "comment";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_TO_USER_ID = "to_user_id";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_Comment = "comments";
    public static final String COLUMN_TIME = "comment_create_time";
    //设置标志 根据标志显示聊天消息
    public static final String COLUMN_TAG = "comment_tag";

    public CommentDataBase(Context context) {
        super(context, "Comment", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建表
        db.execSQL("CREATE TABLE "
                + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_TO_USER_ID + " INTEGER,"
                + COLUMN_Comment + " TEXT NOT NULL,"
                + COLUMN_TIME + " TIMESTAMP default (datetime('now', 'localtime')),"  //插入数据的utc时间
                + COLUMN_TAG + " TEXT NOT NULL"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static CommentDataBase instance = null;

    public static CommentDataBase getInstance(Context context) {
        if (null == instance) {
            instance = new CommentDataBase(context);
        }
        return instance;
    }

//    public SQLiteDatabase getDb(boolean writeable) {
//        if (writeable)
//            return getWritableDatabase();
//        else
//            return getReadableDatabase();
//    }
}
