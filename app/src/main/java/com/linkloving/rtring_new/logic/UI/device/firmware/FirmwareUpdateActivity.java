package com.linkloving.rtring_new.logic.UI.device.firmware;

import android.os.Bundle;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;

public class FirmwareUpdateActivity extends ToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);

    }
    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.firmware_update_title)));
    }

    @Override
    protected void initListeners() {

    }

}
