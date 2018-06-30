//========================================
// ItemTests.java
// by Zaine Ashe 09469010
// for CAB302 Software Development Assessment 2
// junit test suite for the Item.java Class8
//========================================

package supermart.testSuites;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import supermart.SupermartEnums.ItemTestType;
import supermart.backEnd.Item;
import supermart.exceptions.DryException;

/**
 * @author Zaine Ashe - 09469010
 */
public class ItemTests {
	
	//random object to pull nextInt() and nextDouble() from
	private static Random random = new Random();
	
	//'magic number' constants
	private static double tempModifier = 2.0;
	private static double tempMax = 10.0;
	private static double doubleMax = 60.0;
	private static int intMax = 600;
	
	//array of names to pick from for dry Items
	private static String[] dryNames = new String[] {
			"Peanut Butter",	"Muesli",
			"Cereal",			"Carrots",
			"Mushrooms",		"Broccoli",
			"Bananas",			"Pineapple",
			"Oranges",			"Potatoes",
	};
	
	//array of names for cold items	
	private static String[] coldNames = new String[] {
			"Ice Cream",		"Milk",
			"Icy Poles",		"Frozen Veggies",
			"Frozen Spinach",	"Sausages",
			"Steaks",			"Chicken",
			"Pork Ribs",		"Yogurt",
	};
	
	// ----------------------------------------
	// private method to pick a random name from the names arrays
	// ----------------------------------------
	private String randName(ItemTestType type) {
		//uses ItemTestType instead of a true/false boolean (to make code more readable)
		if (type == ItemTestType.DRY) {
			//if the method is asking for a dry name, return a random name from the dryNames array
			return dryNames[random.nextInt(dryNames.length)];
		} else if (type == ItemTestType.COLD) {
			//if cold, return a random name from the coldNames array
			return coldNames[random.nextInt(coldNames.length)];
		} else {
			//else, return an error message as the name
			return "Name assignment failed (invalid item type)";
		}
	}
	
	// ----------------------------------------
	// method to generate a random double using the doubleMax constant
	// ----------------------------------------
	private double randDouble() {
		return random.nextDouble() * doubleMax;
	}
	
	// ----------------------------------------
	// method to generate a random int using the intMax constant
	// ----------------------------------------
	private int randInt() {
		return random.nextInt(intMax);
	}
	
	// ----------------------------------------
	// method to generate a random temperature.
	// the algorithm used is -tempMax + random.nextDouble() * tempMax * tempModifier
	// this guarantees a range of positive and negative numbers
	// considering tempMax's value is 10
	// and tempModifier's value is 2
	// this algorithm equates to -10 + (any number up to 20)
	// which makes a range of -10 to 10
	// ----------------------------------------
	private double randTemp() {
		return -tempMax + random.nextDouble() * tempMax * tempModifier;
	}
	
	
	Item item;
	
	
	// ----------------------------------------
	// Setup the item object before running tests
	// ----------------------------------------
	@Before
	public void Setup() {
		//the item variable has been initialized, but it doesn't have a value yet
		//in this setup method we give item a null value, so we
		//can use it in comparisons later on
		item = null;
	}
	
	
	// ----------------------------------------
	// Test one: constructing the item
	// ----------------------------------------
	@Test
	public void TestInit() {
		
		//create a new dry item
		//randName grabs a random name from the dryNames array
		//randDouble is used to generate price and cost
		//randInt is used to generate reorderPoint and reorderAmount
		item = new Item(randName(ItemTestType.DRY), randDouble(), 
						randDouble(), randInt(), randInt());
		
		//for this test to pass, the item object must be properly constructed
		//item originally has a null value from the Setup() method
		//ergo assertNotNull is used, to see if items value has changed from the setup
		assertNotNull(item);
	}
	

	// ----------------------------------------
	// Test two: constructing a dry item, and checking if it's dry
	// ----------------------------------------
	@Test
	public void TestDryInit() {
		
		//construct the item, without a temperature value
		item = new Item(randName(ItemTestType.DRY), randDouble(), 
						randDouble(), randInt(), randInt());

		//if the item's CheckIfDry method returns true, then test is passed
		assertEquals(true, item.CheckIfDry());
		
	}
	

	// ----------------------------------------
	// Test three: constructing a cold item, and checking if its cold
	// ----------------------------------------
	@Test
	public void TestColdInit() {
		
		//construct the item, with a temperature value included
		item = new Item(randName(ItemTestType.COLD), randDouble(), randDouble(),
						randInt(), randInt(), randTemp());
		
		//if the items CheckIfDry method returns false, test is failed
		assertEquals(false, item.CheckIfDry());
	}
	
	
	// ----------------------------------------
	// Test four: check if GetName works correctly
	// ----------------------------------------
	@Test
	public void TestGetName() {
		
		//generate a name from the array of example names
		String generatedName = randName(ItemTestType.DRY);
		
		//construct a new item with the generated name
		item = new Item(generatedName, randDouble(), 
						randDouble(), randInt(), randInt());
		
		//check if the item's GetName() method returns the same value
		//as the generated name
		assertEquals(generatedName, item.GetName());
	}
	

	// ----------------------------------------
	// Test five: check if GetCost works correctly
	// ----------------------------------------
	@Test
	public void TestGetCost() {
		
		//generate a double for the item cost using randDouble()
		double generatedCost = randDouble();
		
		//construct a new item with the generated cost
		item = new Item(randName(ItemTestType.DRY), generatedCost, 
						randDouble(), randInt(), randInt());
		
		//check if the item's GetCost() method returns the generated cost
		assertEquals(generatedCost, item.GetCost(), 0);
	}
	

	// ----------------------------------------
	// Test six: check if GetPrice works
	// ----------------------------------------
	@Test
	public void TestGetPrice() {
		
		//generate a double for the item price using randDouble()
		double generatedPrice = randDouble();
		
		//construct a new item with the generated price
		item = new Item(randName(ItemTestType.DRY), randDouble(), 
						generatedPrice, randInt(), randInt());
		
		//check if the items GetPrice() method returns the generated price
		assertEquals(generatedPrice, item.GetPrice(), 0);
	}
	

	// ----------------------------------------
	// Test seven: check if the GetReorderPoint method works
	// ----------------------------------------
	@Test
	public void TestGetReorderPoint() {
		
		//generate an int for the items reorder point randInt()
		int generatedReorderPoint = randInt();
		
		//construct a new item with the generated reorder point
		item = new Item(randName(ItemTestType.DRY), randDouble(), 
						randDouble(), generatedReorderPoint, randInt());
		
		//check if the items GetReorderPoint method returns the same value
		//as the generated reorder point
		assertEquals(generatedReorderPoint, item.GetReorderPoint());
	}
	

	// ----------------------------------------
	// Test eight: check if GetReorderAmount works
	// ----------------------------------------
	@Test
	public void TestGetReorderAmount() {
		
		//generate an int for the items reorder amount using randInt()
		int generatedReorderAmount = randInt();
		
		//construct a new item with the generated reorder amount
		item = new Item(randName(ItemTestType.DRY), randDouble(), 
						randDouble(), randInt(), generatedReorderAmount);
		
		//check if the items GetReorderAmount method returns the same value
		//as the generated reorder amount
		assertEquals(generatedReorderAmount, item.GetReorderAmount());
	}
	

	// ----------------------------------------
	// Test nine: check if GetTemperature works with a cold item
	// ----------------------------------------
	@Test
	public void TestGetTemperature() {
		
		//generate a new temperature using the randTemp() method
		double generatedTemperature = randTemp();
		
		//construct a new item using the generated temperature
		//because a temperature value is included in the constructor, the 
		//item class will immediately determine that this is a cold item
		item = new Item(randName(ItemTestType.COLD), randDouble(), randDouble(),
				randInt(), randInt(), generatedTemperature);
		
		// the following try/catch is set to trip if the item can't call it's GetTemperature method
		// (which means that the temperature wouldn't be properly initialized)
		// this shouldn't happen in this test, because a temperature value has been assigned
		try {
			
			//try to check the items temperature using the GetTemperature() method
			assertEquals(generatedTemperature, item.GetTemperature(), 0);
			
		} catch(Exception dry) {
			
			//if the DryException is tripped, than this test has failed
			//run a junit fail method with a quick error log saying that the item has been
			//defined as dry.
			fail("Item has been defined as dry");
		}
	}
	

	// ----------------------------------------
	// Test ten: check if GetTemperature throws a DryException on a dry item
	// ----------------------------------------
	@Test
	public void TestDryException() {
		
		//generate a new temperature to be used in the try assertion
		double generatedTemperature = randTemp();
		
		//construct a new item BUT, exclusively *leave out* the temperature value this time
		//this means that the item will construct without initializing a temperature value,
		//making the item dry.
		Item uninitialisedItem = new Item(randName(ItemTestType.DRY), randDouble(), randDouble(),
				randInt(), randInt());
		
		//run the same try/catch statement as in test nine. However, the goals are flipped
		//if this assertion trips the DryException, then the test has passed. 
		//this is because we've intentionally set this item up as dry to try
		//and trip the exception.
		try {
			//run the assertion, to see if the generated temperature matches the
			//GetTemperature method of the item
			//THIS SHOULD TRIP AN EXCEPTION(because the item is dry for this test)
			assertEquals(generatedTemperature, uninitialisedItem.GetTemperature(), 0);
			//if the line above doesn't trip the DryException, fail the test
			fail("DryException wasn't tripped, test failed");
			
		} catch(DryException dry) {
			//if the DryException has been successfully tripped, then the test has been passed
			//assert true and exit the test.
			assertTrue(true);
		}
	}
	
	// ----------------------------------------
	// Test eleven: check if toString returns a valid CSV format on dry item
	// ----------------------------------------
	@Test
	public void TestDryToString() {
		
		
		//generate a new set of values, without temperature
		String name = randName(ItemTestType.DRY);
		double cost = randDouble();
		double price = randDouble();
		int reorderPoint = randInt();
		int reorderAmount = randInt();
		
		//construct a dry item (no temperature)
		item = new Item(name, cost, price,
				reorderPoint, reorderAmount);
		
		//put together a predicted string for testing
		//this string is what the item.toString() method should also output
		String predictedString = 	name + "," + 
									cost + "," + 
									price + "," + 
									reorderPoint + "," + 
									reorderAmount;
		
		//check if item.toString() of the constructed item matches the predicted string
		assertEquals(predictedString, item.toString());
	}
	
	// ----------------------------------------
	// Test eleven: check if toString returns a valid CSV format on cold item
	// ----------------------------------------
	@Test
	public void TestColdToString() {
		
		//generate a new set of values, with temperature
		String name = randName(ItemTestType.COLD);
		double cost = randDouble();
		double price = randDouble();
		int reorderPoint = randInt();
		int reorderAmount = randInt();
		double temperature = randTemp();
		
		//construct a cold item (temperature included)
		item = new Item(name, cost, price,
				reorderPoint, reorderAmount, temperature);
		
		//put together a predicted string for testing
		//this string is what the item.toString() method should also output
		String predictedString = 	name + "," + 
									cost + "," + 
									price +"," + 
									reorderPoint + "," + 
									reorderAmount + "," + 
									temperature;
		
		//check if item.toString() of the constructed item matches the predicted string
		assertEquals(predictedString, item.toString());
	}
	
	
	@Test
	public void checkGetSafeTemp() {
		
		Double temperature = randTemp();
		
		Item item = new Item(	"ice cream", randDouble(), randDouble(), 
								randInt(), randInt(), temperature);
		
		assertEquals(temperature, item.GetSafeTemperature());
		
	}
	
	@Test
	public void checkSafeTempIfDry() {
		
		Item item = new Item(	"Beans", randDouble(), randDouble(),
								randInt(), randInt());
		
		assertNull(item.GetSafeTemperature());
		
	}
}
