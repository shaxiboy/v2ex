package com.hjx.v2ex.util;

import android.text.TextUtils;

import com.hjx.v2ex.entity.Member;
import com.hjx.v2ex.entity.Node;
import com.hjx.v2ex.entity.NodesGuide;
import com.hjx.v2ex.entity.NodesPlane;
import com.hjx.v2ex.entity.PageData;
import com.hjx.v2ex.entity.Reply;
import com.hjx.v2ex.entity.Topic;
import com.hjx.v2ex.entity.V2EX;
import com.hjx.v2ex.network.RetrofitService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by shaxiboy on 2017/3/8 0008.
 */

public class HTMLUtil {

    public static V2EX parseV2EX(String html) {
        V2EX v = new V2EX();
        Element box = Jsoup.parse(html).getElementById("Rightbar").getElementsByClass("box").get(5);
        Element cell = box.getElementsByClass("cell").get(1);
        v.setMembers(Integer.parseInt(cell.getElementsByTag("td").get(1).text()));
        v.setTopics(Integer.parseInt(cell.getElementsByTag("td").get(3).text()));
        v.setReplies(Integer.parseInt(cell.getElementsByTag("td").get(5).text()));
        return v;
    }

    public static List<Topic> parseTopicsFromTabPage(String html) {
        List<Topic> topics = new ArrayList<>();
        Elements elements = Jsoup.parse(html).body().getElementsByClass("cell item");
        for (Element element : elements) {
            topics.add(parseTopicItemFromList(element));
        }
        return topics;
    }

    public static PageData<Topic> parseAllTopics(String html) {
        PageData<Topic> pageData = new PageData<>();
        Element mainEle = Jsoup.parse(html).body().getElementById("Main");
        for (Element element : mainEle.getElementsByClass("cell item")) {
            pageData.getCurrentPageItems().add(parseTopicItemFromList(element));
        }
        Element summarizeEle = mainEle.getElementsByClass("header").first().getElementsByClass("fade").first();
        pageData.setTotalItems(Integer.parseInt(summarizeEle.text().split("共")[1].split("个")[0].trim()));
        pageData.setCurrentPage(Integer.parseInt(mainEle.getElementsByClass("header").first().nextElementSibling().getElementsByTag("input").first().attr("value")));
        pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM - 1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
        return pageData;
    }

    public static PageData<Topic> parseNodeTopics(String html) {
        PageData<Topic> pageData = new PageData<>();
        Element topicsEle = Jsoup.parse(html).body().getElementById("TopicsNode");
        for (Element element : topicsEle.children()) {
            pageData.getCurrentPageItems().add(parseTopicItemFromList(element));
        }
        Element summarizeEle = topicsEle.nextElementSibling().nextElementSibling();
        pageData.setTotalItems(Integer.parseInt(summarizeEle.text().split("共")[1].split("个")[0].trim()));
        pageData.setCurrentPage(Integer.parseInt(topicsEle.previousElementSibling().getElementsByTag("input").first().attr("value")));
        pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM - 1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
        return pageData;
    }

    public static Topic parseTopicItemFromList(Element element) {
        Topic topic = new Topic();
        Member member = new Member();
        Node node = new Node();
        Elements tds = element.getElementsByTag("td");
        int mainTDIndex = 0;//会员发表的主题页中的主题项没有会员信息
        if (tds.size() >= 4) {
            mainTDIndex = 2;
        }

        if (mainTDIndex == 2) {
            Element memberTD = tds.get(0);
            member.setUsername(memberTD.getElementsByTag("a").first().attr("href").split("/")[2]);
            member.setPhoto("https:" + memberTD.getElementsByTag("img").first().attr("src"));
        }

        Element mainTD = tds.get(mainTDIndex);
        Element titleSpan = mainTD.getElementsByTag("span").first();
        Element titleA = titleSpan.getElementsByTag("a").first();
        topic.setId(Integer.parseInt(titleA.attr("href").split("#")[0].split("/")[2]));
        topic.setTitle(titleA.text());

        Element bottomSpan = mainTD.getElementsByTag("span").get(1);
        org.jsoup.nodes.Node secondNode = bottomSpan.childNode(1);

        //首页主题项中有节点信息，节点页中则无
        if (secondNode instanceof Element && ((Element) secondNode).tagName().equals("a")) {
            node.setName(secondNode.attr("href").split("/")[2]);
            node.setTitle(((Element) secondNode).text());
        }

        if (secondNode instanceof TextNode) {
            //节点页
            topic.setLastRepliedTime(((TextNode) secondNode).text().split("  •  ")[1]);
        } else {
            //首页
            if (bottomSpan.childNodeSize() >= 5) {
                topic.setLastRepliedTime(((TextNode) bottomSpan.childNode(4)).text().split("  •  ")[1]);
            }
        }

        Element replyTD = tds.get(mainTDIndex + 1);
        String reply = replyTD.text();
        if (!TextUtils.isEmpty(reply)) {
            topic.setReplyNum(Integer.parseInt(reply));
        }
        topic.setMember(member);
        topic.setNode(node);

        return topic;
    }

    public static Topic parseTopicDetails(String html) {
        Topic topic = new Topic();
        Document doc = Jsoup.parse(html);
        Element headerEle = doc.getElementsByClass("header").first();

        Element authorEle = headerEle.getElementsByClass("fr").first();
        Member member = new Member();
        member.setUsername(authorEle.getElementsByTag("a").attr("href").split("/")[2]);
        member.setPhoto("https:" + authorEle.getElementsByTag("img").attr("src"));
        topic.setMember(member);

        Element nodeEle = headerEle.getElementsByTag("span").first().nextElementSibling();
        Node node = new Node();
        node.setName(nodeEle.attr("href").split("/")[2]);
        node.setTitle(nodeEle.text());
        topic.setNode(node);

        topic.setTitle(headerEle.getElementsByTag("h1").first().text());
        topic.setCreatedTime(headerEle.getElementsByTag("small").first().text().split(" · ")[1]);

        topic.setContent(doc.getElementsByClass("topic_content").first().html());
        List<Topic.PS> psList = new ArrayList<>();
        for (Element psEle : doc.getElementsByClass("subtle")) {
            psList.add(parseTopicPS(psEle));
        }
        topic.setPsList(psList);

        Element topicButtons = doc.getElementsByClass("topic_buttons").first();
        if (topicButtons != null) {
            //登录以后才有
            for (Element aEle : topicButtons.getElementsByTag("a")) {
                if (aEle.text().equals("加入收藏") || aEle.text().equals("取消收藏")) {
                    topic.setCollectHref("https:" + aEle.attr("href"));
                } else if (aEle.text().equals("感谢")) {
                    String thankInfo = aEle.attr("onclick").split("thankTopic")[1];
                    String topicId = Pattern.compile("[^0-9]").matcher(thankInfo.split("'")[0]).replaceAll("");
                    topic.setId(Integer.parseInt(topicId));
                    String thankToken = thankInfo.split("'")[1];
                    topic.setThankToken(thankToken);
                }
            }
            String thanksInfoEle = topicButtons.getElementsByTag("div").first().text();
            Pattern pattern = Pattern.compile("[^0-9]");
            for (String info : thanksInfoEle.split("∙")) {
                int num = Integer.parseInt(pattern.matcher(info).replaceAll(""));
                if (info.contains("人收藏")) {
                    topic.setCollectedNum(num);
                } else if (info.contains("人感谢")) {
                    topic.setThanksNum(num);
                }
            }
        }

        Element replyBox = doc.getElementById("Main").getElementsByClass("box").get(1);
        if (replyBox.children().size() > 1) {
            topic.setReplyNum(Integer.parseInt(replyBox.getElementsByTag("div").get(0).getElementsByTag("span").first().text().split(" 回复 ")[0]));
        }
        return topic;
    }

    private static Topic.PS parseTopicPS(Element psEle) {
        Topic.PS ps = new Topic.PS();
        ps.setTime(psEle.getElementsByTag("span").first().text().split("  ·  ")[1]);
        ps.setContent(psEle.getElementsByClass("topic_content").first().html());
        return ps;
    }

    public static PageData<Reply> parseTopicReplies(String html) {
        PageData<Reply> pageData = new PageData<>();
        Document doc = Jsoup.parse(html);
        Element replyBox = doc.getElementById("Main").getElementsByClass("box").get(1);
        pageData.setTotalItems(Integer.parseInt(replyBox.getElementsByTag("div").get(0).getElementsByTag("span").first().text().split(" 回复 ")[0]));
        pageData.setCurrentPage(Integer.parseInt(replyBox.getElementsByClass("cell").get(1).getElementsByTag("input").first().attr("value")));
        pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPIC_REPLIES_ITEM_NUM - 1) / RetrofitService.PAGE_TOPIC_REPLIES_ITEM_NUM);
        for (Element divEle : replyBox.getElementsByTag("div")) {
            if (divEle.attr("id") != null && divEle.attr("id").startsWith("r_")) {
                pageData.getCurrentPageItems().add(parseTopicReply(divEle));
            }
        }
        return pageData;
    }

    private static Reply parseTopicReply(Element replyEle) {
        Reply reply = new Reply();
        Element photoTD = replyEle.getElementsByTag("td").get(0);
        Member member = new Member();
        member.setPhoto("https:" + photoTD.getElementsByTag("img").first().attr("src"));

        Element mainTD = replyEle.getElementsByTag("td").get(2);
        member.setUsername(mainTD.getElementsByTag("strong").first().text());
        reply.setMember(member);
        String timeInfo = mainTD.getElementsByTag("span").get(1).text();
        if (timeInfo.contains(" via ")) {
            timeInfo = timeInfo.split(" via ")[0];
        }
        reply.setReplyTime(timeInfo);
        reply.setContent(mainTD.getElementsByClass("reply_content").first().html());
        return reply;
    }

    public static Member parseMemberDetails(String html) {
        Member member = new Member();
        Document doc = Jsoup.parse(html);
        Element memberInfoBox = doc.getElementById("Main").getElementsByClass("box").first();
        member.setUsername(memberInfoBox.getElementsByTag("h1").first().text());
        member.setPhoto("https:" + memberInfoBox.getElementsByTag("img").first().attr("src"));
        member.setJoinTime(memberInfoBox.text().split("加入于")[1].split("，")[0]);
        for (Element inputEle : memberInfoBox.getElementsByTag("input")) {
            if (inputEle.attr("value").equals("加入特别关注") || inputEle.attr("value").equals("取消特别关注")) {
                member.setNoticeHref(RetrofitService.BASE_URL + inputEle.attr("onclick").split("location.href")[1].split("'")[1]);
            }
        }
        return member;
    }

    public static PageData<Topic> parseMemberTopics(String html) {
        PageData<Topic> pageData = new PageData<>();
        Document doc = Jsoup.parse(html);
        for (Element element : doc.getElementsByClass("cell item")) {
            pageData.getCurrentPageItems().add(parseTopicItemFromList(element));
        }
        if (!pageData.getCurrentPageItems().isEmpty()) {
            Element summarizeEle = doc.getElementsByClass("header").first();
            pageData.setTotalItems(Integer.parseInt(summarizeEle.text().split("主题总数  ")[1].trim()));
            pageData.setCurrentPage(Integer.parseInt(summarizeEle.nextElementSibling().getElementsByTag("input").first().attr("value")));
            pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM - 1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
        }
        return pageData;
    }

    public static PageData<Map<Reply, Topic>> parseMemberTopicReplies(String html) {
        PageData<Map<Reply, Topic>> pageData = new PageData<>();
        Document doc = Jsoup.parse(html);
        Element strongEle =  doc.getElementById("Main").getElementsByTag("strong").get(0);
        if(strongEle != null) {
            pageData.setCurrentPage(1);
            pageData.setTotalItems(Integer.parseInt(strongEle.text()));
            pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM - 1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
            Element inputEle = doc.getElementById("Main").getElementsByTag("input").first();
            if(inputEle != null) {
                pageData.setCurrentPage(Integer.parseInt(inputEle.attr("value")));
            }
            for (Element topicDiv : doc.getElementById("Main").getElementsByClass("dock_area")) {
                Element nodeEle = topicDiv.getElementsByTag("a").get(1);
                Node node = new Node();
                node.setName(nodeEle.attr("href").split("/")[2]);
                node.setTitle(nodeEle.text());

                Element topicEle = topicDiv.getElementsByTag("a").get(2);
                Topic topic = new Topic();
                topic.setId(Integer.parseInt(topicEle.attr("href").split("/")[2].split("#")[0]));
                topic.setContent(topicEle.text());
                topic.setNode(node);

                Reply reply = new Reply();
                reply.setReplyTime(topicDiv.getElementsByTag("span").first().text());
                reply.setContent(topicDiv.nextElementSibling().text());

                Map<Reply, Topic> replyMap = new HashMap<>();
                replyMap.put(reply, topic);
                pageData.getCurrentPageItems().add(replyMap);
            }
        }
        return pageData;
    }

    public static NodesPlane parseAllNodes(String html) {
        NodesPlane nodesPlane = new NodesPlane();
        Document doc = Jsoup.parse(html);
        String text = doc.getElementsByTag("h2").first().text();
        int num = Integer.parseInt(Pattern.compile("[^0-9]").matcher(text).replaceAll(""));
        nodesPlane.setNodeCount(num);
        Element main = doc.getElementById("Main");
        List<Node> nodes;
        Node node;
        for (Element box : main.getElementsByClass("box")) {
            Elements inners = box.getElementsByClass("inner");
            if (inners.isEmpty()) continue;
            nodes = new ArrayList<>();
            for (Element a : inners.first().getElementsByTag("a")) {
                node = new Node();
                node.setTitle(a.text());
                node.setName(a.attr("href").split("/")[2]);
                nodes.add(node);
            }
            Element header = box.child(0);
            String tag = header.html().split("\n<span")[0];
            nodesPlane.getNodeSections().put(tag, nodes);
        }
        return nodesPlane;
    }

    public static NodesGuide parseNodesGuide(String html) {
        NodesGuide nodesGuide = new NodesGuide();
        Document doc = Jsoup.parse(html);
        Element boxEle = doc.getElementById("Main").getElementsByClass("box").get(1);
        for (Element div : boxEle.children()) {
            Elements tds = div.getElementsByTag("td");
            if (tds.size() >= 2) {
                List<Node> nodeList = new ArrayList<>();
                for (Element nodeEle : tds.get(1).getElementsByTag("a")) {
                    Node node = new Node();
                    node.setName(nodeEle.attr("href").split("/")[2]);
                    node.setTitle(nodeEle.text());
                    nodeList.add(node);
                }
                nodesGuide.getNodeSections().put(tds.first().text(), nodeList);
            }
        }
        return nodesGuide;
    }

    public static List<Node> parseHottestNodes(String html) {
        List<Node> hottestNodes = new ArrayList<>();
        Element boxEle = Jsoup.parse(html).getElementById("Rightbar").getElementsByClass("box").get(3);
        if (boxEle.getElementsByClass("cell").first().text().equals("最热节点")) {
            for (Element nodeEle : boxEle.getElementsByClass("cell").get(1).getElementsByTag("a")) {
                Node node = new Node();
                node.setName(nodeEle.attr("href").split("/")[2]);
                node.setTitle(nodeEle.text());
                hottestNodes.add(node);
            }
        }
        return hottestNodes;
    }

    public static Node parseNodeDetails(String html) {
        Node node = new Node();
        Document doc = Jsoup.parse(html);
        Element headerEle = doc.getElementById("Main").getElementsByClass("header").first();
        node.setTitle(((TextNode) headerEle.getElementsByClass("chevron").first().nextSibling()).text().trim());
        Element photoEle = headerEle.getElementsByTag("img").first();
        if (photoEle != null) {
            node.setPhoto("https:" + photoEle.attr("src"));
        }
        node.setTopicNum(Integer.parseInt(headerEle.getElementsByTag("strong").first().text()));
        for (Element aEle : headerEle.getElementsByTag("a")) {
            if (aEle.text().equals("加入收藏") || aEle.text().equals("取消收藏")) {
                node.setId(Integer.parseInt(aEle.attr("href").split("node/")[1].split("\\?once=")[0]));
                node.setCollectHref("https:" + aEle.attr("href"));
            }
        }
        Element spanEle = headerEle.getElementsByTag("input").first().previousElementSibling().previousElementSibling();
        if (spanEle.tagName().equals("span") && !spanEle.attr("class").equals("chevron")) {
            node.setDesc(spanEle.text());
        }
        for (Element strongEle : doc.getElementById("Rightbar").getElementsByTag("strong")) {
            if (strongEle.text().equals("父节点")) {
                Node parentNode = new Node();
                parentNode.setPhoto("https:" + strongEle.parent().getElementsByTag("img").first().attr("src"));
                parentNode.setName(strongEle.parent().getElementsByTag("a").first().attr("href").split("/")[2]);
                parentNode.setTitle(strongEle.parent().getElementsByTag("a").first().text());
                node.setParent(parentNode);
            } else if (strongEle.text().equals("Related Nodes")) {
                Node relatedNode;
                Element aEle;
                for (Element imgEle : strongEle.parent().getElementsByTag("img")) {
                    relatedNode = new Node();
                    relatedNode.setPhoto("https:" + imgEle.attr("src"));
                    aEle = imgEle.nextElementSibling();
                    relatedNode.setName(aEle.attr("href").split("/")[2]);
                    relatedNode.setTitle(aEle.text());
                    node.getRelatives().add(relatedNode);
                }
            } else if (strongEle.text().equals("子节点")) {
                Node childNode;
                Element aEle;
                for (Element imgEle : strongEle.parent().getElementsByTag("img")) {
                    childNode = new Node();
                    childNode.setPhoto("https:" + imgEle.attr("src"));
                    aEle = imgEle.nextElementSibling();
                    childNode.setName(aEle.attr("href").split("/")[2]);
                    childNode.setTitle(aEle.text());
                    node.getChildren().add(childNode);
                }
            }
        }
        return node;
    }

    public static Map<String, String> parseSigninParams(String html) {
        Map<String, String> signinParams = new HashMap<>();
        Document doc = Jsoup.parse(html);
        Element formEle = doc.select("form[action='/signin']").first();
        for (Element inputEle : formEle.getElementsByTag("input")) {
            if (inputEle.attr("type").equals("text")) {
                signinParams.put(inputEle.attr("name"), "username");
            } else if (inputEle.attr("type").equals("password")) {
                signinParams.put(inputEle.attr("name"), "password");
            } else if (inputEle.attr("type").equals("hidden")) {
                signinParams.put(inputEle.attr("name"), inputEle.attr("value"));
            }
        }
        return signinParams;
    }

    public static String parseSigninFailedMsg(String html) {
        Element problemEle = Jsoup.parse(html).getElementsByClass("problem").first();
        if (problemEle != null) {
            return problemEle.getElementsByTag("li").first().text();
        }
        return null;
    }

    public static int parseSessionId(String html) {
        Element topEle = Jsoup.parse(html).getElementById("Top");
        if (topEle != null) {
            for (Element aEle : topEle.getElementsByTag("a")) {
                if (aEle.text().equals("登出")) {
                    return Integer.parseInt(aEle.attr("onclick").split("once=")[1].split("'; }")[0]);
                }
            }
        }
        return -1;
    }

    public static String parseSignoutResultMsg(String html) {
        Element buttonEle = Jsoup.parse(html).getElementById("Main").getElementsByTag("input").first();
        if (buttonEle != null) {
            return buttonEle.attr("value");
        } else {
            return "";
        }
    }

    public static List<Node> parseMyFollowingNodes(String html) {
        List<Node> collectedNodes = new ArrayList<>();
        for (Element aEle : Jsoup.parse(html).getElementById("Main").getElementsByTag("a")) {
            if (aEle.text().equals("V2EX")) continue;
            Node collectedNode = new Node();
            collectedNode.setName(aEle.attr("href").split("/")[2]);
            collectedNode.setPhoto("https:" + aEle.getElementsByTag("img").first().attr("src"));
            collectedNode.setTitle(aEle.text().split(" ")[0]);
            collectedNode.setTopicNum(Integer.parseInt(aEle.text().split(" ")[1]));
            collectedNodes.add(collectedNode);
        }
        return collectedNodes;
    }

    public static List<Member> parseMyFollowingMembers(String html) {
        List<Member> followedMebers = new ArrayList<>();
        for (Element boxEle : Jsoup.parse(html).getElementById("Rightbar").getElementsByClass("box")) {
            if (boxEle.text().contains("我关注的人")) {
                for(Element imgEle : boxEle.getElementsByTag("img")) {
                    Member member = new Member();
                    member.setPhoto("https:" + imgEle.attr("src"));
                    member.setUsername(imgEle.parent().attr("href").split("/")[2]);
                    followedMebers.add(member);
                }
            }
        }
        return followedMebers;
    }
}
