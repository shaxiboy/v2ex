package com.hjx.v2ex.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hjx.v2ex.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class DataLoadingBaseFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.loading_layout)
    FrameLayout loadingLayout;
    @BindView(R.id.loading)
    LinearLayout loading;
    @BindView(R.id.error)
    LinearLayout error;
    @BindView(R.id.reload)
    Button reload;
    FrameLayout contentLayout;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_data_loading_base, container, false);
        contentLayout = (FrameLayout) root.findViewById(R.id.content_layout);
        contentLayout.addView(inflater.inflate(getContentRes(), contentLayout, false));
        unbinder = ButterKnife.bind(this, root);
        initView();
        loadData();
        return root;
    }

    public abstract int getContentRes();

    protected abstract void initView();

    protected abstract void loadData();

    @OnClick(R.id.reload)
    public void reloadData() {
        error.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        loadData();
    }

    public void successLoadingData() {
        loadingLayout.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

    public void errorLoadingData() {
        contentLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
