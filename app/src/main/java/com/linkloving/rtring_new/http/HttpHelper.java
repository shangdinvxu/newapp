package com.linkloving.rtring_new.http;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.http.basic.MyJsonRequest;
import com.linkloving.rtring_new.http.data.ActionConst;
import com.linkloving.rtring_new.http.data.DataFromClientNew;
import com.linkloving.rtring_new.http.data.JobDispatchConst;
import com.linkloving.rtring_new.logic.dto.SportRecordUploadDTO;
import com.linkloving.rtring_new.utils.LanguageHelper;
import com.yolanda.nohttp.Request;

import java.util.HashMap;
import java.util.Map;

 /**
 * Created by Administrator on 2016/3/22.
 */
public class HttpHelper {
     public final static String url_new= CommParams.SERVER_CONTROLLER_URL_NEW;//服务器的根地址（新）
    /**
     * 提交卡号到服务端接口参数
     */
    public static Request<String> createUpCardNumberRequest(String userId, String cardnumber) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", userId);
        newData.put("card_number", cardnumber);

        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_7);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }

    /**
     * 提交deviceId到服务端接口参数
     */
    public static Request<String> createUpDeviceIdRequest(String user_id, String last_sync_device_id,String last_sync_device_id2) {

        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);
        newData.put("last_sync_device_id", last_sync_device_id);
        newData.put("last_sync_device_id2", last_sync_device_id2);

        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_8);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }

     /**
      *  提交运动数据到服务器
      */
     public static Request<String> sportDataSubmitServer(SportRecordUploadDTO sportRecordUploadDTO) {
         MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
         DataFromClientNew dataFromClient = new DataFromClientNew();
         dataFromClient.setActionId(ActionConst.ACTION_101);
         dataFromClient.setJobDispatchId(JobDispatchConst.REPORT_BASE);
         dataFromClient.setData(JSON.toJSONString(sportRecordUploadDTO));
         httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
         return httpsRequest;
     }

     /**
      *  提交运动数据到服务器
      */
     public static String sport2Server(SportRecordUploadDTO sportRecordUploadDTO) {
         DataFromClientNew dataFromClient = new DataFromClientNew();
         dataFromClient.setActionId(ActionConst.ACTION_101);
         dataFromClient.setJobDispatchId(JobDispatchConst.REPORT_BASE);
         dataFromClient.setData(JSON.toJSONString(sportRecordUploadDTO));
         return JSON.toJSONString(dataFromClient);
     }

     /**
      *  提交运动数据到服务器 JSON
      */
     public static String sportDataSubmitServerJSON(SportRecordUploadDTO sportRecordUploadDTO) {
         DataFromClientNew dataFromClient = new DataFromClientNew();
         dataFromClient.setActionId(ActionConst.ACTION_101);
         dataFromClient.setJobDispatchId(JobDispatchConst.REPORT_BASE);
         dataFromClient.setData(JSON.toJSONString(sportRecordUploadDTO));
         return JSON.toJSONString(dataFromClient);
     }

    /**
     * 生成云同步
     */
    public static DataFromClientNew GenerateCloudSyncParams(String userId,int pageIndex) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", userId);
        newData.put("pageNum", pageIndex+"");
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_100);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.REPORT_BASE);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        return dataFromClientNew;
    }

     /**
      * 生成常见问题等请求
      */
     public static String creatTermUrl(Context context,int type) {
         String termUrl="";
         if(MyApplication.getInstance(context).getLocalUserInfoProvider().getDeviceEntity().getDevice_type() > 0 ){
             termUrl = "https://linkloving.com/linkloving_server_2.0/web/service?" +
                     "page="+type+
                     "&device="+MyApplication.getInstance(context).getLocalUserInfoProvider().getDeviceEntity().getDevice_type()+"&app=1&chan=1&" +
                     "isEn="+!LanguageHelper.isChinese_SimplifiedChinese();

         }else{
             termUrl = "https://linkloving.com/linkloving_server_2.0/web/service?" +
                     "page="+type+
                     "&device="+1+"&app=1&chan=1&" +
                     "isEn="+!LanguageHelper.isChinese_SimplifiedChinese();
         }
         return termUrl;
     }
}
