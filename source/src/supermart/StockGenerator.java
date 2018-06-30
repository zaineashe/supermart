package supermart;

import java.util.Random;

import supermart.backEnd.Item;
import supermart.backEnd.Stock;
import supermart.exceptions.DryException;

//static class used to generate random stocks for TruckTest.java and Manifest.java
//each truck needs to be filled with a randomly generated stock, and given the these tests
//are testing the functionality of truck and manifest (as opposed to the functionality of stock),
//the same method of stock generation can be used across both tests.
//So instead of putting this set of methods into both junit suites, this abstract class has been created.

/**
 * 
 * @author Zaine Ashe - 09469010
 *
 * A class that generates a random stock for use in testing
 * <p>
 * Used for TruckTest.java and ManifestTest.java where trucks need
 * to be filled with stock before testing each classes functionality.
 * Placed into it's own class for neatness's sake.
 */
public class StockGenerator {
	
	static Random rand = new Random();
	
	//list of example items used to generate a new stock
	static Item[] itemsList = new Item[] {
			new Item("Beans", 4, 5, 120, 200),
			new Item("Rice", 1, 3, 300, 320),
			new Item("Potatoes", 1, 3, 300, 320),
			new Item("Broccoli", 1, 3, 300, 320),
			new Item("Tomatoes", 1, 3, 300, 320),
			new Item("Beef", 10, 12, 300, 320, 5),
			new Item("Chicken", 11, 13, 300, 320,4),
			new Item("Salmon", 11, 13, 300, 320,3),
			new Item("ice cream", 6, 12, 260, 300, -5),
			new Item("icy pole", 6, 12, 260, 300, -6),
			new Item("frozen vegetables", 6, 12, 260, 300, -4)
	};
	
	/**
	 * Abstracted method to pick a random item from the item list.
	 * @return the randomly selected item
	 */
	public static Item generateItem() {
		return itemsList[rand.nextInt(itemsList.length)];
	}
	
	/**
	 * Generates and returns a randomly generated temperature
	 * value from -10 to 10.
	 * @return the randomly generatred temperature double
	 */
	public static double generateTemperature() {
		return -20 + rand.nextDouble() * 5;
	}
	
	/**
	 * Sets the size of the randomly generated stock
	 * @param maxSize The maximum size of the generated stock
	 * @return maxSize
	 */
	public static int stockSize(int maxSize) {
		return 1 + rand.nextInt(maxSize);
	}
	
	/**
	 * Generate a stock of a certain size containing only dry items.
	 * @param size The size of the stock generated
	 * @return the generated stock
	 */
	public static Stock generateStock(int size) {
		
		//System.out.println(" -- > GENERATING A DRY STOCK ------------------------------------------");
		
		//initialise new stock to return
		Stock stock = new Stock();
		
		//repeat item generation for the size of the stock
		for (int i = 0; i < size; i++) {
			//generate a new item toAdd
			Item toAdd = generateItem();
			
			//before adding the generated item into the stock, run
			//a quick while loop. The loop checks if the item is
			//dry or not, regenerating the item if its cold
			while (toAdd.CheckIfDry() == false) {
				
				//this means that each time the item is
				//generated, the while loop will re-check if
				//it is dry or not. If its not a dry item
				//it will recall this line and generate a new
				//item
				toAdd = generateItem();
			}
			
			//after the generated item is guaranteed to be dry
			//add it to the stock
			
			//System.out.println("adding " + toAdd.toString() + "...");
			
			stock.addItem(toAdd);
			
			stock.AddQuantity(toAdd, 1);
		}
		
		//return the final stock (length of the size argument)
		return stock;
	}
	
	// generate stock (including cold items)
	// temperature argument is included so that this stock can adhere to a temperature restriction
	// i.e. if the temperature double is set to 5, the generator will add any
	// item that is dry, or has a temp requirement thats 5 degrees or hotter.
	// (if temperature is set to 5, and ice cream needs -3, ice cream will never be added to this stock)
	/**
	 * Generate a stock of a certain size containing both dry and non-dry items.
	 * 
	 * @param temperature The minimum temperature a cold item can be to be added to the stock
	 * @param size The size of the generated stock
	 * @return The generated stock
	 */
	public static Stock generateStock(int size, double temperature) {
		
		//System.out.println(" -- > GENERATING A COLD STOCK ------------------------------------------");
		
		//initialise new stock to return
		Stock stock = new Stock();
		
		//repeat the item generation process
		//for the given size of the stock
		//(for loop runs using given size argument)
		for (int i = 0; i < size; i++) {
			
			//generate a new item
			Item toAdd = generateItem();
			
			// for this stock, the item can be dry, or it can be cold.
			// a dry item gets a free pass into the stock
			// but a cold item needs to meet certain temperature requirements to be added
			// (can't be colder than the given temp argument, or else it will spoil) 
				
			// to do this, we need to try and run a GetTemperature() method
			// because the GetTemperature() method can throw a DryException, we put
			// this loop inside a try/catch statement
			try {
				
				// while loop used to guarantee the item temperature is equal to
				// or above the given temperature argument.
				while(toAdd.GetTemperature() < temperature) {
					
					// if the item's temperature requirement is less than the given temp,
					// then it will spoil, so we need to regenerate it
					toAdd = generateItem();
				}
			} catch (DryException dry) {
				
				//if the dry exception is thrown, then this item is dry.
				//a dry item gets a free pass into the stock, because it doesn't
				//need to conform to a temperature standard.
				//ergo, do nothing.
			}
			
			//once the item is guaranteed to fit the criteria, add it to the stock.
			//System.out.println("adding " + toAdd.toString() + "...");
			stock.addItem(toAdd);
			
			stock.AddQuantity(toAdd, 1);
		}
		
		//return the final stock
		return stock;
	}
	
}
