package com.zt.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.zt.common.FileDiretory;
import com.zt.common.SunModbusTcpbyIp;
import com.zt.pojo.SaveMessage;

public class ParamSetDialogAll extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField valueField1;
	private JTextField valueField2;
	private JTextField valueField3;
	private JTextField valueField4;
	private JTextField valueField5;
	private JTextField valueField6;
	private SaveMessage saveMessage = SaveMessage.getInstance();

	public ParamSetDialogAll(String setType) {
		setTitle("参数设置");
		setLayout(null);
		setIconImage(new ImageIcon(FileDiretory.getCurrentDir()+"/images/Set.png").getImage());
	    // 关闭窗体后退出程序
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    // 自动适配所有控件大小
		setSize(450, 250);
		setLocationRelativeTo(null);//居中
		setAlwaysOnTop(true);
		if(setType.equals("IP")){
			createIP();
		}
		if(setType.equals("RTC")){
			createRTC();
		}
		
	}
	public void createIP(){
		add(createJLabelItem("IP:", 0, 60, 60, 30));
		
		valueField1 = (JTextField) add(createJTextField("0", 0, 50, 60, 80, 30));
		add(createJLabelItem(".", 130, 60, 10, 30));
		valueField2 = (JTextField) add(createJTextField("0", 0, 140, 60, 80, 30));
		add(createJLabelItem(".", 220, 60, 10, 30));
		valueField3 = (JTextField) add(createJTextField("0", 0, 230, 60, 80, 30));
		add(createJLabelItem(".", 310, 60, 10, 30));
		valueField4 = (JTextField) add(createJTextField("0", 0, 320, 60, 80, 30));
		
		add(createJButtonItem("设置","419", 130, 160, 70, 30));
		add(createJButtonItem("退出",null, 250, 160, 70, 30));
	}
	public void createRTC(){
		add(createJLabelItem("年月日:", 20, 40, 100, 30));
		add(createJLabelItem("时分秒:", 20, 80, 100, 30));
		
		valueField1 = (JTextField) add(createJTextField("0", 0, 100, 40, 80, 30));
		add(createJLabelItem("-", 180, 40, 20, 30));
		valueField2 = (JTextField) add(createJTextField("0", 0, 200, 40, 80, 30));
		add(createJLabelItem("-", 280, 40, 20, 30));
		valueField3 = (JTextField) add(createJTextField("0", 0, 300, 40, 80, 30));
		
		valueField4 = (JTextField) add(createJTextField("0", 0, 100, 80, 80, 30));
		add(createJLabelItem(":", 180, 80, 20, 30));
		valueField5 = (JTextField) add(createJTextField("0", 0, 200, 80, 80, 30));
		add(createJLabelItem(":", 280, 80, 20, 30));
		valueField6 = (JTextField) add(createJTextField("0", 0, 300, 80, 80, 30));
		
		add(createJButtonItem("设置","51", 130, 160, 70, 30));
		add(createJButtonItem("退出",null, 250, 160, 70, 30));
	}
	
	public JButton createJButtonItem(String text,final String defaultAddr,int x,int y, int width,int height){
		JButton button = new JButton(text);
		button.setBounds(x, y, width, height);
		button.setActionCommand(text);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("设置")){
					String[] ipCom = saveMessage.getIpAndCom();
					String valueField = valueField1.getText()+","+valueField2.getText()+","+valueField3.getText()+","+
										valueField4.getText()+","+valueField5.getText()+","+valueField6.getText();
					int modbusType = saveMessage.getModbustcp();
						SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ipCom[0], Integer.parseInt(ipCom[1]), modbusType,defaultAddr,
								valueField);
						SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ipCom[0], Integer.parseInt(ipCom[1]), modbusType,"0000",
								"8DH");
					dispose();//只关闭当前窗口
				}
				if(e.getActionCommand().equals("退出")){
					dispose();//只关闭当前窗口
				}
			}
		});
		return button;
	}
	
	public JLabel createJLabelItem(String text,int x,int y, int width,int height){
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(x, y, width, height);
		return label;
	}
	
	public JTextField createJTextField(String text,int isEdit,int x,int y, int width,int height){
		JTextField field = new JTextField();
		field.setText(text);
		field.setBounds(x, y, width, height);
		field.setBorder(BorderFactory.createLoweredBevelBorder());
		if(isEdit==1){
			field.setEditable(false);
		}
		return field;
	}

}


















