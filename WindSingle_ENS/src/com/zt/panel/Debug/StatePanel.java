package com.zt.panel.Debug;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.zt.common.FileDiretory;
import com.zt.common.GlobalUntils;
import com.zt.common.ReadModbusXml;
import com.zt.common.SunModbusTcpbyIp;
import com.zt.pojo.ControlModelParam;
import com.zt.pojo.SaveMessage;

public class StatePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private int width;
	@SuppressWarnings("unused")
	private int height;
	private JPanel panel1;
	private SaveMessage saveMessage = SaveMessage.getInstance();
	private JPanel panel2;

	public StatePanel(int width, int height) {
		this.width = width;
		this.height = height;
		createUI();
	}

	public void createUI() {
		setLayout(null);
		panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createTitledBorder("状态"));
		panel1.setLayout(new FlowLayout(0, 50, 30));
		panel1.setBounds(0, 0, width-20, 250);
		StateModel(panel1);

		panel2 = new JPanel();
		panel2.setBounds(0, 250, width-20, 120);
		panel2.setBorder(BorderFactory.createTitledBorder("控制"));
		panel2.setLayout(new FlowLayout(0, 30, 10));
		ControlModel(panel2);
		add(panel1);
		add(panel2);
	}

	public void StateModel(JPanel panel) {
		panel.removeAll();
		List<Object> objects = new ReadModbusXml().setLists(ControlModelParam.class, GlobalUntils.PATH_CONTROL_MODEL);
		SaveMessage saveMessage = SaveMessage.getInstance();
		String values = saveMessage.getStrValue();
		String[] strings = values.split(",");
		for (Object object : objects) {
			ControlModelParam controlModelParam = (ControlModelParam) object;
			if (controlModelParam.getModbus_type() == SaveMessage.getInstance().getModbustcp()
					&& controlModelParam.getModel_type() == 2) {
				String[] bits = controlModelParam.getBit_id().split("\\.");
				String bitValue = strings[Integer.parseInt(bits[0])];
				int state = getBitState(Integer.parseInt(bitValue), bits);
				panel.add(createLabelAndImage(controlModelParam.getName(), state, controlModelParam.getBit_id()));
				changeButtonEnable(controlModelParam.getName(), state);
			}
		}
		panel.updateUI();
		panel.invalidate();
	}

	public void changeButtonEnable(String name, int state) {
		if (panel2 != null) {
			int num = panel2.getComponentCount();
			for (int i = 0; i < num; i++) {
				Component comp = panel2.getComponent(i);
				if (comp instanceof JButton) {
					JButton jButton = (JButton) comp;
					if (jButton.getName()!=null&&jButton.getName().equals(name)) {
						if (state == 0) {
							jButton.setEnabled(false);
						} else {
							jButton.setEnabled(true);
						}
					}
				}
			}
		}

	}

	private int getBitState(int bitValue, String[] bits) {
		String binaryStr = getstatebitlist(bitValue);
		int bitNum = Integer.parseInt(bits[1]);
		int state = Integer.parseInt((binaryStr.substring(15 - bitNum, 16 - bitNum)));
		return state;
	}

	public void ControlModel(JPanel panel) {
		panel.add(createButton("启动", "启动请求", "0071H"));
//		panel.add(createButton("励磁", "励磁请求", "009EH"));
		panel.add(createButton("加载", "加载请求", "0072H"));
//		panel.add(createButton("机侧停机", null, "0075H"));
		panel.add(createButton("停机", "停机请求", "0073H"));
		panel.add(createButton("复位", "复位请求", "0074H"));
//		panel.add(createButton("Chopper测试", null, "00A8H"));
//		panel.add(createButton("Crowbar测试", null, "00A9H"));
	}

	private JLabel createLabelAndImage(String bitVarName, int state, String order) {
		JLabel label = new JLabel(bitVarName, JLabel.CENTER);
		label.setToolTipText(order);
		label.setBackground(Color.white);
		label.setBorder(BorderFactory.createLineBorder(Color.gray));
		label.setPreferredSize(new Dimension(160, 40));
		label.setOpaque(true);
		if (state == 0) {
			label.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan01.png"));
		} else {
			label.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan.png"));
		}

		return label;
	}

	public JLabel createLabel(String text, String order) {
		JLabel label = new JLabel(text, JLabel.CENTER);
		label.setToolTipText(order);
		label.setBackground(Color.white);
		label.setBorder(BorderFactory.createLineBorder(Color.gray));
		label.setPreferredSize(new Dimension(100, 40));
		label.setOpaque(true);
		return label;
	}

	public JButton createButton(String text, String name, String code) {
		JButton button = new JButton(text);
		button.setPreferredSize(new Dimension(120, 40));
		button.setName(name);
		button.setToolTipText(code);
		button.setActionCommand(code);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] strings = saveMessage.getIpAndCom();
				SunModbusTcpbyIp.WriteSunModbusTcpStrAll(strings[0], Integer.parseInt(strings[1]),
						saveMessage.getModbustcp(), "0000", e.getActionCommand());

			}
		});
		return button;
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
