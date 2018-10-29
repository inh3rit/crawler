package com.zxsoft.crawler.p.ctrl;

import org.inh3rit.httphelper.entity.ProxyEntity;
import org.junit.Test;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.p.kit.parse.ParseKit;

/**
 * Created by cox on 2016/2/17.
 */
public class NetworkFocusParseCtrlTest {

	@Test
	public void testParseFocus() throws Exception {
		String type = "weibo_tencent";
		switch (type) {
		case "weibo_tencent":
			this.testParseFocusWeiboTencent();
			break;
		default:
			break;
		}
	}

	public void testParseFocusWeiboTencent() throws Exception {

		String keywordEncoding = "UTF-8", url = "http://t.qq.com/xuezhe3777", listdom = "#talkList, #talkListTop",
				linedom = ".msgBox", urldom = "a.time", datedom = "a.time", synopsisdom = ".msgCnt", updatedom = "",
				authordom = "", replycntdom = ".replyBox .msgCnt", replyurl = ".mask", userName = "xuezhe3777",
				nickName = "学者";

		url = "http://t.qq.com/xuezhe3777";
		userName = "xuezhe3777";
		nickName = "学者";

		url = "http://t.qq.com/Conane";
		userName = "Conane";
		nickName = "犯罪心理学";

		ListRuleEntity lre = new ListRuleEntity();
		lre.setListdom(listdom).setLinedom(linedom).setUrldom(urldom).setDatedom(datedom).setSynopsisdom(synopsisdom)
				.setUpdatedom(updatedom).setAuthordom(authordom).set("replycntdom", replycntdom)
				.set("replyurl", replyurl);

		JobEntity je = new JobEntity();
		je.setPlatform(3).setType("腾讯微博").setSource_id(8).setWorkerId(0).setLocationCode(10000).setProvince_code(10000)
				.setCity_code(10000).setKeywordEncode(keywordEncoding).setJobId("0").setIdentify_md5("iaceob")
				.setCountry_code(1).setGoInto(false).setJobType(JobType.NETWORK_FOCUS).setUrl(url)
				.setSource_name("腾讯微博").setUsername(userName).setNickName(nickName);
		je.setListRule(lre);

		ParseKit parseKit = new NetworkFocusParseCtrl();
		String[] address = new String[] { "http://192.168.32.17:20000/sentiment/index" };
		ProxyEntity pe = new ProxyEntity("192.168.25.254", 28129);
		pe.setAccount("yproxyq");
		pe.setPassword("zproxyx0#");
		je.setCookie(
				"wb_regf=%3B0%3B%3B36.7.150.150%3B0; wbilang_10000=zh_CN; pgv_pvid=8153131880; pgv_info=ssid=s1898170483; ts_last=t.qq.com/Runsns; ts_refer=36.7.150.150/Info_Monitoring/YQ_Index.aspx; ts_uid=6562238689; mb_reg_from=8; ad_play_index=54; pt_clientip=fc7d0abf97d16398; pt_serverip=476e0abf0660b48a; ptisp=ctc; ptui_loginuin=3115565062; pt2gguin=o3115565062; uin=o3115565062; skey=@vt6x46m03; RK=hTvasQRj3I; luin=o3115565062; lskey=00010000364230d233dd3cc6d7aabd748549f424d6e00bd3f43b67b454a6a928d1155d8a3210371eaf0b3bff; p_uin=o3115565062; p_skey=iMDJ1xOayZ*Cmg0wwBCG4DaXyHKhFjPaHGu2gVlyfJM_; pt4_token=1xVB0yOt0LuNxu-Rcojdyk1Q3ZBZCtqwZpIbRhj1Eb4_; p_luin=o3115565062; p_lskey=0004000050b6b44044904c4c26209df717b17f4de727885b09aef48e75b2a4a04110af2816c272f3d7b8a101; ");
		// parseKit.parse(je, address, pe, new BlacklistEntity[] {}, null,
		// null);
	}
}