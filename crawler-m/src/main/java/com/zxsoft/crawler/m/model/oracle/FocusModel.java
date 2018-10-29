/**
 * 爬虫数据库操作包
 * @author wg
 */
package com.zxsoft.crawler.m.model.oracle;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;
import com.zxsoft.crawler.common.entity.redis.JobEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.Tool;
import com.zxsoft.crawler.common.type.FocusStat;
import com.zxsoft.crawler.common.type.SourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 重点关注数据库操作模型
 * @author wg
 */
public class FocusModel {

	/**
	 * 日志操作对象
	 */
    private static final Logger log = LoggerFactory.getLogger(FocusModel.class);

    /**
     * oracle数据库操作对象
     */
    private static final DbPro db = Db.use(SourceType.MYSQL_BUSI.toString());

    /**
     * 重点关注操作对象
     */
    public static final FocusModel dao = new FocusModel();

    /**
     * 根据数量生成 in 的占位符数量
     * @param number 数量
     * @return String
     */
    private String genPlaceholder(Integer number) {
        StringBuilder sb = new StringBuilder();
        for (; (number--) > 0; )
            sb.append("?").append((number > 0 ? "," : ""));
        return sb.toString();
    }


    /**
     * 获取重点关注列表
     * @param type  重点关注的任务类型
     * @return  获取重点关注任务列表
     */
    private List<JobEntity> getFocusList(String... type) {
        String sql = "select xz.id, xz.mc, xz.user_name, xz.dz, xz.sjc, fc.id as ly, fc.sourceid, fc.gsfl as sourcetype, fc.zdbs as platform, fc.zdmc from xtgl_zdgz xz " +
                "left join fllb_cjlb fc on xz.sswz=fc.zdmc and fc.zdms like '%重点关注%' " +
                "where xz.bz in (##) and fc.zdmc in (##)"; // and xz.id=101
        sql = sql.replaceFirst("##", this.genPlaceholder(2));
        sql = sql.replaceFirst("##", this.genPlaceholder(type.length));

        if(log.isDebugEnabled())
        {
        	log.debug("type is [{}], and sql = [{}]",type.toString() ,sql);
        }
        Object[] os = new Object[type.length + 2];
        Integer[] stats = new Integer[]{FocusStat.NORMAL.getIndex(), FocusStat.WAIT.getIndex()};
        System.arraycopy(stats, 0, os, 0, 2);
        System.arraycopy(type, 0, os, 2, type.length);
        List<Record> list = db.find(sql, os);
        if (CollectionKit.isEmpty(list)) return null;
        List<JobEntity> ljb = new ArrayList<JobEntity>();
        for (Integer i = list.size(); i-- > 0; ) {
            Record f = list.get(i);
            JobEntity je = new JobEntity();
            je.setJobId(String.valueOf(f.getInt("ID")))
                    .setNickName(f.getStr("MC"))
                    .setUsername(f.getStr("USER_NAME"))
                    .setUrl(f.getStr("DZ"))
                    .setTimestamp(Tool.parseLong(f.getStr("SJC")))
                    .setSectionId(f.getBigDecimal("LY").intValue())
                    .setSource_id(f.getBigDecimal("SOURCEID").intValue())
                    .setPlatform(f.getBigDecimal("PLATFORM").intValue())
                    .setSource_name(f.getStr("ZDMC"));
            ljb.add(je);
        }

        if(log.isDebugEnabled())
        {
        	log.debug("任务列表 = [{}].", ljb.toArray().toString());
        }
        return ljb;
    }


    /**
     * 获取重点关注任务列表
     * @return
     */
    public List<JobEntity> getFocusList() {
        return this.getFocusList("腾讯微博");
    }


    /**
     * 修改重点关注用户名
     *
     * @param jobId    任务 id
     * @param userName 用户名
     * @return 更新用户名是否成功
     */
    public Boolean updateUserName(String jobId, String userName) {
        String sql = "update xtgl_zdgz set user_name=? where id=?";
        return db.update(sql, userName, jobId) != 0;
    }

    /**
     * 修改状态
     * @param jobId 任务 id
     * @return 修改状态是否成功
     */
    public Boolean updateStat(String jobId, FocusStat stat) {
        String sql = "update xtgl_zdgz set bz=? where id=?";
        return db.update(sql, stat.getIndex(), jobId) != 0;
    }


    /**
     * 修改最后文章发布时间
     * @param jobId 任务 id
     * @param ts    时间
     * @return 修改是否成功
     */
    public Boolean updateLastPostTime(String jobId, Long ts) {
        String sql = "update xtgl_zdgz set sjc=? where id=?";
        return db.update(sql, ts, jobId) != 0;
    }

}
