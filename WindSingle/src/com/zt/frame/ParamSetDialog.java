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

import com.zt.common.FileDiretory;
import com.zt.common.SunModbusTcpbyIp;
import com.zt.pojo.SaveMessage;

public class ParamSetDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private String datalimitmax;
	private String datalimitmin;
	private String unit;
	private String value;
	private String addrName;
	private int addr;
	private String codeStr;
	private JTextField valueField;
	private int num;
	private SaveMessage saveMessage = SaveMessage.getInstance();

	public ParamSetDialog(String codeStr, int num, int addr, String addrName, String value, String unit, String datalimitmin,
			String datalimitmax) {
		setValues(codeStr,num,addr, addrName, value, unit, datalimitmin, datalimitmax);
		
		setTitle("参数设置");
		setLayout(null);
		setIconImage(new ImageIcon(FileDiretory.getCurrentDir()+"/images/Set.png").getImage());
	    // 关闭窗体后退出程序
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    // 自动适配所有控件大小
		setSize(450, 250);
		setLocationRelativeTo(null);//居中
		setAlwaysOnTop(true);
		createUI();
	}
	
	public void createUI(){
		add(createJLabelItem(addrName, 20, 20, 100, 20));
		add(createJLabelItem(unit, 235, 65, 100, 20));
		add(createJLabelItem("参数值:", 40, 65, 100, 20));
		add(createJLabelItem("参数范围:", 40, 105, 100, 20));
		
		valueField = (JTextField) add(createJTextField(value, 0, 130, 60, 100, 30));
		add(createJTextField(datalimitmin+"~"+datalimitmax, 1, 130, 100, 270, 30));
		
		add(createJButtonItem("设置", 130, 160, 70, 30));
		add(createJButtonItem("退出", 250, 160, 70, 30));
	}
	
	public JButton createJButtonItem(String text,int x,int y, int width,int height){
		JButton button = new JButton(text);
		button.setBounds(x, y, width, height);
		button.setActionCommand(text);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("设置")){
					String[] codes = codeStr.split(",");
					String[] ipCom = saveMessage.getIpAndCom();
					int modbusType = saveMessage.getModbustcp();
					if(codes[0].equals("网侧FLASH参数")||codes[0].equals("机侧FLASH参数")){
						SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ipCom[0], Integer.parseInt(ipCom[1]), modbusType, "1",
								String.valueOf(num));
						SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ipCom[0], Integer.parseInt(ipCom[1]), modbusType, "2",
								valueField.getText());
					}else {
						SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ipCom[0], Integer.parseInt(ipCom[1]), modbusType,String.valueOf(addr),
								valueField.getText());
					}
					SunModbusTcpbyIp.WriteSunModbusTcpStrAll(ipCom[0], Integer.parseInt(ipCom[1]), modbusType, "0000",
							codes[1]);
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

	public void setValues(String codeStr, int num, int addr, String addrName, String value, String unit, String datalimitmin,
			String datalimitmax) {
		this.codeStr=codeStr;
		this.num = num;
		this.addr = addr;
		this.addrName = addrName;
		this.value = value;
		this.unit = unit;
		this.datalimitmin=datalimitmin;
		this.datalimitmax = datalimitmax;
	}
}


















