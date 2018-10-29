/*
* Copyright (c) 2016 Javaranger.com. All Rights Reserved.
*/
package com.zxsoft.crawler.p.cleaning.filter;

import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.p.cleaning.util.BCConvert;
import com.zxsoft.crawler.p.cleaning.util.FilterSet;
import com.zxsoft.crawler.p.cleaning.util.WordNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by 施洋青
 * DATE： 16-9-21.
 * 创建一个FilterSet，枚举了0~65535的所有char是否是某个敏感词开头的状态
 * 判断是否是 敏感词开头 | | 是 不是 获取头节点 OK--下一个字 然后逐级遍历，DFA算法
 * <p/>
 * 返回的分值越大则排除是敏感词的可能性越高 最大1.0 最小0.0
 */
public class SensitiveKeyWordFilter extends FilterChainImpl {

    private static final FilterSet set = new FilterSet(); // 存储首字
    private static final Map<Integer, WordNode> nodes = new HashMap<Integer, WordNode>(1024, 1); // 存储节点
    private static final Set<Integer> stopwdSet = new HashSet<Integer>(); // 停顿词
    private double threshold = 0.0; // 阀值

    public SensitiveKeyWordFilter() {
        threshold = 0.5;
        addFilter(this);
        init(null, null);
    }

    public SensitiveKeyWordFilter(double threshold) {
        this.threshold = threshold;
        addFilter(this);
        init(null, null);
    }

    public SensitiveKeyWordFilter(List<String> sensitiveWord) {
        threshold = 0.5;
        addFilter(this);
        init(sensitiveWord, null);
    }

    public SensitiveKeyWordFilter(double threshold, List<String> sensitiveWord) {
        this.threshold = threshold;
        addFilter(this);
        init(sensitiveWord, null);
    }

/*    static {
        try {
            init();
        } catch (Exception e) {
            // 加载失败
        }
    }*/

    private static void init(List<String> sensitiveWord, List<String> stopWord) {
        // 获取敏感词
        addSensitiveWord(sensitiveWord == null || sensitiveWord.isEmpty() ? readWordFromFile("wd.txt") : sensitiveWord);
        addStopWord(stopWord == null || stopWord.isEmpty() ? readWordFromFile("stopwd.txt") : stopWord);
    }


    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public double doFilter(final Map<String, Object> map) {
        List<String> keyWord = (List<String>) map.get(Filter.TITLE_KEYWORD);
        if (CollectionKit.isEmpty(keyWord)) {
            return 1.0;
        }
        String src = String.join(",", keyWord);

        int sensitiveTimes = 0;
//        int originalKeyWordLength = src.length();
        int originalKeyWordLength = keyWord.size();

        if (set != null && nodes != null) {
            char[] chs = src.toCharArray();
            int length = chs.length;
            int currc; // 当前检查的字符
            int cpcurrc; // 当前检查字符的备份
            int k;
            WordNode node;
            for (int i = 0; i < length; i++) {
                currc = charConvert(chs[i]);
                if (!set.contains(currc)) {
                    continue;
                }
                node = nodes.get(currc);// 日 2
                if (node == null)// 其实不会发生，习惯性写上了
                    continue;
                boolean couldMark = false;
                int markNum = -1;
                if (node.isLast()) {// 单字匹配（日）
                    couldMark = true;
                    markNum = 0;
                }
                // 继续匹配（日你/日你妹），以长的优先
                // 你-3 妹-4 夫-5
                k = i;
                cpcurrc = currc; // 当前字符的拷贝
                for (; ++k < length; ) {
                    int temp = charConvert(chs[k]);
                    if (temp == cpcurrc)
                        continue;
                    if (stopwdSet != null && stopwdSet.contains(temp))
                        continue;
                    node = node.querySub(temp);
                    if (node == null)// 没有了
                        break;
                    if (node.isLast()) {
                        couldMark = true;
                        markNum = k - i;// 3-2
                    }
                    cpcurrc = temp;
                }
                if (couldMark) {
                    //sensitiveTimes += (markNum + 1);
                    sensitiveTimes++;
                    /*for (k = 0; k <= markNum; k++) {
                        //chs[k + i] = SIGN;
                        sensitiveTimes++;
                    }*/
                    i = i + markNum;
                }
            }
            //System.out.println("敏感词长度：" + sensitiveTimes);
            //System.out.println("原始词长度：" + originalKeyWordLength);

            // 调整阀值 根据原始词长度调整百分比
            //threshold = threshold - ((10 - keyWord.size()) / keyWord.size());

            double score = 1.0 - (sensitiveTimes / (double) originalKeyWordLength);
            //System.out.println("敏感词可能性：" + sensitiveTimes);
            score = (int) (score * 1000000 + 0.5) / (double) 1000000;
            //return new String(chs);
            return score;
        }
        return 0.0;
    }


    /**
     * 增加词典
     *
     * @param path
     * @return
     */
    private static List<String> readWordFromFile(String path) {
        List<String> words;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(SensitiveKeyWordFilter.class.getClassLoader().getResourceAsStream(path)));
            words = new ArrayList<String>(1200);
            for (String buf = ""; (buf = br.readLine()) != null; ) {
                if (buf == null || buf.trim().equals(""))
                    continue;
                words.add(buf);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
            }
        }
        return words;
    }

    private static void addSensitiveWord(final List<String> words) {
        if (CollectionKit.notEmpty(words)) {
            char[] chs;
            int fchar;
            int lastIndex;
            WordNode fnode; // 首字母节点
            for (String curr : words) {
                chs = curr.toCharArray();
                fchar = charConvert(chs[0]);
                if (!set.contains(fchar)) {// 没有首字定义
                    set.add(fchar);// 首字标志位 可重复add,反正判断了，不重复了
                    fnode = new WordNode(fchar, chs.length == 1);
                    nodes.put(fchar, fnode);
                } else {
                    fnode = nodes.get(fchar);
                    if (!fnode.isLast() && chs.length == 1)
                        fnode.setLast(true);
                }
                lastIndex = chs.length - 1;
                for (int i = 1; i < chs.length; i++) {
                    fnode = fnode.addIfNoExist(charConvert(chs[i]), i == lastIndex);
                }
            }
        }
    }

    /**
     * 增加停顿词
     *
     * @param words
     */
    private static void addStopWord(final List<String> words) {
        if (CollectionKit.notEmpty(words)) {
            char[] chs;
            for (String curr : words) {
                chs = curr.toCharArray();
                for (char c : chs) {
                    stopwdSet.add(charConvert(c));
                }
            }
        }
    }

    /**
     * 大写转化为小写 全角转化为半角
     *
     * @param src
     * @return
     */
    private static int charConvert(char src) {
        int r = BCConvert.qj2bj(src);
        return (r >= 'A' && r <= 'Z') ? r + 32 : r;
    }
}