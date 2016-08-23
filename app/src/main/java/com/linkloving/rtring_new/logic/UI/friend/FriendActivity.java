package com.linkloving.rtring_new.logic.UI.friend;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.friend.fragment.Attention_me;
import com.linkloving.rtring_new.logic.UI.friend.fragment.Comment_me;
import com.linkloving.rtring_new.logic.UI.friend.fragment.My_attention;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.viewpagerindicator.TabPageIndicator;
import com.yolanda.nohttp.Response;

public class FriendActivity extends ToolBarActivity {

    public final static String TAG = FriendActivity.class.getSimpleName();
    int  from_tag;
    public final static int PAGE_INDEX_ONE = 1;
    public final static int PAGE_INDEX_TWO = 2;
    private Fragment[] myfragment;

    private ViewPager mViewPager;

    private TabPageIndicator titleIndicator;
    UserEntity userEntity;
    private static final String[] TITLE = new String[3];
    private TextView unread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String str1 = getString(R.string.myfans);
        String str2 = getString(R.string.myattention);
        String str3 = getString(R.string.my_message);
        TITLE[0] = str1;
        TITLE[1] = str2;
        TITLE[2] = str3;
        setContentView(R.layout.activity_friend);
        myfragment = new Fragment[3];
        myfragment[0] = new Attention_me();
        myfragment[1] = new My_attention();
        myfragment[2] = new Comment_me();
        userEntity=MyApplication.getInstance(this).getLocalUserInfoProvider();

    }

    @Override
    protected void getIntentforActivity() {
        Intent intent=getIntent();
        from_tag=intent.getIntExtra("_jump_friend_",PAGE_INDEX_ONE);
        MyLog.e(TAG,"from_tag="+from_tag);
    }


    public void updateUnreadCount() {
        CallServer.getRequestInstance().add(FriendActivity.this, false, CommParams.HTTP_UNREAD_QUERY, NoHttpRuquestFactory.Query_Unread_Request(userEntity.getUser_id() + ""), new HttpCallback<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                String result = response.get();
                DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
                String value=dataFromServer.getReturnValue().toString();
                if (dataFromServer.getErrorCode()==1){
                    if(Integer.parseInt(value)>0){
                        //未读消息
            unread.setVisibility(View.VISIBLE);
            unread.setText(ToolKits.getUnreadString(Integer.parseInt(value)));
        } else {
            unread.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadCount();
    }

    @Override
    protected void initView() {

        HideButtonRight(false);
        SetBarTitleText(getString(R.string.relationship));
        //ContextCompat.getDrawable(LoginFromPhoneActivity.this,R.drawable.textview_black)
        final Button btn = getRightButton();
      /*
        *  android:layout_width="20dp"
            android:layout_height="20dp"
            android:layout_marginEnd="20dp"
            android:layout_centerVertical="true"
            */

     /* btn.setHeight(DensityUtils.dp2px(FriendActivity.this,20));
        btn.setWidth(DensityUtils.dp2px(FriendActivity.this, 20));
       */

        unread = (TextView) findViewById(R.id.comments_item_unread_text);
//        updateUnreadCount();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //进入添加附近的人页面

                IntentFactory.start_Attention_Activity(FriendActivity.this);
//                finish();
            }

        });

        btn.setBackground(ContextCompat.getDrawable(FriendActivity.this, R.drawable.title_add));

        mViewPager = (ViewPager) findViewById(R.id.my_ViewPager);

        titleIndicator = (TabPageIndicator) findViewById(R.id.indicator);

        FragmentPagerAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(adapter);

        titleIndicator.setViewPager(mViewPager);

        // 进入界面默认显示我的关注
        if(from_tag==PAGE_INDEX_TWO)
            mViewPager.setCurrentItem(2);
        else
            mViewPager.setCurrentItem(1);
        titleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                MyLog.i(TAG, "onPageSelected" + "position=" + position);

                if (position == 1) {

                    HideButtonRight(false);

                } else {

                    HideButtonRight(true);
                }

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
