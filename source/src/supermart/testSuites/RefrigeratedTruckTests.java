//---------------------------------
//TruckTests.java
//Zaine Ashe 09469010.
//junit test suite for the refrigerated truck class.
//---------------------------------
package supermart.testSuites;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import supermart.StockGenerator;
import supermart.SupermartEnums.ToStringType;
import supermart.backEnd.RefrigeratedTruck;
import supermart.backEnd.Stock;
import supermart.backEnd.Truck;
import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;

/**
 * @author Zaine Ashe - 09469010
 */
public class RefrigeratedTruckTests {
	
	//-/-/-/-/-/-/-/-/-/ FIELDS /-/-/-/-/-/-/-/-/-/-/-/-/-/
	
	//initalise random controller
	Random rand = new Random();
		
	//initialise the truck class to be used in tests
	Truck truck;
		
	//each type of truck can handle 500 items.
	int testStockCount = 500;
	
	//-/-/-/-/-/-/-/-/-/ INSTANCE GENERATORS /-/-/-/-/-/-/-/-/-/-/-/-/-/

	private Truck generateColdTruck(Stock stock) {
		
		//see generateDryTruck for a breakdown of how this code works
		//this is probably vestigial, an alternative: 'generateTruck(Stock stock, boolean isDry)' 
		
		Truck generatedTruck;
		
		try {
			
			generatedTruck = new RefrigeratedTruck(stock);
		} catch (DeliveryException cap) {
			
			fail(cap.getMessage());
			generatedTruck = null;
		}
		
		return generatedTruck;
		
	}
	
	//-/-/-/-/-/-/-/-/-/ TEST SUITE /-/-/-/-/-/-/-/-/-/-/-/-/-/
	
	@Test
	// initialize refrigerated, assert not null
	public void testColdInit() {
		
		double expectedTemperature = StockGenerator.generateTemperature();
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount), expectedTemperature);
		
		truck = generateColdTruck(stock);
		
		assertNotNull(truck);
	}
	
	@Test
	// test getStock from refrigerated truck and make sure stock is the same
	public void testColdGetStock(){
		
		double expectedTemperature = StockGenerator.generateTemperature();
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount), expectedTemperature);
		
		truck = generateColdTruck(stock);
		
		assertEquals(stock, truck.getStock());
	}
	
	@Test
	// check if getPrice matches price equation for refrigerated truck
	public void testColdGetPrice() {
				
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount), StockGenerator.generateTemperature());
		
		double expectedTemperature = stock.getLowestTemp();
		
		truck = generateColdTruck(stock);
		
		double expectedPrice = 900.0 + 200.0 * Math.pow(0.7, expectedTemperature/5);
				
		assertEquals(expectedPrice, truck.getPrice(), 0);
	}
	
	@Test
	// check if getCount matches given count of items for refrigerated truck
	public void testColdGetCount() {
		
		double expectedTemperature = StockGenerator.generateTemperature();
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount), expectedTemperature);
		
		truck = generateColdTruck(stock);
		
		assertEquals(stock.size(), truck.getCount());
	}
	
	@Test
	// test getTemperature matches generated temperature for refrigerated truck
	public void testColdGetTemperature() {
				
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount), StockGenerator.generateTemperature());

		double expectedTemperature = stock.getLowestTemp();
		
		truck = generateColdTruck(stock);
			
		try {
			assertEquals(expectedTemperature, truck.getTemperature(), 0);
		} catch (DryException dry) {
			fail(dry.getMessage());
		}
		
		
	}
	
	@Test
	// fill refrigerated truck over capacity (custom exception)
	public void testColdOverFlow() {
		
		double expectedTemperature = StockGenerator.generateTemperature();
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount), expectedTemperature);
		
		truck = generateColdTruck(stock);
		
		try {
			
			//when stock is set to have more than 800 items, it should break the capacity of a dry truck
			stock = StockGenerator.generateStock(801);
			
			//this line of code should throw a DeliveryException
			truck.SetStock(stock);
			
		} catch (DeliveryException cap) {
			//if the exception is caught, the test has passed
			assertTrue(true);
		} catch (DryException dry) {
			fail(dry.getMessage());
		}
	}
	
	@Test
	//test if a refrigerated truck still has room
	public void testColdNotFull() {
		
		Stock stock = StockGenerator.generateStock(100, StockGenerator.generateTemperature());
		
		truck = generateColdTruck(stock);
		
		assertTrue(!(truck.IsFull()));
	}
	
	@Test
	public void testColdIsFull(){
		
		double expectedTemperature = StockGenerator.generateTemperature();
		
		Stock stock = StockGenerator.generateStock(800, expectedTemperature);
		
		truck = generateColdTruck(stock);
		
		assertTrue(truck.IsFull());
	}
	
	@Test
	// check if toString matches for refrigerated
	public void testColdToString() {
		
		//perform the same test but with a refrigerated truck
		
		double expectedTemperature = StockGenerator.generateTemperature();
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount), expectedTemperature);
		
		truck = generateColdTruck(stock);
		
		//here the trucks opener is changed from ">Ordinary" to ">Refrigerated"
		String expectedString = ">Refrigerated\n"+stock.toString(ToStringType.NAME);
		
		assertEquals(expectedString, truck.toString());
	}
	
	@Test
	public void testColdSetStock() {
		
		double temperature = -10;
		
		Stock oldStock = StockGenerator.generateStock(testStockCount, temperature);
		
		Truck truck = generateColdTruck(oldStock);
		
		Stock newStock = StockGenerator.generateStock(StockGenerator.stockSize(testStockCount), temperature);
		
		
		try {
			truck.SetStock(newStock);
		} catch(DryException | DeliveryException e) {
			fail( e.getClass().getName() + " thrown: "+e.getMessage());
		}
		
		assertEquals(newStock, truck.getStock());
		
	}

}
