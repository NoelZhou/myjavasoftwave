package com.zt.custom;

import javax.swing.table.AbstractTableModel;

public class MyOsiTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;
	final String[] columnName = {"","网侧波形","机侧波形"};
	final Object[][] vect;
	public MyOsiTableModel(Object[][] vect) {
		this.vect = vect;
	}

	@Override
	public int getColumnCount() {
		return columnName.length;
	}

	@Override
	public int getRowCount() {
		return vect.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (vect!=null)
			return  vect[rowIndex][columnIndex];
		else
			return null;
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return true;
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
		if(columnIndex==0){
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex);
	}

	public void setValueAt(Object aValue, int row, int column) {
		vect[row][column] = aValue;  
		fireTableCellUpdated(row, column);
	}
}
