package com.hjx.v2ex.bean;

import com.hjx.v2ex.ui.ListBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaxiboy on 2017/4/26 0026.
 */

public class FavoriteMembers implements ListBaseFragment.ListBaseFragmentData {

    private PageData<Member> favoriteMembers = new PageData<>();

    public PageData<Member> getFavoriteMembers() {
        return favoriteMembers;
    }

    @Override
    public PageData getPageData() {
        return favoriteMembers;
    }
}
