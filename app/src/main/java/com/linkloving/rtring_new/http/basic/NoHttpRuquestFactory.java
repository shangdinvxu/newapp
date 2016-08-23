package com.linkloving.rtring_new.http.basic;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.http.data.ActionConst;
import com.linkloving.rtring_new.http.data.DataFromClientNew;
import com.linkloving.rtring_new.http.data.JobDispatchConst;
import com.linkloving.rtring_new.logic.UI.device.FirmwareDTO;
import com.linkloving.rtring_new.logic.UI.launch.dto.UserRegisterDTO;
import com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem.RankListDTO;
import com.linkloving.rtring_new.logic.dto.ModifyPasswordDTO;
import com.linkloving.rtring_new.logic.dto.UserBase;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Request;

import java.util.HashMap;
import java.util.Map;

//import com.linkloving.rtring_c_watch.logic.dto.ModifyPasswordDTO;

/**
 * Created by leo.wang on 2016/3/17.
 */
public class NoHttpRuquestFactory {
    private static final String TAG = NoHttpRuquestFactory.class.getSimpleName();

    public final static String url= CommParams.SERVER_CONTROLLER_URL_ROOT+"MyControllerJSON";//服务器的根地址

    public final static String url_new= CommParams.SERVER_CONTROLLER_URL_NEW;//服务器的根地址（新）

    /**
     * 生成查看我关注的人接口参数
     * @param user_id
     * @param user_time
     * @return
     */
    public static Request<String> create_My_Attention_Request(String user_id,String user_time ){

     /*   Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_mail", loginName);
        newData.put("user_psw", loginPsw);
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_0);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        request.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());*/
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);
        newData.put("user_time", user_time);
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();

        dataFromClientNew.setActionId(ActionConst.ACTION_18);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.SNS_BASE);

        dataFromClientNew.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }

    /**
     * 生成查看关注我的人接口参数
     */
    public static Request<String> create_Attention_Me_Request(String userId, String userTime) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", userId);
        newData.put("user_time", userTime);
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_19);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }
    /**
     * 注册（手机号码）
     * @param userRegisterDTO
     * @return
     */
    public static Request<String> createMobileRegisterRequest(UserRegisterDTO userRegisterDTO) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_2);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(userRegisterDTO));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }

    /*用邮箱登录的
    * */
    public static Request<String> create_Login_From_Email_Request(String loginName, String loginPsw) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_mail", loginName);
        newData.put("user_psw", loginPsw);
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_0);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }
     /*用手机登录的
    * */
     public static Request<String> create_Login_From_Phone_Request(String loginName, String loginPsw) {

         Map<String, String> newData = new HashMap<String, String>();
         newData.put("user_mobile", loginName);
         newData.put("user_psw", loginPsw);

         MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
         DataFromClientNew dataFromClientNew = new DataFromClientNew();
         dataFromClientNew.setActionId(ActionConst.ACTION_1);
         dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
         dataFromClientNew.setData(JSON.toJSONString(newData));
         httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
         return httpsRequest;

     }

    /**
     * 自身排行榜请求(总榜用)
     */
    public static Request<String> create_Ranking_Count(String userId, String userTime) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", userId);
        newData.put("date_str", userTime);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.REPORT_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_103);
        dataFromClient.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG, "自身排行榜请求参数===" + JSON.toJSONString(dataFromClient));
        return httpsRequest;

    }
    /**
     * 自身排行榜请求（企业排行用）
     */
    public static Request<String> create_Ranking_Count(String userId, String userTime,String ent_id) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", userId);
        newData.put("date_str", userTime);
        newData.put("ent_id", ent_id);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.REPORT_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_103);
        dataFromClient.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG, "自身排行榜请求参数===" + JSON.toJSONString(dataFromClient));
        return httpsRequest;

    }
    /**
     * 排行榜基础信息请求（昵称 签名）
     */
    public static Request<String> create_Ranking_Count_Basic_Info(RankListDTO rankListDTO) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.REPORT_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_102);
        dataFromClient.setData(JSON.toJSONString(rankListDTO));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG,"JSON.toJSONString(dataFromClient)==="+JSON.toJSONString(dataFromClient));
        return httpsRequest;

    }
    //查找好友请求
    public static Request<String> create_My_FindFriend_Request(String mCondition,int pageIndex) {
        JSONObject obj = new JSONObject();
        obj.put("page", pageIndex);
        obj.put("condition", mCondition);
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_25);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClientNew.setData(obj.toJSONString());
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }

    /**
     * 获得下载指定用户头像的完整http地址.
     * <p>
     * 形如：“http://192.168.88.138:8080/UserAvatarDownloadController?
     * action=ad&user_uid=400007&user_local_cached_avatar=400007_91c3e0d81b2039caa9c9899668b249e8.jpg
     * &enforceDawnload=0”。
     *
     * @param context
     * @param userId 要下载头像的用户uid
     * @param userLocalCachedAvatar 用户缓存在本地的头像文件名（本参数只在enforceDawnload==false时有意义）
     * @param enforceDawnload true表示无论客户端有无提交缓存图片名称本方法都将无条件返回该用户头像（如果头像确实存在的话），否则
     * 将据客户端提交上来的可能的本地缓存文件来判断是否需要下载用户头像（用户头像没有被更新过当然就不需要下载了！）
     * @return 完整的http文件下载地址
     */
    public static String getUserAvatarDownloadURL(Context context, String userId, String userLocalCachedAvatar
            , boolean enforceDawnload)
    {
//        String fileURL = CommParams.AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT
//                +"?action=ad"
//                +"&user_uid="+userUid
//                +(userLocalCachedAvatar==null?"":"&user_local_cached_avatar="+userLocalCachedAvatar)
//                +"&enforceDawnload="+(enforceDawnload?"1":"0");
//                 return fileURL;
        //（新）
//        if(!CommonUtils.isStringEmpty(userLocalCachedAvatar)){

            String fileURL = CommParams.AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT_NEW
                +"avatar?fileName="
                +(userLocalCachedAvatar==""?"":userLocalCachedAvatar)
                +"&userId="+userId;
                MyLog.e(TAG, fileURL);
        return fileURL;
//        }
//        return null;

    }

    //获取下载群组头像的url

    public static String getEntAvatarDownloadURL(String entLocalCachedAvatar)
    {
        String fileURL = CommParams.AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT_NEW
                +"group?fileName="
                +(entLocalCachedAvatar==""?"":entLocalCachedAvatar);
        MyLog.e(TAG, fileURL);
        return fileURL;

    }


    public static Request<String> create_Near_People_Request(String userId, double longitude, double latitude, int pageIndex) {

        JSONObject obj = new JSONObject();
        obj.put("user_id", userId);
        obj.put("longitude", longitude);
        obj.put("latitude", latitude);
        obj.put("pageNum", pageIndex);

        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_17);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClientNew.setData(obj.toJSONString());
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }

    /**
     *提交注册信息
     * @param registerData
     * @return
     */
    public static Request<String> submit_RegisterationToServer(UserRegisterDTO registerData) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_2);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(registerData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;

    }

    /**
     * 修改用户信息,身高,体重,昵称
     * @param dataU
     * @return
     */
    public static Request<String> submit_RegisterationToServer_Modify(UserBase dataU) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClient.setActionId(ActionConst.ACTION_3);
        dataFromClient.setData(JSON.toJSONString(dataU));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;

    }

    /*修改密码*/
    public static Request<String> submit_pwd_ToServer_Modify(ModifyPasswordDTO pwd) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClient.setActionId(ActionConst.ACTION_4);
        dataFromClient.setData(JSON.toJSONString(pwd));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;

    }

    /**
     * 进入主界面查询是否有客服反馈
     * @param userId
     * @return
     */
    public static Request<String> queryfeedbackMsg(String userId) {

        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
//        Map<String, String> newData = new HashMap<String, String>();
//        newData.put("user_id", userId);
        JSONObject json = new JSONObject();
        json.put("user_id", userId);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_21);
        dataFromClient.setData(json.toString());
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG, "进入主界面查询是否有客服反馈===" + JSON.toJSONString(dataFromClient));
        return httpsRequest;

    }
    /**
     * 提交反馈信息
     * @param feedback
     * @return
     */
    public static Request<String> submit_Feedback_Info(String feedback) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_20);
        dataFromClient.setData(feedback);
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;

    }

    /**
     * 查询客服是否回复
     * @param userId
     * @return
     */
    public static Request<String> submit_Feedback_Reply(String userId) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
//        Map<String, String> newData = new HashMap<String, String>();
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_22);
        dataFromClient.setData(userId);
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;

    }
///**
// * 提交UTC时间和扫描出的MAC地址是否被绑定的请求到服务端.
// * 目前用于绑定时、同步完运动数据时设置设备时间时。
// *
// * @return
// */
//public static Request<String> submitGetServerUTCAndBoundedToServer(String user_id, String mac) {
//        Request<String> request= NoHttp.createStringRequestPost(url);
//        Map<String, String> newData = new HashMap<String, String>();
//        newData.put("user_id", user_id);
//        newData.put("mac", mac);
//        DataFromClient dataFromClient = new DataFromClient();
//        dataFromClient.setJobDispatchId(1);
//        dataFromClient.setProcessorId(1008);
//        dataFromClient.setActionId(28);
//        dataFromClient.setNewData(JSON.toJSONString(newData));
//        request.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
//        return request;
//    }
    //绑定设备请求
    public static Request<String> submitBoundMACToServer(String user_id, String last_sync_device_id,int type,String modelName) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);
        newData.put("last_sync_device_id", last_sync_device_id);
        newData.put("device_type", type+"");
        newData.put("model_name", modelName);
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_5);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        MyLog.e(TAG, "绑定设备请求参数：" + JSON.toJSONString(dataFromClientNew));
        return httpsRequest;
    }
    //解绑请求
    public static Request<String> UnboundMACToServer(String user_id) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_6);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        MyLog.e(TAG,"解绑请求参数："+JSON.toJSONString(dataFromClientNew));
        return httpsRequest;
    }

    /**
     * 通过modelName获得硬件信息
     * @param userId
     * @param modelName
     * @return
     */
    public static Request<String> createModelRequest(int userId,String modelName) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", userId+"");
        newData.put("model_name",modelName);
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClient.setActionId(ActionConst.ACTION_12);
        dataFromClient.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;
    }

//OAD最新版本信息获取
    public static Request<String> create_OAD_Request(FirmwareDTO obj) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);

        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.FIRMWARE_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_50);
        dataFromClient.setData(JSON.toJSONString(obj));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;
    }
//排行榜点赞请求
    public static  Request<String> submit_Rangking_ding_Request(String user_time,String type,String user_id,String my_praise_user_id) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        JSONObject dataObj = new JSONObject();
        dataObj.put("user_time", user_time);
        dataObj.put("type", type);
        dataObj.put("user_id", user_id);
        dataObj.put("praise_user_id", my_praise_user_id);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_23);
        dataFromClient.setData(dataObj.toJSONString());
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG,"JSON.toJSONString(dataFromClient)"+JSON.toJSONString(dataFromClient));
     return httpsRequest;
    }
    public static  Request<String> submit_Rangking_cancle_zan_Request(String user_time,String type,String user_id,String my_praise_user_id) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        JSONObject dataObj = new JSONObject();
        dataObj.put("user_time", user_time);
        dataObj.put("type", type);
        dataObj.put("user_id", user_id);
        dataObj.put("praise_user_id", my_praise_user_id);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_24);
        dataFromClient.setData(dataObj.toJSONString());
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG, "JSON.toJSONString(dataFromClient)" + JSON.toJSONString(dataFromClient));
        return httpsRequest;
    }
//获取好友信息请求
    public static  Request<String> Load_FriendInfo_Request(String my_id,String user_id) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("attention_user_id", user_id);
        newData.put("user_id", my_id);

        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_16);
        dataFromClient.setData(new Gson().toJson(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG,JSON.toJSONString(dataFromClient));
        return httpsRequest;
    }

    //手机验证找回密码接口
    /*action 10   user_mobile, user_psw
        重置密码的
    * */
    public static  Request<String> Change_Password(String user_mobile,String user_psw) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_mobile", user_mobile);
        newData.put("user_psw", user_psw);
        MyLog.e(TAG, ">>>>>>>>.>>>>>>>>>>>>>>>>>>>changepassword");
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClient.setActionId(ActionConst.ACTION_10);
        dataFromClient.setData(new Gson().toJson(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG,JSON.toJSONString(dataFromClient));
        return httpsRequest;
    }



    /**
     * 关注和取消关注请求
     * @return
     */
    public static Request<String> NO_Attention_Friend_Request(int user_id,int attention_user_id,boolean is_attention)
    {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", user_id);
        jsonObject.put("attention_user_id", attention_user_id);
        jsonObject.put("is_attention", is_attention);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_15);
        dataFromClient.setData(jsonObject.toJSONString());
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;
    }





    /**
     * 生成标记未读数接口参数
     * @param user_id
     * @return
     */
    public static Request<String> Query_Unread_Request(String user_id)
    {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", user_id);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_1);
        dataFromClient.setData(jsonObject.toJSONString());
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;
    }


//    /**
//     * 生成标记未读数接口参数
//     * @param
//     * @return
//     */
//    public static Request<String> queryLocation()
//    {
//        float latitude = 31.31073f;
//        float longitude = 120.679613f;
//        String key = "H76loLHUBXGQhwrjYgjs26zNHB8FLsO4";
//        String url = "http://api.map.baidu.com/geocoder/v2/" +"?ak="+key+"&location="+latitude+","+longitude+"&output=json&pois=1";
//        Request<String> request= NoHttp.createStringRequestGet(url);
//        return request;
//    }

    /*
    * 生成新增消息接口
    * */
    public static Request<String> create_Comment_me(String user_id, String to_user_id,String chat_content) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);
        newData.put("to_user_id", to_user_id);
        newData.put("chat_content", chat_content);

        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_14);
        dataFromClient.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG,"user_id"+user_id+"to_user_id"+to_user_id+"chat_content"+chat_content);
        return httpsRequest;

    }
    //设置未读为已读
    public static Request<String> change_unread(String[] chat_ids) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
//        Map<String, String> newData = new HashMap<String, String>();
//        newData.put("chat_ids", chat_ids);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chat_ids",chat_ids);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_13);
        dataFromClient.setData(jsonObject.toJSONString());
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;

    }

    /* 消息详情 */
    public static Request<String> message_details(String user_id, String to_user_id) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);
        newData.put("to_user_id", to_user_id);

        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_12);
        dataFromClient.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        MyLog.e(TAG, "user_id"+user_id+"to_user_id"+to_user_id);
        return httpsRequest;

    }
    /* 未读消息列表(按user分组) */
    public static Request<String> unread_messages_list(String user_id) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_11);
        dataFromClient.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;
    }


//    //上传头像的请求
//
//    public static Request<String> uploadAvatarFile_request(String localUserUid,File file)
//    {
//        String url="http://192.168.88.107:8080/linkloving_server_2.0/upload/avatar";
//        Request<String> request= NoHttp.createStringRequestPost(url);
//        request.add("user_id", localUserUid);
//        request.add("file", new FileBinary(file));
//        return request;
//    }
//查找群组请求
public static Request<String> create_My_FindQun_Request(String condition,int page) {
    MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
    Map<String, String> newData = new HashMap<String, String>();
    newData.put("ent_name", condition);
    newData.put("page", page + "");

    DataFromClientNew dataFromClient = new DataFromClientNew();
    dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
    dataFromClient.setActionId(ActionConst.ACTION_3);
    dataFromClient.setData(JSON.toJSONString(newData));
    httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
    return httpsRequest;
}
    //加入群组请求
    public static Request<String> create_FindQun_JoinGroup_Request(String user_id, String ent_id) {

        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);
        newData.put("ent_id", ent_id);

        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_4);
        dataFromClient.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;
    }
    //云同步
    public static Request<String> creatCloud(DataFromClientNew dataFromClient) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;
    }
    //退出群组请求
    public static Request<String> create_FindQun_QuitGroup_Request(String user_id) {

        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);

        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.SNS_BASE);
        dataFromClient.setActionId(ActionConst.ACTION_5);
        dataFromClient.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;
    }


    public static Request<String> Check_Is_Register(String name) {
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_account", name);
        DataFromClientNew dataFromClient = new DataFromClientNew();
        dataFromClient.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClient.setActionId(ActionConst.ACTION_9);
        dataFromClient.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
        return httpsRequest;
    }

    /*第三方登录*/
    public static Request<String> create_Login_Other_Request(UserBase userBase) {

//        Map<String, String> newData = new HashMap<String, String>();
//        newData.put("user_mail", weixin_openid);
        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_11);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(userBase));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;

    }

}
