package io.openmessaging.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fbhw on 17-12-5.
 */
public class EncodeAndDecode {

        //需要解析queueId,Topic字段

        public Map decode(ByteBuf byteBuf){


            byte[] allLength = new byte[4];
            byteBuf.readBytes(allLength);

            byte[] topicByteLen = new byte[1];

            byteBuf.readBytes(topicByteLen);

            int topicIntLen = topicByteLen[0];

            byte[] topicByte = new byte[topicIntLen];

            byteBuf.readBytes(topicByte);

            String topic = new String(topicByte);

            byte[] queueIdByteLen = new byte[1];

            byteBuf.readBytes(queueIdByteLen);

            int queueIdIntLen = queueIdByteLen[0];

            byte[] queueIdByte = new byte[queueIdIntLen];

            byteBuf.readBytes(queueIdByte);

            String queueId = new String(queueIdByte);

            Map map = new HashMap(1);

            map.put(topic,queueId);

            return map;



        }

        public ByteBuf encodeSendMessageBack(){
            byte[] sendResultBytes = "1".getBytes();
            ByteBuf byteBuf = Unpooled.buffer(sendResultBytes.length);

            byteBuf.writeBytes(sendResultBytes);

            return byteBuf;
        }
}
