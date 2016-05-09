package net.internetTelephone.program.login;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.SimpleSHA1;
import net.internetTelephone.program.common.base.MyJsonResponse;
import net.internetTelephone.program.common.network.MyAsyncHttpClient;
import net.internetTelephone.program.common.ui.BackActivity;
import net.internetTelephone.program.common.util.ActivityNavigate;
import net.internetTelephone.program.common.util.SingleToast;
import net.internetTelephone.program.common.util.ViewStyleUtil;
import net.internetTelephone.program.common.widget.LoginEditText;
import net.internetTelephone.program.common.widget.ValidePhoneView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.activity_phone_register)
public class PhoneRegisterActivity extends BackActivity {

    public static String REGIST_TIP = "注册 Coding 账号表示您已同意<font color=\"#3bbd79\">《Coding 服务条款》</font>";

    @ViewById
    LoginEditText globalKeyEdit, phoneEdit, passwordEdit, phoneCodeEdit, captchaEdit;

    @ViewById
    View loginButton;

    @ViewById
    TextView textClause;

    @ViewById
    ValidePhoneView sendCode;

    @AfterViews
    void initPhoneVerifyFragment() {
        View androidContent = findViewById(android.R.id.content);
        androidContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = androidContent.getHeight();
                if (height > 0) {
                    View layoutRoot = findViewById(R.id.layoutRoot);
                    ViewGroup.LayoutParams lp = layoutRoot.getLayoutParams();
                    lp.height = height;
                    layoutRoot.setLayoutParams(lp);
                    androidContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        ViewStyleUtil.editTextBindButton(loginButton, globalKeyEdit, phoneEdit,
                passwordEdit, phoneCodeEdit, captchaEdit);

            textClause.setText(Html.fromHtml(REGIST_TIP));

        sendCode.setEditPhone(phoneEdit);
        sendCode.setUrl(ValidePhoneView.REGISTER_SEND_MESSAGE_URL);

        needShowCaptch();
    }

    @Override
    public void onStop() {
        sendCode.onStop();
        super.onStop();
    }

    @Click
    void loginButton() {
        Global.popSoftkeyboard(this, phoneEdit, false);

        String phone = phoneEdit.getTextString();
        String code = phoneCodeEdit.getTextString();
        String globalKeyString = globalKeyEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String captcha = captchaEdit.getTextString();

        if (globalKeyString.length() < 3) {
            showMiddleToast("用户名（个性后缀）至少为3个字符");
            return;
        }

        if (password.length() < 6) {
            SingleToast.showMiddleToast(this, "密码至少为6位");
            return;
        } else if (64 < password.length()) {
            SingleToast.showMiddleToast(this, "密码不能大于64位");
            return;
        }

        RequestParams params = new RequestParams();
        params.put("phone", phone);
        params.put("global_key", globalKeyString);
        params.put("code", code);

        String sha1 = SimpleSHA1.sha1(password);
        params.put("password", sha1);
        params.put("confirm", sha1);

        if (captchaEdit.getVisibility() == View.VISIBLE) {
            params.put("j_captcha", captcha);
        }

        String url = Global.HOST_API + "/v2/account/register?channel=InternetTelephone-Android";
        MyAsyncHttpClient.post(this, url, params, new MyJsonResponse(this) {
            @Override
            public void onMySuccess(JSONObject response) {
                super.onMySuccess(response);
                EmailRegisterActivity.parseRegisterSuccess(PhoneRegisterActivity.this, response);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onMyFailure(JSONObject response) {
                super.onMyFailure(response);
                needShowCaptch();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                showProgressBar(false);
            }
        });


        showProgressBar(true, "");
    }

    private void needShowCaptch() {
        if (captchaEdit.getVisibility() == View.VISIBLE) {
            captchaEdit.requestCaptcha();
            return;
        }

        String HOST_NEED_CAPTCHA = Global.HOST_API + "/captcha/register";
        MyAsyncHttpClient.get(this, HOST_NEED_CAPTCHA, new MyJsonResponse(this) {
            @Override
            public void onMySuccess(JSONObject response) {
                super.onMySuccess(response);
                if (response.optBoolean("data")) {
                    captchaEdit.setVisibility(View.VISIBLE);
                    captchaEdit.requestCaptcha();
                }
            }
        });
    }

    @Click
    void otherRegister() {
        EmailRegisterActivity_.intent(this)
                .startForResult(EmailRegisterActivity.RESULT_REGISTER_EMAIL);
        overridePendingTransition(R.anim.right_to_left, R.anim.hold);
    }

    @Click
    void textClause() {
        ActivityNavigate.startTermActivity(this);
    }

    @OnActivityResult(EmailRegisterActivity.RESULT_REGISTER_EMAIL)
    void resultEmailRegister(int result) {
        if (result == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
