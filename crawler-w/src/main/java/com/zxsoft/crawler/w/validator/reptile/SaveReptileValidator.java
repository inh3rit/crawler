package com.zxsoft.crawler.w.validator.reptile;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import com.zxsoft.crawler.w.util.Tool;

/**
 * Created by cox on 2015/9/29.
 */
public class SaveReptileValidator extends Validator {
    @Override
    protected void validate(Controller controller) {
        super.validateRequiredString("name", "null_name", "请输入名称");
        super.validateRequiredString("redis", "null_redis", "请输入 redis 缓存服务器地址");
        super.validateInteger("type", 1, 3, "fail_type", "请选择正确的爬虫类型");
        super.validateRequiredString("alias", "null_alias", "请输入区域别名");
        Integer type = controller.getParaToInt("type");
        if (type!=2) {
            super.validateRequiredString("url", "null_url", "请输入正确的任务数据库 URL");
            super.validateRequiredString("usr", "null_usr", "请输入数据库账户");
        }
        // super.validateRequiredString("passwd", "null_passwd", "请输入数据库密码");
    }

    @Override
    protected void handleError(Controller controller) {
        if (StrKit.notBlank(controller.getAttrForStr("null_name"))) {
            controller.renderJson(Tool.pushResult(-1, "null_name", controller.getAttrForStr("null_name")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("null_redis"))) {
            controller.renderJson(Tool.pushResult(-1, "null_redis", controller.getAttrForStr("null_redis")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("fail_type"))) {
            controller.renderJson(Tool.pushResult(-1, "fail_type", controller.getAttrForStr("fail_type")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("null_alias"))) {
            controller.renderJson(Tool.pushResult(-1, "null_alias", controller.getAttrForStr("null_alias")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("null_url"))) {
            controller.renderJson(Tool.pushResult(-1, "null_url", controller.getAttrForStr("null_url")));
            return;
        }
        if (StrKit.notBlank(controller.getAttrForStr("null_usr"))) {
            controller.renderJson(Tool.pushResult(-1, "null_usr", controller.getAttrForStr("null_usr")));
            return;
        }
//        if (StrKit.notBlank(controller.getAttrForStr("null_passwd"))) {
//            controller.renderJson(Tool.pushResult(-1, "null_passwd", controller.getAttrForStr("null_passwd")));
//            return;
//        }
    }
}
