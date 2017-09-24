package com.muzi.lovingd.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.muzi.lovingd.R;
import com.muzi.lovingd.dao.DaoManager;
import com.muzi.lovingd.dao.EntityManager;
import com.muzi.lovingd.dao.SaveCalendarItemDao;
import com.muzi.lovingd.item.SaveCalendarItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * AddCalendarActivity
 *
 * @author: 17040880
 * @time: 2017/9/18 16:38
 */
public class AddCalendarActivity extends AppCompatActivity {
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.btn_option)
    TextView btnOption;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ed_content)
    EditText edContent;

    private TimePickerView timePickerView;
    private String minTime, yearTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calendar);
        ButterKnife.bind(this);
        txtTitle.setText("新建日程");
        btnOption.setVisibility(View.VISIBLE);
        btnOption.setText("完成");
        initCustomTimePicker();
    }

    @OnClick({R.id.btn_back, R.id.btn_option, R.id.tv_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_option:

                saveData();

                break;
            case R.id.tv_time:
                if (timePickerView != null)
                    timePickerView.show();

                break;
        }
    }

    private void saveData() {
        if (TextUtils.isEmpty(tvTime.getText().toString().trim())) {
            Toast.makeText(AddCalendarActivity.this, "请选择日期", 1).show();
            return;
        }
        if (TextUtils.isEmpty(edContent.getText().toString().trim())||edContent.getText().toString().trim().equals("")) {
            Toast.makeText(AddCalendarActivity.this, "请输入日程信息", 1).show();
            return;
        }
        SaveCalendarItemDao saveDao = EntityManager.getInstance().getSaveCalendarItemDao();
        SaveCalendarItem item = new SaveCalendarItem(null, edContent.getText().toString(), yearTime, minTime);
        saveDao.insert(item);
        setResult(RESULT_OK);
        onBackPressed();
    }

    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        //时间选择器 ，自定义布局
        timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                tvTime.setText(getYearTime(date));
                yearTime = getYearTime(date);
                minTime = getMinTime(date);
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
               /*.gravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePickerView.returnData();
                                timePickerView.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePickerView.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, true})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .build();

    }

    private String getYearTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private String getMinTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }
}
