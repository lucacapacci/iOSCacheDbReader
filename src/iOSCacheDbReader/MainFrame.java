package iOSCacheDbReader;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainFrame extends JFrame{

	private static final long serialVersionUID = -7921608152759036851L;
	
	private final MainFrame mainGui;
	
	private JTextField searchField;
	
	private HashMap<String, CacheTable> rows;
	
	public MainFrame()
	{
		searchField = new JTextField(20);
		setSystemLookAndFeel();	
		mainGui = this;
	}
	
	private void setSystemLookAndFeel()
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} 
		catch (UnsupportedLookAndFeelException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void generalSettings()
	{
		getContentPane().removeAll();
		setTitle("iOS Cache.db reader");
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE); 
	}
	
	private JScrollPane createTable(Object[][] data, String[] columnNames)
	{
		ArrayList<Object[]> newData = new ArrayList<Object[]>();
		if (!searchField.getText().trim().equals(""))
		{
			for (Object[] row : data)
			{
				for (Object rowElement : row)
				{
					if ((rowElement+"").contains(searchField.getText()))
					{
						newData.add(row);
						break;
					}
				}
			}
			
			data = newData.toArray(new Object[0][0]);
		}				
		
		JTable table = new MultiLineJTable(data, columnNames);
									
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		return scrollPane;
	}
	
	public void showFileChooser()
	{	
		JFileChooser fc = new JFileChooser();
		
		int returnVal = fc.showOpenDialog(null);
	
	    if (returnVal == JFileChooser.APPROVE_OPTION) 
	    {
	        File file = fc.getSelectedFile();
	        searchField.setText("");
	        startGui(file);	        
	    } 
	    else 
	    {
	    	System.out.println("Open command cancelled by user");
	    }
	}
	
	private void createTabs(HashMap<String, CacheTable> tables)
	{
		JTabbedPane tabbedPane = new JTabbedPane();
		
		for (String tableName : tables.keySet())
		{
			tabbedPane.addTab(tableName, createTable(tables.get(tableName).getData(), tables.get(tableName).getColumnNames()));
		}
		add(tabbedPane);
	}
	
	private void createTopBar()
	{

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		JLabel filterLabel = new JLabel("Insert text here then press the Filter button: ");
		
		searchField.setMaximumSize(searchField.getPreferredSize());
		
		JButton searchButton = new JButton("Filter");
		JButton clearButton = new JButton("Clear filter");
		JButton openButton = new JButton("Open");
		JButton aboutButton = new JButton("About");
		
		toolBar.add(filterLabel);	
		toolBar.add(searchField);		
		toolBar.add(searchButton);
		toolBar.addSeparator();
		toolBar.add(clearButton);
		
		toolBar.addSeparator();
		
		toolBar.add(openButton);
		
		toolBar.addSeparator();
		
		toolBar.add(aboutButton);
		toolBar.addSeparator();
		
		searchButton.addActionListener(new ActionListener() 
										{		 
								            public void actionPerformed(ActionEvent e)
								            {
								            	resetGui();
								            }
										});   
		
		clearButton.addActionListener(new ActionListener() 
		{		 
            public void actionPerformed(ActionEvent e)
            {
            	searchField.setText("");
            	resetGui();
            }
		});   
		
		openButton.addActionListener(new ActionListener() 
										{		 
								            public void actionPerformed(ActionEvent e)
								            {
								                showFileChooser();
								            }
										});   
		
		aboutButton.addActionListener(new ActionListener() 
										{		 
								            public void actionPerformed(ActionEvent e)
								            {
								            	JOptionPane.showMessageDialog(mainGui, "iOS Cache.db reader\nby Luca Capacci\n(luca.capacci@gmail.com)", "About", JOptionPane.INFORMATION_MESSAGE);
								            }
										}); 
		
		add(toolBar, BorderLayout.NORTH);
	}
	
	private void resetGui()
	{		
		generalSettings();
        createTopBar();
        createTabs(rows);
        setVisible(true);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
	}

	private void startGui(File file)
	{
        System.out.println("Opening: " + file.getName());
        
        DbReader dbReader = new DbReader(file);
        rows = dbReader.readDb();
        
        resetGui();
	}
}