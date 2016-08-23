package com.linkloving.rtring_new.logic.UI.friend.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.dto.UserBase;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo.wang on 2016/3/18.
 *
 * 查找好友
 */
    public class FindFriend extends Fragment implements   View.OnClickListener {

    //网络请求的what
    public final static int REQUEST_GET_FRIEND=13;

    public final static String TAG = FindFriend.class.getSimpleName();

    private String mCondition;//当前的输入内容

    private EditText search_content;
    private Button find;

    //SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;

    private int pageIndex = 1;//开始加载第一页

    LinearLayoutManager mLayoutManager;

    SearchAdapter adapter;

    private int afterpage=0;

    int lastVisibleItem;

    private List<UserBase> list = new ArrayList<UserBase>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflateAndSetupView(inflater, container, savedInstanceState, R.layout.fragment_find_friend);

    }

        private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);

            find= (Button) layout.findViewById(R.id.search_btn);

            find.setOnClickListener(this);

            search_content= (EditText) layout.findViewById(R.id.search_content);

           // mSwipeRefreshLayout= (SwipeRefreshLayout) layout.findViewById(R.id.swipe_refresh_widget);

            mRecyclerView= (RecyclerView) layout.findViewById(R.id.my_attention_recyclerView);

          //  mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));

          //  mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.black);

            //进入页面的时候加载进度条
          //  mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

            mLayoutManager=new LinearLayoutManager(getContext());

            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            mRecyclerView.setLayoutManager(mLayoutManager);
            //添加分隔线
            return layout;
        }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

       // mSwipeRefreshLayout.setRefreshing(false);


    }

    RecyclerView.OnScrollListener mOnScrollListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (adapter == null) {
                return;
            }

            if(afterpage==pageIndex){
                return;
            }


            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {

                MyLog.i(TAG, "加载更多newState= " + newState + "lastVisibleItem=" + lastVisibleItem + "adapter.getItemCount()=" + adapter.getItemCount());

                getFriend(mCondition);

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

        }

        };


        //请求网络数据
             private  void getFriend(String mCondition){

                 afterpage++;

            String userId = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id()+"";

            CallServer.getRequestInstance().add(getContext(), false, REQUEST_GET_FRIEND, NoHttpRuquestFactory.create_My_FindFriend_Request(mCondition,  pageIndex), httpCallback);

             }

        private HttpCallback<String> httpCallback=new HttpCallback<String>(){
        @Override
        public void onSucceed(int what, Response<String> response) {

            MyLog.i(TAG, "onSucceed");
            DataFromServer dataFromServer =JSON.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            MyLog.i(TAG, "value=" + value);
         //  mSwipeRefreshLayout.setRefreshing(false);
            if(dataFromServer.getErrorCode()!=1){
                return;
            }

            if(CommonUtils.isStringEmpty(value)){
                //f返回是空的且是第一次
                if(pageIndex < 2)
                    MyToast.showShort(getContext(),getString(R.string.relationship_search_empty));
                afterpage--;
                return;
            }

            MyLog.i(TAG, value);
//            ArrayList<UserBase> temp = (ArrayList<UserBase>) JSON.parseArray(value, UserBase.class);
            ArrayList<UserBase> temp = new Gson().fromJson(value, new TypeToken<List<UserBase>>() {  }.getType());

            MyLog.i(TAG,"temp:"+JSON.toJSONString(temp));
            MyLog.i(TAG, "what=" + what + "");

            if(temp.size()<1)

            {
                if(pageIndex < 2){
                    MyToast.showShort(getContext(),getString(R.string.relationship_search_empty));
                    afterpage--;
                 }

                else{

                    MyToast.showShort(getContext(),"只有这么多了哦");

                    mRecyclerView.removeOnScrollListener(mOnScrollListener);//消除监听
                    return;
                }

            }



            if(pageIndex==1){
                //第一次获得数据
                MyLog.i("pageIndex="+pageIndex,"temp.size()="+temp.size());
                list=temp;
                MyLog.i(TAG, " list=" + list.size());
                adapter=new SearchAdapter(getContext(),temp);
                mRecyclerView.setAdapter(adapter);
                //判断要不要隐藏foot
                pageIndex++;

            }
            else
            {

                MyLog.i(TAG,"pageIndex="+pageIndex+"temp.size()="+temp.size());

                pageIndex++;


                MyLog.i(TAG,"pageIndex="+pageIndex);

                adapter.addMoreItem(temp);
            }

            if (temp.size()!=50){

                MyLog.i(TAG,"取消监听");

                mRecyclerView.removeOnScrollListener(mOnScrollListener);//消除监听

            }


        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {


            afterpage--;

            MyLog.i(TAG,"onFailed");

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.search_btn:

                mRecyclerView.addOnScrollListener(mOnScrollListener);

                String condition = search_content.getEditableText().toString();

                ToolKits.HideKeyboard(search_content);

                if(condition == null || condition.isEmpty())
                {

                    MyToast.showShort(getContext(),getString(R.string.relationship_search_condition_empty));

                    return;
                }



                pageIndex = 1;
                afterpage=0;

                list.clear();

                mCondition = condition;

                getFriend(condition);

                break;

        }

    }

    //SearchAdapter

    public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<UserBase> newDatas=new ArrayList<UserBase>();

        //上拉加载更多
        public static final int  PULLUP_LOAD_MORE=0;
        //正在加载中
        public static final int  LOADING_MORE=1;
        //上拉加载更多状态-默认为0
        private int load_more_status=0;
        private static final int TYPE_ITEM = 0;  //普通Item View
        private static final int TYPE_FOOTER = 1;  //顶部FootView

        private LayoutInflater mInflater;
        private List<UserBase> list = new ArrayList<UserBase>();
        Context context;
        public SearchAdapter(Context context, List<UserBase> list){

            this.context=context;
            newDatas=list;
            this.list=list;
            this.mInflater=LayoutInflater.from(context);

        }



        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            if(viewType==TYPE_ITEM){

                View view=mInflater.inflate(R.layout.list_item_search,parent,false);
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
        public int getItemViewType(int position) {


            // 最后一个item设置为footerView
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(holder instanceof ItemViewHolder){

               // MyLog.i(TAG, "ItemViewHolder=" + position);

                final ItemViewHolder itemViewHolder = ((ItemViewHolder) holder);

                itemViewHolder.search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(list.get(position).getUser_avatar_file_name()==null){
                            Intent intent= IntentFactory.create_FriendInfoActivity(context, list.get(position).getUser_id()+"","");
                            context.startActivity(intent);
                        }else{
                            Intent intent= IntentFactory.create_FriendInfoActivity(context, list.get(position).getUser_id()+"",list.get(position).getUser_avatar_file_name());
                            context.startActivity(intent);
                        }


                    }
                });
                if(list.get(position).getUser_avatar_file_name()!=null){
                    //图像以后设置
                    String url= NoHttpRuquestFactory.getUserAvatarDownloadURL(context,list.get(position).getUser_id()+"",list.get(position).getUser_avatar_file_name(),true);
                    //MyLog.i(TAG,url);
                    DisplayImageOptions options;
                    options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                            .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                            .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
//                            .showImageOnFail(R.mipmap.default_avatar)//加载失败显示图片
                            .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                            .build();//构建完成

                    ImageLoader.getInstance().displayImage(url, itemViewHolder.head, options, new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            if(list.get(position).getUser_sex()==0){
                                //女生
                                itemViewHolder.head.setImageResource(R.mipmap.default_avatar_f);
                            }else {
                                //男生
                                itemViewHolder.head.setImageResource(R.mipmap.default_avatar_m);
                            }
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            ImageView  mhead=  (ImageView)view;

                            mhead.setImageBitmap(loadedImage);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            if(list.get(position).getUser_sex()==0){
                                //女生
                                itemViewHolder.head.setImageResource(R.mipmap.default_avatar_f);
                            }else {
                                //男生
                                itemViewHolder.head.setImageResource(R.mipmap.default_avatar_m);
                            }
                        }
                    });
                }

             //   ImageLoader.getInstance().displayImage(url, itemViewHolder.head);
                //图片的加载
                itemViewHolder.label.setText(list.get(position).getWhat_s_up());

                itemViewHolder.nickName.setText(list.get(position).getNickname());

            }

            else if(holder instanceof FootViewHolder){

                MyLog.i(TAG, "FootViewHolderr+position=" + position);

                FootViewHolder footViewHolder=(FootViewHolder)holder;

                    int i=footViewHolder.getAdapterPosition();

                if (newDatas.size()!=50){

                    MyLog.i(TAG, "int i=" + i +"new="+newDatas.size());
                    footViewHolder.foot_view_item.setVisibility(View.GONE);

                }

            }




        }


        @Override
        public int getItemCount() {

            return list.size()+1;

        }

        public void addMoreItem(List<UserBase> newDatas) {

            this.newDatas=newDatas;

            MyLog.i(TAG,newDatas.size()+">>>>>>>>>>>>>>>");
            list.addAll(newDatas);
            notifyDataSetChanged();
        }


        public void addItem(List<UserBase> newDatas) {
            //mTitles.add(position, data);
            //notifyItemInserted(position);
            MyLog.i(TAG, "下拉刷新");
            newDatas.addAll(list);
            list.removeAll(list);
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
            public LinearLayout search;
            public ImageView head;
            public TextView nickName;
            public TextView label;
            public ItemViewHolder(View itemView) {
                super(itemView);
                search= (LinearLayout) itemView.findViewById(R.id.linear_search);
                 head = (ImageView) itemView.findViewById(R.id.user_head);
                 label = (TextView) itemView.findViewById(R.id.search_label);
                 nickName = (TextView) itemView.findViewById(R.id.search_nickName);

            }
        }


    }



}
