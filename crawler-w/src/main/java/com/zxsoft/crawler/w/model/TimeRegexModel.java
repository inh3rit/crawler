package com.zxsoft.crawler.w.model;

import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.inh3rit.nldp.Nldp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cox on 2015/11/20.
 */
public class TimeRegexModel {

    private static final Logger log = LoggerFactory.getLogger(TimeRegexModel.class);
    public static final TimeRegexModel dao = new TimeRegexModel();

    /**
     * 保存时间抓取规则
     *
     * @param sample
     * @param regex
     * @param sort
     * @param mark
     */
    public Boolean saveRegex(String sample, String regex, Integer sort, String mark, Timestamp ts, String usr) {
        if (this.repeatRegex(regex)) return true;
        String sql = "insert into timereg (sample, regex, mark, sort, result, usr) values (?,?,?,?,?,?)";
        return Db2.update(sql, sample, regex, mark, sort, ts, usr) != 0;
    }

    public Boolean updateRegex(Integer id, String sample, String regex, Integer sort, String mark, Timestamp ts,
                               String usr) {
        String sql = "update timereg set sample=?, regex=?, mark=?, sort=?, usr=?, result=?, mtime=now() where id=?";
        return Db2.update(sql, sample, regex, mark, sort, usr, ts, id) != 0;
    }


    public Page<Record> getPageTimeRegex(Integer pageNumber, Integer pageSize) {
        String sql1 = "select id, sample, regex, mark, sort, result, usr, mtime";
        String sql2 = "from timereg order by mtime desc, sort desc";
        return Db2.paginate(pageNumber, pageSize, sql1, sql2);
    }

    public Boolean deleteTimeRegex(Integer id) {
        String sql = "delete from timereg where id=?";
        return Db2.update(sql, id) != 0;
    }


    /**
     * 规则是否重复
     *
     * @param regex
     * @return
     */
    public Boolean repeatRegex(String regex) {
        String sql = "select 1 from timereg where regex=?";
        return Db2.findFirst(sql, regex) != null;
    }


    public Timestamp matchTime(String sample, String regex, String mark) {
        try {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(sample);
            if (!m.find()) return null;
            String ptime = m.group(mark);
            Nldp nldp = new Nldp(ptime);
            Date date = nldp.extractDate();
            if (date == null) return null;
            Timestamp ts = new Timestamp(date.getTime());
            return ts;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
