package src;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
	static ServerSocket server = null;
	static Socket socket = null;
	static List<Socket> list = new ArrayList<Socket>();  // 存储客户端
	//static publicClass keyList = new publicClass();
	public static List<String> userList = new ArrayList<>();
	static int port = 9000;

	
	public static void main(String[] args) {
		MultiChat multiChat = new MultiChat();  // 新建聊天系统界面


		try {
			// 在服务器端对客户端开启文件传输的线程
			ServerFileThread serverFileThread = new ServerFileThread();
			serverFileThread.start();


			WabPWAServer pass = new WabPWAServer(port);
			server = pass.serverSocket;  // 服务器端套接字（只能建立一次）
			// 等待连接并开启相应线程

			while (true) {
				pass.run();

				if (pass.passFlag==1) {
					socket = server.accept();  // 等待连接
					list.add(socket);  // 添加当前客户端到列表
					userList.add(pass.getUserNameHashCode());
					//keyList.insert(pass.getPublicKey(), pass.getUserName(),socket);
					//keyList.insertSocket(socket);//
					// 在服务器端对客户端开启相应的线程
					ServerReadAndPrint readAndPrint = new ServerReadAndPrint(socket, pass.getUserNameHashCode(),multiChat);
					readAndPrint.start();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();  // 出现异常则打印出异常的位置
		}
	}
}

/**
 *  服务器端读写类线程
 *  用于服务器端读取客户端的信息，并把信息发送给所有客户端
 */
class ServerReadAndPrint extends Thread{
	Socket nowSocket = null;
	String userName =null;
	MultiChat multiChat = null;
	BufferedReader in =null;
	PrintWriter out = null;
	// 构造函数
	public ServerReadAndPrint(Socket s,String userName, MultiChat multiChat) {
		this.multiChat = multiChat;  // 获取多人聊天系统界面
		this.userName= userName;
		this.nowSocket = s;  // 获取当前客户端
	}
	
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(nowSocket.getInputStream()));  // 输入流
			// 获取客户端信息并把信息发送给所有客户端
			while (true) {
				String str = in.readLine();
				if(str.equals(""))
					continue;
				for(Socket socket: Server.list) {
					out = new PrintWriter(socket.getOutputStream());  // 对每个客户端新建相应的socket套接字
					if(socket == nowSocket) {  // 发送给当前客户端
						continue;
					}
					else {  // 发送给其它客户端
						System.out.println("forward:"+str);
						out.println(str);
					}
					out.flush();  // 清空out中的缓存
				}
				// 调用自定义函数输出到图形界面+
				multiChat.setTextArea(str);
			}
		} catch (Exception e) {
			Server.list.remove(nowSocket);  // 线程关闭，移除相应套接字
			Server.userList.remove(userName);
		}
	}
}
