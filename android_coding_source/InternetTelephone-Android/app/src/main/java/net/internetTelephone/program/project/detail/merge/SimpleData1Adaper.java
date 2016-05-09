package net.internetTelephone.program.project.detail.merge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.comment.BaseCommentHolder;
import net.internetTelephone.program.common.comment.BaseCommentParam;
import net.internetTelephone.program.common.widget.DataAdapter;
import net.internetTelephone.program.maopao.item.ImageCommentHolder;

/**
 * Created by chenchao on 15/5/29.
 */
public abstract class SimpleData1Adaper<T> extends DataAdapter {

    BaseCommentParam mCommentParam;

    public SimpleData1Adaper(BaseCommentParam param) {
        mCommentParam = param;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseCommentHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_task_comment_much_image, parent, false);
            holder = new ImageCommentHolder(convertView, mCommentParam);
            convertView.setTag(R.id.layout, holder);
        } else {
            holder = (BaseCommentHolder) convertView.getTag(R.id.layout);
        }

        T data = (T) getItem(position);
        holder.setContent(data);

        return convertView;
    }
}
