//package com.zxsoft.crawler.w.validator.section;
//
//import com.jfinal.core.Controller;
//import com.jfinal.kit.StrKit;
//import com.jfinal.validate.Validator;
//import com.zxsoft.crawler.w.util.Tool;
//
///**
// * Created by cox on 2015/12/4.
// */
//public class GetSectionByWebsiteValidator extends Validator {
//    @Override
//    protected void validate(Controller controller) {
//        super.validateInteger("website", "fail_website", "不存在当前站点");
//    }
//
//    @Override
//    protected void handleError(Controller controller) {
//        if (StrKit.notBlank(controller.getAttrForStr("fail_website"))) {
//            controller.renderJson(Tool.pushResult(-1, "fail_website", controller.getAttrForStr("fail_website")));
//            return;
//        }
//    }
//}
