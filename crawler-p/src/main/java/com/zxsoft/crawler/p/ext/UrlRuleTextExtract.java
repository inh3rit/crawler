package com.zxsoft.crawler.p.ext;

import com.jfinal.kit.StrKit;
import com.zxsoft.crawler.common.entity.sync.UrlRuleEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 简介：根据已知规则抽取网页正文
 * 作者：施洋青
 * 时间：2016-08-12 15:46
 */
public class UrlRuleTextExtract {

    public final static String URL_RULE_RETURN_TEXT_IS_NULL = "url_rule_return_text_is_null;"; //正文抽取 结果为空
    public final static String URL_RULE_NOT_IN_CACHEMAP = "url_rule_not_in_cache;"; //正文抽取 没有该网站规则


    /**
     * 站点规则缓存数据
     */
    private static Map<String, UrlRuleEntity> cacheMap = null;
    private static UrlRuleTextExtract extract = null;


    private UrlRuleTextExtract(Map<String, UrlRuleEntity> _cacheMap) {
        cacheMap = _cacheMap;
    }

    /**
     * 获取单例对象，出于对内存考虑
     *
     * @param cacheMap 缓存数据集合
     * @return 单例对象
     */
    public static UrlRuleTextExtract getUrlRuleTextExtract(Map<String, UrlRuleEntity> cacheMap) {
        if (extract != null)
            return extract;
        extract = new UrlRuleTextExtract(cacheMap);
        return extract;
    }

    /**
     * 重新载入规则缓存数据
     *
     * @param cacheMap 缓存数据集合
     * @return 单例对象
     */
    public static UrlRuleTextExtract reloadCacheMap(Map<String, UrlRuleEntity> cacheMap) {
        extract = null;
        return getUrlRuleTextExtract(cacheMap);
    }


    /**
     * 传入网页html源码以及对应正文解析规则抽取正文
     *
     * @param _html 网页的html源码
     * @param key   该网页的对应规则 key
     * @param value 该网页的对应规则 value
     * @return 网页正文
     */
    public String geText(String _html, String key, String value) {
        Document doc = Jsoup.parse(_html);
        return geText(doc, key, value);
    }

    /**
     * 传入网页Document，根据对应规则抽取网页正文
     *
     * @param doc   网页 org.jsoup.nodes.Document 对象
     * @param key   该网页的对应规则 key
     * @param value 该网页的对应规则 value
     * @return 网页正文
     */
    public String geText(Document doc, String key, String value) {
        if (doc == null)
//            return URL_RULE_RETURN_TEXT_IS_NULL;
            return null;

        if (StrKit.isBlank(key) || StrKit.isBlank(value))
//            return URL_RULE_RETURN_TEXT_IS_NULL;
            return null;

        key = key.trim().replaceAll("\"", "");
        value = value.trim().replaceAll("\"", "");
        String key_2 = "id".equals(key) ? "class" : "id";

        Elements links = doc.getElementsByAttributeValue(key, value);
        if (links == null || links.size() <= 0)
            links = doc.getElementsByAttributeValue(key_2, value);

        if (links == null || links.size() <= 0)
//            return URL_RULE_RETURN_TEXT_IS_NULL;
            return null;

        List<TextNode> textNodes = new ArrayList<>();
        textNodes = callbackGetTextNode(textNodes, links, -1, -1);
        StringBuffer html = new StringBuffer();

        for (TextNode textNode : textNodes) {
            if (StrKit.notBlank(textNode.text())) {
                html.append(textNode.text());
                if (textNode.text().trim().length() > 4)
                    html.append("\n");
                else
                    html.append(" ");
            }
        }
        return html.toString();
    }

    /**
     * 递归函数，寻找最子节点，获取最小子节点的文本信息
     *
     * @param textNodes 文本信息节点
     * @param elements  节点列表
     * @return List<TextNode></>
     */
    private List<TextNode> callbackGetTextNode(List<TextNode> textNodes, Elements elements, int childNodeSize, int previousChildNodeSize) {
        if (textNodes == null)
            textNodes = new ArrayList<>();

        for (Element element : elements) {
            List<Node> nodeList = element.childNodes();
            for (Node node : nodeList) {
                if (node instanceof TextNode) {
                    if (childNodeSize == previousChildNodeSize) {
                        textNodes.add((TextNode) node);
                    } else if (textNodes.size() > 0 && (childNodeSize == 1 || previousChildNodeSize == 0)) {
                        int index = textNodes.size() - 1;
                        TextNode textNode = textNodes.get(index);
                        textNode.text(textNode.text() + ((TextNode) node).text());
                        textNodes.set(index, textNode);
                    } else {
                        textNodes.add((TextNode) node);
                    }
                } else if (node instanceof Element) {
                    Element node1 = (Element) node;
                    Elements elements1;
                    elements1 = new Elements(node1);
                    callbackGetTextNode(textNodes, elements1, elements1.size(), childNodeSize);
                    childNodeSize = node1.childNodeSize();
                }
            }
        }
        return textNodes;
    }

    /**
     * 传入网页html源码以及host查找缓存中存在的规则抽取正文
     *
     * @param _html 网页的html源码
     * @param _url  网页地址，也可是网页站点的host
     * @return 网页正文
     */

    public String geText(String _html, String _url) {
        UrlRuleEntity rule = getUrl_rule(_url);
        if (rule == null)
//            return URL_RULE_NOT_IN_CACHEMAP;
            return null;
        String[] content_element = rule.getContentElement().split("=");
        return geText(_html, content_element[0], content_element[1]);
    }


    /**
     * 传入网页Document以及host查找缓存中存在的规则抽取正文
     *
     * @param doc  网页 org.jsoup.nodes.Document 对象
     * @param _url 网页地址，也可站点的host
     * @return 网页正文
     */
    public String geText(Document doc, String _url) {
        UrlRuleEntity rule = getUrl_rule(_url);
        if (rule == null)
            return URL_RULE_NOT_IN_CACHEMAP;
        String[] content_element = rule.getContentElement().split("=");
        return geText(doc, content_element[0], content_element[1]);
    }


    /**
     * 根据传入的域名，获取对应站点规则
     *
     * @param _url 网页地址，也可是网页站点的host
     * @return 网页站点的对应规则对象
     */
    private UrlRuleEntity getUrl_rule(String _url) {
        try {
            if (StrKit.isBlank(_url))
                return null;
            if (cacheMap == null || cacheMap.size() <= 0)
                return null;

            UrlRuleEntity rule = null;
            String key = _url.substring(_url.indexOf("://") + 3, _url.length());
            // 1、频道式查找
            do {
                rule = cacheMap.get(key);
                if (rule == null)
                    rule = cacheMap.get(key + "/");
                key = key.substring(0, key.lastIndexOf("/"));
            } while (rule == null && key.lastIndexOf("/") > 1 && key.length() > 8);

            // 2、普通主域名查找规则
            key = new java.net.URL(_url).getHost();// 获取域名
            if (rule == null)
                rule = cacheMap.get(key);

            // 3、子域名查找规则
            if (rule == null)
                rule = cacheMap.get("www." + key.substring(key.indexOf(".") + 1, key.length()));
            if (rule == null)
                rule = cacheMap.get(key.substring(key.indexOf(".") + 1, key.length()));

            return rule;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
