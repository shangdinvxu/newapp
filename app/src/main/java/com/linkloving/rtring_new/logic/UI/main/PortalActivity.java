package com.linkloving.rtring_new.logic.UI.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jaeger.library.StatusBarUtil;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.basic.CustomProgressBar;
import com.linkloving.rtring_new.db.sport.UserDeviceRecord;
import com.linkloving.rtring_new.db.summary.DaySynopicTable;
import com.linkloving.rtring_new.db.weight.UserWeight;
import com.linkloving.rtring_new.db.weight.WeightTable;
import com.linkloving.rtring_new.http.HttpHelper;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.customerservice.serviceItem.Feedback;
import com.linkloving.rtring_new.logic.UI.device.DeviceActivity;
import com.linkloving.rtring_new.logic.UI.device.FirmwareDTO;
import com.linkloving.rtring_new.logic.UI.main.boundwatch.BoundActivity;
import com.linkloving.rtring_new.logic.UI.main.datachatactivity.WeightActivity;
import com.linkloving.rtring_new.logic.UI.main.materialmenu.Left_viewVO;
import com.linkloving.rtring_new.logic.UI.main.materialmenu.MenuNewAdapter;
import com.linkloving.rtring_new.logic.UI.main.materialmenu.MenuVO;
import com.linkloving.rtring_new.logic.UI.main.update.UpdateClientAsyncTask;
import com.linkloving.rtring_new.logic.UI.more.MoreActivity;
import com.linkloving.rtring_new.logic.dto.SportRecordUploadDTO;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.notify.NotificationCollectorService;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.LocalInfoVO;
import com.linkloving.rtring_new.prefrences.devicebean.ModelInfo;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.HttpUtils;
import com.linkloving.rtring_new.utils.PermissionUtil;
import com.linkloving.rtring_new.utils.ScreenUtils;
import com.linkloving.rtring_new.utils.SwitchUnit;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.UnitTookits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.utils.manager.AsyncTaskManger;
import com.linkloving.rtring_new.utils.sportUtils.SportDataHelper;
import com.linkloving.rtring_new.utils.sportUtils.TimeUtils;
import com.linkloving.utils.TimeZoneHelper;
import com.lnt.rechargelibrary.impl.BalanceCallbackInterface;
import com.lnt.rechargelibrary.impl.BalanceUtil;
import com.lnt.rechargelibrary.impl.RechargeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yolanda.nohttp.Response;
import com.zhy.autolayout.AutoLayoutActivity;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PortalActivity extends AutoLayoutActivity implements MenuNewAdapter.OnRecyclerViewListener{
    private static final String TAG = PortalActivity.class.getSimpleName();

    private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
    private static final int REQUSET_FOR_PERSONAL = 1;
    private static final int LOW_BATTERY = 3;
    private static final int JUMP_FRIEND_TAG_TWO = 2;
    ViewGroup contentLayout;
    @InjectView(R.id.drawer_layout) DrawerLayout drawer;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.recycler_view) RecyclerView menu_RecyclerView;

    @InjectView(R.id.user_head) ImageView user_head;    //头像
    @InjectView(R.id.device_img) ImageView device_img;
    @InjectView(R.id.user_name) TextView user_name;     //昵称

    @InjectView(R.id.text_battery) TextView text_Battery;
    @InjectView(R.id.text_wallet) TextView text_Wallet;
    @InjectView(R.id.text_sync) TextView text_sync;
    @InjectView(R.id.text_step) TextView text_Step;
    @InjectView(R.id.text_distance) TextView text_Distance;
    @InjectView(R.id.text_cal) TextView text_Cal;
    @InjectView(R.id.text_run) TextView text_Run;
    @InjectView(R.id.text_sleep) TextView text_Sleep;
    @InjectView(R.id.text_weightmy) TextView MyWeight;
    @InjectView(R.id.text_weightgoal) TextView GoalWeight;
    /**进度条系列*/
    @InjectView(R.id.progressBar_battery) CustomProgressBar Battery_ProgressBar;
    @InjectView(R.id.progressBar_step) CustomProgressBar Step_ProgressBar;
    @InjectView(R.id.progressBar_distance) CustomProgressBar Distance_ProgressBar;
    @InjectView(R.id.progressBar_cal) CustomProgressBar Cal_ProgressBar;
    @InjectView(R.id.progressBar_run) CustomProgressBar Run_ProgressBar;
    @InjectView(R.id.progressBar_sleep) CustomProgressBar Sleep_ProgressBar;
    /**ITEM布局系列*/
    @InjectView(R.id.linear_date) LinearLayout date;
    @InjectView(R.id.linear_unbund) LinearLayout linear_unbund;
    @InjectView(R.id.linear_step) LinearLayout linear_Step;
    @InjectView(R.id.linear_distance) LinearLayout linear_Distance;
    @InjectView(R.id.linear_cal) LinearLayout linear_Cal;
    @InjectView(R.id.linear_run) LinearLayout linear_Run;
    @InjectView(R.id.linear_sleep) LinearLayout linear_Sleep;
    @InjectView(R.id.linear_battery) LinearLayout linear_Battery;
    @InjectView(R.id.linear_wallet) LinearLayout linear_wallet;
    @InjectView(R.id.linear_weight) LinearLayout linear_Weight;
    @InjectView(R.id.text_time) TextView time;
    //下拉刷新
    @InjectView(R.id.mainScrollView) PullToRefreshScrollView mScrollView;
    //修改日期左右的按钮
    @InjectView(R.id.leftBtn) Button btnleft;
    @InjectView(R.id.rightBtn) Button btnright;
    private MenuNewAdapter menuAdapter;
    private ProgressDialog progressDialog;
    //** 地理位置
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private String User_avatar_file_name;

    private UserEntity userEntity;
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    private BLEProvider provider;
    //获取钱包详情专用
    private LPDeviceInfo deviceInfo = new LPDeviceInfo();
    String startDateString;
    String endDateString;
    String timeNow;
    boolean isReadingCard = false; //防止主界面没读卡完毕用户就点击金额导致读取失败
    //目标值
    private int step_goal, distace_goal, cal_goal, runtime_goal;
    private float sleeptime_goal, weight_goal;

    /**
     * 下拉同步ui的超时复位延迟执行handler （防止意外情况下，一直处于“同步中”的状态）
     */
    private Handler mScrollViewRefreshingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mScrollView.isRefreshing())
                mScrollView.onRefreshComplete();
            super.handleMessage(msg);
        }
    };
    Runnable mScrollViewRefreshingRunnable = new Runnable() {
        @Override
        public void run() {
            Message ms = new Message();
            mScrollViewRefreshingHandler.sendMessageDelayed(ms, 15000);
        }
    };

    /**
     * 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM)
     */
    private AsyncTask<Object, Object, DaySynopic> currentDataAsync = null;

    @Override
    protected void onPause() {
        super.onPause();
        if(provider.getBleProviderObserver()!=null){
            provider.setBleProviderObserver(null);
        }
    }

    @Override
    protected void onPostResume() {
        MyLog.e(TAG, "onPostResume()了");
        super.onPostResume();
        provider = BleService.getInstance(PortalActivity.this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(bleProviderObserver);

        UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();

        if (userEntity == null || userEntity.getDeviceEntity() == null || userEntity.getUserBase() == null)
            return;
        MyLog.e(TAG, "u.getDeviceEntity().getDevice_type():" + userEntity.getDeviceEntity().getDevice_type());
        //判断下要隐藏哪些
        refreshVISIBLE();
        //运动目标重置一下
        initGoal();
        getInfoFromDB();
        if(!CommonUtils.isStringEmpty(userEntity.getDeviceEntity().getLast_sync_device_id())){
            if(provider.isConnectedAndDiscovered())
                BleService.getInstance(PortalActivity.this).syncAllDeviceInfoAuto(PortalActivity.this, false, null);
        }

        if (userEntity.getUserBase().getUser_avatar_file_name() == null){
            MyLog.e(TAG, "u.getUserBase().getUser_avatar_file_name()是空的........");
            return;
        }

        if (!userEntity.getUserBase().getUser_avatar_file_name().equals(User_avatar_file_name) || !userEntity.getUserBase().getNickname().equals(userEntity.getUserBase().getNickname())) {
            refreshHeadView();
        }
        //刷新企业logo
        refreshEntHead();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        provider.setBleProviderObserver(null);
        AppManager.getAppManager().removeActivity(this);
        // 如果有未执行完成的AsyncTask则强制退出之，否则线程执行时会空指针异常哦！！！
        AsyncTaskManger.getAsyncTaskManger().finishAllAsyncTask();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        MyLog.e(TAG, "onWindowFocusChanged:" + hasFocus);
//        if(hasFocus){
//            ScreenUtils.Dimension dimen1 = ScreenUtils.getAreaOne(this);
//            ScreenUtils.Dimension dimen2 = ScreenUtils.getAreaTwo(this);
//            int top = (int) DensityUtils.px2dp(this,dimen1.mHeight-dimen2.mHeight);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_main);
//        ScreenUtils.applyKitKatTranslucency(this);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.inject(this);
        contentLayout = (ViewGroup) findViewById(R.id.main);
//        COUNT_MODELNAME = 0;
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        // 系统到本界面中，应该已经完成准备好了，开启在网络连上事件时自动启动同步线程的开关吧
        MyApplication.getInstance(this).setPreparedForOfflineSyncToServer(true);
        //初始化百度地图
        initLocation();
        //初始化目标
        initGoal();
        //初始化UI
        initView();
        initListener();
//        refreshVISIBLE();
        getServerInfo();
        //开始定位
        mLocationClient.start();
        // 刷新电量UI
        refreshBatteryUI();
        //自动装载体重数据
        autoInsertWeight();
        //开始时间
        startDateString = TimeUtils.getstartDateTime(0, new Date());
        //结束时间
        endDateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());

        //自动下拉刷新
        mScrollView.autoRefresh();
//        new AsyncTask<Object, Object, SportRecordUploadDTO>() {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected SportRecordUploadDTO doInBackground(Object... params) {
//                // 看看数据库中有多少未同步（到服务端的数据）
//                ArrayList<SportRecord> listdata = com.linkloving.rtring_c_watch.db.sport.UserDeviceRecord.findHistoryAlldata(PortalActivity.this,MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id()+"");
//                for (SportRecord sp:listdata){
//                    MyLog.e("查询出来的原始数据"+sp.toString());
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(SportRecordUploadDTO sportRecordUploadDTO) {
//                super.onPostExecute(sportRecordUploadDTO);
//            }
//        }.execute();

    }
    private void autoInsertWeight() {
        SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
        String nowdateStr = sdf.format(new Date()); //今天的日期 2016-05-04
//        List<UserWeight> weightlist = new ArrayList<>();
//        weightlist.add(new UserWeight("2016-05-01",MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id()+"","60.0"));
//        WeightTable.saveToSqliteAsync(this,weightlist,MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id()+"");

        //去查询数据库最后一条
        UserWeight userWeight = WeightTable.queryWeightByDay(this, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id() + "", null);
        if (userWeight == null) {
            Log.e(TAG, "userWeight是null");
            //查询是null的时候代表一条记录也没有
            return;
        }
        String weight = userWeight.getWeight();
        String lastdateStr = userWeight.getTime();
        if (lastdateStr.equals(nowdateStr)) {
            //最后一天是今天 就什么都不做了
            return;
        }
        Date lastdate = ToolKits.stringToDate(lastdateStr, ToolKits.DATE_FORMAT_YYYY_MM_DD);
        //今天和最后一条记录差距的天数
        int i = ToolKits.getindexfromDate(lastdate, new Date());
        List<String> list = ToolKits.getlistDate(lastdate, i);
        List<UserWeight> weightlist = new ArrayList<>();
        for (String date : list) {
            Log.e(TAG, "data是:" + date);
            weightlist.add(new UserWeight(date, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id() + "", weight));
        }
        WeightTable.saveToSqliteAsync(this, weightlist, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id() + "");
    }


    private void initLocation() {
        mLocationClient = new LocationClient(PortalActivity.this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02 bd09ll bd09
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true); // 返回地址
        mLocationClient.setLocOption(option);
}

    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.general_loading));

        timeNow = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());
        time.setText(timeNow);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        Rect outRect = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        setSupportActionBar(toolbar);
        //隐藏title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menu_RecyclerView.setLayoutManager(layoutManager);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);
        //无阴影
        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        contentLayout.setBackgroundDrawable(getResources().getDrawable(R.mipmap.background));
        StatusBarUtil.setTranslucentForDrawerLayout(this, drawer, 0);
        ScreenUtils.setMargins(toolbar,0,ScreenUtils.getStatusHeight(this) ,0,0);

        //侧边栏适配器
        setAdapter();
        refreshHeadView();
        refreshEntHead();
    }

    private void initListener() {
        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                //刷新同步数据
                String s = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id();
                if (CommonUtils.isStringEmpty(s)) {
                   //
                    showBundDialog();
                    mScrollView.onRefreshComplete();
                } else {
                    // 启动超时处理handler
                    // 进入扫描和连接处理过程
                    provider.setCurrentDeviceMac(s);
                    //开始同步
                    BleService.getInstance(PortalActivity.this).syncAllDeviceInfoAuto(PortalActivity.this, false, null);
                    mScrollViewRefreshingHandler.post(mScrollViewRefreshingRunnable);
                }
            }
        });

        btnleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                点击按钮 时间往后一天  刷新界面
                timeNow = ToolKits.getSpecifiedDayBefore(timeNow);
                time.setText(timeNow);
                Date date = ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
                startDateString = TimeUtils.getstartDateTime(0, date);
                endDateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date);
                //查询数据
                getInfoFromDB();
            }
        });

        btnright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击按钮  弹框提示明天还未来临
                if (timeNow.equals(sdf.format(new Date()))) {
                    ToolKits.showCommonTosat(PortalActivity.this, false, ToolKits.getStringbyId(PortalActivity.this, R.string.ranking_wait_tomorrow), Toast.LENGTH_SHORT);
//                    Toast.makeText(PortalActivity.this, getString(R.string.ranking_wait_tomorrow), Toast.LENGTH_LONG).show();
                } else {
                    timeNow = ToolKits.getSpecifiedDayAfter(timeNow);
                    time.setText(timeNow);
                    Date date = ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
                    startDateString = TimeUtils.getstartDateTime(0, date);
                    endDateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date);
                    //查询数据
                    getInfoFromDB();
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar;// 用来装日期的
                calendar = Calendar.getInstance();
                DatePickerDialog dialog1;
                dialog1 = new DatePickerDialog(PortalActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        Date date = new Date(calendar.getTimeInMillis());
                        endDateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date);
                        if (ToolKits.compareDate(date, new Date())) {
                            ToolKits.showCommonTosat(PortalActivity.this, false, ToolKits.getStringbyId(PortalActivity.this, R.string.ranking_wait_tomorrow), Toast.LENGTH_SHORT);
                        } else {
                            time.setText(endDateString);
                            timeNow = endDateString;
                            MyLog.e(TAG, "riqi" + startDateString + "   " + endDateString);
                            getInfoFromDB();
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog1.show();
            }
        });

        user_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入个人信息.详细页面
                Intent intent = IntentFactory.create_PersonalInfo_Activity_Intent(PortalActivity.this);
                startActivityForResult(intent, REQUSET_FOR_PERSONAL);
            }
        });
    }

    //获取目标值
    private void initGoal() {
        step_goal = Integer.parseInt(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_STEP));
        distace_goal = (int)(Float.parseFloat(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_DISTANCE)));
        cal_goal = (int)(Float.parseFloat(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_CAL)));
        Log.e(TAG,"卡路里是:"+cal_goal);
        runtime_goal = Integer.parseInt(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_RUN));
        sleeptime_goal = Float.parseFloat(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_SLEEP));
        weight_goal = Float.parseFloat(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_WEIGHT));
        if(PreferencesToolkits.getLocalSettingUnitInfo(PortalActivity.this)!=ToolKits.UNIT_GONG){ //公制
            weight_goal = ToolKits.calcKG2LB(weight_goal);
        }
    }

    private void refreshVISIBLE() {
        if (CommonUtils.isStringEmpty(MyApplication.getInstance(this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {
            linear_unbund.setVisibility(View.VISIBLE); //未绑定提示出现
            linear_Battery.setVisibility(View.GONE);
            linear_wallet.setVisibility(View.GONE);
            BleService.getInstance(this).releaseBLE();
        } else {
            //刷新电量
            refreshBatteryUI();
            //绑定时候
            linear_unbund.setVisibility(View.GONE);   //未绑定提示消失
            linear_Battery.setVisibility(View.VISIBLE);
            //手环的时候显示手环 手表显示手表
            UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
            MyLog.e(TAG, "获取modelName："+userEntity.getDeviceEntity().getModel_name());
            if(CommonUtils.isStringEmpty(userEntity.getDeviceEntity().getModel_name())){
                linear_wallet.setVisibility(View.GONE);
                return;
            }
            if (userEntity.getDeviceEntity().getDevice_type()==MyApplication.DEVICE_WATCH) {
                device_img.setImageDrawable(getResources().getDrawable(R.mipmap.device_watch));
                userEntity.getDeviceEntity().setDevice_type(MyApplication.DEVICE_WATCH);
            } else if (userEntity.getDeviceEntity().getDevice_type()==MyApplication.DEVICE_BAND){
                device_img.setImageDrawable(getResources().getDrawable(R.mipmap.bound_band_on));
                userEntity.getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND);
            } else if (userEntity.getDeviceEntity().getDevice_type()==MyApplication.DEVICE_BAND_VERSION3) {
                device_img.setImageDrawable(getResources().getDrawable(R.mipmap.bound_3_on));
                userEntity.getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND_VERSION3);
            }
            ModelInfo modelInfo = PreferencesToolkits.getInfoBymodelName(PortalActivity.this,userEntity.getDeviceEntity().getModel_name());
            if(modelInfo!=null){
                if(modelInfo.getFiscard()==0){ //不支持金融卡
                    linear_wallet.setVisibility(View.GONE);
                }else{
                    linear_wallet.setVisibility(View.VISIBLE);
                }
            }else{
                //modelInfo是null的时候
                CallServer.getRequestInstance().add(PortalActivity.this, false,CommParams.HTTP_UPDATA_MODELNAME, NoHttpRuquestFactory.createModelRequest(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id(),userEntity.getDeviceEntity().getModel_name()), httpCallback);
            }
        }
    }

    private void getServerInfo() {
        /**
         * 查询是否有反馈信息
         */
        CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_FEEDBACK_QUERY, NoHttpRuquestFactory.queryfeedbackMsg(userEntity.getUser_id() + ""), httpCallback);

        /**
         * 获取消息未读数
         */
        CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_UNREAD_QUERY, NoHttpRuquestFactory.Query_Unread_Request(userEntity.getUser_id() + ""), httpCallback);
    }

    @OnClick(R.id.linear_step)
    void toStep(View view) {
        Intent intent1 = IntentFactory.cteate_StepDataActivityIntent(PortalActivity.this);
        intent1.putExtra("time", time.getText());
        startActivity(intent1);
    }

    @OnClick(R.id.linear_unbund)
    void unBund(View view) {
        if (MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider() == null ||
                CommonUtils.isStringEmpty(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {
            showBundDialog();
        }
    }
    @OnClick(R.id.linear_distance)
    void toDistance(View view) {
        Intent intent = IntentFactory.cteate_DiatanceDataActivityIntent(PortalActivity.this);
        intent.putExtra("time", time.getText());
        startActivity(intent);
    }

    @OnClick(R.id.linear_cal)
    void toCal(View view) {
        Intent intent = IntentFactory.cteate_CalDataActivityIntent(PortalActivity.this);
        intent.putExtra("time", time.getText());
        startActivity(intent);
    }

    @OnClick(R.id.linear_sleep)
    void toSleep(View view) {
        Intent intent = IntentFactory.cteate_SleepDataActivityIntent(PortalActivity.this);
        intent.putExtra("time", time.getText());
        startActivity(intent);
    }

    @OnClick(R.id.linear_wallet)
    void toWallet(View view) {
        userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        if (userEntity == null || CommonUtils.isStringEmpty(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {
            showBundDialog();
        } else {
            ModelInfo modelInfo = PreferencesToolkits.getInfoBymodelName(PortalActivity.this,userEntity.getDeviceEntity().getModel_name());
            if(modelInfo!=null) {
                if (modelInfo.getFiscard() == 0) { //不支持金融卡
                    Snackbar.make(drawer, getString(R.string.pay_no_function), Snackbar.LENGTH_SHORT).setAction("Dismiss", null).show();
                } else {

                    if (provider.isConnectedAndDiscovered()) {
                        if(isReadingCard){
                            Snackbar.make(drawer, getString(R.string.pay_isreading), Snackbar.LENGTH_SHORT).setAction("Dismiss", null).show();
                        }else{
                            startActivity(IntentFactory.start_WalletActivityIntent(PortalActivity.this));
                        }
                    } else {
                        Toast.makeText(PortalActivity.this, getString(R.string.pay_no_connect), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @OnClick(R.id.linear_battery)
    void toBattery(View view) {
        userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        if (userEntity == null || CommonUtils.isStringEmpty(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {
            showBundDialog();
        } else {
//            ModelInfo modelInfo = PreferencesToolkits.getInfoBymodelName(PortalActivity.this,userEntity.getDeviceEntity().getModel_name());
//            if(modelInfo!=null){
                startActivity(IntentFactory.star_DeviceActivityIntent(PortalActivity.this,0));
//            }else{
//                ToolKits.showCommonTosat(PortalActivity.this, true, ToolKits.getStringbyId(PortalActivity.this, R.string.device_info_no), Toast.LENGTH_SHORT);
//
//            }

        }
    }

    @OnClick(R.id.linear_weight)
    void toWeight(View view) {
        Intent intent=new Intent(PortalActivity.this, WeightActivity.class);
        intent.putExtra("time", time.getText());
        startActivity(intent);
    }

    /**
     * 实现GPS定位回调监听。
     */
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (PortalActivity.this != null) {
//                if (null != location && location.getLocType() != BDLocation.TypeServerError) {
//
//                }
//                Toast.makeText(PortalActivity.this,"当前经纬度："+location.getLatitude() +"=========="+location.getLongitude(),Toast.LENGTH_LONG).show();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String city = location.getCity();
                UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
                if (userEntity.getUserBase() == null) {
                    return;
                }
                userEntity.getUserBase().setLongitude(longitude + "");
                userEntity.getUserBase().setLatitude(latitude + "");
                CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_SUBMIT_BAIDU, NoHttpRuquestFactory.submit_RegisterationToServer_Modify(userEntity.getUserBase()), httpCallback);
                MyLog.i("百度定位", "MyLocationListener" + longitude + ">>>>" + latitude + "========city:" + city);
                mLocationClient.stop();
            } else {
                mLocationClient.stop();
            }
        }
    }

    /**
     * 刷新用户头像和昵称
     */
    private void refreshHeadView() {

        //图像以后设置
        UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        if (userEntity == null) {
            return;
        }
        MyLog.i(TAG, "获得的UserEntity的名字=" + userEntity.getUserBase().getNickname());
        user_name.setText(userEntity.getUserBase().getNickname());
        User_avatar_file_name = userEntity.getUserBase().getUser_avatar_file_name();
        if (User_avatar_file_name != null) {
            String url = NoHttpRuquestFactory.getUserAvatarDownloadURL(PortalActivity.this, userEntity.getUser_id() + "", userEntity.getUserBase().getUser_avatar_file_name(), true);
            User_avatar_file_name = userEntity.getUserBase().getUser_avatar_file_name();
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .showImageOnFail(R.mipmap.default_avatar)//加载失败显示图片
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                    .build();//构建完成

            ImageLoader.getInstance().displayImage(url, user_head, options, new ImageLoadingListener() {

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
        }

    }

    // 刷新企业图标
    private  void refreshEntHead(){
        //刷新企业图标
        //企业定制的时候需要动态更换
        if(!CommonUtils.isStringEmpty(MyApplication.getInstance(this).getLocalUserInfoProvider().getEntEntity().getEnt_id())){
            String imageUrl=NoHttpRuquestFactory.getEntAvatarDownloadURL(MyApplication.getInstance(this).getLocalUserInfoProvider().getEntEntity().getPortal_logo_file_name());
            ImageLoader.getInstance().loadImage(imageUrl,new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    MyLog.i(TAG, imageUri);
                   Resources res = getResources();
//                    Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.logo);
                    Drawable drawable =new BitmapDrawable(res,loadedImage);
                    toolbar.setLogo(drawable);
                }
            });
        }else {
            MyLog.i(TAG,"获得的getEntEntity是空的");
            Resources res = getResources();
            Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.new_logo);
            Drawable drawable =new BitmapDrawable(res,bmp);
            toolbar.setLogo(drawable);
        }
    }

    /**
     * 侧滑栏适配器
     */
    private void setAdapter() {
        List<MenuVO> list = new ArrayList<MenuVO>();
        for (int i = 0; i < Left_viewVO.menuIcon.length; i++) {
            MenuVO vo = new MenuVO();
            vo.setImgID(Left_viewVO.menuIcon[i]);
            vo.setTextID(Left_viewVO.menuText[i]);
            list.add(vo);
        }
        menuAdapter = new MenuNewAdapter(this, list);
        menuAdapter.setOnRecyclerViewListener(this);
        menu_RecyclerView.setAdapter(menuAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            MoreActivity.ExitApp(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.portal, menu);
        return true;
    }

    /**
     * toolbar右上角的点击选项
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        int num = MyApplication.getInstance(this).getCommentNum();
        if (id == R.id.action_settings) {
            MyLog.e(TAG, "点击了设置");

//            BleService.getInstance(PortalActivity.this).getCurrentHandlerProvider().SetDeviceTime(PortalActivity.this);
//            if(num > 0){
            item.setTitle(ToolKits.getUnreadString(num));
            //跳转到评论页面
//                Intent intent =new Intent(PortalActivity.this,FriendActivity.class);
//                startActivity(intent);
            IntentFactory.start_FriendActivity(PortalActivity.this, JUMP_FRIEND_TAG_TWO);
//            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //侧边栏点击事件 可以在这里复写 暂时没用到
    @Override
    public void onItemClick(int position) {

    }

    //刷新手表电量  + 获取存在数据库的数据
    private void getInfoFromDB() {
        //子线程去计算汇总数据
        MyLog.e(TAG, "====================开始执行异步任务====================");
        AsyncTask<Object, Object, DaySynopic> dataasyncTask = new AsyncTask<Object, Object, DaySynopic>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                if (progressDialog != null && !progressDialog.isShowing())
//                    progressDialog.show();
            }

            @Override
            protected DaySynopic doInBackground(Object... params) {
                DaySynopic mDaySynopic = null;
                if (timeNow.equals(sdf.format(new Date()))) {
                    ArrayList<DaySynopic> mDaySynopicArrayList = new ArrayList<DaySynopic>();
                    MyLog.e(TAG, "endDateString:" + endDateString);
                    //今天的话 无条件去汇总查询
                    mDaySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(PortalActivity.this, endDateString, endDateString);
                    if (mDaySynopic.getTime_zone() == null) {
                        return null;
                    }
                    MyLog.e(TAG, "daySynopic:" + mDaySynopic.toString());
                    mDaySynopicArrayList.add(mDaySynopic);
                    DaySynopicTable.saveToSqliteAsync(PortalActivity.this, mDaySynopicArrayList, userEntity.getUser_id() + "");
                    /****************今天的步数给到 方便OAD完成后回填步数 的变量里面去****************/
                    //今天的步数给到 方便OAD完成后回填步数 的变量里面去
                    //走路 步数
                    int walkStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_step()), 0));
                    //跑步 步数
                    int runStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_step()), 0));
                    int step = walkStep + runStep;
                    MyApplication.getInstance(PortalActivity.this).setOld_step(step);
                    /****************今天的步数给到 方便OAD完成后回填步数 的变量里面去****************/
                } else {
                    ArrayList<DaySynopic> mDaySynopicArrayList = DaySynopicTable.findDaySynopicRange(PortalActivity.this, userEntity.getUser_id() + "", endDateString, endDateString, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
                    MyLog.e(TAG, "mDaySynopicArrayList:" + mDaySynopicArrayList.toString());
                    //在判断一次,如果得到集合是空,我就去明细表里去查询数据.进行汇总
                    if (mDaySynopicArrayList == null || mDaySynopicArrayList.size() == 0) {
                        mDaySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(PortalActivity.this, endDateString, endDateString);
                        MyLog.e(TAG, "daySynopic:" + mDaySynopic.toString());
                        DaySynopicTable.saveToSqliteAsync(PortalActivity.this, mDaySynopicArrayList, userEntity.getUser_id() + "");
                    } else {
                        mDaySynopic = mDaySynopicArrayList.get(0);
                    }
                }
                return mDaySynopic;
            }

            @Override
            protected void onPostExecute(DaySynopic mDaySynopic) {
                super.onPostExecute(mDaySynopic);
                //=============计算基础卡路里=====START========//
//                if (timeNow.equals(sdf.format(new Date()))) {
//                    int hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
//                    int minute = Integer.parseInt(new SimpleDateFormat("mm").format(new Date()));
//                    cal_base = (int) ((hour * 60 + minute) * 1.15);//当前时间今天的卡路里
//
//                } else {
//                    cal_base = 1656;
//                }
//                //=============计算基础卡路里=====OVER========//
                //查询体重,获得体重的集合
                List<UserWeight> list=WeightTable.queryWeights(PortalActivity.this,userEntity.getUser_id()+"",endDateString,endDateString);
                double weight = 60.0;

                if(list!=null&&list.size()>0){
                    weight=CommonUtils.getScaledDoubleValue(Double.valueOf(list.get(0).getWeight()), 1);
                    if(PreferencesToolkits.getLocalSettingUnitInfo(PortalActivity.this)!=ToolKits.UNIT_GONG){ //公制
                        weight = ToolKits.calcKG2LB((float) weight);
                    }
                }else {
                    weight=0;
                }
                if (mDaySynopic == null) {
                    MyLog.e(TAG, "mDaySynopic空的");
                    refreshView(0, 0, 0, 0, 0, weight);
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                    return;
                }
                //走路 步数
                int walkStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_step()), 0));
                //跑步 步数
                int runStep = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_step()), 0));
                int step = walkStep + runStep;
                //daySynopic:[data_date=2016-04-14,data_date2=null,time_zone=480,record_id=null,user_id=null,run_duration=1.0,run_step=68.0,run_distance=98.0
                // ,create_time=null,work_duration=178.0,work_step=6965.0,work_distance=5074.0,sleepMinute=2.0916666984558105,deepSleepMiute=1.25 gotoBedTime=1460645100 getupTime=1460657160]
                //走路 里程
                int walkDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_distance()), 0));
                //跑步 里程
                int runDistance = (int) (CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_distance()), 0));
                int distance = walkDistance + runDistance;

                //浅睡 小时
                double qianleephour = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getSleepMinute()), 1);
                //深睡 小时
                double deepleephour = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getDeepSleepMiute()), 1);

                double sleeptime = CommonUtils.getScaledDoubleValue(qianleephour + deepleephour, 1);
                //走路 分钟
                double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                //跑步 分钟
                double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);

                double worktime = CommonUtils.getScaledDoubleValue(walktime + runtime, 1);

                int walkcal = ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getWork_distance()),(int) walktime * 60,userEntity.getUserBase().getUser_weight());
                int runcal = ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getRun_distance()),(int) runtime * 60,userEntity.getUserBase().getUser_weight());

//                int runcal = _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration())), (int) runtime * 60, userEntity.getUserBase().getUser_weight());

//                int walkcal = _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance()) / (Double.parseDouble(mDaySynopic.getWork_duration())), (int) walktime * 60, userEntity.getUserBase().getUser_weight());

                MyLog.e(TAG,"runcal1:"+runcal);
                MyLog.e(TAG,"walkcal:"+walkcal);

                int calValue = (int)runcal + (int)walkcal;

//                double speed=(distance)/(worktime*60);

//                int weight1=userEntity.getUserBase().getUser_weight();

//                Log.i(TAG,"distance="+distance+"speed="+speed+"worktime"+worktime*60+"getUser_weight="+userEntity.getUserBase().getUser_weight());

//                double count= CountCalUtil.calculateCalories(distance, (int) worktime * 60, weight1);
//
//                Log.i(TAG,"卡路里"+calValue+"体重="+userEntity.getUserBase().getUser_weight()+"count="+count);
                // 计算卡路里
//                Log.i(TAG,"卡路里"+calValue+"体重="+userEntity.getUserBase().getUser_weight()+"count="+count);
//                //将数据显示在控件上
                refreshView(step, distance, runtime, sleeptime, calValue, weight);

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
            }
        };
        // 确保当前只有一个AsyncTask在运行，否则用户恶心切换会OOM
        if (currentDataAsync != null)
            AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(currentDataAsync, true);

        AsyncTaskManger.getAsyncTaskManger().addAsyncTask(currentDataAsync = dataasyncTask);
        dataasyncTask.execute();
    }

    /**
     * 刷新条目明细 （数值，进度条，百分比）
     *
     * @param step
     * @param distance
     * @param runtime
     * @param sleeptime
     * @param calValue
     */
    private void refreshView(int step, int distance, double runtime, double sleeptime, int calValue, double weight) {
        MyLog.e(TAG, step + " " + distance + " " + runtime + " " + sleeptime + " " + weight);
        MyLog.e(TAG, step_goal + " " + distace_goal + " " + runtime_goal + " " + sleeptime_goal + " " + weight_goal);
        refreshMoneyView();
        if (SwitchUnit.getLocalUnit(PortalActivity.this) == ToolKits.UNIT_GONG) {
            text_Distance.setText(distance + getResources().getString(R.string.unit_m));
            MyWeight.setText(weight + getResources().getString(R.string.unit_kilogramme));
        } else {
            text_Distance.setText(UnitTookits.MChangetoMIRate(distance) + getResources().getString(R.string.unit_mile));
            MyWeight.setText(weight + getResources().getString(R.string.unit_pound));
        }
        text_Step.setText(step + getResources().getString(R.string.unit_step));

        //  ext_Cal= (TextView) findViewById(R.id.text_cal);
        text_Run.setText(runtime + getResources().getString(R.string.unit_minute));
        text_Sleep.setText(sleeptime + getResources().getString(R.string.unit_short_hour));
        text_Cal.setText(calValue + getResources().getString(R.string.unit_cal));

        NumberFormat nf=new DecimalFormat( "0.0 ");
        MyWeight.setText(getResources().getString(R.string.weight)+nf.format(weight)+ getResources().getString(R.string.unit_kilogramme));
        GoalWeight.setText(getResources().getString(R.string.change_weight_goal)+nf.format(weight_goal)+ getResources().getString(R.string.unit_kilogramme));
//            MyLog.e(TAG,"Float.parseFloat(money):"+ToolKits.stringtofloat(money));
        //进度条
        Step_ProgressBar.setCurProgress((int) Math.ceil(step * 100 * 1.0f / step_goal));
        Distance_ProgressBar.setCurProgress((int) Math.ceil(distance * 100 * 1.0f / distace_goal));
        Run_ProgressBar.setCurProgress((int) Math.ceil(runtime * 100 * 1.0f / runtime_goal));
        Sleep_ProgressBar.setCurProgress((int) Math.ceil(sleeptime * 100 * 1.0f / sleeptime_goal));
        Cal_ProgressBar.setCurProgress((int) Math.ceil(calValue * 100 * 1.0f / cal_goal));
        refreshBatteryUI();
    }

    /**
     * 单独刷新电量
     */
    private void refreshBatteryUI() {
        if (!CommonUtils.isStringEmpty(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {
            if (provider.isConnectedAndDiscovered()) {  //蓝牙连接上的
                LocalInfoVO LocalInfoVO = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
                if (!LocalInfoVO.userId.equals("-1")) {
                    int battery = LocalInfoVO.getBattery();
                    MyLog.e(TAG, "LocalInfoVO电量:" + LocalInfoVO.getBattery());
                    if (battery < LOW_BATTERY) {
                        //电量低于30的时候 弹出低电量警告框
                        AlertDialog dialog_battery = new AlertDialog.Builder(PortalActivity.this)
                                .setTitle(ToolKits.getStringbyId(PortalActivity.this, R.string.portal_main_battery_low))
                                .setMessage(ToolKits.getStringbyId(PortalActivity.this, R.string.portal_main_battery_low_msg))
                                .setPositiveButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        dialog_battery.show();
                    }
                    text_Battery.setText( MessageFormat.format(getString(R.string.bracelet_battery), battery));//根据电量显示不同的文字提示

                    Battery_ProgressBar.setCurProgress((int) (Math.ceil(battery * 100 * 1.0f / 100)));
//                    text_Battery_Progress.setText((int) (Math.ceil(battery * 100 * 1.0f / 100)) + "%");
                }
            } else if (provider.isConnecting()) { //正在连接
                refreshBattery(PortalActivity.this.getString(R.string.portal_main_state_connecting));
//                Battery_ProgressBar.setIndeterminate(true);
            } else {  //蓝牙未连接上
                refreshBattery(PortalActivity.this.getString(R.string.portal_main_state_unconnect));
//                Battery_ProgressBar.setIndeterminate(false);
            }
        } else {
            //未绑定
            refreshBattery(PortalActivity.this.getString(R.string.portal_main_state_connecting));
//            Battery_ProgressBar.setIndeterminate(true);
        }
    }

    /**
     * 单独刷新电量子方法
     *
     * @param msg
     */
    private void refreshBattery(String msg) {
        //提示词
        text_Battery.setText(msg + " ");//根据电量显示不同的文字提示
        Battery_ProgressBar.setCurProgress((int) (Math.ceil(0 * 100 * 1.0f / 100)));
//        text_Battery_Progress.setText((int) (Math.ceil(0 * 100 * 1.0f / 100)) + "%"); // 0%
    }

    /**
     * 单独刷新钱包子方法
     */
    private void refreshMoneyView(){
        LocalInfoVO localvo = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
        String money = localvo.getMoney();
        //提示词
        text_Wallet.setText(getResources().getString(R.string.menu_pay_yue)+getResources().getString(R.string.menu_pay_yuan) + money);
        text_sync.setText(new SimpleDateFormat(ToolKits.getStringbyId(PortalActivity.this, R.string.portal_main_sync_data)).format(new Date(localvo.getSyncTime())) + "");
    }

    /**
     * 网络请求的返回
     */
    HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            if (result==null){
                return;
            }
            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            if (dataFromServer.getErrorCode() == 1) {
                switch (what) {
                    case CommParams.HTTP_FEEDBACK_QUERY:
                        MyLog.e(TAG, "value=" + value + "CommonUtils.getIntValue(value)" + CommonUtils.getIntValue(value));
                        if (CommonUtils.getIntValue(value) > 0) {
                            AlertDialog dialog = new AlertDialog.Builder(PortalActivity.this)
                                    .setTitle(ToolKits.getStringbyId(PortalActivity.this, R.string.main_about_adv_feedback))
                                    .setMessage(ToolKits.getStringbyId(PortalActivity.this, R.string.portal_main_new_feedback_msg))
                                    .setPositiveButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_look),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    IntentFactory.start_CustomerService_ActivityIntent(PortalActivity.this, Feedback.PAGE_INDEX_ONE);
                                                }
                                            })
                                    .setNegativeButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_cancel), null)
                                    .create();
                            dialog.show();
                        }
                        break;

                    case CommParams.HTTP_UNREAD_QUERY:
                        if (CommonUtils.getIntValue(value) > 0) {
                            //未读消息
                            MyApplication.getInstance(PortalActivity.this).setCommentNum(Integer.parseInt(value));
                        }
                        break;

                    case CommParams.HTTP_SUBMIT_BAIDU:
                        MyLog.e(TAG, "经纬度上传成功...");
                        UserEntity userAuthedInfo = new Gson().fromJson(value, UserEntity.class);
                        MyApplication.getInstance(PortalActivity.this).setLocalUserInfoProvider(userAuthedInfo);
                        refreshVISIBLE();
                        // 后台启动客户端版本检查和更新线程
                        String updateeime=PreferencesToolkits.getUpdateTime(PortalActivity.this);
                        if(updateeime.equals(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()))){
                            //如果是今天就不在提示更新了
                        }else {
                            new UpdateClientAsyncTask(PortalActivity.this) {
                                @Override
                                protected void relogin() {

                                }
                            }.execute();
                        }
                        break;
                    case CommParams.HTTP_UPDATA_CARDNUMBER:
                        MyLog.e(TAG, "卡号上传成功..." );
                        //不做处理
                        break;

                    case CommParams.HTTP_UPDATA_DEVICEID:
                        MyLog.e(TAG, "deviceId上传成功..."+ value);
                        UserEntity userEntity = new Gson().fromJson(value, UserEntity.class);
                        MyApplication.getInstance(PortalActivity.this).setLocalUserInfoProvider(userEntity);
//                        CallServer.getRequestInstance().add(BleService.this, false, CommParams.HTTP_DOWN_USERENETTY, HttpHelper.createUserEntityRequest(userEntity.getUser_id() + ""), httpCallback);
                        break;

                    case CommParams.HTTP_UPDATA_MODELNAME:
                        MyLog.e(TAG, "modelname上传成功...: "+value);
                        if(CommonUtils.isStringEmpty(value)) return;
                        //获取了服务器的设备信息 并且保存到本地
                        String model_name = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getModel_name();
                        MyLog.e(TAG, "modelname上传成功...: "+model_name);
                        ModelInfo modelInfo=JSONObject.parseObject(value, ModelInfo.class);
                        MyLog.e(TAG, "modelname上传成功...: "+modelInfo.getFiscard());
                        PreferencesToolkits.saveInfoBymodelName(PortalActivity.this,model_name,modelInfo);
                        if(modelInfo.getAncs()!=0){ //有消息提醒
                            MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().setDevice_type(MyApplication.DEVICE_WATCH);
                            if(!ToolKits.isEnabled(PortalActivity.this)){
                                startActivity(new Intent(NotificationCollectorService.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                            }
                        }else{
                            MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND);
                        }
                        //开始处理页面是否显示
                        refreshVISIBLE();

                        break;

                    default:
                        break;
                }
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BleService.REQUEST_ENABLE_BT) {
            switch (resultCode) {
                case Activity.RESULT_CANCELED: //用户取消打开蓝牙
                    BleService.setNEED_SCAN(false);
                    break;

                case Activity.RESULT_OK:       //用户打开蓝牙
                    Log.e(TAG, "//用户打开蓝牙");
                    BleService.setNEED_SCAN(true);
                    provider.scanForConnnecteAndDiscovery();
                    break;

                default:
                    break;
            }
            return;
        } else if (requestCode == CommParams.REQUEST_CODE_BOUND && resultCode == Activity.RESULT_OK){
            String type = data.getStringExtra(BundTypeActivity.KEY_TYPE);
            if(type.equals(BundTypeActivity.KEY_TYPE_WATCH)){
                startActivityForResult(new Intent(PortalActivity.this, BoundActivity.class), CommParams.REQUEST_CODE_BOUND_WATCH);
            }else if(type.equals(BundTypeActivity.KEY_TYPE_BAND)){
                startActivityForResult(IntentFactory.startActivityBundBand(PortalActivity.this),CommParams.REQUEST_CODE_BOUND_BAND);
            }else if(type.equals(BundTypeActivity.KEY_TYPE_BAND_VERSION_3)){
                startActivityForResult(IntentFactory.startActivityBundBand3Step1(PortalActivity.this),CommParams.REQUEST_CODE_BOUND_BAND_3);
            }
        }
        else if (requestCode == CommParams.REQUEST_CODE_BOUND_BAND && resultCode == Activity.RESULT_OK) {
            MyLog.e(TAG, "手环绑定成功");
        } else if (requestCode == CommParams.REQUEST_CODE_BOUND_WATCH && resultCode == Activity.RESULT_OK) {
        }
    }

    /**
     * 提示绑定的弹出框
     */
    private void showBundDialog() {

        // 您还未绑定 请您绑定一个设备
//        LayoutInflater inflater = getLayoutInflater();
//        View layoutbund = inflater.inflate(R.layout.modify_sex_dialog, (ViewGroup) findViewById(R.id.linear_modify_sex));
//        final RadioButton band = (RadioButton) layoutbund.findViewById(R.id.rb_left);
//        band.setText(getString(R.string.bound_link_band));
//        final RadioButton watch = (RadioButton) layoutbund.findViewById(R.id.rb_right);
//        watch.setText(getString(R.string.bound_link_watch));
        AlertDialog dialog = new AlertDialog.Builder(PortalActivity.this)
                .setTitle(ToolKits.getStringbyId(PortalActivity.this, R.string.portal_main_unbound))
                .setMessage(ToolKits.getStringbyId(PortalActivity.this, R.string.portal_main_unbound_msg))
                .setPositiveButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IntentFactory.startBundTypeActivity(PortalActivity.this);
                            }
                        })
                .setNegativeButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_cancel), null)
                .create();
        dialog.show();
    }

    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return PortalActivity.this;
        }

        /**********用户没打开蓝牙*********/
        @Override
        public void updateFor_handleNotEnableMsg() {
            //用户未打开蓝牙
            Log.i(TAG, "updateFor_handleNotEnableMsg");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            getActivity().startActivityForResult(enableBtIntent, BleService.REQUEST_ENABLE_BT);
        }

        @Override
        public void updateFor_handleUserErrorMsg(int id) {
            MyLog.e(TAG, "updateFor_handleConnectSuccessMsg");
        }

        /**********BLE连接中*********/
        @Override
        public void updateFor_handleConnecting() {
            //正在连接
            MyLog.e(TAG, "updateFor_handleConnecting");
            refreshBatteryUI();
        }

        /**********扫描BLE设备TimeOut*********/
        @Override
        public void updateFor_handleScanTimeOutMsg() {
            MyLog.e(TAG, "updateFor_handleScanTimeOutMsg");
            if (mScrollView.isRefreshing()){
                mScrollView.onRefreshComplete();
            }
        }

        /**********BLE连接失败*********/
        @Override
        public void updateFor_handleConnectFailedMsg() {
            //连接失败
            MyLog.e(TAG, "updateFor_handleConnectFailedMsg");
            if (mScrollView.isRefreshing()){
                mScrollView.onRefreshComplete();
            }
        }

        /**********BLE连接成功*********/
        @Override
        public void updateFor_handleConnectSuccessMsg() {
            //连接成功
            MyLog.e(TAG, "updateFor_handleConnectSuccessMsg");
        }
        /**********BLE断开连接*********/
        @Override
        public void updateFor_handleConnectLostMsg() {
            MyLog.e(TAG, "updateFor_handleConnectLostMsg");
            //蓝牙断开的显示
            refreshBatteryUI();
            isReadingCard = false;
            if (mScrollView.isRefreshing()){
                mScrollView.onRefreshComplete();
            }
        }

        /**********0X13命令返回*********/
        @Override
        public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
            MyLog.e(TAG, "updateFor_notifyFor0x13ExecSucess_D");
            isReadingCard = true;
            mScrollViewRefreshingHandler.removeCallbacks(mScrollViewRefreshingRunnable);
        }

        @Override
        public void updateFor_notifyForDeviceUnboundSucess_D() {
            MyLog.e(TAG, "updateFor_notifyForDeviceUnboundSucess_D");
        }

        /**********剩余同步运动条目*********/
        @Override
        public void updateFor_SportDataProcess(Integer obj) {
            super.updateFor_SportDataProcess(obj);
            MyLog.e(TAG, "updateFor_SportDataProcess");
            if(mScrollView.isRefreshing()){
                String second_txt = MessageFormat.format(getString(R.string.refresh_data), obj);
                mScrollView.getHeaderLayout().getmHeaderText().setText(second_txt);
            }
        }

        /**********运动记录读取完成*********/
        @Override
        public void updateFor_handleDataEnd() {
            MyLog.e(TAG, " updateFor_handleDataEnd ");
            //把数据库未同步到server的数据提交上去
            if (ToolKits.isNetworkConnected(PortalActivity.this)) {
                new AsyncTask<Object, Object, SportRecordUploadDTO>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected SportRecordUploadDTO doInBackground(Object... params) {
                        // 看看数据库中有多少未同步（到服务端的数据）
                        final ArrayList<SportRecord> up_List = UserDeviceRecord.findHistoryWitchNoSync(PortalActivity.this, MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id() + "");
                        MyLog.e(TAG, "【NEW离线数据同步】一共查询出" +up_List.size()+"条数据");
                        //有数据才去算
                        if (up_List.size() > 0) {
                            SportRecordUploadDTO sportRecordUploadDTO = new SportRecordUploadDTO();
                            final String startTime = up_List.get(0).getStart_time();
                            final String endTime = up_List.get(up_List.size() - 1).getStart_time();
                            sportRecordUploadDTO.setDevice_id("1");
                            sportRecordUploadDTO.setUtc_time("1");
                            sportRecordUploadDTO.setOffset(TimeZoneHelper.getTimeZoneOffsetMinute() + "");
                            sportRecordUploadDTO.setUser_id(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id());
                            sportRecordUploadDTO.setList(up_List);
                            HttpUtils.doPostAsyn(CommParams.SERVER_CONTROLLER_URL_NEW, HttpHelper.sport2Server(sportRecordUploadDTO), new HttpUtils.CallBack() {
                                @Override
                                public void onRequestComplete(String result) {
                                    MyLog.e(TAG, "【NEW离线数据同步】服务端返回" +result);
                                    long sychedNum = UserDeviceRecord.updateForSynced(PortalActivity.this, MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id() + "", startTime, endTime);
                                    MyLog.d(TAG, "【NEW离线数据同步】本次共有" + sychedNum + "条运动数据已被标识为\"已同步\"！[" + startTime + "~" + endTime + "]");
                                }
                            });
                            return sportRecordUploadDTO;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(SportRecordUploadDTO sportRecordUploadDTO) {
                        super.onPostExecute(sportRecordUploadDTO);
                    }
                }.execute();
            }
        }
        /**********消息提醒设置成功*********/
        @Override
        public void updateFor_notify() {
            super.updateFor_notify();
            MyLog.e(TAG, "消息提醒设置成功！");
        }

        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            super.updateFor_notifyForModelName(latestDeviceInfo);
//            if(latestDeviceInfo.modelName==null || latestDeviceInfo.modelName.startsWith("LW100")){
//                if(COUNT_MODELNAME==0){
//                    startActivity(IntentFactory.star_DeviceActivityIntent(PortalActivity.this,DeviceActivity.DEVICE_UPDATE));
//                    COUNT_MODELNAME++;
//                }
//                return;
//            }
            if(latestDeviceInfo.modelName==null)
                latestDeviceInfo.modelName = "LW100" ;
            MyLog.e(TAG, "modelName:" + latestDeviceInfo.modelName);
            MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().setModel_name(latestDeviceInfo.modelName);
            //去服务器获取显示页面的bean
            CallServer.getRequestInstance().add(PortalActivity.this, false,CommParams.HTTP_UPDATA_MODELNAME, NoHttpRuquestFactory.createModelRequest(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id(),latestDeviceInfo.modelName), httpCallback);
//            if((System.currentTimeMillis()/1000)-PreferencesToolkits.getOADUpdateTime(getActivity())>86400)
            {
                // 查询是否要更新固件
                final LocalInfoVO vo = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
                FirmwareDTO firmwareDTO = new FirmwareDTO();
                int deviceType = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getDevice_type();
                if(deviceType ==MyApplication.DEVICE_BAND || deviceType ==MyApplication.DEVICE_BAND - 1){
                    deviceType = 1;
                }else{
                    deviceType = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getDevice_type();
                }
                firmwareDTO.setDevice_type(deviceType);
                firmwareDTO.setFirmware_type(DeviceActivity.DEVICE_VERSION_TYPE);
                int version_int = ToolKits.makeShort(vo.version_byte[1], vo.version_byte[0]);
                firmwareDTO.setVersion_int(version_int + "");
                firmwareDTO.setModel_name(latestDeviceInfo.modelName);
                if(MyApplication.getInstance(PortalActivity.this).isLocalDeviceNetworkOk()){
                    //请求网络
                    CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_OAD, NoHttpRuquestFactory.create_OAD_Request(firmwareDTO), new HttpCallback<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
                            String value = dataFromServer.getReturnValue().toString();
                            if(!CommonUtils.isStringEmpty(response.get())) {
                                if (dataFromServer.getErrorCode() != 10020) {
                                    JSONObject object = JSON.parseObject(value);
                                    String version_code = object.getString("version_code");
                                    int priority = object.getIntValue("priority"); //0为不提示 1只提示 2强制更新
                                    if (Integer.parseInt(version_code, 16) > Integer.parseInt(vo.version, 16)) {
                                        if(priority==1){
                                            AlertDialog dialog = new AlertDialog.Builder(PortalActivity.this)
                                                    .setTitle(ToolKits.getStringbyId(PortalActivity.this, R.string.general_tip))
                                                    .setMessage(ToolKits.getStringbyId(PortalActivity.this, R.string.bracelet_oad_Portal))
                                                    .setPositiveButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_ok),
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    startActivity(IntentFactory.star_DeviceActivityIntent(PortalActivity.this, DeviceActivity.DEVICE_UPDATE));
                                                                }
                                                            })
                                                    .setNegativeButton(ToolKits.getStringbyId(PortalActivity.this, R.string.general_cancel), null)
                                                    .create();
                                            if(System.currentTimeMillis()/1000 -PreferencesToolkits.getOADUpdateTime(PortalActivity.this) > 24*3600 ){
                                                PreferencesToolkits.setOADUpdateTime(PortalActivity.this);
                                                dialog.show();
                                            }
                                        }
                                        if(priority==2){
                                            startActivity(IntentFactory.star_DeviceActivityIntent(PortalActivity.this, DeviceActivity.DEVICE_UPDATE));
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

                        }
                    });
                }
                if (mScrollView.isRefreshing()){
                    mScrollView.onRefreshComplete();
                }
            }
        }

        /**********闹钟提醒设置成功*********/
        @Override
        public void updateFor_notifyForSetClockSucess() {
            super.updateFor_notifyForSetClockSucess();
            MyLog.e(TAG, "updateFor_notifyForSetClockSucess！");
        }

        /**********久坐提醒设置成功*********/
        @Override
        public void updateFor_notifyForLongSitSucess() {
            super.updateFor_notifyForLongSitSucess();
            MyLog.e(TAG, "updateFor_notifyForLongSitSucess！");
        }

        /**********身体信息(激活设备)设置成功*********/
        @Override
        public void updateFor_notifyForSetBodySucess() {
            MyLog.e(TAG, "updateFor_notifyForSetBodySucess");
            refreshBatteryUI();
        }

        /**********设置时间失败*********/
        @Override
        public void updateFor_handleSetTimeFail() {
            MyLog.e(TAG, "updateFor_handleSetTimeFail");
            super.updateFor_handleSetTimeFail();
        }

        /**********设置时间成功*********/
        @Override
        public void updateFor_handleSetTime() {
            MyLog.e(TAG, "updateFor_handleSetTime");
            mScrollView.getHeaderLayout().getmHeaderText().setText(getString(R.string.refresh_time));
            getInfoFromDB();
        }
//        @Override
//        public void updateFor_notifyForDeviceFullSyncSucess_D(LPDeviceInfo deviceInfo) {
//            PreferencesToolkits.updateLocalDeviceInfo(PortalActivity.this, deviceInfo);
//            MyLog.e(TAG, "updateFor_notifyForDeviceFullSyncSucess_D");
//        }

        /**********获取设备ID*********/
        @Override
        public void updateFor_getDeviceId(String obj) {
            super.updateFor_getDeviceId(obj);
            MyLog.e(TAG, "读到的deviceid:" + obj);
            //如果读到的卡号 并且本地还没有设置
            if(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity()==null){
                return;
            }
            if(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getEntEntity()==null || !CommonUtils.isStringEmpty(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getEntEntity().getEnt_name())){
                return;
            }
            if (MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id2()==null || !MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id2().equals(obj) ) {
                /***本地***/
                MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().setLast_sync_device_id2(obj);
                /***云端***/
                CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_UPDATA_DEVICEID,
                        HttpHelper.createUpDeviceIdRequest(MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getUser_id() + "", provider.getCurrentDeviceMac(), obj), httpCallback);
            }
        }

        /**********卡号读取成功*********/
        @Override
        public void updateFor_CardNumber(String cardId) {
            MyLog.e(TAG, "updateFor_CardNumber："+cardId);
            super.updateFor_CardNumber(cardId);
            /*****************拿到卡号后存储过程START*****************/
            UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
            if(userEntity.getDeviceEntity()==null){
                return;
            }
            ModelInfo modelInfo = PreferencesToolkits.getInfoBymodelName(PortalActivity.this,MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider().getDeviceEntity().getModel_name());
            if(modelInfo==null){
                if(!cardId.equals("NO_PAY")){
                    //获取余额
                    getMoneyfromDevice();
                }
            }else{
                if(modelInfo.getFiscard()!=0){ //不是0代表支持金融卡
                    //获取余额
                    getMoneyfromDevice();
                }
            }

            if (userEntity.getDeviceEntity().getCard_number()==null || userEntity.getDeviceEntity().getCard_number().equals("") || !userEntity.getDeviceEntity().getCard_number().equals(cardId)){
                MyLog.e(TAG, "卡号不是空 开始报存卡号:" + cardId);
                userEntity.getDeviceEntity().setCard_number(cardId);
                /***本地***/
                MyApplication.getInstance(PortalActivity.this).setLocalUserInfoProvider(userEntity);
                /***云端***/
                //拿到卡号存到服务器去
                CallServer.getRequestInstance().add(PortalActivity.this, false, CommParams.HTTP_UPDATA_CARDNUMBER, HttpHelper.createUpCardNumberRequest(userEntity.getUser_id() + "", cardId), httpCallback);
                /******************拿到卡号后存储过程OVER*******************/
            }
            mScrollView.onRefreshComplete();
        }

        /**********上电成功*********/
        @Override
        public void updateFor_OpenSmc(boolean isSuccess) {
            super.updateFor_OpenSmc(isSuccess);
            MyLog.e(TAG, "开卡成功！");
            if (isSuccess) {
                provider.AIDSmartCard(PortalActivity.this, deviceInfo);
            }else{
                MyLog.e(TAG, "开卡失败--------------------------------------------");
            }
        }

        /**********AID*********/
        @Override
        public void updateFor_AIDSmc(boolean isSuccess) {
            super.updateFor_AIDSmc(isSuccess);
            if (isSuccess) {
                //读余额
                provider.PINSmartCard(PortalActivity.this, deviceInfo);
            }
        }

        /**********校验PIN*********/
        @Override
        public void updateFor_checkPINSucess_D() {
            super.updateFor_checkPINSucess_D();
            provider.readCardBalance(PortalActivity.this, deviceInfo);
        }

        /**********余额读取成功*********/
        @Override
        public void updateFor_GetSmcBalance(Integer obj) {
            super.updateFor_GetSmcBalance(obj);
            MyLog.e(TAG, "updateFor_GetSmcBalance：");
//            String balance = ToolKits.stringtofloat(obj+"")+"";
//            MyLog.e(TAG,"读出来的余额是:"+balance);
            provider.closeSmartCard(PortalActivity.this);
            //把余额保存到本地 方便主界面显示
            LocalInfoVO localvo = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
            localvo.setMoney(ToolKits.inttoStringMoney(obj));
            PreferencesToolkits.setLocalDeviceInfoVo(PortalActivity.this, localvo);
            //此时调用是为了刷新金额
            refreshMoneyView();
            isReadingCard = false;
        }
    }

    private void getMoneyfromDevice() {
        UserEntity userEntity = MyApplication.getInstance(PortalActivity.this).getLocalUserInfoProvider();
        if (userEntity.getDeviceEntity() == null || userEntity.getDeviceEntity().getCard_number() == null) {
            isReadingCard = false;
            return;
        } else {
            String card = userEntity.getDeviceEntity().getCard_number();
            if (card.startsWith(LPDeviceInfo.SUZHOU_)) {
                MyLog.e(TAG, "刷新===页面数据===和===钱包===");
                deviceInfo.customer = LPDeviceInfo.SUZHOU_;   //苏州
                    if (provider.isConnectedAndDiscovered()) {
                        provider.closeSmartCard(PortalActivity.this);
                        // 首先清空集合
                    provider.openSmartCard(PortalActivity.this);
                }
            }else if(card.startsWith(LPDeviceInfo.LIUZHOU_4) || card.startsWith(LPDeviceInfo.LIUZHOU_5)){
                deviceInfo.customer = LPDeviceInfo.LIUZHOU_4;   //柳州
                if (provider.isConnectedAndDiscovered()) {
                    provider.closeSmartCard(PortalActivity.this);
                    // 首先清空集合
                    provider.openSmartCard(PortalActivity.this);
                }
            }else if(card.startsWith(LPDeviceInfo.LINGNANTONG) ){
                deviceInfo.customer = LPDeviceInfo.LINGNANTONG;   //岭南通
                if (provider.isConnectedAndDiscovered()) {
                    Log.e(TAG, "卡号为岭南通---");
                    //岭南通内嵌读取流程
                    RechargeUtil.setBluetoothBase(PortalActivity.this,provider);
                    BalanceUtil.queryBalance(PortalActivity.this,PortalActivity.this,RechargeUtil.LINKLOVE,provider.getCurrentDeviceMac(),null,false,new BalanceCallbackInterface() {
                        @Override
                        public void onSuccess(String msg, String balance, String cardNum, String thresholdValue) {
                            // TODO Auto-generated method stub
                            Log.e(TAG, "查询余额onSuccess回调msg:"+msg);
                            Log.e(TAG, "查询余额onSuccess回调balance:"+balance);
                            Log.e(TAG, "查询余额onSuccess回调cardNum:"+cardNum);
                            Log.e(TAG, "查询余额onSuccess回调thresholdValue:"+thresholdValue);
                            LocalInfoVO localvo = PreferencesToolkits.getLocalDeviceInfo(PortalActivity.this);
                            localvo.setMoney(balance);
                            PreferencesToolkits.setLocalDeviceInfoVo(PortalActivity.this, localvo);
                            //此时调用是为了刷新金额
                            refreshMoneyView();
                            isReadingCard = false;
                        }

                        @Override
                        public void onFail(String arg0) {
                            // TODO Auto-generated method stub
                        }
                    });
                }
            }else{
                //若都不是
                isReadingCard = false;
            }
        }
    }

    private void requestPermissions(final String permission, final int requestcode) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        if (shouldProvideRationale) {
            Snackbar.make(
                    findViewById(R.id.drawer_layout),
                    "短信时触发一个广播",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(PortalActivity.this, new String[]{permission},requestcode);
                        }
                    })
                    .show();
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(PortalActivity.this,new String[]{permission}, requestcode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtil.REQUEST_PERMISSIONS_BOOK_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.e("checkBLEPermission", "权限已经打开：");
                } else {
                    AppManager.getAppManager().finishAllActivity();
                    System.exit(0);
                }
                break;
            case PermissionUtil.REQUEST_PERMISSIONS_SMS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.e("checkBLEPermission", "权限已经打开：");
                } else {
                    AppManager.getAppManager().finishAllActivity();
                    System.exit(0);
                }
                break;

        }
    }




}
