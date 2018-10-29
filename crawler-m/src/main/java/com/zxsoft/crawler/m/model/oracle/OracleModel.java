package com.zxsoft.crawler.m.model.oracle;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.entity.redis.ClientInfoEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.type.SourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Oracle 数据库相关操作
 * 全网搜索任务来源
 *
 * 修正:新版oa任务存放在mysql数据库,类名暂时不做修改
 */
public class OracleModel {

    private static final Logger log = LoggerFactory.getLogger(OracleModel.class);
    private static final DbPro db = Db.use(SourceType.MYSQL_BUSI.toString());
    public static final OracleModel dao = new OracleModel();

    private JobEntity oracleRecordToJobEntity(Record r) {
        JobEntity je = new JobEntity();
        je.setJobId(r.getStr("subtaskid")).setKeyword(r.getStr("keywords"))
                .setSectionId(r.getInt("tid")).setSource_id(r.getInt("sourceid"))
                .setPlatform(r.getInt("platform")).setSource_name(r.getStr("name"));
        return je;
    }

    /**
     * 獲取任務列表
     *
     * @return List<JobEntity>
     */
    public List<JobEntity> queryTaskQueue(Integer pageSize, Integer location) {
        String sql1 = "select sfq.subtaskid, sfq.keywords, sfq.siteid, sw.sourceid, sw.tid, sw.sitetype as platform, sw.name from spd_full_queues sfq " +
                "inner join site_websites sw on sfq.siteid = sw.uid ";

        // 注释location功能,不区分地区,统合使用所有机器
//        if (location == null) {
//            sql1 += "where sfq.selfspd=0";
//        } else { // 拥有自己爬虫的地区只执行本地区的任务
//            sql1 += "where sfq.syscode=" + location;
//        }
        sql1 += " and sfq.siteId!='0f88ac1d92c611e79fb2000c29064513'"; // 新浪搜索爬虫使用
        String order = " order by sfq.priority desc limit 0,20;";
        try {
            List<Record> list = db.find(sql1 + order);
            if (list.size() == 0)
                return null;
            List<JobEntity> ljb = new ArrayList<JobEntity>();
            for (Record r : list) {
                ljb.add(this.oracleRecordToJobEntity(r));
            }
            if (log.isDebugEnabled()) {
                log.debug("job list : ljb = {}", ljb.toArray().toString());
            }
            return ljb;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取恢复任务信息
     *
     * @param jobs 所有任务 id
     * @return List<JobEntity>
     */
    public List<JobEntity> getRestoreTask(String[] jobs) {
        try {
            String sql = "select sfs.uid as subtaskid, sfs.keywords, sfs.tid, sfs.sitename as name, sw.sourceid, sw.sitetype as platform "
                    + "from spd_full_subtasks sfs " +
                    "inner join site_websites sw on sfs.siteid = sw.uid " +
                    "where sfs.uid in (##);";
            StringBuilder sb = new StringBuilder();
            for (Integer i = jobs.length; i-- > 0; ) {
                sb.append(i == 0 ? "?" : "?,");
            }
            sql = sql.replaceAll("##", sb.toString());
            List<Record> list = db.find(sql, jobs);
            List<JobEntity> ljb = new ArrayList<JobEntity>();
            for (Record r : list) {
                ljb.add(this.oracleRecordToJobEntity(r));
            }
            return ljb;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public String getLocation(String ip) {
        if (StrKit.isBlank(ip))
            return "";
        String location = db.findFirst("select get_ip_value(?) location", ip).getStr("location");
        return StrKit.isBlank(location) ? "" : location;
    }

    public int getLocationCode(String ip) {
        if (StrKit.isBlank(ip))
            return 0;
        String location = db.findFirst("select get_ip_code(?) location", ip).getStr("location");
        return StrKit.isBlank(location) ? 0 : Integer.parseInt(location);
    }

    public Boolean completeWeiboTask(String id) {
        Integer res1 = db.update("delete from spd_full_queues where subtaskid=?", id);
        Boolean res2 = this.updateTaskExecuteStatus(id);
        if (res1 == 0 || !res2) {
            log.error("微博任务完成失败");
            return false;
        }
        return true;
    }

    /**
     * 将任务队列`spd_full_queues`中对应任务记录删除
     *
     * @param id 任务id
     */
    private Boolean deleteTaskListById(String id) {
        Integer res = db.update("delete from spd_full_queues where subtaskid=?", id);
        if (res == 0)
            log.error("delete id {}, from spd_full_queues error", id);
        return res != 0;
    }

    /**
     * 将子任务表`spd_full_subtasks`中对应任务记录的机器号字段置为本机器
     * 实际上这对 JAVA 爬虫来说这并不是必须的
     *
     * @param id 任务id
     * @return Boolean
     */
    private Boolean updateMachineFlagTaskById(String id, Integer machine) {
        Integer res = db.update("update spd_full_subtasks set machinenumber=? where uid=?", machine, id);
        if (res == 0)
            log.error("update spd_full_subtasks id is {} machine={} error", id, machine);
        return res != 0;
    }

    /**
     * 爬取任务前要做的数据库操作
     *
     * @param id 任务id
     * @return Boolean
     */
    public Boolean delCrawlerJob(final String id, final ClientInfoEntity client) {
        Boolean dtlb = OracleModel.dao.deleteTaskListById(id);
//        Boolean umftb = OracleModel.dao.updateMachineFlagTaskById(id, client.getCli());
        return dtlb;
    }

    /**
     * 更改任务执行状态
     * 执行成功后标记taskState为2（完成）
     *
     * @param id 任务在子任务表spd_full_subtasks中的id
     * @return Boolean
     */
    public Boolean updateTaskExecuteStatus(String id) {
        Integer res = db.update("update spd_full_subtasks set previousexecuteendtime=?,successexecutions=successexecutions+1 where uid=?", new Date(), id);
        if (res == 0)
            log.error("更新 spd_full_subtasks 全网搜索任务 id 为 " + id + " 的数据失败");
        return res != 0;
    }

    /**
     * 推送未完成的任务, 在爬虫升级或者机器中断后, 任务无法完成,
     * 则使用此方法将未完成的任务重新推送到任务列表中
     */
    public void pushUnfinishedTasks() {
        if (log.isDebugEnabled()) {
            log.debug("Enter function pushUnfinishedTasks......");
        }
        String sql = "insert into spd_full_queues (uid, subtaskid, keywords, spdtype, previousexecutetime, tid, siteid, sitename, priority, syscode, selfspd) " +
                "select REPLACE(UUID(), '-', ''), uid, keywords, spdtype, previousexecutetime, tid, siteid, sitename, priority , syscode, selfspd " +
                "from spd_full_subtasks where state = 0 and previousexecuteendtime is null and taskstate != 2 and uid not in (select subtaskid from spd_full_queues);";
        int insCnt = db.update(sql);
        if (log.isDebugEnabled()) {
            log.debug("Exit function pushUnfinishedTasks : sql = {}", sql);
            log.debug("insert record count: {}", insCnt);
        }
    }

}
