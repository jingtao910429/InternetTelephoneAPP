package net.internetTelephone.program.common.comment;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.ImageLoadTool;
import net.internetTelephone.program.model.BaseComment;
import net.internetTelephone.program.model.Commit;
import net.internetTelephone.program.model.DynamicObject;

/**
 * Created by chaochen on 14-10-27.
 */
public class BaseCommentHolder {

    protected ImageView icon;
    protected TextView name;
    protected TextView time;
    protected View layout;
    protected Html.ImageGetter imageGetter;
    protected ImageLoadTool imageLoadTool;
    protected String globalKey = "";

    public BaseCommentHolder(View convertView, View.OnClickListener onClickComment, Html.ImageGetter imageGetter, ImageLoadTool imageLoadTool, View.OnClickListener clickUser) {
        layout = convertView.findViewById(R.id.Commentlayout);
        layout.setOnClickListener(onClickComment);

        icon = (ImageView) convertView.findViewById(R.id.icon);
        icon.setOnClickListener(clickUser);
        name = (TextView) convertView.findViewById(R.id.name);
        time = (TextView) convertView.findViewById(R.id.time);

        this.imageLoadTool = imageLoadTool;
        this.imageGetter = imageGetter;
    }

    public BaseCommentHolder(View convertView, BaseCommentParam param) {
        this(convertView, param.onClickComment, param.imageGetter, param.imageLoadTool, param.clickUser);
    }

    public void setContent(Object param) {
        if (param instanceof BaseComment) {
            BaseComment comment = (BaseComment) param;

            String nameString = comment.owner.name;
            long timeParam = comment.created_at;
            String iconUri = comment.owner.avatar;

            imageLoadTool.loadImage(icon, iconUri);
            icon.setTag(comment.owner.global_key);
            name.setText(nameString);
            time.setText(Global.dayToNow(timeParam));
            layout.setTag(comment);

        } else if (param instanceof Commit) {
            Commit commit = (Commit) param;
            String nameString = commit.getName();
            long timeParam = commit.getCommitTime();
            String iconUri = commit.getIcon();


            imageLoadTool.loadImage(icon, iconUri);
            icon.setTag(commit.getGlobalKey());
            name.setText(nameString);
            time.setText(Global.dayToNow(timeParam));
            layout.setTag(commit);
        } else if (param instanceof DynamicObject.DynamicMergeRequest) {
            DynamicObject.User user = ((DynamicObject.DynamicMergeRequest) param).user;
            imageLoadTool.loadImage(icon, user.avatar);
            name.setText(user.getName());
            time.setText(Global.dayToNow(((DynamicObject.DynamicMergeRequest) param).created_at));
            layout.setTag(param);
        }
    }

}
