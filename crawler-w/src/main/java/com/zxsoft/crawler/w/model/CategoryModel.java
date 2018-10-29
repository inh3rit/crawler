package com.zxsoft.crawler.w.model;

import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by cox on 2015/8/18.
 */
public class CategoryModel extends Model<CategoryModel> {

    public static final CategoryModel dao = new CategoryModel();

    public List<Record> getCategoryList() {
        String sql = "select id, comment from category";
        return Db2.find(sql);
    }

}
