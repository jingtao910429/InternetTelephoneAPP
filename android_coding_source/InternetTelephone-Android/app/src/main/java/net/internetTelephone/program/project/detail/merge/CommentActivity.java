package net.internetTelephone.program.project.detail.merge;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.ui.BackActivity;
import net.internetTelephone.program.model.BaseComment;
import net.internetTelephone.program.model.RequestData;
import net.internetTelephone.program.project.detail.TopicAddActivity;
import net.internetTelephone.program.project.detail.TopicEditFragment;
import net.internetTelephone.program.task.TaskDespPreviewFragment_;
import net.internetTelephone.program.third.EmojiFilter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@EActivity(R.layout.activity_comment)
public class CommentActivity extends BackActivity implements TopicEditFragment.SaveData {

    private static final String HOST_SEND_COMMENT = "HOST_SEND_COMMENT";

    @Extra
    CommentParam mParam;

    CommentEditFragment editFragment;
    Fragment previewFragment;
    private TopicAddActivity.TopicData modifyData = new TopicAddActivity.TopicData();

    @AfterViews
    void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String atName = mParam.getAtSome();
        if (!atName.isEmpty()) {
            modifyData.content = String.format("@%s ", atName);
        }

        editFragment = CommentEditFragment_.builder().mMergeUrl(mParam.getAtSomeUrl()).build();
        previewFragment = TaskDespPreviewFragment_.builder().build();

        switchEdit();
    }

    @Override
    public void onBackPressed() {
        if (!editFragment.isEmpty()) {
            showDialog("发表评论", "确定放弃已写的评论？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            finish();
        }
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
        getSupportFragmentManager().beginTransaction().replace(R.id.container, previewFragment, "previewFragment").commit();
    }

    @Override
    public void switchEdit() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, editFragment, "editFragment").commit();
    }

    @Override
    public void exit() {
        String contentString = modifyData.content;
        if (EmojiFilter.containsEmptyEmoji(this, contentString)) {
            return;
        }

        RequestData request = mParam.getSendCommentParam(contentString);
        postNetwork(request.url, request.params, HOST_SEND_COMMENT);
        showProgressBar(true, "发送中");
    }

    @Override
    public String getProjectPath() {
        return mParam.getProjectPath();
    }

    @Override
    public boolean isProjectPublic() {
        return mParam.isPublicProject();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(HOST_SEND_COMMENT)) {
            showProgressBar(false);
            if (code == 0) {
                JSONObject jsonData = respanse.getJSONObject("data");
                if (!jsonData.optString("noteable_id").isEmpty()) {
                    Intent intent = new Intent();
                    intent.putExtra("data", jsonData.toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    BaseComment comment = new BaseComment(jsonData);
                    Intent intent = new Intent();
                    intent.putExtra("data", comment);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {
                showErrorMsg(code, respanse);
            }
        }
    }

    public static abstract class CommentParam implements Serializable {
        public abstract RequestData getSendCommentParam(String input);

        public abstract String getAtSome();

        public abstract String getAtSomeUrl();

        public abstract String getProjectPath();

        public abstract boolean isPublicProject();
    }
}
