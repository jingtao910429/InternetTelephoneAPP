package net.internetTelephone.program.login;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.umeng.UmengEvent;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_reset_password_base)
public class ResetPasswordActivity extends ResetPasswordBaseActivity {

    @Override
    String getRequestHost() {
        umengEvent(UmengEvent.USER, "重置密码");
        return Global.HOST_API + "/resetPassword";
    }
}
