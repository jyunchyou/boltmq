import io.netty.buffer.ByteBuf;
import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.net.EncodeAndDecode;
import io.openmessaging.table.AbstractFile;
import io.openmessaging.table.AbstractMessage;
import io.openmessaging.table.FileQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Main {


    private File file = null;

    private RandomAccessFile randomAccessFile = null;

    private MappedByteBuffer mappedByteBuffer = null;

    private FileChannel channel = null;

    @Before
    public void s() {
        init("test", "test", 10000);
    }

    public void init(String queueId, String fileName, int fileSize) {


        file = new File(ConstantBroker.ROOT_PATH + fileName);
        try {

            if (!file.exists()) {
                file.createNewFile();
            }


            randomAccessFile = new RandomAccessFile(file, "rw");
            channel = randomAccessFile.getChannel();
            mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public AbstractMessage getMessage(int fileOffset, int len) {


        byte[] messageByte = new byte[len];

        for (int indexNum = 0; indexNum < len; indexNum++) {
            messageByte[indexNum++] = mappedByteBuffer.get(fileOffset++);
        }
        return new AbstractMessage(messageByte);
    }

    public void putMessage(byte[] messageByte) {
        mappedByteBuffer.put(messageByte);


    }

    @Test
    public void test() {
         /*   byte[] b = "   abc发  射的  发送，反倒是撒发f  asdf a".getBytes();
            mappedByteBuffer.put(b);
            mappedByteBuffer.force();
            for (int i= 0;i < b.length;i++) {
                System.out.print(b[i]);
            }
            System.out.println();

            int start = 0;
            byte[] backB = new byte[b.length];
            for (int i= 0;i < b.length;i++) {
                backB[i] = mappedByteBuffer.get(start++);
            }
            for (int i= 0;i < backB.length;i++) {
                System.out.print(backB[i]);
            }




        }
*/

        byte[] b = "   abc发  射的  发送，反倒是撒发f  asdf a".getBytes();

        FileQueue fileQueue = new FileQueue("TOPIC_01");
        AbstractFile abstractFile = new AbstractFile("TOPIC_01",1+"", (int) ConstantBroker.FILE_SIZE);
        abstractFile.putMessage(b);

        for (int i= 0;i < b.length;i++) {
            System.out.print(b[i]);
        }
        System.out.println();


        int start = 0;
        byte[] backB = abstractFile.getMessage(0,b.length).getMessageByte();

        for (int i= 0;i < backB.length;i++) {
            System.out.print(backB[i]);
        }



    }

}
