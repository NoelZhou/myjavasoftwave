package com.zt.frame;

import java.awt.*;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import com.zt.common.BackImageUtills;
import com.zt.common.FileDiretory;
import com.zt.common.GlobalUntils;
import com.zt.panel.ParaMonitor;
import com.zt.panel.RecordModelPanelForJF;
import com.zt.panel.SystemMessagePanel;
import com.zt.thread.THD_DEBUG;
import com.zt.thread.THD_OSI;
import com.zt.thread.THD_SEC;
import com.zt.thread.THD_SYS;

public class TheWholeFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JButton jb1, jb2, jb3, jb4, jb5, jb6, jb7;
	private ParaMonitor pMonitor;
	private THD_SEC sec;
	private THD_SYS sys;
	private THD_DEBUG tDebug;

	public TheWholeFrame() {

		getContentPane().add(createToolBarAndLogoPanel(), BorderLayout.NORTH); // 添加工具条
		getContentPane().add(buildPanel("系统信息"), BorderLayout.CENTER);
		getContentPane().add(createStateAndTimePanel(), BorderLayout.SOUTH);

		this.setResizable(false);
		this.setTitle("变流器监控系统V1.0");
		this.setIconImage(new ImageIcon(FileDiretory.getCurrentDir() + "/images/SC.png").getImage());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // 得到屏幕的尺寸
		this.setSize(screenSize.width, screenSize.height - 50);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * 创建toolbar和logo
	 */
	public JPanel createToolBarAndLogoPanel() {
		JPanel panel = new JPanel();
		panel.setName("导航栏");
		panel.setBackground(Color.white);
		panel.setForeground(Color.white);
		panel.setLayout(new BorderLayout());
		// 创建工具条
		JPanel panelBtn = new JPanel();
		panelBtn.setLayout(new FlowLayout(0, 0, 0));
		panelBtn.setBackground(Color.white);
		jb1 = createButton(0, 0, 60, 60, GlobalUntils.PATH_BTN_IMAGE_SYSMESSAGE, "系统信息");
		jb2 = createButton(60, 0, 60, 60, GlobalUntils.PATH_BTN_IMAGE_PARAMONTIOR, "参数监控");
		jb3 = createButton(120, 0, 60, 60, GlobalUntils.PATH_BTN_IMAGE_SCOPE, "示波器");
		jb4 = createButton(180, 0, 60, 60, GlobalUntils.PATH_BTN_IMAGE_CONTROL, "控制面板");
		jb5 = createButton(240, 0, 60, 60, GlobalUntils.PATH_BTN_IMAGE_RECORD, "记录模式");
		jb6 = createButton(300, 0, 60, 60, GlobalUntils.PATH_BTN_IMAGE_VERSION, "版本信息");
		jb7 = createButton(360, 0, 60, 60, GlobalUntils.PATH_BTN_IMAGE_HELP, "帮助");
		clickButton(jb1);// 系统信息
		clickButton(jb2);// 参数监控
		clickOsiOrDebugButton(jb3);// 示波器
		clickOsiOrDebugButton(jb4);// 控制面板
		clickButton(jb5);// 记录模式
		clickVersionButton(jb6);// 版本信息
		clickHelpButton(jb7);// 帮助
		panelBtn.add(jb1);
		panelBtn.add(jb2);
		panelBtn.add(jb3);
		panelBtn.add(jb4);
		panelBtn.add(jb5);
		panelBtn.add(jb6);
		panelBtn.add(jb7);
		panel.add(panelBtn, BorderLayout.WEST);

		JLabel imgLabel = new JLabel();
		imgLabel.setIcon(new ImageIcon(GlobalUntils.PATH_LOGO));
		panel.add(imgLabel, BorderLayout.EAST);
		return panel;
	}

	private void clickVersionButton(JButton button) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				VersionFrame vFrame = new VersionFrame();
				vFrame.findVersionMess();
				vFrame.setVisible(true);
			}
		});
	}

	private void clickHelpButton(JButton button) {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 打开帮助文档
				File file = new File(GlobalUntils.PATH_HELP);
				Runtime ce = Runtime.getRuntime();
				try {
					ce.exec("cmd   /c   start  " + file.getAbsolutePath());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void clickOsiOrDebugButton(JButton btn) {
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				if (button.getText().equals("示波器")) {
					OscilloscopeFrame oFrame = new OscilloscopeFrame();
					THD_OSI osi = new THD_OSI(oFrame);
					oFrame.setOSIThread(osi);
//					oFrame.add(BackImageUtills.createBackgroundPanel());//背景图
					oFrame.setVisible(true);
					oFrame.validate();
				} else if (button.getText().equals("控制面板")) {
					tDebug = new THD_DEBUG();
					DebugFrame debugFrame = DebugFrame.getdFrame();
					debugFrame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
//					debugFrame.setAlwaysOnTop(true);
					debugFrame.setLocationRelativeTo(null);
					debugFrame.setDebugThread(tDebug);
					debugFrame.setVisible(true);
					tDebug.setControlAndSatePanel(debugFrame.getControlPanel(), debugFrame.getStatePane());
					tDebug.start();
				}

			}
		});
	}

	private JLabel label;
	private JTextField tField1;
	private JTextField tField2;

	/**
	 * 通讯状态和时间显示
	 * 
	 * @return
	 */
	public JPanel createStateAndTimePanel() {
		JPanel panel = new JPanel();
		panel.setName("通讯状态");
		panel.setForeground(new Color(238, 238, 238));
		panel.setBackground(new Color(238, 238, 238));
		panel.setLayout(new BorderLayout());
		label = new JLabel("通讯中 . . .");
		label.setFont(new Font(null, Font.PLAIN, 14));
		panel.add(label, BorderLayout.WEST);
		JPanel panelE = new JPanel();
		tField1 = createTimeField("1970-1-1");
		tField2 = createTimeField("00:00:00");
		panelE.add(tField1);
		panelE.add(tField2);
		panel.add(panelE, BorderLayout.EAST);
		return panel;
	}

	/**
	 * 界面下方时间显示
	 * 
	 * @param timeText
	 * @return
	 */
	public JTextField createTimeField(String timeText) {
		JTextField tJTextField = new JTextField();
		tJTextField.setText(timeText);
		tJTextField.setFont(new Font("TimesRoman", Font.PLAIN, 14));
		tJTextField.setEditable(false);
		tJTextField.setBorder(BorderFactory.createLoweredBevelBorder());
		return tJTextField;
	}

	/**
	 * 实时刷新状态和时间
	 * 
	 * @param text
	 * @param red
	 * @param time1
	 * @param time2
	 */
	public void sendStateAndTimeNow(String text, Color color, String time1, String time2) {
		label.setText(text);
		label.setForeground(color);
		tField1.setText(time1);
		tField2.setText(time2);
	}

	public JButton createButton(int x, int y, int width, int height, String imagePath, String text) {
		JButton button = new JButton(new ImageIcon(imagePath));
		button.setText(text);
		button.setMargin(new Insets(0, 5, 0, 5));// 设置按钮间距
		button.setFocusPainted(false);// 去除按钮上细线边框
		button.setFont(new Font(null, Font.PLAIN, 13));
		button.setForeground(Color.black);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setBackground(Color.white);
		button.setToolTipText(text);// 鼠标悬停提示信息
		button.setBorderPainted(false);
		return button;
	}

	private static RecordModelPanelForJF rModelPanel;

	public JPanel buildPanel(String title) {
		if (title.equals("系统信息")) {
			SystemMessagePanel sMessagePanel = new SystemMessagePanel();
			sMessagePanel.setName(title);
			sMessagePanel.add(BackImageUtills.createBackgroundPanel());
			sMessagePanel.setBorder(BorderFactory.createTitledBorder(title));
			sMessagePanel.setOpaque(false);
			sMessagePanel.validate();
			sys = new THD_SYS();
			sys.setSystemPanel(sMessagePanel);
			sys.start();
			return sMessagePanel;
		}
		if (title.equals("参数监控")) {
			pMonitor = new ParaMonitor();
			pMonitor.setName(title);
			pMonitor.setLayout(null);
			pMonitor.add(BackImageUtills.createBackgroundPanel());
			pMonitor.setOpaque(false);
			pMonitor.validate();
			sec = new THD_SEC();
			pMonitor.setTsec(sec);
			sec.setJtableAndVect(pMonitor.getTable(), pMonitor.getVect());
			sec.start();
			return pMonitor;
		}
		if (title.equals("记录模式")) {
			if (rModelPanel != null) {
				rModelPanel.refleshLeftPanel();
				return rModelPanel;
			}
			rModelPanel = new RecordModelPanelForJF();
			rModelPanel.setName(title);
			rModelPanel.setVisible(true);
			rModelPanel.setOpaque(false);
			rModelPanel.validate();
			return rModelPanel;
		}
		return null;
	}

	public void clickButton(JButton btn) {
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				TheWholeFrame lon = TheWholeFrame.this;
				Component[] components = lon.getContentPane().getComponents();
				for (Component component : components) {
					if (component instanceof JPanel) {
						JPanel panel = (JPanel) component;
						if (panel.getName().equals("通讯状态") || panel.getName().equals("导航栏")) {
						} else {
							lon.getContentPane().remove(component);
							lon.getContentPane().validate();
							if (panel.getName().equals("系统信息")) {
								sys.stopThread();
							}
							if (panel.getName().equals("参数监控")) {
								sec.stopThread();
							}
						}

					}
				}
				lon.getContentPane().add(buildPanel(button.getToolTipText()), BorderLayout.CENTER);
				lon.getContentPane().repaint();// 重新绘制界面
				lon.getContentPane().validate();
			}
		});
	}

}