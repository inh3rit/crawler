package com.zxsoft.crawler.w.validator.prop;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/12/19.
 */
public class SaveReptilePropValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateInteger("reptile", "reptile_null", "未发现当前区域");
        String[] props = controller.getParaValues("id");
        if (props==null || props.length==0) {
            super.addError("prop_null", "请选择配置");
            return;
        }
        for (String prop : props) {
            if (Tool.isNumber(prop)) continue;
            super.addError("prop_fail", "未发现当前配置: " + prop);
            return;
        }
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("reptile_null"))) {
            controller.renderJson(Tool.pushResult(-1, "reptile_null", controller.getAttrForStr("reptile_null")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("prop_null"))) {
            controller.renderJson(Tool.pushResult(-1, "prop_null", controller.getAttrForStr("prop_null")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("prop_fail"))) {
            controller.renderJson(Tool.pushResult(-1, "prop_fail", controller.getAttrForStr("prop_fail")));
            return;
        }
    }
}
