package com.linkloving.rtring_new.logic.UI.main.update;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leo.wang on 2016/5/18.
 */
public class AutoUpdateDaemon {

    private static final String TAG = AutoUpdateDaemon.class.getSimpleName();

    public Activity parentActivity = null;
    private int newVersionCode = 0;
    private String newVersionDowloadURL;
    private long newVersionFileSize;
    private ProgressDialog progressDialog;

    private String default_apk_name;
    private ProcessHandle progressHandle = new ProcessHandle();

    private Thread theDoawnloadThread = null;

    private final int CACHE_SIZE = 1024;

    public AutoUpdateDaemon(Activity activity, String newVersionCode, long newVersionFileSize, String newVersionDowloadURL)
    {
        this.parentActivity = activity;
        this.newVersionCode = Integer.valueOf(newVersionCode).intValue();
        this.newVersionFileSize = newVersionFileSize;
        this.newVersionDowloadURL = newVersionDowloadURL;
        this.default_apk_name = parentActivity.getPackageName() + ".apk";
    }

    public void doUpdate()
            throws Exception
    {
        if (checkAPKExsists())
        {
            downloadTheFile(this.newVersionDowloadURL);
            showDownWaitDialog();
        }
    }

    private String getNewVersionFileName()
    {
        System.out.println("downURL==" + this.newVersionDowloadURL);
        if ((this.newVersionDowloadURL != null) && (this.newVersionDowloadURL.lastIndexOf('/') != -1)
                && (this.newVersionDowloadURL.toLowerCase().endsWith(".apk")))
        {
            return this.newVersionDowloadURL.substring(this.newVersionDowloadURL.lastIndexOf('/') + 1);
        }else
        {
            return default_apk_name;
        }
    }
    private File getTheAPKFile() throws Exception
    {

        /**/
        MyLog.e(TAG,"Environment.getExternalStorageDirectory().getPath()"+Environment.getExternalStorageDirectory().getPath());
        MyLog.e(TAG,"Environment.getExternalStorageDirectory()"+Environment.getExternalStorageDirectory());
        MyLog.e(TAG, "getNewVersionFileName()" + getNewVersionFileName());

        File doawnLoadedFile = new File(Environment.getExternalStorageDirectory(), getNewVersionFileName());
        if (!doawnLoadedFile.exists()){
            doawnLoadedFile.createNewFile();
        }
        return doawnLoadedFile;
    }

    private boolean checkAPKExsists() throws Exception
    {
        final File doawnLoadedFile = getTheAPKFile();
        PackageManager pm = this.parentActivity.getPackageManager();

        PackageInfo existsPkgInfo = pm.getPackageArchiveInfo(doawnLoadedFile
                .getAbsolutePath(), 1);

        if (existsPkgInfo != null)
        {
            MyLog.e(TAG,"existsPkgInfo"+existsPkgInfo);
            int existsPkgVersion = existsPkgInfo.versionCode;
            MyLog.e(TAG, "existsPkgVersion=" + existsPkgVersion + "existsPkgInfo.versionCode=" + existsPkgInfo.versionCode);
            if (existsPkgVersion == this.newVersionCode)
            {
                AlertDialog dlg = new AlertDialog.Builder(this.parentActivity)
                        .setTitle(parentActivity.getString(R.string.update_title))
                        .setMessage(parentActivity.getString(R.string.update_message))
                        .setPositiveButton(parentActivity.getString(R.string.general_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FileSystemHelper.viewFile(doawnLoadedFile, AutoUpdateDaemon.this.parentActivity);
                                //将时间保存到本地,以免一天多次提示更新
                                PreferencesToolkits.setUpdateTime(parentActivity, new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()));
                                dialog.dismiss();
                            }
                        }).setNegativeButton(parentActivity.getString(R.string.general_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                AutoUpdateDaemon.this.downloadTheFile(AutoUpdateDaemon.this.newVersionDowloadURL);
                                AutoUpdateDaemon.this.showDownWaitDialog();
                                dialog.dismiss();
                            }
                        })
                        .create();
                dlg.setCanceledOnTouchOutside(false);
                dlg.show();
                return false;
            }
        }
        return true;
    }
    public void showDownWaitDialog()
    {
        this.progressDialog = new ProgressDialog(this.parentActivity);
        this.progressDialog.setMessage(parentActivity.getString(R.string.general_uploading));
        this.progressDialog.setProgressStyle(1);
        this.progressDialog.setMax(100);
        this.progressDialog.setProgress(20);
        this.progressDialog.setCancelable(true);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
//        this.progressDialog.setTitle("Latest Version downloading");
    }
    private void downloadTheFile(String strPath)
    {
        if ((this.theDoawnloadThread == null) ||
                (!this.theDoawnloadThread.isAlive()))
        {
            this.theDoawnloadThread = new Thread(new DownloadThread(strPath));
            this.theDoawnloadThread.start();
        }
    }
    private void doDownloadTheFile(String strPath)
    {
        InputStream is = null;
        if (!URLUtil.isNetworkUrl(strPath)) {
            Log.i(TAG, strPath + "不是一个合法的URL地址!");
        }
        else
        {
           /* File fileFloder=Environment.getExternalStorageDirectory();

            String filename=getNewVersionFileName();

            Log.i(TAG, "fileFloder="+fileFloder.getName()+"filename="+filename);

            downLoad(strPath,fileFloder.getName(),filename);
*/
            //开始下载文件
            try
            {
                URL myURL = new URL(strPath);
                URLConnection conn = myURL.openConnection();
                conn.setReadTimeout(60000);
                conn.connect();
                is = conn.getInputStream();
                if (is == null) {
                    throw new RuntimeException("stream is null");
                }
                File myTempFile = getTheAPKFile();
                FileOutputStream fos = new FileOutputStream(myTempFile);
                byte[] buf = new byte[1024];
                int i = 0;
                while (true)
                {
                    int numread = is.read(buf);
                    if (numread <= 0)
                        break;
                    fos.write(buf, 0, numread);
                    Message message = new Message();
                    message.arg1 = i;
                    i++;
                    this.progressHandle.sendMessage(message);
                }
                Log.i(TAG, "doDownloadTheFile() Download  ok...");
                this.progressDialog.cancel();
                this.progressDialog.dismiss();
                FileSystemHelper.viewFile(myTempFile, this.parentActivity);
                //将时间保存到本地,以免一天多次提示更新
                PreferencesToolkits.setUpdateTime(parentActivity, new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()));

            }
            catch (MalformedURLException e)
            {
//                MyToast.showShort(this.parentActivity,"Error at downloading, " + e.getMessage());
                Log.d(TAG, "新版下载时遇到错误，" + e.getMessage(), e);
            }
            catch (IOException e)
            {
                this.progressDialog.cancel();
                this.progressDialog.dismiss();
                Message message = new Message();
                message.obj = Boolean.valueOf(false);
                this.progressHandle.sendMessage(message);
                Log.d(TAG, "新版下载时遇到错误，" + e.getMessage(), e);
            }
            catch (Exception e)
            {
//                MyToast.showShort(this.parentActivity,"Error at downloading, " + e.getMessage());
                Log.d(TAG, "新版下载时遇到错误，" + e.getMessage(), e);
            }
            try {
                is.close();
            }
            catch (Exception ex)
            {
                Log.d(TAG, "is.close()时遇到错误，" + ex.getMessage(), ex);
            }
        }
    }
    private void downLoad(String url, String fileFloder, String filename) {
        MyLog.e(TAG,"=====url==="+url);
        DownloadRequest downloadRequest = NoHttp.createDownloadRequest(url, fileFloder, filename, true, false);
        CallServer.getDownloadInstance().add(0, downloadRequest, downloadListener);
    }
      private DownloadListener downloadListener = new DownloadListener() {

          @Override
          public void onDownloadError(int what, Exception exception) {
              Toast.makeText(parentActivity, parentActivity.getString(R.string.bracelet_down_file_fail), Toast.LENGTH_SHORT).show();
          }

          @Override
        public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
            Log.i(TAG, "下载开始");

        }

        @Override
        public void onProgress(int what, int progress, long downCount) {
            Log.i(TAG, "正在下载");

        }

        @Override
        public void onFinish(int what, String filePath) {
            Log.i(TAG, "下载完成" + filePath);
            //下载完成,显示开始写入固件
            File file = new File(filePath);
            FileSystemHelper.viewFile(file, parentActivity);
        }


        @Override
        public void onCancel(int what) {
            Log.i(TAG, "下载取消");
        }

    };

    private class DownloadThread
            implements Runnable
    {
        private String strPath;

        public DownloadThread(String strPath)
        {
            this.strPath = strPath;
        }

        public void run()
        {
            AutoUpdateDaemon.this.doDownloadTheFile(this.strPath);
        }
    }

        private class ProcessHandle extends Handler
        {
        private ProcessHandle()
        {
        }
        public void handleMessage(Message msg)
        {
            if (msg.obj == null)
            {
                int process = (int)(msg.arg1 * 1024 * 1.0D / AutoUpdateDaemon.this.newVersionFileSize * 100.0D);
                AutoUpdateDaemon.this.progressDialog.setProgress(process);
            }
            else
            {
                Boolean ok = (Boolean)msg.obj;
//                MyToast.showShort(AutoUpdateDaemon.this.parentActivity,"Error: Please check SD card.");
                return;
            }
        }
    }

}
