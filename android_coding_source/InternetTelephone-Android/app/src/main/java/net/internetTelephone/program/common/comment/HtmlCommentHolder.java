package net.internetTelephone.program.common.comment;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.DialogCopy;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.HtmlContent;
import net.internetTelephone.program.common.ImageLoadTool;
import net.internetTelephone.program.common.LongClickLinkMovementMethod;
import net.internetTelephone.program.maopao.MaopaoListBaseFragment;
import net.internetTelephone.program.model.BaseComment;

/**
 * Created by chaochen on 14-10-27.
 */
public class HtmlCommentHolder extends BaseCommentHolder {

    protected TextView content;

    public HtmlCommentHolder(View convertView, View.OnClickListener onClickComment, Html.ImageGetter imageGetter, ImageLoadTool imageLoadTool, View.OnClickListener clickUser) {
        super(convertView, onClickComment, imageGetter, imageLoadTool, clickUser);

        content = (TextView) convertView.findViewById(R.id.content);
        content.setMovementMethod(LongClickLinkMovementMethod.getInstance());
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.performClick();
            }
        });

        content.setOnLongClickListener(DialogCopy.getInstance());
    }

    public void setContent(BaseComment comment) {
        super.setContent(comment);

        String contentString = comment.content;
        Global.MessageParse parse = HtmlContent.parseMessage(contentString);
        content.setText(Global.changeHyperlinkColor(parse.text, imageGetter, Global.tagHandler));
        content.setTag(comment);
        content.setTag(MaopaoListBaseFragment.TAG_COMMENT_TEXT, parse.text);
    }

}
