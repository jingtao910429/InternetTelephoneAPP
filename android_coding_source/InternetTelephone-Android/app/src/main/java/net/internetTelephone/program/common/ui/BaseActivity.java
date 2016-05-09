package net.internetTelephone.program.common.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.internetTelephone.program.FootUpdate;
import net.internetTelephone.program.R;
import net.internetTelephone.program.common.DialogUtil;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.GlobalSetting;
import net.internetTelephone.program.common.ImageLoadTool;
import net.internetTelephone.program.common.StartActivity;
import net.internetTelephone.program.common.UnreadNotify;
import net.internetTelephone.program.common.network.NetworkCallback;
import net.internetTelephone.program.common.network.NetworkImpl;
import net.internetTelephone.program.common.umeng.UmengActivity;
import net.internetTelephone.program.common.util.SingleToast;
import net.internetTelephone.program.model.RequestData;
import net.internetTelephone.program.user.UserDetailActivity_;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cc191954 on 14-8-16.
 * 封装了图片下载并缓存
 */
public class BaseActivity extends UmengActivity implements NetworkCallback, StartActivity {

    protected LayoutInflater mInflater;
    protected FootUpdate mFootUpdate = new FootUpdate();
    protected View.OnClickListener mOnClickUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String globalKey = (String) v.getTag();

            UserDetailActivity_.intent(BaseActivity.this)
                    .globalKey(globalKey)
                    .start();
        }
    };
    SingleToast mSingleToast;

    private ImageLoadTool imageLoadTool = new ImageLoadTool();
    private ProgressDialog mProgressDialog;
    private NetworkImpl networkImpl;
    /**
     * 载入动画
     */
    private DialogUtil.LoadingPopupWindow mDialogProgressPopWindow = null;

    protected void showProgressBar(boolean show) {
        showProgressBar(show, "");
    }

    public void showProgressBar(boolean show, String message) {
        if (show) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } else {
            mProgressDialog.hide();
        }
    }

    protected void showProgressBar(boolean show, int message) {
        String s = getString(message);
        showProgressBar(show, s);
    }

    protected void showProgressBar(int messageId) {
        String message = getString(messageId);
        showProgressBar(true, message);
    }

    public void showErrorMsg(int code, JSONObject json) {
        if (code == NetworkImpl.NETWORK_ERROR) {
            showButtomToast(R.string.connect_service_fail);
        } else {
            String msg = Global.getErrorMsg(json);
            if (!msg.isEmpty()) {
                showButtomToast(msg);
            }
        }
    }

    protected void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected void setActionBarTitle(int title) {
        String titleString = getString(title);
        setActionBarTitle(titleString);
    }

    public void showErrorMsgMiddle(int code, JSONObject json) {
        if (code == NetworkImpl.NETWORK_ERROR) {
            showMiddleToast(R.string.connect_service_fail);
        } else {
            String msg = Global.getErrorMsg(json);
            if (!msg.isEmpty()) {
                showMiddleToast(msg);
            }
        }
    }

    public ImageLoadTool getImageLoad() {
        return imageLoadTool;
    }

    protected boolean isLoadingFirstPage(String tag) {
        return networkImpl.isLoadingFirstPage(tag);
    }

    protected boolean isLoadingLastPage(String tag) {
        return networkImpl.isLoadingLastPage(tag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSingleToast = new SingleToast(this);

        networkImpl = new NetworkImpl(this, this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        mInflater = getLayoutInflater();
        initSetting();

        UnreadNotify.update(this);
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        GlobalSetting.getInstance().removeMessageNoNotify();

        super.onDestroy();
    }

    protected void initSetting() {
        networkImpl.initSetting();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
    }

    protected void getNextPageNetwork(String url, final String tag) {
        networkImpl.getNextPageNetwork(url, tag);
    }

    protected void postNetwork(RequestData request, String tag) {
        postNetwork(request.url, request.params, tag);
    }

    protected void postNetwork(RequestData request, String tag, Object data) {
        postNetwork(request.url, request.params, tag, -1, data);
    }

    protected void postNetwork(String url, String tag) {
        postNetwork(url, new RequestParams(), tag);
    }

    protected void postNetwork(String url, RequestParams params, final String tag) {
        networkImpl.loadData(url, params, tag, -1, null, NetworkImpl.Request.Post);
    }

    protected void postNetwork(String url, RequestParams params, final String tag, int dataPos, Object data) {
        networkImpl.loadData(url, params, tag, dataPos, data, NetworkImpl.Request.Post);
    }

    @Override
    public void getNetwork(String url, final String tag) {
        networkImpl.loadData(url, null, tag, -1, null, NetworkImpl.Request.Get);
    }

    protected void getNetwork(String url, final String tag, int dataPos, Object data) {
        networkImpl.loadData(url, null, tag, dataPos, data, NetworkImpl.Request.Get);
    }

    protected void putNetwork(String url, RequestParams params, final String tag) {
        networkImpl.loadData(url, params, tag, -1, null, NetworkImpl.Request.Put);
    }

    protected void putNetwork(String url, RequestParams params, String tag, int pos, Object object) {
        networkImpl.loadData(url, params, tag, pos, object, NetworkImpl.Request.Put);
    }

    protected void putNetwork(String url, final String tag, int dataPos, Object data) {
        networkImpl.loadData(url, null, tag, dataPos, data, NetworkImpl.Request.Put);
    }

    protected void deleteNetwork(String url, final String tag) {
        networkImpl.loadData(url, null, tag, -1, null, NetworkImpl.Request.Delete);
    }

    protected void deleteNetwork(String url, RequestParams params, final String tag) {
        networkImpl.loadData(url, params, tag, -1, null, NetworkImpl.Request.Delete);
    }

    protected void deleteNetwork(String url, final String tag, Object id) {
        networkImpl.loadData(url, null, tag, -1, id, NetworkImpl.Request.Delete);
    }

    protected void deleteNetwork(String url, final String tag, int dataPos, Object id) {
        networkImpl.loadData(url, null, tag, dataPos, id, NetworkImpl.Request.Delete);
    }

    protected void showDialog(String title, String msg, DialogInterface.OnClickListener clickOk) {
        showDialog(title, msg, clickOk, null);
    }


//    protected void showListDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        AlertDialog dialog = builder.setItems()
//    }

    protected void showDialog(String title, String msg, DialogInterface.OnClickListener clickOk,
                              DialogInterface.OnClickListener clickCancel) {
        showDialog(title, msg, clickOk, clickCancel, "确定", "取消");
    }

    protected void showDialog(String title, String msg, DialogInterface.OnClickListener clickOk,
                              DialogInterface.OnClickListener clickCancel,
                              String okButton,
                              String cancelButton) {
        showDialog(title, msg, clickOk, clickCancel, null, okButton, cancelButton, "");
    }

    protected void showDialog(String title, String msg, DialogInterface.OnClickListener clickOk,
                              DialogInterface.OnClickListener clickCancel,
                              DialogInterface.OnClickListener clickNeutral,
                              String okButton,
                              String cancelButton,
                              String neutralButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg);
        if (okButton != null) {
            builder.setPositiveButton(okButton, clickOk);
        }
        if (cancelButton != null) {
            builder.setNegativeButton(cancelButton, clickCancel);
        }

        if (clickNeutral != null && !neutralButton.isEmpty()) {
            builder.setNeutralButton(neutralButton, clickNeutral);
        }

        AlertDialog dialog = builder.show();
    }

    public void showButtomToast(String msg) {
        mSingleToast.showButtomToast(msg);
    }

    public void showMiddleToast(int id) {
        mSingleToast.showMiddleToast(id);
    }

    public void showMiddleToast(String msg) {
        mSingleToast.showMiddleToast(msg);
    }

    public void showMiddleToastLong(String msg) {
        mSingleToast.showMiddleToastLong(msg);
    }

    public void showButtomToast(int messageId) {
        mSingleToast.showButtomToast(messageId);
    }

    protected void iconfromNetwork(ImageView view, String url) {
        imageLoadTool.loadImage(view, Global.makeSmallUrl(view, url));
    }

    protected void iconfromNetwork(ImageView view, String url, SimpleImageLoadingListener animate) {
        imageLoadTool.loadImage(view, Global.makeSmallUrl(view, url), animate);
    }

    protected void imagefromNetwork(ImageView view, String url) {
        imageLoadTool.loadImageFromUrl(view, url);
    }

    protected void imagefromNetwork(ImageView view, String url, DisplayImageOptions options) {
        imageLoadTool.loadImageFromUrl(view, url, options);
    }

    public void initDialogLoading() {
        if (mDialogProgressPopWindow == null) {
            PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {
                public void onDismiss() {
                    hideProgressDialog();
                }
            };

            mDialogProgressPopWindow = DialogUtil.initProgressDialog(this, onDismissListener);
        }
    }

    public void showDialogLoading(String title) {
        initDialogLoading();
        DialogUtil.showProgressDialog(this, mDialogProgressPopWindow, title);
    }

    public void showDialogLoading() {
        showDialogLoading("");
    }

    public void hideProgressDialog() {
        if (mDialogProgressPopWindow != null) {
            DialogUtil.hideDialog(mDialogProgressPopWindow);
        }
    }

}
