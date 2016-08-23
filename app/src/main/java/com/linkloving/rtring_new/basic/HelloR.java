package com.linkloving.rtring_new.basic;

import android.util.Log;

/**
 * Created by zkx on 2016/2/24.
 */
public class HelloR {
    private static final String TAG =  HelloR.class.getSimpleName();
    private String defaultRClassPath = null;

    public String getDefaultRClassPath()
    {
        return this.defaultRClassPath;
    }

    public void setDefaultRClassPath(String defaultRClassPath) {
        this.defaultRClassPath = defaultRClassPath;
    }

    public int layout(String name)
    {
        return getResourseIdByName(getDefaultRClassPath(), "layout", name);
    }

    public int anim(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "anim", name);
    }

    public int attr(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "attr", name);
    }

    public int color(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "color", name);
    }

    public int drawable(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "drawable", name);
    }

    public int mipmap(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "mipmap", name);
    }

    public int id(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "id", name);
    }

    public int menu(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "menu", name);
    }

    public int raw(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "raw", name);
    }

    public int string(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "string", name);
    }

    public int style(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "style", name);
    }

    public int styleable(String name) {
        return getResourseIdByName(getDefaultRClassPath(), "styleable", name);
    }

    private int getResourseIdByName(String packageNameOfR, String className, String name)
    {
        Log.i(TAG , "EVA.Android平台正在反射R资源：" + packageNameOfR + ".R." + className + "." + name);
        if (packageNameOfR == null)
            throw new IllegalArgumentException("Note by Jack Jiang: packageNameOfR can't be null!");
        int id = 0;
        try
        {
            id = getResourseIdByName(Class.forName(packageNameOfR + ".R"), className, name);
        }
        catch (ClassNotFoundException e)
        {//
            Log.e(TAG,"Note: " + packageNameOfR + ".R 类不存在.");
            e.printStackTrace();
        }
        return id;
    }

    private int getResourseIdByName(Class clazzOfR, String className, String name)
    {
        if (clazzOfR == null) {
            throw new IllegalArgumentException("Note : clazzOfR can't be null!");
        }
        Class r = clazzOfR;
        int id = 0;

        String hintMsgIfException = "反射R常量:" + clazzOfR.getName() + "$" + className + "." + name + "时出错,";
        try
        {
            Class[] classes = r.getClasses();
            Class desireClass = null;
            for (int i = 0; i < classes.length; i++)
            {
                if (!classes[i].getName().split("\\$")[1].equals(className))
                    continue;
                desireClass = classes[i];
                break;
            }

            if (desireClass != null) {
                id = desireClass.getField(name).getInt(desireClass);
            }

        }
        catch (IllegalArgumentException e)
        {
            Log.e(TAG,"Note: " + hintMsgIfException + e.getMessage());
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            Log.e(TAG,"Note: " + hintMsgIfException + e.getMessage());
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            Log.e(TAG,"Note: " + hintMsgIfException + e.getMessage());
            e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
            Log.e(TAG,"Note: " + hintMsgIfException + e.getMessage());
            e.printStackTrace();
        }
        return id;
    }
}
