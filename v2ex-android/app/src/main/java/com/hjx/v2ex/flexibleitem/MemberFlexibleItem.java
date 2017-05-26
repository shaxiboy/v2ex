package com.hjx.v2ex.flexibleitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.Member;
import com.hjx.v2ex.ui.DataLoadingBaseActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/12 0012.
 */

public class MemberFlexibleItem extends AbstractFlexibleItem<MemberFlexibleItem.MemberViewHolder> {

    private Member member;

    public MemberFlexibleItem(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_item_member;
    }

    @Override
    public MemberViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new MemberViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, MemberViewHolder holder, int position, List payloads) {
        final Context context = holder.itemView.getContext();
        Glide.with(context).load(member.getPhoto()).into(holder.photo);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataLoadingBaseActivity.gotoMemberDetailsActivity(context, member.getUsername());
            }
        });
        holder.member.setText(member.getUsername());
    }

    static class MemberViewHolder extends FlexibleViewHolder {

        CircleImageView photo;
        TextView member;

        public MemberViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            photo = (CircleImageView) view.findViewById(R.id.photo);
            member = (TextView) view.findViewById(R.id.member);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberFlexibleItem that = (MemberFlexibleItem) o;

        return member != null ? member.equals(that.member) : that.member == null;

    }

    @Override
    public int hashCode() {
        return member != null ? member.hashCode() : 0;
    }
}
