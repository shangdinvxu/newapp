package com.linkloving.rtring_new.logic.UI.main.datachatactivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.utils.sportUtils.TimeUtils;
import com.zhy.autolayout.AutoRelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by leo.wang on 2016/4/12.
 */
public class DetailChartControl extends RelativeLayout {
    private final String TAG = DetailChartControl.class.getSimpleName();
    //有两个textview,用来显示开始和结束时间
    ///还有一个 LinearLayout
    ImageView dataView;
    ImageView lineView;
    FrameLayout linearLayout;
    TextView timetv;

    Context context;
    int imageWidth;
    int imageHeight;
    /** 每个时间片对应像素宽度（px/片) */
    float xScale;
    float yScale;
    /** // 每个像素xxx分钟 */
    float xlineScale;
    //图表日期
    Date chartDate;
    /** 图片生成器 */
    DetailBitmapCreator detailBitmapCreator2;
    // !!!!!!!!!!!!
    private ArrayList<BRDetailData> detailDatas = new ArrayList<BRDetailData>();

    private List<SportRecord> sportRecords=new ArrayList<>();

    int dayindexNow;
    String timeNow;

    public DetailChartControl(Context context) {
        super(context);
        InitDetailChartControl(context);
    }
    public DetailChartControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitDetailChartControl(context);
    }
    public DetailChartControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitDetailChartControl(context);
    }
    /**
     * 初始化图表
     *
     * @param context
     */
    private void InitDetailChartControl(Context context)
    {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sleepchat, this);
        InitView();
        getViewHigh();

    }
    private void InitView()
    {
        this.dataView = (ImageView) findViewById(R.id.sleep_chat1);
        linearLayout= (FrameLayout) findViewById(R.id.chat);
        lineView = (ImageView) findViewById(R.id.line_chat);
        timetv = (TextView) findViewById(R.id.time);
        linearLayout.setOnTouchListener(new OnTouchListenerImpl());
    }

    private class OnTouchListenerImpl implements OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            MyLog.e(TAG,"onTouch X:"+event.getX());
            String time = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(chartDate);
            //获取今天开始时间的long值
            long timeLong = TimeUtils.stringToLong(time, "yyyy-MM-dd");
            MyLog.e(TAG,"timeLong:"+timeLong);//图表日期的-6点
            //因为从前一天6点开始 所以-6小时
            timeLong = timeLong- 6*3600;
//            MyLog.e(TAG,"event.getX():"+event.getX());
//            MyLog.e(TAG,"dataView.getWidth():"+dataView.getWidth());
//            MyLog.e(TAG,"每个像素的分钟:"+xlineScale);

            MyLog.e(TAG,"chartDate:"+timeLong);//图表日期的-6点
            if(event.getX()> 0 && event.getX()<dataView.getWidth()){
                MyLog.e(TAG,"当前的时间是："+( (long)(event.getX()*xlineScale) * 60+ timeLong ) );
                long nowtime =(long)(event.getX() * xlineScale) * 60+ timeLong ;
                timetv.setText(TimeUtils.formatTimeHHMM(nowtime));
                moveLineViewWithFinger(lineView,event.getX());
                moveTimeViewWithFinger(timetv,event.getX());
            }
            return true;
        }
    }

    private void moveTimeViewWithFinger(View view, float rawX) {
        MyLog.e(TAG,"view.getLeft()："+view.getLeft());
        MyLog.e(TAG,"view.getRight()："+view.getRight());
        MyLog.e(TAG,"rawX："+rawX);
        if(lineView.getLeft()<view.getWidth()/2){
            AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = 0;
            view.setLayoutParams(layoutParams);
            return;
        }
        if((dataView.getWidth()-lineView.getRight())<view.getWidth()/2 ){
            AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = 0;
            view.setLayoutParams(layoutParams);
            return;
        }
        AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int) rawX - view.getWidth() / 2;
        view.setLayoutParams(layoutParams);
    }

    /**
     * 设置View的布局属性，使得view随着手指移动 注意：view所在的布局必须使用RelativeLayout 而且不得设置居中等样式
     *
     * @param view
     * @param rawX
     */
    private void moveLineViewWithFinger(View view, float rawX) {
        AutoRelativeLayout.LayoutParams layoutParams = (AutoRelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = (int) rawX - view.getWidth() / 2;
        view.setLayoutParams(layoutParams);
    }

    private void initValues()
    {
        initXScale();
        detailBitmapCreator2 = new DetailBitmapCreator(context);
        detailBitmapCreator2.initChartParameter(new ChartParameter(xScale, yScale, imageWidth, imageHeight));
    }
    private void initXScale()
    {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;//用屏幕的宽度
        imageWidth =dataView.getWidth();
        xScale = (float) width / (120*30f);// 30小时一屏,120是一个小时的时间片(30s一片)  每个时间片所占的像素
        xlineScale = (60*30f) / (float) imageWidth;// 每个像素xxx分钟
        yScale = imageHeight / 80;
    }
        private void  getViewHigh(){
        final ViewTreeObserver vto = linearLayout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageHeight= linearLayout.getMeasuredHeight();
                int width = DetailChartControl.this.getMeasuredWidth();
                initValues();
                MyLog.i("linearLayout获取到的高度:" + imageHeight+"   "+width);
                return true;
            }
        });
    }
    /**
     * 添加图表新条目（分为 左边添加 和 右边添加 两种）
     * @param
     * @return
     */
    private void AsyncAddDetailChart()
    {
        new AsyncTask<Void,Void,List<Bitmap>>(){
            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bitmaps=new ArrayList<Bitmap>();
                Bitmap bitmapChart2=detailBitmapCreator2.drawDetailChart(detailDatas,dayindexNow);
                bitmaps.add(bitmapChart2);
                return bitmaps;
            }

            @Override
            protected void onPostExecute(List<Bitmap> bitmaps) {
                dataView.setImageBitmap(bitmaps.get(0));
            }
        }.execute();
    }


    public void initDayIndex(ArrayList<BRDetailData> list,int dayindex,Date date)
    {
        dataView.setImageBitmap(null);
        chartDate=date;
        detailDatas=list;
        dayindexNow=dayindex;

        if(detailDatas!=null && detailDatas.size()>0)
            AsyncAddDetailChart();
    }

    /**
     * @param list
     * @param startdata
     */
    /** @deprecated */
    public void initDayIndex(List<SportRecord> list,String startdata)
    {
        dataView.setImageBitmap(null);
        sportRecords=list;
        timeNow=startdata;
        if(sportRecords!=null && sportRecords.size()>0)
            AsyncAddsportRecordDetailChart();
    }
    /**
     * 添加图表新条目（分为 左边添加 和 右边添加 两种）
     */
    /** @deprecated */
    private void AsyncAddsportRecordDetailChart()
    {
        new AsyncTask<Void,Void,List<Bitmap>>(){
            @Override
            protected List<Bitmap> doInBackground(Void... params) {
                List<Bitmap> bitmaps=new ArrayList<Bitmap>();
                Bitmap bitmapChart2=detailBitmapCreator2.drawDetailChart1(sportRecords,timeNow);
                bitmaps.add(bitmapChart2);
                return bitmaps;
            }
            @Override
            protected void onPostExecute(List<Bitmap> bitmaps) {
                dataView.setImageBitmap(bitmaps.get(0));
            }
        }.execute();
    }



}
