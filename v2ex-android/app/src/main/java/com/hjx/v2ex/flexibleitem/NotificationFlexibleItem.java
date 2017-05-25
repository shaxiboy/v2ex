package com.hjx.v2ex.flexibleitem;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.Notification;
import com.hjx.v2ex.ui.DataLoadingBaseActivity;
import com.hjx.v2ex.util.TextViewImageGetter;
import com.hjx.v2ex.util.V2EXUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/15 0015.
 */

public class NotificationFlexibleItem extends AbstractFlexibleItem<NotificationFlexibleItem.NotificationViewHolder> {

    private Notification notification;

    public NotificationFlexibleItem(Notification notification) {
        this.notification = notification;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_item_member_reply_notification;
    }

    @Override
    public NotificationViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new NotificationViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, NotificationViewHolder holder, int position, List payloads) {
        final Context context = holder.itemView.getContext();
        holder.member.setText(notification.getMember().getUsername());
        holder.member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoadingBaseActivity.gotoMemberDetailsActivity(context, notification.getMember().getUsername());
            }
        });
        switch (notification.getType()) {
            case REPLY:
                holder.notificationInfo.setText("回复了你的主题");
                break;
            case AT:
                holder.notificationInfo.setText("提到了你");
                break;
            case FAVORITE:
                holder.notificationInfo.setText("收藏了你的主题");
                break;
            case THANK:
                if(notification.getReply() == null) holder.notificationInfo.setText("感谢了你的主题");
                else holder.notificationInfo.setText("感谢了你的回复");
                break;
        }
        holder.time.setText(notification.getTime());
        holder.topic.setText(notification.getTopic().getTitle());
        holder.topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoadingBaseActivity.gotoTopicDetailsActivity(view.getContext(), notification.getTopic().getId());
            }
        });
        int maxWidth = V2EXUtil.getDisplayWidth(context) - V2EXUtil.dp(context, 100);
        if(notification.getReply() != null) {
            holder.reply.setText(V2EXUtil.fromHtml(notification.getReply().getContent(), new TextViewImageGetter(context, holder.reply, maxWidth), null, -1));
            holder.reply.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationFlexibleItem that = (NotificationFlexibleItem) o;

        return notification.equals(that.notification);

    }

    @Override
    public int hashCode() {
        return notification.hashCode();
    }

    class NotificationViewHolder extends FlexibleViewHolder {

        @BindView(R.id.member)
        TextView member;
        @BindView(R.id.notification_info)
        TextView notificationInfo;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.topic)
        TextView topic;
        @BindView(R.id.reply)
        TextView reply;

        public NotificationViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
