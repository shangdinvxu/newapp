package com.linkloving.rtring_new.logic.UI.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.http.HttpHelper;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

public class AboutUsActivity extends ToolBarActivity {
    // 条款
    private Button termsBtn = null;
    // 隐私
    private Button privacyBtn = null;
    // officalwebsite
    private Button websiteBtn = null;
    // mail
    private Button mailBtn = null;


    // ----------------------------------- SNS2

    private TextView versionView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.main_about_us)));
        termsBtn = (Button) findViewById(R.id.main_about_terms_conditions);
        privacyBtn = (Button) findViewById(R.id.main_about_privacy_policy);
        websiteBtn = (Button) findViewById(R.id.main_about_official_website);
        mailBtn = (Button) findViewById(R.id.main_about_mail);
        versionView = (TextView) findViewById(R.id.main_about_versionView);
        versionView.setText(MoreActivity.getAPKVersionName(this) + "(" + MoreActivity.getAPKVersionCode(this) + ")");
    }

    @Override
    protected void initListeners() {
       // 常见问题是1 服务条款2 隐私条款3
        mailBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(AboutUsActivity.this, SendMailActivity.class));
            }
        });

        termsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {   //条款
                String termsURL= HttpHelper.creatTermUrl(AboutUsActivity.this,2);
                MyLog.i("URL", termsURL);
                startWebAcvitity(AboutUsActivity.this, termsURL);
            }
        });

        privacyBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            { //隐私政策
//                startActivity(IntentFactory.createCommonWebActivityIntent(AboutUsActivity.this, LanguageHelper.isChinese_SimplifiedChinese() ? CommParams.PRIVACY_CN_URL : CommParams.PRIVACY_EN_URL));
                String privacyURL= HttpHelper.creatTermUrl(AboutUsActivity.this,3);
                startWebAcvitity(AboutUsActivity.this, privacyURL);
            }
        });

        websiteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startWebAcvitity(AboutUsActivity.this, CommParams.LINKLOVING_OFFICAL_WEBSITE);
            }
        });
    }

    View.OnClickListener shareOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_TEXT, "The email subject text");
            it.setType("text/plain");
            startActivity(Intent.createChooser(it, "Choose Share Client"));
        }
    };
    public static void startWebAcvitity(Activity activity, String url)
    {
        activity.startActivity(IntentFactory.createCommonWebActivityIntent(activity, url));
    }
}
