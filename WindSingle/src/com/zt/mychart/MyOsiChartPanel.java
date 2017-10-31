package com.zt.mychart;

import java.awt.event.MouseEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.Zoomable;
import com.zt.thread.THD_OSI;
import org.jfree.chart.axis.ValueAxis;

public class MyOsiChartPanel extends ChartPanel {

	private static final long serialVersionUID = 1L;
	private THD_OSI osi;
	public MyOsiChartPanel(JFreeChart chart) {
		super(chart);
	}
	@Override
	public void restoreAutoRangeBounds() {
		XYPlot plot = (XYPlot)this.getChart().getPlot();
        if (plot instanceof Zoomable) {
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            ValueAxis vy = plot.getRangeAxis();
    		vy.setRange(-5000D, 5000D);
            plot.setNotify(savedNotify);
        }
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(!osi.getState().toString().equals("RUNNABLE")){
			super.mouseDragged(e);
		}
	};
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(!osi.getState().toString().equals("RUNNABLE")){
			super.mouseReleased(e);
		}
	}
	public THD_OSI getOsi() {
		return osi;
	}
	public void setOsi(THD_OSI osi) {
		this.osi = osi;
	}
	
}












