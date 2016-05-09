package net.internetTelephone.program.search;

import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import net.internetTelephone.program.R;
import net.internetTelephone.program.common.Global;
import net.internetTelephone.program.common.MyImageGetter;
import net.internetTelephone.program.common.adapter.SearchMaopaoAdapter;
import net.internetTelephone.program.common.network.RefreshBaseFragment;
import net.internetTelephone.program.model.Maopao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Vernon on 15/11/28.
 */
@EFragment(R.layout.fragment_search_list)
public class SearchMaopaoFragment extends RefreshBaseFragment {
    final String url = Global.HOST_API + "/esearch/all?q=%s";
    final String tmp = "&types=%s&pageSize=10";
    String page = "&page=%s";
    int pos = 1;
    ArrayList<Maopao.MaopaoObject> mData = new ArrayList<>();
    private String keyword = "";
    private String tabPrams;
    private boolean hasMore = true;
    private boolean isLoading = true;
    private LinearLayout emptyView;
    private MyImageGetter myImageGetter;

    @ViewById
    ListView listView;


    SearchMaopaoAdapter adapter;

    @AfterViews
    protected void init() {
        initRefreshLayout();
        myImageGetter = new MyImageGetter(getActivity());
        setRefreshing(true);
        emptyView = (LinearLayout) getView().findViewById(R.id.emptyView);
        mFootUpdate.init(listView, mInflater, this);

        adapter = new SearchMaopaoAdapter(mData, myImageGetter, getActivity());
        listView.setAdapter(adapter);
        listView.setOnScrollListener(mOnScrollListener);
        loadMore();
    }

    AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount == totalItemCount) {
                if (hasMore && !isLoading) {
                    pos++;
                    isLoading = true;
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
                JSONArray array = respanse.getJSONObject("data").getJSONObject("tweets").getJSONArray("list");
                for (int i = 0; i < array.length(); ++i) {
                    JSONObject item = array.getJSONObject(i);
                    Maopao.MaopaoObject oneData = new Maopao.MaopaoObject(item);
                    mData.add(oneData);
                }
                emptyView.setVisibility(mData.size() == 0 ? View.VISIBLE : View.GONE);
                if (array.length() > 0) {
                    hasMore = true;
                    mFootUpdate.updateState(code, false, mData.size());
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
