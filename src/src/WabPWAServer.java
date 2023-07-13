package src;

import src.dbconn.JdbcUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static src.Server.userList;


public class WabPWAServer {
    //public JdbcUtils conn = new JdbcUtils();


    public short passFlag = 0;
    //public ServerSocket serverSocket =new ServerSocket(9000);
    private String publicKey="";
    private String userNameHashCode = "";
    public ServerSocket serverSocket = null;
    public int port = 0;
    public WabPWAServer(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
    }

    public String getUserNameHashCode(){
        return userNameHashCode;
    }

    public boolean checkUserName(String userNameHashCode) throws SQLException {
        boolean tmp = false;
        String sql = "select count(*) from login_table where user_hashcode = '" + userNameHashCode + "'";
        Connection conn = JdbcUtils.getConnection();
        Statement stmt=conn.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        if (result.next()&&result.getInt(1)==1){
            System.out.println(result.getInt(1));
            tmp = true;
        }
        return tmp;
    }
    public boolean checkPasswd(String userNameHashCode,String passwdHashCode) throws SQLException {
        boolean tmp = false;
        String sql = "select passwd_hashcode from login_table where user_hashcode = '" + userNameHashCode + "'";
        Connection conn = JdbcUtils.getConnection();
        Statement stmt=conn.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        if (result.next()){
            tmp = result.getString(1).equals(passwdHashCode);
        }
        return tmp;
    }
    public boolean loginCheck(String userNameHashCode,String passwdHashCode) throws SQLException {
        boolean tmp=false;
        if (checkUserName(userNameHashCode)){
            if (checkPasswd(userNameHashCode,passwdHashCode)){
                tmp = true;
            }
        }
        return tmp;
    }


    /*public String readTxt(String userName) {
        String lineTxt="";
        String filePath = "server_Data/"+userName+".txt";

        try {
            File file = new File(filePath);
            if(file.isFile() && file.exists()) {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                lineTxt = br.readLine();

                br.close();
            } else {
                passFlag = 0;

                System.out.println("error username:"+userName);
            }
        } catch (Exception e) {
            System.out.println("error file");
        }
        return lineTxt;

    }*/
    public void run() {

        try {
            System.out.println("PWSystem is ok");
            Socket accept = serverSocket.accept();

            InputStream inputStream = accept.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);//为输入端添加缓冲
            String info="";
            String result="";
            while ((info=bufferedReader.readLine())!=null)
            {
                System.out.println("Client:"+info);
                result=info;
            }

            //4.服务器对客户端进行响应
            OutputStream outputStream =accept.getOutputStream();
            PrintWriter printWriter=new PrintWriter(outputStream);


            userNameHashCode = result.substring(0,result.indexOf("#"));
            String pwa = result.substring(userNameHashCode.length()+1,result.indexOf("##"));//**//
            publicKey = result.substring(userNameHashCode.length()+pwa.length()+3);
            System.out.println(publicKey);
            //String localPW = readTxt(userNameHashCode);

            if (userList.size()>=2){
                passFlag = 3;
            }
            else if(loginCheck(userNameHashCode,pwa))//判断密码是否正确
            {
                passFlag = 1;
            }
            else
            {
                passFlag = 0;
            }

            for (String username:userList){
                if (username.equals(userNameHashCode)&&passFlag!=0) {
                    passFlag = 2;
                    break;
                }
            }

            printWriter.println(String.valueOf(passFlag));
            printWriter.flush();//刷新缓冲区
            accept.shutdownOutput();


            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            outputStream.close();
            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

