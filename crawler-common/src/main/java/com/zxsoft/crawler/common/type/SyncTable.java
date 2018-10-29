package com.zxsoft.crawler.common.type;

/**
 * 同步列表枚举类
 */
public enum SyncTable {

	/*
     * 以增加配置列表視圖同步, 不建議在單獨同步站點 板塊等表 便於分析數據 站點 表同步將會刪除, 不再受支持
	 */

    // 未知表
    /**
     * 未知表
     */
    UNKNOW("unknow"),
    // @Deprecated
    // WEBSITE("website"), // 站點
    // @Deprecated
    // SECTION("section"), // 板塊
    // @Deprecated
    // CONF_LIST("conf_list"), // 規則列表
    // @Deprecated
    // CONF_DETAIL("conf_detail"), // 規則詳情頁
    /**
     * 黑名單
     */
    BLACKLIST("blacklist"),

    /**
     * 白名单
     */
    BLANKLIST("blanklist"),

    /**
     * url规则
     */
    URL_RULE("url_rule"),

    /**
     * 時間規則
     */
    TIMEREG("timereg"),

    /**
     * 未定義來源
     */
    TID404("tid404"),

    /**
     * 區域配置
     */
    V_PROP_REPTILE("v_prop_reptile"),

    /**
     * 規則列表視圖
     */
    V_CONF_LIST("v_conf_list"),

    /**
     * 敏感词词库
     */
    SENSITIVE_KEYWORDS("sensitive_keywords"),

    /**
     * 內容黑名單
     */
    TEXTBLACKLIST("textblacklist"),;

    /**
     * 同步表表名
     */
    private final String name;

    /**
     * SyncTable构造方法
     *
     * @param name 同步表表名
     */
    SyncTable(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * 获取同步表信息，获取不到列表则返回 UNKNOW
     *
     * @param name 表名
     * @return 同步表信息或返回UNKNOW
     */
    public static SyncTable getSyncTable(String name) {
        for (SyncTable st : SyncTable.values()) {
            if (!name.trim().toUpperCase().equals(st.getName().toUpperCase()))
                continue;
            return st;
        }
        return UNKNOW;
    }
}
