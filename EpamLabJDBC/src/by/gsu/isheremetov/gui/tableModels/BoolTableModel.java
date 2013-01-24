package by.gsu.isheremetov.gui.tableModels;

import javax.swing.table.AbstractTableModel;

public class BoolTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1834871459586737553L;
	private Object[][] rowData;
	private String[] columnNames;

	public BoolTableModel(String[] header, Object[][] rowData) {
		this.rowData = rowData;
		columnNames = header;
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return rowData.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
		return rowData[row][column];
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		return (getValueAt(0, column).getClass());
	}
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		rowData[row][column] = value;
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return (column != 0);
	}
}

