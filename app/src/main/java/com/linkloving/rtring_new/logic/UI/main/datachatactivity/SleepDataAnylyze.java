package com.linkloving.rtring_new.logic.UI.main.datachatactivity;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo.wang on 2016/4/13.
 */
public class SleepDataAnylyze {
    private final String TAG = SleepDataAnylyze.class.getSimpleName();
    public SleepDataAnylyze() {
    }
    public List<AnylyzeResult> anylyzeDetailData(List<BRDetailData> detailData){
        List<AnylyzeResult> anylyzeResult = new ArrayList<AnylyzeResult>();
        AnylyzeResult result = new AnylyzeResult();
        result.setBeginIndex(0);
        result.setisSleep(detailData.get(0).isSleep());
        int i =0;
        anylyze: while(i < detailData.size() -1)
        {
            MyLog.i(TAG,"转换前的数据"+detailData.get(i).toString());
            if(detailData.get(i+1).isSleep()  != detailData.get(i).isSleep()){
                result.setEndIndex(i);
                anylyzeResult.add(result);
                result = new AnylyzeResult();
                result.setisSleep(detailData.get(i+1).isSleep());
                result.setBeginIndex(i+1);
            }
            i++;
        }
        result.setEndIndex(i);
        anylyzeResult.add(result);
        return anylyzeResult;
    }
    public List<AnylyzeResult> anylyzeSportRecord(List<SportRecord> detailData){
        List<AnylyzeResult> anylyzeResult = new ArrayList<AnylyzeResult>();
        AnylyzeResult result = new AnylyzeResult();
        result.setBeginIndex(0);
        result.setisSleep(isSleep(detailData.get(0)));
        int i =0;
        anylyze: while(i < detailData.size() -1)
        {
            MyLog.i(TAG, "转换前的数据" + detailData.get(i).toString());
            if(isSleep(detailData.get(i+1))  != isSleep(detailData.get(i))){
                result.setEndIndex(i);
                anylyzeResult.add(result);
                result = new AnylyzeResult();
                result.setisSleep(isSleep(detailData.get(i + 1)));
                result.setBeginIndex(i+1);
            }
            i++;
        }
        result.setEndIndex(i);
        anylyzeResult.add(result);
        return anylyzeResult;
    }

    private boolean isSleep(SportRecord sportRecord){
        switch (Integer.parseInt(sportRecord.getState())){
            case 4:
                return true;
            case 5:
                return true;
                default:
                    return false;
        }
    }

}
