package com.linkloving.rtring_new.logic.UI.youzanshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.youzan.sdk.YouzanBridge;
import com.youzan.sdk.web.plugin.YouzanChromeClient;
import com.youzan.sdk.web.plugin.YouzanWebClient;

/**
 * html5交互
 */
public class H5Activity extends ToolBarActivity {
    public static final String SIGN_URL = "URL";
    /**
     * H5和原生的桥接对象
     */
    private YouzanBridge bridge;
    /**
     * WebView
     */
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        iniView();
        initBridge();
        openWebview();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }


    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString(R.string.shop));
    }

    @Override
    protected void initListeners() {

    }


    /**
     * 初始化视图
     */
    private void iniView() {
        LinearLayout rootView = new LinearLayout(this);
//        LinearLayout configView = new LinearLayout(this);
        Button buttonShare = new Button(this);
        buttonShare.setVisibility(View.GONE);

        Button buttonRefresh = new Button(this);
        buttonRefresh.setVisibility(View.GONE);

        TextView tvTips = new TextView(this);
        web = new WebView(this);

        rootView.setOrientation(LinearLayout.VERTICAL);
//        configView.setOrientation(LinearLayout.HORIZONTAL);
//
//        configView.addView(buttonShare);
//        configView.addView(buttonRefresh);
//        configView.addView(tvTips);
//        rootView.addView(configView);
        rootView.addView(web, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        setContentView(rootView);

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bridge != null) {//分享
                    bridge.sharePage();
                }
            }
        });
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (web != null) {//刷新
                    web.reload();
                }
            }
        });
    }

    /**
     * 打开链接
     */
    private void openWebview() {
        Intent intent = getIntent();
        String url = intent.getStringExtra(SIGN_URL);
        if (web != null && !TextUtils.isEmpty(url)) {
            web.loadUrl(url);
        }
    }


    /**
     * 初始化桥接对象.
     * <pre>
     * 可使用的扩展有:
     *
     * ...
     * </pre>
     */
    private void initBridge() {
        bridge = YouzanBridge.build(this, web)
                .setWebClient(new WebClient())
                .setChromeClient(new ChromeClient())
                .create();
        bridge.hideTopbar(true);//隐藏顶部店铺信息栏
        //根据需求添加相应的桥接事件
    }

    public YouzanBridge getYzBridge() {
        return bridge;
    }

    public WebView getWebView() {
        return web;
    }

    /**
     * 自定义ChromeClient
     * 必须继承自{@link YouzanWebClient}
     */
    private class ChromeClient extends YouzanChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //这里获取到WebView的标题
        }
    }

    /**
     * 自定义WebClient
     * 必须继承自{@link YouzanWebClient}
     */
    private class WebClient extends YouzanWebClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (super.shouldOverrideUrlLoading(view, url)) {
                return true;
            }
            return false;//或者做其他操作
        }
    }
    /**
     * 页面回退
     * bridge.pageGoBack()返回True表示处理的是网页的回退
     */
    @Override
    public void onBackPressed() {
        if (bridge == null || !bridge.pageGoBack()) {
            super.onBackPressed();
        }
    }

    /**
     * 处理WebView上传文件
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (bridge.isReceiveFileForWebView(requestCode, data)) {
            return;
        }
        //...Other request things
    }

}
