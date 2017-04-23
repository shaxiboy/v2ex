package com.hjx.v2ex.flexibleitem;

import android.text.TextUtils;
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
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/21 0021.
 */

public class TopicDetailsFlexibleItem extends AbstractFlexibleItem<TopicDetailsFlexibleItem.TopicDetailsViewHolder> {

    private Topic topic;

    public TopicDetailsFlexibleItem(Topic topic) {
        this.topic = topic;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_header_topic_details;
    }

    @Override
    public TopicDetailsViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new TopicDetailsViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, TopicDetailsViewHolder holder, int position, List payloads) {
        holder.photoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoadingBaseActivity.gotoMemberDetailsActivity(view.getContext(), topic.getMember().getUsername());
            }
        });
        Glide.with(holder.itemView.getContext()).load(topic.getMember().getPhoto()).into(holder.photo);
        holder.author.setText(topic.getMember().getUsername());
        holder.node.setText(topic.getNode().getTitle());
        holder.node.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoadingBaseActivity.gotoNodeDetailsActivity(view.getContext(), topic.getNode().getName());
            }
        });
        holder.time.setText(topic.getCreatedTime());
        holder.replyNum.setBadgeCount(topic.getReplyNum());
        holder.title.setText(topic.getTitle());
        if(TextUtils.isEmpty(topic.getContent())) {
            holder.content.setVisibility(View.GONE);
            holder.line2.setVisibility(View.GONE);
        } else {
            holder.content.setText(topic.getContent());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicDetailsFlexibleItem that = (TopicDetailsFlexibleItem) o;

        return topic != null ? topic.equals(that.topic) : that.topic == null;

    }

    @Override
    public int hashCode() {
        return topic != null ? topic.hashCode() : 0;
    }

    class TopicDetailsViewHolder extends FlexibleViewHolder {

        @BindView(R.id.photo_container)
        LinearLayout photoContainer;
        @BindView(R.id.photo)
        CircleImageView photo;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.node)
        TextView node;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.replyNum)
        BadgeView replyNum;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.line2)
        View line2;

        public TopicDetailsViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
