package net.internetTelephone.program.common.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.ImageLoadTool;
import net.internetTelephone.program.model.TaskObject;
import net.internetTelephone.program.search.HoloUtils;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Vernon on 15/11/26.
 */
public class SearchReslutAdapter extends BaseAdapter {
    private List<TaskObject.SingleTask> mData;
    private Context context;
    private String key;


    public SearchReslutAdapter(List<TaskObject.SingleTask> mData, Context context, String key) {
        this.mData = mData;
        this.context = context;
        this.key = key;

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.search_task_list, null);
            holder = new ViewHolder();
            holder.nameTask = (TextView) convertView.findViewById(R.id.nameTask);
            holder.iconTask = (ImageView) convertView.findViewById(R.id.iconTask);
            holder.descTask = (TextView) convertView.findViewById(R.id.descTask);
            holder.bottomName = (TextView) convertView.findViewById(R.id.bottomName);
            holder.bottomTime = (TextView) convertView.findViewById(R.id.bottomTime);
            holder.bottomHeartCount = (TextView) convertView.findViewById(R.id.bottomHeartCount);
            holder.bottomCommentCount = (TextView) convertView.findViewById(R.id.bottomCommentCount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TaskObject.SingleTask bean = mData.get(position);
        HoloUtils.setHoloText(holder.nameTask, key, bean.creator.name);
        HoloUtils.setHoloText(holder.descTask, key, bean.description);
        holder.bottomName.setText(bean.owner.name);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        holder.bottomTime.setText(format.format(bean.created_at));
        holder.bottomCommentCount.setText(bean.comments + "");
        holder.bottomHeartCount.setText("");
        ImageLoader.getInstance().displayImage(bean.creator.avatar, holder.iconTask, ImageLoadTool.optionsImage);
        return convertView;
    }


    static class ViewHolder {
        TextView nameTask;
        TextView descTask;
        TextView bottomName;
        TextView bottomTime;
        TextView bottomCommentCount;
        TextView bottomHeartCount;
        ImageView iconTask;
    }

}
