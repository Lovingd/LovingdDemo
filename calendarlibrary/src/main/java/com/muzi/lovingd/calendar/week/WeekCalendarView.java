package com.muzi.lovingd.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;


import com.muzi.lovingd.calendar.CalendarUtils;
import com.muzi.lovingd.calendar.OnCalendarClickListener;
import com.muzi.lovingd.calendar.R;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * @author: Lovingd
 * @time: 2017/9/10 11:00
 */
public class WeekCalendarView extends ViewPager implements OnWeekClickListener {

    private OnCalendarClickListener mOnCalendarClickListener;
    private WeekAdapter mWeekAdapter;
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
            setPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public WeekCalendarView(Context context) {
        this(context, null);
    }

    public WeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initWeekAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.WeekCalendarView));
    }

    private void initWeekAdapter(Context context, TypedArray array) {
        mWeekAdapter = new WeekAdapter(context, array, this);
        setAdapter(mWeekAdapter);
        Calendar calendar = Calendar.getInstance();

        int weeks = CalendarUtils.getWeeksAgo(CalendarUtils.startYear, CalendarUtils.startMonth, CalendarUtils.startDay, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        setCurrentItem(weeks, false);
    }

    @Override
    public void onClickDate(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
    }

    private void setPageSelected(final int position) {
        WeekView weekView = mWeekAdapter.getViews().get(position);
        if (weekView != null) {

            Calendar calendar = Calendar.getInstance();
            boolean isToday = false;
            boolean isOther = false;
            int index = 0;


            otherTo:
            if (position == 0) {
                for (int i = 0; i < 7; i++) {
                    DateTime date = weekView.getmStartDate().plusDays(i);
                    if (date.getDayOfMonth() == 1) {
                        index = i;
                        isOther = true;
                        break otherTo;
                    }
                }
            }
            isToday = isToday(weekView, calendar, isToday);
            if (isToday) {
                if (mOnCalendarClickListener != null) {
                    // 这个是显示今天
                    mOnCalendarClickListener.onPageChange(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }
                weekView.clickThisWeek(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            } else {
                if (isOther) {
                    DateTime date = weekView.getmStartDate().plusDays(index);
                    if (mOnCalendarClickListener != null) {
                        //  这个是默认的页面是当周的星期天
                        mOnCalendarClickListener.onPageChange(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
                    }
                    weekView.clickThisWeek(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
                } else {
                    DateTime date = weekView.getmStartDate().plusDays(0);
                    if (mOnCalendarClickListener != null) {
                        //   这个是默认的页面是当周的星期天
                        mOnCalendarClickListener.onPageChange(date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());
                    }
                    weekView.clickThisWeek(date.getYear(), date.getMonthOfYear()-1, date.getDayOfMonth());
                }
            }

        } else {
            WeekCalendarView.this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOnPageChangeListener.onPageSelected(position);
                }
            }, 50);
        }
    }

    /**
     * 判断 是否是今天 通过区间判断节省 for循环的次数
     * @param weekView
     * @param calendar
     * @param isToday
     * @return isToday
     */
    private boolean isToday(WeekView weekView, Calendar calendar, boolean isToday) {
        DateTime date0 = weekView.getmStartDate().plusDays(0);
        DateTime date6 = weekView.getmStartDate().plusDays(6);
        DateTime dateCalendar = new DateTime(calendar);

        todayTo:
        if (CalendarUtils.compareDateTime(date0, dateCalendar) == 1 && CalendarUtils.compareDateTime(dateCalendar, date6) == 1) {
            for (int i = 0; i < 7; i++) {
                DateTime date = weekView.getmStartDate().plusDays(i);

                if (date.getYear() == calendar.get(Calendar.YEAR)
                        && date.getMonthOfYear() - 1 == calendar.get(Calendar.MONTH)
                        && date.getDayOfMonth() == calendar.get(Calendar.DAY_OF_MONTH)) {
                    isToday = true;
                    break todayTo;
                } else {
                    isToday = false;

                }
            }
        } else if (CalendarUtils.compareDateTime(date0, dateCalendar) == 0) {
            isToday = true;

        } else if (CalendarUtils.compareDateTime(dateCalendar, date6) == 0) {
            isToday = true;
        }
        return isToday;
    }

    /**
     * 设置点击日期监听
     *
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<WeekView> getWeekViews() {
        return mWeekAdapter.getViews();
    }

    public WeekAdapter getWeekAdapter() {
        return mWeekAdapter;
    }

    public WeekView getCurrentWeekView() {
        return getWeekViews().get(getCurrentItem());
    }

}
