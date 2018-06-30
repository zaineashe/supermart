package supermart.backEnd;

import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;

/**
 * @author Zaine Ashe - 09469010
 *  
 * Interface for use in RefrigeratedTruck and Ordinary Truck classes
 */
public interface Truck {
	
	/**
	 * Sets the stock the truck uses
	 * <p>
	 * DryException is only thrown in OrdinaryTruck as no cold items can
	 * be stored in it.
	 * 
	 * @param stock The stock used by the truck to contain items
	 * @throws DeliveryException thrown when truck's stock is over capacity
	 * @throws DryException thrown when a non-dry item is added to stock
	 */
	public void SetStock(Stock stock) throws DeliveryException, DryException;
	
	/**
	 * Returns the stock used by the truck
	 * @return The stock used by truck
	 */
	public Stock getStock();
	
	/**
	 * Returns the cost of using the truck
	 * <p>
	 * Calculated based either the number of items in the trucks stock
	 * or the lowest temperature of the truck depending on whether the
	 * truck is refrigerated or ordinary.
	 * 
	 * @return The price of using the truck
	 */
	public double getPrice();
	
	/**
	 * Returns the total number of items in the truck
	 * 
	 * @return The size of the stock in the truck
	 */
	public int getCount();
	
	/**
	 * Returns the temperature set by the truck. Throws exception when used on Ordinary Truck
	 * 
	 * @return The temperature of the truck
	 * @throws DryException Thrown when used on OrdinaryTruck has it has no temperature
	 */
	public double getTemperature() throws DryException;

	/**
	 * Returns true if the truck's stock is at capacity.
	 * Returns false if the truck is not at capacity.
	 * @return true if the truck's stock is at capacity, false if not
	 */
	public boolean IsFull();
	
	/**
	 * Returns a string of the type of truck, the items in it's stock
	 * and their quantities.
	 * <p>
	 * Expected string should be "&gt;Refrigerated/Ordinary
	 * <p>
	 * itemName1, itemQuantity1, itemName2, itemQuantity2, etc."
	 * 
	 * @return A string of the truck's type and each item and item quantity in it's stock
	 */
	public String toString();
	
}
