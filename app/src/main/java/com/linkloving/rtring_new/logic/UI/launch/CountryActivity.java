package com.linkloving.rtring_new.logic.UI.launch;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.UI.launch.country.CountryListView;
import com.linkloving.rtring_new.logic.UI.launch.country.GroupListView;
import com.linkloving.rtring_new.logic.UI.launch.country.SearchEngine;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import static com.mob.tools.utils.R.getStringRes;
public class CountryActivity extends ToolBarActivity implements GroupListView.OnItemClickListener {
public final static String TAG = CountryActivity.class.getSimpleName();
private String id;
// 国家号码规则
private HashMap<String, String> countryRules;
private EventHandler handler;
private CountryListView listView;
    private Dialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();

        id=intent.getStringExtra("currentId");

        MyLog.i("", "此时的id" + id);

    SearchEngine.prepare(CountryActivity.this, new Runnable() {
        public void run() {
            afterPrepare();
        }
    });
        pd=new ProgressDialog(this);
        pd.show();
    }

    @Override
     protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {

        HideButtonRight(true);

        SetBarTitleText(getString(R.string.smssdk_country_List));

    }

    @Override
    protected void initListeners() {

    }


    private void afterPrepare() {


        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {

                              if (countryRules == null || countryRules.size() <= 0) {
                                  handler = new EventHandler() {
                                      @SuppressWarnings("unchecked")
                                      public void afterEvent(int event, final int result, final Object data) {
                                          if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {

                                              runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {

                                                      if (result == SMSSDK.RESULT_COMPLETE) {
                                                          if (pd != null && pd.isShowing()) {
                                                              pd.dismiss();
                                                          }

                                                          onCountryListGot((ArrayList<HashMap<String, Object>>) data);
                                                      } else {
                                                          if (pd != null && pd.isShowing()) {
                                                              pd.dismiss();
                                                          }
                                                          ((Throwable) data).printStackTrace();
                                                          int resId = getStringRes(CountryActivity.this, "smssdk_network_error");
                                                          if (resId > 0) {
                                                              Toast.makeText(CountryActivity.this, resId, Toast.LENGTH_SHORT).show();
                                                          }
                                                          finish();
                                                      }
                                                  }
                                              });

                                          }
                                      }
                                  };
                                  // 注册回调接口
                                  SMSSDK.registerEventHandler(handler);
                                  // 获取国家列表
                                  SMSSDK.getSupportedCountries();
                              } else {
                                  if (pd != null && pd.isShowing()) {
                                      pd.dismiss();
                                  }
                                  initPage();
                              }

                          }
                      }
        );


    }

    private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
        // 解析国家列表
        for (HashMap<String, Object> country : countries) {
            String code = (String) country.get("zone");
            String rule = (String) country.get("rule");
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }

            if (countryRules == null) {
                countryRules = new HashMap<String, String>();
            }
            countryRules.put(code, rule);
        }
        //回归页面初始化操作
        initPage();
    }

    private void initPage() {
        setContentView(R.layout.activity_country);
        listView= (CountryListView) findViewById(R.id.countrylist);
        listView.setOnItemClickListener(this);

    }


    //listview的点击事件
    @Override
    public void onItemClick(GroupListView parent, View view, int group, int position) {

        if(position >= 0){
            String[] country = listView.getCountry(group, position);
            if (countryRules != null && countryRules.containsKey(country[1])) {
                id = country[2];
                finish();
            } else {
                int resId = getStringRes(CountryActivity.this, "smssdk_country_not_support_currently");
                if (resId > 0) {
                    Toast.makeText(CountryActivity.this, resId, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public void finish() {
        //回掉
        SMSSDK.unregisterEventHandler(handler);

        Intent intent=new Intent();

        intent.putExtra("id",id);

        setResult(RESULT_OK,intent);

        super.finish();

    }


}
