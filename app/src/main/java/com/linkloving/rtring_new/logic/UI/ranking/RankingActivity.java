package com.linkloving.rtring_new.logic.UI.ranking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.UI.ranking.rank_fragment.Companies_list;
import com.linkloving.rtring_new.logic.UI.ranking.rank_fragment.Total_list;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.ScreenHotAsyncTask;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.viewpagerindicator.TabPageIndicator;

public class RankingActivity extends ToolBarActivity {
    public final static String TAG = RankingActivity.class.getSimpleName();
    private Fragment[] myfragment;
    private ViewPager mViewPager;
    private TabPageIndicator titleIndicator;
    private UserEntity currUser;
    String[] TITLE;
    Button btn;
    String filePathCache = "/sdcard/ranking_v0.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
    }

    @Override
    protected void getIntentforActivity() {
    }

    @Override
    protected void initView() {
        HideButtonRight(false);
        SetBarTitleText(getString((R.string.ranking_title)));
        //获取是否是企业排行
        currUser = MyApplication.getInstance(this).getLocalUserInfoProvider();
        String ent=currUser.getEntEntity().getEnt_id();
        MyLog.e(TAG,"getEid()==="+ent);
        if (CommonUtils.isStringEmpty(ent)) {
            //有群组的时候
            TITLE = new String[]{
//                    getString(R.string.myattention),
                    getString(R.string.ranking_all) };
            myfragment = new Fragment[1];
//            myfragment[0] = new My_attention();
            myfragment[0] = new Total_list();
        } else {
            //没有群组的时候
            TITLE = new String[]{
//                    getString(R.string.myattention),
                    getString(R.string.ranking_all),
                    currUser.getEntEntity().getEnt_name() };
            myfragment = new Fragment[2];
//            myfragment[0] = new My_attention();
            myfragment[0] = new Total_list();
            myfragment[1] = new Companies_list();
        }
        mViewPager = (ViewPager) findViewById(R.id.rank_ViewPager);
        titleIndicator = (TabPageIndicator) findViewById(R.id.rank_indicator);
        FragmentPagerAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
//        mViewPager.setCurrentItem(1);
        titleIndicator.setViewPager(mViewPager);
//        mViewPager.setCurrentItem(1);

        titleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                filePathCache ="/sdcard/ranking_v" + position + ".png";
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        btn = getRightButton();
        btn.setBackground(ContextCompat.getDrawable(RankingActivity.this, R.mipmap.btn_share));
    }

    @Override
    protected void initListeners() {

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行分享操作
                if (ToolKits.isNetworkConnected(RankingActivity.this)) {
                    new ScreenHotAsyncTask(filePathCache, RankingActivity.this).execute(RankingActivity.this.getWindow().getDecorView());
                } else {
                    new AlertDialog.Builder(RankingActivity.this)
                            .setTitle(ToolKits.getStringbyId(RankingActivity.this, R.string.share_content_fail))
                            .setMessage(ToolKits.getStringbyId(RankingActivity.this, R.string.main_more_sycn_fail))
                            .setPositiveButton(R.string.general_ok, null).show();
                }
            }
        });
    }

    class TabFragmentAdapter extends FragmentPagerAdapter {
        private int mCount = TITLE.length;

        public TabFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //新建一个Fragment来展示ViewPager item的内容，并传递参数
            return myfragment[position];
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE[position];
        }
    }

}

