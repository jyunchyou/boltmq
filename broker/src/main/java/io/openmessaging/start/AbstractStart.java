package io.openmessaging.start;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.broker.NameServerInfo;
import io.openmessaging.exception.RegisterException;
import io.openmessaging.net.NettyServer;
import io.openmessaging.processor.ProcessorIn;
import io.openmessaging.table.*;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;


/**
 * Created by fbhw on 17-12-7.
 */
public class AbstractStart {

    private static BProperties implProperties = null;

    public static NamingService naming = null;

    public static void main(String[] args) throws IOException {
        loadMassageIndex();


    }


    public static void loadMassageIndex() throws IOException {
        File file = new File(ConstantBroker.ROOT_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        String[] indexFileNames = file.list();

        Integer[] i = new Integer[indexFileNames.length];


        if (i.length == 0) {
            return;//无消息数据
        }

        for (int index = 0;index < indexFileNames.length;index++) {

            i[index] = Integer.parseInt(indexFileNames[index]);

        }

        String fileName = ConstantBroker.ROOT_PATH + Collections.max(Arrays.asList(i));
        File lastFile = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(lastFile);


        long index = 0;
        byte b = (byte) fileInputStream.read();
        long lastMark = 0;
        index++;
        int fileCount = 0;//单个文件含有的索引节点数
        while (b != -1) {
            b = (byte) fileInputStream.read();

            index++;
            if (b == ConstantBroker.loadFlag) {
                lastMark = index;
                ++fileCount;
            }
        }

        int fileSize = indexFileNames.length;

        //设置写入下标
        MessageFileQueue.fileWriteIndex = (fileSize - 1) * ConstantBroker.FILE_SIZE + lastMark;

        //设置已用文件个数
        MessageFileQueue.queueIndex = fileSize - 1;


        //初始化文件对象
        MessageFileQueue.abstractMessageFile = new AbstractMessageFile(Collections.max(Arrays.asList(i)) + "",ConstantBroker.FILE_SIZE);
        MessageFileQueue.messageIndex = (Long.parseLong(String.valueOf(Collections.max(Arrays.asList(i))/ ConstantBroker.INDEX_FILE_SIZE)) * ConstantBroker.INDEX_FILE_NODE_NUM + fileCount);
        //跳到写入位置
        MessageFileQueue.abstractMessageFile.getMappedByteBuffer().position((int) lastMark);

    }

    public static void loadIndexMap() throws IOException {
        File root = new File(ConstantBroker.ROOT_INDEX_PATH.substring(0,ConstantBroker.ROOT_INDEX_PATH.lastIndexOf("\\")));
        if (!root.exists()) {
            root.mkdirs();
            //如果不存在初始化索引root地址
            return;
        }

        //加载topic
        File[] files = root.listFiles();

        int fileSize = 0;
        for (File file : files) {
            IndexFileQueue indexFileQueue = new IndexFileQueue(file.getName());
            String[] indexFileNames = file.list();
            if (indexFileNames == null) {
                continue;
            }
            fileSize = indexFileNames.length;
            Integer[] i = new Integer[indexFileNames.length];
            if (i.length == 0) {

                continue;
            }
            for (int index = 0;index < indexFileNames.length;index++) {

                i[index] = Integer.parseInt(indexFileNames[index]);

            }


            String fileName = ConstantBroker.ROOT_INDEX_PATH + file.getName() + "\\" + Collections.max(Arrays.asList(i));

            File lastFile = new File(fileName);

            FileInputStream fileInputStream = new FileInputStream(lastFile);
            byte[] endFlag = new byte[8];
            int longNum = 0;

            byte[] t = new byte[8];

            fileInputStream.skip(t.length);
            longNum++;//第一个message的offset可能就为0,跳过
            while (fileInputStream.read(t) != -1) {
                if (Arrays.equals(endFlag,t)) {
                    longNum++;
                    break;
                }
            }
            fileInputStream.close();
            indexFileQueue.setQueueIndex(fileSize - 1);
            indexFileQueue.setIndex((fileSize - 1) * ConstantBroker.INDEX_FILE_SIZE + (longNum - 1) * 8);


            //初始化最新消息下标
            indexFileQueue.setMessageIndex(indexFileNames.length * ConstantBroker.INDEX_FILE_NODE_NUM + longNum);


            //初始化文件对象
            indexFileQueue.setAbstractIndexFile(new AbstractIndexFile(file.getName(),Collections.max(Arrays.asList(i)) + "", ConstantBroker.INDEX_FILE_SIZE));
            IndexFileQueueMap.indexQueueMap.put(file.getName(),indexFileQueue);
            //跳到待写入位置
            indexFileQueue.getAbstractIndexFile().position((int) (indexFileQueue.getIndex() - indexFileQueue.getQueueIndex() * ConstantBroker.INDEX_FILE_SIZE));
            }
        //加载索引下标




/*

        ProcessorIn processorIn = new ProcessorIn();
        long  l  =  33333333L;
        int  l2  = 33333333;
        long  l3  = 33333333L;
        //IndexFileQueueMap.indexQueueMap.put("topictest1",new IndexFileQueue("topictest1"));
        for (int index = 0;index < 10;index++) {
            processorIn.inputIndex(l, l2, l3, "topictest1");
        }
*/

    }

    //开启服务发现
    public static void registry(BProperties properties) throws RegisterException {

        implProperties = properties;

        //接收producer发来的消息
        //NettyServer.nettyServer.bind(ConstantBroker.BROKER_MESSAGE_PORT);
        /*//发送表到nameServer
        java.util.Timer timer = new java.util.Timer();//
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                nettyServer.sendTableToNameServer(nameServerInfo);

            }

        },0, ConstantBroker.SEND_TABLE_TIMER_PERIOD);

        nettyServer.bindNameServerPort(ConstantBroker.NAMESERVER_PORT);
        */
        //绑定到nacos



        try {
            naming = NamingFactory.createNamingService(properties.getServer());
            if (properties.getGroup() == null) {
                naming.registerInstance(properties.getServiceName(),properties.getIp(),properties.getPort());
                return;
            }
            naming.registerInstance(properties.getServiceName(), properties.getIp(),properties.getPort(),properties.getGroup());

        }catch (Exception e) {
            e.printStackTrace();
            throw new RegisterException();
        }





    }

}
