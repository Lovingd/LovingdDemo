package com.muzi.lovingd.item;

/**
 * ScheduleBean
 *
 * @author: 17040880
 * @time: 2017/8/9 19:34
 */
public class CalendarItem {
    /**
     * agendaName	        属性		String			日程名字
     * agendaStartTime	属性		String			日程开始时间	格式为'yyyy-mm-dd HH24:mi:ss'
     * agendaEndTime	    属性		String			日程结束时间
     */

    private String agendaName;
    private String agendaStartTime;
    private String agendaEndTime;

    public String getAgendaName() {
        return agendaName;
    }

    public void setAgendaName(String agendaName) {
        this.agendaName = agendaName;
    }

    public String getAgendaStartTime() {
        return agendaStartTime;
    }

    public void setAgendaStartTime(String agendaStartTime) {
        this.agendaStartTime = agendaStartTime;
    }

    public String getAgendaEndTime() {
        return agendaEndTime;
    }

    public void setAgendaEndTime(String agendaEndTime) {
        this.agendaEndTime = agendaEndTime;
    }
}
