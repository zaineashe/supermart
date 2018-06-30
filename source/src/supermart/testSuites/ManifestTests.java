package supermart.testSuites;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import supermart.StockGenerator;
import supermart.backEnd.Manifest;
import supermart.backEnd.OrdinaryTruck;
import supermart.backEnd.RefrigeratedTruck;
import supermart.backEnd.Stock;
import supermart.backEnd.Truck;
import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;

/**
 * @author Zaine Ashe - 09469010
 */
public class ManifestTests {

	
	Random rand = new Random();
	
	Manifest manifest;
	
	int testStockCount = 500;
	
	//array of items
	
	//method to generate a new trucks with a size argument
	//ordinary, dry truck
	private Truck generateTruck(int stockSize) {
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(stockSize));
		
		try {		
			
			return new OrdinaryTruck(stock);
			
		} catch (DryException dry) {
			
			fail(dry.getMessage());
			return null;
			
		} catch (DeliveryException cap) {
			
			fail(cap.getMessage());
			return null;
			
		}
	}
	
	//refrigerated truck
	private Truck generateTruck(int stockSize, double temperature) {
		
		Stock stock = StockGenerator.generateStock(StockGenerator.stockSize(stockSize), temperature);
		
		try {		
			
			return new RefrigeratedTruck(stock);
			
		} catch (DeliveryException cap) {
			
			fail(cap.getMessage());
			return null;
			
		}
	}
	
	@Before
	public void setup() {
		manifest = null;
	}
	
	@Test
	// test initialise manifest (not null)
	public void testInit() {
		
		manifest = new Manifest();
		
		assertNotNull(manifest);
	}
	
	@Test
	// test add truck
	public void testAdd() {
		
		ArrayList<Truck> expectedList = new ArrayList<Truck>();
		
		manifest = new Manifest();
		
		Truck toAdd = generateTruck(StockGenerator.stockSize(testStockCount));
		
		expectedList.add(toAdd);
		
		manifest.add(toAdd);
		
		assertEquals(expectedList, manifest.toList());
	}
	
	@Test
	//tests if adding an null value to the manifest will make it return null
	public void testAddFails() {
		
		manifest = new Manifest();
		
		assertTrue(!manifest.add(null));
		
	}
	
	@Test
	//test if the Contains() method returns true when it should
	public void testContains() {
		
		manifest = new Manifest();
		
		Truck toAdd = generateTruck(StockGenerator.stockSize(testStockCount));
		
		manifest.add(toAdd);
		
		assertTrue(manifest.contains(toAdd));
		
	}
	
	@Test
	//test if Contains() returns false when it should
	public void testDoesNotContain() {
		
		manifest = new Manifest();
		
		Truck toAdd = generateTruck(StockGenerator.stockSize(testStockCount));
		Truck notAdded = generateTruck(StockGenerator.stockSize(testStockCount));
		
		manifest.add(toAdd);
		
		assertTrue(!(manifest.contains(notAdded)));
		
	}
	
	@Test
	//test getSize works for a random number of trucks from 1 to 10
	public void testGetSize() {
		
		manifest = new Manifest();
		
		int expectedSize = 1 + rand.nextInt(9);
		
		for (int i=0; i<expectedSize; i++) {
			
			Truck toAdd = generateTruck(StockGenerator.stockSize(testStockCount));
			
			manifest.add(toAdd);
		}
		
		assertEquals(expectedSize, manifest.size());
	}
	
	@Test
	//test get truck
	public void testGetTruck() {
		
		manifest = new Manifest();
	
		Truck toAdd0 = generateTruck(StockGenerator.stockSize(testStockCount));
		
		//index 0
		manifest.add(toAdd0);
		
		//this is calling a nullpointer exception
		assertTrue(toAdd0 == manifest.get(0));

	}
	
	@Test
	//test get multiple trucks
	public void testGetTruckMultiples() {
		
		
		manifest = new Manifest();
	
		Truck toAdd0 = generateTruck(StockGenerator.stockSize(testStockCount));
		Truck toAdd1 = generateTruck(StockGenerator.stockSize(testStockCount), StockGenerator.generateTemperature());
		Truck toAdd2 = generateTruck(StockGenerator.stockSize(testStockCount));
		
		//index 0
		manifest.add(toAdd0);
		//index 1
		manifest.add(toAdd1);
		//index 2
		manifest.add(toAdd2);
		
		//check if each truck is the same as the ones in the manifest
		//this boolean basically is running three different AssertEquals
		//tests at once, and combining them into one true/false statement
		//using the && operator.
		
		//because of the nature of &&, if any of these tests fail,
		//then testsPassed will be false.
 		boolean testsPassed = 	(toAdd0 == manifest.get(0)) && 
								(toAdd1 == manifest.get(1)) && 
								(toAdd2 == manifest.get(2));
 		
		assertTrue(testsPassed);

	}
	
	@Test
	//testToString
	public void testToString() {
		
		manifest = new Manifest();
		
		//refrigerated
		Truck toAdd0 = generateTruck(StockGenerator.stockSize(testStockCount), StockGenerator.generateTemperature());
		//refrigerated
		Truck toAdd1 = generateTruck(StockGenerator.stockSize(testStockCount), StockGenerator.generateTemperature());
		//ordinary
		Truck toAdd2 = generateTruck(StockGenerator.stockSize(testStockCount));

		
		//therefore the expected toString should be ">Refrigerated, ...[STOCK_INFO]... ,>Refrigerated, ...[STOCK_INFO]... ,>Ordinary, ...[STOCK_INFO]... "
		
		String expectedString = ""+toAdd0.toString()+"\n"+toAdd1.toString()+"\n"+toAdd2.toString();
		System.out.println(expectedString);
		//add the trucks to the manifest.
		manifest.add(toAdd0);
		manifest.add(toAdd1);
		manifest.add(toAdd2);
		
		assertEquals(expectedString,manifest.toString());
		
		
	}
}
