package com.hjx.v2ex.entity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hjx.v2ex.R;
import com.hjx.v2ex.ui.NodeDetailsActivity;
import com.jauker.widget.BadgeView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/15 0015.
 */

public class NodeDetailsFlexibleItem extends AbstractFlexibleItem<NodeDetailsFlexibleItem.NodeDetailsViewHolder> {

    private Node node;

    public NodeDetailsFlexibleItem(Node node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeDetailsFlexibleItem that = (NodeDetailsFlexibleItem) o;

        return node != null ? node.equals(that.node) : that.node == null;

    }

    @Override
    public int hashCode() {
        return node != null ? node.hashCode() : 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_header_node_details;
    }

    @Override
    public NodeDetailsViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new NodeDetailsViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, NodeDetailsViewHolder holder, int position, List payloads) {
        Context context = holder.itemView.getContext();
        Glide.with(context).load(node.getPhoto()).into(holder.phote);
        holder.node.setText(node.getTitle());
        holder.desc.setText(node.getDesc());
        holder.topicNum.setBadgeCount(node.getTopicNum());
        if (node.getParent() == null && node.getChildren().isEmpty() && node.getRelatives().isEmpty()) {
            holder.view2.setVisibility(View.GONE);
        } else {
            if (node.getParent() == null) {
                holder.parentNode.setVisibility(View.GONE);
            } else {
                holder.parentNodeContainer.addView(getNodeComponent(context, node.getParent()));
            }
            if (node.getChildren().isEmpty()) {
                holder.childNode.setVisibility(View.GONE);
            } else {
                for (Node child : node.getChildren()) {
                    holder.childNodeContainer.addView(getNodeComponent(context, child));
                }
            }
            if (node.getRelatives().isEmpty()) {
                holder.relativeNode.setVisibility(View.GONE);
            } else {
                for (Node relative : node.getRelatives()) {
                    holder.relativeNodeContainer.addView(getNodeComponent(context, relative));
                }
            }
        }
    }

    private View getNodeComponent(final Context context, final Node node) {
        CardView.LayoutParams cardViewLP = new CardView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardViewLP.setMargins(8, 8, 8, 8);
        CardView cardView = new CardView(context);
        TextView textView = new TextView(context);
        textView.setText(node.getTitle());
        textView.setLayoutParams(cardViewLP);
        cardView.addView(textView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NodeDetailsActivity.class);
                intent.putExtra("node", node);
                context.startActivity(intent);
            }
        });
        FlowLayout.LayoutParams flowLayoutLP = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flowLayoutLP.setMargins(8, 8, 8, 8);
        cardView.setLayoutParams(flowLayoutLP);
        return cardView;
    }

    static class NodeDetailsViewHolder extends FlexibleViewHolder {

        @BindView(R.id.photo)
        ImageView phote;
        @BindView(R.id.node)
        TextView node;
        @BindView(R.id.desc)
        TextView desc;
        @BindView(R.id.topicNum)
        BadgeView topicNum;
        @BindView(R.id.parent_node)
        LinearLayout parentNode;
        @BindView(R.id.parent_node_container)
        FlowLayout parentNodeContainer;
        @BindView(R.id.child_node)
        LinearLayout childNode;
        @BindView(R.id.child_node_container)
        FlowLayout childNodeContainer;
        @BindView(R.id.relative_node)
        LinearLayout relativeNode;
        @BindView(R.id.relative_node_container)
        FlowLayout relativeNodeContainer;
        @BindView(R.id.line2)
        View view2;

        public NodeDetailsViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
