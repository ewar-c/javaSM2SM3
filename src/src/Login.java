package src;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Login {
	JTextField textField = null;

	JPasswordField pwdField = null;

	ClientReadAndPrint.LoginListen listener=null;
	Register.RegisterListen registerListen = null;
	// 构造函数
	public Login() {
		init();
	}

	void init() {
		JFrame jf = new JFrame("Login");
		jf.setBounds(500, 250, 310, 230);
		jf.setResizable(false);  // 设置是否缩放

		JPanel jp1 = new JPanel();
		JLabel headJLabel = new JLabel("Login Interface");
		headJLabel.setFont(new Font(null, Font.PLAIN, 35));  // 设置文本的字体类型、样式 和 大小
		jp1.add(headJLabel);

		JPanel jp2 = new JPanel();
		JLabel nameJLabel = new JLabel("USERNAME: ");
		textField = new JTextField(20);
		JLabel pwdJLabel = new JLabel("PASSWORD: ");
		pwdField = new JPasswordField(20);
		JButton loginButton = new JButton("Login");
		jp2.add(nameJLabel);
		jp2.add(textField);
		jp2.add(pwdJLabel);
		jp2.add(pwdField);
		jp2.add(loginButton);

		JButton registerButton = new JButton("Register");
		jp2.add(registerButton);
		registerListen = new Register.RegisterListen();
		registerButton.addActionListener(registerListen);

		JPanel jp = new JPanel(new BorderLayout());  // BorderLayout布局
		jp.add(jp1, BorderLayout.NORTH);
		jp.add(jp2, BorderLayout.CENTER);

		// 设置监控
		listener = new ClientReadAndPrint.LoginListen();  // 新建监听类
		listener.setJTextField(textField);  // 调用PoliceListen类的方法
		listener.setJPasswordField(pwdField);
		listener.setJFrame(jf);
		pwdField.addActionListener(listener);  // 密码框添加监听
		loginButton.addActionListener(listener);  // 按钮添加监听

		jf.add(jp);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 设置关闭图标作用
		jf.setVisible(true);  // 设置可见
	}
}

