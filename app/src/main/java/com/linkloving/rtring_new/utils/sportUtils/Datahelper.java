package com.linkloving.rtring_new.utils.sportUtils;

import android.content.Context;

import com.example.android.bluetoothlegatt.utils.TimeZoneHelper;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.rtring_new.db.sport.UserDeviceRecord;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leo.wang on 2016/4/5.
 */
public class Datahelper {
    private static final String TAG = Datahelper.class.getSimpleName();
    ArrayList<SportRecord> myDayData=new ArrayList<SportRecord>();//按照指定时期从数据库查到的数据
    ArrayList<SportRecord> myData=new ArrayList<>();//存放时间片的数据
    ArrayList<Integer> list_end_time = new ArrayList<>();//结束时间集合
    ArrayList<Integer> list_start_time=new ArrayList<>();//开始时间集合
    Map<Integer,ArrayList<SportRecord>> mydata=new HashMap<>();
    Context context;
    public Datahelper(Context context, String acountUidOfOwner, String startDate, String endDate, boolean isSleep){
            inittime();
            this.context=context;
            getDayData(context, acountUidOfOwner, startDate, endDate, isSleep);
    }
    //查询获取到指定日期时间内的明细数据
    private void getDayData(Context context, String acountUidOfOwner, String startDate, String endDate, boolean isSleep){
        myDayData=UserDeviceRecord.findHistoryChart(context, acountUidOfOwner, startDate, endDate, false);
        MyLog.i(TAG, context + acountUidOfOwner + startDate + endDate);
        MyLog.i(TAG, "查询到的日明细数据" + myDayData.size());
        double count=0;
       /* if(myDayData!=null&&myDayData.size()>0){
            for(SportRecord sportRecord:myDayData){
                MyLog.i("查询到的日明细数据",sportRecord.toString());
                double walkDistance =  (CommonUtils.getScaledDoubleValue(Double.valueOf(sportRecord.getDistance()), 0));
                //跑步 里程
                count = walkDistance + count;
            }
        }
        MyLog.i(TAG, "总距离"+count);*/
    }
       public  Map<Integer,ArrayList<SportRecord>> getMydata(){
         if(myDayData==null||myDayData.size()<=0)  {
             return null;
         }
        int first=0;
        int dfirst=getDuration(myDayData.get(0).getStart_time()); //yyymmhh转LONG
           for(int m=0;m<list_end_time.size();m++){
           MyLog.i(TAG, "当前开始时间"+list_start_time.get(m)+"____"+dfirst+"______结束时间="+list_end_time.get(m));
           if(dfirst<list_end_time.get(m)&&dfirst>=list_start_time.get(m)){
               first=m;
               break;
           }
             }
        MyLog.i(TAG, "first=" +first);
            //判断从哪个时间段开始查找
            int n=0;
            time:for(int i=first;i<list_end_time.size();i++){
            MyLog.i(TAG, "当前的i=" +i);
            //取出一个时间段
            data:for(int j=n;j<myDayData.size();j++){
            MyLog.i(TAG,"当前的i=" +i+"当前的j=" +j);
            MyLog.i(TAG, "当数据"+myDayData.get(j).toString());
            //取出一条数据,来判断当前的时间应该在那条数据内
            //首先计算,开始时间的是在哪个时间段
            int d=getDuration(myDayData.get(j).getStart_time());
            MyLog.i(TAG, "当前开始时间time"+list_start_time.get(i)+"____"+"______结束时间time="+list_end_time.get(i));
             if(d<list_end_time.get(i)&&d>=list_start_time.get(i)){
                MyLog.i(TAG, "当前开始时间"+list_start_time.get(i)+"____"+d+"______结束时间="+list_end_time.get(i));
                //开始的时间在当前的时间段内
                //接着判断当前的时间加上Duration是不是还在这个时间段,如果在的话直接加上
                if((d+Integer.parseInt(myDayData.get(j).getDuration()))<list_end_time.get(i)){
                    //还在当前的时间段,直接加上就可以了
                    mydata.get(i).add(myDayData.get(j));
                    MyLog.i(TAG, "直接加上" +myDayData.get(j).toString());
                }else{
                    //不在这个时间段,要做处理,
                    // list_end_time-stattime,这么多是在这个时间范围内的,
                    SportRecord cp = new SportRecord();
                    cp.setDevice_id(myDayData.get(j).getDevice_id());
//                    cp.setStart_time(myDayData.get(j).getStart_time());
                    cp.setStart_time(add(myDayData.get(j).getStart_time(), d-list_start_time.get(i)));
                    cp.setState(myDayData.get(j).getState());
                    //数据的处理
                    cp.setDuration(String.valueOf(list_end_time.get(i) - d));
//                  cp.setStep(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * (list_end_time.get(i) - d)));
                    cp.setStep(String.valueOf(Double.parseDouble(myDayData.get(j).getStep()) / Double.parseDouble(myDayData.get(j).getDuration()) * (list_end_time.get(i) - d)));
//                    cp.setDistance(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * (list_end_time.get(i) - d)));
                    cp.setDistance(String.valueOf(Double.parseDouble(myDayData.get(j).getStep()) / Double.parseDouble(myDayData.get(j).getDuration()) * (list_end_time.get(i) - d)));
                    mydata.get(i).add(cp);
                    //除去上面去掉的数据,现在还身下起始时间为下一次开始的时间
                    //剩下的Duration如果是最后一条,很有可能超过了24*3600这时候直接将最后改为一天的最后值
                    int du=0;
                    if(d+Integer.parseInt(myDayData.get(j).getDuration())>24*3600){
                    du=24*3600-list_end_time.get(i)-1;
                    }else {
                       du=d+Integer.parseInt(myDayData.get(j).getDuration())-list_end_time.get(i);
                    }
                    if(du<=0){
                       //只有一种情况会出现就是刚刚好事最后一个时间段了,
                        MyLog.i(TAG, "只有一种情况会出现就是刚刚好事最后一个时间段了="+"du="+du);
                    }else {
                        SportRecord temp = new SportRecord();
                        temp.setDevice_id(myDayData.get(j).getDevice_id());
                        temp.setStart_time(add(myDayData.get(j).getStart_time(), list_end_time.get(i)));   //除去上面去掉的数据,现在还身下起始时间为下一次开始的时间
                        temp.setState(myDayData.get(j).getState());
                        //数据的处理
                        //剩下的时间,再去做个半个小时,为一次跨度的循环,判断是不是在下一个,或者跟下一个的循环
                        i=i+1;//切换到下一个时间段
                        int size=du/1800;   //判断循环几次
                        int dulast=du%1800;//最后一次的
                        temp:for(int k=0;k<=size;k++){
                            MyLog.i(TAG, "最后一次的tempi=" +i);
                            if(k==size){
                                //最后一次
                                temp.setDuration(String.valueOf(dulast));
//                      temp.setStep(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * dulast));
//                      temp.setDistance(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * dulast));
                                temp.setStep(String.valueOf(Double.parseDouble(myDayData.get(j).getStep()) / Double.parseDouble(myDayData.get(j).getDuration()) * dulast));
                                temp.setDistance(String.valueOf(Double.parseDouble(myDayData.get(j).getStep()) / Double.parseDouble(myDayData.get(j).getDuration()) * dulast));
                                MyLog.i(TAG, "最后一次的temp=" + temp.toString());
                                mydata.get(i).add(temp);
                            }else{
                                temp.setDuration(String.valueOf(1800));
                                temp.setStep(String.valueOf(Double.parseDouble(myDayData.get(j).getStep()) / Double.parseDouble(myDayData.get(j).getDuration()) * 1800));
                                temp.setDistance(String.valueOf(Double.parseDouble(myDayData.get(j).getStep()) / Double.parseDouble(myDayData.get(j).getDuration()) * 1800));
//                      temp.setStep(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * 1800));
//                      temp.setDistance(String.valueOf(Integer.parseInt(myDayData.get(j).getStep()) / Integer.parseInt(myDayData.get(j).getDuration()) * 1800));
                                MyLog.i(TAG, "temp=" + temp.toString());
                                mydata.get(i).add(temp);
                                //改变时间
                                temp.setStart_time(add(temp.getStart_time(),1800));   //开始时间加上1800s
                                i++;//切换时间区间
                            }
                        }
                    }
                    }
                n=n+1;//下次直接从j+1条开始
                }else{
                 //跳到下一个时间区域
                break data;
                }

        }

     }
       return mydata;
    }
        //计算当前的时间的Duration,当天开始时间,Duration是0 ,最后的时间Duration是24*3600
        private int getDuration(String stringDate){

        MyLog.i(TAG, "传来计算的时间:" + stringDate);

        String s=TimeZoneHelper.__getLocalTimeFromUTC0(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, stringDate);

        MyLog.i(TAG, "计算的时间:" + s);

        Date date1=ToolKits.stringToDate(s,ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
            SimpleDateFormat s1=new SimpleDateFormat("HH");
            SimpleDateFormat s2=new SimpleDateFormat("mm");
            SimpleDateFormat s3=new SimpleDateFormat("ss");
        int duration=Integer.parseInt(s1.format(date1))*3600+Integer.parseInt(s2.format(date1))*60+Integer.parseInt(s3.format(date1));
        MyLog.i(TAG,"duration:"+duration+"       "+stringDate);
        return duration;
    }

    //将起始时间加上s
    private String add(String date,int s){
        Date d=ToolKits.stringToDate(date,ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
        Date d2=new Date(d.getTime()+s*1000);
        return new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(d2);
    }
    private void inittime(){
        //1800为 30分钟的秒数
                int start=0;
                int end=1800;
             for(int i=0;i<48;i++){
                 list_start_time.add(start);
                 list_end_time.add(end);
                 start=start+1800;
                 end=end+1800;
                 MyLog.i(TAG,"时间后"+start+"   "+end+">>>>>>"+list_end_time.get(i)+"start+"+list_start_time.get(i));
                 mydata.put(i, new ArrayList<SportRecord>());
        }
    }

    //将utc时间转换为本地时间



    }
