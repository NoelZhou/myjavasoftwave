package com.zt.custom;

import java.util.List;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	final String[] columnName = {"序号","参数ID","参数名称","参数值","单位","描述","备注","最小值","最大值","系数"};
	final Vector<List<Object>> vect;
	public MyTableModel(Vector<List<Object>> vect) {
		this.vect = vect;
	}

	@Override
	public int getColumnCount() {
		return columnName.length;
	}

	@Override
	public int getRowCount() {
		return vect.size()-1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (!vect.isEmpty())
			return ((Vector<Object>) vect.elementAt(rowIndex)).elementAt(columnIndex);
		else
			return null;
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if((Boolean)vect.get(vect.size()-1).get(0)){
			if (columnIndex == 3) {
				return true;
			}
		}
		return false;
	}
	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return columnName[column];
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return super.getColumnClass(columnIndex);
	}

	public void setValueAt(Object aValue, int row, int column) {
		Vector<Object> rowVector = (Vector<Object>) vect.elementAt(row);
		rowVector.setElementAt(aValue, column);
		fireTableCellUpdated(row, column);
	}
}