package net.internetTelephone.program.common.base;

import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.network.RefreshBaseFragment;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;

/**
 * Created by chenchao on 15/3/9.
 */
@EFragment

public abstract class CustomMoreFragment extends RefreshBaseFragment {

    protected abstract String getLink();

    @OptionsItem
    protected void action_copy() {
        String link = getLink();
        Global.copy(getActivity(), link);
        showButtomToast("已复制链接 " + link);
    }

    public void search(String text) {}

}
