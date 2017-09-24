package com.muzi.lovingd.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.muzi.lovingd.item.HomeItem;
import com.muzi.lovingd.R;

import java.util.List;

/**
 * HomeAdapter
 *
 * @author: 17040880
 * @time: 2017/9/18 14:54
 */
public class HomeAdapter extends BaseQuickAdapter<HomeItem,BaseViewHolder>{
    public HomeAdapter(@LayoutRes int layoutResId, @Nullable List<HomeItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeItem item) {
        helper.setText(R.id.text, item.getTitle());
        helper.setImageResource(R.id.icon, item.getImageResource());
    }
}
