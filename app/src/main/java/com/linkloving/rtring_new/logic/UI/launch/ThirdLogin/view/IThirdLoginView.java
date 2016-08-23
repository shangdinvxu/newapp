package com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.view;

/**
 * Created by zkx on 2016/7/14.
 */
public interface IThirdLoginView {

    //显示dialog
    void showLoading();

    //隐藏Loading
    void dismissLoading();

    //跳转到设置头像界面
    void toAvatarActivity();

    //跳转到主界面
    void toPortaActivity();

    //登录失败提示
    void showFailMessage();
}
