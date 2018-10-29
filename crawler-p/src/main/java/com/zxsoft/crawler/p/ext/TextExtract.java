package com.zxsoft.crawler.p.ext;

/**
 * @author Xin Chen
 * Created on 2009-11-11
 * Updated on 2010-08-09
 * Email:  xchen@ir.hit.edu.cn
 * Blog:   http://hi.baidu.com/爱心同盟_陈鑫
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * <p>
 * 在线性时间内抽取主题类（新闻、博客等）网页的正文。
 * 采用了<b>基于行块分布函数</b>的方法，为保持通用性没有针对特定网站编写规则。
 * </p>
 * @author  Chen Xin
 * @version 1.0, 2009-11-11
 */
public class TextExtract {

	private static List<String> lines;
	private final static int blocksWidth;
	private static int threshold;
	private static String html;
	public static String detialHtml;
	private static int start;
	private static int end;
	private static StringBuilder text;
	private static ArrayList<Integer> indexDistribution;

	static {
		lines = new ArrayList<String>();
		indexDistribution = new ArrayList<Integer>();
		text = new StringBuilder();
		blocksWidth = 3;
		/* 当待抽取的网页正文中遇到成块的新闻标题未剔除时，只要增大此阈值即可。 */
		/* 阈值增大，准确率提升，召回率下降；值变小，噪声会大，但可以保证抽到只有一句话的正文 */
		threshold = 80;
	}

	public static void setthreshold(int value) {
		threshold = value;
	}

	/**
	 * 抽取网页正文，不判断该网页是否是目录型。即已知传入的肯定是可以抽取正文的主题类网页。
	 *
	 * @param _html 网页HTML字符串
	 *
	 * @return 网页正文string
	 */
	public static String parse(String _html) {
		return parse(_html, false);
	}

	/**
	 * 判断传入HTML，若是主题类网页，则抽取正文；否则输出<b>"unkown"</b>。
	 *
	 * @param _html 网页HTML字符串
	 * @param _flag true进行主题类判断, 省略此参数则默认为false
	 *
	 * @return 网页正文string
	 */
	public static String parse(String _html, boolean _flag) {
		html = _html;
		detialHtml = _html;
		preProcess();
		// System.out.println(html);
		return getText();
	}

	private static void preProcess() {
		html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
		// remove html common
		html = html.replaceAll("(?is)<!--.*?-->", "");
		// remove javascript
		html = html.replaceAll("(?is)<script.*?>.*?</script>", "");
		// remove css
		html = html.replaceAll("(?is)<style.*?>.*?</style>", "");
		// remove special char
		html = html.replaceAll("&.{2,5};|&#.{2,5};", " ");
		html = html.replaceAll("(?is)<.*?>", "");
		// <!--[if !IE]>|xGv00|9900d21eb16fa4350a3001b3974a9415<![endif]-->
	}

	private static String getText() {
		List<String> textBlocks = new ArrayList<String>();
		lines = Arrays.asList(html.split("\n"));
		indexDistribution.clear();

		for (int i = 0; i < lines.size() - blocksWidth; i++) {
			int wordsNum = 0;
			for (int j = i; j < i + blocksWidth; j++) {
				lines.set(j, lines.get(j).replaceAll("\\s+", ""));
				wordsNum += lines.get(j).length();
			}
			indexDistribution.add(wordsNum);
			// System.out.println(wordsNum);
		}

		start = -1;
		end = -1;
		boolean boolstart = false, boolend = false;
		text.setLength(0);

		for (int i = 0; i < indexDistribution.size() - 3; i++) {
			if (indexDistribution.get(i) > threshold && !boolstart) {
				if (indexDistribution.get(i + 1).intValue() != 0 || indexDistribution.get(i + 2).intValue() != 0
						|| indexDistribution.get(i + 3).intValue() != 0) {
					boolstart = true;
					start = i;
					continue;
				}
			}
			if (boolstart) {
				if (indexDistribution.get(i).intValue() == 0 || indexDistribution.get(i + 1).intValue() == 0) {
					end = i;
					boolend = true;
				}
			}
			StringBuilder tmp = new StringBuilder();
			if (boolend) {
				for (int ii = start; ii <= end; ii++) {
					if (lines.get(ii).length() < 5)
						continue;
					tmp.append(lines.get(ii) + "\n");
				}
				String str = tmp.toString();
				if (str.contains("Copyright") || str.contains("版权所有"))
					continue;
				// text.append(str);
				boolstart = boolend = false;
				textBlocks.add(str);
			}
		}
		// return text.toString();
		return filterText(textBlocks);
	}

	/**
	 * 在根据文本密度获取的文字块中筛选出正文,
	 * 筛选方式:文字块中的信息在原html中是否包含p标签,
	 *   如果所有文字块都不含有,则不做处理,
	 *   否则:1.部分含有时,筛选出含有p标签的文字块;
	 *          2.全部含有时,取出所有p标签的内容代替文字块
	 * @param textBlocks: 文字块
	 * @return text: 正文
	 */
	public static String filterText(List<String> textBlocks) {
		boolean isContains = false;
		List<String> newTextBlocks = new ArrayList<String>();
		List<String> pTagTexts = new ArrayList<String>();
		Set<String> _pTagTexts = new HashSet<String>();

		Document doc = Jsoup.parse(detialHtml);
		Elements pTags = doc.getElementsByTag("p");
		// 过滤空字符的p元素
		for (Element e : pTags) {
			String pText = e.text().trim();
			pText = filterStr(pText);

			if ("".equals(pText))
				continue;
			pTagTexts.add(pText);
		}

		for (String textBlock : textBlocks) {
			for (String pText : pTagTexts) {
				if (filterStr(textBlock).contains(pText)) {
					_pTagTexts.add(pText);
					isContains = true;
				}
			}

			if (isContains)
				newTextBlocks.add(textBlock);
		}

		if (_pTagTexts.size() > 0) {
			List<String> pTextList = new ArrayList<String>();
			// 全含有的情况
			for (String text : _pTagTexts) {
				pTextList.add(text + "\n");
			}
			newTextBlocks = pTextList;
		} else {
			if (newTextBlocks.size() == 0) {
				// 全不含有的情况
				newTextBlocks = null;
			}
			// 部分含有的情况
		}

		StringBuilder text = new StringBuilder();
		for (String textBlock : newTextBlocks) {
			text.append(textBlock);
		}

		return text.toString();
	}

	// 过滤字段中不需要的字符
	private static String filterStr(String text) {
		text = text.replaceAll(" ", "");
		text = text.replaceAll("　", "");
		text = text.replaceAll("\\s", "");
		text = text.replaceAll("“", "");
		text = text.replaceAll("”", "");

		return text;
	}
}