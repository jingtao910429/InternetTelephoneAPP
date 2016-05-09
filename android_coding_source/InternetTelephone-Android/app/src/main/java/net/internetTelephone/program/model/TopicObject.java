package net.internetTelephone.program.model;

import android.text.TextUtils;

import com.loopj.android.http.RequestParams;

import net.internetTelephone.program.common.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by cc191954 on 14-8-18.
 * 讨论
 */
public class TopicObject extends BaseComment implements Serializable {
    public static final int SORT_OLD = 0;
    public static final int SORT_NEW = 1;
    public int child_count;
    public String current_user_role_id = "";
    public int parent_id;
    public ProjectObject project;
    public String project_id = "";
    public String title = "";
    public long updated_at;
    public ArrayList<TopicLabelObject> labels = new ArrayList<>();
    public int comment_count;
    private int number;
    private int commentSort = SORT_OLD;

    public TopicObject(JSONObject json) throws JSONException {
        super(json);

        child_count = json.optInt("child_count");
        current_user_role_id = json.optString("current_user_role_id");
        parent_id = json.optInt("parent_id");
        if (json.has("project")) {
            project = new ProjectObject(json.optJSONObject("project"));
        }
        project_id = json.optString("project_id");
        title = json.optString("title");
        updated_at = json.optLong("updated_at");
        number = json.optInt("number");
        comment_count = json.optInt("comment_count");

        {
            JSONArray array = json.optJSONArray("labels");
            for (int i = 0, n = array.length(); i < n; i++) {
                TopicLabelObject label = new TopicLabelObject(array.getJSONObject(i));
                if (!label.isEmpty()) {
                    labels.add(label);
                }
            }
        }
    }

    public String getRefId() {
        return "#" + number;
    }

    public String getHttpComments() {
        String urlCommentList = Global.HOST_API + "/topic/%d/comments?pageSize=20&type=%d";
        return String.format(urlCommentList, id, commentSort);
    }

    public void setSortOld(int sort) {
        commentSort = sort;
    }

    public interface LabelUrl {
        String getLabels();

        RequestData addLabel(String name, String color);

        String removeLabel(int labelId);

        RequestData renameLabel(int labelId, String name, String color);

        RequestData saveTopic(Collection<Integer> ids);
    }

    public static class TopicLabelUrl implements LabelUrl {
//        private static final String URI_GET_LABEL = "/api/user/%s/project/%s/topics/labels";
//        private static final String URI_ADD_LABEL = "/api/user/%s/project/%s/topics/label";
//        private static final String URI_REMOVE_LABEL = URI_ADD_LABEL + "/%s";
//        private static final String URI_RENAME_LABEL = URI_REMOVE_LABEL;
//        private static final String URI_SAVE_TOPIC_LABELS = "/api/user/%s/project/%s/topics/%s/labels";
//        private static final String COLOR = "#701035";

        private String projectPath;
        private int id;

        public TopicLabelUrl(String projectPath, int id) {
            this.projectPath = projectPath;
            this.id = id;
        }

        @Override
        public String getLabels() {
            return String.format("%s%s/topics/labels", Global.HOST_API, projectPath);
        }

        @Override
        public RequestData addLabel(String name, String color) {
            String url = String.format("%s%s/topics/label", Global.HOST_API, projectPath);
            RequestParams body = new RequestParams();
            body.put("name", name);
            body.put("color", color);
            return new RequestData(url, body);
        }

        @Override
        public String removeLabel(int labelId) {
            return String.format("%s%s/topics/label/%d", Global.HOST_API, projectPath, labelId);
        }

        @Override
        public RequestData renameLabel(int labelId, String name, String color) {
            String url = removeLabel(labelId);
            RequestParams body = new RequestParams();
            body.put("name", name);
            body.put("color", color);
            return new RequestData(url, body);
        }

        @Override
        public RequestData saveTopic(Collection<Integer> ids) {
            String url = String.format("%s%s/topics/%d/labels", Global.HOST_API, projectPath, id);
            RequestParams body = new RequestParams();
            body.put("label_id", TextUtils.join(",", ids));
            return new RequestData(url, body);
        }
    }

    public static class TaskLabelUrl implements LabelUrl {
        TopicLabelUrl topicLabelUrl;
        private String projectPath;
        private int id;

        public TaskLabelUrl(String projectPath, int id) {
            topicLabelUrl = new TopicLabelUrl(projectPath, id);
            this.projectPath = projectPath;
            this.id = id;
        }

        @Override
        public String getLabels() {
            return topicLabelUrl.getLabels();
        }

        @Override
        public RequestData addLabel(String name, String color) {
            return topicLabelUrl.addLabel(name, color);
        }

        @Override
        public String removeLabel(int labelId) {
            return topicLabelUrl.removeLabel(labelId);
        }

        @Override
        public RequestData renameLabel(int labelId, String name, String color) {
            return topicLabelUrl.renameLabel(labelId, name, color);
        }

        @Override
        public RequestData saveTopic(Collection<Integer> ids) {
            String url = String.format("%s%s/task/%d/labels", Global.HOST_API, projectPath, id);
            RequestParams body = new RequestParams();
            body.put("label_id", TextUtils.join(",", ids));
            return new RequestData(url, body);
        }
    }
}