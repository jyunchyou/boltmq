package io.openmessaging.consumer.handler;

import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.openmessaging.consumer.constant.ConstantConsumer;
import io.openmessaging.consumer.consumer.ConsumeCallBack;
import io.openmessaging.consumer.filter.FilterList;
import io.openmessaging.consumer.listener.ListenerMessage;
import io.openmessaging.consumer.net.EncodeAndDecode;
import io.openmessaging.consumer.net.NettyConsumer;
import io.openmessaging.consumer.table.ConsumerBrokerMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by fbhw on 17-12-9.
 */
//一个channel对应一个handler，一个topic有broker个数的channel,一个consumer对应一个topic,一个进程对应多个consumer
public class ReceiveMessageHandlerAdapter extends ChannelHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(ReceiveMessageHandlerAdapter.class);

    private EncodeAndDecode encodeAndDecode = new EncodeAndDecode();

    private List list;

    private ListenerMessage listenerMessage = null;

    private int num;

    private CountDownLatch countDownLatch = null;

    private FilterList filterList = new FilterList();

    private ConsumeCallBack consumeCallBack;

    private String channelInstanceId = null;

    private ByteBuf lastByteBuf = null;

    private Long consumeIndex;

    private MappedByteBuffer mappedByteBuffer;

    private String consumerName;

    private int consumeModel;

    public ReceiveMessageHandlerAdapter(String topic,String channelInstanceId,String consumerName,int model) throws IOException {
/*
        this.num = num;
        this.listenerMessage = listenerMessage;
        this.countDownLatch = countDownLatch;
        this.list = new ArrayList(num);*/
        this.consumeModel = model;
        this.consumerName = consumerName;
        this.channelInstanceId = channelInstanceId;
        //获取消费下标，供持久化保证最少一次消费
        consumeIndex = NettyConsumer.consumeIndexMap.get(topic + channelInstanceId);

        if (consumeIndex == null) {

            consumeIndex = 1L;
            NettyConsumer.consumeIndexMap.put(topic + channelInstanceId,1L);
        }

        consumeCallBack = NettyConsumer.consumeCallBackMap.get(consumerName);

        //获取消费下标文件流对象
        File consumeAdd = new File(ConstantConsumer.CONSUME_INDEX_FILE_ADDRESS + topic);
        if (!consumeAdd.exists()) {
            consumeAdd.mkdirs();
        }

            File consumeFile = new File(ConstantConsumer.CONSUME_INDEX_FILE_ADDRESS + topic + "\\" + channelInstanceId);
            if (!consumeFile.exists()) {
                consumeFile.createNewFile();
            }


            mappedByteBuffer = new RandomAccessFile(consumeFile, "rw").getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 8);


         }




    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("method channelActive has executed2");
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        //收到broker返回的消息
        //logger.info("method channelActive has executed3");
        ByteBuf byteBuf = (ByteBuf) msg;


        if (lastByteBuf != null) {

            lastByteBuf.writeBytes(byteBuf);
            byteBuf = lastByteBuf;
        }
        if (consumeCallBack == null) {

            consumeCallBack = NettyConsumer.consumeCallBackMap.get(consumerName);
        }
        lastByteBuf = encodeAndDecode.deCode(byteBuf,consumeCallBack,this,mappedByteBuffer,consumeModel,channelHandlerContext.channel());

//        ByteBuf b = Unpooled.buffer(4);
//        b.write(8);
//        channelHandlerContext.writeAndFlush(b);
        /*
        if (byteBuf.readableBytes() <= 0) {
            countDownLatch.countDown();
            return;
        }

//decode缓存byte[],为一级解析
// channelhandleradapter缓存 list,为二级解析,只返回list 没有大小限制,可能小于pullNum
// 也可能等于,也可能大于


        List backList = encodeAndDecode.decodeMessage(byteBuf,num);





        if (backList.size() == 0) {
            countDownLatch.countDown();
            return;
        }else {

            filterList.setPullNum(num);
            List<List> list = filterList.filter(backList);
            if (list == null) {
                countDownLatch.countDown();
                return;
            }

            for (List l : list) {

                listenerMessage.listener(l);

            }

        }
        ReferenceCountUtil.release(byteBuf);
    }*/
    }

    public long getConsumeIndex() {
        return consumeIndex;
    }

    public void setConsumeIndex(long consumeIndex) {
        this.consumeIndex = consumeIndex;
    }
}