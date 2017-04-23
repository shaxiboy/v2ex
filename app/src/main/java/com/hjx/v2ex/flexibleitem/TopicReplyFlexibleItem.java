package com.hjx.v2ex.flexibleitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.ui.DataLoadingBaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/15 0015.
 */

public class TopicReplyFlexibleItem extends AbstractFlexibleItem<TopicReplyFlexibleItem.TopicReplyViewHolder> {

    private Reply reply;

    public TopicReplyFlexibleItem(Reply reply) {
        this.reply = reply;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_item_reply;
    }

    @Override
    public TopicReplyViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new TopicReplyViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, TopicReplyViewHolder holder, int position, List payloads) {
        Glide.with(holder.itemView.getContext()).load(reply.getMember().getPhoto()).into(holder.photo);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoadingBaseActivity.gotoMemberDetailsActivity(view.getContext(), reply.getMember().getUsername());
            }
        });
        holder.author.setText(reply.getMember().getUsername());
        holder.time.setText(reply.getReplyTime());
        holder.content.setText(reply.getContent());
        holder.floor.setText(holder.getAdapterPosition() + "楼");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicReplyFlexibleItem that = (TopicReplyFlexibleItem) o;

        return reply != null ? reply.equals(that.reply) : that.reply == null;

    }

    @Override
    public int hashCode() {
        return reply != null ? reply.hashCode() : 0;
    }

    class TopicReplyViewHolder extends FlexibleViewHolder {

        @BindView(R.id.photo)
        CircleImageView photo;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.floor)
        TextView floor;

        public TopicReplyViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
