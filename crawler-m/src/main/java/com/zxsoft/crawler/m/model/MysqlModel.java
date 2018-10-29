/**
 * 爬虫数据库MODEL接口
 *
 * @author xiawenchao
 */
package com.zxsoft.crawler.m.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.entity.prop.PropEntity;
import com.zxsoft.crawler.common.entity.redis.DetailRuleEntity;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.entity.redis.ListRuleEntity;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.type.JobType;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.common.type.SourceType;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.m.entity.SyncEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * MySQL数据库操作接口
 *
 * @author xiawenchao
 */
public class MysqlModel {

    /**
     * 日志操作实例
     */
    private static final Logger log = LoggerFactory.getLogger(MysqlModel.class);

    /**
     *
     */
    private static final DbPro db = Db.use(SourceType.MYSQL_CONF.toString());
    public static final MysqlModel dao = new MysqlModel();

    /**
     * 獲取信息同步表所有數據
     *
     * @param tables  同步表
     * @param reptile 区域信息
     * @return ses 同步表信息实体列表
     */
    public List<SyncEntity> getSyncList(SyncTable[] tables, ReptileEntity reptile) {
        if (log.isDebugEnabled()) {
            log.debug("enter function getSyncList, and starting sync tables : tables = {} ,reptile =  {}",
                    tables.toString(), reptile.toString());
        }
        List<SyncEntity> ses = new ArrayList<SyncEntity>();
        for (SyncTable table : tables) {
            SyncEntity se = new SyncEntity();
            // String sql = "select * from " + table.getName();
            StringBuilder sb = new StringBuilder();
            sb.append("select * from ").append(table.getName());
            List<Record> data;
            switch (table) {
                //时间规则表
                case TIMEREG:
                    sb.append(" order by sort desc ");
                    data = db.find(sb.toString());
                    break;

                //	区域配置表
                case V_PROP_REPTILE:
                    sb.append(" where reptile=? ");
                    data = db.find(sb.toString(), reptile.getId());
                    break;

                //列表规则
                case V_CONF_LIST:
                    sb.append(" where job_type=? ");
                    data = db.find(sb.toString(), reptile.getType().getIndex());
                    break;

                //未定义来源表
                case TID404:
                    sb.append(" where reptile=? ");
                    data = db.find(sb.toString(), reptile.getId());
                    break;

                //默认
                default:
                    data = db.find(sb.toString());
                    break;
            }
            if (data == null)
                continue;
            se.setTable(table).setData(data);
            ses.add(se);
        }
        if (log.isDebugEnabled()) {
            log.debug("exit function getSyncList, and sync tables started!    ses = {}", ses.toArray().toString());
        }

        return ses;
    }

    /**
     * 根据区域别名获取目标任务数据库连接信息
     *
     * @param alias 别名
     * @return 区域信息
     */
    public ReptileEntity getReptileByAlias(String alias) {

        String sql = "select id, redis, type, alias, url, usr, passwd, active, location from reptile where alias=?";
        if (log.isDebugEnabled()) {
            log.debug("Enter function getReptileByAlias : sql = {} , alias = {}", sql, alias);
        }

        // 查询第一条记录
        Record r = db.findFirst(sql, alias);
        ReptileEntity re = new ReptileEntity();

        // 将字段写入区域信息实体
        for (String key : r.getColumnNames()) {
            if ("type".equals(key)) {
                re.setType(r.getInt(key));
                continue;
            }
            re.set(key, r.get(key));
        }
        if (log.isDebugEnabled()) {
            log.debug("Exit function getReptileByAlias : 区域信息re = {}", re.toString());
        }
        return re;
    }

    /**
     * 若 tid 在 website 中未被发现, 则在此表中进行记录,
     * 当 website 中已添加不存在的 tid 后当前表的 tid 记录应该删除
     *
     * @param tid 來源
     * @return
     */
    public Boolean saveTid404(Integer tid, ReptileEntity reptile) {
        if (MysqlModel.dao.isTid404(tid, reptile))
            return true;
        String sql = "insert into tid404(tid, reptile) values (?,?)";
        return db.update(sql, tid, reptile.getId()) != 0;
    }

    /**
     * tid404 表中是否存在当前 tid
     * 因查询慢问题 不建议使用
     * 方法以转移至 redis.JobModel
     * 仅仅在添加未识别的来源时使用
     *
     * @param tid 來源
     * @return 检验是不是404页面
     */
    @Deprecated
    public Boolean isTid404(Integer tid, ReptileEntity reptile) {
        String sql = "select 1 from tid404 where tid=? and reptile=?";
        return db.findFirst(sql, tid, reptile.getId()) != null;
    }

    /**
     * 获取任务的其它参数, 数据同步已经
     *
     * @param tid 來源
     * @return
     */
    @Deprecated
    public JobEntity getBasicInfos(Integer tid, JobType type) {

        String sql = "select "
                + "w.comment as source_name, w.region, w.tid, ifnull(w.provinceId, 100000) as provinceId, ifnull(w" +
                ".cityId, 100000) as cityId, "
                + "ifnull(w.areaId, 100000) as areaId, s.id as sectionId, s.url, s.comment as type, s.keywordEncode, " +
                "w.cookie, "
                + "c.ajax, s.category, c.listdom, c.linedom, c.urldom, c.datedom, c.updatedom, "
                + "c.synopsisdom, c.authordom " + "from website as w " + "inner join section as s on w.id=s.site "
                + "inner join conf_list as c on s.url=c.url "
                + "where w.tid=? "; /* and s.category='search' */
        Record r = db.findFirst(sql, tid);
        if (r == null) {
            // throw new CrawlerException(ErrorCode.CONF_ERROR, "在 website 表中未发现
            // tid 为 " + tid + " 的数据");
            return null;
        }
        ListRuleEntity lre = new ListRuleEntity();
        lre.setAjax(r.getBoolean("ajax")).setCategory(r.getStr("category")).setListdom(r.getStr("listdom"))
                .setLinedom(r.getStr("linedom")).setUrldom(r.getStr("urldom")).setDatedom(r.getStr("datedom"))
                .setUpdatedom(r.getStr("updatedom")).setSynopsisdom(r.getStr("synopsisdom"))
                .setAuthordom(r.getStr("authordom"));
        JobEntity je = new JobEntity();
        je// .setJobType(type)
                // .setUrl(r.getStr("url"))
                .setSource_name(r.getStr("source_name"))
                // .setSource_id(r.getInt("tid"))
                .setSectionId(r.getInt("sectionId")).setType(r.getStr("type"))
                .setProvince_code(r.getLong("provinceId").intValue()).setCity_code(r.getLong("cityId").intValue())
                .setLocationCode(r.getLong("areaId").intValue()).setCountry_code(r.getInt("region"))
                .setKeywordEncode(r.getStr("keywordEncode")).setCookie(r.getStr("cookie")).setListRule(lre);
        if (type == JobType.NETWORK_SEARCH) {
            je.setUrl(r.getStr("url"));
        }
        Record rd = new Record();
        rd.set("ajax", false);

        return je;
    }

    public List<JobEntity> getAllInspectJob(ReptileEntity reptile) {
        String sql = "select " + "p.section, w.tid, w.comment as source_name, " + "w.region, w.cookie, "
                + "s.comment as type, s.keywordencode, " + "cl.url, s.category, "
                + "cl.listdom, cl.linedom, cl.urldom,  " + "cl.datedom, cl.synopsisdom, "
                + "cd.content as detail_content, " + "cd.author as detail_author, " + "cd.date as detail_date, "
                + "(CASE WHEN s.category = 'news' THEN 1 ELSE 2 END) AS platform " + "from cw_patrol as p "
                + "left join section as s on p.section=s.id "
                + "left join website as w on s.site=w.id " + "left join conf_list as cl on s.url=cl.url "
                + "left join conf_detail as cd on s.url=cd.listurl "
                + "where p.reptile=? ";
        if (log.isDebugEnabled()) {
            log.debug("getAllInspectJob sql is [{}]  and reptile ID is {}.", sql, reptile.getId());
        }
        List<Record> lr = db.find(sql, reptile.getId());
        if (lr == null || lr.isEmpty())
            return null;
        List<JobEntity> lje = new ArrayList<JobEntity>();
        for (Record r : lr) {
            JobEntity je = new JobEntity();
            ListRuleEntity lre = new ListRuleEntity();
            DetailRuleEntity dre = new DetailRuleEntity();
            dre.setContent(r.getStr("detail_content")).setAuthor(r.getStr("detail_author"))
                    .setDate(r.getStr("detail_date"));
            lre.setCategory(r.getStr("category")).setListdom(r.getStr("listdom")).setLinedom(r.getStr("linedom"))
                    .setUrldom(r.getStr("urldom")).setDatedom(r.getStr("datedom"))
                    .setSynopsisdom(r.getStr("synopsisdom"));
            je.setJobId(String.valueOf(r.getInt("section"))).setJobType(JobType.NETWORK_INSPECT).setUrl(r.getStr("url"))
                    .setCountry_code(r.getInt("region"))
                    // TODO 各站点来源暂尚未统一, 待统一后使用 sourceid(oracle
                    // fllb_cjlb.sourceid) 字段 而不是 tid(oracle fllb_cjlb/id)
                    .setSource_id(r.getInt("tid")).setSource_name(r.getStr("source_name")).setType(r.getStr("type"))
                    .setCookie(r.getStr("cookie")).setPlatform(Long.bitCount(r.getLong("platform")))
                    .setKeywordEncode(r.getStr("keywordencode")).setListRule(lre).setDetailRules(dre);
            lje.add(je);
        }
        return lje;
    }

    public Boolean updateInspectCrawlTime(ReptileEntity reptile, String sectionId) {
        String sql = "update cw_patrol set prev_time=now() where reptile=? and section=?";
        return db.update(sql, reptile.getId(), sectionId) != 0;
    }

    /**
     * 获取某个 key 所有区域配置项
     *
     * @param key 区域信息键
     * @param re  区域信息
     * @return 代理实体列表
     */
    public List<PropEntity> getPropList(PropKey key, ReptileEntity re) {

        String sql = "select val from v_prop_reptile where name=? and reptile=?";
        if (log.isDebugEnabled()) {
            log.debug("关键字:{}, 区域信息实体:{}", key, re.toString());
            log.debug(sql, key.getKey(), re.getId());
        }
        List<Record> lr = db.find(sql, key.getKey(), re.getId());
        if (CollectionKit.isEmpty(lr))
            return null;
        List<PropEntity> lpe = new ArrayList<PropEntity>();
        for (Record r : lr) {
            PropEntity pe = new PropEntity();
            pe.setVal(r.getStr("val"));
            lpe.add(pe);
        }
        if (log.isDebugEnabled()) {
            log.debug("代理实体列表lpe:{}", lpe.toArray().toString());
        }
        return lpe;
    }

    /**
     * 获取代理信息的val字段数组
     *
     * @param key 爬虫运行配置关键字
     * @param re  区域信息实体
     * @return 代理信息数组
     */
    public String[] getProp(PropKey key, ReptileEntity re) {
        if (log.isDebugEnabled()) {
            log.debug("enter function getProp.... and key = {} , re = {}", key.toString(), re.toString());
        }

        // 获取代理信息实体列表
        List<PropEntity> props = this.getPropList(key, re);
        if (CollectionKit.isEmpty(props)) {
            return null;
        }
        String[] res = new String[props.size()];
        for (Integer i = 0; i < props.size(); i++)
            res[i] = props.get(i).getVal();
        if (log.isDebugEnabled()) {
            log.debug("exit function getProp....and res = {}", res.toString());
        }

        return res;
    }

}
