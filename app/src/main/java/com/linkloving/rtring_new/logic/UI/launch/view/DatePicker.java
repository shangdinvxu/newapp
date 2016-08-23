/*
 * AUTHOR：YOLANDA
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © ZhiMore. All Rights Reserved
 *
 */
package com.linkloving.rtring_new.logic.UI.launch.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.logic.UI.launch.view.wheelview.NumericWheelAdapter;
import com.linkloving.rtring_new.logic.UI.launch.view.wheelview.OnWheelChangedListener;
import com.linkloving.rtring_new.logic.UI.launch.view.wheelview.WheelView;

import java.util.Calendar;

/**
 * <p>日期选择框</p>
 * Created in Jan 12, 2016 3:50:49 PM
 * @author YOLANDA; QQ: 757699476
 */
public class DatePicker extends LinearLayout {
    /**
     * 开始的年份
     */
    public int startYear = 1940;

    /**
     * 结束的年份
     */
    public int endYear = 2222;

    /**
     * 年
     */
    private WheelView mViewYear;
    /**
     * 月
     */
    private WheelView mViewMonth;
    /**
     * 日
     */
    private WheelView mViewDay;

    private String yearNum[],
            monthNum[] = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"},
            riNum[] = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DatePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttribute();
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute();
    }

    public DatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePicker(Context context) {
        this(context, null, 0);
    }

    /**
     * 初始化View。
     */
    private void initAttribute() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_date_picker, this, true);

        mViewYear = (WheelView) findViewById(R.id.wheelview_dialog_year);
        mViewYear.setVisibleItems(4);
        mViewMonth = (WheelView) findViewById(R.id.wheelview_dialog_month);
        mViewMonth.setVisibleItems(4);
        mViewDay = (WheelView) findViewById(R.id.wheelview_dialog_day);
        mViewDay.setVisibleItems(4);

        initialize();
    }

    /**
     * 初始化。
     */
    private void initialize() {
        Calendar holdCalendar = Calendar.getInstance();// 日历的实例

        // 初始化年的轮子
        int curYear = holdCalendar.get(Calendar.YEAR);// 为高亮显示今年做准备
        // 初始化保存年的数组
        int count = 0, heightYear = 0;// 年数组临时下标、今年在数组中的下标
        yearNum = new String[endYear - startYear];
        for (int i = startYear; i < endYear; i++) {
            yearNum[count] = String.valueOf(i);
            if (i == curYear) {// 如果年份是今年则把下标记录下来
                heightYear = count;
            }
            count++;
        }
        NumericWheelAdapter yearWheelAdapter = new DateNumericAdapter(getContext(), startYear, endYear, heightYear);
        yearWheelAdapter.setLabel(getResources().getString(R.string.unit_year));//年的描述
        mViewYear.setViewAdapter(yearWheelAdapter);// 设置年的数据适配
        mViewYear.addChangingListener(mYearMonthListener);
        mViewYear.setCyclic(true);

        // 初始化月的轮子
        int curMonth = holdCalendar.get(Calendar.MONTH);
        NumericWheelAdapter monthWheelAdapter = new DateNumericAdapter(getContext(), 1, 12, curMonth);
        monthWheelAdapter.setLabel(getResources().getString(R.string.unit_month)); //月的描述
        mViewMonth.setViewAdapter(monthWheelAdapter);
        mViewMonth.addChangingListener(mYearMonthListener);
        mViewMonth.setCyclic(true);

        // 选中今天
        mViewYear.setCurrentItem(holdCalendar.get(Calendar.YEAR) - startYear);
        mViewMonth.setCurrentItem(holdCalendar.get(Calendar.MONTH));

        // 初始化日的轮子
        updateDays();
        mViewDay.setCyclic(true);

        // 选中今天
        mViewDay.setCurrentItem(holdCalendar.get(Calendar.DAY_OF_MONTH) - 1);
    }

    /**
     * 设置开始年份，例如：1970。
     *
     * @param startYear 默认为1970。
     */
    public void setStartYear(int startYear) {
        this.startYear = startYear;
        initialize();
    }

    /**
     * 设置结束年份，例如：2222。
     *
     * @param endYear 默认为2222。
     */
    public void setEndYear(int endYear) {
        this.endYear = endYear;
        initialize();
    }

    /**
     * 获取日期。
     *
     * @return 返回的是三个长度的{@code String}数组，第一位是年，第二位是月，第三位是日。
     */
    public String[] getDate() {
        String year = yearNum[mViewYear.getCurrentItem()];
        String month = monthNum[mViewMonth.getCurrentItem()];
        String day = riNum[mViewDay.getCurrentItem()];
        return new String[]{year, month, day};
    }

    /**
     * 获取年。
     *
     * @return 年。
     */
    public String getYear() {
        return yearNum[mViewYear.getCurrentItem()];
    }

    /**
     * 获取月。
     *
     * @return 月。
     */
    public String getMonth() {
        return monthNum[mViewMonth.getCurrentItem()];
    }

    /**
     * 获取日。
     *
     * @return 日。
     */
    public String getDay() {
        return riNum[mViewDay.getCurrentItem()];
    }

    /**
     * 年和月改变时联动日
     */
    private OnWheelChangedListener mYearMonthListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateDays();
        }
    };

    /**
     * 更新日
     */
    private void updateDays() {
        // 1.因为日历默认是今年；2.因为除了年，日和月都是从1开始的，所以用下标替换即可
        Calendar changeCalendar = Calendar.getInstance();

        int currentYear = mViewYear.getCurrentItem() + startYear;

        changeCalendar.set(Calendar.YEAR, currentYear);
        changeCalendar.set(Calendar.MONTH, mViewMonth.getCurrentItem());

        int maxDays = changeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);// 拿到这个月的最大的日
        NumericWheelAdapter dayWheelAdapter = new DateNumericAdapter(getContext(), 1, maxDays, changeCalendar.get(Calendar.DAY_OF_MONTH) - 1);
        dayWheelAdapter.setLabel(getResources().getString(R.string.unit_day)); //日的描述
        mViewDay.setViewAdapter(dayWheelAdapter);// 设置日的轮子日期
        // 比较两个今天和选定的日的大小，之后选定小的；因为如果现在选定的是3.31，那么月切换到2月后，2月最大为29，那么就要选定29
        int curDay = Math.min(maxDays, mViewDay.getCurrentItem());
        mViewDay.setCurrentItem(curDay, true);
    }

    /**
     * 数字轮子的适配器，给当前项目高亮显示
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        /**
         * 要高亮的项目
         */
        private int highItem;
        /**
         * 当前项目的索引
         */
        private int currentItem;

        public DateNumericAdapter(Context context, int minValue, int maxValue, int heightItem) {
            super(context, minValue, maxValue);
            this.highItem = heightItem;
            setTextSize(20);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == highItem) {
                view.setTextColor(Color.parseColor("#FF4F638B"));
            }
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;// 保存当前item
            return super.getItem(index, cachedView, parent);
        }
    }
}
