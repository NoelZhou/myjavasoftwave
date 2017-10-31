package com.zt.frame;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.zt.common.FileDiretory;
import com.zt.common.GlobalUntils;
import com.zt.thread.THD_ONE;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	String ipAddr;
	String com;
	private THD_ONE tOne;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public LoginFrame() {
		System.setProperty("java.net.preferIPv4Stack", "true");// win7以上启用ipv4
		readDeviceInfo();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 150, 300, 200);
		setLocationRelativeTo(null);// 居中显示
		setIconImage(new ImageIcon(FileDiretory.getCurrentDir() + "/images/SC.png").getImage());
		setTitle("电网模拟器");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		textField = new JTextField();
		textField.setBounds(106, 43, 140, 21);
		textField.setText(ipAddr);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(106, 79, 140, 21);
		textField_1.setText(com);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		JButton button = new JButton("登陆");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ipAddr = textField.getText();
				com = textField_1.getText();
				writeDeviceInfo(ipAddr,com);
				TheWholeFrame theWholeFrame = new TheWholeFrame();
				tOne = new THD_ONE();
				tOne.setIpAndCom(ipAddr, com);
				tOne.setModbusTcp(1);
				tOne.setTheWholwFrame(theWholeFrame);
				tOne.start();
				setVisible(false);
				theWholeFrame.setVisible(true);
			}
		});

		button.setBounds(111, 126, 93, 23);
		contentPane.add(button);

		JLabel lblIp = new JLabel("IP地址：");
		lblIp.setBounds(26, 46, 70, 15);
		contentPane.add(lblIp);

		JLabel label = new JLabel("端口号：");
		label.setBounds(26, 82, 70, 15);
		contentPane.add(label);
	}
	protected void writeDeviceInfo(String ipAddr2, String com2) {
		try {
			File file = new File(GlobalUntils.PATH_DEVICE_INFO);
			OutputStream fos = new FileOutputStream(file);
			String string = "deviceIp="+ipAddr2+"\nport="+com2;
			byte[] b = string.getBytes();
			fos.write(b);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readDeviceInfo() {
		try {
			File file = new File(GlobalUntils.PATH_DEVICE_INFO);
			InputStream fis = new FileInputStream(file);
			int len = fis.available();
			byte[] b = new byte[len];
			fis.read(b);
			fis.close();
			String[] strings = new String(b).split("\n");
			ipAddr = strings[0].split("=")[1];
			com = strings[1].split("=")[1];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
