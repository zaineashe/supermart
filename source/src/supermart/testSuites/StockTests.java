package supermart.testSuites;

import static org.junit.Assert.assertEquals; 
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import supermart.SupermartEnums.ToStringType;
import supermart.backEnd.Item;
import supermart.backEnd.Stock;
import supermart.backEnd.Store;
import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;
import supermart.exceptions.StockException;

/**
 * @author Harrison Berryman - 09745092
 * @author Zaine Ashe - 09469010
 * 
 * NOTE: Zaine made minor changes to tests after Harrison wrote them due to refactoring Stock
 * See REFACTOR CHANGES in comments below for details
 * <p>
 * Tests 1 - 14, 18 - 25 by Harrison
 * Tests 15 - 17 by Zaine
 */
public class StockTests {
	
	
	//REFACTOR CHANGES: I needed to make some changes with Stock<Item> to be able to reference items
	//individually. So all instances of 'Stock<Item>' have been replaced with just 'Stock'
	
	//(the problem was that, by specifying 'Stock<Item>', I was accidentally making Stock a generic class
	// this was a mistake i made when we were first implementing the skeletons of each class)
	
	// Test items to add and remove to arrays
	Item testItem1 = new Item("testItem1", 20, 20, 2, 20);
	Item testItem2 = new Item("testItem2", 20, 20, 2, 20);
	Item testItem3 = new Item("testItem3", 20, 20, 2, 20);
	
	ArrayList<Item> testList2;
	Stock testStock = null;
	
	//Makes testStock null for Test 1
	@Before
	public void preTest() {
		testStock = null;
	}
	
	// Test 1: Initialise Stock and prove it's been initialised by showing it as not null.	
	@Test
	public void stockCreateTest() {
		Stock testStock = new Stock();
		assertNotNull(testStock);
	}
		
	//Test 2: Adding one item to stock
	@Test
	public void addTest() {
		HashMap<Item, Integer> testList2 = new HashMap<Item, Integer>();
		testList2.put(testItem1,1);
		
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		
		assertEquals(testStock.getMap(), testList2);
	}
	
	//Test 3: Adding multiple items to stock
	@Test
	public void addMultiplesTest() {
		
		String expectedString = "testItem2,3";
		Stock testStock = new Stock();
		//Add testItem2 and increase quantity to 3
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 3);
		assertEquals(expectedString, testStock.toString(ToStringType.NAME));
	}
	
	//Test 4: Adding multiple different items to stock
	@Test
	public void addDiffMultiplesTest() {
		String expectedString = "testItem1,1\ntestItem2,2\ntestItem3,1";
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 2);
		testStock.addItem(testItem3);
		testStock.AddQuantity(testItem3, 1);
		assertEquals(expectedString, testStock.toString(ToStringType.NAME));
	}
	
	
			
	//Test 5: Adding less than 0 items - should produce a stock exception
	@Test(expected = StockException.class) 
	public void addNegativeItemsTest() throws StockException {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		
		//SetQuantity should cause a Stock Exception to be thrown
		testStock.SetQuantity(testItem1, -1);
	}
		
	//Test 6: Convert testList to string
	@Test
	public void toStringTest() {
		Stock testStock = new Stock();
		
		//Expected string should be "testItem1,testItem2,testItem3"
		
		// ----------------------------------------------------------------------
		//refactored: quantities are necessary as well
		//ergo: "testItem1,1,testItem2,1,testItem3,1"
		// ----------------------------------------------------------------------
		String testString = testItem1.GetName()+",1\n"+testItem2.GetName()+",1\n"+testItem3.GetName()+",1";
		
		//because stock is using a hashmap instead of an arraylist
		//the toString wont come out in the right order
		//to mitigate this: stock's toString method will return items in alphabetical order
		//so to test stock, these items need to be in alphabetical order
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 1);
		testStock.addItem(testItem3);
		testStock.AddQuantity(testItem3, 1);
		
		// ----------------------------------------------------------------------
		//refactored: toString takes argument ToStringType (so that we 
		//can use one toString function to give multiple types of toString)
		//[Some parts of the assessment require full item descriptions, where
		//some parts of the assessment only need names and quantities (i.e. truck stocks)]
		// ----------------------------------------------------------------------
		assertEquals(testStock.toString(ToStringType.NAME), testString);
	}
	
	//Test 7: Test Stock.keyset() accurately returns the Key set
	@Test
	public void keySetTest() {
		//Create test stock
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 1);
		
		//Create testSet to compare against testStock.KeySet
		//This testSet is the expected output
		Set<Item> testSet = new HashSet<Item>();
		testSet.add(testItem1);
		testSet.add(testItem2);
		
		assertEquals(testStock.keySet(), testSet);
	}
	
	//Test 8: Test that getLowestTemp returns the item with the lowest temp with both cold and dry items in stock
	@Test
	public void getLowestTempTest() {
		//Create cold items for testing
		Stock testStock = new Stock();
		Item coldItem1 = new Item("testItem1", 20, 20, 2, 20, 5);
		Item coldItem2 = new Item("testItem2", 20, 20, 2, 20, 1);
		Item coldItem3 = new Item("testItem3", 20, 20, 2, 20, -5);

		double expectedTemp = -5;
		
		//Add cold items and one dry item to test stock
		testStock.addItem(coldItem1);
		testStock.AddQuantity(coldItem1, 1);
		testStock.addItem(coldItem2);
		testStock.AddQuantity(coldItem2, 1);
		testStock.addItem(coldItem3);
		testStock.AddQuantity(coldItem3, 1);
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		
		//Expected temp is -5
		assertEquals(testStock.getLowestTemp(), expectedTemp, 0);
		
	}
	
	//Test 9: Test that addItem returns false as it's adding an item that already exists
	@Test
	public void addItemAlreadyExistsTest() {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		assertTrue(!(testStock.addItem(testItem1)));
	}
	
	//Test 10: Test that removeItem removes the item entirely
	@Test
	public void removeItemTest() {
		//Create test stock with 3 unique items
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 1);
		testStock.addItem(testItem3);
		testStock.AddQuantity(testItem3, 1);
		
		//Remove 1 unique item
		testStock.removeItem(testItem1);
		
		int expectedNumber = 2;
		
		//Should only be 2 unique items left
		assertEquals(expectedNumber, testStock.CountUniqueItems());
	}
		
	//Test 11: Test removeItem returns false if trying to remove nonexistent item
	@Test
	public void removeMissingItemTest() {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		
		//testItem3 was never added to testStock, therefore nonexistent
		assertTrue(!(testStock.removeItem(testItem3)));
		
	}
	
	//Test 12: Test SetQuantity throws StockException when setting quantity of nonexistent item
	@Test(expected = StockException.class)
	public void setQuantityExceptionTest() throws StockException {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		
		//testItem2 was never added to testStock, therefore nonexistent
		testStock.SetQuantity(testItem2, 2);

	}
	
	//Test 13: Test that size returns sum of all item quantities
	@Test
	public void sizeTest() {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 5);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 5);
		testStock.addItem(testItem3);
		testStock.AddQuantity(testItem3, 5);
		
		//3 items of quantity 5 = 15 total items
		int expectedNumber = 15;
		
		assertEquals(expectedNumber, testStock.size());
	}
	
	//Test 14: Test that CountUniqueItems accurately counts the number of unique items
	@Test
	public void countUniqueItemsTest() {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.addItem(testItem2);
		testStock.addItem(testItem3);
		
		int expectedNumber = 3;
		
		assertEquals(expectedNumber, testStock.CountUniqueItems());
	}
	

	// REFACTOR CHANGES
	// these two are testing my shortcut method AddQuantity (setQuantity throws a custom StockException now,
	// so i've put together a little shortcut that makes exceptions return false. This just keeps the test suites neat)
	
	//Test 15: Test that adding a negative quantity fails
	@Test
	public void testAddQuantityFailsNegative() {
		
		Stock stock = new Stock();
		
		stock.addItem(testItem1);
		
		assertTrue(!(stock.AddQuantity(testItem1,-1)));
	}
	
	//Test 16: Test adding a quantity to a non-existent item fails
	@Test
	public void testAddQuantityFailsWrongItem() {
		
		Stock stock = new Stock();
		
		stock.addItem(testItem1);
		
		assertTrue(!(stock.AddQuantity(testItem2, 1)));
	}
	
	@Test
	//Test 17: Test getEntrySet() returns a correct entry set
	public void testGetEntrySet() {
		
		HashMap<Item, Integer> testMap = new HashMap<Item, Integer>();
		
		testMap.put(testItem1, 3);
		
		Stock testStock = new Stock();
		
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 3);
		
		assertEquals(testMap.entrySet(), testStock.entrySet());
	}
	
	//Test 18: Check stock.contains returns true
	@Test
	public void stockContainsTest() {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 1);
		
		//testStock.contains should detect testItem1 in the stock
		assertEquals(true, testStock.Contains(testItem1));
	}
	
	//Test 19: Test stock.getItem returns the correct item based on itemName
	@Test
	public void stockGetItemTest() {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 1);
		
		assertEquals(testItem2, testStock.getItem("testItem2"));
	}
	
	
	//Test 20: Test stock.isEmpty returns true when used on an empty stock
	@Test
	public void stockIsEmptyTrueTest() {
		Stock testStock = new Stock();
		//Stock = null;
		assertEquals(true, testStock.isEmpty());
	}
	
	//Test 21: Test stock.isEmpty returns false when used on a stock with at least one item
	@Test
	public void stockIsEmptyFalseTest() {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		assertEquals(false, testStock.isEmpty());
	}
	
	//Test 22: Test toString.DETAILS returns correct string
	//EXPECTED STRING EXAMPLE "item1,20.0,20.0,100,150,item2,4.0,5.0,150,175,4.0,"
	@Test
	public void toStringDetailsTest() {
		String expectedString = "testItem1,20.0,20.0,2,20,testItem2,20.0,20.0,2,20";
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 1);
		assertEquals(expectedString, testStock.toString(ToStringType.DETAILS));
	}
	
	
	//Test 23: Test building a new stock using an arrayList function correctly
	@Test
	public void testArrayListStockBuild() throws DeliveryException {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 2);
		
		Store testStore = new Store("testStore", 100000, testStock);
		ArrayList<String> testList = new ArrayList<String>();

		testList.add(">Ordinary");
		testList.add("testItem1");
		testList.add("1");
		testList.add("testItem2");
		testList.add("2");
		
		Stock testStock2 = new Stock(testStore, testList);
		assertEquals(testStock, testStock2);
		
	}

	
	//Test 24: Test an exception is thrown when using an arrayList with an unrecognised Item
	@Test(expected = DeliveryException.class)
	public void testArrayListStockBuildException() throws DeliveryException {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 2);
		
		Store testStore = new Store("testStore", 100000, testStock);
		ArrayList<String> testList = new ArrayList<String>();

		testList.add(">Ordinary");
		testList.add("testItem1");
		testList.add("1");
		testList.add("testItem2");
		testList.add("2");
		testList.add("testItem4");
		testList.add("4");
		//testItem4 does not exist and was never added to testStock
		Stock testStock2 = new Stock(testStore, testList);

	}
	
	//Test 25: Test that Stock.Contains correctly identifies that a stock contains the chosen item
	@Test
	public void testContains() {
		Stock testStock = new Stock();
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		assertEquals(true, testStock.Contains(testItem1));
	}
}
