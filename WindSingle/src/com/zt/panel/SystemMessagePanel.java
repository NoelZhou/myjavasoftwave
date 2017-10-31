package com.zt.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
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
import com.zt.frame.SysSubFrame.WarnFrame;
import com.zt.pojo.SystemMessageParam;
import com.zt.thread.THD_FAULT;
import com.zt.thread.THD_WRAN;

public class SystemMessagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private THD_FAULT tFault;
	private THD_WRAN tWran;
	private  List<SystemMessageParam> lMessageParams = new ArrayList<SystemMessageParam>();
	public SystemMessagePanel() {
		createUI();
	}
	public void createUI(){
		setUIFont();
		readxml();
		setLayout(null);
		add(debugBtnPanel());
		add(createImageJLabel());
		add(createPanelBlock(260,190,1));
		add(createPanelBlock(1030,230,2));
		add(createPanelBlock(700,40,3));
		add(createPanelBlock(620,320,4));
		add(createPanelBlock(450,405,5));
		add(createPanelBlock(830,405,6));
	}
	private JLabel imageLabel1;
	private JLabel imageLabel2;
	private JPanel debugBtnPanel() {
		JPanel panel = new JPanel();
		panel.setName("故障告警");
		panel.setOpaque(false);
		panel.setBounds(50, 30, 100, 100);
		panel.setLayout(null);
		imageLabel1 = new JLabel();
		imageLabel1.setBounds(5, 17, 22, 22);
		imageLabel1.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan01.png"));
		JButton button1 = new JButton("故障");
		button1.setBounds(30, 10, 60, 30);
		buttonClickListener(button1);
		imageLabel2 = new JLabel();
		imageLabel2.setBounds(5, 52, 22, 22);
		imageLabel2.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan01.png"));
		JButton button2 = new JButton("告警");
		button2.setBounds(30, 45, 60, 30);
		buttonClickListener(button2);
		panel.add(imageLabel1);
		panel.add(button1);
		panel.add(imageLabel2);
		panel.add(button2);
		return panel;
	}
	
	
	public void buttonClickListener(JButton button){
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton curBtn = (JButton)e.getSource();
				String name = curBtn.getText();
				if(name.equals("故障")){
					DFrame dFrame = new DFrame();
					tFault = new THD_FAULT();
					tFault.setTableAndVect(dFrame.getTable(),dFrame.getVect());
					tFault.setDFrame(dFrame);
					dFrame.setFaultThread(tFault);
					tFault.start();
					dFrame.setVisible(true);
				}else{
					WarnFrame warnFrame = new WarnFrame();
					tWran = new THD_WRAN();
					tWran.setWarnFrame(warnFrame);
					warnFrame.setFaultThread(tWran);
					tWran.start();
					warnFrame.setVisible(true);
				}
				
			}
		});
	}
	
	
	/**
	 * 刷新故障告警
	 * @param strValue
	 */
	public void sendDebugState(String strValue){
		if(!strValue.equals("")){
			String[] values = strValue.split(",");
			String bitString37_6 = getbit(Integer.parseInt(values[37]),6);
			if(Integer.parseInt(bitString37_6)!=0){
				imageLabel1.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan02.png"));
			}else {
				imageLabel1.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan01.png"));
			}
			int bit34[] = {0,1,2,3,4,10,11,12,13};
			String binary34 = getbit(Integer.parseInt(values[34]), -1);
			outloop:for(int i=0;i<bit34.length;i++){
				if(binary34.substring(15-bit34[i], 16-bit34[i]).equals("1")){
					imageLabel2.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan02.png"));
					break outloop;
				}
				imageLabel2.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan01.png"));
			}
		}
	}
	
	/**
	 * 刷新参数信息
	 * @param lMessageParams
	 */
	public void sendSystemMessageParams(List<SystemMessageParam> lMessageParams,String strValue){
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
						for(int z=0;z<lMessageParams.size();z++){
							SystemMessageParam systemMessageParam = lMessageParams.get(z);
							if (jField.getName().equals(systemMessageParam.getAddr())) {
								if(systemMessageParam.getAddr().equals("30")){
									short[] arraytmp = new short[2];
									arraytmp[0] = (short) Integer.parseInt(systemMessageParam.getValue());
									arraytmp[1] = (short) Integer.parseInt(strValue.split(",")[31]);
									long unshort = unshortToInt(arraytmp, 0);
									jField.setText(String.valueOf(unshort));
								}else{
									float value = Integer.parseInt(systemMessageParam.getValue());
									int cof = systemMessageParam.getCof();
									jField.setText(String.valueOf(value/cof));
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
	 *  刷新图片组合
	 * @param strValue
	 */
	public void sendSystemImage(String strValue) {
		if(!strValue.equals("")){
			String[] arr = strValue.split(",");
			String str21_5 = getbit(Integer.parseInt(arr[21]),5);
			String str71_5 = getbit(Integer.parseInt(arr[71]),5);
			String str231_5 = getbit(Integer.parseInt(arr[231]),5);
			String allbit = str21_5+str71_5+str231_5;
			String imagepath = "";
			if(allbit.equals("000")){
				imagepath = "/images/DoubleFed1.png";
			}
			if(allbit.equals("001")){
				imagepath = "/images/DoubleFed4.png";
			}
			if(allbit.equals("010")){
				imagepath = "/images/DoubleFed3.png";
			}
			if(allbit.equals("011")){
				imagepath = "/images/DoubleFed6.png";
			}
			if(allbit.equals("100")){
				imagepath = "/images/DoubleFed2.png";
			}
			if(allbit.equals("101")){
				imagepath = "/images/DoubleFed5.png";
			}
			if(allbit.equals("110")){
				imagepath = "/images/DoubleFed7.png";
			}
			if(allbit.equals("111")){
				imagepath = "/images/DoubleFed8.png";
			}
			int len = this.getComponentCount();
			for (int i = 0; i < len; i++) {
				Component comp = this.getComponent(i);
				if (comp instanceof JLabel) {
					JLabel label = (JLabel) comp;
					label.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+imagepath));
				}

			}
		}
		
	}
	/**
	 * * bit位
	 * 
	 * @param begin
	 * @param end
	 * @param shortvalue
	 */
	public String getbit(int shortvalue,int num) {
		short sv = (short) shortvalue;
		String bstr = Integer.toBinaryString(sv);
		bstr = bstr.length() < 16 ? "0000000000000000".substring(bstr.length()) + bstr
				: bstr.substring(bstr.length() - 16, bstr.length());// 二进制16位补零
		if(num==-1) {
			return bstr;
		}
		return bstr.substring(15-num, 16-num);
	}
	
	public  void readxml(){
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
	public JPanel createPanelBlock(int x,int y,int layout){
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setOpaque(false);
		int num =0;
		for(int i=0;i<lMessageParams.size();i++){
			SystemMessageParam sParam = lMessageParams.get(i);
			if(sParam.getLayouttype()==layout){
				int subY = num*25;
				JLabel label = new JLabel(sParam.getName()+"("+sParam.getUnit()+")");
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
		panel.setBounds(x,y, 220, num*25);
		return panel;
	}
	
	public JLabel createImageJLabel(){
		JLabel label = new JLabel();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //得到屏幕的尺寸
		label.setBounds(screenSize.width/2-600, screenSize.height/2-250, 994, 304);
		label.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+"/images/DoubleFed1.png"));
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
