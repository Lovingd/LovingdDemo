package com.muzi.lovingd.calendar.schedule;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.muzi.lovingd.calendar.CalendarUtils;
import com.muzi.lovingd.calendar.OnCalendarClickListener;
import com.muzi.lovingd.calendar.R;
import com.muzi.lovingd.calendar.month.MonthCalendarView;
import com.muzi.lovingd.calendar.month.MonthView;
import com.muzi.lovingd.calendar.week.WeekCalendarView;
import com.muzi.lovingd.calendar.week.WeekView;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

/**
 * @author: Lovingd
 * @time: 2017/9/10 11:00
 */
public class ScheduleLayout extends FrameLayout {

    private final int DEFAULT_MONTH = 0;
    private final int DEFAULT_WEEK = 1;
    /**
     * 绑定 月和周 日历视图
     */
    int monthRow = 6;
    private MonthCalendarView mcvCalendar;
    private WeekCalendarView wcvCalendar;
    //    private RelativeLayout rlMonthCalendar;
    private RelativeLayout rlScheduleList;
    private ScheduleRecyclerView rvScheduleList;
    private int mCurrentSelectYear;
    private int mCurrentSelectMonth;
    private int mCurrentSelectDay;
    private int mRowSize;
    private int mDefaultView;
    private int mAutoScrollDistance;
    private boolean mIsAutoChangeMonthRow;
    private int mCurrentRows = 6;
    private ScheduleState mState;
    private int mMinDistance;
    private float mDownPosition[] = new float[2];
    private GestureDetector mGestureDetector;
    private boolean mIsScrolling = false;
    private OnCalendarClickListener mOnCalendarClickListener;
    private OnCalendarClickListener mMonthCalendarClickListener = new OnCalendarClickListener() {
        @Override
        public void onClickDate(int year, int month, int day) {
            wcvCalendar.setOnCalendarClickListener(null);
            int weeks = CalendarUtils.getWeeksAgo(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay, year, month, day);
            resetCurrentSelectDate(year, month, day);
            int position = wcvCalendar.getCurrentItem() + weeks;
            if (weeks != 0) {
                wcvCalendar.setCurrentItem(position, false);
            }
            resetWeekView(position);
            wcvCalendar.setOnCalendarClickListener(mWeekCalendarClickListener);
        }

        @Override
        public void onPageChange(int year, int month, int day) {
            computeCurrentRowsIsSix(year, month);
        }
    };
    private OnCalendarClickListener mWeekCalendarClickListener = new OnCalendarClickListener() {
        @Override
        public void onClickDate(int year, int month, int day) {
            mcvCalendar.setOnCalendarClickListener(null);
            int months = CalendarUtils.getMonthsAgo(mCurrentSelectYear, mCurrentSelectMonth, year, month);
            resetCurrentSelectDate(year, month, day);
            if (months != 0) {
                int position = mcvCalendar.getCurrentItem() + months;
                mcvCalendar.setCurrentItem(position, false);
            }
            resetMonthView();
            mcvCalendar.setOnCalendarClickListener(mMonthCalendarClickListener);
            if (mIsAutoChangeMonthRow) {
                mCurrentRows = CalendarUtils.getMonthRows(year, month);
            }
        }

        @Override
        public void onPageChange(int year, int month, int day) {
            if (mIsAutoChangeMonthRow) {
                if (mCurrentSelectMonth != month) {
                    mCurrentRows = CalendarUtils.getMonthRows(year, month);
                }
            }
        }
    };
    private ShowScheduleStateListener mShowScheduleStateListener;

    public ScheduleLayout(Context context) {
        this(context, null);
    }

    public ScheduleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context.obtainStyledAttributes(attrs, R.styleable.ScheduleLayout));
        initDate();
        initGestureDetector();
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new OnScheduleScrollListener(this));
    }

    private void initAttrs(TypedArray array) {
        mDefaultView = array.getInt(R.styleable.ScheduleLayout_default_view, DEFAULT_MONTH);
        mIsAutoChangeMonthRow = array.getBoolean(R.styleable.ScheduleLayout_auto_change_month_row, false);
        array.recycle();
        mState = ScheduleState.OPEN;
        mRowSize = getResources().getDimensionPixelSize(R.dimen.week_calendar_height);
        mAutoScrollDistance = getResources().getDimensionPixelSize(R.dimen.auto_scroll_distance);
        mMinDistance = getResources().getDimensionPixelSize(R.dimen.calendar_min_distance);
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        resetCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mcvCalendar = (MonthCalendarView) findViewById(R.id.mcvCalendar);
        wcvCalendar = (WeekCalendarView) findViewById(R.id.wcvCalendar);
//        rlMonthCalendar = (RelativeLayout) findViewById(R.id.rlMonthCalendar);
        rlScheduleList = (RelativeLayout) findViewById(R.id.rlScheduleList);
        rvScheduleList = (ScheduleRecyclerView) findViewById(R.id.rvScheduleList);
        bindingMonthAndWeekCalendar();
    }

    private void bindingMonthAndWeekCalendar() {
        mcvCalendar.setOnCalendarClickListener(mMonthCalendarClickListener);
        wcvCalendar.setOnCalendarClickListener(mWeekCalendarClickListener);
        // 初始化视图
        Calendar calendar = Calendar.getInstance();
        if (mIsAutoChangeMonthRow) {
            mCurrentRows = CalendarUtils.getMonthRows(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        }
        if (mDefaultView == DEFAULT_MONTH) {
            wcvCalendar.setVisibility(GONE);
            mState = ScheduleState.OPEN;

            if (mCurrentRows == 5) {
                rlScheduleList.setY(rlScheduleList.getY() - mRowSize);
            } else if (mCurrentRows == 4) {
                rlScheduleList.setY(rlScheduleList.getY() - 2 * mRowSize);
            }
        } else if (mDefaultView == DEFAULT_WEEK) {
            wcvCalendar.setVisibility(VISIBLE);
            mState = ScheduleState.CLOSE;
            int row = CalendarUtils.getWeekRow(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            mcvCalendar.setY(-row * mRowSize);
            rlScheduleList.setY(rlScheduleList.getY() - 5 * mRowSize);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosition[0] = ev.getRawX();
                mDownPosition[1] = ev.getRawY();
                mGestureDetector.onTouchEvent(ev);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsScrolling) {
            return true;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                float x = ev.getRawX();
                float y = ev.getRawY();
                float distanceX = Math.abs(x - mDownPosition[0]);
                float distanceY = Math.abs(y - mDownPosition[1]);
                if (distanceY > mMinDistance && distanceY > distanceX * 2.0f) {
                    return (y > mDownPosition[1] && isRecyclerViewTouch()) || (y < mDownPosition[1] && mState == ScheduleState.OPEN);
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isRecyclerViewTouch() {
        return mState == ScheduleState.CLOSE && (rvScheduleList.getChildCount() == 0 || rvScheduleList.computeVerticalScrollOffset() == 0);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosition[0] = event.getRawX();
                mDownPosition[1] = event.getRawY();
                resetCalendarPosition();
                return true;
            case MotionEvent.ACTION_MOVE:
                transferEvent(event);
                mIsScrolling = true;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                transferEvent(event);
                mGestureDetector.onTouchEvent(event);
                changeCalendarState();
                resetScrollingState();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void transferEvent(MotionEvent event) {
        if (mState == ScheduleState.CLOSE) {
            mcvCalendar.setVisibility(VISIBLE);
            wcvCalendar.setVisibility(INVISIBLE);
            mGestureDetector.onTouchEvent(event);
        } else {
            mGestureDetector.onTouchEvent(event);
        }
    }

    protected void onCalendarScroll(float distanceY) {
        MonthView monthView = mcvCalendar.getCurrentMonthView();
        distanceY = Math.min(distanceY, mAutoScrollDistance);
        float calendarDistanceY = distanceY / (5.0f);
        if (mCurrentRows == 6) {
            calendarDistanceY = distanceY / (5.0f);
        } else if (mCurrentRows == 5) {
            calendarDistanceY = distanceY / (4.0f);
        } else {
            calendarDistanceY = distanceY / (3.0f);
        }
        int row = monthView.getWeekRow() - 1;
        int calendarTop = -row * mRowSize;
        int scheduleTop = mRowSize;
        float calendarY = mcvCalendar.getY() - calendarDistanceY * row;
        calendarY = Math.min(calendarY, 0);
        calendarY = Math.max(calendarY, calendarTop);
        mcvCalendar.setY(calendarY);
        float scheduleY = rlScheduleList.getY() - distanceY;
        if (mCurrentRows == 6) {
            scheduleY = Math.min(scheduleY, mcvCalendar.getHeight());
        } else if (mCurrentRows == 5) {
            scheduleY = Math.min(scheduleY, mcvCalendar.getHeight() - mRowSize);
        } else {
            scheduleY = Math.min(scheduleY, mcvCalendar.getHeight() - 2 * mRowSize);

        }
        scheduleY = Math.max(scheduleY, scheduleTop);
        rlScheduleList.setY(scheduleY);
    }

    private void changeCalendarState() {
        if (rlScheduleList.getY() > mRowSize * 2 &&
                rlScheduleList.getY() < mcvCalendar.getHeight() - mRowSize) { // 位于中间
            ScheduleAnimation animation = new ScheduleAnimation(this, mState, mAutoScrollDistance);
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    changeState();
                    mShowScheduleStateListener.showState(mState);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlScheduleList.startAnimation(animation);
        } else if (rlScheduleList.getY() <= mRowSize * 2) { // 位于顶部
            ScheduleAnimation animation = new ScheduleAnimation(this, ScheduleState.OPEN, mAutoScrollDistance);
            animation.setDuration(50);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mState == ScheduleState.OPEN) {
                        changeState();
                    } else {
                        resetCalendar();
                    }
                    mShowScheduleStateListener.showState(mState);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlScheduleList.startAnimation(animation);
        } else {
            ScheduleAnimation animation = new ScheduleAnimation(this, ScheduleState.CLOSE, mAutoScrollDistance);
            animation.setDuration(50);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mState == ScheduleState.CLOSE) {
                        mState = ScheduleState.OPEN;
                    }
                    mShowScheduleStateListener.showState(mState);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlScheduleList.startAnimation(animation);
        }
    }


    private void resetScrollingState() {
        mDownPosition[0] = 0;
        mDownPosition[1] = 0;
        mIsScrolling = false;
    }


    private void resetCalendar() {
        if (mState == ScheduleState.OPEN) {
            mcvCalendar.setVisibility(VISIBLE);
            wcvCalendar.setVisibility(INVISIBLE);
        } else {
            mcvCalendar.setVisibility(INVISIBLE);
            wcvCalendar.setVisibility(VISIBLE);
        }
    }

    private void resetCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
    }

    private void computeCurrentRowsIsSix(int year, int month) {
        if (mIsAutoChangeMonthRow) {
            int rows = CalendarUtils.getMonthRows(year, month);
            if (rows != mCurrentRows) {

                if (mState == ScheduleState.OPEN) {

                    AutoMoveAnimation animation = new AutoMoveAnimation(rlScheduleList, (rows - mCurrentRows) * mRowSize);
                    rlScheduleList.startAnimation(animation);
                    Log.e("TAG___", "computeCurrentRowsIsSix: " + rlScheduleList.getY());

                    mcvCalendar.setY(0);
                }
                mCurrentRows = rows;
            }

        }
    }

    private void resetWeekView(int position) {
        WeekView weekView = wcvCalendar.getCurrentWeekView();
        if (weekView != null) {
            weekView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
            weekView.invalidate();
        } else {
            WeekView newWeekView = wcvCalendar.getWeekAdapter().instanceWeekView(position);
            newWeekView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
            newWeekView.invalidate();
            wcvCalendar.setCurrentItem(position);
        }
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
        }
    }

    private void resetMonthView() {
        MonthView monthView = mcvCalendar.getCurrentMonthView();
        if (monthView != null) {
            monthView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
            monthView.invalidate();
        }
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
        }
        resetCalendarPosition();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        resetViewHeight(rlScheduleList, height - mRowSize);
//        resetViewHeight(this, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void resetViewHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.height != height) {
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    private void resetCalendarPosition() {
        if (mState == ScheduleState.OPEN) {
            mcvCalendar.setY(0);
            if (mCurrentRows == 6) {
                rlScheduleList.setY(mcvCalendar.getHeight());
            } else if (mCurrentRows == 5) {
                rlScheduleList.setY(mcvCalendar.getHeight() - mRowSize);
            } else {
                rlScheduleList.setY(mcvCalendar.getHeight() - 2 * mRowSize);
            }
        } else {
            mcvCalendar.setY(-CalendarUtils.getWeekRow(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay) * mRowSize);
            rlScheduleList.setY(mRowSize);
        }
    }


    private void changeState() {
        if (mState == ScheduleState.OPEN) {
            mState = ScheduleState.CLOSE;
            mcvCalendar.setVisibility(INVISIBLE);
            wcvCalendar.setVisibility(VISIBLE);
            mcvCalendar.setY((1 - mcvCalendar.getCurrentMonthView().getWeekRow()) * mRowSize);
            checkWeekCalendar();
        } else {
            mState = ScheduleState.OPEN;
            mcvCalendar.setVisibility(VISIBLE);
            wcvCalendar.setVisibility(INVISIBLE);
            mcvCalendar.setY(0);
        }
    }

    private void checkWeekCalendar() {
        WeekView weekView = wcvCalendar.getCurrentWeekView();
        DateTime start = weekView.getStartDate();
        DateTime end = weekView.getEndDate();
        DateTime current = new DateTime(mCurrentSelectYear, mCurrentSelectMonth + 1, mCurrentSelectDay, 23, 59, 59);
        int week = 0;
        while (current.getMillis() < start.getMillis()) {
            week--;
            start = start.plusDays(-7);
        }
        current = new DateTime(mCurrentSelectYear, mCurrentSelectMonth + 1, mCurrentSelectDay, 0, 0, 0);
        if (week == 0) {
            while (current.getMillis() > end.getMillis()) {
                week++;
                end = end.plusDays(7);
            }
        }
        if (week != 0) {
            int position = wcvCalendar.getCurrentItem() + week;
            if (wcvCalendar.getWeekViews().get(position) != null) {
                wcvCalendar.getWeekViews().get(position).setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
                wcvCalendar.getWeekViews().get(position).invalidate();
            } else {
                WeekView newWeekView = wcvCalendar.getWeekAdapter().instanceWeekView(position);
                newWeekView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
                newWeekView.invalidate();
            }
            wcvCalendar.setCurrentItem(position, false);
        }
    }


    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    private void resetMonthViewDate(final int year, final int month, final int day, final int position) {
        if (mcvCalendar.getMonthViews().get(position) == null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetMonthViewDate(year, month, day, position);
                }
            }, 50);
        } else {
            mcvCalendar.getMonthViews().get(position).clickThisMonth(year, month, day);
        }
    }

    /**
     * 初始化年月日
     *
     * @param year
     * @param month (0-11)
     * @param day   (1-31)
     */
    public void initData(int year, int month, int day) {
        int monthDis = CalendarUtils.getMonthsAgo(mCurrentSelectYear, mCurrentSelectMonth, year, month);
        int position = mcvCalendar.getCurrentItem() + monthDis;
        mcvCalendar.setCurrentItem(position);
        resetMonthViewDate(year, month, day, position);
    }

    /**
     * 添加多个圆点提示
     *
     * @param hints
     */
    public void addTaskHints(List<Integer> hints) {
        CalendarUtils.getInstance(getContext()).addTaskHints(mCurrentSelectYear, mCurrentSelectMonth, hints);
        if (mcvCalendar.getCurrentMonthView() != null) {
            mcvCalendar.getCurrentMonthView().invalidate();
        }
        if (wcvCalendar.getCurrentWeekView() != null) {
            wcvCalendar.getCurrentWeekView().invalidate();
        }
    }

    /**
     * 添加多个圆点提示
     *
     * @param hints
     */
    public void addMoreTaskHints(List<Integer> lasthints, List<Integer> hints, List<Integer> nexthints) {
        int lastMonth = mCurrentSelectMonth - 1;
        int lastYear = mCurrentSelectYear;
        if (lastMonth == 0) {
            lastMonth = 11;
            lastYear = mCurrentSelectYear - 1;
        }
        int nextMonth = mCurrentSelectMonth + 1;
        int nextYear = mCurrentSelectYear;
        if (lastMonth == 11) {
            nextMonth = 0;
            nextYear = mCurrentSelectYear + 1;
        }
        if (lasthints.size() > 0) {
            CalendarUtils.getInstance(getContext()).addTaskHints(lastYear, lastMonth, lasthints);
        }
        if (hints.size() > 0) {
            CalendarUtils.getInstance(getContext()).addTaskHints(mCurrentSelectYear, mCurrentSelectMonth, hints);
        }
        if (nexthints.size() > 0) {
            CalendarUtils.getInstance(getContext()).addTaskHints(nextYear, nextMonth, nexthints);
        }
        if (mcvCalendar.getCurrentMonthView() != null) {
            mcvCalendar.getCurrentMonthView().invalidate();
        }
        if (wcvCalendar.getCurrentWeekView() != null) {
            wcvCalendar.getCurrentWeekView().invalidate();
        }
    }

    /**
     * 清空所有的小圆点数据
     *
     * @return
     */
    public boolean cleanTaskHints() {
        return CalendarUtils.getInstance(getContext()).claenTaskHints();
    }

    /**
     * 删除多个圆点提示
     *
     * @param hints
     */
    public void removeTaskHints(List<Integer> hints) {
        CalendarUtils.getInstance(getContext()).removeTaskHints(mCurrentSelectYear, mCurrentSelectMonth, hints);
        if (mcvCalendar.getCurrentMonthView() != null) {
            mcvCalendar.getCurrentMonthView().invalidate();
        }
        if (wcvCalendar.getCurrentWeekView() != null) {
            wcvCalendar.getCurrentWeekView().invalidate();
        }
    }

    /**
     * 添加一个圆点提示
     *
     * @param day
     */
    public void addTaskHint(Integer day) {
        if (mcvCalendar.getCurrentMonthView() != null) {
            if (mcvCalendar.getCurrentMonthView().addTaskHint(day)) {
                if (wcvCalendar.getCurrentWeekView() != null) {
                    wcvCalendar.getCurrentWeekView().invalidate();
                }
            }
        }
    }

    /**
     * 删除一个圆点提示
     *
     * @param day
     */
    public void removeTaskHint(Integer day) {
        if (mcvCalendar.getCurrentMonthView() != null) {
            if (mcvCalendar.getCurrentMonthView().removeTaskHint(day)) {
                if (wcvCalendar.getCurrentWeekView() != null) {
                    wcvCalendar.getCurrentWeekView().invalidate();
                }
            }
        }
    }

    /**
     * 获得列表的控件
     *
     * @return
     */
    public ScheduleRecyclerView getRecyclerView() {
        return rvScheduleList;
    }

    /**
     * 月日历(月视图)
     *
     * @return
     */
    public MonthCalendarView getMonthCalendar() {
        return mcvCalendar;
    }

    /**
     * 一周的日历(周视图)
     *
     * @return
     */
    public WeekCalendarView getWeekCalendar() {
        return wcvCalendar;
    }

    /**
     * 当前选择年
     *
     * @return
     */
    public int getCurrentSelectYear() {
        return mCurrentSelectYear;
    }

    /**
     * 当前选择月
     *
     * @return
     */
    public int getCurrentSelectMonth() {
        return mCurrentSelectMonth;
    }

    /**
     * 当前选择的一天
     *
     * @return
     */
    public int getCurrentSelectDay() {
        return mCurrentSelectDay;
    }

    /**
     * 获得展示的是月视图 还是周视图
     *
     * @return OPEN  表示月视图  CLOSE 表示周视图
     */
    public ScheduleState getmState() {
        return mState;
    }

    /**
     * 上一页
     *
     * @param year
     * @param month
     * @param day
     */
    public void onClickLastMonth(int year, int month, int day) {
        mcvCalendar.onClickLastMonth(year, month, day);
    }

    /**
     * 下一页
     *
     * @param year
     * @param month
     * @param day
     */
    public void onClickNextMonth(int year, int month, int day) {
        mcvCalendar.onClickNextMonth(year, month, day);
    }

//    protected void onCalendarScroll(float distanceY) {
//        MonthView monthView = mcvCalendar.getCurrentMonthView();
//        distanceY = Math.min(distanceY, mAutoScrollDistance);
//        float calendarDistanceY = distanceY / (mCurrentRowsIsSix ? 5.0f : 4.0f);
//        int row = monthView.getWeekRow() - 1;
//        int calendarTop = -row * mRowSize;
//        int scheduleTop = mRowSize;
//        float calendarY = rlMonthCalendar.getY() - calendarDistanceY * row;
//        calendarY = Math.min(calendarY, 0);
//        calendarY = Math.max(calendarY, calendarTop);
//        rlMonthCalendar.setY(calendarY);
//        float scheduleY = rlScheduleList.getY() - distanceY;
//        if (mCurrentRowsIsSix) {
//            scheduleY = Math.min(scheduleY, mcvCalendar.getHeight());
//        } else {
//            scheduleY = Math.min(scheduleY, mcvCalendar.getHeight() - mRowSize);
//        }
//        scheduleY = Math.max(scheduleY, scheduleTop);
//        rlScheduleList.setY(scheduleY);
//    }

    public void setVisibleView() {
//        resetCalendarPosition();
        changeState();
        resetCalendarPosition();

    }

    public void setShowScheduleStateListener(ShowScheduleStateListener mShowScheduleStateListener) {
        this.mShowScheduleStateListener = mShowScheduleStateListener;
    }
}
