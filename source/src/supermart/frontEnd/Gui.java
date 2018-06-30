package supermart.frontEnd;

import java.awt.BorderLayout;      
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.awt.GridBagConstraints;


import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import supermart.SupermartEnums.CSVType;
import supermart.backEnd.Item;
import supermart.backEnd.Stock;
import supermart.backEnd.Store;
import supermart.exceptions.CSVFormatException;
import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;
import supermart.exceptions.StockException;


/**
 * @author Harrison Berryman - 09745092
 * 
 * Builds a GUI that acts as the front end for entire project.
 * <p>
 * Constructs a GUI that enables the importing of manifest, item lists 
 * and sales log CSVs. As well as the exporting of manifest CSVs. 
 * The store object is called here and capital and store
 * stock changes based on what CSVs are imported/exported.
 */
public class Gui extends JFrame implements ActionListener{
	
	//Create variables before constructor, otherwise ActionListener breaks
	private static final long serialVersionUID = 2805412124517060375L;
	static int WIDTH = 960;
	static int HEIGHT = 480;
	
	Stock stock = new Stock();
	Store store = new Store("SuperMart", 100000.00, stock);
	
	//Used to keep track of what items are in the table
	ArrayList<String> tableList = new ArrayList<String>();
	
	DefaultTableModel itemModel;
	JTable itemTable;
	JLabel tableLabel;
	
	JButton importItems;
	JButton importSalesLog;
	JButton importManifest;
	JButton exportManifest;
	
	JTextField storeName;
	JTextField capital;
	
	JScrollPane tablePane;
	JScrollPane infoPane;
	
	JPanel leftPanel;
	JPanel bottomPanel;
	
	JTextArea infoLog;
	
	JFileChooser itemSelect = new JFileChooser();
	JFileChooser salesLogSelect = new JFileChooser();
	JFileChooser importManifestSelect = new JFileChooser();
	JFileChooser exportManifestSelect = new JFileChooser();
	
	File itemFile;
	File salesLogFile;
	File importManifestFile;
	
	/**
	 * Constructs a GUI object
	 * <p>
	 * Constructs a GUI by building three panels, using gridBaglayout
	 * to add JButtons, a JTable and a JTextArea to the window.
	 */
	public Gui () {
		
		JPanel leftPanel = new JPanel();
		JPanel rightPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		
		//Create main window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setLayout(new BorderLayout());
		
		//Add panels, add panel layouts, add colours
		add(leftPanel,BorderLayout.LINE_START);
		add(rightPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.PAGE_END);
		leftPanel.setBackground(Color.LIGHT_GRAY);
		leftPanel.setLayout(new GridBagLayout());
		rightPanel.setLayout(new BorderLayout());
		
		//Initialise all components for all panels
		JLabel storeLabel = new JLabel ("Store Name:");
		JLabel capitalLabel = new JLabel ("Capital ($):");
		
		
		importItems = new JButton("Import Items");
		importSalesLog = new JButton("Import Sales Log");
		importManifest = new JButton("Import Manifest");
		exportManifest = new JButton("Export Manifest");
		
		tableLabel = new JLabel("Store Inventory", SwingConstants.CENTER);
		
		storeName = new JTextField(store.getName());
		capital = new JTextField("" + String.format("%.2f", store.getCapital()));
		
		infoLog = new JTextArea("Infolog: Errors and feedback will appear here! \n", 6,60);
		
		//Creates JTables and adds columns
		itemModel = new DefaultTableModel();
		itemTable = new JTable(itemModel);
		
		itemModel.addColumn("Name");
		itemModel.addColumn("Quantity");
		itemModel.addColumn("Cost ($)");
		itemModel.addColumn("Price ($)");
		itemModel.addColumn("Reorder Point");
		itemModel.addColumn("Reorder Amount");
		itemModel.addColumn("Temperature (°C)");
		
		//Creates scroll panes and utomatically scrolls the scrollpane to the bottom when new text is added
		infoLog.setCaretPosition(infoLog.getDocument().getLength());
		
		tablePane = new JScrollPane(itemTable);
		infoPane = new JScrollPane(infoLog);
		
		//Centers all cells
		DefaultTableCellRenderer centerCells = new DefaultTableCellRenderer();
		centerCells.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < itemTable.getColumnCount(); i++) {
			itemTable.getColumnModel().getColumn(i).setCellRenderer(centerCells);
		}

		
		//Enable the action listener for all buttons
		importItems.addActionListener(this);
		importSalesLog.addActionListener(this);
		importManifest.addActionListener(this);
		exportManifest.addActionListener(this);
		
		//Disables these labels from being edited by the user
		storeName.setEditable(false);
		capital.setEditable(false);
		infoLog.setEditable(false);

		//Add buttons and textareas to leftPanel, formats them to leave adequete spacing
		GridBagConstraints bc = new GridBagConstraints(); 
		bc.anchor = GridBagConstraints.FIRST_LINE_START;
		bc.gridx = 0;
		bc.gridy = 0;
		bc.gridwidth = 5;
		bc.gridheight = 1;
		bc.insets = new Insets(5,5,5,5);
		bc.anchor = GridBagConstraints.CENTER;
		leftPanel.add(storeLabel, bc);
		bc.gridy = 1;
		bc.ipadx = 1;
		leftPanel.add(storeName, bc);
		bc.ipadx = 0;
		bc.gridy = 2;
		bc.weightx = 1;
		leftPanel.add(capitalLabel, bc);
		bc.weightx = 0;
		bc.gridy = 3;
		leftPanel.add(capital, bc);
		bc.gridy = 4;
		leftPanel.add(importItems, bc);
		bc.gridy = 5;
		leftPanel.add(exportManifest, bc);
		bc.gridy = 6;
		leftPanel.add(importManifest, bc);
		bc.gridy = 7;
		leftPanel.add(importSalesLog, bc);
		
		//Adds table, text area and label to right and bottom panels
		rightPanel.add(tableLabel, BorderLayout.PAGE_START);
		rightPanel.add(tablePane, BorderLayout.CENTER);
		bottomPanel.add(infoPane);

		//Sets titles for JFileChooser windows
		itemSelect.setDialogTitle("Select Item Properties File");
		salesLogSelect.setDialogTitle("Select Sales Log File");
		importManifestSelect.setDialogTitle("Select Manifest File");
		exportManifestSelect.setDialogTitle("Save Manifest");
		
		//Makes the entire frame visible, without this nothing would appear
		updateTable();
		setVisible(true);
		pack();
	}
	
	/**
	 * ActionListener for the GUI
	 * <p>
	 * Called whenever a button is pressed, this listener
	 * uses getSource() to determine the source of the press was and
	 * performs a function dependent on that source.
	 * <p>
	 * All sources call CSVReader.ImportCSV to import a CSV file,
	 * the infolog provides updates during the importing process
	 * including any exceptions that are thrown.
	 */
	public void actionPerformed(ActionEvent e) {

		//Activates when importItems button is pressed, calls importCSV for CSVType ITEMS
		if (e.getSource() == importItems) {
			infoLog.append("Select an item file to import \n");
			int i = itemSelect.showOpenDialog(this);
			if (i == JFileChooser.APPROVE_OPTION) {
				itemFile = itemSelect.getSelectedFile();
				infoLog.append("Item file selected \n");
				try {
					CSVReader.ImportCSV(CSVType.ITEMS, store, itemFile);
					infoLog.append("Item file succesfully imported! \n");
				} catch (DeliveryException | StockException e1) {
					e1.printStackTrace();
					infoLog.append("EXCEPTION THROWN: Item File import failed! \n" + e1.getMessage() + "\n");
				}
				catch (CSVFormatException e1) {
					e1.printStackTrace();
					System.out.println(e1.getMessage());
					infoLog.append("EXCEPTION THROWN: Item File import failed! \n");
					infoLog.append("Selected file formatted incorrectly! Expected Item Properties file \n");
				}
			}
			
			else {
				infoLog.append("File selection cancelled by user \n");
			}
			updateTable();
		}
		
		//Activates when importSalesLog button is pressed, calls importCSV for CSVType SALES
		if (e.getSource() == importSalesLog) {
			infoLog.append("Select a sales log to import \n");
			
			int i = salesLogSelect.showOpenDialog(this);
			
			if (i == JFileChooser.APPROVE_OPTION) {
				salesLogFile = salesLogSelect.getSelectedFile();
				infoLog.append("Sales log selected \n");
				try {
					clearTable();
					CSVReader.ImportCSV(CSVType.SALES, store, salesLogFile);
					infoLog.append("Sales Log succesfully imported! \n");
				} catch (DeliveryException | StockException e1) {
					e1.printStackTrace();
					infoLog.append("EXCEPTION THROWN: Sales Log import failed! \n" + e1.getMessage() + "\n");
				}
				catch (CSVFormatException e1) {
					e1.printStackTrace();
					infoLog.append("EXCEPTION THROWN: Sales Log import failed! \n");
					infoLog.append("Selected file formatted incorrectly! Expected Sales Log file \n");
				}
			}
			
			else {
				infoLog.append("File selection cancelled by user \n");
			}
			updateTable();
			capital.setText("" + String.format("%.2f", store.getCapital()));
		}
		
		//Activates when importManifest button is pressed, calls importCSV for CSVType MANIFEST
		if (e.getSource() == importManifest) {
			infoLog.append("Select a manifest to import \n" );
			
			int i = importManifestSelect.showOpenDialog(this);
			
			if (i == JFileChooser.APPROVE_OPTION) {
				importManifestFile = importManifestSelect.getSelectedFile();
				infoLog.append("Manifest selected \n");
				try {
					clearTable();
					CSVReader.ImportCSV(CSVType.MANIFEST, store, importManifestFile);
					infoLog.append("Manifest succesfully imported! \n");
				} catch (DeliveryException | StockException e1) {
					e1.printStackTrace();
					infoLog.append("EXCEPTION THROWN: Manifest import failed! \n" + e1.getMessage() + "\n");
				}
				catch (NullPointerException e1) {
					e1.printStackTrace();
					infoLog.append("EXCEPTION THROWN: Manifest import failed! \n");
					infoLog.append("NULL POINTER EXCEPTION: Manifest contains items not imported into store inventory! \n");
					infoLog.append("Please import item properties first! \n");
				}
				catch (CSVFormatException e1) {
					e1.printStackTrace();
					infoLog.append("EXCEPTION THROWN: Manifest import failed! \n");
					infoLog.append("Selected file formatted incorrectly! Expected Manifest file \n");
				}
			}
				
			else {
				infoLog.append("File selection cancelled by user \n");
			}

			updateTable();
			capital.setText("" + String.format("%.2f", store.getCapital()));
		}
		
		//Activates when exportManifest button is pressed, calls CSVReader.ExportManifest to generate manifest file, then saves that file
		if (e.getSource() == exportManifest) {
			infoLog.append ("Select save location for manifest \n");
			int i = exportManifestSelect.showSaveDialog(this);
			if (i == JFileChooser.APPROVE_OPTION) {
				infoLog.append("EXPORTING MANIFEST: Please wait for \"Export Successful\" message \n");
				File exportedManifest = exportManifestSelect.getSelectedFile();
				try {
					CSVReader.ExportManifest(store,exportedManifest);
					infoLog.append("Export Successful! \n");
				} catch (DeliveryException | CSVFormatException e1) {
					infoLog.append("EXCEPTION THROWN: Manifest export failed! \n" + e1.getMessage() + "\n");
					e1.printStackTrace();
				}
			}
			else {
				infoLog.append("Manifest export cancelled by user \n");
			}
			updateTable();
			capital.setText("" + String.format("%.2f", store.getCapital()));
		}


	 }


	/**
	 * Updates JTable when store's stock is changed and buttons are pressed.
	 * <p>
	 * Called during the actionListener the method takes the stores
	 * stock, parses it, and adds the items in the stock to the JTable.
	 * All item names are added to an ArrayList after being added to the table
	 * to ensure items aren't added more than once.
	 */
	public void updateTable() {
		
		//Orders items alphabetically. Taken from Stock.java, keeps the Table neat and prevents rows being shuffled when updated.
		Comparator<Item> lexicographicComparator = Comparator.comparing(Item::GetName);
		List<Item> items = new ArrayList<Item>();
		items.addAll((this.stock.keySet()));
		Collections.sort(items, lexicographicComparator);
		
		//Adds item row to JTable if itemRow doesn't already exist
		//If a dryException is thrown the Temperature column isn't updated as it's a dry item which has no temperature
		for (Item item : items) {
			if (!(tableList.contains(item.GetName()))){
			try {
				itemModel.addRow(new Object[] {item.GetName() , Double.toString(store.getStock().getQuantity(item)) , Double.toString(item.GetCost()) , Double.toString(item.GetPrice()) ,
				Double.toString(item.GetReorderPoint()) , Double.toString(item.GetReorderAmount()) , Double.toString(item.GetTemperature())});
				tableList.add(item.GetName());
			} catch (DryException e) {
				itemModel.addRow(new Object[] {item.GetName() , Double.toString(store.getStock().getQuantity(item)) , Double.toString(item.GetCost()) , Double.toString(item.GetPrice()) ,
						Double.toString(item.GetReorderPoint()) , Double.toString(item.GetReorderAmount())});
				tableList.add(item.GetName());
			} 
			
		} 
		}
	}
	
	/**
	 * Clears the Jtable and tableList ArrayList.
	 * <p>
	 * Clears the JTable by removing all rows and clears the
	 * Arraylist that prevents items from being added twice.
	 * Used whenever items are updated to replace the old table.
	 */
	public void clearTable() {
		itemModel.setRowCount(0);
		tableList.clear();
	}
	
	/**
	 * Creates a new GUI Object and automatically displays it.
	 */
	public static void main(String[] args) {
		Gui Newgui = new Gui();

	}
}
