package net.internetTelephone.program.project.git;


import android.support.v4.app.Fragment;
import android.widget.EditText;

import net.internetTelephone.program.R;
import net.internetTelephone.program.model.GitFileObject;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


@EFragment(R.layout.fragment_edit_code)
public class EditCodeFragment extends Fragment {

    @ViewById
    EditText editText;

    @Override
    public void onResume() {
        super.onResume();

        GitFileObject file = ((EditCodeActivity) getActivity()).getFile().getGitFileObject();
        editText.setText(file.data);
    }
    public String getInput() {
        return editText.getText().toString();
    }
}
