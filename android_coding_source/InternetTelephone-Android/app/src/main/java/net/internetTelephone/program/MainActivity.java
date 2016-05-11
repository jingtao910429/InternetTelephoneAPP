package net.internetTelephone.program;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.service.XGPushService;
import net.internetTelephone.program.common.interf.OnTabReselectListener;
import net.internetTelephone.program.common.LoginBackground;
import net.internetTelephone.program.common.htmltext.URLSpanNoUnderline;
import net.internetTelephone.program.common.network.util.Login;
import net.internetTelephone.program.common.ui.BaseActivity;
import net.internetTelephone.program.common.ui.MainTab;
import net.internetTelephone.program.common.widget.MyFragmentTabHost;
import net.internetTelephone.program.login.MarketingHelp;
import net.internetTelephone.program.login.ZhongQiuGuideActivity;
import net.internetTelephone.program.maopao.MaopaoListFragment;
import net.internetTelephone.program.maopao.MaopaoListFragment_;
import net.internetTelephone.program.message.UsersListFragment_;
import net.internetTelephone.program.model.AccountInfo;
import net.internetTelephone.program.project.ProjectFragment;
import net.internetTelephone.program.project.ProjectFragment_;
import net.internetTelephone.program.project.init.InitProUtils;
import net.internetTelephone.program.setting.SettingFragment_;
import net.internetTelephone.program.task.TaskFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment_.NavigationDrawerCallbacks, TabHost.OnTabChangeListener, View.OnTouchListener {

    public static final String TAG = "MainActivity";
    public static final String BroadcastPushStyle = "BroadcastPushStyle";
    NavigationDrawerFragment_ mNavigationDrawerFragment;
    String mTitle;
    @Extra
    String mPushUrl;
    @StringArrayRes
    String drawer_title[];
    @StringArrayRes
    String maopao_action_types[];
    @ViewById
    ViewGroup drawer_layout;

    //http://www.cnblogs.com/mengdd/archive/2015/06/23/4595973.html(ButterKnife基本使用)
//    @ViewById
    public MyFragmentTabHost mTabHost;

    private static boolean sNeedWarnEmailNoValidLogin = false;

    public static void setNeedWarnEmailNoValidLogin() {
        sNeedWarnEmailNoValidLogin = true;
    }

    private static boolean sNeedWarnEmailNoValidRegister = false;

    public static void setNeedWarnEmailNoValidRegister() {
        sNeedWarnEmailNoValidRegister = true;
    }

    boolean mFirstEnter = true;
    BroadcastReceiver mUpdatePushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotifyService();
        }
    };
    int mSelectPos = 0;
    MySpinnerAdapter mSpinnerAdapter;
    private View actionbarCustom;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ZhongQiuGuideActivity.showHolidayGuide(this);

        IntentFilter intentFilter = new IntentFilter(BroadcastPushStyle);
        registerReceiver(mUpdatePushReceiver, intentFilter);

//        XGPushConfig.enableDebug(this, true);
        // qq push
        //updateNotifyService();
        //pushInXiaomi();

        LoginBackground loginBackground = new LoginBackground(this);
        loginBackground.update();

        mFirstEnter = (savedInstanceState == null);

        if (savedInstanceState != null) {
            mSelectPos = savedInstanceState.getInt("pos", 0);
            mTitle = savedInstanceState.getString("mTitle");
        }

        if (mPushUrl != null) {
            URLSpanNoUnderline.openActivityByUri(this, mPushUrl, true);
            mPushUrl = null;
            getIntent().getExtras().remove("mPushUrl");
        }

        MarketingHelp.showMarketing(this);

        warnMailNoValidLogin();
        warnMailNoValidRegister();
    }


    private void warnMailNoValidLogin() {
        if (sNeedWarnEmailNoValidLogin) {
            sNeedWarnEmailNoValidLogin = false;

            String emailString = MyApp.sUserObject.email;
            boolean emailValid = MyApp.sUserObject.isEmailValidation();
            if (!emailString.isEmpty() && !emailValid) {
                new AlertDialog.Builder(this)
                        .setTitle("激活邮件")
                        .setMessage(R.string.alert_activity_email2)
                        .setPositiveButton("重发激活邮件", (dialog, which) -> {
                            Login.resendActivityEmail(MainActivity.this);
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
        }
    }

    private void warnMailNoValidRegister() {
        if (sNeedWarnEmailNoValidRegister) {
            sNeedWarnEmailNoValidRegister = false;

            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(R.string.alert_activity_email)
                    .setPositiveButton("确定", null)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUpdatePushReceiver);
        super.onDestroy();
    }

    // 信鸽文档推荐调用，防止在小米手机上收不到推送
    private void pushInXiaomi() {
        Context context = getApplicationContext();
        Intent service = new Intent(context, XGPushService.class);
        context.startService(service);
    }

    private void updateNotifyService() {
        boolean needPush = AccountInfo.getNeedPush(this);

        if (needPush) {
            String globalKey = MyApp.sUserObject.global_key;
            XGPushManager.registerPush(this, globalKey);
        } else {
            XGPushManager.registerPush(this, "*");
        }
    }

    @AfterViews
    final void initMainActivity() {
        Intent intent = new Intent(this, UpdateService.class);
        intent.putExtra(UpdateService.EXTRA_BACKGROUND, true);
        intent.putExtra(UpdateService.EXTRA_WIFI, true);
        intent.putExtra(UpdateService.EXTRA_DEL_OLD_APK, true);
        startService(intent);

        mSpinnerAdapter = new MySpinnerAdapter(getLayoutInflater(), maopao_action_types);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setCustomView(R.layout.actionbar_custom_spinner);
        actionbarCustom = supportActionBar.getCustomView();
        Spinner spinner = (Spinner) supportActionBar.getCustomView().findViewById(R.id.spinner);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String[] strings = getResources().getStringArray(R.array.maopao_action_types);

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment;
                Bundle bundle = new Bundle();
                mSpinnerAdapter.setCheckPos(position);

                switch (position) {
                    case 1:
                        fragment = new MaopaoListFragment_();
                        bundle.putSerializable("mType", MaopaoListFragment.Type.friends);
                        break;

                    case 2:
                        fragment = new MaopaoListFragment_();
                        bundle.putSerializable("mType", MaopaoListFragment.Type.hot);
                        break;

                    case 0:
                    default:
                        fragment = new MaopaoListFragment_();
                        bundle.putSerializable("mType", MaopaoListFragment.Type.time);

                        break;
                }

                fragment.setArguments(bundle);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Log.d("", ft == null ? "is null" : "is good");
                ft.replace(R.id.container, fragment, strings[position]);
                ft.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mNavigationDrawerFragment = (NavigationDrawerFragment_)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = drawer_title[0];

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (mFirstEnter) {
            onNavigationDrawerItemSelected(0);
        }
        initView();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        mSelectPos = position;
        Fragment fragment = null;

        switch (position) {
            case 0://防止重复加载数据
//                fragment = new ProjectFragment_();
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                boolean containFragment = false;
                for (Fragment item : fragments) {
                    if (item instanceof ProjectFragment_) {
                        containFragment = true;
                        break;
                    }
                }
                if (!containFragment) {
                    fragment = new ProjectFragment_();
                }
                break;
            case 1:
                fragment = new TaskFragment_();
                break;
            case 2:
                // 进入冒泡页面，单独处理
                break;

            case 3:
                fragment = new UsersListFragment_();
                break;

            case 4:
                fragment = new SettingFragment_();
                break;
        }

        if (position == 2) {
            ActionBar actionBar = getSupportActionBar();
            Spinner spinner;
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(actionbarCustom);
            spinner = (Spinner) actionbarCustom.findViewById(R.id.spinner);
            List<Fragment> fragments = getSupportFragmentManager().getFragments();

            boolean containFragment = false;
            for (Fragment item : fragments) {
                if (item instanceof MaopaoListFragment) {
                    containFragment = true;
                    break;
                }
            }

            if (!containFragment) {
                int pos = spinner.getSelectedItemPosition();
                spinner.getOnItemSelectedListener().onItemSelected(null, null, pos, pos);
            }
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pos", mSelectPos);
//        outState.putSerializable("mPushOpened", mPushOpened);
        outState.putString("mTitle", mTitle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectPos = savedInstanceState.getInt("pos", 0);
        mTitle = savedInstanceState.getString("mTitle");
        restoreActionBar();
    }

    public void restoreActionBar() {
        mTitle = drawer_title[mSelectPos];
        ActionBar actionBar = getSupportActionBar();
        if (mSelectPos != 2) {
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
//            actionBar.setIcon(R.drawable.ic_lancher);
        } else {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(actionbarCustom);
            actionBar.setTitle("");
//             Spinner   spinner = (Spinner) actionbarCustom.findViewById(R.id.spinner);
//            spinner.setSelection(1);
//            spinner.setSelection(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    //当项目设置里删除项目后，重新跳转到主界面，并刷新ProjectFragment
    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getStringExtra("action");
        if (!TextUtils.isEmpty(action) && action.equals(InitProUtils.FLAG_REFRESH)) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment item : fragments) {
                if (item instanceof ProjectFragment) {
                    if (item.isAdded()) {
                        ((ProjectFragment) item).onRefresh();
                    }
                    break;
                }
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    public void onTabChanged(String tabId) {

        final int size = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                v.setSelected(true);
            } else {
                v.setSelected(false);
            }
        }
        supportInvalidateOptionsMenu();

        mSelectPos = mTabHost.getCurrentTab();

        Fragment fragment = null;

        fragment = new TaskFragment_();

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouchEvent(event);
        boolean consumed = false;
        // use getTabHost().getCurrentTabView to decide if the current tab is
        // touched again
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && v.equals(mTabHost.getCurrentTabView())) {
            // use getTabHost().getCurrentView() to get a handle to the view
            // which is displayed in the tab - and to get this views context
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null
                    && currentFragment instanceof OnTabReselectListener) {
                OnTabReselectListener listener = (OnTabReselectListener) currentFragment;
                listener.onTabReselect();
                consumed = true;
            }
        }
        return consumed;
    }

    //设置tabbar
    private void initTabs() {
        MainTab[] tabs = MainTab.values();
        final int size = tabs.length;
        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];
            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
            Drawable drawable = this.getResources().getDrawable(
                    mainTab.getResIcon());
            title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,
                    null);
            if (i == 2) {
                indicator.setVisibility(View.INVISIBLE);
                mTabHost.setNoTabChangedTag(getString(mainTab.getResName()));
            }
            title.setText(getString(mainTab.getResName()));
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);
            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }

    }

    private void initView() {

        mTabHost = (MyFragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.container);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }

        initTabs();

//        // 中间按键图片触发
//        mAddBt.setOnClickListener(this);

        mTabHost.setCurrentTab(0);
        mTabHost.setOnTabChangedListener(this);
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showButtomToast("再按一次退出网络电话");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(
                mTabHost.getCurrentTabTag());
    }


    class MySpinnerAdapter extends BaseAdapter {

        final int spinnerIcons[] = new int[]{
                R.drawable.ic_spinner_maopao_time,
                R.drawable.ic_spinner_maopao_friend,
                R.drawable.ic_spinner_maopao_hot,
        };
        int checkPos = 0;
        private LayoutInflater inflater;
        private String[] project_activity_action_list;

        public MySpinnerAdapter(LayoutInflater inflater, String[] titles) {
            this.inflater = inflater;
            this.project_activity_action_list = titles;
        }

        public void setCheckPos(int pos) {
            checkPos = pos;
        }

        @Override
        public int getCount() {
            return spinnerIcons.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.spinner_layout_head, parent, false);
            }

            ((TextView) convertView).setText(project_activity_action_list[position]);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.spinner_layout_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(project_activity_action_list[position]);

            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageResource(spinnerIcons[position]);

            if (checkPos == position) {
                convertView.setBackgroundColor(getResources().getColor(R.color.divide));
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            return convertView;
        }
    }

}
