package src;

import src.SM2.FileSM2;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static src.ClientReadAndPrint.*;
import static src.FileUtils.fileToByte;
import static src.SM2.UseSM2.encryptStr;

public class ClientFileThread extends Thread{
	//public static FileSM2 fileSM2 = new FileSM2();
	private Socket socket = null;
	private JFrame chatViewJFrame = null;
	static String userName = null;
	static PrintWriter out = null;  // 普通消息的发送（Server.java传来的值）
	static DataInputStream fileIn = null;
	static DataOutputStream fileOut = null;
	static DataInputStream fileReader = null;
	static DataOutputStream fileWriter = null;
	
	public ClientFileThread(String userName, JFrame chatViewJFrame, PrintWriter out) {
		ClientFileThread.userName = userName;
		this.chatViewJFrame = chatViewJFrame;
		ClientFileThread.out = out;
	}
	
	// 客户端接收文件
	public void run() {
		try {
			InetAddress addr = InetAddress.getByName(null);  // 获取主机地址
			socket = new Socket(addr, 9002);  // 客户端套接字
			fileIn = new DataInputStream(socket.getInputStream());  // 输入流
			fileOut = new DataOutputStream(socket.getOutputStream());  // 输出流
			// 接收文件
			while(true) {
				String textName = fileIn.readUTF();
				long totleLength = fileIn.readLong();
				int result = JOptionPane.showConfirmDialog(chatViewJFrame, "是否接受？"+textName, "提示", JOptionPane.YES_NO_OPTION);
				int length = -1;
				byte[] buff = new byte[1024];
				long curLength = 0;
				// 提示框选择结果，0为确定，1位取消
				if(result == 0){
					System.out.println("【" + userName + "选择了接收文件！】");
					File userFile = new File("Client_DATA/" + userName);
					if(!userFile.exists()) {  // 新建当前用户的文件夹
						userFile.mkdirs();
					}
					File file = new File("Client_DATA/" + userName + "/"+ textName);
					fileWriter = new DataOutputStream(new FileOutputStream(file));
					while((length = fileIn.read(buff)) > 0) {  // 把文件写进本地
						fileWriter.write(buff, 0, length);
						fileWriter.flush();
						curLength += length;
						//out.println("【接收进度:" + curLength/totleLength*100 + "%】");
						//out.flush();
						if(curLength == totleLength) {  // 强制结束
							break;
						}
					}
					String unlockFileName = textName.substring(0, textName.length() - 8);
					FileSM2.unlockFile(pass.getPrivateKey(),"Client_DATA/" + userName +"/"+ textName,"Client_DATA/" + userName, unlockFileName);
					out.println(encryptStr("【" + userName + "接收了文件！】",otherPubKey));
					System.out.println("【" + userName + "接收了文件！】");
					//out.println("【" + userName + "接收了文件！】");
					out.flush();
					// 提示文件存放地址
					JOptionPane.showMessageDialog(chatViewJFrame, "文件存放地址：\n" +
									"Client_DATA/" +
							userName + "/" + unlockFileName, "提示", JOptionPane.INFORMATION_MESSAGE);
				}
				else {  // 不接受文件
					while((length = fileIn.read(buff)) > 0) {
						curLength += length;
						if(curLength == totleLength) {  // 强制结束
							break;
						}
					}
				}
				fileWriter.close();
			}
		} catch (Exception e) {}
	}
	
	// 客户端发送文件
	static void outFileToServer(String path) {
		if (otherPubKey.equals("")) {
			String tipsMSG = "无对面公钥，无法加密，等待对方公钥";
			textShow.append(tipsMSG + '\n');  // 添加进聊天客户端的文本区域
			textShow.setCaretPosition(textShow.getDocument().getLength());  // 设置滚动条在最下面
			tipsMSG = "plsPubKey\n";
			out.println(tipsMSG);
			out.flush();
		} else {
			try {
				System.out.println(path + "\n" + userName);
				File unlockFile = new File(path);
				FileSM2.lockFile(otherPubKey, fileToByte(path), "Client_DATA/" + userName, unlockFile.getName() + ".lockSM2");

				File lockFile = new File("Client_DATA/" + userName + "/" + unlockFile.getName() + ".lockSM2");
				fileReader = new DataInputStream(new FileInputStream(lockFile));
				fileOut.writeUTF(lockFile.getName());  // 发送文件名字
				fileOut.flush();
				fileOut.writeLong(lockFile.length());  // 发送文件长度
				fileOut.flush();
				int length = -1;
				byte[] buff = new byte[1024];
				while ((length = fileReader.read(buff)) > 0) {  // 发送内容

					fileOut.write(buff, 0, length);
					fileOut.flush();
				}

				out.println(encryptStr("【" + userName + "已成功发送文件！】", otherPubKey));
				//out.println("【" + userName + "已成功发送文件！】");
				out.flush();
			} catch (Exception e) {
			}
		}
	}
}
