package io.openmessaging.client.net;

import io.openmessaging.client.exception.OutOfBodyLengthException;
import io.openmessaging.client.exception.OutOfByteBufferException;
import io.openmessaging.client.producer.Message;
import io.openmessaging.client.producer.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.util.Iterator;
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
    public ByteBuffer encode(Message message,Properties properties, RequestDto requestDto) throws OutOfBodyLengthException, OutOfByteBufferException {



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


        ByteBuffer byteBuffer = ByteBuffer.allocate(byteBufferLen);

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



        byteBuffer.put(allLengthByte);


        byteBuffer.put(idLen);
        byteBuffer.put(id);
        byteBuffer.put(languageLen);
        byteBuffer.put(language);
        byteBuffer.put(versionLen);
        byteBuffer.put(version);
        byteBuffer.put(serialModelLen);
        byteBuffer.put(serialModel);
        byteBuffer.put(codeLen);
        byteBuffer.put(code);
        byteBuffer.put(delayTimeLen);
        byteBuffer.put(delayTime);
        byteBuffer.put(topicLen);
        byteBuffer.put(topic);
        byteBuffer.put(orderIdLen);
        byteBuffer.put(orderId);
        byteBuffer.put(bodyLen);
        byteBuffer.put(body);
        Set<Map.Entry> entrySet = properties.entrySet();
        Iterator iterator = entrySet.iterator();


        int testLen = 0;

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            byte keyLenByte = (byte) key.length();
            byteBuffer.put(keyLenByte);
            byteBuffer.put(key.getBytes());

            int valueLen = value.length();
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
            byteBuffer.put(valueLenByte);
            byteBuffer.put(value.getBytes());
        }


        return byteBuffer;
    }
}
