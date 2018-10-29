package com.zxsoft.crawler.w.validator.account;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/8/31.
 */
public class AccountValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateRequiredString("name", "msg_name_null", "请输入名称");
        super.validateRequiredString("passwd", "msg_passwd_null", "请输入密码");
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("msg_name_null"))) {
            controller.renderJson(Tool.pushResult(-1, "msg_name_null", controller.getAttrForStr("msg_name_null")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("msg_passwd_null"))) {
            controller.renderJson(Tool.pushResult(-1, "msg_passwd_null", controller.getAttrForStr("msg_passwd_null")));
            return;
        }
    }
}
