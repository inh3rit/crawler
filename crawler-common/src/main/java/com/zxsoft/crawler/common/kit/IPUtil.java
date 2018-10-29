package com.zxsoft.crawler.common.kit;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class IPUtil {
    public IPUtil() {
    }

    public static List<String> getIPv4() {
        Enumeration allNetInterfaces = null;

        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException var5) {
            return null;
        }

        LinkedList ips = new LinkedList();

        while(allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface)allNetInterfaces.nextElement();
            Enumeration inetAddrs = netInterface.getInetAddresses();

            while(inetAddrs.hasMoreElements()) {
                InetAddress inetAddr = (InetAddress)inetAddrs.nextElement();
                if(inetAddr != null && !inetAddr.isLoopbackAddress() && inetAddr instanceof Inet4Address) {
                    ips.add(inetAddr.getHostAddress());
                }
            }
        }

        return ips;
    }

    public static List<String> getIPv6() {
        Enumeration allNetInterfaces = null;

        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException var5) {
            return null;
        }

        LinkedList ips = new LinkedList();

        while(allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface)allNetInterfaces.nextElement();
            Enumeration inetAddrs = netInterface.getInetAddresses();

            while(inetAddrs.hasMoreElements()) {
                InetAddress inetAddr = (InetAddress)inetAddrs.nextElement();
                if(inetAddr != null && !inetAddr.isLoopbackAddress() && inetAddr instanceof Inet6Address) {
                    ips.add(inetAddr.getHostAddress());
                }
            }
        }

        return ips;
    }
}
