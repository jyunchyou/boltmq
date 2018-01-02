package io.openmessaging.client.compress;


import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressOfDeflater {



    public static byte[] compress(byte[] inputByte) throws IOException {
        int len = 0;
        Deflater defl = new Deflater();
        defl.setLevel(1);//快速压缩
        defl.setInput(inputByte);
        defl.finish();
        byte[] outputByte = new byte[1024];

        while (!defl.finished()) {
            // 压缩并将压缩后的内容输出到字节输出流bos中
            len = defl.deflate(outputByte);
        }
        defl.end();

        return outputByte;
    }


    public static byte[] uncompress(byte[] inputByte) throws IOException {
        int len = 0;
        Inflater infl = new Inflater();
        infl.setInput(inputByte);

        byte[] outByte = new byte[1024];

            while (!infl.finished()) {
                // 解压缩并将解压缩后的内容输出到字节输出流bos中
                try {
                    len = infl.inflate(outByte);
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }
                if (len == 0) {
                    break;
                }

            }
            infl.end();

        return outByte;
    }
}
