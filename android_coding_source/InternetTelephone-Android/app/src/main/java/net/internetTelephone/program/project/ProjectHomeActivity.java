package net.internetTelephone.program.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.widget.FrameLayout;

import net.internetTelephone.program.FileUrlActivity;
import net.internetTelephone.program.R;
import net.internetTelephone.program.common.ui.BaseActivity;
import net.internetTelephone.program.model.ProjectObject;
import net.internetTelephone.program.project.detail.ProjectActivity;
import net.internetTelephone.program.project.init.InitProUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity(R.layout.activity_project_home)
//@OptionsMenu(R.menu.menu_project_home)
public class ProjectHomeActivity extends BaseActivity {

    public static final String BROADCAST_CLOSE = ProjectHomeActivity.class.getName() + ".close";

    @Extra
    ProjectObject mProjectObject;

    @Extra
    ProjectActivity.ProjectJumpParam mJumpParam;

    @Extra
    boolean mNeedUpdateList = false; // 需要更新项目列表的排序

    @ViewById
    FrameLayout container;

    @StringArrayRes
    String[] dynamic_type_params;

    String mProjectUrl;

    private Fragment mCurrentFragment;

    @AfterViews
    protected void initProjectHomeActivity() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (mProjectObject != null) {
            initFragment();
        } else if (mJumpParam != null) {
            mProjectUrl = String.format(FileUrlActivity.HOST_PROJECT, mJumpParam.mUser, mJumpParam.mProject);
            getNetwork(mProjectUrl, mProjectUrl);
        } else {
            finish();
        }
    }

    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BROADCAST_CLOSE)) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_CLOSE);
        registerReceiver(refreshReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(mProjectUrl)) {
            if (code == 0) {
                mProjectObject = new ProjectObject(respanse.getJSONObject("data"));
                initFragment();
            } else {
                showErrorMsg(code, respanse);
            }
        } else if (tag.equals(PrivateProjectHomeFragment.HOST_VISTIT)) {
            if (code == 0) {
                sendBroadcast(new Intent(ProjectFragment.RECEIVER_INTENT_REFRESH_PROJECT));
            } else {
                showErrorMsg(code, respanse);
            }
        }
    }

    private void initFragment() {
        if (mNeedUpdateList) {
            String url = String.format(PrivateProjectHomeFragment.HOST_VISTIT, mProjectObject.getId());
            getNetwork(url, PrivateProjectHomeFragment.HOST_VISTIT);
        }

        Fragment fragment;
        if (mProjectObject.isPublic()) {
            fragment = PublicProjectHomeFragment_.builder()
                    .mProjectObject(mProjectObject)
                    .build();
        } else {
            fragment = PrivateProjectHomeFragment_.builder()
                    .mProjectObject(mProjectObject)
                    .build();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
        mCurrentFragment = fragment;
    }

    @OptionsItem(android.R.id.home)
    final protected void clickBack() {
        if (mCurrentFragment instanceof BaseProjectHomeFragment) {
            if (((BaseProjectHomeFragment) mCurrentFragment).isBackToRefresh()) {
                InitProUtils.backIntentToMain(this);
                return;
            }
        }

        finish();
    }
}
