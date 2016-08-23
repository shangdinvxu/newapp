package com.linkloving.rtring_new.logic.UI.friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by leo.wang on 2016/3/21.
 */
public class NearbyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static String TAG = NearbyAdapter.class.getSimpleName();

    ArrayList<UserSelected> list =new ArrayList<UserSelected>();

    ArrayList<UserSelected> newDatas=new ArrayList<UserSelected>();

    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int  LOADING_MORE = 1;
    //上拉加载更多状态-默认为0
    private int load_more_status=0;
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView

    private LayoutInflater mInflater;

    Context context;

    public NearbyAdapter (Context context, ArrayList<UserSelected> list){

        this.context=context;
        newDatas=list;
        this.list=list;
        this.mInflater=LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==TYPE_ITEM){

            View view=mInflater.inflate(R.layout.nearby_activity_listview_item,parent,false);
            ItemViewHolder itemViewHolder=new ItemViewHolder(view);
            return itemViewHolder;

        }

        else if(viewType==TYPE_FOOTER){

            View foot_view=mInflater.inflate(R.layout.recycler_load_more_layout,parent,false);

            FootViewHolder footViewHolder=new FootViewHolder(foot_view);

            footViewHolder.setIsRecyclable(false);

            return footViewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(holder instanceof ItemViewHolder) {
                MyLog.i(TAG,"position="+position);

                final UserSelected rowData = list.get(position);
            final ItemViewHolder itemViewHolder = ((ItemViewHolder) holder);

                itemViewHolder.near.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= IntentFactory.create_FriendInfoActivity(context, rowData.getUser_id()+"",rowData.getUserAvatar());
                        context.startActivity(intent);

                    }
                });

            itemViewHolder.viewNickname.setText(rowData.getNickname());
//            if()
            itemViewHolder.viewDesc.setText(MessageFormat.format(context.getString(R.string.nearby_list_item_distance_desc), getDistance(rowData.getDistance()+"", (Activity) context), context.getString(getTimeAgoResId(rowData.getTime_ago()))));

            itemViewHolder.viewWhatsup.setText(rowData.getWhat_s_up());

            if(CommonUtils.isStringEmpty(rowData.getCome_from()))
            {
                itemViewHolder.entGroup.setVisibility(View.GONE);
            }else
            {
                itemViewHolder.entGroup.setVisibility(View.VISIBLE);
                itemViewHolder.viewComeFrom.setText(rowData.getCome_from());
                itemViewHolder. viewComeFrom.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
                itemViewHolder. entGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                     //   context.startActivity(IntentFactory.createCommonWebActivityIntent((Activity) context, rowData.getEnt_url()));

                    }
                });
            }
                //图像以后设置
                String url= NoHttpRuquestFactory.getUserAvatarDownloadURL(context, rowData.getUser_id()+"", rowData.getUserAvatar(), true);
             //   MyLog.i(TAG,url);
                DisplayImageOptions options;
                options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                        .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                        .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                        .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                        .showImageOnFail(R.mipmap.default_avatar_m)//加载失败显示图片
                        .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                        .build();//构建完成
                    ImageLoader.getInstance().displayImage(url, itemViewHolder.viewAvatar, options, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        ImageView  mhead=  (ImageView)view;
                        mhead.setImageBitmap(loadedImage);

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });



        }

        else if(holder instanceof FootViewHolder){

          //  MyLog.i(TAG, "FootViewHolderr+position=" + position);

            FootViewHolder footViewHolder=(FootViewHolder)holder;

            int i=footViewHolder.getAdapterPosition();

            if (newDatas.size()!=50){

             //   MyLog.i(TAG,"int i="+i+"new="+newDatas.size());

                footViewHolder.foot_view_item.setVisibility(View.GONE);

            }

        }


    }

    private static String getDistance(String distance, Activity context)
    {
        int d = (int) Float.parseFloat(distance);
        if(d > 1000)
            return d / 1000 + context.getString(R.string.nearby_unit_km);
        if(d < 50)
            return "50" + context.getString(R.string.nearby_unit_m);
        else
            return (int)d + context.getString(R.string.nearby_unit_m);

    }


    public static int getTimeAgoResId(String date)

    {

        if(CommonUtils.isStringEmpty(date)){
            return R.string.nearby_1_month_ago;
        }
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            Date before = sdf.parse(date);
            long second =(now.getTime() - before.getTime()) / 1000;

            //5分钟以内
            if(second <= 5 * 60)
                return R.string.nearby_5_minute_ago;
            //10分钟以内
            if(second <= 10 * 60)
                return R.string.nearby_10_minute_ago;
            //30分钟以内
            if(second <= 30 * 60)
                return R.string.nearby_30_minute_ago;
            //1小时以内
            if(second <= 1 * 60 * 60)
                return R.string.nearby_1_hour_ago;
            //2小时以内
            if(second <= 2 * 60 * 60)
                return R.string.nearby_2_hour_ago;
            //3小时以内
            if(second <= 3 * 60 * 60)
                return R.string.nearby_3_hour_ago;
            //4小时以内
            if(second <= 4 * 60 * 60)
                return R.string.nearby_4_hour_ago;
            //5小时以内
            if(second <= 5 * 60 * 60)
                return R.string.nearby_5_hour_ago;
            //6小时以内
            if(second <= 6 * 60 * 60)
                return R.string.nearby_6_hour_ago;
            //7小时以内
            if(second <= 7 * 60 * 60)
                return R.string.nearby_7_hour_ago;
            //8小时以内
            if(second <= 8 * 60 * 60)
                return R.string.nearby_8_hour_ago;
            //9小时以内
            if(second <= 9 * 60 * 60)
                return R.string.nearby_9_hour_ago;
            //10小时以内
            if(second <= 10 * 60 * 60)
                return R.string.nearby_10_hour_ago;
            //11小时以内
            if(second <= 11 * 60 * 60)
                return R.string.nearby_11_hour_ago;
            //12小时以内
            if(second <= 12 * 60 * 60)
                return R.string.nearby_12_hour_ago;
            //13小时以内
            if(second <= 13 * 60 * 60)
                return R.string.nearby_13_hour_ago;
            //14小时以内
            if(second <= 14 * 60 * 60)
                return R.string.nearby_14_hour_ago;
            //15小时以内
            if(second <= 15 * 60 * 60)
                return R.string.nearby_15_hour_ago;
            //16小时以内
            if(second <= 16 * 60 * 60)
                return R.string.nearby_16_hour_ago;
            //17小时以内
            if(second <= 17 * 60 * 60)
                return R.string.nearby_17_hour_ago;
            //18小时以内
            if(second <= 18 * 60 * 60)
                return R.string.nearby_18_hour_ago;
            //19小时以内
            if(second <= 19 * 60 * 60)
                return R.string.nearby_19_hour_ago;
            //20小时以内
            if(second <= 20 * 60 * 60)
                return R.string.nearby_20_hour_ago;
            //21小时以内
            if(second <= 21 * 60 * 60)
                return R.string.nearby_21_hour_ago;
            //22小时以内
            if(second <= 22 * 60 * 60)
                return R.string.nearby_22_hour_ago;
            //23小时以内
            if(second <= 23 * 60 * 60)
                return R.string.nearby_23_hour_ago;
            //1天以内
            if(second <= 1 * 24 * 60 * 60)
                return R.string.nearby_1_day_ago;
            //2天以内
            if(second <= 2 * 24 * 60 * 60)
                return R.string.nearby_2_day_ago;
            //3天以内
            if(second <= 3 * 24 * 60 * 60)
                return R.string.nearby_3_day_ago;
            //4天以内
            if(second <= 4 * 24 * 60 * 60)
                return R.string.nearby_4_day_ago;
            //5天以内
            if(second <= 5 * 24 * 60 * 60)
                return R.string.nearby_5_day_ago;
            //6天以内
            if(second <= 6 * 24 * 60 * 60)
                return R.string.nearby_6_day_ago;
            //1周以内
            if(second <= 7 * 24 * 60 * 60)
                return R.string.nearby_1_week_ago;
            //2周以内
            if(second <= 2 * 7 * 24 * 60 * 60)
                return R.string.nearby_2_week_ago;
            //3周以内
            if(second <= 3 * 7 * 24 * 60 * 60)
                return R.string.nearby_3_week_ago;
            //1个月以内
            if(second <= 30 * 24 * 60 * 60)
                return R.string.nearby_1_month_ago;
        }
        catch (ParseException e)
        {

        }
        //未匹配到则返回很久没上
        return R.string.nearby_too_long_ago;
    }

    @Override
    public int getItemViewType(int position) {

        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {

        return list.size()+1;

    }

    public void addMoreItem(ArrayList<UserSelected> newDatas) {

        this.newDatas=newDatas;

        MyLog.i(TAG, newDatas.size() + ">>>>>>>>>>>>>>>");

        list.addAll(newDatas);

        notifyDataSetChanged();
    }




    /**
     * 底部FootView布局
     */

    public  class FootViewHolder extends  RecyclerView.ViewHolder{
        private LinearLayout foot_view_item;
        public FootViewHolder(View view) {
            super(view);
            foot_view_item=(LinearLayout)view.findViewById(R.id.my_foot_linearlayout);
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public  class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView viewAvatar = null;
        TextView viewNickname = null;
        TextView viewDesc = null;
        ViewGroup entGroup = null;
        TextView viewComeFrom = null;
        TextView viewWhatsup = null;
        ImageView underline;
        LinearLayout near;

        public ItemViewHolder(View convertView) {
            super(convertView);
            near= (LinearLayout) convertView.findViewById(R.id.linear_near);
            viewNickname = (TextView) convertView.findViewById(R.id.nearby_activity_listview_item_nickname);
            viewDesc = (TextView) convertView.findViewById(R.id.nearby_activity_listview_item_distance);
            viewAvatar = (ImageView) convertView.findViewById(R.id.nearby_activity_listview_item_imageView);
            entGroup = (ViewGroup) convertView.findViewById(R.id.nearby_list_item_come_from_LL);
            viewComeFrom = (TextView) convertView.findViewById(R.id.nearby_list_item_come_from);
            viewWhatsup = (TextView) convertView.findViewById(R.id.nearby_list_item_what_s_up);


        }
    }
}
