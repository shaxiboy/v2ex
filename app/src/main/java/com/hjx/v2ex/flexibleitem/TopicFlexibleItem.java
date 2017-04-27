package com.hjx.v2ex.flexibleitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.ui.DataLoadingBaseActivity;
import com.jauker.widget.BadgeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/15 0015.
 */

public class TopicFlexibleItem extends AbstractFlexibleItem<TopicFlexibleItem.TopicViewHolder> {

    private Topic topic;

    public TopicFlexibleItem(Topic topic) {
        this.topic = topic;
    }

    public Topic getTopic() {
        return topic;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_item_topic;
    }

    @Override
    public TopicViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new TopicViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, TopicViewHolder holder, int position, List payloads) {
        if(topic.getMember() == null) {
            holder.photoContainer.setVisibility(View.GONE);
        } else {
            Glide.with(holder.itemView.getContext()).load(topic.getMember().getPhoto()).into(holder.photo);
            holder.photoContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataLoadingBaseActivity.gotoMemberDetailsActivity(view.getContext(), TopicFlexibleItem.this.topic.getMember().getUsername());
                }
            });
            holder.author.setText(topic.getMember().getUsername());
        }
        holder.topic.setText(topic.getTitle());
        holder.topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoadingBaseActivity.gotoTopicDetailsActivity(view.getContext(), TopicFlexibleItem.this.topic.getId());
            }
        });
        if(topic.getNode() == null) {
            holder.node.setVisibility(View.GONE);
        } else {
            holder.node.setText(topic.getNode().getTitle());
            holder.node.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataLoadingBaseActivity.gotoNodeDetailsActivity(view.getContext(), TopicFlexibleItem.this.topic.getNode().getName());
                }
            });
        }
        holder.time.setText(topic.getLastRepliedTime());
        holder.reply.setBadgeCount(topic.getReplyNum());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicFlexibleItem that = (TopicFlexibleItem) o;

        return topic != null ? topic.equals(that.topic) : that.topic == null;

    }

    @Override
    public int hashCode() {
        return topic != null ? topic.hashCode() : 0;
    }

    class TopicViewHolder extends FlexibleViewHolder {

        @BindView(R.id.photo_container)
        LinearLayout photoContainer;
        @BindView(R.id.photo)
        CircleImageView photo;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.topic)
        TextView topic;
        @BindView(R.id.node)
        TextView node;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.reply)
        BadgeView reply;

        public TopicViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }

}
