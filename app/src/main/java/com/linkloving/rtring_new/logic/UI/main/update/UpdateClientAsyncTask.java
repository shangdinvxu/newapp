package com.linkloving.rtring_new.logic.UI.main.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.MyJsonRequest;
import com.linkloving.rtring_new.utils.manager.AsyncTaskManger;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.Response;

import org.json.JSONException;
/**
 * 提交用户登陆请求到服务端并接收处理结果的异常线程实现类.
 */
public abstract class UpdateClientAsyncTask extends AsyncTask<Object, Object, Object>
{
	private final static String TAG = UpdateClientAsyncTask.class.getSimpleName();

	private  String responseResult;
	
	private Activity activity = null;
	public UpdateClientAsyncTask(Activity activity)
	{
		this.activity = activity;
		AsyncTaskManger.getAsyncTaskManger().addAsyncTask(this);
	}

	@Override
	protected Object doInBackground(Object... parems)
	{
			String baseUrl = "http://fir.im/api/v2/app/version/%s?token=%s";
			String checkUpdateUrl = String.format(baseUrl, "572b1b1bf2fc425631000023", "e340c54edf0d774794784c577eecf255");
			MyJsonRequest httpsRequest = new MyJsonRequest(checkUpdateUrl, RequestMethod.GET);

			Log.e(TAG, "请求网络的地址:"+checkUpdateUrl);

			CallServer.getRequestInstance().add(activity, false,1000000,httpsRequest, httpCallback);

			return responseResult;
	}

	protected void onPostExecute(Object result)
	{

		AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
	}
	
	/** 登陆验证失败时会调用的方法 */
	protected abstract void relogin();

	// 版本更新实施方法
	public void fireUpdate(AutoUpdateInfoFromServer aui) 
	{
		// 进入版本更新处理类进行更新处理
		AutoUpdateDaemon up = new AutoUpdateDaemon(activity,
				aui.getLatestClientAPKVercionCode(),
				aui.getLatestClientAPKFileSize(),
				aui.getLatestClientAPKURL()
				);
		try {
			Log.d(TAG, "开始更新了啊啊 啊啊啊啊啊啊啊，");
					up.doUpdate();
		} 
		catch (Exception e) {
			Toast.makeText(activity,"版本更新时发生错误："+ e.getMessage(),
					Toast.LENGTH_LONG).show();
			Log.d(TAG, "新版下载时遇到错误，" + e.getMessage(), e);
		}
	}

	HttpCallback<String> httpCallback = new HttpCallback<String>() {
		@Override
		public void onSucceed(int what, Response<String> response) {
			Log.d(TAG, "httpCallback，" + response.get());
			responseResult=response.get();
			if(responseResult!=null){
				try {
					final AutoUpdateInfoFromServer aui =  new AutoUpdateInfoFromServer();
					org.json.JSONObject versionJsonObj = new org.json.JSONObject((String)responseResult);
					Log.e(TAG, "versionJsonObj:"+versionJsonObj.toString());
					//FIR上当前的versionCode
					org.json.JSONObject jsonObject_binary = versionJsonObj.getJSONObject("binary");
					long fsize =  jsonObject_binary.getLong("fsize");
					String firVersionCode = versionJsonObj.getString("version");
					String installUrl = versionJsonObj.getString("installUrl");
					Log.i("info","fsize:"+fsize);
					Log.i("info","firVersionCode:"+firVersionCode);
					Log.i("info","installUrl:"+installUrl);
					PackageManager pm = activity.getPackageManager();
					PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES);
					if (pi != null) {
						int currentVersionCode = pi.versionCode;
						if (Integer.parseInt(firVersionCode) > currentVersionCode) {
							//需要更新
							Log.i("info","need update");
							aui.setNeedUpdate(true);
							aui.setLatestClientAPKURL(installUrl);
							aui.setLatestClientAPKFileSize(fsize);
							aui.setLatestClientAPKVercionCode(firVersionCode);
							new AlertDialog.Builder(activity)
									.setTitle(activity.getString(R.string.login_form_have_latest_version))
									.setMessage(activity.getString(R.string.login_form_have_latest_version_descrption))
									.setPositiveButton(activity.getString(R.string.login_form_have_latest_version_update_now), new DialogInterface.OnClickListener()
									{
										@Override
										public void onClick(DialogInterface dialog,int which)
										{
											fireUpdate(aui);
										}
									})
									.setNegativeButton(activity.getString(R.string.login_form_have_latest_version_ignore), null)
									.show();
						}else {
							//不需要更新,当前版本高于FIR上的app版本.
							Log.i("info"," no need update");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

			}else {
				//获取到的是空的啊
				Log.i("info"," 获取到的是空的啊");
			}

		}
		@Override
		public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
			Log.d(TAG, "httpCallback，onFailed" + url+message.toString());
		}
	};

}