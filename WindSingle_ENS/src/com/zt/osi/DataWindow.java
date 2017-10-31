package com.zt.osi;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DateFormat;

import javax.swing.JFrame;

public class DataWindow extends JFrame {

    private static final long serialVersionUID = -5628586708228044760L;

    DateFormat fmt = DateFormat.getDateTimeInstance();

    public DataWindow() {
        initComponents();
    }

    public DataWindow(String ieee) {
        initComponents();
        setTitle(ieee);
    }

    public void addData(long t, int v1, int v2) {
        dataPanelTop.addData(t, v1);
//        dataPanelBottom.addData(t, v2);
    }

    public static void main(String args[]) {
        final DataWindow dw = new DataWindow("Dynamic Line Chart Demo");
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                dw.setVisible(true);
            }
        });
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                	try {
	                    long t = System.currentTimeMillis();
	                    int v1 = (int)(Math.sin(t / 1000 * Math.PI / 30) * 50) + 50;
	                    int v2 = (int)(Math.cos(t / 1000 * Math.PI / 30) * 50) + 50;
	                    dw.addData(t, v1, v2);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.run();
    }

    private void initComponents() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        dataPanelTop = new DataPanel();
        dataPanelTop.setMinimumSize(new Dimension(600, 220));
        dataPanelTop.setPreferredSize(new Dimension(600, 220));
        getContentPane().add(dataPanelTop, BorderLayout.NORTH);
        pack();
    }

    private DataPanel dataPanelTop;

}