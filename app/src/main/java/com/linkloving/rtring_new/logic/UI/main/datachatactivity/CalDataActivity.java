package com.linkloving.rtring_new.logic.UI.main.datachatactivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.db.summary.DaySynopicTable;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.DateSwitcher;
import com.linkloving.rtring_new.utils.ScreenHotAsyncTask;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.utils.manager.AsyncTaskManger;
import com.linkloving.rtring_new.utils.sportUtils.Datahelper;
import com.linkloving.rtring_new.utils.sportUtils.SportDataHelper;
import com.linkloving.utils.TimeZoneHelper;
import com.linkloving.utils._Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalDataActivity extends ToolBarActivity implements View.OnClickListener {
    private static final String TAG = CalDataActivity.class.getSimpleName();

    private static final int BASE_CAL = 0;
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> dayDataAsync = null;
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> weekDataAsync = null;
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> monthDataAsync = null;

    private CombinedChart combbarChart;
    private UserEntity userEntity;
    private RadioButton dayButton,weekButton,monthButton,day1,day2,day3;
    private ArrayList<BarEntry> mentries;
    private ArrayList<DaySynopic> listData;
    private Map<Integer,View> listView;//存放viewpager的view
    Map<Integer,ArrayList<SportRecord>> data;//日数据查询结果
    Map<Integer,ArrayList<SportRecord>> timenowdata;//用来保存用户所选时间的数据,避免多次查询数据
    String timeNow;
    Date date;
    private ViewPager mViewPager;
    MyPageadapter dayadapter;
    MyPageadapter weekadapter;
    MyPageadapter monthadapter;
    private DateSwitcher weekSwitcher = null;
    private DateSwitcher monthSwitcher= null;
    String startDateString;
    String endDateString;
    public static final int WALKING = 1;
    public static final int RUNNING = 2;
    public final static int ACTIVE = 3;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal_date);

        weekSwitcher = new DateSwitcher(DateSwitcher.PeriodSwitchType.week);
        monthSwitcher= new DateSwitcher(DateSwitcher.PeriodSwitchType.month);

        listView=new HashMap<>();
        pd=new ProgressDialog(this);
        pd.setMessage(getString(R.string.summarizing_data));
        pd.setCanceledOnTouchOutside(false);
        initTitle();//刚刚进页面的时候初始化那三个字段
    }
    @Override
    protected void getIntentforActivity() {

        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        timeNow=getIntent().getStringExtra("time");
        date= ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
        MyLog.i(TAG,"开始时间======="+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
        timenowdata=data=new Datahelper(CalDataActivity.this, String.valueOf(userEntity.getUser_id()), timeNow,
                new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date),false).getMydata();
    }

    private void initchat(CombinedChart barChart) {
        //设置图表的一些属性
        barChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER});
        Legend mLegend = barChart.getLegend();
        // 设置窗体样式
        mLegend.setForm(Legend.LegendForm.CIRCLE);
        mLegend.setEnabled(false);
        // 字体颜色
        mLegend.setTextColor(getResources().getColor(R.color.white));
        // 设置背景
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true); //是否显示X坐标轴及对应的刻度竖线，默认是true
        //x坐标的颜色
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setSpaceBetweenLabels(0);
        //X坐标点描述的颜色
        xAxis.setTextColor(Color.WHITE);
        // 隐藏左Y坐标轴
        barChart.getAxisLeft().setEnabled(false);
        // 隐藏右Y坐标轴
        barChart.getAxisRight().setEnabled(false);
        // 显示表格颜色
        barChart.setGridBackgroundColor(getResources().getColor(R.color.yellow_title));
        // 打开或关闭绘制的图表边框。（环绕图表的线） 最外边环绕的线
        barChart.setDrawBorders(false);
        // 是否显示表格颜色
        barChart.setDrawGridBackground(false);
        // 是否可以拖拽
        barChart.setDragEnabled(false);
        // 是否可以缩放
        barChart.setScaleEnabled(false);
        // 集双指缩放
        barChart.setPinchZoom(false);
        //设置Y方向上动画animateY(int time);
        barChart.animateY(3000);
        //图表描述
        barChart.setDescription("");
        //去除了中间的字体
        barChart.setNoDataText(" ");
        barChart.setNoDataTextDescription("No data(⊙o⊙)");
    }
    @Override
    protected void initView() {
        HideButtonRight(false);
        SetBarTitleText(getString(R.string.unit_cal));
        Button btn = getRightButton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                执行分享操作
                if (ToolKits.isNetworkConnected(CalDataActivity.this)) {
                    String filePath = "/sdcard/CalDataActivity_v.png";
                    new ScreenHotAsyncTask(filePath, CalDataActivity.this).execute(getWindow().getDecorView());
                } else {
                    new AlertDialog.Builder(CalDataActivity.this)
                            .setTitle(ToolKits.getStringbyId(CalDataActivity.this, R.string.share_content_fail))
                            .setMessage(ToolKits.getStringbyId(CalDataActivity.this, R.string.main_more_sycn_fail))
                            .setPositiveButton(R.string.general_ok, null).show();
                }
            }
        });
        btn.setBackground(ContextCompat.getDrawable(CalDataActivity.this, R.mipmap.btn_share));

        combbarChart =(CombinedChart )findViewById(R.id.barChart);

        dayButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_dayRb);
        weekButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_weekRb);
        monthButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_monthRb);
        day1=(RadioButton) findViewById(R.id.day01);
        day2=(RadioButton) findViewById(R.id.day02);
        day3=(RadioButton) findViewById(R.id.day03);
        mViewPager= (ViewPager) findViewById(R.id.my_ViewPager);
        initchat(combbarChart);

    }

    private void initTitle(){
        //日详情
        if(dayButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0),new Date());
            dayadapter=new MyPageadapter(days);
            mViewPager.setAdapter(dayadapter);
            int day=ToolKits.getBetweenDay(date,new Date());
            MyLog.i(TAG, "days=" + days + "  " + day);
           /* if(dayposition==-1){
                //第一次进来
                mViewPager.setCurrentItem(days - day - 1);
            }else {
                mViewPager.setCurrentItem(dayposition);
            }*/
            mViewPager.setCurrentItem(days - day - 1);
        }
        //周数据
        else if(weekButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1>3){
                weekcount=weekcount+1;
            }
            //计算传进来时间与1970/01/01差几周
            int day=ToolKits.getBetweenDay(new Date(0),date);
            int weeknow=day/7;//系统时间1970/01/01与总的周数
            int week2=day%7;
            if(week2>3){
                weeknow=weeknow+1;
            }
            weekadapter=new MyPageadapter(weekcount);
            mViewPager.setAdapter(weekadapter);
            MyLog.i(TAG, "相差几周=" + (weekcount - weeknow));

            mViewPager.setCurrentItem(weeknow - 1);

          /*  if(weekposition==-1){
                //第一次进来
                mViewPager.setCurrentItem(weeknow-1);
            }else {
                MyLog.i(TAG, "相差几周 weekposition=" + weekposition);
                mViewPager.setCurrentItem(weekposition);
            }*/

        }
        //月数据
        else if(monthButton.isChecked()){
            SimpleDateFormat sim1 = new SimpleDateFormat("MM");
//            SimpleDateFormat sim2 = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat sim3 = new SimpleDateFormat("yyyy");
            int year=Integer.parseInt(sim3.format(new Date()));
            int nowyear=Integer.parseInt(sim3.format(date));
            int s1=Integer.parseInt( sim1.format(date));//传进来时间的月份
            int s2=Integer.parseInt(sim1.format(new Date()));//当前时间的月份
            int count=(year-1970)*12+s2+1;
            monthadapter=new MyPageadapter(count);
            mViewPager.setAdapter(monthadapter);

            mViewPager.setCurrentItem((nowyear-1970)*12+s1);//计算出当前时间应该是第几个月

           /* if(monthposition==-1){
                //第一次进来
                mViewPager.setCurrentItem((nowyear-1970)*12+s1);//计算出当前时间应该是第几个月
            }else {
                MyLog.i(TAG, "相差几周 weekposition=" + monthposition);
                mViewPager.setCurrentItem(monthposition);
            }*/

        }
    }

    private CombinedData generateDayBarData() {
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        //横坐标标签
        ArrayList<String> xVals = new ArrayList<String>();
        //查看天时
        for(int i = 0;i < 48 ;i++){
            switch (i){
                case 0:
                    xVals.add("12am");
                    break;
                case 12:
                    xVals.add("6am");
                    break;
                case 24:
                    xVals.add("12pm");
                    break;
                case 36:
                    xVals.add("6pm");
                    break;
                case 47:
                    xVals.add("12am");
                    break;
                default: xVals.add("");
                    break;
            }
        }
        CombinedData data = new CombinedData(xVals);
        if(getDayBarData()!=null)
            data.setData(getDayBarData());
        return data;
    }

    private CombinedData generateWeekBarData() {
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        String s1=getString(R.string.sun);
        String s2=getString(R.string.mon);
        String s3=getString(R.string.tues);
        String s4=getString(R.string.wed);
        String s5=getString(R.string.thurs);
        String s6=getString(R.string.fri);
        String s7=getString(R.string.sat);
        //横坐标标签
        ArrayList<String> xVals = new ArrayList<String>();
        //查看天时
        for(int i = 0;i < 7 ;i++){
            switch (i){
                case 0:
                    xVals.add(s2);
                    break;
                case 1:
                    xVals.add(s3);
                    break;
                case 2:
                    xVals.add(s4);
                    break;
                case 3:
                    xVals.add(s5);
                    break;
                case 4:
                    xVals.add(s6);
                    break;
                case 5:
                    xVals.add(s7);
                    break;
                default: xVals.add(s1);
                    break;
            }
        }
        CombinedData data = new CombinedData(xVals);
        //data.setData(generateLineData());
        if(getWeekBarData()!=null)
            data.setData(getWeekBarData());
        return data;
    }

    private CombinedData generateMonthBarData(String timeNow) {
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        //横坐标标签
        Calendar c = Calendar.getInstance();
        Date date=ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
        c.setTime(date);
        int j=c.getActualMaximum(Calendar.DAY_OF_MONTH);
        MyLog.i(TAG, "这个月多少天" + j);
        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 1;i<=j ;i++){
            switch (i){
                case 1:
                    xVals.add(i-1,i+"");
                    break;
                case 5:
                    xVals.add(i-1,i+"");
                    break;
                case 10:
                    xVals.add(i-1,i+"");
                    break;
                case 15:
                    xVals.add(i-1,i+"");
                    break;
                case 20:
                    xVals.add(i-1,i+"");
                    break;
                case 25:
                    xVals.add(i-1,i+"");
                    break;
                case 30:
                    xVals.add(i-1,i+"");
                    break;
                default:
                    xVals.add(i-1,"");
                    break;
            }
        }
        CombinedData data = new CombinedData(xVals);
        data.setData(getMonthBarData(j));
        return data;
    }

    //获取日数据
    private BarData getDayBarData() {
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        //一天有48条数据,要加上基础值,每个时间段的基础值是不一样的,一天总共是基础值是1656,
        //计算出每半个小时的基础值
        if(data==null){
            //每段时间都是基础值,不加消耗的
            if(day2.getText().equals(getString(R.string.today))){
                //表示当前是今天的数据,此时就要判断当前系统时间了
                //获取当前时间的小时数和分钟数
                SimpleDateFormat s1=new SimpleDateFormat("HH");
                SimpleDateFormat s2=new SimpleDateFormat("mm");
                //找到当前的所在时间的下标
                int h=Integer.parseInt(s1.format(new Date()));
                int m=Integer.parseInt(s2.format(new Date()));
                int callast=0;//最后一段应加上的卡路里
                int last=0;
                if(m>=30){
                    last=h*2+2;
                    callast= 0;
                }else {
                    last = h*2 + 1;
                    callast= 0;
                }
                for(int i=0;i<48;i++){
                    if(i<last-1){
                        //之前的,都直接加上半小时的基础值
                        float cal= 0;
                        entries.add(new BarEntry(cal,i));
                    }
                    else if(i==last-1){
                        //加上当前的基础值
                        float cal= callast;
                        entries.add(new BarEntry(cal,i));

                    }else {
                        //还没有到的时间.肯定不能画数据了
                        entries.add(new BarEntry(0,i));
                    }

                }

            }else {
                float cal= 0;
                for(int i=0;i<48;i++){
                    entries.add(new BarEntry(cal,i));
                }
            }
        }else {
            //取出来的数据不是空的,就要加上运动消耗的
            if(day2.getText().equals(getString(R.string.today))){
                //表示当前是今天的数据,此时就要判断当前系统时间了
                //获取当前时间的小时数和分钟数
                SimpleDateFormat s1=new SimpleDateFormat("HH");
                SimpleDateFormat s2=new SimpleDateFormat("mm");
                //找到当前的所在时间的下标
                int h=Integer.parseInt(s1.format(new Date()));
                int m=Integer.parseInt(s2.format(new Date()));
                int callast=0;//最后一段应加上的卡路里
                int last=0;
                for(int i=0;i<48;i++){
                        //不加,当然也没有数据
                        entries.add(new BarEntry(getcal(data.get(i)),i));
                }
            }else {
                float cal= 0;
                for(int i=0;i<48;i++){
                    entries.add(new BarEntry(getcal(data.get(i))+cal,i));
                }
            }
        }
        mentries=entries;
        addline(R.id.report_page_activity_circleviews_dayRb);
        BarDataSet dataSet;
        dataSet = new BarDataSet(entries, "");
        //柱状图颜色
        dataSet.setColor(getResources().getColor(R.color.yellow_title));
//        dataSet.setColors(getChartColors(CalDataActivity.this));
        //,getResources().getColor(R.color.orangeOver)
//        dataSet.
        //柱形图顶端字是否显示
        dataSet.setDrawValues(false);
        //柱形图顶端字体颜色
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        //柱形图顶端字体大小
        dataSet.setValueTextSize(10f);
        dataSet.setHighlightEnabled(false);
        bardata.addDataSet(dataSet);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return bardata;
    }

    private int getcal(ArrayList<SportRecord> arrayList) {
        int i = 0;
        for (SportRecord s : arrayList) {
            switch (Integer.parseInt(s.getState())) {
                case WALKING:
                    //运动数据里加了一条数据
//                    i = i + _Utils.calculateCalories(Double.parseDouble(s.getDistance())/Double.parseDouble(s.getDuration()), Integer.parseInt(s.getDuration()),userEntity.getUserBase().getUser_weight());
                    i = i + (int)ToolKits.calculateCalories(Float.parseFloat(s.getDistance()),Integer.parseInt(s.getDuration()),userEntity.getUserBase().getUser_weight());
//                    MyLog.e(TAG,"WALKING:"+i);
                    break;
                case RUNNING:
//                    i = i + _Utils.calculateCalories(Double.parseDouble(s.getDistance()) / Double.parseDouble(s.getDuration()), Integer.parseInt(s.getDuration()), userEntity.getUserBase().getUser_weight());
                    i = i + (int)ToolKits.calculateCalories(Float.parseFloat(s.getDistance()),Integer.parseInt(s.getDuration()),userEntity.getUserBase().getUser_weight());
//                    MyLog.e(TAG,"RUNNING:"+i);
                    break;
                case ACTIVE:
//                    i = i + _Utils.calculateCalories(Double.parseDouble(s.getDistance()) / Double.parseDouble(s.getDuration()), Integer.parseInt(s.getDuration()), userEntity.getUserBase().getUser_weight());
                    i = i + (int)ToolKits.calculateCalories(Float.parseFloat(s.getDistance()),Integer.parseInt(s.getDuration()),userEntity.getUserBase().getUser_weight());
//                    MyLog.e(TAG,"ACTIVE:"+i);
                    break;
            }
        }
        MyLog.e(TAG,"总共的i是:"+i);
        return i;
    }

    //获取周数据
    private BarData getWeekBarData() {
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        //一周7条数据
        //判断是不是本周
        if(day2.getText().equals(getString(R.string.thisweek))){
            MyLog.i(TAG,"本周");
            List<Integer> list=new ArrayList<>();
            //如果是今天的数据,则加上的是基础量,是当前时间的
            SimpleDateFormat s1=new SimpleDateFormat("HH");
            SimpleDateFormat s2=new SimpleDateFormat("mm");
            int h=Integer.parseInt(s1.format(new Date()));
            int m=Integer.parseInt(s2.format(new Date()));
            int cal= (int) (( h*60 + m )*(float)(BASE_CAL/24/60));//当前时间今天的卡路里
            MyLog.i(TAG,"当前的卡路里="+cal);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(new Date());
            MyLog.i(TAG, "今天是星期几:" + (calendar.get(Calendar.DAY_OF_WEEK) - 1));
            if(listData!=null&&listData.size()>0){
                for(int i=0;i<listData.size();i++){
                    //遍历从当前数据查出来的本周的数据
                    MyLog.i(TAG, listData.get(i).toString());
                    Calendar c = Calendar.getInstance();
                    Date date=ToolKits.stringToDate(listData.get(i).getData_date(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
                    c.setTime(date);
                    //c.add(Calendar.DATE, -1);
                    DaySynopic mDaySynopic=listData.get(i);
                    //走路 分钟
                    double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                    //跑步 分钟
                    double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);
                    int runcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration())), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
                    int walkcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance()) / (Double.parseDouble(mDaySynopic.getWork_duration())), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                    runcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getRun_distance()),(int) runtime * 60,userEntity.getUserBase().getUser_weight());
                    walkcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getWork_distance()),(int) walktime * 60,userEntity.getUserBase().getUser_weight());
                    int calValue=runcal+walkcal;
//                  int calValue = _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration())), Integer.parseInt(mDaySynopic.getRun_duration()),userEntity.getUserBase().getUser_weight())
//                        + _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance())/ (Double.parseDouble(mDaySynopic.getWork_duration())),Integer.parseInt(mDaySynopic.getWork_duration()), userEntity.getUserBase().getUser_weight());
                    MyLog.i(TAG,"计算的卡路里="+calValue);
                    MyLog.i(TAG, "查询的是星期几:" + (c.get(Calendar.DAY_OF_WEEK) - 1));
                    if(calendar.get(c.DAY_OF_WEEK)==c.get(c.DAY_OF_WEEK)){
                        //是当前天
                        MyLog.i(TAG, "是当前天=" + (c.get(c.DAY_OF_WEEK) - 1));
                        if((c.get(Calendar.DAY_OF_WEEK) - 1)==0){
                            entries.add(new BarEntry(calValue + cal, 6));
                        }else {
                            entries.add(new BarEntry(calValue + cal, c.get(c.DAY_OF_WEEK) - 2));
                        }
                    }else {
                        //不是当前天
                        MyLog.i(TAG,"不是当前天="+calValue+BASE_CAL);
                        if((c.get(Calendar.DAY_OF_WEEK) - 1)==0){
                            entries.add(new BarEntry(calValue + BASE_CAL, 6));
                            list.add(6);
                        }else {
                            entries.add(new BarEntry(calValue + BASE_CAL, c.get(c.DAY_OF_WEEK) - 2));
                            list.add(c.get(c.DAY_OF_WEEK)-2);
                        }
                    }
                    //list.add(c.get(c.DAY_OF_WEEK)-2);
                }
                //还有没有查出来的数据的,也要默认加上一天的卡路里
            }else {

            }
            MyLog.i(TAG, "已经设置的=" + list.toString());
            for(int j=0;j<calendar.get(Calendar.DAY_OF_WEEK)-1;j++){
                if(list.contains(j)){
                    continue;
                }else {
                    if(j==calendar.get(Calendar.DAY_OF_WEEK)-2){
                        MyLog.i(TAG, "//是今天的数据=" + j);
                        entries.add(new BarEntry(cal,j));//是今天的数据
                        continue;
                    }else {
                        MyLog.i(TAG, "不是今天的数据=" + j);
                        entries.add(new BarEntry(BASE_CAL,j));
                        continue;
                    }
                }
            }
        }else {
            //不是本周
            //每天都加上一天的卡路里
            List<Integer> list=new ArrayList<>();
            if(listData!=null&&listData.size()>0){
                for(int i=0;i<listData.size();i++){
                    MyLog.i(TAG, listData.get(i).toString());
                    Calendar c = Calendar.getInstance();
                    Date date=ToolKits.stringToDate(listData.get(i).getData_date(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
                    c.setTime(date);
                    //c.add(Calendar.DATE, -1);
                    DaySynopic mDaySynopic=listData.get(i);
                    //走路 分钟
                    double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                    //跑步 分钟
                    double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);
                    int runcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance())/(Double.parseDouble(mDaySynopic.getRun_duration())),(int)runtime*60,userEntity.getUserBase().getUser_weight());
                    int walkcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance()) / (Double.parseDouble(mDaySynopic.getWork_duration())), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                    runcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getRun_distance()),(int) runtime * 60,userEntity.getUserBase().getUser_weight());
                    walkcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getWork_distance()),(int) walktime * 60,userEntity.getUserBase().getUser_weight());
                    int calValue=runcal+walkcal;
//                  int calValue = _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration())), Integer.parseInt(mDaySynopic.getRun_duration()),userEntity.getUserBase().getUser_weight())
//                        + _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance())/ (Double.parseDouble(mDaySynopic.getWork_duration())),Integer.parseInt(mDaySynopic.getWork_duration()), userEntity.getUserBase().getUser_weight());
                    MyLog.i(TAG, "卡路里=" + calValue);
                    if((c.get(Calendar.DAY_OF_WEEK) - 1)==0){
                        entries.add(new BarEntry(calValue + BASE_CAL, 6));
                        list.add(6);
                    }else {
                        entries.add(new BarEntry(calValue + BASE_CAL, c.get(c.DAY_OF_WEEK) - 2));
                        list.add(c.get(c.DAY_OF_WEEK) - 2);
                    }

                }

            }else {


            }
            //还有没有查出来的数据的,也要默认加上一天的卡路里
            for(int j=0;j<7;j++){
                if(list.contains(j)){
                    continue;
                }else {
                    entries.add(new BarEntry(BASE_CAL,j));
                }
            }
        }
        mentries=entries;
        addline(R.id.report_page_activity_circleviews_weekRb);
        BarDataSet dataSet;
        dataSet = new BarDataSet(entries, "");
        dataSet.setColor(getResources().getColor(R.color.yellow_title));
        // 柱形图顶端字是否显示
        dataSet.setDrawValues(false);
        // 柱形图顶端字体颜色
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        //  柱形图顶端字体大小
        dataSet.setValueTextSize(10f);
        bardata.addDataSet(dataSet);

        dataSet.setHighlightEnabled(false);

        dataSet.setBarSpacePercent(60);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return bardata;
    }

    //获取月数据
    private BarData getMonthBarData(int days) {
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        MyLog.i(TAG,"获得数据" +listData.size());
        List<Integer> list=new ArrayList<>();
        //如果是今天的数据,则加上的是基础量,是当前时间的
        SimpleDateFormat s1=new SimpleDateFormat("HH");
        SimpleDateFormat s2=new SimpleDateFormat("mm");
        int h=Integer.parseInt(s1.format(new Date()));
        int m=Integer.parseInt(s2.format(new Date()));
        int cal= (int) (( h*60 + m )*(float)(BASE_CAL/24/60));//当前时间今天的卡路里
        MyLog.i(TAG,"当前的卡路里="+cal);
        SimpleDateFormat sim1 = new SimpleDateFormat("dd");
        int day=Integer.parseInt(sim1.format(new Date()));//当前日期是几号
        if(day2.getText().equals(getString(R.string.thismonth))){
            //是本月
            for(int i=0;i<listData.size();i++){
                MyLog.i(TAG, listData.get(i).toString());
                Calendar c = Calendar.getInstance();
                Date date=ToolKits.stringToDate(listData.get(i).getData_date(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
                c.setTime(date);
                String date1 = sim1.format(c.getTime());
                int s=Integer.parseInt(date1);
                DaySynopic mDaySynopic=listData.get(i);
                //计算卡路里
                if(day==s){
                    //走路 分钟
                    double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                    //跑步 分钟
                    double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);
                    int runcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance())/(Double.parseDouble(mDaySynopic.getRun_duration())),(int)runtime*60,userEntity.getUserBase().getUser_weight());
                    int walkcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance()) / (Double.parseDouble(mDaySynopic.getWork_duration())), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                    runcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getRun_distance()),(int) runtime * 60,userEntity.getUserBase().getUser_weight());
                    walkcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getWork_distance()),(int) walktime * 60,userEntity.getUserBase().getUser_weight());
                    int calValue=runcal+walkcal;
                    //查到的是本天的数据
//                    int calValue = _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration()) ), Integer.parseInt(mDaySynopic.getRun_duration()), userEntity.getUserBase().getUser_weight())
//                            + _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance())/ (Double.parseDouble(mDaySynopic.getWork_duration())),Integer.parseInt(mDaySynopic.getWork_duration()), userEntity.getUserBase().getUser_weight());
                    MyLog.i(TAG,"卡路里="+calValue);
                    entries.add(new BarEntry(calValue+cal, s - 1));//计算出,这条记录是在哪一个下标
                }else {
                    //走路分钟
                    double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                    //跑步 分钟
                    double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);
                    int runcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance())/(Double.parseDouble(mDaySynopic.getRun_duration())),(int)runtime*60,userEntity.getUserBase().getUser_weight());
                    int walkcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance()) / (Double.parseDouble(mDaySynopic.getWork_duration())), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                    runcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getRun_distance()),(int) runtime * 60,userEntity.getUserBase().getUser_weight());
                    walkcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getWork_distance()),(int) walktime * 60,userEntity.getUserBase().getUser_weight());
                    int calValue=runcal+walkcal;
//                   int calValue = _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration()) ), Integer.parseInt(mDaySynopic.getRun_duration()), userEntity.getUserBase().getUser_weight())
//                            + _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance())/ (Double.parseDouble(mDaySynopic.getWork_duration())),Integer.parseInt(mDaySynopic.getWork_duration()), userEntity.getUserBase().getUser_weight());
                    MyLog.i(TAG,"卡路里="+calValue);
                    entries.add(new BarEntry(calValue+BASE_CAL, s - 1));//计算出,这条记录是在哪一个下标
                }
                MyLog.i(TAG, "本月的第几天"+endDateString+ date1);
                list.add(s-1);
            }
            //只遍历到今天的日期
            //还有没有查出来的数据的,也要默认加上一天的卡路里
            MyLog.e(TAG, "当前是几号="+day);
            MyLog.e(TAG, "已经加入的=" + list.toString());
            for(int j=0;j<day;j++){
                if(list.contains(j)){
                    continue;
                }else {
                    if(j==day-1){
                        MyLog.i(TAG, "//是今天的数据=" + j);
                        entries.add(new BarEntry(cal,j));//是今天的数据
                    }else {
                        entries.add(new BarEntry(BASE_CAL,j));
                    }
                }
            }
        }else {
            //不是本月
            for(int i=0;i<listData.size();i++){
                //float profit= random.nextFloat()*1000;
                MyLog.i(TAG, listData.get(i).toString());
                Calendar c = Calendar.getInstance();
                Date date=ToolKits.stringToDate(listData.get(i).getData_date(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
                c.setTime(date);
                String date1 = sim1.format(c.getTime());
                int s=Integer.parseInt(date1);
                DaySynopic mDaySynopic=listData.get(i);
                //计算卡路里

                double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                //跑步 分钟
                double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);
                int runcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration())), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
                int walkcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance()) / (Double.parseDouble(mDaySynopic.getWork_duration())), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                runcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getRun_distance()),(int) runtime * 60,userEntity.getUserBase().getUser_weight());
                walkcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getWork_distance()),(int) walktime * 60,userEntity.getUserBase().getUser_weight());
                int calValue=runcal+walkcal;


//                int calValue = _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration()) ), Integer.parseInt(mDaySynopic.getRun_duration()), userEntity.getUserBase().getUser_weight())
//                        + _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance())/ (Double.parseDouble(mDaySynopic.getWork_duration())),Integer.parseInt(mDaySynopic.getWork_duration()), userEntity.getUserBase().getUser_weight());
                MyLog.i(TAG,"卡路里="+calValue);
                // MyLog.i(TAG, "本月的第几天"+endDateString+ date1);
                entries.add(new BarEntry(calValue+BASE_CAL,s-1));//计算出,这条记录是在哪一个下标
                list.add(s-1);
                MyLog.i(TAG, "查到的这个月的数据:" + list.toString());
            }
            //还有没有查出来的数据的,也要默认加上一天的卡路里
            for(int j=0;j<days;j++){
                if(list.contains(j)){
                    MyLog.i(TAG, "本月的第几天"+j);
                    continue;
                }else {
                    MyLog.i(TAG, "插入数据"+j);
                    entries.add(new BarEntry(BASE_CAL,j));
                }
            }

        }
        mentries=entries;
        addline(R.id.report_page_activity_circleviews_weekRb);
        BarDataSet dataSet;
        dataSet = new BarDataSet(entries, "");
        dataSet.setColor(getResources().getColor(R.color.yellow_title));
        // 柱形图顶端字是否显示
        dataSet.setDrawValues(false);
        // 柱形图顶端字体颜色
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        //  柱形图顶端字体大小
        dataSet.setValueTextSize(10f);
        bardata.addDataSet(dataSet);
        dataSet.setHighlightEnabled(false);//设置成t触摸的时候回变成灰色
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return bardata;
    }


    public class MyPageadapter extends PagerAdapter {
        int mcount;

        public MyPageadapter(int count){
            mcount=count;
        }

        @Override
        public int getCount() {
            return mcount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        //销毁Item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(listView.get(position));
        }
        //实例化Item
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //根据position区添加新的view
            View view=getView(position);
            view.setId(position);
            listView.put(position,view);
            container.addView(view);
            return view;
        }
    }

    private View getView(int position) {
        SimpleDateFormat s1=new SimpleDateFormat("HH");
        SimpleDateFormat s2=new SimpleDateFormat("mm");
        int h=Integer.parseInt(s1.format(new Date()));
        int m=Integer.parseInt(s2.format(new Date()));
        int cal= (int) ((h*60+m)*1.15);//当前时间今天的卡路里
        Date date1=ToolKits.getDayFromDate(new Date(0), position + 1);
        if(dayButton.isChecked()){
            LayoutInflater inflater=getLayoutInflater();
            View view = inflater.inflate(R.layout.diatance_data_view, null);
           /* RelativeLayout shuju= (RelativeLayout) view.findViewById(R.id.shuju);//有数据的时候显示
            LinearLayout nulldata= (LinearLayout) view.findViewById(R.id.null_data_LL);//没数据的时候显示
            TextView data= (TextView) view.findViewById(R.id.data);
            TextView title= (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.cal_title));
            if(querydata(position)==null || querydata(position).size()<=0) {
                shuju.setVisibility(View.GONE);
                nulldata.setVisibility(View.VISIBLE);
            }else{
                //计算卡路里
                DaySynopic mDaySynopic=querydata(position).get(0);
                //走路 分钟
                double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                //跑步 分钟
                double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);
                int runcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration())), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
                int walkcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getWork_duration())), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                int calValue=runcal+walkcal;
                if(ToolKits.getBetweenDay(date1,new Date())==0){
                    //是今天
                    calValue=cal+calValue;
                }else {
                    calValue=calValue+BASE_CAL;
                }
                MyLog.e(TAG, "这天的calValue=" + calValue);

                data.setText(calValue+"");
            }*/

            return view;
        }else {
            //星期和月
            //选择了周和月
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.step_data_view, null);

           /* LinearLayout shuju = (LinearLayout) view.findViewById(R.id.shuju);//有数据的时候显示
            LinearLayout nulldata = (LinearLayout) view.findViewById(R.id.null_data_LL);//没数据的时候显示
            TextView titleview = (TextView) view.findViewById(R.id.data_count_title);
            titleview.setText(getString(R.string.cal_title));//卡路里消耗
            TextView data = (TextView) view.findViewById(R.id.step_count);
            ListView listviewstep = (ListView) view.findViewById(R.id.listview_step);
            List<Calory> myCal=initData(position);
            if (myCal!= null && myCal.size() > 0) {
                int count=0;
                for(Calory claory:myCal){
                    count=count+claory.getCalory();
                }
                if(count==0){
                    shuju.setVisibility(View.GONE);
                    nulldata.setVisibility(View.VISIBLE);
                }else {
                    shuju.setVisibility(View.VISIBLE);
                    nulldata.setVisibility(View.GONE);
                    data.setText(count + "(" + getString(R.string.unit_cal) + ")");
                    MyCalAdapter adapter=new MyCalAdapter(CalDataActivity.this, (ArrayList<Calory>) myCal);
                    listviewstep.setAdapter(adapter);
                }
            }else {
                shuju.setVisibility(View.GONE);
                nulldata.setVisibility(View.VISIBLE);
            }*/

            return view;
        }
    }



    private void setDataToView(int position,List<DaySynopic> daySynopicList,List<Calory> caloryList){


//        int cal= (int) ((h*60+m)*1.15);//当前时间今天的卡路里
        Date date1=ToolKits.getDayFromDate(new Date(0), position + 1);

        if(dayButton.isChecked()){
            View view=mViewPager.findViewById(position);
            RelativeLayout shuju= (RelativeLayout) view.findViewById(R.id.shuju);//有数据的时候显示
            LinearLayout nulldata= (LinearLayout) view.findViewById(R.id.null_data_LL);//没数据的时候显示
            TextView data= (TextView) view.findViewById(R.id.data);
            TextView title= (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.cal_title));
            shuju.setVisibility(View.VISIBLE);
            nulldata.setVisibility(View.GONE);
            int calValuecount=0;

            if(daySynopicList==null || daySynopicList.size()<=0) {
                //今天的数据是空的

                if(ToolKits.getBetweenDay(date1,new Date())==0){
                    //是今天
                    calValuecount=0;
                }else {
                    calValuecount=BASE_CAL;
                }

            }else{
                //计算卡路里
                DaySynopic mDaySynopic=daySynopicList.get(0);
                //走路 分钟
                double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getWork_duration()), 1);
                //跑步 分钟
                double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(mDaySynopic.getRun_duration()), 1);
                int runcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getRun_distance()) / (Double.parseDouble(mDaySynopic.getRun_duration())), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
                int walkcal =  _Utils.calculateCalories(Double.parseDouble(mDaySynopic.getWork_distance()) / (Double.parseDouble(mDaySynopic.getWork_duration())), (int) walktime * 60, userEntity.getUserBase().getUser_weight());
                runcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getRun_distance()),(int) runtime * 60,userEntity.getUserBase().getUser_weight());
                walkcal =(int)ToolKits.calculateCalories(Float.parseFloat(mDaySynopic.getWork_distance()),(int) walktime * 60,userEntity.getUserBase().getUser_weight());
                int calValue=runcal+walkcal;
                if(ToolKits.getBetweenDay(date1,new Date())==0){
                    //是今天
                    calValuecount=calValue;
                }else {
                    calValuecount=calValue+BASE_CAL;
                }
            }

            MyLog.e(TAG, "这天的calValue=" + calValuecount);
            data.setText(calValuecount+"");

        }else {
            View view=mViewPager.findViewById(position);
            LinearLayout shuju = (LinearLayout) view.findViewById(R.id.shuju);//有数据的时候显示
            LinearLayout nulldata = (LinearLayout) view.findViewById(R.id.null_data_LL);//没数据的时候显示
            TextView titleview = (TextView) view.findViewById(R.id.data_count_title);
            titleview.setText(getString(R.string.cal_title));//卡路里消耗
            TextView data = (TextView) view.findViewById(R.id.step_count);
            ListView listviewstep = (ListView) view.findViewById(R.id.listview_step);

            shuju.setVisibility(View.VISIBLE);
            nulldata.setVisibility(View.GONE);

            if (caloryList!= null && caloryList.size() > 0) {
                int count=0;
                for(Calory claory:caloryList){
                    count=count+claory.getCalory();
                }
                if(count==0){
                    shuju.setVisibility(View.GONE);
                    nulldata.setVisibility(View.VISIBLE);
                }else {
                    shuju.setVisibility(View.VISIBLE);
                    nulldata.setVisibility(View.GONE);
                    data.setText(count + "(" + getString(R.string.unit_cal) + ")");
                    for(Calory calory : caloryList){
                        MyLog.e(TAG, "这天的calValue=" + calory.getCalory());
                    }
                    MyCalAdapter adapter=new MyCalAdapter(CalDataActivity.this, (ArrayList<Calory>) caloryList);
                    listviewstep.setAdapter(adapter);
                }
            }else {
                shuju.setVisibility(View.GONE);
                nulldata.setVisibility(View.VISIBLE);
            }

        }

    }

    //处理数据,主要是判断截止日期,如果日期是今天的日期,后面的数据就不要了
    private   List<Calory> initData(ArrayList<DaySynopic> mDaySynopicArrayList){
        MyLog.i(TAG,"开始initData");
        SimpleDateFormat s1=new SimpleDateFormat("HH");
        SimpleDateFormat s2=new SimpleDateFormat("mm");
        int h=Integer.parseInt(s1.format(new Date()));
        int m=Integer.parseInt(s2.format(new Date()));
        int cal= (int) (( h*60 + m )*(float)(BASE_CAL/24/60));//当前时间今天的卡路里
        MyLog.i(TAG,"当前的卡路里====="+cal);
        List<Calory> list=new ArrayList<>();
        //1将每天的基础值加上,还要去掉当前日期之后的日期
        //得到两个日期的相差的天数
        if(mDaySynopicArrayList==null||mDaySynopicArrayList.size()<=0){
            return list;
        }
        for(DaySynopic myDaySynopic:mDaySynopicArrayList){
            Calory mycal=new Calory();
            try {
                int i=ToolKits.getBetweenDay(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).parse(myDaySynopic.getData_date()),new Date());
                MyLog.i(TAG,"int i="+i+myDaySynopic.getData_date());
                if(i>0){
                    //没有超过今天
                    // 计算当天的卡路里
                    //走路 分钟
                    double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(myDaySynopic.getWork_duration()), 1);
                    double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(myDaySynopic.getRun_duration()), 1);
                    int runcal =  _Utils.calculateCalories(Double.parseDouble(myDaySynopic.getRun_distance()) / (Double.parseDouble(myDaySynopic.getRun_duration())), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
                    int walkcal =  _Utils.calculateCalories(Double.parseDouble(myDaySynopic.getWork_distance())/(Double.parseDouble(myDaySynopic.getWork_duration())),(int)walktime*60,userEntity.getUserBase().getUser_weight());
                    runcal =(int)ToolKits.calculateCalories(Float.parseFloat(myDaySynopic.getRun_distance()),(int) runtime * 60,userEntity.getUserBase().getUser_weight());
                    walkcal =(int)ToolKits.calculateCalories(Float.parseFloat(myDaySynopic.getWork_distance()),(int) walktime * 60,userEntity.getUserBase().getUser_weight());
                    int calValue=runcal+walkcal;
                    mycal.setCalory(calValue+BASE_CAL);
                    mycal.setDate(myDaySynopic.getData_date());
                    list.add(mycal);
                }
                if(i==0) {
                    double walktime = CommonUtils.getScaledDoubleValue(Double.valueOf(myDaySynopic.getWork_duration()), 1);
                    double runtime = CommonUtils.getScaledDoubleValue(Double.valueOf(myDaySynopic.getRun_duration()), 1);
                    int runcal =  _Utils.calculateCalories(Double.parseDouble(myDaySynopic.getRun_distance()) / (Double.parseDouble(myDaySynopic.getRun_duration())), (int) runtime * 60, userEntity.getUserBase().getUser_weight());
                    int walkcal =  _Utils.calculateCalories(Double.parseDouble(myDaySynopic.getWork_distance())/(Double.parseDouble(myDaySynopic.getWork_duration())),(int)walktime*60,userEntity.getUserBase().getUser_weight());
                    int calValue=runcal+walkcal;
                    mycal.setCalory(calValue+cal);//只加上当前的
                    mycal.setDate(myDaySynopic.getData_date());
                    list.add(mycal);
                }else {
                    //日期超过了今天,要去除
                    MyLog.i(TAG,"日期超过了今天,要去除"+i+myDaySynopic.getData_date());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //viewpage在切换过程中更改button的字样
    private void changeDate(int position){
        //日详情
        if(dayButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0), new Date());
            int day=days-position-1;
//            dayposition=position;//记录天滑到的位置
            //也要随着天数页面日期的变化,周和月的日期也要随着变化
            Date dataChange=ToolKits.getDayFromDate(new Date(),-(day));

            date=dataChange;
            if(day==0){
                //传进来的是今天的数据
                day1.setText(getString(R.string.yesterday));
                day2.setText(getString(R.string.today));
                day2.setChecked(true);
                day3.setText("");
            } else if(day==1) {
                //计算传传进来的日期是几天
                Date d=ToolKits.getDayFromDate(new Date(),-2);
                day1.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(d));
                day2.setText(getString(R.string.yesterday));
                day2.setChecked(true);
                day3.setText(getString(R.string.today));
            }else if(day==2){
                Date date1=ToolKits.getDayFromDate(new Date(),-(day));
                Date date3=ToolKits.getDayFromDate(new Date(),-(day+1));
                day1.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(date3));
                day2.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(date1));
                day3.setText(getString(R.string.yesterday));
            }
            else{
                Date date1=ToolKits.getDayFromDate(new Date(),-(day+1));
                Date data2=ToolKits.getDayFromDate(new Date(),-(day));
                Date date3=ToolKits.getDayFromDate(new Date(),-(day-1));
                day1.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(date1));
                day2.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(data2));
                day2.setChecked(true);
                day3.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD).format(date3));
            }

        }

        //周数据
        else if(weekButton.isChecked()){
//            weekposition=position;//记录周的位置
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD);
            //传来的position是的几周//更当前时间比较
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1>3){
                weekcount=weekcount+1;
            }
            MyLog.i(TAG,"相差几周="+(weekcount-position-1));
            if(weekcount-position-1==0){
                //传进来的是本周
                // 上周  本周
                day1.setText(getString(R.string.lastweek));
                day2.setText(getString(R.string.thisweek));
                day2.setChecked(true);
                day3.setText("");
            }
            else if(weekcount-position-1==1){
                // 日期  上周  本周
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(7*2))));
                day1.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                day2.setText(getString(R.string.lastweek));
                day2.setChecked(true);
                day3.setText(getString(R.string.thisweek));
            }else if(weekcount-position-1==2){
                //日期 日期  上周
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(7*3))));//相差三周的日期
                day1.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(7*2))));//相差两周周的日期
                day2.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                day3.setText(getString(R.string.lastweek));

            }else {
                //日期 日期  日期
                GregorianCalendar base1 = new GregorianCalendar();
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position)*7)));//减一周的日期
                day1.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position-1)*7)));//中间是当前日期
                day2.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position-2)*7)));//加一周的日期
                day3.setText(simpleDateFormat.format(weekSwitcher.getStartDate())+ "~" +simpleDateFormat.format(ToolKits.getDayFromDate(weekSwitcher.getEndDate(), -1)));
            }
        }

        //月数据
        else if(monthButton.isChecked()){
//            monthposition=position;
            //根据position当前是第几月  代表的是第几个月
            int month=monthadapter.getCount();//当前的总共的月
            MyLog.i(TAG,"month="+month+"position="+position);
            SimpleDateFormat sim1 = new SimpleDateFormat("MM");
            SimpleDateFormat sim2 = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat sim3 = new SimpleDateFormat("yyyy");
            if(position==month-1){
                //传进来的是本月的数据
                //当前是本月
                day1.setText(getString(R.string.lastmonth));
                day2.setText(getString(R.string.thismonth));
                day2.setChecked(true);
                day3.setText("");
            } else if(position==month-2) {
                //上个月
                GregorianCalendar base1= new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.MONTH, -2);
                String s=sim2.format(base1.getTime());
                day1.setText(s);
                day2.setText(getString(R.string.lastmonth));
                day2.setChecked(true);
                day3.setText(getString(R.string.thismonth));
            }else if(position==month-3){
                GregorianCalendar base1 = new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.MONTH, -3);
                String days1=sim2.format(base1.getTime()) ;//相差三个月
                GregorianCalendar base2 = new GregorianCalendar();
                base2.setTime(new Date());
                base2.add(GregorianCalendar.MONTH, -2);
                String days2=sim2.format(base2.getTime()) ;//相差两个个月
                day1.setText(days1);
                day2.setText(days2);//相差两个月
                day2.setChecked(true);
                day3.setText(getString(R.string.lastmonth));
            }else{

                //相差了month-position-1
                GregorianCalendar base1= new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.MONTH, -(month-position));
                GregorianCalendar base2 = new GregorianCalendar();
                base2.setTime(new Date());
                base2.add(GregorianCalendar.MONTH,-(month-position-1));
                GregorianCalendar base3 = new GregorianCalendar();
                base3.setTime(new Date());
                base3.add(GregorianCalendar.MONTH, -(month-position-2));
                String days1=sim2.format(base1.getTime()) ;//前一个月
                String days2=sim2.format(base2.getTime()) ;//本月
                String days3=sim2.format(base3.getTime());//后一个月
                day1.setText(days1);
                day2.setText(days2);
                day2.setChecked(true);
                day3.setText(days3);
            }
        }

    }

    @Override
    protected void initListeners() {
        dayButton.setOnClickListener(this);
        weekButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                MyLog.i(TAG, "onPageSelected=" + position);
                changeDate(position);
                changeChat(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    //切换图表设置
    @SuppressWarnings("unchecked")
    private void changeChat(final int position) {
        if(dayButton.isChecked()){
            AsyncTask myDayAsync=new AsyncTask<Object,Object,List<DaySynopic>>(){
                @Override
                protected void onPreExecute() {

                    if(pd!=null){
                        pd.show();
                    }else {
                        MyLog.i(TAG,"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    }
                }
                @Override
                protected List<DaySynopic> doInBackground(Object... params) {
                    Date date1=ToolKits.getDayFromDate(new Date(0), position+1);
                    String time=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date1);
                    MyLog.i(TAG,"查询数据的时间="+time+"position="+position);
                    Map<Integer,ArrayList<SportRecord>> datatemp=new Datahelper(CalDataActivity.this, String.valueOf(userEntity.getUser_id()), time,time,false).getMydata();
                    data=datatemp;
                    //查询汇总表的数据
                    List<DaySynopic> mDaySynopicArrayList= DaySynopicTable.findDaySynopicRange(CalDataActivity.this, userEntity.getUser_id()+"", time, time, String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
                    DaySynopic mDaySynopic = new DaySynopic();
                    ArrayList<DaySynopic> temp=new ArrayList<>();
                    if(mDaySynopicArrayList==null||mDaySynopicArrayList.size()<=0){
                        mDaySynopic=SportDataHelper.offlineReadMultiDaySleepDataToServer(CalDataActivity.this, time, time);
                        if(mDaySynopic.getTime_zone()==null){
                            return null;
                        }
                        temp.add(mDaySynopic);
                        DaySynopicTable.saveToSqliteAsync(CalDataActivity.this,temp,userEntity.getUser_id()+"");
                    }else {
                        return mDaySynopicArrayList;
                    }
                    MyLog.e(TAG, "temp=" + temp.toString());
                    MyLog.e(TAG, "temp=" +temp.size());
                    if(temp.size()>0){
                        return temp;
                    }else {
                        //可能还没去同步这一天的数据,
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(List<DaySynopic> aVoid) {
                    MyLog.i(TAG,"重画图");
                    combbarChart.setData(generateDayBarData());
                    combbarChart.invalidate();
                    setDataToView(position, aVoid, null);
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                }
            };
            if (dayDataAsync != null)
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(dayDataAsync, true);
            AsyncTaskManger.getAsyncTaskManger().addAsyncTask(dayDataAsync =  myDayAsync);
            myDayAsync.execute();
        }
        if(weekButton.isChecked()){
            int days=ToolKits.getBetweenDay(new Date(0),new Date());//当前时间与系统时间之间差了多少天
            int weekcount=days/7;//系统时间1970/01/01与总的周数
            int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
            if(week1 > 3){
                weekcount=weekcount + 1;
            }
            MyLog.i(TAG, "相差几周=" + (weekcount - position - 1));
            weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position-1)*7)));//中间是当前日期
            final Date lastDate=weekSwitcher.getEndDate();
            startDateString=weekSwitcher.getStartDateStr();
            endDateString=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getDayFromDate(lastDate, -1));
            MyLog.i(TAG, weekSwitcher.getSQLBetweenAnd());
            MyLog.i(TAG, "startDateString=" + startDateString + "endDateString=" + endDateString);
            AsyncTask myWeekAsync=new AsyncTask<Object,Object,Object>(){
                @Override
                protected void onPreExecute() {
                    if(pd!=null){
                        pd.show();
                    }
                }
                @Override
                protected Void doInBackground(Object... params) {
                    MyLog.i(TAG, "开始时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    ArrayList<DaySynopic> mDaySynopicArrayList=ToolKits.getFindWeekData(CalDataActivity.this,weekSwitcher.getStartDate(),userEntity);
                    listData=mDaySynopicArrayList;
                    return null;
                }
                @Override
                protected void onPostExecute(Object aVoid) {
                    MyLog.i(TAG, "结束时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    combbarChart.setData(generateWeekBarData());
                    combbarChart.invalidate();

                    setDataToView(position, null,initData(listData));

                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                }
            };
            if (weekDataAsync != null)
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(weekDataAsync, true);
            AsyncTaskManger.getAsyncTaskManger().addAsyncTask(weekDataAsync = myWeekAsync);
            myWeekAsync.execute();
        }
        if(monthButton.isChecked()){
            SimpleDateFormat sim1 = new SimpleDateFormat("MM");
            SimpleDateFormat sim3 = new SimpleDateFormat("yyyy");
            int year=Integer.parseInt(sim3.format(new Date()));
            int s2=Integer.parseInt(sim1.format(new Date()));//当前时间的月份
            int month=(year-1970)*12+s2+1;
            GregorianCalendar base2 = new GregorianCalendar();
            base2.setTime(new Date());
            base2.add(GregorianCalendar.MONTH, -(month - position - 1));
            monthSwitcher.setBaseTime(base2.getTime());
            MyLog.i(TAG, "结束时间" + monthSwitcher.getEndDateStr());
            Date lastDate=monthSwitcher.getEndDate();
            startDateString=monthSwitcher.getStartDateStr();
            endDateString=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getDayFromDate(lastDate, -1));
            MyLog.i(TAG, monthSwitcher.getSQLBetweenAnd());
            MyLog.i(TAG, "startDateString=" + startDateString + "endDateString=" + endDateString);
            AsyncTask myMonthAsybc=new AsyncTask<Object,Object,Object>(){
                @Override
                protected void onPreExecute() {
                    if(pd!=null){
                        pd.show();
                    }
                }
                @Override
                protected Object doInBackground(Object... params) {
                    MyLog.i(TAG, "开始时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    ArrayList<DaySynopic> mDaySynopicArrayList=ToolKits.getFindMonthData(CalDataActivity.this,monthSwitcher.getStartDate(),userEntity);
                    listData=mDaySynopicArrayList;
                    return null;
                }
                @Override
                protected void onPostExecute(Object aVoid) {
                    MyLog.i(TAG, "结束时间=" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
                    combbarChart.setData(generateMonthBarData(monthSwitcher.getStartDateStr()));
                    combbarChart.invalidate();
                    setDataToView(position, null, initData(listData));
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                }
            };
            if (monthDataAsync != null)
                AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(monthDataAsync, true);
            AsyncTaskManger.getAsyncTaskManger().addAsyncTask(monthDataAsync = myMonthAsybc);
            myMonthAsybc.execute();
        }

    }

    private void addline(int i){
        YAxis yAxis=combbarChart.getAxisLeft();
        yAxis.removeAllLimitLines();
        float f=0;
        for(BarEntry barEntry:mentries){
            MyLog.e(TAG, "y轴："+ barEntry.getVal() );
            if(barEntry.getVal() > f){
                f=barEntry.getVal();
            }
        }
        //没有数据就不画了
        if(f==0){
            return;
        }
        switch (i){
            case R.id.report_page_activity_circleviews_dayRb:
                if((int)f<90f){
                    yAxis.setAxisMaxValue(90f);
                }else{
                    //这句如果不加 最大值就会是今天的卡路里数
                    yAxis.setAxisMaxValue(f);
                }
                MyLog.i(TAG, yAxis.getAxisMaxValue() + "天最大值");
                LimitLine limitLine1 = new LimitLine(30, 30+"");
                limitLine1.setLineColor(Color.WHITE);
                limitLine1.setLineWidth(1f);
                limitLine1.setTextColor(Color.WHITE);
                limitLine1.setTextSize(12f);
                limitLine1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP); //警戒线的文字位置

                LimitLine limitLine2 = new LimitLine(60f, 60+"");
                limitLine2.setLineColor(Color.WHITE);
                limitLine2.setLineWidth(1f);
                limitLine2.setTextColor(Color.WHITE);
                limitLine2.setTextSize(12f);
                limitLine2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
//                yAxis.setDrawLimitLinesBehindData(true); //警戒线在图表底层
                yAxis.addLimitLine(limitLine2);
                yAxis.addLimitLine(limitLine1);

                break;
            case R.id.report_page_activity_circleviews_weekRb:
                //紧界线
                if((int)f<1200){
                    yAxis.setAxisMaxValue(1200);
                }else {
                    yAxis.setAxisMaxValue(f+500);
                }
                MyLog.i(TAG, yAxis.getAxisMaxValue() + "周最大值");
                LimitLine limitLine = new LimitLine(1000,1000+ "");
                limitLine.setLineColor(Color.WHITE);
                limitLine.setLineWidth(1f);
                limitLine.setTextColor(Color.WHITE);
                limitLine.setTextSize(12f);
                limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                yAxis.addLimitLine(limitLine);
                break;
            case R.id.report_page_activity_circleviews_monthRb:
                break;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.report_page_activity_circleviews_dayRb:
                if(dayButton.isChecked())
                    initTitle();
                break;
            case R.id.report_page_activity_circleviews_weekRb:
                if(weekButton.isChecked())
                    initTitle();
                break;
            case R.id.report_page_activity_circleviews_monthRb:
                if(monthButton.isChecked())
                    initTitle();
                break;
        }

    }


    private  class MyCalAdapter extends BaseAdapter {
        String s1=getString(R.string.sunday);
        String s2=getString(R.string.monday);
        String s3=getString(R.string.tuesday);
        String s4=getString(R.string.wednesday);
        String s5=getString(R.string.thursday);
        String s6=getString(R.string.friday);
        String s7=getString(R.string.saturday);
        String[] weekDays = { s1, s2, s3, s4, s5, s6, s7 };
        private Context mcontext;
        private ArrayList<Calory> list;
        public MyCalAdapter(Context context,ArrayList<Calory> list){
            this.list=list;
            this.mcontext=context;
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mcontext).inflate(R.layout.step_listview_item, null);
                vh = new ViewHolder();
                vh.chatItem= (LinearLayout) convertView.findViewById(R.id.chart_item);
                vh.textViewcount= (TextView) convertView.findViewById(R.id.count);
                vh.textViewdate=(TextView) convertView.findViewById(R.id.date);
                vh.textViewpercent=(TextView) convertView.findViewById(R.id.percent);
                vh.textViewweek=(TextView) convertView.findViewById(R.id.week);
                vh.unit_step= (TextView)convertView.findViewById(R.id.unit_step);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            vh.chatItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //切换图表到当前的日期
                    if(!CommonUtils.isStringEmpty(list.get(position).getDate())){
                     /* int days=ToolKits.getBetweenDay(new Date(0),new Date());
                      int day=ToolKits.getBetweenDay(ToolKits.stringToDate(list.get(position).getDate(),ToolKits.DATE_FORMAT_YYYY_MM_DD),new Date());
                      dayposition=days - day - 1;*/
                        Date dateChage=ToolKits.stringToDate(list.get(position).getDate(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
                        date=dateChage;
                        dayButton.setChecked(true);
                        initTitle();
                    }else {
                        //得到的日期是空的
                        MyLog.i(TAG,"点击后,获得的时间是空的");
                    }

                }
            });

            vh.textViewdate.setText(list.get(position).getDate());
            //星期,目标
            //获取本地目标
            Calendar cal=Calendar.getInstance();
            cal.setTime(ToolKits.stringToDate(list.get(position).getDate(), ToolKits.DATE_FORMAT_YYYY_MM_DD));
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0) {
                w = 0;
            }
            vh.textViewweek.setText(weekDays[w]);
            Calory calory=list.get(position);

            if(calory!=null)
                vh.textViewcount.setText(calory.getCalory()+"");
            vh.unit_step.setText(getString(R.string.unit_cal));
            int  cal_goal = (int)(Float.parseFloat(PreferencesToolkits.getGoalInfo(CalDataActivity.this, PreferencesToolkits.KEY_GOAL_CAL)));
            if(cal_goal==0){
            }else {
                //Math.ceil(Float.parseFloat(money) * 100*1.0f/ money_goal))
                vh.textViewpercent.setText(Math.ceil(calory.getCalory()*100*1.0f/ cal_goal)+"%");
            }
            return convertView;
        }
        public class ViewHolder {
            public TextView textViewdate, textViewweek,textViewcount,textViewpercent,unit_step;
            public LinearLayout chatItem;
        }

    }
}
