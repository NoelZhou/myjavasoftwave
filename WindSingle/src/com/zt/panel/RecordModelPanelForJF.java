package com.zt.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import com.zt.common.OutExcel;
import com.zt.mychart.MyChartPanel;
import com.zt.pojo.SaveMessage;

public class RecordModelPanelForJF extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private TimeSeriesCollection tSeriesCollection= new TimeSeriesCollection();
	private JButton startButton;//开始按钮
    private JButton oputButton;//结束按钮
    private MyChartPanel chartPanel;
	private Timer timer;
//	private List<?> listSeries;
	private final static int ONE_MILLISECOND = 1000;
	private final static Color[] COLORS = {Color.BLACK,Color.BLUE,Color.RED,Color.green,Color.CYAN,Color.YELLOW,
											Color.MAGENTA,Color.ORANGE,Color.PINK,Color.DARK_GRAY,Color.LIGHT_GRAY,Color.GRAY};
	private final Color TRANSPANCE = new Color(255, 255, 255, 0);//透明色
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // 得到屏幕的尺寸
	private SaveMessage saveMessage = SaveMessage.getInstance();
	private List<String> lParams = saveMessage.getParamsForRecordModel();
	public RecordModelPanelForJF() {
		buildUI();
	}
	
	public void buildUI(){
		setLayout(null);//设置布局为空
		RightPanel();//右侧波形显示
		LeftPanel();//左侧
		TimerThread();//实时
		
	}
	
	private static JTextField field1;//YMax
	private static JTextField field2;//YMin
	private static JTextField field3;//X轴区间
	private List<Color> lColors = new ArrayList<>();//颜色收集器
	public void LeftPanel(){
		
		JPanel lPanel = new JPanel();
		lPanel.setName("LeftPanel");
		lPanel.setLayout(null);
		lPanel.setBounds(0, 0, 300, 600);
//		lPanel.setBackground(Color.white);
		
		createSPanel(lPanel);//创建曲线Panel
		
		startButton = new JButton("开始");
		startButton.setActionCommand("10");
		startButton.setBounds(10, 500, 60, 30);
		startButton.setMargin(new Insets(0, 0, 0, 0));
		startButton.addActionListener(this);
		oputButton = new JButton("导出");
		oputButton.setActionCommand("out");
		oputButton.setBounds(80, 500, 60, 30);
		oputButton.setMargin(new Insets(0, 0, 0, 0));
        oputButton.addActionListener(this);
        lPanel.add(startButton);
        lPanel.add(oputButton);
        
        JLabel label1 = new JLabel("YMax:");
        label1.setBounds(10, 540, 40, 20);
        lPanel.add(label1);
        field1 = new JTextField();
        field1.setText("1500");
        field1.setBorder(BorderFactory.createLoweredBevelBorder());//凹
        field1.setBounds(50, 540, 60, 20);
        lPanel.add(field1);
        
        JLabel label2 = new JLabel("YMin:");
        label2.setBounds(130, 540, 40, 20);
        lPanel.add(label2);
        field2 = new JTextField();
        field2.setText("-1500");
        field2.setBorder(BorderFactory.createLoweredBevelBorder());//凹
        field2.setBounds(170, 540, 60, 20);
        lPanel.add(field2);
        
        JButton button = new JButton("设置");
        button.setBounds(240, 540, 50, 20);
        button.setMargin(new Insets(0, 0, 0, 0));
        lPanel.add(button);
        button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double up = (double)Integer.valueOf(field1.getText());
				double lo = (double)Integer.valueOf(field2.getText());
				JFreeChart jChart = chartPanel.getChart();
				XYPlot xyPlot = (XYPlot) jChart.getPlot();
				ValueAxis vy = xyPlot.getRangeAxis();
				vy.setRange(lo, up);
			}
		});
        
        JLabel label3 = new JLabel("X轴区间(Min):");
        label3.setBounds(10, 570, 100, 20);
        lPanel.add(label3);
        field3 = new JTextField();
        field3.setText("15");
        field3.setBorder(BorderFactory.createLoweredBevelBorder());//凹
        field3.setBounds(120, 570, 60, 20);
        lPanel.add(field3);
        JButton button3 = new JButton("设置");
        button3.setBounds(240, 570, 50, 20);
        button3.setMargin(new Insets(0, 0, 0, 0));
        lPanel.add(button3);
        button3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double interval = (double)Integer.valueOf(field3.getText());
				JFreeChart jChart = chartPanel.getChart();
				XYPlot xyPlot = (XYPlot) jChart.getPlot();
				ValueAxis vy = xyPlot.getDomainAxis();
				vy.setFixedAutoRange(interval*60*1000);
			}
		});
        add(lPanel);
	}
	
	public void createSPanel(JPanel lPanel){
		JPanel seriesPanel = new JPanel();
		seriesPanel.setBounds(0, 0, 300, 500);
		seriesPanel.setLayout(null);
		buildSeriesPanel(seriesPanel);
		lPanel.add(seriesPanel);
	}
	
	public void buildSeriesPanel(JPanel panel){
//		TimeSeriesCollection tmpSeriesCol = new TimeSeriesCollection();
//		excelDatas = new ArrayList<>();//初始化导出excel数据
		tSeriesCollection.removeAllSeries();
		XYPlot xyPlot = (XYPlot)chartPanel.getChart().getPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();//获取曲线
		for(int i=1;i<=12;i++){
			int x = 10;
			int y = 40*i-30;
			Rectangle rectangleButL = new Rectangle(x, y, 30, 30);
			Rectangle rectangleButR = new Rectangle(x+200, y, 60, 30);
			Rectangle rectangleText = new Rectangle(x+40, y, 150, 30);
			if((!lParams.isEmpty())&&lParams.size()>=i){
				renderer.setSeriesPaint(i-1, COLORS[i-1]);//设置曲线颜色
				JButton tmpButtonL = createButton(String.valueOf(i),"1",lParams.get(i-1).split("-")[1], rectangleButL, COLORS[i-1],true);
				JTextField tmpField = createTextField(lParams.get(i-1), rectangleText);
				JButton tmpButtonR = createButton("删除","delete",lParams.get(i-1).split("-")[1], rectangleButR, Color.GRAY,true);
				lColors.add(tmpButtonL.getBackground());
				TimeSeries series = new TimeSeries(lParams.get(i-1).split("-")[1]);
				tSeriesCollection.addSeries(series);
//				tmpSeriesCol.addSeries(series);
				panel.add(tmpButtonL);
				panel.add(tmpField);
				panel.add(tmpButtonR);
			}else {
				panel.add(createButton(String.valueOf(i),"1",null, rectangleButL, Color.lightGray,false));
				panel.add(createButton("删除",null,null, rectangleButR, Color.lightGray,false));
				panel.add(createTextField(null, rectangleText));
			}
		}
//		listSeries = tmpSeriesCol.getSeries();
	}
	
	public void refleshLeftPanel(){
		int len = this.getComponentCount();
		for(int i =0;i<len;i++){
			Component comp = this.getComponent(i);
			if(comp instanceof JPanel){
				JPanel panel = (JPanel)comp;
				if(panel.getName().equals("LeftPanel")){
					int len2 = panel.getComponentCount();
					for(int j=0;j<len2;j++){
						Component comp2 = panel.getComponent(j);
						if(comp2 instanceof JPanel){
							panel.remove(comp2);
							createSPanel(panel);
						}
					}
				}
			}
		}
	}
	
	public JButton createButton(String text,String commond,String name,Rectangle r,Color bg,Boolean boolean1){
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
				XYPlot xyPlot = (XYPlot)chartPanel.getChart().getPlot();
				XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();//获取曲线
				int seriesIndex = 0;
				for(int i=0;i<tSeriesCollection.getSeriesCount();i++){
					TimeSeries tmp =(TimeSeries)tSeriesCollection.getSeries().get(i);
					if(tmp.getKey().equals(button.getName())){
						seriesIndex = i;
						break;
					}
				}
				if(button.getActionCommand().equals("1")){
					button.setActionCommand("0");
					button.setBorder(BorderFactory.createLoweredBevelBorder());
					renderer.setSeriesPaint(seriesIndex, TRANSPANCE);
				}else if(button.getActionCommand().equals("0")) {
					button.setActionCommand("1");
					button.setBorder(BorderFactory.createRaisedBevelBorder());
					renderer.setSeriesPaint(seriesIndex, lColors.get(seriesIndex));
				}else if(button.getActionCommand().equals("delete")) {
					deleteSeries(button.getName());
					lParams.remove(seriesIndex);
					tSeriesCollection.removeSeries((TimeSeries)tSeriesCollection.getSeries().get(seriesIndex));
					Color color = lColors.get(seriesIndex);
					lColors.remove(color);
					System.out.println(lColors);
					for(int j=0;j<lColors.size();j++){
						renderer.setSeriesPaint(j, lColors.get(j));
					}
					saveMessage.setParamsForRecordModel(lParams);//更新参数通道
				}
				
			}
		});
		return button;
	}
	
	public void deleteSeries(String name){
		int len = this.getComponentCount();
		for(int i =0;i<len;i++){
			Component comp = this.getComponent(i);
			if(comp instanceof JPanel){
				JPanel panel = (JPanel)comp;
				if(panel.getName().equals("LeftPanel")){
					int len2 = panel.getComponentCount();
					for(int j=0;j<len2;j++){
						Component comp2 = panel.getComponent(j);
						if(comp2 instanceof JPanel){
							JPanel panel2 = (JPanel)comp2;
							int len3 = panel2.getComponentCount();
							for(int z=0;z<len3;z++){
								Component comp3 = panel2.getComponent(z);
								if(comp3 instanceof JButton){
									JButton jb = (JButton)comp3;
									if(jb.getName()!=null&&jb.getName().equals(name)){
										jb.setEnabled(false);
										jb.setBackground(Color.lightGray);
									}
								}
								if(comp3 instanceof JTextField){
									JTextField jtf = (JTextField)comp3;
									if(!jtf.getText().isEmpty()){
										String text = jtf.getText().split("-")[1];
										if(text.equals(name)){
											jtf.setText(null);
										}
									}
									
								}
							}
						}
					}
				}
			}
		}
	}
	
	public JTextField createTextField(String text,Rectangle r){
		JTextField tField = new JTextField(text);
		tField.setBounds(r);
		tField.setOpaque(false);
		tField.setEditable(false);
		tField.setBorder(BorderFactory.createLoweredBevelBorder());//凹
		return tField;
	}
	
	private XYDataset dataset;
	public void RightPanel(){
		dataset = tSeriesCollection;//曲线集
		final JFreeChart chart = createChart(dataset);
		chartPanel = new MyChartPanel(chart);
		chartPanel.setName("RightPanel");
		chartPanel.setBounds(300, 0,(int) screenSize.getWidth() - 300, 600);
		chartPanel.setPreferredSize(new java.awt.Dimension(1000, 500));
		chartPanel.setMouseZoomable(true, false);
//		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setBorder(BorderFactory.createLoweredBevelBorder());//凹
		chartPanel.setZoomOutlinePaint(Color.black);
		add(chartPanel);
	}

	private JFreeChart createChart(final XYDataset dataset) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null, null, dataset, false, true, true);
		/* 网格线和背景颜色设置 */
		XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.setBackgroundPaint(Color.white);
		xyPlot.setDomainGridlinePaint(Color.gray);
		xyPlot.setRangeGridlinePaint(Color.gray);
		/* 设置坐标轴属性 */
			/* Y轴属性设置 */
		ValueAxis vy = xyPlot.getRangeAxis();
		vy.setRange(-1500D, 1500D);
		vy.setLabelPaint(Color.black);
		vy.setLabelFont(new Font("Serif", Font.CENTER_BASELINE, 15));
		vy.setTickLabelFont(new Font("Serif", Font.CENTER_BASELINE, 13));
		vy.setAxisLineVisible(false);// 坐标轴线条是否可见
		vy.setMinorTickCount(200);
		vy.setAutoRange(false);

			/* X轴属性设置 */
		DateAxis vx = (DateAxis) xyPlot.getDomainAxis();
		vx.setTickLabelFont(new Font("Serif", Font.CENTER_BASELINE, 13));
		vx.setLabelFont(new Font("Serif", Font.CENTER_BASELINE, 15));
		vx.setLabelInsets(new RectangleInsets(0, 0, 0, 50));
		vx.setFixedAutoRange(15*60*1000D);
		vx.setAutoRange(true);
		vx.setAxisLineVisible(false);
		/*设置曲线颜色*/
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();     // 能够得到renderer对象，就可以使用此方法设置颜色
		for (int i=0;i<COLORS.length;i++) {
			renderer.setSeriesPaint(i, COLORS[i]); // 设置第一条折现的颜色，以此类推。
//			renderer.setSeriesStroke(i, new BasicStroke(2.0F));
		}
		return chart;
	}
	
	
	public void TimerThread(){
		timer = new Timer(ONE_MILLISECOND, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				AnimateSeries();
			}
		});
		chartPanel.setTimer(timer);
	}

	private List<String> excelTitles = lParams;
	private List<List<String>> excelDatas = new ArrayList<>();
	private void AnimateSeries() {
		try {
			Millisecond current = new Millisecond();
			Date date = current.getEnd();
			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateTime = simpleDateFormat.format(date);
			List<?> lSeries = tSeriesCollection.getSeries();
			String[] strValues = saveMessage.getStrValue().split(",");
			List<String> tmpList = new ArrayList<>();
			tmpList.add(dateTime);
			if(strValues.length>666){
				for (int i=0;i<lSeries.size();i++) {
					String addr = lParams.get(i).split("-")[0];
					String value =  strValues[Integer.parseInt(addr)];
					TimeSeries series = (TimeSeries) lSeries.get(i);
					series.add(current,(double)Integer.parseInt(value));
					tmpList.add(value);
				}
				excelDatas.add(tmpList);
			}else {
				Toolkit.getDefaultToolkit().beep();//警告声
                JOptionPane.showMessageDialog(null, "连接失败！", "警告", JOptionPane.WARNING_MESSAGE);
                tmpList = new ArrayList<>();
				timer.stop();
				startButton.setText("开始");
				startButton.setActionCommand("10");
			}
		} catch (SeriesException e) {
			System.err.println("Error");
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String commond = e.getActionCommand();
		if(commond.equals("10")){
			timer.start();
			startButton.setText("暂停");
			startButton.setActionCommand("11");
		}
		if(commond.equals("11")){
			timer.stop();
			startButton.setText("开始");
			startButton.setActionCommand("10");
		}
		if(commond.equals("out")){
			if(timer.isRunning()){
				Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "请暂停后，导出Excel", "警告", JOptionPane.WARNING_MESSAGE);
			}else {
				if(excelDatas.isEmpty()){
					Toolkit.getDefaultToolkit().beep();
	                JOptionPane.showMessageDialog(null, "无数据，请先开始采集！", "警告", JOptionPane.WARNING_MESSAGE);
				}else {
					new OutExcel(excelTitles,excelDatas);
				}
				
			}
			
		}
	}
}
