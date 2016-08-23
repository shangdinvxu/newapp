package com.linkloving.rtring_new.http.basic;

import android.text.TextUtils;

import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.StringRequest;

/**
 * Created by Administrator on 2016/6/3.
 */
public class MyJsonRequest extends StringRequest {

    public MyJsonRequest(String url) {
        super(url, RequestMethod.POST);
        setContentTypeJson();
    }

    public MyJsonRequest(String url,RequestMethod method) {
        super(url,method);
    }

    private String contentType;

    public void setContentTypeJson() {
        this.contentType = "application/json";
    }

    @Override
    public String getContentType() {
        return TextUtils.isEmpty(contentType) ? super.getContentType() : contentType;
    }

    @Override
    public String getAccept() {
        return "application/json,application/xml,text/html,application/xhtml+xml";
    }
}
