package com.linkloving.rtring_new.logic.UI.personal;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.friend.CommentActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yolanda.nohttp.Response;

public class FriendInfoActivity extends ToolBarActivity implements View.OnClickListener {
    public final static String TAG = FriendInfoActivity.class.getSimpleName();
    TextView name, sex, address, sign;
    Button attention, message;
    ImageView head;
    private UserEntity u;
    private String fromUserID;
    AttentionUserInfoDTO attentUserinfoDTO;
    private boolean mAttention = false;
    public final static int COMEIN_COMMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);

    }

    @Override
    protected void getIntentforActivity() {
        Intent intent = getIntent();
        fromUserID = intent.getExtras().getString("__user_id__", "");
//        fromgUserAvatar = intent.getExtras().getString("__UserAvatar__", "");
        u = MyApplication.getInstance(this).getLocalUserInfoProvider();
        loadData();
    }

    private void loadData() {
        //创建请求获取数据
        if (MyApplication.getInstance(this).isLocalDeviceNetworkOk()) {
//            String user_time = new SimpleDateFormat(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());
            CallServer.getRequestInstance().add(this, getString(R.string.general_submitting), CommParams.HTTP_LOAD_FRIENDINFO, NoHttpRuquestFactory.Load_FriendInfo_Request(u.getUser_id() + "", fromUserID), httpCallback);
        } else {
            MyToast.show(FriendInfoActivity.this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
        }
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            DataFromServer dataFromServer = JSONObject.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();

            switch (what) {
                case CommParams.HTTP_LOAD_FRIENDINFO:
                    MyLog.e(TAG, "returnValue=" + response.get());
                    if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
                        attentUserinfoDTO = JSONObject.parseObject(value, AttentionUserInfoDTO.class);
                        MyLog.e(TAG, "attentUserinfoDTO.getIsAttention()=" + attentUserinfoDTO.getIsAttention());
                        MyLog.e(TAG, "attentUserinfoDTO.getNickname()=" + attentUserinfoDTO.getNickname());
                        update(attentUserinfoDTO);
                    }
                    break;
                case CommParams.HTTP_ATTENTION_FRIEND:
                    //关注成功
                    MyLog.e(TAG, "response.get()=" + response.get());
                    mAttention = true;
                    attention.setText(getString(R.string.relationship_cancel_attention));
                    MyToast.showShort(FriendInfoActivity.this, getString(R.string.relationship_attention_success));
                    break;

                case CommParams.HTTP_NO_ATTENTION_FRIEND:
                    mAttention = false;
                    attention.setText(getString(R.string.relationship_attention));
                    break;
//
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

            switch (what) {
                case CommParams.HTTP_LOAD_FRIENDINFO:
                    //获取信息
                    break;
                case CommParams.HTTP_ATTENTION_FRIEND:
                    //关注
                    MyToast.showShort(FriendInfoActivity.this, getString(R.string.relationship_attention_failed));
                    break;
                case CommParams.HTTP_NO_ATTENTION_FRIEND:
                    //取消关注
                    MyToast.showShort(FriendInfoActivity.this, getString(R.string.relationship_cancel_attention_failed));
                    break;

            }
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    public void update(AttentionUserInfoDTO profile) {
        //图像以后设置
        String url = NoHttpRuquestFactory.getUserAvatarDownloadURL(FriendInfoActivity.this, fromUserID, profile.getUser_avatar_file_name(), true);
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成
        ImageLoader.getInstance().displayImage(url, head, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                if(attentUserinfoDTO!=null){
                    if(attentUserinfoDTO.getUser_sex()==0){
                        head.setImageResource(R.mipmap.default_avatar_f);
                    }else {
                        head.setImageResource(R.mipmap.default_avatar_m);
                    }
                }


            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                head.setImageBitmap(loadedImage);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(attentUserinfoDTO!=null){
                    if(attentUserinfoDTO.getUser_sex()==0){
                        head.setImageResource(R.mipmap.default_avatar_f);
                    }else {
                        head.setImageResource(R.mipmap.default_avatar_m);
                    }
                }
            }
        });
        // profile.getUserAvatar()
        name.setText(profile.getNickname());
        sign.setText(profile.getWhat_s_up());

        if (profile.getUser_sex()==0) {
            sex.setText(getString(R.string.general_female));
        } else {
            sex.setText(getString(R.string.general_male));
        }
//        address.setText(profile.getLongitude()+","+profile.getLatitude());
//        longitude 经度  latitude 纬度
        if (!CommonUtils.isStringEmpty(profile.getLatitude()) && !CommonUtils.isStringEmpty(profile.getLongitude())) {
//            LatLng center =  new LatLng(Double.parseDouble(profile.getLatitude()),Double.parseDouble(profile.getLongitude()));
            LatLng latLng = new LatLng(Float.valueOf(profile.getLatitude()), Float.valueOf(profile.getLongitude()));
            GeoCoder geocoder = GeoCoder.newInstance();
            geocoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            geocoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                    MyLog.e(TAG, "onGetGeoCodeResult" + geoCodeResult.toString());
                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                    MyLog.e(TAG, "反地理编码成功" + result.toString());
                    ReverseGeoCodeResult.AddressComponent component = result.getAddressDetail();
                    if (!component.city.isEmpty()) {
                        address.setText(component.city);
                    }
                    GeoCoder.newInstance().destroy();
                }
            });
//            LatLng latLng = new LatLng(latitude,longitude);
////            option.location(latLng);
//            GeoCoder geocoder = GeoCoder.newInstance();
//            geocoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
//            geocoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//
//                @Override
//                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//                    Log.e("百度定位", "onGetGeoCodeResult" + geoCodeResult.toString());
//                }
//
//                @Override
//                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
//                    Log.e("百度定位", "反地理编码成功" + result.toString());
//                    ReverseGeoCodeResult.AddressComponent component = result.getAddressDetail();
//                    if (!component.city.isEmpty()) {
//                        Toast.makeText(PortalActivity.this,"定位的城市是" + component.city,Toast.LENGTH_LONG).show();
//                        Log.e("百度定位", "定位的城市是" + component.city);
//                    }
//                    GeoCoder.newInstance().destroy();
//                }
//            });
        }


        if (!String.valueOf(u.getUser_id()).equals(fromUserID)) {
            attention.setVisibility(View.VISIBLE);
            if (profile.getIsAttention()) {
                // 关注
                mAttention = true;
                attention.setText(getString(R.string.relationship_cancel_attention));

            } else {
//取消关注
                mAttention = false;
                attention.setText(getString(R.string.relationship_attention));
            }
        } else {
            attention.setVisibility(View.GONE);
        }

      /*  if(profile.isVirtual())
        {
            attention.setVisibility(View.GONE);
            commentsUIWrapper.isVirtual();
        }
        */
    }

    @Override
    protected void initView() {


        SetBarTitleText(getString(R.string.friendinfo_activity_title));
        head = (ImageView) findViewById(R.id.user_head);
        name = (TextView) findViewById(R.id.text_nick_name);
        sex = (TextView) findViewById(R.id.text_sex);
        address = (TextView) findViewById(R.id.text_address);
        sign = (TextView) findViewById(R.id.text_sign);
        attention = (Button) findViewById(R.id.text_attention);
        message = (Button) findViewById(R.id.text_message);

    }

    @Override
    protected void initListeners() {
        attention.setOnClickListener(this);
        message.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_attention:
                if (!ToolKits.isNetworkConnected(FriendInfoActivity.this)) {
                    MyToast.showLong(FriendInfoActivity.this, getString(R.string.general_network_faild));
                    return;
                }
                if (mAttention) {
                    if (MyApplication.getInstance(this).isLocalDeviceNetworkOk()) {

                        //取消关注
                        // loadData(true, HttpSnsHelper.GenerateCancelConcernParams(u.getUser_id(), fromUserID), REQ_CANCEL_ATTENTION);
                        CallServer.getRequestInstance().add(this, false, CommParams.HTTP_NO_ATTENTION_FRIEND, NoHttpRuquestFactory.NO_Attention_Friend_Request(u.getUser_id(), Integer.parseInt(fromUserID), false), httpCallback);
                    } else
                        MyToast.show(this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);

                } else {
                    if (MyApplication.getInstance(this).isLocalDeviceNetworkOk()) {
                        //关注
                        CallServer.getRequestInstance().add(FriendInfoActivity.this, false, CommParams.HTTP_ATTENTION_FRIEND, NoHttpRuquestFactory.NO_Attention_Friend_Request(u.getUser_id(), Integer.parseInt(fromUserID), true), httpCallback);
                        MyLog.e(TAG, "u.getUser_id()====" + u.getUser_id() + "===fromUserID===" + fromUserID);
                    } else
                        MyToast.show(this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
                }
                break;
            case R.id.text_message:
                //跳转到评论  跳转过去需要传的值：好友id、昵称 时间
//                startActivity(IntentFactory.createUserDetialActivityIntent(FriendInfoActivity.this, u.getUser_id(), Integer.parseInt(fromUserID),COMEIN_COMMENT));
                Intent intent = new Intent(FriendInfoActivity.this, CommentActivity.class);
                intent.putExtra("__user_id__", Integer.parseInt(fromUserID));
                intent.putExtra("_user_avatar_file_name_", attentUserinfoDTO.getUser_avatar_file_name()==null?"":attentUserinfoDTO.getUser_avatar_file_name());
                intent.putExtra("_nickname_", attentUserinfoDTO.getNickname());
                intent.putExtra("__check_tag_",COMEIN_COMMENT);
                MyLog.e(TAG,"传值到聊天界面：fromUserID"+fromUserID+",COMEIN_COMMENT="+COMEIN_COMMENT);
                startActivity(intent);
//                intent.putExtra(Integer.parseInt(fromUserID));
                break;

        }

    }
}
