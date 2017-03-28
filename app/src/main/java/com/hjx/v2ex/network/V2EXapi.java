package com.hjx.v2ex.network;

import com.hjx.v2ex.entity.Member;
import com.hjx.v2ex.entity.Node;
import com.hjx.v2ex.entity.NodesGuide;
import com.hjx.v2ex.entity.NodesPlane;
import com.hjx.v2ex.entity.PageData;
import com.hjx.v2ex.entity.Reply;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.entity.V2EX;
import com.hjx.v2ex.util.HTMLUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by shaxiboy on 2017/3/26 0026.
 */

public class V2EXapi {

    //获取V2EX网站相关信息
    public static V2EX getV2EX() {
        try {
            V2EX v2ex = HTMLUtil.parseV2EX(RetrofitSingleton.getInstance().homePage(null).execute().body().string());
            v2ex.setNodes(getAllNodes().getNodeCount());
            return v2ex;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取tab页的主题
    public static List<Topic> getTopicsFromTab(String tab) {
        try {
            return HTMLUtil.parseTopicsFromTabPage(RetrofitSingleton.getInstance().homePage(tab).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取全部主题（需登录）
    public static PageData<Topic> getAllTopics(int page) {
        try {
            return HTMLUtil.parseAllTopics(RetrofitSingleton.getInstance().allTopicsPage(page).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取主题详情
    public static Topic getTopicDetails(int topicId) {
        try {
            Topic topic = HTMLUtil.parseTopicDetails(RetrofitSingleton.getInstance().topicDetailsPage(topicId, null).execute().body().string());
            topic.setId(topicId);
            return topic;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取主题回复列表
    public static PageData<Reply> getTopicReplies(int topicId, int page) {
        try {
            return HTMLUtil.parseTopicReplies(RetrofitSingleton.getInstance().topicDetailsPage(topicId, page).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取用户详细信息
    public static Member getMemberDetails(String memberName) {
        try {
            Member member = HTMLUtil.parseMemberDetails(RetrofitSingleton.getInstance().memberDetailsPage(memberName).execute().body().string());
            member.setUsername(memberName);
            return member;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取用户发表的主题列表
    public static PageData<Topic> getMemberTopics(String memberName, int page) {
        try {
            return HTMLUtil.parseMemberTopics(RetrofitSingleton.getInstance().memberTopicsPage(memberName, page).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取用户发表的回复列表
    public static PageData<Map<Reply, Topic>> getMemberTopicReplies(String memberName, int page) {
        try {
            return HTMLUtil.parseMemberTopicReplies(RetrofitSingleton.getInstance().memberTopicRepliesPage(memberName, page).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取最热节点
    public static List<Node> getHottestNodes() {
        try {
            return HTMLUtil.parseHottestNodes(RetrofitSingleton.getInstance().homePage(null).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取节点导航信息
    public static NodesGuide getNodesGuide() {
        try {
            return HTMLUtil.parseNodesGuide(RetrofitSingleton.getInstance().homePage(null).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取所有节点
    public static NodesPlane getAllNodes() {
        try {
            return HTMLUtil.parseAllNodes(RetrofitSingleton.getInstance().allNodesPage().execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取节点详细信息
    public static Node getNodeDetails(String nodeName) {
        try {
            Node node = HTMLUtil.parseNodeDetails(RetrofitSingleton.getInstance().nodeDetailsPage(nodeName, 1).execute().body().string());
            node.setName(nodeName);
            return node;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取节点下的主题列表
    public static PageData<Topic> getNodeTopics(String nodeName, int page) {
        try {
            return HTMLUtil.parseNodeTopics(RetrofitSingleton.getInstance().nodeDetailsPage(nodeName, page).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //登陆
    public static int login(String username, String password) {
        try {
            String html = RetrofitSingleton.getInstance().signinPage().execute().body().string();
            Map<String, String> signinParams = HTMLUtil.parseSigninParams(html);
            for(String key : signinParams.keySet()) {
                if(signinParams.get(key).equals("username")) {
                    signinParams.put(key, username);
                } else if(signinParams.get(key).equals("password")) {
                    signinParams.put(key, password);
                }
            }
            html = RetrofitSingleton.getInstance().signin(signinParams).execute().body().string();
            String errorMsg = HTMLUtil.parseSigninFailedMsg(html);
            if(errorMsg == null) {
//                LogUtil.d("signin success");
                System.out.println("signin success");
                int sessionId = HTMLUtil.parseSessionId(html);
                System.out.println("sessionid: " + sessionId);
                return sessionId;
            } else  {
//                LogUtil.d("signin failed: " + errorMsg);
                System.out.println("signin failed: " + errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //登出
    public static void loginout(int sessionId) {
        try {
            String html = RetrofitSingleton.getInstance().signout(sessionId).execute().body().string();
            String resultMsg = HTMLUtil.parseSignoutResultMsg(html);
            System.out.println("resultMsg: " + resultMsg);
            if(resultMsg.equals("重新登录")) {
//                LogUtil.d("signin success");
                System.out.println("signout success");
            } else {
//                LogUtil.d("signin failed: " + errorMsg);
                System.out.println("signout failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关注用户、取消关注
    public static void followMember(Member member) {
        if(member.getNoticeHref() != null) {
            try {
                System.out.println(member.getNoticeHref());
                RetrofitSingleton.getInstance().doGet(member.getNoticeHref()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //我关注的人
    public static List<Member> getMyFollowingMembers() {
        try {
            return HTMLUtil.parseMyFollowingMembers(RetrofitSingleton.getInstance().myFollowingMembersPage(1).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //我关注的人的最新主题
    public static PageData<Topic> getMyFollowingMembersTopics(int page) {
        try {
            return HTMLUtil.parseMemberTopics(RetrofitSingleton.getInstance().myFollowingMembersPage(page).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //感谢主题
    public static void thankTopic(Topic topic) {
        if(topic.getThankToken() != null) {
            try {
                RetrofitSingleton.getInstance().thankTopic(topic.getId(), topic.getThankToken()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //收藏主题、取消收藏主题
    public static void followTopic(Topic topic) {
        if(topic.getCollectHref() != null) {
            try {
                System.out.println(topic.getCollectHref());
                RetrofitSingleton.getInstance().doGet(topic.getCollectHref()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //获取收藏的主题
    public static PageData<Topic> getMyFollowingTopics(int page) {
        try {
            return HTMLUtil.parseMemberTopics(RetrofitSingleton.getInstance().myFollowingTopicsPage(page).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //收藏节点、取消收藏节点
    public static void followNode(Node node) {
        if(node.getCollectHref() != null) {
            try {
                RetrofitSingleton.getInstance().doGet(node.getCollectHref()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //获取收藏的节点
    public static List<Node> getMyFollowingNodes() {
        try {
            return HTMLUtil.parseMyFollowingNodes(RetrofitSingleton.getInstance().myFollowingNodesPage().execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
