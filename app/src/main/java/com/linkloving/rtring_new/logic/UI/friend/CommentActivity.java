package com.linkloving.rtring_new.logic.UI.friend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.db.comment.Comment_Util;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.friend.chatbean.CommentChat;
import com.linkloving.rtring_new.logic.UI.friend.chatbean.UserChat;
import com.linkloving.rtring_new.logic.UI.friend.chatbean.UserChatDTO;
import com.linkloving.rtring_new.logic.UI.friend.fragment.CommentAdapter;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CommentActivity extends ToolBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = CommentActivity.class.getSimpleName();
    private Button sendMessage;
    private TextView content;
    String comment_content;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    CommentAdapter commentAdapter;
    //发送者ID
    private int fromUserID;
    //接受者ID
    private int to_user_id;
    //从好友界面进入界面传递过来的参数
    private String user_avatar_file_name;//好友头像
    private String nickname;

    //    进入界面标志  值为1 表示从我的消息界面进入  否则是从好友信息界面进入
    private int comein_comment;
    UserEntity userEntity;
    private static int MSGTAG = 0;
    private int page = 1;
    //服务端取出的原始数据
    LinkedList<CommentChat> commentlist = new LinkedList<>();
    List<UserChatDTO> list = new ArrayList<UserChatDTO>();
    List<UserChatDTO> userChatDTOs = new ArrayList<UserChatDTO>();
    boolean flag = false;
    Comment_Util comment_util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common);
        if (comein_comment == 1)
        mSwipeRefreshLayout.setRefreshing(true);
        comment_util = new Comment_Util(this);
        initData();
        if (comein_comment != 1)
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initData() {
        ArrayList<CommentChat> list = Comment_Util.Query(userEntity.getUser_id(), fromUserID);
        commentlist.addAll(list);
        for (CommentChat commentChat : list) {
            MyLog.e(TAG, "数据库查询：" + commentChat.getComment_create_time() + "==to_user_id==" + commentChat.getTo_user_id() + "==getComments===" + commentChat.getComments());
        }

        if (comein_comment == 1)
            QueryFeedbackReply();
        else
            QueryMymessage();

        commentAdapter = new CommentAdapter(CommentActivity.this, commentlist);
        if (commentlist.size() > 0)
            mRecyclerView.scrollToPosition(commentlist.size() - 1);
        mRecyclerView.setAdapter(commentAdapter);
//        commentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void getIntentforActivity() {
        userEntity = MyApplication.getInstance(CommentActivity.this).getLocalUserInfoProvider();
        Intent intentTemp = getIntent();
        // intentTemp = IntentFactory.parseUserDetialActivityIntent(getIntent());
//        fromUserID = intentTemp[0];
//        to_user_id = intentTemp[1];
//        comein_comment=intentTemp[2];
        fromUserID = intentTemp.getExtras().getInt("__user_id__");
//        to_user_id = intentTemp.getExtras().getInt("__to_user_id__");
        comein_comment = intentTemp.getExtras().getInt("__check_tag_");
        if (comein_comment != 1) {
            user_avatar_file_name = intentTemp.getExtras().getString("_user_avatar_file_name_");
            nickname = intentTemp.getExtras().getString("_nickname_");
        }
        MyLog.e(TAG, "接收到传递过来的参数：fromUserID=" + fromUserID + ",userEntity.getUser_id()=" + userEntity.getUser_id() + ",comein_comment=" + comein_comment + "nickname" + nickname);

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString(R.string.whats_up_detail_sendMsg));
        sendMessage = (Button) findViewById(R.id.comment_about_comment_btn);
        content = (TextView) findViewById(R.id.comment_about_comments_edittext);
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_list_recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.comment_swipe_refresh_widget);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.black);

        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mLayoutManager = new LinearLayoutManager(CommentActivity.this);

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
        //添加分割线
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void initListeners() {

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送消息  上传网络
                //上传之前判断输入的内容是否为空  为空提示 内容不能为空
                comment_content = String.valueOf(content.getEditableText()).trim();
                if (comment_content.isEmpty()) {
                    MyToast.show(CommentActivity.this, getString(R.string.relationship_comment_empty), Toast.LENGTH_LONG);
                    return;
                }
                sendMsg(comment_content);
                content.setText("");
//                ToolKits.HideKeyboard(content);
            }
        });
    }


    private void sendMsg(String content) {
        if (MyApplication.getInstance(CommentActivity.this).isLocalDeviceNetworkOk()) {
            //发送消息
//            if(comein_comment==1) {
            //从我的消息界面进入  自己的id为to_user_id  接收者的id为 fromUserID
            CallServer.getRequestInstance().add(CommentActivity.this, getString(R.string.general_submitting), CommParams.HTTP_SEND_MSG, NoHttpRuquestFactory.create_Comment_me(userEntity.getUser_id() + "", fromUserID + "", content), httpCallback);
            MyLog.e(TAG, "从我的消息进入=userEntity.getUser_id()" + userEntity.getUser_id() + ",fromUserID=" + fromUserID);
//            }
//            else {
//                //从好友消息界面进入  自己的id直接获取本地存储的id  接收者的id为 fromUserID（从好友界面传递而来）
//                CallServer.getRequestInstance().add(CommentActivity.this, getString(R.string.general_submitting), CommParams.HTTP_SEND_MSG, NoHttpRuquestFactory.create_Comment_me(fromUserID + "", userEntity.getUser_id() + "", content), httpCallback);
//                MyLog.e(TAG,"从好友进入=to_user_id" + to_user_id + ",userEntity.getUser_id()=" + userEntity.getUser_id());
//            }
        } else {
            Toast.makeText(CommentActivity.this, getString(R.string.general_network_faild), Toast.LENGTH_LONG).show();
        }
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            mSwipeRefreshLayout.setRefreshing(false);
            DataFromServer dataFromServer = JSONObject.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            MyLog.e(TAG, "response.get()==" + response.get() + "value===" + value);
            if (dataFromServer.getErrorCode() == 1) {

                boolean addTag = true;
                //显示自己发送的消息  显示在右边界面上  并存储到本地（一个存储到本地数据库，一个存储到我的关注列表中）  以便下次进入界面查看
                //存储在本地
                Date now = new Date();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
                SimpleDateFormat dateFormat = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);//可以方便地修改日期格式
                String date = dateFormat.format(now);
                commentlist.add(new CommentChat(fromUserID, userEntity.getUser_id(), comment_content, date, "1"));
                if (comein_comment == 1) {
                    // 从我的消息界面进入时自己的id为：userEntity.getUser_id()
                    Comment_Util.InsertData(userEntity.getUser_id(), fromUserID, comment_content, date, "1");
                } else {
//                从好友信息界面进入时自己的id为：userEntity.getUser_id()
                    Comment_Util.InsertData(userEntity.getUser_id(), fromUserID, comment_content, date, "1");
                    //存到我的消息界面显示  先遍历list 看是否保存过该用户的聊天记录  没保存则保存  否则 不保存
                    UserChatDTO userChatDTO = new UserChatDTO();
                    userChatDTO.setNickname(nickname);
                    userChatDTO.setTo_user_id(userEntity.getUser_id());//返回消息列表时 自己的id相当于从服务器返回的touserid 这样从我的消息界面进入聊天界面时数据才会显示正确
                    userChatDTO.setUser_avatar_file_name(user_avatar_file_name);
                    userChatDTO.setUser_id(fromUserID);
                    userChatDTO.setLast_content(comment_content);
                    userChatDTO.setLast_time(date);
                    userChatDTO.setUnread(0);
                    userChatDTOs.add(userChatDTO);

                    if (list.size() == 0) {
                        list.add(userChatDTO);
                        PreferencesToolkits.saveComment(CommentActivity.this, JSON.toJSONString(list));
                        MyLog.e(TAG," 存储数据：JSON.toJSONString(list)="+JSON.toJSONString(list));
                    } else {
//                    遍历list 判断是否保存过
                        MyLog.e(TAG, "开始循环！");
                        for (int i = 0; i < userChatDTOs.size(); i++) {


                                for (int j = 0; j < list.size(); j++) {
                                int my_userId = list.get(j).getUser_id();
                                if (my_userId == fromUserID) {
                                    addTag = false;
                                    MyLog.e(TAG, "添加一条数据：list.get(j).getUser_id():" + list.get(j).getUser_id() + "--fromUserID:" + fromUserID);
                                }

                            }
                            if (addTag) {
                                list.add(userChatDTO);
                                PreferencesToolkits.saveComment(CommentActivity.this, JSON.toJSONString(list));
                                MyLog.e(TAG, " 存储数据1：JSON.toJSONString(list)=" + JSON.toJSONString(list));
                            }
                        }


                    }
                }
                ToolKits.HideKeyboard(content);
                MyLog.e(TAG, "发送时插入的数据=fromUserID" + fromUserID + "userEntity.getUser_id()=" + userEntity.getUser_id() + "comment_content=" + comment_content + date);
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            mSwipeRefreshLayout.setRefreshing(false);
            }
        };

    private void QueryFeedbackReply() {
        list.clear();
        String value = PreferencesToolkits.getComment(CommentActivity.this);
        MyLog.e(TAG, "获取到本地存储的聊天信息列表" + value);
        if (!"fail".equals(value)) {
            list = JSONObject.parseArray(value, UserChatDTO.class);
            for (UserChatDTO userChatDTO : list) {
                MyLog.e(TAG, "获取到本地的list" + userChatDTO.toString());
            }
        }
        if (MyApplication.getInstance(CommentActivity.this).isLocalDeviceNetworkOk()) {
            //查询详细消息
            CallServer.getRequestInstance().add(CommentActivity.this, getString(R.string.general_submitting), CommParams.HTTP_CHECK_MSG, NoHttpRuquestFactory.message_details(fromUserID + "", userEntity.getUser_id() + ""), HttpCallback1);
        } else {
            MyToast.show(CommentActivity.this, getString(R.string.smssdk_network_error), Toast.LENGTH_LONG);
        }

    }

    //从好友信息界面进入执行此操作 好友界面进入时不需要请求网络  只要在发送消息时保存消息到我的消息界面列表中
    private void QueryMymessage() {
        list.clear();
        String value = PreferencesToolkits.getComment(CommentActivity.this);
        MyLog.e(TAG, "获取到本地存储的聊天信息列表" + value);
        if (!"fail".equals(value)) {
            list = JSONObject.parseArray(value, UserChatDTO.class);
            for (UserChatDTO userChatDTO : list) {
                MyLog.e(TAG, "获取到本地的list" + userChatDTO.toString());
            }
        }
        if (MyApplication.getInstance(CommentActivity.this).isLocalDeviceNetworkOk()) {
            //查询详细消息
            CallServer.getRequestInstance().add(CommentActivity.this, getString(R.string.general_submitting), CommParams.HTTP_CHECK_MSG, NoHttpRuquestFactory.message_details(fromUserID + "", userEntity.getUser_id() + ""), HttpCallback1);
        } else {
            MyToast.show(CommentActivity.this, getString(R.string.smssdk_network_error), Toast.LENGTH_LONG);
        }

    }

    private HttpCallback<String> HttpCallback1 = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {

            MyLog.e(TAG, "====response.get()=====" + response.get());
            DataFromServer dataFromServer = JSONObject.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            if (!(CommonUtils.isStringEmpty(value) || ToolKits.isJSONNullObj(value))) {
                //返回的是一个UserChat对象:
                if (what == CommParams.HTTP_CHECK_MSG) {
                    List<UserChat> userChat = JSONObject.parseArray(value, UserChat.class);
                    for (UserChat commentChat : userChat) {
                        MyLog.e(TAG, "查询未读消息：" + commentChat.getUser_id() + "==to_user_id==" + commentChat.getTo_user_id() + "==getComments===" + commentChat.getChat_content());
                    }
                    //修改未读为已读参数为数组 此处数组的大小可以根据前一个页面传递过来的未读数确定（后期修改）
                    String[] strings = new String[userChat.size()];
                    for (int i = 0; i < userChat.size(); i++) {
                        strings[i] = userChat.get(i).getChat_id();
                        //添加到显示列表
                        commentlist.add(new CommentChat(userChat.get(i).getTo_user_id(), userChat.get(i).getUser_id(), userChat.get(i).getChat_content(), userChat.get(i).getChat_time(), "0"));
                        //存储到本地
                        Comment_Util.InsertData(userChat.get(i).getTo_user_id(), userChat.get(i).getUser_id(), userChat.get(i).getChat_content(), userChat.get(i).getChat_time(), "0");
                    }

                    // 得到userChat后设置消息已读
                    CallServer.getRequestInstance().add(CommentActivity.this, getString(R.string.general_submitting), CommParams.HTTP_CHANGE_MSG_READ, NoHttpRuquestFactory.change_unread(strings), HttpCallback1);
                } else if (what == CommParams.HTTP_CHANGE_MSG_READ) {
//                    设置消息已读返回结果后不做处理
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.e(TAG, "访问数据失败!");
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    public void onRefresh() {
        MyLog.e(TAG, "============执行刷新操作================");
        //按照新接口实现，需要在刷新时获取存储在本地的聊天数据
        commentlist.clear();
//执行刷新操作直接从数据库中读取数据显示在界面
        ArrayList<CommentChat> list = Comment_Util.Query(userEntity.getUser_id(), fromUserID);
        commentlist.addAll(list);
        MyLog.e(TAG, "list.toString==" + commentlist.toString());
        if (comein_comment == 1)
            QueryFeedbackReply();
        else
            QueryMymessage();
//        把集合放到设配器中  显示数据
//        commentAdapter.addItem(commentlist);
        commentAdapter = new CommentAdapter(CommentActivity.this, commentlist);
        mRecyclerView.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
    }


}

//}
