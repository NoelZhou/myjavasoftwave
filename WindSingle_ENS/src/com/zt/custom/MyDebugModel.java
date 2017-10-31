package com.zt.custom;

import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.zt.common.FileDiretory;

public class MyDebugModel extends AbstractTableModel {

	
	private static final long serialVersionUID = 1L;
	final String[] columnName = {"序号","","故障信息","状态"};
	final Vector<List<Object>> vect;
	public MyDebugModel(Vector<List<Object>> vect) {
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
//		if (!vect.isEmpty())
//			return ((Vector<Object>) vect.elementAt(rowIndex)).elementAt(columnIndex);
//		else
//			return null;
		if(!vect.isEmpty()){
			if(columnIndex == 3){
				String state = (String) ((Vector<Object>) vect.elementAt(rowIndex)).elementAt(columnIndex);
				if(Integer.parseInt(state)==0){
					return new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan01.png");
				}else {
					return new ImageIcon(FileDiretory.getCurrentDir()+"/image/yuan02.png");
				}
			}
			return ((Vector<Object>) vect.elementAt(rowIndex)).elementAt(columnIndex);
		}
		return null;
		
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex==3){
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
