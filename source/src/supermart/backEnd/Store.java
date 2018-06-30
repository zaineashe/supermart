package supermart.backEnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import supermart.exceptions.DeliveryException;
import supermart.exceptions.StockException;

/**
 * 
 * @author Zaine Ashe - 09469010
 * 
 * Builds a Store object.
 * <p>
 * Builds a Store object with it's own stock and capital that
 * changes when new items are bought and sold.
 * Must be a Singleton.
 */
public class Store {

	String name;
	double capital;
	Stock stock;
	
	private static Store store;
	
	//Store constructor the gives the stores name, capital and stock
	/**
	 * Constructor for a store object.
	 * <p>
	 * Builds a store object and sets it's name, starting capital and stock.
	 * @param name The name of the store
	 * @param capital The starting capital of the store (dollars and cents)
	 * @param stock The stock used by the store
	 */
	public Store(String name, double capital, Stock stock) {
		this.name = name;
		this.capital = capital;
		this.stock = stock;
	}
	
	/**
	 * Static getInstance method used to construct singleton Store.
	 * <p>
	 * Used to ensure the Store is singleton.
	 * 
	 * @param name The name of the store
	 * @param capital The starting capital of the store (dollars and cents)
	 * @param stock The stock used by the store
	 * @return Store the constructed store
	 */
	public static Store getInstance(String name, double capital, Stock stock) {
		//check if store has already been constructed or not
		if (store == null) {
			//if store is null (not yet constructed), construct store
			store = new Store(name, capital, stock);
		}
		//return the constructed Store (to use as reference in other classes)
		return store;
	}
		

	/**
	 * Returns the name of the store
	 * @return The name of the store
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the capital of the store which always starts at 100,000
	 * @return The current  capital of the store
	 */
	public double getCapital() {
		return capital; 
	}
	
	/**
	 * Returns the stock of the store
	 * @return the stock being used by the store
	 */
	public Stock getStock() {
		return stock; 
	}
	
	/**
	 * Searches for an item in the store stock by using it's itemName
	 * @param itemName The name of the item being searched for
	 * @return The item being searched for
	 */
	public Item getItem(String itemName) {
		return stock.getItem(itemName);
	}

	//new methods added to the store class
	//these methods allow the store to change values
	//depending on the request of the GUI
	
	// ======================================================
	// ======================================================
	
	/**
	 * Sets the stores name to the parameter provided
	 * @param name The new name of the store
	 */
	public void SetName(String name) {
		this.name = name;
	}
	
	/**
	 * Sets the store capital
	 * @param capital The stores new capital
	 */
	public void SetCapital (double capital) {
		this.capital = capital;
	}
	
	/**
	 * Changes the stores capital by the parameter's amount
	 * @param capital The amount to be added to the capital
	 */
	public void ChangeCapital(double capital) {
		this.capital += capital;
	}
	
	/**
	 * Adds to the quantity of the selected item in the stores stock
	 * @param item The item to have it's quantity changed
	 * @param quantity The amount added to the items quantity 
	 * @throws StockException Thrown if the item isn't in the stores stock
	 */
	public void ChangeQuantity(Item item, int quantity) throws StockException {
		
		int currentQuantity = 0;
		
		if (stock.Contains(item)) {
				currentQuantity = stock.getQuantity(item);
		} else {
			StockException e = new StockException(item.GetName() + "isn't in the store's inventory yet! Initialise Item!");
			throw e;
		}

		
		stock.SetQuantity(item, currentQuantity + quantity);
	}
	
	/**
	 * Adds the set amount to the stores already existing capital
	 * @param capital The amount to be added 
	 * @throws DeliveryException Thrown if the result is negative
	 */
	public void AddCapital (double capital) throws DeliveryException {
		this.capital+= capital;
		
		if (this.capital<0) {
			DeliveryException deliveryErr = new DeliveryException("ERR: Negative Capital!");
			throw deliveryErr;
		}
	}
	
	/**
	 * Sets the stores stock to a new one
	 * @param newStock The new stock the store will use
	 */
	public void SetStock(Stock newStock) {
		this.stock = newStock;
	}
	
	/**
	 * Generate a new stock order to send through to the CSV Reader
	 * <p>
	 * algorithm that checks each item in the store's stock, and builds an arraylist
	 * of all the items that need to be reordered. The list is sorted in ascending order by each
	 * item's temperature, with the dry items appended on the end.
	 * @return array list of each item in the current inventory that needs reordering
	 */
	public ArrayList<Item> generateNewOrder() {
		
		//to generate an optimised stock, we first need to order it by it's temperature.
		//create two arraylists, one to store the dry items in the stock, and one to store the cold items
		//(when we're using a comparator to sort our cold items by temperature, having dry items
		//in the mix will cause a whole host of nullpointerexceptions. Therefore, we'll sort our
		//cold items in a seperate arraylist, and then append our dry items on to it afterwards)
		//the array list for cold items will also be the final arraylist we return from this method
		//so it will be named finalOrder.
		ArrayList<Item> finalOrder = new ArrayList<Item>();
		ArrayList<Item> dryOrder = new ArrayList<Item>();
		
		//loop through store stock and grab each item whose quantity is below the items reorder point
		for (Item item : stock.keySet()) {

			//for this current item in the for loop, check if its quantity in the store
			//is less than the items reorderPoint.
			if (stock.getQuantity(item) <= item.GetReorderPoint()) {
				
				//if the item is below its reorder point, we need to add it to the stock order.
				//check if the item is dry or not.
				if (item.CheckIfDry()) {
					
					//if the item is dry, add it to the dry list
					dryOrder.add(item);
				} else {
					
					//if the item is cold, add it to the cold list (which is also serving as the final arraylist)
					finalOrder.add(item);
				}
			}
		}
		
		//initialise our comparator to use in ordering our cold items array.
		//use the Item :: GetSafeTemeperature method as a way to sort the list.
		//GetSafeTemperature is an altered version of getTemperature which is made explicitly to suppress errors.
		//(because the comparator can't handle custom methods with unhandled exceptions.)
		Comparator<Item> temperatureCompare = Comparator.comparing(Item :: GetSafeTemperature);
		
		//extra comparator for sorting dry items by their reorder amount (make sure the largest orders are placed first)
		Comparator<Item> reorderCompare = Comparator.comparing(Item :: GetReorderAmount);
		
		//use the temperature comparator to sort the cold arraylist
		Collections.sort(finalOrder,temperatureCompare);
		
		//use the reorder comparator to sort dry list
		Collections.sort(dryOrder,reorderCompare.reversed());

		//after the cold array list has been sorted, we're going to
		//append all of the items from our dry arraylist onto it.
		//loop through each item in the dryOrder
		for (Item item : dryOrder) {
			
			//add each item from the dryOrder into the finalOrder (which already contains the sorted cold items)
			//because of the indexed nature of arraylists, each of these dry items will be placed *after* the sorted cold items
			//therefore this append respects the original temperature sorting.
			finalOrder.add(item);
		}
		
		//after the final arraylist of items has been constructed, return it.
		return finalOrder;
	}
	
	/**
	 * Process sales from a given sales stock
	 * <p>
	 * This method calls when a sales log CSV is imported in the GUI.
	 * the CSV Reader generates a sales stock from the given csv file, and passes that data
	 * over to the ImportSales method for it to make changes.
	 * This method then loops through each item in the sales stock and 'sells' them. Increasing store capital
	 * and decreasing quantities in the store's inventory
	 * @param toAdd the toAdd stock passed through by the CSV reader, contains item references and quantities for reordering
	 * @throws StockException if a sale results in a negative quantity, or an item in log is not in the inventory
	 */
	public void ImportSales(Stock toAdd) throws StockException {
	
		//loop through each item in the given stock		
		for (Item item : toAdd.keySet()) {
			
			try {
				
				
				//for the current item, use the store's ChangeQuantity method
				//to decrease the stores current quantity of this item.
				ChangeQuantity(item,-(toAdd.getQuantity(item)));
				
				
				//increase the stores capital, based on this item's sell price multiplied by the quantity sold.
				ChangeCapital(item.GetPrice() * toAdd.getQuantity(item));
				
			} catch (NullPointerException e) {
				
				StockException stockErr = new StockException("sales trying to reference an item which does not exist");
				throw stockErr;
				
			}
		}
		
	}
	
	/**
	 * Initialise new items into the store's inventory
	 * <p>
	 * this is the method called from the back-end when the 'Import Items' button is pushed in the GUI
	 * it takes a stock full of items from the CSV reader, and uses it to initialise new items into 
	 * the store stock. Each new item is initialised at quantity 0
	 * @param toAdd a stock instance full of items that will be iterated through in the ImportItems method
	 */
	public void ImportItems(Stock toAdd) {
		
		//loop through each item in the given stock (use the stock's keyset to do a for each loop)
		for (Item item : toAdd.keySet()) {
			
			//check if the item isn't already in the stores stock
			if (!stock.Contains(item)) {
				
				//if the items doesn't exist yet, add it to the stores stock
				stock.addItem(item);
			}
		}
		
		//import items store-side
	}
	
	/**
	 * Process a delivery order to restock the store's inventory
	 * <p>
	 * This is the method that's called by the Gui's "Import Manifest" button.
	 * Method loops through each item in the given truck order, and 'buys' them.
	 * Altering given quantities in the stores inventory and changing store capital based on the price of trucks
	 * and buying cost of each item involved.
	 * @param toAdd the manifest given by the CSVReader's 'ImportManifest' method
	 * @throws DeliveryException if the manifest tries to reference an item which doesnt exist in the store inventory
	 * @throws StockException if the changeQuantity method (processing adding quantity to an inventory item) creates a negative quantity
	 */
	public void ImportManifest(Manifest toAdd) throws DeliveryException, StockException {

		//loop through each truck in the manifest
		for (Truck truck : toAdd) {
			
			//initialise a temp stock, and assign it to the trucks stock reference.
			Stock tempStock = truck.getStock();
			
			//loop through each item in the tempStock (use keyset to do an iterable for each loop)
			for (Item item : tempStock.keySet()) {
				
				try {
					//try changing the quantity and capital of current item.
					//this works inversely to the importSales method.
					//the quantity increases by the given quantity in the trucks stock
					ChangeQuantity(item, tempStock.getQuantity(item));
					
					//the capital decreases by the current item's buy cost, multiplied by its quantity within the current truck.
					ChangeCapital(-(item.GetCost() * tempStock.getQuantity(item)));
					
				} catch (NullPointerException | StockException e) {
					
					//if a null pointer exception is thrown, then the manifest is trying to reference an item which hasnt been
					//initialised in the store yet.
					
					//translate that error into a delivery exception, and throw it.
					DeliveryException deliveryErr = new DeliveryException("manifest is trying to reference an item which doesnt exist");
					throw deliveryErr;
				}
			}
			//after each truck has successfully iterated, we also need to decrease the capital by the truck's calculated price.
			ChangeCapital(-truck.getPrice());
		}
		
	}
	
	// ======================================================

}
