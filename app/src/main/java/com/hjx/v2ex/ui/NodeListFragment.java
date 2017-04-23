package com.hjx.v2ex.ui;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.HomePage;
import com.hjx.v2ex.bean.Node;
import com.hjx.v2ex.flexibleitem.NodeCategoryFlexibleHeaderItem;
import com.hjx.v2ex.flexibleitem.NodeFlexibleItem;
import com.hjx.v2ex.bean.NodesPlane;
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

/**
 * Created by shaxiboy on 2017/4/12 0012.
 */

public class NodeListFragment extends DataLoadingBaseFragment {

    private String tab;
    private boolean hasCreateView;
    private boolean hasLoadData;
    private boolean isVisibleToUser;
    private FlexibleAdapter<AbstractFlexibleItem> nodeListAdapter;
    private List<AbstractFlexibleItem> nodes = new ArrayList<>();

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static NodeListFragment newInstance(String tab) {
        NodeListFragment nodeListFragment = new NodeListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tab", tab);
        nodeListFragment.setArguments(bundle);
        return nodeListFragment;
    }

    @Override
    public int getContentRes() {
        return R.layout.fragment_node_list;
    }

    @Override
    protected void initView() {
        tab = getArguments().getString("tab");
        setHasOptionsMenu(true);
        nodeListAdapter = new FlexibleAdapter<>(nodes);
        nodeListAdapter.setDisplayHeadersAtStartUp(true)
                .expandItemsAtStartUp()
                .setStickyHeaders(true);
        recyclerView.setLayoutManager(createLayoutManager());
        recyclerView.setAdapter(nodeListAdapter);

        hasCreateView = true;
    }

    @Override
    protected void loadData() {
        if (isVisibleToUser) {
            loadNodes();
        }
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
                if (nodeListAdapter.hasNewSearchText(newText)) {
                    nodeListAdapter.setSearchText(newText);
                    nodeListAdapter.filterItems(new ArrayList<>(nodes), 200);
                }
                return true;
            }
        });
    }

    //数据懒加载
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && hasCreateView && !hasLoadData) {
            loadNodes();
        }
    }

    private void loadNodes() {
        if (tab.equals("最热") || tab.equals("导航")) {
            RetrofitSingleton.getInstance().homePage(null).enqueue(new Callback<HomePage>() {
                @Override
                public void onResponse(Call<HomePage> call, Response<HomePage> response) {
                    if (tab.equals("最热")) {
                        for (Node node : response.body().getHottestNodes()) {
                            nodes.add(new NodeFlexibleItem(node, null));
                        }
                    } else if (tab.equals("导航")) {
                        Map<String, List<Node>> nodeSections = response.body().getNodeGuide();
                        for (String categoryName : nodeSections.keySet()) {
                            NodeCategoryFlexibleHeaderItem category = new NodeCategoryFlexibleHeaderItem(categoryName);
                            for (Node node : nodeSections.get(categoryName)) {
                                category.addSubItem(new NodeFlexibleItem(node, category));
                            }
                            nodes.add(category);
                        }
                    }
                    successLoadingData();
                    nodeListAdapter.updateDataSet(new ArrayList<>(nodes));
                    hasLoadData = true;
                }

                @Override
                public void onFailure(Call<HomePage> call, Throwable throwable) {
                    errorLoadingData();
                    throwable.printStackTrace();
                }
            });
        } else if (tab.equals("全部")) {
            RetrofitSingleton.getInstance().allNodesPage().enqueue(new Callback<NodesPlane>() {
                @Override
                public void onResponse(Call<NodesPlane> call, Response<NodesPlane> response) {
                    Map<String, List<Node>> nodeSections = response.body().getNodeSections();
                    for (String categoryName : nodeSections.keySet()) {
                        NodeCategoryFlexibleHeaderItem category = new NodeCategoryFlexibleHeaderItem(categoryName);
                        for (Node node : nodeSections.get(categoryName)) {
                            category.addSubItem(new NodeFlexibleItem(node, category));
                        }
                        nodes.add(category);
                    }
                    successLoadingData();
                    nodeListAdapter.updateDataSet(new ArrayList<>(nodes));
                    hasLoadData = true;
                }

                @Override
                public void onFailure(Call<NodesPlane> call, Throwable throwable) {
                    errorLoadingData();
                    throwable.printStackTrace();
                }
            });
        }
    }

    private RecyclerView.LayoutManager createLayoutManager() {
        GridLayoutManager layoutManager = new SmoothScrollGridLayoutManager(getActivity(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (nodeListAdapter.getItemViewType(position)) {
                    case R.layout.recycler_item_header_simple:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        return layoutManager;
    }

}
