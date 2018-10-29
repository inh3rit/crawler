package com.zxsoft.crawler.w.validator.timereg;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/11/20.
 */
public class SaveTimeRegexValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateRequiredString("sample", "null_sample", "请输入样本");
        super.validateRequiredString("regex", "null_regex", "请输入规则");
        super.validateRequiredString("key", "null_key", "请输入正则内容标记");
        String regex = controller.getAttrForStr("regex");
        String mark = controller.getAttrForStr("key");
        if (StrKit.isBlank(regex)||StrKit.isBlank(mark)) return;
        if (regex.contains(mark)) return;
        super.addError("fail_regex_key", "正则中不存在取内容标记");
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("null_sample"))) {
            controller.renderJson(Tool.pushResult(-1, "null_sample", controller.getAttrForStr("null_sample")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("null_regex"))) {
            controller.renderJson(Tool.pushResult(-1, "null_regex", controller.getAttrForStr("null_regex")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("null_key"))) {
            controller.renderJson(Tool.pushResult(-1, "null_key", controller.getAttrForStr("null_key")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("fail_regex_key"))) {
            controller.renderJson(Tool.pushResult(-1, "fail_regex_key", controller.getAttrForStr("fail_regex_key")));
            return;
        }
    }
}
