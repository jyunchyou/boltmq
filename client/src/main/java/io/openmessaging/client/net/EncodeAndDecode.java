package io.openmessaging.client.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.client.exception.OutOfBodyLengthException;
import io.openmessaging.client.exception.OutOfByteBufferException;
import io.openmessaging.client.producer.BrokerInfo;
import io.openmessaging.client.producer.Message;
import io.openmessaging.client.producer.Properties;
import io.openmessaging.client.producer.SendQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fbhw on 17-11-25.
 */

/**
 * 协议格式:
 * 使用定长编解码方式, 格式如下
 * /
 * 消息总字长
 * /RequestDto字长/内容{/字长/内容/}
 * /Message字长/内容{/字长/内容/}
 * /Properties字长/内容{/字长/内容/}
 * /
 * 具体内容如下
 *
 * message
 * {
 * private String topic = null;

 private String orderId = null;//顺序发送时定义组id

 private byte[] body = null;//消息内容 字长占位4
 }
 * properties{Map 属性} 内容字长占位2
 *
 * requestDto{
 *     private String id = null;//序号

 private String language = null;//发送语言

 private String version = null;//序列化版本

 private String serialModel = null;//序列化方式

 private int code;//注意是返回Broker结果　-1发送超时或失败;0未发送;1发送成功;

 private int delayTime = ConstantClient.DELAY_TIME;//规定允许的超时时间
 *
 * }
 */

public class EncodeAndDecode {

    Logger logger = LoggerFactory.getLogger(EncodeAndDecode.class);
    public ByteBuf encodeMessage(Message message,Properties properties, RequestDto requestDto) throws OutOfBodyLengthException, OutOfByteBufferException {



        //requestDto byte
        byte[] id = requestDto.getId().getBytes();
        byte[] language = requestDto.getLanguage().getBytes();
        byte[] version = requestDto.getVersion().getBytes();
        byte[] serialModel = requestDto.getSerialModel().getBytes();
        byte code   = (byte) requestDto.getCode();
        byte delayTime = (byte) requestDto.getDelayTime();

        //requestDto length
        byte idLen = (byte) id.length;
        byte languageLen = (byte) language.length;
        byte versionLen = (byte) version.length;
        byte serialModelLen = (byte) serialModel.length;
        byte codeLen = 1;
        byte delayTimeLen = 1;

        //properties byte
        int allLength = properties.getAllLength();



        //massage byte
        byte[] topic = message.getTopic().getBytes();
        byte[] orderId = message.getOrderId().getBytes();
        byte[] body = message.getBody();

        //message length
        byte topicLen = (byte) topic.length;
        byte orderIdLen = (byte) orderId.length;
        byte[] bodyLen = new byte[4];
        int len =  body.length;

        if (body.length > 127 * 127 * 127 * 127) {


            logger.error("消息内容长度超过限定长度");
            throw new OutOfBodyLengthException();

        }

        else if(len > 127 * 127 * 127){
            bodyLen[0] = (byte) (len / (127 * 127 * 127));
            bodyLen[1] = (byte) (len / (bodyLen[0] * 127 * 127));
            bodyLen[2] = (byte) (len / (bodyLen[0] * bodyLen[1] * 127));
            bodyLen[3] = (byte) (len / (bodyLen[0] * bodyLen[1] * bodyLen[2]));
        }
        else if(len > 127 * 127){
            bodyLen[0] = 0;
            bodyLen[1] = (byte) (len / (127 * 127));
            bodyLen[2] = (byte) (len / (bodyLen[1] * 127));
            bodyLen[3] = (byte) (len / (bodyLen[1] * bodyLen[2]));
        }else if(len > 127){
                bodyLen[0] = 0;
                bodyLen[1] = 0;
                bodyLen[2] = (byte) (len / 127);
                bodyLen[3] = (byte) (len / bodyLen[2]);
        }else {
            bodyLen[0] = 0;
            bodyLen[1] = 0;
            bodyLen[2] = 0;
            bodyLen[3] = (byte) len;


        }


        //开始putByteBuffer
        int byteBufferLen = 4 +
                idLen+languageLen+versionLen+serialModelLen+2+6+//20

                allLength+(properties.getSize()*(2+1))+//51

        topicLen+orderIdLen+body.length+(1+1+4);//35


        ByteBuf byteBuf = Unpooled.buffer(byteBufferLen);


        byte[] allLengthByte = new byte[4];
        if (byteBufferLen > 127 * 127 * 127 * 127) {


            logger.error("消息属性和请求传输对象总长度超过byteBuffer限定长度");
            throw new OutOfByteBufferException();

        }

        else if(byteBufferLen > 127 * 127 * 127){
            allLengthByte[0] = (byte) (byteBufferLen / (127 * 127 * 127));
            allLengthByte[1] = (byte) (byteBufferLen / (allLengthByte[0] * 127 * 127));
            allLengthByte[2] = (byte) (byteBufferLen / (allLengthByte[0] * allLengthByte[1] * 127));
            allLengthByte[3] = (byte) (byteBufferLen / (allLengthByte[0] * allLengthByte[1] * allLengthByte[2]));
        }
        else if(byteBufferLen > 127 * 127){
            allLengthByte[0] = 0;
            allLengthByte[1] = (byte) (byteBufferLen / (127 * 127));
            allLengthByte[2] = (byte) (byteBufferLen / (allLengthByte[1] * 127));
            allLengthByte[3] = (byte) (byteBufferLen / (allLengthByte[1] * allLengthByte[2]));
        }else if(byteBufferLen > 127){
            allLengthByte[0] = 0;
            allLengthByte[1] = 0;
            allLengthByte[2] = (byte) (byteBufferLen / 127);
            allLengthByte[3] = (byte) (byteBufferLen / allLengthByte[2]);
        }else {
            allLengthByte[0] = 0;
            allLengthByte[1] = 0;
            allLengthByte[2] = 0;
            allLengthByte[3] = (byte) byteBufferLen;


        }



        byteBuf.writeBytes(allLengthByte);


        byteBuf.writeBytes(new byte[]{topicLen});
        byteBuf.writeBytes(topic);
        byteBuf.writeBytes(new byte[]{orderIdLen});
        byteBuf.writeBytes(orderId);
        byteBuf.writeBytes(bodyLen);
        byteBuf.writeBytes(body);

        byteBuf.writeBytes(new byte[]{idLen});
        byteBuf.writeBytes(id);
        byteBuf.writeBytes(new byte[]{languageLen});
        byteBuf.writeBytes(language);
        byteBuf.writeBytes(new byte[]{versionLen});
        byteBuf.writeBytes(version);
        byteBuf.writeBytes(new byte[]{serialModelLen});
        byteBuf.writeBytes(serialModel);
        byteBuf.writeBytes(new byte[]{codeLen});
        byteBuf.writeBytes(new byte[]{code});
        byteBuf.writeBytes(new byte[]{delayTimeLen});
        byteBuf.writeBytes(new byte[]{delayTime});

        Set<Map.Entry> entrySet = properties.entrySet();
        Iterator iterator = entrySet.iterator();


        int testLen = 0;

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            byte[] valueByte = value.getBytes();
            byte[] keyByte = key.getBytes();
            byte keyLenByte = (byte) keyByte.length;

            byteBuf.writeBytes(new byte[]{keyLenByte});
            byteBuf.writeBytes(keyByte);

            int valueLen = valueByte.length;
            byte[] valueLenByte = new byte[2];
            if (valueLen > 127 * 127) {


                logger.error("消息属性和请求传输对象总长度超过byteBuffer限定长度");
                throw new OutOfByteBufferException();

            }

            else if(valueLen > 127){

                valueLenByte[0] = (byte) (valueLen/127);
                valueLenByte[1] = (byte) (valueLen/(valueLenByte[0]));

            }
            else {

                valueLenByte[0] = 0;
                valueLenByte[1] = (byte) valueLen;
            }
            byteBuf.writeBytes(valueLenByte);
            byteBuf.writeBytes(value.getBytes());
        }


        return byteBuf;
    }

    /**
     *nameServerRouteTable协议格式
     *
     *
     *  topicName长度 1字节
     *  topicName byte[]
        queueId长度 1字节
        queueId byte[]
        brokerInfo {
        ip长度 1字节
        ip byte[]
        port转String长度 1字节
        port 转为byte[]

     }

     *
     *
     */
    public List<SendQueue> decodeNameServerRoute(ByteBuf byteBuf, List<SendQueue> list){

        list.clear();
        while (byteBuf.isReadable()) {
            SendQueue sendQueue = new SendQueue();
            BrokerInfo brokerInfo = new BrokerInfo();

            //topicName
            byte[] topicNameLength = new byte[1];
            byteBuf.readBytes(topicNameLength);
            int topicNameLengthInt = topicNameLength[0];

            byte[] topicNameByte = new byte[topicNameLengthInt];
            byteBuf.readBytes(topicNameByte);
            String topicName = new String(topicNameByte);

            sendQueue.setTopicName(topicName);
            //queueId
            byte[] queueIdLength = new byte[1];
            byteBuf.readBytes(queueIdLength);
            int queueIdLengthInt = queueIdLength[0];

            byte[] queueIdByte = new byte[queueIdLengthInt];
            byteBuf.readBytes(queueIdByte);
            String queueId = new String(queueIdByte);



            sendQueue.setQueueId(queueId);

            //ip
            byte[] ipLength = new byte[1];
            byteBuf.readBytes(ipLength);
            int ipLengthInt = ipLength[0];

            byte[] ipByte = new byte[ipLengthInt];
            byteBuf.readBytes(ipByte);
            String ip = new String(ipByte);
            brokerInfo.setIp(ip);
            //port
            byte[] portLength = new byte[1];
            byteBuf.readBytes(portLength);
            int portLengthInt = portLength[0];
            byte[] portByte = new byte[portLengthInt];
            byteBuf.readBytes(portByte);

            String port = new String(portByte);

            int portInteger = Integer.parseInt(port);



            brokerInfo.setPort(portInteger);



            sendQueue.setBrokerInfo(brokerInfo);
            System.out.println(topicName);
            System.out.println(portInteger);
            System.out.println(queueId);
            System.out.println(ip);

            list.add(sendQueue);
        }
        return list;
    }
}
