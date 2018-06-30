package supermart.exceptions;

/**
 * @author Zaine Ashe - 09469010
 * 
 * An exception thrown whenever an Item that doesn't exist in the stock
 * is sold, or has quantity added. Can also be thrown when an item in the Store's
 * inventory has a negative amount.
 */
public class StockException extends Exception {

	private static final long serialVersionUID = 1L;

	public StockException(String message) {
		super("STOCK ERROR: " + message);
	}
	
	public String getMessage() {
		return super.getMessage();
	}
}
