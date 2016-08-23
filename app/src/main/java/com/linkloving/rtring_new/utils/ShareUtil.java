package com.linkloving.rtring_new.utils;

import android.app.Activity;

import com.linkloving.rtring_new.R;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by Linkloving on 2016/4/6.
 */
public class ShareUtil {

    public static void showShare(String imgPath,Activity context) {
        OnekeyShare oks = new OnekeyShare();

        if(!LanguageHelper.isChinese_SimplifiedChinese()){
            oks.addHiddenPlatform(SinaWeibo.NAME);
            oks.addHiddenPlatform(Wechat.NAME);
            oks.addHiddenPlatform(WechatMoments.NAME);
            oks.addHiddenPlatform(QZone.NAME);
            oks.addHiddenPlatform(GooglePlus.NAME);
            oks.addHiddenPlatform(QQ.NAME);

        }else{
            oks.addHiddenPlatform(GooglePlus.NAME);
            oks.addHiddenPlatform(QQ.NAME);
            oks.addHiddenPlatform(Facebook.NAME);
            oks.addHiddenPlatform(Twitter.NAME);
        }
        oks.setTitle(context.getString(R.string.evenote_title));
        oks.setTitleUrl("http://linkloving.com");
        oks.setText(context.getString(R.string.share_content));
        oks.setFilePath(imgPath);
        oks.setImagePath(imgPath);
        oks.setComment(context.getString(R.string.ssdk_oks_share));
        /**
         * 仅QQ空间使用的字段
         */
        oks.setSite(context.getString(R.string.app_name));
        oks.setSiteUrl("http://linkloving.com");

        /**
         * 微博中使用的地理位置信息
         * !!!!
         * 暂时为性能考虑不添加位置信息!!!!!!!!!!!!!!!!!!
         * 暂时为性能考虑不添加位置信息!!!!!!!!!!!!!!!!!!
         * 暂时为性能考虑不添加位置信息!!!!!!!!!!!!!!!!!!
         * 暂时为性能考虑不添加位置信息!!!!!!!!!!!!!!!!!!
         */
//		oks.setLatitude(23.056081f);
//		oks.setLongitude(113.385708f);

        oks.setSilent(false);

        // 令编辑页面显示为Dialog模式
//        oks.setDialogMode();

        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();

        // 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
        // oks.setCallback(new OneKeyShareCallback());
//        oks.setShareContentCustomizeCallback(new ShareContentCustomize());

        oks.show(context);
    }
}
