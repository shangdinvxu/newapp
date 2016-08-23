package com.linkloving.rtring_new.logic.UI.main.Portal.model;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

/**
 * Created by zkx on 2016/7/14.
 * 逻辑实现层
 */
public class PortalBinder {
    private static final String TAG = PortalBinder.class.getSimpleName();
    //** 地理位置
    private LocationClient mLocationClient;
    public BDLocationListener mMyLocationListener;

    public void initLocation(final Context context, final ILocationListener listener) {
        final double[] longitude = new double[1];
        final double[] latitude = new double[1];
        mLocationClient = new LocationClient(context.getApplicationContext());
        mMyLocationListener = new BDLocationListener(){
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (context!= null) {
                    longitude[0] = location.getLongitude();
                    latitude[0] = location.getLatitude();
                    String city = location.getCity();
                    UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
                    userEntity.getUserBase().setLongitude(longitude[0] + "");
                    userEntity.getUserBase().setLatitude(latitude[0] + "");
                    //此时经纬度逻辑已经走完
                    listener.locationSucc();
                    MyLog.i("百度定位", "MyLocationListener" + longitude[0] + ">>>>" + latitude[0] + "========city:" + city);
                    mLocationClient.stop();
                } else {
                    mLocationClient.stop();
                }
            }
        };
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02 bd09ll bd09
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true); // 返回地址
        mLocationClient.setLocOption(option);
    }

    public void checkAppVersionUpdata(final Context context,IVersionUpLintener iVersionUpLintener) {

    }

}
