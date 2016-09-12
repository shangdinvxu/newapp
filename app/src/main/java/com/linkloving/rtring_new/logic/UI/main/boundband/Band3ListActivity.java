package com.linkloving.rtring_new.logic.UI.main.boundband;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.linkloving.rtring_new.logic.UI.main.bundband.BandListActivity;
import com.linkloving.rtring_new.logic.UI.main.materialmenu.CommonAdapter;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.ModelInfo;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zkx on 2016/8/8. 手环3.0 绑定列表
 */
public class Band3ListActivity extends ToolBarActivity {

    public static final String TAG = Band3ListActivity.class.getSimpleName();
    private BLEListProvider listProvider;
    private BLEProvider provider;
    private BLEListHandler handler;

    public  String modelName = null;

    private ListView mListView;
    private List<DeviceVO> macList = new ArrayList<DeviceVO>();
    private macListAdapter mAdapter;

    private ProgressDialog progressDialog;
    String BoundFailMSG_SHOUHUAN;
    public static final int RESULT_BACK = 999;
    public static final int RESULT_FAIL = 998;
    public static final int RESULT_DISCONNECT = 996;

    private BLEHandler.BLEProviderObserverAdapter observerAdapter = null;

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



    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (provider != null) {
            if (provider.getBleProviderObserver() == null) {
                provider.setBleProviderObserver(observerAdapter);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blelist);
        progressDialog = new ProgressDialog(this);
        MyLog.e(TAG,"oncreate了-----");
        observerAdapter = new BLEProviderObserver();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(observerAdapter);
        handler = new BLEListHandler(Band3ListActivity.this) {
            @Override
            protected void handleData(BluetoothDevice device) {
                for (DeviceVO v : macList) {
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
    protected void initView() {
        HideButtonRight(false);
        SetBarTitleText(getString(R.string.bound_ble_list));
        Button btn = getRightButton();
        ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
        layoutParams.width = 200;
        layoutParams.height = 200;
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                provider.setCurrentDeviceMac(macList.get(index).mac);
                provider.setmBluetoothDevice(macList.get(index).bledevice);
                provider.connect_mac(macList.get(index).mac);
                if (progressDialog != null && !progressDialog.isShowing()){
                    progressDialog.setMessage(getString(R.string.portal_main_state_connecting));
                    progressDialog.show();
                }

            }
        });

    }

    @Override
    protected void initListeners() {

    }


    private class BLEProviderObserver extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return Band3ListActivity.this;
        }

        @Override
        public void updateFor_handleNotEnableMsg() {
            super.updateFor_handleNotEnableMsg();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Band3ListActivity.this.startActivityForResult(enableBtIntent, BleService.REQUEST_ENABLE_BT);
        }

        @Override
        public void updateFor_handleSendDataError() {
            super.updateFor_handleSendDataError();
        }

        @Override
        public void updateFor_handleConnectLostMsg() {
            Log.i("BandListActivity", "断开连接");
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            provider.clearProess();
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            setResult(RESULT_DISCONNECT);
            finish();
        }

        @Override
        public void updateFor_handleConnectFailedMsg() {
            super.updateFor_handleConnectFailedMsg();

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            provider.release();
            provider.setCurrentDeviceMac(null);
            provider.setmBluetoothDevice(null);
            provider.resetDefaultState();
            setResult(RESULT_FAIL);
            finish();
        }

        @Override
        public void updateFor_handleConnectSuccessMsg() {
            Log.i("BandListActivity", "连接成功");
            try {
                new Thread().sleep(BleService.TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            provider.getModelName(Band3ListActivity.this);

        }

        @Override
        public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
            if(latestDeviceInfo==null){
                //未获取成功  重新获取
                provider.getModelName(Band3ListActivity.this);
            }else{
                modelName = latestDeviceInfo.modelName;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //获取成功
                startBound();
            }
        }

        /**
         * 通知：设备绑定信息同步到服务端完成
         */
        @Override
        public void updateFor_boundInfoSyncToServerFinish(Object resultFromServer) {
            if (resultFromServer != null) {
                if (((String) resultFromServer).equals("1")) {
                    Log.e(TAG, "绑定成功！");
                    provider.getdeviceId(Band3ListActivity.this);
                    MyApplication.getInstance(Band3ListActivity.this).getLocalUserInfoProvider().getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac());
                    MyApplication.getInstance(Band3ListActivity.this).getLocalUserInfoProvider().getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND_VERSION3);
                    MyLog.e(TAG,"MyApplication.DEVICE_BAND_VERSION3-----"+MyApplication.DEVICE_BAND_VERSION3);
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    ToolKits.showCommonTosat(Band3ListActivity.this, true, ToolKits.getStringbyId(Band3ListActivity.this, R.string.portal_main_bound_success), Toast.LENGTH_LONG);
                    setResult(RESULT_OK);
                    finish();
                } else if (((String) resultFromServer).equals("10024")) {
                    MyLog.e(TAG, "========绑定失败！========");
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    new android.support.v7.app.AlertDialog.Builder(Band3ListActivity.this)
                            .setTitle(getActivity().getResources().getString(R.string.general_prompt))
                            .setMessage(MessageFormat.format(ToolKits.getStringbyId(getActivity(), R.string.portal_main_has_bound_other), BoundFailMSG_SHOUHUAN))
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
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                BleService.getInstance(getActivity()).releaseBLE();
            }
        }


    }

    private void startBound() {
        // 绑定设备时必须保证首先从服务端取来标准UTC时间，以便给设备校时(要看看网络是否连接)
        MyLog.e(TAG, "startBound()");
        if(progressDialog!=null && !progressDialog.isShowing())
            progressDialog.setMessage(getString(R.string.general_submitting));
        progressDialog.show();
        if (ToolKits.isNetworkConnected(Band3ListActivity.this)) {
            UserEntity ue = MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider();
            String user_id = ue.getUser_id() + "";
            if (ue != null && provider != null && provider.getCurrentDeviceMac() != null) {
                // 将用户id 和 MAC地址交到服务端进行匹配
                submitBoundMACToServer(user_id, provider.getCurrentDeviceMac());
            } else {
                BleService.getInstance(Band3ListActivity.this).releaseBLE();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                setResult(RESULT_FAIL);
                finish();
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            BleService.getInstance(Band3ListActivity.this).releaseBLE(); // 没有网络去绑定设备
            // 就断开连接
        }
    }

    private void submitBoundMACToServer(String user_id, String Mac) {
        if (MyApplication.getInstance(this).isLocalDeviceNetworkOk()) {
            String last_sync_device_id2 = MyApplication.getInstance(Band3ListActivity.this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id2();
            CallServer.getRequestInstance().add(getApplicationContext(), false, CommParams.HTTP_BOUND, NoHttpRuquestFactory.submitBoundMACToServer(user_id, Mac, MyApplication.DEVICE_BAND_VERSION3,modelName), HttpCallback);
            MyLog.e(TAG, "=====user_id=======" + user_id + "==Mac==" + Mac + "===last_sync_device_id2===" + last_sync_device_id2);
        } else {
            MyToast.show(Band3ListActivity.this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
        }


    }

    private com.linkloving.rtring_new.http.basic.HttpCallback<String> HttpCallback = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            //处理服务端返回的登陆结果信息.
            String result = response.get();
            DataFromServer dataFromServer = JSONObject.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            if (dataFromServer.getErrorCode() == 1) {
                //获取了服务器的设备信息 并且保存到本地
                ModelInfo modelInfo=JSONObject.parseObject(response.get(), ModelInfo.class);
                PreferencesToolkits.saveInfoBymodelName(Band3ListActivity.this,modelName,modelInfo);
                MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider().getDeviceEntity().setModel_name(modelName);
                MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider().getDeviceEntity().setLast_sync_device_id(provider.getCurrentDeviceMac());
                MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider().getDeviceEntity().setDevice_type(MyApplication.DEVICE_BAND_VERSION3);
                if (observerAdapter != null)
                    observerAdapter.updateFor_boundInfoSyncToServerFinish(dataFromServer.getErrorCode() + "");
            }
            if (dataFromServer.getErrorCode() == 10024) {
//                设备已被绑定，弹框提示绑定的用户
                JSONObject object = JSON.parseObject(value);
                BoundFailMSG_SHOUHUAN = object.getString("nickname");
                MyLog.e(TAG, "HttpCallback=====result=======" + result + "===BoundFailMSG====" + BoundFailMSG_SHOUHUAN);
                if (observerAdapter != null)
                    observerAdapter.updateFor_boundInfoSyncToServerFinish(dataFromServer.getErrorCode() + "");
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

    class DeviceVO {
        public String mac;
        public String name;
        public BluetoothDevice bledevice;

    }

    class macListAdapter extends CommonAdapter<DeviceVO> {
        public class ViewHolder {
            public TextView mac;
        }

        ViewHolder holder;

        public macListAdapter(Context context, List<DeviceVO> list) {
            super(context, list);
        }

        @Override
        protected View noConvertView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.list_item_ble_list, parent, false);
            holder = new ViewHolder();
            holder.mac = (TextView) convertView.findViewById(R.id.activity_sport_data_detail_sleepSumView);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        protected View hasConvertView(int position, View convertView, ViewGroup parent) {
            holder = (ViewHolder) convertView.getTag();
            return convertView;
        }

        @Override
        protected View initConvertView(int position, View convertView, ViewGroup parent) {
            String mac = list.get(position).mac.substring(list.get(position).mac.length() - 5, list.get(position).mac.length());
            holder.mac.setText("ID:   " + removeCharAt(mac, 2));
            return convertView;
        }

    }

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
}
