package com.linkloving.rtring_new.logic.UI.main.datachatactivity.sleepUtils;

import android.os.Parcel;

import com.github.mikephil.charting.data.Entry;

/**
 * Created by Administrator on 2016/4/11.
 */
public class SleepEntry extends Entry {
    public SleepEntry(float val, int xIndex) {
        super(val, xIndex);
    }
    public SleepEntry(float val, int xIndex, Object data) {
        super(val, xIndex, data);
    }
    protected SleepEntry(Parcel in) {
        super(in);
    }
}
