package src;

import java.net.Socket;


public class publicClass {
    public int id =0;

    public static class pubUserID{
        public String pubKey;
        public String userName;
        public Socket socket;
        public void getPubKey(String key){
            pubKey = key;
        }
        public void  getUserName(String str){
            userName=str;
        }
        public void insertSocket(Socket s){
            socket=s;
        }
    }
    public pubUserID[] pubList = new pubUserID[64];

    public void insert (String key,String name,Socket s){
        pubList[id] = new pubUserID();
        pubList[id].getPubKey(key);
        pubList[id].getUserName(name);
        pubList[id].insertSocket(s);
        id++;
    }

    public String getUSerName(int p){
        return pubList[p].userName;
    }

    public String getPublicKey(int p){
        return pubList[p].pubKey;
    }

    public Socket getSocket(int p){return pubList[p].socket;}


}
