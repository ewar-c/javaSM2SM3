package src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

import static src.SM2.UseSM2.decryptStr;
import static src.SM2.UseSM2.encryptStr;

/**
 * 负责客户端的读和写，以及登录和发送的监听
 * 之所以把登录和发送的监听放在这里，是因为要共享一些数据，比如mySocket,textArea
 */
public class ClientReadAndPrint extends Thread {
    static Socket mySocket = null;
    static JTextField textInput;
    static JTextArea textShow;
    static JFrame chatViewJFrame;
    static BufferedReader in = null;
    static PrintWriter out = null;
    static String userName;
    static String address = "127.0.0.1";
    public static WabPWAClient pass = new WabPWAClient();
    public static String otherPubKey = "";


    // 用于接收从服务端发送来的消息
    public void run() {

        try {
            in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));  // 输入流
            //输入流

            while (true) {

                String str = in.readLine();  // 获取服务端发送的信
                //System.out.println(str);
                if (str.equals("")) {
                    continue;
                }
                if ("pubKey".equals(str.substring(0, 6))) {
                    otherPubKey = str.substring(6);
                    textShow.append("收到对方公钥\n");  // 添加进聊天客户端的文本区域
                    textShow.setCaretPosition(textShow.getDocument().getLength());
                    System.out.println("获得对方公钥:" + otherPubKey);
                } else if ("plsPubKey".equals(str) && Objects.equals(otherPubKey, "")) {
                    textShow.append("对方请求公钥\n");  // 添加进聊天客户端的文本区域
                    textShow.setCaretPosition(textShow.getDocument().getLength());
                } else {
                    System.out.println("收到密文：" + str);
                    String srcText = decryptStr(str, pass.getPrivateKey());
                    if (srcText.equals("ClsPub")) {
                        otherPubKey = "";
                        textShow.append("对方已离开\n");
                        textShow.setCaretPosition(textShow.getDocument().getLength());
                    } else {
                        textShow.append(srcText + '\n');  // 添加进聊天客户端的文本区域
                        textShow.setCaretPosition(textShow.getDocument().getLength());  // 设置滚动条在最下面
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**********************登录监听(内部类)**********************/
    static class LoginListen implements ActionListener {
        JTextField textField;
        JPasswordField pwdField;
        JFrame loginJFrame;  // 登录窗口本身

        ChatView chatView = null;

        public void setJTextField(JTextField textField) {
            this.textField = textField;
        }

        public void setJPasswordField(JPasswordField pwdField) {
            this.pwdField = pwdField;
        }

        public void setJFrame(JFrame jFrame) {
            this.loginJFrame = jFrame;
        }

        public void actionPerformed(ActionEvent event) {
            userName = textField.getText();

            String SM3UserPsd = SM3.encrypt(String.valueOf(pwdField.getPassword()));//用SM3加密密码
            String SM3UserName = SM3.encrypt(String.valueOf(userName));//用SM3加密密码
            pass.run(SM3UserName, SM3UserPsd, address);
            //认证

            if (pass.passFlag == 1) {

                chatView = new ChatView(userName);  // 新建聊天窗口,设置聊天窗口的用户名（静态）
                // 建立和服务器的联系
                try {
                    // 获取主机地址
                    mySocket = new Socket(address, 9000);  // 客户端套接字
                    loginJFrame.setVisible(false);  // 隐藏登录窗口
                    out = new PrintWriter(mySocket.getOutputStream());  // 输出流

                    if (Objects.equals(otherPubKey, "")) {
                        System.out.println(userName + "等待公钥");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 新建普通读写线程并启动
                ClientReadAndPrint readAndPrint = new ClientReadAndPrint();
                readAndPrint.start();
                // 新建文件读写线程并启动
                ClientFileThread fileThread = new ClientFileThread(userName, chatViewJFrame, out);
                fileThread.start();
            } else if (pass.passFlag == 2) {
                JOptionPane.showMessageDialog(loginJFrame, "账号已在其它位置登录！", "提示", JOptionPane.WARNING_MESSAGE);
            } else if (pass.passFlag == 0) {
                JOptionPane.showMessageDialog(loginJFrame, "账号或密码错误，请重新输入！", "提示", JOptionPane.WARNING_MESSAGE);
            } else if (pass.passFlag == 3) {
                JOptionPane.showMessageDialog(loginJFrame, "用户已满，禁止加入！", "提示", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(loginJFrame, "未知错误！", "提示", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**********************聊天界面监听(内部类)**********************/
    static class PubSendListen implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String pubKey = "pubKey" + pass.getPublicKey();
            System.out.println("我的公钥:" + pass.getPublicKey());
            out.println(pubKey);
            out.flush();
        }
    }

    static class ChatViewListen implements ActionListener {
        public void setJTextField(JTextField text) {
            textInput = text;  // 放在外部类，因为其它地方也要用到
        }

        public void setJTextArea(JTextArea textArea) {
            textShow = textArea;  // 放在外部类，因为其它地方也要用到
        }

        public void setChatViewJf(JFrame jFrame) {
            chatViewJFrame = jFrame;  // 放在外部类，因为其它地方也要用到
            // 设置关闭聊天界面的监听
            chatViewJFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {

                    if (!Objects.equals(otherPubKey, "")) {
                        String tipsMSG = "ClsPub";
                        out.println(encryptStr(tipsMSG, otherPubKey));
                        out.flush();
                    }

                    System.exit(0);
                }
            });
        }

        // 监听执行函数
        public void actionPerformed(ActionEvent event) {
            try {

                String str = textInput.getText();//获取键盘内容
                // 文本框内容为空
                if ("".equals(str)) {
                    textInput.grabFocus();  // 设置焦点（可行）
                    // 弹出消息对话框（警告消息）
                    JOptionPane.showMessageDialog(chatViewJFrame, "输入为空，请重新输入！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!Objects.equals(otherPubKey, "")) {
                    String outStr = userName + ":" + str;
                    textShow.append(outStr + '\n');  // 添加进聊天客户端的文本区域
                    textShow.setCaretPosition(textShow.getDocument().getLength());
                    ///System.out.println(outStr);
                    String sendStr = encryptStr(outStr, otherPubKey);

                    out.println(sendStr);  // 输出给服务端
                    out.flush();  // 清空缓冲区out中的数据

                    textInput.setText("");  // 清空文本框
                    textInput.grabFocus();  // 设置焦点（可行）
                } else {
                    String tipsMSG = "无对面公钥，无法加密，等待对方公钥";
                    textShow.append(tipsMSG + '\n');  // 添加进聊天客户端的文本区域
                    textShow.setCaretPosition(textShow.getDocument().getLength());  // 设置滚动条在最下面
                    tipsMSG = "plsPubKey\n";
                    out.println(tipsMSG);
                    out.flush();

                }

            } catch (Exception ignored) {
            }
        }
    }
}
