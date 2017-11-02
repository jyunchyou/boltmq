package io.openmessaging.demo;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.util.Iterator;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
/**
*无状态的连接对象，面向线程池里面的线程
*
*/
public class Broker
{
    
    
    private Broker(){
        
    }
    
    
    
    public static Selector init() throws IOException{
        ServerSocketChannel serverSocketChannel = null;
        try
        {
            serverSocketChannel = ServerSocketChannel.open();
        }
        catch (IOException e)
        {
            throw e;
        }
        serverSocketChannel.configureBlocking(false);
        

        serverSocketChannel.socket().bind(new InetSocketAddress(80));
      
        Selector selector = Selector.open();
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        return selector;
        
    }
    //开始接收数据
    
    public static Iterator accept(Selector selector) throws IOException{
        
            try
            {
                selector.select();
            }
            catch (IOException e)
            {
                throw e;
            }
           Iterator<SelectionKey> iterator =  selector.selectedKeys().iterator();
       

           return iterator;
            
        }
        
  
    
}
