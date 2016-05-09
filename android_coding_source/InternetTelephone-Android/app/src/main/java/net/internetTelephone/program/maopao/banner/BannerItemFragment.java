package net.internetTelephone.program.maopao.banner;

import android.view.View;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import net.internetTelephone.program.R;
import net.internetTelephone.program.WebActivity_;
import net.internetTelephone.program.common.ui.BaseFragment;
import net.internetTelephone.program.common.umeng.UmengEvent;
import net.internetTelephone.program.model.BannerObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_banner_item)
public class BannerItemFragment extends BaseFragment {

    @ViewById
    ImageView image;
    @ViewById
    View itemLayout;

    @FragmentArg
    BannerObject data;

    @AfterViews
    protected final void initBannerItemFragment() {
        updateDisplay();
    }

    public void setData(BannerObject bannerObject) {
        data = bannerObject;
        updateDisplay();
    }

    private void updateDisplay() {
        iconfromNetwork(image, data.getImage());
    }

    @Click
    protected void itemLayout() {
        WebActivity_.intent(getActivity())
                .url(data.getLink())
                .start();
        MobclickAgent.onEvent(getActivity(), UmengEvent.MAOPAO, "点击banner");
    }
}
