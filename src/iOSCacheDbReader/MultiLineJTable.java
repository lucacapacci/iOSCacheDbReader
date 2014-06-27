package iOSCacheDbReader;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class MultiLineJTable extends JTable{
	
	private static final long serialVersionUID = 91730591587066550L;
		
	private MultiLineCellRenderer multiLineCellRenderer = null;
	
	public MultiLineJTable(Object[][] data, String[] columnNames) 
	{
		super(data, columnNames);
		multiLineCellRenderer = new MultiLineCellRenderer(data.length, columnNames.length);
	}

	public TableCellRenderer getCellRenderer(int row, int column) 
	{
            return multiLineCellRenderer;
    }

}
