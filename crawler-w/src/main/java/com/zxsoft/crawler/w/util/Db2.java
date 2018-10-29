package com.zxsoft.crawler.w.util;

import com.jfinal.plugin.activerecord.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by cox on 2015/8/17.
 */
public class Db2 {

    private static final Logger log = LoggerFactory.getLogger(Db2.class);

    public static int update(String sql) {
        return update(sql, new Object[0]);
    }

    public static int update(String sql, Object ... arg) {
        try {
            return Db.update(sql, arg);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    public static Boolean update(String tableName, Record record) {
        try {
            return Db.update(tableName, record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static Boolean update(String tableName, String primaryKey, Record record) {
        try {
            return Db.update(tableName, primaryKey, record);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static Boolean tx(IAtom i) {
        try {
            return Db.tx(i);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static List<Record> find(String sql) {
        return Db.find(sql, new Object[0]);
    }

    public static List<Record> find(String sql, Object ... paras) {
        try {
            return Db.find(sql, paras);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Record findFirst(String sql) {
        return findFirst(sql, new Object[0]);
    }

    public static Record findFirst(String sql, Object ... paras) {
        try {
            return Db.findFirst(sql, paras);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Page<Record> paginate(Integer pageNumber, Integer pageSize, String select, String sqlExceptSelect) {
        return paginate(pageNumber, pageSize, select, sqlExceptSelect, new Object[0]);
    }

    public static Page<Record> paginate(Integer pageNumber, Integer pageSize, String select, String sqlExceptSelect, Object ... args) {
        try {
            return Db.paginate(pageNumber, pageSize, select, sqlExceptSelect, args);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
