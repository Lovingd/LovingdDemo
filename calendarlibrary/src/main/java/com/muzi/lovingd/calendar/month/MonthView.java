package com.muzi.lovingd.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.muzi.lovingd.calendar.CalendarUtils;
import com.muzi.lovingd.calendar.LunarCalendarUtils;
import com.muzi.lovingd.calendar.R;

import java.util.Calendar;
import java.util.List;

/**
 * @author: Lovingd
 * @time: 2017/9/10 11:00
 */
public class MonthView extends View {

    private static final int NUM_COLUMNS = 7;
    private static final int NUM_ROWS = 6;
    private Paint mPaint;
    private Paint mLunarPaint;

    private int mNormalDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mSelectBGTodayColor;
    private int mCurrentDayColor;
    private int mHintCircleColor;
    private int mLastOrNextMonthTextColor;
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;
    private int mColumnSize, mRowSize, mSelectCircleSize;
    private int mDaySize;
    private int mWeekRow; // 当前月份第几周
    private int mLunarTextSize;
    private int mLunarTextColor;
    private boolean mIsShowLunar;

    private int mCircleRadius = 5;
    private int[][] mDaysText;
    private boolean mIsShowHint;
    private boolean mIsShowAllHint = false;
    private DisplayMetrics mDisplayMetrics;
    private OnMonthClickListener mDateClickListener;
    private GestureDetector mGestureDetector;

    public MonthView(Context context, int year, int month) {
        this(context, null, year, month);
    }

    public MonthView(Context context, TypedArray array, int year, int month) {
        this(context, array, null, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int year, int month) {
        this(context, array, attrs, 0, year, month);
    }

    public MonthView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttr, int year, int month) {
        super(context, attrs, defStyleAttr);
        initAttrs(array, year, month);
        initPaint();
        initMonth();
        initGestureDetector();
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    private void initAttrs(TypedArray array, int year, int month) {
        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_text_color, Color.parseColor("#FFFFFF"));
            mSelectBGColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_color, Color.parseColor("#336EFD"));
            mSelectBGTodayColor = array.getColor(R.styleable.MonthCalendarView_month_selected_circle_today_color, Color.parseColor("#336EFD"));
            mNormalDayColor = array.getColor(R.styleable.MonthCalendarView_month_normal_text_color, Color.parseColor("#3A3F52"));
            mCurrentDayColor = array.getColor(R.styleable.MonthCalendarView_month_today_text_color, Color.parseColor("#FF8594"));
            mHintCircleColor = array.getColor(R.styleable.MonthCalendarView_month_hint_circle_color, Color.parseColor("#FE8595"));
            mLastOrNextMonthTextColor = array.getColor(R.styleable.MonthCalendarView_month_last_or_next_month_text_color, Color.parseColor("#BFC4CF"));
            mDaySize = array.getInteger(R.styleable.MonthCalendarView_month_day_text_size, 13);
            mIsShowHint = array.getBoolean(R.styleable.MonthCalendarView_month_show_task_hint, true);
            mLunarTextColor = array.getColor(R.styleable.MonthCalendarView_month_lunar_text_color, Color.parseColor("#ACA9BC"));
            mIsShowLunar = array.getBoolean(R.styleable.MonthCalendarView_month_show_lunar, true);

            mLunarTextSize = array.getInteger(R.styleable.MonthCalendarView_month_day_lunar_text_size, 8);
            mIsShowAllHint = array.getBoolean(R.styleable.MonthCalendarView_month_show_all_task_hint, false);
        } else {
            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#336EFD");
            mSelectBGTodayColor = Color.parseColor("#336EFD");
            mNormalDayColor = Color.parseColor("#3A3F52");
            mCurrentDayColor = Color.parseColor("#FF8594");
            mHintCircleColor = Color.parseColor("#FE8595");
            mLastOrNextMonthTextColor = Color.parseColor("#BFC4CF");
            mLunarTextColor=Color.parseColor("#ACA9BC");
            mDaySize = 13;
            mIsShowHint = true;
            mIsShowLunar = true;
            mIsShowAllHint = false;
        }
        mCurrentDayColor = mNormalDayColor;
        mSelYear = year;
        mSelMonth = month;
    }

    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);

        mLunarPaint = new Paint();
        mLunarPaint.setAntiAlias(true);
        mLunarPaint.setTextSize(mLunarTextSize * mDisplayMetrics.scaledDensity);
        mLunarPaint.setColor(mLunarTextColor);

    }

    private void initMonth() {
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);

        if (mSelYear == mCurrYear && mSelMonth == mCurrMonth) {
            setSelectYearMonth(mSelYear, mSelMonth, mCurrDay);
        } else {
            setSelectYearMonth(mSelYear, mSelMonth, 1);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 200;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        clearData();
        drawLastMonth(canvas);
        int selected[] = drawThisMonth(canvas);
        drawNextMonth(canvas);
        drawHintLastCircle(canvas);
        drawHintCircle(canvas);
        drawHintNextCircle(canvas);
        drawLunarText(canvas, selected);

    }


    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight() / NUM_ROWS;
        mSelectCircleSize = (int) (mColumnSize / 3.5);
        while (mSelectCircleSize > mRowSize / 2) {
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }
    }

    private void clearData() {
        mDaysText = new int[6][7];
    }

    /**
     * 上个月
     *
     * @param canvas
     */
    private void drawLastMonth(Canvas canvas) {
        int lastYear, lastMonth;
        if (mSelMonth == 0) {
            lastYear = mSelYear - 1;
            lastMonth = 11;
        } else {
            lastYear = mSelYear;
            lastMonth = mSelMonth - 1;
        }
        mPaint.setColor(mLastOrNextMonthTextColor);
        int monthDays = CalendarUtils.getMonthDays(lastYear, lastMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        for (int day = 0; day < weekNumber - 1; day++) {
            mDaysText[0][day] = monthDays - weekNumber + day + 2;
            String dayString = String.valueOf(mDaysText[0][day]);
//            if (dayString.length() == 1) {
//                dayString = "0" + dayString;
//            }
            if (dayString.equals(String.valueOf(mCurrDay)) && mCurrMonth == lastMonth && mCurrYear == lastYear) {
                dayString = "今";
            }
            int startX = (int) (mColumnSize * day + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
        }
    }

    private int[] drawThisMonth(Canvas canvas) {
        String dayString;
        int selectedPoint[] = new int[2];
        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        for (int day = 0; day < monthDays; day++) {
            dayString = String.valueOf(day + 1);
            int col = (day + weekNumber - 1) % 7;
            int row = (day + weekNumber - 1) / 7;
            mDaysText[row][col] = day + 1;
            String name = dayString;

            if (dayString.equals(String.valueOf(mSelDay))) {
                int startRecX = mColumnSize * col;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                if (mSelYear == mCurrYear && mCurrMonth == mSelMonth && day + 1 == mCurrDay) {
                    mPaint.setColor(mSelectBGTodayColor);
                    name = "今";
                } else {
                    mPaint.setColor(mSelectBGColor);
                }
                canvas.drawCircle((startRecX + endRecX) / 2, (startRecY + endRecY) / 2, mSelectCircleSize, mPaint);
                mWeekRow = row + 1;
            }
            if (dayString.equals(String.valueOf(mSelDay))) {
                selectedPoint[0] = row;
                selectedPoint[1] = col;
                mPaint.setColor(mSelectDayColor);
            } else if (dayString.equals(String.valueOf(mCurrDay)) && mCurrDay != mSelDay && mCurrMonth == mSelMonth && mCurrYear == mSelYear) {
                mPaint.setColor(mCurrentDayColor);
                name = "今";
            } else {
                mPaint.setColor(mNormalDayColor);
            }
//            if (name.length() == 1 && !name.equals("今")) {
//                name = "0" + name;
//            }
            int startX = (int) (mColumnSize * col + (mColumnSize - mPaint.measureText(name)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(name, startX, startY, mPaint);
        }
        return selectedPoint;
    }

    /**
     * 下个月
     *
     * @param canvas
     */
    private void drawNextMonth(Canvas canvas) {
        mPaint.setColor(mLastOrNextMonthTextColor);
        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        int nextMonthDays = 42 - monthDays - weekNumber + 1;
        int nextMonth = mSelMonth + 1;
        int nextYear = mSelYear;

        if (mSelMonth == 11) {
            nextMonth = 0;
            nextYear =mSelYear+ 1;
        } else {
            nextMonth = mSelMonth + 1;
            nextYear = mSelYear;
        }

        for (int day = 0; day < nextMonthDays; day++) {
            int column = (monthDays + weekNumber - 1 + day) % 7;
            int row = 5 - (nextMonthDays - day - 1) / 7;

            try {
                mDaysText[row][column] = day + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            String dayString = String.valueOf(mDaysText[row][column]);
            if (dayString.equals(String.valueOf(mCurrDay)) && mCurrMonth == nextMonth && mCurrYear == nextYear) {
                dayString = "今";
            }
//            if (dayString.length() == 1) {
//                dayString = "0" + dayString;
//            }
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
        }
    }

    /**
     * 画上个月的小圆点
     *
     * @param canvas
     */
    private void drawHintLastCircle(Canvas canvas) {
        if (mIsShowHint && mIsShowAllHint) {
            mPaint.setColor(mHintCircleColor);
            int lastYear, lastMonth;
            if (mSelMonth == 0) {
                lastYear = mSelYear - 1;
                lastMonth = 11;
            } else {
                lastYear = mSelYear;
                lastMonth = mSelMonth - 1;
            }
            List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(lastYear, lastMonth);
            if (hints.size() > 0) {
                int monthDays = CalendarUtils.getMonthDays(lastYear, lastMonth);
                int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
                for (int day = 0; day < weekNumber - 1; day++) {
                    int col = (day + 1) % 7;//+1 是因为列数是从1开始的
                    int row = (day) / 7;
                    if (!hints.contains(monthDays - weekNumber + day + 2 + 1)) continue;
                    float circleX = (float) (mColumnSize * col + mColumnSize * 0.5);
                    float circleY = (float) (mRowSize * row + mRowSize * 0.85);
                    canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);
                }
            }
        }
    }

    private void drawHintNextCircle(Canvas canvas) {
        if (mIsShowHint && mIsShowAllHint) {
            int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            int nextMonthDays = 42 - monthDays - weekNumber + 1;
            int nextMonth = mSelMonth + 1;
            int nextYear = mSelYear;
            if (nextMonth == 12) {
                nextMonth = 0;
                nextYear += 1;
            }
            mPaint.setColor(mHintCircleColor);

            List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(nextYear, nextMonth);
            if (hints.size() > 0) {
                for (int day = 0; day < nextMonthDays; day++) {
                    int col = (monthDays + weekNumber - 1 + day) % 7;
                    int row = 5 - (nextMonthDays - day - 1) / 7;
                    if (!hints.contains(day + 1)) continue;// TODO: 2017/8/29 BUG
                    float circleX = (float) (mColumnSize * col + mColumnSize * 0.5);
                    float circleY = (float) (mRowSize * row + mRowSize * 0.85);
                    canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);
                }
            }
        }
    }

    /**
     * 绘制农历
     *
     * @param canvas
     * @param selected
     */
    private void drawLunarText(Canvas canvas, int[] selected) {
        if (mIsShowLunar) {
            int firstYear, firstMonth, firstDay;
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            if (weekNumber == 1) {
                firstYear = mSelYear;
                firstMonth = mSelMonth + 1;
                firstDay = 1;
            } else {
                int monthDays;
                if (mSelMonth == 0) {
                    firstYear = mSelYear - 1;
                    firstMonth = 11;
                    monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth);
                    firstMonth = 12;
                } else {
                    firstYear = mSelYear;
                    firstMonth = mSelMonth - 1;
                    monthDays = CalendarUtils.getMonthDays(firstYear, firstMonth);
                    firstMonth = mSelMonth;
                }
                firstDay = monthDays - weekNumber + 2;
            }
            LunarCalendarUtils.Lunar lunar = LunarCalendarUtils.solarToLunar(new LunarCalendarUtils.Solar(firstYear, firstMonth, firstDay));
            int days;
            int day = lunar.lunarDay;
            int leapMonth = LunarCalendarUtils.leapMonth(lunar.lunarYear);
            days = LunarCalendarUtils.daysInMonth(lunar.lunarYear, lunar.lunarMonth, lunar.isLeap);
            for (int i = 0; i < 42; i++) {
                int column = i % 7;
                int row = i / 7;
                if (day > days) {
                    day = 1;
                    if (lunar.lunarMonth == 12) {
                        lunar.lunarMonth = 1;
                        lunar.lunarYear = lunar.lunarYear + 1;
                    }
                    if (lunar.lunarMonth == leapMonth) {
                        days = LunarCalendarUtils.daysInMonth(lunar.lunarYear, lunar.lunarMonth, lunar.isLeap);
                    } else {
                        lunar.lunarMonth++;
                        days = LunarCalendarUtils.daysInLunarMonth(lunar.lunarYear, lunar.lunarMonth);
                    }
                }
                if (row == 0 && mDaysText[row][column] >= 23 || row >= 4 && mDaysText[row][column] <= 14) {
                    mLunarPaint.setColor(mLunarTextColor);
                } else {
                    mLunarPaint.setColor(Color.parseColor("#A68BFF"));
                }
                String dayString = "";
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarHoliday(lunar.lunarYear, lunar.lunarMonth, day);
                }
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarDayString(day);
                    mLunarPaint.setColor(mLunarTextColor);
                }
                if (selected[0] == row && selected[1] == column) {
                    mLunarPaint.setColor(mSelectDayColor);
                }
                int startX = (int) (mColumnSize * column + (mColumnSize - mLunarPaint.measureText(dayString)) / 2);
                int startY = (int) (mRowSize * row + mRowSize * 0.72 - (mLunarPaint.ascent() + mLunarPaint.descent()) / 2);
                canvas.drawText(dayString, startX, startY, mLunarPaint);
                day++;
            }
        }
    }

    /**
     * 绘制圆点提示
     *
     * @param canvas
     */
    private void drawHintCircle(Canvas canvas) {
        if (mIsShowHint) {
            List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(mSelYear, mSelMonth);
            if (hints.size() > 0) {
                mPaint.setColor(mHintCircleColor);
                int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
                int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
                for (int day = 0; day < monthDays; day++) {
                    int col = (day + weekNumber - 1) % 7;
                    int row = (day + weekNumber - 1) / 7;
                    if (!hints.contains(day + 1)) continue;
                    float circleX = (float) (mColumnSize * col + mColumnSize * 0.5);
                    float circleY = (float) (mRowSize * row + mRowSize * 0.85);
                    canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);
                }
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }

    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;
        int row = y / mRowSize;
        int column = x / mColumnSize;
        column = Math.min(column, 6);
        int clickYear = mSelYear, clickMonth = mSelMonth;
        if (row == 0) {
            if (mDaysText[row][column] >= 23) {
                if (mSelMonth == 0) {
                    clickYear = mSelYear - 1;
                    clickMonth = 11;
                } else {
                    clickYear = mSelYear;
                    clickMonth = mSelMonth - 1;
                }
                if (mDateClickListener != null) {
                    mDateClickListener.onClickLastMonth(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        } else {
            int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            int nextMonthDays = 42 - monthDays - weekNumber + 1;
            if (mDaysText[row][column] <= nextMonthDays && row >= 4) {
                if (mSelMonth == 11) {
                    clickYear = mSelYear + 1;
                    clickMonth = 0;
                } else {
                    clickYear = mSelYear;
                    clickMonth = mSelMonth + 1;
                }
                if (mDateClickListener != null) {
                    mDateClickListener.onClickNextMonth(clickYear, clickMonth, mDaysText[row][column]);
                }
            } else {
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        }
    }

    /**
     * 跳转到某日期
     *
     * @param year
     * @param month
     * @param day
     */
    public void clickThisMonth(int year, int month, int day) {
        if (mDateClickListener != null) {
            mDateClickListener.onClickThisMonth(year, month, day);
        }
        setSelectYearMonth(year, month, day);
        invalidate();
    }

    /**
     * 获取当前选择年
     *
     * @return
     */
    public int getSelectYear() {
        return mSelYear;
    }

    /**
     * 获取当前选择月
     *
     * @return
     */
    public int getSelectMonth() {
        return mSelMonth;
    }

    /**
     * 获取当前选择日
     *
     * @return
     */
    public int getSelectDay() {
        return this.mSelDay;
    }

    public int getRowSize() {
        return mRowSize;
    }

    public int getWeekRow() {
        return mWeekRow;
    }

    /**
     * 添加多个圆点提示
     *
     * @param hints
     */
    public void addTaskHints(List<Integer> hints) {
        if (mIsShowHint) {
            CalendarUtils.getInstance(getContext()).addTaskHints(mSelYear, mSelMonth, hints);
            invalidate();
        }
    }

    /**
     * 删除多个圆点提示
     *
     * @param hints
     */
    public void removeTaskHints(List<Integer> hints) {
        if (mIsShowHint) {
            CalendarUtils.getInstance(getContext()).removeTaskHints(mSelYear, mSelMonth, hints);
            invalidate();
        }
    }

    /**
     * 添加一个圆点提示
     *
     * @param day
     */
    public boolean addTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).addTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
                return true;
            }
        }
        return false;
    }

    /**
     * 删除一个圆点提示
     *
     * @param day
     */
    public boolean removeTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).removeTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
                return true;
            }
        }
        return false;
    }

    /**
     * 设置点击日期监听
     *
     * @param dateClickListener
     */
    public void setOnDateClickListener(OnMonthClickListener dateClickListener) {
        this.mDateClickListener = dateClickListener;
    }

}

