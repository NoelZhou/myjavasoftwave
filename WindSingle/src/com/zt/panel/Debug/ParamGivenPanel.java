package com.zt.panel.Debug;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.zt.common.SunModbusTcpbyIp;
import com.zt.pojo.SaveMessage;

public class ParamGivenPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private int width;
	@SuppressWarnings("unused")
	private int height;
	private JPanel panelLeft;
	private JPanel panelRight;
	private JComboBox<String> comboBoxLeft;
	private JComboBox<String> comboBoxRight;
	private JTextField jFieldLeft;
	private JTextField jFieldRight;
	private SaveMessage saveMessage = SaveMessage.getInstance();
	public ParamGivenPanel(int width,int height){
		this.width = width;
		this.height = height;
		createUI();
	}
	
	public void createUI(){
		setLayout(null);
		
		panelLeft = new JPanel();
		panelLeft.setBounds(width/2-340, 10, 300, 500);
		panelLeft.setLayout(null);
		addLeft();
		panelRight = new JPanel();
		panelRight.setBounds(width/2+10, 10, 300, 500);
		panelRight.setLayout(null);
		addRight();
		
		add(panelLeft);
		add(panelRight);
	}
	
	public void addLeft(){
		panelLeft.add(addJLalbel("网侧参数", 0, 0, 100, 20));
		comboBoxLeft = addJComboBox(new String[]{"开环占空比","开环频率","闭环无功电流",
				"闭环频率","前馈角度补偿","前馈幅值补偿",
				"整流器无功电流","整流器直流电压补偿"}, 20, 50, 150, 20);
		jFieldLeft = addJTexField("", 200, 50, 100, 20);
		panelLeft.add(comboBoxLeft);
		panelLeft.add(jFieldLeft);
		JButton button = new JButton("确定");
		button.setActionCommand("left");
		button.setBounds(90, 100, 80, 30);
		addButtonActionListener(button);
		panelLeft.add(button);
	}
	
	public void addRight(){
		panelRight.add(addJLalbel("机侧参数", 0, 0, 100, 20));
		comboBoxRight = addJComboBox(new String[]{"开环占空比","开环频率","闭环无功电流",
				"闭环频率","前馈角度补偿","前馈幅值补偿",
				"整流器有功电流","整流器无功电流"}, 20, 50,150, 20);
		jFieldRight = addJTexField("", 200, 50, 100, 20);
		panelRight.add(comboBoxRight);
		panelRight.add(jFieldRight);
		JButton button = new JButton("确定");
		button.setActionCommand("right");
		button.setBounds(90, 100, 80, 30);
		addButtonActionListener(button);
		panelRight.add(button);
	}
	
	public void addButtonActionListener(JButton button){
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] strings = saveMessage.getIpAndCom();
				if(e.getActionCommand().equals("left")){
					SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
							saveMessage.getModbustcp(), "1", String.valueOf(comboBoxLeft.getSelectedIndex()+1));
					SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
							saveMessage.getModbustcp(), "2", jFieldLeft.getText());
					SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
							saveMessage.getModbustcp(), "0000", "00A2H");
				}else{
					SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
							saveMessage.getModbustcp(), "1", String.valueOf(comboBoxRight.getSelectedIndex()+1));
					SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
							saveMessage.getModbustcp(), "2", jFieldRight.getText());
					SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
							saveMessage.getModbustcp(), "0000", "00A3H");
				}
				
			}
		});
		
	}
	
	
	public JComboBox<String> addJComboBox(String[] texts,int x, int y,int width,int height){
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(texts));
		comboBox.setBounds(x,y,width,height);
		return comboBox;
	}
	
	public JLabel addJLalbel(String text,int x, int y,int width,int height){
		JLabel label = new JLabel(text);
		label.setBounds(x, y, width, height);
		return label;
	}
	
	public JTextField addJTexField(String text,int x, int y,int width,int height){
		JTextField jTextField = new JTextField();
		jTextField.setBounds(x, y, width, height);
		jTextField.setBorder(new EmptyBorder(0, 0, 0, 0));
		jTextField.setText("0");
		return jTextField;
	}
	
}











