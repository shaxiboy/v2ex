package com.hjx.v2ex.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.bean.ReplyTopicResult;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.bean.TopicFavoriteResult;
import com.hjx.v2ex.bean.TopicPage;
import com.hjx.v2ex.event.AtMemberEvent;
import com.hjx.v2ex.event.ShowMemberRepliesEvent;
import com.hjx.v2ex.flexibleitem.TopicDetailsFlexibleItem;
import com.hjx.v2ex.flexibleitem.TopicReplyFlexibleItem;
import com.hjx.v2ex.network.RetrofitService;
import com.hjx.v2ex.network.RetrofitServiceSingleton;
import com.hjx.v2ex.util.V2EXUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hjx.v2ex.util.V2EXUtil.showProgressDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopicDetailsFragment extends ListBaseFragment<TopicPage> {

    @BindView(R.id.edit_container)
    LinearLayout editContainer;
    @BindView(R.id.editText)
    EditText replyEdt;
    @BindView(R.id.send)
    Button sendBtn;

    private int topicId;
    private Topic topic;

    public static TopicDetailsFragment newInstance(String topicId) {
        TopicDetailsFragment topicDetailsFragment = new TopicDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DataLoadingBaseActivity.ARG_TOPICID, Integer.parseInt(topicId));
        topicDetailsFragment.setArguments(bundle);
        return topicDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topicId = getArguments().getInt(DataLoadingBaseActivity.ARG_TOPICID);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getContentRes() {
        return R.layout.fragment_topic_details;
    }

    @Override
    protected void initView() {
        super.initView();
        replyEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().equals("")) {
                    sendBtn.setEnabled(false);
                } else {
                    sendBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyTopic();
            }
        });
    }

    @Override
    protected void loadData() {
        loadTopicDetails();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem favoriteItem = menu.findItem(R.id.menu_favorite);
        MenuItem replyItem = menu.findItem(R.id.menu_reply);
        if (V2EXUtil.isLogin(getContext()) && !getListAdapter().isEmpty()) {
            String favoriteURL = ((TopicDetailsFlexibleItem) getListAdapter().getScrollableHeaders().get(0)).getTopic().getFavoriteURL();
            FavoriteTopicType type = getFavoriteTopicType(favoriteURL);
            if (type != null) {
                if (type == FavoriteTopicType.UNFAVORITE) {
                    favoriteItem.setIcon(R.drawable.favorite_red);
                } else if (type == FavoriteTopicType.FAVORITE) {
                    favoriteItem.setIcon(R.drawable.favorite_white);
                }
                favoriteItem.setVisible(true);
            } else {
                favoriteItem.setVisible(false);
            }
            replyItem.setVisible(true);
        } else {
            favoriteItem.setVisible(false);
            replyItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                favoriteTopic();
                break;
            case R.id.menu_reply:
                showReplyEditView(ShowReplyEditViewType.CHANGE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        topic = null;
        super.onRefresh();
    }

    @Override
    ListData getListData(TopicPage data) {
        ListData listData = new ListData();
        topic = data.getTopic();
        listData.setHeader(new TopicDetailsFlexibleItem(data.getTopic()));
        PageData<AbstractFlexibleItem> pageData = new PageData<>();
        copyPageDataStatistics(data.getReplies(), pageData);
        for (Reply reply : data.getReplies().getCurrentPageItems()) {
            pageData.getCurrentPageItems().add(new TopicReplyFlexibleItem(reply));
        }
        listData.setPageData(pageData);
        return listData;
    }

    @Subscribe
    public void onAtMemberEvent(AtMemberEvent event) {
        replyEdt.append("@" + event.getMemberName() + " ");
        showReplyEditView(ShowReplyEditViewType.SHOW);
    }

    @Subscribe
    public void onShowMemberRepliesEvent(ShowMemberRepliesEvent event) {
        ArrayList<TopicReplyFlexibleItem> replyFlexibleItems = new ArrayList<>();
        for(int i = 1; i <= event.getTerminateIndex(); i++) {
            TopicReplyFlexibleItem item = (TopicReplyFlexibleItem) getListAdapter().getItem(i);
            if(item.getReply().getMember().getUsername().equals(event.getMemberName())) replyFlexibleItems.add(new TopicReplyFlexibleItem(item.getReply()));
        }
        if(!replyFlexibleItems.isEmpty()) {
            MemberRepliesDialogFragment.newInstance(replyFlexibleItems).show(getChildFragmentManager(), "memberReplies");
        }
    }

    private void loadTopicDetails() {
        RetrofitServiceSingleton.getInstance(getActivity().getApplication()).getTopicPage(topicId, getCurrentPage()).enqueue(getListBaseFragmentCallBack());
    }

    private void favoriteTopic() {
        String favoriteURL = ((TopicDetailsFlexibleItem) getListAdapter().getScrollableHeaders().get(0)).getTopic().getFavoriteURL();
        final FavoriteTopicType type = getFavoriteTopicType(favoriteURL);
        if (type != null) {
            String referer = RetrofitService.BASE_URL + "t/" + topicId;
            final ProgressDialog progressDialog = showProgressDialog(getContext(), "正在" + type);
            RetrofitServiceSingleton.getInstance(getActivity().getApplication()).favoriteTopic(favoriteURL, referer).enqueue(new Callback<TopicFavoriteResult>() {
                @Override
                public void onResponse(Call<TopicFavoriteResult> call, Response<TopicFavoriteResult> response) {
                    progressDialog.dismiss();
                    TopicFavoriteResult result = response.body();
                    boolean success = false;
                    if (result != null) {
                        FavoriteTopicType newType = getFavoriteTopicType(result.getFavoriteURL());
                        if (newType != null) {
                            if (type == FavoriteTopicType.UNFAVORITE && newType == FavoriteTopicType.FAVORITE
                                    || type == FavoriteTopicType.FAVORITE && newType == FavoriteTopicType.UNFAVORITE) {
                                success = true;
                            }
                        }
                    }
                    if (success) {
                        Toast.makeText(TopicDetailsFragment.this.getContext(), type + "操作成功", Toast.LENGTH_SHORT).show();
                        ((TopicDetailsFlexibleItem) getListAdapter().getScrollableHeaders().get(0)).getTopic().setFavoriteURL(result.getFavoriteURL());
                        getActivity().invalidateOptionsMenu();
                    } else {
                        Toast.makeText(TopicDetailsFragment.this.getContext(), type + "操作失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TopicFavoriteResult> call, Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(TopicDetailsFragment.this.getContext(), type + "操作失败", Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                }
            });
        }
    }

    private void showReplyEditView(ShowReplyEditViewType type) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (editContainer.getVisibility() == View.VISIBLE && type != ShowReplyEditViewType.SHOW) {
            editContainer.setVisibility(View.GONE);
            imm.hideSoftInputFromWindow(editContainer.getWindowToken(), 0);
        } else if (editContainer.getVisibility() == View.GONE && type != ShowReplyEditViewType.HIDE) {
            editContainer.setVisibility(View.VISIBLE);
            replyEdt.requestFocus();
            imm.showSoftInput(replyEdt, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void replyTopic() {
        final ProgressDialog dialog = V2EXUtil.showProgressDialog(getContext(), "正在发送回复");
        final String referer = RetrofitService.BASE_URL + "t/" + topicId;
        RetrofitServiceSingleton.getInstance(getActivity().getApplication()).replyTopic(referer, topicId, replyEdt.getText().toString().trim(), topic.getReplyOnce()).enqueue(new Callback<ReplyTopicResult>() {
            @Override
            public void onResponse(Call<ReplyTopicResult> call, Response<ReplyTopicResult> response) {
                dialog.dismiss();
                ReplyTopicResult result = response.body();
                boolean success = false;
                if (result != null) {
                    topic.setReplyOnce(result.getReplyOnce());
                    if (result.isSuccess()) {
                        success = true;
                    }
                }
                if (success) {
                    Toast.makeText(getContext(), "回复成功", Toast.LENGTH_SHORT).show();
                    replyEdt.setText("");
                    showReplyEditView(ShowReplyEditViewType.HIDE);
                    onRefresh();
                } else {
                    String msg = "回复失败";
                    if(result.getFailedMsg() != null) msg += "：" + result.getFailedMsg();
                    else msg += "，请重试";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReplyTopicResult> call, Throwable throwable) {
                dialog.dismiss();
                Toast.makeText(getContext(), "回复失败，请重试", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }
        });
    }

    public FavoriteTopicType getFavoriteTopicType(String url) {
        if (url != null) {
            if (url.contains("unfavorite")) return FavoriteTopicType.UNFAVORITE;
            if (url.contains("favorite")) return FavoriteTopicType.FAVORITE;
        }
        return null;
    }

    public enum FavoriteTopicType {

        FAVORITE("收藏主题"), UNFAVORITE("取消收藏主题");

        private String action;

        FavoriteTopicType(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }

    public enum ShowReplyEditViewType {
        CHANGE, SHOW, HIDE
    }
}
