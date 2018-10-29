package com.zxsoft.crawler.w.validator.website;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/12/15.
 */
public class UpdateWebsiteValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateRequiredString("id", "id_null", "未找到当前站点");
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("id_null"))) {
            controller.renderJson(Tool.pushResult(-1, "id_null", controller.getAttrForStr("id_null")));
            return;
        }
    }
}
