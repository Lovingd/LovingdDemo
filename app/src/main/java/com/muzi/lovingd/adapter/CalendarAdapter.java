package com.muzi.lovingd.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.muzi.lovingd.R;
import com.muzi.lovingd.item.CalendarItem;
import com.muzi.lovingd.item.SaveCalendarItem;
import com.muzi.lovingd.utils.TimeUtils;

import java.util.List;

/**
 * MyScheduleAdapter
 *
 * @author: 17040880
 * @time: 2017/8/9 19:31
 */
public class CalendarAdapter extends BaseQuickAdapter<SaveCalendarItem, BaseViewHolder> {
    private int year;
    private int month;
    private int day;

    public CalendarAdapter(@Nullable List<SaveCalendarItem> data) {
        super(R.layout.adapter_schedule_view, data);
    }

    public void setYearMonthDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    protected void convert(BaseViewHolder helper, SaveCalendarItem item) {
        boolean isThisDay = false;
        int mYear = Integer.parseInt(TimeUtils.getYyyy(item.getYearTime()));
        int mMonth = Integer.parseInt(TimeUtils.getMM(item.getYearTime()));
        int mDay = Integer.parseInt(TimeUtils.getDD(item.getYearTime()));
        String strDay = String.valueOf(mDay);
        String strMonth = String.valueOf(mMonth);
        if (mDay < 10) {
            strDay = "0" + strDay;
        }
        if (mMonth < 10) {
            strMonth = "0" + strMonth;
        }
        if (mYear == year && mMonth == (month + 1) && mDay == day) {

            helper.setText(R.id.tv_schedule_adapter_time, TimeUtils.getHhMmTime(item.getMinTime()))
                    .setText(R.id.tv_schedule_adapter_mmdd, strMonth + "-" + strDay);
            ViewGroup.LayoutParams lp = helper.getView(R.id.tv_schedule_adapter_mmdd).getLayoutParams();
            lp.height = 0;
            helper.getView(R.id.tv_schedule_adapter_mmdd).setLayoutParams(lp);

        } else {
            helper.setText(R.id.tv_schedule_adapter_time, TimeUtils.getHhMmTime(item.getMinTime()))
                    .setText(R.id.tv_schedule_adapter_mmdd, strMonth + "-" + strDay);
            ViewGroup.LayoutParams lp = helper.getView(R.id.tv_schedule_adapter_mmdd).getLayoutParams();
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            helper.getView(R.id.tv_schedule_adapter_mmdd).setLayoutParams(lp);
        }

        helper.setText(R.id.tv_schedule_adapter_name, item.getName());
    }

}
