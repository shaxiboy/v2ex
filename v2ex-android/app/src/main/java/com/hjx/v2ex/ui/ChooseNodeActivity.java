package com.hjx.v2ex.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.hjx.v2ex.R;
import com.hjx.v2ex.adapter.MyFavoritesPagerAdapter;
import com.hjx.v2ex.adapter.NodesPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseNodeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;

    private NodesPagerAdapter nodesPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_viewpager_layout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tabLayout.setupWithViewPager(viewPager, false);
        nodesPagerAdapter = new NodesPagerAdapter(getSupportFragmentManager(), NodeListFragment.NODEACTIONTYPE_CHOOSE);
        viewPager.setAdapter(nodesPagerAdapter);
    }
}
