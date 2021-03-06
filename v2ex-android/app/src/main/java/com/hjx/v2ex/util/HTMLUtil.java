package com.hjx.v2ex.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.hjx.v2ex.bean.FavoriteMembers;
import com.hjx.v2ex.bean.FavoriteNodes;
import com.hjx.v2ex.bean.Member;
import com.hjx.v2ex.bean.MemberFavoriteResult;
import com.hjx.v2ex.bean.MemberMoreInfo;
import com.hjx.v2ex.bean.MemberTopicReplies;
import com.hjx.v2ex.bean.NewTopicOnce;
import com.hjx.v2ex.bean.NewTopicResult;
import com.hjx.v2ex.bean.Node;
import com.hjx.v2ex.bean.NodeFavoriteResult;
import com.hjx.v2ex.bean.NodePage;
import com.hjx.v2ex.bean.NodesAll;
import com.hjx.v2ex.bean.NodesHottest;
import com.hjx.v2ex.bean.NodesNavigation;
import com.hjx.v2ex.bean.Notification;
import com.hjx.v2ex.bean.NotificationsPageData;
import com.hjx.v2ex.bean.PageData;
import com.hjx.v2ex.bean.Reply;
import com.hjx.v2ex.bean.ReplyTopicResult;
import com.hjx.v2ex.bean.SigninParams;
import com.hjx.v2ex.bean.SigninResult;
import com.hjx.v2ex.bean.SignoutResult;
import com.hjx.v2ex.bean.Topic;
import com.hjx.v2ex.bean.TopicFavoriteResult;
import com.hjx.v2ex.bean.TopicPage;
import com.hjx.v2ex.bean.TopicsPageData;
import com.hjx.v2ex.bean.UnReadNotificationNum;
import com.hjx.v2ex.bean.V2EXIntroduction;
import com.hjx.v2ex.bean.V2EXStatistics;
import com.hjx.v2ex.network.RetrofitService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Created by shaxiboy on 2017/3/8 0008.
 */

public class HTMLUtil {

    public static V2EXIntroduction parseV2EXIntroduction(String html) {
        V2EXIntroduction introduction = new V2EXIntroduction();
        introduction.setIntroduction(Jsoup.parse(html).getElementById("Main").getElementsByClass("page").first().html());
        return introduction;
    }

    public static V2EXStatistics parseV2EXStatistics(String html) {
        V2EXStatistics statistics = new V2EXStatistics();
        Document doc = Jsoup.parse(html);
        boolean homePage = false;
        for(Element boxEle : doc.getElementById("Rightbar").getElementsByClass("box")) {
            if(boxEle.text().contains("社区运行状况")) {
                homePage = true;
                Element cell = boxEle.getElementsByClass("cell").get(1);
                statistics.setMembers(parseInt(cell.getElementsByTag("td").get(1).text()));
                statistics.setTopics(parseInt(cell.getElementsByTag("td").get(3).text()));
                statistics.setReplies(parseInt(cell.getElementsByTag("td").get(5).text()));
                break;
            }
        }
        //所有节点页面
        if(!homePage) {
            String text = doc.getElementById("Main").getElementsByClass("box").first().getElementsByTag("h2").first().text();
            int num = parseInt(V2EXUtil.getNumber(text));
            statistics.setNodes(num);
        }
        return statistics;
    }

    public static TopicsPageData parseTopicsPageData(String html) {
        return parseTopicPageData(Jsoup.parse(html));
    }

    private static TopicsPageData parseTopicPageData(Document doc) {
        TopicsPageData topicsPageData = new TopicsPageData();
        Element mainDiv = doc.getElementById("Main");
        Elements topicDivs = mainDiv.getElementsByClass("cell item");
        Element headerDiv = null;
        Element pageNavigationDiv = null;
        if(topicDivs.isEmpty()) {
            //节点主题页面
            Element topicsNodeDiv = mainDiv.getElementById("TopicsNode");
            if(topicsNodeDiv != null) {
                topicDivs = topicsNodeDiv.children();
                headerDiv = topicsNodeDiv.parent().getElementsByClass("node_header").first();
                Element elementSibling = headerDiv.nextElementSibling();
                if(elementSibling != null && elementSibling.attr("class").equals("cell")) {
                    if(!elementSibling.getElementsByTag("input").isEmpty()) {
                        pageNavigationDiv = elementSibling;
                    }
                }
            }
        }
        for(Element topicDiv : topicDivs) {
            topicsPageData.getTopics().getCurrentPageItems().add(parseTopicDiv(topicDiv));
        }
        if(!topicDivs.isEmpty()) {
            Element firstSibling = topicDivs.first().previousElementSibling();
            if(firstSibling != null) {
                if(firstSibling.attr("class").equals("cell")) {
                    if(!firstSibling.getElementsByTag("input").isEmpty()) {
                        pageNavigationDiv = firstSibling;
                    }
                } else if(firstSibling.attr("class").equals("header")) {
                    headerDiv = firstSibling;
                }
                Element secondSibling = firstSibling.previousElementSibling();
                if(secondSibling != null && secondSibling.attr("class").equals("header")) {
                    headerDiv = secondSibling;
                }
            }
        }
        if(headerDiv != null) {
            for(Element span : headerDiv.getElementsByTag("span")) {
                if(span.text().contains("主题总数")) {
                    Element elementSibling = span.nextElementSibling();
                    if(elementSibling != null && elementSibling.tagName().equals("strong")) {
                        topicsPageData.getTopics().setTotalItems(Integer.parseInt(elementSibling.text()));
                    }
                } else if(span.text().contains("个主题")) {
                    //最近的主题页面
                    topicsPageData.getTopics().setTotalItems(Integer.parseInt(V2EXUtil.getNumber(span.text())));
                }
            }
        } else {
            //网站首页
            topicsPageData.getTopics().setTotalItems(topicsPageData.getTopics().getCurrentPageItems().size());
        }
        if(pageNavigationDiv != null) {
            topicsPageData.getTopics().setCurrentPage(Integer.parseInt(pageNavigationDiv.getElementsByTag("input").first().attr("value")));
            topicsPageData.getTopics().setTotalPage(Integer.parseInt(pageNavigationDiv.getElementsByTag("a").last().text()));
        } else {
            topicsPageData.getTopics().setCurrentPage(1);
            topicsPageData.getTopics().setTotalPage(1);
        }
        return topicsPageData;
    }

    public static Topic parseTopicDiv(Element topicDiv) {
        Topic topic = new Topic();
        Member member = new Member();
        Node node = new Node();
        Elements tds = topicDiv.getElementsByTag("td");
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
        if(member.getUsername() != null) {
            topic.setMember(member);
        }
        if(node.getName() != null) {
            topic.setNode(node);
        }

        return topic;
    }

    public static TopicPage parseTopicPage(String html) {
        TopicPage topicPage = new TopicPage();
        Document doc = Jsoup.parse(html);
        topicPage.setTopic(parseTopicDetails(doc));
        topicPage.setReplies(parseTopicReplies(doc));
        return topicPage;
    }

    public static Topic parseTopicDetails(Document doc) {
        Topic topic = new Topic();
        Element headerEle = doc.getElementById("Main").getElementsByClass("header").first();

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
            topic.setContent(contentEle.html());
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
                    String topicId = V2EXUtil.getNumber(thankInfo.split("'")[0]);
                    topic.setId(parseInt(topicId));
                    String thankToken = thankInfo.split("'")[1];
                    topic.setThankToken(thankToken);
                }
            }
            String thanksInfoEle = topicButtons.getElementsByTag("div").first().text();
            for (String info : thanksInfoEle.split("∙")) {
                int num = parseInt(V2EXUtil.getNumber(info));
                if (info.contains("人收藏")) {
                    topic.setCollectedNum(num);
                } else if (info.contains("人感谢")) {
                    topic.setThanksNum(num);
                }
            }
        }

        Element replyBox = doc.getElementById("Main").getElementsByClass("box").get(1);
        if (replyBox.children().size() > 1) {
            String text = replyBox.getElementsByTag("div").get(0).getElementsByTag("span").first().text().split("回复")[0];
            text = text.substring(0, text.length() - 1);
            topic.setReplyNum(parseInt(text));
        }
        for(Element inputEle : doc.getElementById("Main").getElementsByTag("input")) {
            if(inputEle.attr("name").equals("once")) {
                topic.setReplyOnce(Integer.parseInt(inputEle.attr("value")));
            }
        }
        return topic;
    }

    public static Topic parseTopicDetails(String html) {
        return parseTopicDetails(Jsoup.parse(html));
    }

    private static Topic.PS parseTopicPS(Element psEle) {
        Topic.PS ps = new Topic.PS();
        ps.setTime(psEle.getElementsByTag("span").first().text().split("  ·  ")[1]);
        ps.setContent(psEle.getElementsByClass("topic_content").first().html());
        return ps;
    }

    public static PageData<Reply> parseTopicReplies(String html) {
        return parseTopicReplies(Jsoup.parse(html));
    }

    private static PageData<Reply> parseTopicReplies(Document doc) {
        PageData<Reply> pageData = new PageData<>();
        Element replyBox = doc.getElementById("Main").getElementsByClass("box").get(1);
        String replyNumInfo = replyBox.getElementsByTag("div").get(0).getElementsByTag("span").first().text();
        if(replyNumInfo.equals("目前尚无回复")) {
            return pageData;
        }
        String text = replyNumInfo.split("回复")[0];
        text = text.substring(0, text.length() - 1);
        pageData.setTotalItems(parseInt(text));
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
        reply.setContent(mainTD.getElementsByClass("reply_content").first().html());
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
                member.setFavoriteURL(RetrofitService.BASE_URL + inputEle.attr("onclick").split("location.href")[1].split("'")[1].substring(1));
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

    public static MemberTopicReplies parseMemberTopicReplies(String html) {
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
                reply.setContent(topicDiv.nextElementSibling().html());

                Map<Reply, Topic> replyMap = new HashMap<>();
                replyMap.put(reply, topic);
                pageData.getCurrentPageItems().add(replyMap);
            }
        }
        return new MemberTopicReplies(pageData);
    }

    public static NodesAll parseNodesAll(String html) {
        NodesAll nodesAll = new NodesAll();
        Document doc = Jsoup.parse(html);
        String text = doc.getElementsByTag("h2").first().text();
        int num = parseInt(V2EXUtil.getNumber(text));
        nodesAll.setNodeNum(num);
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
            nodesAll.getNodeSections().put(tag, nodes);
        }
        return nodesAll;
    }

    public static NodesNavigation parseNodesNavigation(String html) {
        NodesNavigation nodesNavigation = new NodesNavigation();
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
                nodesNavigation.getNodeSections().put(tds.first().text(), nodeList);
            }
        }
        return nodesNavigation;
    }

    public static NodesHottest parseNodesHottest(String html) {
        NodesHottest nodesHottest = new NodesHottest();
        Document doc = Jsoup.parse(html);
        for(Element boxEle : doc.getElementById("Rightbar").getElementsByClass("box")) {
            if (boxEle.text().contains("最热节点")) {
                for (Element nodeEle : boxEle.getElementsByClass("cell").get(1).getElementsByTag("a")) {
                    Node node = new Node();
                    node.setName(nodeEle.attr("href").split("/")[2]);
                    node.setTitle(nodeEle.text());
                    nodesHottest.getHottestNodes().add(node);
                }
                break;
            }
        }
        return nodesHottest;
    }

    public static NodePage parseNodePage(String html) {
        NodePage nodePage = new NodePage();
        Document doc = Jsoup.parse(html);
        nodePage.setNode(parseNodeDetails(doc));
        nodePage.setTopics(parseTopicPageData(doc).getTopics());
        return nodePage;
    }

    public static Node parseNodeDetails(Document doc) {
        Node node = new Node();
        Element headerEle = doc.getElementById("Main").getElementsByClass("node_header").first();
        node.setTitle(((TextNode) headerEle.getElementsByClass("chevron").first().nextSibling()).text().trim());
        Element photoEle = headerEle.getElementsByTag("img").first();
        if (photoEle != null) {
            node.setPhoto("https:" + photoEle.attr("src"));
        }
        node.setTopicNum(parseInt(headerEle.getElementsByTag("strong").first().text()));
        for (Element aEle : headerEle.getElementsByTag("a")) {
            if (aEle.text().equals("加入收藏") || aEle.text().equals("取消收藏")) {
                node.setId(parseInt(aEle.attr("href").split("node/")[1].split("\\?once=")[0]));
                node.setFavoriteURL("https:" + aEle.attr("href"));
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
                if(inputEle.attr("placeholder").contains("用户名")) signinParams.setName(inputEle.attr("name"));
                else if(inputEle.attr("placeholder").contains("验证码")) signinParams.setCode(inputEle.attr("name"));
            } else if (inputEle.attr("type").equals("password")) {
                signinParams.setPassword(inputEle.attr("name"));
            } else if (inputEle.attr("type").equals("hidden")) {
                if(inputEle.attr("name").equals("once")) {
                    signinParams.setOnce(inputEle.attr("value"));
                }
            }
        }
        for (Element divEle : formEle.getElementsByTag("div")) {
            if(divEle.attr("style").contains("background-image")) {
                String backgroundImage = divEle.attr("style").split(";")[0].split(":")[1];
                String imgUrl = RetrofitService.BASE_URL + backgroundImage.substring(7, backgroundImage.length() - 2);
                signinParams.setCodeImg(imgUrl);
                break;
            }
        }
        return signinParams;
    }

    public static SigninResult parseSigninResult(String html) {
        SigninResult result = new SigninResult();
        Document doc = Jsoup.parse(html);
        int sessionId = -1;
        sessionId = parseSignoutOnce(doc);
        if(sessionId != -1) {
            result.setSigin(true);
            result.setSignoutOnce(sessionId);
            Element imgEle = doc.getElementById("Rightbar").getElementsByClass("box").first().getElementsByTag("img").first();
            result.setPhoto("https:" + imgEle.attr("src"));
            result.setName(imgEle.parent().attr("href").split("/")[2]);
        } else {
            result.setSigin(false);
            result.setErrorMsg(parseSigninFailedMsg(doc));
        }
        return result;
    }

    public static String parseSigninFailedMsg(Document doc) {
        Element problemEle = doc.getElementsByClass("problem").first();
        if (problemEle != null) {
            return problemEle.getElementsByTag("li").first().text();
        }
        return null;
    }

    public static int parseSignoutOnce(String html) {
        return parseSignoutOnce(Jsoup.parse(html));
    }

    private static int parseSignoutOnce(Document doc) {
        int once = -1;
        Element topEle = doc.getElementById("Top");
        if (topEle != null) {
            for (Element aEle : topEle.getElementsByTag("a")) {
                if (aEle.text().equals("登出")) {
                    once = Integer.parseInt(aEle.attr("onclick").split("once=")[1].split("'")[0]);
                }
            }
        }
        return once;
    }

    public static SignoutResult parseSignoutResult(String html) {
        SignoutResult result = new SignoutResult();
        Element buttonEle = Jsoup.parse(html).getElementById("Main").getElementsByTag("input").first();
        result.setSignout(true);
        if (buttonEle != null) {
            if(buttonEle.attr("value").equals("Retry Sign Out")) {
                result.setSignout(false);
                result.setNewSignoutOnce(Integer.parseInt(buttonEle.attr("onclick").split("once=")[1].split("'")[0]));
            }
        }
        return result;
    }

    public static TopicFavoriteResult parseTopicFavoriteResult(String html) {
        Document doc = Jsoup.parse(html);
        Element topicButtons = doc.getElementsByClass("topic_buttons").first();
        if (topicButtons != null) {
            for (Element aEle : topicButtons.getElementsByTag("a")) {
                if (aEle.text().equals("加入收藏") || aEle.text().equals("取消收藏")) {
                    return new TopicFavoriteResult("https:" + aEle.attr("href"));
                }
            }
        }
        return null;
    }

    public static NodeFavoriteResult parseNodeFavoriteResult(String html) {
        Document doc = Jsoup.parse(html);
        Element headerEle = doc.getElementById("Main").getElementsByClass("node_header").first();
        for (Element aEle : headerEle.getElementsByTag("a")) {
            if (aEle.text().equals("加入收藏") || aEle.text().equals("取消收藏")) {
                return new NodeFavoriteResult("https:" + aEle.attr("href"));
            }
        }
        return null;
    }

    public static MemberFavoriteResult parseMemberFavoriteResult(String html) {
        Document doc = Jsoup.parse(html);
        Element memberInfoBox = doc.getElementById("Main").getElementsByClass("box").first();
        for (Element inputEle : memberInfoBox.getElementsByTag("input")) {
            if (inputEle.attr("value").equals("加入特别关注") || inputEle.attr("value").equals("取消特别关注")) {
                return new MemberFavoriteResult(RetrofitService.BASE_URL + inputEle.attr("onclick").split("location.href")[1].split("'")[1].substring(1));
            }
        }
        return null;
    }

    public static FavoriteNodes parseFavoriteNodes(String html) {
        FavoriteNodes favoriteNodes = new FavoriteNodes();
        for (Element aEle : Jsoup.parse(html).getElementById("Main").getElementsByTag("a")) {
            if (aEle.text().equals("V2EX") || aEle.text().equals("浏览 V2EX 全部节点")) continue;
            Node favoriteNode = new Node();
            favoriteNode.setName(aEle.attr("href").split("/")[2]);
            favoriteNode.setPhoto("https:" + aEle.getElementsByTag("img").first().attr("src"));
            favoriteNode.setTitle(aEle.text().split(" ")[0]);
            favoriteNode.setTopicNum(parseInt(aEle.text().split(" ")[1]));
            favoriteNodes.getFavoriteNodes().add(favoriteNode);
        }
        return favoriteNodes;
    }

    public static FavoriteMembers parseFavoriteMembers(String html) {
        FavoriteMembers favoriteMembers = new FavoriteMembers();
        for (Element boxEle : Jsoup.parse(html).getElementById("Rightbar").getElementsByClass("box")) {
            if (boxEle.text().contains("我关注的人")) {
                for(Element imgEle : boxEle.getElementsByTag("img")) {
                    Member member = new Member();
                    member.setPhoto("https:" + imgEle.attr("src"));
                    member.setUsername(imgEle.parent().attr("href").split("/")[2]);
                    favoriteMembers.getFavoriteMembers().add(member);
                }
            }
        }
        return favoriteMembers;
    }

    public static ReplyTopicResult parseReplyTopicResult(String html) {
        ReplyTopicResult result = new ReplyTopicResult();
        Element mainEle = Jsoup.parse(html).getElementById("Main");
        if(mainEle.children().size() == 2) result.setSuccess(false);
        for(Element inputEle : mainEle.getElementsByTag("input")) {
            if(inputEle.attr("name").equals("once")) {
                result.setReplyOnce(Integer.parseInt(inputEle.attr("value")));
            }
        }
        if(!result.isSuccess()) {
            Element liEle = mainEle.getElementsByTag("li").first();
            if(liEle != null) result.setFailedMsg(liEle.text());
        }
        return result;
    }

    public static NewTopicOnce parseNewTopicOnce(String html) {
        NewTopicOnce result = new NewTopicOnce();
        Element mainEle = Jsoup.parse(html).getElementById("Main");
        for(Element inputEle : mainEle.getElementsByTag("input")) {
            if(inputEle.attr("name").equals("once")) {
                result.setOnce(Integer.parseInt(inputEle.attr("value")));
                break;
            }
        }
        return result;
    }

    public static NewTopicResult parseNewTopicResult(String html) {
        NewTopicResult result = new NewTopicResult();
        Element mainEle = Jsoup.parse(html).getElementById("Main");
        for(Element inputEle : mainEle.getElementsByTag("input")) {
            if(inputEle.attr("value").equals("回复")) {
                result.setSuccess(true);
                for(Element aEle : mainEle.getElementsByClass("topic_buttons").first().getElementsByTag("a")) {
                    if(aEle.text().equals("加入收藏")) {
                        result.setNewTopicId(Integer.parseInt(aEle.attr("href").split("\\?")[0].split("/")[3]));
                        break;
                    }
                }
                break;
            }
        }
        if(!result.isSuccess()) {
           Element liEle = mainEle.getElementsByClass("problem").first().getElementsByTag("li").first();
            result.setFailedMsg(liEle.text());
            for(Element inputEle : mainEle.getElementsByTag("input")) {
                if(inputEle.attr("name").equals("once")) {
                    result.setNewOnce(Integer.parseInt(inputEle.attr("value")));
                    break;
                }
            }
        }
        return result;
    }

    public static UnReadNotificationNum parseUnReadNotificationNum(String html) {
        UnReadNotificationNum unReadNotificationNum = new UnReadNotificationNum();
        for(Element aEle : Jsoup.parse(html).getElementById("Rightbar").getElementsByClass("box").first().getElementsByTag("a")) {
            if(aEle.text().contains("条未读提醒")) {
                unReadNotificationNum.setNum(parseInt(V2EXUtil.getNumber(aEle.text())));
            }
        }
        return unReadNotificationNum;
    }

    public static NotificationsPageData parseNotificationsPageData(String html) {
        NotificationsPageData notificationsPageData = new NotificationsPageData();
        Element mainEle = Jsoup.parse(html).getElementById("Main");
        for(Element cellEle : mainEle.getElementsByClass("box").first().getElementsByClass("cell")) {
            if(!cellEle.getElementsByTag("input").isEmpty()) {
                notificationsPageData.getNotifications().setCurrentPage(Integer.parseInt(cellEle.getElementsByTag("input").first().attr("value")));
                notificationsPageData.getNotifications().setTotalPage(Integer.parseInt(cellEle.getElementsByTag("a").last().text()));
            } else {
                Notification notification = new Notification();
                Element tdEle = cellEle.getElementsByTag("td").get(1);
                Element firstSpanEle = tdEle.getElementsByTag("span").first();
                String firstSpanEleText = firstSpanEle.text();
                if(firstSpanEleText.contains("里回复了你")) notification.setType(Notification.NotificationType.REPLY);
                else if(firstSpanEleText.contains("时提到了你")) notification.setType(Notification.NotificationType.AT);
                else if(firstSpanEleText.contains("收藏了你发布的主题")) notification.setType(Notification.NotificationType.FAVORITE);
                else notification.setType(Notification.NotificationType.THANK);
                Member member = new Member();
                member.setUsername(firstSpanEle.getElementsByTag("a").first().text());
                notification.setMember(member);
                Topic topic = new Topic();
                Element topicEle = firstSpanEle.getElementsByTag("a").get(1);
                topic.setTitle(topicEle.text());
                topic.setId(Integer.parseInt(topicEle.attr("href").split("#")[0].split("/")[2]));
                notification.setTopic(topic);
                notification.setTime(tdEle.getElementsByTag("span").get(1).text());
                Element replyEle = tdEle.getElementsByClass("payload").first();
                if(replyEle != null) {
                    Reply reply = new Reply();
                    reply.setContent(replyEle.html());
                    notification.setReply(reply);
                }
                notificationsPageData.getNotifications().getCurrentPageItems().add(notification);
            }
        }
        if(!notificationsPageData.getNotifications().getCurrentPageItems().isEmpty()) {
            notificationsPageData.getNotifications().setTotalItems(Integer.parseInt(mainEle.getElementsByClass("header").first().getElementsByTag("strong").first().text()));
            if(notificationsPageData.getNotifications().getCurrentPage() == 0) notificationsPageData.getNotifications().setCurrentPage(1);
            if(notificationsPageData.getNotifications().getTotalPage() == 0) notificationsPageData.getNotifications().setTotalPage(1);
        }
        return notificationsPageData;
    }

    public static Bitmap parseBitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }
}
