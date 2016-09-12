package com.linkloving.rtring_new.logic.UI.more.cloud;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.utils.ToolKits;

/**
 * Created by Administrator on 2016/9/12.
 */
public class CloudAlertDialog extends AlertDialog.Builder implements RadioGroup.OnCheckedChangeListener{
    private static final String TAG = CloudAlertDialog.class.getSimpleName();
    private static final int ONE_MONTH = 30;
    private static final int THREE_MONTH= 90;
    private static final int ONE_YEAR = 365;
    private static final int DATE_ALL = 0; //0是全部日期
    private ICloudListener listener;
    private int dateType ;

    public CloudAlertDialog(Context context) {
        super(context);
        setTitle(ToolKits.getStringbyId(context, R.string.main_more_sycn_title));
        setMessage(ToolKits.getStringbyId(context, R.string.main_more_sycn_message));
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout =inflater.inflate(R.layout.cloudlayout, null);
        setView(layout);
        RadioGroup group = (RadioGroup) layout.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(this);
        setPositiveButton(ToolKits.getStringbyId(context, R.string.general_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.checkValue(dateType);
            }
        });
        setNegativeButton(ToolKits.getStringbyId(context, R.string.general_no),null);
        create();
        show();
    }


    public CloudAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public CloudAlertDialog setCheckListener(ICloudListener listener){
        this.listener = listener;
        return null;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //获取变更后的选中项的ID
        int radioButtonId = group.getCheckedRadioButtonId();
        switch (radioButtonId){
            case R.id.month1data:
                dateType = ONE_MONTH;
                break;
            case R.id.month3data:
                dateType = THREE_MONTH;
                break;
            case R.id.month12data:
                dateType = ONE_YEAR;
                break;
            case R.id.alldata:
                dateType = DATE_ALL;
                break;
        }
    }
}
