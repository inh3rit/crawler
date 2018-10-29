package com.zxsoft.crawler.common.entity.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.type.JobType;


/**
 * Created by cox on 2015/9/6.
 */
public class JobEntity extends Record {

    private static final Logger log = LoggerFactory.getLogger(JobEntity.class);


    public JobEntity() {
        super();
    }

    /**
     * 任务状态，1表示执行，0表示暂停
     */
    public enum State {
        JOB_STOP(0), JOB_EXCUTING(1);
        private int index;
        State(int index) {
            this.index = index;
        }
        public int getIndex() {
            return this.index;
        }
    }

    public Long getCount() {
        return super.getLong("count");
    }

    public JobEntity setCount(Long count) {
        super.set("count", count);
        return this;
    }

    public JobType getJobType() {
        Object obj = super.get("jobType");
        if (obj instanceof JobType)
            return (JobType) obj;
        String type = obj.toString();
        JobType[] jts = JobType.values();
        for(JobType jt : jts)
            if (jt.toString().equals(type))
                return jt;
        return JobType.UNKNOWN;
    }

    public JobEntity setJobType(JobType jobType) {
        super.set("jobType", jobType);
        return this;
    }

    public String getUrl() {
        return super.getStr("url");
    }

    public JobEntity setUrl(String url) {
        super.set("url", url);
        return this;
    }

    public int getWorkerId() {
        return super.getInt("workerId");
    }

    public JobEntity setWorkerId(int workerId) {
        super.set("workerId", workerId);
        return this;
    }

    public String getKeyword() {
        return super.getStr("keyword");
    }

    public JobEntity setKeyword(String keyword) {
        super.set("keyword", keyword);
        return this;
    }

    public String getKeywordEncode() {
        return super.getStr("keywordEncode");
    }

    public JobEntity setKeywordEncode(String keywordEncode) {
        super.set("keywordEncode", keywordEncode);
        return this;
    }

    public boolean isAutoUrl() {
        return super.getBoolean("autoUrl");
    }

    public JobEntity setAutoUrl(boolean autoUrl) {
        super.set("autoUrl", autoUrl);
        return this;
    }

    public String getEncode() {
        return super.getStr("encode");
    }

    public JobEntity setEncode(String encode) {
        super.set("encode", encode);
        return this;
    }

    public String getSource_name() {
        return super.getStr("source_name");
    }

    public JobEntity setSource_name(String source_name) {
        super.set("source_name", source_name);
        return this;
    }

    public int getPlatform() {
        return super.getInt("platform");
    }

    public JobEntity setPlatform(int platform) {
        super.set("platform", platform);
        return this;
    }

    public int getSource_id() {
        return super.getInt("source_id");
    }

    public JobEntity setSource_id(int source_id) {
        super.set("source_id", source_id);
        return this;
    }

    public String getJobId() {
        Object obj = super.get("jobId");
        return obj.toString();
    }

    public JobEntity setJobId(String jobId) {
        super.set("jobId", jobId);
        return this;
    }

    public String getIp() {
        return super.getStr("ip");
    }

    public JobEntity setIp(String ip) {
        super.set("ip", ip);
        return this;
    }

    public int getCountry_code() {
        return super.getInt("country_code");
    }

    public JobEntity setCountry_code(int country_code) {
        super.set("country_code", country_code);
        return this;
    }

    public int getProvince_code() {
        return super.getInt("province_code");
    }

    public JobEntity setProvince_code(int province_code) {
        super.set("province_code", province_code);
        return this;
    }

    public int getCity_code() {
        return super.getInt("city_code");
    }

    public JobEntity setCity_code(int city_code) {
        super.set("city_code", city_code);
        return this;
    }

    public State getState() {
        return super.get("state");
    }

    public JobEntity setState(State state) {
        super.set("state", state);
        return this;
    }

    public boolean isRecurrence() {
        return super.getBoolean("recurrence");
    }

    public JobEntity setRecurrence(boolean recurrence) {
        super.set("recurrence", recurrence);
        return this;
    }

    public Long getPrevFetchTime() {
        return super.getLong("prevFetchTime");
    }

    public JobEntity setPrevFetchTime(Long prevFetchTime) {
        super.set("prevFetchTime", prevFetchTime);
        return this;
    }

    public int getSectionId() {
        return super.getInt("sectionId");
    }

    public JobEntity setSectionId(int sectionId) {
        super.set("sectionId", sectionId);
        return this;
    }

    public String getType() {
        return super.getStr("type");
    }

    public JobEntity setType(String type) {
        super.set("type", type);
        return this;
    }

    public int getFetchinterval() {
        return super.getInt("fetchinterval");
    }

    public JobEntity setFetchinterval(int fetchinterval) {
        super.set("fetchinterval", fetchinterval);
        return this;
    }

    public Long getStart() {
        return super.getLong("start");
    }

    public JobEntity setStart(Long start) {
        super.set("start", start);
        return this;
    }

    public int getRetry() {
        return super.getInt("retry");
    }

    public JobEntity setRetry(int retry) {
        super.set("retry", retry);
        return this;
    }

    public String getUsername() {
        return super.getStr("username");
    }

    public JobEntity setUsername(String username) {
        super.set("username", username);
        return this;
    }

    public String getPassword() {
        return super.getStr("password");
    }

    public JobEntity setPassword(String password) {
        super.set("password", password);
        return this;
    }

    public Boolean getAuth() {
        return super.getBoolean("auth");
    }

    public JobEntity setAuth(Boolean auth) {
        super.set("auth", auth);
        return this;
    }

    public ListRuleEntity getListRule() {
        if (super.get("listRule") instanceof ListRuleEntity) {
            return super.get("listRule");
        }
        ListRuleEntity lre = new ListRuleEntity();
        if (super.get("listRule") instanceof Record) {
            Record r = super.get("listRule");
            String[] sc = r.getColumnNames();
            for (Integer i=sc.length; i-->0;)
                lre.set(sc[i], r.get(sc[i]));
            r.clear();
            return lre;
        }
        if (super.get("listRule") instanceof Map) {
            Map lreMap = super.get("listRule");
            for (Object keys : lreMap.keySet())
                lre.set(keys.toString(), lreMap.get(keys));
            return lre;
        }
        return lre;
    }

    public JobEntity setListRule(ListRuleEntity listRule) {
        super.set("listRule", listRule);
        return this;
    }

    public DetailRuleEntity getDetailRules() {

        return super.get("detailRules");
    }

    public JobEntity setDetailRules(DetailRuleEntity detailRules) {
        super.set("detailRules", detailRules);
        return this;
    }

    public String getLocation() {
        return super.getStr("location");
    }

    public JobEntity setLocation(String location) {
        super.set("location", location);
        return this;
    }

    public int getLocationCode() {
        return super.getInt("locationCode");
    }

    public JobEntity setLocationCode(int locationCode) {
        super.set("locationCode", locationCode);
        return this;
    }

    public String getIdentify_md5() {
        return super.getStr("identify_md5");
    }

    public JobEntity setIdentify_md5(String identify_md5) {
        super.set("identify_md5", identify_md5);
        return this;
    }

    public Boolean getGoInto() {
        return super.getBoolean("goInto");
    }

    public JobEntity setGoInto(Boolean goInto) {
        super.set("goInto", goInto);
        return this;
    }

    public Integer getMachine() {
        return this.getInt("machine");
    }

    public JobEntity setMachine(Integer machine) {
        super.set("machine", machine);
        return this;
    }

    public String getNickName() {
        return super.getStr("nickname");
    }

    public JobEntity setNickName(String nickName) {
        super.set("nickname", nickName);
        return this;
    }

    public Long getTimestamp() {
        return super.getLong("timestamp");
    }

    public JobEntity setTimestamp(Long timestamp) {
        super.set("timestamp", timestamp);
        return this;
    }

    public String getCookie() {
        return super.getStr("cookie");
    }

    public JobEntity setCookie(String cookie) {
        super.set("cookie", cookie);
        return this;
    }

    public ClientInfoEntity getClient() {
        return super.get("client");
    }

    public JobEntity setClient(ClientInfoEntity client) {
        super.set("client", client);
        return this;
    }

    @Override
	public JobEntity set(String key, Object value) {
        super.set(key, value);
        return this;
    }

    @Override
	public <T> T get(String key) {
        return super.get(key);
    }

    public JobEntity merge(JobEntity je) {
        if (je == null) return this;
        String[] cns = je.getColumnNames();
        for(Integer i=cns.length; i-->0;)
            super.set(cns[i], je.get(cns[i]));
        return this;
    }

	@Override
	public String toString() {
		return "JobEntity [getCount()=" + getCount() + ", getJobType()=" + getJobType()
				+ ", getUrl()=" + getUrl() + ", getWorkerId()=" + getWorkerId() + ", getKeyword()=" + getKeyword()
				+ ", getKeywordEncode()=" + getKeywordEncode() + ", isAutoUrl()=" + isAutoUrl() + ", getEncode()="
				+ getEncode() + ", getSource_name()=" + getSource_name() + ", getPlatform()=" + getPlatform()
				+ ", getSource_id()=" + getSource_id() + ", getJobId()=" + getJobId() + ", getIp()=" + getIp()
				+ ", getCountry_code()=" + getCountry_code() + ", getProvince_code()=" + getProvince_code()
				+ ", getCity_code()=" + getCity_code() + ", getState()=" + getState() + ", isRecurrence()="
				+ isRecurrence() + ", getPrevFetchTime()=" + getPrevFetchTime() + ", getSectionId()=" + getSectionId()
				+ ", getType()=" + getType() + ", getFetchinterval()=" + getFetchinterval() + ", getStart()="
				+ getStart() + ", getRetry()=" + getRetry() + ", getUsername()=" + getUsername() + ", getPassword()="
				+ getPassword() + ", getAuth()=" + getAuth() + ", getListRule()=" + getListRule()
				+ ", getDetailRules()=" + getDetailRules() + ", getLocation()=" + getLocation() + ", getLocationCode()="
				+ getLocationCode() + ", getIdentify_md5()=" + getIdentify_md5() + ", getGoInto()=" + getGoInto()
				+ ", getMachine()=" + getMachine() + ", getNickName()=" + getNickName() + ", getTimestamp()="
				+ getTimestamp() + ", getCookie()=" + getCookie() + ", getClient()=" + getClient() + "]";
	}


}
