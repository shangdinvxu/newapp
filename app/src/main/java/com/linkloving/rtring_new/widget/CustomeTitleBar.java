package com.linkloving.rtring_new.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.rtring_new.basic.RHolder;

/**
 *  Created by zkx on 2016/2/24.
 *  为了统一项目的titlebar
 */
public class CustomeTitleBar extends LinearLayout{
    /** title 上左返回按钮*/
    private Button leftBackButton = null;

    private Button leftGeneralButton = null;

    private TextView titleView = null;

    private Button rightGeneralButton = null;

    private FrameLayout mainLayout = null;

    private LinearLayout leftBtnLayout = null;

    private LinearLayout titleLayout = null;

    private LinearLayout rightBtnLayout = null;

    public CustomeTitleBar(Context context)
    {
        this(context, null);
    }

    public CustomeTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
        initListeners();
    }

    protected void initViews()
    {
        if (isInEditMode()) return;

        LayoutInflater.from(getContext()).inflate(
                RHolder.getInstance().getEva$android$R().layout("widget_title_bar"),
                this, true);

        this.leftBackButton = ((Button)findViewById(
                RHolder.getInstance().getEva$android$R().id("widget_title_left_backBtn")));

        this.leftGeneralButton = ((Button)findViewById(
                RHolder.getInstance().getEva$android$R().id("widget_title_left_generalBtn")));

        this.titleView = ((TextView)findViewById(
                RHolder.getInstance().getEva$android$R().id("widget_title_textView")));

        this.rightGeneralButton = ((Button)findViewById(
                RHolder.getInstance().getEva$android$R().id("widget_title_right_generalBtn")));

        this.titleLayout = ((LinearLayout)findViewById(
                RHolder.getInstance().getEva$android$R().id("widget_title_textLayout")));

        this.leftBtnLayout = ((LinearLayout)findViewById(
                RHolder.getInstance().getEva$android$R().id("widget_title_leftBtnLayout")));

        this.rightBtnLayout = ((LinearLayout)findViewById(
                RHolder.getInstance().getEva$android$R().id("widget_title_rightBtnLayout")));

        this.mainLayout = ((FrameLayout)findViewById(
                RHolder.getInstance().getEva$android$R().id("widget_title_bar")));

        this.rightGeneralButton.setVisibility(View.GONE);
        this.leftGeneralButton.setVisibility(View.GONE);
    }

    protected void initListeners()
    {
        this.leftBackButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                CustomeTitleBar.this.fireBack();
            }
        });
    }

    protected void fireBack()
    {
        if ((getContext() instanceof Activity))
            ((Activity)getContext()).finish();
    }

    public Button getLeftBackButton()
    {
        return this.leftBackButton;
    }

    public TextView getTitleView() {
        return this.titleView;
    }

    public Button getRightGeneralButton() {
        return this.rightGeneralButton;
    }

    public Button getLeftGeneralButton() {
        return this.leftGeneralButton;
    }

    public void setText(String txt)
    {
        this.titleView.setText(txt);
    }

    public LinearLayout getLeftBtnLayout()
    {
        return this.leftBtnLayout;
    }

    public LinearLayout getRightBtnLayout() {
        return this.rightBtnLayout;
    }

    public LinearLayout getTitleLayout() {
        return this.titleLayout;
    }

    public FrameLayout getMainLayout() {
        return this.mainLayout;
    }

    public void setBottomShadowLineVisible(boolean visible)
    {
        findViewById( RHolder.getInstance().getEva$android$R().id("widget_title_bottomShadowLine_ll"))
                .setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
