/**
 * 爬虫master数据库操作模型包
 */
package com.zxsoft.crawler.m.model;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.zxsoft.crawler.common.entity.reptile.ReptileEntity;
import com.zxsoft.crawler.common.type.PropKey;
import com.zxsoft.crawler.common.type.SourceType;
import com.zxsoft.crawler.common.type.SyncTable;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * oracle操作对象
 */
public class BootModel {

	private static final Logger log = LoggerFactory.getLogger(BootModel.class);
	public static final BootModel dao = new BootModel();

	/**
	 * 开启 Oracle 数据库链接
	 * @param jdbcUrl 数据库链接
	 * @param usr 账户
	 * @param passwd 密码
	 * @param maxActive 连接池最大活跃数量
	 * @param showSql 是否显示 sql
	 * @return 数据库开启是否成功
	 */
	public Boolean openOracleDb(String jdbcUrl, String usr, String passwd, Integer maxActive, Boolean showSql) {
		if(log.isDebugEnabled())
		{
			log.debug("jdbcurl = {} , usr = {} , passwd = {} , maxactive = {} , showsql = {}." , jdbcUrl, usr, passwd, maxActive, showSql);
		}
		try {
			//启动数据源
			DruidPlugin dpo = new DruidPlugin(jdbcUrl, usr, passwd);

			//添加过滤器
			dpo.addFilter(new StatFilter());
			WallFilter wf = new WallFilter();
			wf.setDbType("mysql");
			dpo.addFilter(wf);
			dpo.setMaxActive(maxActive);
			dpo.setValidationQuery("select 1");

			ActiveRecordPlugin arpo = new ActiveRecordPlugin(SourceType.MYSQL_BUSI.getName(), dpo);
			arpo.setDialect(new MysqlDialect());
			// arpo.setContainerFactory(new CaseInsensitiveContainerFactory());
			arpo.setShowSql(showSql);

			return dpo.start() && arpo.start();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 生成同步表信息
	 * @param reptile 区域信息实体
	 * @return SyncTable[] 同步表表名数组
	 */
	public SyncTable[] genSyncTables(ReptileEntity reptile) {
		if (log.isDebugEnabled()) {
			log.debug("enter function genSyncTables: reptile = {} .", reptile.toString());
		}

		//获取sync.table同步表表名
		String[] tbs = MysqlModel.dao.getProp(PropKey.SYNC_TABLE, reptile);
		// 如果未发现配置信息, 则表示没有需要同步的表
		if (tbs == null || tbs.length == 0) {
			log.error("数据库中没有需要同步的表! ");
			return null;
		}

		List<SyncTable> lst = new ArrayList<SyncTable>();
		for (String tb : tbs) {
			//
			SyncTable st = SyncTable.getSyncTable(tb);
			if (st == SyncTable.UNKNOW)
				continue;
			lst.add(st);
		}
		if(log.isDebugEnabled())
		{
			log.debug("exit function genSyncTables: lst = {} .", lst.toArray().toString());
		}
		return lst.toArray(new SyncTable[lst.size()]);
	}

	/**
	 * 打开数据库监控
	 * @param webdir webapp 目录
	 * @param contextPath 访问路径
	 * @param port 端口
	 */
	public void openMonitor(String webdir, String contextPath, Integer port) {
		try {
			Server server = new Server(port);
			if(log.isDebugEnabled())
			{
				log.debug("webapp dir: {}", webdir);
			}
			WebAppContext context = new WebAppContext();
			context.setResourceBase(webdir);
			context.setDescriptor(webdir + "WEB-INF/web.xml");
			context.setConfigurations(new Configuration[] {
					// new AnnotationConfiguration(),
					new WebXmlConfiguration(), new WebInfConfiguration(), new TagLibConfiguration(),
					// new PlusConfiguration(),
					new MetaInfConfiguration(), new FragmentConfiguration(),
					// new EnvConfiguration()
			});
			context.setContextPath(contextPath);
			context.setParentLoaderPriority(true);
			server.setHandler(context);
			server.start();
			server.join();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
