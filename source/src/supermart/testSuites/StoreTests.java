package supermart.testSuites;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;
import org.junit.Test;

import supermart.SupermartEnums.ToStringType;
import supermart.backEnd.Item;
import supermart.backEnd.Manifest;
import supermart.backEnd.OrdinaryTruck;
import supermart.backEnd.RefrigeratedTruck;
import supermart.backEnd.Stock;
import supermart.backEnd.Store;
import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;
import supermart.exceptions.StockException;

import static org.junit.Assert.assertEquals;

/**
 * @author Harrison Berryman - 09745092
 * @author Zaine Ashe - 09469010
 * 
 * NOTE: Zaine made changes to tests after Harrison wrote them due to refactoring Store
 * See REFACTOR CHANGES in comments below for details
 * <p>
 * Tests 1 - 13 by Harrison
 * Tests 14, 15, 16 by Zaine
 */
public class StoreTests {

	static Random rand = new Random();
		
	// Filler names for SuperMart locations.
	static String[] namelist = new String[] {
		"SuperMart North", "SuperMart East", "SuperMart West"
	};

	
	//REFACTOR CHANGES (fixed nullpointerexception errors)
	//------------------------------
	//added a quick little 'stock generator' to StoreTests.java
	//this includes an itemsList array to pick random items from, and a CreateTestStock()
	//function to generate a new stock with
	
	//also, making Store.java a singleton messed the tests up a bit (originally each test had a 'new Store()' constructor,
	// which broke once store became a singleton)
	//------------------------------
	
	//list of example items used to generate a new stock
	static Item[] itemsList = new Item[] {
			new Item("ice cream", 6, 12, 260, 300, -5),
			new Item("Beans", 4, 5, 120, 200),
			new Item("Rice", 1, 3, 300, 320),
			new Item("Potatoes", 1, 3, 300, 320),
			new Item("Broccoli", 1, 3, 300, 320),
			new Item("Tomatoes", 1, 3, 300, 320),
			new Item("Beef", 10, 12, 300, 320, 5),
			new Item("Chicken", 11, 13, 300, 320,4)
	};
	
	Item testItem1 = new Item("testItem1", 20, 20, 2, 20);
	Item testItem2 = new Item("testItem2", 20, 20, 2, 20);
	Item testItem3 = new Item("testItem3", 20, 20, 2, 20);
	
	//abstract method used to randomly generate a stock
	private Stock createTestStock(Stock stock) {
		
		int size = 1 + rand.nextInt(4);
		
		stock = new Stock();
		
		for (int i = 0; i < size; i++) {
			Item item = itemsList[rand.nextInt(itemsList.length)];
			stock.addItem(item);
			stock.AddQuantity(item,1);
		}
		
		return stock;
		
	}

	//Create variables for use in tests
	double capital = rand.nextInt();
	
	//initialise stock and set it to an empty Stock
	Stock stock = new Stock();
	
	//use the static store singleton method to intialise the store
	Store store = Store.getInstance("Test Store", capital, stock);
	
	Stock testStock = new Stock();
	
	// Test 1: Initialise Store object and prove it's been initialised by showing it as not null.
	@Test
	public void storeCreateTest() {
		
		String name = namelist[rand.nextInt(namelist.length)];
		
		stock = createTestStock(stock);
		
		store.SetName(name);
	
		assertNotNull(store);
	}
	
	// Test 2: Test that Store's name is correct using getName
	@Test
	public void storeNameTest() {
		
		String name = namelist[rand.nextInt(namelist.length)];
		
		stock = createTestStock(stock);
		
		store.SetName(name);
		
		assertEquals(name, store.getName());
	}
	
	// Test 3: Test that Store's capital is correct using getCapital
	@Test
	public void storeCapitalTest() {
		
		stock = createTestStock(stock);
		
		store.SetCapital(capital);
		
		assertEquals((Double) capital, (Double) store.getCapital());
	}
	
	// Test 4: Test that Store's stock is correct using getCapital
	@Test
	public void storeStockTest() {
		
		stock = createTestStock(stock);
		
		store.SetStock(stock);
		
		assertEquals(stock, store.getStock());
	}
	

	//Test 5: Test getItem returns correct item using itemName
	@Test
	public void getItemTest() {
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 1);
		
		store.SetStock(testStock);

		assertEquals(store.getItem("testItem2"), testItem2 );

	}
	
	//Test 5: Test setCapital correctly sets store capital
	@Test
	public void setCapitalTest() {
		store.SetCapital(9999);
		assertEquals(store.getCapital(), 9999, 0);

	}
	
	
	//Test 6: Test that changeQuantity correctly adds to quantity
	@Test
	public void changeQuantityTest() throws StockException {
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 1);
		
		store.SetStock(testStock);
		
		store.ChangeQuantity(testItem1, 9);
		//testItem1 quantity of 9 + testItem2 quantity of 1 = 10
		assertEquals(testStock.getQuantity(testItem1), 10);
	}
	
	//Test 7: Test that changeQuantity throws a StockException when checking an item not in the stores stock
	@Test(expected = StockException.class) 
	public void changeQuantityExceptionTest() throws StockException {
		testStock.addItem(testItem1);
		testStock.AddQuantity(testItem1, 1);
		testStock.addItem(testItem2);
		testStock.AddQuantity(testItem2, 1);
		
		store.SetStock(testStock);
		//testItem3 doesn't exist in testStock so an exception should be thrown
		store.ChangeQuantity(testItem3, 1);

	}

	
	//Test 8: Test addCapital increases capital by selected amount
	@Test
	public void addCapitalTest() throws DeliveryException {
		store.SetCapital(0);
		double expectedCapital = 24601;
		
		//A very specific capital of 24601 to ensure it's not a fluke that the capitals are equal
		store.AddCapital(24601);
		assertEquals(store.getCapital(), expectedCapital, 0);
	}
	
	//Test 9: Test addCapital decreases capital by selected amount
	//Entering a negative amount into addCapital decreases the capital by that amount.
	@Test
	public void addCapitalRemoveTest() throws DeliveryException {
		store.SetCapital(1000);
		double expectedCapital = 1;
		
		store.AddCapital(-999);
		//After removing 999 from 1000, only 1 should be left in capital
		assertEquals(store.getCapital(), expectedCapital, 0);
	}
	
	//Test 10: Test making capital negative using addCapital throws Delivery Exception
	@Test(expected = DeliveryException.class) 
	public void addCapitalExceptionTest() throws DeliveryException {
		store.SetCapital(0);
		//Deducting any number from 0 should result in a thrown exception 
		store.AddCapital(-999);
	}
	
	
	//Test 11: Test importItems correctly initialises a stock of new items (with quantity 0)
	@Test
	public void importItemsTest() {
			
		testStock.addItem(testItem1);
		testStock.addItem(testItem2);
		testStock.addItem(testItem3);
		store.SetStock(testStock);
		store.ImportItems(testStock);
		assertEquals(store.getStock(), testStock);
		
	}
	
	
	//Test 12: Test importSales correctly takes a stock of items and sells them
	//This should increase capital and decrease quantity
	//Reference issue requires extraTestStock due to design of Stock.java
	@Test
	public void importSalesTest() throws StockException {
		testStock.addItem(testItem1);
		testStock.SetQuantity(testItem1, 10);
		testStock.addItem(testItem2);
		testStock.SetQuantity(testItem2, 10);

		Stock extraTestStock = new Stock();
		
		extraTestStock.addItem(testItem1);
		extraTestStock.SetQuantity(testItem1, 10);
		extraTestStock.addItem(testItem2);
		extraTestStock.SetQuantity(testItem2, 10);
		
		store.SetStock(extraTestStock);
		store.SetCapital(1);
		store.ImportSales(testStock);
		assertEquals(extraTestStock.getQuantity(testItem1), 0);
		assertEquals(extraTestStock.getQuantity(testItem2), 0);
		assertEquals(store.getCapital(), 401, 0);
	}
	
	
	//Test 13: Test importSales throws stock exception when given stock references a non-existent item
	@Test(expected = StockException.class)
	public void importSalesExceptionTest() throws StockException {
		testStock.addItem(testItem1);
		testStock.SetQuantity(testItem1, 10);
		testStock.addItem(testItem2);
		testStock.SetQuantity(testItem2, 10);
		testStock.addItem(testItem3);

		Stock extraTestStock = new Stock();
		
		extraTestStock.addItem(testItem1);
		extraTestStock.SetQuantity(testItem1, 10);
		extraTestStock.addItem(testItem2);
		extraTestStock.SetQuantity(testItem2, 10);
		
		//testItem3 was not added to extraTestStock, throwing the exception
		store.SetStock(extraTestStock);
		store.ImportSales(testStock);
	}
	
	
	
	//Test 14: Test generateNewOrder correctly creates a new order
	@Test
	public void testGenerateNewOrder() {
		
		//initialise some new dry items to test the reorder list orders by reorder amount (after ordering by temperature)
		Item item1 = new Item("pasta", 2.0, 3.0, 100, 199);
		Item item2 = new Item("biscuits", 2.0, 3.0, 100, 198);
		Item item3 = new Item("peanut butter", 2.0, 3.0, 100, 300);
		
		//so the ideal order of items is (ordered by coldest to driest, then from largest reorder amount to smallest):
		//ice cream, chicken, beef, potatoes, peanut butter, beans, pasta, biscuits
		//(some dry items have been omitted for having the same reorder amount)
		
		//therefore the list of item references should be
		// itemsList[0], itemsList[7], itemsList[6] , itemsList[3], item3, itemsList[1], item1, item2
		
		//set the stores stock to contain all of these items (at quantities below their reorder point)
		//having a quantity of 0 will work perfectly (and reduce each of our item adds to only one line)
		
		Stock stock = new Stock();
		
		stock.addItem(item1);
		stock.addItem(item2);
		stock.addItem(item3);
		stock.addItem(itemsList[0]);
		stock.addItem(itemsList[1]);
		stock.addItem(itemsList[3]);
		stock.addItem(itemsList[6]);
		stock.addItem(itemsList[7]);
		
		//add a couple of extra items, but put them above the reorder point so they wont be addressed in the
		//generateNewOrder method:
		stock.addItem(itemsList[2]);
		stock.AddQuantity(itemsList[2], 600);
		stock.addItem(itemsList[5]);
		stock.AddQuantity(itemsList[5], 600);
		
		store.SetStock(stock);
		
		//now that the stores stock is properly set, we need to check that the generateNewOrder will properly order all of the items
		
		//initialise to array list to use as comparison
		ArrayList<Item> toCompare = new ArrayList<Item>();
		
		//add each of the items to the arraylist in the specified order
		// itemsList[0], itemsList[7], itemsList[6] , itemsList[3], item3, itemsList[1], item1, item2
		
		toCompare.add(itemsList[0]);
		toCompare.add(itemsList[7]);
		toCompare.add(itemsList[6]);
		toCompare.add(itemsList[3]);
		toCompare.add(item3);
		toCompare.add(itemsList[1]);
		toCompare.add(item1);
		toCompare.add(item2);
		
		//compare the two arraylists
		assertEquals(toCompare, store.generateNewOrder());	
	}
	
	//Test 15: Test import manifest correctly imports a manifest
	@Test
	public void testImportManifest() {
		
		//set the store's inventory to work with these items:
		//chicken, beef, broccoli, potatoes, rice
		//itemsList[7], itemsList[6], itemsList[2], itemsList[3], itemsList[4]
		
		Stock inventory = new Stock();
		inventory.addItem(itemsList[7]);
		inventory.addItem(itemsList[6]);
		inventory.addItem(itemsList[2]);
		inventory.addItem(itemsList[3]);
		inventory.addItem(itemsList[4]);
		
		//set the stores capital back to 10,000
		store.SetCapital(100000);
		store.SetStock(inventory);
		
		//test to see if a manifest import changes the quantities and capital of the store accordingly
		
		//create a manifest to import
		
		//manifest design:
		//>Refrigerated
		//Chicken, 120
		//Beef, 200
		//>Ordinary
		//broccoli, 175
		//potatoes, 100
		//rice, 300
		
		Manifest manifest = new Manifest();
		
		Stock stock1 = new Stock();
		
		stock1.addItem(itemsList[7]);
		stock1.AddQuantity(itemsList[7], 120);
		
		stock1.addItem(itemsList[6]);
		stock1.AddQuantity(itemsList[6], 200);
		
		Stock stock2 = new Stock();
		
		stock2.addItem(itemsList[4]);
		stock2.AddQuantity(itemsList[4], 175);
		
		stock2.addItem(itemsList[3]);
		stock2.AddQuantity(itemsList[3], 100);
		
		stock2.addItem(itemsList[2]);
		stock2.AddQuantity(itemsList[2], 300);
		
		//calculate what the final capital will be equal to after import
		Double expectedCapital = store.getCapital();
		
		//take the buy cost of each item in the manifest, multiplied by its quantity
		expectedCapital -=	 	 (itemsList[7].GetCost() * 120)
								+(itemsList[6].GetCost() * 200)
								+(itemsList[4].GetCost() * 175)
								+(itemsList[3].GetCost() * 100)
								+(itemsList[2].GetCost() * 300);
		
		try {
			manifest.add(new RefrigeratedTruck(stock1));
			manifest.add(new OrdinaryTruck(stock2));
		} catch (DryException | DeliveryException e) {
			fail("exception thrown in truck construction: " + e.getMessage());
		}
		
		//take the cost of each of the truck's hire from the expected capital
		expectedCapital -= manifest.get(0).getPrice();
		expectedCapital -= manifest.get(1).getPrice();
		
		//perform the import manifest, and then assert that the changes made are true.
		//each item in the stores inventore should now have an increased stock
		//(each item increased by the amount specified in the manifest)
		
		try {
			store.ImportManifest(manifest);
		} catch(DeliveryException | StockException e) {
			fail("exception thrown importing manifest: "+ e.getMessage());
		}
		
		//if the import manifest has successfully been called, now assert that all the quantities have been
		//changed, and that the capital has changed.
		
		Double storeCapital = store.getCapital();
		
		assertEquals(expectedCapital, storeCapital);
		
		//assert each of the items are at the right quantities
		assertEquals(120, store.getStock().getQuantity(itemsList[7]));
		assertEquals(200, store.getStock().getQuantity(itemsList[6]));
		assertEquals(175, store.getStock().getQuantity(itemsList[4]));
		assertEquals(100, store.getStock().getQuantity(itemsList[3]));
		assertEquals(300, store.getStock().getQuantity(itemsList[2]));
		
	}
	
		//Test 16: Test that Import Manifest correctly throws Exception Error
		@Test(expected = DeliveryException.class)
		public void testImportManifestFails() throws DeliveryException {
			
			//set the store's inventory to work with these items:
			//chicken, beef, broccoli, potatoes, rice
			//itemsList[7], itemsList[6], itemsList[2], itemsList[3], itemsList[4]
			
			Stock inventory = new Stock();
			inventory.addItem(itemsList[7]);
			inventory.addItem(itemsList[6]);
			inventory.addItem(itemsList[2]);
			inventory.addItem(itemsList[3]);
			inventory.addItem(itemsList[4]);
			
			//set the stores capital back to 10,000
			store.SetCapital(100000);
			store.SetStock(inventory);
			
			//test to see if a manifest import changes the quantities and capital of the store accordingly
			
			//create a manifest to import
			
			//manifest design:
			//>Refrigerated
			//Chicken, 120
			//Beef, 200
			//>Ordinary
			//broccoli, 175
			//potatoes, 100
			//rice, 300
			
			Manifest manifest = new Manifest();
			
			Stock stock1 = new Stock();
			
			stock1.addItem(itemsList[7]);
			stock1.AddQuantity(itemsList[7], 120);
			
			stock1.addItem(itemsList[6]);
			stock1.AddQuantity(itemsList[6], 200);
			
			stock1.addItem(itemsList[0]);
			stock1.AddQuantity(itemsList[0],20);
			
			Stock stock2 = new Stock();
			
			stock2.addItem(itemsList[4]);
			stock2.AddQuantity(itemsList[4], 175);
			
			stock2.addItem(itemsList[3]);
			stock2.AddQuantity(itemsList[3], 100);
			
			stock2.addItem(itemsList[2]);
			stock2.AddQuantity(itemsList[2], 300);
			
			
			
			//calculate what the final capital will be equal to after import
			Double expectedCapital = store.getCapital();
			
			//take the buy cost of each item in the manifest, multiplied by its quantity
			expectedCapital -=	 	 (itemsList[7].GetCost() * 120)
									+(itemsList[6].GetCost() * 200)
									+(itemsList[4].GetCost() * 175)
									+(itemsList[3].GetCost() * 100)
									+(itemsList[2].GetCost() * 300);
			
			try {
				manifest.add(new RefrigeratedTruck(stock1));
				manifest.add(new OrdinaryTruck(stock2));
			} catch (DryException | DeliveryException e) {
				fail("exception thrown in truck construction: " + e.getMessage());
			}
			
			//take the cost of each of the truck's hire from the expected capital
			expectedCapital -= manifest.get(0).getPrice();
			expectedCapital -= manifest.get(1).getPrice();
			
			//perform the import manifest, and then assert that the changes made are true.
			//each item in the stores inventore should now have an increased stock
			//(each item increased by the amount specified in the manifest)
			
			try {
				store.ImportManifest(manifest);
			} catch(StockException e) {
				fail("exception thrown importing manifest: "+ e.getMessage());
			} 
		}
		
		//Test 16: Test that Import Manifest correctly throws Exception Error
}
