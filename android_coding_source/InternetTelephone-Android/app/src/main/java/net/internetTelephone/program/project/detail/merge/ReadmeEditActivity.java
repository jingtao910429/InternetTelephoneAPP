package net.internetTelephone.program.project.detail.merge;

import android.support.v4.app.Fragment;

import com.loopj.android.http.RequestParams;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.base.MyJsonResponse;
import net.internetTelephone.program.common.network.MyAsyncHttpClient;
import net.internetTelephone.program.common.ui.BackActivity;
import net.internetTelephone.program.model.ProjectObject;
import net.internetTelephone.program.project.detail.TopicAddActivity;
import net.internetTelephone.program.project.detail.TopicEditFragment;
import net.internetTelephone.program.task.TaskDescrip;
import net.internetTelephone.program.task.TaskDespEditFragment;
import net.internetTelephone.program.task.TaskDespEditFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.json.JSONObject;

import java.io.Serializable;

@EActivity(R.layout.activity_readme_edit)
public class ReadmeEditActivity extends BackActivity implements TaskDescrip, TopicEditFragment.SaveData {

    @Extra
    ProjectObject mProjectObject;

    @Extra
    PostParam mPostParam;

    TaskDespEditFragment editFragment;
    Fragment previewFragment;
    private TopicAddActivity.TopicData modifyData = new TopicAddActivity.TopicData();

    @AfterViews
    protected final void initTaskDescriptionActivity() {
        editFragment = TaskDespEditFragment_.builder().build();
        String url = mProjectObject.getHttpReadmePreview(mPostParam.version, mPostParam.name);
        previewFragment = ReadmePerviewFragment_.builder().url(url).build();

//        "https://coding.net/api" + "/user/gggg/project/gggg_ghhjj" + "/git/blob-preview/master%252FREADME.md"
//        if (markdown.isEmpty()) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.container, editFragment).commit();
//        } else {
        modifyData.content = mPostParam.data;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, editFragment).commit();
//        }
    }

    @Override
    public void saveData(TopicAddActivity.TopicData data) {
        modifyData = data;
    }

    @Override
    public TopicAddActivity.TopicData loadData() {
        return modifyData;
    }

    @Override
    public void switchPreview() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, previewFragment).commit();
    }

    @Override
    public void switchEdit() {

        getSupportFragmentManager().beginTransaction().replace(R.id.container, editFragment).commit();
    }

    @Override
    public void exit() {
        RequestParams params = new RequestParams();
        params.put("content", modifyData.content);
        params.put("message", "update README.md");
        params.put("lastCommitSha", mPostParam.lastCommitId);
        String url = mProjectObject.getHttpReadme(mPostParam.version, mPostParam.name);
        MyAsyncHttpClient.post(this, url, params, new MyJsonResponse(ReadmeEditActivity.this) {
            @Override
            public void onMySuccess(JSONObject response) {
                super.onMySuccess(response);
                closeAndSave("");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                showProgressBar(false);
            }
        });
        showProgressBar(true);
    }

    @Override
    public String getProjectPath() {
        return mProjectObject.getProjectPath();
    }

    @Override
    public boolean isProjectPublic() {
        return mProjectObject.isPublic();
    }

    @Override
    public void closeAndSave(String s) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public String createLocateHtml(String s) {
        try {
            final String bubble = Global.readTextFile(getAssets().open("markdown"));
            return bubble.replace("${webview_content}", s);
        } catch (Exception e) {
            Global.errorLog(e);
            return "";
        }
    }

    public static class PostParam implements Serializable {
        String lastCommitId;
        String name;
        String data;
        String version;

        public PostParam(JSONObject jsonData, String version) {
            JSONObject headCommit = jsonData.optJSONObject("headCommit");
            lastCommitId = headCommit.optString("commitId", "");

            JSONObject json = jsonData.optJSONObject("readme");
            name = json.optString("name", "");
            data = json.optString("data", "");
            this.version = version;
        }
    }
}
