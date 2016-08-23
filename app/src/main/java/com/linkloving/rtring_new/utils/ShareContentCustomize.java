package com.linkloving.rtring_new.utils;

/**
 * Created by DC 2016/4/1.
 */

import com.linkloving.rtring_new.R;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * 快捷分享项目现在添加为不同的平台添加不同分享内容的方法。
 *本类用于演示如何区别Twitter的分享内容和其他平台分享内容。
 *本类会在{link DemoPage#showShare(boolean, String)}方法
 *中被调用。
 */
public class ShareContentCustomize implements ShareContentCustomizeCallback {

    public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
        // 改写twitter分享内容中的text字段，否则会超长，
        // 因为twitter会将图片地址当作文本的一部分去计算长度
        if ("Twitter".equals(platform.getName())) {
            String text = platform.getContext().getString(R.string.share_content_short);
            paramsToShare.setText(text);
        }
    }

}
