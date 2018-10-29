
# 爬虫从机

version: 3.4.beta

## Slave 

爬虫从机启动类

### initConf

启动初始化, 连接 redis 获取当前区域爬虫信息

### startThreadHeart

爬虫机器心跳线程, 定时像 redis 中 `client_heart` 写入心跳, 供 Master 判断当前机器是否可用

### startThreadSwapJob

启动任务交换线程, 并执行任务爬取

### restoreJob

恢复任务, 在爬虫程序更新时因为有残留任务, 再次启动后需要将任务重新推送

## 爬虫从机爬取流程

Slave 启动时, 启动任务交换线程 `SwapJobThread` , 此线程定时动 redis 中 `job_swap` 获取任务信息, 找到当前机器被分发的任务后, 调用 `ParseThread` 线程进行爬取作业.

最终调用 `ParseKit.parse` 接口执行具体任务爬取.

```java
    /**
     * 根据任务类型获取相应的爬虫解析器
     * @return ParseKit
     */
    private ParseKit getParseCtrl() {
        switch (this.type) {
            case NETWORK_SEARCH:
                return new NetworkSearchParseCtrl();
            case NETWORK_INSPECT:
                return new NetworkInspectParseCtrl();
            case NETWORK_FOCUS:
                return new NetworkFocusParseCtrl();
            default:
                return new NetworkDefaultParseCtrl();
        }
    }

```

 

