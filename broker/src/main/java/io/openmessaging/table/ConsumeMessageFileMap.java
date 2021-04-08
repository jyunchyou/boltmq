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

public class ConsumeMessageFileMap {
    private static Map<String, MappedByteBuffer> map = new ConcurrentHashMap();

    public MappedByteBuffer createMappedByteBuffer(String fileName) throws IOException {

        File file = new File(ConstantBroker.ROOT_PATH + fileName);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, ConstantBroker.FILE_SIZE);
        map.put(fileName,mappedByteBuffer);

        return mappedByteBuffer;
    }

    public MappedByteBuffer getMappedByteBuffer(String fileName) throws IOException {
        MappedByteBuffer mappedByteBuffer = null;
        if ((mappedByteBuffer = map.get(fileName)) == null) {
            //mmap未初始化
            return createMappedByteBuffer(fileName);
        }
        return mappedByteBuffer;
    }

    public static Map<String, MappedByteBuffer> getMap() {
        return map;
    }


}
