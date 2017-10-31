package com.zt.panel.Debug;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.zt.common.GlobalUntils;
import com.zt.common.ReadModbusXml;
import com.zt.common.SunModbusTcpbyIp;
import com.zt.pojo.ControlModelParam;
import com.zt.pojo.ModbusBit;
import com.zt.pojo.SaveMessage;

public class ControlPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private SaveMessage saveMessage = SaveMessage.getInstance();
	private int width;
	@SuppressWarnings("unused")
	private int height;
	private JPanel panel1;
	private JPanel panel2;
	public ControlPanel(int width, int height) {
		this.width=width;
		this.height=height;
		createUI();
	}

	public void createUI(){
		setLayout(null);
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createTitledBorder("功能模式设置"));
		panel1.setLayout(new FlowLayout(0,10,10));
		panel1.setBounds(0, 0, width-20, 250);
		functionModel(panel1);
		
		panel2 = new JPanel();		
		panel2.setBounds(0, 250, width-20, 120);
		panel2.setBorder(BorderFactory.createTitledBorder("运行模式设置"));
		panel2.setLayout(new FlowLayout(1,20,10));
		runModel(panel2);
		add(panel1);
		add(panel2);
	}
	
	public void functionModel(JPanel panel){
		panel.removeAll();
		List<Object> objects=new ReadModbusXml().setLists(ControlModelParam.class, GlobalUntils.PATH_CONTROL_MODEL);
		String values = saveMessage.getStrValue();
		List<ModbusBit> lBits = saveMessage.getModbusBits();
		String[] strings = values.split(",");
		for (Object object : objects) {
			ControlModelParam controlModelParam = (ControlModelParam) object;
			if(controlModelParam.getModbus_type()==SaveMessage.getInstance().getModbustcp()&&controlModelParam.getModel_type()==1){
				String[] bits = controlModelParam.getBit_id().split("\\.");
				String bitValue = strings[Integer.parseInt(bits[0])];
				panel.add(createButton(controlModelParam.getName(),controlModelParam.getCode()));
				panel.add(createLabel(getBitVarName(lBits,Integer.parseInt(bitValue),bits), getBitVarName(lBits,Integer.parseInt(bitValue),bits)));
			}
		}
		panel.updateUI();
		panel.invalidate();
	}
	
	private String  getBitVarName(List<ModbusBit> lBits, int bitValue, String[] bits) {
		String binaryStr = getstatebitlist(bitValue);
		String addr = bits[0];
		int bitNum = Integer.parseInt(bits[1]);
		int state = Integer.parseInt((binaryStr.substring(15-bitNum, 16-bitNum)));
		for (ModbusBit modbusBit : lBits) {
			String addrString =modbusBit.getAddr();
			if(addrString!=null){
				if(addrString.equals(addr)&&modbusBit.getBit_id()==bitNum&&state==0){
					return modbusBit.getVar0();
				}
				if(addrString.equals(addr)&&modbusBit.getBit_id()==bitNum&&state==1) {
					return modbusBit.getVar1();
				}
			}
		}
		return "";
	}
	public void runModel(JPanel panel){
		panel.add(createLabel("开环模式","11H"));
		panel.add(createLabel("闭环模式","22H"));
		panel.add(createLabel("前馈模式","33H"));
		panel.add(createLabel("整流并网模式","44H"));
		panel.add(createLabel("风场运行模式","77H"));
		panel.add(createButton("模式选择切换","007EH"));
		panel.add(createButton("模式选择确认","007AH"));
	}
	
	public JLabel createLabel(String text,String order){
		JLabel label = new JLabel(text,JLabel.CENTER);
		label.setToolTipText(order);
		label.setBackground(Color.white);
		label.setFont(new Font("宋体", Font.PLAIN, 12));
		label.setBorder(BorderFactory.createLineBorder(Color.gray));
		label.setPreferredSize(new Dimension(100,40));
		label.setOpaque(true);
		return label ;
	}
	public JButton createButton(String text,String code){
		JButton button = new JButton(text);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setPreferredSize(new Dimension(120, 40));
		button.setToolTipText(code);
		button.setActionCommand(code);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] strings = saveMessage.getIpAndCom();
				SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]), saveMessage.getModbustcp(), "0000", e.getActionCommand());
			}
		});
		return button;
	}
	
	public void sendRunModelState(int num){
		int len = panel2.getComponentCount();
		for(int i=0;i<len;i++){
			Component comp = panel2.getComponent(i);
			if(comp instanceof JLabel){
				JLabel label = (JLabel)comp;
				String value = label.getToolTipText();
				int decValue = 0;
				if(value.indexOf("H")!=-1){
					decValue = Integer.parseInt(value.substring(0, value.length()-1), 16);
				}
				if(num==decValue){
					label.setBackground(Color.blue);
					label.setForeground(Color.white);
				}else{
					label.setBackground(Color.white);
					label.setForeground(Color.black);
				}
			}
		}
	}
	
	public String getstatebitlist(int shortvalue) {
		short sv = (short) shortvalue;
		String bstr = Integer.toBinaryString(sv);
		bstr = bstr.length() < 16 ? "0000000000000000".substring(bstr.length()) + bstr
				: bstr.substring(bstr.length() - 16, bstr.length());// 二进制16位补零
		return bstr;
	}

	public JPanel getPanel1() {
		return panel1;
	}

	public void setPanel1(JPanel panel1) {
		this.panel1 = panel1;
	}

	public JPanel getPanel2() {
		return panel2;
	}

	public void setPanel2(JPanel panel2) {
		this.panel2 = panel2;
	}
	
	
	
}
