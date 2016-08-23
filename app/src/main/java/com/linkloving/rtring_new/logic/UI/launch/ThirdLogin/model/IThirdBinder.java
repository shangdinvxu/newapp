package com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.model;

import android.content.Context;

/**
 * Created by Administrator on 2016/7/14.
 */
public interface IThirdBinder {
    void loginQQ(Context context, ThirdLoginLintener loginLintener);
    void loginWechat(Context context, ThirdLoginLintener loginLintener);
    void loginFacebook(Context context, ThirdLoginLintener loginLintener);
    void loginTwitter(Context context, ThirdLoginLintener loginLintener);
}
