package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.broker.BrokerInfo;
import io.openmessaging.processor.ProcessorIn;
import io.openmessaging.processor.ProcessorOut;
import io.openmessaging.table.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;

/**
 * Created by fbhw on 17-12-5.
 */
public class EncodeAndDecode {


    private boolean discard = false;

    private int unReadNum = 0;

    private byte[] cacheBytes;

    private byte[] cachePullBytes;

    private int index = 0;

    private Lock lock = null;

    public static final byte[] magicByte = {0x16,0x52,0xB,0x23};

    public EncodeAndDecode(Lock lock){

        this.lock = lock;

    }
    public EncodeAndDecode(){

    }



    /*
    private final int constantLen = 27;

    private int totalSize;//消息总长 4字节!

    private boolean isDelay;//是否延时!

    private boolean isSeq;//是否顺序!

    private boolean isCallBack;//是否返回!

    private boolean isOneWay;//是否单向！

    private boolean isSyn;//是否同步！ 判断位共一字节

    private long sendTimeStamp;//消息发送时的事件戳 8字节

    private long conserveTime;//消息保存时间,单位ms 8字节

    private byte delayTime;//延时时间,支持1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h，不等向短靠近 1字节

    private String delayTimeUnit;//延时消息单位,s,m,h 1字节

    //消息体=============================================
    private int batchNum = 1;

    private byte topicLength;//topic长度 1字节！

    private String topic;！

    private int valueLength;//value长度 4字节！

    private String value;！

*/



    public static Object decodeConsumeBackge2(ByteBuf byteBuf,ProcessorOut processorOut,ChannelHandlerContext cxt,Map ackMap) throws IOException, ExecutionException, InterruptedException {

        byte[] b = new byte[byteBuf.readableBytes()];
        int i = byteBuf.readerIndex();
        byteBuf.readBytes(b);

        byteBuf.readerIndex(i);
        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();

        if (readable < 4) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        int topicLen = byteBuf.readInt();

        if (byteBuf.readableBytes() < topicLen + 22) {
            byteBuf.readerIndex(savedReaderIndex);
            return DecodeResult.NEED_MORE_INPUT;
        }
        byte[] topicB = new byte[topicLen];
        byteBuf.readBytes(topicB);
        String topic = new String(topicB);

        boolean isAck = byteBuf.readBoolean();
        long messageId = byteBuf.readLong();

        if (isAck) {
            //将ackMap置为已执行
            ackMap.remove(messageId,1);
            byteBuf.skipBytes(13);
            return null;
        }
        boolean consumeModel = byteBuf.readBoolean();
        long offset = 0;

        if (consumeModel) {
            //集群消費
            offset = loadConsumeOffset(topic);
            byteBuf.skipBytes(8);

        }else {
            offset = byteBuf.readLong();


        }
        int messageNum = byteBuf.readInt();
        processorOut.outIndexShardingPage(topic,offset,messageNum,cxt,consumeModel,ackMap);
        return null;
    }

    public static long loadConsumeOffset(String topic) throws IOException {//获取消费下标，在集群消费模式时调用
        MappedByteBuffer mappedByteBuffer = (MappedByteBuffer) NettyServer.ConsumeIndexMap.get(topic);
        if (mappedByteBuffer == null) {
            File file = new File(ConstantBroker.CONSUME_INDEX_FILE_ADDRESS);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            FileChannel channel = randomAccessFile.getChannel();
            mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 8);
            NettyServer.ConsumeIndexMap.put(topic,mappedByteBuffer);
            mappedByteBuffer.putLong(1L);
        }
        mappedByteBuffer.position(0);

        long offset = mappedByteBuffer.getLong();

        mappedByteBuffer.position(0);

        mappedByteBuffer.putLong(offset + 1);

        return offset;
    }

        public static ByteBuf decodeConsumeBackge(ByteBuf byteBuf, ProcessorOut processorOut, ChannelHandlerContext cxt,Map ackMap) throws IOException, ExecutionException, InterruptedException {


            try {
                do {
                    //int savedReaderIndex = byteBuf.readerIndex();
                    Object msg = null;
                    try {
                        msg = decodeConsumeBackge2(byteBuf,processorOut,cxt,ackMap);
                    } catch (Exception e) {
                        throw e;
                    }
                    if (msg == DecodeResult.NEED_MORE_INPUT) {
                      //  byteBuf.readerIndex(savedReaderIndex);
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

    enum DecodeResult {
        NEED_MORE_INPUT, DISCARD
    }

    public static boolean compereByteArray(byte[] b1, byte[] b2) {

//1：
        if(b1.length == 0 || b2.length == 0 ){
            return false;
        }

//2：
        if (b1.length != b2.length) {
            return false;
        }

//3：
        boolean isEqual = true;

        for (int i = 0; i < b1.length && i < b2.length; i++) {
            if (b1[i] != b2[i]) {
                isEqual = false;
                break;
            }
        }
        return isEqual;
    }

    public Object decode2(ByteBuf byteBuf, Channel channel, ProcessorIn processorIn){

        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();

        if (readable < 4) {//记录总长数的字节数
            byteBuf.readerIndex(savedReaderIndex);
            return DecodeResult.NEED_MORE_INPUT;
        }
        int test = byteBuf.readerIndex();
        int totalLen = byteBuf.readInt();

        readable = byteBuf.readableBytes();

/*
        if (totalLen == 0) {
            byteBuf.readerIndex(test);
            byte[] b = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(b);
             }
*/
        if (readable < totalLen) {

            byteBuf.readerIndex(savedReaderIndex);
            return DecodeResult.NEED_MORE_INPUT;
        }

        int testM = byteBuf.readerIndex();

        int messageIndex = byteBuf.readerIndex() - 4;

        byteBuf.readerIndex(messageIndex);

        byte[] messageAllByte = new byte[totalLen + 4];

        byteBuf.readBytes(messageAllByte);

        byteBuf.readerIndex(messageIndex + 4);

        //消息类型
        byte type = byteBuf.readByte();


        int i = type;

        byte i1 = (byte) (type>>4);
        byte i2 = (byte) (type>>3);
        byte i3 = (byte) (type>>2);
        byte i4 = (byte) (type>>1);
        boolean isDelay = (i1&1)==1?true:false;//是否延时

        boolean isSeq = (i2&1)==1?true:false;//是否顺序!

        boolean isCallBack = (i3&1)==1?true:false;//是否返回!

        boolean isOneWay = (i4&1)==1?true:false;//是否单向！

        boolean isSyn = (type&1)==1?true:false;//是否同步！ 判断位共一字节


        byteBuf.skipBytes(18);//跳过一定字节数读取topic


        byte topicLenByte = byteBuf.readByte();

        int topicLen = topicLenByte;
        byte[] topicByte = new byte[topicLen];
        byteBuf.readBytes(topicByte);

        int testMark = byteBuf.readerIndex();
        String topic = new String(topicByte);


        byteBuf.skipBytes(totalLen - 20 - topicLen - 8);

        long confirmIndex = byteBuf.readLong();

        ByteBuf confirmBuf = Unpooled.buffer(8);

        confirmBuf.writeLong(confirmIndex);
        channel.writeAndFlush(confirmBuf);
        processorIn.inputMessage(messageAllByte,topic);

        return null;

    }

    public ByteBuf deCode(ByteBuf byteBuf,Channel channel,ProcessorIn processorIn){



       /* byte[] by = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(by);
        */ try {
            do {
                //int savedReaderIndex = byteBuf.readerIndex();
                Object msg = null;
                try {
                    msg = decode2(byteBuf,channel,processorIn);
                } catch (Exception e) {
                    throw e;
                }
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

    //包括粘包处理
        public List decode(ByteBuf byteBuf){




            List list = new ArrayList();

            while (byteBuf.readableBytes() >= 4) {

                if (cacheBytes != null) {

                    byte[] arrayBuffer = new byte[byteBuf.readableBytes()];
                    byteBuf.readBytes(arrayBuffer);
                    int newLen = cacheBytes.length + arrayBuffer.length;
                    byteBuf = Unpooled.buffer(newLen);
                    byteBuf.writeBytes(cacheBytes);
                    byteBuf.writeBytes(arrayBuffer);
                }


                //设置resetIndex标记
                byteBuf.markReaderIndex();
                byte[] allLength = new byte[4];
                byteBuf.readBytes(allLength);

                int allLenInt = 0;
                if (allLength[0] != 0 && allLength[1] != 0 && allLength[2] != 0 && allLength[3] != 0) {
                    allLenInt = allLength[0] * 127 * 127 * 127 + allLength[1] * 127 * 127 +  allLength[2] * 127 + allLength[3];


                } else if (allLength[1] != 0 && allLength[2] != 0 && allLength[3] != 0) {

                    allLenInt = allLength[1] * 127 * 127 +  allLength[2] * 127 + allLength[3];

                } else if (allLength[2] != 0 && allLength[3] != 0) {

                    allLenInt = allLength[2] * 127 + allLength[3];

                } else {
                    allLenInt = allLength[3];
                }








           /* if (allLenInt != 68) {

                    Map map = new HashMap(1);

                    String t = "被丢弃";

                    String q = "discard";

                    map.put(t,q);

                    setDiscard(false);

                    return map;



            }*/


                int remain = byteBuf.readableBytes();


                if (allLenInt == remain) {






                    byte topicByteLen = byteBuf.readByte();

                    int topicIntLen = topicByteLen;

                    byte[] topicByte = new byte[topicIntLen];

                    byteBuf.readBytes(topicByte);

                    String topic = new String(topicByte);


                    byte[] queueIdByteLen = new byte[1];

                    byteBuf.readBytes(queueIdByteLen);

                    int queueIdIntLen = queueIdByteLen[0];

                    byte[] queueIdByte = new byte[queueIdIntLen];

                    byteBuf.readBytes(queueIdByte);

                    String queueId = new String(queueIdByte);

                    long sendTime = byteBuf.readLong();

                    byteBuf.resetReaderIndex();


                    byte[] data = new byte[allLenInt + 4];

                    byteBuf.readBytes(data);

                    Map map = new HashMap(1);

                    map.put("topic", topic);
                    map.put("queueId", queueId);
                    map.put("sendTime",sendTime);
                    map.put("data", data);

                    list.add(map);

                    cacheBytes = null;
                    return list;

                } else if (allLenInt < remain) {






                    int topicIntLen = byteBuf.readByte();

                    byte[] topicByte = new byte[topicIntLen];

                    byteBuf.readBytes(topicByte);

                    String topic = new String(topicByte);

                    byte[] queueIdByteLen = new byte[1];

                    byteBuf.readBytes(queueIdByteLen);

                    int queueIdIntLen = queueIdByteLen[0];

                    byte[] queueIdByte = new byte[queueIdIntLen];

                    byteBuf.readBytes(queueIdByte);


                    String queueId = new String(queueIdByte);

                    long sendTime = byteBuf.readLong();

                    byteBuf.resetReaderIndex();
                    byte[] data = new byte[allLenInt + 4];
                    byteBuf.readBytes(data);


                    /*cacheBytes = new byte[byteBuf.readableBytes()];*/


                   /* byteBuf.readBytes(cacheBytes);*/

                    Map map = new HashMap(1);

                    map.put("topic", topic);
                    map.put("queueId", queueId);
                    map.put("sendTime",sendTime);
                    map.put("data", data);


                    cacheBytes = null;
                    list.add(map);



               /* ip长度 1字节
                ip byte[]
                port转String长度 1字节
                port 转为byte[]*/


                } else {

                    if (cacheBytes != null) {

                        ByteBuf b = Unpooled.buffer(remain + 4 + cacheBytes.length);
                        b.writeBytes(cacheBytes);

                        byte[] data = new byte[remain + 4];
                        byteBuf.resetReaderIndex();
                        byteBuf.readBytes(data);
                        b.writeBytes(data);
                        cacheBytes = new byte[remain + 4 + cacheBytes.length];
                        b.readBytes(cacheBytes);


                    }
                    cacheBytes = new byte[remain + 4];
                    byteBuf.resetReaderIndex();
                    byteBuf.readBytes(cacheBytes);

                    return list;


                }


            }

            if (cacheBytes != null) {

                int remain = byteBuf.readableBytes();
                ByteBuf b = Unpooled.buffer(remain + cacheBytes.length);
                b.writeBytes(cacheBytes);

                byte[] data = new byte[remain];

                byteBuf.readBytes(data);
                b.writeBytes(data);
                cacheBytes = new byte[remain + cacheBytes.length];
                b.readBytes(cacheBytes);
                return list;

            }

            cacheBytes = new byte[byteBuf.readableBytes()];

            byteBuf.readBytes(cacheBytes);

            return list;
        }


        public ByteBuf encodeSendMessageBack(){
            byte[] sendResultBytes = "1".getBytes();
            ByteBuf byteBuf = Unpooled.buffer(sendResultBytes.length);

            byteBuf.writeBytes(sendResultBytes);

            return byteBuf;
        }



    public ByteBuf encodeBrokerInfo(BrokerInfo brokerInfo,ByteBuf byteBuf){
        String ip = brokerInfo.getIp();
        String producerPort = brokerInfo.getProducerPort() + "";
        String nameServerPort = brokerInfo.getNameServerPort() + "";
        String consumerPort = brokerInfo.getConsumerPort() + "";
        byte[] ipByte = ip.getBytes();

        int ipIntLen = ipByte.length;

        byte[] ipByteLen = new byte[1];

        ipByteLen[0] = (byte) ipIntLen;


        byte[] producerPortByte = producerPort.getBytes();
        int producerPortIntLen = producerPortByte.length;
        byte[] producerPortByteLen = new byte[1];
        producerPortByteLen[0] = (byte) producerPortIntLen;

        byte[] nameServerPortByte = nameServerPort.getBytes();
        int nameServerPortIntLen = nameServerPortByte.length;
        byte[] nameServerPortByteLen = new byte[1];
        nameServerPortByteLen[0] = (byte) nameServerPortIntLen;

        byte[] consumerPortByte = consumerPort.getBytes();
        int consumerPortIntLen = consumerPortByte.length;
        byte[] consumerPortByteLen = new byte[1];
        consumerPortByteLen[0] = (byte) consumerPortIntLen;



        byteBuf.writeBytes(ipByteLen);
        byteBuf.writeBytes(ipByte);
        byteBuf.writeBytes(producerPortByteLen);
        byteBuf.writeBytes(producerPortByte);

        byteBuf.writeBytes(nameServerPortByteLen);
        byteBuf.writeBytes(nameServerPortByte);

        byteBuf.writeBytes(consumerPortByteLen);
        byteBuf.writeBytes(consumerPortByte);


        return byteBuf;
    }
    /*ip,port,queueId,topic,offset,len*/
    public  ByteBuf  encodeToNameServer(BrokerInfo brokerInfo) {
        ByteBuf byteBuf = Unpooled.buffer(ConstantBroker.BUFFER_ROUTE_SIZE);
        byteBuf.markWriterIndex();
        byteBuf.writeBytes(new byte[4]);

        if (IndexFileQueueMap.indexQueueMap.size() == 0) {
            byteBuf = encodeBrokerInfo(brokerInfo,byteBuf);
            int byteBufferLen = byteBuf.readableBytes() - 4;
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
            int index = byteBuf.writerIndex();
            byteBuf.resetWriterIndex();
            byteBuf.writeBytes(allLengthByte);
            byteBuf.writerIndex(index);
            return byteBuf;
        }
        String ip = brokerInfo.getIp();
        String producerPort = brokerInfo.getProducerPort() + "";
        String nameServerPort = brokerInfo.getNameServerPort() + "";
        String consumerPort = brokerInfo.getConsumerPort() + "";
        byte[] ipByte = ip.getBytes();
        int ipIntLen = ipByte.length;
        byte[] ipByteLen = new byte[1];
        ipByteLen[0] = (byte) ipIntLen;
        byte[] producerPortByte = producerPort.getBytes();
        int producerPortIntLen = producerPortByte.length;
        byte[] producerPortByteLen = new byte[1];
        producerPortByteLen[0] = (byte) producerPortIntLen;
        byte[] nameServerPortByte = nameServerPort.getBytes();
        int nameServerPortIntLen = nameServerPortByte.length;
        byte[] nameServerPortByteLen = new byte[1];
        nameServerPortByteLen[0] = (byte) nameServerPortIntLen;
        byte[] consumerPortByte = consumerPort.getBytes();
        int consumerPortIntLen = consumerPortByte.length;
        byte[] consumerPortByteLen = new byte[1];
        consumerPortByteLen[0] = (byte) consumerPortIntLen;
        byteBuf.writeBytes(ipByteLen);
        byteBuf.writeBytes(ipByte);
        byteBuf.writeBytes(producerPortByteLen);
        byteBuf.writeBytes(producerPortByte);
        byteBuf.writeBytes(nameServerPortByteLen);
        byteBuf.writeBytes(nameServerPortByte);
        byteBuf.writeBytes(consumerPortByteLen);
        byteBuf.writeBytes(consumerPortByte);
        byteBuf.writeInt(IndexFileQueueMap.indexQueueMap.size());
        Set<String> set = IndexFileQueueMap.indexQueueMap.keySet();
        for (String topic : set){
            byte[] topicByte = topic.getBytes();
            byte topicByteLen = (byte) topicByte.length;
            byteBuf.writeBytes(new byte[]{topicByteLen});
            byteBuf.writeBytes(topicByte);
        }
        int byteBufferLen = byteBuf.readableBytes() - 4;
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
        int index = byteBuf.writerIndex();
        byteBuf.resetWriterIndex();
        byteBuf.writeBytes(allLengthByte);
        byteBuf.writerIndex(index);
        return byteBuf;
    }

        public Map decodePull(ByteBuf byteBuf) {






                    byte[] magicNum = new byte[1];
                    byteBuf.readBytes(magicNum);

                    if (!"#".equals(new String(magicNum))) {
                        return null;
                    }

                    byte[] topicByteLen = new byte[1];
                    byteBuf.readBytes(topicByteLen);
                    int topicByteLenInt = topicByteLen[0];
                    byte[] topicByte = new byte[topicByteLenInt];
                    byteBuf.readBytes(topicByte);

                    String topic = new String(topicByte);
                    byte[] pullNumByte = new byte[1];
                    byteBuf.readBytes(pullNumByte);

                    int pullNum = pullNumByte[0];

                    byte[] uniqIdByteLen = new byte[1];

                    byteBuf.readBytes(uniqIdByteLen);

                    byte[] uniqIdByte = new byte[uniqIdByteLen[0]];

                    byteBuf.readBytes(uniqIdByte);

                    String uniqIdString = new String(uniqIdByte);

                    long uniqId = Long.parseLong(uniqIdString);

                    Map map = new HashMap(3);
                    map.put("topic",topic);
                    map.put("pullNum",pullNum);
                    map.put("uniqId",uniqId);



            return map;
        }


       /* public void decodeRestart(ByteBuf byteBuf){



            byte[] ipByteLen = new byte[1];
            byteBuf.readBytes(ipByteLen);
            int ipByteLenInt = ipByteLen[0];
            byteBuf.skipBytes(ipByteLenInt);

            byte[] producerPortByteLen = new byte[1];
            byteBuf.readBytes(producerPortByteLen);
            int producerPortByteLenInt = producerPortByteLen[0];
            byteBuf.skipBytes(producerPortByteLenInt);

            byte[] nameServerPortByteLen = new byte[1];
            byteBuf.readBytes(nameServerPortByteLen);
            int nameServerPortByteLenInt = nameServerPortByteLen[0];
            byteBuf.skipBytes(nameServerPortByteLenInt);

            byte[] consumerPortByteLen = new byte[1];
            byteBuf.readBytes(consumerPortByteLen);
            int consumerPortByteLenInt = consumerPortByteLen[0];
            byteBuf.skipBytes(consumerPortByteLenInt);

            byte[] topicByteLen = new byte[1];
            byteBuf.readBytes(topicByteLen);
            int topicByteLenInt = topicByteLen[0];

            byte[] topicByte = new byte[topicByteLenInt];
            byteBuf.readBytes(topicByte);

            byte[] queueIdByteLen = new byte[1];
            byteBuf.readBytes(queueIdByteLen);
            int queueIdByteLenInt = queueIdByteLen[0];

            byte[] queueIdByte = new byte[queueIdByteLenInt];
            byteBuf.readBytes(queueIdByte);

            long offset = byteBuf.readLong();
            long len = byteBuf.readLong();
            String topic = new String(topicByte);
            String queueId = new String(queueIdByte);


            Map map = MessageInfoQueues.concurrentHashMap;

            MessageInfoQueue messageInfoQueue = new MessageInfoQueue(topic);
            List list = messageInfoQueue.getList();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setOffset(offset);
            messageInfo.setLen(len);

            list.add(messageInfo);
            map.put(topic,messageInfoQueue);

            int mapSize = byteBuf.readInt();
            for (int checkNum = 0;checkNum < mapSize;checkNum++) {
                long consumerUniqId = byteBuf.readLong();
                int consumerIndex = byteBuf.readInt();
                ConsumerIndexTable.concurrentHashMap.put(consumerUniqId,consumerIndex);


            }

        }

        public ByteBuf encodeConsumerIndex(ByteBuf byteBuf){



                Set<Map.Entry<Long, Integer>> set = ConsumerIndexTable.concurrentHashMap.entrySet();

                int mapSize = ConsumerIndexTable.concurrentHashMap.size();

                byteBuf.writeInt(mapSize);
                for (Map.Entry entry : set) {

                    long uniqConcumerId = (long) entry.getKey();
                    int consumerIndex = (int) entry.getValue();

                    byteBuf.writeLong(uniqConcumerId);
                    byteBuf.writeInt(consumerIndex);
                }

            return byteBuf;
        }
*/

        public byte[] encodeIndex(long offset,long len,long sendTime){

        byte[] offsetByte = longToBytes(offset);
        byte[] lenByte = longToBytes(len);
        byte[] sendTimeByte = longToBytes(sendTime);
        byte[] indexByte = new byte[ConstantBroker.INDEX_SIZE];


        int indexNum = 0;

            for (int checkNum = 0;checkNum < 8;checkNum++) {
                indexByte[indexNum++] = offsetByte[checkNum];

            }

            for (int checkNum = 0;checkNum < 8;checkNum++) {
                indexByte[indexNum++] = lenByte[checkNum];

            }

            for (int checkNum = 0;checkNum < 8;checkNum++) {
                indexByte[indexNum++] = sendTimeByte[checkNum];

            }


            return indexByte;

        }


        ByteBuffer b = ByteBuffer.allocate(8);


    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        byte[] b = buffer.array();

        return b;
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer b = ByteBuffer.allocate(8);

        b.put(bytes, 0, bytes.length);
        b.flip();//need flip

        long l = b.getLong();

        return l;
    }


    public AbstractIndex decodeIndex(byte[] indexByte){
        byte[] offsetByte = new byte[8];
        byte[] lenByte = new byte[8];
        byte[] sendTimeByte = new byte[8];
        int indexNum = 0;

        for (int checkNum = 0;checkNum < 8;checkNum++) {
             offsetByte[checkNum] = indexByte[indexNum++];

        }

        for (int checkNum = 0;checkNum < 8;checkNum++) {
             lenByte[checkNum] =  indexByte[indexNum++];

        }

        for (int checkNum = 0;checkNum < 8;checkNum++) {
             sendTimeByte[checkNum] = indexByte[indexNum++];

        }

        long offset = bytesToLong(offsetByte);

        long len = bytesToLong(lenByte);

        long sendTime = bytesToLong(sendTimeByte);

        return new AbstractIndex(offset,len,sendTime);




    }

    public static void main(String[] args){
        byte b = 1;

        int j = b>>1;

    }
}



