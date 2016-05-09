package net.internetTelephone.program.login.phone;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.ui.BackActivity;
import net.internetTelephone.program.login.EmailRegisterActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;

/*
 *  通过手机号重置密码
 */
@EActivity(R.layout.activity_phone_set_password)
public class PhoneSetPasswordActivity extends BackActivity {

    @Extra
    String account;

    @AfterViews
    final void initPhoneSetPasswordActivity() {
        Fragment fragment;
        fragment = PhoneSetPasswordFragment2_.builder().account(account).build();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            new AlertDialog.Builder(this)
                    .setTitle("不激活就无法使用 Coding，确定放弃?")
                    .setPositiveButton("确定", (dialog, which) -> finish())
                    .setNegativeButton("取消", null)
                    .show();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            finish();
        }
    }

    @OnActivityResult(EmailRegisterActivity.RESULT_REGISTER_EMAIL)
    void resultEmailRegister(int result) {
        if (result == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

}
