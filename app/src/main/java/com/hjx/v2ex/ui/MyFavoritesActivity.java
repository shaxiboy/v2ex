package com.hjx.v2ex.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hjx.v2ex.R;
import com.hjx.v2ex.adapter.MyFavoritesPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFavoritesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;

    private MyFavoritesPagerAdapter myFavoritesPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_viewpager_layout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tabLayout.setupWithViewPager(viewPager, false);
        myFavoritesPagerAdapter = new MyFavoritesPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myFavoritesPagerAdapter);
    }
}
