package com.linkloving.rtring_new.logic.UI.main.Portal.view;

import android.os.Bundle;
import android.util.Log;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.logic.UI.main.Portal.presenter.PortalPersenter;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.ButterKnife;

/**
 * Created by zkx on 2016/7/14.
 */
public class Portal2Activity extends AutoLayoutActivity implements IPortalView{

    private PortalPersenter persenter = new PortalPersenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_main);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        persenter.initLocation(this);
    }

    @Override
    public void initLocationSuccess() {
        Log.e("Portal2Activity","经纬度获取成功了");
    }
}
