package com.zxsoft.crawler.m.model.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.common.entity.redis.ClientHeartEntity;
import com.zxsoft.crawler.common.entity.redis.ClientInfoEntity;
import com.zxsoft.crawler.common.entity.redis.RunJobEntity;
import com.zxsoft.crawler.common.kit.rediskey.ClientHeartKit;
import com.zxsoft.crawler.common.kit.rediskey.ClientInfoKit;
import com.zxsoft.crawler.common.kit.rediskey.RunJobKit;
import com.zxsoft.crawler.common.type.CacheKey;
import com.zxsoft.crawler.exception.CrawlerException;

/**
 * Created by iaceob on 2015/11/14.
 */
public class ClientModel {

	private static final Logger log = LoggerFactory.getLogger(ClientModel.class);
	public static final ClientModel dao = new ClientModel(Const.CACHE_NAME);
	private static final Integer BUFSIZE = 32;
	private static Cache redis;

	public ClientModel(String cacheName) {
		ClientModel.redis = Redis.use(cacheName);
	}

	/**
	 * 檢測機器心跳是否超時
	 *
	 * @return
	 */
	public Boolean isExpire(Long ts, Long expire) {
		Long now = System.currentTimeMillis();
		if ((now - ts) > expire) {
			log.debug("now: {}", now);
			log.debug("ts: {}", ts);
			log.debug("expire: {}", now - ts > expire);
		}
		return now - ts > expire;
	}

	/**
	 * cli:ip:maxPerform:timestamp
	 * 获取所有爬虫机信息
	 *
	 * @return List<ClientHeartEntity>
	 * @throws CrawlerException
	 */
	private List<ClientHeartEntity> getAllHeart() throws CrawlerException {
		Set<String> set = redis.zrevrange(CacheKey.KEY_HEART.getKey(), 0, -1);
		if (set == null || set.isEmpty())
			throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR_SLAVE_BUSY, "No executed client");
		List<ClientHeartEntity> lche = new ArrayList<ClientHeartEntity>();
		for (String s : set) {
			lche.add(ClientHeartKit.resolve(s));
		}
		return lche;
	}

	/**
	 * cli:ip:job:timestamp
	 * 获取所有待执行的任务
	 * @return
	 * @throws CrawlerException
	 */
	public List<ClientInfoEntity> getAllClientInfo() throws CrawlerException {
		Set<String> set = redis.zrevrange(CacheKey.KEY_RUN.getKey(), 0, -1);
		List<ClientInfoEntity> lrje = new ArrayList<ClientInfoEntity>();
		// 遍历所有正在执行的任务
		for (String s : set) {
			// 将任务转化为 RunJobEntity 对象
			ClientInfoEntity cie = ClientInfoKit.resolve(s);
			Boolean exist = false; // 标记是否存在
			// 遍历所有任务列表, 查看当前任务是否存在与 lrje 中
			for (Integer i = lrje.size(); i-- > 0;) {
				ClientInfoEntity r2 = lrje.get(i);
				// 依据 cli 和 ip 共同判断
				if (!cie.getCli().equals(r2.getCli()) || !cie.getIp().equals(r2.getIp()))
					continue;
				r2.setTotal(r2.getTotal() + 1); // 存在修改执行总数
				lrje.set(i, r2); // 修改 lrje 中的机器任务信息
				exist = true; // 如果存在则标记为 true
				break;
			}
			if (exist)
				continue;
			// 不存在只需要添加执行数为 1
			cie.setTotal(1);
			lrje.add(cie);
		}
		return this.mergeHeartRunJob(lrje);
	}

	/**
	 * 获取所有正在运行的任务
	 * @return
	 */
	public List<RunJobEntity> getAllRunJob() {
		Set<String> set = redis.zrevrange(CacheKey.KEY_RUN.getKey(), 0, -1);
		List<RunJobEntity> lrje = new ArrayList<RunJobEntity>();
		for (String s : set) {
			RunJobEntity rje = RunJobKit.resolve(s);
			lrje.add(rje);
		}
		return lrje;
	}

	/**
	 * 合并机器心跳执行, 若不存在任务, 但机器仍然是可用的 需要加入到所有的执行机器中
	 *
	 * @param lrje
	 * @return
	 * @throws CrawlerException
	 */
	private List<ClientInfoEntity> mergeHeartRunJob(List<ClientInfoEntity> lrje) throws CrawlerException {
		List<ClientHeartEntity> lche = this.getAllHeart();
		for (ClientHeartEntity che : lche) {
			Boolean exist = false;
			for (ClientInfoEntity rje : lrje) {
				if (!che.getIp().equals(rje.getIp()) || !che.getCli().equals(rje.getCli()))
					continue;
				exist = true;
			}
			if (exist)
				continue;
			ClientInfoEntity var2 = new ClientInfoEntity();
			var2.setCli(che.getCli()).setIp(che.getIp()).setTs(che.getTs()).setTotal(0);
			lrje.add(var2);
		}
		return lrje;
	}

	/**
	 * 获取最优的执行机器
	 *
	 * @return
	 */
	private ClientInfoEntity getBestClient(Long expire) throws CrawlerException {
		List<ClientHeartEntity> lche = this.getAllHeart();
		List<ClientInfoEntity> lrje = this.getAllClientInfo();

		Boolean run = true;
		ClientInfoEntity minRje = null;
		while (run) {
			if (lrje.isEmpty()) {
				run = false;
				continue;
			}
			for (Integer i = lrje.size(); i-- > 0;) {
				ClientInfoEntity rje = lrje.get(i);
				if (minRje == null) {
					minRje = rje;
					continue;
				}
				if (minRje.getTotal() <= rje.getTotal())
					continue;
				minRje = rje;
			}
			if (!this.availableClient(minRje, lche, expire)) {
				lrje.remove(minRje);
				minRje = null;
				continue;
			}
			run = false;
		}
		if (minRje == null)
			throw new CrawlerException(CrawlerException.ErrorCode.SYSTEM_ERROR_SLAVE_BUSY, "No executed client");
		return minRje;
	}

	private Boolean availableClient(ClientInfoEntity rje, List<ClientHeartEntity> lche, Long expire) {
		for (ClientHeartEntity che : lche) {
			if (!che.getCli().equals(rje.getCli()) || !che.getIp().equals(rje.getIp()))
				continue;
			if (rje.getTotal() >= che.getMaxPerform())
				return false;
			return !this.isExpire(che.getTs(), expire);
		}
		return false;
	}

	public ClientInfoEntity getUseableClient(Long expire) throws CrawlerException {
		return this.getBestClient(expire);
	}

	/**
	 * 删除过期的爬虫机
	 * cli:ip:maxPerform:timestamp
	 * @param expire 过期时间
	 */
	public void delExpireClient(Long expire) {
		if (log.isDebugEnabled()) {
			log.debug("Starting del expire client... and expire = {}", expire.toString());
		}
		Set<String> set = redis.zrevrange(CacheKey.KEY_HEART.getKey(), 0, -1);
		Integer count = 0;
		List<String> ls = new ArrayList<String>();
		for (String s : set) {
			ClientHeartEntity che = ClientHeartKit.resolve(s);
			if (!this.isExpire(che.getTs(), expire))
				continue;
			redis.zrem(CacheKey.KEY_HEART.getKey(), s);
			ls.add(che.getIp());
			count += 1;
		}
		if (log.isDebugEnabled()) {
			log.debug("delete {} client, {}", count, JsonKit.toJson(ls));
		}
		set.clear();
	}

	// /**
	// * 發送任務給執行機器
	// * 以不再使用
	// * @param ip 機器 ip
	// * @param port 服務端口
	// * @param je 任務
	// * @return
	// */
	// @Deprecated
	// public Boolean receiveJob(String ip, Integer port, JobEntity je) {
	// InputStream in;
	// OutputStream out;
	// try {
	// Socket socket = new Socket(ip, port);
	//
	// socket.setSoTimeout(15000);
	// in = socket.getInputStream();
	// out = socket.getOutputStream();
	//
	// String recdata = Disgest.encodeRC4(JsonKit.toJson(je), Const.SALT_JOB);
	// out.write(recdata.getBytes());
	// socket.shutdownOutput();
	//
	//
	// String res = "";
	// Integer recvMsgSize;
	// byte[] receiveBuf = new byte[BUFSIZE];
	// while ((recvMsgSize = in.read(receiveBuf)) != -1) {
	// res += new String(receiveBuf, 0, recvMsgSize);
	// }
	// String dres = Disgest.decodeRC4(res, Const.SALT_JOB);
	// ExchangeEntity ee = JSON.parseObject(dres, ExchangeEntity.class);
	// if (ee.getStat() < 0) {
	// log.error(ee.getMsg());
	// return false;
	// }
	// return true;
	// } catch (UnknownHostException e) {
	// log.error(e.getMessage());
	// } catch (IOException e) {
	// log.error(e.getMessage());
	// } catch (Exception e) {
	// log.error(e.getMessage());
	// }
	// return false;
	// }

}
