package io.openmessaging.store;

import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by fbhw on 17-12-6.
 */
public class MessageStoreTest {

    private  MessageStore messageStore;

    @Before
    public void init(){
        messageStore = MessageStore.getMessageStore();
    }

    @Test
    public void fileIndexTest() {
        File file = new File("/home/fbhw/fileIndexData");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024 * 1024);
        while (byteBuffer.hasRemaining()) {
            byteBuffer.put("a".getBytes());

        }




        try {
            fileOutputStream.write(byteBuffer.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
         }
}
