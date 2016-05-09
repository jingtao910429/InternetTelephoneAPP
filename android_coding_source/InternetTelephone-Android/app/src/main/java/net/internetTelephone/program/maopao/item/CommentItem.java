package net.internetTelephone.program.maopao.item;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.DialogCopy;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.HtmlContent;
import net.internetTelephone.program.common.LongClickLinkMovementMethod;
import net.internetTelephone.program.maopao.MaopaoListBaseFragment;
import net.internetTelephone.program.model.Maopao;

/**
 * Created by chaochen on 15/1/14.
 */
class CommentItem {

    private TextView comment;
    private TextView name;
    private TextView time;
    private View layout;

    public CommentItem(View convertView, View.OnClickListener onClickComment, int i) {
        layout = convertView;
        layout.setOnClickListener(onClickComment);
        name = (TextView) convertView.findViewById(R.id.name);
        time = (TextView) convertView.findViewById(R.id.time);
        comment = (TextView) convertView.findViewById(R.id.comment);
        comment.setMovementMethod(LongClickLinkMovementMethod.getInstance());
        comment.setOnClickListener(onClickComment);
        comment.setOnLongClickListener(DialogCopy.getInstance());
    }

    public void setContent(Maopao.Comment commentData, Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
        layout.setTag(MaopaoListBaseFragment.TAG_COMMENT, commentData);
        comment.setTag(MaopaoListBaseFragment.TAG_COMMENT, commentData);
        comment.setTag(MaopaoListBaseFragment.TAG_COMMENT_TEXT, commentData.content);

        name.setText(commentData.owner.name);
        time.setText(Global.dayToNow(commentData.created_at));
        Global.MessageParse parse = HtmlContent.parseMessage(commentData.content);
        comment.setText(Global.changeHyperlinkColor(parse.text, imageGetter, tagHandler));
    }

    public void setVisibility(int visibility) {
        layout.setVisibility(visibility);
    }
}
