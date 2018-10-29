package com.zxsoft.crawler.m.model.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.kit.CollectionKit;
import com.zxsoft.crawler.common.kit.RecordKit;
import com.zxsoft.crawler.common.type.CacheKey;
import com.zxsoft.crawler.common.type.SyncTable;
import com.zxsoft.crawler.m.entity.SyncEntity;
import com.zxsoft.crawler.m.model.MysqlModel;

/**
 * Created by iaceob on 2015/11/14.
 */
public class SyncModel {

    private static final Logger log = LoggerFactory.getLogger(SyncModel.class);
    public static final SyncModel dao = new SyncModel(Const.CACHE_NAME);
    // private static String cacheName;
    private static Cache redis;

    public SyncModel(String cacheName) {
        // SyncModel.cacheName = cacheName;
        SyncModel.redis = Redis.use(cacheName);
    }

    private Double genScore() {
        return 1.0d / (System.currentTimeMillis() / 60000.0d);
    }

//    /**
//     * 删除未发现的信息, 主要是 mysql 中被删除的数据, 再次同步的时候需要删除 redis 中的数据
//     *
//     * @param table 同步表
//     * @param res   redis 中的数据
//     * @param sycd  从 mysql 查询出的数据
//     */
//    private void delNotfoundInfo(SyncTable table, List<Record> res, List<Record> sycd) {
//        // SyncTable st = SyncTable.getSyncTable(table.getName());
//
//        for (Integer i = 0; i < res.size(); i++) {
//            Record r = res.get(i);
//
//            Boolean exist = false;
//            for (Record s : sycd) {
//                switch (table) {
//                    case BLACKLIST:
//                        if (!r.getInt("id").equals(s.getInt("id"))) continue;
//                        exist = true;
//                        if (!s.equals(r)) redis.zrem(table.getName(), RecordKit.toJsonSort(r));
//                        break;
//                    case TIMEREG:
//                        if (!r.getInt("id").equals(s.getInt("id"))) continue;
//                        exist = true;
//                        if (!s.equals(r)) redis.zrem(table.getName(), RecordKit.toJsonSort(r));
//                        break;
//                    case V_PROP_REPTILE:
//                        if (!r.getInt("prop").equals(s.getInt("prop")) &&
//                                !r.getInt("reptile").equals(s.getInt("reptile"))) continue;
//                        exist = true;
//                        if (!s.equals(r)) redis.zrem(table.getName(), RecordKit.toJsonSort(r));
//                        break;
//
//                    case CONF_LIST:
//                        if (!r.getStr("url").equals(s.getStr("url"))) continue;
//                        exist = true;
//                        if (!s.equals(r)) redis.zrem(table.getName(), RecordKit.toJsonSort(r));
//                        break;
//                    case CONF_DETAIL:
//                        if (!r.getStr("listurl").equals(s.getStr("listurl")) &&
//                                !r.getStr("host").equals(s.getStr("host"))) continue;
//                        exist = true;
//                        if (!s.equals(r)) redis.zrem(table.getName(), RecordKit.toJsonSort(r));
//                        break;
//                    case SECTION:
//                        if (!r.getInt("id").equals(s.getInt("id"))) continue;
//                        exist = true;
//                        if (!s.equals(r)) redis.zrem(table.getName(), RecordKit.toJsonSort(r));
//                        break;
//                    case WEBSITE:
//                        if (!r.getInt("id").equals(s.getInt("id"))) continue;
//                        exist = true;
//                        if (!s.equals(r)) redis.zrem(table.getName(), RecordKit.toJsonSort(r));
//                        break;
//                }
//            }
//
//            if (exist) continue;
//            String sr = RecordKit.toJsonSort(r);
//            Long rz = redis.zrem(table.getName(), sr);
//            if (rz != 0L) continue;
//            log.debug("delete not found info -[{}]-, result: {}", sr, rz);
//
//        }
//
//    }

    /**
     * 将同步表中的信息写入redis
     * @param ses 表数据
     */
    private void syncInfo(List<SyncEntity> ses) {
    	log.debug("Enter function syncInfo, and ses = {}", ses.toArray().toString());
        Integer sum = 0;
        for (SyncEntity se : ses) {
        	//先删除redis中存有的数据信息
            redis.del(se.getTable().getName());
            for (Record d : se.getData()) {
            	//将新获取的数据写入redis
                redis.zadd(se.getTable().getName(), this.genScore(), RecordKit.toJsonSort(d));
                sum += 1;
            }
//            List<Record> res = this.getAllInfoByTable(se.getTable());
//            this.delNotfoundInfo(se.getTable(), res, se.getData());
//            for (Record d : se.getData()) {
//                // Record der = this.delRepeat(se.getTableName(), d, res);
//                switch (se.getTable()) {
//                    case TIMEREG:
//                        // d.set("regex", Pattern.compile(d.getStr("regex")));
//                        break;
//                    default:
//                        break;
//                }
//                redis.zadd(se.getTable().getName(), this.genScore(), RecordKit.toJsonSort(d));
//                // sync += der.getInt("sum");
//                sum += 1;
//            }
        }
        log.debug("Exit function syncInfo :  total {}, update {}", sum, sum);
    }

    /**
     * 获取同步表的数据, 此处添加更新锁检测, 若当前正在进行数据更新锁住状态
     * 则等待五秒中后继续, 直到解锁
     *
     * @param table 同步表
     * @return List
     */
    public List<Record> getAllInfoByTable(SyncTable table) {
        if (this.isLock()) {
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            return this.getAllInfoByTable(table);
        }
        Set set = redis.zrevrange(table.getName(), 0, -1);
        List<Record> bls = new ArrayList<Record>();
        if (CollectionKit.isEmpty(set)) return null;
        for (Object obj : set)
            bls.add(com.zxsoft.crawler.m.kit.JsonKit.fromJson(String.valueOf(obj), Record.class));
        return bls;
    }

    /**
     * 同步鎖, 在進行同步前設鎖, 防止在更新時部分數據無法獲取
     *
     * @return Boolean
     */
    public Boolean lock() {
        return redis.set(CacheKey.KEY_LOCK_SYNC.getKey(), true).equals("OK");
    }

    /**
     * 解鎖
     *
     * @return Boolean
     */
    public Boolean unlock() {
        return redis.del(CacheKey.KEY_LOCK_SYNC.getKey()) != 0L;
    }

    /**
     * 當前是否被鎖定
     *
     * @return Boolean
     */
    public Boolean isLock() {
        Boolean lock = redis.get(CacheKey.KEY_LOCK_SYNC.getKey());
        return lock == null ? false : lock;
    }

    /**
     * 同步表
     *
     * @param tables  表
     * @param reptile 区域
     */
    public void syncInfo(SyncTable[] tables, ReptileEntity reptile) {
    	if(log.isDebugEnabled())
    	{
    		log.debug("Starting sync mysql to redis.... and tables = {}, reptile = {}", tables.toString(), reptile.toString() );
    	}

    	//对redis需要修改的数据加锁
        this.lock();
        Long start = System.currentTimeMillis();

        //根据区域获取所有同步表所有信息
        List<SyncEntity> ses = MysqlModel.dao.getSyncList(tables, reptile);
        this.syncInfo(ses);
        Long end = System.currentTimeMillis();
        if(log.isDebugEnabled())
        {
        	 log.debug("Sync mysql to redis use {} seconds", (end - start) / 1000);
        }

        //修改完以后对redis的数据进行解锁
        this.unlock();
    }

    public List<Record> getAllTid404() {
        return this.getAllInfoByTable(SyncTable.TID404);
    }


//    public List<Record> getAllBlacklist() {
//        return this.getAllInfoByTable(SyncTable.BLACKLIST);
//    }

//    public Record getBlacklist(Integer id) {
//        List<Record> lr = this.getAllBlacklist();
//        for (Record r : lr) {
//            if (id.equals(r.getInt("id")))
//                return r;
//        }
//        return null;
//    }




//    @Deprecated
//    public List<Record> getAllConfDetail() {
//        return this.getAllInfoByTable(SyncTable.CONF_DETAIL);
//    }
//
//    @Deprecated
//    public List<Record> getAllConfList() {
//        return this.getAllInfoByTable(SyncTable.CONF_LIST);
//    }
//
//    @Deprecated
//    public List<Record> getAllSection() {
//        return this.getAllInfoByTable(SyncTable.SECTION);
//    }
//
//    @Deprecated
//    public List<Record> getAllWebsite() {
//        return this.getAllInfoByTable(SyncTable.WEBSITE);
//    }


//    @Deprecated
//    public Record getConfDetail(String host, String listUrl) {
//        List<Record> lr = this.getAllInfoByTable(SyncTable.CONF_DETAIL);
//        for (Record r : lr) {
//            if (host.equals(r.getStr("host")) && listUrl.equals(r.getStr("listurl")))
//                return r;
//        }
//        return null;
//    }
//
//    @Deprecated
//    public Record getConfList(String url) {
//        List<Record> lr = this.getAllInfoByTable(SyncTable.CONF_LIST);
//        for (Record r : lr) {
//            if (url.equals(r.getStr("url")))
//                return r;
//        }
//        return null;
//    }
//
//    @Deprecated
//    public Record getSection(Integer id) {
//        List<Record> lr = this.getAllInfoByTable(SyncTable.SECTION);
//        for (Record r : lr) {
//            if (id.equals(r.getInt("id")))
//                return r;
//        }
//        return null;
//    }
//
//    @Deprecated
//    public Record getSectionBySite(Integer site) {
//        List<Record> lr = this.getAllInfoByTable(SyncTable.SECTION);
//        for (Record r : lr) {
//            if (site.equals(r.getInt("site")))
//                return r;
//        }
//        return null;
//    }
//
//    @Deprecated
//    public Record getWebsite(Integer id) {
//        List<Record> lr = this.getAllInfoByTable(SyncTable.WEBSITE);
//        for (Record r : lr) {
//            if (id.equals(r.getInt("id")))
//                return r;
//        }
//        return null;
//    }
//
//    @Deprecated
//    public Record getWebsiteByTid(Integer tid) {
//        List<Record> lr = this.getAllInfoByTable(SyncTable.WEBSITE);
//        for (Record r : lr) {
//            if (tid.equals(r.getInt("tid")))
//                return r;
//        }
//        return null;
//    }


}
