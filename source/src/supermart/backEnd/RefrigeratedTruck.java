package supermart.backEnd;

import supermart.SupermartEnums.ToStringType;
import supermart.SupermartEnums.TruckType;
import supermart.exceptions.DeliveryException;

/**
 * @author Harrison Berryman - 09745092
 * 
 * Constructs a RefigeratedTruck object based on the Truck interface
 */
public class RefrigeratedTruck implements Truck {
	
	
	Stock stock;
	double temperature;
	int capacity;
	DeliveryException cap = new DeliveryException("Refrigerated Truck over capacity. given cap: "+capacity);
	
	/**
	 * Constructs a RefigeratedTruck object
	 * <p>
	 * Constructs a RefigeratedTruck object with a set capacity
	 * of 800, assigns it a stock and sets a temperature based 
	 * on the coldest item in the stock.
	 * 
	 * @param stock The stock used by the truck to contain items
	 * @throws DeliveryException thrown when truck's stock over capacity 
	 */
	public RefrigeratedTruck(Stock stock) throws DeliveryException {
		capacity = TruckType.REFRIGERATED.getCapacity();
		this.stock = stock;
		temperature = stock.getLowestTemp();
	}
	
	/**
	 * Sets the stock the truck uses
	 *<p>
	 * Sets stock only if the total number of items in the stock
	 * is under the capacity of the truck, otherwise a DeliveryException
	 * is thrown
	 * 
	 * @param stock The stock used by the truck to contain items
	 * @throws DeliveryException thrown when truck's stock is over capacity
	 */
	public void SetStock(Stock stock) throws DeliveryException {
		if (stock.size() > capacity) {
			throw cap;
		}
		else {
			this.stock = stock;
			temperature = stock.getLowestTemp();
		}
		
	}
	

	/**
	 * Returns the stock used by the truck
	 * @return The stock used by truck
	 */
	public Stock getStock() {
		return stock;
	}

	/**
	 * Returns the cost of using the truck
	 * <p>
	 * Calculated based on the lowest temperature the
	 * truck requires to be to keep items fresh.
	 * 
	 * @return The price of using the truck
	 */
	public double getPrice() {
		double price = 900.0 + 200.0 * Math.pow(0.7, temperature/5);
		return price;
	}
	
	/**
	 * Returns the temperature set by the truck.
	 * 
	 * @return The temperature of the truck
	 */
	public double getTemperature() {
		return temperature;
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
	 * @return True if the truck's stock is at capacity, false if not
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
	 * Expected string should be "Refrigerated
	 * <p>
	 * itemName1, itemQuantity1, itemName2, itemQuantity2, etc."
	 * 
	 * @return A string of the truck's type and each item
	 * and item quantity in it's stock
	 */
	public String toString() {
		String string = ">Refrigerated\n" + stock.toString(ToStringType.NAME);
		return string;
	}

}

