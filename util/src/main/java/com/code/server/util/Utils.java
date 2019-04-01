package com.code.server.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by sunxianping on 2018/5/24.
 */
public class Utils {

    public static final String getLocalIp() throws SocketException {
        String ipString = "";
        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address && !ip.getHostAddress().equals("127.0.0.1")) {
                    return ip.getHostAddress();
                }
            }
        }
        return ipString;
    }


    public static boolean isHasMode(int type, int mode) {
        int c = mode;
        return (c & (1 << type)) >> type == 1;
    }
}
