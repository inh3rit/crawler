package com.zxsoft.crawler.w.core;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.render.ViewType;
import com.zxsoft.crawler.w.controller.*;
import com.zxsoft.crawler.w.factory.BeetlFactory;
import com.zxsoft.crawler.w.handler.BasePathHandler;
import com.zxsoft.crawler.w.handler.ParamsHandler;
import com.zxsoft.crawler.w.interceptor.AccountInterceptor;
import com.zxsoft.crawler.w.util.RedisPool;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.fn.*;
import org.beetl.ext.format.XSSFormat;
import org.beetl.ext.tag.CompressorPageTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by cox on 2015/8/17.
 */
public class AppConfig extends JFinalConfig {

    private Prop conf = PropKit.use("crawler.properties");
    private static Logger log = LoggerFactory.getLogger(AppConfig.class);


    @Override
    public void configConstant(Constants me) {
        log.debug("是否开发模式: " + (this.conf.getBoolean("crawler.debug") ? "是" : "否"));
        // me.setLoggerFactory(new Slf4jLogFactory());
        me.setDevMode(this.conf.getBoolean("crawler.debug"));
        me.setEncoding("UTF-8");
        me.setMainRenderFactory(new BeetlFactory());
        me.setViewType(ViewType.JSP);
        GroupTemplate gte = BeetlFactory.groupTemplate;
        gte.registerFunction("toJson", new JsonFunction());
        gte.registerFunction("note", new NoteFunction());
        gte.registerFunction("pagination", new PaginationFunction());
        gte.registerFunction("jobType", new JobtypeFunction());
        gte.registerFunction("confKey", new ConfKeyFunction());
        gte.registerTag("compressor", CompressorPageTag.class);
        gte.registerFormat("xss", new XSSFormat());
    }

    @Override
    public void configRoute(Routes me) {
        me.add("/", IndexController.class);
        me.add("/website", WebsiteController.class);
        me.add("/website/section", SectionController.class);
        me.add("/website/conf", ConfController.class);
        me.add("/reptile", ReptileController.class);
        me.add("/help", HelpController.class);
        me.add("/account", AccountController.class);
        me.add("/monitor", MonitorController.class);
        me.add("/blacklist", BlacklistController.class);
        me.add("/timereg", TimeRegexController.class);
        me.add("/service", ServiceController.class);
        me.add("/patrol", PatrolController.class);
        me.add("/prop", PropController.class);
    }

    @Override
    public void configPlugin(Plugins me) {
        DruidPlugin dp = new DruidPlugin(conf.get("db.mysql.url"), conf.get("db.mysql.username"), conf.get("db.mysql.passwd"));
        dp.setInitialSize(3).setMaxActive(10);
        dp.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setDbType("mysql");
        dp.addFilter(wall);
        me.add(dp);
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        arp.setDialect(new MysqlDialect());
        arp.setShowSql(conf.getBoolean("crawler.debug"));
        me.add(arp);
    }

    @Override
    public void configInterceptor(Interceptors me) {
        me.add(new AccountInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {
        me.add(new DruidStatViewHandler("/druid"));
        me.add(new BasePathHandler("resPath", "/well"));
        me.add(new ParamsHandler());
//        me.add(new AccountHandler());
    }

    @Override
    public void afterJFinalStart() {
        RedisPool.clear();

        //String[] cns = ReptileModel.dao.getRedisCacheNames();

        // 同步 mysql 数据到 redis 在 master 中, web 中以无需在做此事
//        if (cns!=null&&cns.length>0) {
//            Long interval = this.conf.getLong("crawler.web.blacklist.interval", 60L);
//            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
//            ses.scheduleAtFixedRate(new SyncBlacklistThread(
//                    cns,
//                    this.conf.get("db.redis.key.blacklist"),
//                    interval
//            ), 1L, interval, TimeUnit.MINUTES);
//        }

        // 启动数据监控
        crawlerDataMonitor();
    }

    /**
     * 监控爬虫数据
     */
    private void crawlerDataMonitor() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 获取当前时间前一小时之间爬虫写入的数据量
                    // 省厅
                    SolrClient solrClient = new HttpSolrClient("http://192.168.32.11:8983/solr/sentiment/");

                    String date = LocalDate.now().toString();
                    int hour = LocalTime.now().getHour();
                    String lasttime = "lasttime:[dateTprehour:00:00Z TO dateTnexthour:00:00Z]";
                    lasttime = lasttime.replaceAll("date", date)
                            .replace("prehour", String.valueOf(hour - 1))
                            .replace("nexthour", String.valueOf(hour));
                    System.out.println(lasttime);

                    SolrQuery params = new SolrQuery();
                    params.set("q", "*:*")
                            .set("fq", "platform:6", lasttime)
                            .set("start", 0)
                            .set("rows", 0)
                            .set("sort", "timestamp desc")
                            .set("shards.tolerant", "true");

                    QueryResponse response = solrClient.query(params);
                    long numFound = response.getResults().getNumFound();

                    log.info("省厅{}日{}点至{}点爬虫写入solr的数据量为：{}", date, hour - 1, hour, numFound);

                    if (0 == numFound) {
                        try {
                            sendWaringMail();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Date(), 60 * 60 * 1000); // 60 * 60 * 1000毫秒
    }

    /**
     * 当爬虫数据异常时，发送示警邮件
     *
     * @throws MessagingException
     */
    private void sendWaringMail() throws MessagingException {
        // 配置发送邮件的环境属性
        final Properties props = new Properties();
        /*
         * 可用的属性： mail.store.protocol / mail.transport.protocol / mail.host /
         * mail.user / mail.from
         */
        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.exmail.qq.com");
        // 发件人的账号
        props.put("mail.user", "xuyetong@zxisl.com");
        // 访问SMTP服务时需要提供的密码
        props.put("mail.password", "Pyf3237829");

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        mailSession.setDebug(true);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        // 设置发件人
        InternetAddress form = new InternetAddress(
                props.getProperty("mail.user"));
        message.setFrom(form);

        // 设置收件人
        InternetAddress to = new InternetAddress("xuyetong@zxisl.com");
        message.setRecipient(Message.RecipientType.TO, to);

        // 设置抄送
        InternetAddress cc = new InternetAddress("shiyangqing@zxisl.com");
        message.setRecipient(Message.RecipientType.CC, cc);

        // 设置密送，其他的收件人不能看到密送的邮件地址
//        InternetAddress bcc = new InternetAddress("aaaaa@163.com");
//        message.setRecipient(Message.RecipientType.CC, bcc);

        // 设置邮件标题
        message.setSubject("爬虫数据监控");

        // 设置邮件的内容体
        int currentHour = LocalTime.now().getHour();
        String content = (currentHour - 1) + "点至" + currentHour + "点之间爬虫写入solr的数据量为零，请查看爬虫程序是否异常！(省厅)";
        message.setContent(content, "text/html;charset=UTF-8");

        // 发送邮件
        Transport.send(message);
    }

    public static void main(String[] args) {
//        JFinal.start(PathKit.getWebRootPath() + "/src/main/webapp", 8099, "/", 5);
        new AppConfig().crawlerDataMonitor();
    }

}
