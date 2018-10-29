package com.zxsoft.crawler.w.validator.reptile;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/9/29.
 */
public class UpdateReptileValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateRequiredString("id", "null_id", "错误的区域爬虫信息");
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("null_id"))) {
            controller.renderJson(Tool.pushResult(-1, "null_id", controller.getAttrForStr("null_id")));
            return;
        }
    }
}
