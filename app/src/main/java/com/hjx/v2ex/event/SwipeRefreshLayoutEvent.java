package com.hjx.v2ex.event;

/**
 * Created by shaxiboy on 2017/3/30 0030.
 */

public class SwipeRefreshLayoutEvent {

    private boolean refreshing;

    public boolean isRefreshing() {
        return refreshing;
    }

    public SwipeRefreshLayoutEvent(boolean refreshing) {
        this.refreshing = refreshing;
    }
}
