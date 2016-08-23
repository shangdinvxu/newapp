package com.linkloving.rtring_new.logic.UI.friend.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.friend.chatbean.CommentChat;
import com.linkloving.rtring_new.logic.UI.personal.AttentionUserInfoDTO;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yolanda.nohttp.Response;

import java.util.LinkedList;

/**
 * Created by Linkloving on 2016/3/31.
 */
public class CommentAdapter extends RecyclerView.Adapter {
    public static final String TAG = CommentAdapter.class.getSimpleName();
    private LinkedList<CommentChat> comment_list;
    //用户提问
    private static final int IMVT_QUESTION_MSG = 0;
    //客服回答
    private static final int IMVT_ANSWER_MSG = 1;
    //需要传进来一个context 一个集合
    Context mContext;
    UserEntity userEntity;
    View view;
    AttentionUserInfoDTO attentUserinfoDTO;
    String url_avatr;
    public CommentAdapter(Context context, LinkedList<CommentChat> CommentChatMsgEntityack) {
        this.mContext = context;
        this.comment_list = CommentChatMsgEntityack;
        userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据类型返回不同的布局
        if (viewType == IMVT_QUESTION_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_right, null);
            return new FeedbackHolder(view);
        } else if (viewType == IMVT_ANSWER_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_left, null);
            return new FeedbackReplyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CommentChat entity = comment_list.get(position);
        if (holder instanceof FeedbackHolder &&entity.getTag().equals("1")) {
//显示用户数据
            UserEntity U = MyApplication.getInstance(mContext).getLocalUserInfoProvider();
            FeedbackHolder headViewHolder = (FeedbackHolder) holder;
            headViewHolder.feedback_Time.setText(entity.getComment_create_time());
            headViewHolder.feedback_UserName.setText(U.getUserBase().getNickname());
            headViewHolder.feedback_Content.setText(entity.getComments());
            String url = NoHttpRuquestFactory.getUserAvatarDownloadURL(mContext, U.getUser_id() + "", U.getUserBase().getUser_avatar_file_name(), true);
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .showImageOnFail(R.mipmap.default_avatar)//加载失败显示图片
                    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                    .build();//构建完成

            ImageLoader.getInstance().displayImage(url, headViewHolder.feedback_Avatar, options, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    MyLog.e(TAG,"=====onLoadingComplete======");
                    ImageView mhead = (ImageView) view;

                    mhead.setImageBitmap(loadedImage);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else if (holder instanceof FeedbackReplyHolder&&entity.getTag().equals("0")) {
            final FeedbackReplyHolder headViewreplyHolder = (FeedbackReplyHolder) holder;
            UserEntity U = MyApplication.getInstance(mContext).getLocalUserInfoProvider();

            if(MyApplication.getInstance(mContext).isLocalDeviceNetworkOk()) {
                CallServer.getRequestInstance().add(mContext, false, CommParams.HTTP_LOAD_FRIENDINFO, NoHttpRuquestFactory.Load_FriendInfo_Request(U.getUser_id() + "", entity.getTo_user_id() + ""), new HttpCallback<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) {
                        DataFromServer dataFromServer = JSONObject.parseObject(response.get(), DataFromServer.class);
                        String value = dataFromServer.getReturnValue().toString();
                        MyLog.e(TAG, "returnValue=" + response.get());
                        if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
                            attentUserinfoDTO = JSONObject.parseObject(value, AttentionUserInfoDTO.class);
                            MyLog.e(TAG, "attentUserinfoDTO.getIsAttention()=" + attentUserinfoDTO.getIsAttention());
                            MyLog.e(TAG, "attentUserinfoDTO.getNickname()=" + attentUserinfoDTO.getNickname());
                            headViewreplyHolder.feedback_UserName.setText(attentUserinfoDTO.getNickname());
                            MyLog.e(TAG, "attentUserinfoDTO.getUser_avatar_file_name()=" + attentUserinfoDTO.getUser_avatar_file_name());
                            url_avatr= NoHttpRuquestFactory.getUserAvatarDownloadURL(mContext, entity.getTo_user_id()+ "", attentUserinfoDTO.getUser_avatar_file_name(), true);

                        }
                    }

                    @Override
                    public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
                        MyLog.e(TAG, "响应失败：getUser_avatar_file_name()");
                    }
                });

            }else {
                Toast.makeText(mContext, mContext.getString(R.string.smssdk_network_error), Toast.LENGTH_LONG).show();
                headViewreplyHolder.feedback_UserName.setText("");
//                url_avatr[0] = NoHttpRuquestFactory.getUserAvatarDownloadURL(mContext, entity.getTo_user_id()+"", "", true);
                 url_avatr= NoHttpRuquestFactory.getUserAvatarDownloadURL(mContext, entity.getTo_user_id()+ "", "", true);
            }
            headViewreplyHolder.feedback_Content.setText(entity.getComments());
            headViewreplyHolder.feedback_Time.setVisibility(View.GONE);
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .showImageOnFail(R.mipmap.default_avatar)//加载失败显示图片
                    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                    .build();//构建完成

            ImageLoader.getInstance().displayImage(url_avatr, headViewreplyHolder.feedback_Avatar, options, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    MyLog.e(TAG,"=====onLoadingComplete=====other=");
                    ImageView mhead = (ImageView) view;

                    mhead.setImageBitmap(loadedImage);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }

    }

    /**
     * 当前item的类型（问 & 答）
     */
    @Override
    public int getItemViewType(int position) {
        CommentChat msgEntity = comment_list.get(position);
        if (msgEntity.getTag().equals("1")) {
            MyLog.e(TAG,"=====IMVT_QUESTION_MSG==右边==");
            return IMVT_QUESTION_MSG;
        } else {
            MyLog.e(TAG,"=====IMVT_ANSWER_MSG======左边=");
            return IMVT_ANSWER_MSG;
        }
    }

    @Override
    public int getItemCount() {
        return comment_list.size();
    }

    private class FeedbackHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView feedback_Avatar;
        public TextView feedback_Time;
        public TextView feedback_UserName;
        public TextView feedback_Content;

        public FeedbackHolder(View itemView) {
            super(itemView);
            feedback_Avatar = (ImageView) itemView.findViewById(R.id.iv_userhead_comment_list);
            feedback_Time = (TextView) itemView.findViewById(R.id.tv_sendtime_comment_list);
            feedback_UserName = (TextView) itemView.findViewById(R.id.tv_username_comment_list);
            feedback_Content = (TextView) itemView.findViewById(R.id.tv_chatcontent_comment_list);

        }

        @Override
        public void onClick(View v) {

        }
    }

    private class FeedbackReplyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView feedback_Avatar;
        public TextView feedback_Time;
        public TextView feedback_UserName;
        public TextView feedback_Content;

        public FeedbackReplyHolder(View itemView) {
            super(itemView);
            feedback_Avatar = (ImageView) itemView.findViewById(R.id.iv_userhead_left_list);
            feedback_Time = (TextView) itemView.findViewById(R.id.tv_sendtime_comment_list);
            feedback_UserName = (TextView) itemView.findViewById(R.id.tv_username_comment_list);
            feedback_Content = (TextView) itemView.findViewById(R.id.tv_chatcontent_comment_list);
        }

        @Override
        public void onClick(View v) {

        }

    }
}
