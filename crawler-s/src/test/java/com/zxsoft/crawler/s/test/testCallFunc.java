//package com.zxsoft.crawler.s.test;
//
//import com.alibaba.druid.filter.stat.StatFilter;
//import com.alibaba.druid.wall.WallFilter;
//import com.jfinal.kit.JsonKit;
//import com.jfinal.kit.Prop;
//import com.jfinal.kit.PropKit;
//import com.jfinal.plugin.activerecord.*;
//import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
//import com.jfinal.plugin.druid.DruidPlugin;
//import com.zxsoft.crawler.common.c.Const;
//import com.zxsoft.crawler.common.type.SourceType;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.CallableStatement;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
///**
// * Created by cox on 2015/9/28.
// */
//public class testCallFunc {
//    private static final Logger log = LoggerFactory.getLogger(testCallFunc.class);
//
//    public testCallFunc() {
//        init();
//    }
//
//    private Prop conf = PropKit.use(Const.PROCFGFILE);
//
//
//    private Boolean init(){
//        try {
//            /*
//             * 启动 mysql 数据源
//             */
//            DruidPlugin dpm = new DruidPlugin(
//                    this.conf.get("db.mysql.url"),
//                    this.conf.get("db.mysql.username"),
//                    this.conf.get("db.mysql.passwd")
//            );
//            dpm.addFilter(new StatFilter());
//            WallFilter wm = new WallFilter();
//            wm.setDbType("mysql");
//            dpm.addFilter(wm);
//
//            ActiveRecordPlugin arpm = new ActiveRecordPlugin(SourceType.MYSQL.getName(), dpm);
//            arpm.setDialect(new MysqlDialect());
//            arpm.setShowSql(false);
//
//            if (!dpm.start()||!arpm.start()) {
//                log.error("连接数据库 Mysql: " + this.conf.get("db.mysql.url") + " 失败, 请在检查配置后重试.");
//                return false;
//            }
//            log.info("连接数据库 Mysql: " + this.conf.get("db.mysql.url") + " 成功.");
//            return true;
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return false;
//        }
//    }
//
////    @Test
////    public void ttd1() {
////        // List<Record> list = Db.use(SourceType.MYSQL.toString()).find("call usp_demo(1)");
////        // log.debug(JsonKit.toJson(list));
////
////        List<Record> obj = (List<Record>) Db.execute(new MyCallBack(6, DbKit.getConfig()));
////        log.debug(JsonKit.toJson(obj));
////
////    }
//
//
//
////    public class MyCallBack implements ICallback {
////        public int tid;
////        public Config cfg;
////        public ResultSet rs = null;
////        MyCallBack(int tid,Config cfg){
////            this.tid=tid;
////            this.cfg=cfg;
////        }
////        @Override
////        public Object call(Connection conn) throws SQLException {
////            CallableStatement proc = null;
////            proc = conn.prepareCall("{ call usp_demo(?) }");
////            // proc.setInt(1, 1);
////            proc.registerOutParameter(1, OracleTypes.CURSOR);
////            proc.execute();
////            log.debug(proc.getObject(1).toString());
////            ResultSet s =  (ResultSet) proc.getObject(1);
////            List<Record> list = (List<Record>) RecordBuilder.build(cfg, s);
////            return list;
////        }
////    }
//}
