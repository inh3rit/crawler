package com.zxsoft.crawler.exception;

public class CrawlerException extends Exception {

    private static final long serialVersionUID = 5591402881944908914L;

    public enum ErrorCode {
        UNKNOW(0),
        SUCCESS(10000),
        CONF_ERROR(20000),
        CONF_ERROR_BLACKLIST(20001),
        CONF_ERROR_BLANKLIST(20002),
        CONF_ERROR_LIST_PAGE(20003),
        NETWORK_ERROR(30000),
        SYSTEM_ERROR(40000),
        SYSTEM_ERROR_SLAVE_BUSY(40001),
        OUTPUT_DATA_ERROR(50000),
        VALIDATOR_ERROR(60000),
        VALIDATOR_ERROR_FOCUS(60001),
        DATA_CLEANING(70000);
        public final int code;

        private ErrorCode(int c) {
            code = c;
        }

        public Integer getCode() {
            return this.code;
        }
    }

    ErrorCode code = ErrorCode.UNKNOW;

    public CrawlerException(ErrorCode code, String msg) {
        super(msg);
        this.code = code;
    }

    public CrawlerException(ErrorCode code, String msg, Throwable th) {
        super(msg, th);
        this.code = code;
    }

    public CrawlerException(ErrorCode code, Throwable th) {
        super(th);
        this.code = code;
    }

    public ErrorCode code() {
        return this.code;
    }

}
