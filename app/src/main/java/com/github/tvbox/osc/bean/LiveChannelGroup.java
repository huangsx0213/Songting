package com.github.tvbox.osc.bean;

import java.util.ArrayList;

public class LiveChannelGroup {
    /**
     * groupIndex : 分组索引号
     * groupName : 分组名称
     * password : 分组密码
     */
    private int groupIndex;
    private String groupName;
    private String groupPassword;
    private ArrayList<LiveChannelItem> liveChannelItems;
    private boolean radioGroup;

    private static final String[] RADIO_GROUP_KEYWORDS = {"广播", "电台", "收音机", "Radio", "radio", "FM", "fm"};

    public static boolean isRadioGroupName(String name) {
        if (name == null || name.length() == 0) return false;
        for (String keyword : RADIO_GROUP_KEYWORDS) {
            if (name.contains(keyword)) return true;
        }
        return false;
    }

    public boolean isRadioGroup() {
        return radioGroup;
    }

    public void setRadioGroup(boolean radioGroup) {
        this.radioGroup = radioGroup;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<LiveChannelItem> getLiveChannels() {
        return liveChannelItems;
    }

    public void setLiveChannels(ArrayList<LiveChannelItem> liveChannelItems) {
        this.liveChannelItems = liveChannelItems;
    }

    public String getGroupPassword() {
        return groupPassword;
    }

    public void setGroupPassword(String groupPassword) {
        this.groupPassword = groupPassword;
    }
}
