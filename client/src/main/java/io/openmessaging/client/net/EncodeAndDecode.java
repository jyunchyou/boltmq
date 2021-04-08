package io.openmessaging.client.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.openmessaging.client.compress.CompressOfDeflater;
import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.exception.OutOfBodyLengthException;
import io.openmessaging.client.exception.OutOfByteBufferException;
import io.openmessaging.client.producer.AbstractProducer;
import io.openmessaging.client.producer.BrokerInfo;
import io.openmessaging.client.producer.BProperties;
import io.openmessaging.client.table.SendQueue;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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


    long start = 0;
    long end = 0;
    long sum = 0;


    public byte booleansConvertByte(boolean isDelay,boolean isSeq,boolean isCallBack,boolean isOneWay,boolean isSyn){
        byte b = 0;
        if (isDelay) {

            b = (byte) ((byte) (b | 1) << 1);

        }else{
            b = (byte) (b << 1);
        }
        if (isSeq) {

            b = (byte) ((byte) (b | 1) << 1);

        }else{
            b = (byte) (b << 1);
        }
        if (isCallBack) {

            b = (byte) ((byte) (b | 1) << 1);

        }else{
            b = (byte) (b << 1);
        }
        if (isOneWay) {

            b = (byte) ((byte) (b | 1) << 1);

        }else{
            b = (byte) (b << 1);
        }
        if (isSyn) {

            b = (byte) ((byte) (b | 1));

        }else{

        }
        return b;

    }

    enum DecodeResult {
        NEED_MORE_INPUT, DISCARD
    }
    public Object decode2(ByteBuf byteBuf,AbstractProducer abstractProducer,Channel channel){

        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();

        if (readable < 8) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        long sendOffset = byteBuf.readLong();



        Map map = abstractProducer.getSendConfirmMap().get(channel);

        map.remove(sendOffset);

        System.out.print("发送中...");
        if (start == 0) {
            start = System.currentTimeMillis();
        }
        sum++;
        System.out.println(sum);
        if (sum == 10000) {

            DecimalFormat df = new DecimalFormat("#.00");

            end = System.currentTimeMillis();
            System.out.println(end - start);
            float f = (end - start) / 1000;
            System.out.println(f);
            float f2 = 10000;
            System.out.println("tps:" + df.format(10000/f) + "每秒");
        }


        CallBackMap.semaphore.release();
        return null;

    }



    public ByteBuf deCode(ByteBuf byteBuf,AbstractProducer abstractProducer,Channel channel) throws Exception {


        try {
        do {

            //int savedReaderIndex = byteBuf.readerIndex();
            Object msg = null;

            msg = decode2(byteBuf,abstractProducer,channel);


            if (msg == DecodeResult.NEED_MORE_INPUT) {
                //   byteBuf.readerIndex(savedReaderIndex);
                break;
            }



        } while (byteBuf.isReadable());
    } finally {
        if (byteBuf.isReadable()) {
            byteBuf.discardReadBytes();//将readIndex和writeIndex之间数据拷贝到index0处，index置于数据长度位置

            return byteBuf;
        }
    }


        return null;
}




    public ByteBuf encodeBaseMessage(BaseMessage baseMessage, AbstractProducer abstractProducer, Channel channel) throws InterruptedException {

        byte[] topicBytes = baseMessage.getTopic().getBytes();
        byte[] valueBytes = baseMessage.getValue();
        int allLen = baseMessage.getConstantLen() + topicBytes.length + valueBytes.length;
        baseMessage.setTotalSize(allLen - 4);
        baseMessage.setTopicLength((byte) topicBytes.length);
        baseMessage.setValueLength(valueBytes.length);
        ByteBuf byteBuf = Unpooled.buffer(allLen);
        byteBuf.writeInt(baseMessage.getTotalSize());//4
        //1
        int byteType = booleansConvertByte(baseMessage.isDelay(),baseMessage.isSeq(),baseMessage.isCallBack(),baseMessage.isOneWay(),baseMessage.isSyn());

        byteBuf.writeByte(byteType);
        byteBuf.writeLong(baseMessage.getSendTimeStamp());//8
        byteBuf.writeLong(baseMessage.getConserveTime());//8
        byteBuf.writeByte(baseMessage.getDelayTime());//1
        byteBuf.writeBytes(baseMessage.getDelayTimeUnit().getBytes());//1


        byteBuf.writeByte(baseMessage.getTopicLength());//1
        byteBuf.writeBytes(topicBytes);
        byteBuf.writeInt(baseMessage.getValueLength());//4
        byteBuf.writeBytes(valueBytes);


        byteBuf.writeLong(0L);

        Long offset = abstractProducer.getSendOffsetMap().get(channel);

        if (offset == null) {
            offset = 0L;
        }
        abstractProducer.getSendOffsetMap().put(channel,offset + 1);

        byteBuf.writeLong(offset);



            synchronized (abstractProducer) {

                Map byteBufMap = abstractProducer.getSendConfirmMap().get(channel);
                if (byteBufMap == null) {
                    byteBufMap = new ConcurrentHashMap();
                    abstractProducer.getSendConfirmMap().put(channel,byteBufMap);
                }


                    //设置最新待确认消息，消息下标加一
                byteBufMap.put(offset + 1,baseMessage);




            }


        return byteBuf;

    }


    //超时重发
    public ByteBuf encodeBaseMessage2(BaseMessage baseMessage,long offset) throws InterruptedException {

        byte[] topicBytes = baseMessage.getTopic().getBytes();
        byte[] valueBytes = baseMessage.getValue();
        int allLen = baseMessage.getConstantLen() + topicBytes.length + valueBytes.length;
        baseMessage.setTotalSize(allLen - 4);
        baseMessage.setTopicLength((byte) topicBytes.length);
        baseMessage.setValueLength(valueBytes.length);
        ByteBuf byteBuf = Unpooled.buffer(allLen);
        int markStart = byteBuf.writerIndex();
        byteBuf.writeInt(baseMessage.getTotalSize());//4
        //1
        int byteType = booleansConvertByte(baseMessage.isDelay(),baseMessage.isSeq(),baseMessage.isCallBack(),baseMessage.isOneWay(),baseMessage.isSyn());

        byteBuf.writeByte(byteType);
        byteBuf.writeLong(baseMessage.getSendTimeStamp());//8
        byteBuf.writeLong(baseMessage.getConserveTime());//8
        byteBuf.writeByte(baseMessage.getDelayTime());//1
        byteBuf.writeBytes(baseMessage.getDelayTimeUnit().getBytes());//1


        byteBuf.writeByte(baseMessage.getTopicLength());//1
        byteBuf.writeBytes(topicBytes);
        byteBuf.writeInt(baseMessage.getValueLength());//4
        byteBuf.writeBytes(valueBytes);


        byteBuf.writeLong(0L);

        byteBuf.writeLong(offset);




        return byteBuf;

    }


    public ByteBuf encodeMessage(io.openmessaging.client.producer.Message message, BProperties BProperties, BaseMessage requestDto) throws OutOfBodyLengthException, OutOfByteBufferException {



        //requestDto byte

        byte delayTime = (byte) requestDto.getDelayTime();

        //requestDto length

        byte codeLen = 1;
        byte delayTimeLen = 1;

        //properties byte
        int allLength = BProperties.getAllLength();



        //massage byte
        byte[] topic = message.getTopic().getBytes();
        byte[] orderId = message.getOrderId().getBytes();
        byte[] body = message.getBody();
        //超过压缩
        if (body.length >= ConstantClient.BODY_OVER_HOW_MUTH_COMRESS) {
            try {
                body = CompressOfDeflater.compress(body);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        //message length
        byte topicLen = (byte) topic.length;
        byte orderIdLen = (byte) orderId.length;
        byte[] bodyLen = new byte[4];
        int len =  body.length;




        if (len > 127 * 127 * 127 * 127) {


        }

        else if(len > 127 * 127 * 127){
            bodyLen[0] = (byte) (len / (127 * 127 * 127));
            bodyLen[1] = (byte) ((len - (bodyLen[0] * (127 * 127 * 127))) / (127 * 127));
            bodyLen[2] = (byte) ((len - (bodyLen[0] * (127 * 127 * 127)) - (bodyLen[1] * (127 * 127))) / 127);
            bodyLen[3] = (byte) (len % 127);
        }
        else if(len > 127 * 127){
            bodyLen[0] = 0;
            bodyLen[1] = (byte) (len / (127 * 127));
            bodyLen[2] = (byte) ((len - (bodyLen[1] * (127 * 127))) / 127);
            bodyLen[3] = (byte) (len % 127);
        }else if(len > 127){
            bodyLen[0] = 0;
            bodyLen[1] = 0;
            bodyLen[2] = (byte) (len / 127);
            bodyLen[3] = (byte) (len % 127);
        }else {
            bodyLen[0] = 0;
            bodyLen[1] = 0;
            bodyLen[2] = 0;
            bodyLen[3] = (byte) len;


        }


        //开始putByteBuffer
        int byteBufferLen =
                //queueIdByteLen+idLen+languageLen+versionLen+serialModelLen+2+7+//20

                allLength+(BProperties.getSize()*(2+1))+//51

        topicLen+orderIdLen+body.length+(1+1+4+8/*sendTime*/);//35

        ByteBuf byteBuf = Unpooled.buffer(byteBufferLen);

        byte[] allLengthByte = new byte[4];
        if (byteBufferLen > 127 * 127 * 127 * 127) {


        }

        else if(byteBufferLen > 127 * 127 * 127){
            allLengthByte[0] = (byte) (byteBufferLen / (127 * 127 * 127));
            allLengthByte[1] = (byte) ((byteBufferLen - (allLengthByte[0] * (127 * 127 * 127))) / (127 * 127));
            allLengthByte[2] = (byte) ((byteBufferLen - (allLengthByte[0] * (127 * 127 * 127)) - (allLengthByte[1] * (127 * 127))) / 127);
            allLengthByte[3] = (byte) (byteBufferLen % 127);
        }
        else if(byteBufferLen > 127 * 127){
            allLengthByte[0] = 0;
            allLengthByte[1] = (byte) (byteBufferLen / (127 * 127));
            allLengthByte[2] = (byte) ((byteBufferLen - (allLengthByte[1] * (127 * 127))) / 127);
            allLengthByte[3] = (byte) (byteBufferLen % 127);
        }else if(byteBufferLen > 127){
            allLengthByte[0] = 0;
            allLengthByte[1] = 0;
            allLengthByte[2] = (byte) (byteBufferLen / 127);
            allLengthByte[3] = (byte) (byteBufferLen % 127);
        }else {
            allLengthByte[0] = 0;
            allLengthByte[1] = 0;
            allLengthByte[2] = 0;
            allLengthByte[3] = (byte) byteBufferLen;


        }


        byteBuf.writeBytes(allLengthByte);

        byteBuf.writeBytes(new byte[]{topicLen});
        byteBuf.writeBytes(topic);
        /*byteBuf.writeBytes(new byte[]{queueIdByteLen});
        byteBuf.writeBytes(queueId);
        *///时间戳
        long nowTime = System.currentTimeMillis();
        byteBuf.writeLong(nowTime);
        byteBuf.writeBytes(bodyLen);
        byteBuf.writeBytes(body);


        byteBuf.writeBytes(new byte[]{orderIdLen});
        byteBuf.writeBytes(orderId);
        /*byteBuf.writeBytes(new byte[]{idLen});
        byteBuf.writeBytes(id);
        byteBuf.writeBytes(new byte[]{languageLen});
        byteBuf.writeBytes(language);
        byteBuf.writeBytes(new byte[]{versionLen});
        byteBuf.writeBytes(version);
        byteBuf.writeBytes(new byte[]{serialModelLen});
        byteBuf.writeBytes(serialModel);
        byteBuf.writeBytes(new byte[]{codeLen});
        byteBuf.writeBytes(new byte[]{code});
        */byteBuf.writeBytes(new byte[]{delayTimeLen});
        byteBuf.writeBytes(new byte[]{delayTime});

        Set<Map.Entry> entrySet = BProperties.entrySet();
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
    public List<SendQueue> decodeNameServerRoute(ByteBuf byteBuf, List<SendQueue> list) {




        list.clear();
        while (byteBuf.isReadable()) {



            byte brokerNumByte = byteBuf.readByte();
            int brokerNum = brokerNumByte;

            for (int checkNum = 0;checkNum < brokerNum;checkNum++) {
                int ipLen = byteBuf.readByte();
                byte[] ipByte = new byte[ipLen];
                byteBuf.readBytes(ipByte);
                String ip = new String(ipByte);

                int portLen = byteBuf.readByte();
                byte[] portByte = new byte[portLen];
                byteBuf.readBytes(portByte);
                String port = new String(portByte);

                byte listSizeByte = byteBuf.readByte();
                int listSize = listSizeByte;






                for (int indexNum = 0;indexNum < listSize;indexNum ++) {

                    byte topicByteLen = byteBuf.readByte();
                    int topicLen = topicByteLen;
                    byte[] topicByte = new byte[topicLen];
                    byteBuf.readBytes(topicByte);

                    SendQueue sendQueue = new SendQueue();
                        BrokerInfo brokerInfo = new BrokerInfo();
                        brokerInfo.setIp(ip);
                        brokerInfo.setProducerPort(Integer.parseInt(port));

                        sendQueue.setBrokerInfo(brokerInfo);
                        sendQueue.setTopicName(new String(topicByte));
                        sendQueue.setQueueId(new String(topicByte));
                        list.add(sendQueue);



                }


            }





        }
        return list;

    }

    /**
     * int到byte[] 由高位到低位
     * @param i 需要转换为byte数组的整行值。
     * @return byte数组
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    /**
     * byte[]转int
     * @param bytes 需要转换成int的数组
     * @return int值
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    public static void main(String[] args) {
        boolean t = true;

        byte b = 0;
        int i = b ^ 1;
          }
}
