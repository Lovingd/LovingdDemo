package com.muzi.lovingd.calendar.week;

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

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

/**
 * @author: Lovingd
 * @time: 2017/9/10 11:00
 */
public class WeekView extends View {

    private static final int NUM_COLUMNS = 7;
    private Paint mPaint;
    private Paint mLunarPaint;

    private int mNormalDayColor;
    private int mLastOrNextDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mSelectBGTodayColor;
    private int mCurrentDayColor;
    private int mLastOrNextWeekTextColor;
    private int mLunarTextSize;

    private int mLunarTextColor;

    private int mHintCircleColor;
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;
    private int mColumnSize, mRowSize, mSelectCircleSize;
    private int mDaySize;
    private int mCircleRadius = 5;
    private boolean mIsShowHint;
    private boolean mIsShowLunar;

    private DateTime mStartDate;
    private DisplayMetrics mDisplayMetrics;
    private OnWeekClickListener mOnWeekClickListener;
    private GestureDetector mGestureDetector;

    public WeekView(Context context, DateTime dateTime) {
        this(context, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, DateTime dateTime) {
        this(context, array, null, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, DateTime dateTime) {
        this(context, array, attrs, 0, dateTime);
    }

    public WeekView(Context context, TypedArray array, AttributeSet attrs, int defStyleAttr, DateTime dateTime) {
        super(context, attrs, defStyleAttr);
        initAttrs(array, dateTime);
        initPaint();
        initWeek();
        initGestureDetector();
    }

    public DateTime getmStartDate() {
        return mStartDate;
    }



    private void initAttrs(TypedArray array, DateTime dateTime) {
        if (array != null) {
            mSelectDayColor = array.getColor(R.styleable.WeekCalendarView_week_selected_text_color, Color.parseColor("#FFFFFF"));
            mSelectBGColor = array.getColor(R.styleable.WeekCalendarView_week_selected_circle_color, Color.parseColor("#336EFD"));
            mSelectBGTodayColor = array.getColor(R.styleable.WeekCalendarView_week_selected_circle_today_color, Color.parseColor("#336EFD"));
            mNormalDayColor = array.getColor(R.styleable.WeekCalendarView_week_normal_text_color, Color.parseColor("#575471"));
            mCurrentDayColor = array.getColor(R.styleable.WeekCalendarView_week_today_text_color, Color.parseColor("#FF8594"));
            mHintCircleColor = array.getColor(R.styleable.WeekCalendarView_week_hint_circle_color, Color.parseColor("#FE8595"));
            mLunarTextColor = array.getColor(R.styleable.WeekCalendarView_week_lunar_text_color, Color.parseColor("#ACA9BC"));
            mLunarTextSize = array.getInteger(R.styleable.WeekCalendarView_week_day_lunar_text_size, 8);
            mIsShowLunar = array.getBoolean(R.styleable.WeekCalendarView_week_show_lunar, true);
            mDaySize = array.getInteger(R.styleable.WeekCalendarView_week_day_text_size, 13);
            mIsShowHint = array.getBoolean(R.styleable.WeekCalendarView_week_show_task_hint, true);
        } else {
            mSelectDayColor = Color.parseColor("#FFFFFF");
            mSelectBGColor = Color.parseColor("#336EFD");
            mSelectBGTodayColor = Color.parseColor("#336EFD");
            mNormalDayColor = Color.parseColor("#575471");
            mCurrentDayColor = Color.parseColor("#FF8594");
            mHintCircleColor = Color.parseColor("#FE8595");
            mLunarTextColor = Color.parseColor("#ACA9BC");
            mDaySize = 13;
            mDaySize = 8;
            mLunarTextSize = 8;
            mIsShowHint = true;
            mIsShowLunar = true;
        }
        mLastOrNextWeekTextColor = Color.parseColor("#ACA9BC");
        mLastOrNextDayColor = Color.parseColor("#BFC4CF");
        mCurrentDayColor = mNormalDayColor;
        mStartDate = dateTime;
        int row = CalendarUtils.getWeekRow(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());

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

    private void initWeek() {
        Calendar calendar = Calendar.getInstance();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        DateTime endDate = mStartDate.plusDays(7);
        if (mStartDate.getMillis() <= System.currentTimeMillis() && endDate.getMillis() > System.currentTimeMillis()) {
            if (mStartDate.getMonthOfYear() != endDate.getMonthOfYear()) {
                if (mCurrDay < mStartDate.getDayOfMonth()) {
                    setSelectYearMonth(mStartDate.getYear(), endDate.getMonthOfYear() - 1, mCurrDay);
                } else {
                    setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mCurrDay);
                }
            } else {
                setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mCurrDay);
            }
        } else {
            setSelectYearMonth(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        }

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

    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
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
        int selected = drawThisWeek(canvas);
//        drawNextWeek(canvas);
        drawLunarText(canvas, selected);

        drawHintCircle(canvas);
    }


    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight();
        mSelectCircleSize = (int) (mColumnSize / 3.5);
        while (mSelectCircleSize > mRowSize / 2) {
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }
    }


    private int drawThisWeek(Canvas canvas) {
        int selected = 0;
        for (int i = 0; i < 7; i++) {
            DateTime date = mStartDate.plusDays(i);
            int day = date.getDayOfMonth();
            String dayString = String.valueOf(day);
            if (day == mSelDay) {
                int startRecX = mColumnSize * i;
                int endRecX = startRecX + mColumnSize;
                if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth && day == mCurrDay) {
                    mPaint.setColor(mSelectBGTodayColor);
                    dayString = "今";
                } else {
                    mPaint.setColor(mSelectBGColor);
                }
                canvas.drawCircle((startRecX + endRecX) / 2, mRowSize / 2, mSelectCircleSize, mPaint);
            }
            if (day == mSelDay) {
                selected = i;
                mPaint.setColor(mSelectDayColor);
            } else if (date.getYear() == mCurrYear && date.getMonthOfYear() - 1 == mCurrMonth
                    && day == mCurrDay && day != mSelDay && mCurrYear == mSelYear) {
                mPaint.setColor(mCurrentDayColor);
//                if(date.getMonthOfYear() - 1 != mSelMonth){
//                    mPaint.setColor(mLastOrNextDayColor);
//                }
                dayString = "今";
            }
//            else if (date.getMonthOfYear() - 1 != mSelMonth) {
//                mPaint.setColor(mLastOrNextDayColor);
//            }
            else {
                if(date.getYear()==CalendarUtils.startYear-1||date.getYear()==CalendarUtils.endYear+1){//这个是为了整年的情况
                    mPaint.setColor(mLastOrNextDayColor);
                }else {
                    mPaint.setColor(mNormalDayColor);
                }
            }
            int startX = (int) (mColumnSize * i + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
        }
        return selected;
    }

    /**
     * 绘制农历
     *
     * @param canvas
     * @param selected
     */
    private void drawLunarText(Canvas canvas, int selected) {
        if (mIsShowLunar) {
            LunarCalendarUtils.Lunar lunar = LunarCalendarUtils.solarToLunar(new LunarCalendarUtils.Solar(mStartDate.getYear(), mStartDate.getMonthOfYear(), mStartDate.getDayOfMonth()));
            int leapMonth = LunarCalendarUtils.leapMonth(lunar.lunarYear);
            int days = LunarCalendarUtils.daysInMonth(lunar.lunarYear, lunar.lunarMonth, lunar.isLeap);
            int day = lunar.lunarDay;
            for (int i = 0; i < 7; i++) {
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
                mLunarPaint.setColor(mLunarTextColor);
                String dayString = "";
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarHoliday(lunar.lunarYear, lunar.lunarMonth, day);
                }
                if ("".equals(dayString)) {
                    dayString = LunarCalendarUtils.getLunarDayString(day);
                    mLunarPaint.setColor(mLunarTextColor);
                }
                if (i == selected) {
                    mLunarPaint.setColor(mSelectDayColor);
                }
                int startX = (int) (mColumnSize * i + (mColumnSize - mLunarPaint.measureText(dayString)) / 2);
                int startY = (int) (mRowSize * 0.72 - (mLunarPaint.ascent() + mLunarPaint.descent()) / 2);
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
            mPaint.setColor(mHintCircleColor);
            int startMonth = mStartDate.getMonthOfYear();
            int endMonth = mStartDate.plusDays(7).getMonthOfYear();
            int startDay = mStartDate.getDayOfMonth();
            if (startMonth == endMonth) {
                List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1);
                for (int i = 0; i < 7; i++) {
                    drawHintCircle(hints, startDay + i, i, canvas);
                }
            } else {
                for (int i = 0; i < 7; i++) {
                    List<Integer> hints = CalendarUtils.getInstance(getContext()).getTaskHints(mStartDate.getYear(), mStartDate.getMonthOfYear() - 1);
                    List<Integer> nextHints = CalendarUtils.getInstance(getContext()).getTaskHints(mStartDate.getYear(), mStartDate.getMonthOfYear());
                    DateTime date = mStartDate.plusDays(i);
                    int month = date.getMonthOfYear();
                    if (month == startMonth) {
                        drawHintCircle(hints, date.getDayOfMonth(), i, canvas);
                    } else {
                        drawHintCircle(nextHints, date.getDayOfMonth(), i, canvas);
                    }
                }
            }
        }
    }

    private void drawHintCircle(List<Integer> hints, int day, int col, Canvas canvas) {
        if (!hints.contains(day)) return;
        float circleX = (float) (mColumnSize * col + mColumnSize * 0.5);
        float circleY = (float) (mRowSize * 0.85);
        canvas.drawCircle(circleX, circleY, mCircleRadius, mPaint);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;
        int column = x / mColumnSize;
        column = Math.min(column, 6);
        DateTime date = mStartDate.plusDays(column);
        clickThisWeek(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
    }

    public void clickThisWeek(int year, int month, int day) {
        boolean isAchieve = false;

        if (year < CalendarUtils.startYear || year > CalendarUtils.endYear) {
            //如果超出了年限就不能做点击事件
            isAchieve = false;
        } else if (CalendarUtils.startMonth > month || CalendarUtils.endMonth < month) {
            //如果没有超出年限但是超出了月份 点击事件也不响应
            isAchieve = false;
        } else if (CalendarUtils.startDay > day || CalendarUtils.endDay < day) {
            isAchieve = false;
        } else {
            isAchieve = true;
        }
        if (isAchieve) {
            if (mOnWeekClickListener != null) {
                mOnWeekClickListener.onClickDate(year, month, day);
            }
            setSelectYearMonth(year, month, day);
            invalidate();
        }
    }

    public void setOnWeekClickListener(OnWeekClickListener onWeekClickListener) {
        mOnWeekClickListener = onWeekClickListener;
    }

    public DateTime getStartDate() {
        return mStartDate;
    }

    public DateTime getEndDate() {
        return mStartDate.plusDays(6);
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

    public int getDefaultDay() {
        return mStartDate.getDayOfMonth();
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
    public void addTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).addTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
            }
        }
    }

    /**
     * 删除一个圆点提示
     *
     * @param day
     */
    public void removeTaskHint(Integer day) {
        if (mIsShowHint) {
            if (CalendarUtils.getInstance(getContext()).removeTaskHint(mSelYear, mSelMonth, day)) {
                invalidate();
            }
        }
    }

}
