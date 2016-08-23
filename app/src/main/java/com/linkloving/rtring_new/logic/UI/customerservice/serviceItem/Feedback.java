package com.linkloving.rtring_new.logic.UI.customerservice.serviceItem;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.customerservice.FeedbackAdapter;
import com.linkloving.rtring_new.logic.UI.customerservice.itemBean.FeedbackMsgEntity;
import com.linkloving.rtring_new.logic.UI.customerservice.itemBean.Feedback_Server;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.CommonUtils;
import com.yolanda.nohttp.Response;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by DC on 2016/3/24.
 */
public class Feedback extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public final static String TAG = Feedback.class.getSimpleName();
    public final static int PAGE_INDEX_ONE = 0;
    public final static int PAGE_INDEX_TWO = 1;
    public final static int PAGE_INDEX_THREE = 2;
    private LinkedList<FeedbackMsgEntity> feedback_list_page = new LinkedList<FeedbackMsgEntity>();
    //服务端取出的原始数据
    private List<Feedback_Server> list_json = new LinkedList<Feedback_Server>();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout comments_layout = null;
    private EditText comments_edittext = null;
    private Button comments_sendBtn = null;
    View view;
    Toast toast;
    private FeedbackAdapter feedbackAdapter;
    private int index = 0;

    //listview的显示的页数
    private int page = 1;
    UserEntity userEntity = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflateAndSetupView(inflater, container, savedInstanceState, R.layout.layout_feedback);
    }

    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);
        view =inflater.inflate (R.layout.toast_layout, null);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = getResources().getDimensionPixelSize(resourceId);
        //得到Toast对象
        toast = new Toast(getContext());
        //设置Toast对象的位置，3个参数分别为位置，X轴偏移，Y轴偏
        toast.setGravity(Gravity.TOP, 0, result+200);
        //设置Toast对象的显示时间
        toast.setDuration(Toast.LENGTH_LONG);
        //设置Toast对象所要展示的视图
        toast.setView(view);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.feedback_list_recyclerView);
        comments_edittext = (EditText) layout.findViewById(R.id.main_about_comments_edittext);
        comments_sendBtn = (Button) layout.findViewById(R.id.main_about_comment_btn);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.feedback_swipe_refresh_widget);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.black);

        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
        //添加分割线
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        userEntity = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();

        feedbackAdapter = new FeedbackAdapter(getContext(), feedback_list_page);
        mRecyclerView.setAdapter(feedbackAdapter);


        comments_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送留言
                String content = String.valueOf(comments_edittext.getText()).trim();
                if (CommonUtils.isStringEmpty(content) || content.length() > 200) {
                    comments_edittext.setError(getString(R.string.main_about_send_adv_content));
                    return;
                }
                if(MyApplication.getInstance(getContext()).isLocalDeviceNetworkOk()){
                    sendMail(content);
                }else {
                    MyToast.show(getContext(), getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
                }

                comments_edittext.setText("");
                ToolKits.HideKeyboard(comments_edittext);
            }
        });

        return layout;
    }
    /** 清空所有集合 */
    private void clearall_list(){
        index = 0;
        feedback_list_page.clear();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(this);//设置监听
        mSwipeRefreshLayout.setRefreshing(true);
    }
    @Override
    public void onRefresh() {

        if(index >= (feedback_list_page.size()-1)){

            //显示没有更多记录

        }else{
            page++;
            getchat();
            feedbackAdapter.notifyDataSetChanged();
        }
        QueryFeedbackReply();
    }

    private void sendMail(String content) {

        /******************Json封装*****************/
        try {
            TelephonyManager mTm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            JSONObject json = new JSONObject();
            json.put("user_id", MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id() + "");
            json.put("feedback_content", content);

            JSONObject mobile = new JSONObject();
            mobile.put("Android_VERSION", android.os.Build.VERSION.RELEASE);
            mobile.put("Mobile_BRAND", android.os.Build.BRAND);
            mobile.put("Mobile_MODEL", android.os.Build.MODEL);
            mobile.put("Mobile_Number", mTm.getLine1Number());

            json.put("mobile_desc", mobile.toJSONString());

            //与网络交互，提交反馈信息
            CallServer.getRequestInstance().add(getContext(), false, CommParams.HTTP_FEEDBACK, NoHttpRuquestFactory.submit_Feedback_Info(json.toString()), HttpCallback);
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    private HttpCallback<String> HttpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            mSwipeRefreshLayout.setRefreshing(false);
            String result = response.get();
            DataFromServer dataFromServer = JSONObject.parseObject(result, DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            if (value != null) {
               //显示Toast
                 toast.show();
//    清理列表
                clearall_list();
//                查询客服回复数据
                QueryFeedbackReply();
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.e(TAG, "反馈信息发送失败：" + message);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };
    //查询客服回复数据
private void QueryFeedbackReply(){
    if(MyApplication.getInstance(getContext()).isLocalDeviceNetworkOk()){
        JSONObject json = new JSONObject();
        json.put("user_id", MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id() + "");
        //进入界面先查询客服是否回复
        CallServer.getRequestInstance().add(getContext(), false, CommParams.HTTP_FEEDBACK_REPLY, NoHttpRuquestFactory.submit_Feedback_Reply(json.toString()), HttpCallback1);
    }else{
        MyToast.show(getContext(), getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
    }

}
    private HttpCallback<String> HttpCallback1 = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            mSwipeRefreshLayout.setRefreshing(false);
//            String value = HttpResponse.praseHttpResponseStr(response.get());
            String result = response.get();
            JSONObject object=JSON.parseObject(result);
            String value = object.getString("returnValue");
            MyLog.e(TAG,"返回值：=="+value);
            if(value!=null){

                list_json = JSON.parseArray(value, Feedback_Server.class);

                //获取所有的有效信息
                Collections.reverse(list_json);
                getchat();
                //再拉取5条
                feedbackAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            mSwipeRefreshLayout.setRefreshing(false);
            MyLog.e(TAG,"查询客服回复失败,what="+what+",message="+message+",responseCode="+responseCode);
        }
    };
    private void getchat() {
        //先反转 把最新的数据放到上面     //取出5条
        for(;index<list_json.size();){

            if( index != 0 && ( index % ( 5 * page ) == 0)){
                return;
            }
            //2
            if(Feedback_Server.STATE_NO_RES.equals(list_json.get(index).getFeedback_state())){

                feedback_list_page.addFirst(new FeedbackMsgEntity(userEntity.getUserBase().getNickname(), list_json.get(index).getCreate_time(), list_json.get(index).getFeedback_content(), true));

            }else{
                feedback_list_page.addFirst(new FeedbackMsgEntity("连爱客服", "" , list_json.get(index).getFeedback_result(), false));
                feedback_list_page.addFirst(new FeedbackMsgEntity(userEntity.getUserBase().getNickname(), list_json.get(index).getCreate_time(), list_json.get(index).getFeedback_content(), true));

            }

            index++;

        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            //只有当该Fragment被用户可见的时候,才加载网络数据
            QueryFeedbackReply();
        }else{
            //否则不加载网络数据

        }

    };

}
