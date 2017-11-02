package java.io.openmessaging.demo;
import java.util.Date;
import java.nio.ByteBuffer;

public class Test
{
    
    public static void main (String[] args) throws InterruptedException{
       System.out.println("1");
       {
          
           System.out.println(2);
       }
       {
           System.out.println(3);
       }
        new Thread(new Runnable(){

                @Override
                public void run()
                {
                    // TODO: Implement this method
                    
                    for (;;) {
                        ByteBuffer bt =  ByteBuffer.allocate(300000000);
                        new Date();
                        for (int a=0;a<10;a++) {

                            bt.putInt(5);
                            System.out.println(bt.toString());
                            
                            ByteBuffer b =  ByteBuffer.allocate(300000000);
                            new Date();
                            for (int bi=0;bi<10;bi++) {

                                bt.putInt(5);
                                System.out.println(bt.toString());
                            }

                        }
                        
                            
                        }

                    }
                    
                
                
           
       }).start();
      
    }
}
