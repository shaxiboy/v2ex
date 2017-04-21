package com.hjx.v2ex.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hjx.v2ex.R;
import com.hjx.v2ex.entity.PageData;
import com.hjx.v2ex.entity.Reply;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.ui.MemberDetailsActivity;
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

    private OnScrollToBottomListener scrollListener;
    private Topic topic;
    private List<Reply> replies = new ArrayList<>();
    private PageData<Reply> pageData;
    private boolean canLoadMore;
    private boolean onLoadingMore;

    public TopicAdapter(OnScrollToBottomListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
        notifyDataSetChanged();
    }

    public void setPageData(PageData<Reply> pageData) {
        this.pageData = pageData;
        onLoadingMore = false;
        if(pageData.getCurrentPage() == 1) {
            replies.clear();
        }
        int position = replies.size();
        List<Reply> currentPageItems = pageData.getCurrentPageItems();
        replies.addAll(currentPageItems);
        if(pageData.getCurrentPage() < pageData.getTotalPage()) {
            canLoadMore = true;
        } else {
            canLoadMore = false;
        }
        if(topic != null) {
            topic.setReplyNum(pageData.getTotalItems());
            notifyItemChanged(0);
            notifyItemRangeChanged(position, currentPageItems.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new TopicDetailsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header_topic_details, parent, false));
            case VIEW_TYPE_FOOTER:
                return new RecyclerViewFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer_data_load_status, parent, false));
            default:
                return new TopicReplyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_reply, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                ((TopicDetailsViewHolder) holder).bind(topic);
                break;
            case VIEW_TYPE_NORMAL:
                ((TopicReplyViewHolder) holder).bind(this.replies.get(position - 1));
                break;
        }
        if(position == replies.size() && canLoadMore) {
            onLoadingMore = true;
            notifyItemChanged(position + 1);
            scrollListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        if(topic != null) {
            if(!onLoadingMore) {
                return replies.size() + 1;
            } else {
                return replies.size() + 2;
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return VIEW_TYPE_HEADER;
        if(onLoadingMore && (position == getItemCount() - 1))
            return VIEW_TYPE_FOOTER;
        return VIEW_TYPE_NORMAL;
    }

    class TopicDetailsViewHolder extends RecyclerView.ViewHolder {

        private Context context;

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

        public TopicDetailsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            photoContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MemberDetailsActivity.class);
                    intent.putExtra("member", topic.getMember().getUsername());
                    context.startActivity(intent);
                }
            });
        }

        protected void bind(final Topic topic) {
            Glide.with(context).load(topic.getMember().getPhoto()).into(photo);
            author.setText(topic.getMember().getUsername());
            node.setText(topic.getNode().getTitle());
            time.setText(topic.getCreatedTime());
            replyNum.setBadgeCount(topic.getReplyNum());
            title.setText(topic.getTitle());
            content.setText(topic.getContent());
        }
    }

    class TopicReplyViewHolder extends RecyclerView.ViewHolder {

        private Context context;

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
            context = itemView.getContext();
        }

        protected void bind(final Reply reply) {
            Glide.with(context).load(reply.getMember().getPhoto()).into(photo);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MemberDetailsActivity.class);
                    intent.putExtra("member", reply.getMember().getUsername());
                    context.startActivity(intent);
                }
            });
            author.setText(reply.getMember().getUsername());
            time.setText(reply.getReplyTime());
            content.setText(reply.getContent());
            floor.setText(getAdapterPosition() + "楼");
        }
    }

    class RecyclerViewFooterViewHolder extends RecyclerView.ViewHolder {

        public RecyclerViewFooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
