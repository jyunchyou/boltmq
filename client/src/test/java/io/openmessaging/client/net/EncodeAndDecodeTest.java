package io.openmessaging.client.net;

import io.netty.buffer.ByteBuf;
import io.openmessaging.client.producer.BProperties;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-1.
 */


public class EncodeAndDecodeTest {
    Logger logger = LoggerFactory.getLogger(EncodeAndDecode.class);

    EncodeAndDecode encodeAndDecode = null;

    @Before
    public void init(){

        encodeAndDecode = new EncodeAndDecode();
    }

    //@Test
    public void testDecode(){
        io.openmessaging.client.producer.Message message = new io.openmessaging.client.producer.Message("TOPIC_01","consumer","消费成功!".getBytes());

        BProperties BProperties = new BProperties();

        BProperties.putProperties("属性key1","属性value1");
        BProperties.putProperties("属性key2","属性value2");
        BProperties.putProperties("属性key3","属性value3");

        BaseMessage requestDto = new BaseMessage();

          requestDto.setDelayTime((byte) 10000);

        ByteBuf byteBuf = null;
        try {
            byteBuf = encodeAndDecode.encodeMessage(message, BProperties,requestDto);

        } catch (Exception e) {
            e.printStackTrace();
        }
       // logger.info(byteBuffer.array().toString());
    }
}
