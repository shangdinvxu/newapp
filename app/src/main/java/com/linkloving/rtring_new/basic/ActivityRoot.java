package com.linkloving.rtring_new.basic;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.widget.CustomeTitleBar;

/**
 * Created by Administrator on 2016/2/24.
 */
public abstract class ActivityRoot extends BaseActivity
{
    public static final String EX1 = "__intent_extra_name1__";
    public static final String EX2 = "__intent_extra_name2__";
    public static final String EX3 = "__intent_extra_name3__";
    public static final String EX4 = "__intent_extra_name4__";
    public static final String EX5 = "__intent_extra_name5__";
    public static final String EX6 = "__intent_extra_name6__";
    public static final String EX7 = "__intent_extra_name7__";
    public static final String EX8 = "__intent_extra_name8__";
    public static final String EX9 = "__intent_extra_name9__";
    public static final String EX10 = "__intent_extra_name10__";
    public static final String EX11 = "__intent_extra_name11__";
    public static final String EX12 = "__intent_extra_name12__";
    public static final String EX13 = "__intent_extra_name13__";
    public static final String EX14 = "__intent_extra_name14__";
    public static final String EX15 = "__intent_extra_name15__";
    public static final String EX16 = "__intent_extra_name16__";
    public static final String EX17 = "__intent_extra_name17__";
    public static final String EX18 = "__intent_extra_name18__";
    public static final String EX19 = "__intent_extra_name19__";
    public static final String EX20 = "__intent_extra_name20__";
    protected boolean goHomeOnBackPressed = false;

    private CustomeTitleBar customeTitleBar = null;

    protected int customeTitleBarResId = -1;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    protected void onDestroy()
    {
        super.onDestroy();
    }

    public void setContentView(int layoutResID)
    {
        unVisibleSystemTitleBar();
        super.setContentView(layoutResID);

        initCustomeTitleBar();
    }

    public void setContentView(View view)
    {
        unVisibleSystemTitleBar();
        super.setContentView(view);

        initCustomeTitleBar();
    }

    public void setContentView(View view, ViewGroup.LayoutParams params)
    {
        unVisibleSystemTitleBar();
        super.setContentView(view, params);

        initCustomeTitleBar();
    }

    protected void unVisibleSystemTitleBar()
    {
        if (this.customeTitleBarResId != -1)
        {
            requestWindowFeature(1);
        }
    }

    protected void initCustomeTitleBar()
    {
        if (this.customeTitleBarResId != -1)
            this.customeTitleBar = ((CustomeTitleBar)findViewById(this.customeTitleBarResId));
    }

    public CustomeTitleBar getCustomeTitleBar()
    {
        return this.customeTitleBar;
    }

    public boolean isUsedCustomeTitleBat()
    {
        return this.customeTitleBarResId != -1;
    }

    public void setTitle(CharSequence title)
    {
        if (this.customeTitleBar != null) {
            this.customeTitleBar.getTitleView().setText(title);
        }
        super.setTitle(title);
    }

    public void setTitle(int titleId)
    {
        if (this.customeTitleBar != null)
            this.customeTitleBar.getTitleView().setText(getText(titleId));
        super.setTitle(titleId);
    }

    public void onBackPressed()
    {
        if (this.goHomeOnBackPressed)
        {
            startActivity(IntentFactory.createPrssedHomeKeyIntent());
        }
        else
            super.onBackPressed();
    }

    public String getTitlePrefix()
    {
        return getResources().getString(RHolder.getInstance().getEva$android$R().string("app_name"));
    }

    public String $$(int id)
    {
        return getResources().getString(id);
    }
}
