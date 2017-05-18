package com.hjx.v2ex.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DataLoadingBaseActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_ACTIVITY_TITLE = "ACTIVITY_TITLE";
    public static final String INTENT_EXTRA_FRAGMENTNAME = "FRAGMENTNAME";
    public static final String INTENT_EXTRA_FRAGENT_ARG_ONE = "FRAGENT_ARG_ONE";
    public static final String INTENT_EXTRA_FRAGENT_ARG_TWO = "FRAGENT_ARG_TWO";
    public static final String ACTIVITY_TITLE_MEMBER_DETAILS = "会员详情";
    public static final String ACTIVITY_TITLE_TOPIC_DETAILS = "主题详情";
    public static final String ACTIVITY_TITLE_NODE_DETAILS = "节点详情";
    public static final String ARG_TOPICID = "TOPIC_ID";
    public static final String ARG_NODENAME = "NODE_NAME";
    public static final String ARG_MEMBERNAME = "MEMBER_NAME";
    public static final String ARG_TABNAME = "TABNAME";
    public static final String ARG_TOPICTYPE = "TOPICTYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(INTENT_EXTRA_ACTIVITY_TITLE));
        if(savedInstanceState != null) return;
        try {
            Class fragmentClass = Class.forName(intent.getStringExtra(INTENT_EXTRA_FRAGMENTNAME));
            String arguOne = intent.getStringExtra(INTENT_EXTRA_FRAGENT_ARG_ONE);
            String arguTwo = intent.getStringExtra(INTENT_EXTRA_FRAGENT_ARG_TWO);
            Fragment fragment;
            if (arguOne != null) {
                if (arguTwo != null) {
                    Method newInstance = fragmentClass.getMethod("newInstance", String.class, String.class);
                    fragment = (Fragment) newInstance.invoke(null, arguOne, arguTwo);
                } else {
                    Method newInstance = fragmentClass.getMethod("newInstance", String.class);
                    fragment = (Fragment) newInstance.invoke(null, arguOne);
                }
            } else {
                fragment = (Fragment) fragmentClass.newInstance();
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(android.R.id.content, fragment);
            transaction.commit();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void gotoTopicDetailsActivity(Context context, int topicId) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ACTIVITY_TITLE, DataLoadingBaseActivity.ACTIVITY_TITLE_TOPIC_DETAILS);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENTNAME, TopicDetailsFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGENT_ARG_ONE, topicId + "");
        context.startActivity(intent);
    }

    public static void gotoNodeDetailsActivity(Context context, String nodeName) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ACTIVITY_TITLE, DataLoadingBaseActivity.ACTIVITY_TITLE_NODE_DETAILS);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENTNAME, NodeDetailsFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGENT_ARG_ONE, nodeName);
        context.startActivity(intent);
    }

    public static void gotoMemberDetailsActivity(Context context, String memberName) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ACTIVITY_TITLE, DataLoadingBaseActivity.ACTIVITY_TITLE_MEMBER_DETAILS);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENTNAME, MemberDetailsFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGENT_ARG_ONE, memberName);
        context.startActivity(intent);
    }

    public static void gotoMemberTopicsActivity(Context context, String memberName) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ACTIVITY_TITLE, memberName + "发表的主题");
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENTNAME, TopicListFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGENT_ARG_ONE, TopicListFragment.TOPICTYPE_MEMBERTOPIC);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGENT_ARG_TWO, memberName);
        context.startActivity(intent);
    }

    public static void gotoMemberRepliesActivity(Context context, String memberName) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ACTIVITY_TITLE, memberName + "发表的回复");
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENTNAME, MemberRepliesFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGENT_ARG_ONE, memberName);
        context.startActivity(intent);
    }

    public static void gotoFavoriteMembersTopicsActivity(Context context) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ACTIVITY_TITLE, "我关注的人发表的主题");
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENTNAME, TopicListFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGENT_ARG_ONE, TopicListFragment.TOPICTYPE_FAVORITEMEMBERSTOPIC);
        context.startActivity(intent);
    }

    public static void gotoFavoriteNodesTopicsActivity(Context context) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ACTIVITY_TITLE, "我收藏节点下的最新主题");
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENTNAME, TopicListFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGENT_ARG_ONE, TopicListFragment.TOPICTYPE_TABTOPIC);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGENT_ARG_TWO, "nodes");
        context.startActivity(intent);
    }
}
