package com.zxsoft.crawler.w.validator.blacklist;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/10/28.
 */
public class UpdateBlacklistValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateInteger("id", "id_fail", "未找到當前規則");
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("id_fail"))) {
            controller.renderJson(Tool.pushResult(-1, "id_fail", controller.getAttrForStr("id_fail")));
            return;
        }
    }
}
