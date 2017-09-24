package com.muzi.lovingd.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.muzi.lovingd.R;
import com.muzi.lovingd.adapter.CalendarAdapter;
import com.muzi.lovingd.calendar.OnCalendarClickListener;
import com.muzi.lovingd.calendar.schedule.ScheduleLayout;
import com.muzi.lovingd.calendar.schedule.ScheduleRecyclerView;
import com.muzi.lovingd.calendar.schedule.ScheduleState;
import com.muzi.lovingd.calendar.schedule.ShowScheduleStateListener;
import com.muzi.lovingd.dao.EntityManager;
import com.muzi.lovingd.item.SaveCalendarItem;
import com.muzi.lovingd.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * CalendarActivity
 *
 * @author: 17040880
 * @time: 2017/9/18 15:06
 */
public class CalendarActivity extends AppCompatActivity implements OnCalendarClickListener, ShowScheduleStateListener {
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.tv_schedule_time)
    TextView tvScheduleTime;
    @BindView(R.id.tv_schedule_all_count)
    TextView tvScheduleAllCount;
    ScheduleRecyclerView rvScheduleList;
    @BindView(R.id.rlScheduleList)
    RelativeLayout rlScheduleList;
    @BindView(R.id.slSchedule)
    ScheduleLayout slSchedule;
    @BindView(R.id.btn_add_event)
    Button btnAddEvent;
    @BindView(R.id.img_schedule_left)
    ImageView imgScheduleLeft;
    @BindView(R.id.img_schedule_right)
    ImageView imgScheduleRight;
    @BindView(R.id.img_schedule_week_or_month)
    ImageView imgScheduleWeekOrMonth;

    private List<SaveCalendarItem> list = new ArrayList<>();
    private List<SaveCalendarItem> allList = new ArrayList<>();
    private CalendarAdapter adapter;
    private int mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay;//当前选择的年月日


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedule);
        ButterKnife.bind(this);
        allList = EntityManager.getInstance().getSaveCalendarItemDao().loadAll();
        initView();


    }

    private void initView() {
        txtTitle.setText("我的日程");
        imgScheduleWeekOrMonth.setTag("1");
        imgScheduleLeft.setVisibility(View.INVISIBLE);
        imgScheduleRight.setVisibility(View.INVISIBLE);
        Calendar calendar = TimeUtils.getConverCalendar();
        setCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        tvScheduleTime.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月");
        initRecycleview();
        adapter.setYearMonthDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void initRecycleview() {
        slSchedule.setOnCalendarClickListener(this);
        slSchedule.setShowScheduleStateListener(this);
        slSchedule.addTaskHints(new ArrayList<Integer>());
        rvScheduleList = slSchedule.getRecyclerView();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvScheduleList.setLayoutManager(manager);
        adapter = new CalendarAdapter(list);
        rvScheduleList.setAdapter(adapter);
        rvScheduleList.setNestedScrollingEnabled(false);
        adapter.setEmptyView(getEmptyView());
    }

    @OnClick({R.id.btn_back, R.id.img_schedule_left, R.id.img_schedule_right, R.id.img_schedule_week_or_month, R.id.btn_add_event})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.img_schedule_week_or_month:
                if (view.getTag().equals("1")) {
                    imgScheduleWeekOrMonth.setTag("2");
                    imgScheduleWeekOrMonth.setImageResource(R.drawable.img_schedule_up);
                    imgScheduleLeft.setVisibility(View.VISIBLE);
                    imgScheduleRight.setVisibility(View.VISIBLE);
                } else {
                    imgScheduleLeft.setVisibility(View.INVISIBLE);
                    imgScheduleRight.setVisibility(View.INVISIBLE);
                    imgScheduleWeekOrMonth.setTag("1");
                    imgScheduleWeekOrMonth.setImageResource(R.drawable.img_schedule_down);
                }
                slSchedule.setVisibleView();
                break;
            case R.id.img_schedule_left:
                imgScheduleLeft.setEnabled(false);
                imgScheduleRight.setEnabled(false);
                if (mCurrentSelectMonth == 0) {
                    slSchedule.onClickLastMonth(mCurrentSelectYear - 1, 11, 1);
                } else {
                    slSchedule.onClickLastMonth(mCurrentSelectYear, mCurrentSelectMonth - 1, 1);
                }
                break;
            case R.id.img_schedule_right:
                imgScheduleRight.setEnabled(false);
                imgScheduleLeft.setEnabled(false);
                if (mCurrentSelectMonth == 11) {
                    slSchedule.onClickNextMonth(mCurrentSelectYear + 1, 0, 1);
                } else {
                    slSchedule.onClickNextMonth(mCurrentSelectYear, mCurrentSelectMonth + 1, 1);
                }
                break;
            case R.id.btn_add_event:
                startActivityForResult(new Intent(CalendarActivity.this, AddCalendarActivity.class), 1000);
                break;
        }
    }

    private void setCurrentSelectDate(int year, int month, int day) {
        slSchedule.initData(year, month, day);
    }

    public View getEmptyView() {
        View view = getLayoutInflater().inflate(R.layout.layout_schedule_no_content, rvScheduleList, false);
        TextView tv_no_content = (TextView) view.findViewById(R.id.tv_no_content);
        tv_no_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    @Override
    public void onClickDate(int year, int month, int day) {
        if (adapter != null) {
            adapter.setYearMonthDay(year, month, day);
        }

        notifyDataSetChanged(year, month, day);
    }

    private void notifyDataSetChanged(int year, int month, int day) {
        String time = year + "-" + ((month + 1 < 10) ? "0" + (month + 1) : month + 1) + "-" + ((day < 10) ? "0" + day : day);
        list = new ArrayList<>();
        List<Integer> lastList = new ArrayList<>();
        List<Integer> currList = new ArrayList<>();
        List<Integer> nextList = new ArrayList<>();
        for (int i = 0; i < allList.size(); i++) {
            if (time.equals(allList.get(i).getYearTime())) {
                list.add(allList.get(i));
            }
            if (isCurrentMonth(allList.get(i).getYearTime()) == 0) {
                currList.add(TimeUtils.thisMonthOfDay(allList.get(i).getYearTime()));
            } else if (isCurrentMonth(allList.get(i).getYearTime()) == -1) {
                lastList.add(TimeUtils.thisMonthOfDay(allList.get(i).getYearTime()));
            } else if (isCurrentMonth(allList.get(i).getYearTime()) == 1) {
                nextList.add(TimeUtils.thisMonthOfDay(allList.get(i).getYearTime()));
            }
        }
        adapter.setNewData(list);
        slSchedule.addMoreTaskHints(lastList, currList, nextList);
        if (!imgScheduleLeft.isEnabled()) {
            imgScheduleLeft.setEnabled(true);
        }
        if (!imgScheduleRight.isEnabled()) {
            imgScheduleRight.setEnabled(true);
        }
        // TODO: 2017/9/18  搜索数据库查看有没有改天的日程
        showTitileTime(year, month, day);

        tvScheduleAllCount.setText((list.size()==0)?"我的日程(0)":"我的日程("+list.size()+")");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            allList = EntityManager.getInstance().getSaveCalendarItemDao().loadAll();
            notifyDataSetChanged(mCurrentSelectYear,
                    mCurrentSelectMonth,
                    mCurrentSelectDay);
        }
    }

    /**
     * 比较和出分这个日期是上个月还是下个月或者本月
     *
     * @param str 这个是要传入的数据
     * @return -1表示上个月的数据 0表示本月数据 1表示下个月数据
     */
    private int isCurrentMonth(String str) {
        int type = 0;
        String month = (slSchedule.getCurrentSelectMonth() + 1 < 10) ? "0" + (slSchedule.getCurrentSelectMonth() + 1) : "" + (slSchedule.getCurrentSelectMonth() + 1);
        String start = slSchedule.getCurrentSelectYear() + month + "00";
        String end = slSchedule.getCurrentSelectYear() + month + "32";
        long startDay = Long.parseLong(start.replace("-", ""));
        long strDay = Long.parseLong(str.replace("-", ""));
        long endDay = Long.parseLong(end.replace("-", ""));
        if (startDay > strDay) {
            type = -1;
        } else if (strDay > endDay) {
            type = 1;
        }
        return type;
    }

    /**
     * 这里面可以显示时间
     *
     * @param year
     * @param month
     * @param day
     */
    private void showTitileTime(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
        tvScheduleTime.setText(year + "年" + (month + 1) + "月");
    }

    @Override
    public void onPageChange(int year, int month, int day) {

    }

    @Override
    public void showState(ScheduleState mState) {
        if (mState == ScheduleState.CLOSE) {
            imgScheduleLeft.setVisibility(View.INVISIBLE);
            imgScheduleRight.setVisibility(View.INVISIBLE);
            imgScheduleWeekOrMonth.setTag("1");
            imgScheduleWeekOrMonth.setImageResource(R.drawable.img_schedule_down);
        } else {
            imgScheduleWeekOrMonth.setTag("2");
            imgScheduleWeekOrMonth.setImageResource(R.drawable.img_schedule_up);
            imgScheduleLeft.setVisibility(View.VISIBLE);
            imgScheduleRight.setVisibility(View.VISIBLE);
        }

    }
}
