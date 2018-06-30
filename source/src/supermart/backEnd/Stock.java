package supermart.backEnd;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import supermart.SupermartEnums.ToStringType;
import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;
import supermart.exceptions.StockException;

/**
 * @author Zaine Ashe - 09469010
 * 
 * Builds a stock for use with Trucks, Stores and Manifests.
 * <p>
 * Builds a stock used to contain Item and Item information.
 * Used by both Trucks, Stores and Manifests as a list of items they carry.
 */
public class Stock extends AbstractMap<Item, Integer> {

	//hashmap of items and their associated quantities
	//this hashmap is the foundation of our stock
	//it lets us assign dynamic quantities to each item
	//and it keeps the memory usage low
	//(this helps with the large amounts of items to be passed through manifests and sales logs)
	
	//the trade-off here is that stock can no longer be accessed via an index.
	//therefore, when we do toString for our stock, we NEED to do it in lexicographical order by name
	HashMap<Item, Integer> stock;
		
	/**
	* Constructs a stock object and builds a hashmap.
	*/
	public Stock() {
		
		//initialise the stock hashmap
		stock = new HashMap<Item, Integer>();
	}
	
	 /**
	 * Constructs a stock object from an arrayList of strings.
	 * @param store the store referenced by the stock
	 * @param builder the ArrayList containing the items used to build the stock
	 * @throws DeliveryException Thrown when an item in the stock doesn't exist
	 */
	public Stock(Store store, ArrayList<String> builder) throws DeliveryException {
		
		stock = new HashMap<Item,Integer>();
		
		int i = 1;
		while (i<builder.size()) {
			
			try {
				
				Item item = store.getItem(builder.get(i));
				
				if (item == null) {
					
					NullPointerException nullErr = new NullPointerException("tried to initialse a null item into stock");
					throw nullErr;
					
				} else {
					Integer quantity = Integer.parseInt(builder.get(i+1));
					
					stock.put(item, quantity);
				}
			
				
				
			} catch (NullPointerException e) {
				
				DeliveryException deliveryErr = new DeliveryException("manifest is trying to reference an item which doesnt exist");
				throw deliveryErr;
			}
			
			i+=2;
		}
		
		
	}
	
	//Add item to the HashMap
	//This initialises an item into the stock with quantity 0
	//this is different from changing the quantity of an item (changeQuantity 
	//can be used to increase or decrease the quantity of an item which has already been initialised)
	 /**
	 * Adds an item to the stock.
	 * <p>
	 * Adds the specified item into the stock with a starting
	 * quantity of 0.
	 * 
	 * @param item The item to be added into the stock
	 * @return returns true if the item is successfully added, false if otherwise
	 */
	public boolean addItem(Item item) {
		
		//first, check if this item isnt already in the stock
		if (!this.Contains(item)) {
			 
			//if the item hasnt already been added, then perform the addItem process
			//set the item as a key in the hashmap, with its associated quantity as 0
			stock.put(item, 0);
			
			//return true to signify that the collection has changed
			return true;
		} else {
			
			//if the item is already being used as a key in the stock hashmap,
			//theres no need to add it
			//return false because the collection hasnt changed
			return false;
		}
		
	}
	
	
	//this method serves counter to addItem, it removes an item 
	//if it is already a key in the stock hashmap
	/**
	 * Removes an item from the stock.
	 * <p>
	 * Checks to see if the item is already in the stock, if so
	 * it removes the specified item from the stock.
	 * This happens from the hashmap so the item is entirely removed.
	 * 
	 * @param item The item to be removed from the stock
	 * @return returns true if the item is succesfully removed, false if otherwise or item not found
	 */
	public boolean removeItem(Item item) {
		
		//check if the given item is in the stock
		if (this.Contains(item)) {
			
			//if the item is a key in the stock hashmap, then remove it.
			stock.remove(item);
			//return true to signify the stock has changed
			return true;
		} else {
			
			//if the item doesnt exist in the stock's hashmap, then it cant be removed
			//return false to signify the hashmap hasnt changed
			return false;
		}
		
	}
	
	/**
	 * Sets the quantity of an item in the stock.
	 * <p>
	 * First checks to ensure the item is in the stock, then sets
	 * the specified quantity for the item.
	 * Throws StockException if a negative quantity is attempted.
	 *  
	 * @param item The item to be modified
	 * @param quantity The quantity to be added to the item, must be positive
	 * @throws StockException Thrown when a negative quantity is attempted
	 */
	public void SetQuantity(Item item, int quantity) throws StockException {
		
		//first, check if the given item is currently within the stock.
		if (this.Contains(item)) {
			
			//calculate a new quantity for the associated item, using the parsed argument
			//if this newly calculated quantity is negative, then throw a stock exception
			if (quantity < 0) {
				
				//if the quantity is negative, then throw stock exception
				//construct stock exception with a custom error message, and throw it
				StockException stockErr = new StockException(	"negative quantity!: " + quantity +
																" is an invalid number to assign to " + item.GetName() +  "!");
				throw stockErr;
			} else {
				
				//if the quantity is valid, then assign it to the item key
				stock.put(item, quantity);
			}
			
		} else {
			
			//if the parsed item doesnt exist, throw a stock exception
			//construct a stock exception with a custom message and throw it
			StockException stockErr = new StockException("The given item key for '"+item.GetName()+"' is not in the stock");
			throw stockErr;
		}
	}
	
	//this is just a simple method to bypass the SetQuantity method
	//it automatically catched the stock exception and returns 'false' if 
	//if you want to quickly add a quantity to the stock without checking it for errors
	//this is the method to use.
	//it will return false if the function threw an error.
	// ================================================================
	// ---		TRY TO AVOID USING THIS METHOD IN THE GUI 			---
	// ---		THIS IS MAINLY FOR MAKING THE TEST SUITES NEATER	---
	// ---		USE SetQuantity() AS MUCH AS POSSIBLE				---
	// ================================================================
	 /**
		 * Adds a set amount to the quantity of an item in the stock
		 * <p>
		 * Checks that the item is in the stock, then adds the amount
		 * to the quantity of an item in the stock.
		 * <p>
		 * This method was built for quick and neat testing and shouldn't
		 * be called outside of tests if at all possible.
		 * 
		 * @param item The item to be added into the stock
		 * @param quantity The quantity to be added to the item's quantity
		 * @return true if the quantity is correctly added, false if otherwise
		 */
	public boolean AddQuantity(Item item, int quantity) {
		
		//check if the item exists in the stock
		if (this.Contains(item)) {
			
			//if the item exists, we can try to add to it's quantity
			//these statements are in a try/catch to supress the throwing of a StockException
			//this makes this method easy to access without the need to handle it over and over in the test suites.
			try {
				
				//try adding to the quantity of the given item
				//the new quantity is calculated by adding the items current quantity
				//together with the quantity given as an argument
				this.SetQuantity(item, this.getQuantity(item) + quantity);
				
				//return true to signify the method succeeded
				return true;
			} catch(StockException stockErr) {
				
				//return false to signify the method has failed
				return false;
			}
		} else {
			
			//if the item doesn't exist in the stock, we can't change its quantity
			//return false to signify the method has failed
			return false;
		}
	}
	
	 /**
     * Returns a set of all the items in the stock.
	 * 
	 * @return A keyset of all unique items in the stock
	 */
	public Set<Item> keySet() {
		
		//return stock's keyset, which is all unique items currently in the stock
		return stock.keySet();
	}
	
	/**
	 * Returns true if stock is empty
	 * Returns false if stock has any items in it
	 */
	public boolean isEmpty() {
		return stock.isEmpty();
	}
	
	 /**
	 * Returns the quantity of a specific item.
	 * 
	 * @param item The item to return the quantity of 
	 * @return The quantity of the specified item
	 */
	public int getQuantity(Item item) {
		
		//get the quantity associated with this item in the list
		//check if this item exists in this list, else throw exception
		//if item exists in this list, return its associated quantity
		return stock.get(item);
	}

	 /**
	 * Returns the total size of all items in the stock.
	 *
	 * @return The size of all items currently in the stock
	 */
	public int size() {
		
		//loop through each item in the stock list and add together their tallies
		//initialise int size to keep track of total quantity
		int size = 0;
		
		//use a for each loop format to iterate through the unindexed set
		for (Item item : stock.keySet()) {
			
			//for this iteration, add the item's associated quantity to the final
			//size count
			size += this.getQuantity(item);
		}
		
		//return the final size count
		return size;
	}
	
	/**
	 * Returns the count of each unique item in the stock
	 * <p>
	 * Only returns the count of unique items in the stock, if an item
	 * has a quantity higher than one it is still only counted once.
	 * 
	 * @return The count of unique items currently in the stock
	 */
	public int CountUniqueItems() {
		
		//return the size of the stocks keyset
		//because the stock hashmap is keyed by item, this
		//keyset will be a set of all items currently in the stock
		return stock.keySet().size();
	}
	
	 /**
	 * Searches to find the specified item in the stock.
	 * <p>
	 * Returns true if the specified item is in the stock.
	 * Returns false if otherwise.
	 * 
	 * @param item The item being searched for in the stock 
	 * @return True if the item is in the stock, false if it isn't
	 */
	public boolean Contains(Item item) {
		
		//check if there is an item in this Stock that has all the same attributes as the parsed item
		return stock.containsKey(item);
	}
	
	 /**
	 * Searches to find the specified item in the stock by searching for the itemName.
	 * <p>
	 * Returns true if the specified item is in the stock.
	 * Returns false if otherwise.
	 * 
	 * @param itemName A string containing the desired items name 
	 * @return True if the item is in the stock, false if it isn't
	 */
	public boolean Contains(String itemName) {
		
		boolean contains = false;
		
		for (Item item : stock.keySet()) {
			if (item.GetName().equals(itemName)) {
				contains = true;
			}
		}
		
		return contains;
	
	}
	
	 /**
	 * Returns the specified item if it is in the stock by searching for the itemName.
	 * 
	 * @param itemName A string containing the desired items name 
	 * @return True if the item successfully retrieved, false if not
	 * 
	 */
	public Item getItem(String itemName) {
		
		for (Item item : stock.keySet()) {
			if (item.GetName().equals(itemName)) {
				return item;
			}
		}
		return null;
	}
	
	//Convert's the itemList to a string
	//Example format: "Beef,Cheese,Corn,Milk"
	//sorts the keys of the stock hashmap lexicographically then returns them
	//alongside their associated quantities
	/**
	 * Returns the itemList converted to a String
	 * <p>
	 * toStringType.NAME returns items and quantities
	 * toStringType.DETAILS returns items and all item details other than quantity
	 * 
	 * @param type The type to be used by toString (either NAME or DETAILS)
	 * @return The itemList converted into a string
	 */
	public String toString(ToStringType type) {
		
		//two types of ways to toString stock
		// items and quantities (NAME)
		// items and details (DETAILS)
		
		// initialise the empty string to be used as the final message
		String message = "";
		
		//check whether or not a ToStringType enum has been listed
		//this will change the final outcome, and is included for scalability
		//(i.e. if an 'export items list' feature ever needs to be added)
		if (type == ToStringType.NAME || type == ToStringType.DETAILS) {
			
			// ------ SORT ITEMS IN THE KEYSET ALPHABETICALLY ------
			
			//this toString needs to be presented in alphabetical order
			//because hashmaps don't order their contents by index like an arraylist does
			//so the first step in a toString, is to alphabetically order the keyset of Items in the Stock
			
			//the first thing needed to order these items in alphabetical order, is a comparator.
			//construct a new comparator for Item which compared by the item's GetName function
			//this will allow us to sort out items list by their string values, which will give
			//the Collections class the data it needs to perform a lexicographical ordering
			Comparator<Item> lexicographicComparator = Comparator.comparing(Item::GetName);
			
			//initialise a new arraylist to order the items
			//because a Set isn't indexed, we need to copy our items over to this local ArrayList
			//to perform the sort() method
			List<Item> items = new ArrayList<Item>();
			
			//copy all items from the stock's keyset (all unique items) over to the sorting list
			items.addAll(this.keySet());	
			
			//sort the items using the lexicographicComaparator
			//this comparator, as previously stated, will reach into each item's GetName() method and sort 
			//the resulting String values
			Collections.sort(items, lexicographicComparator);
			
			//now that our items are alphabetically ordered, we run the actual toString code
			//loop through each item in the alphabetically sorted list
			for (int i = 0; i < items.size(); i++) {	
				
				//construct a new local item and assign its value to the currently indexed item
				Item item = items.get(i);
				
				//check what type of toString is being performed
				if (type == ToStringType.NAME) {
					
					//if this toString's type is NAME, then just give the names and quantities of the current item
					//these two values are seperated by a comma (no space, CSV formatting)
					message += item.GetName() + "," + this.getQuantity(item);
					
				} else if (type == ToStringType.DETAILS) {
					
					//if this toString's type is DETAILS, then give a list of all the item's details
					//this is done by calling the item's toString method
					message += item.toString();
				}
				
				//each item in the toString needs to be seperated by a comma ','
				//but if this is the final item, then there shouldn't be a comma
				//ergo, check if the current index is not yet at the end of the list
				if (i < items.size() - 1) {
					
					//if this isn't the final item, then add a comma before the next iteration
					if (type == ToStringType.NAME) {
						message += "\n";
					} else {
						message += ",";
					}
				}
			}
			
		} 
		
		//return the final message that has been constructed
		return message;
	}
	
	/**
	 * Exports the stock into it's raw hashmap format
	 * 
	 * @return The hashmap used to make the stock
	 */
	public HashMap<Item, Integer> getMap() {
		
		//return the stock as a hashmap
		return stock;
	}
	
	/**
	 * 
	 * @author Harrison Berryman - 09745092
	 * 
	 * Returns the temperature of the item in the stock with the lowest temperature
	 * <p>
	 * Uses a for loop to add each temperature for each item in the stock
	 * into an ArrayList, which is then sorted to find the lowest number.
	 * This lowest number is the minimum temperature required for any refrigerated truck
	 * carrying this stock.
	 *  
	 * @return The minimum temperature required for this stock
	 */
	public double getLowestTemp() {
		
		//construct a temporary arrayList to store each item's temperature
		ArrayList<Double> temp = new ArrayList<Double>();
		
		//loop through each unique item in the stock to grab their temperatures
		for (Item i : stock.keySet()) {
			
			//check if the current item is cold (has a temperature)
			//a dry item won't count in this test, so it's immediately overlooked
			if (i.CheckIfDry() == false) {
				
				//try to add the current item's temperature to the temp list
				try {
					
					//using the current item's GetTemperature method, add items
					//temperature to the list
					//GetTemperature() is a method which can throw a DryException, which is why it's in
					//a try/catch statement
					temp.add(i.GetTemperature());
					
				} catch (DryException e) {
					
					// if a dry exception is caught, then overlook the item
					// print the stack trace of the error for debugging
					//e.printStackTrace();
				}
			}
		}
		
		//after all of the items in the stock have been cycled through and added to the temp list
		//use the static 'min()' method inherited from the Collections class to return the minimum temperature 
		return Collections.min(temp);
	}
	
	/**
	 * An inherited method from the HashMap class to return an entry set of the stock
	 * 
	 * @return The stock HashMap's entry set.
	 */
	@Override
	public Set<Entry<Item, Integer>> entrySet() {
		
		//return the stock HashMap's entry set
		return stock.entrySet();
	}

}
