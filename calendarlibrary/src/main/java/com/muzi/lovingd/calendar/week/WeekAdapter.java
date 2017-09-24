package com.muzi.lovingd.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;


import com.muzi.lovingd.calendar.CalendarUtils;
import com.muzi.lovingd.calendar.R;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * @author: Lovingd
 * @time: 2017/9/10 11:00
 */
public class WeekAdapter extends PagerAdapter {

    private SparseArray<WeekView> mViews;
    private Context mContext;
    private TypedArray mArray;
    private WeekCalendarView mWeekCalendarView;
    private DateTime mStartDate;
    private int mWeekCount = 220;

    public WeekAdapter(Context context, TypedArray array, WeekCalendarView weekCalendarView) {
        mContext = context;
        mArray = array;
        mWeekCalendarView = weekCalendarView;
        mViews = new SparseArray<>();
        initStartDate();
        //  这个时间是初虑计算的
        int weekCount = CalendarUtils.getDefaultWeeks();
        mWeekCount = array.getInteger(R.styleable.WeekCalendarView_week_count, weekCount);
    }

    private void initStartDate() {
        mStartDate = new DateTime();
        mStartDate = mStartDate.plusDays(-mStartDate.getDayOfWeek() % 7);
    }

    @Override
    public int getCount() {
        return mWeekCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        for (int i = 0; i < 3; i++) {
            if (position - 2 + i >= 0 && position - 2 + i < mWeekCount && mViews.get(position - 2 + i) == null) {
                instanceWeekView(position - 2 + i);
            }
        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public SparseArray<WeekView> getViews() {
        return mViews;
    }

    public int getWeekCount() {
        return mWeekCount;
    }

    public WeekView instanceWeekView(int position) {
//        WeekView weekView = new WeekView(mContext, mArray, mStartDate.plusWeeks(position - mWeekCount / 2));
        Calendar calendar = Calendar.getInstance();
        int weeks = CalendarUtils.getWeeksAgo(CalendarUtils.startYear, CalendarUtils.startMonth, CalendarUtils.startDay, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        WeekView weekView = new WeekView(mContext, mArray, mStartDate.plusWeeks(position - weeks));
        weekView.setId(position);
        weekView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        weekView.setOnWeekClickListener(mWeekCalendarView);
        weekView.invalidate();
        mViews.put(position, weekView);
        return weekView;
    }

}
