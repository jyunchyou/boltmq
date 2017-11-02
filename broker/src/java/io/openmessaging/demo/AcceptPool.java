package io.openmessaging.demo;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class AcceptPool
{
    
    private ExecutorService executorService = null;
    private static final AcceptPool acceptPool = new AcceptPool();
   
    private AcceptPool(){
        init();     
    }
   
    public void init(){
       
      
        executorService = Executors.newFixedThreadPool(BrokerConstant.ACCEPT_POOL_SIZE);
        for (int checkNum = 0;checkNum < BrokerConstant.ACCEPT_POOL_SIZE;checkNum++) {
            
            DefaultWorker defaultWorker = new DefaultWorker();
            
         //  executorService.execute();
            
        }
    }
    
    
    
    
    
}
