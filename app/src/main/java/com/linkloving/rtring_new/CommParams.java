package com.linkloving.rtring_new;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by zkx on 2016/3/2.
 */
public class CommParams {
    //app网络请求队列
    public final static int HTTP_RANK = 0;
    public final static int HTTP_REGISTERED_EMAIL = 1;
    public final static int HTTP_FEEDBACK=2;
    public final static int HTTP_FEEDBACK_REPLY=3;
    public final static int HTTP_FEEDBACK_QUERY = 4;
    public final static int HTTP_SEND_MSG=5;
    public final static int HTTP_CHECK_MSG=8;
    public final static int HTTP_LOGIN_EMAIL = 6;
    public final static int HTTP_LOGIN_PHONE = 7;
    public final static int HTTP_CHANGE_MSG_READ=9;
    public final static int HTTP_BOUND=25;
    public final static int HTTP_REGISTER_MOBILE = 60;
    public final static int HTTP_UPDATA_CARDNUMBER = 20;
    public final static int HTTP_UPDATA_DEVICEID = 21;
    public final static int HTTP_UPDATA_MODELNAME = 22;
    public final static int REQUEST_GET_GROUP = 33;
    public final static int HTTP_SYNC_DATA = 30;
    public final static int HTTP_SUBMIT_DATA = 31;
    public final static int HTTP_SUBMIT_BODYDATA = 80;
    public final static int HTTP_UNBUND = 40;
    public final static int HTTP_DOWN_USERENETTY = 99;
    public final static int HTTP_LOAD_FRIENDINFO = 50;
    public final static int HTTP_ATTENTION_FRIEND = 51;
    public final static int HTTP_NO_ATTENTION_FRIEND= 52;
    public final static int HTTP_UNREAD_QUERY = 53;
    public final static int HTTP_USER_NAME = 54;
    public final static int HTTP_USER_HEAD = 55;
    public final static int HTTP_USER_PWD = 56;
    public final static int HTTP_USER_SEX = 57;
    public final static int HTTP_USER_SIGN = 58;
    public final static int HTTP_USER_HEIGHT = 59;
    public final static int HTTP_USER_BIRTHDAY = 100;
    public final static int HTTP_CHECK_IS_REGISTER = 90;
    public final static int HTTP_CLOUD_DATA = 101;
    public final static int HTTP_SUBMIT_BAIDU = 102;


    public final static int HTTP_OAD= 10;
    //主界面绑定类型
    public final static int REQUEST_CODE_BOUND= 110;
    //主界面绑定手环CODE ID
    public final static int REQUEST_CODE_BOUND_BAND= 111;
    //主界面绑定手表CODE ID
    public final static int REQUEST_CODE_BOUND_WATCH= 112;
    //主界面绑定手环3.0 CODE ID
    public final static int REQUEST_CODE_BOUND_BAND_3= 113;

    //修改昵称
    public final static int REQUEST_CODE_NICKNAME= 213;
    //修改身高
    public final static int REQUEST_CODE_HEIGHT= 214;
    //修改说说
    public final static int REQUEST_CODE_SHUOSHUO= 215;


    public final static int HTTP_LOGIN_QQ=11;
    public final static int HTTP_LOGIN_WECHAT=12;
    public final static int HTTP_LOGIN_FACEBOOK=13;
    public final static int HTTP_LOGIN_TWITTER=14;
    public final static int HTTP_LOGIN_GOOGLE=15;


    /** 有赞商城链接 */
    public final static String URL_HOMEPAGE = "https://wap.koudaitong.com/v2/showcase/homepage?alias=1i2gue9jp";
    /** APP的服务器根地址 */
    public final static String APP_ROOT_URL ="app.linkloving.com/linkloving_server-watch/";
    /** APP的服务器根地址  台式机(新)*/
//    public final static String APP_ROOT_URL_NEW ="http://192.168.88.168:8080/linkloving_server_2.0/";
    /** APP的服务器根地址 (新)*/
//    public final static String APP_ROOT_URL_NEW ="http://192.168.88.126:8080/linkloving_server_2.0/";
    /** APP的服务器根地址 (新)*/
//    public final static String APP_ROOT_URL_NEW ="http://192.168.88.123:8080/linkloving_server_2.0/";
    //传麟的电脑http://192.168.88.115:8080/linkloving_server_2.0
//    public final static String APP_ROOT_URL_NEW ="192.168.88.115:8080/linkloving_server_2.0/";
    /** APP的服务器根地址)*/
    public final static String APP_ROOT_URL_NEW ="http://linkloving.com:6080/linkloving_server_2.0/";
    //public final static String APP_ROOT_URL_NEW ="http://linkloving.com/linkloving_server_2.0/";
    /** APP的服务器根地址 */
    public final static String SERVER_CONTROLLER_URL_ROOT = "http://"+APP_ROOT_URL;
    /** APP的服务器 */
    public final static String SERVER_CONTROLLER_URL = "http://"+APP_ROOT_URL+"MyControllerJSON";
    /** APP的服务器 （新）*/
    public final static String SERVER_CONTROLLER_URL_NEW = APP_ROOT_URL_NEW+"business/service";
    /**用户上传头像的*/
    public final static String AVATAR_UPLOAD_CONTROLLER_URL_ROOT ="http://"+APP_ROOT_URL+"UserAvatarUploadController";
    /** 用户头像下载Servlet地址*/
    public final static String AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT ="http://"+APP_ROOT_URL+"UserAvatarDownloadController";
    /** 用户头像、OAD下载Servlet地址(新)*/
    public final static String AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT_NEW=APP_ROOT_URL_NEW+"download/";
    /** 企业用户下载Servlet地址*/
    public final static String ENT_DOWNLOAD_CONTROLLER_URL_ROOT ="http://"+APP_ROOT_URL+"BinaryDownloadController";
    /** 服务条款地址*/
    public final static String REGISTER_AGREEMENT_EN_URL = " ";
    /** 服务条款*/
    public final static String REGISTER_AGREEMENT_CN_URL = "http://" + APP_ROOT_URL + "clause/agreement_cn.html";
    /**FAQ*/
    public final static String FAQ_EN_URL = "http://" + APP_ROOT_URL + "clause/qna.html";
    /**FAQ*/
    public final static String FAQ_CN_URL = "http://" + APP_ROOT_URL + "clause/qna_cn.html";
    /**隐私申明*/
    public final static String PRIVACY_EN_URL = "http://" + APP_ROOT_URL + "clause/privacy.html";
    public final static String PRIVACY_CN_URL = "http://" + APP_ROOT_URL + "clause/privacy_cn.html";
    //http://app.linkloving.com/linkloving_server-watch/clause/privacy_cn.html


    /**消息提醒 -- 电话提醒*/
    public final static String REMINDER_SETTING_ANDROID_CALL_URL = "http://" + APP_ROOT_URL + "clause_reminder/android/calls.html";

    /**消息提醒 -- 短信提醒*/
    public final static String REMINDER_SETTING_ANDROID_SMS_URL = "http://" + APP_ROOT_URL + "clause_reminder/android/sms.html";

    /**消息提醒 -- QQ提醒*/
    public final static String REMINDER_SETTING_ANDROID_QQ_URL = "http://" + APP_ROOT_URL + "clause_reminder/android/qq.html";

    /**消息提醒 -- 微信提醒*/
    public final static String REMINDER_SETTING_ANDROID_WCHAT_URL = "http://" + APP_ROOT_URL + "clause_reminder/android/wchat.html";

    /**消息提醒 -- 闹钟提醒 */
    public final static String REMINDER_SETTING_ANDROID_ALARM_URL = "http://" + APP_ROOT_URL + "clause_reminder/android/alarm.html";

    /**连爱官网 */
    public final static String LINKLOVING_OFFICAL_WEBSITE = "http://www.linkloving.com";

    /** 联系邮件 */
    public final static String LINKLOVING_OFFICAL_MAIL = "support@linkloving.com";

    /** QQ开放平台APP_ID*/
    public static final String QQ_OPEN_APP_ID = "1104705744";  //正式APPID：1104705744 222222

    /** 微信开放平台APP_ID*/
    public static final String WX_OPEN_APP_ID = "wxa0210eb16406ac8e";  // wxa0210eb16406ac8e
    /** 微信公众平台APP_ID*/
    public static final String WX_OPEN_APP_ID_SPORT = "wxbe75dce56bb3f9ff";


    /** 微信开放平台AppSecret*/
    public static final String WX_AppSecret = "dc6183b11dca2be1bd912df3d343b476";
    /** 微信公众平台AppSecret*/
    public static final String WX_AppSecret_SPORT = "6b7975bb9a2f2c76d2636601a0eafe7b";

    public static  ImageLoaderConfiguration getImgConfig(Context context, MyApplication.Const const_){
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, const_.DIR_KCHAT_AVATART_RELATIVE_DIR);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).
                diskCache(new UnlimitedDiskCache(cacheDir))
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .build();
        return  config;
    }

}
