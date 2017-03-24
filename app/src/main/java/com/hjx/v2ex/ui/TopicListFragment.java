package com.hjx.v2ex.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hjx.v2ex.R;
import com.hjx.v2ex.adapter.TopicListAdapter;
import com.hjx.v2ex.network.RetrofitSingleton;
import com.hjx.v2ex.util.HTMLUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shaxiboy on 2017/3/6 0006.
 */

public class TopicListFragment extends Fragment {


    private String tab;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private TopicListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.topic_list, container, false);
        ButterKnife.bind(this, root);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new TopicListAdapter(this.getContext());
        recyclerView.setAdapter(adapter);
        tab = getArguments().getString("tab");
        RetrofitSingleton.getInstance().vistHomePage(tab).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    adapter.setTopics(HTMLUtil.parseTopicList(response.body().string()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
        return root;
    }

    public static Fragment newInstance(String tab) {
        TopicListFragment topicListFragment = new TopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tab", tab);
        topicListFragment.setArguments(bundle);
        return topicListFragment;
    }
}
