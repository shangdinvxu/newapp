package com.linkloving.rtring_new.logic.UI.main.update;

import java.io.Serializable;

/**
 * Created by leo.wang on 2016/5/18.
 */
public class AutoUpdateInfoFromServer implements Serializable {
    private boolean needUpdate = false;

    private String latestClientAPKVercionCode = "0";

    private String latestClientAPKURL = null;

    private long latestClientAPKFileSize = 0L;

    public boolean isNeedUpdate()
    {
        return this.needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }

    public String getLatestClientAPKURL() {
        return this.latestClientAPKURL;
    }

    public void setLatestClientAPKURL(String latestClientAPKURL) {
        this.latestClientAPKURL = latestClientAPKURL;
    }

    public long getLatestClientAPKFileSize() {
        return this.latestClientAPKFileSize;
    }

    public void setLatestClientAPKFileSize(long latestClientAPKFileSize) {
        this.latestClientAPKFileSize = latestClientAPKFileSize;
    }

    public String getLatestClientAPKVercionCode() {
        return this.latestClientAPKVercionCode;
    }

    public void setLatestClientAPKVercionCode(String latestClientAPKVercionCode) {
        this.latestClientAPKVercionCode = latestClientAPKVercionCode;
    }

}
