package com.zxsoft.crawler.w.validator.blacklist;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/10/28.
 */
public class SaveBlacklistValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateRequiredString("name", "null_name", "請輸入黑名單規則名");
        super.validateRequiredString("regex", "null_regex", "請輸入黑名單規則");
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("null_name"))) {
            controller.renderJson(Tool.pushResult(-1, "null_name", controller.getAttrForStr("null_name")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("null_regex"))) {
            controller.renderJson(Tool.pushResult(-1, "null_regex", controller.getAttrForStr("null_regex")));
            return;
        }
    }
}
