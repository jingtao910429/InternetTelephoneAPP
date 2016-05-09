package net.internetTelephone.program.maopao;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.model.Maopao;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chenchao on 15/12/2.
 */
public class LikeUserImage extends CircleImageView {

    public static final int TAG = R.id.likeUsersLayout;

    private int mWidth;

    public LikeUserImage(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public LikeUserImage(Context context) {
        super(context);

        init();
    }

    private void init() {
        setBorderColor(0xFFFFAE03);
        setBorderWidth(0);

        mWidth = Global.dpToPx(1);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        Object o = getTag(TAG);
        if (o instanceof Maopao.Like_user) {
            if (((Maopao.Like_user) o).type == Maopao.Like_user.Type.Like) {
                setBorderWidth(0);
            } else {
                setBorderWidth(mWidth);
            }
        }

        super.setImageDrawable(drawable);
    }
}
