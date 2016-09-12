package com.linkloving.rtring_new.logic.UI.main.boundwatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEListHandler;
import com.example.android.bluetoothlegatt.BLEListProvider;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.main.materialmenu.CommonAdapter;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.notify.NotificationCollectorService;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.ModelInfo;
import com.linkloving.rtring_new.utils.LanguageHelper;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BLEListActivity extends ToolBarActivity {
    public static final String TAG = BLEListActivity.class.getSimpleName();
    private BLEListProvider listProvider;
    private BLEProvider provider;
    private BLEListHandler handler;

    public  String modelName = null;

    public static final int sendcount_MAX = 15;
    private int sendcount = 0;
    public static final int sendcount_time = 2000;
    String BoundFailMSG;
    private ListView mListView;
    private List<DeviceVO> macList = new ArrayList<DeviceVO>();
//
    private macListAdapter mAdapter;

    private AlertDialog dialog_bound;
    private Dialog dialog_server;
    private ProgressDialog progressDialog;
    private Button boundBtn;

    public static final int RESULT_OTHER = 1000;
    public static final int RESULT_BACK=999;
    public static final int RESULT_FAIL=998;
    public static final int RESULT_NOCHARGE = 997;
    public static final int RESULT_DISCONNECT = 996;

    public static final int REFRESH = 0x123;

    private int button_txt_count = 40;
    private Object[]  button_txt={button_txt_count};
    private Timer timer;
    private BLEHandler.BLEProviderObserverAdapter observerAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blelist);
        dialog_server = new Dialog(getApplicationContext());
        dialog_server.setTitle(getString(R.string.general_loading));
        observerAdapter = new BLEProviderObserver();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        handler = new BLEListHandler(BLEListActivity.this)
        {
            @Override
            protected void handleData(BluetoothDevice device)
            {
                for (DeviceVO v : macList)
                {
                    if (v.mac.equals(device.getAddress()))
                        return;
                }
                DeviceVO vo = new DeviceVO();
                vo.mac = device.getAddress();
                vo.name = device.getName();
                vo.bledevice = device;
                macList.add(vo);
                mAdapter.notifyDataSetChanged();
            }
        };
        listProvider = new BLEListProvider(this, handler);
        mAdapter = new macListAdapter(this, macList);
        initView();
        listProvider.scanDeviceList();
    }



    @Override
    protected void getIntentforActivity() {

    }
    @Override
    protected void initView()
    {
        HideButtonRight(false);
        SetBarTitleText(getString(R.string.bound_ble_list));
        Button btn = getRightButton();
        ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
        layoutParams.width=200;
        layoutParams.height=200;
        btn.setLayoutParams(layoutParams);
        btn.setText(getString(R.string.bound_ble_refresh));
        btn.setTextColor(getResources().getColor(R.color.white));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macList.clear();
                mAdapter.notifyDataSetChanged();
                listProvider.scanDeviceList();
            }
        });
        mListView = (ListView) findViewById(R.id.ble_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)
            {

                provider.setCurrentDeviceMac(macList.get(index).mac);
                provider.setmBluetoothDevice(macList.get(index).bledevice);


                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.bound_xiangxi, (LinearLayout) findViewById(R.id.bound_xiangxi));
                FrameLayout frameLayout = (FrameLayout) layout.findViewById(R.id.bound_step_2);
                if(!LanguageHelper.isChinese_SimplifiedChinese()){
                    frameLayout.setVisibility(View.GONE);
                }
                boundBtn = (Button) layout.findViewById(R.id.scan);
                button_txt[0] = button_txt_count;

                dialog_bound = new AlertDialog.Builder(BLEListActivity.this)
                        .setView(layout)
                        .setTitle(R.string.portal_main_isbounding)
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {

                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
                                {
                                    return true;
                                }
                                else
                                {
                                    return false;
                                }
                            }
                        })
                        .setCancelable(false).create();
                progressDialog=new ProgressDialog(BLEListActivity.this);
                provider.connect();

                dialog_bound.show();


                if(dialog_bound!=null &&  dialog_bound.isShowing()){
                    timer = new Timer(); // 每分钟更新一下蓝牙状态
                    timer.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            boundhandler.post(butttonRunnable);
                            button_txt_count-- ;
                            Log.e(TAG, "Timer开始了");
                            if(button_txt_count < 0){
                                timer.cancel();
                            }
                        }
                    }, 0, 1000);
                }

                boundBtn.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        dialog_bound.dismiss();
                        if(timer!=null) {
                            timer.cancel();
                        }
                        provider.clearProess();
                        BleService.getInstance(BLEListActivity.this).releaseBLE();
                        setResult(RESULT_BACK);
                        finish();
                    }
                });

            }
        });
    }

    @Override
    protected void initListeners() {

    }

    class DeviceVO
    {
        public String mac;
        public String name;
        public BluetoothDevice bledevice;

    }
    class macListAdapter extends CommonAdapter<DeviceVO>
    {
        public class ViewHolder
        {
            public TextView mac;
        }

        ViewHolder holder;

        public macListAdapter(Context context, List<DeviceVO> list)
        {
            super(context, list);
        }

        @Override
        protected View noConvertView(int position, View convertView, ViewGroup parent)
        {
            convertView = inflater.inflate(R.layout.list_item_ble_list, parent, false);
            holder = new ViewHolder();
            holder.mac = (TextView) convertView.findViewById(R.id.activity_sport_data_detail_sleepSumView);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        protected View hasConvertView(int position, View convertView, ViewGroup parent)
        {
            holder = (ViewHolder) convertView.getTag();
            return convertView;
        }

        @Override
        protected View initConvertView(int position, View convertView, ViewGroup parent)
        {
            String mac = list.get(position).mac.substring(list.get(position).mac.length() - 5, list.get(position).mac.length());
            holder.mac.setText("ID:   " + removeCharAt(mac, 2));
            return convertView;
        }

    }
    public static String removeCharAt(String s, int pos)
    {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    Runnable butttonRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            Message msg = new Message();
            msg.what = REFRESH;
            boundhandler.sendMessage(msg);
        };
    };


    Runnable boundRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            Message msg = new Message();
            msg.what = 0x333;
            boundhandler.sendMessage(msg);
        };
    };

    Handler boundhandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0x333:
                    provider.requestbound(BLEListActivity.this);
                    break;
                case REFRESH:
                    button_txt[0] = button_txt_count;
                    Log.e(TAG, button_txt_count+"");
                    String second_txt = MessageFormat.format(getString(R.string.bound_scan_sqr), button_txt);
                    boundBtn.setText(second_txt);
                    if( button_txt_count ==0 ){
                        if (dialog_bound != null && dialog_bound.isShowing()){
                            if(timer!=null)
                                timer.cancel();
                            dialog_bound.dismiss();

                        }
                        BleService.getInstance(BLEListActivity.this).releaseBLE();
                        Log.e(TAG,"REFRESH");
                        setResult(RESULT_FAIL);
                        finish();
                    }
                    break;
            }
        };
    };




    private class BLEProviderObserver extends BLEHandler.BLEProviderObserverAdapter
    {

        @Override
        protected Activity getActivity()
        {
            return BLEListActivity.this;
        }



        @Override
        public void updateFor_handleNotEnableMsg() {
            super.updateFor_handleNotEnableMsg();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            BLEListActivity.this.startActivityForResult(enableBtIntent, BleService.REQUEST_ENABLE_BT);
        }



        @Override
        public void updateFor_handleSendDataError()
        {
            super.updateFor_handleSendDataError();
            if (dialog_bound != null && dialog_bound.isShowing()){
                if(timer!=null)
                    timer.cancel();
                dialog_bound.dismiss();
            }
        }

        @Override
        public void updateFor_handleConnectLostMsg()
        {
            Log.i("BLEListActivity", "断开连接");

            if (dialog_bound != null && dialog_bound.isShowing()){
                if(timer!=null)
                    timer.cancel();
                dialog_bound.dismiss();
            }


            provider.clearProess();
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            Log.e(TAG, "updateFor_handleConnectLostMsg");
            setResult(RESULT_FAIL);
            finish();
        }

        @Override
        public void updateFor_handleConnectFailedMsg()
        {
            super.updateFor_handleConnectFailedMsg();

            if (dialog_bound != null && dialog_bound.isShowing()){
                if(timer!=null)
                    timer.cancel();
                dialog_bound.dismiss();
            }
            provider.release();
            Log.e(TAG, "updateFor_handleConnectFailedMsg");
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            setResult(RESULT_FAIL);
            finish();
        }

        @Override
        public void updateFor_handleConnectSuccessMsg()
        {
            Log.i("BLEListActivity", "连接成功");
            try
            {
                new Thread().sleep(BleService.TIMEOUT);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            provider.requestbound(BLEListActivity.this);
        }

        @Override
        public void updateFor_BoundContinue()
        {
            super.updateFor_BoundContinue();
            if (sendcount < sendcount_MAX)
            {
                boundhandler.postDelayed(boundRunnable , sendcount_time);
                sendcount++;
            }
            else
            {
                Log.e("BLEListActivity", "已经发送超出15次");
                provider.clearProess();
                BleService.getInstance(BLEListActivity.this).releaseBLE();
                setResult(RESULT_FAIL);
                finish();
            }
        }


        @Override
        public void updateFor_BoundSucess()
        {
            provider.SetDeviceTime(BLEListActivity.this);
        }

        @Override
        public void updateFor_handleSetTime() {
            provider.getModelName(BLEListActivity.this);

        }

        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            if(latestDeviceInfo==null){
                //未获取成功  重新获取
                provider.getModelName(BLEListActivity.this);
            }else{
                modelName = latestDeviceInfo.modelName;
                if (dialog_bound != null && dialog_bound.isShowing()){
                    if(timer!=null)
                        timer.cancel();
                    dialog_bound.dismiss();
                }
                //获取成功
                startBound();
            }
        }


        /** 通知：设备绑定信息同步到服务端完成 */
        @Override
        public void updateFor_boundInfoSyncToServerFinish(Object resultFromServer) {

            if (resultFromServer!=null) {
                if (((String) resultFromServer).equals("1")) {
                    Log.e(TAG, "绑定成功！");
                    MyApplication.getInstance(BLEListActivity.this).getLocalUserInfoProvider().getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac().substring(3));
                    MyApplication.getInstance(BLEListActivity.this).getLocalUserInfoProvider().getDeviceEntity().setDevice_type(MyApplication.DEVICE_WATCH);
                    if(dialog_server!=null && dialog_server.isShowing())
                        dialog_server.dismiss();
                    ToolKits.showCommonTosat(BLEListActivity.this, true, ToolKits.getStringbyId(BLEListActivity.this, R.string.portal_main_bound_success), Toast.LENGTH_LONG);
                    //用户打开系统消息权限
                    setResult(RESULT_OK);
                    finish();
                } else if (((String) resultFromServer).equals("10024")) {
                    MyLog.e(TAG, "========绑定失败！========");
                    if(dialog_server!=null && dialog_server.isShowing())
                        dialog_server.dismiss();
                    new android.support.v7.app.AlertDialog.Builder(BLEListActivity.this)
                            .setTitle(getActivity().getResources().getString(R.string.general_prompt))
                            .setMessage(MessageFormat.format(ToolKits.getStringbyId(getActivity(),R.string.portal_main_has_bound_other), BoundFailMSG))
                            .setPositiveButton(getActivity().getResources().getString(R.string.general_ok), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog_, int which) {
                                    dialog_.dismiss();
                                    BleService.getInstance(getActivity()).releaseBLE();
                                    setResult(RESULT_FAIL);
                                    finish();
                                }
                            })
                            .show();
                }
            } else {

                Log.e(TAG, "boundAsyncTask result is null!!!!!!!!!!!!!!!!!");
                if(dialog_server!=null && dialog_server.isShowing())
                    dialog_server.dismiss();
                BleService.getInstance(getActivity()).releaseBLE();
            }
        }


        @Override
        public void updateFor_BoundFail()
        {
            Log.e(TAG, "updateFor_BoundFail");
            // BLEListActivity.this.notifyAll();
            if (dialog_bound != null && dialog_bound.isShowing()){
                if(timer!=null)
                    timer.cancel();
                dialog_bound.dismiss();
            }
            provider.clearProess();
            provider.unBoundDevice(BLEListActivity.this);
            if(dialog_server!=null && dialog_server.isShowing())
                dialog_server.dismiss();
            setResult(RESULT_FAIL);
            finish();
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(observerAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        Log.i(TAG, "finish");
        listProvider.stopScan();
        // 及时清除此Observer，以便在重新登陆时，正在运行中的蓝牙处理不会因此activity的回收而使回调产生空指针等异常
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        if (provider != null)
            provider.setBleProviderObserver(null);
    }

    private void startBound() {
//        dialog_server.show();
        // 绑定设备时必须保证首先从服务端取来标准UTC时间，以便给设备校时(要看看网络是否连接)
        if (ToolKits.isNetworkConnected(BLEListActivity.this)) {
            UserEntity ue = MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider();
            String user_id = ue.getUser_id()+"";
            if (ue != null && provider != null && provider.getCurrentDeviceMac() != null) {
                // 将用户id 和 MAC地址交到服务端进行匹配
                submitBoundMACToServer(user_id, provider.getCurrentDeviceMac());
            }else {
                BleService.getInstance(BLEListActivity.this).releaseBLE();
                if(dialog_server!=null && dialog_server.isShowing())
                    dialog_server.dismiss();
                setResult(RESULT_FAIL);
                finish();
            }
        } else {
            if(dialog_server!=null && dialog_server.isShowing())
                dialog_server.dismiss();
            BleService.getInstance(BLEListActivity.this).releaseBLE(); // 没有网络去绑定设备
            // 就断开连接
        }
    }


private void submitBoundMACToServer(String user_id,String Mac){
    progressDialog.setMessage(getString(R.string.general_submitting));
    progressDialog.show();
    if(MyApplication.getInstance(this).isLocalDeviceNetworkOk()){
        String last_sync_device_id2 =  MyApplication.getInstance(BLEListActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id2();
        CallServer.getRequestInstance().add(getApplicationContext(), false, CommParams.HTTP_BOUND, NoHttpRuquestFactory.submitBoundMACToServer(user_id, Mac.substring(3),MyApplication.DEVICE_WATCH,modelName), HttpCallback);
        MyLog.e(TAG,"=====user_id======="+user_id+"==Mac=="+Mac+"===last_sync_device_id2==="+last_sync_device_id2);
    }else {
        MyToast.show(BLEListActivity.this, "当前网络不给力！", Toast.LENGTH_LONG);
    }

}
    private HttpCallback<String> HttpCallback = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            progressDialog.dismiss();
            String result = response.get();
//            JSONObject object=JSON.parseObject(result);
//            String value = object.getString("returnValue");
            //处理服务端返回的登陆结果信息.
            MyLog.e(TAG, "绑定后返回结果===" + response.get());
            DataFromServer dataFromServer=JSONObject.parseObject(response.get(), DataFromServer.class);
            String value=dataFromServer.getReturnValue().toString();
            if(dataFromServer.getErrorCode()==1){
                //获取了服务器的设备信息 并且保存到本地
                ModelInfo modelInfo=JSONObject.parseObject((String)dataFromServer.getReturnValue(), ModelInfo.class);
                PreferencesToolkits.saveInfoBymodelName(BLEListActivity.this,modelName,modelInfo);
                MyLog.e(TAG, "绑定后返回结果=modelInfo==" +modelInfo.getMotor());
                //该设备未绑定  执行绑定操作
                UserEntity userEntity = MyApplication.getInstance(BLEListActivity.this).getLocalUserInfoProvider();
                userEntity.getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac().substring(3));
                userEntity.getDeviceEntity().setDevice_type(MyApplication.DEVICE_WATCH);
                userEntity.getDeviceEntity().setModel_name(modelName);
                MyApplication.getInstance(BLEListActivity.this).setLocalUserInfoProvider(userEntity);
                if (observerAdapter != null)
                    observerAdapter.updateFor_boundInfoSyncToServerFinish(dataFromServer.getErrorCode()+"");
            }
            if (dataFromServer.getErrorCode()==10024){
//                设备已被绑定，弹框提示绑定的用户
                JSONObject object=JSON.parseObject(value);

                BoundFailMSG =object.getString("nickname");

                MyLog.e(TAG,"HttpCallback=====result======="+result+"===BoundFailMSG===="+BoundFailMSG);
                if (observerAdapter != null)
                    observerAdapter.updateFor_boundInfoSyncToServerFinish(dataFromServer.getErrorCode()+"");
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
                    Log.e("BLEListActivity", "用户取消打开蓝牙");
                    break;

                case Activity.RESULT_OK:       //用户打开蓝牙
                    listProvider.scanDeviceList();
                    /**
                     * 全局的变量 防止在这里关闭蓝牙导致连接上后 后台循环扫描的定时器不走
                     */
                    BleService.setNEED_SCAN(true);
                    Log.e("BLEListActivity", "/用户打开蓝牙");

                    break;

                default:
                    break;
            }
            return;
        }

    }




    private void broadcastUpdate(final String action, Context context)
    {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    //判断是否获取了消息通知权限
    private boolean isEnabled()
    {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),NotificationCollectorService.ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
