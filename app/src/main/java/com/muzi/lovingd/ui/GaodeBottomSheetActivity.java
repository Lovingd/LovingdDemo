package com.muzi.lovingd.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.muzi.lovingd.R;
import com.muzi.lovingd.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * GaodeBottomSheetActivity
 *
 * @author: 17040880
 * @time: 2017/9/19 14:29
 */
public class GaodeBottomSheetActivity extends AppCompatActivity {

    @BindView(R.id.tv_gold_search_content)
    TextView tvGoldSearchContent;
    @BindView(R.id.ll_gold_search_bg)
    LinearLayout llGoldSearchBg;
    @BindView(R.id.tv_gold_down_more)
    TextView tvGoldDownMore;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.design_bottom_sheet_bar)
    RelativeLayout designBottomSheetBar;
    @BindView(R.id.btn_back)
    TextView btnBack;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.btn_option)
    TextView btnOption;
    @BindView(R.id.rv_gold_mine_list)
    RecyclerView rvGoldMineList;
    @BindView(R.id.fra_bottom_sheet)
    RelativeLayout fraBottomSheet;
    @BindView(R.id.bottom_sheet_coordinatorLayout)
    CoordinatorLayout bottomSheetCoordinatorLayout;


    private BottomSheetBehavior behavior;
    private boolean isHasNavigationBar = false;
    private boolean isSetBottomSheetHeight;
    private int fraBottomSheetHeight;
    private boolean isHid = false;
    private int listBehaviorHeight = 0;
    private GaodeAdapter adapter;


    /**
     * 判断NavigationBar（就是虚拟返回键 home键）是存在
     *
     * @param activity
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean checkDeviceHasNavigationBar(Context activity) {

        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

    /**
     * 判断NavigationBar（就是虚拟返回键 home键）是否显示
     *
     * @return
     */
    public boolean isNavigationBarShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaode_bottomsheet);
        ButterKnife.bind(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvGoldMineList.setLayoutManager(manager);
        List<String> list=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("我是第"+i);
        }
        adapter = new GaodeAdapter(list);
        rvGoldMineList.setAdapter(adapter);
        behavior = BottomSheetBehavior.from(fraBottomSheet);
        behavior.setHideable(true);
        behavior.setSkipCollapsed(false);
        listBehaviorHeight = DensityUtil.dpToPx(345);
        setListener();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //修改SetBottomSheet的高度 留出顶部工具栏的位置
        if (!isSetBottomSheetHeight) {
            CoordinatorLayout.LayoutParams linearParams = (CoordinatorLayout.LayoutParams) fraBottomSheet.getLayoutParams();
            linearParams.height = bottomSheetCoordinatorLayout.getHeight() - DensityUtil.dpToPx(90);
            fraBottomSheetHeight = linearParams.height;
            fraBottomSheet.setLayoutParams(linearParams);
            isSetBottomSheetHeight = true;
        }


    }

    /**
     * 设置监听
     */
    private void setListener() {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        try {
            getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    //这个监听的方法时为了让有NavigationBar处理布局的变化的
                    if (isHasNavigationBar) {
                        boolean is = isNavigationBarShow();
                        if (isHid != is) {
                            CoordinatorLayout.LayoutParams linearParams = (CoordinatorLayout.LayoutParams) fraBottomSheet.getLayoutParams();
                            linearParams.height = bottomSheetCoordinatorLayout.getHeight() - DensityUtil.dpToPx(90);
                            fraBottomSheetHeight = linearParams.height;
                            fraBottomSheet.setLayoutParams(linearParams);
                            isSetBottomSheetHeight = true;
                        }
                        isHid = is;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //底栏状态改变的监听
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                Log.i("TAG", "TTTT-----" + newState);

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tvGoldDownMore.setVisibility(View.GONE);

                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tvGoldDownMore.setVisibility(View.VISIBLE);

                }


            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.e("TAG", "TTT-----------" + bottomSheet.getTop() + "--------" + llGoldSearchBg.getTop());
                Log.i("TAG", "TTT-----------" + (bottomSheet.getTop() - DensityUtil.dpToPx(90)) + "---------" + slideOffset);
                if (bottomSheet.getTop() < DensityUtil.dpToPx(135)) {

                    //设置底栏完全展开时，出现的顶部工具栏的动画
                    designBottomSheetBar.setVisibility(View.VISIBLE);
                    designBottomSheetBar.setAlpha(slideOffset);
                    designBottomSheetBar.setTranslationY(-bottomSheet.getTop() + DensityUtil.dpToPx(135));
                    llGoldSearchBg.setTranslationY(bottomSheet.getTop() - DensityUtil.dpToPx(135));
                    if (1 == slideOffset) {
                        llGoldSearchBg.setVisibility(View.INVISIBLE);
                    } else {
                        llGoldSearchBg.setVisibility(View.VISIBLE);

                    }
                } else {
                    designBottomSheetBar.setVisibility(View.INVISIBLE);
                    llGoldSearchBg.setVisibility(View.VISIBLE);
                    llGoldSearchBg.setTranslationY(DensityUtil.dpToPx(0));


                }
            }
        });


    }

    @OnClick(R.id.tv_gold_down_more)
    public void onClick() {
        if (isHasNavigationBar) {
            CoordinatorLayout.LayoutParams linearParams = (CoordinatorLayout.LayoutParams) fraBottomSheet.getLayoutParams();
            linearParams.height = bottomSheetCoordinatorLayout.getHeight() - DensityUtil.dpToPx(90);
            fraBottomSheetHeight = linearParams.height;
            fraBottomSheet.setLayoutParams(linearParams);
            isSetBottomSheetHeight = true;
        }
        behavior.setPeekHeight((fraBottomSheetHeight - DensityUtil.dpToPx(45) > listBehaviorHeight) ? listBehaviorHeight : (int) (fraBottomSheetHeight / 2));
        fraBottomSheet.setVisibility(View.VISIBLE);
        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}

