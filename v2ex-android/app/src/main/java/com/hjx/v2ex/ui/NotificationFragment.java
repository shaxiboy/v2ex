package com.hjx.v2ex.ui;

import com.hjx.v2ex.bean.Notification;
import com.hjx.v2ex.bean.NotificationsPageData;
import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.flexibleitem.NotificationFlexibleItem;
import com.hjx.v2ex.network.RetrofitServiceSingleton;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * Created by shaxiboy on 2017/5/21 0021.
 */

public class NotificationFragment extends ListBaseFragment<NotificationsPageData> {

    @Override
    protected void loadData() {
        RetrofitServiceSingleton.getInstance(getContext()).getNotifications(getCurrentPage()).enqueue(getListBaseFragmentCallBack());
    }

    @Override
    ListData getListData(NotificationsPageData data) {
        ListData listData = new ListData();
        PageData<AbstractFlexibleItem> pageData = new PageData<>();
        copyPageDataStatistics(data.getNotifications(), pageData);
        for(Notification notification : data.getNotifications().getCurrentPageItems()) {
            pageData.getCurrentPageItems().add(new NotificationFlexibleItem(notification));
        }
        listData.setPageData(pageData);
        return listData;
    }
}
