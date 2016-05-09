package net.internetTelephone.program.project.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import net.internetTelephone.program.ImagePagerFragment;
import net.internetTelephone.program.ImagePagerFragment_;
import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.network.MyAsyncHttpClient;
import net.internetTelephone.program.common.ui.BackActivity;
import net.internetTelephone.program.common.url.UrlCreate;
import net.internetTelephone.program.model.GitFileBlobObject;
import net.internetTelephone.program.model.GitFileInfoObject;
import net.internetTelephone.program.model.ProjectObject;
import net.internetTelephone.program.project.git.BranchCommitListActivity_;
import net.internetTelephone.program.project.git.EditCodeActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@EActivity(R.layout.activity_gitview)
@OptionsMenu(R.menu.git_view)
public class GitViewActivity extends BackActivity {
    private static String TAG = GitViewActivity.class.getSimpleName();

    private static final int RESULT_EDIT = 1;

    @Extra
    String mProjectPath;

    @Extra
    GitFileInfoObject mGitFileInfoObject;

    @Extra
    String mVersion = ProjectGitFragment.MASTER;

    @ViewById
    WebView webview;

    @ViewById
    ViewPager pager;
    int mPagerPosition = 0;

    ImagePager adapter;
    ArrayList<String> mArrayUri;
    AsyncHttpClient client;

    File mTempPicFile;

    String urlBlob = Global.HOST_API + "%s/git/blob/%s/%s";
    String urlImage = Global.HOST + "%s/git/raw/%s/%s";

    GitFileBlobObject mFile;

    @AfterViews
    protected final void initGitViewActivity() {
        setActionBarTitle(mGitFileInfoObject.name);

        client = MyAsyncHttpClient.createClient(GitViewActivity.this);

        urlBlob = String.format(urlBlob, mProjectPath, mVersion, Global.encodeUtf8(Global.encodeUtf8(mGitFileInfoObject.path)));
        webview.getSettings().setBuiltInZoomControls(true);
        Global.initWebView(webview);

        mArrayUri = new ArrayList<>();
        adapter = new ImagePager(getSupportFragmentManager());
        pager.setAdapter(adapter);

        showDialogLoading();
        getNetwork(urlBlob, urlBlob);
    }

    @OptionsItem
    void action_edit() {
        EditCodeActivity_.intent(this)
                .mProjectPath(mProjectPath)
                .mGitFileInfoObject(mGitFileInfoObject)
                .mVersion(mVersion)
                .mGitAll(mFile)
                .startForResult(RESULT_EDIT);
    }


    @OptionsItem
    void action_history() {

        String peek = mGitFileInfoObject.path;
        if (peek.isEmpty() && mVersion.isEmpty()) {
            showButtomToast("没有Commit记录");
            return;
        }

        String commitUrl = UrlCreate.gitTreeCommit(mProjectPath, mVersion, peek);
        BranchCommitListActivity_.intent(this).mCommitsUrl(commitUrl).start();
//        RedPointTip.markUsed(getActivity(), RedPointTip.Type.CodeHistory);
    }

//    @OptionsItem
//    void action_commit() {
//
//    }

    @OnActivityResult(RESULT_EDIT)
    void onResultEdit(int resultCode, @OnActivityResult.Extra GitFileBlobObject resultData) {
        if (resultCode == RESULT_OK) {
            mFile = resultData;
            bindUIByData();
        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(urlBlob)) {
            hideProgressDialog();

            if (code == 0) {
                mFile = new GitFileBlobObject(respanse.getJSONObject("data"));
                bindUIByData();

            } else {
                hideProgressDialog();
                showErrorMsg(code, respanse);
            }
        }
    }

    public void bindUIByData() {
        if (mFile.getGitFileObject().mode.equals("image")) {
            try {
                mTempPicFile = File.createTempFile("Coding_", ".tmp", getCacheDir());
                mTempPicFile.deleteOnExit();
                String s = ProjectObject.translatePathToOld(mProjectPath);
                download(String.format(urlImage, s, mVersion, mFile.getGitFileObject().path));
            } catch (IOException e) {
                showButtomToast("图片无法下载");
            }

        } else {
            pager.setVisibility(View.GONE);
            Global.setWebViewContent(webview, mFile.getGitFileObject());
        }
    }


    private void download(String url) {
        //url = "https://coding.net/api/project/5166/files/58705/download";
        //File mFile = FileUtil.getDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mFileObject.name);
        Log.d(TAG, "FileUrl:" + url);

        client.get(GitViewActivity.this, url, new FileAsyncHttpResponseHandler(mTempPicFile) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                for (Header header : headers) {
                    Log.v(TAG, "onFailure:" + statusCode + " " + header.getName() + ":" + header.getValue());
                }
                showButtomToast("下载失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                mArrayUri.add("file:///" + response.getAbsolutePath());
                adapter.notifyDataSetChanged();
                pager.setVisibility(View.VISIBLE);

            }

        });
    }

//    @Override
//    protected String getLink() {
//        String s = ProjectObject.translatePathToOld(mProjectPath);
//        return Global.HOST + s + "/git/blob/" + mVersion + "/" + mGitFileInfoObject.path;
//    }

    class ImagePager extends FragmentPagerAdapter {

        public ImagePager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            ImagePagerFragment_ fragment = new ImagePagerFragment_();
            Bundle bundle = new Bundle();
            bundle.putString("uri", mArrayUri.get(i));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImagePagerFragment fragment = (ImagePagerFragment) super.instantiateItem(container, position);
            fragment.setData(mArrayUri.get(position));
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mArrayUri.size();
        }
    }
}
