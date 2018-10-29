package com.zxsoft.crawler.w.validator.timereg;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/11/20.
 */
public class UpdateTimeRegexValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateInteger("id", "fail_id", "错误的规则");
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("fail_id"))) {
            controller.renderJson(Tool.pushResult(-1, "fail_id", controller.getAttrForStr("fail_id")));
            return;
        }
    }
}
