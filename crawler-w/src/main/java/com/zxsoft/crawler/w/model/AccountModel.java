package com.zxsoft.crawler.w.model;

import com.zxsoft.crawler.w.util.Db2;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import name.iaceob.kit.id.IdKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/8/31.
 */
public class AccountModel extends Model<AccountModel> {

    private static final Logger log = LoggerFactory.getLogger(AccountModel.class);
    public static final AccountModel dao = new AccountModel();

    public Boolean saveAccount(String name, String passwd){
        String sql = "insert into account(id, username, password) values (?,?,?)";
        String id = IdKit.run.genId();
        log.debug("ID: " + id);
        return Db2.update(sql, id, name, passwd)!=0;
    }

    public Record getAccountByName(String name){
        String sql = "select id, username, password from account where username=?";
        return Db2.findFirst(sql, name);
    }

    public Boolean existName(String name) {
        String sql = "select 1 from account where username=?";
        return Db2.findFirst(sql, name)!=null;
    }

}
