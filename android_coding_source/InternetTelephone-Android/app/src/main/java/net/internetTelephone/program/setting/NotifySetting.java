package net.internetTelephone.program.setting;

import android.content.Intent;
import android.widget.CheckBox;

import net.internetTelephone.program.MainActivity;
import net.internetTelephone.program.R;
import net.internetTelephone.program.common.ui.BackActivity;
import net.internetTelephone.program.model.AccountInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_notify_setting)
public class NotifySetting extends BackActivity {

    @ViewById
    CheckBox allNotify;

    @AfterViews
    protected final void initNotifySetting() {
        boolean mLastNotifySetting = AccountInfo.getNeedPush(this);
        allNotify.setChecked(mLastNotifySetting);
    }

    @Click
    void allNotify() {
        AccountInfo.setNeedPush(this, allNotify.isChecked());

        Intent intent = new Intent(MainActivity.BroadcastPushStyle);
        sendBroadcast(intent);
    }

}
