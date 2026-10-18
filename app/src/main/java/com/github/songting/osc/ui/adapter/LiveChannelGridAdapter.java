package com.github.songting.osc.ui.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.songting.osc.R;
import com.github.songting.osc.bean.LiveChannelItem;
import com.github.songting.osc.util.ImgUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class LiveChannelGridAdapter extends BaseQuickAdapter<LiveChannelItem, BaseViewHolder> {
    private boolean radioMode;

    public void setRadioMode(boolean radioMode) {
        this.radioMode = radioMode;
    }

    public LiveChannelGridAdapter() {
        super(R.layout.item_live_channel_grid, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder holder, LiveChannelItem item) {
        ImageView ivLogo = holder.getView(R.id.ivChannelLogo);
        TextView tvName = holder.getView(R.id.tvChannelName);
        tvName.setText(item.getChannelName());
        String logo = item.getChannelLogo();
        int fallback = radioMode ? R.drawable.ic_home_radio : R.drawable.ic_home_tv;
        Glide.with(ivLogo)
                .load(TextUtils.isEmpty(logo) ? null : ImgUtil.getImageModel(logo))
                .placeholder(fallback)
                .error(fallback)
                .fallback(fallback)
                .fitCenter()
                .into(ivLogo);
    }
}
