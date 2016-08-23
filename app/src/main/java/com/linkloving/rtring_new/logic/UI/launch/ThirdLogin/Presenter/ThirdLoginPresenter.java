package com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.Presenter;

import android.content.Context;
import android.os.Handler;

import com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.model.ThirdLoginLintener;
import com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.model.ThirdUserBinder;
import com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.view.IThirdLoginView;

/**
 * Created by zkx on 2016/7/14.
 */
public class ThirdLoginPresenter {
    ThirdUserBinder userBinder;
    IThirdLoginView userloginView;
    private Handler handler = new Handler();

    public ThirdLoginPresenter(IThirdLoginView userloginView) {
        this.userBinder = new ThirdUserBinder();
        this.userloginView = userloginView;
    }

    public void LoginQq(final Context context){
        userloginView.showLoading();
        userBinder.loginQQ(context, new ThirdLoginLintener() {
            @Override
            public void loginSuccess(final boolean toRegister) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userloginView.dismissLoading();
                        if(toRegister){
                            userloginView.toAvatarActivity();
                        }else{
                            userloginView.toPortaActivity();
                        }
                    }
                });

            }

            @Override
            public void loginFail() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userloginView.dismissLoading();
                        userloginView.showFailMessage();
                    }
                });
            }
        });
    }

    public void LoginWeChat(final Context context){
        userloginView.showLoading();

        userBinder.loginWechat(context, new ThirdLoginLintener() {
            @Override
            public void loginSuccess(final boolean toRegister) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userloginView.dismissLoading();
                        if(toRegister){
                            userloginView.toAvatarActivity();
                        }else{
                            userloginView.toPortaActivity();
                        }
                    }
                });

            }

            @Override
            public void loginFail() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userloginView.dismissLoading();
                        userloginView.showFailMessage();
                    }
                });

            }
        });
    }

    public void LoginFace(final Context context){
        userloginView.showLoading();

        userBinder.loginFacebook(context, new ThirdLoginLintener() {
            @Override
            public void loginSuccess(final boolean toRegister) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userloginView.dismissLoading();
                        if(toRegister){
                            userloginView.toAvatarActivity();
                        }else{
                            userloginView.toPortaActivity();
                        }
                    }
                });


            }

            @Override
            public void loginFail() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userloginView.dismissLoading();
                        userloginView.showFailMessage();
                    }
                });

            }
        });
    }

    public void LoginTwitter(final Context context){
        userloginView.showLoading();

        userBinder.loginTwitter(context, new ThirdLoginLintener() {
            @Override
            public void loginSuccess(final boolean toRegister) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userloginView.dismissLoading();
                        if(toRegister){
                            userloginView.toAvatarActivity();
                        }else{
                            userloginView.toPortaActivity();
                        }
                    }
                });

            }

            @Override
            public void loginFail() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userloginView.dismissLoading();
                        userloginView.showFailMessage();
                    }
                });

            }
        });
    }

}
