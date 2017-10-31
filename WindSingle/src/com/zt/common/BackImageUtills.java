package com.zt.common;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.zt.panel.BackgroundPanel;

public class BackImageUtills {
	public static JPanel createBackgroundPanel() {
		Image image = new ImageIcon(FileDiretory.getCurrentDir() + "/images/DFIGbackground.png").getImage();
		BackgroundPanel bPanel = new BackgroundPanel(image);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //得到屏幕的尺寸
		bPanel.setBounds(0, 0, screenSize.width, screenSize.height-100);
		return bPanel;
	}
}
