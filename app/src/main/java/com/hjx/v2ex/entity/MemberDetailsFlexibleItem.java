package com.hjx.v2ex.entity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hjx.v2ex.R;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by shaxiboy on 2017/4/17 0017.
 */

public class MemberDetailsFlexibleItem extends AbstractFlexibleItem<MemberDetailsFlexibleItem.MemberDetailsViewHolder> {

    private Member member;

    public MemberDetailsFlexibleItem(Member member) {
        this.member = member;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_header_member_details;
    }

    @Override
    public MemberDetailsViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new MemberDetailsViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, MemberDetailsViewHolder holder, int position, List payloads) {
        Context context = holder.itemView.getContext();
        Glide.with(context).load(member.getPhoto()).into(holder.phote);
        holder.name.setText(member.getUsername());
        String basicInfo = member.getBasicInfo();
        holder.basicInfo.setText(basicInfo.substring(basicInfo.indexOf("第")).replaceAll("，", "\n"));
        if (member.getMoreInfos().isEmpty()) {
            holder.view2.setVisibility(View.GONE);
        } else {
            for (MemberMoreInfo moreInfo : member.getMoreInfos()) {
                holder.moreInfoContainer.addView(getMoreInfoComponent(context, moreInfo));
            }
        }
    }

    private View getMoreInfoComponent(final Context context, final MemberMoreInfo moreInfo) {
        View moreInfoComponent = LayoutInflater.from(context).inflate(R.layout.member_more_info, null);
        FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8, 8, 8, 8);
        moreInfoComponent.setLayoutParams(lp);
        Glide.with(context).load(moreInfo.getPhoto()).into(ButterKnife.<ImageView>findById(moreInfoComponent, R.id.imageView));
        ButterKnife.<TextView>findById(moreInfoComponent, R.id.textView).setText(moreInfo.getInfo());
        moreInfoComponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(moreInfo.getLink()));
                context.startActivity(intent);
            }
        });
        return moreInfoComponent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberDetailsFlexibleItem that = (MemberDetailsFlexibleItem) o;

        return member != null ? member.equals(that.member) : that.member == null;

    }

    @Override
    public int hashCode() {
        return member != null ? member.hashCode() : 0;
    }

    static class MemberDetailsViewHolder extends FlexibleViewHolder {

        @BindView(R.id.photo)
        CircleImageView phote;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.basic_info)
        TextView basicInfo;
        @BindView(R.id.more_info_container)
        FlowLayout moreInfoContainer;
        @BindView(R.id.line2)
        View view2;

        public MemberDetailsViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
