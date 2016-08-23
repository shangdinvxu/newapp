package com.linkloving.rtring_new.basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Stack;

/**
 * Created by zkx on 2016/2/24.
 */
public class AppManager {
    private static final String TAG = AppManager.class.getSimpleName();

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    public static AppManager getAppManager()
    {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }
    /** @SuppressWarning */
    public void addActivity(Activity activity)
    {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }

        Log.d(TAG, "【AM+】activity" + activity.getClass().getCanonicalName() + "正在被加入全局管理列表.");
        activityStack.add(activity);
    }

    public Activity currentActivity()
    {
        Activity activity = (Activity)activityStack.lastElement();
        return activity;
    }

    public void removeActivity()
    {
        Activity activity = (Activity)activityStack.lastElement();
        removeActivity(activity);
    }

    public void removeActivity(Activity activity)
    {
        if (activity != null) {
            Log.d(TAG, "【AM-】finishActivity：activity" + activity.getClass().getCanonicalName() + "正在从全局管理列表中移除.");
            activityStack.remove(activity);

            activity = null;
        }
    }

    public void removeActivity(Class<?> cls)
    {
        for (Activity activity : activityStack)
            if (activity.getClass().equals(cls))
                removeActivity(activity);
    }

    public void finishAllActivity()
    {
        int i = 0; for (int size = activityStack.size(); i < size; i++) {
        if (activityStack.get(i) != null) {
            Log.d(TAG, "【AM-】finishAllActivity：activity" +((Activity)activityStack.get(i)).getClass().getCanonicalName() + "正在从全局管理列表中移除.");
            try {
                ((Activity)activityStack.get(i)).finish();
            }
            catch (Exception e) {
                Log.w(TAG, "finishAllActivity时出错了，" + e.getMessage(), e);
            }
        }
    }
        activityStack.clear();
    }

    public void AppExit(Context context)
    {
        try
        {
            int currentVersion = Build.VERSION.SDK_INT;
            Log.d(TAG, "【APP正在退出】" + currentVersion);
//            if (currentVersion > 7) {
                Intent startMain = new Intent("android.intent.action.MAIN");
                startMain.addCategory("android.intent.category.HOME");
                startMain.setFlags(268435456);
                context.startActivity(startMain);
                System.exit(0);
//            }  //此方法是1.5~2.1版本的退出方法  本app最低为4.3
//            else
//            {
//                ActivityManager am = (ActivityManager)context.getSystemService("activity");
//                am.restartPackage(context.getPackageName());
//            }
        }
        catch (Exception e)
        {
            Log.w(TAG, e.getMessage(), e);
        }
    }
}
