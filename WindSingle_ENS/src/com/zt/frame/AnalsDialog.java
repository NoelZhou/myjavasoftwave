package com.zt.frame;
import java.awt.Color;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import com.zt.common.FileDiretory;
import javax.swing.table.DefaultTableColumnModel;

import com.zt.custom.MyDialogModel;

public class AnalsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	public static volatile AnalsDialog buttonDialog;
	private Vector<Vector<Object>> vect = new Vector<>();
	private JTable table;
	private JPanel contentPane;
	private String name;
	public AnalsDialog(String name) {
		this.name = name;
		createUI();
	}
	private void createUI() {
		setTitle(name);
		setIconImage(new ImageIcon(FileDiretory.getCurrentDir()+"/images/SC.png").getImage());
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(0, 0, 350, 400);
		setLocationRelativeTo(null);// 居中显示
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);
		addJtable();
	}
	
	public void addJtable(){
		MyDialogModel myDialogModel = new MyDialogModel(vect);
		table = new JTable(myDialogModel);
		table.setRowHeight(30);
		int[] arr = {0,1,2};
		hideAndChangeColumn(table, arr);
		JScrollPane jPane = new JScrollPane(table);
		jPane.setBounds(0, 0, 350, 380);
		jPane.getViewport().setBackground(Color.white);
		jPane.setAutoscrolls(true);
		contentPane.add(jPane);
	}
	
	
	public void  hideAndChangeColumn(JTable table,int[] arr){
		DefaultTableColumnModel dcm = (DefaultTableColumnModel) table // 获取列模型
				.getColumnModel();
		for(int i=0;i<arr.length;i++){
			if(arr[i]==0) columnChange(dcm, i, 60);
			if(arr[i]==2) columnChange(dcm, i, 80);
		}
		}
		public void columnChange(DefaultTableColumnModel dcm,int i,int width){
			dcm.getColumn(i).setPreferredWidth(width);
			dcm.getColumn(i).setMinWidth(width);
			dcm.getColumn(i).setMaxWidth(width);
		}
	
	public Vector<Vector<Object>> getVect(){
		return vect;
	}
	
	public JTable getTable(){
		return table;
	}

}





