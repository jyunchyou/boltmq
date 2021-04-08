    package io.openmessaging.consumer.handler;
    import java.io.*;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.Map;
public class  Test {

    public static void main(String[] args) {
        String twoBigFileWay = "D:\\cjnf\\reader_jf_srv\\target\\0407定位6.txt";
        String fileWay = twoBigFileWay;
        File file = new File(fileWay);
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> arrayList = new ArrayList<String>();
        try {
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(new FileInputStream(file),"GBK")));
            String tmp = "";
            while ((tmp = bufferedReader.readLine()) != null){
//                System.out.println(tmp);
                if(tmp.startsWith("EPC电子编码")){
                    stringBuilder.append(tmp);
                    arrayList.add(tmp);
                }
            }
            bufferedReader.close();

        }catch (Exception e){
            e.printStackTrace();
        }
//        System.out.println(stringBuilder.toString());
        String readerNo = "191";
        HashMap<String,ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();
        for (String s : arrayList) {
//            System.out.println(s);//s.length = 58
            String readerNumber = s.substring(47,50);
            if (Character.isDigit(s.charAt(38))){
                readerNumber = s.substring(48,51);
            }
            if(readerNumber.equals(readerNo)){
                String epc = s.substring(8,32) + "-";

                if(Character.isDigit(s.charAt(38))){
                    epc = epc + s.charAt(37) + s.charAt(38);
                }else epc = epc + s.charAt(37);

                int l = s.length();
                String Rssi = s.substring(l-2,l);
                ArrayList<String> rssi = null;
                if(hashMap.containsKey(epc)){
                    rssi = hashMap.get(epc);
                }else rssi = new ArrayList<String>();
                rssi.add(Rssi);
                hashMap.put(epc,rssi);
            }
        }
        for(Map.Entry entry : hashMap.entrySet()){
            String epc = (String) entry.getKey();
            ArrayList<String> rssi = (ArrayList<String>)entry.getValue();
            StringBuilder sb = new StringBuilder();
            sb.append(epc + ":[");
            for(String r : rssi){
                sb.append(r + ",");
            }
            System.out.println(sb.deleteCharAt(sb.length() - 1).toString() + "]");
        }
//        System.out.println(1);
    }
}
