package com.zt.frame.SysSubFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableColumnModel;
import com.zt.common.FileDiretory;
import com.zt.custom.MyDebugModel;
import com.zt.thread.THD_FAULT;

public class DFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private Vector<List<Object>> vect;
	private JTable table;
	private THD_FAULT tFault;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DFrame(String name) {
		this.name = name;
		createUI();
	}

	public void createUI() {
		setTitle("故障信息");
		setBounds(0, 0, 460, 500);
		setIconImage(new ImageIcon(FileDiretory.getCurrentDir() + "/images/SC.png").getImage());
		setLocationRelativeTo(null);// 居中
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setBackground(Color.white);
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				tFault.stopThread();// 关闭窗口时，停止线程
			}
		});
		setLayout(null);
		setAlwaysOnTop(true);
		setResizable(false);
		if (name.equals("A相故障"))
			addButtonA();
		else if (name.equals("B相故障"))
			addButtonB();
		else if (name.equals("C相故障"))
			addButtonC();
		addJTable();
	}

	public void addButtonA() {
		String[] strings = { "网侧故障字1,74", "网侧故障字2,75", "网侧故障字3,76", "网侧故障字4,77", "机侧故障字1,101", "机侧故障字2,102",
				"机侧故障字3,103","监控节点故障字,116", "其他故障,26" };
		for (int i = 0; i < strings.length; i++) {
			JButton button = commonButton(strings[i], 5, 5 + i * 40, 140, 30);
			add(button);
		}
	}

	public void addButtonB() {
		String[] strings = { "网侧故障字1,134", "网侧故障字2,135", "网侧故障字3,136", "网侧故障字4,137", "机侧故障字1,161", "机侧故障字2,162",
				"机侧故障字3,163","监控节点故障字,176", "其他故障,27" };
		for (int i = 0; i < strings.length; i++) {
			JButton button = commonButton(strings[i], 5, 5 + i * 40, 140, 30);
			add(button);
		}
	}

	public void addButtonC() {
		String[] strings = { "网侧故障字1,194", "网侧故障字2,195", "网侧故障字3,196", "网侧故障字4,197", "机侧故障字1,221", "机侧故障字2,222",
				"机侧故障字3,223","监控节点故障字,236", "其他故障,28" };
		for (int i = 0; i < strings.length; i++) {
			JButton button = commonButton(strings[i], 5, 5 + i * 40, 140, 30);
			add(button);
		}
	}

	public JButton commonButton(String str, int x, int y, int width, int height) {
		String[] strs = str.split(",");
		JButton button = new JButton(strs[0]);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setActionCommand(strs[1]);
		button.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan01.png"));
		button.setBounds(x, y, width, height);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton btn = (JButton) e.getSource();
				tFault.setButtonCommandTag(btn.getActionCommand());
			}
		});
		return button;
	}

	public void sendButtonState(Vector<List<Object>> vect2) {
		int len = getContentPane().getComponentCount();
		for (int i = 0; i < len; i++) {
			Component comp = getContentPane().getComponent(i);
			if (comp instanceof JButton) {
				JButton button = (JButton) comp;
				loop: for (List<Object> list : vect2) {
					if (list.size() != 0 && button.getActionCommand().equals(list.get(1))) {
						if (Integer.parseInt((String) list.get(3)) == 1) {
							button.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan02.png"));
							break loop;
						}
						button.setIcon(new ImageIcon(FileDiretory.getCurrentDir() + "/image/yuan01.png"));
					}
				}

			}
		}
	}

	public void addJTable() {
		vect = new Vector<List<Object>>();
		MyDebugModel model = new MyDebugModel(vect);
		table = new JTable(model);
		table.setRowHeight(30);
		int[] arr = { 0, 1, 2, 3 };
		hideAndChangeColumn(table, arr);
		JScrollPane jScrollPane = new JScrollPane(table);
		jScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
		jScrollPane.setBounds(160, 0, 300, 480);
		jScrollPane.setAutoscrolls(true);
		jScrollPane.getViewport().setBackground(Color.white);
		add(jScrollPane);
	}

	public void hideAndChangeColumn(JTable table, int[] arr) {
		DefaultTableColumnModel dcm = (DefaultTableColumnModel) table // 获取列模型
				.getColumnModel();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == 0)
				columnChange(dcm, i, 50);
			if (arr[i] == 1)
				columnChange(dcm, i, 0);
			if (arr[i] == 2)
				columnChange(dcm, i, 150);
			if (arr[i] == 3)
				columnChange(dcm, i, 80);

		}

	}

	public void columnChange(DefaultTableColumnModel dcm, int i, int width) {
		dcm.getColumn(i).setPreferredWidth(width);
		dcm.getColumn(i).setMinWidth(width);
		dcm.getColumn(i).setMaxWidth(width);
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public Vector<List<Object>> getVect() {
		return vect;
	}

	public void setVect(Vector<List<Object>> vect) {
		this.vect = vect;
	}

	public void setFaultThread(THD_FAULT tFault) {
		this.tFault = tFault;
	}
}
