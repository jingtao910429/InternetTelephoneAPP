package net.internetTelephone.program.common;

import android.webkit.WebView;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.ui.BackActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_terms)
public class TermsActivity extends BackActivity {

    @ViewById
    WebView webView;

    @AfterViews
    protected void initTermsActivity() {
        Global.initWebView(webView);
        try {
            webView.loadDataWithBaseURL(null, Global.readTextFile(getAssets().open("terms")), "text/html", "UTF-8", null);
        } catch (Exception e) {
            Global.errorLog(e);
        }
    }
}
