package io.openmessaging.client;


import com.alibaba.nacos.api.exception.NacosException;
import io.netty.channel.Channel;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.exception.RegisterException;
import io.openmessaging.client.net.BaseMessage;
import io.openmessaging.client.net.NettyClient;
import io.openmessaging.client.process.ProcessDelayAck;
import io.openmessaging.client.producer.AbstractProducer;
import io.openmessaging.client.producer.BProperties;
import io.openmessaging.client.producer.FactoryProducer;
import io.openmessaging.client.producer.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fbhw on 17-11-10.
 *
 *
 *
 <dependency>
 <groupId>a</groupId>
 <artifactId>b</artifactId>
 <version>*</version>
 <systemPath>/home/fbhw/下载/dysms_java/java/api_demo/alicom-dysms-api/libs/aliyun-java-sdk-core-3.3.1.jar</systemPath>
 <scope>system</scope>
 </dependency>
 <dependency>
 <groupId>c</groupId>
 <artifactId>d</artifactId>
 <version>*</version>
 <systemPath>/home/fbhw/下载/dysms_java/java/api_demo/alicom-dysms-api/libs/aliyun-java-sdk-dysmsapi-1.0.0.jar</systemPath>

 <scope>system</scope>
 </dependency>
 */
public class Test {
    public static void main(String[] args) throws NacosException, RegisterException, InterruptedException, IOException/* throws ClientException*/ {
        BProperties bProperties = new BProperties();
        bProperties.setServiceName("client1");
        bProperties.setGroup("clients");
        bProperties.setServer("localhost:8848");
        bProperties.setIp("127.0.0.1");
        bProperties.setPort(ConstantClient.CLIENT_PORT);

        ProcessDelayAck processDelayAck = new ProcessDelayAck();
        new Thread(processDelayAck).start();
        AbstractProducer abstractProducer = FactoryProducer.createProducer();

        FactoryProducer.registry(bProperties,abstractProducer);
        Object[] stri =  NettyClient.channels.keySet().toArray();


        for (Object s :stri) {

            String s1 = (String) s;
            Channel channel = NettyClient.channels.get(s);
            processDelayAck.delayAckMap.put(channel,abstractProducer.getSendConfirmMap());


        }



            for (int index = 0;index < 10001;index++) {
                System.out.println(stri.length);
            for (Object s :stri) {
                String s1 = (String) s;
                Channel channel = NettyClient.channels.get(s);
                BaseMessage baseMessage = new BaseMessage();
                baseMessage.setTopic("hhjjqq");

                byte[] b = new byte[2000];
                baseMessage.setValue(b);
                abstractProducer.send(baseMessage, null, true, false, false, false, false, 0, (byte) 0, "h", channel);
            }
        }
       /* channel = NettyClient.channels.get(NettyClient.channels.keySet().toArray()[1]);
        baseMessage.setTopic("hujunqiu");
        baseMessage.setValue("1234567891");
        abstractProducer.send(baseMessage,null,true,false,false,false,false,0, (byte) 0,"h",channel);
*/
   /*     Logger logger = LoggerFactory.getLogger(Test.class);
        logger.trace("======trace") ;
        logger.info("fads");
        logger.warn("======warn");
        logger.error("=====assembly=error");
        //设置超时时间-可自行调整
        //10m发送超时
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
        //替换成你的AK
        final String accessKeyId = "LTAInulQxgUtA2DU";//你的accessKeyId,参考本文档步骤2
        final String accessKeySecret = "YytdErG7fPeTQF6k8oPwYgPiZkG8SO";//你的accessKeySecret，参考本文档步骤2
        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式


        request.setPhoneNumbers("13666218774");
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("胡俊秋");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_114065291");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam("{\"code\":\"9527\"}");
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK") ) {
            //请求成功
            logger.info(sendSmsResponse.getCode());
            logger.info("请求成功");
        }else {
            logger.info(sendSmsResponse.getCode());
            logger.info("请求失败");
        }
    }
*/

    }
}