package com.linkloving.rtring_new.logic.UI.help;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.band.ui.DatasProcessHelper;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.db.sport.UserDeviceRecord;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.logic.UI.main.datachatactivity.DetailChartControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zkx on 2016/3/2.
 */
public class HelpActivity extends ToolBarActivity implements View.OnClickListener {
    private static final String TAG = HelpActivity.class.getSimpleName();
    private Random random;//用于产生随机数
    private CombinedChart combbarChart;
    private UserEntity userEntity;
    private RadioButton dayButton,weekButton,monthButton;
    private DetailChartControl detailChartControl;
    String timeNow;
    private List<BRDetailData> detailDatas = new ArrayList<BRDetailData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        detailDatas = DatasProcessHelper.parseSR2BR(UserDeviceRecord.findHistoryForCommon_l(HelpActivity.this, String.valueOf(userEntity.getUser_id()), timeNow, timeNow, false));
        initData();
    }

    @Override
    protected void getIntentforActivity() {
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        timeNow=getIntent().getStringExtra("time");

    }

    @Override
    protected void initView() {
        HideButtonRight(true);
//        SetBarTitleText(getString(R.string.help_activity));
        combbarChart =(CombinedChart )findViewById(R.id.barChart);
        dayButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_dayRb);
        weekButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_weekRb);
        monthButton= (RadioButton) findViewById(R.id.report_page_activity_circleviews_monthRb);
        detailChartControl = (DetailChartControl)findViewById(R.id.activity_sport_data_detail_detailChartView1);


        BarChartCombined(combbarChart);
    }

    @Override
    protected void initListeners() {
        dayButton.setOnClickListener(this);
        weekButton.setOnClickListener(this);
        monthButton.setOnClickListener(this);
    }

    private void initData() {
        //此处可以 但是需要用异步线程实现
        //计算汇总数据
        //开始时间                  从数据库取今天-1的数据
//        String startDateString = TimeUtils.getstartDateTime(-2,new Date());
//        //结束时间                  从数据库取今天-1的数据
//        String endDateString = new SimpleDateFormat(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());
//        //从数据库取昨天~~今天的数据
//        ArrayList<SportRecord> srsOffline = UserDeviceRecord.findHistoryForCommon_l(HelpActivity.this, userEntity.getUser_id(), startDateString, endDateString, false);
//        String dateString = TimeUtils.getstartDateTime(0,new Date());
//        List<DaySynopic> daySynopics = SportDataHelper.creatDaySynopiclist(srsOffline);
//        for(DaySynopic ds:daySynopics){
//            MyLog.e(TAG, ds.toString());
//        }
//        DaySynopicTable.saveToSqlite(HelpActivity.this, SportDataHelper.creatDaySynopiclist(srsOffline),userEntity.getUser_id());
    }

    private void BarChartCombined(CombinedChart barChart){
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
                    xVals.add("12pm");
                    break;
                default: xVals.add("");
                    break;
            }
        }
        barChart.setDrawOrder(new CombinedChart.DrawOrder[] {CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER});
        // 设置比例图标示//  //设置坐标线样式
        Legend mLegend = barChart.getLegend();
        // 设置窗体样式
        mLegend.setForm(Legend.LegendForm.CIRCLE);
        mLegend.setEnabled(false);
        // 字体颜色
        mLegend.setTextColor(getResources().getColor(R.color.white));
        // 设置背景
        barChart.setBackgroundColor(getResources().getColor(R.color.yellow_title));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);
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
        barChart.setDragEnabled(true);
        // 是否可以缩放
        barChart.setScaleEnabled(true);
        // 集双指缩放
        barChart.setPinchZoom(true);
        //设置Y方向上动画animateY(int time);
        barChart.animateY(3000);
        //图表描述
        barChart.setDescription("");
        
        CombinedData data = new CombinedData(xVals);
        //data.setData(generateLineData());
        data.setData(generateBarData());
        barChart.setData(data);
    }

    public BarData generateBarData() {
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        random=new Random();//随机数
        //一天有48条数据
        for(int i=0;i<48;i++){
            float profit= random.nextFloat()*1000;
            entries.add(new BarEntry(profit,i));
        }
        BarDataSet dataSet;
        dataSet = new BarDataSet(entries, "");
        dataSet.setColor(getResources().getColor(R.color.white));
        // 柱形图顶端字是否显示
        dataSet.setDrawValues(false);
        // 柱形图顶端字体颜色
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        //  柱形图顶端字体大小
        dataSet.setValueTextSize(10f);
        bardata.addDataSet(dataSet);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return bardata;
    }
    private void BarChartCombinedWeek(CombinedChart barChart){
        //横坐标标签
        ArrayList<String> xVals = new ArrayList<String>();
        //查看天时
        for(int i = 0;i < 7 ;i++){
            switch (i){
                case 0:
                    xVals.add("一");
                    break;
                case 1:
                    xVals.add("二");
                    break;
                case 2:
                    xVals.add("三");
                    break;
                case 3:
                    xVals.add("四");
                    break;
                case 4:
                    xVals.add("五");
                    break;
                case 5:
                    xVals.add("六");
                    break;
                default: xVals.add("日");
                    break;
            }
        }
        barChart.setDrawOrder(new CombinedChart.DrawOrder[] {CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER});
        // 设置比例图标示
        Legend mLegend = barChart.getLegend();
        // 设置窗体样式
        mLegend.setForm(Legend.LegendForm.CIRCLE);
        mLegend.setEnabled(false);
        // 字体颜色
        mLegend.setTextColor(getResources().getColor(R.color.white));
        // 设置背景
        barChart.setBackgroundColor(getResources().getColor(R.color.yellow_title));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setDrawAxisLine(false); //是否显示X坐标轴及对应的刻度竖线，默认是true
        xAxis.setLabelsToSkip(40);    //设置坐标相隔多少，参数是int类型
        xAxis.resetLabelsToSkip();
        // 隐藏左Y坐标轴
        barChart.getAxisLeft().setEnabled(true);
        // 隐藏右Y坐标轴
        barChart.getAxisRight().setEnabled(false);
        YAxis yAxis=barChart.getAxisLeft();
        yAxis.setShowOnlyMinMax(true);//只显示最大和最小的两位
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(true);
        barChart.getYMax();
     /*   MyLog.i(TAG, barChart.getHighestVisibleXIndex()+"");
        ImageView imageView=new ImageView(HelpActivity.this);
        imageView.setImageResource(R.mipmap.underline_set);
        barChart.addView(imageView, 1,barChart.getHighestVisibleXIndex());*/
        // 显示表格颜色
        barChart.setGridBackgroundColor(getResources().getColor(R.color.yellow_title));
        // 打开或关闭绘制的图表边框。（环绕图表的线） 最外边环绕的线
        barChart.setDrawBorders(false);
        // 是否显示表格颜色
        barChart.setDrawGridBackground(false);
        // 是否可以拖拽
        barChart.setDragEnabled(true);
        // 是否可以缩放
        barChart.setScaleEnabled(true);
        // 集双指缩放
        barChart.setPinchZoom(true);
        //设置Y方向上动画animateY(int time);
        barChart.animateY(1);
        //紧界线
        MyLog.i(TAG, yAxis.getAxisMaxValue() + "最大值");
        LimitLine limitLine = new LimitLine(900,"1200");
        limitLine.setLineColor(Color.WHITE);
        limitLine.setLineWidth(1f);
        limitLine.setTextColor(Color.BLACK);
        limitLine.setTextSize(12f);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        yAxis.addLimitLine(limitLine);
        //图表描述
        barChart.setDescription("");
        CombinedData data = new CombinedData(xVals);
        data.setData(generateBarDataWeek());
        barChart.setData(data);
    }

    public BarData generateBarDataWeek() {
        BarData bardata = new BarData();
        /**图表具体设置*/
        ArrayList<BarEntry> entries = new ArrayList<>();//显示条目
        random=new Random();//随机数
        //一天有48条数据
        for(int i=0;i<7;i++){
            float profit= random.nextFloat()*1000;
            entries.add(new BarEntry(profit,i));
        }
        BarDataSet dataSet;
        dataSet = new BarDataSet(entries, "");
        dataSet.setColor(getResources().getColor(R.color.white));
        // 柱形图顶端字是否显示
        dataSet.setDrawValues(false);
        // 柱形图顶端字体颜色
        dataSet.setValueTextColor(getResources().getColor(R.color.white));
        //  柱形图顶端字体大小
        dataSet.setValueTextSize(10f);
        bardata.addDataSet(dataSet);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        return bardata;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           case R.id.report_page_activity_circleviews_dayRb:
               BarChartCombined(combbarChart);
               MyToast.showShort(HelpActivity.this, "按日查找");
            break;
           case R.id.report_page_activity_circleviews_weekRb:
               BarChartCombinedWeek(combbarChart);
               MyToast.showShort(HelpActivity.this,"按周查找");
               break;
            case R.id.report_page_activity_circleviews_monthRb:
                MyToast.showShort(HelpActivity.this,"按月查找");
                break;
        }

    }
}
