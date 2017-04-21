package com.hjx.v2ex.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.entity.HomePage;
import com.hjx.v2ex.entity.Node;
import com.hjx.v2ex.entity.NodeCategoryFlexibleHeaderItem;
import com.hjx.v2ex.entity.NodeFlexibleItem;
import com.hjx.v2ex.entity.NodesPlane;
import com.hjx.v2ex.network.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shaxiboy on 2017/4/12 0012.
 */

public class NodeListFragment extends Fragment implements FlexibleAdapter.OnItemClickListener{

    private String tab;
    private Unbinder unbinder;
    private boolean hasCreateView;
    private boolean hasLoadData;
    private boolean isVisibleToUser;
    private FlexibleAdapter<AbstractFlexibleItem> nodeListAdapter;
    private List<AbstractFlexibleItem> nodes = new ArrayList<>();

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static Fragment newInstance(String tab) {
        NodeListFragment nodeListFragment = new NodeListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tab", tab);
        nodeListFragment.setArguments(bundle);
        return nodeListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tab = getArguments().getString("tab");
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.fragment_node_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        swipeRefreshLayout.setRefreshing(true);
        nodeListAdapter = new FlexibleAdapter<>(nodes, this);
        nodeListAdapter.setDisplayHeadersAtStartUp(true)
                .expandItemsAtStartUp()
                .setStickyHeaders(true);
        recyclerView.setLayoutManager(createNewGridLayoutManager());
        recyclerView.setAdapter(nodeListAdapter);

        hasCreateView = true;
        if (isVisibleToUser) {
            loadNodes();
        }
        return root;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void loadNodes() {
        if (tab.equals("最热") || tab.equals("导航")) {
            RetrofitSingleton.getInstance().homePage(null).enqueue(new Callback<HomePage>() {
                @Override
                public void onResponse(Call<HomePage> call, Response<HomePage> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
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
                    nodeListAdapter.updateDataSet(new ArrayList<>(nodes));
                    hasLoadData = true;
                }

                @Override
                public void onFailure(Call<HomePage> call, Throwable throwable) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else if (tab.equals("全部")) {
            RetrofitSingleton.getInstance().allNodesPage().enqueue(new Callback<NodesPlane>() {
                @Override
                public void onResponse(Call<NodesPlane> call, Response<NodesPlane> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                    Map<String, List<Node>> nodeSections = response.body().getNodeSections();
                    for (String categoryName : nodeSections.keySet()) {
                        NodeCategoryFlexibleHeaderItem category = new NodeCategoryFlexibleHeaderItem(categoryName);
                        for (Node node : nodeSections.get(categoryName)) {
                            category.addSubItem(new NodeFlexibleItem(node, category));
                        }
                        nodes.add(category);
                    }
                    nodeListAdapter.updateDataSet(new ArrayList<>(nodes));
                    hasLoadData = true;
                }

                @Override
                public void onFailure(Call<NodesPlane> call, Throwable throwable) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private GridLayoutManager createNewGridLayoutManager() {
        GridLayoutManager gridLayoutManager = new SmoothScrollGridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (nodeListAdapter.getItemViewType(position)) {
                    case R.layout.recycler_item_header_simple:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        return gridLayoutManager;
    }

    @Override
    public boolean onItemClick(int position) {
        AbstractFlexibleItem item = nodeListAdapter.getItem(position);
        if(item instanceof NodeFlexibleItem) {
            Node node = ((NodeFlexibleItem) item).getNode();
            Intent intent = new Intent(getContext(), NodeDetailsActivity.class);
            intent.putExtra("node", node);
            startActivity(intent);
            return true;
        } else {
            return false;
        }
    }
}
