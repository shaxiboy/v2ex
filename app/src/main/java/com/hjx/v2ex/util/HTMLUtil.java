package com.hjx.v2ex.util;

import android.text.TextUtils;

import com.hjx.v2ex.bean.FavoriteResult;
import com.hjx.v2ex.bean.HomePage;
import com.hjx.v2ex.bean.Member;
import com.hjx.v2ex.bean.MemberMoreInfo;
import com.hjx.v2ex.bean.MemberTopicRepliesPage;
import com.hjx.v2ex.bean.MemberTopicsPage;
import com.hjx.v2ex.bean.Node;
import com.hjx.v2ex.bean.NodePage;
import com.hjx.v2ex.bean.NodesPlane;
import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.bean.SigninParams;
import com.hjx.v2ex.bean.SigninResult;
import com.hjx.v2ex.bean.SignoutResult;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.bean.TopicPage;
import com.hjx.v2ex.bean.V2EX;
import com.hjx.v2ex.bean.V2EXMoreInfo;
import com.hjx.v2ex.network.RetrofitService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

/**
 * Created by shaxiboy on 2017/3/8 0008.
 */

public class HTMLUtil {

    public static V2EX parseV2EX(Document doc) {
        V2EX v = new V2EX();
        for(Element boxEle : doc.getElementById("Rightbar").getElementsByClass("box")) {
            if(boxEle.text().contains("社区运行状况")) {
                Element cell = boxEle.getElementsByClass("cell").get(1);
                v.setMembers(parseInt(cell.getElementsByTag("td").get(1).text()));
                v.setTopics(parseInt(cell.getElementsByTag("td").get(3).text()));
                v.setReplies(parseInt(cell.getElementsByTag("td").get(5).text()));
            }
        }
        return v;
    }

    public static V2EXMoreInfo parseV2EXMoreInfo(String html) {
        V2EXMoreInfo moreInfo = new V2EXMoreInfo();
        moreInfo.setMoreInfo(Jsoup.parse(html).getElementById("Main").getElementsByClass("page").first().text());
        return moreInfo;
    }

    public static List<Topic> parseTopicsFromTabPage(String html) {
        List<Topic> topics = new ArrayList<>();
        Elements elements = Jsoup.parse(html).body().getElementsByClass("cell item");
        for (Element element : elements) {
            topics.add(parseTopicItemFromList(element));
        }
        return topics;
    }

    public static HomePage parseHomePage(String html) {
        HomePage homePage = new HomePage();
        List<Topic> topics = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.body().getElementsByClass("cell item");
        for (Element element : elements) {
            topics.add(parseTopicItemFromList(element));
        }
        homePage.setTopics(topics);
        homePage.setV2ex(parseV2EX(document));
        homePage.setHottestNodes(parseHottestNodes(document));
        homePage.setNodeGuide(parseNodesGuide(document));
        return homePage;
    }

    public static PageData<Topic> parseAllTopics(String html) {
        PageData<Topic> pageData = new PageData<>();
        Element mainEle = Jsoup.parse(html).body().getElementById("Main");
        for (Element element : mainEle.getElementsByClass("cell item")) {
            pageData.getCurrentPageItems().add(parseTopicItemFromList(element));
        }
        Element summarizeEle = mainEle.getElementsByClass("header").first().getElementsByClass("fade").first();
        pageData.setTotalItems(parseInt(summarizeEle.text().split("共")[1].split("个")[0].trim()));
        pageData.setCurrentPage(1);
        Element inputEle = mainEle.getElementsByClass("header").first().nextElementSibling().getElementsByTag("input").first();
        if(inputEle != null) {
            pageData.setCurrentPage(parseInt(inputEle.attr("value")));
        }
        pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM - 1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
        return pageData;
    }

    public static PageData<Topic> parseNodeTopics(Document doc) {
        PageData<Topic> pageData = new PageData<>();
        Element topicsEle = doc.body().getElementById("TopicsNode");
        for (Element element : topicsEle.children()) {
            pageData.getCurrentPageItems().add(parseTopicItemFromList(element));
        }
//        Element summarizeEle = topicsEle.nextElementSibling().nextElementSibling();
//        if(summarizeEle == null) {
//            summarizeEle = topicsEle.nextElementSibling();
//        }
        Element summarizeEle = topicsEle.lastElementSibling();
        pageData.setTotalItems(parseInt(summarizeEle.text().split("共")[1].split("个")[0].trim()));
        pageData.setCurrentPage(1);
        Element sibling = topicsEle.previousElementSibling();
        if(sibling.attr("class").equals("cell")) {
            Element inputEle = sibling.getElementsByTag("input").first();
            if(inputEle != null) {
                pageData.setCurrentPage(parseInt(inputEle.attr("value")));
            }
        }
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
        topic.setId(parseInt(titleA.attr("href").split("#")[0].split("/")[2]));
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
            topic.setReplyNum(parseInt(reply));
        }
        topic.setMember(member);
        topic.setNode(node);

        return topic;
    }

    public static TopicPage parseTopicPage(String html) {
        TopicPage topicPage = new TopicPage();
        topicPage.setTopic(parseTopicDetails(html));
        topicPage.setReplies(parseTopicReplies(html));
        return topicPage;
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


        Element contentEle = doc.getElementsByClass("topic_content").first();
        if(contentEle != null) {
            topic.setContent(contentEle.text());
        }
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
                    topic.setFavoriteURL("https:" + aEle.attr("href"));
                } else if (aEle.text().equals("感谢")) {
                    String thankInfo = aEle.attr("onclick").split("thankTopic")[1];
                    String topicId = Pattern.compile("[^0-9]").matcher(thankInfo.split("'")[0]).replaceAll("");
                    topic.setId(parseInt(topicId));
                    String thankToken = thankInfo.split("'")[1];
                    topic.setThankToken(thankToken);
                }
            }
            String thanksInfoEle = topicButtons.getElementsByTag("div").first().text();
            Pattern pattern = Pattern.compile("[^0-9]");
            for (String info : thanksInfoEle.split("∙")) {
                int num = parseInt(pattern.matcher(info).replaceAll(""));
                if (info.contains("人收藏")) {
                    topic.setCollectedNum(num);
                } else if (info.contains("人感谢")) {
                    topic.setThanksNum(num);
                }
            }
        }

        Element replyBox = doc.getElementById("Main").getElementsByClass("box").get(1);
        if (replyBox.children().size() > 1) {
            topic.setReplyNum(parseInt(replyBox.getElementsByTag("div").get(0).getElementsByTag("span").first().text().split(" 回复 ")[0]));
        }
        return topic;
    }

    public static FavoriteResult parseFavoriteResult(String html) {
        Document doc = Jsoup.parse(html);
        Element topicButtons = doc.getElementsByClass("topic_buttons").first();
        if (topicButtons != null) {
            for (Element aEle : topicButtons.getElementsByTag("a")) {
                if (aEle.text().equals("加入收藏") || aEle.text().equals("取消收藏")) {
                    return new FavoriteResult("https:" + aEle.attr("href"));
                }
            }
        }
        return null;
    }

    private static Topic.PS parseTopicPS(Element psEle) {
        Topic.PS ps = new Topic.PS();
        ps.setTime(psEle.getElementsByTag("span").first().text().split("  ·  ")[1]);
        ps.setContent(psEle.getElementsByClass("topic_content").first().text());
        return ps;
    }

    public static PageData<Reply> parseTopicReplies(String html) {
        PageData<Reply> pageData = new PageData<>();
        Document doc = Jsoup.parse(html);
        Element replyBox = doc.getElementById("Main").getElementsByClass("box").get(1);
        String replyNumInfo = replyBox.getElementsByTag("div").get(0).getElementsByTag("span").first().text();
        if(replyNumInfo.equals("目前尚无回复")) {
            return pageData;
        }
        pageData.setTotalItems(parseInt(replyNumInfo.split(" 回复 ")[0]));
        pageData.setCurrentPage(1);
        Element inputEle = replyBox.getElementsByClass("cell").get(1).getElementsByTag("input").first();
        if(inputEle != null) {
            pageData.setCurrentPage(parseInt(inputEle.attr("value")));
        }
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
        reply.setContent(mainTD.getElementsByClass("reply_content").first().text());
        return reply;
    }

    public static Member parseMember(String html) {
        Member member = new Member();
        Document doc = Jsoup.parse(html);
        Element memberInfoBox = doc.getElementById("Main").getElementsByClass("box").first();
        member.setUsername(memberInfoBox.getElementsByTag("h1").first().text());
        member.setPhoto("https:" + memberInfoBox.getElementsByTag("img").first().attr("src"));
        Element basicInfoBox = memberInfoBox.child(0);
        for(Element spanEle : basicInfoBox.getElementsByTag("span")) {
            String text = spanEle.text();
            if(text.contains("号会员") && text.contains("加入于")) {
                member.setBasicInfo(text);
            }
        }
        for (Element inputEle : memberInfoBox.getElementsByTag("input")) {
            if (inputEle.attr("value").equals("加入特别关注") || inputEle.attr("value").equals("取消特别关注")) {
                member.setNoticeHref(RetrofitService.BASE_URL + inputEle.attr("onclick").split("location.href")[1].split("'")[1]);
            }
        }
        for(Element divEle : memberInfoBox.children()) {
            boolean target = true;
            for(Element child : divEle.children()) {
                if(!(child.tagName().equals("a"))) {
                    target = false;
                    break;
                }
            }
            if(target) {
                MemberMoreInfo moreInfo;
                for(Element aEle : divEle.children()) {
                    moreInfo = new MemberMoreInfo(aEle.text(), aEle.attr("href"), RetrofitService.BASE_URL + aEle.child(0).attr("src").substring(1));
                    member.getMoreInfos().add(moreInfo);
                }
            }
        }
        return member;
    }

    public static MemberTopicsPage parseMemberTopicsPage(String html) {
        PageData<Topic> pageData = new PageData<>();
        Document doc = Jsoup.parse(html);
        for (Element element : doc.getElementsByClass("cell item")) {
            pageData.getCurrentPageItems().add(parseTopicItemFromList(element));
        }
        if (!pageData.getCurrentPageItems().isEmpty()) {
            Element summarizeEle = doc.getElementsByClass("header").first();
            pageData.setTotalItems(parseInt(summarizeEle.text().split("主题总数  ")[1].trim()));
            pageData.setCurrentPage(1);
            Element inputEle = summarizeEle.nextElementSibling().getElementsByTag("input").first();
            if(inputEle != null) {
                pageData.setCurrentPage(parseInt(inputEle.attr("value")));
            }
            pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM - 1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
        }
        return new MemberTopicsPage(pageData);
    }

    public static MemberTopicRepliesPage parseMemberTopicRepliesPage(String html) {
        PageData<Map<Reply, Topic>> pageData = new PageData<>();
        Document doc = Jsoup.parse(html);
        Element strongEle =  doc.getElementById("Main").getElementsByTag("strong").get(0);
        if(strongEle != null) {
            pageData.setCurrentPage(1);
            pageData.setTotalItems(parseInt(strongEle.text()));
            pageData.setTotalPage((pageData.getTotalItems() + RetrofitService.PAGE_TOPICS_ITEM_NUM - 1) / RetrofitService.PAGE_TOPICS_ITEM_NUM);
            Element inputEle = doc.getElementById("Main").getElementsByTag("input").first();
            if(inputEle != null) {
                pageData.setCurrentPage(parseInt(inputEle.attr("value")));
            }
            for (Element topicDiv : doc.getElementById("Main").getElementsByClass("dock_area")) {
                Element nodeEle = topicDiv.getElementsByTag("a").get(1);
                Node node = new Node();
                node.setName(nodeEle.attr("href").split("/")[2]);
                node.setTitle(nodeEle.text());

                Element topicEle = topicDiv.getElementsByTag("a").get(2);
                Topic topic = new Topic();
                topic.setId(parseInt(topicEle.attr("href").split("/")[2].split("#")[0]));
                topic.setTitle(topicEle.text());
                topic.setNode(node);

                Reply reply = new Reply();
                reply.setReplyTime(topicDiv.getElementsByTag("span").first().text());
                reply.setContent(topicDiv.nextElementSibling().text());

                Map<Reply, Topic> replyMap = new HashMap<>();
                replyMap.put(reply, topic);
                pageData.getCurrentPageItems().add(replyMap);
            }
        }
        return new MemberTopicRepliesPage(pageData);
    }

    public static NodesPlane parseNodesPlane(String html) {
        NodesPlane nodesPlane = new NodesPlane();
        Document doc = Jsoup.parse(html);
        String text = doc.getElementsByTag("h2").first().text();
        int num = parseInt(Pattern.compile("[^0-9]").matcher(text).replaceAll(""));
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

    public static Map<String, List<Node>> parseNodesGuide(Document doc) {
        Map<String, List<Node>> nodesGuide = new LinkedHashMap<>();
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
                nodesGuide.put(tds.first().text(), nodeList);
            }
        }
        return nodesGuide;
    }

    public static List<Node> parseHottestNodes(Document doc) {
        List<Node> hottestNodes = new ArrayList<>();
        for(Element boxEle : doc.getElementById("Rightbar").getElementsByClass("box")) {
            if (boxEle.text().contains("最热节点")) {
                for (Element nodeEle : boxEle.getElementsByClass("cell").get(1).getElementsByTag("a")) {
                    Node node = new Node();
                    node.setName(nodeEle.attr("href").split("/")[2]);
                    node.setTitle(nodeEle.text());
                    hottestNodes.add(node);
                }
                break;
            }
        }
        return hottestNodes;
    }

    public static NodePage parseNodePage(String html) {
        NodePage nodePage = new NodePage();
        Document doc = Jsoup.parse(html);
        nodePage.setNode(parseNodeDetails(doc));
        nodePage.setTopics(parseNodeTopics(doc));
        return nodePage;
    }

    public static Node parseNodeDetails(Document doc) {
        Node node = new Node();
        Element headerEle = doc.getElementById("Main").getElementsByClass("header").first();
        node.setTitle(((TextNode) headerEle.getElementsByClass("chevron").first().nextSibling()).text().trim());
        Element photoEle = headerEle.getElementsByTag("img").first();
        if (photoEle != null) {
            node.setPhoto("https:" + photoEle.attr("src"));
        }
        node.setTopicNum(parseInt(headerEle.getElementsByTag("strong").first().text()));
        for (Element aEle : headerEle.getElementsByTag("a")) {
            if (aEle.text().equals("加入收藏") || aEle.text().equals("取消收藏")) {
                node.setId(parseInt(aEle.attr("href").split("node/")[1].split("\\?once=")[0]));
                node.setCollectHref("https:" + aEle.attr("href"));
            }
        }
        Elements spans = headerEle.getElementsByTag("span");
        String desc = spans.get(spans.size() - 1).text();
        if (! desc.equals(" › ")) {
            node.setDesc(desc);
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

    public static SigninParams parseSigninParams(String html) {
        SigninParams signinParams = new SigninParams();
        Document doc = Jsoup.parse(html);
        Element formEle = doc.select("form[action='/signin']").first();
        for (Element inputEle : formEle.getElementsByTag("input")) {
            if (inputEle.attr("type").equals("text")) {
                signinParams.setName(inputEle.attr("name"));
            } else if (inputEle.attr("type").equals("password")) {
                signinParams.setPassword(inputEle.attr("name"));
            } else if (inputEle.attr("type").equals("hidden")) {
                if(inputEle.attr("name").equals("once")) {
                    signinParams.setOnce(inputEle.attr("value"));
                }
            }
        }
        return signinParams;
    }

    public static SigninResult parseSigninResult(String html) {
        SigninResult result = new SigninResult();
        Document doc = Jsoup.parse(html);
        int sessionId = -1;
        Element topEle = doc.getElementById("Top");
        if (topEle != null) {
            for (Element aEle : topEle.getElementsByTag("a")) {
                if (aEle.text().equals("登出")) {
                    sessionId = Integer.parseInt(aEle.attr("onclick").split("once=")[1].split("'")[0]);
                }
            }
        }
        if(sessionId != -1) {
            result.setSigin(true);
            result.setSessionId(sessionId);
            Element imgEle = doc.getElementById("Rightbar").getElementsByClass("box").first().getElementsByTag("img").first();
            result.setPhoto("https:" + imgEle.attr("src"));
            result.setName(imgEle.parent().attr("href").split("/")[2]);
        } else {
            result.setSigin(false);
            result.setErrorMsg(parseSigninFailedMsg(html));
        }
        return result;
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
                    return parseInt(aEle.attr("onclick").split("once=")[1].split("'; }")[0]);
                }
            }
        }
        return -1;
    }

    public static SignoutResult parseSignoutResult(String html) {
        SignoutResult result = new SignoutResult();
        Element buttonEle = Jsoup.parse(html).getElementById("Main").getElementsByTag("input").first();
        if (buttonEle != null) {
            if(buttonEle.attr("value").equals("重新登录")) {
                result.setSignout(true);
            } else {
                result.setSignout(false);
            }
        }
        return result;
    }

    public static List<Node> parseMyFollowingNodes(String html) {
        List<Node> collectedNodes = new ArrayList<>();
        for (Element aEle : Jsoup.parse(html).getElementById("Main").getElementsByTag("a")) {
            if (aEle.text().equals("V2EX")) continue;
            Node collectedNode = new Node();
            collectedNode.setName(aEle.attr("href").split("/")[2]);
            collectedNode.setPhoto("https:" + aEle.getElementsByTag("img").first().attr("src"));
            collectedNode.setTitle(aEle.text().split(" ")[0]);
            collectedNode.setTopicNum(parseInt(aEle.text().split(" ")[1]));
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
