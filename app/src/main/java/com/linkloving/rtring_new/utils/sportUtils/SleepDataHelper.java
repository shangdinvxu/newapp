package com.linkloving.rtring_new.utils.sportUtils;

import android.content.Context;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.rtring_new.db.sport.UserDeviceRecord;
import com.linkloving.rtring_new.db.summary.DaySynopicTable;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.TimeZoneHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo.wang on 2016/4/9.
 */
public class SleepDataHelper {
    private static final String TAG = SleepDataHelper.class.getSimpleName();
    public static final int WALKING = 1;
    public static final int RUNNING = 2;
    public final static int ACTIVE = 3;
    public final static int LIGHT_SLEEP = 4;
    public final static int DEEP_SLEEP = 5;
    public final static int REAL_IDLE = 6;
    ArrayList<SportRecord> myDayData=new ArrayList<SportRecord>();//按照指定时期从数据库查到的数据
    List<BRDetailData> result;
    ArrayList<SleepData> mySleepData=new ArrayList<>();//存储当天睡眠的数据,画图表用到的数据就是这个
    //询这天的数据
    public SleepDataHelper(Context context, String acountUidOfOwner, String startDate, String endDate, boolean isSleep){
        getDayData(context, acountUidOfOwner, startDate, endDate, isSleep);
    }
    //查询获取到指定日期时间内的明细数据
    private void getDayData(Context context, String acountUidOfOwner, String startDate, String endDate, boolean isSleep){
        myDayData= UserDeviceRecord.findHistoryForCommon_l(context, acountUidOfOwner, startDate, endDate, false);
        MyLog.i(TAG, context + acountUidOfOwner + startDate + endDate);
        MyLog.i(TAG, "查询到的日明细数据" + myDayData.size());
        ArrayList<SportRecord> data=new ArrayList<>();
        for(SportRecord s:myDayData){
            if(Integer.parseInt(s.getState())==6){
                data.add(s);
            }
        }
        myDayData.removeAll(data);
        MyLog.i(TAG, "查询到的日明细数据" + myDayData.size());
        //result=DatasProcessHelper.parseSR2BR(myDayData);
        //MyLog.i(TAG, "查询到的日明细数据" + result.size()+"    "+result.toString());
    }
    //分析今天的数据,将其根据状态,分为多段,分段完成后,然去分析分段好的数据,如果两段睡眠数据之间夹了一段运动的数据,看看这段运动数据的时间是否超过一段时间,如果时间
    //超过了5分钟,则视为这两段睡眠是两次,如果是小于5分钟,则将这两段时间设置为同一次睡眠中间醒来多长时间
    public ArrayList<SleepData> anylyzeDetailData(){
        int i=0;
       for(;i<result.size();i++){

           MyLog.i(TAG," 此时的i="+i);
           //寻找第一条sleep数据
            if(result.get(i).isSleep()){
                MyLog.i(TAG," //寻找第一条sleep数据i="+i);
                //睡眠开始
                SleepData sleepData=new SleepData();
                sleepData.setSleepBegintime(String.valueOf(result.get(i).getBegin()));
                ArrayList<SleepAwake> sleepAwakes=new ArrayList<>();//记录睡醒的次数
                int sleepduration=result.get(i).getDuration();
                int j=i+1;
                sleep:for(;j<result.size();j++){
                    MyLog.i(TAG," 此时的j="+j);
                    if(result.get(j).isSleep()){
                        //还在睡觉
                        MyLog.i(TAG," ////还在睡觉j="+j);
                        sleepduration=result.get(j).getDuration()+sleepduration;
                        if(j==result.size()-1){
                            //已近到最后一条了,这段睡眠,一直持续到最后
                            sleepData.setDuration(sleepduration);
                            sleepData.setSleepAwake(sleepAwakes);
                            mySleepData.add(sleepData);
                        }
                        i=j;
                        }else{
                        //找到睡觉后出现运动的开始数据
                        int k=j+1;
                        int begintime=result.get(j).getBegin();//记录一下第一次的开始时间,有可能下面要用到
                        int awakeduration=result.get(j).getDuration();//记录醒来的时间啊有多长时间
                        isawake: for(;k<result.size();k++){
                            if(!result.get(k).isSleep()){
                                awakeduration=awakeduration+result.get(k).getDuration();
                                //还在接着运动,目前设置其为五分钟,如果超过五分钟,就判定他本次睡眠已经结束了,要跳出循环了
                                if(awakeduration>60*5){
                                    MyLog.i(TAG," 睡眠已经结束了k="+k);
                                    sleepData.setDuration(sleepduration);
                                    sleepData.setSleepAwake(sleepAwakes);
                                    mySleepData.add(sleepData);
                                    i=k;
                                    break sleep;
                                }else{
                                   //后面没有数据了,但是,时间却小于五分钟

                                    }

                                }else{
                                //不在运动了,又到了睡眠,将该段运动设置为根本睡觉的醒来时间
                                sleepduration=sleepduration+awakeduration;
                                SleepAwake sleepAwake=new SleepAwake();
                                sleepAwake.setDuration(awakeduration);
                                sleepAwake.setBegintime(String.valueOf(begintime));
                                sleepAwakes.add(sleepAwake);
                                //跳出本次循环 isawake
                                MyLog.i(TAG," 睡眠中醒了一段时间="+awakeduration+"此时的k="+j);
                                j=k;
                                break isawake;
                                }
                        }
                        i=j;
                    }

                }
            }
        }
        return mySleepData;
    }

    public ArrayList<SleepData> getMySleepData(){
        //遍历查到的日明细数据,
        //如果是空直接返回
        if(myDayData==null||myDayData.size()<=0){
            return mySleepData;
        }else{
            MyLog.i(TAG,myDayData.toString());
            //不是空的时候,遍历数组,开始分析数据,找到第一个sleep状态的开始时间,将其设为,该天第一次睡觉的开始时间
            int i=0;//用来记录第一次跳出循环遍历的时候 I的值,下次循环遍历的时候,就直接从该值开始遍历
            sleep:for (;i<myDayData.size();i++){
                MyLog.i(TAG,"sleep:"+i);
                if(Integer.parseInt(myDayData.get(i).getState())==LIGHT_SLEEP||Integer.parseInt(myDayData.get(i).getState())==DEEP_SLEEP){
                    MyLog.i(TAG,"找到睡眠的开始时间:"+myDayData.get(i).getStart_time()+"此时的i="+i);
                    //找到睡眠的开始时间,新建一个Sleepdata对象,将其开始时间设置为
                    SleepData sleepData=new SleepData();
                    ArrayList<SleepAwake> sleepAwakes=new ArrayList<>();//记录睡醒的次数
                    int sleepduration=Integer.parseInt(myDayData.get(i).getDuration());
                    sleepData.setSleepBegintime(myDayData.get(i).getStart_time());
                    //接着遍历从i+1条开始,看该段睡眠,什么时候结束
                            int j=i+1;
                        awake:for(;j<myDayData.size();j++){
                            MyLog.i(TAG,"此时的j:"+j);
                        if(!(Integer.parseInt(myDayData.get(j).getState())==DEEP_SLEEP||Integer.parseInt(myDayData.get(j).getState())==LIGHT_SLEEP)){
                            MyLog.i(TAG,"找到睡眠的醒来的开始时间:"+myDayData.get(j).getStart_time()+"此时i="+i);
                            int awakeduration=Integer.parseInt(myDayData.get(j).getDuration());
                            String Begintime=myDayData.get(j).getStart_time();//将这段时间设置为改段睡眠,醒来的开始时间
                               isawake: for(int k=j+1;k<myDayData.size();k++){
                                   if(Integer.parseInt(myDayData.get(k).getState())==LIGHT_SLEEP||Integer.parseInt(myDayData.get(k).getState())==DEEP_SLEEP){
                                           //又进入了睡眠
                                        // awakeduration=awakeduration+Integer.parseInt(myDayData.get(k).getDuration());
                                         if(awakeduration>60*5){
                                           //上一段的睡眠时间结束,又进入了下一段睡眠
                                             sleepData.setDuration(sleepduration);
                                             if(sleepAwakes!=null&&sleepAwakes.size()>0){
                                                 sleepData.setAwake(sleepAwakes.size());
                                                 sleepData.setSleepAwake(sleepAwakes);
                                             }
                                             mySleepData.add(sleepData);
                                             i=k-1;//因为跳出循环的额时候,i++了,所以减一,为了让他找到第二次睡眠的开始时间
                                             MyLog.i(TAG," //还是这次睡眠,这次醒来时间结束:k-1="+i);
                                             continue sleep;
                                         }  else {
                                             MyLog.i(TAG," //还是这次睡眠,这次醒来时间结束:k="+k);
                                             sleepduration=sleepduration+awakeduration;//加醒来的时间
                                             SleepAwake sleepAwake=new SleepAwake();
                                             sleepAwake.setBegintime(Begintime);
                                             sleepAwake.setEndtime(myDayData.get(k).getStart_time());
                                             sleepAwake.setDuration(awakeduration);
                                             sleepAwakes.add(sleepAwake);//加入到醒来的集合当中
                                             j=k;//从下一条记录,接着寻找此次睡眠的醒来时间
                                             break isawake;
                                         }
                                   }
                                   else {
                                        //还没进入睡眠,接着计算
                                      awakeduration=awakeduration+Integer.parseInt(myDayData.get(k).getDuration());
                                       if(awakeduration>60*5){
                                            //这次睡眠时间结束
                                           sleepData.setDuration(sleepduration);
                                           if(sleepAwakes!=null&&sleepAwakes.size()>0){
                                               sleepData.setAwake(sleepAwakes.size());
                                               sleepData.setSleepAwake(sleepAwakes);
                                           }
                                           mySleepData.add(sleepData);
                                            i=k;
                                           continue sleep;
                                       }else {
                                           //取出来数据,最后的一段的时间是运动的,而且运动的数据是比五分钟小的
                                            if(k==myDayData.size()-1){
                                                sleepData.setDuration(sleepduration);
                                                if(sleepAwakes!=null&&sleepAwakes.size()>0){
                                                    sleepData.setAwake(sleepAwakes.size());
                                                    sleepData.setSleepAwake(sleepAwakes);
                                                }
                                                mySleepData.add(sleepData);
                                                break sleep;
                                            }
                                       }

                                   }

                            }
                        }else{
                            //还在睡觉,汇总睡眠时间
                            sleepduration=sleepduration+Integer.parseInt(myDayData.get(j).getDuration());
                            MyLog.i(TAG,"还在睡觉,汇总睡眠时间:"+sleepduration);
                            MyLog.i(TAG,"还在睡觉此时的j="+j);
                            if(j==myDayData.size()-1){
                                //到了最后一条还在睡觉
                                sleepData.setDuration(sleepduration);
                                if(sleepAwakes!=null&&sleepAwakes.size()>0){
                                    sleepData.setAwake(sleepAwakes.size());
                                    sleepData.setSleepAwake(sleepAwakes);
                                }
                                mySleepData.add(sleepData);
                                break sleep;
                            }

                          /*  if(Integer.parseInt(myDayData.get(j).getState())==LIGHT_SLEEP||Integer.parseInt(myDayData.get(j).getState())==DEEP_SLEEP){

                                sleepduration=sleepduration+Integer.parseInt(myDayData.get(j).getDuration());
                                MyLog.i(TAG,"还在睡觉,汇总睡眠时间:"+sleepduration);
                                MyLog.i(TAG,"还在睡觉此时的j="+j);
                                continue awake;
                            }
                            else if(Integer.parseInt(myDayData.get(j).getState())==WALKING||Integer.parseInt(myDayData.get(j).getState())==RUNNING){
                                //本次睡觉结束,设置睡眠字段的各个值,结束时间,醒来的次数,还有就是
                                MyLog.i(TAG,"本次睡觉结束,汇总睡眠时间:"+myDayData.get(j).getStart_time()+"sleepduration="+sleepduration);
                                MyLog.i(TAG,"本次睡觉结束j="+j+"此时的状态="+myDayData.get(j).getState());
                                sleepData.setSleepEndtime(myDayData.get(j).getStart_time());
                                sleepData.setDuration(sleepduration);
                                if(sleepAwakes!=null&&sleepAwakes.size()>0){
                                    sleepData.setAwake(sleepAwakes.size());
                                    sleepData.setSleepAwake(sleepAwakes);
                                }
                                mySleepData.add(sleepData);
                                i=j;
                                //跳出循环awake
                                break awake;
                            }*/
                            }
                        }
                    }
                }


            }

        return mySleepData;
    }


    public static String[]  creatsqlSleeptime(Context context,String userId,String datalocal){
        ArrayList<DaySynopic> mDaySynopicArrayList= DaySynopicTable.findDaySynopicRange(context,userId,datalocal,datalocal,String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
        if(mDaySynopicArrayList.size()>0){  //有汇总数据
            MyLog.i(TAG, "日汇总明细:"+mDaySynopicArrayList.get(0).toString());
            DaySynopic mDaySynopic = mDaySynopicArrayList.get(0);
            String gotoBedTime  = mDaySynopic.getGotoBedTime();
            String getupTime  = mDaySynopic.getGetupTime();
            getGotoBedTime(gotoBedTime,getupTime,datalocal);

        }
        return null;
    }

    /**
     * 暂未写完
     * @param gotoBedHour 23:00
     * @param getupHour   07:00
     * @param datalocal   2016-04-13
     * @return
     */
    private static String getGotoBedTime(String gotoBedHour,String getupHour,String datalocal){
        String gotoBedTime =gotoBedHour.replace(":","");
        String getupTime = getupHour.replace(":","");
        String time;
        if(Integer.parseInt(gotoBedTime)>Integer.parseInt(getupTime)){
            time = datalocal+" "+gotoBedTime;
        }else{
            time = datalocal+" ";
        }
        return  time ;
    }


}
