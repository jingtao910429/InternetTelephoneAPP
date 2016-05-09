package net.internetTelephone.program.project.init.setting;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.internetTelephone.program.MyApp;
import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.model.ProjectObject;
import net.internetTelephone.program.model.TaskObject;
import net.internetTelephone.program.project.detail.MembersActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_project_transfer)
public class ProjectTransferActivity extends ProjectAdvanceSetBaseActivity {


    public static final int RESULT_REQUEST_SELECT_USER = 3;

    @Extra
    ProjectObject mProjectObject;

    @ViewById
    ImageView circleIcon;
    @ViewById
    TextView name;

    TaskObject.Members mUser;

    @Click
    public void linearlayout1() {
        MembersActivity_
                .intent(this)
                .mProjectObjectId(mProjectObject.getId())
                .startForResult(RESULT_REQUEST_SELECT_USER);
    }


    protected boolean checkInput() {
        if (mUser.user.isMe()) {
            showMiddleToast("转让的对象不能是自己");
            return false;
        }

        return true;
    }

    @AfterViews
    final void initProjectTransferActivity() {
        mUser = new TaskObject.Members(MyApp.sUserObject);
        updatePickMemberDisplay();
    }

    @Override
    void actionDelete2FA(String code) {
        showProgressBar(true);
        RequestParams params = new RequestParams();
        params.put("two_factor_code", code);
        putNetwork(mProjectObject.getHttpTransferProject(mUser.user.global_key), params,
                TAG_TRANSFER_PROJECT);
    }

    @OnActivityResult(RESULT_REQUEST_SELECT_USER)
    void onResultPickMember(int result, Intent intent) {
        if (result == RESULT_OK) {
            mUser = (TaskObject.Members) intent.getSerializableExtra("members");
            updatePickMemberDisplay();
        }
    }

    private void updatePickMemberDisplay() {
        name.setText(mUser.user.name);
        ImageLoader.getInstance().displayImage(Global.makeSmallUrlSquare(mUser.user.avatar,
                getResources().getDimensionPixelSize(R.dimen.task_add_user_icon_width)), circleIcon);
    }
}
