package com.linkloving.rtring_new.logic.UI.main.boundwatch;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.utils._Utils;

/**
 * Created by Linkloving on 2016/3/28.
 */
public class HTTP_BOUND {
    public static final String TAG=HTTP_BOUND.class.getSimpleName();
    public static Object[] pareseServerUTCAndBounded(Object retValue)
    {
        long _beforeSubmitTm = System.currentTimeMillis();
//		DataFromServer dfs = HttpHelper.submitGetServerUTCAndBoundedToServer(user_id, mac);
//		if(dfs.isSuccess())
        {
            if(retValue != null)
            {
                try
                {
                    JSONObject nwObj = JSONObject.parseObject((String) retValue);
                    String beBoundMail = nwObj.getString("be_bound_mail");

                    String serverUtcTm = nwObj.getString("server_utc_tm");
                    // 返回的服务端时间戳
                    long _utcFronServer = _Utils.getUTC0FromUTCTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, serverUtcTm);
                    long localDeviceTm = System.currentTimeMillis(); // 当前时间戳
                    long delta = localDeviceTm - _beforeSubmitTm;    // 本次查询耗时
                    // 服务端时间戳+此次好时，就认为是此刻服务端的
                    long utcFronServer = _utcFronServer + delta;
                    System.out.println("[CC]TESTTEST, 【成功】当前服务端tm:"+_utcFronServer
                            +",查询耗时:"+delta+"毫秒,本地tm:"+localDeviceTm+",本地比服务端快："
                            +(localDeviceTm - utcFronServer)+"毫秒！boundMac="+beBoundMail);
                    return new Object[]{utcFronServer, beBoundMail};
                }
                catch (Exception e)
                {
                    Log.w(TAG, e.getMessage(), e);
                }
            }
            return new Object[]{0, null};
        }
//		else
//		{
//			System.out.println("[CC]TESTTEST, 【失败】从服务端获取时间失败，原因是："+dfs.getReturnValue());
//		}
//		return new Object[]{0, null};
    }
}
