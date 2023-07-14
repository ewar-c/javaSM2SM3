package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Register {
    static public class RegisterView {

        JTextField userName = null;
        JPasswordField passwd0 = null;
        JPasswordField passwd1 = null;

        public RegisterView() {
            init();
        }

        void init() {
            JFrame jf = new JFrame("Register");
            jf.setBounds(500, 200, 310, 330);  //设置坐标和大小
            jf.setResizable(false);

            JPanel jp1 = new JPanel();
            JLabel headJLabel = new JLabel("Register Interface");
            headJLabel.setFont(new Font(null, Font.PLAIN, 35));  // 设置文本的字体类型、样式 和 大小
            jp1.add(headJLabel);


            JPanel jp2 = new JPanel();
            JLabel nameJLabel = new JLabel("USERNAME: ");
            userName = new JTextField(20);
            JLabel pwdJLabel0 = new JLabel("PASSWORD: ");
            passwd0 = new JPasswordField(20);
            JLabel pwdJLabel1 = new JLabel("PASSWORD: ");
            passwd1 = new JPasswordField(20);


            jp2.add(nameJLabel);
            jp2.add(userName);
            jp2.add(pwdJLabel0);
            jp2.add(passwd0);
            jp2.add(pwdJLabel1);
            jp2.add(passwd1);

            JButton registerButton = new JButton("Register");
            jp2.add(registerButton);
            RegisterButtonListen registerButtonListen = new RegisterButtonListen();
            registerButtonListen.setUseName(userName);
            registerButtonListen.setPasswd(passwd0, passwd1);
            registerButtonListen.setJFrame(jf);
            registerButton.addActionListener(registerButtonListen);


            JPanel jp = new JPanel(new BorderLayout());
            jp.add(jp1, BorderLayout.NORTH);
            jp.add(jp2, BorderLayout.CENTER);

            jf.add(jp);
            //jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jf.setVisible(true);
        }

    }

    static class RegisterListen implements ActionListener {
        RegisterView registerView = null;


        @Override
        public void actionPerformed(ActionEvent e) {
            registerView = new RegisterView();
            //System.out.println('r');
        }
    }

    static class RegisterButtonListen implements ActionListener {

        JTextField useName;
        JPasswordField passwd0;
        JPasswordField passwd1;
        JFrame registerJFrame;

        public void setUseName(JTextField useName) {
            this.useName = useName;
        }

        public void setPasswd(JPasswordField passwd0, JPasswordField passwd1) {
            this.passwd0 = passwd0;
            this.passwd1 = passwd1;
        }

        public void setJFrame(JFrame jFrame) {
            this.registerJFrame = jFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (!Arrays.equals(passwd0.getPassword(), passwd1.getPassword())) {
                JOptionPane.showMessageDialog(registerJFrame, "两次密码不同！", "提示", JOptionPane.WARNING_MESSAGE);
            } else {
                String srcUserName = useName.getText();
                String SM3UserName = SM3.encrypt(String.valueOf(useName.getText()));
                String SM3PassWd = SM3.encrypt(String.valueOf(passwd0.getPassword()));


                JOptionPane.showMessageDialog(registerJFrame, "注册成功！", "提示", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
