package com.linkloving.rtring_new.basic.toolbar;

import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/6/15.
 */
public abstract class SportToolBarActivity extends AppCompatActivity {
    private static final String TAG = SportToolBarActivity.class.getSimpleName();
    private static final int DEFAULT_BG_ALPHA = 0;

    private ToolBarHelper mToolBarHelper ;
    public Toolbar toolbar ;

    Button btn;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MobclickAgent.setDebugMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        setBase();
        // 竖屏锁定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mToolBarHelper = new ToolBarHelper(this,layoutResID) ;
        toolbar = mToolBarHelper.getToolBar() ;
        //toolbar背景透明 方便统一设置背景颜色
        setContentView(mToolBarHelper.getContentView());
        /** 把toolbar设置到Activity中*/
        setSupportActionBar(toolbar);
        /** 自定义的一些操作*/
        onCreateCustomToolBar(toolbar) ;
        AppManager.getAppManager().addActivity(this);
        getIntentforActivity();
        initView();
        initListeners();
    }

    protected abstract void getIntentforActivity();

    protected abstract void initView();

    protected abstract void initListeners();

    private void setBase() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 例
    }

    public void onCreateCustomToolBar(Toolbar toolbar){
        toolbar.setContentInsetsRelative(0,0);
        toolbar.showOverflowMenu() ;
        getLayoutInflater().inflate(R.layout.toobar_layout, toolbar) ;
        title = (TextView) toolbar.findViewById(R.id.title);
        //暂时用不到 gone掉
        btn = (Button) toolbar.findViewById(R.id.id_txt_btn);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    /**
     * 隐藏右侧按钮
     */
    public Button getRightButton() {
        if (null != btn)
            return btn;
        return null;
    }

    /**
     * 隐藏右侧按钮
     */
    public void HideButtonRight(boolean bSetHide) {
        if (null != btn) {
            if (bSetHide) {
                btn.setVisibility(View.GONE);
            }else{
                btn.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置模板上导航栏中间的标题文字
     * @param titleText
     * @return 修改成功返回true，失败返回false
     */
    public boolean SetBarTitleText(String titleText) {
        if (null != title) {
            title.setText(titleText);
            return true;
        }
        return false;
    }

    protected void setStatusBar() {
        //状态栏透明
        StatusBarUtil.setTranslucent(this, DEFAULT_BG_ALPHA);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }
}
