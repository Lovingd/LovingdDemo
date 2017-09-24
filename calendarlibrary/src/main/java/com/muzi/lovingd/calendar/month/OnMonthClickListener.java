package com.muzi.lovingd.calendar.month;

/**
 * @author: Lovingd
 * @time: 2017/9/10 11:00
 */
public interface OnMonthClickListener {
    void onClickThisMonth(int year, int month, int day);
    void onClickLastMonth(int year, int month, int day);
    void onClickNextMonth(int year, int month, int day);
}
