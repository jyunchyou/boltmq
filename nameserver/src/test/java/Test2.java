import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.openmessaging.constant.ConstantNameServer;
import org.junit.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by fbhw on 17-12-16.
 */
public class Test2 {

    @org.junit.Test
    public void testByteBufferToByteBuf(){
        ByteBuf byteBuf = Unpooled.buffer(3000);
        byteBuf.writeBytes("fdasfa".getBytes());

        ByteBuffer byteBuffer = byteBuf.nioBuffer();

        //byteBuffer.flip();

        File file = new File(ConstantNameServer.INDEX_STORE_PATH);

        FileOutputStream fileOutputStream = null;
        try {
             fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileChannel fileChannel = fileOutputStream.getChannel();

        try {
            fileChannel.force(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
