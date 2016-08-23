package com.linkloving.rtring_new.logic.UI.friend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo.wang on 2016/3/30.
 */
public class AttentionMeAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static String TAG = SampleAdapyer.class.getSimpleName();

    private LayoutInflater mInflater;

    private List<AttentionUser> list = new ArrayList<AttentionUser>();

    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;

    //上拉加载更多状态-默认为0
    private int load_more_status=0;

    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView

    Context context;

    public AttentionMeAdapter(Context context, List<AttentionUser> list) {

        this.context=context;
        this.list=list;
        this.mInflater=LayoutInflater.from(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyLog.i(TAG, "RecyclerView.ViewHolder onCreateViewHolde");
        //进行判断显示类型，来创建返回不同的View
        View view=mInflater.inflate(R.layout.sampeadapteritem,parent,false);
        //这边可以做一些属性设置，甚至事件监听绑定
        //view.setBackgroundColor(Color.RED);
        ItemViewHolder itemViewHolder=new ItemViewHolder(view);
        return itemViewHolder;
    }

    /**
     * 数据的绑定显示
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyLog.i(TAG,"onBindViewHolde");
        ItemViewHolder itemViewHolder= ((ItemViewHolder)holder);
        itemViewHolder.myLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳入个人信息页面
                Intent intent= IntentFactory.create_FriendInfoActivity(context, list.get(position).getUser_id()+"",list.get(position).getUserAvatar());
                context.startActivity(intent);
            }
        });
        //图像以后设置
        String url= NoHttpRuquestFactory.getUserAvatarDownloadURL(context, list.get(position).getUserAvatar(), list.get(position).getUserAvatar(), true);
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

        ImageLoader.getInstance().displayImage(url, itemViewHolder.head, options, new ImageLoadingListener() {
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
        itemViewHolder.label.setText( list.get(position).getWhat_s_up());
        itemViewHolder.nickName.setText(list.get(position).getNickname());

        /*  if(list.get(position).getDistance() == null || list.get(position).getDistance().isEmpty() || list.get(position).getDistance().equals("0"))*/

        if(list.get(position).getDistance()==0)
        {
            //holder.steps.setText("0");
            itemViewHolder.unitStep.setVisibility(View.GONE);
            itemViewHolder.steps.setVisibility(View.GONE);
        }
        else
        {
            itemViewHolder.unitStep.setVisibility(View.VISIBLE);
            itemViewHolder.steps.setVisibility(View.VISIBLE);
            itemViewHolder.steps.setText(list.get(position).getDistance()+"");
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public  class ItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout myLinearLayout;
        public ImageView head;
        public TextView nickName;
        public TextView label;
        public TextView steps;
        public TextView unitStep;
        public ItemViewHolder(View view){
            super(view);
            myLinearLayout= (LinearLayout) view.findViewById(R.id.my_attention_layout);
            head = (ImageView) view.findViewById(R.id.user_head);
            label = (TextView) view.findViewById(R.id.concern_label);
            nickName = (TextView) view.findViewById(R.id.concern_nickname);
            steps = (TextView) view.findViewById(R.id.concern_steps);
            unitStep = (TextView) view.findViewById(R.id.concern_unit_step);
        }
    }
    /**
     * 底部FootView布局
     */

   /* public static class FootViewHolder extends  RecyclerView.ViewHolder{
        private TextView foot_view_item_tv;
        public FootViewHolder(View view) {
            super(view);
            foot_view_item_tv=(TextView)view.findViewById(R.id.foot_view_item_tv);
        }
    }*/

    //添加数据
    public void addItem(List<AttentionUser> newDatas) {
        //mTitles.add(position, data);
        //notifyItemInserted(position);
        MyLog.i(TAG,"下拉刷新");
        newDatas.addAll(list);
        list.removeAll(list);
        list.addAll(newDatas);
        notifyDataSetChanged();
    }

   /* public void addMoreItem(List<AttentionUser> newDatas) {

        list.addAll(newDatas);

        notifyDataSetChanged();

    }*/

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
   /* public void changeMoreStatus(int status){

        load_more_status = status;

        MyLog.i(TAG,"加载更多");

    }
*/

}
