package io.openmessaging.consumer.table;

import io.openmessaging.consumer.constant.ConstantConsumer;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by fbhw on 17-12-13.
 */
public class Test {


    @org.junit.Test
    public void testGenerateUnitId() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String ip = inetAddress.getHostAddress();

        char[] ipChar = new char[ip.length()];
        for (int checkNum = 0, charAt = 0; checkNum < ip.length(); checkNum++) {
            char indexChar = ip.charAt((checkNum));
            if (indexChar == '.') {
                continue;

            }

            ipChar[charAt] = ip.charAt(checkNum);
            ++charAt;
        }
        String ipString = new String(ipChar).trim();
        int ipInt = Integer.parseInt(ipString);
        int port = ConstantConsumer.CONSUMER_PORT;
        long currentTime = System.currentTimeMillis();
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pidString = name.split("@")[0];
        int pid = Integer.parseInt(pidString);
        long uniqId = ipInt + port + currentTime + pid;


    }
}
