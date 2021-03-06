package net.internetTelephone.program.maopao;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.ImageLoadTool;
import net.internetTelephone.program.model.Maopao;

import java.util.ArrayList;

public class LikeUsersArea {
    Fragment fragment;
    Activity activity;
    View likeUsersAllLayout;
    public LinearLayout likeUsersLayout;

    ImageLoadTool imageLoadTool;
    View.OnClickListener mOnClickUser;

    public LikeUsersArea(View convertView, Fragment fragment, ImageLoadTool imageLoadTool, View.OnClickListener mOnClickUser) {
        this(convertView, fragment, null, imageLoadTool, mOnClickUser);
    }

    public LikeUsersArea(View convertView, Activity activity, ImageLoadTool imageLoadTool, View.OnClickListener mOnClickUser) {
        this(convertView, null, activity, imageLoadTool, mOnClickUser);
    }

    private LikeUsersArea(View convertView, Fragment fragment, Activity activity, ImageLoadTool imageLoadTool, View.OnClickListener mOnClickUser) {
        this.fragment = fragment;
        this.activity = activity;
        this.imageLoadTool = imageLoadTool;
        this.mOnClickUser = mOnClickUser;
        likeUsersAllLayout = convertView.findViewById(R.id.likesAllLayout);
        likeUsersLayout = (LinearLayout) convertView.findViewById(R.id.likeUsersLayout);
        likeUsersLayout.getViewTreeObserver().addOnPreDrawListener(new MyPreDraw(likeUsersAllLayout, likeUsersLayout));
    }

    private Activity getActivity() {
        if (activity != null) {
            return activity;
        } else {
            return fragment.getActivity();
        }
    }

    private void startActivity(Intent intent) {
        if (activity != null) {
            activity.startActivity(intent);
        } else {
            fragment.startActivity(intent);
        }
    }

    private class MyPreDraw implements ViewTreeObserver.OnPreDrawListener {

        private LinearLayout layout;
        private View allLayout;

        public MyPreDraw(View allLayout, LinearLayout linearLayout) {
            layout = linearLayout;
            this.allLayout = allLayout;
        }

        @Override
        public boolean onPreDraw() {
            int width = layout.getWidth();

            if (width <= 0) {
                return true;
            }

            if (layout.getChildCount() > 0) {
                layout.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }

            width -= (layout.getPaddingLeft() + layout.getPaddingRight());

            int imageWidth = Global.dpToPx(30);
            int imageMargin = Global.dpToPx(5);

            int shenxia = width % (imageWidth + imageMargin);
            int count = width / (imageWidth + imageMargin);
            imageMargin += shenxia / count;
            imageMargin /= 2;

            final int MAX_DISPLAY_USERS = 10;
            if (count > MAX_DISPLAY_USERS) {
                count = MAX_DISPLAY_USERS;
            }

            for (int i = 0; i < count; ++i) {
                LikeUserImage view = new LikeUserImage(getActivity());
                layout.addView(view);

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                lp.width = imageWidth;
                lp.height = imageWidth;
                lp.leftMargin = imageMargin;
                lp.rightMargin = imageMargin;
                view.setLayoutParams(lp);
                view.setOnClickListener(mOnClickUser);
            }

            TextView textView = new TextView(getActivity());
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) textView.getLayoutParams();
            lp.width = imageWidth;
            lp.height = imageWidth;
            lp.leftMargin = imageMargin;
            lp.rightMargin = imageMargin;
            textView.setBackgroundResource(R.drawable.ic_bg_good_count);
            textView.setTextColor(0xffffffff);
            textView.setVisibility(View.GONE);
            textView.setOnClickListener(onClickLikeUsrs);

            displayLikeUser();

            return true;
        }
    }

    public void displayLikeUser() {
        Maopao.MaopaoObject maopaoData = (Maopao.MaopaoObject) likeUsersLayout.getTag(MaopaoListBaseFragment.TAG_MAOPAO);

        if ((maopaoData.likes + maopaoData.rewards) == 0) {
            likeUsersAllLayout.setVisibility(View.GONE);
        } else {
            likeUsersAllLayout.setVisibility(View.VISIBLE);
        }

        if (likeUsersLayout.getChildCount() == 0) {
            likeUsersLayout.setTag(maopaoData);
            return;
        }

        int readUserCount = maopaoData.likes + maopaoData.rewards;
        final ArrayList<Maopao.Like_user> displayUsers = new ArrayList<>(maopaoData.reward_users);
        for (Maopao.Like_user like : maopaoData.like_users) {
            boolean find = false;
            for (Maopao.Like_user reward : displayUsers) {
                if (like.global_key.equals(reward.global_key)) {
                    find = true;
                    break;
                }
            }

            if (!find) {
                displayUsers.add(like);
            }
        }

        int imageCount = likeUsersLayout.getChildCount() - 1;

        Log.d("", "ddd disgood " + imageCount + "," + displayUsers.size() + "," + readUserCount);

        likeUsersLayout.getChildAt(imageCount).setTag(maopaoData.id);

        if (displayUsers.size() < imageCount) {
            if (readUserCount <= imageCount) {
                int i = 0;
                for (; i < displayUsers.size(); ++i) {
                    updateImageDisplay(displayUsers, i);
                }

                for (; i < imageCount; ++i) {
                    likeUsersLayout.getChildAt(i).setVisibility(View.GONE);
                }

                likeUsersLayout.getChildAt(i).setVisibility(View.GONE);

            } else {
                int i = 0;
                for (; i < displayUsers.size(); ++i) {
                    updateImageDisplay(displayUsers, i);
                }

                for (; i < imageCount; ++i) {
                    likeUsersLayout.getChildAt(i).setVisibility(View.GONE);
                }

                TextView textV = (TextView) likeUsersLayout.getChildAt(imageCount);
                textV.setVisibility(View.VISIBLE);
                textV.setText(readUserCount + "");
            }

        } else {
            --imageCount;
            for (int i = 0; i < imageCount; ++i) {
                updateImageDisplay(displayUsers, i);
            }

            likeUsersLayout.getChildAt(imageCount).setVisibility(View.GONE);
            TextView textView = (TextView) likeUsersLayout.getChildAt(imageCount + 1);
            textView.setVisibility(View.VISIBLE);
            textView.setText(readUserCount + "");
        }

        imageCount = likeUsersLayout.getChildCount() - 1;
        for (int i = 0; i < imageCount; ++i) {
            View v = likeUsersLayout.getChildAt(i);
            if (v.getVisibility() == View.VISIBLE) {
                v.setTag(displayUsers.get(i).global_key);
            } else {
                break;
            }
        }
    }

    private void updateImageDisplay(ArrayList<Maopao.Like_user> likeUsers, int i) {
        ImageView image = (ImageView) likeUsersLayout.getChildAt(i);
        image.setVisibility(View.VISIBLE);

        Maopao.Like_user like_user = likeUsers.get(i);
        image.setTag(LikeUserImage.TAG, like_user);
        imageLoadTool.loadImage(image, like_user.avatar);
    }

    View.OnClickListener onClickLikeUsrs = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), LikeUsersListActivity_.class);
            intent.putExtra("id", (int) v.getTag());
            startActivity(intent);
        }
    };

}