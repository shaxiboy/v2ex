package com.hjx.v2ex.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.FavoriteNodes;
import com.hjx.v2ex.bean.HomePage;
import com.hjx.v2ex.bean.Node;
import com.hjx.v2ex.bean.NodesHottest;
import com.hjx.v2ex.bean.NodesNavigation;
import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.flexibleitem.NodeCategoryFlexibleHeaderItem;
import com.hjx.v2ex.flexibleitem.NodeFlexibleItem;
import com.hjx.v2ex.bean.NodesAll;
import com.hjx.v2ex.flexibleitem.ViewMoreFlexibleItem;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.data;

/**
 * Created by shaxiboy on 2017/4/12 0012.
 */

public class NodeListFragment extends ListBaseFragment implements FlexibleAdapter.OnItemClickListener{

    public static final String ARG_TAB = "TAB";
    public static final String ARG_NODEACTIONTYPE = "NODEACTIONTYPE";
    public static final String NODEACTIONTYPE_VIEW = "NODEACTIONTYPE_VIEW";
    public static final String NODEACTIONTYPE_CHOOSE = "NODEACTIONTYPE_CHOOSE";

    private String tab;
    private String nodeActionType;
    private List<AbstractFlexibleItem> nodes = new ArrayList<>();

    public static NodeListFragment newInstance(String tab, String nodeActionType) {
        NodeListFragment nodeListFragment = new NodeListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TAB, tab);
        bundle.putString(ARG_NODEACTIONTYPE, nodeActionType);
        nodeListFragment.setArguments(bundle);
        return nodeListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tab = getArguments().getString(ARG_TAB);
        nodeActionType = getArguments().getString(ARG_NODEACTIONTYPE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListAdapter().addListener(this);
    }

    @Override
    ListData getListData(Object data) {
        ListData listData = new ListData();
        if(getCurrentPage() == 1) {
            nodes.clear();
        }
        if(data instanceof NodesHottest || data instanceof FavoriteNodes) {
            List<Node> nodeList = null;
            if(data instanceof NodesHottest) {
                nodeList = ((NodesHottest) data).getHottestNodes();
            } else if(data instanceof FavoriteNodes) {
                nodeList = ((FavoriteNodes) data).getFavoriteNodes();
            }
            for(Node node : nodeList) {
                nodes.add(new NodeFlexibleItem(node, null));
            }
            if(data instanceof FavoriteNodes && !nodeList.isEmpty()) {
                nodes.add(new ViewMoreFlexibleItem(null, ViewMoreFlexibleItem.ViewMoreType.NODESTOPICS));
            }
        } else if(data instanceof NodesNavigation || data instanceof NodesAll) {
            Map<String, List<Node>> nodeSections = null;
            if(data instanceof NodesNavigation) {
                nodeSections = ((NodesNavigation) data).getNodeSections();
            } else if(data instanceof NodesAll) {
                nodeSections = ((NodesAll) data).getNodeSections();
            }
            for (String categoryName : nodeSections.keySet()) {
                NodeCategoryFlexibleHeaderItem category = new NodeCategoryFlexibleHeaderItem(categoryName);
                for (Node node : nodeSections.get(categoryName)) {
                    category.addSubItem(new NodeFlexibleItem(node, category));
                }
                nodes.add(category);
            }
        }
        listData.setPageData(getOnePageData(nodes));
        return listData;
    }

    @Override
    protected void loadData() {
        loadNodes();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_nodes, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_nodes_search);

        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("搜索节点");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (getListAdapter().hasNewSearchText(newText)) {
                    getListAdapter().setSearchText(newText);
                    getListAdapter().filterItems(new ArrayList(nodes), 200);
                }
                return true;
            }
        });
    }

    private void loadNodes() {
        if (tab.equals("最热")) {
            RetrofitSingleton.getInstance(getContext()).getHottestNodes().enqueue(getListBaseFragmentCallBack());
        } else if (tab.equals("导航")) {
            RetrofitSingleton.getInstance(getContext()).getNavigationNodes().enqueue(getListBaseFragmentCallBack());
        } else if (tab.equals("全部")) {
            RetrofitSingleton.getInstance(getContext()).getAllNodes().enqueue(getListBaseFragmentCallBack());
        } else if(tab.equals("收藏")) {
            RetrofitSingleton.getInstance(getContext()).getFavoriteNodes().enqueue(getListBaseFragmentCallBack());
        }
    }

    @Override
    RecyclerView.LayoutManager getLayoutManager() {
        GridLayoutManager layoutManager = new SmoothScrollGridLayoutManager(getActivity(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (getListAdapter().getItemViewType(position)) {
                    case R.layout.recycler_item_header_simple:
                    case R.layout.recycler_view_item_view_more:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        return layoutManager;
    }

    @Override
    public boolean onItemClick(int position) {
        if(getListAdapter().getItemViewType(position) == R.layout.recycler_item_node) {
            Node node = ((NodeFlexibleItem) getListAdapter().getItem(position)).getNode();
            if(nodeActionType.equals(NODEACTIONTYPE_VIEW)) {
                DataLoadingBaseActivity.gotoNodeDetailsActivity(getContext(), node.getName());
                return true;
            } else if(nodeActionType.equals(NODEACTIONTYPE_CHOOSE)) {
                Intent data = new Intent();
                data.putExtra("node", node);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
                return true;
            }
        }
        return false;
    }
}
