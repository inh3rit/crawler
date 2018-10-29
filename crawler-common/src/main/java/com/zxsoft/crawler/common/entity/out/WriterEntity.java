package com.zxsoft.crawler.common.entity.out;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by cox on 2015/10/13.
 */
public class WriterEntity extends Record {

//    private Integer success;
//    private Integer fail;
//    private List<RecordInfoEntity> dataFail;

    public WriterEntity() {
        this(0, 0);
    }

    public WriterEntity(Integer success, Integer fail) {
        this.setSuccess(success);
        this.setFail(fail);
    }

    public String getMessage() {
        return super.getStr("msg");
    }

    public WriterEntity setMessage(String msg) {
        super.set("msg", msg);
        return this;
    }

    public String getUrl() {
        return super.getStr("url");
    }

    public WriterEntity setUrl(String url) {
        super.set("url", url);
        return this;
    }

    public Integer getSuccess() {
        return super.getInt("success");
    }

    public WriterEntity setSuccess(Integer success) {
        super.set("success", success);
        return this;
    }

    public Integer getFail() {
        return super.getInt("fail");
    }

    public WriterEntity setFail(Integer fail) {
        super.set("fail", fail);
        return this;
    }

    public List<RecordInfoEntity> getDataFail() {
        return super.get("data-fail");
    }

    public WriterEntity setDataFail(List<RecordInfoEntity> dataFail) {
        super.set("data-fail", dataFail);
        return this;
    }
}
