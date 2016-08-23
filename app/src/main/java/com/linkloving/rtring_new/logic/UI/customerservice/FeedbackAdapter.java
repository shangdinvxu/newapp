package com.linkloving.rtring_new.logic.UI.customerservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.logic.UI.customerservice.itemBean.FeedbackMsgEntity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.LinkedList;

/**
 * Created by DC on 2016/3/25.
 */
public class FeedbackAdapter extends RecyclerView.Adapter {
    //需要传进来一个context 一个集合
    Context mContext;
    private LinkedList<FeedbackMsgEntity> feedback_list;
    private UserEntity userEntity;
    //用户提问
    private static final int IMVT_QUESTION_MSG = 0;
    //客服回答
    private static final int IMVT_ANSWER_MSG = 1;
    View view;

    public FeedbackAdapter(Context context, LinkedList<FeedbackMsgEntity> FeedbFeedbackMsgEntityack) {
        this.mContext = context;
        this.feedback_list = FeedbFeedbackMsgEntityack;
        userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据类型返回不同的布局
        if (viewType == IMVT_QUESTION_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item_right, null);
            return new FeedbackHolder(view);
        } else if(viewType == IMVT_ANSWER_MSG){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item_left, null);
            return new FeedbackReplyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FeedbackMsgEntity entity = feedback_list.get(position);
//        boolean isComMsg = entity.getMsgType();
        if (holder instanceof FeedbackHolder) {
//显示用户数据
            UserEntity U = MyApplication.getInstance(mContext).getLocalUserInfoProvider();
            FeedbackHolder headViewHolder = (FeedbackHolder) holder;
            headViewHolder.feedback_Time.setText(entity.getDate());
            headViewHolder.feedback_UserName.setText(entity.getName());
            headViewHolder.feedback_Content.setText(entity.getText());
            String url = NoHttpRuquestFactory.getUserAvatarDownloadURL(mContext, U.getUserBase().getUser_id()+"", U.getUserBase().getUser_avatar_file_name(), true);
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                    .build();//构建完成

            ImageLoader.getInstance().displayImage(url, headViewHolder.feedback_Avatar, options, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    ImageView mhead = (ImageView) view;
                    mhead.setImageResource(R.mipmap.default_avatar);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageView mhead = (ImageView) view;

                    mhead.setImageBitmap(loadedImage);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    ImageView mhead = (ImageView) view;
                    mhead.setImageResource(R.mipmap.default_avatar);

                }
            });
        } else if(holder instanceof FeedbackReplyHolder) {
            UserEntity U = MyApplication.getInstance(mContext).getLocalUserInfoProvider();
            FeedbackReplyHolder headViewreplyHolder = (FeedbackReplyHolder) holder;
            headViewreplyHolder.feedback_UserName.setText(entity.getName());
            headViewreplyHolder.feedback_Content.setText(entity.getText());
            headViewreplyHolder.feedback_Time.setVisibility(View.GONE);
        }

    }

    /**
     * 当前item的类型（问 & 答）
     */
    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        FeedbackMsgEntity msgEntity = feedback_list.get(position);
        if (msgEntity.getMsgType()) {
            return IMVT_QUESTION_MSG;
        } else {
            return IMVT_ANSWER_MSG;
        }
    }

    @Override
    public int getItemCount() {
        return feedback_list.size();
    }

    private class FeedbackHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView feedback_Avatar;
        public TextView feedback_Time;
        public TextView feedback_UserName;
        public TextView feedback_Content;
        public FeedbackHolder(View itemView) {
            super(itemView);
            feedback_Avatar = (ImageView) itemView.findViewById(R.id.iv_userhead_comment);
            feedback_Time = (TextView) itemView.findViewById(R.id.tv_sendtime_comment);
            feedback_UserName = (TextView) itemView.findViewById(R.id.tv_username_comment);
            feedback_Content = (TextView) itemView.findViewById(R.id.tv_chatcontent_comment);

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
            feedback_Avatar = (ImageView) itemView.findViewById(R.id.iv_userhead_left);
            feedback_Time = (TextView) itemView.findViewById(R.id.tv_sendtime_comment);
            feedback_UserName = (TextView) itemView.findViewById(R.id.tv_username_comment);
            feedback_Content = (TextView) itemView.findViewById(R.id.tv_chatcontent_comment);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
