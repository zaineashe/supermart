package supermart.exceptions;

import supermart.backEnd.Item;

/**
 * @author Harrison Berryman - 09745092
 * 
 * An exception thrown whenever getTemperature is attempted on
 * a Dry item with no temperature or whenever a cold item is
 * added to an ordinary truck.
 */
public class DryException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public DryException(Item item, String name) {
		super(	"ITEM ERROR: " + name + " is a dry Item and doesnt have temperature! Item Hash: " 
				+ System.identityHashCode(item));
	}
	
	public DryException() {
		super(	"TRUCK ERROR: This is a dry Truck and doesnt accept temperature controlled items!");
		
	}


}
