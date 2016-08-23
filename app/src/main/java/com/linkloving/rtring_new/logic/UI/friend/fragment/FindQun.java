package com.linkloving.rtring_new.logic.UI.friend.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.logic.UI.friend.group.SearchGroupAdapter;
import com.linkloving.rtring_new.logic.UI.friend.group.groupmodel.Group;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo.wang on 2016/3/18.
 * <p/>
 * 查找群组
 */
public class FindQun extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;
    private Button find;
    LinearLayoutManager mLayoutManager;
    public final static String TAG = FindQun.class.getSimpleName();

    private String mCondition;//当前的输入内容

    private EditText search_content;
    private int pageIndex = 1;//开始加载第一页
    SearchGroupAdapter adapter;

    private int afterpage = 0;

    int lastVisibleItem;
    private List<Group> list = new ArrayList<Group>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflateAndSetupView(inflater, container, savedInstanceState, R.layout.fragment_find_group);

    }

    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);

        find = (Button) layout.findViewById(R.id.search_btn_group);

        find.setOnClickListener(this);

        search_content = (EditText) layout.findViewById(R.id.search_content_group);


        mRecyclerView = (RecyclerView) layout.findViewById(R.id.my_attention_recyclerView_group);

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
        //添加分隔线
        return layout;
    }

    @Override
    public void onRefresh() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.search_btn_group:

                mRecyclerView.addOnScrollListener(mOnScrollListener);

                String condition = search_content.getEditableText().toString();

                ToolKits.HideKeyboard(search_content);

                if (condition == null || condition.isEmpty()) {

                    MyToast.showShort(getContext(), getString(R.string.relationship_search_condition_empty));

                    return;
                }


                pageIndex = 1;
                afterpage = 0;

                list.clear();

                mCondition = condition;

                getGroup(condition);

                break;

        }
    }

    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (adapter == null) {
                return;
            }

            if (afterpage == pageIndex) {
                return;
            }


            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {

                MyLog.e(TAG, "加载更多newState= " + newState + "lastVisibleItem=" + lastVisibleItem + "adapter.getItemCount()=" + adapter.getItemCount());

                getGroup(mCondition);

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

        }

    };

    //请求网络数据
    private void getGroup(String mCondition) {
if (MyApplication.getInstance(getActivity()).isLocalDeviceNetworkOk()){
        afterpage++;
        CallServer.getRequestInstance().add(getContext(), false, CommParams.REQUEST_GET_GROUP, NoHttpRuquestFactory.create_My_FindQun_Request(mCondition,pageIndex), httpCallback);
        }else{
        MyToast.show(getContext(),"还未连接网络！", Toast.LENGTH_LONG);
        }
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {

            MyLog.e(TAG, "onSucceed");

            //  mSwipeRefreshLayout.setRefreshing(false);

            MyLog.e(TAG, "response=" + JSON.toJSONString(response.get()));
            //不为空开始解析
            JSONObject object = JSON.parseObject(response.get());
            String value = object.getString("returnValue");

            if (CommonUtils.isStringEmpty(value)) {
                //返回是空的且是第一次
                if (pageIndex < 2)
                    MyToast.showShort(getContext(), getString(R.string.relationship_search_empty));
                afterpage--;
                return;
            }

            MyLog.e(TAG, value);
            ArrayList<Group> temp = (ArrayList<Group>) JSON.parseArray(value, Group.class);
            MyLog.e(TAG, "temp:" + JSON.toJSONString(value)+"Group");
            MyLog.e(TAG, "what=" + what + "pageIndex="+pageIndex);

            if (temp.size() < 1) {
                if (pageIndex < 2) {
                    MyToast.showShort(getContext(), getString(R.string.relationship_search_empty));
                    afterpage--;
                } else {
//                    MyToast.showShort(getContext(), "只有这么多了哦");
                    mRecyclerView.removeOnScrollListener(mOnScrollListener);//消除监听
                    return;

                }

            }
            if (pageIndex == 1) {
                //第一次获得数据
                MyLog.e("pageIndex=" + pageIndex, "temp.size()=" + temp.size());
                list = temp;
                MyLog.e(TAG, " list=" + list.size());
                adapter=new SearchGroupAdapter(getActivity(),temp);
                mRecyclerView.setAdapter(adapter);
                //判断要不要隐藏foot
                pageIndex++;

            } else {

                MyLog.e(TAG, "pageIndex=" + pageIndex + "temp.size()=" + temp.size());

                pageIndex++;


                MyLog.e(TAG, "pageIndex=" + pageIndex);

                adapter.addMoreItem(temp);
            }

            if (temp.size() != 50) {

                MyLog.e(TAG, "取消监听");

                mRecyclerView.removeOnScrollListener(mOnScrollListener);//消除监听

            }


        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {


            afterpage--;

            MyLog.e(TAG, "onFailed");

        }
    };
}
