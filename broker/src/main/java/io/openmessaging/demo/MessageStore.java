package io.openmessaging.demo;

import io.openmessaging.KeyValue;
import io.openmessaging.MessageHeader;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageStore {

    private static final MessageStore INSTANCE = new MessageStore();

    private Map<String,QueueProxy>  map = new HashMap();
    public static MessageStore getInstance() {
        return INSTANCE;
    }

    private ByteBuffer byteBuffer = ByteBuffer.allocate(BrokerConstant.BUFFER_RECEIVE_SIZE);

    private ByteBuffer receiveBuffer = ByteBuffer.allocate(BrokerConstant.BUFFER_RECEIVE_SIZE);
    
    private ByteBuffer sendBuffer = ByteBuffer.allocate(BrokerConstant.BUFFER_RECEIVE_SIZE);
    
    
    private AtomicInteger atomicIntegerFileName = new AtomicInteger(0);

    private AtomicBoolean atomicBooleanOverFlag = new AtomicBoolean(true);

    private HashMap<String,List<Integer>> threadIdMap =new HashMap();

    private AtomicInteger atomicIntegerThreadId = new AtomicInteger(1);

    private HashMap<Integer,Queue<DefaultBytesMessage>> queueMap = new HashMap(20);

    private AtomicBoolean flushFlag = new AtomicBoolean(true);

    private Semaphore semaphore = new Semaphore(20);


    private MessageStore(){
        
       
    }
    public synchronized byte[] serianized(DefaultBytesMessage message){

        StringBuilder stringBuilder = new StringBuilder();
        for (String key : message.headers().keySet()){
            stringBuilder.append(key);
            stringBuilder.append(":");
            stringBuilder.append(message.headers().getString(key));
            stringBuilder.append("#");

        }
        stringBuilder.append(BrokerConstant.cutChar);
        for (String propertiesKey : message.properties().keySet()) {
            stringBuilder.append(propertiesKey);
            stringBuilder.append(":");
            stringBuilder.append(message.properties().getString(propertiesKey));
            stringBuilder.append("#");

        }
        stringBuilder.append(BrokerConstant.cutChar);
        byte[] headerPropertiesByte = stringBuilder.toString().getBytes();
        byte[] body = message.getBody();
        byte[] messageByte = new byte[headerPropertiesByte.length+body.length+1];
        for (int index = 0;index < headerPropertiesByte.length;index++) {
            messageByte[index] = headerPropertiesByte[index];
        }
        for (int index = 0,indexNum = headerPropertiesByte.length;index < body.length;index++,indexNum++) {
            messageByte[indexNum] = body[index];

        }
        messageByte[messageByte.length - 1] = BrokerConstant.cutFlag;
        return messageByte;
    }



//写入
    public synchronized void sendMessage(ByteBuffer byteBuffer) throws FileNotFoundException, IOException{
        File file = new File(BrokerConstant.FILE_ROOT_PATH +"/"+atomicIntegerFileName.get());

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                
                throw e;
            }

        }
        File writeFile = new File(BrokerConstant.FILE_ROOT_PATH +"/"+atomicIntegerFileName.getAndAdd(1));
        FileOutputStream fileOutputStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(writeFile);
        }
        
        catch (FileNotFoundException e)
        {
            throw e;
        }
        FileChannel fileChannel = fileOutputStream.getChannel();
        byteBuffer.flip();
        fileChannel.write(byteBuffer);
        fileChannel.close();
        fileOutputStream.close();
       
       
            }

           
            
            //负责将buffer消息写到pagecache
            
    

            public synchronized void writeMessage() throws IOException{
                File file = new File(BrokerConstant.FILE_ROOT_PATH + "/" + atomicIntegerFileName.get());

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {

                        throw e;
                    }

                }
                File writeFile = new File(BrokerConstant.FILE_ROOT_PATH + "/" + atomicIntegerFileName.getAndAdd(1));
                FileOutputStream fileOutputStream = null;
                try
                {
                    fileOutputStream = new FileOutputStream(writeFile);
                }

                catch (FileNotFoundException e)
                {
                    throw e;
                }
                FileChannel fileChannel = fileOutputStream.getChannel();
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
                fileChannel.close();
                fileOutputStream.close();
                semaphore.release();
                
                
                
                
                
            }

    public synchronized void putMessage(DefaultBytesMessage message,KeyValue properties) throws IOException {

        byte[] messageByte = serianized(message);

        if (messageByte.length >= byteBuffer.remaining()) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendMessage(byteBuffer);

            byteBuffer = ByteBuffer.allocate(BrokerConstant.buffSize);
        }
            byteBuffer.put(messageByte);

    }

    public synchronized ByteBuffer deSerianied(KeyValue properties){
        File file = new File(properties.getString("STORE_PATH") +"/"+atomicIntegerFileName.getAndAdd(1));
        if (!file.exists()) {
            atomicBooleanOverFlag.compareAndSet(true,false);
            return null;

        }

        FileInputStream fileInputStream = null;
        FileChannel fileChannel = null;


        try {

           fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();


        }

        try {
            fileChannel = fileInputStream.getChannel();
            byteBuffer.clear();
            fileChannel.read(byteBuffer);

        } catch (IOException e) {
            e.printStackTrace();

        }
        try {
            fileChannel.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


       /* Path path = Paths.get(properties.getString("STORE_PATH")+"/"+atomicIntegerFileName.getAndAdd(1));
        AsynchronousFileChannel asynchronousFileChannel = null;
        try {
            asynchronousFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Future future = asynchronousFileChannel.read(byteBuffer,0);

        while (!future.isDone());*/

        return byteBuffer;
    }


    public static void main (String[] args) {
        MessageStore main = new MessageStore();
        DefaultBytesMessage defaultBytesMessage = new DefaultBytesMessage("hello".getBytes());
        defaultBytesMessage.putHeaders("topic","TOPIC_1");
        defaultBytesMessage.putProperties("ppp","kkk");
        defaultBytesMessage.putProperties("STORE_PATH","/home/fbhw/race");

        DefaultBytesMessage defaultBytesMessage1 = new DefaultBytesMessage(null);
        byte[] buffBytes = main.serianized(defaultBytesMessage);
        int cutCount = 1;
        int seek = 0;
        byte[] headerByte = null;
        byte[] propertiesByte = null;
        byte[] body = null;
        String headerStirng = null;
        String propertiesString = null;
        for (int indexNum = 0;indexNum < buffBytes.length;indexNum++) {

            if (buffBytes[indexNum] == BrokerConstant.cutFlag) {

                if (cutCount == 1) {

                    headerByte = new byte[indexNum - seek];

                    for (int checkNum = 0;checkNum < headerByte.length;checkNum++,seek++) {
                        headerByte[checkNum] = buffBytes[seek];

                    }
                  //  System.out.println(Arrays.toString(headerByte));
                    int seekChild = 0;
                    for (int index = 0;index < headerByte.length;index++) {
                        if (headerByte[index] == BrokerConstant.cutChild) {

                            byte[] header = new byte[index - seekChild];
                            index++;

                            for (int i = 0;i < header.length;i++,seekChild++) {
                                header[i] = headerByte[seekChild];


                            }
                            headerStirng = new String(header);
                            String[] headers = headerStirng.split(":");
                            String headerKey = headers[0];

                            String headerValue = headers[1];
                           // System.out.println(headerKey+headerValue);
                            defaultBytesMessage1.putHeaders(headerKey,headerValue);


                            seekChild++;
                        }


                    }




                }

                if (cutCount == 2) {

                    propertiesByte = new byte[indexNum - seek];

                    for (int checkNum = 0; checkNum <propertiesByte.length; checkNum++, seek++) {
                        propertiesByte[checkNum] = buffBytes[seek];

                    }
                   // System.out.println(Arrays.toString(propertiesByte));
                    int seekChild = 0;
                    for (int index = 0; index < propertiesByte.length; index++) {
                        if (propertiesByte[index] == BrokerConstant.cutChild) {


                            byte[] properties = new byte[index - seekChild];
                            index++;
                            for (int i = 0; i < properties.length; i++, seekChild++) {
                                properties[i] = propertiesByte[seekChild];


                            }
                            propertiesString = new String(properties);

                            String[] propertie = propertiesString.split(":");
                            String propertiesKey = propertie[0];
                            String propertiesValue = propertie[1];
                          /*  System.out.println(propertiesKey);
                            System.out.println(propertiesValue);*/
                            defaultBytesMessage1.putProperties(propertiesKey,propertiesValue);

                            seekChild++;
                        }


                    }


                }

                    if (cutCount == 3) {
                    body = new byte[indexNum - seek];
                    for (int checkNum = 0; checkNum < body.length; checkNum++, seek++) {
                        body[checkNum] = buffBytes[seek];

                    }


                    defaultBytesMessage1.setBody(body);



                    cutCount = 0;
                }

                ++cutCount;
                ++seek;
                }



        }
System.out.println("======");
System.out.println(defaultBytesMessage1.headers().getString("topic"));
       System.out.println(defaultBytesMessage1.properties().getString("STORE_PATH"));
        System.out.println(defaultBytesMessage1.properties().getString("ppp"));
        System.out.println(new String(defaultBytesMessage1.getBody()));


    }
    public synchronized void insertMessage(ByteBuffer byteBuffer){
        byteBuffer.flip();
        byte[] buffBytes = byteBuffer.array();

        int cutCount = 1;
        int seek = 0;
        byte[] headerByte = null;
        byte[] propertiesByte = null;
        byte[] body = null;
        String headerStirng = null;
        String propertiesString = null;
        DefaultBytesMessage defaultBytesMessage1 = new DefaultBytesMessage(null);
        for (int indexNum = 0;indexNum < buffBytes.length;indexNum++) {

            if (buffBytes[indexNum] == BrokerConstant.cutFlag) {

                if (cutCount == 1) {

                    headerByte = new byte[indexNum - seek];

                    for (int checkNum = 0;checkNum < headerByte.length;checkNum++,seek++) {
                        headerByte[checkNum] = buffBytes[seek];

                    }
                    //  System.out.println(Arrays.toString(headerByte));
                    int seekChild = 0;
                    for (int index = 0;index < headerByte.length;index++) {
                        if (headerByte[index] == BrokerConstant.cutChild) {

                            byte[] header = new byte[index - seekChild];
                            index++;

                            for (int i = 0;i < header.length;i++,seekChild++) {
                                header[i] = headerByte[seekChild];


                            }
                            headerStirng = new String(header);
                            String[] headers = headerStirng.split(":");
                            String headerKey = headers[0];

                            String headerValue = headers[1];
                          //  System.out.println(headerKey+headerValue);
                            defaultBytesMessage1.putHeaders(headerKey,headerValue);


                            seekChild++;
                        }


                    }




                }

                if (cutCount == 2) {

                    propertiesByte = new byte[indexNum - seek];

                    for (int checkNum = 0; checkNum <propertiesByte.length; checkNum++, seek++) {
                        propertiesByte[checkNum] = buffBytes[seek];

                    }
                    // System.out.println(Arrays.toString(propertiesByte));
                    int seekChild = 0;
                    for (int index = 0; index < propertiesByte.length; index++) {
                        if (propertiesByte[index] == BrokerConstant.cutChild) {


                            byte[] properties = new byte[index - seekChild];
                            index++;
                            for (int i = 0; i < properties.length; i++, seekChild++) {
                                properties[i] = propertiesByte[seekChild];


                            }
                            propertiesString = new String(properties);

                            String[] propertie = propertiesString.split(":");
                            String propertiesKey = propertie[0];
                            String propertiesValue = propertie[1];

                         //   System.out.println(propertiesKey+propertiesValue);
                            defaultBytesMessage1.putProperties(propertiesKey,propertiesValue);

                            seekChild++;
                        }


                    }


                }

                if (cutCount == 3) {
                    body = new byte[indexNum - seek];
                    for (int checkNum = 0; checkNum < body.length; checkNum++, seek++) {
                        body[checkNum] = buffBytes[seek];

                    }

                  //  System.out.println(new String(body));

                    defaultBytesMessage1.setBody(body);
                  /*  String headerKey = defaultBytesMessage1.headers().keySet().iterator().next();
                    String headerValue = defaultBytesMessage1.headers().getString(headerKey);
                    String propert = defaultBytesMessage1.properties().keySet().iterator().next();
                    String properti = defaultBytesMessage1.properties().getString(propert);
                    String bod = new String(defaultBytesMessage1.getBody());

                    System.out.println(headerKey+"-"+headerValue+"-"+propert+"-"+properti+"-"+new String(bod));
*/

                        String bucket = defaultBytesMessage1.headers().keySet().contains(MessageHeader.TOPIC)?defaultBytesMessage1.headers().getString(MessageHeader.TOPIC):defaultBytesMessage1.headers().getString(MessageHeader.QUEUE);

                    List<Integer> list = threadIdMap.get(bucket);
                    if (list == null) {
                        list = new ArrayList<Integer>();
                        threadIdMap.put(bucket,list);

                    }
                    Queue queue = null;
                    for (int id : list) {

                        queue = queueMap.get(id);
                        queue.add(defaultBytesMessage1);
                    }


                    if (++indexNum < buffBytes.length){
                        defaultBytesMessage1 = new DefaultBytesMessage(null);
                    }
                    cutCount = 0;
                }

                ++cutCount;
                ++seek;
            }









        }

    }
    public synchronized DefaultBytesMessage pullMessage(KeyValue properties,int threadId) {


        while (true) {
            Queue<DefaultBytesMessage> defaultBytesMessagesQueue = queueMap.get(threadId);
            DefaultBytesMessage defaultBytesMessage = defaultBytesMessagesQueue.poll();

            if (defaultBytesMessage == null && atomicBooleanOverFlag.get() == false) {
                return null;

            }
            if (defaultBytesMessage == null && atomicBooleanOverFlag.get() == true) {

                ByteBuffer byteBuffer = deSerianied(properties);
                if (byteBuffer == null) {


                    continue;
                }
                insertMessage(byteBuffer);

                continue;

            }

        /*    String headerKey = defaultBytesMessage.headers().keySet().iterator().next();
            String headerValue = defaultBytesMessage.headers().getString(headerKey);
            String propertiesKey = defaultBytesMessage.properties().keySet().iterator().next();
            String propertiesValue = defaultBytesMessage.properties().getString(propertiesKey);
            String body = new String(defaultBytesMessage.getBody());*/

       // System.out.println(defaultBytesMessage);
            return defaultBytesMessage;
        }
    }

    public synchronized void attachInit(Collection<String> topics,String queue,KeyValue properties,int threadId){




            queueMap.put(threadId,new LinkedList<DefaultBytesMessage>());

        List<Integer> list = new ArrayList();
        list.add(threadId);
        threadIdMap.put(queue,list);

        for (String topic : topics) {
            List listTopic = threadIdMap.get(topic);
            if (listTopic == null) {
                listTopic = new ArrayList();
                listTopic.add(threadId);
                threadIdMap.put(topic,listTopic);

                continue;

            }
            listTopic.add(threadId);

        }
        /*
        QueueProxy queueProxyQueue = new QueueProxy();
        map.put(queue,queueProxyQueue);
  //      queueProxyQueue.attachQueue(queue);


        QueueProxy queueProxyTopic = null;
        for(String topic : topics) {
            queueProxyTopic = map.get(topic);

            if (queueProxyTopic == null) {
                queueProxyTopic = new QueueProxy();
                map.put(topic,queueProxyTopic);

            }
//            queueProxyTopic.attachTopics(topic);



*/
    }
   public void flush(KeyValue properties) throws FileNotFoundException, IOException {

        if (flushFlag.compareAndSet(true,false)) {
           
            File file = new File(properties.getString("STORE_PATH") + "/" + atomicIntegerFileName.get());

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            if (byteBuffer.hasRemaining()) {
                File wariteFile = new File(properties.getString("STORE_PATH") + "/" + atomicIntegerFileName.getAndAdd(1));
        
                FileOutputStream fileOutputStream = null;
               try
               {
                   fileOutputStream = new FileOutputStream(file);
               }
               catch (FileNotFoundException e)
               {
                   throw e;
               }

                FileChannel fileChannel = fileOutputStream.getChannel();
                byteBuffer.flip();
               try
               {
                   fileChannel.write(byteBuffer);
               }
               catch (IOException e)
               {
                   throw e;
               }
            
               fileChannel.close();
               fileOutputStream.close();

            atomicIntegerFileName.set(0);
            System.out.println("发送完毕!");
        }
           }}


    public AtomicInteger getAtomicIntegerThreadId() {
        return atomicIntegerThreadId;
    }

    public void setAtomicIntegerThreadId(AtomicInteger atomicIntegerThreadId) {
        this.atomicIntegerThreadId = atomicIntegerThreadId;
    }

    public HashMap getQueueMap() {
        return queueMap;
    }

    public void setQueueMap(HashMap queueMap) {
        this.queueMap = queueMap;
    }
    
    public ByteBuffer getReceiveBuffer(){
        return receiveBuffer;
    }
    
    public void setReceiveBuffer(ByteBuffer byteBuffer){
        
        this.receiveBuffer = byteBuffer;
    }
    
    public ByteBuffer getsendBuffer(){
        return sendBuffer;
    }
    
    
    public void setSendBuffer(ByteBuffer byteBuffer){

        this.sendBuffer = byteBuffer;
    }
}
