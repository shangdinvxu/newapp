package com.linkloving.rtring_new.logic.UI.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.utils.CommonUtils;

public class SendMailActivity extends ToolBarActivity {
    private TextView mailView = null;

    private Button sendBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.main_about_send_mail_title)));
        mailView = (TextView) findViewById(R.id.main_about_send_mail_content);
        sendBtn = (Button) findViewById(R.id.main_about_send_mail_sendBtn);
    }

    @Override
    protected void initListeners() {
        sendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String content = String.valueOf(mailView.getText()).trim();
                if (CommonUtils.isStringEmpty(content))
                {
                    mailView.setError(getString(R.string.main_about_send_mail_content));
                    return;
                }
                sendMail(content);
            }
        });
    }
    private void sendMail(String content)
    {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] emailReciver = new String[] { CommParams.LINKLOVING_OFFICAL_MAIL };
        String emailSubject = "You have a letter feedback";
        String emailBody = MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id() + "("
                + MyApplication.getInstance(this).getLocalUserInfoProvider().getUserBase().getUser_mail() + ")" + " said: \n\t" + content;
        // 设置邮件默认地址
        email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
        // 设置邮件默认标题
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
        // 设置要默认发送的内容
        email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
        // 调用系统的邮件系统
        startActivity(Intent.createChooser(email, "Please select sending mail software"));

        SendMailActivity.this.finish();
    }
}
