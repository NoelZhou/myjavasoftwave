package com.zt.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import com.zt.common.FileDiretory;
import com.zt.common.GlobalUntils;
import com.zt.mychart.MyOsiChartPanel;
import com.zt.osi.MyConfigDialog;
import com.zt.thread.THD_OSI;

public class OscilloscopeFrame extends JFrame implements ChangeListener {

	private static final long serialVersionUID = 1L;
	private XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
	private MyOsiChartPanel chartPanel;
	private JPanel osiNamePanel;
	private MyConfigDialog mDialog;
	private final static Color[] COLORS = {new Color(0, 0, 0),new Color(211, 211, 211), new Color(0, 0, 128), new Color(100, 149, 237), 
			new Color(0, 100, 0),new Color(0, 250, 154),new Color(255,255, 0), new Color(205, 92, 92), 
			new Color(255, 165, 0), new Color(165, 42, 42), new Color(255, 0, 0), new Color(176, 48, 96), 
			new Color(186, 85, 211),new Color(138, 43, 226), new Color(139, 139, 131), new Color(209, 238, 238),
			new Color(139, 0, 0),new Color(0, 0, 139),new Color(104, 34, 139),new Color(139, 104, 139),
			new Color(139, 34, 82),new Color(205, 16, 118),new Color(238, 0, 0),new Color(139, 54, 38),
			new Color(205, 133, 0),new Color(139, 76, 57),new Color(139, 101, 8),new Color(255, 255, 0),
			new Color(0, 255, 0),new Color(83, 134, 139),new Color(0, 205, 205),new Color(78, 238, 148),};
	private final Color TRANSPANCE = new Color(255, 255, 255, 0);// 透明色
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // 得到屏幕的尺寸
	private THD_OSI osi;
	private JButton startBtn;//开始按钮
	private JButton pauseBtn;//暂停按钮
	private JButton configBtn;//配置信息按钮
	private int bugSerial;//故障编号
	private String bugIp;//故障录波模式(网侧/机侧)ip
	private String switchFrequency;//开关频率
	private String gridIp;//网侧IP
	private String rotIp;//机侧IP
	
	public void setFrequencyAndIps(String switchFrequency,String gridIp,String rotIp){
		this.switchFrequency = switchFrequency;
		this.gridIp = gridIp;
		this.rotIp = rotIp;
	}
	
	public void setBugNumAndIp(int bugSerial,String bugIp){
		this.bugSerial = bugSerial;
		this.bugIp  =bugIp;
	}
	
	public XYSeriesCollection getxySeriesCollection() {
		return xySeriesCollection;
	}

	public OscilloscopeFrame() {
		init();// 初始化界面
	}

	public void configDialog(){
		mDialog = new MyConfigDialog(this);
		mDialog.setVisible(true);
	}
	public void init() {
		this.setLayout(new BorderLayout());// 设置布局为空
		this.setSize(screenSize.width, screenSize.height - 50);
		this.setTitle("示波器");
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setIconImage(new ImageIcon(FileDiretory.getCurrentDir() + "/images/SC.png").getImage());
		this.getContentPane().add(createToolBarAndLogoPanel(), BorderLayout.NORTH); // 添加工具条
		this.getContentPane().add(osiPanel(), BorderLayout.CENTER);// 示波器波形显示区
		// this.getContentPane().add(rightOsiSelectPanel(),
		// BorderLayout.EAST);//波形名称显示选则区域
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				configDialog();//界面打开后自动弹出配置界面
			}
			@Override
			public void windowClosing(WindowEvent e){
				osi.stopThread();//窗口关闭的时候停止线程
			}
		});
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
		panelBtn.setLayout(new FlowLayout(0, 10, 0));
		panelBtn.setBackground(Color.white);

		JLabel imgLabel = new JLabel();
		imgLabel.setIcon(new ImageIcon(GlobalUntils.PATH_LOGO));
		panelBtn.add(imgLabel);

		startBtn = createButton("Start", GlobalUntils.PATH_BTN_START);
		pauseBtn = createButton("Pause", GlobalUntils.PATH_BTN_PAUSE);
		pauseBtn.setEnabled(false);//默认不可点击
		configBtn = createButton("Config", GlobalUntils.PATH_BTN_CONFIG);
		panelBtn.add(startBtn);
		panelBtn.add(pauseBtn);
		panelBtn.add(configBtn);
		buttonActionListener();//按钮监听方法
		panel.add(panelBtn, BorderLayout.WEST);

		return panel;
	}
	/**
	 * 按钮监听
	 */
	public void buttonActionListener(){
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings({ "unchecked"})
				List<XYSeries> seriesList = xySeriesCollection.getSeries();
				for (XYSeries xySeries : seriesList) {
					xySeries.clear();
				}
				if(osi.getState().toString().equals("TERMINATED")||osi.getState().toString().equals("RUNNABLE")){
					osi = new THD_OSI(OscilloscopeFrame.this);
				}
				chartPanel.setOsi(osi);
				osi.receiveFrequencyAndIp(switchFrequency,gridIp,rotIp,stringA,stringB);//发送开关频率和ip地址到osi线程
				osi.receiveBugSerialAndIp(bugSerial,bugIp);
				osi.receiveButtons(startBtn,pauseBtn,configBtn);
				osi.start();//开启线程
				startBtn.setEnabled(false);
				pauseBtn.setEnabled(true);
				configBtn.setEnabled(false);
//				System.out.println(osi.getState().toString());
			}
		});
		pauseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					osi.stopThread();//停止线程
					startBtn.setEnabled(true);
					pauseBtn.setEnabled(false);
					configBtn.setEnabled(true);
//				System.out.println(osi.getState().toString());
			}
		});
		
		configBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearAllParams();
				configDialog();
			}
		});
	}
	public void clearAllParams() {
		switchFrequency = null;
		gridIp = null;
		rotIp = null;
		bugSerial = 0;
		bugIp = null;
		stringA = null;
		stringB = null;
	}
	public JButton createButton(String name, String filename) {
		JButton button = new JButton(new ImageIcon(filename));
		button.setText(name);
		button.setMargin(new Insets(0, 5, 0, 5));// 设置按钮间距
		button.setFocusPainted(false);// 去除按钮上细线边框
		button.setFont(new Font(null, Font.PLAIN, 13));
		button.setForeground(Color.black);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setBackground(Color.white);
		button.setToolTipText(name);// 鼠标悬停提示信息
		button.setBorderPainted(false);
		return button;
	}

	/**
	 * 示波器显示区域
	 */
	private XYDataset dataset;

	public JPanel osiPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		panel.setOpaque(true);
		panel.setLayout(null);
		panel.add(leftPanel());// 左边示波器区域
		panel.add(rightOsiSelectPanel());// 右侧波形名称显示选择
		return panel;
	}

	public JPanel leftPanel() {
		dataset = xySeriesCollection;// 曲线集
		final JFreeChart chart = createChart(dataset);
		chartPanel = new MyOsiChartPanel(chart);
		chartPanel.setName("RightPanel");
		chartPanel.setBounds(0, 0, (int) screenSize.getWidth() - 250, 600);
		chartPanel.setMouseZoomable(true, false);
		// chartPanel.setMouseWheelEnabled(true);
		chartPanel.setBorder(BorderFactory.createLoweredBevelBorder());// 凹
		chartPanel.setZoomOutlinePaint(Color.black);
		return chartPanel;
	}

	private JFreeChart createChart(final XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, true, true);

		/* 网格线和背景颜色设置 */
		XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.setBackgroundPaint(Color.white);
		xyPlot.setDomainGridlinePaint(Color.gray);
		xyPlot.setRangeGridlinePaint(Color.gray);
		/* 设置坐标轴属性 */
		/* Y轴属性设置 */
		ValueAxis vy = xyPlot.getRangeAxis();
		vy.setRange(-5000D, 5000D);
		vy.setLabelPaint(Color.black);
		vy.setLabelFont(new Font("Serif", Font.CENTER_BASELINE, 15));
		vy.setTickLabelFont(new Font("Serif", Font.CENTER_BASELINE, 13));
		vy.setAxisLineVisible(false);// 坐标轴线条是否可见
		vy.setMinorTickCount(200);
		vy.setAutoRange(false);

		/* X轴属性设置 */
		NumberAxis vx = (NumberAxis) xyPlot.getDomainAxis();
		vx.setTickLabelFont(new Font("Serif", Font.CENTER_BASELINE, 13));
		vx.setLabelFont(new Font("Serif", Font.CENTER_BASELINE, 15));
		vx.setLabelInsets(new RectangleInsets(0, 0, 0, 50));
		 vx.setFixedAutoRange(1000D);
		vx.setAutoRange(true);
//		vx.setLowerBound(0);
//		vx.setUpperBound(1000);
		vx.setAutoTickUnitSelection(true);
		vx.setAxisLineVisible(false);
		/* 设置曲线颜色 */
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();     // 能够得到renderer对象，就可以使用此方法设置颜色
		for (int i=0;i<COLORS.length;i++) {
			renderer.setSeriesPaint(i, COLORS[i]); // 设置第一条折现的颜色，以此类推。
//			renderer.setSeriesStroke(i, new BasicStroke(2.0F));
		}
		return chart;
	}

	/**
	 * 右侧波形名称显示区域
	 * 
	 * @return
	 */
	public JPanel rightOsiSelectPanel() {
		osiNamePanel = new JPanel();
		osiNamePanel.setLayout(null);
		osiNamePanel.setBackground(Color.white);
		osiNamePanel.setBounds((int) screenSize.getWidth() - 250, 0, 250, 600);
		osiNamePanel.setBorder(BorderFactory.createLoweredBevelBorder());// 凹
		addXYSlider();
		osiNameArea();
		return osiNamePanel;
	}

	public void addXYSlider(){
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		panel.setName("XY轴范围");
		panel.setLayout(null);
		panel.setBounds(10, 10, 240, 90);
		
		JLabel xLabel = new JLabel("水平调节");
		xLabel.setBounds(20, 0, 200, 20);
		panel.add(xLabel);
		JSlider xSlider = new JSlider(SwingConstants.HORIZONTAL, 10, 15000, 1000);
		xSlider.setName("X");
		xSlider.setBackground(Color.white);
		xSlider.setBounds(20, 20, 190, 20);
		xSlider.addChangeListener(this);//监听滑块值改变
		panel.add(xSlider);
		
		JLabel yLabel = new JLabel("垂直调节");
		yLabel.setBounds(20, 50, 200, 20);
		panel.add(yLabel);
		JSlider ySlider = new JSlider(SwingConstants.HORIZONTAL, 10, 12800, 5000);
		ySlider.setName("Y");
		ySlider.setBackground(Color.white);
		ySlider.setBounds(20, 70, 190, 20);
		ySlider.addChangeListener(this);//监听滑块值改变
		panel.add(ySlider);
		
		osiNamePanel.add(panel);
	}
	
	private JPanel gridAndRotOsiName;
	public JPanel getGridAndRotOsiName(){
		return this.gridAndRotOsiName;
	}
	public void osiNameArea() {
		gridAndRotOsiName = new JPanel();
		gridAndRotOsiName.setBackground(Color.white);
		gridAndRotOsiName.setName("波形");
		gridAndRotOsiName.setLayout(null);
		gridAndRotOsiName.setBounds(10, 110, 240, 490);
//		panel.setBorder(BorderFactory.createLoweredBevelBorder());// 凹
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
		osiNamePanel.add(gridAndRotOsiName);
		
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
				XYPlot xyPlot = (XYPlot)chartPanel.getChart().getPlot();
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

	public void setOSIThread(THD_OSI osi) {
		this.osi = osi;
	}

	
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider)e.getSource();
		JFreeChart jChart = chartPanel.getChart();
		XYPlot xyPlot = (XYPlot) jChart.getPlot();
		if(slider.getName().equals("X")){
			double up = (double)slider.getValue();
			ValueAxis vx = xyPlot.getDomainAxis();
			vx.setFixedAutoRange(up);
		}else if(slider.getName().equals("Y")) {
			double up = (double)slider.getValue();
			double lo = -up;
			ValueAxis vy = xyPlot.getRangeAxis();
			vy.setRange(lo, up);
		}
	}
	private String[] stringA;
	private String[] stringB;
	public void setCodeAAndB(String[] stringA, String[] stringB) {
		this.stringA = stringA;
		this.stringB = stringB;
	}

	public MyOsiChartPanel getChartPanel() {
		return chartPanel;
	}
}
