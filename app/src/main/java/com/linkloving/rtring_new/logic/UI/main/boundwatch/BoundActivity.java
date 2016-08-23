package com.linkloving.rtring_new.logic.UI.main.boundwatch;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.utils.ToolKits;

public class BoundActivity extends ToolBarActivity {
    private Button next;
    private Button skipBtn;

    private BLEProvider provider;

    //请求码：前往相机扫描
    public static final int REQUEST_CODE_CAMERA = 4;
    public static final int REQUEST_CODE_BLE_LIST = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound);
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        HideButtonRight(false);
        SetBarTitleText(getString(R.string.bound_title));
        next = (Button) findViewById(R.id.next);
        skipBtn = getRightButton();
        ViewGroup.LayoutParams layoutParams = skipBtn.getLayoutParams();
        layoutParams.width=100;
        layoutParams.height=200;
        skipBtn.setLayoutParams(layoutParams);
        skipBtn.setText(getString(R.string.bound_skip));
        skipBtn.setTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected void initListeners() {

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 网络畅通的情况下才能绑定（否则无法完成从服务端拿到utc时间等问题）
                startActivityForResult(new Intent(BoundActivity.this, BoundActivity_2.class), REQUEST_CODE_BLE_LIST);
            }
        });

//
        skipBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void bindListener() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_BLE_LIST) {
            if (resultCode == Activity.RESULT_OK) {
                //只要走绑定流程 就直接去设置时间
                setResult(Activity.RESULT_OK);
                finish();

            } else if (resultCode == BLEListActivity.RESULT_FAIL) {
                ToolKits.showCommonTosat(BoundActivity.this, false, ToolKits.getStringbyId(BoundActivity.this, R.string.bound_failed_reason), Toast.LENGTH_LONG);
            } else if (resultCode == BLEListActivity.RESULT_BACK) {

                BleService.getInstance(BoundActivity.this).releaseBLE();
            }
        }

    }
}
