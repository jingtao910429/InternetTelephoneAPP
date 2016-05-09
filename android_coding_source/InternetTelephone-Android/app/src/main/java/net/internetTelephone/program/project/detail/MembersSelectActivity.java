package net.internetTelephone.program.project.detail;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.base.CustomMoreFragment;
import net.internetTelephone.program.common.ui.BackActivity;
import net.internetTelephone.program.model.Merge;
import net.internetTelephone.program.model.ProjectObject;
import net.internetTelephone.program.project.detail.merge.MergeReviewerListFragment;
import net.internetTelephone.program.project.detail.merge.MergeReviewerListFragment_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

@EActivity(R.layout.activity_members_select)
public class MembersSelectActivity extends BackActivity {

    @Extra
    ProjectObject mProjectObject;

    @Extra
    String mMergeUrl;

    @Extra
    Merge mMerge;  // 写 Reviewer 功能加上

    @Extra
    boolean mSelect = false;

    CustomMoreFragment fragment;

    @AfterViews
    void init() {
        if (mProjectObject != null && mMerge == null) {
            fragment = new MembersListFragment_
                    .FragmentBuilder_()
                    .mProjectObject(mProjectObject)
                    .mSelect(true)
                    .build();
        } else if (mMergeUrl != null) {
            setActionBarTitle("选择@对象");
            fragment = new MembersListFragment_
                    .FragmentBuilder_()
                    .mMergeUrl(mMergeUrl)
                    .mSelect(true)
                    .build();
        } else if (mMerge != null) {
            fragment = new MergeReviewerListFragment_
                    .FragmentBuilder_()
                    .mMerge(mMerge)
                    .mSelect(mSelect)
                    .build();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.users_fans, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setIcon(R.drawable.ic_menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        try { // 更改搜索按钮的icon
            int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
            ImageView v = (ImageView) searchView.findViewById(searchImgId);
            v.setImageResource(R.drawable.ic_menu_search);
        } catch (Exception e) {
            Global.errorLog(e);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                fragment.search(s);
                return true;
            }
        });


        return true;
    }
}
