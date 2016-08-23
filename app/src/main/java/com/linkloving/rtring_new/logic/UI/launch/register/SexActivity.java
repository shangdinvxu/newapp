package com.linkloving.rtring_new.logic.UI.launch.register;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SexActivity extends ToolBarActivity {
    @InjectView(R.id.body_info_manCb)
    RadioButton manBtn;

    @InjectView(R.id.body_info_womanCb)
    RadioButton womanBtn;

    @InjectView(R.id.next)
    Button next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex);
        ButterKnife.inject(this);
        womanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(womanBtn.isChecked()){
                    manBtn.setChecked(false);
                }
            }
        });
        manBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(manBtn.isChecked()){
                    womanBtn.setChecked(false);
                }
            }
        });
    }

    @Override
    protected void getIntentforActivity() {
        int tag = getIntent().getIntExtra("tag",0);
    }

    @OnClick(R.id.next)
    void next(){
        if(manBtn.isChecked()){
            MyApplication.getInstance(SexActivity.this).getLocalUserInfoProvider().getUserBase().setUser_sex(1);
        }else{
            MyApplication.getInstance(SexActivity.this).getLocalUserInfoProvider().getUserBase().setUser_sex(0);
        }
        //进入设置身高
        IntentFactory.startHeightActivityIntent(SexActivity.this,1); //1是注册
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListeners() {

    }
}
