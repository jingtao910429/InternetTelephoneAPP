package net.internetTelephone.program.model.util;

import com.loopj.android.http.RequestParams;

import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.util.FileUtil;
import net.internetTelephone.program.model.AttachmentFileHistoryObject;
import net.internetTelephone.program.model.AttachmentFileObject;
import net.internetTelephone.program.model.RequestData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by chenchao on 15/8/19.
 * file history 网络请求类
 */
public class FileRequestHelp {

    String mProjectPath = "";
    int mFileId = 0;

    public FileRequestHelp(String mProjectPath, int mFileId) {
        this.mProjectPath = mProjectPath;
        this.mFileId = mFileId;
    }

    public String getHttpRequest() {
        return String.format(Global.HOST_API + mProjectPath + "/files/%d/histories", mFileId);
    }

    public ArrayList<AttachmentFileHistoryObject> parseJson(JSONObject json, String downloadPath, int projectId) {
        JSONArray jsonArray = json.optJSONArray("data");

        ArrayList<AttachmentFileHistoryObject> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            AttachmentFileHistoryObject item = new AttachmentFileHistoryObject(jsonArray.optJSONObject(i));
            setDownloadStatus(item, downloadPath, projectId);
            list.add(item);
        }

        return list;
    }

    private void setDownloadStatus(AttachmentFileObject mFileObject, String downloadPath, int projectId) {
        File mFile = FileUtil.getDestinationInExternalPublicDir(downloadPath, mFileObject.getSaveName(projectId));
        if (mFile.exists() && mFile.isFile()) {
            mFileObject.isDownload = true;
        } else {
            mFileObject.downloadId = 0L;
            mFileObject.isDownload = false;
        }
    }

    public RequestData getHttpHistoryRemark(int historyId, String input) {
        String url = String.format(Global.HOST_API + mProjectPath + "/files/%s/histories/%s/remark",
                mFileId, historyId);
        RequestParams paraams = new RequestParams();
        paraams.put("remark", input);
        return new RequestData(url, paraams);
    }

    public String getHttpHistoryDelete(int historyId) {
        return String.format(Global.HOST_API + mProjectPath + "/files/histories/%s",
                historyId);
    }
}
