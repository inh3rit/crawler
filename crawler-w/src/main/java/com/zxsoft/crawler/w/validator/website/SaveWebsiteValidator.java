package com.zxsoft.crawler.w.validator.website;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/12/17.
 */
public class SaveWebsiteValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateRequiredString("tid", "tid_null", "请输入来源");
        super.validateRequiredString("site", "site_null", "请输入站点地址");
        super.validateRequiredString("comment", "comment_null", "请输入站点名称");
        super.validateInteger("type", JobType.NETWORK_SEARCH.getIndex(), JobType.NETWORK_FOCUS.getIndex(), "type_fail", "请选择类型");
        String status = controller.getPara("status");
        if (StrKit.isBlank(status)) {
            super.addError("status_null", "请选择站点状态");
            return;
        }
        if (!"OPEN".equals(status.toUpperCase()) && !"CLOSE".equals(status.toUpperCase())) {
            super.addError("status_fail", "错误的站点状态");
        }
    }

    @Override
    protected void handleError(Controller controller) {

        if (StrKit.notBlank(controller.getAttrForStr("tid_null"))) {
            controller.renderJson(Tool.pushResult(-1, "tid_null", controller.getAttrForStr("tid_null")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("site_null"))) {
            controller.renderJson(Tool.pushResult(-1, "site_null", controller.getAttrForStr("site_null")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("comment_null"))) {
            controller.renderJson(Tool.pushResult(-1, "comment_null", controller.getAttrForStr("comment_null")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("status_null"))) {
            controller.renderJson(Tool.pushResult(-1, "status_null", controller.getAttrForStr("status_null")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("status_fail"))) {
            controller.renderJson(Tool.pushResult(-1, "status_fail", controller.getAttrForStr("status_fail")));
        }
        if (StrKit.notBlank(controller.getAttrForStr("type_fail"))) {
            controller.renderJson(Tool.pushResult(-1, "type_fail", controller.getAttrForStr("type_fail")));
        }
    }
}
