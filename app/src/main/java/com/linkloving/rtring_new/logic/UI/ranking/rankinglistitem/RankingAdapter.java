package com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.personal.FriendInfoActivity;
import com.linkloving.rtring_new.logic.UI.personal.PersonalInfoActivity;
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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by DC.king on 2016/3/16.
 */
public class RankingAdapter extends RecyclerView.Adapter {
    public final static String TAG = RankingAdapter.class.getSimpleName();
    private Context mContext;
    List<RankingVO> rankingVOs;
    List<MyRankVO> myRankVOs = new ArrayList<MyRankVO>();
    private List<RankingVO> list = new ArrayList<RankingVO>();
    View view;
    private View mHeaderView;
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    private static final int TYPE_HEAD = 2;//头部Item
    private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
    boolean zanTag = true;
    DisplayImageOptions options;
    ImageLoader imageLoader;
    List<Integer> integerlist=new ArrayList<>();
    public RankingAdapter(Context context, List<RankingVO> rankVo, List<MyRankVO> myRankVOs) {
        this.mContext = context;
        this.rankingVOs = rankVo;
        this.myRankVOs = myRankVOs;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
//                .showImageOnFail(R.mipmap.default_avatar)//加载失败显示图片
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .build();//构建完成
        imageLoader = ImageLoader.getInstance();
    }
    /**
     * 在任何ViewHolder被实例化的时候，OnCreateViewHolder将会被触发
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rank, null);
            return new RankingViewHolder(view);
        } else if (viewType == TYPE_HEAD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rank_head, parent, false);
            HeadViewHolder headViewHolder = new HeadViewHolder(view);
            return headViewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_load_more_layout, parent, false);
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            footViewHolder.setIsRecyclable(false);

            return footViewHolder;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {//显示头
            return TYPE_HEAD;
        } else if (position + 1 == getItemCount()) {//显示foot
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;

        }
    }

    /**
     * onBindViewHolder方法负责将数据与ViewHolder绑定
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeadViewHolder) {
            final UserEntity U = MyApplication.getInstance(mContext).getLocalUserInfoProvider();
            final HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            final MyRankVO myRankVO = myRankVOs.get(0);//显示数据源
            headViewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳入个人信息页面
                    Intent intent = new Intent(mContext, PersonalInfoActivity.class);
                    mContext.startActivity(intent);
                }
            });
            headViewHolder.ranking_activity_listview_item_nickname_head.setText(U.getUserBase().getNickname());
            headViewHolder.ranking_list_item_dingValue_head.setText(myRankVO.getZan());
            if (myRankVO.getDistance().equals("0")) {
                headViewHolder.ranking_activity_listview_item_distance_head.setText(mContext.getString(R.string.general_null_data));
                headViewHolder.tv_steps.setVisibility(View.GONE);
                headViewHolder.ranking_list_item_myRank_head.setVisibility(View.GONE);
            } else {
                headViewHolder.ranking_activity_listview_item_distance_head.setText(myRankVO.getDistance());
                String rank = MessageFormat.format(mContext.getString(R.string.ranking_rank), myRankVO.getRank());
                headViewHolder.ranking_list_item_myRank_head.setText(rank);
            }
            String url = NoHttpRuquestFactory.getUserAvatarDownloadURL(mContext, U.getUser_id() + "", U.getUserBase().getUser_avatar_file_name(), true);

            //下载失败的时候我们做了处理,根据性别去显示他的默认图像
            Resources res = mContext.getResources();
            imageLoader.displayImage(url, headViewHolder.ranking_activity_listview_item_imageView_head, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    if(U.getUserBase().getUser_sex()==0){
                        //女生
                        headViewHolder.ranking_activity_listview_item_imageView_head.setImageResource(R.mipmap.default_avatar_f);
                    }else {
                        //男生
                        headViewHolder.ranking_activity_listview_item_imageView_head.setImageResource(R.mipmap.default_avatar_m);
                    }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    ImageView mhead = (ImageView) view;
                    headViewHolder.ranking_activity_listview_item_imageView_head.setImageBitmap(loadedImage);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    if(U.getUserBase().getUser_sex()==0){
                        //女生
                        headViewHolder.ranking_activity_listview_item_imageView_head.setImageResource(R.mipmap.default_avatar_f);
                    }else {
                        //男生
                        headViewHolder.ranking_activity_listview_item_imageView_head.setImageResource(R.mipmap.default_avatar_m);
                    }
                }
            });
        } else if (holder instanceof RankingViewHolder) {
            final RankingViewHolder rholder = (RankingViewHolder) holder;
            final RankingVO rankingVO = rankingVOs.get(position - 1);
            MyLog.i("排行榜的数据="+rankingVO.toString());
            rholder.ranking_list_item_dingValue.setTag(position);
//            rholder.setIsRecyclable(false);
            rholder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到好友信息界面
                    Intent intent = new Intent(mContext, FriendInfoActivity.class);
                    intent.putExtra("__user_id__", rankingVO.getUser_id());
                    intent.putExtra("_nickname_", rankingVO.getNickname());
                    intent.putExtra("__UserAvatar__", rankingVO.getUser_avatar_file_name());
                    mContext.startActivity(intent);
                }
            });

            rholder.ranking_activity_listview_item_distance.setText(rankingVO.getStep() + "");
            MyLog.e(TAG, "=======排名===position=" +position);
//            if(CommonUtils.isStringEmpty(rankingVO.getRank()))
            if (rankingVO.getRank().equals("1")) {
                rholder.ranking_activity_listview_item_rank.setText("");
                rholder.ranking_activity_listview_item_rank.setBackgroundResource(R.mipmap.ranking_1);
                rholder.setIsRecyclable(false);
            } else if (rankingVO.getRank().equals("2")) {
                rholder.ranking_activity_listview_item_rank.setText("");
                rholder.ranking_activity_listview_item_rank.setBackgroundResource(R.mipmap.ranking_2);
                rholder.setIsRecyclable(false);
            } else if (rankingVO.getRank().equals("3")) {
                rholder.ranking_activity_listview_item_rank.setText("");
                rholder.ranking_activity_listview_item_rank.setBackgroundResource(R.mipmap.ranking_3);
                rholder.setIsRecyclable(false);
            } else {
                rholder.ranking_activity_listview_item_rank.setText(rankingVO.getRank());
            }
//            final int yizan = Integer.parseInt(rankingVO.getYizan());
            rholder.ranking_list_item_come_from.setText(rankingVO.getEnt_name());
            rholder.ranking_list_item_dingValue.setText(rankingVO.getZan());
            rholder.ranking_list_item_dingValue.setTag(position);
            if (Integer.parseInt(rankingVO.getYizan()) == 0) {
                // rholder.ranking_list_item_dingValue.setChecked(false);
            } else {
                if(!integerlist.contains(position))
                    integerlist.add(position);
                MyLog.e(TAG, "=======sdfsafsa排名===position=" + position);
                // rholder.ranking_list_item_dingValue.setChecked(true);
            }

            if(integerlist.contains(position)){
                rholder.ranking_list_item_dingValue.setChecked(true);
            }else{
                rholder.ranking_list_item_dingValue.setChecked(false);
            }

            rholder.ranking_list_item_dingValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MyLog.e(TAG, "=======sdfsafsa排名===position=" + integerlist.toString());

                    Integer pos = (Integer) v.getTag();

                    MyLog.e(TAG, "=======sdfsafsa排名===pos=" + pos);

                    if (integerlist.contains(pos)) {
                        MyLog.e(TAG, "取消赞操作！");
                        //请求网络  修改数据s
                        String userTime = sdf.format(new Date());
                        String user_id = MyApplication.getInstance(mContext).getLocalUserInfoProvider().getUserBase().getUser_id() + "";
                        MyLog.e(TAG, "==总赞数等于0并且当天没踩过====所选用户的user_id=======" + rankingVO.getUser_id());
                        CallServer.getRequestInstance().add(mContext, mContext.getString(R.string.general_submitting), CommParams.HTTP_RANK, NoHttpRuquestFactory.submit_Rangking_cancle_zan_Request(userTime, "0", rankingVO.getUser_id(), user_id), new HttpCallback<String>() {
                            @Override
                            public void onSucceed(int what, Response<String> response) {
                                DataFromServer dataFromClientNew = JSON.parseObject(response.get(), DataFromServer.class);
                                MyLog.e(TAG, "==总赞数等于0并且当天没踩过====dataFromClientNew.getErrorCode()=======" + dataFromClientNew.getErrorCode());
                                if (dataFromClientNew.getErrorCode() == 1) {
                                    if (Integer.parseInt(rankingVO.getYizan()) == 1) {
                                        rholder.ranking_list_item_dingValue.setText((Integer.parseInt(rankingVO.getZan()) - 1) + "");
                                        MyLog.e(TAG, "==总赞数等于0并且当天没踩过=" + rholder.ranking_list_item_dingValue.getText());
                                    } else
                                        rholder.ranking_list_item_dingValue.setText(Integer.parseInt(rankingVO.getZan()) + "");
                                    rankingVOs.get(position - 1).setZan((Integer.parseInt(rankingVO.getZan()) - 1) + "");

                                    rankingVOs.get(position - 1).setYizan("0");

                                    integerlist.remove((Integer) position);
                                }
                            }

                            @Override
                            public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

                            }
                        });
                        MyLog.e(TAG, "=======sdfsafsa排名取消赞操作hou===position=" + integerlist.toString());

                    } else {
                        MyLog.e(TAG, "执行赞操作！");
                        String userTime = sdf.format(new Date());
                        String user_id = MyApplication.getInstance(mContext).getLocalUserInfoProvider().getUser_id() + "";
                        MyLog.e(TAG, "===点赞===所选用户的user_id=======" + rankingVO.getUser_id());
                        CallServer.getRequestInstance().add(mContext, mContext.getString(R.string.general_submitting), CommParams.HTTP_RANK, NoHttpRuquestFactory.submit_Rangking_ding_Request(userTime, "0", rankingVO.getUser_id(), user_id), new HttpCallback<String>() {
                            @Override
                            public void onSucceed(int what, Response<String> response) {
                                DataFromServer dataFromClientNew = JSON.parseObject(response.get(), DataFromServer.class);
                                MyLog.e(TAG, "===isChecked====提交数据返回结果response.get()为：" + response.get() + ",zan=" + Integer.parseInt(rankingVO.getYizan()));

                                if (dataFromClientNew.getErrorCode() == 1) {
                                    if (Integer.parseInt(rankingVO.getYizan()) == 1) {
                                        rholder.ranking_list_item_dingValue.setText(Integer.parseInt(rankingVO.getZan()) + "");
                                    } else {
                                        rholder.ranking_list_item_dingValue.setText((Integer.parseInt(rankingVO.getZan()) + 1) + "");
                                    }
                                    rankingVOs.get(position - 1).setZan((Integer.parseInt(rankingVO.getZan()) + 1) + "");

                                    rankingVOs.get(position - 1).setYizan("1");

                                    integerlist.add(position);
                                }

                            }

                            @Override
                            public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

                            }
                        });

                    }
                }
            });
            //下载失败的时候我们做了处理,根据性别去显示他的默认图像
            if (!CommonUtils.isStringEmpty(rankingVOs.get(position - 1).getUser_avatar_file_name())) {
                String url = NoHttpRuquestFactory.getUserAvatarDownloadURL(mContext, rankingVOs.get(position - 1).getUser_id(), rankingVOs.get(position - 1).getUser_avatar_file_name(), true);
                rholder.ranking_user_head.setTag(url);
                imageLoader.displayImage(url, rholder.ranking_user_head, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        MyLog.e(TAG, "开始加载options是：" + options);
                    }
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (rholder.ranking_user_head.getTag().equals(imageUri)) {
                            MyLog.e(TAG, "加载失败options是：" + options);
                            if(Integer.parseInt(rankingVOs.get(position - 1).getUser_sex())==0){
                                rholder.ranking_user_head.setImageResource(R.mipmap.default_avatar_f);
                            }else {
                                rholder.ranking_user_head.setImageResource(R.mipmap.default_avatar_m);
                            }
                        }
                    }
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        MyLog.e(TAG, "加载完成options是：" + options);
                        MyLog.e(TAG, "rholder.ranking_user_head.getTag()：" + rholder.ranking_user_head.getTag());
                        MyLog.e(TAG, "imageUri：" + imageUri);
                        if (rholder.ranking_user_head.getTag().equals(imageUri)) {
                            MyLog.e(TAG, "加载完成options是：" + options);
                            ImageView mhead = (ImageView) view;
                            mhead.setImageBitmap(loadedImage);
                        }
                    }
                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        if (rholder.ranking_user_head.getTag().equals(imageUri)) {
                            MyLog.e(TAG, "加载取消options是：" + options);
                            if(Integer.parseInt(rankingVOs.get(position - 1).getUser_sex())==0){
                                rholder.ranking_user_head.setImageResource(R.mipmap.default_avatar_f);
                            }else {
                                rholder.ranking_user_head.setImageResource(R.mipmap.default_avatar_m);
                            }

                        }
                    }
                });
            }else{
                //头像名是空的时候
                MyLog.i(TAG,"rankingVOs.get(position - 1).getUser_sex()"+rankingVOs.get(position - 1).getUser_sex());

                if(Integer.parseInt(rankingVOs.get(position - 1).getUser_sex())==0){
                    //女
                    rholder.ranking_user_head.setImageResource(R.mipmap.default_avatar_f);
                }else {
                    //男
                    rholder.ranking_user_head.setImageResource(R.mipmap.default_avatar_m);
                }
            }

            rholder.ranking_activity_listview_item_nickname.setText(rankingVO.getNickname());

            if (rankingVOs.get(position - 1).getStep() == 0) {
                //隐藏item
                view.setVisibility(View.GONE);
            } else {
                //显示item
                view.setVisibility(View.VISIBLE);
            }
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;

            int i = footViewHolder.getAdapterPosition();
            if (rankingVOs.size() != 50) {
                footViewHolder.foot_view_item.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return rankingVOs.size() + 2;
    }

    // 添加长点击事件
    private class RankingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout mLinearLayout;
        public TextView ranking_activity_listview_item_nickname, ranking_list_item_come_from;
        public TextView ranking_activity_listview_item_rank,
                ranking_activity_listview_item_distance;
        public ImageView ranking_user_head;
        CheckBox ranking_list_item_dingValue;

        public RankingViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.rank_list_layout);
            ranking_activity_listview_item_rank = (TextView) itemView.findViewById(R.id.ranking_activity_listview_item_rank);
            //昵称
            ranking_activity_listview_item_nickname = (TextView) itemView.findViewById(R.id.ranking_activity_listview_item_nickname);
            //企业
            ranking_list_item_come_from = (TextView) itemView.findViewById(R.id.ranking_list_item_come_from);
//步数
            ranking_activity_listview_item_distance = (TextView) itemView.findViewById(R.id.ranking_activity_listview_item_distance);
//踩
            ranking_list_item_dingValue = (CheckBox) itemView.findViewById(R.id.ranking_list_item_dingValue);


            ranking_user_head = (ImageView) itemView.findViewById(R.id.ranking_activity_listview_item_imageView);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//跳转页面
                case R.id.rank_list_layout:
                    //跳转页面
                    break;

            }
        }
    }
    public class FootViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout foot_view_item;
        public FootViewHolder(View view) {
            super(view);
            foot_view_item = (LinearLayout) view.findViewById(R.id.my_foot_linearlayout);
        }
    }
    public class HeadViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLinearLayout;
        ImageView ranking_activity_listview_item_imageView_head;
        TextView ranking_activity_listview_item_nickname_head, ranking_activity_listview_item_distance_head, ranking_list_item_myRank_head, tv_steps;
        CheckBox ranking_list_item_dingValue_head;
        public HeadViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.rank_list_layout_head);
            ranking_activity_listview_item_imageView_head = (ImageView) itemView.findViewById(R.id.ranking_activity_listview_item_imageView_head);
            ranking_activity_listview_item_nickname_head = (TextView) itemView.findViewById(R.id.ranking_activity_listview_item_nickname_head);
            ranking_activity_listview_item_distance_head = (TextView) itemView.findViewById(R.id.ranking_activity_listview_item_distance_head);
            tv_steps = (TextView) itemView.findViewById(R.id.tv_steps);
            ranking_list_item_myRank_head = (TextView) itemView.findViewById(R.id.ranking_list_item_myRank_head);
            ranking_list_item_dingValue_head = (CheckBox) itemView.findViewById(R.id.ranking_list_item_dingValue_head);
        }
    }
    //添加数据
    public void addItem(List<RankingVO> newDatas) {
        MyLog.i(TAG, "上拉加载");
//        this.rankingVOs=newDatas;
        rankingVOs.addAll(newDatas);
        notifyDataSetChanged();
    }
    public void addItemMine(List<MyRankVO> newDatas) {
        MyLog.i(TAG, "上拉加载");
//        this.rankingVOs=newDatas;
        myRankVOs.addAll(newDatas);
        notifyDataSetChanged();
    }
}
