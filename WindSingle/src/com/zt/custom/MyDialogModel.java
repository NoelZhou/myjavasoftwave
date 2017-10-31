package com.zt.custom;

import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.zt.common.FileDiretory;

public class MyDialogModel extends AbstractTableModel {

	
	private static final long serialVersionUID = 1L;
	final String[] columnName = {"位号","名称","状态","地址"};
	final Vector<Vector<Object>> vect;
	public MyDialogModel(Vector<Vector<Object>> vect) {
		this.vect = vect;
	}
	@Override
	public int getRowCount() {
		return vect.size();
	}

	@Override
	public int getColumnCount() {
		return columnName.length;
	}
	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return columnName[column];
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(!vect.isEmpty()){
			if(columnIndex == 2){
				String state = (String) ((Vector<Object>) vect.elementAt(rowIndex)).elementAt(columnIndex);
				String addr = (String) ((Vector<Object>) vect.elementAt(rowIndex)).elementAt(3);
				if(Integer.parseInt(state)==0){
					return new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan01.png");
				}else {
					if(addr.equals("70")||addr.equals("230")){
						return new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan.png");
					}
					return new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan02.png");
				}
			}
			return ((Vector<Object>) vect.elementAt(rowIndex)).elementAt(columnIndex);
		}
		return null;
		
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==2){
			return ImageIcon.class;
		}
		return super.getColumnClass(columnIndex);
	}
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		Vector<Object> rowVector = (Vector<Object>) vect.elementAt(row);
		rowVector.setElementAt(aValue, column);
		fireTableCellUpdated(row, column);
	}
}
