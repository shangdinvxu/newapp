package com.linkloving.rtring_new.logic.UI.ranking.rank_fragment;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem.MyRankVO;
import com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem.RankListDTO;
import com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem.RankingAdapter;
import com.linkloving.rtring_new.logic.UI.ranking.rankinglistitem.RankingVO;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.TimeZoneHelper;
import com.yolanda.nohttp.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Linkloving on 2016/3/19.
 */
/*这是排行榜总榜单的页面*/
public class Total_list extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public final static String TAG = Total_list.class.getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    private RankingAdapter mRankAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    boolean last=true;
    int  afterpage=0;
    //倒数第几个开始加载
    private int mCountDown = 1;
    TextView ranking_activity_dateView;
    Button ranking_activity_leftBtn, ranking_activity_rightBtn;
    int pageIndex = 1;
    int lastVisibleItem=1;

    private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
    String userId, userTime;
    String end_datetime_utc;
    String star_datetime_utc;
    List<MyRankVO> myRankVOs = new ArrayList<MyRankVO>();
    MyRankVO myRankVO=new MyRankVO();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userId = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id()+"";
        userTime = sdf.format(new Date());
        star_datetime_utc=sdf.format(new Date());
        end_datetime_utc=getSpecifiedDayAfter(star_datetime_utc);

        return inflateAndSetupView(inflater, container, savedInstanceState, R.layout.rank_fragment_total_list);
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @param layoutResourceId
     * @return
     */
    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.total_list_recyclerView);
        ranking_activity_dateView = (TextView) layout.findViewById(R.id.ranking_activity_dateView);
        ranking_activity_leftBtn = (Button) layout.findViewById(R.id.ranking_activity_leftBtn);
        ranking_activity_dateView.setText(userTime);
        ranking_activity_leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                点击按钮 时间往后一天  刷新界面
                userTime = getSpecifiedDayBefore(userTime);
                end_datetime_utc = star_datetime_utc;
                star_datetime_utc=getSpecifiedDayBefore(star_datetime_utc);
                ranking_activity_dateView.setText(userTime);
                pageIndex=1;
                afterpage=0;
                last=true;
                getDataFromServlet();
            }
        });
        ranking_activity_rightBtn = (Button) layout.findViewById(R.id.ranking_activity_rightBtn);
        ranking_activity_rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击按钮  弹框提示明天还未来临
                if (userTime.equals(sdf.format(new Date()))) {
                    Toast.makeText(getContext(), getString(R.string.ranking_wait_tomorrow), Toast.LENGTH_LONG).show();
                } else {
                    userTime = getSpecifiedDayAfter(userTime);
                    star_datetime_utc=getSpecifiedDayAfter(star_datetime_utc);
                    end_datetime_utc=getSpecifiedDayAfter(star_datetime_utc);
                    ranking_activity_dateView.setText(userTime);
                    pageIndex=1;
                    afterpage=0;
                    last=true;
                    getDataFromServlet();
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.rank_swipe_refresh_widget);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.black);

        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

//添加分割线
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        给RecyclerView添加滑动监听事件
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int loadPos = recyclerView.getAdapter().getItemCount() - mCountDown;
//                //最后一个可见视图在 recyclerview  中的位置
//                int lvvPos = mLayoutManager.findLastVisibleItemPosition();
//                /**
//                 *上拉加载更多,添加脚布局
//                 */
//                //当指定 加载点视图 小于等于最后可见视图，且 向下滑动； 加载机会
//                if (dy > 0 && loadPos <= lvvPos) {
//
//                    MyLog.e(TAG, "监听已滑到底部：pageIndex++=" + pageIndex);
//                    if (last){
//
//                        if(afterpage==pageIndex){
//                            return;
//                        }
//                        getDataFromServlet();
//                    }
//                }
//
//
//            }
//        });
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(this);//设置监听
//        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }
    RecyclerView.OnScrollListener mOnScrollListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);


            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mRankAdapter.getItemCount()) {

                if (last) {
                    MyLog.e("ceshi", "触发时候afterpage= " + afterpage + "pageIndex=" +pageIndex);
                    if (afterpage == pageIndex) {
                        return;
                    }
                    getDataFromServlet();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        }

    };
    @Override
    public void onRefresh() {
        pageIndex=1;
        afterpage=0;
        last=true;
        getDataFromServlet();
    }

    private void getDataFromServlet() {
        /**
         * 请求服务器数据
         */
        afterpage++;
        if(MyApplication.getInstance(getContext()).isLocalDeviceNetworkOk()){
            String ent_id=MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getEntEntity().getEnt_id();
            CallServer.getRequestInstance().add(getContext(),getString(R.string.general_submitting), CommParams.HTTP_RANK, NoHttpRuquestFactory.create_Ranking_Count(MyApplication.getInstance(getContext()).getLocalUserInfoProvider().getUser_id()+"", userTime), httpCallback1);
            MyLog.e(TAG,"时间参数userTime="+userTime);
            //userId ent_id pageIndex
//            RankListDTO rankListDTO=new RankListDTO();
//            rankListDTO.setUser_id(MyApplication.getInstance(getContext()).getLocalUserInfoProvider().getUser_id());
//            rankListDTO.setDate_str(userTime);
//            rankListDTO.setEnd_datetime_utc(TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, end_datetime_utc+ " 00:00:00"));
//            rankListDTO.setStart_datetime_utc(TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, star_datetime_utc + " 00:00:00"));
//            rankListDTO.setPage(pageIndex);
//            CallServer.getRequestInstance().add(getContext(),  getString(R.string.general_submitting), CommParams.HTTP_RANK, NoHttpRuquestFactory.create_Ranking_Count_Basic_Info(rankListDTO), httpCallback);
        }else {
            MyToast.show(getContext(), getString(R.string.general_network_faild), Toast.LENGTH_LONG);
        }

    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            MyLog.e(TAG,"==response==="+response.get());
            JSONObject object = JSON.parseObject(response.get());
            String value = object.getString("returnValue");
            List<RankingVO> temp = JSON.parseArray(value, RankingVO.class);
            MyLog.e(TAG, "================RankingVO.toString=============" + temp.toString());
//            if(temp.isEmpty()){
//                return;
//            }
                if(temp.size()<50){
                last=false;
                }

                if(pageIndex==1){

                if(temp.size()==0){
                        afterpage--;
                        getDataFromServlet();
                }else {
                    pageIndex++;
                    mRankAdapter = new RankingAdapter(getContext(),temp,myRankVOs);
                    mRecyclerView.setAdapter(mRankAdapter);
                }
            }else{
                   pageIndex++;
                   mRankAdapter.addItemMine(myRankVOs);
                   mRankAdapter.addItem(temp);
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            mSwipeRefreshLayout.setRefreshing(false);
            afterpage--;
            MyLog.e(TAG, "请求排行榜失败：" + message);
//            MyToast.show(getContext(), getString(R.string.login_form_error), Toast.LENGTH_SHORT);
        }
    };
    private HttpCallback<String> httpCallback1 = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            MyLog.e(TAG, "请求自身排行成功,即将放入适配器：" + response.get());
            JSONObject object = JSON.parseObject(response.get());
            String value = object.getString("returnValue");
            JSONObject object1 = JSON.parseObject(value);
            if(!CommonUtils.isStringEmpty(object1.toString())){
                MyLog.e(TAG, "请求自身排行成功,即将放入适配器：" + value);
                myRankVO.setDistance(object1.getString("my_steps"));
                myRankVO.setRank(object1.getString("my_ranking"));
                myRankVO.setZan(object1.getString("zan"));
                myRankVOs.add(myRankVO);
                MyLog.e(TAG, "请求自身排行成功：" + value);
            }else {
                MyLog.e(TAG, "自身排行为空：" + value);
                //CallServer.getRequestInstance().add(getContext(), false, CommParams.HTTP_RANK, NoHttpRuquestFactory.create_Ranking_Count(MyApplication.getInstance(getContext()).getLocalUserInfoProvider().getUser_id()+"", userTime), httpCallback1);
                afterpage--;
                getDataFromServlet();
                return;
            }

            mSwipeRefreshLayout.setRefreshing(false);
            RankListDTO rankListDTO=new RankListDTO();
            rankListDTO.setUser_id(MyApplication.getInstance(getContext()).getLocalUserInfoProvider().getUser_id());
            rankListDTO.setDate_str(userTime);
            rankListDTO.setEnd_datetime_utc(TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, end_datetime_utc + " 00:00:00"));
            rankListDTO.setStart_datetime_utc(TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, star_datetime_utc + " 00:00:00"));
            rankListDTO.setPage(pageIndex);
            CallServer.getRequestInstance().add(getContext(), false, CommParams.HTTP_RANK, NoHttpRuquestFactory.create_Ranking_Count_Basic_Info(rankListDTO), httpCallback);
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.e(TAG, "请求自身排行失败：" + message);
            mSwipeRefreshLayout.setRefreshing(false);
            MyToast.show(getContext(), getString(R.string.login_form_error), Toast.LENGTH_SHORT);
        }
    };

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {//可以用new Date().toLocalString()传递参数
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }
    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayAfter(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
                .format(c.getTime());
        return dayAfter;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            //只有当该Fragment被用户可见的时候,才加载网络数据
            if (userTime==null) {
                userTime = sdf.format(new Date());
                star_datetime_utc = sdf.format(new Date());
                end_datetime_utc = getSpecifiedDayAfter(star_datetime_utc);
            }
            afterpage=0;
            pageIndex=1;
            last=true;
            getDataFromServlet();
        }else{
            //否则不加载网络数据

        }

    };
}
