package com.zt.panel;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import com.zt.common.FileDiretory;
import com.zt.common.GlobalUntils;
import com.zt.common.ReadNativeXml;
import com.zt.frame.SysSubFrame.DFrame;
import com.zt.pojo.SystemMessageParam;
import com.zt.thread.THD_FAULT;

public class SystemMessagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private THD_FAULT tFault;
//	private THD_WRAN tWran;
	private List<SystemMessageParam> lMessageParams = new ArrayList<SystemMessageParam>();

	public SystemMessagePanel() {
		createUI();
	}

	public void createUI() {
		setUIFont();
		readxml();
		setLayout(null);
		add(debugBtnPanel());
		add(createImageJLabel(null, "left", 350, 60, 250));
		add(createImageJLabel("A", "Elect4", 600, 0, 106));
		add(createImageJLabel("B", "Elect4", 600, 120, 106));
		add(createImageJLabel("C", "Elect4", 600, 240, 106));
		add(createNomalLabel("A柜", 800, 10, 100, 30));
		add(createNomalLabel("B柜", 800, 130, 100, 30));
		add(createNomalLabel("C柜", 800, 260, 100, 30));
		add(createPanelBlock(300, 350, 1));
		add(createPanelBlock(550, 350, 2));
		add(createPanelBlock(800, 350, 3));
	}

	private JLabel imageLabel1;
	private JLabel imageLabel2;
	private JLabel imageLabel3;
	
	private JPanel debugBtnPanel() {
		JPanel panel = new JPanel();
		panel.setName("故障告警");
		panel.setOpaque(false);
		panel.setBounds(50, 30, 130, 120);
		panel.setLayout(null);
		imageLabel1 = stateIconLabel(5, 15, 22, 22);
		JButton button1 = new JButton("A相故障");
		button1.setBounds(30, 10, 100, 30);
		buttonClickListener(button1);
		
		imageLabel2 = stateIconLabel(5, 50, 22, 22);
		JButton button2 = new JButton("B相故障");
		button2.setBounds(30, 45, 100, 30);
		buttonClickListener(button2);
		
		imageLabel3 = stateIconLabel(5, 85, 22, 22);
		JButton button3 = new JButton("C相故障");
		button3.setBounds(30, 80, 100, 30);
		buttonClickListener(button3);
		
		panel.add(imageLabel1);
		panel.add(button1);
		panel.add(imageLabel2);
		panel.add(button2);
		panel.add(imageLabel3);
		panel.add(button3);
		return panel;
	}
	
	public JLabel stateIconLabel(int x,int y,int width,int height){
		JLabel label = new JLabel();
		label.setBounds(x, y, width, height);
		label.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan01.png"));
		return label;
	}

	public void buttonClickListener(JButton button) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton curBtn = (JButton) e.getSource();
				String name = curBtn.getText();
//				if (name.equals("A相故障")) {
					DFrame dFrame = new DFrame(name);
					tFault = new THD_FAULT();
					tFault.setTableAndVect(dFrame.getTable(), dFrame.getVect());
					tFault.setDFrame(dFrame);
					dFrame.setFaultThread(tFault);
					tFault.start();
					dFrame.setVisible(true);
//				} else {
//					WarnFrame warnFrame = new WarnFrame();
//					tWran = new THD_WRAN();
//					tWran.setWarnFrame(warnFrame);
//					warnFrame.setFaultThread(tWran);
//					tWran.start();
//					warnFrame.setVisible(true);
//				}

			}
		});
	}

	/**
	 * 刷新故障告警
	 * 
	 * @param strValue
	 */
	public void sendDebugState(String strValue) {
		if (!strValue.equals("")) {
			String[] values = strValue.split(",");
			String bitString40_3 = getbit(Integer.parseInt(values[40]), 3);
			String bitString41_3 = getbit(Integer.parseInt(values[41]), 3);
			String bitString42_3 = getbit(Integer.parseInt(values[42]), 3);
			if (Integer.parseInt(bitString40_3) != 0) {
				imageLabel1.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan02.png"));
			} else {
				imageLabel1.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan01.png"));
			}
			if (Integer.parseInt(bitString41_3) != 0) {
				imageLabel2.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan02.png"));
			} else {
				imageLabel2.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan01.png"));
			}
			if (Integer.parseInt(bitString42_3) != 0) {
				imageLabel3.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan02.png"));
			} else {
				imageLabel3.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan01.png"));
			}
//			int bit34[] = { 0, 1, 2, 3, 4, 10, 11, 12, 13 };
//			String binary34 = getbit(Integer.parseInt(values[34]), -1);
//			outloop: for (int i = 0; i < bit34.length; i++) {
//				if (binary34.substring(15 - bit34[i], 16 - bit34[i]).equals("1")) {
//					imageLabel2.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan02.png"));
//					break outloop;
//				}
//				imageLabel2.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan01.png"));
//			}
		}
	}

	/**
	 * 刷新参数信息
	 * 
	 * @param lMessageParams
	 */
	public void sendSystemMessageParams(List<SystemMessageParam> lMessageParams, String strValue) {
		int len = this.getComponentCount();
		for (int i = 0; i < len; i++) {
			Component comp = this.getComponent(i);
			if (comp instanceof JPanel) {
				JPanel panel = (JPanel) comp;
				int count = panel.getComponentCount();
				for (int j = 0; j < count; j++) {
					Component comp1 = panel.getComponent(j);
					if (comp1 instanceof JTextField) {
						JTextField jField = (JTextField) comp1;
						for (int z = 0; z < lMessageParams.size(); z++) {
							SystemMessageParam systemMessageParam = lMessageParams.get(z);
							if (jField.getName().equals(systemMessageParam.getAddr())) {
								if (systemMessageParam.getAddr().equals("30")) {
									short[] arraytmp = new short[2];
									arraytmp[0] = (short) Integer.parseInt(systemMessageParam.getValue());
									arraytmp[1] = (short) Integer.parseInt(strValue.split(",")[31]);
									long unshort = unshortToInt(arraytmp, 0);
									jField.setText(String.valueOf(unshort));
								} else {
									jField.setText(systemMessageParam.getValue());
								}

							}
						}
					}
				}
			}

		}
	}

	public static long unshortToInt(short[] unshort, int off) {
		long b0 = unshort[off] & 0xFFFF;
		long b1 = unshort[off + 1] & 0xFFFF;
		return (b1 << 16) | b0;
	}

	/**
	 * 刷新图片组合
	 * 
	 * @param strValue
	 */
	public void sendSystemImage(String strValue) {
		if (!strValue.equals("")) {
			String[] arr = strValue.split(",");
			String str71_5 = getbit(Integer.parseInt(arr[71]), 5);
			String str98_10 = getbit(Integer.parseInt(arr[98]), 10);
			String str131_5 = getbit(Integer.parseInt(arr[131]), 5);
			String str158_10 = getbit(Integer.parseInt(arr[158]), 10);
			String str191_5 = getbit(Integer.parseInt(arr[191]), 5);
			String str218_10 = getbit(Integer.parseInt(arr[218]), 10);

			String Abit = str71_5 + str98_10;
			String Bbit = str131_5 + str158_10;
			String Cbit = str191_5 + str218_10;
			String imagepathA = changeImage(Abit);
			String imagepathB = changeImage(Bbit);
			String imagepathC = changeImage(Cbit);

			int len = this.getComponentCount();
			for (int i = 0; i < len; i++) {
				Component comp = this.getComponent(i);
				if (comp instanceof JLabel) {
					JLabel label = (JLabel) comp;
					if (label.getName() != null) {
						if (label.getName().equals("A"))
							label.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + imagepathA));
						if (label.getName().equals("B"))
							label.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + imagepathB));
						if (label.getName().equals("C"))
							label.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + imagepathC));
					}
				}

			}
		}

	}

	public String changeImage(String bits) {
		String imagepath = "";
		if (bits.equals("00")) {
			imagepath = "/images/Elect4.png";
		}
		if (bits.equals("01")) {
			imagepath = "/images/Elect1.png";
		}
		if (bits.equals("10")) {
			imagepath = "/images/Elect3.png";
		}
		if (bits.equals("11")) {
			imagepath = "/images/Elect2.png";
		}
		return imagepath;
	}

	/**
	 * * bit位
	 * 
	 * @param begin
	 * @param end
	 * @param shortvalue
	 */
	public String getbit(int shortvalue, int num) {
		short sv = (short) shortvalue;
		String bstr = Integer.toBinaryString(sv);
		bstr = bstr.length() < 16 ? "0000000000000000".substring(bstr.length()) + bstr
				: bstr.substring(bstr.length() - 16, bstr.length());// 二进制16位补零
		if (num == -1) {
			return bstr;
		}
		return bstr.substring(15 - num, 16 - num);
	}

	public void readxml() {
		List<String> lStrings = new ReadNativeXml().setlist(GlobalUntils.PATH_SYS_PARAM);
		for (int i = 0; i < lStrings.size(); i++) {
			String strone = lStrings.get(i);
			String[] str = strone.split(",");
			if (str.length > 2) {
				SystemMessageParam sParam = new SystemMessageParam();
				sParam.setAddr(str[2]);
				sParam.setCof(Integer.parseInt(str[5]));
				sParam.setLayouttype(Integer.parseInt(str[8]));
				sParam.setName(str[10]);
				sParam.setUnit(str[6]);
				lMessageParams.add(sParam);
			}
		}
	}

	public JPanel createPanelBlock(int x, int y, int layout) {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setOpaque(false);

		int num = 0;
		for (int i = 0; i < lMessageParams.size(); i++) {
			SystemMessageParam sParam = lMessageParams.get(i);
			if (sParam.getLayouttype() == layout) {
				int subY = num * 25;
				JLabel label = new JLabel(sParam.getName() + "(" + sParam.getUnit() + ")");
				label.setBounds(0, subY, 130, 25);
				label.setHorizontalAlignment(SwingConstants.RIGHT);
				JTextField hField = new JTextField("0");
				hField.setBounds(135, subY, 80, 25);
				hField.setName(sParam.getAddr());
				hField.setOpaque(false);
				hField.setEditable(false);
				hField.setBorder(BorderFactory.createLoweredBevelBorder());
				panel.add(hField);
				panel.add(label);
				num++;
			}
		}
		panel.setBounds(x, y, 220, num * 25);
		return panel;
	}

	public JLabel createImageJLabel(String name, String imageName, int x, int y, int height) {
		JLabel label = new JLabel();
		label.setName(name);
		label.setBounds(x, y, 300, height);
		label.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/images/" + imageName + ".png"));
		return label;
	}
	
	public JLabel createNomalLabel(String text, int x, int y,int width, int height){
		JLabel label = new JLabel();
		label.setText(text);
		label.setBounds(x, y, width, height);
		return label;
	}

	public void setUIFont() {
		Font f = new Font(null, Font.PLAIN, 13);
		String names[] = { "Label", "CheckBox", "PopupMenu", "MenuItem", "CheckBoxMenuItem", "JRadioButtonMenuItem",
				"ComboBox", "Button", "Tree", "ScrollPane", "TabbedPane", "EditorPane", "TitledBorder", "Menu",
				"TextArea", "OptionPane", "MenuBar", "ToolBar", "ToggleButton", "ToolTip", "ProgressBar", "TableHeader",
				"Panel", "List", "ColorChooser", "PasswordField", "TextField", "Table", "Label", "Viewport",
				"RadioButtonMenuItem", "RadioButton", "DesktopPane", "InternalFrame" };
		for (String item : names) {
			UIManager.put(item + ".font", f);
		}
	}

}
