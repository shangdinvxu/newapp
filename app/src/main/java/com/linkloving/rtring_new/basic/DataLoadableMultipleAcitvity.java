package com.linkloving.rtring_new.basic;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.linkloving.rtring_new.utils.manager.AsyncTaskManger;

/**
 * Created by zkx on 2016/2/24.
 */
public class DataLoadableMultipleAcitvity extends ActivityRoot {
    private AsyncTaskManger asyncTaskManger = new AsyncTaskManger();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        init();
    }
    protected void init()
    {
        initDataFromIntent();
        initViews();
        initListeners();
    }

    protected void initDataFromIntent()
    {
    }

    protected void initViews()
    {
    }

    protected void initListeners()
    {
    }

//    protected abstract void refreshToView(String taskName,Object taskObj,Object paramObject);

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(getOptionsMenuRes(), menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        fireOptionsItemSelected(item.getItemId());
        return true;
    }

    protected int getOptionsMenuRes()
    {
        return RHolder.getInstance().getEva$android$R().menu("common_data_loadable_activity_menu");
    }

    protected void fireOptionsItemSelected(int itemId)
    {
        if (itemId == RHolder.getInstance().getEva$android$R().id("newspaper_list_menu_back"))
        {
            finish();
        }
    }

    public void removeAllAsyncTask()
    {
        asyncTaskManger.finishAllAsyncTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllAsyncTask();
    }
}
