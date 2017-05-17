package com.hjx.v2ex.flexibleitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjx.v2ex.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.ExpandableViewHolder;

/**
 * Created by shaxiboy on 2017/4/12 0012.
 */

public class NodeCategoryFlexibleHeaderItem extends AbstractExpandableHeaderItem<NodeCategoryFlexibleHeaderItem.NodeCategoryViewHolder, ISectionable> {

    private String categoryName;

    public NodeCategoryFlexibleHeaderItem(String categoryName) {
        super();
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeCategoryFlexibleHeaderItem that = (NodeCategoryFlexibleHeaderItem) o;

        return categoryName != null ? categoryName.equals(that.categoryName) : that.categoryName == null;

    }

    @Override
    public int hashCode() {
        return categoryName != null ? categoryName.hashCode() : 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_item_header_simple;
    }

    @Override
    public NodeCategoryViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new NodeCategoryViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, NodeCategoryViewHolder holder, int position, List payloads) {
        holder.nodeCategory.setText(categoryName);
    }

    static class NodeCategoryViewHolder extends ExpandableViewHolder {

        TextView nodeCategory;

        public NodeCategoryViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            nodeCategory = (TextView) view.findViewById(R.id.header);
        }
    }
}
