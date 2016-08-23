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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.logic.UI.friend.AttentionUser;
import com.linkloving.rtring_new.logic.UI.friend.SampleAdapyer;
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
 *
 * 我的关注页面
 */
public class My_attention extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //网络请求的what
    public final static int REQUEST_GET_MY_ATTENTION=14;

    public final static String TAG = My_attention.class.getSimpleName();

    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;
    LinearLayout linearLayout_ent_follw;
    TextView ent_name;
   // int lastVisibleItem;

    SampleAdapyer adapter;

    LinearLayoutManager mLayoutManager;

    private List<AttentionUser> list = new ArrayList<AttentionUser>();

    private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflateAndSetupView(inflater, container, savedInstanceState, R.layout.fragment_my_attention);

    }

    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);

        mSwipeRefreshLayout= (SwipeRefreshLayout) layout.findViewById(R.id.swipe_refresh_widget);

        mRecyclerView= (RecyclerView) layout.findViewById(R.id.my_attention_recyclerView);
       /* if(!CommonUtils.isStringEmpty(MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getEntEntity().getEnt_id())){
            linearLayout_ent_follw= (LinearLayout) layout.findViewById(R.id.linearLayout_ent_follw);
            linearLayout_ent_follw.setVisibility(View.VISIBLE);
            ent_name= (TextView) layout.findViewById(R.id.ent_name);
            ent_name.setText(MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getEntEntity().getEnt_name());
        }*/
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.black);
        //进入页面的时候加载进度条
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mLayoutManager=new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //添加分隔线

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter=new SampleAdapyer(getContext(),list);

        mRecyclerView.setAdapter(adapter);

        return layout;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(this);//设置监听

        mSwipeRefreshLayout.setRefreshing(true);


        getMyattention();



      /*  mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<String> newDatas = new ArrayList<String>();
                            for (int i = 0; i < 5; i++) {
                                int index = i + 1;
                                newDatas.add("more item" + index);
                            }

                            adapter.addMoreItem(newDatas);

                            //adapter.changeMoreStatus(SampleAdapyer.PULLUP_LOAD_MORE);
                        }
                    }, 2500);

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });*/


    }


        private HttpCallback<String> httpCallback=new HttpCallback<String>(){

            @Override
            public void onSucceed(int what, Response<String> response) {

                MyLog.i(TAG, "onSucceed");

                mSwipeRefreshLayout.setRefreshing(false);

                MyLog.i(TAG, "response=" + JSON.toJSONString(response.get()));

                if(CommonUtils.isStringEmpty(response.get().toString())){
                    return;
                }

                list.clear();

                JSONObject object=JSON.parseObject(response.get());

                String value=object.getString("returnValue");

                MyLog.i(TAG, "returnValue"+value);

                List<AttentionUser> temp=JSON.parseArray(value,AttentionUser.class);

                MyLog.i(TAG,"temp:"+JSON.toJSONString(temp));

                MyLog.i(TAG, "what=" + what + "");

                if(temp.isEmpty())

                {
                    return;
                }

                else
                {
                   // list.addAll(temp);

                    adapter.addItem(temp);

                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

                MyLog.i(TAG,"onFailed");

                mSwipeRefreshLayout.setRefreshing(false);

            }
        };

        private void getMyattention(){
        String userId = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id()+"";
        String userTime = sdf.format(new Date());
        CallServer.getRequestInstance().add(getContext(), false,REQUEST_GET_MY_ATTENTION, NoHttpRuquestFactory.create_My_Attention_Request(userId, userTime), httpCallback);

    }
    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setRefreshing(true);
        getMyattention();
    }

    @Override
    public void onRefresh() {
        //加载网络请求数据,刷新的时候去加载数据

        MyLog.i(TAG, "正在刷新");

        getMyattention();

     /*   new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> newDatas = new ArrayList<String>();
                for (int i = 0; i <5; i++) {
                    int index = i + 1;
                    newDatas.add("new item" + index);
                }
                adapter.addItem(newDatas);

                mSwipeRefreshLayout.setRefreshing(false);

                Toast.makeText(getActivity(), "更新了五条数据...", Toast.LENGTH_SHORT).show();
            }
        }, 5000);
    }
*/
    }

}
