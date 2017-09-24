package com.muzi.lovingd.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;


import com.muzi.lovingd.calendar.CalendarUtils;
import com.muzi.lovingd.calendar.R;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * @author: Lovingd
 * @time: 2017/9/10 11:00
 */
public class MonthAdapter extends PagerAdapter {

    private SparseArray<MonthView> mViews;
    private Context mContext;
    private TypedArray mArray;
    private MonthCalendarView mMonthCalendarView;
    private int mMonthCount;

    public MonthAdapter(Context context, TypedArray array, MonthCalendarView monthCalendarView) {
        mContext = context;
        mArray = array;
        mMonthCalendarView = monthCalendarView;
        mViews = new SparseArray<>();
        int monthCount= CalendarUtils.getDefaultMonths();
        mMonthCount = array.getInteger(R.styleable.MonthCalendarView_month_count, monthCount);
    }

    @Override
    public int getCount() {
        return mMonthCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mViews.get(position) == null) {
            int date[] = getYearAndMonth(position);
            MonthView monthView = new MonthView(mContext, mArray, date[0], date[1]);
            monthView.setId(position);
            monthView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            monthView.invalidate();
            monthView.setOnDateClickListener(mMonthCalendarView);
            mViews.put(position, monthView);
        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    private int[] getYearAndMonth(int position) {
        int date[] = new int[2];
        DateTime time = new DateTime();
        Calendar ca=Calendar.getInstance();
        int year1=ca.get(Calendar.YEAR);
        int month1=ca.get(Calendar.MONTH);
        int months = CalendarUtils.getMonthsAgo(CalendarUtils.startYear, CalendarUtils.startMonth, year1, month1);

        time = time.plusMonths(position - months);
        date[0] = time.getYear();
        date[1] = time.getMonthOfYear() - 1;
        return date;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public SparseArray<MonthView> getViews() {
        return mViews;
    }

    public int getMonthCount() {
        return mMonthCount;
    }

}
