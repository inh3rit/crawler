package com.zxsoft.crawler.w.validator.prop;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.w.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/12/18.
 */
public class SavePropValidator extends Validator {

    private static final Logger log = LoggerFactory.getLogger(SavePropValidator.class);


    private Boolean checkVal(String val, PropKey key) {
        if (StrKit.isBlank(val)) return false;
        Boolean res = true;
        switch (key.getType()) {
            case NUMBER:
                res = com.zxsoft.crawler.common.kit.Tool.isNumber(val);
                break;
            case DOUBLE:
                res = com.zxsoft.crawler.common.kit.Tool.isDouble(val);
                break;
            case BOOLEAN:
                res = com.zxsoft.crawler.common.kit.Tool.isBoolean(val);
                break;
            case JSON:
                try {
                    JSON.parse(val);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    res = false;
                }
                break;
            case IPV4:
                if (!Tool.isNumber(val.replaceAll("\\.", ""))) {
                    res = false;
                    break;
                }
                String[] ipv4s = val.split("\\.");
                if (ipv4s.length!=4) {
                    res = false;
                    break;
                }
                for (String ipv4 : ipv4s) {
                    if (ipv4.length()<3) continue;
                    Integer ipn = Integer.valueOf(ipv4);
                    if (ipn<255 && ipn > 0) continue;
                    res = false;
                    break;
                }
                break;
            case URL:
                res = val.toLowerCase().matches("(http|https)://.*?");
                break;
            case JOB_TYPE:
                if (!Tool.isNumber(val)) {
                    res = false;
                    break;
                }
                if (JobType.getIndex(Integer.valueOf(val)) == JobType.UNKNOWN) {
                    res = false;
                }
                break;
            case SYNC_TABLE:
                if (SyncTable.getSyncTable(val) == SyncTable.UNKNOW)
                    res = false;
                break;
            case TEXT:
                break;
            default:
                break;
        }
        return res;
    }

    @Override
    protected void validate(Controller controller) {
        super.validateRequiredString("key", "null_key", "请选择配置键");
        super.validateRequiredString("val", "null_val", "请输入配置值");
        String key = controller.getPara("key");
        String val = controller.getPara("val");
        if (StrKit.isBlank(key) || StrKit.isBlank(val)) return;
        PropKey ck = PropKey.getConfKey(key);
        if (!this.checkVal(val, ck)) {
            super.addError("fail_val", "配置值格式错误, 允许输入 " + ck.getType().getRemark());
        }
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("null_key"))) {
            controller.renderJson(Tool.pushResult(-1, "null_key", controller.getAttrForStr("null_key")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("null_val"))) {
            controller.renderJson(Tool.pushResult(-1, "null_val", controller.getAttrForStr("null_val")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("fail_val"))) {
            controller.renderJson(Tool.pushResult(-1, "fail_val", controller.getAttrForStr("fail_val")));
            return;
        }
    }
}
