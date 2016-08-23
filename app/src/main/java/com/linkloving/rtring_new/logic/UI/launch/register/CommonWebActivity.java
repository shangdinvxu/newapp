package com.linkloving.rtring_new.logic.UI.launch.register;

import android.app.ProgressDialog;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.http.HttpHelper;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

public class CommonWebActivity extends ToolBarActivity {
    private static final String TAG = "CommonWebActivity";
    WebView webView;
    String weburl;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_web);

    }

    @Override
    protected void getIntentforActivity() {
        weburl = IntentFactory.parseCommonWebIntent(getIntent());
    }

    @Override
    protected void initView() {

        String termsURL= HttpHelper.creatTermUrl(CommonWebActivity.this, 2);//条款

        String privacyURL= HttpHelper.creatTermUrl(CommonWebActivity.this,3);//隐私政策

        if(weburl.equals(CommParams.PRIVACY_CN_URL)){
            SetBarTitleText(getString(R.string.privacy_clause));
        }else if(weburl.equals(CommParams.REGISTER_AGREEMENT_CN_URL)){
            SetBarTitleText(getString(R.string.service_provision));
        }else if(weburl.equals(CommParams.LINKLOVING_OFFICAL_WEBSITE)){
            SetBarTitleText(getString(R.string.main_about_us));
        }else if(weburl.equals(CommParams.FAQ_CN_URL)){
            SetBarTitleText(getString(R.string.common_problem));
        }else if(weburl.equals(termsURL)){
           // main_about_terms_conditions//条款
            SetBarTitleText(getString(R.string.main_about_terms_conditions));
        }else if(weburl.equals(privacyURL)){
            SetBarTitleText(getString(R.string.main_about_privacy_policy));
        }




       /* else{
            SetBarTitleText("");
        }*/
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading...");
        progressDialog.show();
        webView = (WebView) findViewById(R.id.fuwu_web_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);

        // 关闭缩放
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setSaveFormData(false);
//        webView.getSettings().setLoadsImagesAutomatically(true);
//        webView.postUrl(url,null);
        MyLog.i(TAG, "传进来的url是："+weburl);
        webView.setWebViewClient(new mWebViewClient());
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                super.onProgressChanged(view, newProgress);
                //这里将textView换成你的progress来设置进度
                if (newProgress == 0)
                {
                    MyLog.e("TAG", "开始加载...");
                }
                if (newProgress == 100)
                {
                    MyLog.e("TAG", "加载完成...");
                    progressDialog.dismiss();
                }
            }
        });
//        String htmlString = "<h1>Title</h1><p>This is HTML text<br /><i>Formatted in italics</i><br />Anothor Line</p>";
//// 载入这个html页面
//        webView.loadData(htmlString, "text/html", "utf-8");
        webView.loadUrl(weburl);
    }
    private class mWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            //忽略证书的错误继续Load页面内容
            handler.proceed();
            //handler.cancel(); // Android默认的<strong>处理</strong>方式
            //handleMessage(Message msg); // 进行其他<strong>处理</strong>
        }
    }
    @Override
    protected void initListeners() {

    }

    /**
     * 捕获back键
     */
    @Override
    public void onBackPressed()
    {
        if (webView.canGoBack())
            webView.goBack(); //goBack()表示返回WebView的上一页面
        else
            super.onBackPressed();
    }

}
