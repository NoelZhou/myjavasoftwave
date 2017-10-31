package com.zt.panel;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel {

	private Image image;
	
	private static final long serialVersionUID = 1L;
	
	public BackgroundPanel(Image image) {
		this.image = image;
		this.setOpaque(true);
	
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.drawImage(image,0,0,this.getWidth(),this.getHeight(),this);
	}
	

}
