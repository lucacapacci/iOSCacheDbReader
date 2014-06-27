package iOSCacheDbReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;

public class DbReader {

	private File dbFile;

	public DbReader(File dbFile)
	{
		this.dbFile = dbFile;
	}

	public HashMap<String, CacheTable> readDb()
	{
		HashMap<String, CacheTable> tables = new HashMap<String, CacheTable>();

		// load the sqlite-JDBC driver using the current class loader
		try 
		{
			Class.forName("org.sqlite.JDBC");
		} 
		catch (ClassNotFoundException e1) 
		{
			e1.printStackTrace();
		}

		Connection connection = null;
		try
		{
			//create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:"+dbFile.getAbsolutePath());      

			DatabaseMetaData md = connection.getMetaData();

			//get table names
			ResultSet tableNamesRs = md.getTables(null, null, "%", null);
			while (tableNamesRs.next()) 
			{
				CacheTable newTable = new CacheTable();
				newTable.setTableName(tableNamesRs.getString("TABLE_NAME"));
				tables.put(newTable.getTableName(), newTable);
			}

			//get column names
			for (String tableName : tables.keySet())
			{
				System.out.println("-------------"+tableName+"-------------");
				ResultSet tableColumnsRs = md.getColumns(null,null,tableName,null);
				while (tableColumnsRs.next()) 
				{
					tables.get(tableName).addColumnName(tableColumnsRs.getString("COLUMN_NAME"), tableColumnsRs.getString("TYPE_NAME"));
					System.out.println(tableColumnsRs.getString("COLUMN_NAME")+" : "+tables.get(tableName).getColumnType(tableColumnsRs.getString("COLUMN_NAME")));
				}
			}

			//get table content
			for (String tableName : tables.keySet())
			{
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30);  // set timeout to 30 sec.

				ResultSet contentRs = statement.executeQuery("select * from "+tableName);
				while(contentRs.next())
				{
					Object[] newRow = new Object[tables.get(tableName).getColumnCount()];
					for (int i=0; i<tables.get(tableName).getColumnCount(); i++)
					{
						newRow[i] = contentRs.getObject(tables.get(tableName).getColumnNames()[i]);
						if (newRow[i] instanceof byte[])
						{
							newRow[i] = parsePlist((byte[]) newRow[i]);
						}
					}
					tables.get(tableName).addData(newRow);
				}
			}
		}
		catch(SQLException e)
		{
			// if the error message is "out of memory", 
			// it probably means no database file is found
			JOptionPane.showMessageDialog(null, "The selected file is not an SQLite database", "Error", JOptionPane.ERROR_MESSAGE);
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(connection != null)
					connection.close();
			}
			catch(SQLException e)
			{
				// connection close failed.
				System.err.println(e);
			}
		}
		
		return tables;
	}
	
	private String parsePlist(byte[] cell)
	{
		try 
		{
			NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(cell);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PropertyListParser.saveAsXML(rootDict, baos);
			return baos.toString();
		} 
		catch(Exception ex) 
		{
			sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
			return "(BLOB converted to Base64) "+encoder.encode(cell);
		}
	}
}