package supermart.backEnd;

import supermart.exceptions.DryException; 

/**
 * @author Harrison Berryman - 09745092
 * 
 * Builds an item object for use in stock.
 * <p>
 * Builds an item object with a name, cost, price, reorder point,
 * reorder amount and temperature (for cold items).
 */
public class Item {

	//fields used to store information about the item
	private String name;
	private double cost;
	private double price;
	private int reorderPoint;
	private int reorderAmount;
	private double temperature;
	
	//isDry is a boolean variable used to determine whether or not the item will require special storage
	private boolean isDry;
	
	/**
	 * An abstract item constructor used in the other two constructors
	 * <p>
	 * As there are two different types of items (Dry and Cold), two different
	 * constructors are needed to prevent a DryException being thrown.
	 * This constructor is used in the building of an item in the other two constructors.
	 * 	@param name The name of the item e.g. "fish"
	 * @param cost The cost of the item when purchased by the store
	 * @param price The price of the item the store sells it at
	 * @param reorderPoint When the item's quantity reaches this point it is reordered
	 * @param reorderAmount When an item is reordered, this is the amount reordered
	 */
	private void SetupItem(String name, double cost, double price, int reorderPoint, int reorderAmount) {
		this.name = name;
		this.cost = cost;
		this.price = price;
		this.reorderPoint = reorderPoint;
		this.reorderAmount = reorderAmount;
	}
	
	/**
	 * An item constructor for cold items with a temperature
	 * <p>
	 * Constructs an item based on the parameters provided, including
	 * a temperature that the item must be kept at when being shipped
	 * in a truck.
	 * @param name The name of the item e.g. "fish"
	 * @param cost The cost of the item when purchased by the store
	 * @param price The price of the item the store sells it at
	 * @param reorderPoint When the item's quantity reaches this point it is reordered
	 * @param reorderAmount When an item is reordered, this is the amount reordered
	 * @param temperature The minimum temperature the item must be kept at when in a truck
	 */
	public Item(String name, double cost, double price, int reorderPoint, int reorderAmount, double temperature) {
		
		SetupItem(name, cost, price, reorderPoint, reorderAmount);
		
		this.temperature = temperature;
		
		this.isDry = false;
	}

	/**
	 * An item constructor for dry objects that have no temperature
	 * <p>
	 * Constructs an item based on the parameters provided.
	 * @param name The name of the item e.g. "corn"
	 * @param cost The cost of the item when purchased by the store
	 * @param price The price of the item the store sells it at
	 * @param reorderPoint The quantity the item must reach before it's reordered
	 * @param reorderAmount The quantity of items reordered whenever the reorderPoint is reached
	 */
	public Item(String name, double cost, double price, int reorderPoint, int reorderAmount) {
		
		SetupItem(name, cost, price, reorderPoint, reorderAmount);
		
		this.isDry = true;
	}
	
	/**
	 * Returns the name of the item
	 * @return The name of the item e.g. "corn"
	 */
	public String GetName() {
		return name;
	}
	
	/**
	 * Returns the cost of the item
	 * @return The cost of the item when purchased by the store
	 */
	public double GetCost() {
		return cost;
	}
	
	/**
	 * Returns the price of the item
	 * @return The price of the item the store sells it at
	 */
	public double GetPrice() {
		return price;
	}
	
	/**
	 * Returns the reordering point of the item
	 * @return The quantity the item must reach before it's reordered
	 */
	public int GetReorderPoint() {
		return reorderPoint;
	}
	
	/**
	 * Returns the reordering point of the item
	 * @return The quantity of items reordered whenever the reorderPoint is reached
	 */
	public int GetReorderAmount() {
		return reorderAmount;
	}
	
	/**
	 * Returns true if the item is dry and has no temperature.
	 * Returns false if the item is cold.
	 * @return Returns true if item is dry, false if otherwise
	 */
	public boolean CheckIfDry() {
		if (isDry == true) {
			return true;
		}
		else {
			return false;
		}
	}	
	
	/**
	 * Returns a string of the item's name and all other parameters
	 * <p>
	 * Expected string should be "itemName, itemCost, itemPrice, reorderPoint, reorderAmount"
	 * If the item is not dry it should also include temperature at the end.
	 * <p>
	 * This is done for use in the GUI and when converted to a CSV.
	 * 
	 * @return A string of the Item's details including temperature
	 * if the item is cold.
	 */
	public String toString() {
		
		if (isDry == true) {
			return name + "," + cost + "," + price + "," + reorderPoint + "," + reorderAmount;
		}
		else {
		return this.name + "," + cost + "," + price + "," + reorderPoint + "," + reorderAmount + "," + temperature;
		}
	}
	
	/**
	 * Returns the temperature of the item, throws exception if item is dry
	 * <p>
	 * Used to return the current temperature of the item, first isDry is checked
	 * to determine if the item is dry, if it is a DryException is thrown
	 * as there is no temperature to return.
	 * 
	 * @return The item's temperature if the item is cold
	 * @throws DryException If the item is dry, to prevent errors
	 */
	// ----------------------------------------
	public double GetTemperature() throws DryException {
		

		DryException dry = new DryException(this, name);
		
		if (isDry) {
			throw dry;
		} else {

			return temperature;
		}
	}
	
	//for use in the comparator
	//explicitly suppresses 
	/**
	 * A method for use in the comparator
	 * 
	 * @return null if the item is dry
	 * @return the temperature if item is cold
	 */
	public Double GetSafeTemperature() {
		if (isDry) {
			return null;
		} else {
			return temperature;
		}
	}
}
