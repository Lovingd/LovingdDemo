package com.muzi.lovingd.calendar;

/**
 * @author: Lovingd
 * @time: 2017/9/10 11:00
 */
public interface OnCalendarClickListener {
    void onClickDate(int year, int month, int day);
    void onPageChange(int year, int month, int day);
}
