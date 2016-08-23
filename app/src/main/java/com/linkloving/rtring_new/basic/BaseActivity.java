package com.linkloving.rtring_new.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by zkx on 2016/2/24.
 *   //在无titlebar的页面调用
 */
public class BaseActivity extends Activity {
    private boolean allowDestroy = true;
    private View view;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
    }


    protected void onDestroy()
    {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    public void setAllowDestroy(boolean allowDestroy)
    {
        this.allowDestroy = allowDestroy;
    }

    public void setAllowDestroy(boolean allowDestroy, View view) {
        this.allowDestroy = allowDestroy;
        this.view = view;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //back
        if ((keyCode == 4) && (this.view != null)) {
            this.view.onKeyDown(keyCode, event);
            if (!this.allowDestroy) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
