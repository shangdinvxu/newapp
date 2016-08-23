package com.linkloving.rtring_new.utils.sportUtils;


import android.content.Context;
import android.util.Log;

import com.eva.epc.common.util.ReflectHelper;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SleepData;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.sleep.DLPSportData;
import com.linkloving.band.sleep.SleepDataHelper;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.band.ui.DatasProcessHelper;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.db.sport.UserDeviceRecord;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.TimeZoneHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class SportDataHelper {
    private final static String TAG = SportDataHelper.class.getSimpleName();

    /**
     * 将设备中读取出来的原始运动数据转成应用层使用的运动数据.
     *
     * @param original
     * @return
     */
    public static List<SportRecord> convertLPSportData(List<LPSportData> original) {
        List<SportRecord> upList = new ArrayList<SportRecord>();
        for (LPSportData sportData : original) {
            try {
                SportRecord sportRecord = new SportRecord();
                sportRecord.setDevice_id("1");
                sportRecord.setDistance(sportData.getDistance() + "");
                sportRecord.setDuration(sportData.getDuration() + "");
                long utcTimestamp = ((long) sportData.getTimeStemp()) * 1000 + ((long) sportData.getRefTime()) * 1000;
                sportRecord.setStart_time(TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, utcTimestamp));
                sportRecord.setStart_time_utc(sportData.getTimeStemp() + sportData.getRefTime() + "");
                MyLog.i(TAG, "传到服务器上的setStart_time" + sportRecord.getStart_time());
                sportRecord.setStep("" + sportData.getSteps());
                sportRecord.setState("" + sportData.getState());
                upList.add(sportRecord);
            } catch (Exception e) {
                Log.w(TAG, "运动数据时间转换成UTC时间时出错，_dt=", e);
            }
        }

        return upList;
    }

    // **** 现在的版本中，无需回填睡眠状态，2015-01-21 by Jack Jiang
//	/**
//	 * 计算睡眠情况并回填到原始数据集合中（以便存放到本地和服务端）。
//	 *
//	 * @param context
//	 * @param upList
//	 */
    public static List<SportRecord> backStauffSleepStatenew(Context context, String userId, List<SportRecord> upList) {
        List<SportRecord> records = new ArrayList<SportRecord>();
        if (upList != null && upList.size() > 0) {
            //********************************************************* 【2】每次同步完数据后计算睡眠情况
            // 计算睡眠是额外要做的事，try catch的目的是保证无论在何种错误下都不应影响数据的保存（切记！）
            try {
                //从第一条数据开始计算10条数据的duration
                long durationSum = DatasProcessHelper.cascatedSportDataDuration(upList);
                final int NEED_DURATION = 90 * 60;
                // 当数据量小于1个半小时时（需要获取额外的数据进行睡眠计算）
                if (durationSum < NEED_DURATION) {
                    Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】当前总数提成时长" + durationSum + "秒，不足" + NEED_DURATION + "秒，需要从网上或本地取前推12小时的数据哦！");
                    // 取出读取到的数据的首行
                    SportRecord firstRow = upList.get(0);
                    // 首行的时间
                    String firstRowStartTime = firstRow.getStart_time();

                    // 计算要前推的数据的起始时间
                    SimpleDateFormat DATE_PATTERN_sdfYYMMDD = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);

                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(DATE_PATTERN_sdfYYMMDD.parse(firstRowStartTime));
                    gc.add(GregorianCalendar.SECOND, -1); // 首行时间-1秒（防重复，因为接下来要用到的查询数据条件是>=start和<=end）

                    String dateTimeEnd_willFetch = DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
                    gc.add(GregorianCalendar.HOUR_OF_DAY, -24);// 前推12小时的时间
                    String dateTimeStart_willFetch = DATE_PATTERN_sdfYYMMDD.format(gc.getTime());

                    Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】数据中首行数据时间" + firstRowStartTime + ", 接下来要取的数据为[" + dateTimeStart_willFetch + "," + dateTimeEnd_willFetch + "]时间范围内的数据！");

                    // 查询（优先本地）前推（12小时）的数据
                    // 查询数据成功，解析之
                    // 查询（从本地）前推（24小时）的数据
                    ArrayList<SportRecord> last24HourDatas = UserDeviceRecord.findHistoryForCommon_l(context, userId, dateTimeStart_willFetch, dateTimeEnd_willFetch, true);

                    int last24HourDatasCount = last24HourDatas.size();

                    Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】前24小时数据读取出来了，记录数：" + last24HourDatasCount);

                    last24HourDatas.addAll(upList); // 将原数据加到前面12小时数据的后面

                    Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】将前24小时数据与现次数据合并了，记录数共：" + last24HourDatas.size());

                    // 计算睡眠后的结果
                    List<DLPSportData> dataAfterCalcuSleep = SleepDataHelper.querySleepDatas2(last24HourDatas);

                    Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】计算完睡眠结果后，记录数共：" + dataAfterCalcuSleep.size());
                    // 将合并计算后的睡眠数据结果的前24小时数据裁剪掉（以便与原始数据一一对应）
                    // 返射ArrayList中的protect方法removeRange以便在性能有保障的情况下裁剪数据
                    com.eva.epc.common.util.ReflectHelper.invokeMethod(ArrayList.class
                            , dataAfterCalcuSleep, "removeRange"
                            , new Class[]{int.class, int.class}
                            // 移除列表中索引在 0（包括）和 willToTrimCount（不包括）之间的所有元素
                            // 如要移除的条数是5（即willToTrimCount=5），则本次移除的索引会是：0、1、2、3、4
                            , new Object[]{0, last24HourDatasCount}, true);
                    Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】计算完睡眠结果后裁剪掉24小时前数据，记录数还余：" + dataAfterCalcuSleep.size());

                    // 清除内存
                    last24HourDatas = null;

                    // 将睡眠算法计算完成的睡眠状态回填（那么这样的话，在组织成日汇总数据时也就能合计出睡眠时间了）
                    records = DatasProcessHelper.putSleepStateFromSleepResult(upList, dataAfterCalcuSleep);
                }
                // 大于1个半小时的数据，直接计算睡眠
                else {
                    // 计算睡眠后的结果
                    List<DLPSportData> dataAfterCalcuSleep = SleepDataHelper.querySleepDatas2(upList);
                    // 将睡眠算法计算完成的睡眠状态回填（那么这样的话，在组织成日汇总数据时也就能合计出睡眠时间了）
                    records = DatasProcessHelper.putSleepStateFromSleepResult(upList, dataAfterCalcuSleep);
                }
            } catch (Exception e) {
                Log.w(TAG, e.getMessage(), e);
            }
        } else {
            Log.w(TAG, "【读取完运动数据后计算睡眠：数据集合是空？】upList=" + upList);
        }
        return records;
    }

    /**
     * 计算睡眠情况并回填到原始数据集合中（以便存放到本地和服务端）。
     *
     * @param context
     * @param upList
     */
    public static List<SportRecord> backStauffSleepState(Context context, String userId, List<SportRecord> upList) {
        List<SportRecord> records = new ArrayList<SportRecord>();
        List<SportRecord> returnrecords = new ArrayList<SportRecord>();
        if (upList != null && upList.size() > 0) {
            //********************************************************* 【2】每次同步完数据后计算睡眠情况
            // 计算睡眠是额外要做的事，try catch的目的是保证无论在何种错误下都不应影响数据的保存（切记！）
            try {
                long durationSum = DatasProcessHelper.cascatedSportDataDuration(upList);
                final int NEED_DURATION = 90 * 60;

//				// 当数据量小于1个半小时时（需要获取额外的数据进行睡眠计算）
//				if(durationSum < NEED_DURATION)
//				{
                // 取出读取到的数据的首行
                SportRecord firstRow = upList.get(0);
                // 首行的时间
                String firstRowStartTime = firstRow.getStart_time();
                // 计算要前推的数据的起始时间
                SimpleDateFormat DATE_PATTERN_sdfYYMMDD = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(DATE_PATTERN_sdfYYMMDD.parse(firstRowStartTime));
                gc.add(GregorianCalendar.SECOND, -1); // 首行时间-1秒（防重复，因为接下来要用到的查询数据条件是>=start和<=end）
                String dateTimeEnd_willFetch = TimeUtils.praseTime(DATE_PATTERN_sdfYYMMDD.format(gc.getTime()));

                gc.add(GregorianCalendar.HOUR_OF_DAY, -24);// 前推24小时的时间
                String dateTimeStart_willFetch = DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
                MyLog.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】数据中首行数据时间" + firstRowStartTime + ", 接下来要取的数据为[" + dateTimeStart_willFetch + "," + dateTimeEnd_willFetch + "]时间范围内的数据！");
                // 查询（从本地）前推（24小时）的数据
                ArrayList<SportRecord> last24HourDatas = UserDeviceRecord.findHistoryForCommon_l(context, userId, dateTimeStart_willFetch, dateTimeEnd_willFetch, true);

                int last24HourDatasCount = last24HourDatas.size();
                Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】24小时数据读取出来了，记录数：" + last24HourDatas.size());

                last24HourDatas.addAll(upList); // 将原数据加到前面12小时数据的后面
                Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】将24小时数据与现次数据合并了，记录数共：" + last24HourDatas.size());

                // 计算睡眠后的结果
                List<DLPSportData> dataAfterCalcuSleep = SleepDataHelper.querySleepDatas2(last24HourDatas);

                Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】计算完睡眠结果后，记录数共：" + dataAfterCalcuSleep.size());
                // 将合并计算后的睡眠数据结果的前12小时数据裁剪掉（以便与原始数据一一对应）
                // 返射ArrayList中的protect方法removeRange以便在性能有保障的情况下裁剪数据
					ReflectHelper.invokeMethod(ArrayList.class, dataAfterCalcuSleep, "removeRange", new Class[]{int.class, int.class}
					// 移除列表中索引在 0（包括）和 willToTrimCount（不包括）之间的所有元素
					// 如要移除的条数是5（即willToTrimCount=5），则本次移除的索引会是：0、1、2、3、4
					, new Object[]{0, last24HourDatasCount}, true);
					MyLog.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】计算完睡眠结果后裁剪掉12小时前数据，记录数还余："+dataAfterCalcuSleep.size());
//					 将睡眠算法计算完成的睡眠状态回填（那么这样的话，在组织成日汇总数据时也就能合计出睡眠时间了）
                records = DatasProcessHelper.putSleepStateFromSleepResult(upList, dataAfterCalcuSleep);
                MyLog.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】回填完数据后，记录数还余："+records.size());
//                if (last24HourDatasCount == 0) {
//                    returnrecords.addAll(records);
//                } else {
//                    for (int i = last24HourDatasCount - 1; i < records.size(); i++) {
//                        returnrecords.add(records.get(i));
//                    }
//                }

                // 清除内存
                last24HourDatas = null;
//				}
//				// 大于1个半小时的数据，直接计算睡眠
//				else
//				{
//					// 计算睡眠后的结果
//					List<DLPSportData> dataAfterCalcuSleep = SleepDataHelper.querySleepDatas2(upList);
//					// 将睡眠算法计算完成的睡眠状态回填（那么这样的话，在组织成日汇总数据时也就能合计出睡眠时间了）
//					records = DatasProcessHelper.putSleepStateFromSleepResult(upList, dataAfterCalcuSleep);
//				}
            } catch (Exception e) {
                Log.w(TAG, e.getMessage(), e);
            }
        } else {
            Log.w(TAG, "【读取完运动数据后计算睡眠：数据集合是空？】upList=" + upList);
        }
        return records;
    }

    //将日期相同的数据整合在一起
    public static DaySynopic creatDaySynopic(ArrayList<SportRecord> srsOffline, String data) {

        List<DaySynopic> daySynopiclist = DatasProcessHelper.convertSportDatasToSynopics(srsOffline);

        for (int i = 0; i < daySynopiclist.size(); i++) {
            if (daySynopiclist.get(i).getData_date().equals(data)) {
                SleepData sleepData = DatasProcessHelper.parseReportForDaySleepDataFromServer(srsOffline, data);
                String gotobedtime = TimeUtils.convertTimeWithPartten(sleepData.getGotoBedTime());
                String getuptime = TimeUtils.convertTimeWithPartten(sleepData.getGetupTime());
                daySynopiclist.get(i).setGotoBedTime(gotobedtime);
                daySynopiclist.get(i).setGetupTime(getuptime);
                daySynopiclist.get(i).setTime_zone(String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
                return daySynopiclist.get(i);
            }
        }
        return null;
    }

    //将日期相同的数据整合在一起
    public static List<DaySynopic> creatDaySynopiclist(ArrayList<SportRecord> srsOffline) {
        long start_time = System.currentTimeMillis() / 1000;
        MyLog.e(TAG, "本次计算开始时间：" + start_time);
        List<DaySynopic> daySynopiclist = DatasProcessHelper.convertSportDatasToSynopics(srsOffline);
        for (DaySynopic ll : daySynopiclist) {
            MyLog.e(TAG, "汇总数据：" + ll.toString());
        }
//		for(int i = 0;i<daySynopiclist.size();i++)
//		{
//				SleepData sleepData = DatasProcessHelper.parseReportForDaySleepDataFromServer(srsOffline, daySynopiclist.get(i).getData_date());
//				MyLog.e(TAG,"睡眠汇总数据："+ sleepData.toString());
//				String gotobedtime = TimeUtils.convertTimeWithPartten(sleepData.getGotoBedTime());
//				String getuptime = TimeUtils.convertTimeWithPartten(sleepData.getGetupTime());
//				daySynopiclist.get(i).setGotoBedTime(gotobedtime);
//				daySynopiclist.get(i).setGetupTime(getuptime);
//				daySynopiclist.get(i).setTime_zone(String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
//			}
        MyLog.e(TAG, "本次计算结束时间：" + (System.currentTimeMillis() / 1000));
        MyLog.e(TAG, "本次计算共耗时：" + (System.currentTimeMillis() / 1000 - start_time));
        return daySynopiclist;
    }

    //将日期相同的数据整合在一起
    public static ArrayList<DaySynopic> creatDaySynopiclist(ArrayList<SportRecord> srsOffline, String localdata) {
        ArrayList<DaySynopic> daySynopiclist = (ArrayList<DaySynopic>) DatasProcessHelper.convertSportDatasToSynopics(srsOffline);
        for (int i = 0; i < daySynopiclist.size(); i++) {
            if (!daySynopiclist.get(i).getData_date().equals(localdata)) {
                daySynopiclist.remove(daySynopiclist.get(i));
            }
        }
        return daySynopiclist;
    }

    //计算出两条日睡眠BRDetailData集合数据
    public static ArrayList<ArrayList<BRDetailData>> creatSleeplist(ArrayList<BRDetailData> srsOffline, int dayindex) {
        ArrayList<ArrayList<BRDetailData>> allList = new ArrayList<>();
        ArrayList<BRDetailData> list_no_other = new ArrayList<>(); //只有18.00之后的数据
        ArrayList<BRDetailData> list_1 = new ArrayList<>(); //前 18.00 到 24.00 的数据
        ArrayList<BRDetailData> list_2 = new ArrayList<>(); //当天 00.00 到 24.00 的数据
        //	这段数据的第一天
        int day_1 = srsOffline.get(0).getDayIndex();
        // 	这段数据的第二天 去判断共多少天
        int day_2 = srsOffline.get(srsOffline.size() - 1).getDayIndex();
        if (day_1 != day_2) {
            //源数据有2天
            for (int i = 0; i < srsOffline.size(); i++) {
                if (srsOffline.get(i).getDayIndex() == day_1) {
                    MyLog.i(TAG, "//第一天的数据" + srsOffline.get(i).toString());
                    //第一天的数据  2160是18点的时间片
                    if (srsOffline.get(i).getBegin() >= 2160 && srsOffline.get(i).getBegin() < 2880) {
                        list_1.add(srsOffline.get(i));
                    }
                }
                if (srsOffline.get(i).getDayIndex() == day_2) {
                    //第二天的数据
                    MyLog.i(TAG, "//第二天的数据" + srsOffline.get(i).toString());
                    if (srsOffline.get(i).getBegin() >= 0 && srsOffline.get(i).getBegin() < 2880) {
                        list_2.add(srsOffline.get(i));
                    }
                }
            }

            if (list_1.size() == 0) {

            } else {
                //第一天最后一条
                list_1.get(list_1.size() - 1).setDuration(2880 - list_1.get(list_1.size() - 1).getBegin());

            }
            allList.add(list_1);
            //第二天第一条

            if (list_1.size() == 0) {

            } else {
                BRDetailData firstData_day2 = list_2.get(0);
                firstData_day2.setSleep(list_1.get(list_1.size() - 1).isSleep());
                firstData_day2.setDuration(list_2.get(0).getBegin());
                firstData_day2.setBegin(0);
                list_2.add(0, firstData_day2);
            }
            allList.add(0, list_1);
            allList.add(1, list_2);
        } else {
            if (srsOffline.get(0).getDayIndex() != dayindex) {
                //第一天有数据
                //源数据有1天 直接返回
                allList.add(srsOffline);
                allList.add(new ArrayList<BRDetailData>());
            } else {
                //第二天有数据
                allList.add(new ArrayList<BRDetailData>());
                allList.add(srsOffline);
            }
        }

	/*	for(BRDetailData detailData:allList.get(0)){
            MyLog.e(TAG, "查询到的前天数据=" + detailData.toString());
		}

		for(BRDetailData detailData:allList.get(1)){
			MyLog.e(TAG, "查询到的今天的数据=" + detailData.toString());
		}*/


        MyLog.i(TAG, list_2.toString());

        return allList;
    }

    /**
     * 离线读取多日睡眠结果.
     * <p/>
     * 注意：查询多天睡眠时，为了节约性能，是假定运动数据在曾今同步时已被计算过睡眠state的
     * ，此处只是将已经过睡眠算法计算过state的睡眠结果进行合计而已，并不需要重新调用睡眠算法！切记！
     * <p/>
     * 目前，数据在从设备中读取出来并被存到本地前，是会去调用睡眠算法计算好并回填state状态的，此次回填state
     * 状态的目的就是用在历史数据里（查询多天睡眠情况的），以便省去重新调用睡眠算法计算的开销，但可能计算结果并没有
     * 使到每天原始数据并重新调用睡眠算法计算精确（因为存在换手机等情况下，因数据不全而计算睡眠不够准确的情况）。
     * <p/>
     * 目前使用回填的睡眠state统计睡眠结果只在历史数据里用到，其它地方如：首页里的睡眠、日详细里的睡眠，都是
     * 通过取得当天原始数据后实时计算的，以便达到计算的绝对准确性（因只计算一天数据，所以性能损失并不大）。
     * @param context
     * @param startDateLocal
     * @param endDateLocal
     * @return
     */
    public static DaySynopic offlineReadMultiDaySleepDataToServer(Context context, final String startDateLocal, final String endDateLocal) {
        DaySynopic daySynopic = new DaySynopic();
        try {
            long tt = System.currentTimeMillis();
            daySynopic.setData_date(startDateLocal);
            String user_id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id() + "";
            //  取出本次查询睡眠的数据（查询多天睡眠时，为了节约性能，是假定运动数据在曾今同步时已被计算过睡眠state的
            // ，此处只是将已经过睡眠算法计算过state的睡眠结果进行合计而已，并不需要重新调用睡眠算法！切记！）
            ArrayList<SportRecord> originalSportDatas = UserDeviceRecord.findHistoryChart(context, user_id, startDateLocal, endDateLocal, true);
            // 计算睡眠
            List<DLPSportData> srs = SleepDataHelper.querySleepDatas2(originalSportDatas);
            DetailChartCountData count = DatasProcessHelper.countSportData(srs, startDateLocal);
            MyLog.e(TAG, "DEBUG【历史数据查询】汇总" + count.toString());
//			[soft_sleep_duration:251.0,deep_sleep_duration:150.0,walking_duration:178.0,walking_steps:6965.0,walking_distance:5074.0,runing_duation:1.0,runing_steps:68.0,runing_distance:98.0,getupTime=1460657160,gotoBedTime=1460645100]
            daySynopic.setWork_duration(count.walking_duration / 2 + "");
            daySynopic.setWork_step(count.walking_steps + "");
            daySynopic.setWork_distance(count.walking_distance + "");
            daySynopic.setRun_duration(count.runing_duation / 2 + "");
            daySynopic.setRun_step(count.runing_steps + "");
            daySynopic.setRun_distance(count.runing_distance + "");
            if (srs == null || srs.size() <= 0) {
                daySynopic.setTime_zone(null);
            } else {
                daySynopic.setTime_zone(String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
            }
            daySynopic.setGetupTime(count.getupTime + "");
            daySynopic.setGotoBedTime(count.gotoBedTime + "");
            double[] ret = new double[2];
            ret[0] = count.soft_sleep_duration / 2 / 60;
            ret[1] = count.deep_sleep_duration / 2 / 60;
            if (ret == null) {
                Log.e(TAG, "DEBUG【历史数据查询-睡眠】" + startDateLocal + "没有睡眠数据！");
            } else {
                daySynopic.setSleepMinute(ret[0] + "");    // 浅睡眠单位：小时
                daySynopic.setDeepSleepMiute(ret[1] + "");// 深睡眠单位：小时
            }
            MyLog.e(TAG, "DEBUG【历史数据查询-睡眠】" + startDateLocal + "日：浅睡" + daySynopic.getSleepMinute() + "小时、深睡" + daySynopic.getDeepSleepMiute() + "小时，计算耗时" + (System.currentTimeMillis() - tt) + "毫秒！");
        } catch (Exception e) {
            Log.w(TAG, e.getMessage(), e);
        }

        return daySynopic;
    }


//
//	public static List<SportRecord> remove_state_6(List<SportRecord> upList) {
//		List<SportRecord> newList = new ArrayList<SportRecord>();
//		for(int i = 0 ;i<upList.size();i++){
//
//			if(!upList.get(i).getState().equals("6")){ //状态为6的时候 不上传到服务端
//				newList.add(upList.get(i));
//			}
//		}
//		upList = null;
//		return newList;
//	}


}
