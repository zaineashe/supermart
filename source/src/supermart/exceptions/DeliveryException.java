package supermart.exceptions;

/**
 * @author Zaine Ashe - 09469010
 * 
 * An used by CSVReader when parsing CSVS
 * <p>
 * Can be thrown when a non-dry item is added to an ordinary truck.
 * Can be thrown when a non-existent truck type is used when building a truck.
 * Can be thrown when a CSV contains an unrecognised truck type.
 */
public class DeliveryException extends Exception {

	private static final long serialVersionUID = 1L;

	public DeliveryException(String message) {
		super("DELIVERY ERROR: " + message);
	}
	
	public String getMessage() {
		return super.getMessage();
	}
	
}
