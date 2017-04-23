package com.hjx.v2ex.flexibleitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hjx.v2ex.R;
import com.hjx.v2ex.ui.DataLoadingBaseActivity;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/12 0012.
 */

public class ViewMoreFlexibleItem extends AbstractFlexibleItem<ViewMoreFlexibleItem.ViewMoreFlexibleViewHolder> {

    private String member;
    private ViewMoreType type;

    public ViewMoreFlexibleItem(String member, ViewMoreType type) {
        this.member = member;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewMoreFlexibleItem that = (ViewMoreFlexibleItem) o;

        if (member != null ? !member.equals(that.member) : that.member != null) return false;
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public int hashCode() {
        int result = member != null ? member.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_view_item_view_more;
    }

    @Override
    public ViewMoreFlexibleViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewMoreFlexibleViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, final ViewMoreFlexibleViewHolder holder, int position, List payloads) {
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type == ViewMoreType.TOPIC) {
                    DataLoadingBaseActivity.gotoMemberTopicsActivity(view.getContext(), member);
                } else if(type == ViewMoreType.REPLY) {
                    DataLoadingBaseActivity.gotoMemberRepliesActivity(view.getContext(), member);
                }
            }
        });
    }

    static class ViewMoreFlexibleViewHolder extends FlexibleViewHolder {

        Button button;

        public ViewMoreFlexibleViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            button = (Button) view.findViewById(R.id.button);
        }
    }

    public enum ViewMoreType {
        TOPIC, REPLY
    }
}
