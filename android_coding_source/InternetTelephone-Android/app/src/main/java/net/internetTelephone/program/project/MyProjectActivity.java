package net.internetTelephone.program.project;

import android.support.v4.app.Fragment;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.ui.BackActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_my_project)
//@OptionsMenu(R.menu.menu_my_project)
public class MyProjectActivity extends BackActivity {

    @AfterViews
    protected final void init() {
        Fragment fragment = ProjectFragment_.builder().build();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
