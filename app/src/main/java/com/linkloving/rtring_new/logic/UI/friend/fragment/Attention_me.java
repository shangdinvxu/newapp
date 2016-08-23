package com.linkloving.rtring_new.logic.UI.friend.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.logic.UI.friend.AttentionMeAdapter;
import com.linkloving.rtring_new.logic.UI.friend.AttentionUser;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by leo.wang on 2016/3/14.
 */
public class Attention_me extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //网络请求的what
    public final static int REQUEST_GET_ATTENTION_ME=10;

    public final static String TAG = Attention_me.class.getSimpleName();

    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;

    LinearLayoutManager mLayoutManager;

    private AttentionMeAdapter adapter;

    private List<AttentionUser> list = new ArrayList<AttentionUser>();

    private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflateAndSetupView(inflater, container, savedInstanceState, R.layout.fragment_my_attention);

    }

    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);

        mSwipeRefreshLayout= (SwipeRefreshLayout) layout.findViewById(R.id.swipe_refresh_widget);

        mRecyclerView= (RecyclerView) layout.findViewById(R.id.my_attention_recyclerView);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.black);

        //进入页面的时候加载进度条
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mLayoutManager=new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        //添加分隔线

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter=new AttentionMeAdapter(getContext(),list);

        mRecyclerView.setAdapter(adapter);

        return layout;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(this);//设置监听

        mSwipeRefreshLayout.setRefreshing(true);
        getAttentionme();

    }

    @Override
    public void onRefresh() {

        getAttentionme();

    }

    private HttpCallback<String> httpCallback=new HttpCallback<String>(){

        @Override
        public void onSucceed(int what, Response<String> response) {

            MyLog.i(TAG, "onSucceed");


            mSwipeRefreshLayout.setRefreshing(false);
            if(CommonUtils.isStringEmpty(response.get())){
                return;
            }
            MyLog.i(TAG, "response=" + JSON.toJSONString(response.get()));

            if(CommonUtils.isStringEmpty(response.get().toString())){
                return;
            }

            list.clear();

            JSONObject object=JSON.parseObject(response.get());

            String value=object.getString("returnValue");

            MyLog.i(TAG, value);

            List<AttentionUser> temp=JSON.parseArray(value,AttentionUser.class);
            if(temp.isEmpty())
            {
                return;
            }
            else
            {
              adapter.addItem(temp);
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

            MyLog.i(TAG,"onFailed");

            mSwipeRefreshLayout.setRefreshing(false);

        }
    };


    private void getAttentionme(){

        String userId = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id()+"";

        String userTime = sdf.format(new Date());

        CallServer.getRequestInstance().add(getContext(), false,REQUEST_GET_ATTENTION_ME, NoHttpRuquestFactory.create_Attention_Me_Request(userId, userTime), httpCallback);

    }

    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setRefreshing(true);
        getAttentionme();
    }

    /*
   private class ConcernMeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private LayoutInflater mInflater;

        private List<AttentionUser> list = new ArrayList<AttentionUser>();

        Context context;

        public ConcernMeAdapter(Context context, List<AttentionUser> list) {

            this.context=context;
            this.list=list;

            this.mInflater=LayoutInflater.from(context);


        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }*/
}
