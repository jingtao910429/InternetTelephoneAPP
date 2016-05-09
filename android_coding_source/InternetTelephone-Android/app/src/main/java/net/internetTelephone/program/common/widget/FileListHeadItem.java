package net.internetTelephone.program.common.widget;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.ImageLoadTool;
import net.internetTelephone.program.common.network.MyAsyncHttpClient;
import net.internetTelephone.program.model.AttachmentFileObject;
import net.internetTelephone.program.project.detail.UploadStyle;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by chenchao on 16/2/27.
 */
public class FileListHeadItem extends FrameLayout {

    View retryUpload;
    View stopUpload;
    ProgressBar progressBar;

    UploadStyle uploadStyle;
    Param postParam;
    private RequestHandle requestHandle;

    public FileListHeadItem(Context context) {
        super(context);

        inflate(context, R.layout.project_attachment_file_list_item_upload, this);
        retryUpload = findViewById(R.id.retryUpload);
        stopUpload = findViewById(R.id.stopUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        retryUpload.setVisibility(GONE);
        retryUpload.setOnClickListener(v -> upload());
        stopUpload.setOnClickListener(v -> stopUpload());
    }

    public void setData(Param param, UploadStyle uploadStyle, ImageLoadTool imageLoadTool) {
        postParam = param;
        this.uploadStyle = uploadStyle;

        String fileName = param.file.getName();

        String[] splitName = fileName.split("\\.");

        int iconId = R.drawable.ic_file_unknown;
        String suffix = splitName[splitName.length - 1];

        if (splitName.length > 1) {
            if (AttachmentFileObject.isImage(suffix)) {
                imageLoadTool.loadImage( ((ImageView) findViewById(R.id.icon)), Uri.fromFile(postParam.file).toString());
            } else {
                iconId = AttachmentFileObject.getIconResourceId(suffix);
                ((ImageView) findViewById(R.id.icon)).setImageResource(iconId);
            }
        } else {
            ((ImageView) findViewById(R.id.icon)).setImageResource(iconId);
        }

        ((TextView) findViewById(R.id.file_name)).setText(fileName);
        upload();
    }

    public void setError() {
        retryUpload.setVisibility(VISIBLE);
    }

    public void setProgress(int progress) { // max = 100
        progressBar.setProgress(progress);
    }

    private void upload() {
        retryUpload.setVisibility(GONE);

        AsyncHttpClient client = MyAsyncHttpClient.createClient(getContext().getApplicationContext());
        RequestParams params = new RequestParams();
        params.put("dir", postParam.dirId);
        try {
            params.put("file", postParam.file);
        } catch (Exception e) {
            Global.errorLog(e);
        }

        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int code = response.optInt("code");
                if (code == 0) {
                    ((ViewGroup) getParent()).removeView(FileListHeadItem.this);
                }
                uploadStyle.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                setError();
                uploadStyle.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                setProgress((int) (bytesWritten * 1.0 / totalSize * 100));
            }
        };
        requestHandle = client.post(getContext().getApplicationContext(), postParam.url, params, jsonHttpResponseHandler);


    }

    private void stopUpload() {
        if (requestHandle != null) {
            AsyncTask.execute(() -> requestHandle.cancel(true));
            ((ViewGroup) getParent()).removeView(FileListHeadItem.this);
        }
    }

    public static class Param {
        String url;
        File file;
        String dirId;

        public Param(String url, String dirId, File file) {
            this.url = url;
            this.dirId = dirId;
            this.file = file;
        }
    }
}
