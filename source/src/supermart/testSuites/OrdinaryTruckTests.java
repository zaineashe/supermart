// ---------------------------------
// TruckTests.java
// Zaine Ashe 09469010.
// junit test suite for the ordinary truck class.
// ---------------------------------


package supermart.testSuites;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import supermart.StockGenerator;
import supermart.SupermartEnums.ToStringType;
import supermart.backEnd.Truck;
import supermart.backEnd.Item;
import supermart.backEnd.OrdinaryTruck;
import supermart.backEnd.Stock;
import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;

/**
 * @author Zaine Ashe - 09469010
 */
public class OrdinaryTruckTests {
	
	//initalise random controller
	Random rand = new Random();
	
	//initialise the truck class to be used in tests
	Truck truck;
	
	//each type of truck can handle 500 items.
	int testStockCount = 500;
	
	// ----		truck methods 	----
	
	// constructor
	// SetStock
	// SetTemperature
	// getStock
	// getPrice
	// getCount
	// getTemperature
	// toString
	
	// -- truck generators which account for exceptions --
	
	//  -- REFACTORED --
	
	// These two private methods are used to generate trucks in this test suite
	// these methods generate trucks while also catching all the possible exceptions that they can throw
	// this is a substitute for putting 'throws xyz' in each method declaration
	
	// instead of having each test throw their own exceptions, i first
	// had each test run their own try/catch 
	// this converted some of the 'errors' into just 'fails', but it was super clunky
	// so i abstracted out the methods to make it a little neater
	
	private Truck generateDryTruck(Stock stock) {
		 
		//initialise the truck to be generated
		Truck generatedTruck;
		
		//this whole chunk of code would have been in every test method
		
		try {
			//try to construct a new dry truck, giving it the stock provided
			generatedTruck = new OrdinaryTruck(stock);
			
		} catch (DryException dry) {
			
			//if a dryException is thrown while trying to generate this truck
			//show the exceptions message
			fail(dry.getMessage());
			//return null for a failed truck construction
			//in most cases, this null return won't even be seen, because the fail
			//method would have already terminated the test.
			generatedTruck = null;
			
		} catch (DeliveryException cap) {
			
			//if a capacity exception is thrown, return its message
			fail(cap.getMessage());
			//return null for a failed construction
			generatedTruck = null;
			
		}
		
		//after the truck has been generated, return its value
		//if the truck throws an exception while constructing, then this returns null
		return generatedTruck;
		
	}	
	
	// ----		truck tests 	---- 

	@Before
	public void setup() {
		truck = null;
	}
	
	@Test
	// initialize regular truck, assert not null
	public void testDryInit() {
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount));
		
		truck = generateDryTruck(stock);

		assertNotNull(truck);
	}
	
	@Test
	// test getStock from regular truck and make sure the same as generated stock
	public void testDryGetStock(){
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount));
		
		truck = generateDryTruck(stock);
		
		assertEquals(stock, truck.getStock());
	}

	@Test
	// check if getPrice matches price equation for regular truck
	public void testDryGetPrice() {
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount));
		
		truck = generateDryTruck(stock);
		
		double expectedPrice = 750.0 + stock.size() * 0.25;
				
		assertEquals(expectedPrice, truck.getPrice(), 0);
	}
	
	@Test
	// check if getCount matches given count of items for regular truck
	public void testDryGetCount() {
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount));
		
		truck = generateDryTruck(stock);
		
		assertEquals(stock.size(), truck.getCount());
	}
	
	@Test
	// test if getTemperature on a dry truck will throw a dryException
	public void testDryGetTemperature() {
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount));
		
		truck = generateDryTruck(stock);
		
		try {
			double temperature = truck.getTemperature();
		} catch (DryException dry) {
			assertTrue(true);
		}
		
	}
	
	@Test
	// fill regular truck over capacity (should throw custom exception)
	public void testDryOverFlow() {
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount));
		
		truck = generateDryTruck(stock);
		
		try {
			
			//when stock is set to have more than 1000 items, it should break the capacity of a dry truck
			stock = StockGenerator.generateStock(1001);
			
			//this line of code should throw a DeliveryException
			truck.SetStock(stock);
			
		} catch (DeliveryException cap) {
			//if the exception is caught, the test has passed
			assertTrue(true);
		} catch (DryException dry) {
			//if a dry exception is caught, the test has failed (wrong exception type)
			fail(dry.getMessage());
		}
	}
	
	@Test
	//test if a dry truck still has room
	public void testDryNotFull() {
		
		Stock stock = StockGenerator.generateStock(100);
		
		truck = generateDryTruck(stock);
		
		assertTrue(!(truck.IsFull()));
	}
	
	@Test
	// put cold items in a regular truck
	public void testDryAddCold() {
		
		//generate a custom stock which is made exclusively of cold items
		//try to set that stock to a dry truck
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount));

		truck = generateDryTruck(stock);
		
		try {
			
			//create some new cold items on the fly
			Item item0 = new Item("ice cream", 6, 12, 260, 300, -5);
			Item item1 = new Item("icy pole", 6, 12, 260, 300, -6);
			Item item2 = new Item("frozen vegetables", 6, 12, 260, 300, -4);
			
			//reset stock as a completely blank slate
			stock = new Stock();
			
			//chuck in only cold items to the stock
			stock.addItem(item0);
			stock.AddQuantity(item0, 1);
			stock.addItem(item1);
			stock.AddQuantity(item1, 1);
			stock.addItem(item2);
			stock.AddQuantity(item2, 1);
			
			//assign the new stock to the truck
			//this should be the line that trips the DryException
			truck.SetStock(stock);
			
		} catch (DryException dry) {
			assertTrue(true);
		} catch (DeliveryException dry) {
			fail(dry.getMessage());
		}
		
	}
	
	@Test
	public void testDryIsFull() {
		
		Stock stock = StockGenerator.generateStock(1000);
		
		truck = generateDryTruck(stock);
		
		assertTrue(truck.IsFull());
	}
	
	@Test
	// check if toString matches for regular truck
	public void testDryToString() {
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount));
		
		truck = generateDryTruck(stock);
		
		// expecting string should be '>[TRUCK_TYPE], [ITEM_1_NAME], [ITEM_1_QUANTITY], etc.'
		// the '>' symbol is super important here, because it will tell the CSV reader when to
		// initialise a new truck.
		// two two truck types are represented as ">Refrigerated" and ">Ordinary" (capital first letter, the rest lower case)
		
		//use stock.tostring (with tostringtype NAME) to get the data for the stock
		String expectedString = ">Ordinary\n"+stock.toString(ToStringType.NAME);
		
		assertEquals(expectedString, truck.toString());
	}
	
	@Test
	public void TestSetStock() {
		
		Stock oldStock = StockGenerator.generateStock(testStockCount);
		
		Truck truck = generateDryTruck(oldStock);
		
		Stock newStock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount));
		
		
		try {
			truck.SetStock(newStock);
		} catch(DryException | DeliveryException e) {
			fail( e.getClass().getName() + " thrown: "+e.getMessage());
		}
		
		assertEquals(newStock, truck.getStock());
		
	}
	
}


