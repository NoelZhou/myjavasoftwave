package com.zt.panel.Debug;

import java.awt.Component;
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

public class WindParamGivenPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private int width;
	private SaveMessage saveMessage = SaveMessage.getInstance();
	@SuppressWarnings("unused")
	private int height;
	private JPanel panelLeft;
	private JPanel panelRight;

	public WindParamGivenPanel(int width, int height) {
		this.width = width;
		this.height = height;
		createUI();
	}

	public void createUI() {
		setLayout(null);

		panelLeft = new JPanel();
		panelLeft.setBounds(width / 2 - 320, 10, 300, 500);
		panelLeft.setLayout(null);
		addLeft();
		panelRight = new JPanel();
		panelRight.setBounds(width / 2 + 50, 10, 300, 500);
		panelRight.setLayout(null);
		addRight();

		add(panelLeft);
		add(panelRight);
	}

	public void addLeft() {
		panelLeft.add(addJLalbel("网侧参数", "", 0, 0, 100, 20));
		panelLeft.add(addJLalbel("无功电流", "4", 20, 50, 100, 20));
		panelLeft.add(addJLalbel("未定义", "5", 20, 100, 100, 20));
		panelLeft.add(addJLalbel("直流电压", "6", 20, 150, 100, 20));

		panelLeft.add(addJTexField("4", 120, 50, 100, 20));
		panelLeft.add(addJTexField("5", 120, 100, 100, 20));
		panelLeft.add(addJTexField("6", 120, 150, 100, 20));

		JButton button = new JButton("确定");
		button.setActionCommand("0");
		button.setBounds(90, 200, 80, 30);
		addActionButtonListener(button);
		panelLeft.add(button);
	}

	public void addRight() {
		panelRight.add(addJLalbel("机侧参数", "", 0, 0, 100, 20));
		panelRight.add(addJComboBox(new String[] { "转矩给定", "有功电流" }, 20, 50, 85, 20));
		panelRight.add(addJComboBox(new String[] { "无功电流", "无功功率", "功率因素" }, 20, 100, 85, 20));
		panelRight.add(addJLalbel("未定义", "9", 20, 150, 100, 20));

		panelRight.add(addJTexField("7", 120, 50, 100, 20));
		panelRight.add(addJTexField("8", 120, 100, 100, 20));
		panelRight.add(addJTexField("9", 120, 150, 100, 20));

		JButton button = new JButton("确定");
		button.setActionCommand("1");
		button.setBounds(90, 200, 80, 30);
		addActionButtonListener(button);
		panelRight.add(button);
	}

	public void addActionButtonListener(JButton button) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel panel;
				if(e.getActionCommand().equals("0")){
					panel = panelLeft;
				}else {
					panel = panelRight;
				}
				String[] strings = saveMessage.getIpAndCom();
				int num = panel.getComponentCount();
				for (int i = 0; i < num; i++) {
					Component component = panel.getComponent(i);
					if (component instanceof JTextField) {
						JTextField jTextField = (JTextField) component;
						if (!jTextField.getText().equals("0")) {
							SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
									saveMessage.getModbustcp(), jTextField.getName(), jTextField.getText());
						}
					}
				}
			}
		});
	}

	public JComboBox<String> addJComboBox(String[] texts, int x, int y, int width, int height) {

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(texts));
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
