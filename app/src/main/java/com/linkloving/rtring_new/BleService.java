package com.linkloving.rtring_new;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.rtring_new.db.sport.UserDeviceRecord;
import com.linkloving.rtring_new.db.summary.DaySynopicTable;
import com.linkloving.rtring_new.logic.UI.device.incomingtel.IncomingTelActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.LocalUserSettingsToolkits;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.DeviceSetting;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.DeviceInfoHelper;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.utils.sportUtils.SportDataHelper;
import com.linkloving.rtring_new.utils.sportUtils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/3/17.
 */
public class BleService extends Service {
//
    private static final String TAG = "BleService";
    //重连的次数
    private int retrycount = 0;

    public static final int REQUEST_ENABLE_BT = 0x10;

    public static final String BLE_STATE_SUCCESS = "com.ble.state";
    public static final String BLE_SYN_SUCCESS = "com.ble.connectsuc";

    public static final String ALARM_KEYER_ACTION = "com.linkloving.alarm_keyer_action";

    private long timer;
    /**
     * 蓝牙连接上到发送指令的延时
     */
    public static final int TIMEOUT = 250;
    private BroadcastReceiver mBroadcastReceiver;
    private BLEProvider provider;
    private UserEntity userEntity;
    private static BleService self = null;

    /**
     * APP是否退出
     * 设置这个变量是为了保证APP退出后 重连上了 不需要进行同步流程
     * 只需要发送一个简短的命令
     */
    private static boolean EXIT_APP = true;

    /**
     * 是否正在同步
     * 如果用户正在同步，再去刷新页面，就不让它走同步流程
     */
    private static boolean IS_SYNING = false;

    /**
     * 当用户自己关闭了蓝牙 后台还在执行30s的查询线程
     * 会导致用户界面堆了许多提醒的弹框 体验 很差
     */
    private static boolean NEED_SCAN = true;

    /**
     * 在OAD的过程中,出现消息提醒会中断OAD
     * 所以在OAD时候 关闭提醒
     */
    private static boolean CANCLE_ANCS = false;

//	public static boolean IS_DESTORY = false;

    public LPDeviceInfo lpDeviceInfo_;


    /********************
     * 》成员变量的get/set方法《
     ***********************/
    public static BleService getInstance(Context context) {
        return self;
    }

    /**
     * 获得当前的蓝牙提供者。
     */
    public BLEProvider getCurrentHandlerProvider() {
        return provider;
    }

    public static boolean isEXIT_APP() {
        return EXIT_APP;
    }

    public static void setEXIT_APP(boolean eXIT_APP) {
        if (eXIT_APP)
            Log.e(TAG, "APP退出...");
        else
            Log.e(TAG, "打开APP");
        EXIT_APP = eXIT_APP;
    }

    public static boolean isNEED_SCAN() {
        return NEED_SCAN;
    }

    public static void setNEED_SCAN(boolean nEED_SCAN) {
        NEED_SCAN = nEED_SCAN;
    }

    public static boolean isCANCLE_ANCS() {
        return CANCLE_ANCS;
    }

    public static void setCANCLE_ANCS(boolean cANCLE_ANCS) {
        CANCLE_ANCS = cANCLE_ANCS;
    }

    /*****》成员变量的get/set方法！end《******/

    /*************************》复写服务生命周期方法《*************************/
    /**
     * 从设备读取到的运动数据的第一条时间
     * 默认是今天 然后从读到的数据去改变
     */
//    private String startDateString = TimeUtils.getendDateTimeUTC(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()), true);

    /**
     * 1.初始化蓝牙
     * 2.创建线程：负责定时检查连接。
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        self = this;
        provider = initBLEProvider(); //初始化bLE提供者
        //启动ble连接并且每一分钟对连接状态检查一遍。确保所有型号的手机处于连接状态。未连接的启动连接。
        bleConnectAndCheckConnectStateInTime();
    }

    /**
     * 主要是检查连接状态并和ble同步数据。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) return START_STICKY;
        int pay_app_msg = intent.getIntExtra("PAY_APP_MSG", 0x01);
        MyLog.e(TAG, "onStartCommand：" + pay_app_msg);
        if (pay_app_msg == 0xff) {
//          其他APP调用此方法就会进入这里
//          ServiceUtils.CLOSE_LINK_BLE(this);
            setNEED_SCAN(false);
            releaseBLE();
        } else if (pay_app_msg == 0x00) {
//          其他APP调用此方法就会进入这里
//          ServiceUtils.OPEN_LINK_BLE(this);
            setNEED_SCAN(true);
            provider.setCurrentDeviceMac(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id());
            if (!provider.isConnectedAndDiscovered()) {
                provider.scanForConnnecteAndDiscovery();
            } else {
                syncAllDeviceInfo(BleService.this);
            }
        } else if (pay_app_msg == 0x02) {
//                if(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider()==null
//                        || MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity()==null
//                        || MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id()==null){
//                    return START_STICKY;
//                }
        } else if (pay_app_msg == 0x01) {
            if(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider()!=null && MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity()!=null){
                //绑定了设备才去扫描
                if(!CommonUtils.isStringEmpty(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())){
                    setNEED_SCAN(true);
                    provider.setCurrentDeviceMac(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id());
                    if (!provider.isConnectedAndDiscovered()) {
                        provider.scanForConnnecteAndDiscovery();
                    } else {
                        provider.keepstate(BleService.this); //保持连接
                    }
                }

            }
        }
        return START_STICKY;
    }

    /**
     * 1.注销定时检测连接的广播接收器
     * 2.注销ble提供者的连接更新广播
     **/
    @Override
    public void onDestroy() {
        if (android.os.Build.MODEL.startsWith("HUAWEI MT7") || android.os.Build.MODEL.startsWith("Galaxy Note4")) {
            //采用定时器没有用到广播
        } else {
            this.unregisterReceiver(mBroadcastReceiver);
        }
        provider.unregisterReciver();
        MyLog.i(TAG, "服务已经onDestroy了......");
//		broadcastUpdate(SERVICE_DESTORY, BleService.this);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /*************************》复写服务生命周期方法，end《*************************/


    /***
     * 初始化蓝牙：
     * 1.复写BLEHandler任务，并创建一个处理器。
     * 2.复写完成创建一个协议定制。
     * 3.创建一个蓝牙提供者（内含协议和消息处理器）。
     */
    private BLEProvider initBLEProvider() {
        //实现一个BLE提供者：主要是复写保存运动数据的功能。
        provider = new BLEProvider(this) {
            /**
             * 每组运动数据读完后的同步保存方法。每次运动数据读取时，因数据量大，而硬件处理能力有限，是
             * 分批次发过来的，应用层每取一次返回的可能是若干条运动数据，取N次直到设备中数据全部读到为止。
             * <p>
             * 子类可重写本方法以便实现此功能。
             */
            //将运动数据LPSportData同步保存至本地数据库。
            @Override
            protected void saveSportSync2DB(List<LPSportData> originalSportDatas) {
                if (originalSportDatas != null && originalSportDatas.size() > 0) {
                    Log.d("【NEW运动数据】", "【NEW运动数据】收到同步保存运动数据请求：条数" + originalSportDatas.size());
//                  // * 将数据保存到本地数据库（以最大可能保证大数据量读取时能最大限度保证数据的可靠性（ * 防止如app崩溃时数据不至于在内存中未来的急被保存而丢失）
                    List<SportRecord> upList = SportDataHelper.convertLPSportData(originalSportDatas);//将原始数据转换成应用层使用的数据模型。
//                  startDateString = TimeUtils.getstartDateTimeUTC(upList.get(0).getStart_time(), true);
//                  MyLog.i(TAG, "第一条的时间" + startDateString +"");
                    for(SportRecord sportRecord:upList){
                        MyLog.i("【NEW运动数据】", "同步上来的原始数据：" + sportRecord.toString() +"");
                    }
//                    UserDeviceRecord.saveToSqlite(BleService.this, upList, MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getUser_id() + "", false);
//                  long t = System.currentTimeMillis(); //方便统计操作使用的时间。
                    List<SportRecord> upList_new = SportDataHelper.backStauffSleepState(BleService.this, MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getUser_id() + "", upList);
                    MyLog.d("【NEW运动数据】", "【NEW运动数据】经过算法后的数据长度是List_new：" + upList_new.size());
                    for(SportRecord sportRecord:upList_new){
                        MyLog.i("【NEW运动数据】", "计算出来的数据：" + sportRecord.toString() +"");
                    }
//                  //调用用户设备记录对象的保存至本地数据库方法。
                    UserDeviceRecord.saveToSqlite(BleService.this, upList_new, MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getUser_id() + "", false);
                    String dateString = TimeUtils.getLocalTimeFromUTC(upList_new.get(0).getStart_time(), ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, ToolKits.DATE_FORMAT_YYYY_MM_DD);
                    MyLog.e("【NEW运动数据】", "第一条数据的日期:"+dateString);
                    ArrayList<DaySynopic> mDaySynopicArrayList = new ArrayList<DaySynopic>();
                    //今天的话 无条件去汇总查询
                    DaySynopic mDaySynopic = SportDataHelper.offlineReadMultiDaySleepDataToServer(BleService.this, dateString, dateString);
                    MyLog.e("【NEW运动数据】", dateString+"号的汇总数据:"+mDaySynopic.toString());
                    if (mDaySynopic.getTime_zone() == null) {
                        return ;
                    }
                    mDaySynopicArrayList.add(mDaySynopic);
                    DaySynopicTable.saveToSqliteAsync(BleService.this, mDaySynopicArrayList, userEntity.getUser_id() + "");
                } else {    //运动数据为空：log显示出来。
                    MyLog.d("【NEW运动数据】", "【NEW运动数据】收到同步保存运动数据请求：但集合是空的，originalSportDatas=" + originalSportDatas);
                }
            }
        };

        //为提供者加载BLE处理器：BLE处理器,复写自定义方法。
        provider.setProviderHandler(new BLEHandler(this) {
            @Override
            protected BLEProvider getProvider() {
                return provider;
            }

            @Override
            protected void handleNotEnableMsg() {
                super.handleNotEnableMsg();
                setNEED_SCAN(false);
            }

            // ** 目前APP的同步逻辑时，当在主界面时，一旦连接上蓝牙（连接的场景有2种：1）每次登陆时的
            // ** 自动同步数据），2）主页上下拉同步时）就同时同步所有信息（全流程同步包括数据）
            // 重写本方法的目的是希望把全流程同步数据放到后台无条件执行（之前是放在Observer里执行，
            // 存在的风险是当切换到其它设置界面时，因重置Observer而导致连接还未完成就被替换了，那么
            // 也就不能调用sync方法了，当用户是用于绑定设备时，则包括绑定数据的定入和提交到服务端就
            // 提交不了了，用户的感受就是没有绑定成功，这就有点恶心了）
            //处理连接成功消息：新建处理线程，停顿150ms后，同步所有设备信息。
            @Override
            protected void handleConnectSuccessMsg() {
                // getLocalUserInfoProvider().setLast_sync_device_id(provider.getCurrentDeviceMac());
                // ** 先调用父类方法
                super.handleConnectSuccessMsg(); //重启新的处理线程ProcessThread，并触发其他后续更新工作。
                //连接上了 重连此时设置为0
                retrycount = 0;
                userEntity = MyApplication.getInstance(BleService.this).getLocalUserInfoProvider();
                try {
                    new Thread().sleep(TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // ** 再默认调用同步方法（即全流程同步）
                // 连接成功就同步数据（包括未定绑定时的绑定设备），但此方法如果是在Observer里，所以还是存
                // 在当未完成时切到其它设置界面时因重置了observer，则可能会使得此同步不会被调用，那么在绑定设备时，
                // 就可能存在不能成功把绑定信息写到设备（及服务端）。放到本方法中在全局唯一的handler中执行
                // 这样很保险，但在诸如设置时的重置如果也要调用这方法的话，就体验不好了，目前先这样吧至少保证绑定等同步
                // 的绝对可靠，只是保存设置时太恶心了（因每次都要同步所有数据，体验就有点恶心了），测试时先这样，不行
                // 的话就取消吧（还是老方法，只是同步又不保险了而已）！
                if (!CommonUtils.isStringEmpty(userEntity.getDeviceEntity().getLast_sync_device_id())) {
                    //退出app后连接成功 只去发送0x5f的命令
                    if (EXIT_APP) {
                        provider.keepstate(BleService.this); //保持连接
                    } else {
                        //app存在过程中 发送0x13
                        BleService.getInstance(BleService.this).syncAllDeviceInfo(BleService.this);
                    }
                }
            }

            //处理连接丢失信息：触发连接丢失处理,清空线程列表和设备信息列表，发送ble同步成功的广播，尝试重新连接蓝牙设备。
            @Override
            protected void handleConnectLostMsg() {
                super.handleConnectLostMsg(); //调用Observe触发连接丢失信息处理：自定义动作。
                IS_SYNING = false;
                MyLog.e(TAG, "BleService 连接断开！");
                provider.clearProess();//同步:线程列队清空和设备信息列表清空。
                //
                if (MyApplication.getInstance(BleService.this).getLocalUserInfoProvider() != null) {
                    //获得本地用户信息提供者非空则：
                    if (!CommonUtils.isStringEmpty(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())
                            &&
                            MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getDevice_type()==MyApplication.DEVICE_WATCH )
                    {
                        //获得最后设备Id非空：
                        if (retrycount < 3) {
                            MyLog.e(TAG, "BleService 正在重连！" + provider.getCurrentDeviceMac());
                            //扫描并重新连接设备。最多尝试3次。
                            if (!CommonUtils.isStringEmpty(provider.getCurrentDeviceMac())) {
                                syncAllDeviceInfoAuto(BleService.this, false, null);
                            }
                            retrycount++;
                        }
                    }
                }
            }

            //数据发送失败的处理：触发执行观察者处理该信息方法，设置同步标志为false，资源释放。
            @Override
            protected void handleSendDataError() {
                super.handleSendDataError();//调用bleProviderObserver并触发响应
                MyLog.e(TAG, "BleService 命令发送失败！");
                IS_SYNING = false; //设置同步标志为false不同步。
                provider.clearProess();
//                provider.getAllDeviceInfoNew(BleService.this);
            }

            //连接失败处理：触发观察者方法，清空处理。
            @Override
            protected void handleConnectFailedMsg() {
                super.handleConnectFailedMsg();
                provider.clearProess();
                MyLog.e(TAG, "BleService 连接过程失败！");
            }

            // 重写此方法是希望把解绑时重置设备id放到后台无条件执行（从而保证在切换到其它设置界面时
            // 也能无条件保证可靠地重置）
            //通知设备解绑成功：
            @Override
            protected void notifyForDeviceUnboundSucess_D() {
                // 解绑成功时，无条件保证重置本地用户存储的mac地址
                MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().setLast_sync_device_id(null);
                super.notifyForDeviceUnboundSucess_D();
                MyLog.e(TAG, "设备解绑成功");
            }

            @Override
            protected void notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
                MyLog.e(TAG, "notifyFor0x13ExecSucess_D");
                if (!IS_SYNING) { // 判断app是否存在状态
                    if (latestDeviceInfo != null) {
                        IS_SYNING = true;  //正在同步 不然用户再次刷新 会发两次指令
                        //==========================初始化数据START==========================//
                        userEntity = MyApplication.getInstance(BleService.this).getLocalUserInfoProvider();
                        MyLog.e(TAG, "电量："+latestDeviceInfo.charge);
                        /**程序初始化的时候,会将此变量设置为false*/
                        setNEED_SCAN(true);
                        lpDeviceInfo_ = latestDeviceInfo;
                        provider.setTimestemp(latestDeviceInfo.timeStamp);
                        provider.setServertime(System.currentTimeMillis());
                        //==========================初始化数据OVER==========================//
                        //保存localvo
                        PreferencesToolkits.updateLocalDeviceInfo(BleService.this, lpDeviceInfo_);
//                        LocalInfoVO LocalInfoVO = PreferencesToolkits.getLocalDeviceInfo(BleService.this);

                        //BLE--->设置身体信息
                        startSetBody();

                        //BLE--->设置消息提醒
                        startSetANCS();

                        MyLog.e(TAG, "notifyFor0x13ExecSucess_D======进入了");
                    } else {
                        //返回的latestDeviceInfo是null的时候 重新获取一次
                        if (provider.isConnectedAndDiscovered()) {
                            provider.getAllDeviceInfoNew(BleService.this);
                        }
                    }
                }
                super.notifyFor0x13ExecSucess_D(latestDeviceInfo);
            }

            /**
             * 设置身体信息
             */
            private void startSetBody() {
//                if (needRegiesterBody())  //判断是否要注册身体信息
                    provider.regiesterNew(BleService.this, DeviceInfoHelper.fromUserEntity(BleService.this, userEntity));
            }

            /**
             * 判断是否要设置身体信息
             * @return
             */
            private boolean needRegiesterBody() {
                if (lpDeviceInfo_.recoderStatus == 1
                        || lpDeviceInfo_.userHeight == Integer.parseInt(userEntity.getUserBase().getUser_height() + "")
                        || lpDeviceInfo_.userWeight == Integer.parseInt(userEntity.getUserBase().getUser_weight() + "")) {
                    return true;
                }
                return false;
            }

            /**
             * 设置消息提醒
             */
            private void startSetANCS() {
                /***********************设置消息提醒**********************/
                MyLog.e(TAG, "===设置消息提醒===");
                DeviceSetting deviceSetting = LocalUserSettingsToolkits.getLocalSetting(BleService.this, userEntity.getUser_id() + "");
                int ancs = deviceSetting.getANCS_value();
                byte[] send_data = IncomingTelActivity.intto2byte(ancs);
                provider.setNotification(BleService.this, send_data);
                /***********************设置消息提醒**********************/
            }

            //消息提醒设置成功
            @Override
            protected void notifyForSetNOTIFYSucess() {
                super.notifyForSetNOTIFYSucess();
                provider.SetClock(BleService.this, DeviceInfoHelper.fromUserEntity(BleService.this, userEntity));
            }

            //闹钟设置成功
            @Override
            protected void notifyForSetClockSucess() {
                super.notifyForSetClockSucess();
                UserEntity userEntity = MyApplication.getInstance(BleService.this).getLocalUserInfoProvider();
                provider.SetLongSit(BleService.this, DeviceInfoHelper.fromUserEntity(BleService.this, userEntity));
            }

            //久坐提醒设置成功
            @Override
            protected void notifyForLongSitSucess() {
                super.notifyForLongSitSucess();
                UserEntity userEntity = MyApplication.getInstance(BleService.this).getLocalUserInfoProvider();
                provider.SetHandUp(BleService.this, DeviceInfoHelper.fromUserEntity(BleService.this, userEntity));
            }

            //勿扰模式设置成功
            @Override
            protected void notifyForSetHandUpSucess() {
                super.notifyForSetHandUpSucess();
                provider.getSportDataNew(BleService.this);
            }

            // 运动数据读取完成
            @Override
            protected void handleDataEnd() {
                super.handleDataEnd();
                MyLog.e(TAG, "【NEW离线数据同步】handleDataEnd" );
                //设置时间指令
                provider.SetDeviceTime(BleService.this);

            }

            //时间设置成功--基本流程完毕
            @Override
            public void notifyForSetDeviceTimeSucess() {
                super.notifyForSetDeviceTimeSucess();
                IS_SYNING = false;
                //在绑定流程会首先去设置时间(但是此时还没有同步到  本地/服务端  就不去设置卡号什么的了)
                if (!CommonUtils.isStringEmpty(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())) {

                    if (lpDeviceInfo_ != null) {  //解决异步线程bug
                        /******拿卡号同时按情况去设置步数START*****/
                        if (lpDeviceInfo_.timeStamp < 365 * 24 * 3600)  //手表时间为1970年的1年内
                        {
                            lpDeviceInfo_.stepDayTotals = MyApplication.getInstance(BleService.this).getOld_step();
                            provider.resetStep(BleService.this, lpDeviceInfo_);
                        }
                        /******拿到卡号同时后按情况去设置步数OVER*****/

                        /**********省电模式START*********/
                        provider.SetPower(BleService.this, DeviceInfoHelper.fromUserEntity(BleService.this, userEntity));
                        /**********省电模式OVER*********/
                    }
                }
            }

            @Override
            protected void notifyForSetPowerSucess() {
                super.notifyForSetPowerSucess();
                provider.getModelName(BleService.this);
                /***读deviceId***/
                provider.getdeviceId(BleService.this);
            }

            @Override
            protected void notifyForModelName(LPDeviceInfo latestDeviceInfo) {
                super.notifyForModelName(latestDeviceInfo);
                if(CommonUtils.isStringEmpty(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id()))
                    return;
                if(latestDeviceInfo.modelName==null){
                    lpDeviceInfo_.modelName= "LW100";
                }else{
                    lpDeviceInfo_.modelName= latestDeviceInfo.modelName;
                }

                //==========================初始化数据OVER==========================//
                //保存localvo
                PreferencesToolkits.updateLocalDeviceInfo(BleService.this, lpDeviceInfo_);
            }

            @Override
            protected void notifyForgetDeviceId_D(String obj) {
                super.notifyForgetDeviceId_D(obj);
                /**********获取卡号*********/
                provider.get_cardnum(BleService.this);
            }
        });
        return provider;
    }

    /**
     * 自动同步所有设备信息：观察者更新，提供者检查蓝牙连接，将设备地址保存至用户信息内，并连接好设备，同步所有设备信息。boolean isScaned
     */
    public static void syncAllDeviceInfoAuto(Context context, boolean needscan, Observer obsForHint) {
        BLEProvider provider = BleService.getInstance(context).getCurrentHandlerProvider();         //获得初始化后的蓝牙提供者。即成员变量的Provider。
        String mac = MyApplication.getInstance(context).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id();

        if(MyApplication.getInstance(context).getLocalUserInfoProvider().getDeviceEntity().getDevice_type()==MyApplication.DEVICE_WATCH){
            needscan = true;//新的版本里面 必须扫描连接
        }else{
            needscan = false;
        }
        //获得用户信息：将当前设备MAC设置到UserEntity的最后同步设备Id变量上。
        if (provider != null) {   //有Ble提供者：
            if (provider.isConnectedAndDiscovered()) {    //有连接则：休眠150毫秒，同步所有设备信息。
                try {
                    new Thread().sleep(TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MyLog.d(TAG, "isConnectedAndDiscovered()==true, 指令将可直接执行。");
                BleService.getInstance(context).syncAllDeviceInfo(context);

            } else {    //没有连接则：提供者重置默认状态，刷新观察者，再重新连接。
                MyLog.d(TAG, "isConnectedAndDiscovered()==false, 即将开始连接过程。。。");
                provider.resetDefaultState();  //设置ble为初始状态:设置状态参数为未连接。
                if (!needscan) {
                    provider.connect_mac(mac);
                } else {
                    provider.scanForConnnecteAndDiscovery(); // syncAllDataHandler);
                }
                if (obsForHint != null)
                    obsForHint.update(null, true);

            }
        }
    }


    //同步所有设备信息：获得所有设备新的信息+用户信息。
    public void syncAllDeviceInfo(Context context) {
        MyLog.i(TAG, "同步所有设备信息：获得所有设备新的信息+用户信息");
        BLEProvider provider = BleService.getInstance(context).getCurrentHandlerProvider();
        provider.getAllDeviceInfoNew(context); //获得设备信息
//        BleSynchronImpl bleSynchronImpl = new BleSynchronImpl(context,provider);
//        bleSynchronImpl.synchronAll();
    }

    /**
     * 按时检测ble连接：每60秒检查一次蓝牙连接并重连。如果用户信息不存在则不进行此动作
     */
    public void bleConnectAndCheckConnectStateInTime() {
        int checkIterval = 60000; //检查间隔时间60秒
        if (android.os.Build.MODEL.startsWith("HUAWEI MT7") || android.os.Build.MODEL.startsWith("Galaxy Note4")) {    //判断机型，如果是华为HUAWEI MT7或者Galaxy Note4机型。采用新Timer线程执行定时检查。
            Timer timer = new Timer();        //每分钟更新一下蓝牙状态
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity() != null) {    //获得本地用户信息提供者并获得最后同步设备的ID非空
                        if (!CommonUtils.isStringEmpty(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id()) && isNEED_SCAN() && !isCANCLE_ANCS()
                                && MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getDevice_type()==MyApplication.DEVICE_WATCH)//蓝牙关闭 和 OAD升级过程中 取消定时器
                        {    //获得本地用户信息提供者，并且成功获取
                            MyLog.e(TAG, "正在Timer扫描...");
                            if (!provider.isConnectedAndDiscovered()) {
                                //如果未发现连接或未连接。。。
                                retrycount = 0;                          // 重置连接次数
                                syncAllDeviceInfoAuto(BleService.this, false, null);
                            } else {
                                //app退出情况下 才去发这个
                                if (EXIT_APP)
                                    provider.keepstate(BleService.this);   //  保持连接
                            }
                        }
                    } else {   //获得本地用户信息提供者并获得最后同步设备的ID为空。
                        MyLog.e(TAG, "该帐号未绑定手表！~~~");
                    }
                }
            }, 0, checkIterval); //60秒更新一下蓝牙。
        } else {
            //如果其他机型：非华为HUAWEI MT7或者Galaxy Note4机型。每隔1分钟发一个广播，处理(完成发现并连接动作，确保状态为连接状态)。
            Intent intent = new Intent(ALARM_KEYER_ACTION); //为Notification准备打开广播，ALARM_KEYER_ACTION的广播,哪里的？？？？。
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
            long firsttime = SystemClock.elapsedRealtime();//从开机到现在的毫秒数,包含睡眠时间。
            timer = System.currentTimeMillis() / 1000; //现在的时间毫秒数，改为了以秒为单位。
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);  //提醒管理器：睡眠时间可用，闹铃每隔1分钟，激活一个sender广播。
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firsttime, checkIterval, sender);
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if (MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity() != null) {
                        ////获得本地用户信息提供者并获得最后同步设备的ID非空                   //获得本地用户信息提供者，并且成功获取。
                        if (!CommonUtils.isStringEmpty(MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id()) && isNEED_SCAN() && !isCANCLE_ANCS()
                                && MyApplication.getInstance(BleService.this).getLocalUserInfoProvider().getDeviceEntity().getDevice_type()==MyApplication.DEVICE_WATCH) {   //蓝牙关闭 和 OAD升级过程中 取消定时器
                            MyLog.e(TAG, "系统当前时间:" + System.currentTimeMillis() / 1000 + "  上次检测时间:" + timer + "  差值:" + (System.currentTimeMillis() / 1000 - timer));
                            if (System.currentTimeMillis() / 1000 - timer > 5) {
                                timer = System.currentTimeMillis() / 1000; //5秒钟更新一下当前时间，用于显示Log.e。
                                if (!provider.isConnectedAndDiscovered()) {  //如果未有连接或发现
                                    retrycount = 0; // 重置连接次数
                                    provider.resetDefaultState();  //重置默认状态
                                    if (!CommonUtils.isStringEmpty(provider.getCurrentDeviceMac()))
                                        syncAllDeviceInfoAuto(BleService.this, false, null);
                                } else {
                                    //如果有连接或者发现连接
                                    //app退出情况下 才去发这个
                                    if (EXIT_APP)
                                        provider.keepstate(BleService.this);  //保持状态。
                                }
                            } else {
                                timer = System.currentTimeMillis() / 1000;  //获得当前时间毫秒数，把时间改为单位为秒。
                            }
                        }
                    } else {
                        MyLog.e(TAG, "该帐号未绑定手表！~~~");

                    }
                }
            };
            IntentFilter filter = new IntentFilter(ALARM_KEYER_ACTION);//动态注册的广播，ALARM_KEYER_ACTION的广播。
            this.registerReceiver(mBroadcastReceiver, filter);
        }
    }

//    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
//
//        @Override
//        public void onSucceed(int what, Response<String> response) {
//            MyLog.i(TAG, "onSucceed");
//            MyLog.e(TAG, "response=" + response.get());
////            JSONObject object=JSON.parseObject((String) response.get());
////            String value=object.getString("returnValue");
//            DataFromServer dataFromServer =JSON.parseObject(response.get(), DataFromServer.class);
//            String value = dataFromServer.getReturnValue().toString();
//            if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
//
//                MyLog.e(TAG, "onSucceed");
//                MyLog.e(TAG, "returnValue" + value);
//
////            boolean success = object.getBoolean("success");
////            boolean success = HttpResponse.praseHttpResponseOK(JSON.toJSONString(response.get()));
//
//                MyLog.e(TAG, "成功" + "value=" + value);
//                switch (what) {
//                    case CommParams.HTTP_UPDATA_CARDNUMBER:
//                        MyLog.e(TAG, "HTTP_UPDATA_CARDNUMBER" + "value=" + value);
//                        //不做处理
//                        break;
//                    case CommParams.HTTP_UPDATA_DEVICEID:
//                        UserEntity userAuthedInfo = new Gson().fromJson(value, UserEntity.class);
//                        MyApplication.getInstance(BleService.this).setLocalUserInfoProvider(userAuthedInfo);
////                        CallServer.getRequestInstance().add(BleService.this, false, CommParams.HTTP_DOWN_USERENETTY, HttpHelper.createUserEntityRequest(userEntity.getUser_id() + ""), httpCallback);
//                        break;
//                    case CommParams.HTTP_DOWN_USERENETTY:
////                    String value = HttpResponse.praseHttpResponseStr(JSON.toJSONString(response.get()));
////                        UserEntity userEntity = JSON.parseObject((String) value, UserEntity.class);
////                        MyApplication.getInstance(BleService.this).setLocalUserInfoProvider(userEntity);
//                        break;
//                }
//            }
//        }
//
//        @Override
//        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
//
//        }
//
//    };


    /**
     * 释放蓝牙相关的所有资源.
     */
    public void releaseBLE() {
        MyLog.e(TAG, "releaseBLE");

        if (provider.isConnectedAndDiscovered()) {
            provider.release();
            provider.resetDefaultState();
        }
        provider.setCurrentDeviceMac(null);
        provider.setmBluetoothDevice(null);
    }


    private void broadcastUpdate(final String action, Context context) {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
