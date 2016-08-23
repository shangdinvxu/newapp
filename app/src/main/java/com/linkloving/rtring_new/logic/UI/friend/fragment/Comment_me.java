package com.linkloving.rtring_new.logic.UI.friend.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.friend.chatbean.UserChatDTO;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by leo.wang on 2016/3/14.
 */
public class Comment_me extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //网络请求的what
    public final static int REQUEST_GET_COMMENT = 11;

    //网络请求的what
    public final static int REQUEST_GET_MESSAGE = 12;
    //进入发消息页面标志
    public final static int COMEIN_COMMENT = 1;
    public final static String TAG = Comment_me.class.getSimpleName();

    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;

    // int lastVisibleItem;

    CimmentMeAdapter adapter;

    LinearLayoutManager mLayoutManager;

    private List<UserChatDTO> list_unread=new ArrayList<UserChatDTO>();
    private List<UserChatDTO> list_unread_total = new ArrayList<UserChatDTO>();
    List<UserChatDTO> list=new ArrayList<UserChatDTO>();
    private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);

    private int unread;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflateAndSetupView(inflater, container, savedInstanceState, R.layout.fragment_my_attention);

    }

    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_refresh_widget);

        mRecyclerView = (RecyclerView) layout.findViewById(R.id.my_attention_recyclerView);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.black);

        mSwipeRefreshLayout.setOnRefreshListener(this);//设置监听

        //进入页面的时候加载进度条
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mLayoutManager = new LinearLayoutManager(getActivity());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        //添加分隔线

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return layout;

    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//
//        super.onActivityCreated(savedInstanceState);
//
//        mSwipeRefreshLayout.setOnRefreshListener(this);//设置监听
//
////        mSwipeRefreshLayout.setRefreshing(true);
//        //添加方法：读取本地的消息记录 显示在页面上:
//        String value = PreferencesToolkits.getComment(getActivity());
//        MyLog.e(TAG, "获取到本地存储的聊天信息列表" + value);
//        if (!"fail".equals(value)) {
//                list = JSONObject.parseArray(value, UserChatDTO.class);
//        }
////        getComment_me();
//    }

    @Override
    public void onRefresh() {
        getComment_me();

    }


    @Override
    public void onResume() {
        super.onResume();
        getComment_me();
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            if(mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
            MyLog.e(TAG, "onSucceed" + response.get());
            DataFromServer dataFromServer = JSONObject.parseObject(response.get(), DataFromServer.class);
            //value 为空 表示该用户没有新的消息，弹框提示用户
            // 从服务器中下载下来的消息列表需要保存到本地，当用户进入界面的时候从本地读取消息记录
            String value = dataFromServer.getReturnValue().toString();
            MyLog.e(TAG, "value=" + value);
            if (!("[]".equals(value) && CommonUtils.isStringEmpty(value))) {
                list_unread = JSONObject.parseArray(value, UserChatDTO.class);
                if (what == REQUEST_GET_COMMENT) {
                    //未读消息数的请求  list_unread 不为空执行以下操作
                    if (!"".equals(list_unread.toString())) {
//                        int commentNum = 0;
//                        for (UserChatDTO userChatDTO : list_unread) {
//                            commentNum += userChatDTO.getUnread();
//                        }
//                        MyApplication.getInstance(getActivity()).setCommentNum(commentNum);
                        MyLog.e(TAG, value);
                        boolean cansave = true;
                        MyLog.e(TAG, "what=" + what + "list_unread.toString()" + list_unread.toString()+"list_unread.size()="+list_unread.size());
//                    保存到本地  判断是否存过  存过直接忽略
                        if(list.size()==0){
                            for (UserChatDTO userChatDTO:list_unread){
                                userChatDTO.setUnread(0);
                            }
                            list.addAll(list_unread);
                        }else{
                            for (int i = 0; i < list_unread.size(); i++) {

                                for (int j = 0;j < list.size() ; j++) {
                                    int my_userId = list.get(j).getUser_id();
                                    int unread_userId = list_unread.get(i).getUser_id();
                                    if (my_userId==unread_userId) {
                                        cansave =false;
                                        list.get(j).setLast_content(list_unread.get(i).getLast_content());
                                        MyLog.e(TAG, "添加一条数据：list.get(j).getUser_id():"+list.get(j).getUser_id()+ "--list_unread.get(i).getUser_id():"+list_unread.get(i).getUser_id());
                                    }
                                }
                                if(cansave){
                                    //未保存过 保存到本地  设置未读为已读

                                    list_unread.get(i).setUnread(0);
                                    list.add(list_unread.get(i));
                                }
                            }
                        }
                        PreferencesToolkits.saveComment(getActivity(), JSON.toJSONString(list));
                        adapter = new CimmentMeAdapter(getActivity(), list);
                        mRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            } else {
                //添加方法：读取本地的消息记录 显示在页面上:
                MyApplication.getInstance(getActivity()).setCommentNum(0);
                return;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

            MyLog.i(TAG, "onFailed");
            mSwipeRefreshLayout.setRefreshing(false);

        }
    };

    private void getComment_me() {

        String userId = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id() + "";
        //添加方法：读取本地的消息记录 显示在页面上:
        list.clear();
        String value= PreferencesToolkits.getComment(getActivity());
        MyLog.e(TAG, "获取到本地存储的聊天信息列表" + value);
        if(!"fail".equals(value)){
            list = JSONObject.parseArray(value, UserChatDTO.class);
            for (UserChatDTO userChatDTO:list){
                MyLog.e(TAG, "获取到本地的list" + userChatDTO.toString());
            }
        }
        //判断网络是否连接
        if (MyApplication.getInstance(getActivity()).isLocalDeviceNetworkOk()) {
            //获取未读消息列表
            CallServer.getRequestInstance().add(getActivity(), false, REQUEST_GET_COMMENT, NoHttpRuquestFactory.unread_messages_list(userId), httpCallback);
        } else
            MyToast.show(getActivity(), "当前网络不给力!", Toast.LENGTH_LONG);
    }

    //适配器类
    private class CimmentMeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<UserChatDTO> list = new ArrayList<UserChatDTO>();
        Context context;

        public CimmentMeAdapter(Context mcontext, List<UserChatDTO> list) {
            this.context = mcontext;
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //进行判断显示类型，来创建返回不同的View
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comments, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            ItemViewHolder itemViewHolder = ((ItemViewHolder) holder);

            /*   itemViewHolder.count.setText(list.get(position).comment_count);
           */
            itemViewHolder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入聊天界面
                    startActivity(IntentFactory.createUserDetialActivityIntent(getActivity(),
                            list.get(position < 0 ? 0 : position).getUser_id(),
                            list.get(position < 0 ? 0 : position).getTo_user_id(),
                            COMEIN_COMMENT));
                    //跳转是需要把选中用户的userId传递过去

                }
            });

            //根据时间显示
            String sting=list.get(position).getLast_time();
            MyLog.i(TAG, ">>>>>>>>>>>>>>stingstingstingstingsting>>>>>>>>>>>>>>>>>>>>>>>"+sting.substring(0,10)+">>>>>>"+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()));
           // Date date=ToolKits.stringToDate(sting,"yyyy/MM/dd HH:mm:ss");
            Date date=null;
            try {
                date=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).parse(sting);
            } catch (ParseException e) {
                MyToast.show(context,"解析时间出现异常",Toast.LENGTH_LONG);
                e.printStackTrace();
            }
            if(date!=null){
                int i=ToolKits.getBetweenDay(date,new Date());
                switch (i){
                    case 0:
                        itemViewHolder.last_time.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_HH_MM).format(date));
                        break;
                    case 1:
                        itemViewHolder.last_time.setText(getString(R.string.yesterday));
                        break;
                    default:
                        itemViewHolder.last_time.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(date));
                        break;
                }
            }

            //tuxiang
            //图像以后设置
            String url = NoHttpRuquestFactory.getUserAvatarDownloadURL(context, list.get(position).getUser_id() + "", list.get(position).getUser_avatar_file_name(), true);
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .showImageOnFail(R.mipmap.default_avatar_m)//加载失败显示图片
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                    .build();//构建完成

            ImageLoader.getInstance().displayImage(url, itemViewHolder.user_avatar_file_name, options, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageView mhead = (ImageView) view;

                    mhead.setImageBitmap(loadedImage);

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            int unRead = list.get(position).getUnread();
            MyLog.e(TAG, "unRead=" + unRead);
            if (unRead != 0) {
                if (unRead > 0) {
                    itemViewHolder.unRead.setVisibility(View.VISIBLE);
                    itemViewHolder.unRead.setText(unRead + "");
                } else {
                    itemViewHolder.unRead.setVisibility(View.GONE);
                }
            } else {
                itemViewHolder.unRead.setVisibility(View.GONE);
            }
            itemViewHolder.last_content.setText(list.get(position).getLast_content());
            itemViewHolder.nickName.setText(list.get(position).getNickname());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        //添加数据
        public void addItem(List<UserChatDTO> newDatas) {
            //mTitles.add(position, data);
            //notifyItemInserted(position);
            MyLog.i(TAG, "下拉刷新");
            newDatas.addAll(list);
            list.removeAll(list);
            list.addAll(newDatas);
            notifyDataSetChanged();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public RelativeLayout message;
            //            public TextView quname;
            public TextView last_content;
            public TextView nickName;
            public TextView last_time;
            public ImageView user_avatar_file_name;
            public TextView unRead;

            public ItemViewHolder(View view) {
                super(view);
                message = (RelativeLayout) view.findViewById(R.id.linear_message);
//                quname = (TextView) view.findViewById(R.id.nickqunname);//群组的
                last_content = (TextView) view.findViewById(R.id.comment);//评论的内容
                last_time = (TextView) view.findViewById(R.id.time);//评论的时间
                user_avatar_file_name = (ImageView) view.findViewById(R.id.head);//头像
                nickName = (TextView) view.findViewById(R.id.nickName);//昵称
                unRead = (TextView) view.findViewById(R.id.comments_item_unread_text);//未读的消息数
            }
        }

    }


    private class CommentsDTO {
        //未读消息
        public String unread;
        public String user_id;
        public String to_user_id;
        public String nickname;
        //头像
        public String user_avatar_file_name;
        public String last_time;
        public String last_content;
    }

}
