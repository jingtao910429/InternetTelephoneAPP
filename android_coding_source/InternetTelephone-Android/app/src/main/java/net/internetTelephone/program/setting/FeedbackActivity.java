package net.internetTelephone.program.setting;

import net.internetTelephone.program.R;
import net.internetTelephone.program.project.detail.TopicAddActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_topic_add)
public class FeedbackActivity extends TopicAddActivity {

    @AfterViews
    protected void init2() {
        setActionBarTitle(R.string.title_activity_feedback);
    }

    @Override
    public boolean canShowLabels() {
        return false;
    }

    @Override
    protected int getTopicId() {
        return 39583;
    }

    @Override
    public String getProjectPath() {
        return "/user/coding/project/InternetTelephone-Android/";
    }

    @Override
    public boolean isProjectPublic() {
        return true;
    }

    @Override
    protected void showSuccess() {
        showButtomToast("反馈成功");
    }

    @Override
    protected String getSendingTip() {
        return "正在发表反馈...";
    }
}
