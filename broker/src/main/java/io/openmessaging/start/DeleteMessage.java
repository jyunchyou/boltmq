package io.openmessaging.start;

import io.openmessaging.Constant.ConstantBroker;
import io.openmessaging.table.MessageInfo;
import io.openmessaging.table.MessageInfoQueue;
import io.openmessaging.table.MessageInfoQueues;

import java.io.File;
import java.util.*;

public class DeleteMessage {


    public void startDelete() {
        //开启定时任务

        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new CycleTask(),  ConstantBroker.DELETE_MESSAGE_INDEX_CYCLE, ConstantBroker.DELETE_MESSAGE_INDEX_CYCLE);
    }


    //定时任务类
    class CycleTask extends TimerTask {


        @Override
        public void run() {


            //删除过期索引
            long nowTime = System.currentTimeMillis();
            Set<Map.Entry<String, MessageInfoQueue>> set = MessageInfoQueues.concurrentHashMap.entrySet();

            for (Map.Entry entry : set) {

                MessageInfoQueue messageInfoQueue = (MessageInfoQueue) entry.getValue();
                List<MessageInfo> list = messageInfoQueue.getList();
                for (MessageInfo messageInfo:list) {

                    long saveTime = messageInfo.getSendTime() - nowTime;
                    if (saveTime > ConstantBroker.MESSAGE_SAVE_HOW_LONG) {
                        list.remove(messageInfo);

                    }

                }

            }

            //释放磁盘
            File file = new File(ConstantBroker.ROOT_PATH);
            long totalSpace = file.getTotalSpace();
            float occupy = (totalSpace - file.getFreeSpace())/totalSpace;
            if (occupy > ConstantBroker.DElETE_DISK_OVER) {
                String[] childFileNames = file.list();
                for (String childFileName : childFileNames) {
                    File childFile = new File(childFileName);
                    if (childFile.exists()) {
                        boolean deleteResult = childFile.delete();
                        if (deleteResult == false) {
                            continue;

                        }
                        float newOccupy = (totalSpace - file.getFreeSpace())/totalSpace;

                        if (occupy > ConstantBroker.DElETE_DISK_OVER) {

                            continue;

                        }


                        }
                }

            }





        }
    }

}