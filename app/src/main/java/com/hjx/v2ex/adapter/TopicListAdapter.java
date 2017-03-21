package com.hjx.v2ex.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hjx.v2ex.R;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.ui.TopicDetailsActivity;
import com.hjx.v2ex.util.V2EXUtil;
import com.jauker.widget.BadgeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shaxiboy on 2017/3/7 0007.
 */

public class TopicListAdapter extends Adapter {


    private List<Topic> topics = new ArrayList<>();
    private Context context;

    public TopicListAdapter(Context context) {
        this.context = context;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TopicViewHolder(LayoutInflater.from(context).inflate(R.layout.topic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TopicViewHolder) holder).bind(topics.get(position));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    class TopicViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo)
        CircleImageView imageView;
        @BindView(R.id.topic)
        TextView topicTV;
        @BindView(R.id.node)
        TextView node;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.reply)
        BadgeView reply;

        public TopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TopicDetailsActivity.class);
                    intent.putExtra("topicId", topics.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }

        protected void bind(Topic topic) {
            Glide.with(context).load(topic.getMember().getAvatarMini()).into(imageView);
            topicTV.setText(topic.getTitle());
            node.setText(topic.getNode().getTitle());
            author.setText(topic.getMember().getUsername());
            if (topic.getLastModifiedS() != null) {
                time.setText(V2EXUtil.parseTime(topic.getLastModifiedS()));
            }
            reply.setBadgeCount(topic.getReplies());
        }
    }
}
