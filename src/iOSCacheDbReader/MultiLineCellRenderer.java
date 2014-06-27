package iOSCacheDbReader;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	private int cellPreferredHeights[][];

	public MultiLineCellRenderer(int rowCount, int columnCount) 
	{
		setLineWrap(true);
		setWrapStyleWord(true);
		cellPreferredHeights = new int[rowCount][columnCount];
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		setText(value+"");
		
		setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);	
		
		cellPreferredHeights[row][column] = getPreferredSize().height;		

		int maxPreferredRowHeight = findMaxPreferredRowHeight(row);

		
		if (table.getRowHeight(row) != maxPreferredRowHeight) 
		{
			table.setRowHeight(row, maxPreferredRowHeight);
		}
		
		return this;
	}
	
	private int findMaxPreferredRowHeight(int row)
	{
		int max = 0;
		for (int i = 0; i<cellPreferredHeights[row].length; i++)
		{
			if (cellPreferredHeights[row][i] > max)
				max = cellPreferredHeights[row][i];
		}
		return max;
	}
} 
