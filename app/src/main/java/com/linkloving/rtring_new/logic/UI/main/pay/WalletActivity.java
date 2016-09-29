package com.linkloving.rtring_new.logic.UI.main.pay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LLTradeRecord;
import com.example.android.bluetoothlegatt.proltrol.dto.LLXianJinCard;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.utils.TimeUtil;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.LocalInfoVO;
import com.linkloving.rtring_new.prefrences.devicebean.ModelInfo;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.lnt.rechargelibrary.impl.BalanceCallbackInterface;
import com.lnt.rechargelibrary.impl.BalanceUtil;
import com.lnt.rechargelibrary.impl.ComplaintQueryUtil;
import com.lnt.rechargelibrary.impl.ComplaintUtil;
import com.lnt.rechargelibrary.impl.LoginCallbackInterface;
import com.lnt.rechargelibrary.impl.LoginUtil;
import com.lnt.rechargelibrary.impl.RechargeCallbackInterface;
import com.lnt.rechargelibrary.impl.RechargeUtil;
import com.lnt.rechargelibrary.impl.RecordCallbackInterface;
import com.lnt.rechargelibrary.impl.RecordUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WalletActivity extends ToolBarActivity {
    private static final String TAG = WalletActivity.class.getSimpleName();

    /**
     * 卡片显示的logo
     */
    private ImageView img_card_city;
    /**
     * 显示卡号
     */
    private TextView textViewcard;
    /**
     * 卡片类型
     */
    private TextView cardtype;
    /**
     * 显示余额
     */
    private TextView balanceResult;
    /**
     * 充值
     */
    private Button rechargeBtn;

    private ProgressDialog dialog_pay = null;

    private RecyclerView record_RecyclerView;
    private WalletAdapter walletAdapeter;

    private UserEntity userEntity;
    private BLEProvider provider;
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    private LPDeviceInfo deviceInfo;
    private boolean isReadingCard;

    /**
     * 钱包集合
     */
    List<LLTradeRecord> list_qianbao = new LinkedList<LLTradeRecord>() {
        public void addFirst(LLTradeRecord object) {
            super.addFirst(object);
            if (size() > 10)
                removeLast();
        }

        @Override
        public boolean add(LLTradeRecord object) {
            if (size() > 10)
                removeLast();
            return super.add(object);
        }

    };
    /**
     * 现金集合
     */
    LinkedList<LLXianJinCard> list_XJ = new LinkedList<LLXianJinCard>() {
        public void addFirst(LLXianJinCard object) {
            super.addFirst(object);
        }

        ;
    };


    //    是否要去读交易记录
    private boolean isreadRecord;


    @Override
    protected void onPause() {
        super.onPause();
        if (provider.isConnectedAndDiscovered()) {
            provider.closeSmartCard(this);
        } else {
            Toast.makeText(WalletActivity.this, getString(R.string.pay_no_connect), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (provider.getBleProviderObserver() == null)
            provider.setBleProviderObserver(bleProviderObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        provider.setBleProviderObserver(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();

        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        MyLog.e(TAG, provider.isConnectedAndDiscovered() + "------------provider.isConnectedAndDiscovered()");
        if (provider.isConnectedAndDiscovered()) {
            if (isReadingCard) {
                dialog_pay.show();
                MyLog.e(TAG, "dialog show l");
            } else {
                MyLog.e(TAG, "dialog no show l");
                initData();
            }
        } else {
            MyLog.e(TAG, "没用到dialog no show l");
            Toast.makeText(WalletActivity.this, getString(R.string.pay_no_connect), Toast.LENGTH_LONG).show();
        }
    }

    private void initData() {
//        湖北数码要求不显示余额
//        if(MyApplication.getInstance(PayActivity.this).getLocalUserInfoProvider().getEid().equals(Company.HUBEI_SHUMA)){
//            balance_layout.setVisibility(View.GONE);
//        }
        ModelInfo modelInfo = PreferencesToolkits.getInfoBymodelName(WalletActivity.this, MyApplication.getInstance(WalletActivity.this).getLocalUserInfoProvider().getDeviceEntity().getModel_name());
        if (modelInfo == null) {
            return;
        }
        SharedPreferences sp = getSharedPreferences("readRecord", MODE_PRIVATE);
        isreadRecord = sp.getBoolean("isreadRecord", false);
        MyLog.e(TAG, isreadRecord + "----------isreadRecord");
        //从卡号里面获知卡地址城市
        String card = userEntity.getDeviceEntity().getCard_number();
        deviceInfo = new LPDeviceInfo();
        MyLog.e(TAG, Calendar.getInstance().getTime() + "time2");
        if (card.startsWith(LPDeviceInfo.SUZHOU_)) {
            deviceInfo.customer = LPDeviceInfo.SUZHOU_;   //苏州
            list_qianbao = PreferencesToolkits.getQianbaoList(WalletActivity.this, userEntity.getDeviceEntity().getCard_number());
            for (int i = 0; i < list_qianbao.size(); i++) {
                MyLog.e(TAG, "suzou2里面的list_qianbao" + list_qianbao.get(i).toString());
            }
//            Collections.reverse(list_qianbao);
            walletAdapeter = new WalletAdapter(this, list_qianbao, WalletAdapter.TYPE_QIANBAO);
            record_RecyclerView.setAdapter(walletAdapeter);
            img_card_city.setBackground(getResources().getDrawable(R.mipmap.szsmk_logo));
            img_card_city.setVisibility(View.VISIBLE);
            textViewcard.setText(card);
            cardtype.setText("苏州市民卡-B卡");
//            苏州卡发消息去判断要不要读取记录
            provider.readExpenseRecord(WalletActivity.this);
        }
        //柳州
        else if (card.startsWith(LPDeviceInfo.LIUZHOU_4) || card.startsWith(LPDeviceInfo.LIUZHOU_5)) {
            deviceInfo.customer = LPDeviceInfo.LIUZHOU_5;
            walletAdapeter = new WalletAdapter(this, list_qianbao, WalletAdapter.TYPE_QIANBAO);
            record_RecyclerView.setAdapter(walletAdapeter);
            cardtype.setText("柳州市民卡");
        }
        //湖北数码
        else if (card.startsWith(LPDeviceInfo.HUBEI_SHUMA)) {
            deviceInfo.customer = LPDeviceInfo.HUBEI_SHUMA;
            walletAdapeter = new WalletAdapter(this, list_XJ, WalletAdapter.TYPE_XIANJIN);
            record_RecyclerView.setAdapter(walletAdapeter);
            cardtype.setText("湖北数码视讯");
        } else if (card.startsWith(LPDeviceInfo.LINGNANTONG)) {
            //岭南通充值
            deviceInfo.customer = LPDeviceInfo.LINGNANTONG;
            img_card_city.setBackground(getResources().getDrawable(R.mipmap.yct_logo));
            img_card_city.setVisibility(View.VISIBLE);
            textViewcard.setText(card);
            cardtype.setText("岭南通·羊城通");
            walletAdapeter = new WalletAdapter(this, list_qianbao, WalletAdapter.TYPE_LNT);
            record_RecyclerView.setAdapter(walletAdapeter);
        } else if (card.startsWith(LPDeviceInfo.DATANG_TUOCHENG)) {
            deviceInfo.customer = LPDeviceInfo.DATANG_TUOCHENG;
            img_card_city.setBackground(getResources().getDrawable(R.mipmap.tuocheng_logo));
            textViewcard.setText(card);
            cardtype.setText("驼城通");
        } else {
            deviceInfo.customer = LPDeviceInfo.UN_KNOW_;
            walletAdapeter = new WalletAdapter(this, list_qianbao, WalletAdapter.TYPE_QIANBAO);
            record_RecyclerView.setAdapter(walletAdapeter);
        }
        if (modelInfo.getFiscard() == 2) {
            //这种卡片可以充值 所以打开充值按钮
            rechargeBtn.setVisibility(View.VISIBLE);
            if (deviceInfo.customer == LPDeviceInfo.LINGNANTONG) {
                HideButtonRight(false);
            }
        } else {
            if (deviceInfo.customer == LPDeviceInfo.LINGNANTONG) {
                HideButtonRight(false);
                rechargeBtn.setVisibility(View.VISIBLE);
            } else {
                rechargeBtn.setVisibility(View.GONE);
            }

        }
        //已经知道是哪个城市了
        if (!deviceInfo.customer.equals(LPDeviceInfo.UN_KNOW_)) {
            //已经城市 && 蓝牙已经连接
            if (provider.isConnectedAndDiscovered()) {
                if (deviceInfo.customer.equals(LPDeviceInfo.LINGNANTONG)) {
                    //开始去查询卡片信息了 弹出dialog
                    dialog_pay.show();
                    provider.closeSmartCard(WalletActivity.this);
                    Button btn = getRightButton();
                    ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
                    layoutParams.width = 200;
                    layoutParams.height = 200;
                    btn.setLayoutParams(layoutParams);
                    btn.setText("投诉");
                    btn.setTextColor(getResources().getColor(R.color.white));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String username = PreferencesToolkits.getLNTusername(WalletActivity.this);
                            if (CommonUtils.isStringEmpty(username)) {
                                //用户从未登录过
                                LoginUtil.login(WalletActivity.this, false, null, new LoginCallbackInterface() {
                                    @Override
                                    public void onLoginState(boolean success, String s, String lntusername) {
                                        if (success) {
                                            MyLog.e(TAG, "username:" + lntusername);
                                            PreferencesToolkits.saveLNTusername(WalletActivity.this, lntusername);
                                            lntTouSu(lntusername);
                                        }
                                    }
                                });
                            } else {
                                //用户曾经登录过
                                lntTouSu(username);
                            }
                        }
                    });
                    //岭南通内嵌读取流程
                    RechargeUtil.setBluetoothBase(WalletActivity.this, provider);
                    lntBalance();
                    MyLog.e(TAG, "岭南通内嵌读取流程开始了");

                } else {
                    //开始去查询卡片信息了 弹出dialog
                    if (card.startsWith(LPDeviceInfo.SUZHOU_) && !isreadRecord) {
                        LocalInfoVO localvo = PreferencesToolkits.getLocalDeviceInfo(WalletActivity.this);
                        String money = localvo.getMoney();
                        balanceResult.setText(money);
                        MyLog.e(TAG, isreadRecord + "不要去读交易记录里面的方法执行了");
                    } else {
                        MyLog.e(TAG, "苏州卡,要去读信息");
                        dialog_pay.show();
                        provider.closeSmartCard(WalletActivity.this);
                        // 首先清空集合
                        provider.openSmartCard(WalletActivity.this);
                        textViewcard.setText(getString(R.string.menu_pay_reading));
                        balanceResult.setText("0.00");
                    }
                }
            } else {
                //蓝牙未连接
                Toast.makeText(WalletActivity.this, getString(R.string.pay_no_connect), Toast.LENGTH_LONG).show();
            }
        } else {
            //未知的卡号
            textViewcard.setText(getString(R.string.menu_pay_unknow));
            cardtype.setText(getString(R.string.menu_pay_unknow));
            balanceResult.setText("0.00");
            if (dialog_pay != null && dialog_pay.isShowing()) {
                dialog_pay.dismiss();
            }
        }
    }


    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getResources().getString(R.string.menu_pay));
        img_card_city = (ImageView) findViewById(R.id.img_card_city);
        img_card_city.setVisibility(View.INVISIBLE);
        textViewcard = (TextView) findViewById(R.id.tv_card_number);
        cardtype = (TextView) findViewById(R.id.cardtype);
        balanceResult = (TextView) findViewById(R.id.card_account);
        rechargeBtn = (Button) findViewById(R.id.rechargeBtn);
        rechargeBtn.setVisibility(View.GONE);
        record_RecyclerView = (RecyclerView) findViewById(R.id.recycler_view_record);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        record_RecyclerView.setLayoutManager(layoutManager);
        String title = getResources().getString(R.string.menu_pay);
        dialog_pay = new ProgressDialog(this);
        dialog_pay.setMessage(getString(R.string.pay_loading));
    }

    @Override
    protected void initListeners() {
        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceInfo.customer.equals(LPDeviceInfo.LINGNANTONG)) {
                    String username = PreferencesToolkits.getLNTusername(WalletActivity.this);
                    if (CommonUtils.isStringEmpty(username)) {
                        //用户从未登录过
                        LoginUtil.login(WalletActivity.this, false, null, new LoginCallbackInterface() {
                            @Override
                            public void onLoginState(boolean success, String s, String lntusername) {
                                if (success) {
                                    MyLog.e(TAG, "username:" + lntusername);
                                    PreferencesToolkits.saveLNTusername(WalletActivity.this, lntusername);
                                    RechargeUtil.recharge(WalletActivity.this, RechargeUtil.LINKLOVE, provider.getCurrentDeviceMac(), lntusername, new RechargeCallbackInterface() {
                                        @Override
                                        public void onFail(String arg0) {
                                            Log.e(TAG, "充值onFail回调:" + arg0);
                                        }
                                        @Override
                                        public void onSuccess(String arg0) {
                                            Log.e(TAG, "充值onSuccess回调:" + arg0);
                                            lntBalance();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        //用户曾经登录过
                        RechargeUtil.recharge(WalletActivity.this, RechargeUtil.LINKLOVE, provider.getCurrentDeviceMac(), username, new RechargeCallbackInterface() {
                            @Override
                            public void onFail(String arg0) {
                                Log.e(TAG, "充值onFail回调:" + arg0);
                            }

                            @Override
                            public void onSuccess(String arg0) {
                                Log.e(TAG, "充值onSuccess回调:" + arg0);
                                lntBalance();
                            }
                        });
                    }
                }
            }
        });
    }

    private void lntBalance() {
        BalanceUtil.queryBalance(WalletActivity.this, WalletActivity.this, RechargeUtil.LINKLOVE, provider.getCurrentDeviceMac(), null, false, new BalanceCallbackInterface() {
            @Override
            public void onSuccess(String msg, String balance, String cardNum, String thresholdValue) {
                // TODO Auto-generated method stub
                Log.e(TAG, "查询余额onSuccess回调msg:" + msg);
                Log.e(TAG, "查询余额onSuccess回调balance:" + balance);
                Log.e(TAG, "查询余额onSuccess回调cardNum:" + cardNum);
                Log.e(TAG, "查询余额onSuccess回调thresholdValue:" + thresholdValue);
                balanceResult.setText(balance);
                //余额查询完后去显示交易记录
                RecordUtil.recordQuery(WalletActivity.this, WalletActivity.this, RecordUtil.LINKLOVE, provider.getCurrentDeviceMac(), false, new RecordCallbackInterface() {
                    @Override
                    public void onSuccess(String s, List<Map<String, Object>> list) {
                        if (list.size() <= 0) {
                            if (dialog_pay != null && dialog_pay.isShowing()) {
                                dialog_pay.dismiss();
                            }
                            return;
                        }
                        for (Map<String, Object> map : list) {
                            //{sh_cost=0.01 Ԫ, sh_type=充值, sh_date=06-22  10:32}
                            Log.e(TAG, "查询记录:" + map.toString());
                            LLTradeRecord record = new LLTradeRecord();
                            String money = map.get("sh_cost") + "";
                            money = money.substring(0, money.length() - 1);
                            record.setTradeAmount(money + " 元");
                            record.setTradeTime(map.get("sh_date") + "");
                            if (String.valueOf(map.get("sh_type")).equals("充值")) {
                                record.setTradeType("in");
                            } else {
                                record.setTradeType("out");
                            }
                            list_qianbao.add(record);
                            walletAdapeter.notifyDataSetChanged();
                            if (dialog_pay != null && dialog_pay.isShowing()) {
                                dialog_pay.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFail(String s) {
                        if (dialog_pay != null && dialog_pay.isShowing()) {
                            dialog_pay.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onFail(String arg0) {
                // TODO Auto-generated method stub
                if (dialog_pay != null && dialog_pay.isShowing()) {
                    dialog_pay.dismiss();
                }
            }
        });
    }


    private void lntTouSu(final String username) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.modify_sex_dialog, (ViewGroup) findViewById(R.id.linear_modify_sex));
        final RadioButton tousu = (RadioButton) layout.findViewById(R.id.rb_left);
        tousu.setText("投诉");
        final RadioButton chaxun = (RadioButton) layout.findViewById(R.id.rb_right);
        chaxun.setText("投诉查询");
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("请选择类型：")
                .setView(layout)
                .setPositiveButton(getString(R.string.general_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (tousu.isChecked()) {
                                    ComplaintUtil.Complaint(WalletActivity.this, username);
                                } else if (chaxun.isChecked()) {
                                    ComplaintQueryUtil.ComplaintQuery(WalletActivity.this, username);
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.general_cancel), null)
                .create().show();
    }

    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
            MyLog.e(TAG, "updateFor_notifyFor0x13ExecSucess_D");
            isReadingCard = true;

        }


        @Override
        protected Activity getActivity() {
            return WalletActivity.this;
        }

        public void updateFor_handleExpense_record(boolean a) {
            MyLog.e(TAG, Calendar.getInstance().getTime() + "-----------time1");
            if (a) {
                MyLog.e(TAG, a + "返回的a是true还是false");
                provider.cleanExpenseRecord(WalletActivity.this);
                provider.getSmartCardTradeRecord(WalletActivity.this, deviceInfo);
            } else {

            }
            isreadRecord = a;
            SharedPreferences sharedpreferences = WalletActivity.this.getSharedPreferences("readRecord", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putBoolean("isreadRecord", a);
            edit.commit();
            MyLog.e(TAG, isreadRecord + "-----------a---isreadRecord");
        }


        @Override
        public void updateFor_OpenSmc(boolean isSuccess) {
            super.updateFor_OpenSmc(isSuccess);
            MyLog.i(TAG, "开卡成功！");
            if (isSuccess) {
                textViewcard.setText(userEntity.getDeviceEntity().getCard_number());
                provider.AIDSmartCard(WalletActivity.this, deviceInfo);
            }
        }

        @Override
        public void updateFor_AIDSmc(boolean isSuccess) {
            super.updateFor_AIDSmc(isSuccess);
            if (isSuccess) {
                //读余额
                provider.PINSmartCard(WalletActivity.this, deviceInfo);
            }
        }

        @Override
        public void updateFor_checkPINSucess_D() {
            super.updateFor_checkPINSucess_D();
            provider.readCardBalance(WalletActivity.this, deviceInfo);
        }

        @Override
        public void updateFor_CardNumber(String id) {
            super.updateFor_CardNumber(id);
            isReadingCard = false;
        }

        //余额
        @Override
        public void updateFor_GetSmcBalance(Integer obj) {
            super.updateFor_GetSmcBalance(obj);
            MyLog.e(TAG, "updateFor_GetSmcBalance执行了");
            String money = ToolKits.inttoStringMoney(obj);
            balanceResult.setText(money);
            //把余额保存到本地 方便主界面显示
            LocalInfoVO localvo = PreferencesToolkits.getLocalDeviceInfo(WalletActivity.this);
            localvo.setMoney(money);
            PreferencesToolkits.setLocalDeviceInfoVo(WalletActivity.this, localvo);
            if (deviceInfo.customer.equals(LPDeviceInfo.HUBEI_SHUMA)) {
                provider.readCardRecord_XJ(WalletActivity.this, deviceInfo);
            } else {
                if (list_qianbao.size() > 0) {
                    deviceInfo.time = list_qianbao.get(0).getTradeTimelong();
                } else {
                    deviceInfo.time = 0;
                }
                /**
                 * 要去读交易记录
                 */
                provider.getSmartCardTradeRecord(WalletActivity.this, deviceInfo);
            }
            isReadingCard = false;
        }

        //钱包单条
        @Override
        public void updateFor_GetSmcTradeRecordAsync(LLTradeRecord record) {
            super.updateFor_GetSmcTradeRecordAsync(record);
            MyLog.e(TAG, "isreadRecord__updateFor_GetSmcTradeRecordAsync" + isreadRecord);
            if (list_qianbao.contains(record)) {
                MyLog.d(TAG, "包含记录！！！" + record.toString());
            } else {
                MyLog.d(TAG, "新纪录：" + record.toString());
                record.setTradeCard(textViewcard.getText().toString());
                record.setTradeBalance(balanceResult.getText().toString());
                //此时清除 交易记录在用户注册之前的数据
                UserEntity userEntity = MyApplication.getInstance(WalletActivity.this).getLocalUserInfoProvider();
//                Date date = TimeUtil.stringToDate(userEntity.getUserBase().getRegister_time(),"yyyy-MM-dd HH:mm:ss.S");
//                TEST:
                Date date = TimeUtil.stringToDate("2014-09-12 11:46:46.0", "yyyy-MM-dd HH:mm:ss.S");
                if (date.getTime() / 1000 < record.getTradeTimelong()) {
                    list_qianbao.add(record);
                    MyLog.e(TAG, "record" + record.toString());
                }
                walletAdapeter.notifyDataSetChanged();
//              setListViewHeightBasedOnChildren(recordListview);
            }

        }


        //钱包集合
        @Override
        public void updateFor_GetSmcTradeRecord(List<LLTradeRecord> list) {
            super.updateFor_GetSmcTradeRecord(list);
            MyLog.e(TAG, "updateFor_GetSmcTradeRecord______" + isreadRecord);
            if (list.size() <= 0) {
                MyLog.e(TAG, "没有记录！");
                //recordResult.setText("没有记录！");
            } else {
                /**
                 * 比较时间
                 */
                Collections.sort(list_qianbao, new Comparator<LLTradeRecord>() {
                    @Override
                    public int compare(LLTradeRecord arg1, LLTradeRecord arg2) {
                        return (int) (arg2.getTradeTimelong() - arg1.getTradeTimelong());
                    }
                });
                MyLog.d(TAG, "获取记录成功！");
                //完全读取完交易记录，保存到本地
                PreferencesToolkits.saveQianbaoList(WalletActivity.this, userEntity.getDeviceEntity().getCard_number(), list_qianbao);
                for (int i = 0; i < list_qianbao.size(); i++) {
                    MyLog.e(TAG, "suzou1里面的list_qianbao" + list_qianbao.get(i).toString());
                }
                walletAdapeter.notifyDataSetChanged();
            }
            if (provider.isConnectedAndDiscovered()) {

                provider.closeSmartCard(WalletActivity.this);
                if (dialog_pay != null && dialog_pay.isShowing()) {
                    dialog_pay.dismiss();
                }
            } else {
                Toast.makeText(WalletActivity.this, getString(R.string.pay_no_connect), Toast.LENGTH_LONG).show();
            }
        }

        //单条 现金 交易记录
        @Override
        public void updateFor_GetXJTradeRecordAsync(LLXianJinCard record) {
            super.updateFor_GetXJTradeRecordAsync(record);
            if (list_XJ.contains(record)) {
                MyLog.e(TAG, "包含记录！！！" + record.toString());
            } else {
                MyLog.e(TAG, "新纪录：" + record.toString());
                record.setTradeCard(textViewcard.getText().toString());
                record.setTradeBalance(balanceResult.getText().toString());
                list_XJ.addFirst(record);
            }
        }

        //所有 现金 交易记录读取完毕
        @Override
        public void updateFor_GetXJTradeRecord(List<LLXianJinCard> list_) {
            super.updateFor_GetXJTradeRecord(list_);
            if (list_XJ.size() <= 0) {
                Log.e(TAG, "没有记录！");
            } else {
                Collections.sort(list_XJ, new Comparator<LLXianJinCard>() {
                    @Override
                    public int compare(LLXianJinCard lhs, LLXianJinCard rhs) {
                        return (int) -(Long.parseLong(lhs.getData_3() + lhs.getTime_3()) - Long.parseLong(rhs.getData_3() + rhs.getTime_3())); ///?????
                    }
                });
            }
            if (dialog_pay != null && dialog_pay.isShowing()) {
                dialog_pay.dismiss();
            }
            provider.closeSmartCard(WalletActivity.this);
            isreadRecord = false;
            SharedPreferences sharedpreferences = WalletActivity.this.getSharedPreferences("readRecord", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putBoolean("isreadRecord", isreadRecord);
            edit.commit();

        }

        @Override
        public void updateFor_handleSendDataError() {
            super.updateFor_handleSendDataError();
            provider.closeSmartCard(WalletActivity.this); //关卡
            if (dialog_pay != null && dialog_pay.isShowing()) {
                dialog_pay.dismiss();
            }
            textViewcard.setText(getString(R.string.menu_pay_read_fail));
            balanceResult.setText("0.0");
            MyLog.e(TAG,"updateFor_handleSendDataError+读取失败了");
////            读取失败就开卡关卡再去读
//            provider.closeSmartCard(WalletActivity.this);
//            // 首先清空集合
//            provider.openSmartCard(WalletActivity.this);
            initData();
        }
    }

    ;


}
