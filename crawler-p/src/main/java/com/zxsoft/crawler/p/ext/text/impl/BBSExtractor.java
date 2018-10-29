package com.zxsoft.crawler.p.ext.text.impl;

import com.zxsoft.crawler.p.ext.text.AbstractExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tony on 16-9-18.
 */
public class BBSExtractor extends AbstractExtractor {

    private static BBSExtractor extractor;

    /**
     * 私有化构造方法
     */
    protected BBSExtractor() {

    }

    /**
     * 获取操作对象单实例
     *
     * @return PHPWindBBSExtractor对象
     */
    public static BBSExtractor getInstance() {
        if (null == extractor)
            extractor = new BBSExtractor();
        return extractor;
    }

    public String getContentByHtml(String html, String key, String value) {
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementsByAttributeValue(key, value).first();

        StringBuffer buffer = new StringBuffer();
        for (TextNode tNode : getTextNodes(element, null)) {
            buffer.append(tNode.text()).append("/n");
        }

        return buffer.toString();
    }

    protected List<TextNode> getTextNodes(Node node, List<TextNode> nodeList) {
        if (null == nodeList)
            nodeList = new ArrayList<>();

        if (node instanceof Element) {
            Element tag = (Element) node;
            for (Node n: tag.childNodes()){
                getTextNodes(n, nodeList);
            }

        } else if (node instanceof TextNode) {
            nodeList.add((TextNode) node);
        }

        return nodeList;
    }
}
