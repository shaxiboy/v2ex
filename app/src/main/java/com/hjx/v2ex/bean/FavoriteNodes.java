package com.hjx.v2ex.bean;

import com.hjx.v2ex.ui.ListBaseFragment;

/**
 * Created by shaxiboy on 2017/4/26 0026.
 */

public class FavoriteNodes implements ListBaseFragment.ListBaseFragmentData{

    private PageData<Node> favoriteNodes = new PageData<>();

    public PageData<Node> getFavoriteNodes() {
        return favoriteNodes;
    }

    @Override
    public PageData getPageData() {
        return favoriteNodes;
    }
}
