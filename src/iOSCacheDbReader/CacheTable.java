package iOSCacheDbReader;

import java.util.ArrayList;
import java.util.HashMap;

public class CacheTable {
	
	private String tableName;
	private HashMap<String, String> columns = new HashMap<String, String>();
	private ArrayList<Object[]> data = new ArrayList<Object[]>();
	
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	
	public String getTableName()
	{
		return tableName;
	}
	
	public void addColumnName(String columnName, String columnType)
	{
		columns.put(columnName, columnType);
	}
	
	public String getColumnType(String columnName)
	{
		return columns.get(columnName);
	}
	
	public String[] getColumnNames()
	{
		return columns.keySet().toArray(new String[0]);
	}
	
	public int getColumnCount()
	{
		return columns.size();
	}
	
	public void addData(Object... dataElement)
	{
		data.add(dataElement);
	}
	
	public Object[][] getData()
	{
		return data.toArray(new Object[0][0]);
	}

}
