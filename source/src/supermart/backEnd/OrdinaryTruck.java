package supermart.backEnd;

import supermart.SupermartEnums.ToStringType;
import supermart.SupermartEnums.TruckType;
import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;

/**
 * @author Harrison Berryman - 09745092
 * 
 * Constructs an OrdinaryTruck object based on the Truck interface
 */
public class OrdinaryTruck implements Truck {
	
	
	Stock stock;
	int capacity;
	DeliveryException cap = new DeliveryException("Refrigerated Truck over capacity. given cap: "+capacity);
	DryException dry = new DryException();

	/**
	 * Constructs an OrdinaryTruck object
	 * <p>
	 * Constructs an OrdinaryTruck object with a set capacity
	 * of 1000 and assigns it a stock.
	 * 
	 * @param stock the stock used by the truck to contain items
	 * @throws DeliveryException thrown when truck's stock is over capacity
	 * @throws DryException thrown if a cold item is in the truck's stock
	 */
	public OrdinaryTruck(Stock stock) throws DeliveryException, DryException {
		
		capacity = TruckType.ORDINARY.getCapacity();
		
		this.stock = stock;
	}
	
	/**
	 * Sets the stock the truck uses
	 *<p>
	 * Sets stock only if the total number of items in the stock
	 * is under the capacity of the truck, otherwise a DeliveryException
	 * is thrown
	 * <p>
	 * If a cold item is added to the stock a DryException is thrown as
	 * only RefrigeratedTrucks can carry cold items.
	 * 
	 * @param stock The stock used by the truck to contain items
	 * @throws DeliveryException thrown when truck's stock is over capacity
	 * @throws DryException thrown if a cold item is added to stock
	 */
	public void SetStock(Stock stock) throws DeliveryException, DryException {
		
		for (Item i : stock.keySet()) {
		if (stock.size() > capacity) {
			throw cap;
		}
		
		else if(i.CheckIfDry() == false) {
			throw dry;
		}
		else {
			this.stock = stock;

		}
		}
	}
	
	/**
	 * Returns the stock used by the truck.
	 * @return The stock used by truck
	 */
	public Stock getStock() {
		return stock;
	}

	/**
	 * Returns the cost of using the truck
	 * <p>
	 * Calculated based on the total number of items in
	 * the trucks stock.
	 * 
	 * @return The price of using the truck
	 */
	public double getPrice() {
		double price = 750 + (0.25 * stock.size());
		return price;
	}
	
	/**
	 * For an OrdinaryTruck this should always just throw a DryException.
	 * This is here as it's inherited from the Truck abstract class.
	 * 
	 * @throws DryException thrown every time the method is called as OrdinaryTrucks do not have temperature
	 */
	public double getTemperature() throws DryException {
		DryException dry = new DryException();
		throw dry;
	}

	/**
	 * Returns the total number of items in the truck.
	 * 
	 * @return The size of the stock in the truck
	 */
	public int getCount() {
		return stock.size();
	}
	
	/**
	 * Returns true if the truck's stock is at capacity.
	 * Returns false if the truck is not at capacity.
	 * @return true if the truck's stock is at capacity, false if not
	 */
	public boolean IsFull() {
		if (stock.size() >= capacity){
			return true;
		}
		else {
			return false;		
		}
	}
	
	/**
	 * Returns a string of the type of truck, the items in it's stock
	 * and their quantities.
	 * <p>
	 * Expected string should be "&gt;Ordinary
	 * <p>
	 * itemName1, itemQuantity1, itemName2, itemQuantity2, etc."
	 * 
	 * @return A string of the truck's type and each item
	 * and item quantity in it's stock
	 */
	public String toString() {
		String string = ">Ordinary\n" + stock.toString(ToStringType.NAME);
		return string;
	}
	


}
