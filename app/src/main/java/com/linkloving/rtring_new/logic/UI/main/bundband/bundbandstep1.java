package com.linkloving.rtring_new.logic.UI.main.bundband;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.UI.main.boundwatch.BLEListActivity;
import com.linkloving.rtring_new.utils.ToolKits;

/**
 * Created by Administrator on 2016/4/13.
 */
public class bundbandstep1 extends ToolBarActivity {
    private Button next;
    private Button skipBtn;
    private BLEProvider provider;

    public static final int REQUEST_CODE_BLE_LIST = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_band);
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        initView();
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString(R.string.bound_title_band));
        HideButtonRight(false);
        skipBtn = getRightButton();
        ViewGroup.LayoutParams layoutParams = skipBtn.getLayoutParams();
        layoutParams.width=100;
        layoutParams.height=200;
        skipBtn.setLayoutParams(layoutParams);
        skipBtn.setText(getString(R.string.bound_skip));
        skipBtn.setTextColor(getResources().getColor(R.color.white));

        next = (Button) findViewById(R.id.next);
    }

    @Override
    protected void initListeners() {
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // 网络畅通的情况下才能绑定（否则无法完成从服务端拿到utc时间等问题）
                if(ToolKits.isNetworkConnected(bundbandstep1.this))
                {
                    startActivityForResult(new Intent(bundbandstep1.this, bundbandstep2.class),REQUEST_CODE_BLE_LIST);
                }
                else
                {
                    AlertDialog dialog = new AlertDialog.Builder(bundbandstep1.this)
                            .setTitle(ToolKits.getStringbyId(bundbandstep1.this, R.string.bound_failed))
                            .setMessage(ToolKits.getStringbyId(bundbandstep1.this, R.string.bound_failed_msg))
                            .setPositiveButton(ToolKits.getStringbyId(bundbandstep1.this, R.string.general_ok),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLE_LIST)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                setResult(Activity.RESULT_OK);
                finish();
            }else if(resultCode == BLEListActivity.RESULT_FAIL){
                ToolKits.showCommonTosat(bundbandstep1.this, false, ToolKits.getStringbyId(bundbandstep1.this, R.string.portal_main_bound_failed), Toast.LENGTH_LONG);
                BleService.getInstance(bundbandstep1.this).releaseBLE();
                BleService.getInstance(bundbandstep1.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
            }else if(resultCode == BLEListActivity.RESULT_NOCHARGE){
                ToolKits.showCommonTosat(bundbandstep1.this, false, ToolKits.getStringbyId(bundbandstep1.this, R.string.portal_main_bound_failed_nocharge), Toast.LENGTH_LONG);
                BleService.getInstance(bundbandstep1.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
                BleService.getInstance(bundbandstep1.this).releaseBLE();
            }else if(resultCode == BLEListActivity.RESULT_BACK){
                BleService.getInstance(bundbandstep1.this).releaseBLE();
                BleService.getInstance(bundbandstep1.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
            }else if(resultCode == BLEListActivity.RESULT_DISCONNECT){
                ToolKits.showCommonTosat(bundbandstep1.this, false, ToolKits.getStringbyId(bundbandstep1.this, R.string.portal_main_bound_failed_ble_dis), Toast.LENGTH_LONG);
                BleService.getInstance(bundbandstep1.this).releaseBLE();
                BleService.getInstance(bundbandstep1.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
            }else if(resultCode == BLEListActivity.RESULT_OTHER){
                BleService.getInstance(bundbandstep1.this).releaseBLE();
                BleService.getInstance(bundbandstep1.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
            }
        }

    }
}
