package com.muzi.lovingd.ui;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.muzi.lovingd.R;

import java.util.List;

/**
 * GaodeAdapter
 *
 * @author: 17040880
 * @time: 2017/9/19 17:16
 */
class GaodeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public GaodeAdapter(@Nullable List<String> data) {
        super(R.layout.adapter_schedule_view,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_schedule_adapter_name,item);
    }
}
