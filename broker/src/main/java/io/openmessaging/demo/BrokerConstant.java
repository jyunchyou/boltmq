package io.openmessaging.demo;

public class BrokerConstant
{
    public static final int buffSize = 8000000;

    public static final byte cutFlag = "$".getBytes()[0];

    public static final String cutChar = "$";

    public static final String cutChildChar = "#";

    public static final byte cutChild = "#".getBytes()[0];
    
    public static final int ACCEPT_POOL_SIZE = 10;
    
    public static final int BUFFER_RECEIVE_SIZE = 3000;
    
    public static final int BUFFER_SEND_SIZE = 3000;
    
    public static final String FILE_ROOT_PATH = "/storage/emulated/0/AppProjects/MQBroker";



}
