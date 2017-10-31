package com.zt.panel.Debug;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import com.zt.common.SunModbusTcpbyIp;
import com.zt.pojo.SaveMessage;
import java.awt.Graphics;
import java.awt.Graphics2D;
public class MutationGivenPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private int width;
	private SaveMessage saveMessage = SaveMessage.getInstance();
	@SuppressWarnings("unused")
	private int height;
	private JPanel panelLeft;
	private JPanel panelRight;
	private JPanel panelLeft00;
	private String jcomboxValue;

	public MutationGivenPanel(int width, int height) {
		this.width = width;
		this.height = height;
		createUI();
	}

	public void createUI() {
		setLayout(null);

		panelLeft00 = new JPanel();
		panelLeft00.setBounds(0, 10, 245, 450);
		panelLeft00.setLayout(null);
		addLeft00();

		panelLeft = new JPanel();
		panelLeft.setBounds(251, 10, 245, 450);
		panelLeft.setLayout(null);
		addLeft();
		panelRight = new JPanel();
		panelRight.setBounds(501, 10, 249, 450);
		panelRight.setLayout(null);
		addRight();

		add(panelLeft00);
		add(panelLeft);
		add(panelRight);
	}
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		Graphics2D g2D=(Graphics2D)g;
		g2D.setColor(Color.lightGray);
		g2D.drawLine(246, 0, 246, 450);
		g2D.drawLine(497, 0, 497, 450);
	}
	private void addLeft00() {
		panelLeft00.add(addJLalbel("稳态电压*1.V", "6", 10, 50, 120, 20));
		panelLeft00.add(addJLalbel("稳态频率*100.Hz", "7", 10, 100, 120, 20));

		panelLeft00.add(addJTexField("6", 130, 50, 100, 20));
		panelLeft00.add(addJTexField("7", 130, 100, 100, 20));

		JButton button1 = new JButton("电压给定确定");
		button1.setActionCommand("0");
		button1.setBounds(10, 200, 100, 30);
		button1.setMargin(new Insets(0, 0, 0, 0));
		addActionButtonListenerLeft00(button1);
		panelLeft00.add(button1);

		JButton button2 = new JButton("频率给定确定");
		button2.setActionCommand("1");
		button2.setBounds(130, 200, 100, 30);
		button2.setMargin(new Insets(0, 0, 0, 0));
		addActionButtonListenerLeft00(button2);
		panelLeft00.add(button2);
	}

	private void addActionButtonListenerLeft00(JButton button) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel panel = panelLeft00;
				String[] strings = saveMessage.getIpAndCom();
				int num = panel.getComponentCount();
				for (int i = 0; i < num; i++) {
					Component component = panel.getComponent(i);
					if (component instanceof JTextField) {
						JTextField jTextField = (JTextField) component;
						// if (!jTextField.getText().equals("0")) {
						if (jTextField.getName().equals("6") && e.getActionCommand().equals("0")) {
							SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
									saveMessage.getModbustcp(), jTextField.getName(), jTextField.getText());
						} else if (jTextField.getName().equals("7") && e.getActionCommand().equals("1")) {
							SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
									saveMessage.getModbustcp(), jTextField.getName(), jTextField.getText());
						}
						// }
					}
				}
			}
		});
	}

	public void addLeft() {
		panelLeft.add(addJLalbel("暂态频率*100.Hz", "1", 0, 50, 140, 20));
		panelLeft.add(addJLalbel("频率暂态持续时间*1.s", "2", 0, 100, 140, 20));

		panelLeft.add(addJTexField("1", 150, 50, 90, 20));
		panelLeft.add(addJTexField("2", 150, 100, 90, 20));

		JButton button = new JButton("确定");
		button.setActionCommand("1");
		button.setBounds(20, 200, 80, 30);
		addActionButtonListener(button);
		panelLeft.add(button);

		JButton button1 = new JButton("触发");
		button1.setActionCommand("AAH");
		button1.setBounds(140, 200, 80, 30);
		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String[] strings = saveMessage.getIpAndCom();
				SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
						saveMessage.getModbustcp(), "0000", e.getActionCommand());
			}
		});
		panelLeft.add(button1);
	}

	public void addRight() {
		panelRight.add(addJLalbel("暂态电压百分比(0-150)", "3", 0, 50, 150, 20));
		panelRight.add(addJLalbel("电压暂态持续时间*1.ms", "4", 0, 100, 150, 20));
		panelRight.add(addJLalbel("电压突变类型", "5", 0, 150, 150, 20));

		panelRight.add(addJTexField("3", 150, 50, 70, 20));
		panelRight.add(addJTexField("4", 150, 100, 70, 20));
		panelRight.add(addJComboBox(new String[] { "A相突变", "B相突变", "C相突变", "AB相突变", "BC相突变", "AC相突变", "ABC相突变" }, 120,
				150, 100, 20));

		JButton button = new JButton("确定");
		button.setActionCommand("2");
		button.setBounds(20, 200, 80, 30);
		addActionButtonListener(button);
		panelRight.add(button);

		JButton button1 = new JButton("触发");
		button1.setActionCommand("A9H");
		button1.setBounds(140, 200, 80, 30);
		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String[] strings = saveMessage.getIpAndCom();
				SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
						saveMessage.getModbustcp(), "0000", e.getActionCommand());
			}
		});
		panelRight.add(button1);
	}

	public void addActionButtonListener(JButton button) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel panel = new JPanel();
				if (e.getActionCommand().equals("1")) {
					panel = panelLeft;
				} else if (e.getActionCommand().equals("2")) {
					panel = panelRight;
				}
				String[] strings = saveMessage.getIpAndCom();
				String paramString = "";//下发参数值
				String addr = "0001";//默认地址
				int num = panel.getComponentCount();
				for (int i = 0; i < num; i++) {
					Component component = panel.getComponent(i);
					if (component instanceof JTextField) {
						JTextField jTextField = (JTextField) component;
						if(paramString.equals("")){
							paramString =jTextField.getText();
						}else {
							paramString = paramString+","+jTextField.getText();
						}
//						SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
//								saveMessage.getModbustcp(), jTextField.getName(), jTextField.getText());
					}else if (component instanceof JComboBox<?>) {
						paramString = paramString+","+jcomboxValue;
						addr = "0003";
//						SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
//								saveMessage.getModbustcp(), "0005", jcomboxValue);
					}
				}
				//一次性发送指令
				SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
						saveMessage.getModbustcp(), addr, paramString);
			}
		});
	}

	public JComboBox<String> addJComboBox(String[] texts, int x, int y, int width, int height) {

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(texts));
		jcomboxValue = "1";
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getItem().equals("A相突变"))
					jcomboxValue = "1";
				if (e.getItem().equals("B相突变"))
					jcomboxValue = "16";
				if (e.getItem().equals("C相突变"))
					jcomboxValue = "256";
				if (e.getItem().equals("AB相突变"))
					jcomboxValue = "17";
				if (e.getItem().equals("BC相突变"))
					jcomboxValue = "272";
				if (e.getItem().equals("AC相突变"))
					jcomboxValue = "257";
				if (e.getItem().equals("ABC相突变"))
					jcomboxValue = "273";
			}
		});
		comboBox.setBounds(x, y, width, height);
		return comboBox;
	}

	public JLabel addJLalbel(String text, String addr, int x, int y, int width, int height) {
		JLabel label = new JLabel(text);
		label.setBounds(x, y, width, height);
		label.setToolTipText(addr);
		return label;
	}

	public JTextField addJTexField(String addr, int x, int y, int width, int height) {
		JTextField jTextField = new JTextField();
		jTextField.setBounds(x, y, width, height);
		jTextField.setBorder(new EmptyBorder(0, 0, 0, 0));
		jTextField.setText("0");
		jTextField.setName(addr);
		return jTextField;
	}

}
