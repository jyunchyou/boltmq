import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Main {
   /* *//**
     *
     * @param inputByte
     *      待解压缩的字节数组
     * @return 解压缩后的字节数组
     * @throws IOException
     *//*
    public static byte[] uncompress(byte[] inputByte) throws IOException {
        int len = 0;
        Inflater infl = new Inflater();
        infl.setInput(inputByte);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] outByte = new byte[1024];
        try {
            while (!infl.finished()) {
                // 解压缩并将解压缩后的内容输出到字节输出流bos中
                len = infl.inflate(outByte);
                if (len == 0) {
                    break;
                }
                bos.write(outByte, 0, len);
            }
            infl.end();
        } catch (Exception e) {
            //
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    *//**
     * 压缩.
     *
     * @param
     *
     * @return 压缩后的数据
     * @throws IOException
     *//*
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

    public static void main(String[] args) {

        String string = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        byte[] b = string.getBytes();
        System.out.println(b.length);
        try {
            byte[] resultBytes  = compress(b);

            System.out.println(resultBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

*//*        try {
            FileInputStream fis = new FileInputStream("D:\\testdeflate.txt");
            int len = fis.available();
            byte[] b = new byte[len];
            fis.read(b);
            byte[] bd = compress(b);
            // 为了压缩后的内容能够在网络上传输，一般采用Base64编码

            byte[] bi = uncompress(bd);
            FileOutputStream fos = new FileOutputStream("D:\\testinflate.txt");
            fos.write(bi);
            fos.flush();
            fos.close();
            fis.close();
        } catch (Exception e) {
            //
        }*//*
    }*/


    public static void main(String[] args){

        List list = new ArrayList();
        System.out.println(list.size());
        list.add("a");
        list.add("b");
        list.add("c");
        System.out.println(list.size());

        list.remove(1);
        System.out.println(list.size());
        System.out.println(list.get(1));



    }
}


