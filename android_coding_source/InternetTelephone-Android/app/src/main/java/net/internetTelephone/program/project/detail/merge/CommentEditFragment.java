package net.internetTelephone.program.project.detail.merge;


import android.app.Activity;
import android.content.Intent;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.StartActivity;
import net.internetTelephone.program.common.TextWatcherAt;
import net.internetTelephone.program.common.enter.EnterLayout;
import net.internetTelephone.program.project.detail.TopicEditFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;

@EFragment(R.layout.fragment_comment_edit)
public class CommentEditFragment extends TopicEditFragment implements StartActivity {

    final int RESULT_REQUEST_AT = 1;

    @FragmentArg
    String mMergeUrl;
    private TextWatcherAt watcher;

    public boolean isEmpty() {
        return Global.isEmptyContainSpace(title);
    }

    @AfterViews
    protected final void initCommentEditFragment() {
        if (watcher == null) {
            watcher = new TextWatcherAt(getActivity(), this, RESULT_REQUEST_AT, mMergeUrl);
            edit.addTextChangedListener(watcher);
        }
    }

    @OnActivityResult(RESULT_REQUEST_AT)
    void onResultAt(int requestCode, Intent data) {
        if (requestCode == Activity.RESULT_OK) {
            String name = data.getStringExtra("name");
            EnterLayout.insertText(edit, name);
        }
    }
}
