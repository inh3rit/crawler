//package com.zxsoft.crawler.s.test;
//
//import com.zxsoft.crawler.plugin.parse.ext.DateExtractor2;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.junit.Test;
//
//import javax.print.Doc;
//
///**
// * Created by cox on 2015/9/17.
// */
//public class TestParseTime {
//
//
//    private long calTime(Document _d, long outTime) {
//
//        if (outTime <= 315504000000L)
//            outTime = 0L;
//
//        long _timeIn = 0;
//
//        DateExtractor2 dateExtractor2 = new DateExtractor2();
//        dateExtractor2.extract(_d);
//        _timeIn = dateExtractor2.getTimeInMs();
//
//        if (_timeIn == 0 )
//            return outTime;
//
//        return _timeIn;
//    }
//
//    @Test
//    public void tpt() {
//        String html = "<html><head><title>新华网安徽频道-新闻</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\" /><style type=\"text/css\"></style><link rel=\"stylesheet\" href=\"2003_sy.css\" type=\"text/css\"><link rel=\"stylesheet\" href=\"http://www.ah.xinhuanet.com/css/2003_sy.css\" type=\"text/css\"></head><body bgcolor=\"#000066\" text=\"#000000\" topmargin=\"2\"><table width=\"778\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#cccccc\" align=\"center\"><tr><td width=\"364\"><font face=\"Arial, Helvetica, sans-serif\"              color=#ffffff></font><table cellspacing=0 cellpadding=0 width=364                                 border=0><form name=\"login\" method=\"post\" action=\"http://mail.xinhuanet.com/NULL/NULL/NULL/NULL/NULL/SignIn.gen\"><tbody><tr><td height=\"10\"><font class=2003_sy_xw>新华网邮箱：</font><font color=\"#FF0000\" class=2003_sy_xw></font><font class=2003_sy_xw></font><font class=2003_sy_xw>用户名</font><input size=8 style=\"HEIGHT: 19px\" onMouseOver=this.focus()  onFocus=this.select()                                  name=LoginName2><font class=2003_sy_xw>密码</font><input onMouseOver=this.focus()  onFocus=this.select()                                  type=password size=8 style=\"HEIGHT: 19px\" name=passwd2><input type=submit   value=登录 name=Submit32><a                                   href=\"http://mail.xinhuanet.com/\"                                   target=_blank></a></td></tr></tbody></form></table><font face=\"Arial, Helvetica, sans-serif\"              color=#ffffff></font></td><td width=\"288\"><font face=\"Arial, Helvetica, sans-serif\"              color=#ffffff><script language=JavaScript              src=\"http://www.ah.xinhuanet.com/js/mulsearch.js\"></script></font><font face=\"Arial, Helvetica, sans-serif\"              color=#ffffff></font></td><td width=\"122\"><iframe border=0 name=bk marginwidth=0 framespacing=0              marginheight=0 src=\"http://www.xinhuanet.com/home_bk2.htm\" frameborder=0              noResize width=110 scrolling=no height=22 vspale=\"0\"></iframe></td></tr></table><table width=\"778\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"><tr><td height=\"330\" valign=\"top\"><table cellspacing=0 cellpadding=0 width=778 bgcolor=#ffffff border=0><tbody><tr><td align=middle width=128 height=77><img width=\"133\" height=\"76\" src=\"http://www.ah.xinhuanet.com/img/index/top.jpg\"></td><td align=middle valign=\"middle\"><script language=JavaScript src=\"http://www.ah.xinhuanet.com/js/link_2.js\"></script></td></tr></tbody></table><table width=\"778\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#FFFFFF\"><tr><td width=\"154\" height=\"42\"><img src=\"/img/index/top_1.jpg\" width=\"154\" height=\"66\"></td><td width=\"475\" height=\"42\"><div align=\"center\"><IFRAME border=0 name=bk marginWidth=0 frameSpacing=0              marginHeight=0 src=\"http://www.ah.xinhuanet.com/gg/dh_gg.htm\" frameBorder=0              noResize width=474 scrolling=no height=67 vspale=\"0\"></IFRAME></div></td><td width=\"149\" height=\"42\"><table width=\"106\" border=\"0\" cellspacing=\"0\" cellpadding=\"2\" class=\"2003_sy_l\" align=\"center\"><tr><td class=\"2004_pd_2\" align=\"right\" valign=\"top\"><div align=\"center\"><font color=\"#FFFFFF\" class=\"2004_pd_2\">::                      设为首页 ::</font></div></td></tr><tr><td class=\"2004_pd_2\" align=\"right\" valign=\"top\"><div align=\"center\"><font color=\"#FFFFFF\" class=\"2004_pd_2\">::                      加入收藏 ::</font></div></td></tr><tr><td class=\"2004_pd_2\" align=\"right\" valign=\"top\"><div align=\"center\"><font color=\"#FFFFFF\" class=\"2004_pd_2\">::                      广告业务 ::</font></div></td></tr></table></td></tr></table><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"73%\" height=\"352\" valign=\"top\" bgcolor=\"#FFFFFF\"><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"25%\" height=\"16\"><img src=\"http://www.ah.xinhuanet.com/img/xwzx/xwzx_t.jpg\" width=\"165\" height=\"51\"></td><td width=\"75%\" height=\"16\" valign=\"top\" bgcolor=\"#FFFFFF\"><table width=\"402\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td height=\"28\" bgcolor=\"C6C7C6\" class=\"2003_sy\" width=\"402\">                          　</td></tr></table></td></tr></table><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td valign=\"top\"><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\"><tr><td><div align=\"center\"><b>合肥一出租房内巡警便衣网住“皮条客”</b><br><font  class=\"2003_sy_xw\"></font></div></td></tr><tr><td></td></tr><tr><td class=\"2003_sy_xw\"><table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" height=\"13\" align=\"center\"><tr><td background=\"http://www.ah.xinhuanet.com/img/xwzx/xwzx_yy.jpg\" height=\"13\"><div align=\"center\"><div align=\"center\"><font color=\"#666666\" class=\"2003_xw_nr\"  background=\"http://www.ah.xinhuanet.com/img/xwzx/xwzx_yy.jpg\" height=\"14\">新华网安徽频道 2005-04-04 08:59</div></td></tr></table></td></tr><tr><td class=\"2003_sy_xw\" valign=\"top\"><br><table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" class=\"2003_sy_xw\"><tr><td><P>&nbsp;&nbsp;&nbsp;&nbsp;安徽市场报讯(杨锋)　昨日下午1时许，一名江苏来肥办事的男子报警，称在合肥市旅游汽车站附近逗留时，被一名女子骗到东五里井附近一出租房内，又被四名男子抢走现金和贵重物品。巡警们根据走访和江苏男子反映的情况，认定这伙人一定还会在附近出现，便便装在附近等候。没过多久，就发现一名和江苏男子描述的相貌特征极为相符的女子出现，并还有一个男子紧紧跟在女子的身后，二人进屋后就关了门。巡警迅速冲进房间将二人全部擒获。该名女子就是那名拉皮条的，同来男子是被骗来的外地人。随后，又有一名涉嫌抢劫的男子在附近被抓获。<P>&nbsp;&nbsp;&nbsp;&nbsp;目前，违法嫌疑人已被移交辖区警方进行深入调查。<P>&nbsp;&nbsp;&nbsp;&nbsp;</P><p align=\"right\">　<font color=\"#666666\">（责编：徐进）</font></p></td></tr></table></td></tr></table></td></tr></table><table width=\"100%\" border=\"0\" cellspacing=\"2\" cellpadding=\"2\"><tr><td width=\"552\"><table width=\"500\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\" align=\"center\" bgcolor=\"#737373\"><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\" bgcolor=\"#FFFFFF\"><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td bgcolor=\"#ECEBEB\"> 　　<font color=\"#9E005D\">相关新闻</font></td></tr><tr><td valign=\"top\" bgcolor=\"#737373\"><img src=\"http://www.ah.xinhuanet.com/img/nr_x_tcs.gif\" width=\"1\" height=\"1\"></td></tr><tr><td></td></tr></table></td></tr></table></td></tr></table></td></tr></table></td><td width=\"27%\" height=\"352\" valign=\"top\" bgcolor=\"ECEFFE\"><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" background=\"http://www.ah.xinhuanet.com/img/index/2_bj.jpg\"><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"7%\" bgcolor=\"5986BE\"><div align=\"center\"><img src=\"/img/index/zuanti3.jpg\" width=\"10\" height=\"9\"></div></td><td width=\"44%\" bgcolor=\"5986BE\" class=\"2004_sy_14s\">焦点网谈</td><td width=\"9%\"><img src=\"/img/index/top_jd.jpg\" width=\"23\" height=\"21\"></td><td width=\"40%\" bgcolor=\"CCC7DC\"><div align=\"right\" class=\"2003_sy\"><a href=\"../../../jiandu/jdwt.htm\" target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">[更多]</font></a></div></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" class=\"2003_sy\"><tr><td class=\"2003_sy\">·<a href=../../../jdyl/2005-03/10/content_3852508.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">产棉大县缘何出现4300万元\"白条\"</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../../jdyl/2005-03/05/content_3821469.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">女博士深陷高学历带来的尴尬</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../../jdyl/2005-02/26/content_3782129.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">合肥能否建成中国的\"科学城\"?</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../../jdyl/2005-01/21/content_3607860.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">博物馆缘何成为\"购物嘉年华\"？</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../../jdyl/2005-01/18/content_3583347.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">安徽民工:崇尚品牌　追求技能</font></a></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"7%\" bgcolor=\"5986BE\"><div align=\"center\"><img src=\"/img/index/zuanti3.jpg\" width=\"10\" height=\"9\"></div></td><td width=\"44%\" bgcolor=\"5986BE\" class=\"2004_sy_14s\">专题集纳</td><td width=\"9%\"><img src=\"/img/index/top_jd.jpg\" width=\"23\" height=\"21\"></td><td width=\"40%\" bgcolor=\"CCC7DC\"><div align=\"right\" class=\"2003_sy\"><a href=\"../../../ztzl/ztzl.htm\" target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">[更多]</font></a></div></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" class=\"2003_sy\"><tr><td class=\"2003_sy\">·<a href=../../../ztzl/2005-03/11/content_3859944.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\"><FONT COLOR=#FF0000 >\"两会关注\"安徽专题网站</FONT><img src=http://imgs.xinhuanet.com/icon/icon/new.gif border=0></font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../../ztzl/2005-01/31/content_3658898.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\"><FONT COLOR=#FF0000 >安徽流脑疫情跟踪报道</FONT></font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../../ztzl/2004-12/14/content_3387422.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\"><FONT COLOR=#FF0000 >新闻追踪:慧慧悲惨身世牵动市民心</FONT></font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../../ztzl/2004-11/13/content_3214545.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">“农村体育年”江淮行专题报道</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../../04dj/zbdjh.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">中国&#149;合肥高新技术项目-资本对接会</font></a></td></tr></table></td></tr></table><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"ECEFFE\"><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"37%\" bgcolor=\"#153975\" class=\"2004_sy_14\"><div align=\"center\">我看安徽</div></td><td width=\"5%\"><img src=\"/img/index/zuanti4.jpg\" width=\"22\" height=\"21\"></td><td colspan=\"2\" bgcolor=\"868EC7\" width=\"58%\"><div align=\"right\" class=\"2003_sy_lsl\"><a href=\"../../../jdyl/wkah.htm\" target=\"_blank\" class=\"2003_sy_lsl\"><font color=\"#000000\">[更多]</font></a></div></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\"><tr><td class=\"2003_sy\">·<a href=../01/content_3987348.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">震动安徽人心的十大网络传言</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../2005-03/31/content_3979601.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">振兴安徽经济之我见</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../2005-03/30/content_3972792.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">合肥崛起 潜龙在渊</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../2005-03/29/content_3964682.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">合肥房地产将走向何方？</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../../2005-03/28/content_3957165.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">苏丹红闹心 你还吃得放心么？</font></a></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"37%\" bgcolor=\"#153975\" class=\"2004_sy_14\"><div align=\"center\">文教</div></td><td width=\"5%\"><img src=\"/img/index/zuanti4.jpg\" width=\"22\" height=\"21\"></td><td colspan=\"2\" bgcolor=\"868EC7\" width=\"58%\"><div align=\"right\" class=\"2003_sy_lsl\"><a href=\"../../kjww.htm\" target=\"_blank\" class=\"2003_sy_lsl\"><font color=\"#000000\">[更多]</font></a></div></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\"><tr><td class=\"2003_sy\">·<a href=../03/content_3991543.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">六高中生自毁前程 高考前被民警从课堂带走</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../03/content_3991542.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">专家：孩子不安分 并非全是“多动症”</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../03/content_3991541.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">长期以单手输写手机短信 手部肌腱伤害严重</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../03/content_3991540.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">“春姑娘”芳踪４日履肥 比往年迟了近10天</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../03/content_3991539.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">合肥四月天象缤纷 流星雨唱罢半影月食登台</font></a></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"37%\" bgcolor=\"#153975\" class=\"2004_sy_14\"><div align=\"center\">财经</div></td><td width=\"5%\"><img src=\"/img/index/zuanti4.jpg\" width=\"22\" height=\"21\"></td><td colspan=\"2\" bgcolor=\"868EC7\" width=\"58%\"><div align=\"right\" class=\"2003_sy_lsl\"><a href=\"../../jjkt.htm\" target=\"_blank\" class=\"2003_sy_lsl\"><font color=\"#000000\">[更多]</font></a></div></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\"><tr><td class=\"2003_sy\">·<a href=http://news.xinhuanet.com/newscenter/2005-04/03/content_2779245.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">北京中行6亿房贷被骗 森豪公寓涉嫌假按揭</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=http://news.xinhuanet.com/fortune/2005-04/02/content_2775795.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">城市家庭汽车拥有率7% 网民购车愿望最强</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../03/content_3991550.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">鼓励青年读书创业　怀宁募集青年发展资金</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../03/content_3991535.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">免检申报正式启动 54类产品今起可申报免检</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../03/content_3991534.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">合肥市有望诞生全国首个炒货产品生产标准</font></a></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"37%\" bgcolor=\"#153975\" class=\"2004_sy_14\"><div align=\"center\">社会</div></td><td width=\"5%\"><img src=\"/img/index/zuanti4.jpg\" width=\"22\" height=\"21\"></td><td colspan=\"2\" bgcolor=\"868EC7\" width=\"58%\"><div align=\"right\" class=\"2003_sy_lsl\"><a href=\"../../shcz.htm\" target=\"_blank\" class=\"2003_sy_lsl\"><font color=\"#000000\">[更多]</font></a></div></td></tr></table></td></tr><tr><td><table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\"><tr><td class=\"2003_sy\">·<a href=content_3994374.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">合肥一出租房内巡警便衣网住“皮条客”</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=content_3994097.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">琐事争吵 肥东乡村午夜床上酿血案</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=content_3993965.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">巢湖打工小伙:神秘电话诈人钱财\"天衣无缝\"</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../03/content_3991566.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">赴港台旅客急剧增多 合肥边检推出便民举措</font></a></td></tr><tr><td class=\"2003_sy\">·<a href=../03/content_3991565.htm target=\"_blank\" class=\"2003_sy\"><font color=\"#000000\">边检提醒:赴台不办签注按规定将被阻止出境</font></a></td></tr></table></td></tr></table></td></tr></table></td></tr></table><table cellspacing=0 cellpadding=0 width=780 border=0 align=\"center\"><tbody><tr><td bgcolor=#ffffff height=5 width=\"780\"></td></tr><tr><td bgcolor=#000000 height=1 width=\"780\"></td></tr><tr><td class=b12 align=middle bgcolor=#ffffff height=111 width=\"780\"><div align=\"center\"><br><span class=\"2003_sy\">|<a class=b12        href=\"http://www.xinhuanet.com/xhsjj/pic1.htm\" target=_blank>新华社简介</a> |<a class=b12 href=\"http://www.xinhuanet.com/aboutus.htm\"        target=_blank>关于我们</a> |<a class=b12        href=\"http://www.xinhuanet.com/guide.htm\" target=_blank>网站导航</a> |<a        class=b12 href=\"http://news.xinhuanet.com/way.htm\" target=_blank>联系方式</a>          |<br>         Copyright &copy; 2000-2004 XINHUANET.com　All Rights Reserved. 制作单位：新华通讯社网络中心<br>         本网站所刊登的新华社及新华网各种新闻﹑信息和各种专题专栏资料，<br>         均为新华通讯社版权所有，未经协议授权，禁止下载使用。<br>         （浏览本网主页，建议将电脑显示屏的分辨率调为1024*768）</span></div></td></tr></tbody></table><div align=\"center\"><script>var tc_user=\"ahpd34\";var tc_class=\"14\";</script></div></body></html> ";
//        Document d = Jsoup.parse(html);
//        System.out.println(this.calTime(d, 315601005620L));
//    }
//
//}