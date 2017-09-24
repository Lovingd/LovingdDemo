package com.muzi.lovingd.item;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * SaveCalendarItem
 *
 * @author: 17040880
 * @time: 2017/9/18 19:39
 */
@Entity
public class SaveCalendarItem {
    @Id
    private Long id;
    private String name;
    private String yearTime;
    private String minTime;


    @Generated(hash = 1256387226)
    public SaveCalendarItem(Long id, String name, String yearTime, String minTime) {
        this.id = id;
        this.name = name;
        this.yearTime = yearTime;
        this.minTime = minTime;
    }

    @Generated(hash = 715337202)
    public SaveCalendarItem() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYearTime() {
        return yearTime;
    }

    public void setYearTime(String yearTime) {
        this.yearTime = yearTime;
    }

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }
}
