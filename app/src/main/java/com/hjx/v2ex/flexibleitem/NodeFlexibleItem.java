package com.hjx.v2ex.flexibleitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.Node;
import com.hjx.v2ex.ui.DataLoadingBaseActivity;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IHeader;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/12 0012.
 */

public class NodeFlexibleItem extends AbstractSectionableItem<NodeFlexibleItem.NodeViewHolder, IHeader> implements IFilterable {

    private Node node;

    public NodeFlexibleItem(Node node, IHeader header) {
        super(header);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_item_node;
    }

    @Override
    public NodeViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new NodeViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, NodeViewHolder holder, int position, List payloads) {
        holder.nodeTitle.setText(node.getTitle());
//        holder.nodeTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DataLoadingBaseActivity.gotoNodeDetailsActivity(view.getContext(), node.getName());
//            }
//        });
    }

    @Override
    public boolean filter(String constraint) {
        return node.getName().contains(constraint) || node.getTitle().contains(constraint);
    }

    static class NodeViewHolder extends FlexibleViewHolder {

        TextView nodeTitle;

        public NodeViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            nodeTitle = (TextView) view.findViewById(R.id.node);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeFlexibleItem that = (NodeFlexibleItem) o;

        return node.equals(that.node);

    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

}
