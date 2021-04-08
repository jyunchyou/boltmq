package io.openmessaging.consumer.consumer;

import com.alibaba.nacos.api.exception.NacosException;/*179 180 181 182 183 184*/
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.channel.Channel;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.exception.RegisterException;
import io.openmessaging.consumer.net.NettyConsumer;
import io.openmessaging.consumer.table.ConsumerBrokerMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by fbhw on 17-12-7.
 */
public class FactoryConsumer {

    //Logger logger = LoggerFactory.getLogger(FactoryConsumer.class);

    public static NamingService naming = null;


    public static void sortInstance(Instance[] instances) throws Exception {
        for (int index = 0;index < instances.length;index++) {

            for (int index2 = index+1;index2 < instances.length;index2++) {

                if (!compare(instances[index],instances[index2])){
                    Instance temp = instances[index];
                    instances[index] = instances[index2];
                    instances[index2] = temp;
                }
            }



        }
    }


    //比较规则 第三网段》第四网段》端口
    public static boolean isIdentica(Instance instance1, Instance instance2) throws Exception {

        int index1 = instance1.getIp().lastIndexOf(",");
        int index2 = instance1.getIp().lastIndexOf(",",index1);
        int networkSegment1 = Integer.parseInt(instance1.getIp().substring(index2,index1));
        int networkSegment2 = Integer.parseInt(instance1.getIp().substring(index1));
        int port = instance1.getPort();


        int i1 = instance2.getIp().lastIndexOf(",");
        int i2 = instance2.getIp().lastIndexOf(",",i1);
        int n1 = Integer.parseInt(instance2.getIp().substring(i2,i1));
        int n2 = Integer.parseInt(instance2.getIp().substring(i1));
        int p = instance2.getPort();
        if (networkSegment1 < n1) {
            return true;
        }
        if (networkSegment1 > n1) {
            return false;
        }
        if (networkSegment2 < n2) {
            return true;
        }
        if (networkSegment2 > n2) {
            return false;
        }
        if (port < p) {
            return true;
        }
        if (port > p) {
            return false;
        }

        throw new Exception();
    }


    //比较规则 第三网段》第四网段》端口
    public static boolean compare(Instance instance1, Instance instance2) throws Exception {

        int index1 = instance1.getIp().lastIndexOf(",");
        int index2 = instance1.getIp().lastIndexOf(",",index1);
        int networkSegment1 = Integer.parseInt(instance1.getIp().substring(index2,index1));
        int networkSegment2 = Integer.parseInt(instance1.getIp().substring(index1));
        int port = instance1.getPort();


        int i1 = instance2.getIp().lastIndexOf(",");
        int i2 = instance2.getIp().lastIndexOf(",",i1);
        int n1 = Integer.parseInt(instance2.getIp().substring(i2,i1));
        int n2 = Integer.parseInt(instance2.getIp().substring(i1));
        int p = instance2.getPort();
        if (networkSegment1 < n1) {
            return true;
        }
        if (networkSegment1 > n1) {
            return false;
        }
        if (networkSegment2 < n2) {
            return true;
        }
        if (networkSegment2 > n2) {
            return false;
        }
        if (port < p) {
            return true;
        }
        if (port > p) {
            return false;
        }

        throw new Exception();
    }

    public static void shardingBroker(String topic,String consumersString,String consumeName,int model) throws Exception {
/*

        new Thread(new Runnable() {
            public void run() {
                for (;;) {
*/
                try {
                    List<Instance> instances = FactoryConsumer.naming.getAllInstances("broker2",true);

                    List<Instance> consumerInstances = FactoryConsumer.naming.getAllInstances(consumersString,true);



                    Object[] brokers = instances.toArray();

                    Object[] consumers =  consumerInstances.toArray();
                    int brokerNum = instances.size();

                    int consumerNum = consumerInstances.size();

                    int consumeBrokerNum = 0;
                    try {
                        consumeBrokerNum = brokerNum / consumerNum;

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //将所有consume的instance与它待消费的broker的instance放在一起

                    for (int index = 0;index < consumerInstances.size();index++) {

                        Instance instances1 = (Instance) consumers[index];

                        List bs = new LinkedList();


                        for (int index2 = index * consumeBrokerNum;index2 < index * consumeBrokerNum + consumeBrokerNum;index2++) {

                            bs.add(brokers[index2]);
                        }

                        ConsumerBrokerMap.consumerBrokerMap.put(instances1.getInstanceId(),bs);

                    }

                    int index2 = 0;

                    //不够整数除，余下的instance从第一个consumer开始分配
                    if (brokerNum%consumerNum != 0) {
                        for (int index = 0; index2 < brokerNum; index++) {

                            index2 = consumerNum * consumeBrokerNum + index;

                            Instance instance = (Instance) consumers[index];
                            List bs = ConsumerBrokerMap.consumerBrokerMap.get(instance.getInstanceId());
                            bs.add(brokers[index2]);
                        }
                    }
            for (Instance instance : instances) {
                NettyConsumer.nettyConsumer.putNewChannel(topic,instance,consumeName,model);
            }
                    Thread.sleep(ConstantConsumer.SCHEDULE_UPDATE_CHANNEL_TIME);

                } catch (NacosException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

/*
            }}
        }).start();
*/


        //定时获取更多连接
       /* ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(new Runnable() {
            public void run() {
                try {
                    List<Instance> instances = FactoryConsumer.naming.getAllInstances("brocker",true);

                    for (Instance instance : instances) {
                        NettyConsumer.nettyConsumer.putNewChannel(instance);
                    }
                } catch (NacosException e) {
                    e.printStackTrace();
                }

            }
        }, ConstantConsumer.SCHEDULE_UPDATE_CHANNEL_TIME, TimeUnit.SECONDS);


*/


    }


    public static void registry(BProperties BProperties) throws RegisterException {
        try {
            naming = NamingFactory.createNamingService(BProperties.getServer());
            if (BProperties.getGroup() == null) {
                naming.registerInstance(BProperties.getServiceName(), BProperties.getIp(), BProperties.getPort());
                return;
            }
            naming.registerInstance(BProperties.getServiceName(), BProperties.getIp(), BProperties.getPort(), BProperties.getGroup());

        }catch (Exception e) {
            e.printStackTrace();
            throw new RegisterException();
        }

    }

    enum CONSUME_MODEL{
        Batch
    }

    public static AbstractConsumer createConsumer(String topic,String instanceId,ConsumeCallBack consumeCallBack,int model) throws RegisterException, Exception {

        FactoryConsumer.shardingBroker(topic,ConstantConsumer.CONSUME_SERVICE_NAME,instanceId,model);


        AbstractConsumer abstractConsumer = new AbstractConsumer(topic,instanceId,consumeCallBack);


        shardingChannel(topic,instanceId);
/*

        new Thread(new Runnable() {
            public void run() {
                //创建channel
                for (;;) {
                  shardingChannel(instanceId,consumeCallBack);

                    try {
                        Thread.sleep(ConstantConsumer.SCHEDULE_UPDATE_CHANNEL_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
*/

        return abstractConsumer;
    }


    public static void shardingChannel(String topic,String instanceId){
            List<Instance> list = (List) ConsumerBrokerMap.consumerBrokerMap.get(instanceId);

            if (list == null) {
                return;
            }

            //新增channel
            for (Instance instance : list) {
                 System.out.println(instance.getInstanceId());
            }
            //删除不属于当前consume的channel
            Set<String> set = NettyConsumer.channelMap.keySet();
            for (String id : set) {

                boolean deleteFlag = true;
                for (Instance instance : list) {


                    if (id.equals(topic + instance.getInstanceId())) {
                        deleteFlag = false;
                    }

                }
                if (deleteFlag) {
                    Channel channel = NettyConsumer.channelMap.get(id);
                    channel.close();
                    NettyConsumer.channelMap.remove(id);

                }
            }




    }


    public static void main(String[] args) throws IOException, ParseException {
      /*  File file = new File("F:\\log2.txt");

        Map<String,Integer> map = null;

        int count = 0;
        String lastTime = "";
            FileInputStream fileInputStream = new FileInputStream(file);



        while (true) {
            byte[] b = new byte[100000];


            if (fileInputStream.read(b) == -1){
                break;
            }


            String s = (new String(b, "gbk"));

            int end = 0;
            for (int index = 0; index < s.length(); index++) {



                if ("{".getBytes()[0] == (b[index])) {


                    if ("1".getBytes()[0] != (b[index + 1])) {

                        end = index;
                        continue;
                    }


                    map = new HashMap();



                    if ("1".getBytes()[0] != (b[index + 1])) {

                        end = index;
                        continue;
                    }

                    byte[] a = new byte[98];
                    int in = 0;
                    try {
                        for (int i = index - 98; i < index; i++, in++) {

                            a[in] = b[i];
                                 }
                        String ai = new String(a,"gbk");
                        if ("188".equals(ai.substring(50, 53))) {
                            System.out.println(ai.substring(0, 20));
                            System.out.println(ai.substring(50, 53));

                        }else{

                            end = index;
                            continue;

                        }
                    }catch (Exception e){
                        end = index;
                        continue;
                    }
                    //System.out.println(new String(a, "gbk"));
                    //System.out.println(++count);



                    for (int i = index;i>end;i--) {

                        if ("E".getBytes()[0] == b[i]) {

                            if ("P".getBytes()[0] == b[i+1]) {
                                if ("C".getBytes()[0] == b[i+2]) {


                                    byte[] fd = new byte[37];
                                    int id = 0;
                                    for (int c = i + 13;c<i + 37;c++) {

                                        fd[id++] = b[c];

                                    }
                                    String s2 = new String(fd);
                                    //System.out.println(s2);


                                    byte[] fd1 = new byte[10];
                                    int id1 = 0;
                                    for (int c = i + 38;c<i+48;c++) {

                                        fd1[id1++] = b[c];

                                    }
                                    String s3 = new String(fd1,"gbk");
                                    //System.out.println(s3 + "11111111111");



                                    if (map.get(s2 + "-" + s3) == null) {
                                        map.put(s2 + "-" + s3,new Integer(1));
                                    }else{
                                        Integer te = map.get(s2 + "-" + s3) + 1;
                                        map.put(s2 + "-" + s3,te);
                                    }




                                }
                            }

                        }
                    }





                    Set<String> keys = map.keySet();

                    String t = "";
                    String[] strings = new String[330];
                    for (String key : keys) {
                        t = key.substring(key.length() - 2,key.length());

                        if (t.indexOf(",") != -1) {
                            t = t.substring(0,1);
                        }
                        Integer i = Integer.parseInt(t) * 10;
                        while (strings[i] != null){

                            i++;
                        }
                               strings[i] = key;
                    }

                    for (int ing = 0;ing < strings.length;ing ++) {
                        if (strings[ing] == null) {
                            continue;
                        }

                        System.out.println(strings[ing] + "读到" + map.get(strings[ing]) + "次");

                    }
                    map.clear();


                    end = index;
                }





            }
        }*/
    }
}
