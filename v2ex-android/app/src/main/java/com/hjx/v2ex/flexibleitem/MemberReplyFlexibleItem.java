package com.hjx.v2ex.flexibleitem;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.ui.DataLoadingBaseActivity;
import com.hjx.v2ex.ui.TextViewImageGetter;
import com.hjx.v2ex.util.V2EXUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/15 0015.
 */

public class MemberReplyFlexibleItem extends AbstractSectionableItem<MemberReplyFlexibleItem.MemberReplyViewHolder, IHeader> {

    private Map<Reply, Topic> replyMap;

    public MemberReplyFlexibleItem(Map<Reply, Topic> replyMap, IHeader header) {
        super(header);
        this.replyMap = replyMap;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_item_member_reply_notification;
    }

    @Override
    public MemberReplyViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new MemberReplyViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, MemberReplyViewHolder holder, int position, List payloads) {
        final Context context = holder.itemView.getContext();
        ArrayList<Map.Entry<Reply, Topic>> entries = new ArrayList<>(replyMap.entrySet());
        final Topic topic = entries.get(0).getValue();
        Reply reply = entries.get(0).getKey();
        holder.node.setText(topic.getNode().getTitle());
        holder.node.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoadingBaseActivity.gotoNodeDetailsActivity(view.getContext(), topic.getNode().getName());
            }
        });
        holder.time.setText(reply.getReplyTime());
        holder.topic.setText(topic.getTitle());
        holder.topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoadingBaseActivity.gotoTopicDetailsActivity(view.getContext(), topic.getId());
            }
        });
        int maxWidth = V2EXUtil.getDisplayWidth(context) - V2EXUtil.dp(context, 100);
        holder.reply.setText(V2EXUtil.fromHtml(reply.getContent(), new TextViewImageGetter(context, holder.reply, maxWidth), null, -1));
        holder.reply.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberReplyFlexibleItem that = (MemberReplyFlexibleItem) o;

        return replyMap != null ? replyMap.equals(that.replyMap) : that.replyMap == null;

    }

    @Override
    public int hashCode() {
        return replyMap != null ? replyMap.hashCode() : 0;
    }

    class MemberReplyViewHolder extends FlexibleViewHolder {

        @BindView(R.id.node)
        TextView node;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.topic)
        TextView topic;
        @BindView(R.id.reply)
        TextView reply;

        public MemberReplyViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
