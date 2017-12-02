package io.openmessaging.client.net;

import io.openmessaging.client.constant.ConstantClient;
import io.openmessaging.client.exception.OutOfBodyLengthException;
import io.openmessaging.client.producer.Message;
import io.openmessaging.client.producer.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

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

    @Test
    public void testDecode(){
        Message message = new Message("TOPIC_01","consumer","消费成功!".getBytes());

        Properties properties = new Properties();

        properties.putProperties("属性key1","属性value1");
        properties.putProperties("属性key2","属性value2");
        properties.putProperties("属性key3","属性value3");

        RequestDto requestDto = new RequestDto();

        requestDto.setId("1");
        requestDto.setCode(0);
        requestDto.setDelayTime(10000);

        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = encodeAndDecode.encodeMessage(message,properties,requestDto);

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(byteBuffer.array().toString());
    }
}
