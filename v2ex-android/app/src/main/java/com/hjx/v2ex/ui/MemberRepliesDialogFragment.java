package com.hjx.v2ex.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.Button;

import com.hjx.v2ex.R;
import com.hjx.v2ex.flexibleitem.TopicReplyFlexibleItem;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

/**
 * Created by shaxiboy on 2017/5/19 0019.
 */

public class MemberRepliesDialogFragment extends DialogFragment {

    public static MemberRepliesDialogFragment newInstance(ArrayList<TopicReplyFlexibleItem> replies) {
        MemberRepliesDialogFragment dialogFragment = new MemberRepliesDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("replies", replies);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<TopicReplyFlexibleItem> replies = (List<TopicReplyFlexibleItem>) getArguments().getSerializable("replies");
        FlexibleAdapter adapter = new FlexibleAdapter(replies);
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(recyclerView);
        return builder.create();
    }
}
