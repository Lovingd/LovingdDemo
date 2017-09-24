package com.muzi.lovingd.utils;

import android.content.Context;
import android.util.TypedValue;

import com.muzi.lovingd.MyApplication;

/**
 * DensityUtil:单位转换工具类
 *
 * @author: 16060822
 * @time: 2016/11/7 11:37
 */

public class DensityUtil {

    private static Context context = MyApplication.getContext();

    private DensityUtil() {
        throw new AssertionError();
    }

    /**
     * dp转px
     *
     * @param dpVal
     * @return
     */
    public static int dpToPx(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }


    /**
     * px转dp
     *
     * @param pxVal
     * @return
     */
    public static float pxToDp(float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */
    public static float pxToSp(float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     * sp转px
     *
     * @param spVal
     * @return
     */
    public static int spToPx(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }
}
