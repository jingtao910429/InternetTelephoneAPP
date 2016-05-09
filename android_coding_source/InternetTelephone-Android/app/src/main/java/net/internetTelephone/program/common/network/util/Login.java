package net.internetTelephone.program.common.network.util;

import com.loopj.android.http.RequestParams;

import net.internetTelephone.program.MyApp;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.base.MyJsonResponse;
import net.internetTelephone.program.common.network.MyAsyncHttpClient;
import net.internetTelephone.program.common.ui.BaseActivity;

import org.json.JSONObject;

/**
 * Created by chenchao on 16/2/20.
 */
public class Login {
    public static void resendActivityEmail(BaseActivity activity) {
        String url = Global.HOST_API + "/account/register/email/send";
        RequestParams params = new RequestParams();
        params.put("email", MyApp.sUserObject.email);
        MyAsyncHttpClient.post(activity, url, params, new MyJsonResponse(activity) {
            @Override
            public void onMySuccess(JSONObject response) {
                super.onMySuccess(response);
                activity.showMiddleToast("发送激活邮件成功");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                activity.showProgressBar(false, "");
            }
        });

        activity.showProgressBar(true, "");
    }
}
