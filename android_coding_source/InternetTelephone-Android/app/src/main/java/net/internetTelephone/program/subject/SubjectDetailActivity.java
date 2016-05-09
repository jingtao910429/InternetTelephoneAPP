package net.internetTelephone.program.subject;

import android.support.v4.app.Fragment;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.ui.BackActivity;
import net.internetTelephone.program.model.Subject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Created by david on 15-7-25.
 */
@EActivity(R.layout.activity_subject_detail)
public class SubjectDetailActivity extends BackActivity {


    @Extra
    Subject.SubjectDescObject subjectDescObject;
    @Extra
    int topicId;


    @AfterViews
    protected final void initSubjectDetailActivity() {
        setTitle("话题详情");
        if (subjectDescObject != null || topicId > 0) {
            showSubjectDetailFragment();
        }
    }


    private void showSubjectDetailFragment() {
        Fragment fragment = SubjectDetailFragment_.builder()
                .subjectDescObject(subjectDescObject)
                .topicId(topicId)
                .build();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

}
