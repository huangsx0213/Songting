package com.github.songting.osc.ui.tv.widget;



import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ListView;

import com.github.songting.osc.ui.activity.LivePlayActivity;

public class ChannelListView extends ListView {
    public int pos= LivePlayActivity.currentChannelGroupIndex;
    private int y;

    public ChannelListView(Context context) {
        super(context);
    }
    public ChannelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ChannelListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            setSelectionFromTop(pos, y);
        }
    }

}
