package com.zxsoft.crawler.w.validator.patrol;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/12/3.
 */
public class PatrolSaveJobValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateInteger("reptile", "msg_reptile", "未发现当前区域");
        String[] ss = controller.getParaValues("section");
        if (ss == null || ss.length == 0) return;
        for (String s : ss) {
            if (Tool.isNumber(s)) continue;
            super.addError("msg_error_site", "未发现当前板块");
            return;
        }
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("msg_reptile"))) {
            controller.renderJson(Tool.pushResult(-1, "msg_reptile", controller.getAttrForStr("msg_reptile")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("msg_error_site"))) {
            controller.renderJson(Tool.pushResult(-1, "msg_error_site", controller.getAttrForStr("msg_error_site")));
            return;
        }
    }
}
