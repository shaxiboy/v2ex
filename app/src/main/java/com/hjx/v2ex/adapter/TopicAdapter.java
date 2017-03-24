package com.hjx.v2ex.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hjx.v2ex.R;
import com.hjx.v2ex.entity.ReplyOld;
import com.hjx.v2ex.entity.TopicOld;
import com.hjx.v2ex.util.V2EXUtil;
import com.jauker.widget.BadgeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shaxiboy on 2017/3/12 0012.
 */

public class TopicAdapter extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_HEADER = -1;
    public static final int VIEW_TYPE_FOOTER = -2;

    private TopicOld topic;
    private List<ReplyOld> replies = new ArrayList<>();
    private Context context;
    private boolean canLoadMore;

    public TopicAdapter(Context context) {
        this.context = context;
    }

    public void setTopic(TopicOld topic) {
        this.topic = topic;
        this.notifyItemChanged(0);
    }

    public void addReplies(List<ReplyOld> replies) {
        int position = this.replies.size() + 1;
        this.replies.addAll(replies);
        this.notifyItemRangeChanged(position, replies.size());
    }

    public List<ReplyOld> getReplies() {
        return replies;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new TopicDetailsViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_header_topic_detail, parent, false));
            case VIEW_TYPE_FOOTER:
                return new RecyclerViewFooterViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_footer, parent, false));
            default:
                return new TopicReplyViewHolder(LayoutInflater.from(context).inflate(R.layout.reply_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                ((TopicDetailsViewHolder) holder).bind(topic);
                break;
            case VIEW_TYPE_NORMAL:
                ((TopicReplyViewHolder) holder).bind(replies.get(position - 1));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return VIEW_TYPE_HEADER;
        if(position == getItemCount() - 1)
            return VIEW_TYPE_FOOTER;
        return VIEW_TYPE_NORMAL;
    }

    class TopicDetailsViewHolder extends RecyclerView.ViewHolder {

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

        public TopicDetailsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void bind(TopicOld topic) {
            Glide.with(context).load(topic.getMember().getAvatarMini()).into(photo);
            author.setText(topic.getMember().getUsername());
            node.setText(topic.getNode().getTitle());
            time.setText(V2EXUtil.parseTime(topic.getCreated()));
            replyNum.setBadgeCount(topic.getReplies());
            title.setText(topic.getTitle());
            content.setText(topic.getContent());
        }
    }

    class TopicReplyViewHolder extends RecyclerView.ViewHolder {

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

        public TopicReplyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void bind(ReplyOld reply) {
            Glide.with(context).load(reply.getMember().getAvatarMini()).into(photo);
            author.setText(reply.getMember().getUsername());
            time.setText(V2EXUtil.parseTime(reply.getCreated()));
            content.setText(reply.getContent());
            floor.setText(getAdapterPosition() + 1 + "楼");
        }
    }

    class RecyclerViewFooterViewHolder extends RecyclerView.ViewHolder {

        public RecyclerViewFooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
