package io.openmessaging.table;

import io.openmessaging.Constant.ConstantBroker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumeIndexFileMap {
    private static Map<String, MappedByteBuffer> map = new ConcurrentHashMap();

    public static void main(String[] args) throws IOException {
        File file = new File("D:\\test.txt");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, ConstantBroker.INDEX_FILE_SIZE);
        long l = mappedByteBuffer.getLong();
        }
    public MappedByteBuffer createMappedByteBuffer(String topic,String fileName) throws IOException {

        File file = new File(ConstantBroker.ROOT_INDEX_PATH + topic + "\\" + fileName);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, ConstantBroker.INDEX_FILE_SIZE);
        map.put(topic+fileName,mappedByteBuffer);

        return mappedByteBuffer;
    }

        public MappedByteBuffer getMappedByteBuffer(String topic,String fileName) throws IOException {
        MappedByteBuffer mappedByteBuffer = null;
        if ((mappedByteBuffer = map.get(topic+fileName)) == null) {
            //mmap未初始化
            return createMappedByteBuffer(topic,fileName);
        }
        return mappedByteBuffer;
    }

    public static Map<String, MappedByteBuffer> getMap() {
        return map;
    }

    public static void setMap(Map<String, MappedByteBuffer> map) {
        ConsumeIndexFileMap.map = map;
    }
}
