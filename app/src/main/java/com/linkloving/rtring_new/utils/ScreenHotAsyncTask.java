package com.linkloving.rtring_new.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import com.linkloving.rtring_new.R;

/**
 * Created by Linkloving on 2016/4/6.
 */
public class ScreenHotAsyncTask extends AsyncTask<View, String, String> {
    View v = null;
    String filePathCache=null;
    Activity activity=null;
    private ProgressDialog loadingDialog = null;
    public ScreenHotAsyncTask(String string,Activity activity) {
        this.filePathCache=string;
        this.activity=activity;
        loadingDialog = new ProgressDialog(activity);
        loadingDialog.setProgressStyle(0);
        loadingDialog.setMessage(activity.getString(R.string.detail_sport_data_image_loading));
        loadingDialog.show();
    }


    @Override
    protected String doInBackground(View... params) {
        v = params[0];
        String  filePath = filePathCache;
        // 截屏
        ToolKits.getScreenHot(v, filePath);
        if (loadingDialog != null)
            loadingDialog.dismiss();

        return filePath;
    }

    @Override
    protected void onPostExecute(String result) {
        ShareUtil.showShare(result, activity);
    }
}
