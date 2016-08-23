package com.linkloving.rtring_new.utils.manager;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Stack;

/**
 * 异步线程请求： 	start--> AsyncTaskManger.getAsyncTaskManger().addAsyncTask(Async);
 *            		over --> AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(Async);
 *            		app结束  AsyncTaskManger.getAsyncTaskManger().finishAllAsyncTask();
 */
public class AsyncTaskManger 
{
	private final static String TAG = AsyncTaskManger.class.getSimpleName();
	
	private static Stack<AsyncTask> asyncTaskStack;
	private static AsyncTaskManger instance;
	
	public AsyncTaskManger(){}
	/**
	 * 单一实例
	 */
	public static AsyncTaskManger getAsyncTaskManger()
	{
		if(instance==null)
		{
			instance=new AsyncTaskManger();
		}
		return instance;
	}
	/**
	 * 添加Activity到堆栈
	 */
	public void addAsyncTask(AsyncTask at){
		if(asyncTaskStack==null){
			asyncTaskStack=new Stack<AsyncTask>();
		}
		
		Log.d(TAG, "【ATM+】activity"+at.getClass().getCanonicalName()+"正在被加入全局管理列表.");
		asyncTaskStack.add(at);
	}
	
	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public AsyncTask currentAsyncTask(){
		AsyncTask activity=asyncTaskStack.lastElement();
		return activity;
	}
	
	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void removeAsyncTask(){
		AsyncTask activity=asyncTaskStack.lastElement();
		removeAsyncTask(activity);
	}
	/**
	 * 结束指定的Activity
	 */
	public void removeAsyncTask(AsyncTask at)
	{
		removeAsyncTask(at, false);
	}
	/**
	 * 结束指定的Activity
	 */
	public void removeAsyncTask(AsyncTask at, boolean cancelIfRunning){
		if(at!=null){
			Log.d(TAG, "【ATM-】finishAsyncTask：AsyncTask" + at.getClass().getCanonicalName()+"正在从全局管理列表中移除.");
			if(!at.isCancelled())
    			at.cancel(true);
			asyncTaskStack.remove(at);
			
			// FIX: 注意此处不应该调用finish，因为当调用本remove方法时通常是Activity中
			//      自行finish时，如果此处再调用finish则会导致多次调用finish，那么将导致
			//      Ativity中有关finish的逻辑混乱。这也说明了国内开源代码的开发者水平并不高，这么明显的bug却被人传来传去！！！
//			if(activity != null)
//				activity.finish();
			at=null;
		}
	}
	/**
	 * 结束指定类名的Activity
	 */
	public void removeAsyncTask(Class<?> cls){
		for (AsyncTask at : asyncTaskStack) {
			if(at.getClass().equals(cls) ){
				removeAsyncTask(at);
			}
		}
	}
	/**
	 * 结束所有Activity.
	 * 
	 * <p>
	 * 说明：通常情况下程序中正常的Activity都是会自已调用finish方法来结束自已的，
	 * 这种情况下就只需要该Activity从本manager中remove掉自已的句柄就可以了。
	 * 需要finishAll的场景一般是：程序正常退出了（那么就由本manager来finish所有余下的的未finish掉的activity）、
	 * 程序崩溃了（同样由本manager来finish所有余下的的未finish掉的activity）。
	 */
	public void finishAllAsyncTask(){
		for (int i = 0, size = asyncTaskStack.size(); i < size; i++){
            if (null != asyncTaskStack.get(i)){
            	Log.d(TAG, "【ATM-】finishAsyncTask：AsyncTask"
            			+ asyncTaskStack.get(i).getClass().getCanonicalName()+"正在从全局管理列表中移除.");
            	try{
            		AsyncTask at = asyncTaskStack.get(i);
            		if(at != null && !at.isCancelled())
            			at.cancel(true);
				}
				catch (Exception e){
					Log.w(TAG, "finishAllActivity时出错了，"+e.getMessage(), e);
				}
            }
	    }
		asyncTaskStack.clear();
	}
}
