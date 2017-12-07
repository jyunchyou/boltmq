package io.openmessaging.concumer.table;

import io.openmessaging.consumer.table.ReceiveMessageTable;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fbhw on 17-12-7.
 */
public class TestReceiveMessageTable {

    Logger logger = LoggerFactory.getLogger(TestReceiveMessageTable.class);

    private ReceiveMessageTable receiveMessageTable = null;

    @Before
    public void init(){

        receiveMessageTable = new ReceiveMessageTable();
    }

    @Test
    public void testUpdateReceiveTableFromNameServer(){
        receiveMessageTable.updateReceiveTableFromNameServer("TOPIC_01");

    }


}
