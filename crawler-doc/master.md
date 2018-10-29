# 爬虫主控

version: 3.4.beta


## 目录结构

```text
.
└── com
    └── zxsoft
        └── crawler
            └── m
                ├── core
                │   ├── boot
                │   │   ├── BootNetworkFocus.java
                │   │   ├── BootNetworkInspect.java
                │   │   └── BootNetworkSearch.java
                │   └── Master.java
                ├── distribution
                │   ├── construct
                │   │   ├── ConstructDefault.java
                │   │   ├── ConstructJobApi.java
                │   │   ├── focus
                │   │   │   └── ConstructFocusWeiboTencent.java
                │   │   └── networksearch
                │   │       ├── ConstructNetworkSearchDefault.java
                │   │       └── ConstructNetworkSearchWeiboTencent.java
                │   └── JobDistribution.java
                ├── entity
                │   └── SyncEntity.java
                ├── kit
                │   └── JsonKit.java
                ├── model
                │   ├── BootModel.java
                │   ├── job
                │   │   └── ValidatorModel.java
                │   ├── MysqlModel.java
                │   ├── oracle
                │   │   ├── FocusModel.java
                │   │   └── OracleModel.java
                │   └── redis
                │       ├── ClientModel.java
                │       ├── ConfModel.java
                │       ├── InfoModel.java
                │       ├── JobModel.java
                │       ├── SwapModel.java
                │       └── SyncModel.java
                ├── package-info.java
                └── thread
                    ├── info
                    │   ├── ClientThread.java
                    │   └── CrawlerConfThread.java
                    ├── job
                    │   ├── networksearch
                    │   │   ├── push
                    │   │   │   └── PushUnfinishedTaskThread.java
                    │   │   └── redis
                    │   │       ├── DeleteExpireJobThread.java
                    │   │       ├── JobCompleteThread.java
                    │   │       └── RestoreJobThread.java
                    │   ├── package-info.java
                    │   └── sync
                    │       └── SyncThread.java
                    └── search
                        ├── NetworkFocusThread.java
                        ├── NetworkInspectThread.java
                        └── NetworkSearchThread.java
```

## 包结构

1. core
爬虫主控启动类

2. core.boot
具体启动某种类型爬虫主控

3. distribution
任务分配功能

4. distribution.construct
任务构造接口

5. distribution.construct.focus
重点关注任务构造实现

6. distribution.construct.networksearch
全网搜索任务构造实现

7. entity
实体

8. kit
工具类

9. model
数据操作层

10. model.job
任务验证
> 这个包应该和其他 model 进行整合, 目前仅仅是作为重点关注部分站点验证而存在

11. model.oracle
所有 Oracle 数据库操作

12. model.redis
所有 redis 数据库操作

13. thread
线程

14. thread.info
爬虫机器检测线程

15. thread.job
任务线程

16. thread.job.networksearch.push
推送未完成的任务线程

17. thread.job.networksearch.redis
redis 操作线程

18. thread.job.sync
信息同步线程

19. thread.search
任务推送线程

## Master 初始化
1. 初始化 MySQL 数据库连接
2. 获取当前区域爬虫配置信息
3. 从区域中获取 redis 连接地址, 连接 redis
4. 根据当前区域获取此区域同步表, 进行首次表同步

## 启动线程

主控默认启动的线程

### startThreadSync

**表同步线程**

```java
this.ses.scheduleAtFixedRate(new SyncThread(tables, reptile), 1L, 2L, TimeUnit.HOURS);
```

与初始化中进行首次表同步功能相同, 此处是定期进行表同步
同步的表在 web 管理界面中的区域配置选择中设定.

**同步锁**
因同步的表数据各爬虫机可能正在使用, 此时进行同步会导致爬虫机无法找到表中的数据, 因此增加同步锁机制.

加锁

```java
SyncModel.dao.lock
```

解锁

```java
SyncModel.dao.unlock
```

判断是否锁定

```java
SyncModel.dao.isLock
```

在 `SyncModel` 中获取同步表数据以实现锁机制, 若有需要在他出获取数据也应该实现锁机制, 否则可能在同步时发生错误.

> `getAllInfoByTable` 获取数据锁机制使用
> ``` java
> if (this.isLock()) {
>  try {
>    TimeUnit.SECONDS.sleep(5L);
>  } catch (InterruptedException e) {
>    log.error(e.getMessage(), e);
>  }
>  return this.getAllInfoByTable(table);
> }
> ```


### startThreadClient

**爬虫可用性检测线程**

```java
this.ses.scheduleAtFixedRate(new ClientThread(expire), 0L, 1L, TimeUnit.MINUTES);
```

定期执行, 检测爬虫心跳是否过期, 若过期则删除当前爬虫机器
过期时间配置在管理界面中 区域配置选择 中配置


### startThreadCompleteJob

**任务完成操作线程**

```java
this.ses.scheduleAtFixedRate(new JobCompleteThread(type), 10L, 10L, TimeUnit.SECONDS);
```

定期执行, 时间间隔不宜过长, 此线程作用为完成删除爬虫机已经爬取完成的任务, 若时间过长会导致爬虫执行的任务会一直处于饱和状态, 而实际中爬虫并未在爬取任务

任务完成会删除 Redis 中 `job_del` 相应的任务, 同时删除任务执行 `job_run` 中的任务, 并修改 Oracle 中任务的状态.

### startThreadDeleteExpireJob

**删除过期任务**

```java
this.ses.scheduleAtFixedRate(new DeleteExpireJobThread(), 20L, 20L, TimeUnit.MINUTES);
```

此线程主要是为了在有爬虫更新后任务执行中存在残留任务, 当前爬虫程序中全网搜索已经有了任务恢复线程, 可以不需要当前线程, 但其他类型的爬虫就需要此线程将残留任务删除释放爬虫执行的任务数


### BootApi

**爬虫程序启动接口**
接口中存在两个实现方法

```java
Boolean start();
void startMonitor(Integer port);
```

`start` 启动接口
`startMonitor` 监控服务


## 全网搜索

开启 Oracle 数据库连接, 使之能获取到任务

开启的线程

### startThreadNetworkSearch

**全网搜索任务分发线程**

所有全网搜索线程均从此处获取并构造任务信息, 分发至各个爬虫机器.

// TODO 全网搜索文档待完善

### startThreadRestoreJob

**任务恢复线程**

```java
this.ses.scheduleAtFixedRate(new RestoreJobThread(interval, expire, reptile, identify), 0L, 5L, TimeUnit.HOURS);
```

因爬虫程序升级导致在升级前有任务未被执行, 升级完毕后会将上次未完成的任务放置于 `job_restore` 中, 此处定时检测是否存在恢复任务, 并重新推送任务

### startThreadPushUnfinishedTask

**推送未完成任务线程**

```java
this.ses.scheduleAtFixedRate(new PushUnfinishedTaskThread(), 1L, 1L, TimeUnit.DAYS);
```

重新推送未完成的任务到任务列表中, 实际中这是前期爬虫整改时任务爬取未控制好(或其他原因), 导致有任务爬取后执行表状态未修改, 长期如此会导致很多任务无法结束, 因此增加当前逻辑.
目前仍然保留当前逻辑, 防止再有类似事情发生.
另外此处的推送也会把正在执行的任务再次推送, 因此不建议时间设置过短, 否则会造成很多任务重复爬取.

## 重点关注

### startThreadNetworkFocus

**重点关注任务启动**

## 网络巡检

### startSyncInspectJob

**网络巡检任务启动**

## 任务分发器

所有类型任务分发均从 `JobDistribution` 类中执行 `emit` 方法进行分发, 分发器中根据具体类型任务调用 `ConstructJobApi` 接口构造详细的任务信息, 随后获取空闲的爬虫机器进行任务分发

### constructNetworkSearch

全网搜索任务构造方法

### constructNetworkInspect

网络巡检任务构造方法

### constructNetworkFocus

重点关注任务构造方法

### emit

任务发射器, 所有将要分发的任务均调用当前方法按不同任务类型分别构造任务信息, 并进行分发到空闲爬虫机.

## 任务分发流程

> 以下所述流程为全网搜索类型任务, 其他类型任务均类似

全网搜索类型任务启动时执行 `Master` 中 `startNetworkSearch` 方法, 此处将实例化全网搜索任务启动接口实现 `NetworkSearchThread`, 并调用其 `start` 方法.

在 `NetworkSearchThread.start` 中

```java
this.startThreadNetworkSearch(PropModel.dao.getLong(PropKey.INTERVAL_SEARCH, this.conf.getLong("crawler.slave.network.interval.search")),
                PropModel.dao.getLong(PropKey.HEART_EXPIRE, this.conf.getLong("crawler.slave.heart.expire")) * 1000L,
                this.reptile, PropModel.dao.getStr(PropKey.DATA_IDENTIFY, this.conf.get("crawler.slave.identify")));
```

随后调用 `startThreadNetworkSearch` 启动全网搜索线程 `NetworkSearchThread` .

在全网搜索线程中, 从 Oracle 数据库中获取待爬取的任务, 并判断每个任务站点有效性 (例如微博任务, 因为多数微博任务和普通站点的全网搜索任务爬取方式不同, 因此暂不做处理 以及 判断任务来源是否存在).
当任务站点检测成功后调用任务分发 `JobDistribution` 中的 `emit` 方法, 对任务信息进行构造并推送给爬虫机器.
> 微博任务当前仅有 腾讯微博 有单独的全网搜索处理

随后在 `JobDistribution` 构造任务信息, 例如此处的全网搜索构造方法中 `constructNetworkSearch` 调用 `ConstructJobApi` 接口根据不同站点实现具体任务构造, 返回构造后的任务.
`emit` 方法获取到构造的任务后, 获取空闲的爬虫机器, 将任务分发到该机器中

> 实际中任务分发是将任务信息写入到 redis 中的 `job_run` 表中, 具体的任务信息放置于  `job_swap` 具体见 redis 与 slave 文档中

此时一个任务分发的流程完成了, 进行下一个任务分发.






