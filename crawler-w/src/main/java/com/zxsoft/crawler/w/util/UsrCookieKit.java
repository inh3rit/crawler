package com.zxsoft.crawler.w.util;

import com.zxsoft.crawler.common.c.Const;
import com.zxsoft.crawler.w.entity.UsrCookieEntity;
import name.iaceob.kit.disgest.Disgest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cox on 2015/12/15.
 */
public class UsrCookieKit {

    private static final Logger log = LoggerFactory.getLogger(UsrCookieKit.class);


    public static String serialize(UsrCookieEntity uce) {
        StringBuilder sb = new StringBuilder();
        sb.append(Disgest.encodeRC4(uce.getId(), Const.HEARTENCRYKEY))
                .append(Const.HEARTESPLIT)
                .append(Disgest.encodeRC4(uce.getName(), Const.HEARTENCRYKEY))
                .append(Const.HEARTESPLIT)
                .append(Disgest.encodeRC4(String.valueOf(uce.getSignTime()), Const.HEARTENCRYKEY));
        return Disgest.encodeRC4(sb.toString(), Const.HEARTENCRYKEY);
    }

    public static UsrCookieEntity resolve(String str) {
        try {
            String deStr = Disgest.decodeRC4(str, Const.HEARTENCRYKEY);
            String[] dss = deStr.split(Const.HEARTESPLIT);
            UsrCookieEntity uce = new UsrCookieEntity();
            uce.setId(Disgest.decodeRC4(dss[0], Const.HEARTENCRYKEY))
                    .setName(Disgest.decodeRC4(dss[1], Const.HEARTENCRYKEY))
                    .setSignTime(Long.valueOf(Disgest.decodeRC4(dss[2], Const.HEARTENCRYKEY)));
            return uce;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
