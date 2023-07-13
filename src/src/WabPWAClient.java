package src;

import src.SM2.UseSM2;

import java.io.*;
import java.net.Socket;


public class WabPWAClient {
    public short passFlag=0;
    public UseSM2 SM2 = new UseSM2();

    public String getPublicKey(){
        return SM2.outPutPubKey();
    }

    public String getPrivateKey(){
        return  SM2.outPutPriKey();
    }

    public void run(String userNameHashCode,String PWAHashcode,String address) {

        String up =userNameHashCode + "#" +PWAHashcode+"##"+ SM2.outPutPubKey();
        System.out.println(up);


        try {
            Socket socket=new Socket(address,9000);

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter=new PrintWriter(outputStream);
            printWriter.println(up);
            printWriter.flush();
            socket.shutdownOutput();


            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);//为输入端添加缓冲
            String info;

            while ((info=bufferedReader.readLine())!=null) {
                passFlag = Short.parseShort(info);
                System.out.println("server:   " + passFlag);

            }
            //关闭资源
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
            printWriter.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

