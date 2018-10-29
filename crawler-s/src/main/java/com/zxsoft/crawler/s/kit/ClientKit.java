package com.zxsoft.crawler.s.kit;

import com.zxsoft.crawler.common.kit.IpKit;

/**
 * Created by cox on 2015/9/7.
 */
public class ClientKit {

    // private static SlaveInfoEntity sie;

    public static Integer getMachineId() {
        String ip4 = getIp4();
        String[] nums = ip4.split("\\.");
        return Integer.valueOf(nums[2] + nums[3]);
    }

    public static String getIp4() {
        return IpKit.getIp4();
    }

//    public static SlaveInfoEntity getSlaveInfo() {
//        return sie;
//    }
//
//    public static void setSlaveInfo(SlaveInfoEntity sie) {
//        ClientKit.sie = sie;
//    }

}
