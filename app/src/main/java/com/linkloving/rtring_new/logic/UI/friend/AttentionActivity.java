package com.linkloving.rtring_new.logic.UI.friend;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.UI.friend.fragment.FindFriend;
import com.linkloving.rtring_new.logic.UI.friend.fragment.FindQun;
import com.linkloving.rtring_new.logic.UI.friend.fragment.NearPeople;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.viewpagerindicator.TabPageIndicator;

public class AttentionActivity extends ToolBarActivity {

    public final static String TAG = AttentionActivity.class.getSimpleName();

    private Fragment[] myfragment;
    private ViewPager mViewPager;
    private TabPageIndicator titleIndicator;

    private static String[] TITLE = new String[3];//{"查找好友","附近的人","查找群组"}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String str1=getString(R.string.find_friend);
        String str2=getString(R.string.near_people);
        String str3=getString(R.string.find_qunzu);
        TITLE[0]=str1;
        TITLE[1]=str2;
        TITLE[2]=str3;
        setContentView(R.layout.activity_friend);
        myfragment=new Fragment[3];
        myfragment[0]=new FindFriend();
        myfragment[1]=new NearPeople();
        myfragment[2]=new FindQun();

    }

    @Override
    protected void getIntentforActivity() {


    }

    @Override
    protected void initView() {


        SetBarTitleText(getString(R.string.attention_activity_title));//
        mViewPager= (ViewPager) findViewById(R.id.my_ViewPager);
        titleIndicator= (TabPageIndicator) findViewById(R.id.indicator);
        FragmentPagerAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        titleIndicator.setViewPager(mViewPager);

        mViewPager.setCurrentItem(1);//刚开始进入附近的人


        titleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                MyLog.i(TAG, "onPageScrollStateChanged" + "state=" + state);
            }
        });

    }


    @Override
    protected void initListeners() {

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
