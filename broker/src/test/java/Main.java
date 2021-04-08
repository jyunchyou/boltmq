import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.exception.RegisterException;
import io.openmessaging.net.NettyServer;
import io.openmessaging.processor.ProcessorIn;
import io.openmessaging.processor.ProcessorOut;
import io.openmessaging.start.AbstractStart;
import io.openmessaging.start.BProperties;
import io.openmessaging.table.AbstractMessage;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;

public class Main {


    private File file = null;

    private RandomAccessFile randomAccessFile = null;

    private MappedByteBuffer mappedByteBuffer = null;

    private FileChannel channel = null;

    @Before
    public void s() {
       // init("test", "test", 10000);
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


    public static  void main(String[] args) throws RegisterException, InterruptedException, IOException, ExecutionException {
        BProperties bProperties = new BProperties();
        bProperties.setServiceName("broker2");
        bProperties.setGroup("brokers");
        bProperties.setServer("127.0.0.1:8848");
        bProperties.setIp("127.0.0.1");
        bProperties.setPort(ConstantBroker.PULL_PORT);


        AbstractStart.registry(bProperties);


        BProperties Properties = new BProperties();
        Properties.setServiceName("broker1");
        Properties.setGroup("brokers");
        Properties.setServer("127.0.0.1:8848");
        Properties.setIp("127.0.0.1");
        Properties.setPort(ConstantBroker.SEND_PORT);


        //接收consumer发来的请求
        NettyServer.nettyServer.bindPullPort(ConstantBroker.PULL_PORT);

        NettyServer.nettyServer.bindUpdateConsumeIndexPort(ConstantBroker.CONSUME_CONFIRM_PORT);


        NettyServer.nettyServer.bind(ConstantBroker.SEND_PORT);


        AbstractStart.registry(Properties);


        AbstractStart.loadIndexMap();
        AbstractStart.loadMassageIndex();

       // ProcessorOut processorOut = new ProcessorOut();
        //processorOut.outIndexShardingPage("iiiiii",10,1,null,false,null);



        Thread.sleep(500000);
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

       /* byte[] b = "   abc发  射的  发送，反倒是撒发f  asdf a".getBytes();

        FileQueue fileQueue = new FileQueue("TOPIC_01");
        AbstractFile abstractFile = new AbstractFile("TOPIC_01",1+"", (int) ConstantBroker.FILE_SIZE);
        abstractFile.putMessage(b);

        for (int i= 0;i <初始化测试 b.length;i++) {
            System.out.print(b[i]);
        }
        System.out.println();


        int start = 0;
        byte[] backB = abstractFile.getMessage(0,b.length).getMessageByte();

        for (int i= 0;i < backB.length;i++) {
            System.out.print(backB[i]);
        }

*/

    }

    @Test
    public void test() throws RegisterException, InterruptedException {


    }

}
