package com.zt.frame.SysSubFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import com.zt.common.FileDiretory;
import com.zt.thread.THD_WRAN;

public class WarnFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private THD_WRAN tWran;
	public WarnFrame(){
		createUI();
	}
	
	public void createUI(){
		setTitle("告警信息");
		setBounds(0, 0, 270, 400);
		setIconImage(new ImageIcon(FileDiretory.getCurrentDir()+"/images/SC.png").getImage());
		setLocationRelativeTo(null);//居中
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setBackground(Color.white);
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				tWran.stopThread();
			}
		});
		setLayout(null);
		setAlwaysOnTop(true);
		setResizable(false);
	}

	public JPanel addJPanel(int x,int y,int width,int height,String text1,String text2, String bitState){
		JPanel jPanel = new JPanel();
		jPanel.setBounds(x, y, width, height);
		jPanel.setBackground(Color.white);
		jPanel.setBorder(new LineBorder(Color.black));
		jPanel.setLayout(new BorderLayout(30,0));
		JLabel label1 = new JLabel(text1);
		jPanel.add(label1,BorderLayout.WEST);
		JLabel label2 = new JLabel(text2);
		label2.setHorizontalAlignment(SwingConstants.LEFT);
		jPanel.add(label2,BorderLayout.CENTER);
		JLabel label3 = new JLabel();
		label3.setHorizontalAlignment(SwingConstants.LEFT);
		if(Integer.parseInt(bitState)==0){
			label3.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan01.png"));
		}else{
			label3.setIcon(new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan02.png"));
		}
		jPanel.add(label3,BorderLayout.EAST);
		return jPanel;
	}
	
	public void setFaultThread(THD_WRAN tWran) {
		this.tWran = tWran;
	}

	public void sendWarnMessage(List<List<Object>> lists) {
		getContentPane().removeAll();
		for(int i=0;i<lists.size();i++){
			List<Object> list = lists.get(i);
			add(addJPanel(0, i*40, 250, 30, String.valueOf((int)list.get(0)),(String)list.get(1),(String)list.get(2)));
		}
	}
	
}













