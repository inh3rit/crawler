package com.zxsoft.crawler.common.entity.out;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by cox on 2015/10/8.
 */
public class RecordInfoEntity extends Record {


//    private String id = "";
//    private int platform = 0;
//    private String mid = "";
//    private String username = "";
//    private String nickname = "";
//    private String original_id = "";
//    private String original_uid = "";
//    private String original_name = "";
//    private String original_title = "";
//    private String original_url = "";
//    private String url = "";
//    private String home_url = "";
//    private String title = "";
//    private String type = "";
//    private boolean isharmful;
//    private String content = "";
//    private int comment_count = 0;
//    private int read_count = 0;
//    private int favorite_count;
//    private int attitude_count = 0;
//    private int repost_count = 0;
//    private String video_url = "";
//    private String pic_url = "";
//    private String voice_url = "";
//    private long timestamp = 0;
//    private int source_id = 0;
//    private long lasttime = 0;
//    private int server_id = 0;
//    private long identify_id = 0;
//    private String identify_md5 = "xiayun";
//    private String keyword = "";
//    private long first_time = 0;
//    private long update_time = 0;
//    private String ip = "";
//    private String location = "";
//    private String geo = "";
//    private String receive_addr = "";
//    private String append_addr = "";
//    private String send_addr = "";
//    private String source_name = "";
//    private int source_type = 0;
//    private int country_code = 0;
//    private int location_code = 0;
//    private int province_code = 0;
//    private int city_code = 0;

    public RecordInfoEntity() {
        super();
    }

    public RecordInfoEntity(Integer sourceId, String type, Integer serverId,
                      String identifyMd5, String keyword, String ip,
                      String location, String sourceName, Integer sourceType,
                      Integer countryCode, Integer locationCode, Integer provinceCode,
                      Integer cityCode) {
        this.setSourceId(sourceId).setType(type).setServerId(serverId)
                .setIdentifyMd5(identifyMd5).setIp(ip)
                .setLocation(location).setSourceName(sourceName).setSourceType(sourceType)
                .setCountryCode(countryCode).setLocationCode(locationCode)
                .setProvinceCode(provinceCode).setCityCode(cityCode);
        if (StrKit.notBlank(keyword)) this.setKeyword(keyword);
        this.setTimestamp(System.currentTimeMillis()).setLasttime(this.getTimestamp())
                .setFirstTime(this.getLasttime());
    }

    public String getId() {
        return super.getStr("id");
    }

    public RecordInfoEntity setId(String id) {
        super.set("id", id);
        return this;
    }

    public Integer getPlatform() {
        return super.getInt("platform");
    }

    public RecordInfoEntity setPlatform(Integer platform) {
        super.set("platform", platform);
        return this;
    }

    public String getMid() {
        return super.getStr("mid");
    }

    public RecordInfoEntity setMid(String mid) {
        super.set("mid", mid);
        return this;
    }

    public String getUsername() {
        return super.getStr("username");
    }

    public RecordInfoEntity setUsername(String username) {
        super.set("username", username);
        return this;
    }

    public String getNickname() {
        return super.getStr("nickname");
    }

    public RecordInfoEntity setNickname(String nickname) {
        super.set("nickname", nickname);
        return this;
    }

    public String getOriginalId() {
        return super.getStr("original_id");
    }

    public RecordInfoEntity setOriginalId(String originalId) {
        super.set("original_id", originalId);
        return this;
    }

    public String getOriginalUid() {
        return super.getStr("original_uid");
    }

    public RecordInfoEntity setOriginalUid(String originalUid) {
        super.set("original_uid", originalUid);
        return this;
    }

    public String getOriginalName() {
        return super.getStr("original_name");
    }

    public RecordInfoEntity setOriginalName(String originalName) {
        super.set("original_name", originalName);
        return this;
    }

    public String getOriginalTitle() {
        return super.getStr("original_title");
    }

    public RecordInfoEntity setOriginalTitle(String originalTitle) {
        super.set("original_title", originalTitle);
        return this;
    }

    public String getOriginalUrl() {
        return super.getStr("original_url");
    }

    public RecordInfoEntity setOriginalUrl(String originalUrl) {
        super.set("original_url", originalUrl);
        return this;
    }

    public String getUrl() {
        return super.getStr("url");
    }

    public RecordInfoEntity setUrl(String url) {
        super.set("url", url);
        return this;
    }

    public String getHomeUrl() {
        return super.getStr("home_url");
    }

    public RecordInfoEntity setHomeUrl(String homeUrl) {
        super.set("home_url", homeUrl);
        return this;
    }

    public String getTitle() {
        return super.getStr("title");
    }

    public RecordInfoEntity setTitle(String title) {
        super.set("title", title);
        return this;
    }

    public String getType() {
        return super.getStr("type");
    }

    public RecordInfoEntity setType(String type) {
        super.set("type", type);
        return this;
    }

    public Boolean isIsharmful() {
        return super.getBoolean("isharmful");
    }

    public RecordInfoEntity setIsharmful(Boolean isharmful) {
        super.set("isharmful", isharmful);
        return this;
    }

    public String getContent() {
        return super.getStr("content");
    }

    public RecordInfoEntity setContent(String content) {
        super.set("content", content);
        return this;
    }

    public Integer getCommentCount() {
        return super.getInt("commentCount");
    }

    public RecordInfoEntity setCommentCount(Integer commentCount) {
        super.set("comment_count", commentCount);
        return this;
    }

    public Integer getReadCount() {
        return super.getInt("read_count");
    }

    public RecordInfoEntity setReadCount(Integer readCount) {
        super.set("read_count", readCount);
        return this;
    }

    public Integer getFavoriteCount() {
        return super.getInt("favorite_count");
    }

    public RecordInfoEntity setFavoriteCount(Integer favoriteCount) {
        super.set("favorite_count", favoriteCount);
        return this;
    }

    public Integer getAttitudeCount() {
        return super.getInt("attitude_count");
    }

    public RecordInfoEntity setAttitudeCount(Integer attitudeCount) {
        super.set("attitude_count", attitudeCount);
        return this;
    }

    public Integer getRepostCount() {
        return super.getInt("repost_count");
    }

    public RecordInfoEntity setRepostCount(Integer repostCount) {
        super.set("repost_count", repostCount);
        return this;
    }

    public String getVideoUrl() {
        return super.getStr("video_url");
    }

    public RecordInfoEntity setVideoUrl(String videoUrl) {
        super.set("video_url", videoUrl);
        return this;
    }

    public String getPicUrl() {
        return super.getStr("pic_url");
    }

    public RecordInfoEntity setPicUrl(String picUrl) {
        super.set("pic_url", picUrl);
        return this;
    }

    public String getVoiceUrl() {
        return super.getStr("voice_url");
    }

    public RecordInfoEntity setVoiceUrl(String voiceUrl) {
        super.set("voice_url", voiceUrl);
        return this;
    }

    public Long getTimestamp() {
        return super.getLong("timestamp");
    }

    public RecordInfoEntity setTimestamp(Long timestamp) {
        super.set("timestamp", timestamp);
        return this;
    }

    public Integer getSourceId() {
        return super.getInt("source_id");
    }

    public RecordInfoEntity setSourceId(Integer sourceId) {
        super.set("source_id", sourceId);
        return this;
    }

    public Long getLasttime() {
        return super.getLong("lasttime");
    }

    public RecordInfoEntity setLasttime(Long lasttime) {
        super.set("lasttime", lasttime);
        return this;
    }

    public Integer getServerId() {
        return super.getInt("server_id");
    }

    public RecordInfoEntity setServerId(Integer serverId) {
        super.set("server_id", serverId);
        return this;
    }

    public Long getIdentifyId() {
        return super.getLong("identify_id");
    }

    public RecordInfoEntity setIdentifyId(Long identifyId) {
        super.set("identify_id", identifyId);
        return this;
    }

    public String getIdentifyMd5() {
        return super.getStr("identify_md5");
    }

    public RecordInfoEntity setIdentifyMd5(String identifyMd5) {
        super.set("identify_md5", identifyMd5);
        return this;
    }

    public String getKeyword() {
        return super.getStr("keyword");
    }

    public RecordInfoEntity setKeyword(String keyword) {
        super.set("keyword", keyword);
        return this;
    }

    public Long getFirstTime() {
        return super.getLong("first_time");
    }

    public RecordInfoEntity setFirstTime(Long firstTime) {
        super.set("first_time", firstTime);
        return this;
    }

    public Long getUpdateTime() {
        return super.getLong("update_time");
    }

    public RecordInfoEntity setUpdateTime(Long updateTime) {
        super.set("update_time", updateTime);
        return this;
    }

    public String getIp() {
        return super.getStr("ip");
    }

    public RecordInfoEntity setIp(String ip) {
        super.set("ip", ip);
        return this;
    }

    public String getLocation() {
        return super.getStr("location");
    }

    public RecordInfoEntity setLocation(String location) {
        super.set("location", location);
        return this;
    }

    public String getGeo() {
        return super.getStr("geo");
    }

    public RecordInfoEntity setGeo(String geo) {
        super.set("geo", geo);
        return this;
    }

    public String getReceiveAddr() {
        return super.getStr("receive_addr");
    }

    public RecordInfoEntity setReceiveAddr(String receiveAddr) {
        super.set("receive_addr", receiveAddr);
        return this;
    }

    public String getAppendAddr() {
        return super.getStr("append_addr");
    }

    public RecordInfoEntity setAppendAddr(String appendAddr) {
        super.set("append_addr", appendAddr);
        return this;
    }

    public String getSendAddr() {
        return super.getStr("send_addr");
    }

    public RecordInfoEntity setSendAddr(String sendAddr) {
        super.set("send_addr", sendAddr);
        return this;
    }

    public String getSourceName() {
        return super.getStr("source_name");
    }

    public RecordInfoEntity setSourceName(String sourceName) {
        super.set("source_name", sourceName);
        return this;
    }

    public Integer getSourceType() {
        return super.getInt("source_type");
    }

    public RecordInfoEntity setSourceType(Integer sourceType) {
        super.set("source_type", sourceType);
        return this;
    }

    public Integer getCountryCode() {
        return super.getInt("country_code");
    }

    public RecordInfoEntity setCountryCode(Integer countryCode) {
        super.set("country_code", countryCode);
        return this;
    }

    public Integer getLocationCode() {
        return super.getInt("location_code");
    }

    public RecordInfoEntity setLocationCode(Integer locationCode) {
        super.set("location_code", locationCode);
        return this;
    }

    public Integer getProvinceCode() {
        return super.getInt("province_code");
    }

    public RecordInfoEntity setProvinceCode(Integer provinceCode) {
        super.set("province_code", provinceCode);
        return this;
    }

    public Integer getCityCode() {
        return super.getInt("city_code");
    }

    public RecordInfoEntity setCityCode(Integer cityCode) {
        super.set("city_code", cityCode);
        return this;
    }

    public RecordInfoEntity merge(RecordInfoEntity rie) {
        if (rie==null) return this;
        String[] cols = rie.getColumnNames();
        for (String key : cols) {
            if (rie.get(key)==null) continue;
            this.set(key, rie.get(key));
        }
        return this;
    }

    public RecordInfoEntity clone() {
        RecordInfoEntity rie = new RecordInfoEntity();
        String[] keys = this.getColumnNames();
        for (String key : keys)
            rie.set(key, this.get(key));
        return rie;
    }

    public String toJson() {
        return JsonKit.toJson(this);
    }

}
