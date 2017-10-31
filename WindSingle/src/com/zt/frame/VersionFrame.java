package com.zt.frame;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.zt.common.FileDiretory;
import com.zt.pojo.SaveMessage;

public class VersionFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private SaveMessage saveMessage = SaveMessage.getInstance();

	public VersionFrame() {
		init();// 初始化版本界面
		buildUI();// 创建UI
	}

	public void init() {
		this.setTitle("版本信息");
		this.setResizable(false);
		this.setBounds(0, 0, 426, 278);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);// 居中显示
		this.setIconImage(new ImageIcon(FileDiretory.getCurrentDir() + "/images/SC.png").getImage());
		this.setLayout(null);
	}

	private JTextField jField1;// 网侧DSP软件版本
	private JTextField jField2;// 机侧DSP软件版本
	private JTextField jField3;// 通讯ARM软件版本
	private JTextField jField4;// 网侧DSP参数版本
	private JTextField jField5;// 机侧DSP参数版本
	private JTextField jField6;// 通讯ARM参数版本

	public void buildUI() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 420, 250);
		this.getContentPane().add(panel);

		panel.add(commonLabel("网侧DSP软件版本:", 10, 10, 150, 30));
		panel.add(commonLabel("机侧DSP软件版本:", 10, 50, 150, 30));
		panel.add(commonLabel("通讯ARM软件版本:", 10, 90, 150, 30));
		panel.add(commonLabel("网侧DSP参数版本:", 10, 130, 150, 30));
		panel.add(commonLabel("机侧DSP参数版本:", 10, 170, 150, 30));
		panel.add(commonLabel("通讯ARM参数版本:", 10, 210, 150, 30));

		jField1 = commonTextField("DSP_WGxxxxKDF_Vxx_GRID_Vx.x", 160, 10, 250, 30);
		jField2 = commonTextField("DSP_WGxxxxKDF_Vxx_MOTOR_Vx.x", 160, 50, 250, 30);
		jField3 = commonTextField("ARM_WGxxxxKDF_Vxx_Vx.x", 160, 90, 250, 30);
		jField4 = commonTextField("Vx.x", 160, 130, 100, 30);
		jField5 = commonTextField("Vx.x", 160, 170, 100, 30);
		jField6 = commonTextField("Vx.x", 160, 210, 100, 30);

		panel.add(jField1);
		panel.add(jField2);
		panel.add(jField3);
		panel.add(jField4);
		panel.add(jField5);
		panel.add(jField6);
	}

	public JLabel commonLabel(String text, int x, int y, int width, int height) {
		JLabel label = new JLabel(text);
		label.setBounds(x, y, width, height);
		label.setFont(new Font(null, Font.BOLD, 14));
		return label;
	}

	public JTextField commonTextField(String text, int x, int y, int width, int height) {
		JTextField jTextField = new JTextField();
		jTextField.setText(text);
		jTextField.setOpaque(false);
		jTextField.setEditable(false);
		jTextField.setBounds(x, y, width, height);
		return jTextField;
	}

	// 查询版本
	public void findVersionMess() {
		/*
		 * 双馈
		 * 版本信息如下: 网侧DSP软件版本:DSP_WGxxxxKDF_Vxx_GRID_Vx.x 地址:94
		 * 机侧DSP软件版本:DSP_WGxxxxKDF_Vxx_ROT_Vx.x 地址:42 通讯ARM软件版本:
		 * ARM_WGxxxxKDF_Vxx _Vx.x 地址:416,417,410 网侧DSP参数版本: Vx.x 地址:95
		 * 机侧DSP参数版本: Vx.x 地址:54 通讯ARM参数版本: Vx.x 地址:468
		 * 
		 * 版本信息如下: 网侧DSP软件版本:DSP_WGxxxxKDF_Vxx_GRID_Vx.x 地址:97
		 * 机侧DSP软件版本:DSP_WGxxxxKDF_Vxx_ROT_Vx.x 地址:264 通讯ARM软件版本:
		 * ARM_WGxxxxKDF_Vxx _Vx.x 地址:50
		 */
		String arm = "ARM_";
		int modbustcp_type = saveMessage.getModbustcp();// 协议类型
		String stringValue = saveMessage.getStrValue();// 协议参数值
		if (modbustcp_type == 0) {
			// 网侧DSP版本
			detailDSPMess(stringValue, 94, "_GRID_", jField1);
			// 机侧DSP版本
			detailDSPMess(stringValue, 42, "_MOTOR_", jField2);
			// arm版本信息
			String colnumvalue_arm416 = stringValue.split(",")[416];
			arm += "WG" + colnumvalue_arm416 + "KDF";
			String colnumvalue_arm417 = stringValue.split(",")[417];
			arm += "_V" + colnumvalue_arm417;
			String colnumvalue_arm410 = stringValue.split(",")[410];
			if (colnumvalue_arm410.length() > 1) {
				arm += "_V" + colnumvalue_arm410.substring(0, 1) + "." + colnumvalue_arm410.substring(1, 2);
			} else {
				arm = "无版本信息";
			}
			if(!arm.equals("无版本信息")){jField3.setText(arm);}
			// 网侧DSP参数版本: Vx.x
			detailParamVersion(stringValue, 95, jField4);
			// 机侧DSP参数版本: Vx.x
			detailParamVersion(stringValue, 54, jField5);
			// 通讯ARM参数版本: Vx.x
			detailParamVersion(stringValue, 468, jField6);

		}
	}

	public void detailDSPMess(String stringValue, int addr, String wcOrJc, JTextField textField) {
		String allStr = "DSP_";
		String colnumvalue_wc = stringValue.split(",")[94];
//		String dspstr = getbits(Integer.valueOf(colnumvalue_wc));
		if (colnumvalue_wc.length() >= 5) {
			if (colnumvalue_wc.substring(0, 1).equals("1")) {
				allStr += "WG1500KDF";
			} else {
				allStr += "WG2000KDF";
			}
			allStr += "_V" + colnumvalue_wc.substring(1, 2);
			allStr += wcOrJc;
			allStr += "_V" + colnumvalue_wc.substring(3, 4) + "." + colnumvalue_wc.substring(4, 5);
		} else {
			allStr = "无版本信息";
		}
		if(!allStr.equals("无版本信息")){
			textField.setText(allStr);
		}
	}

	public void detailParamVersion(String stringValue, int addr, JTextField textField) {
		String v = "V";
		String colnumvalue_jcdsp = stringValue.split(",")[addr];
		if (colnumvalue_jcdsp.length() > 1) {
			v += colnumvalue_jcdsp.substring(0, 1) + "." + colnumvalue_jcdsp.substring(1, 2);
		} else {
			v = "无版本信息";
		}
		if(!v.equals("无版本信息")){
			textField.setText(v);
		}
		
	}

	/**
	 * * bit位
	 * 
	 * @param begin
	 * @param end
	 * @param shortvalue
	 */
	public String getbits(int shortvalue) {
		short sv = (short) shortvalue;
		String bstr = Integer.toBinaryString(sv);
		bstr = bstr.length() < 16 ? "0000000000000000".substring(bstr.length()) + bstr
				: bstr.substring(bstr.length() - 16, bstr.length());// 二进制16位补零
		return bstr;
	}
}
