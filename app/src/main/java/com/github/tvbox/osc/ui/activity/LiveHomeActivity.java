package com.github.tvbox.osc.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.bean.LiveChannelGroup;
import com.github.tvbox.osc.bean.LiveChannelItem;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.ui.adapter.LiveChannelGridAdapter;
import com.github.tvbox.osc.ui.adapter.LiveChannelGroupAdapter;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.HistoryHelper;
import com.github.tvbox.osc.util.live.TxtSubscribe;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;
import com.orhanobut.hawk.Hawk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LiveHomeActivity extends BaseActivity {

    public static final String EXTRA_MODE = "live_mode";
    public static final String EXTRA_CHANNEL_NAME = "live_channel_name";
    public static final String MODE_TV = "tv";
    public static final String MODE_RADIO = "radio";

    private TvRecyclerView mHomeGroupView;
    private RecyclerView mHomeChannelView;
    private TextView tvHomeDate;
    private TextView tvHomeWarning;
    private TextView tvHomeMode;
    private TextView btnLiveRadio;
    private TextView btnLiveConfig;

    private LiveChannelGroupAdapter groupAdapter;
    private LiveChannelGridAdapter channelAdapter;

    private final List<LiveChannelGroup> tvGroups = new ArrayList<>();
    private boolean radioMode = false;

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd  E  HH:mm", Locale.CHINA);
    private final Runnable mDateRun = new Runnable() {
        @Override
        public void run() {
            if (isFinishing() || isDestroyed()) return;
            tvHomeDate.setText(mDateFormat.format(new Date()));
            mHandler.postDelayed(this, 30000);
        }
    };

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_live_home;
    }

    @Override
    protected void init() {
        mHomeGroupView = findViewById(R.id.mHomeGroupView);
        mHomeChannelView = findViewById(R.id.mHomeChannelView);
        tvHomeDate = findViewById(R.id.tvHomeDate);
        tvHomeWarning = findViewById(R.id.tvHomeWarning);
        tvHomeMode = findViewById(R.id.tvHomeMode);
        btnLiveRadio = findViewById(R.id.btnLiveRadio);
        btnLiveConfig = findViewById(R.id.btnLiveConfig);
        setLoadSir(findViewById(R.id.mHomeChannelView));
        mHandler.post(mDateRun);
        setupLists();
        btnLiveRadio.setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            radioMode = !radioMode;
            bindLiveConfig();
        });
        btnLiveConfig.setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            showConfigDialog();
        });
        btnLiveRadio.requestFocus();
        // 保证直播配置中的本地代理可用。
        ControlManager.get().getAddress(true);
        loadLiveConfig();
    }

    private void setupLists() {
        mHomeGroupView.setLayoutManager(new V7LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        groupAdapter = new LiveChannelGroupAdapter(R.layout.item_live_home_group);
        groupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, android.view.View view, int position) {
                if (position < 0 || position >= tvGroups.size()) return;
                selectGroup(position);
            }
        });
        mHomeGroupView.setAdapter(groupAdapter);

        mHomeChannelView.setLayoutManager(new GridLayoutManager(this, 6));
        channelAdapter = new LiveChannelGridAdapter();
        channelAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, android.view.View view, int position) {
                LiveChannelItem item = channelAdapter.getItem(position);
                if (item == null) return;
                open(item.getChannelName(), radioMode ? MODE_RADIO : MODE_TV);
            }
        });
        mHomeChannelView.setAdapter(channelAdapter);
    }

    private void loadLiveConfig() {
        showLoading();
        ApiConfig.get().loadLiveConfig(true, new ApiConfig.LoadConfigCallback() {
            @Override
            public void success() {
                mHandler.post(LiveHomeActivity.this::loadChannelList);
            }

            @Override
            public void error(String msg) {
                mHandler.postDelayed(() -> {
                    showSuccess();
                    Toast.makeText(LiveHomeActivity.this, "加载直播源失败: " + msg, Toast.LENGTH_SHORT).show();
                }, 600);
            }

            @Override
            public void notice(String msg) {
                mHandler.post(() -> Toast.makeText(LiveHomeActivity.this, msg, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadChannelList() {
        if (isFinishing() || isDestroyed()) return;
        List<LiveChannelGroup> groups = ApiConfig.get().getChannelGroupList();
        if (groups.size() != 1 || !groups.get(0).getGroupName().startsWith("http://127.0.0.1")) {
            bindLiveConfig();
            return;
        }
        String url = groups.get(0).getGroupName();
        try {
            String ext = Uri.parse(url).getQueryParameter("ext");
            String decoded = new String(Base64.decode(ext, Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
            if (decoded.startsWith("http://") || decoded.startsWith("https://")) url = decoded;
        } catch (Exception ignored) {
            // 非普通远程列表继续通过本地代理读取。
        }
        OkGo.<String>get(url).tag(this).execute(new AbsCallback<String>() {
            @Override
            public String convertResponse(okhttp3.Response response) throws Throwable {
                return response.body().string();
            }

            @Override
            public void onSuccess(Response<String> response) {
                if (isFinishing() || isDestroyed()) return;
                ApiConfig.get().loadLives(TxtSubscribe.parseToJsonArray(response.body()));
                bindLiveConfig();
            }

            @Override
            public void onError(Response<String> response) {
                if (isFinishing() || isDestroyed()) return;
                showEmpty();
                Toast.makeText(LiveHomeActivity.this, "加载直播频道列表失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindLiveConfig() {
        if (isFinishing() || isDestroyed()) return;
        List<LiveChannelGroup> all = ApiConfig.get().getChannelGroupList();
        tvHomeMode.setText(radioMode ? "广播" : "直播");
        btnLiveRadio.setText(radioMode ? "📺" : "📻");
        btnLiveRadio.setContentDescription(radioMode ? "切换到直播" : "切换到广播");
        channelAdapter.setRadioMode(radioMode);
        groupAdapter.clearGroupState();
        tvGroups.clear();
        for (LiveChannelGroup group : all) {
            if (group.isRadioGroup() != radioMode) continue;
            if (group.getLiveChannels() == null || group.getLiveChannels().isEmpty()) continue;
            tvGroups.add(group);
        }
        if (tvGroups.isEmpty()) {
            showEmpty();
            Toast.makeText(this, radioMode ? "未加载到广播频道" : "未加载到直播频道", Toast.LENGTH_SHORT).show();
            groupAdapter.setNewData(new ArrayList<>());
            channelAdapter.setNewData(new ArrayList<>());
            return;
        }
        showSuccess();
        String warning = ApiConfig.get().getWarningText();
        tvHomeWarning.setText(warning);
        tvHomeWarning.setSelected(TextUtils.isEmpty(warning));
        groupAdapter.setNewData(new ArrayList<>(tvGroups));
        selectGroup(0);
    }

    private void selectGroup(int index) {
        if (index < 0 || index >= tvGroups.size()) return;
        LiveChannelGroup group = tvGroups.get(index);
        groupAdapter.setSelectedGroupIndex(group.getGroupIndex());
        groupAdapter.setFocusedGroupIndex(group.getGroupIndex());
        mHomeGroupView.setSelection(index);
        channelAdapter.setNewData(group.getLiveChannels() == null ? new ArrayList<>() : group.getLiveChannels());
        mHomeChannelView.scrollToPosition(0);
    }

    private void open(String channelName, String mode) {
        try {
            Intent intent = new Intent(this, LivePlayActivity.class);
            intent.putExtra(EXTRA_MODE, mode);
            if (!TextUtils.isEmpty(channelName)) intent.putExtra(EXTRA_CHANNEL_NAME, channelName);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "启动失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConfigDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        input.setSingleLine(true);
        input.setText(Hawk.get(HawkConfig.LIVE_API_URL, ""));
        input.setSelection(input.getText().length());
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                .setTitle("直播源地址")
                .setMessage("修改后重新进入首页即可生效")
                .setView(input, 30, 10, 30, 0)
                .setPositiveButton("保存", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (TextUtils.isEmpty(value)) {
                        Toast.makeText(this, "地址不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String old = Hawk.get(HawkConfig.LIVE_API_URL, "");
                    if (value.equals(old)) {
                        dialog.dismiss();
                        return;
                    }
                    Hawk.put(HawkConfig.LIVE_API_URL, value);
                    HistoryHelper.setLiveApiHistory(value);
                    Toast.makeText(this, "已保存，重新进入首页生效", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadLiveConfig();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        OkGo.getInstance().cancelTag(this);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
