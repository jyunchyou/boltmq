package io.openmessaging.demo;
import java.io.IOException;
import java.util.Iterator;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import io.openmessaging.demo.MessageStore;

public class DefaultWorker implements Runnable{
    
    private MessageStore messageStore = MessageStore.getInstance();
    private ByteBuffer byteBuffer = MessageStore.getInstance().getReceiveBuffer();
    
    
    
    public DefaultWorker(){
        
    }
    @Override
    public void run()
    {
        while (true) {
       
        Selector selector = null;
        try
        {
           selector = Broker.init();
        }
        catch (IOException e)
        {}
        
        Iterator<SelectionKey> iterator = null;
        try
        {
           iterator = Broker.accept(selector);
        }
        catch (IOException e)
        {}
     
        
        
        //在stage里面一个线程iterator全执行
        while (iterator.hasNext()) {
            SelectionKey selectionKey =  iterator.next();

            iterator.remove();
            if (selectionKey.isReadable()) {

                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();


                try
                {
                    socketChannel.read(byteBuffer);
                }
                catch (IOException e)
                {}
                if (!byteBuffer.hasRemaining()) {
                    
                    try
                    {
                        messageStore.sendMessage(byteBuffer);
                    }
                    catch (IOException e)
                    {}

                    messageStore.setReceiveBuffer(ByteBuffer.allocate(BrokerConstant.BUFFER_RECEIVE_SIZE));
               
                   
                    }
            }
            
        }
        
        

        
        }
        
    
}
        
  

}
