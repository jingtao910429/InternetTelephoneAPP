package net.internetTelephone.program.search;

import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.adapter.SearchFileAdapter;
import net.internetTelephone.program.common.network.RefreshBaseFragment;
import net.internetTelephone.program.model.AttachmentFileObject;
import net.internetTelephone.program.project.detail.AttachmentsDetailBaseActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vernon on 15/11/30.
 */
@EFragment(R.layout.fragment_search_list)
public class SearchFileFragment extends RefreshBaseFragment {

    private static final String TAG = SearchTaskFragment.class.getSimpleName();
    final String url = Global.HOST_API + "/esearch/all?q=%s";
    final String tmp = "&types=%s&pageSize=10";
    ArrayList<AttachmentFileObject> mData = new ArrayList<>();
    String page = "&page=%s";
    int pos = 1;
    private String keyword = "";
    private String tabPrams;
    private boolean hasMore = true;
    private boolean isLoading = true;
    @ViewById
    ListView listView;
    @ViewById(R.id.emptyView)
    LinearLayout emptyView;

    SearchFileAdapter adapter;

    @AfterViews
    protected void init() {
        initRefreshLayout();
        setRefreshing(true);
        mFootUpdate.init(listView, mInflater, this);
        adapter = new SearchFileAdapter(mData, keyword, getActivity());
        listView.setOnScrollListener(mOnScrollListener);
        listView.setAdapter(adapter);
        loadMore();
    }

    @ItemClick
    final void listView(AttachmentFileObject itemData) {
        AttachmentsDetailBaseActivity_.intent(this).mAttachmentFileObject(itemData).start();
    }

    AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount == totalItemCount) {
                if (hasMore && !isLoading) {
                    isLoading = true;
                    pos++;
                    loadMore();
                }
            }
        }
    };

    public String getKeyword() {
        return keyword;
    }

    public String getTabPrams() {
        return tabPrams;
    }

    public void setTabPrams(String tabPrams) {
        this.tabPrams = tabPrams;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    private String getUrl(int pos) {
        String tag = "";
        tag = String.format(url, getKeyword()) + String.format(tmp, getTabPrams()) + String.format(page, pos + "");
        return tag;
    }


    @Override
    public void loadMore() {
        getNetwork(getUrl(pos), keyword);
    }

    @Override
    public void onRefresh() {
        pos = 1;
        loadMore();
    }


    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(keyword)) {
            setRefreshing(false);
            if (code == 0) {
                if (pos == 1) {
                    mData.clear();
                }
                JSONArray array = respanse.getJSONObject("data").getJSONObject("files").getJSONArray("list");
                for (int i = 0; i < array.length(); ++i) {
                    JSONObject item = array.getJSONObject(i);
                    AttachmentFileObject oneData = new AttachmentFileObject(item);
                    mData.add(oneData);
                }
                emptyView.setVisibility(mData.size() == 0 ? View.VISIBLE : View.GONE);
                if (array.length() > 0) {
                    mFootUpdate.updateState(code, false, mData.size());
                    hasMore = true;
                } else {
                    hasMore = false;
                    mFootUpdate.updateState(code, true, mData.size());
                }
                adapter.notifyDataSetChanged();
                isLoading = false;
            } else {
                showErrorMsg(code, respanse);
                hasMore = false;
            }
        }
    }
}

