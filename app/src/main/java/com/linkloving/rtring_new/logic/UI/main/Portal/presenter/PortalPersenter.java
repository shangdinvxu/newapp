package com.linkloving.rtring_new.logic.UI.main.Portal.presenter;

import android.content.Context;

import com.linkloving.rtring_new.logic.UI.main.Portal.model.ILocationListener;
import com.linkloving.rtring_new.logic.UI.main.Portal.model.PortalBinder;
import com.linkloving.rtring_new.logic.UI.main.Portal.view.IPortalView;

/**
 * Created by zkx on 2016/7/14.
 */
public class PortalPersenter {
    PortalBinder portalBinder;
    IPortalView portalView;

    public PortalPersenter(IPortalView portalView) {
        portalBinder = new PortalBinder();
        this.portalView = portalView;
    }

    public void  initLocation(Context context){
        portalBinder.initLocation(context, new ILocationListener() {
            @Override
            public void locationSucc() {
                portalView.initLocationSuccess();
            }
        });
    }

    public void  checkUpdata(Context context){
        portalBinder.initLocation(context, new ILocationListener() {
            @Override
            public void locationSucc() {

            }
        });
    }
}
