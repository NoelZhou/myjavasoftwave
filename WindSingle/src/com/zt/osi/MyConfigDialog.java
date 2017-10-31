package com.zt.osi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.zt.common.FileDiretory;
import com.zt.custom.MyOsiTableModel;
import com.zt.frame.OscilloscopeFrame;
import com.zt.panel.TableCellListener;

public class MyConfigDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private OscilloscopeFrame oscilloscopeFrame;

	public MyConfigDialog(OscilloscopeFrame oscilloscopeFrame) {
		super(oscilloscopeFrame, true);
		this.oscilloscopeFrame = oscilloscopeFrame;
		init();// 初始化界面
	}

	public void init() {
		this.setLayout(null);
		this.setSize(400, 700);
		this.setLocationRelativeTo(null);
		this.setModal(true);// 模态窗口
		this.setResizable(false);
		this.setIconImage(new ImageIcon(FileDiretory.getCurrentDir() + "/images/SC.png").getImage());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.getContentPane().add(partOne());
		this.getContentPane().add(partSecond());
		this.getContentPane().add(partThird());

		/**************** 确定及取消按钮 *******************/
		JButton sureBtn = new JButton("确定");
		sureBtn.setBounds(120, 630, 60, 25);
		sureBtn.addActionListener(this);
		sureBtn.setActionCommand("1");
		this.getContentPane().add(sureBtn);

		JButton cancelBtn = new JButton("取消");
		cancelBtn.setBounds(220, 630, 60, 25);
		cancelBtn.addActionListener(this);
		cancelBtn.setActionCommand("0");
		this.getContentPane().add(cancelBtn);

	}

	/**
	 * 第一部分 配置选项
	 */
	private JComboBox<String> jBox1;// 模式
	private JComboBox<String> jBox2;// 开关频率
	private JComboBox<String> jBox3;// 故障波形
	private JTextField textFieldBN;// 故障编号
	private JTextField gridIp;// 网侧ip
	private JTextField rotIp;// 机侧ip

	public JPanel partOne() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.lightGray), "配置选项"));
		panel.setBounds(0, 10, 395, 120);
		/************* 模式 ***************/
		panel.add(commonLabel("模式:", 0, 25, 70, 20));
		jBox1 = new JComboBox<>();
		jBox1.setModel(new DefaultComboBoxModel<String>(new String[] { "示波器模式", "故障录波模式" }));
		jBox1.setBounds(70, 25, 120, 20);
		panel.add(jBox1);
		/************* 开关频率 ***************/
		panel.add(commonLabel("开关频率:", 220, 25, 70, 20));
		jBox2 = new JComboBox<>();
		jBox2.setModel(new DefaultComboBoxModel<String>(new String[] { "2K", "2.5K", "3K" }));
		jBox2.setBounds(290, 25, 55, 20);
		panel.add(jBox2);
		/************* 故障波形 ***************/
		panel.add(commonLabel("故障波形:", 0, 55, 70, 20));
		jBox3 = new JComboBox<>();
		jBox3.setModel(new DefaultComboBoxModel<String>(new String[] { "网侧波形", "机侧波形" }));
		jBox3.setBounds(70, 55, 120, 20);
		panel.add(jBox3);
		/************* 故障编号 ***************/
		panel.add(commonLabel("故障编号:", 220, 55, 70, 20));
		textFieldBN = commonTextField("1", 290, 55, 55, 20);
		panel.add(textFieldBN);
		/************* 网侧ip 、机侧ip ***************/
		panel.add(commonLabel("网侧IP:", 0, 85, 70, 20));
		gridIp = commonTextField("192.168.68.237", 70, 85, 100, 20);
		panel.add(gridIp);
		panel.add(commonLabel("机侧IP:", 220, 85, 70, 20));
		rotIp = commonTextField("192.168.68.238", 290, 85, 100, 20);
		panel.add(rotIp);
		return panel;
	}

	/**
	 * 第二部分 网侧机侧编号选择
	 * 
	 * @return
	 */
	private String[] osiNumber = { "网侧波形编号", "机侧波形编号" };
	private JPanel gridPanel;// 网侧波形编号
	private JPanel rotPanel;// 机侧波形编号

	public JPanel partSecond() {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		panel.setBounds(0, 140, 395, 225);
		/**************** 网侧波形编号 ***********************/
		gridPanel = osiSerialNumberPanel("grid", 10, 10, 385, 100, 0);
		panel.add(gridPanel);
		/**************** 机侧波形编号 ***********************/
		rotPanel = osiSerialNumberPanel("rot", 10, 110, 385, 100, 1);
		panel.add(rotPanel);
		return panel;
	}

	/**
	 * 波形编号
	 * 
	 * @param name
	 * @param px
	 * @param py
	 * @param pWidth
	 * @param pHeight
	 * @param type
	 * @return
	 */
	public JPanel osiSerialNumberPanel(String name, int px, int py, int pWidth, int pHeight, int type) {
		JPanel panel = new JPanel();
		panel.setName(name);
		panel.setLayout(null);
		panel.setBounds(px, py, pWidth, pHeight);
		for (int i = 0; i < 8; i++) {
			int x = 10 + 90 * (i % 4);
			int y = 50 * (i / 4);
			panel.add(commonLabel(osiNumber[type] + (i + 1), x, y, 95, 20));
			panel.add(commonTextField("1", x, y + 25, 40, 25));
		}
		return panel;
	}

	/**
	 * 第三部分 波形及编号集合
	 */
	public JTabbedPane partThird() {
		JTabbedPane tPane = new JTabbedPane(JTabbedPane.TOP);
		tPane.setBounds(0, 370, 400, 250);
		tPane.add(osiTable(), "波形及编号");
		return tPane;
	}

	/**
	 * 波形及编号表格
	 */
	private Object[][] dataVector = { {new Boolean(true),"1.电网电压Uab", "1.电网电压Uab" }, { new Boolean(true),"2.电网电压Ubc", "2.电网电压Ubc" },
			{ new Boolean(true),"3.网侧A相电流", "3.定子线电压Uuv" }, { new Boolean(true),"4.网侧B相电流", "4.定子线电压Uuw" }, { new Boolean(true),"5.网侧C相电流", "5.定子U相电流" },
			{ new Boolean(true),"6.母线电压", "6.定子V相电流" }, { new Boolean(true),"7.滤波电容电流(相)", "7.定子W相电流" }, { new Boolean(true),"8.网侧A相占空比", "8.转子K相电流" },
			{ new Boolean(true),"9.网侧B相占空比", "9.转子L相电流" }, { new Boolean(true),"10.网侧C相占空比", "10.转子M相电流" }, { new Boolean(true),"11.电网锁相角度", "11.母线电压" },
			{ new Boolean(true),"12.电网频率", "12.机侧M相占空比" }, { new Boolean(true),"13.锁相环角频率", "13.转子三相电流和" }, { new Boolean(true),"14.电网故障标志", "14.定子三相电流和" },
			{ new Boolean(true),"15.电网电压有效值", "15.定子d轴电流" }, { new Boolean(true),"16.电网正序分量", "16.定子q轴电流" }, { new Boolean(true),"17.电网负序分量", "17.发电机转速" },
			{ new Boolean(true),"18.直流撬棒触发信号", "18.编码器脉冲计数" }, { new Boolean(true),"19.直流撬棒FA状态", "19.Z信号保护计数" }, { new Boolean(true),"20.网侧三相电流之和", "20.电网故障标志" },
			{ new Boolean(true),"21.无功电流给定", "21.交流撬棒电流" }, { new Boolean(true),"22.电压调节器给定", "22.交流撬棒触发状态" }, { new Boolean(true),"23.电压调节器给出", "23.交流撬棒FA状态" },
			{ new Boolean(true),"24.网侧正序电流q轴给定", "24.转子d轴正序电流给定" }, { new Boolean(true),"25.网侧负序电流d轴反馈", "25.转子q轴正序电流给定" },
			{ new Boolean(true),"26.网侧负序电流q轴反馈", "26.转子d轴正序电流反馈" }, { new Boolean(true),"27.网侧正序电流d轴反馈", "27.转子q轴正序电流反馈" }, { new Boolean(true),"28.网侧正序电流q轴反馈", "28.转矩给定" },
			{ new Boolean(true),"29.网侧主接触器状态", "29.转矩反馈" }, { new Boolean(true),"30.急停信号", "30.定子接触器状态" }, { new Boolean(true),"31.主控制字", "31.主控制字" },
			{ new Boolean(true),"32.主状态字", "32.主状态字" }, };
	private JTable table;// 波形及编号表格
	public JScrollPane osiTable() {
		DefaultTableModel dModel = new DefaultTableModel();
		dModel.setDataVector(dataVector, new String[] {"","网侧波形及编号", "机侧波形及编号" });
		table = new JTable(dModel);
		table.setBorder(BorderFactory.createRaisedBevelBorder());
		table.setRowHeight(30);
		table.setEnabled(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		JScrollPane jPane = new JScrollPane(table);
		jPane.setBounds(0, 350, 390, 250);
		jPane.getViewport().setBackground(Color.white);
		jPane.setAutoscrolls(true);
		jPane.setBorder(BorderFactory.createRaisedBevelBorder());
		return jPane;
	}

	public JLabel commonLabel(String text, int x, int y, int width, int height) {
		JLabel label = new JLabel(text);
		label.setBounds(x, y, width, height);
		if (text.length() >= 6) {
			label.setHorizontalAlignment(SwingConstants.LEFT);
		} else {
			label.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return label;
	}

	public JTextField commonTextField(String text, int x, int y, int width, int height) {
		JTextField jTextField = new JTextField(text);
		jTextField.setBounds(x, y, width, height);
		jTextField.setBorder(BorderFactory.createLoweredBevelBorder());
		return jTextField;
	}
	private final static Color[] COLORS = {new Color(0, 0, 0),new Color(211, 211, 211), new Color(0, 0, 128), new Color(100, 149, 237), 
			new Color(0, 100, 0),new Color(0, 250, 154),new Color(255,255, 0), new Color(205, 92, 92), 
			new Color(255, 165, 0), new Color(165, 42, 42), new Color(255, 0, 0), new Color(176, 48, 96), 
			new Color(186, 85, 211),new Color(138, 43, 226), new Color(139, 139, 131), new Color(209, 238, 238),
			new Color(139, 0, 0),new Color(0, 0, 139),new Color(104, 34, 139),new Color(139, 104, 139),
			new Color(139, 34, 82),new Color(205, 16, 118),new Color(238, 0, 0),new Color(139, 54, 38),
			new Color(205, 133, 0),new Color(139, 76, 57),new Color(139, 101, 8),new Color(255, 255, 0),
			new Color(0, 255, 0),new Color(83, 134, 139),new Color(0, 205, 205),new Color(78, 238, 148),};
	private final Color TRANSPANCE = new Color(255, 255, 255, 0);// 透明色
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("1")) {
			int modelType = jBox1.getSelectedIndex();
			if (modelType == 0) {// 示波器模式
				JPanel panel = oscilloscopeFrame.getGridAndRotOsiName();// 获取示波器界面波形区域
				panel.removeAll();
				loadNewPanel(panel);//重新画panel
				XYSeriesCollection xySeriesCollection = oscilloscopeFrame.getxySeriesCollection();
				xySeriesCollection.removeAllSeries();
				List<Integer> girdIntegers = getSerialList(gridPanel);
				List<Integer> rotIntegers = getSerialList(rotPanel);
				int gridSerial = 0;
				int rotSerial = 0;
				int tmpG = 0;//
				int tmpR = 0;//
				String[] stringA = new String[8];
				String[] stringB = new String[8];
				for (int i = 0; i < panel.getComponentCount(); i++) {
					Component comp = panel.getComponent(i);
					if (comp instanceof JTextField) {
						JTextField tmpTF = (JTextField) comp;
						if (tmpTF.getName().equals("网侧波形")) {
							if(tmpG<girdIntegers.size()){
								String text = (String) table.getValueAt((Integer)girdIntegers.toArray()[gridSerial] - 1, 1);
								tmpTF.setText(text);
								xySeriesCollection.addSeries(new XYSeries("网侧-"+text.split("\\.")[1]+"_"+gridSerial));
								stringA[gridSerial]= text.split("\\.")[0];
								gridSerial++;
							}
							tmpG++;
						} else {
							if(tmpR<rotIntegers.size()){
								String text = (String) table.getValueAt((Integer)rotIntegers.toArray()[rotSerial] - 1, 2);
								tmpTF.setText(text);
								xySeriesCollection.addSeries(new XYSeries("机侧-"+text.split("\\.")[1]+"_"+rotSerial));
								stringB[rotSerial]= text.split("\\.")[0];
								rotSerial++;
							}
							tmpR++;
						}
					}
				}
				oscilloscopeFrame.setCodeAAndB(stringA,stringB);
				oscilloscopeFrame.setFrequencyAndIps((String) jBox2.getSelectedItem(), gridIp.getText(),
						rotIp.getText());
				oscilloscopeFrame.repaint();// 刷新示波器界面
			} else if (modelType == 1) {// 故障录波模式
				JPanel panel = oscilloscopeFrame.getGridAndRotOsiName();// 获取示波器界面波形区域
				panel.removeAll();
				XYSeriesCollection xySeriesCollection = oscilloscopeFrame.getxySeriesCollection();
				xySeriesCollection.removeAllSeries();
				MyOsiTableModel osiTableModel = new MyOsiTableModel(dataVector);
				JTable jTable = new JTable(osiTableModel) {
					private static final long serialVersionUID = 1L;
					public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
						Component component = super.prepareRenderer(renderer, row, column);
						component.setFont(new Font("Dialog", Font.BOLD, 13));
						component.setBackground(new Color(0, 0, 0, 10));
						if (row == 0&&column!=0) {component.setForeground(COLORS[0]);}
						if (row == 1&&column!=0) {component.setForeground(COLORS[1]);}
						if (row == 2&&column!=0) {component.setForeground(COLORS[2]);}
						if (row == 3&&column!=0) {component.setForeground(COLORS[3]);}
						if (row == 4&&column!=0) {component.setForeground(COLORS[4]);}
						if (row == 5&&column!=0) {component.setForeground(COLORS[5]);}
						if (row == 6&&column!=0) {component.setForeground(COLORS[6]);}
						if (row == 7&&column!=0) {component.setForeground(COLORS[7]);}
						if (row == 8&&column!=0) {component.setForeground(COLORS[8]);}
						if (row == 9&&column!=0) {component.setForeground(COLORS[9]);}
						if (row == 10&&column!=0) {component.setForeground(COLORS[10]);}
						if (row == 11&&column!=0) {component.setForeground(COLORS[11]);}
						if (row == 12&&column!=0) {component.setForeground(COLORS[12]);}
						if (row == 13&&column!=0) {component.setForeground(COLORS[13]);}
						if (row == 14&&column!=0) {component.setForeground(COLORS[14]);}
						if (row == 15&&column!=0) {component.setForeground(COLORS[15]);}
						if (row == 16&&column!=0) {component.setForeground(COLORS[16]);}
						if (row == 17&&column!=0) {component.setForeground(COLORS[17]);}
						if (row == 18&&column!=0) {component.setForeground(COLORS[18]);}
						if (row == 19&&column!=0) {component.setForeground(COLORS[19]);}
						if (row == 20&&column!=0) {component.setForeground(COLORS[20]);}
						if (row == 21&&column!=0) {component.setForeground(COLORS[21]);}
						if (row == 22&&column!=0) {component.setForeground(COLORS[22]);}
						if (row == 23&&column!=0) {component.setForeground(COLORS[23]);}
						if (row == 24&&column!=0) {component.setForeground(COLORS[24]);}
						if (row == 25&&column!=0) {component.setForeground(COLORS[25]);}
						if (row == 26&&column!=0) {component.setForeground(COLORS[26]);}
						if (row == 27&&column!=0) {component.setForeground(COLORS[27]);}
						if (row == 28&&column!=0) {component.setForeground(COLORS[28]);}
						if (row == 29&&column!=0) {component.setForeground(COLORS[29]);}
						if (row == 30&&column!=0) {component.setForeground(COLORS[30]);}
						if (row == 31&&column!=0) {component.setForeground(COLORS[31]);}
						return component;
					}
				};
				columnChange(jTable.getColumnModel(), 0, 20);
				if (jBox3.getSelectedItem().equals("网侧波形")) {
					columnChange(jTable.getColumnModel(), 2, 0);
					for(int i=0;i<dataVector.length;i++){
						xySeriesCollection.addSeries(new XYSeries((String)dataVector[i][1]));
					}
					oscilloscopeFrame.setBugNumAndIp(Integer.valueOf(textFieldBN.getText()), gridIp.getText());//传递故障编号和ip地址到示波器界面
				}else {
					columnChange(jTable.getColumnModel(), 1, 0);
					for(int i=0;i<dataVector.length;i++){
						xySeriesCollection.addSeries(new XYSeries((String)dataVector[i][2]));
					}
					oscilloscopeFrame.setBugNumAndIp(Integer.valueOf(textFieldBN.getText()), rotIp.getText());//传递故障编号和ip地址到示波器界面
				}
				jTable.setBorder(BorderFactory.createRaisedBevelBorder());
				jTable.setRowHeight(30);//监听表格参数变化
				addDataChangeListener(jTable);
				JScrollPane jPane = new JScrollPane(jTable);
				jPane.setBounds(0, 0, 240, 490);
				jPane.getViewport().setBackground(Color.white);
				jPane.setAutoscrolls(true);
				jPane.setBorder(BorderFactory.createRaisedBevelBorder());
				panel.add(jPane);
				oscilloscopeFrame.repaint();// 刷新示波器界面
			}
			this.dispose();// 关闭窗口
		} else if (e.getActionCommand().equals("0")) {
			this.dispose();// 关闭窗口
		}
	}
	
	public void loadNewPanel(JPanel gridAndRotOsiName){
		JLabel label1 = new JLabel("网侧波形");
		label1.setBackground(Color.lightGray);
		label1.setOpaque(true);
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		label1.setBounds(60, 0, 150, 25);
		gridAndRotOsiName.add(label1);
		for (int i = 1; i <= 8; i++) {
			int x = 20;
			int y = 25 * i;
			Rectangle rectangleButL = new Rectangle(x, y+5, 30, 20);
			Rectangle rectangleText = new Rectangle(x + 40, y, 150, 25);
			JButton tmpButtonL = createButton(String.valueOf(i), "1", null, rectangleButL, COLORS[i - 1], true);
			JTextField tmpField = createTextField(null, rectangleText);
			tmpField.setName("网侧波形");
			gridAndRotOsiName.add(tmpButtonL);
			gridAndRotOsiName.add(tmpField);
		}
		
		JLabel label2 = new JLabel("机侧波形");
		label2.setBackground(Color.lightGray);
		label2.setOpaque(true);
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		label2.setBounds(60, 225, 150, 25);
		gridAndRotOsiName.add(label2);
		for (int i = 9; i <= 16; i++) {
			int x = 20;
			int y = 250+25 * (i-9);
			Rectangle rectangleButL = new Rectangle(x, y+5, 30, 20);
			Rectangle rectangleText = new Rectangle(x + 40, y, 150, 25);
			JButton tmpButtonL = createButton(String.valueOf(i), "1", null, rectangleButL, COLORS[i - 1], true);
			JTextField tmpField = createTextField(null, rectangleText);
			tmpField.setName("机侧波形");
			gridAndRotOsiName.add(tmpButtonL);
			gridAndRotOsiName.add(tmpField);
		}
	}
	
	private void addDataChangeListener(final JTable jTable) {
		// 检测单元格数据变更
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				TableCellListener tcl = (TableCellListener) e.getSource();
				int row = tcl.getRow();
				int col = tcl.getColumn();
				Object oldValue = tcl.getOldValue();
				Object newValue = tcl.getNewValue();
				if(col==0){
					XYPlot xyPlot = (XYPlot)oscilloscopeFrame.getChartPanel().getChart().getPlot();
					XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();//获取曲线
					if(!(boolean) newValue){
						renderer.setSeriesPaint(row, TRANSPANCE);
					}
					if((boolean) newValue){
						renderer.setSeriesPaint(row, COLORS[row]);
					}
				}
				System.out.printf("cell changed at [%d,%d] : %s -> %s%n", row, col, oldValue, newValue);
			}
		};
		@SuppressWarnings("unused")
		TableCellListener tcl1 = new TableCellListener(jTable, action);
		System.out.printf("cell changed%n");
	}
	
	public void columnChange(TableColumnModel dcm,int i,int width){
		dcm.getColumn(i).setPreferredWidth(width);
		dcm.getColumn(i).setMinWidth(width);
		dcm.getColumn(i).setMaxWidth(width);
	}
	public List<Integer> getSerialList(JPanel panel) {
		int num = panel.getComponentCount();
		List<Integer> lIntegers = new ArrayList<Integer>();
		for (int i = 0; i < num; i++) {
			Component comp = panel.getComponent(i);
			if (comp instanceof JTextField) {
				JTextField tmpTF = (JTextField) comp;
				lIntegers.add(Integer.valueOf(tmpTF.getText()));
			}
		}
		
//		Set<Integer> set = new HashSet<Integer>();
//		for(int i=0;i<lIntegers.size();i++){
//			set.add(lIntegers.get(i));
//		}
		return lIntegers;
	}
	public JButton createButton(String text, String commond, String name, Rectangle r, Color bg, Boolean boolean1) {
		JButton button = new JButton(text);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setActionCommand(commond);
		button.setName(name);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setBounds(r);
		button.setForeground(Color.white);
		button.setBackground(bg);
		button.setEnabled(boolean1);
		button.setBorder(BorderFactory.createRaisedBevelBorder());
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton)e.getSource();
				int buttonText = Integer.valueOf(button.getText());
				XYPlot xyPlot = (XYPlot)oscilloscopeFrame.getChartPanel().getChart().getPlot();
				XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();//获取曲线
				if(button.getActionCommand().equals("1")){
					button.setActionCommand("0");
					button.setBorder(BorderFactory.createLoweredBevelBorder());
					renderer.setSeriesPaint(buttonText-1, TRANSPANCE);
				}else if(button.getActionCommand().equals("0")) {
					button.setActionCommand("1");
					button.setBorder(BorderFactory.createRaisedBevelBorder());
					renderer.setSeriesPaint(buttonText-1, COLORS[buttonText-1]);
				}
			}
		});
		return button;
	}

	public JTextField createTextField(String text, Rectangle r) {
		JTextField tField = new JTextField(text);
		tField.setBounds(r);
		tField.setOpaque(true);
		tField.setEditable(false);
		tField.setBorder(BorderFactory.createLoweredBevelBorder());// 凹
		return tField;
	}
}
