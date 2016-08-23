package com.linkloving.rtring_new.logic.UI.launch.register;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.db.weight.UserWeight;
import com.linkloving.rtring_new.db.weight.WeightTable;
import com.linkloving.rtring_new.logic.UI.launch.view.ScaleRulerView;
import com.linkloving.rtring_new.utils.ToolKits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class WeightActivity extends ToolBarActivity {
    @InjectView(R.id.next)
    Button next;
    @InjectView(R.id.scaleWheelView_weight)
    ScaleRulerView mWeightWheelView;
    @InjectView(R.id.tv_user_weight_value)
    TextView mWeightValue;


    private float mWeight = 65;
    private float mMaxWeight = 200;
    private float mMinWeight = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_register);
        ButterKnife.inject(this);
        mWeightWheelView.initViewParam(mWeight, mMaxWeight, mMinWeight);
        mWeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mWeightValue.setText(value + "");
                mWeight = value;
                MyApplication.getInstance(WeightActivity.this).getLocalUserInfoProvider().getUserBase().setUser_weight((int)mWeight);
            }
        });
    }


    @Override
    protected void getIntentforActivity() {
        int tag = getIntent().getIntExtra("tag",0);
    }

    @OnClick(R.id.next)
    void next(){
        List<UserWeight> weightlist = new ArrayList<>();
        String dateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());
        weightlist.add(new UserWeight(dateString,MyApplication.getInstance(WeightActivity.this).getLocalUserInfoProvider().getUser_id()+"",(int)mWeight+""));
        WeightTable.saveToSqliteAsync(WeightActivity.this,weightlist,MyApplication.getInstance(WeightActivity.this).getLocalUserInfoProvider().getUser_id()+"");

        IntentFactory.startBrithActivityIntent(WeightActivity.this,1); //1是注册
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initListeners() {

    }
}
