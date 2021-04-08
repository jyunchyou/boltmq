package io.openmessaging.client.producer;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.exception.RegisterException;
import io.openmessaging.client.net.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by fbhw on 17-10-31.
 */
public class FactoryProducer {
    Logger logger = LoggerFactory.getLogger(FactoryProducer.class);

    public static NamingService naming = null;

    public static NettyClient nettyClient = new NettyClient();

    public static void registry(BProperties BProperties, final AbstractProducer abstractProducer) throws RegisterException, NacosException {

        //注册当前服务
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


        try {
            List<Instance> instances = FactoryProducer.naming.getAllInstances(ConstantClient.brokerServiceName,true);

            for (Instance instance : instances) {
                nettyClient.putChannel(instance,abstractProducer);
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }


        //定时获取更多连接
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(new Runnable() {
            @Override
            public void run() {

                try {
                    List<Instance> instances = FactoryProducer.naming.getAllInstances("brocker1",true);

                    for (Instance instance : instances) {
                        nettyClient.putChannel(instance,abstractProducer);
                    }
                } catch (NacosException e) {
                    e.printStackTrace();
                }


            }
        }, ConstantClient.SCHEDULE_UPDATE_CHANNEL_TIME, TimeUnit.SECONDS);






    }


    public static AbstractProducer createProducer() throws IOException {


        AbstractProducer mqProducer = new AbstractProducer();

        return mqProducer;



    }

}
