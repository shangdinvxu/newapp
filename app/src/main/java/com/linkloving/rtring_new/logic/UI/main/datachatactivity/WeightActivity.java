package com.linkloving.rtring_new.logic.UI.main.datachatactivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.db.weight.UserWeight;
import com.linkloving.rtring_new.db.weight.WeightTable;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.CountCalUtil;
import com.linkloving.rtring_new.utils.DateSwitcher;
import com.linkloving.rtring_new.utils.ScreenHotAsyncTask;
import com.linkloving.rtring_new.utils.SwitchUnit;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.utils.manager.AsyncTaskManger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightActivity extends ToolBarActivity implements View.OnClickListener {
    final int Request_Code_ForChageWeight=1000;
    private ArrayList<Entry> mentries=new ArrayList<>();
    private static final String TAG = WeightActivity.class.getSimpleName();
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> weekDataAsync = null;
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> monthDataAsync = null;
    /** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
    private AsyncTask<Object, Object, Object> yearDataAsync = null;
    //存取用户体重信息的集合
    List<UserWeight> weekList=new ArrayList<>();
    List<UserWeight> monthList=new ArrayList<>();
    List<UserWeight> yearList=new ArrayList<>();
    private LineChart lineChart;//图表实现类
    //先获取传进来的时间
    String timeNow;
    Date date;
    private ViewPager mViewPager;
    private Map<Integer,View> listView;//存放viewpager的view
    /*下方界面的三个adapter,本来准备使用一个但是因为周,月,年,
    每一个页面的个数不一样,所有要使用三个,
    这样就能在切换到,本周,本月,本年的时候不能在往后切了*/
    MyPageadapter weekadapter;
    MyPageadapter monthadapter;
    MyPageadapter yearadapter;
    private DateSwitcher weekSwitcher = null;
    private DateSwitcher monthSwitcher= null;
    private DateSwitcher yearSwitcher=null;
    ProgressDialog pd;
    private UserEntity userEntity;
    private float weight_goal;
    int lastposition=0;
    //周数据的三个,和下面显示的三个时间
    private RadioButton yearButton,weekButton,monthButton,day1,day2,day3;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        MyLog.i(TAG, "修改了体重,要重新刷新图表了" + requestCode+"resultCode="+resultCode+"RESULT_OK="+RESULT_OK);
        //从修改体重界面回来,刷新数据
        if(requestCode==Request_Code_ForChageWeight&&resultCode==RESULT_OK){
            //刷新图表
            MyLog.i(TAG, "修改了体重,要重新刷新图表了" + lastposition);
            initTitle();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //插入点数据测试一下
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);
        listView=new HashMap<>();
        weekSwitcher = new DateSwitcher(DateSwitcher.PeriodSwitchType.week);
        monthSwitcher= new DateSwitcher(DateSwitcher.PeriodSwitchType.month);
        yearSwitcher= new DateSwitcher(DateSwitcher.PeriodSwitchType.year);
        pd=new ProgressDialog(this);
        pd.setMessage(getString(R.string.summarizing_data));
        pd.setCanceledOnTouchOutside(false);
        weight_goal =  Float.parseFloat(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_WEIGHT));
        //测试的时候,自己手动添加的数据
        /* Date lastdate = ToolKits.stringToDate("2016-01-01", ToolKits.DATE_FORMAT_YYYY_MM_DD);
        //今天和最后一条记录差距的天数
        int i = ToolKits.getindexfromDate(lastdate,new Date());
        List<String> list=  ToolKits.getlistDate(lastdate,i);
        List<UserWeight> weightlist = new ArrayList<>();
        for(String date:list){
            float value = (float) (Math.random() * 100) + 3;
            Log.e(TAG, "data是:" + date);
            weightlist.add(new UserWeight(date, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id()+"",value+""));
        }
        WeightTable.saveToSqliteAsync(this, weightlist, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id() + "");*/
        initTitle();
    }
    @Override
    protected void getIntentforActivity() {
        //获取传进来的时间
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        timeNow=getIntent().getStringExtra("time");
        date= ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
        MyLog.i(TAG, "开始时间=======" + new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()));
    }
    @Override
    protected void initView() {
        HideButtonRight(false);
        SetBarTitleText(getString(R.string.weight));
        Button btn = getRightButton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                执行分享操作
                if (ToolKits.isNetworkConnected(WeightActivity.this)) {
                    String filePath = "/sdcard/WeightActivity_v.png";
                    new ScreenHotAsyncTask(filePath, WeightActivity.this).execute(getWindow().getDecorView());
                } else {
                    new AlertDialog.Builder(WeightActivity.this)
                            .setTitle(ToolKits.getStringbyId(WeightActivity.this, R.string.share_content_fail))
                            .setMessage(ToolKits.getStringbyId(WeightActivity.this, R.string.main_more_sycn_fail))
                            .setPositiveButton(R.string.general_ok, null).show();
                }
            }
        });
        btn.setBackground(ContextCompat.getDrawable(WeightActivity.this, R.mipmap.btn_share));


        lineChart = (LineChart) findViewById(R.id.barChart);

        weekButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_weekRb);
        monthButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_monthRb);
        yearButton = (RadioButton) findViewById(R.id.report_page_activity_circleviews_yearRb);

        day1=(RadioButton) findViewById(R.id.day01);
        day2=(RadioButton) findViewById(R.id.day02);
        day3=(RadioButton) findViewById(R.id.day03);
        //图表下方的viewpager
        mViewPager= (ViewPager) findViewById(R.id.my_ViewPager);
        initChat(lineChart);

    }
    //初始化图表
    private void initChat(LineChart mLineChat){
    //设置图表的一些属性
        //图表描述
        mLineChat.setDescription("");
        //去除了中间的字体
        mLineChat.setNoDataText(" ");
        //没有数据显示
        mLineChat.setNoDataTextDescription("No data(⊙o⊙)");
        //是否在折线图上添加边框
        mLineChat.setDrawBorders(false);
        Legend mLegend = mLineChat.getLegend();
        // 设置窗体样式
        mLegend.setForm(Legend.LegendForm.CIRCLE);
        mLegend.setEnabled(false);
        // 字体颜色
        mLegend.setTextColor(getResources().getColor(R.color.white));
        XAxis xAxis = mLineChat.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        //X坐标点描述的颜色
        xAxis.setTextColor(Color.WHITE);
        xAxis.setSpaceBetweenLabels(0);
        //设置下标字体的大小
        //xAxis.setTextSize(5);
        // 隐藏左Y坐标轴
        mLineChat.getAxisLeft().setEnabled(false);
        // 隐藏右Y坐标轴
        mLineChat.getAxisRight().setEnabled(false);
        // 显示表格颜色
        mLineChat.setGridBackgroundColor(getResources().getColor(R.color.yellow_title));
        // 打开或关闭绘制的图表边框。（环绕图表的线） 最外边环绕的线
        mLineChat.setDrawBorders(false);
        // 是否显示表格颜色
        mLineChat.setDrawGridBackground(false);
        // 是否可以拖拽
        mLineChat.setDragEnabled(false);
        // 是否可以缩放
        mLineChat.setScaleEnabled(false);
        // 集双指缩放
        mLineChat.setPinchZoom(false);
        //设置Y方向上动画animateY(int time);
        mLineChat.animateY(3000);

    }
    //创建图表显示的数据
    private LineData getLineData(String timeNow){
       ArrayList<String> xVals=new ArrayList<>();
        if(weekButton.isChecked()){
            String s1=getString(R.string.sun);
            String s2=getString(R.string.mon);
            String s3=getString(R.string.tues);
            String s4=getString(R.string.wed);
            String s5=getString(R.string.thurs);
            String s6=getString(R.string.fri);
            String s7=getString(R.string.sat);
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
        }
        else if(monthButton.isChecked()){
            //生成月的X轴
            Calendar c = Calendar.getInstance();
            Date date=ToolKits.stringToDate(timeNow, ToolKits.DATE_FORMAT_YYYY_MM_DD);
            c.setTime(date);
            int j=c.getActualMaximum(Calendar.DAY_OF_MONTH);
            MyLog.i(TAG, "这个月多少天" + j);
            for(int i = 1;i<=j ;i++){
                switch (i){
                    case 1:
                        xVals.add(i-1,i+"");
                        break;
                    case 5:
                        xVals.add(i - 1, i+"");
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
        }

        else if(yearButton.isChecked()){
            //生成年的X轴
            for(int i=0;i<12;i++){
                xVals.add((i+1)+"");
            }
        }

        addline();//画上目标参考线

        //生成Y轴数据
        LineDataSet lineDataSet = new LineDataSet(getyValues(), " ");
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setDrawValues(false);
        lineDataSet.setCircleSize(2f);// 显示的圆形大小
        lineDataSet.setColor(Color.GREEN);// 显示颜色
        lineDataSet.setCircleColor(Color.GREEN);// 圆形的颜色
        lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色
        lineDataSet.setHighlightEnabled(false);
//        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
//        lineDataSets.add(lineDataSet); // add the datasets
        LineData lineData = new LineData(xVals,lineDataSet);
        return lineData;
    }

        //生成y轴显示的点的数据
        private ArrayList<Entry> getyValues(){
        // y轴的数据
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        if(weekButton.isChecked()){
           //如果查询的数据是空的,就直接将他返回
            if(weekList==null||weekList.size()<=0){
                MyLog.i(TAG,"weekList查询的数据是空的哦");
                return yValues;
            }
                //取出体重里面的时间,和当前的时间做比较,如果是到了今天的时间,就将后面的y轴的值设置为空的
            for(UserWeight userWeight:weekList){
                Calendar c = Calendar.getInstance();
                Date date=ToolKits.stringToDate(userWeight.getTime(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
                c.setTime(date);
                MyLog.i(TAG, "今天是星期几:" + userWeight.getTime() + (c.get(Calendar.DAY_OF_WEEK) - 2));

                if((c.get(Calendar.DAY_OF_WEEK) - 1)==0){

                    yValues.add(new Entry((float) CommonUtils.getScaledDoubleValue(Double.parseDouble(userWeight.getWeight()), 1), 6));

                }else {
                    yValues.add(new Entry((float) CommonUtils.getScaledDoubleValue(Double.parseDouble(userWeight.getWeight()), 1), c.get(c.DAY_OF_WEEK) - 2));
                }

            }
            }
        else if(monthButton.isChecked()){
            SimpleDateFormat sim1 = new SimpleDateFormat("dd");
            //生成月的Y轴
            if(monthList==null||monthList.size()<=0){
                MyLog.i(TAG,"monthList查询的数据是空的哦");
                return yValues;
            }
            for(UserWeight userWeight:monthList){
                Calendar c = Calendar.getInstance();
                Date date=ToolKits.stringToDate(userWeight.getTime(), ToolKits.DATE_FORMAT_YYYY_MM_DD);
                c.setTime(date);
                String date1 = sim1.format(c.getTime());
                int s=Integer.parseInt(date1);
                yValues.add(new Entry((float) CommonUtils.getScaledDoubleValue(Double.parseDouble(userWeight.getWeight()),1), s-1));
            }
        }
        else if(yearButton.isChecked()){
            SimpleDateFormat sim1 = new SimpleDateFormat("MM");
            SimpleDateFormat sim2 = new SimpleDateFormat("yyyy-MM");
                //生成年的Y轴
            if(yearList==null||yearList.size()<=0){
                MyLog.i(TAG,"yearList查询的数据是空的哦");
                return yValues;
            }
            MyLog.i(TAG,"yearList的大小="+yearList.size());
            for(UserWeight userWeight:yearList){
                try {
                    int month=Integer.parseInt(sim1.format(sim2.parse(userWeight.getTime())));
                    MyLog.i(TAG,"yearList查询的数据+month="+userWeight.getTime());
                    yValues.add(new Entry((float) CommonUtils.getScaledDoubleValue(Double.parseDouble(userWeight.getWeight()),1), month-1));
                 } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return yValues;
    }
    @Override
    protected void initListeners() {
        yearButton.setOnClickListener(this);
        weekButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                MyLog.i(TAG, "onPageSelected=" + position);
                lastposition=position;
                changeDate(position);//选定时间button的位置
                changeChat(position);

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

        private void changeChat(final int position) {
            //去查询数据
            if(weekButton.isChecked()){
                int days=ToolKits.getBetweenDay(new Date(0), new Date());//当前时间与系统时间之间差了多少天
                int weekcount=days/7;//系统时间1970/01/01与总的周数
                int week1=days%7;//1970/01/01是周四所以大于三的话右跳到下周
                if(week1 > 3){
                    weekcount=weekcount + 1;
                }

                MyLog.e(TAG,"weekSwitcher相差几周="+(weekcount-position-1));

                weekSwitcher.setBaseTime(ToolKits.getMondayOfThisWeek(ToolKits.getDayFromDate(new Date(), -(weekcount-position-1)*7)));//中间是当前日期

                Date lastDate=weekSwitcher.getEndDate();
                final String startDateString=weekSwitcher.getStartDateStr();
                final String endDateString=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getDayFromDate(lastDate, -1));
                MyLog.e(TAG, weekSwitcher.getSQLBetweenAnd());
                MyLog.e(TAG, "weekSwitcherstartDateString=" + startDateString + "weekSwitcherendDateString=" + endDateString);

                AsyncTask myWeekAsync=new AsyncTask<Object,Object,List<UserWeight>>(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if(pd!=null){
                            pd.show();
                        }
                    }
                    @Override
                    protected List<UserWeight> doInBackground(Object... params) {
                        List<UserWeight> list=WeightTable.queryWeights(WeightActivity.this,userEntity.getUser_id()+"",startDateString,endDateString);
                        weekList.clear();
                        weekList=list;
                        return list;

                    }

                    @Override
                    protected void onPostExecute(List<UserWeight> list) {
                        MyLog.e(TAG, "重画图");
                        lineChart.setData(getLineData(timeNow));
                        lineChart.invalidate();
                        //重新设置viewpager的view的值
                        setDataToView(position,list);
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
                base2.set(GregorianCalendar.DAY_OF_MONTH, 1);
                base2.add(GregorianCalendar.MONTH, -(month - position - 1));
                monthSwitcher.setBaseTime(base2.getTime());
                MyLog.e(TAG, "monthSwitcher结束时间" + monthSwitcher.getEndDateStr());
                Date lastDate=monthSwitcher.getEndDate();
                final String startDateString=monthSwitcher.getStartDateStr();
                final String endDateString=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getDayFromDate(lastDate, -1));
                MyLog.e(TAG, monthSwitcher.getSQLBetweenAnd());
                MyLog.e(TAG, "monthSwitcherstartDateString=" + startDateString + "monthSwitcherendDateString=" + endDateString);
                AsyncTask myMonthAsybc=new AsyncTask<Object,Object, List<UserWeight>>(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if(pd!=null){
                            pd.show();
                        }
                    }
                    @Override
                    protected  List<UserWeight> doInBackground(Object... params) {
                        List<UserWeight> list=WeightTable.queryWeights(WeightActivity.this,userEntity.getUser_id()+"",startDateString,endDateString);
                        monthList=list;
                        return list;
                    }

                    @Override
                    protected void onPostExecute( List<UserWeight> list) {
                        MyLog.e(TAG, "重画图");
                        lineChart.setData(getLineData(timeNow));
                        lineChart.invalidate();
                        //重新设置viewpager的view的值
                        setDataToView(position,list);
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

            if(yearButton.isChecked()){
                //年数据
                SimpleDateFormat sim3 = new SimpleDateFormat("yyyy");
                final int year = Integer.parseInt(sim3.format(new Date()));//当前年份
                int countyear = year - 1970;
                GregorianCalendar base2 = new GregorianCalendar();
                base2.set(GregorianCalendar.DAY_OF_YEAR, 1);
                base2.add(GregorianCalendar.YEAR, -(countyear - position - 1));
                yearSwitcher.setBaseTime(base2.getTime());
                MyLog.e(TAG, "yearSwitcher结束时间" + yearSwitcher.getEndDateStr());
                Date lastDate=yearSwitcher.getEndDate();
                final String startDateString=yearSwitcher.getStartDateStr();
                final String endDateString=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(ToolKits.getDayFromDate(lastDate, -1));
                MyLog.e(TAG, "yearSwitcher"+yearSwitcher.getSQLBetweenAnd());
                MyLog.e(TAG, "yearSwitcherstartDateString=" + startDateString + "yearSwitcherendDateString=" + endDateString);
                AsyncTask myYearAsync=new AsyncTask<Object,Object,List<UserWeight>>(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if(pd!=null){
                          pd.show();
                        }
                    }
                    @Override
                    protected List<UserWeight> doInBackground(Object... params) {
                        List<UserWeight> list=WeightTable.queryWeights(WeightActivity.this,userEntity.getUser_id()+"",startDateString,endDateString);
                        return aveargeUserWeightBymonth(list);
                    }
                    @Override
                    protected void onPostExecute(List<UserWeight> list) {
                        MyLog.e(TAG, "重画图");
                        yearList=list;
                        lineChart.setData(getLineData(timeNow));
                        lineChart.invalidate();
                        //重新设置viewpager的view的值
                        //要处理数据,将每个月的数据去显示出来
                        setDataToView(position,list);
                        if(pd!=null&&pd.isShowing()){
                            pd.dismiss();
                        }
                        AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
                    }
                };
                if (yearDataAsync != null)
                    AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(weekDataAsync, true);
                    AsyncTaskManger.getAsyncTaskManger().addAsyncTask(weekDataAsync = myYearAsync);
                    myYearAsync.execute();
            }
        }
        private void initTitle() {
        //周数据
         if (weekButton.isChecked()) {
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
             mViewPager.setCurrentItem(weeknow-1);
            }

        else if(monthButton.isChecked()){
             SimpleDateFormat sim1 = new SimpleDateFormat("MM");
             SimpleDateFormat sim3 = new SimpleDateFormat("yyyy");
             int year = Integer.parseInt(sim3.format(new Date()));
             int nowyear = Integer.parseInt(sim3.format(date));
             int s1 = Integer.parseInt(sim1.format(date));//传进来时间的月份
             int s2 = Integer.parseInt(sim1.format(new Date()));//当前时间的月份
             int count = (year - 1970) * 12 + s2 + 1;
             monthadapter = new MyPageadapter(count);
             mViewPager.setAdapter(monthadapter);
             mViewPager.setCurrentItem((nowyear - 1970) * 12 + s1);//计算出当前时间应该是第几个月
         }
        else if(yearButton.isChecked()){
             //年数据
             SimpleDateFormat sim3 = new SimpleDateFormat("yyyy");
             int year = Integer.parseInt(sim3.format(new Date()));//当前年份
             int nowyear = Integer.parseInt(sim3.format(date));
             int countyear =year-1970;
             yearadapter=new MyPageadapter(countyear);
             mViewPager.setAdapter(yearadapter);
             MyLog.i(TAG, "当前的年份到1970" + countyear + "现在的时间="+(nowyear-1970-1));
             mViewPager.setCurrentItem(nowyear-1970-1);//计算出当前时间应该是第几个月
         }
    }
    //viewpage在切换过程中更改button的字样
    private void changeDate(int position){
        if(weekButton.isChecked()){
            //选定的是周数据体重
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

        if(monthButton.isChecked()){
            //月数据
            //根据position当前是第几月  代表的是第几个月
            //当前的总共的月
            int month=monthadapter.getCount();
            MyLog.e(TAG,"month="+month+"position="+position);
            SimpleDateFormat sim2 = new SimpleDateFormat("yyyy-MM");
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

        if(yearButton.isChecked()){
            //年数据
            //根据传来的位置,去判断当前的年数是多少
            int year=yearadapter.getCount();//当前的总共的月

            MyLog.e(TAG,"year="+year+"position="+position);

            SimpleDateFormat sim3 = new SimpleDateFormat("yyyy");
            if(position==year-1){
                //传进来的是本月的数据
                //当前是今年
                day1.setText(getString(R.string.lastyear));
                day2.setText(getString(R.string.thisyear));
                day2.setChecked(true);
                day3.setText("");
            } else if(position==year-2) {
                //去年
                GregorianCalendar base1= new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.YEAR, -2);
                String s=sim3.format(base1.getTime());
                day1.setText(s);
                day2.setText(getString(R.string.lastyear));
                day2.setChecked(true);
                day3.setText(getString(R.string.thisyear));

            }else if(position==year-3){

                GregorianCalendar base1 = new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.YEAR, -3);
                String days1=sim3.format(base1.getTime()) ;//相差三个月
                GregorianCalendar base2 = new GregorianCalendar();
                base2.setTime(new Date());
                base2.add(GregorianCalendar.YEAR, -2);
                String days2=sim3.format(base2.getTime()) ;//相差两个个月
                day1.setText(days1);
                day2.setText(days2);//相差两个月
                day2.setChecked(true);
                day3.setText(getString(R.string.lastyear));

            }else{
                //相差了month-position-1
                GregorianCalendar base1= new GregorianCalendar();
                base1.setTime(new Date());
                base1.add(GregorianCalendar.YEAR, -(year-position));
                GregorianCalendar base2 = new GregorianCalendar();
                base2.setTime(new Date());
                base2.add(GregorianCalendar.YEAR,-(year-position-1));
                GregorianCalendar base3 = new GregorianCalendar();
                base3.setTime(new Date());
                base3.add(GregorianCalendar.YEAR, -(year-position-2));
                String days1=sim3.format(base1.getTime()) ;//当前年的前一年
                String days2=sim3.format(base2.getTime()) ;//当前年
                String days3=sim3.format(base3.getTime());//当前年的后一年
                day1.setText(days1);
                day2.setText(days2);
                day2.setChecked(true);
                day3.setText(days3);
            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.report_page_activity_circleviews_weekRb:
                initTitle();
                break;
            case R.id.report_page_activity_circleviews_monthRb:
                initTitle();
                break;
            case R.id.report_page_activity_circleviews_yearRb:
                initTitle();
                break;
        }
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
            View view=getView();
            view.setId(position);
            listView.put(position, view);
            container.addView(view);
            return view;
        }
    }

    private View getView() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.weight_viewpager, null);
        return view;
    }


    private void setDataToView(final int position, final List<UserWeight> myUserWeights){
        UserWeight userWeight  = WeightTable.queryWeightByDay(WeightActivity.this,MyApplication.getInstance(WeightActivity.this).getLocalUserInfoProvider().getUser_id() + "",null);
        //当天的体重
        final float weightNow ;
        if(userWeight==null){
            weightNow = 0;
        }else{
            weightNow = Float.parseFloat(userWeight.getWeight());
        }
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view=mViewPager.findViewById(position);
        LinearLayout shuju = (LinearLayout) view.findViewById(R.id.shuju);//有数据的时候显示
//        LinearLayout nulldata = (LinearLayout) view.findViewById(R.id.null_data_LL); //没数据的时候显示
        LinearLayout titlelaout = (LinearLayout) view.findViewById(R.id.title_layout); //有数据的时候显示
        //距离目标体重差距
        final TextView differenceWeight= (TextView) view.findViewById(R.id.step_count);

        final TextView unit=(TextView)view.findViewById(R.id.unit_kg);
        final TextView title=(TextView)view.findViewById(R.id.data_count_title);

        if (SwitchUnit.getLocalUnit(WeightActivity.this) == ToolKits.UNIT_GONG) {
            unit.setText(getString(R.string.unit_kilogramme));
        }else {
            unit.setText(getResources().getString(R.string.unit_pound));
        }
        TextView changeGoalWeight=(TextView) view.findViewById(R.id.goal);
        changeGoalWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View weightlayout = LayoutInflater.from(WeightActivity.this).inflate( R.layout.dialog_layout_sport, null);
                final EditText editWeight = (EditText) weightlayout.findViewById(R.id.show_msg_sport_goal);
                android.app.AlertDialog dialog6 = new android.app.AlertDialog.Builder(WeightActivity.this)
                        .setTitle(ToolKits.getStringbyId(WeightActivity.this, R.string.change_weight_goal))
                        .setView(weightlayout)
                        .setPositiveButton(ToolKits.getStringbyId(WeightActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(editWeight.getText().toString().trim())) {
                                            editWeight.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(editWeight);
                                            CountCalUtil.allowCloseDialog(dialog, false);//对话框消失
                                            return;
                                        }else {
                                            weight_goal=Float.parseFloat(editWeight.getText().toString());
                                            PreferencesToolkits.setGoalInfo(WeightActivity.this, PreferencesToolkits.KEY_GOAL_WEIGHT, editWeight.getText().toString());
                                            float mfloat=Math.abs(weightNow-weight_goal);
                                            if(mfloat>0){
                                                title.setText(getString(R.string.distance_to_goal));
                                                NumberFormat nf=new DecimalFormat( "0.0 ");
                                                differenceWeight.setVisibility(View.VISIBLE);
                                                unit.setVisibility(View.VISIBLE);
                                                differenceWeight.setText(nf.format(mfloat));
                                            }else {
                                                title.setText(getString(R.string.achieve_target));
                                                //隐藏后面的view
                                                differenceWeight.setVisibility(View.GONE);
                                                unit.setVisibility(View.GONE);
                                            }
                                            //去刷新图表的界面的参考线
                                            //去刷新和图表相差的值
                                            addline();
                                            CountCalUtil.allowCloseDialog(dialog, true);
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(WeightActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog6.show();
            }
        });

        TextView changeMeWeight=(TextView) view.findViewById(R.id.set_weight);
        changeMeWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入更改体重的界面
               /* startActivityForResult(new Intent(WeightActivity.this,ChangeWeightActivity.class),Request_Code_ForChageWeight);*/
                View weightlayout = LayoutInflater.from(WeightActivity.this).inflate(
                        R.layout.dialog_layout_sport, null);
                final EditText editWeight;
                editWeight = (EditText) weightlayout.findViewById(R.id.show_msg_sport_goal);
                android.app.AlertDialog dialog6 = new android.app.AlertDialog.Builder(WeightActivity.this)
                        .setTitle(ToolKits.getStringbyId(WeightActivity.this, R.string.body_info_weight))
                        .setView(weightlayout)
                        .setPositiveButton(ToolKits.getStringbyId(WeightActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(editWeight.getText().toString().trim())) {
                                            editWeight.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(editWeight);
                                            CountCalUtil.allowCloseDialog(dialog, false);//对话框消失
                                            return;
                                        }else {
                                            List<UserWeight> weightlist = new ArrayList<>();
                                            float weight =  Float.parseFloat(editWeight.getText().toString());
//                                            if (SwitchUnit.getLocalUnit(WeightActivity.this) != ToolKits.UNIT_GONG) {
//                                                //此时单位是英制的话 需要把输入的值转换再存入数据库
//                                                weight = ToolKits.calcLBS2KG(weight);
//                                            }
                                            weightlist.add(new UserWeight(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()), MyApplication.getInstance(WeightActivity.this).getLocalUserInfoProvider().getUser_id() + "",weight+""));
                                            WeightTable.saveToSqliteAsync(WeightActivity.this, weightlist, MyApplication.getInstance(WeightActivity.this).getLocalUserInfoProvider().getUser_id() + "");
                                            MyApplication.getInstance(WeightActivity.this).getLocalUserInfoProvider().getUserBase().setUser_weight(Integer.parseInt(editWeight.getText()+""));
                                            //去刷新图表的界面的参考线
                                            changeDate(position);
                                            changeChat(position);
                                            //去刷新和图表相差的值
                                            CountCalUtil.allowCloseDialog(dialog, true);
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(WeightActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog6.show();
            }
        });

        ListView listviewWeight = (ListView) view.findViewById(R.id.listview_step);
        //判断数据
       if(myUserWeights==null||myUserWeights.size()<=0){
            //没有体重数据
           titlelaout.setVisibility(View.VISIBLE);
           shuju.setVisibility(View.VISIBLE);
//           nulldata.setVisibility(View.VISIBLE);
           //设置相差多少
           float mfloat=Math.abs(weightNow-weight_goal);

           MyLog.e(TAG, "mfloat="+mfloat);

           if(mfloat>0){
               title.setText(getString(R.string.distance_to_goal));
               differenceWeight.setVisibility(View.VISIBLE);
               unit.setVisibility(View.VISIBLE);
               NumberFormat nf=new DecimalFormat( "0.0 ");
               differenceWeight.setText(nf.format(mfloat));
           }else {
               title.setText(getString(R.string.achieve_target));
               //隐藏后面的view
               differenceWeight.setVisibility(View.GONE);
               unit.setVisibility(View.GONE);
           }
        }else {
            MyLog.e(TAG, "myUserWeights=" + myUserWeights.toString() + "myUserWeights" + myUserWeights.size());
            //有数据
            shuju.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
//            nulldata.setVisibility(View.GONE);
            //设置相差多少
                float mfloat=Math.abs(weightNow-weight_goal);

                MyLog.e(TAG, "mfloat="+mfloat);

                if(mfloat>0){
                    title.setText(getString(R.string.distance_to_goal));
                    differenceWeight.setVisibility(View.VISIBLE);
                    unit.setVisibility(View.VISIBLE);
                    NumberFormat nf=new DecimalFormat( "0.0 ");
                    differenceWeight.setText(nf.format(mfloat));
                }else {
                    title.setText(getString(R.string.achieve_target));
                    //隐藏后面的view
                    differenceWeight.setVisibility(View.GONE);
                    unit.setVisibility(View.GONE);
                }
            MyWeightAdapter adapter=new MyWeightAdapter(WeightActivity.this, myUserWeights);
            listviewWeight.setAdapter(adapter);
        }

    }

    private  class MyWeightAdapter extends BaseAdapter {
        String s1=getString(R.string.sunday);
        String s2=getString(R.string.monday);
        String s3=getString(R.string.tuesday);
        String s4=getString(R.string.wednesday);
        String s5=getString(R.string.thursday);
        String s6=getString(R.string.friday);
        String s7=getString(R.string.saturday);
        String[] weekDays = { s1, s2, s3, s4, s5, s6, s7 };
        private Context mcontext;
        private List<UserWeight> list;
        public MyWeightAdapter(Context context,List<UserWeight> list){
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mcontext).inflate(R.layout.step_listview_item, null);
                vh = new ViewHolder();
                vh.textViewcount= (TextView) convertView.findViewById(R.id.count);
                vh.textViewdate=(TextView) convertView.findViewById(R.id.date);
                vh.textViewpercent=(TextView) convertView.findViewById(R.id.percent);
                vh.textViewweek=(TextView) convertView.findViewById(R.id.week);
                vh.unit_step= (TextView)convertView.findViewById(R.id.unit_step);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.textViewdate.setText(list.get(position).getTime());
            //星期,目标
            //获取本地目标
            if(yearButton.isChecked()){
                vh.textViewweek.setVisibility(View.GONE);
            }else {
                Calendar cal=Calendar.getInstance();
                cal.setTime(ToolKits.stringToDate(list.get(position).getTime(), ToolKits.DATE_FORMAT_YYYY_MM_DD));
                int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
                if (w < 0) {
                    w = 0;
                }
                vh.textViewweek.setText(weekDays[w]);
            }

            UserWeight userWeight=list.get(position);
            if(userWeight!=null&&Float.parseFloat(userWeight.getWeight())!=0)
                vh.textViewcount.setText(CommonUtils.getScaledDoubleValue(Double.parseDouble(userWeight.getWeight()),1)+"");
           //vh.unit_step.setText("kg");

            if (SwitchUnit.getLocalUnit(WeightActivity.this) == ToolKits.UNIT_GONG) {
                vh.unit_step.setText(getString(R.string.unit_kilogramme));
            }else {
                vh.unit_step.setText(getResources().getString(R.string.unit_pound));
            }
            vh.textViewpercent.setVisibility(View.GONE);
            /* int  cal_goal = Integer.parseInt(PreferencesToolkits.getGoalInfo(mcontext, PreferencesToolkits.KEY_GOAL_CAL));
            if(cal_goal==0){
            }else {
                //Math.ceil(Float.parseFloat(money) * 100*1.0f/ money_goal))
                vh.textViewpercent.setText(Math.ceil(calory.getCalory()*100*1.0f/ cal_goal)+"%");
            }*/
            return convertView;
        }
        public class ViewHolder {
            public TextView textViewdate, textViewweek,textViewcount,textViewpercent,unit_step;
        }

    }


    //将一年的体重按每月取平均
    private List<UserWeight> aveargeUserWeightBymonth(List<UserWeight> list){


        Map<Integer,List<UserWeight>> myuserWeight=new HashMap<>();

        List<UserWeight> av_UserWeight=new ArrayList<>();

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM");
        if(list==null||list.size()<=0){
            //没有取到数据
            MyLog.i(TAG,"没有取到数据");
            return av_UserWeight;
        }else {
            MyLog.i(TAG,"取到数据="+list.toString());
        }
        //建立一个集合,大小为12
        for(int i=0;i<12;i++){
            myuserWeight.put(i+1,new ArrayList<UserWeight>());
        }
        for(UserWeight userWeight:list){
            try {
                int month=Integer.parseInt(simpleDateFormat.format(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).parse(userWeight.getTime())));
                MyLog.i(TAG,"month="+month);

                switch (month){
                    case 1:
                        myuserWeight.get(month).add(userWeight);
                        break;
                    case 2:
                        myuserWeight.get(month).add(userWeight);
                        break;
                    case 3:
                        myuserWeight.get(month).add(userWeight);
                        break;
                    case 4:
                        myuserWeight.get(month).add(userWeight);
                        break;
                    case 5:
                        myuserWeight.get(month).add(userWeight);
                        break;
                    case 6:
                        myuserWeight.get(month).add(userWeight);
                        break;
                    case 7:
                         myuserWeight.get(month).add(userWeight);
                        break;
                    case 8:
                        myuserWeight.get(month).add(userWeight);
                        break;
                    case 9:
                        myuserWeight.get(month).add(userWeight);
                        break;
                    case 10:
                        myuserWeight.get(month).add(userWeight);
                        break;
                    case 11:
                       myuserWeight.get(month).add(userWeight);
                        break;
                    case 12:
                        myuserWeight.get(month).add(userWeight);
                        break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //然后去平均
        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
        SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("yyyy-MM");
        for(int i=1;i<13;i++){
            UserWeight m=new UserWeight();
            int size=myuserWeight.get(i).size();
            if(size!=0){
                float count_weight=0;
                for(UserWeight userWeight:myuserWeight.get(i)){
                    //取出体重的总量
                    count_weight=count_weight+Float.parseFloat(userWeight.getWeight());
                }
                try {
                    m.setTime(simpleDateFormat2.format(simpleDateFormat1.parseObject(myuserWeight.get(i).get(0).getTime())));
                    m.setWeight((count_weight / size) * 1.0f + "");
                    av_UserWeight.add(m);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else {

            }
        }
        return av_UserWeight;
    }

    private void addline() {
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.removeAllLimitLines();


        float weight_goal=Float.parseFloat(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_WEIGHT));
        //取出数据里面的最大值
        float f=0;

        mentries=getyValues();

        for(Entry barEntry:mentries){
            MyLog.e(TAG,"barEntry.getVal()="+barEntry.getVal());
            if(barEntry.getVal()>f){
                f=barEntry.getVal();
            }
        }
        MyLog.e(TAG,"weight_goal="+weight_goal+"float f="+f);
        if(weight_goal==0){
            yAxis.removeAllLimitLines();
        }else {
            if((int)weight_goal<(int)f){
                yAxis.setAxisMaxValue(f+10);
            }else {
                yAxis.setAxisMaxValue(weight_goal+10);
            }
            LimitLine limitLine2 = new LimitLine(weight_goal, weight_goal+"");
            limitLine2.setLineColor(Color.WHITE);
            limitLine2.setLineWidth(0.5f);
            limitLine2.setTextColor(Color.WHITE);
            limitLine2.setTextSize(12f);
            limitLine2.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
            yAxis.addLimitLine(limitLine2);
        }

    }
}
