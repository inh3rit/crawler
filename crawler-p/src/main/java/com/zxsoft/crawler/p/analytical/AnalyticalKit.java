package com.zxsoft.crawler.p.analytical;

import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.p.analytical.focus.AnalyticalFocusWeiboTencent;
import com.zxsoft.crawler.p.analytical.inspect.AnalyticalNetworkInspectForeign;
import com.zxsoft.crawler.p.analytical.networksearch.AnalyticalNetworkSearchDefault;
import com.zxsoft.crawler.p.analytical.networksearch.AnalyticalNetworkSearchWeiboTencent;

import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫解析器接口调用
 */
public class AnalyticalKit {

    private static final AnalyticalApi API_DEFAULT = new AnalyticalDefault();
    private static final String DEFAULT = "default";
    private static final Map<String, AnalyticalApi> API_FOCUS;
    private static final Map<String, AnalyticalApi> API_NETWORK_SEARCH;
    private static final Map<String, AnalyticalApi> API_INSPECT;

    static {
        /*
         * 重点关注实现类
         */
        API_FOCUS = new HashMap<String, AnalyticalApi>();
        API_INSPECT = new HashMap<String, AnalyticalApi>();
        API_NETWORK_SEARCH = new HashMap<String, AnalyticalApi>();

        API_FOCUS.put("腾讯微博", new AnalyticalFocusWeiboTencent());
        API_FOCUS.put(DEFAULT, API_DEFAULT);

        /*
        全网搜索实现类
         */
        API_NETWORK_SEARCH.put("腾讯微博", new AnalyticalNetworkSearchWeiboTencent());
        API_NETWORK_SEARCH.put(DEFAULT, new AnalyticalNetworkSearchDefault());

        /*
        网络巡检实现类
         */
        API_INSPECT.put(DEFAULT, API_DEFAULT);
        API_INSPECT.put("FOREIGN", new AnalyticalNetworkInspectForeign());
    }

    /**
     * 选择终端关注解析器
     * @param je 任务
     * @return AnalyticalApi
     */
    private static AnalyticalApi chooseFocusAnalytical(JobEntity je) {
        AnalyticalApi api = API_FOCUS.get(je.getSource_name());
        return api == null ? API_FOCUS.get(DEFAULT) : api;
    }

    /**
     * 选择全网搜索解析器
     * @param je 任务
     * @return AnalyticalApi
     */
    private static AnalyticalApi chooseNetworkSearchAnalytical(JobEntity je) {
        AnalyticalApi api = API_NETWORK_SEARCH.get(je.getSource_name());
        return api == null ? API_NETWORK_SEARCH.get(DEFAULT) : api;
    }

    /**
     * 选择网络巡检解析其
     * @param je 任务
     * @return AnalyticalApi
     */
    private static AnalyticalApi chooseInspectAnalytical(JobEntity je) {
        // 当前仅有境外巡检
        return API_INSPECT.get("FOREIGN");
    }

    public static AnalyticalApi chooseAnalytical(JobEntity je) {
        switch (je.getJobType()) {
            case NETWORK_SEARCH:
                return AnalyticalKit.chooseNetworkSearchAnalytical(je);
            case NETWORK_FOCUS:
                return AnalyticalKit.chooseFocusAnalytical(je);
            case NETWORK_INSPECT:
                return AnalyticalKit.chooseInspectAnalytical(je);
            default:
                return new AnalyticalDefault();
        }
    }

}
