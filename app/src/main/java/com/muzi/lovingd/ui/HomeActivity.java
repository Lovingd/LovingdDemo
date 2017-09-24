package com.muzi.lovingd.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.muzi.lovingd.item.HomeItem;
import com.muzi.lovingd.R;
import com.muzi.lovingd.adapter.HomeAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private HomeAdapter adapter;
    private ArrayList<HomeItem> homeItems=new ArrayList<>();

    private static final Class<?>[] ACTIVITY = {CalendarActivity.class,GaodeBottomSheetActivity.class};
    private static final String[] TITLE = {"日历","bottomsheet"};
    private static final int[] IMG = {R.mipmap.icon_calendar,R.mipmap.bottomsheet};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        GridLayoutManager linearLayoutManager=new GridLayoutManager(this,2);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        initData();
        adapter=new HomeAdapter(R.layout.adapter_home_item,homeItems);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(HomeActivity.this,ACTIVITY[position]));
            }
        });
    }

    private void initData() {
        homeItems=new ArrayList<>();
        for (int i = 0; i < ACTIVITY.length; i++) {
            HomeItem item=new HomeItem();
            item.setActivity(ACTIVITY[i]);
            item.setImageResource(IMG[i]);
            item.setTitle(TITLE[i]);
            homeItems.add(item);
        }
    }
}
