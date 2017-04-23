package com.hjx.v2ex.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DataLoadingBaseActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_TITLE = "TITLE";
    public static final String INTENT_EXTRA_TITLE_MEMBER_DETAILS = "会员详情";
    public static final String INTENT_EXTRA_TITLE_TOPIC_DETAILS = "主题详情";
    public static final String INTENT_EXTRA_TITLE_NODE_DETAILS = "节点详情";
    public static final String INTENT_EXTRA_FRAGMENT = "FRAGMENT";
    public static final String INTENT_EXTRA_ARGU_NAME = "ARGU_NAME";
    public static final String INTENT_EXTRA_ARGU_TOPIC = "TOPIC_ID";
    public static final String INTENT_EXTRA_ARGU_NODE = "NODE_NAME";
    public static final String INTENT_EXTRA_ARGU_MEMBER = "MEMBER_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setTitle(intent.getStringExtra(INTENT_EXTRA_TITLE));
        try {
            Class fragmentClass = Class.forName(intent.getStringExtra(INTENT_EXTRA_FRAGMENT));
            Method newInstance = fragmentClass.getMethod("newInstance", String.class);
            Fragment fragment = (Fragment) newInstance.invoke(null, intent.getStringExtra(intent.getStringExtra(INTENT_EXTRA_ARGU_NAME)));
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
        }
    }

    public static void gotoTopicDetailsActivity(Context context, int topicId) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_TITLE, DataLoadingBaseActivity.INTENT_EXTRA_TITLE_TOPIC_DETAILS);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENT, TopicDetailsFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_NAME, DataLoadingBaseActivity.INTENT_EXTRA_ARGU_TOPIC);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_TOPIC, topicId + "");
        context.startActivity(intent);
    }

    public static void gotoNodeDetailsActivity(Context context, String nodeName) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_TITLE, DataLoadingBaseActivity.INTENT_EXTRA_TITLE_NODE_DETAILS);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENT, NodeDetailsFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_NAME, DataLoadingBaseActivity.INTENT_EXTRA_ARGU_NODE);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_NODE, nodeName);
        context.startActivity(intent);
    }

    public static void gotoMemberDetailsActivity(Context context, String memberName) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_TITLE, DataLoadingBaseActivity.INTENT_EXTRA_TITLE_MEMBER_DETAILS);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENT, MemberDetailsFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_NAME, DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER, memberName);
        context.startActivity(intent);
    }

    public static void gotoMemberTopicsActivity(Context context, String memberName) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_TITLE, memberName + "发表的主题");
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENT, MemberTopicsFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_NAME, DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER, memberName);
        context.startActivity(intent);
    }
    public static void gotoMemberRepliesActivity(Context context, String memberName) {
        Intent intent = new Intent(context, DataLoadingBaseActivity.class);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_TITLE, memberName + "发表的回复");
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_FRAGMENT, MemberRepliesFragment.class.getName());
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_NAME, DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER);
        intent.putExtra(DataLoadingBaseActivity.INTENT_EXTRA_ARGU_MEMBER, memberName);
        context.startActivity(intent);
    }
}
