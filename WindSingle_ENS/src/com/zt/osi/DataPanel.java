package com.zt.osi;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DateFormat;
import java.util.ArrayList;

import javax.swing.JPanel;

public class DataPanel extends JPanel {

    private static final long serialVersionUID = -9039511286331044798L;

    private int index = 0;
    private ArrayList<Long> time = new ArrayList<>();
    private ArrayList<Integer> val = new ArrayList<>();
    DateFormat fmt = DateFormat.getDateTimeInstance();

    public DataPanel() {
    }
    
    public void addData(long t, int v) {
        time.add(t);
        val.add(v);
        index++;
        repaint();
    }
    
    // Graph the sensor values in the dataPanel JPanel
    public void paint(Graphics g) {
        super.paint(g);
        int left = getX() + 10;       // get size of pane
        int top = 10;
        int right = left + getWidth() - 20;
        int bottom = getHeight() - 20;
        
        int y0 = bottom - 20;                   // leave some room for margins
        int yn = top;
        int x0 = left + 33;
        int xn = right;
        double vscale = (yn - y0) / 120.0;      // light values range from 0 to 800
        double tscale = 1.0 / 2000.0;           // 1 pixel = 2 seconds = 2000 milliseconds
        
        // draw X axis = time
        g.setColor(Color.BLACK);
        g.drawLine(x0, yn, x0, y0);
        g.drawLine(x0, y0, xn, y0);
        int tickInt = 60 / 2;
        for (int xt = x0 + tickInt; xt < xn; xt += tickInt) {   // tick every 1 minute
            g.drawLine(xt, y0 + 5, xt, y0 - 5);
            int min = (xt - x0) / (60 / 2);
            g.drawString(Integer.toString(min), xt - (min < 10 ? 3 : 7) , y0 + 20);
        }
        
        // draw Y axis = sensor reading
        g.setColor(Color.BLUE);
        for (int vt = 120; vt > 0; vt -= 20) {         // tick every 200
            int v = y0 + (int)(vt * vscale);
            g.drawLine(x0 - 5, v, x0 + 5, v);
            g.drawString(Integer.toString(vt), x0 - 38 , v + 5);
        }

        // graph sensor values
        int xp = -1;
        int vp = -1;
        for (int i = 0; i < index; i++) {
            int x = x0 + (int)((time.get(i) - time.get(0)) * tscale);
            int v = y0 + (int)(val.get(i) * vscale);
            if (xp > 0) {
                g.drawLine(xp, vp, x, v);
            }
            xp = x;
            vp = v;
        }
    }

}