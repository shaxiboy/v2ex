package com.hjx.v2ex.flexibleitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjx.v2ex.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/12 0012.
 */

public class SimpleFlexibleHeaderItem extends AbstractHeaderItem<SimpleFlexibleHeaderItem.SimpleHeaderViewHolder> {

    private String headerText;

    public SimpleFlexibleHeaderItem(String headerText) {
        super();
        this.headerText = headerText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleFlexibleHeaderItem that = (SimpleFlexibleHeaderItem) o;

        return headerText != null ? headerText.equals(that.headerText) : that.headerText == null;

    }

    @Override
    public int hashCode() {
        return headerText != null ? headerText.hashCode() : 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_item_header_simple;
    }

    @Override
    public SimpleHeaderViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new SimpleHeaderViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SimpleHeaderViewHolder holder, int position, List payloads) {
        holder.header.setText(headerText);
    }

    static class SimpleHeaderViewHolder extends FlexibleViewHolder {

        TextView header;

        public SimpleHeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            header = (TextView) view.findViewById(R.id.header);
        }
    }
}
