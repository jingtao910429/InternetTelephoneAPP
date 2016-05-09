package net.internetTelephone.program.project.detail;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.internetTelephone.program.FootUpdate;
import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.base.MyJsonResponse;
import net.internetTelephone.program.common.network.MyAsyncHttpClient;
import net.internetTelephone.program.common.ui.BackActivity;
import net.internetTelephone.program.model.TaskObject;
import net.internetTelephone.program.model.UserObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@EActivity(R.layout.activity_members)
public class MembersActivity extends BackActivity implements FootUpdate.LoadMore {

    @Extra
    int mProjectObjectId;

    @Extra
    ArrayList<UserObject> mWatchUsers = new ArrayList<>(); // 任务的关注者

    @Extra
    int mTaskId = 0; // 任务的 id 号，0 表示新建任务

    @Extra
    boolean mPickWatch = false;

    String getProjectMembers = "getProjectMembers";
    String urlMembers = "";

    ArrayList<TaskObject.Members> mMembersArray = new ArrayList<>();
    @ViewById
    ListView listView;
    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mMembersArray.size();
        }

        @Override
        public Object getItem(int position) {
            return mMembersArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_members_list_item, parent, false);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.watchCheck = convertView.findViewById(R.id.watchCheck);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TaskObject.Members data = mMembersArray.get(position);
            holder.name.setText(data.user.name);
            iconfromNetwork(holder.icon, data.user.avatar);

            updateChecked(holder, data);

            if (position == mMembersArray.size() - 1) {
                loadMore();
            }

            return convertView;
        }

        private void updateChecked(ViewHolder holder, TaskObject.Members data) {
            if (!mPickWatch) {
                return;
            }

            for (UserObject item : mWatchUsers) {
                if (data.user.id == item.id) {
                    holder.watchCheck.setVisibility(View.VISIBLE);
                    return;
                }
            }

            holder.watchCheck.setVisibility(View.INVISIBLE);
        }
    };

    @AfterViews
    protected final void initMembersActivity() {
        if (mPickWatch) {
            setActionBarTitle("关注者列表");
        }

        final String format = Global.HOST_API + "/project/%d/members?";
        urlMembers = String.format(format, mProjectObjectId);

        mFootUpdate.init(listView, mInflater, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (!mPickWatch) {
                Intent intent = new Intent();
                TaskObject.Members members = mMembersArray.get(position);
                intent.putExtra("members", members);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                TaskObject.Members members = mMembersArray.get(position);
                for (int i = 0; i < mWatchUsers.size(); ++i) {
                    UserObject item = mWatchUsers.get(i);
                    if (members.user.id == item.id) {
                        UserObject deleteUser = mWatchUsers.remove(i);
                        removeWatchUser(deleteUser);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }

                addWatchUser(members.user);
                mWatchUsers.add(members.user);
                adapter.notifyDataSetChanged();
            }
        });

        loadMore();
    }

    private void removeWatchUser(UserObject user) {
        if (mTaskId == 0) {
            return;
        }

        String url = String.format(Global.HOST_API + "/task/%d/user/%s/watch", mTaskId, user.global_key);
        MyAsyncHttpClient.delete(this, url, new MyJsonResponse(this) {
            @Override
            public void onMySuccess(JSONObject response) {
                super.onMySuccess(response);
            }

            @Override
            public void onMyFailure(JSONObject response) {
                super.onMyFailure(response);
                mWatchUsers.add(user);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addWatchUser(UserObject user) {
        if (mTaskId == 0) {
            return;
        }

        String url = String.format(Global.HOST_API + "/task/%d/user/%s/watch", mTaskId, user.global_key);
        MyAsyncHttpClient.post(this, url, new MyJsonResponse(this) {
            @Override
            public void onMySuccess(JSONObject response) {
                super.onMySuccess(response);
            }

            @Override
            public void onMyFailure(JSONObject response) {
                super.onMyFailure(response);
                mWatchUsers.remove(user);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mPickWatch) {
            Intent intent = new Intent();
            intent.putExtra("resultData", mWatchUsers);
            setResult(Activity.RESULT_OK, intent);
        }

        super.onBackPressed();
    }

    @Override
    public void loadMore() {
        getNextPageNetwork(urlMembers, getProjectMembers);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(getProjectMembers)) {
            if (code == 0) {
                ArrayList<TaskObject.Members> usersInfo = new ArrayList<>();

                JSONArray members = respanse.getJSONObject("data").getJSONArray("list");

                for (int i = 0; i < members.length(); ++i) {
                    mMembersArray.add(new TaskObject.Members(members.getJSONObject(i)));
                }

                adapter.notifyDataSetChanged();
            } else {
                showErrorMsg(code, respanse);
            }
        }
    }

    @OptionsItem
    public void action_add() {

    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
        View watchCheck;
    }
}
