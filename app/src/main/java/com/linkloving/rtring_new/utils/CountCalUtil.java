package com.linkloving.rtring_new.utils;

import android.content.DialogInterface;

import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.lang.reflect.Field;

/**
 * Created by leo.wang on 2016/5/12.
 */
public class CountCalUtil {

    private static final String TAG = CountCalUtil.class.getSimpleName();
    public static double calculateCalories(double diastance, int seconds, int weight) {

        double calory = 0.0D;

        double modulus = 1.0D;

        double s = diastance * 3600.0D / 1000.0D/seconds;

        MyLog.i(TAG,"weight="+weight+"s="+s+"seconds="+seconds+"diastance="+diastance);

        if(s <= 9.654D) {
            MyLog.i(TAG,"计算的卡路里"+(diastance * (double)weight * 1.099D)/1000+"");
            return (diastance* (double)weight * 1.099D)/1000;
        }
        else {
            if(s > 19.308D) {
                modulus = 0.13D;
            } else if(s > 17.699D) {
                modulus = 0.113D;
            } else if(s > 16.09D) {
                modulus = 0.1D;
            } else if(s > 14.481D) {
                modulus = 0.094D;
            } else if(s > 12.872D) {
                modulus = 0.089D;
            } else if(s > 11.263D) {
                modulus = 0.085D;
            } else if(s > 9.654D) {
                modulus = 0.079D;
            }
            calory = getCalory((double)seconds, weight, modulus);
            MyLog.i("kalulli=calory=",+calory+"");
            return calory;
        }
    }

    private static double getCalory(double minute, int weight, double modulus) {
        return (minute*weight*modulus)/(0.453*60);
    }



    public static void allowCloseDialog(DialogInterface dialog, boolean allow)
    {
        try
        {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            try
            {
                field.set(dialog, allow);
            }
            catch (IllegalArgumentException e)
            {
//				e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
//				e.printStackTrace();
            }
        }
        catch (SecurityException e)
        {
//			e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
//			e.printStackTrace();
        }
    }

}
