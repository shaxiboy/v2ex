package com.hjx.v2ex.util;

import android.text.TextUtils;

import com.hjx.v2ex.entity.Member;
import com.hjx.v2ex.entity.Node;
import com.hjx.v2ex.entity.NodeGuide;
import com.hjx.v2ex.entity.PageData;
import com.hjx.v2ex.entity.NodePlane;
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

    public static List<Topic> parseTopicList(String html) {
        List<Topic> topics = new ArrayList<>();
        Elements elements = Jsoup.parse(html).body().getElementsByClass("cell item");
        for (Element element : elements) {
            topics.add(parseTopicItem(element));
        }
        return topics;
    }

    public static void parseAllTopicList(PageData<Topic> pageData, String html) {
        List<Topic> topics = new ArrayList<>();
        Element mainEle = Jsoup.parse(html).body().getElementById("Main");
        for (Element element : mainEle.getElementsByClass("cell item")) {
            topics.add(parseTopicItem(element));
        }
        pageData.setCurrentPageItems(topics);
        Element summarizeEle = mainEle.getElementsByClass("header").first().getElementsByClass("fade").first();
        pageData.setTotalItems(Integer.parseInt(summarizeEle.text().split("共")[1].split("个")[0].trim()));
        pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM -1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
    }

    public static void parseNodeTopicList(PageData<Topic> pageData, String html) {
        List<Topic> topics = new ArrayList<>();
        Element topicsEle = Jsoup.parse(html).body().getElementById("TopicsNode");
        for (Element element : topicsEle.children()) {
            topics.add(parseTopicItem(element));
        }
        pageData.setCurrentPageItems(topics);
        Element summarizeEle = topicsEle.nextElementSibling().nextElementSibling();
        pageData.setTotalItems(Integer.parseInt(summarizeEle.text().split("共")[1].split("个")[0].trim()));
        pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM -1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
    }

    public static void parseMemberTopicList(PageData<Topic> pageData, String html) {
        Document doc = Jsoup.parse(html);
        List<Topic> topics = new ArrayList<>();
        for (Element element : doc.getElementsByClass("cell item")) {
            topics.add(parseTopicItem(element));
        }
        if(topics.isEmpty()) {
            return;
        }
        pageData.setCurrentPageItems(topics);
        Element summarizeEle = doc.getElementsByClass("header").first();
        pageData.setTotalItems(Integer.parseInt(summarizeEle.text().split("主题总数  ")[1].trim()));
        pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM -1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
    }

    public static Topic parseTopicItem(Element element) {
        Topic topic = new Topic();
        Member member = new Member();
        Node node = new Node();
        Elements tds = element.getElementsByTag("td");
        int mainTDIndex = 0;//会员发表的主题页中的主题项没有会员信息
        if(tds.size() >= 4) {
            mainTDIndex = 2;
        }

        if(mainTDIndex == 2) {
            Element memberTD = tds.get(0);
            member.setUsername(memberTD.getElementsByTag("a").first().attr("href").split("/")[2]);
            member.setPhoto("https:" + memberTD.getElementsByTag("img").first().attr("src"));
        }

        Element mainTD = tds.get(mainTDIndex);
        Element titleSpan =  mainTD.getElementsByTag("span").first();
        Element titleA =titleSpan.getElementsByTag("a").first();
        topic.setId(Integer.parseInt(titleA.attr("href").split("#")[0].split("/")[2]));
        topic.setTitle(titleA.text());

        Element bottomSpan =  mainTD.getElementsByTag("span").get(1);
        org.jsoup.nodes.Node secondNode = bottomSpan.childNode(1);

        //首页主题项中有节点信息，节点页中则无
        if(secondNode instanceof Element && ((Element) secondNode).tagName().equals("a")) {
            node.setName(secondNode.attr("href").split("/")[2]);
            node.setTitle(((Element) secondNode).text());
        }

        if(secondNode instanceof TextNode) {
            //节点页
            topic.setLastRepliedTime(((TextNode) secondNode).text().split("  •  ")[1]);
        } else {
            //首页
            if(bottomSpan.childNodeSize() >=5 ) {
                topic.setLastRepliedTime(((TextNode) bottomSpan.childNode(4)).text().split("  •  ")[1]);
            }
        }

        Element replyTD = tds.get(mainTDIndex + 1);
        String reply = replyTD.text();
        if(!TextUtils.isEmpty(reply)) {
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
        for(Element psEle : doc.getElementsByClass("subtle")) {
            psList.add(parseTopicPS(psEle));
        }
        topic.setPsList(psList);

        Element topicButtons = doc.getElementsByClass("topic_buttons").first();
        if(topicButtons != null) {
            //登录以后才有
            String thanksInfoEle = topicButtons.getElementsByTag("div").first().text();
            for(String info : thanksInfoEle.split("  ∙  ")) {
                if(info.contains(" 人收藏")) {
                    topic.setCollectedNum(Integer.parseInt(info.split(" 人收藏")[0]));
                } else if(info.contains(" 人感谢")) {
                    topic.setThanksNum(Integer.parseInt(info.split(" 人感谢")[0]));
                }
            }
        }

        Element replyBox = doc.getElementById("Main").getElementsByClass("box").get(1);
        if(replyBox.children().size() > 1) {
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

    public static void parseTopicReplies(PageData<Reply> pageData, String html) {
        Document doc = Jsoup.parse(html);
        Element replyBox = doc.getElementById("Main").getElementsByClass("box").get(1);
        pageData.setTotalItems(Integer.parseInt(replyBox.getElementsByTag("div").get(0).getElementsByTag("span").first().text().split(" 回复 ")[0]));
        pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPIC_REPLIES_ITEM_NUM - 1) / RetrofitService.PAGE_TOPIC_REPLIES_ITEM_NUM);
        List<Reply> replies = new ArrayList<>();
        for(Element divEle : replyBox.getElementsByTag("div")) {
            if(divEle.attr("id") != null && divEle.attr("id").startsWith("r_")) {
                replies.add(parseTopicReply(divEle));
            }
        }
        pageData.setCurrentPageItems(replies);
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
        if(timeInfo.contains(" via ")) {
            timeInfo = timeInfo.split(" via ")[0];
        }
        reply.setReplyTime(timeInfo);
        reply.setContent(mainTD.getElementsByClass("reply_content").first().html());
        return reply;
    }

    public static NodePlane parseNodePlane(String html) {
        NodePlane nodePlane = new NodePlane();
        Document doc = Jsoup.parse(html);
        String text = doc.getElementsByTag("h2").first().text();
        int num = Integer.parseInt(Pattern.compile("[^0-9]").matcher(text).replaceAll(""));
        nodePlane.setNodeCount(num);
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
            nodePlane.getNodeSections().put(tag, nodes);
        }
        return nodePlane;
    }

    public static NodeGuide parseNodeGuide(String html) {
        NodeGuide nodeGuide = new NodeGuide();
        Document doc = Jsoup.parse(html);
        Element boxEle = doc.getElementById("Main").getElementsByClass("box").get(1);
        for(Element div : boxEle.children()) {
            Elements tds = div.getElementsByTag("td");
            if(tds.size() >= 2) {
                List<Node> nodeList = new ArrayList<>();
                for(Element nodeEle : tds.get(1).getElementsByTag("a")) {
                    Node node = new Node();
                    node.setName(nodeEle.attr("href").split("/")[2]);
                    node.setTitle(nodeEle.text());
                    nodeList.add(node);
                }
                nodeGuide.getNodeSections().put(tds.first().text(), nodeList);
            }
        }
        return nodeGuide;
    }

    public static List<Node> parseHottestNodes(String html) {
        List<Node> hottestNodes = new ArrayList<>();
        Element boxEle = Jsoup.parse(html).getElementById("Rightbar").getElementsByClass("box").get(3);
        if(boxEle.getElementsByClass("cell").first().text().equals("最热节点")) {
            for(Element nodeEle : boxEle.getElementsByClass("cell").get(1).getElementsByTag("a")) {
                Node node = new Node();
                node.setName(nodeEle.attr("href").split("/")[2]);
                node.setTitle(nodeEle.text());
                hottestNodes.add(node);
            }
        }
        return hottestNodes;
    }

    public static Map<String, String> parseSigninParams(String html) {
        Map<String, String> signinParams = new HashMap<>();
        Document doc = Jsoup.parse(html);
        Element formEle = doc.select("form[action='/signin']").first();
        for(Element inputEle : formEle.getElementsByTag("input")) {
            if(inputEle.attr("type").equals("text")) {
                signinParams.put(inputEle.attr("name"), "username");
            } else if(inputEle.attr("type").equals("password")) {
                signinParams.put(inputEle.attr("name"), "password");
            } else if(inputEle.attr("type").equals("hidden")) {
                signinParams.put(inputEle.attr("name"), inputEle.attr("value"));
            }
        }
        return signinParams;
    }

    public static String parseSigninFailedMsg(String html) {
        Element problemEle = Jsoup.parse(html).getElementsByClass("problem").first();
        if(problemEle != null) {
            return problemEle.getElementsByTag("li").first().text();
        }
        return null;
    }

    public static int parseSessionId(String html) {
        Element topEle = Jsoup.parse(html).getElementById("Top");
        if(topEle != null) {
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
        if(buttonEle != null) {
            return buttonEle.attr("value");
        } else {
            return "";
        }
    }
}
