package com.linkloving.rtring_new.utils;

import android.util.Log;

import java.util.Locale;

/**
 * Created by zkx on 2016/2/24.
 */
public class LanguageHelper {
    private static final String TAG = LanguageHelper.class.getSimpleName();
    /**
     * 是否简体中文.
     * @return
     */
    public static boolean isChinese_SimplifiedChinese()
    {
        String l = Locale.getDefault().getLanguage();
        Log.i(TAG ,"[DEBUG] 此设备的语言是(getLanguage)："+l);
        // 中文 且 国家是大陆的就表示是简体中文了
        return (l != null && l.toLowerCase().equals("zh")) // 中文（包括简体中文、繁体中文）
                && isChina();// 地区是大陆的
    }

    public static boolean isChina() {
        String c = Locale.getDefault().getCountry();
        Log.i(TAG ,"[DEBUG] 此设备的国家是(getCountry)："+c);
        return c != null && c.toLowerCase().equals("cn"); // 大陆是CN、台湾是TW、香港是HK
    }
}
