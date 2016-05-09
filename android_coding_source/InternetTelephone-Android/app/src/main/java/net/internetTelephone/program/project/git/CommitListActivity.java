package net.internetTelephone.program.project.git;

import android.view.View;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.BlankViewDisplay;
import net.internetTelephone.program.common.ClickSmallImage;
import net.internetTelephone.program.common.MyImageGetter;
import net.internetTelephone.program.common.comment.BaseCommentParam;
import net.internetTelephone.program.common.widget.RefreshBaseActivity;
import net.internetTelephone.program.model.Commit;
import net.internetTelephone.program.model.Merge;
import net.internetTelephone.program.project.detail.merge.CommitFileListActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;

@EActivity(R.layout.fragment_project_dynamic)
//@OptionsMenu(R.menu.menu_commit_list)
public class CommitListActivity extends RefreshBaseActivity {

    private static final String HOST_COMMITS = "HOST_COMMITS";

    @ViewById
    protected ExpandableStickyListHeadersListView listView;
    @ViewById
    protected View blankLayout;
    @Extra
    Merge mMerge;
    CommitsAdapter mAdapter;
    private View.OnClickListener mOnClickListItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Commit commit = (Commit) v.getTag();
            CommitFileListActivity_.intent(CommitListActivity.this).mCommit(commit).mProjectPath(mMerge.getProjectPath()).start();
        }
    };
    private View.OnClickListener onClickRetry = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRefresh();
        }
    };

    @AfterViews
    protected final void initCommitListActivity() {
        BaseCommentParam param = new BaseCommentParam(new ClickSmallImage(this), mOnClickListItem,
                new MyImageGetter(this), getImageLoad(), mOnClickUser);
        mAdapter = new CommitsAdapter(param);
        listView.setAdapter(mAdapter);

        initByNetwork();
    }

    private void initByNetwork() {
        getNetwork(mMerge.getHttpCommits(), HOST_COMMITS);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(HOST_COMMITS)) {
            setRefreshing(false);
            if (code == 0) {
                mAdapter.clearData();
                JSONArray jsonArray = respanse.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    Commit commit = new Commit(jsonArray.getJSONObject(i));
                    mAdapter.appendData(commit);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                showErrorMsg(code, respanse);
            }
            BlankViewDisplay.setBlank(mAdapter.getCount(), this, false, blankLayout, onClickRetry);
        }
    }

    @Override
    public void onRefresh() {
        initByNetwork();
    }
}
