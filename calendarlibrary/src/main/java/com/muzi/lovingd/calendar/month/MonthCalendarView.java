package com.muzi.lovingd.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;


import com.muzi.lovingd.calendar.CalendarUtils;
import com.muzi.lovingd.calendar.OnCalendarClickListener;
import com.muzi.lovingd.calendar.R;

import java.util.Calendar;

/**
 * @author: Lovingd
 * @time: 2017/9/10 11:00
 */
public class MonthCalendarView extends ViewPager implements OnMonthClickListener {

    private MonthAdapter mMonthAdapter;
    private OnCalendarClickListener mOnCalendarClickListener;
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

    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initMonthAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView));
    }

    private void initMonthAdapter(Context context, TypedArray array) {
        mMonthAdapter = new MonthAdapter(context, array, this);
        setAdapter(mMonthAdapter);
        Calendar ca = Calendar.getInstance();
        int year1 = ca.get(Calendar.YEAR);
        int month1 = ca.get(Calendar.MONTH);
        int months = CalendarUtils.getMonthsAgo(CalendarUtils.startYear, CalendarUtils.startMonth, year1, month1);
        setCurrentItem(months, false);
    }

    @Override
    public void onClickThisMonth(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
    }

    @Override
    public void onClickLastMonth(int year, int month, int day) {
        if (getCurrentItem() - 1 >= 0) {
            MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() - 1);
            if (monthDateView != null) {
                monthDateView.setSelectYearMonth(year, month, day);
            }
            setCurrentItem(getCurrentItem() - 1, true);
        }
    }

    @Override
    public void onClickNextMonth(int year, int month, int day) {
        if (getCurrentItem() + 1 < mMonthAdapter.getCount()) {
            MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() + 1);
            if (monthDateView != null) {
                monthDateView.setSelectYearMonth(year, month, day);
                monthDateView.invalidate();
            }
            onClickThisMonth(year, month, day);
            setCurrentItem(getCurrentItem() + 1, true);
        }
    }


    private void setPageSelected(final int position) {
        MonthView monthView = mMonthAdapter.getViews().get(getCurrentItem());
        if (monthView != null) {
            Calendar calendar = Calendar.getInstance();
            if (monthView.getSelectYear() == calendar.get(Calendar.YEAR) && monthView.getSelectMonth() == calendar.get(Calendar.MONTH)) {
                if (mOnCalendarClickListener != null) {
                    mOnCalendarClickListener.onPageChange(monthView.getSelectYear(), monthView.getSelectMonth(), calendar.get(Calendar.DAY_OF_MONTH));
                }
                monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), calendar.get(Calendar.DAY_OF_MONTH));
            } else {
                if (mOnCalendarClickListener != null) {
                    mOnCalendarClickListener.onPageChange(monthView.getSelectYear(), monthView.getSelectMonth(), 1);
                }
                monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(), 1);
            }

        } else {
            MonthCalendarView.this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOnPageChangeListener.onPageSelected(position);
                }
            }, 50);
        }
    }

    /**
     * 跳转到今天
     */
    public void setTodayToView() {
        setCurrentItem(mMonthAdapter.getMonthCount() / 2, true);
        MonthView monthView = mMonthAdapter.getViews().get(mMonthAdapter.getMonthCount() / 2);
        if (monthView != null) {
            Calendar calendar = Calendar.getInstance();
            monthView.clickThisMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }
    }

    /**
     * 设置点击日期监听
     *
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<MonthView> getMonthViews() {
        return mMonthAdapter.getViews();
    }

    public MonthView getCurrentMonthView() {
        return getMonthViews().get(getCurrentItem());
    }

}
