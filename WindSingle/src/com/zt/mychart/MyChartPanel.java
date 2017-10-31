package com.zt.mychart;

import java.awt.event.MouseEvent;

import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.axis.ValueAxis;

public class MyChartPanel extends ChartPanel {

	private static final long serialVersionUID = 1L;
	private Timer timer;
	public MyChartPanel(JFreeChart chart) {
		super(chart);
	}
	@Override
	public void restoreAutoRangeBounds() {
		XYPlot plot = (XYPlot)this.getChart().getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            ValueAxis vy = plot.getRangeAxis();
    		vy.setRange(-1500D, 1500D);
            plot.setNotify(savedNotify);
        }
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(!timer.isRunning()){
			super.mouseDragged(e);
		}
	};
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(!timer.isRunning()){
			super.mouseReleased(e);
		}
	}
	public Timer getTimer() {
		return timer;
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	
}












