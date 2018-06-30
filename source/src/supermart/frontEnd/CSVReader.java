package supermart.frontEnd;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;

import supermart.SupermartEnums.CSVType;
import supermart.SupermartEnums.TruckType;
import supermart.backEnd.Item;
import supermart.backEnd.Manifest;
import supermart.backEnd.OrdinaryTruck;
import supermart.backEnd.RefrigeratedTruck;
import supermart.backEnd.Stock;
import supermart.backEnd.Store;
import supermart.backEnd.Truck;
import supermart.exceptions.CSVFormatException;
import supermart.exceptions.DeliveryException;
import supermart.exceptions.DryException;
import supermart.exceptions.StockException;

/**
 * @author Zaine Ashe - 09469010
 * 
 * Class full of static methods for reading / writing CSV files and parsing their
 * data into back-end objects.
 * 
 * Holds methods called by GUI for Importing Items Properties, Sales Logs and Manifests.
 * Also Exports Manifests using the Store's generateStockOrder method.
 * 
 * Export manifest algorithm is stored in this class. The algorithm
 * finds the most optimum collection of trucks to export as a CSV file.
 * Also included are algorithms for splitting CSV data into Stocks and Manifests
 * 
 * Most of the methods in CSVReader.java require a Store instance to be passed to it
 * as an argument. This is so then the CSV Reader can call the store's methods,
 * and make decisions using data from the store (such as whether or not items have been initialised in it,
 * or how many items in the inventory need to be reordered)
 */
public class CSVReader {
	
	// --------------
	// CSV Reader
	//
	// reads csv files from a given File object (easy to plug in from gui File selecter)
	// compares full file strings to regex patterns assigned in SupermartEnums
	// splits valid CSV data into an array of strings, then runs through in three
	// different methods (ImportManifest, ImportSales and ImportItems)
	// the import method can inheret a whole bunch of different DeliveryExceptions (replaced CapacityException)
	// so basically, you wanna run your import method in your gui, with a try/catch that prints the error's getMessage() to the info log
	// --------------
	
	// THERE ARE A LOT OF PRIVATE METHODS IN HERE
	// THE ONLY METHOD YOU NEED TO ACCESS IS 'Import(CSVType type, Store store, File file)' AND 'ExportManifest(Store store, File file)'
	// use the CSVType enumerator to decide what type of import to perform.
	
	//define some constant ASCII chars for the file reader
	//the return char is at the end of each line on the provided CSV's
	static int CARRIAGE_RETURN_CHAR = 13;
	//-1 is the terminate char that Reader.read() throws when there's no more file to read
	static int TERMINATE_CHAR = -1;
	//newline char is the ASCII code for '\n', which creates a line break.
	static int NEWLINE_CHAR = 10;
	
	// ==============================================================================================================================
	
	//quick little abstract method built to construct and throw a CSVFormatException, with given message
	/**
	 * Throws a format error
	 * <p>
	 * abstract method built to throw a CSVFormatException in one line
	 * constructs the exception and throws it
	 * @param string for the message that goes into the CSVFormatException
	 * @throws CSVFormatException on call
	*/
	private static void ThrowFormatErr(String message) throws CSVFormatException {
		
		//construct CSVFormatException
		CSVFormatException formatErr = new CSVFormatException(message);
		//throw constructed error.
		throw formatErr;
	}
	
	// ==============================================================================================================================
	
	//abstract method to throw Delivery Error, so it can be referenced in one line in the code body
	/**
	 * Throws a delivery error
	 * <p>
	 * abstract method built to throw a DeliveryException in one line
	 * constructs the exception and throws it
	 * @param string for the message that goes into the DeliveryException
	 * @throws DeliveryException on call
	*/
	private static void ThrowDeliveryErr(String message) throws DeliveryException {
		
		//construct delivery exception with given string argument as its message
		DeliveryException deliveryErr = new DeliveryException(message);
		//throw the constructed error
		throw deliveryErr;
	}
	
	// ==============================================================================================================================
	
	/**
	 * Shortcut method for generating cold item from string array
	 * <p>
	 * abstract method for pulling a cold item from an array of string values
	 * throws numberFormat or indexOutofBounds exception, indexOutOfBounds throws when this item constructor
	 * reaches past the data array length.
	 * if the last item in a sales log is a dry item, and you try
	 * to build a cold item out of that data, you will be trying to read one value over the given data
	 * (this is because the cold items have one extra value in the CSV, this is handling that by throwing exceptions)
	 * the given arguments in this method, are the data array itself, and the current index of the while loop this
	 * method is called in. This method is made to be called halfway through a while loop
	 * @param data an array of strings to serve as the data to parse in this method
	 * @param i the index used to indicate the current place in the data array
	 * @throws NumberFormatException if the double or integer parsing can't create the values needed by the Item object
	 * @throws IndexOutOfBoundsException if the data assignment reaches past the given data array
	*/
	private static Item FindColdItem(String data[], int i) throws NumberFormatException, IndexOutOfBoundsException {
		
		//we can take advantage of the fact that the values in the CSV are listed sequentially
		//our current position, i, is at the start of a new item, which is a string name
		//all of the values in this string array are strings (because its just been ripped from the CSV file, where everything
		//was stored as characters)
		
		//so to get the data we need to create our new item, we can extend past our index's reach (i.e. i+1, i+2, i+3,...,i+5)
		//this of course can cause indexOutofBoundsExceptions very easily, which is why the ImportItems method is built
		//to catch those errors.
		return new Item(	data[i],
							Double.parseDouble(data[i+1]),
							Double.parseDouble(data[i+2]),
							Integer.parseInt(data[i+3]),
							Integer.parseInt(data[i+4]),
							Double.parseDouble(data[i+5]));
		//each of the values are parsed from a string to their native value types. This has a change to create a NumberFormatException
		//(i.e. if somewhere in the CSV, a reorder point is not constructed in a valid way, then it will throw exception).
		//however, again, the ImportItems method is built to catch these errors
	}
	
	// ==============================================================================================================================
	
	
	/**
	 * Shortcut method for generating dry item from string array
	 * <p>
	 * findDryItem is built the same as FindColdItem, and is called in similar circumstances
	 * However, findDryItem is used to parse sections of the CSV which only have five bits of data before the
	 * next new item. Essentially, these two variations of the same method are needed to deal with the temperature
	 * value in CSV's. We can't approach the Items List CSV with a rigid structure, because a dry item will be a different
	 * length than a cold item, as opposed to just using a 'no_temperature' string or something
	 * @param data an array of strings to serve as the data to parse in this method
	 * @param i the index used to indicate the current place in the data array
	 * @throws NumberFormatException if the double or integer parsing can't create the values needed by the Item object
	 * @throws IndexOutOfBoundsException if the data assignment reaches past the given data array
	*/
	private static Item FindDryItem(String data[], int i)  throws NumberFormatException, IndexOutOfBoundsException  {
		
		//perform the same sequential over-reach method as seen in FindColdItem, except now only with 5 pieces of data.
		return new Item(	data[i],
							Double.parseDouble(data[i+1]),
							Double.parseDouble(data[i+2]),
							Integer.parseInt(data[i+3]),
							Integer.parseInt(data[i+4]));
		//final constructed item is returned.
	}
	
	// ==============================================================================================================================
	
	/**
	 * Shortcut method to create an ordinary truck without calling a DryException
	 * <p>
	 * Ordinary trucks throw a DryException, so this is just a
	 * method to turn those DryExceptions into DeliveryExceptions, just to narrow
	 * down the error scope of the CSVReader Import method
	 * (hopefully because of this, the only try/catch you'll need to handle when calling Import is DeliveryException)
	 * @param stock the stock to generate the ordinary truck with
	 * @throws DeliveryException if the ordinary truck construction throws a dryException, then it translates into a DeliveryException
	 * @returns the final constructed ordinary truck, or null if the exception is thrown
	*/
	private static Truck SafeOrdinaryTruck(Stock stock) throws DeliveryException {
		
		//initialise the truck to return
		Truck toAdd;
		
		try {
			
			//try building an ordinary truck with the given stock
			toAdd = new OrdinaryTruck(stock);
			//if successful, return the successfully constructed truck
			return toAdd;
			
		} catch (DryException dry) {
			
			//if a dry exception is caught, throw a delivery exception with a custom message
			//this custom message will include the dry exceptions message, so all error logging is laid bare.
			ThrowDeliveryErr("Truck is dry, attempted to add a cold item to an ordinary truck: " + dry.getMessage());
			//finally, return null for a failed construction
			return null;
		}
	}
	
	// ==============================================================================================================================
	
	/**
	 * shortcut method for constructing a truck (no matter what type)
	 * <p>
	 * using the type argument, the buildtruck method determines what kind of truck needs to be constructed
	 * if the truck construction is successful, it is returned, otherwise a DeliveryException is thrown.
	 * @param store the store instance being used in the gui
	 * @param builder an arraylist of strings that is used to construct a stock for the truck
	 * @param type an enum to denote the truck's type (ORDINARY or REFRIGERATED)
	 * @throws DeliveryException if neither ORDINARY or REFRIGERATED are recognised for type argument.
	 * @returns return the truck that's been build, or null if the delivery exception has been thrown
	*/
	private static Truck BuildTruck(Store store, ArrayList<String> builder, TruckType type) throws DeliveryException {
		
		//check if the truck is ordinary type
		if (type == TruckType.ORDINARY) {
			
			//if so, build an ordinary truck using the SafeOrdinaryTruck method
			//build the stock at the same time as the truck to avoid referencing errors
			//this utilises the stock constructor which takes a store and a arraylist of strings
			return SafeOrdinaryTruck(new Stock(store, builder));
			
		} else if (type == TruckType.REFRIGERATED) {
			
			//if the truck type is refrigerated, then build a refrigerated truck
			//use the same reference-independant stock constructor for this build as well
			return new RefrigeratedTruck(new Stock(store, builder));
			
		} else {
			
			//if neither truck types are recognised, throw a delivery exception and return null
			ThrowDeliveryErr("Truck type unrecognisable! (this error shouldnt throw)");
			return null;
		}
		
	}
	
	// ==============================================================================================================================
	
	
	
	/**
	 * Shortcut method to get truck type enum from a string
	 * <p>
	 * method to grab a truck enum type from a given string
	 * this is used in manifest importing to decode the ">Ordinary" or ">Refrigerated"
	 * values into their enum equivalents
	 * (hopefully because of this, the only try/catch you'll need to handle when calling Import is DeliveryException)
	 * @param type a string version of the truck type to be interpreted
	 * @throws DeliveryException if neither of the truck enums are recognised
	 * @returns the truck type enum (REFRIGERATED or ORDINARY) translated from the string
	*/
	private static TruckType getType(String type) throws DeliveryException {
		
		//check if the string argument reads ">Ordinary"
		if (type.equals(">Ordinary")) {
			
			//return the ordinary enum
			return TruckType.ORDINARY;
		
		} else if (type.equals(">Refrigerated")) {
			
			//if not ">Ordinary", and string argument reads ">Refrigerated"
			//return the refrigerated enum
			return TruckType.REFRIGERATED;
			
		} else {
			//if the string arguments reads neither, throw a DeliveryException and return null
			//this means the manifest .csv is using a truck type that isnt recognised by the trucktype enumerator
			ThrowDeliveryErr("Can't parse truck type from the csv value (needs to be '>Ordinary' or '>Refrigerated')");
			return null;
		}
	}
	
	// ==============================================================================================================================
	
	
	/**
	 * Shortcut method to get truck type enum from an item
	 * <p>
	 * method to grab a truck type which associates with a given item
	 * this is used to built trucks for the manifest export 
	 * the algorithm finds the coldest item, and creates a truck of type to fit
	 * that items type.
	 * if the coldest item found is actually a dry item, then it needs to build
	 * and ordinary truck. Hence this method is necessary to make that distinction
	 * @param item an item whose CheckIfDry value will determine the returned TruckType
	 * @returns the truck type enum (REFRIGERATED or ORDINARY) translated from the string
	*/
	private static TruckType getType(Item item) {
		
		//check if the given item argument is dry or not
		if (item.CheckIfDry()) {
			
			//if the item is dry, return ordinary enum
			return TruckType.ORDINARY;
		} else {
			
			//if the item is not dry, return refrigerated enum
			return TruckType.REFRIGERATED;
		}
	}
	
	// ==============================================================================================================================
	// ==============================================================================================================================
	// FILE MANIPULATION METHODS
	// ==============================================================================================================================
	// ==============================================================================================================================
	
	// ==============================================================================================================================
	
	
	/**
	 * Takes a manifest instance and writes a CSV file to the given filepath.
	 * <p>
	 * try to create the file and dump the manifests toString method to it. If the 
	 * file writing process is interrupted at any point, the catch will call a CSVFormatException
	 * @throws CSVFormatException if the file creation throws an IO error, translate it to a CSVFormatException
	 * @param manifest the generated manifest to turn into a .csv file.
	 * @param file the file directory given by the gui to write the file to.
	*/
	private static void WriteManifest(Manifest manifest, File file) throws CSVFormatException {
		
		//check if th given manifest is empty or not
		//use the toList method to grab the manifest internal list reference
		if (manifest.toList().isEmpty()) {
			
			//if the manifest is empty, a file can't be written
			//throw a csvformat exception stating that there are no contents to write to the file
			ThrowFormatErr("Cannot export manifest, there aren't any items to reorder!");
			
		} else {
			
			//if the manifest isnt empty, we can start writing to the filepath			
			try {
				
				//try creating a new file at the given directory from File object	
				file.createNewFile();
					
				//create a writer for the new file
				FileWriter writer = new FileWriter(file);
					
				//use writer's string intake method to dump the entire manifest toString into the file
				writer.write(manifest.toString());
					
				//close the file from the writer
				writer.close();
					
			} catch(IOException e) {
				
				//if any of the methods in the try block throw an IO exception, then something
				//has gone wrong in the file writing process
				//translate the into error into a CSVFormatException, keeping the original error message
				ThrowFormatErr("IO Error: " + e.getMessage());
			}
		}
	}
	
	// ==============================================================================================================================
	

	/**
	 * takes a File object (filepath) and returns the string data of that file.
	 * <p>
	 * this method takes a File object and outputs its raw string data
	 * this is the method called by Import to get the CharacterSequence for regex comparison
	 * @param file the file directory given by the gui to read the file from.
	 * @throws CSVFormatException if the file opening & reading processes are interruped, a CSVFormat error is thrown
	 * @returns a string value containing the entire file's contents.
	*/
	private static String ReadFile(File file) throws CSVFormatException {
		
		//initialise the final return string as a blank string		
		String data = "";
		
		//check if the given file has read permissions
		if (file.canRead()) {
			
			try {
				
				//try opening the file with a FileReader object, this can catch a FileNotFound exception
				//only if the filepath is invalid.
				FileReader reader = new FileReader(file);
				
				try {
					//try to loop through the data in the file.
					//if this steam is interrupted before the file is closed, 
					//an IOException can throw
					
					//begin by intialising the boolean that will
					//control our while loop
					boolean breakRead = false;
					
					//for as long as the breakpoint is false, continue looping through
					//the file
					while (!breakRead) {
						
						//grab the next character from the filestream and assign
						//it to the buffer.
						//buffer is filetype int because we're using C style char
						//variables (reading file character by character w/ ASCII int values)
						int charBuffer = reader.read();
						
						//check if the terminate character has been read (-1)
						//this means the reader has reached the end of the file
						if (charBuffer == TERMINATE_CHAR) {
							
							//if theres no more data to grab, break the while loop
							breakRead = true;
							
							//the next special character we need to handle is the 
							//newline character. Regex has trouble reading '\n'
							//so for the sake of data collection we'll change it to 
							//another comma.
						} else if (charBuffer == NEWLINE_CHAR) {
							
							//if a newline character has been read, 
							//insert a comma into the final data string
							//this saves a lot of trouble with the
							//regex validation.
							data += ",";
							
							//finally, check if the given character isn't the carriage return.
							//the carriage return character accompanied the newline character
							//in the blackboard-given CSV files. I think they're used
							//to specify a newline within excel. I'll make sure to use
							//carriage return characters next to my newline characters when doing
							//the export manifest method.
						} else if (charBuffer != CARRIAGE_RETURN_CHAR) {
							
							//if there arent any illegal characters detected,
							//then add the currently indexed character to the final
							//data output string.
							data += (char) charBuffer;
							
						}
						
					}
					
					//this is the end of the while loop
					//after the terminate char is discovered, and the breakRead boolean
					//is switched to true, we can assume that the CSV file has been completely ripped
					
					//end the data output string with a comma, so then
					//the regex can successfully recur properly.
					//(this wont matter too much, we can warp the data as much as
					//we need to once we've ripped it from the file)
					// -- 
					//the only time we need to be careful with out csv formatting is when we're writing a manifest
					//in the export manifest method
					data += ",";
					
					//after csv file has been successfully ripped, close the file from within the reader.
					reader.close();
					
				} catch(IOException ioErr) {
					
					//if an IOException occurs during the filestreaming while loop, throw a format error
					//make sure the format error includes the message from the ioError.
					//this will show up in the infolog as a custom message.
					ThrowFormatErr("Input/Output error: " + ioErr.getMessage());	
				}				
				
			} catch(FileNotFoundException notFound) {
				
				//if the file can't be found (potentially invalid filepath)
				//then throw a format error
				//the FileNotFoundException's message is included in this
				//format error message, and will be displayed on the infolog
				ThrowFormatErr("File not Found: " + notFound.getMessage());	
			}
			
		} else {
			
			//if the file hasn't got read permissions, then throw a format error.
			ThrowFormatErr("This file is unreadable");
		}
		
		//finally, return the constructed message.
		return data;
		
	}
	
	// =================================================================
	// =================================================================
	// IMPORT / EXPORT METHODS
	// =================================================================
	// =================================================================
	
	// ==============================================================================================================================
	
	/**
	 * method to create a stock of items from an imported .csv items file.
	 * <p>
	 * This is the method called by the import items button in GUI.
	 * it takes the data array given to it by the parent import method, and translates
	 * it into a stock object, so that the store can add it to its inventory.
	 * @param store the store instance currently being used by GUI. This needs to be passed through so that store can be referenced in the method
	 * @param data the data array provided by the parent import method. To be used in building the stock
	 * @throws CSVFormatException if generating the items fails in any way, the CSVFormat will be thrown
	*/
	private static void ImportItems(Store store, String[] data) throws CSVFormatException {
		
		//initialise the index to loop through each string in the data array
		int i=0;
		
		//initialise the new stock that this data will translate to
		Stock toAdd = new Stock();
		
		//iterate through each string in data array
		while (i<data.length) {
			
			//init the item to be generated from the array
			Item item;
			
			try {
				
				//try building an item from the array with a temperature value involved
				//if FindColdItem throws an error, then the current item to be added must be dry
				item = FindColdItem(data, i);
				
				//if this is successfully, FindColdItem will have burned through the next six pieces of data
				//by constructing itself, so to get to the next item name, increase the index by 6
				i+=6;
				
			} catch (IndexOutOfBoundsException | NumberFormatException indexErr) {
				
				//if an index out of bounds error is thrown, then we're on the final item, and it's NOT a temp item
				//(overreaching into i+5 caused the out of bounds error)
				//create a dry item from the next 5 pieces of data
				item = FindDryItem(data, i);
				//because a dry item has one less piece of data than a cold item (dry doesnt have a temperature value)
				//increase index by 5
				i+=5;
			}
			
			//add the constructed item to the stock
			toAdd.addItem(item);
		}	
		
		//after the stock has been created from the data index, run store's ImportItems method to make changes to the store.
		store.ImportItems(toAdd);
	}
	
	// ==============================================================================================================================
	
	/**
	 * method to create a stock of items for the store to process sales with
	 * <p>
	 * This is the method called by the import sales log button in GUI.
	 * it takes the data array given to it by the parent import method, and translates
	 * it into a stock object, so that the store can change capital and inventory quantities based on it.
	 * @param store the store instance currently being used by GUI. This needs to be passed through so that store can be referenced in the method
	 * @param data the data array provided by the parent import method. To be used in building the stock
	 * @throws DeliveryException if the data array depicts an item which doesn't exist in the store, throw this exception
	 * @throws StockException if the data array contains any quantity amounts that are negative, this exception throws
	*/
	private static void ImportSales(Store store, String[] data) throws DeliveryException, StockException {

		//initialise the index for looping through each piece of data
		int i = 0;
		
		//initialise new stock to store the sales data in
		Stock toAdd = new Stock();
		
		//loop through each string in the given data array
		while (i<data.length) {
			
			//data[i] will be an item name (a la 'beef' or 'rice')
			//the first check will be to see if the store has an item
			//of that name in its stock
			//so ergo, check if stores stock contains an item
			//of name given in data[i]
			if (store.getStock().Contains(data[i])) {
				
				//grab the item that was just confirmed to exist
				//stock's getItem method takes a string and returns
				//an item that has that name.
				Item item = store.getStock().getItem(data[i]);
				
				//add that item to the sales tock
				toAdd.addItem(item);
				
				//try setting the quantity for the newly added item

				//the string at data[i+1] is a number for the item quantity
				//try setting the quantity, keyed by item, to the Integer parse of data[i+1]
				toAdd.SetQuantity(item, Integer.parseInt(data[i+1]));
				
			} else {
				
				//if item doesnt exist, then throw the delivery exception
				ThrowDeliveryErr("Item in CSV hasn't been initialised in the Store Stock.");
			}
			
			//because this iteration has burned through two pieces of data in the array, increase i by 2
			i+=2;
		} 
		store.ImportSales(toAdd);
	}
	
	// ==============================================================================================================================
	
	/**
	 * method to create a manifest of trucks to process delivery orders with
	 * <p>
	 * This is the method called by the import manifest log button in GUI.
	 * it takes the data array given to it by the parent import method, and translates
	 * it into a manifest full of trucks. This lets the store process it into capital changes and item quantity changes.
	 * @param store the store instance currently being used by GUI. This needs to be passed through so that store can be referenced in the method
	 * @param data the data array provided by the parent import method. To be used in building the manifest
	 * @throws DeliveryException if the data array depicts an item which doesnt exist in the store, throw this exception
	 * @throws StockException if the data array contains any quantity amounts that are negative, this exception throws
	*/
	private static void ImportManifest(Store store, String[] data) throws DeliveryException, StockException {
		
		
		//to turn an array of strings into a manifest, first the data array needs to be split
		//into multiple arraylists of Strings.
		
		//the trucks in these manifests cant be built over more than one line of code, or
		//else it will create a reference error (if each new truck construction uses the same Truck instance,
		//then it wont be able to be passed through into a manifest).
		
		//So, to make a for loop which will subvert the referencing problem, we must first
		//split our data array into multiple sub-arrays
		//to give these sub-arrays a variable length, we need to use a Collection
		//so, to emulate a two dimensional array within a Collection, use an ArrayList of ArrayLists
		//each of the ArrayLists inside the parent ArrayList contains string values (mirroring the data array)
		ArrayList<ArrayList<String>> builder = new ArrayList<ArrayList<String>>();

		//to iterate through both arraylists, we need three indexing variables
		//'truckIndex' to determine what truck we're on
		//'i' to assign pieces of data from the data array to the truck's arraylist
		//and 'offset', which we use to make data's 'i' index applicable to all truck iterations
		int truckIndex = 0;
		int offset = 0;
		
		//using our 'i' index, iterate through each index in the data array
		for (int i = 0; i<data.length; i++) {
			
			//check if this is a new list. if the current index is at ">Ordinary"
			//or ">Refrigerated", then we need to make a new array list (analogous to making a new truck)
			if (data[i].substring(0,1).equals(">")) {
				
				//check if this is not the first list
				//if i is equal to zero than that means this is the very start of the for loop
				//in that case, some steps need to be skipped
				if (i != 0) {
					
					//if this isnt the start of the for loop, then that means this
					//is the construction of a new truck.
					//we need to increase truckIndex and our offset.
					//truckIndex going up by one means we'll be accessing a different arrayList now
					//and adding new data to it as opposed to the ArrayList we were working with before.
					truckIndex += 1;
					//offset needs to be increased, because for example:
					//if our for loop is at i=16, and we're just now creating a new arraylist
					//then the loop will reiterate into i=17, and the algorithm will try to put
					//the new piece of data into index 17 of our new arraylist.
					
					//This doesnt make sense, so we need to offset our index every time we make a new truck, so that
					//our 'i' index is 'technically' starting back at i=0;
					offset += i - offset;
				}
				
				//initialise new list at current truck index
				builder.add(truckIndex, new ArrayList<String>());
				
			}

			//grab the data, add it to the builder at coords [truckIndex, i-offset]
			//i-offset is used as the index so then each arraylist starts back at index 0.
			//this is the purpose of offset.
			builder.get(truckIndex).add(i-offset, data[i]);
		}

		//now that our array list of array lists has been built, we can loop through it and use it to
		//create our trucks in one line.
		
		//initialise new manifest to store the csv data into
		Manifest manifest = new Manifest();
				
		//loop through our builder parent arraylist, grabbing a new
		//child arraylist in each iteration. These child arraylists will be
		//used to build trucks
		for (ArrayList<String> truck : builder) {
			
			//the string at the 0 index of each of these arraylists should
			//be either ">Ordinary" or ">Refrigerated".
			//using the getType private method, parse this string
			//value into a TruckType enum, and check what type it is
			if (getType(truck.get(0)) == TruckType.ORDINARY) {
				
				//if the truckType is ordinary, use the safeOrdinaryTruck method
				//to add a new ordinary truck to the manifest
				//use the current truck arraylist in the constructor for the truck's stock.
				manifest.add(SafeOrdinaryTruck(new Stock(store, truck)));
				
			} else if (getType(truck.get(0))  == TruckType.REFRIGERATED)  {
				
				//if the truck type is refrigerated, do the same as ordinary truck
				//except use the raw constructor for refrigerated truck, instead of 
				//the safeOrdinaryTruck method.
				manifest.add(new RefrigeratedTruck(new Stock(store, truck)));
			} else {
				
				//if neither of the two truck types are recognised, throw a delivery error.
				ThrowDeliveryErr("incompatible type recognised in manifest CSV (somehow)");
			}
		}		
		
		//after the manifest has been successfully constructed, call store's ImportManifest method using
		//the manifest.
		store.ImportManifest(manifest);
		
	}
	
	// =================================================================
	// =================================================================
	// PARENT IMPORT METHOD (used to run regex verification, which i managed to abstract out to the enums)
	// THIS IS THE ONLY IMPORT METHOD THAT NEEDS TO BE CALLED FROM CSV READER
	// USE CSVTYPE ENUM TO DETERMINE WHAT KIND OF IMPORT YOU'RE DOING
	//
	// (This lets CSVType hold the big gross regex patterns i had to write up, which makes this method a lot neater.)
	// =================================================================
	// =================================================================
	
	// ==============================================================================================================================
	
	/**
	 * The parent import method used to call each of the three 'import' methods from within CSVReader
	 * <p>
	 * this method uses the ReadFile method to grab the raw data, then compares that data to the Regex expression
	 * that is defined by the type argument (CSVType enum comes with a paired Regex expression that specifies it's CSV anatomy)
	 * if the regex passes, then this method determines which of the three 'child' methods to call (ImportSales, ImportItems, ImportManifest)
	 * This is essentially a universal gateway method for processing GUI inputs (except export manifest, thats its own method).
	 * @param type the CSVType enum determines what kind of regex to compare the file's contents to, and how to handle its methods.
	 * @param store the current stores reference needs to be passed through so it can be used in later methods.
	 * @param file the given file to read and parse data from
	 * @throws DeliveryException the ImportCSV parent method inherets the DeliveryException throws from it's child methods
	 * @throws StockException the ImportCSV parent method inherets the StockException throws from it's child methods
	 * @throws CSVFormatException inhereted from child methods, CSVFormatException also throws if the regex expression doesnt match.
	*/
	public static void ImportCSV(CSVType type, Store store, File file) throws CSVFormatException, DeliveryException, StockException {
		
		//this is one of the only public CSV reader methods, so its the one being accessed by the frontend
		//the given args are the store the Gui is using, the CSVType enum (which holds the regex pattern for the raw data),
		//and a File object for the CSV that's going to be dissected
		
		//first things first is to get the raw data from the File object, using the previously defined ReadFile method
		String data = ReadFile(file);
		
		//this is the statement that checks if the raw data fits its file type
		//a regex comparator is used to see if the CSVType matches up with the raw data thats been given
		//using the static Pattern method matches(), compare the raw data (casted as a char sequence), to
		//the regex value of the given type enum
		//data needs to be casted to a CharSequence to make the regex comparator work.
		//type.getRegex() grabs the regular expression from the SupermartEnums.java.
		//(storing regex in the enums class makes this method a lot cleaner)
		
		if (Pattern.matches(type.getRegex(), (CharSequence) data)) {
			
			//if the regex is successful, then we need a switch
			//statement to see which import type we're dealing with
			switch (type) {
			
				case ITEMS:
						
					//if this is an items import, then run the importitems method, giving the store
					//and the data, split into an array by commas
					ImportItems(store, data.split(","));
					break;
				
				case SALES:
					
					//for the sales type, run the importsales method with the same arguments
					//we split the raw string data into an array to make it easier to
					//iterate through in the while loops.
					ImportSales(store, data.split(","));
					break;
				
				case MANIFEST:
					
					//for manifest type, run importmanifest with the same arguments.
					//when the data is split by comma, it completely removes the commas
					//from the situation. This leaves us with a clean array of strings
					//containing all the data we need to construct our manifests and stocks
					ImportManifest(store, data.split(","));
					break;
				
				default:
				
					//default case is called when the switch statement doesnt
					//recognise the given type argument.
					//if no type (or invalid type) has been specified for the import method,
					//then return a csv format error
					ThrowFormatErr("no CSVtype specified by the method caller");
			}
		} else {
			
			//if the regex comparison fails, then throw a format error, giving the type name in the error message.
			ThrowFormatErr("File (type "+type.name()+") is not correctly formatted (REGEX DID NOT MATCH)");
		}		
	}
	
	// ==============================================================================================================================
	
	/**
	 * Algorithm for generating an optimum manifest from a store's stock order.
	 * <p>
	 * The export manifest method grabs the stock order from store, and runs through it. An optimum collection of trucks is generated
	 * using an iterative algorithm, and finally the WriteManifest method is called on the given File object.
	 * @param store the ExportManifest method needs to have the current store passed through to it to generate a stock order.
	 * @param file The file path given by the GUI, to eventually write the optimized manifest to.
	 * @throws DeliveryException the ImportCSV parent method inherets the DeliveryException throws from it's internal methods
	 * @throws CSVFormatException inhereted from internal methods.
	*/
	public static void ExportManifest(Store store, File file) throws DeliveryException, CSVFormatException {
		
		//use the store's generateNewOrder method to grab an arraylist of all items that need
		//to be reordered.
		ArrayList<Item> items = store.generateNewOrder();		
		
		//create a new manifest to be exported as a csv
		Manifest manifest = new Manifest();
		
		//this is the arraylist that will build stocks for trucks
		//(using the stock constructor designed to dodge reference errors)
		ArrayList<String> builder = new ArrayList<String>();
		
		//initialise variables to be used in the following for loop
		//type will be used to keep track of what type of truck is
		//being constructed
		
		//current_capacity and current_quantity will simulate the capacity and quantity
		//of the trucks stock, without having to construct a truck yet
		//(this is because of the referencing errors, the final truck needs
		//to be created entirely in one line)
		TruckType type = null;
		int current_capacity = 0;
		int current_quantity = -1;
		
		//previous item is a stand-in instance used to stash the last
		//iteration's item in the memory.
		//If an items re-order amount overflows the trucks quantity,
		//we'll need to add the rest of the reorder amount in the next truck.
		//however by then the item's reference will be gone because the for loop will have
		//reached its next iteration. So we use previousItem to keep a reference to our last iteration's item.
		Item previousItem = null;
		
		//loop through each item in the store-generated item order
		for (Item item : items) {			
			
			//first, check if we need to perform some extra methods before adding
			//our item's names and quantities.
			
			//if the type is currently equal to null, then this is the very start of the for loop
			//if the current_quantity is over current_capacity, that means our current truck is full or overflowing.
			//either way, it means we need to set up a new truck.
			if (type == null || current_quantity >= current_capacity) {
				
				//check if type isn't null.
				//the type variable starts at null to signify that the for loop has just begun.
				//if this isn't the very first iteration, and this part of the code is being called
				//then that means our current truck is full
				if (type!=null) {
					
					//our current truck is full, which means it's good to
					//add to the manifest
					
					//initialise new truck and add it to the manifest
					//build the trucks stock using the current builder arraylist
					manifest.add(BuildTruck(store, builder, type));
				}
				
				//when initialising a new truck, first step is to clear out the builder array
				//this means we'll have a blank canvas to add our item names and quantities to.
				builder.clear();
				
				//find the difference between current_quantity and current_capacity.
				//if the truck is overflowing, then this will calculate the amount of overflow.
				int diff = current_quantity - current_capacity;
				
				//check if the overflow amount is greater than 0, and that 
				//this isn't the first iteration (first iteration is signified by type being null)
				if ((diff > 0) && (type!=null)) {
					
					//set new type of truck to be constructed based on current items CheckIfDry() value
					type = getType(previousItem);
					
					//add the truck type to the builder arraylist
					//the stock constructor which uses array lists of strings to build its stock will
					//ignore its first piece of data, because the first piece of data in an imported manifest
					//is either ">Ordinary" or ">Refrigerated".
					//So we're going to emulate this first piece of data by adding in the current TruckType type value,
					//parsed as its string using name()
					builder.add(">"+type.name());
					
					//these are the methods that deal with the overflow.
					//using the previousItem reference, we will add it's name to this fresh builder arraylist
					//with the quantity being equal to the diff value that we calculated earlier (diff value is
					//the amount of overflow from the previous truck).
					builder.add(previousItem.GetName());
					builder.add(Integer.toString(diff));
					
					//reset current quantity
					//right now, because this is a fresh truck, the current quantity is
					//equal to the last trucks overflow.
					current_quantity = diff;
					
				} else {
					
					//this is the handler for initialising a new truck when there was no overflow on the previous truck
					
					//set new type of truck to be constructed based on current items CheckIfDry method
					type = getType(item);
					
					//add the trucks type to the first index of the builder arraylist
					builder.add(">"+type.name());
					
					//reset current quantity
					current_quantity = 0;
				}
				
				//set current capacity to the associated capacity value
				current_capacity = type.getCapacity();
				
			}
			
			//set this item reference in the memory in case its reorder amount overflows the
			//capacity
			previousItem = item;
			
			//add the items name and reorder point to the builder arraylist
			//a quick calculation needs to be done here to ensure we aren't filling the truck over capacity.
			//initialise this quantity int as the current items reorder amount 
			int quantity = item.GetReorderAmount(); 
			
			//check if the truck will overflow after the reorder amount is added.
			if ((current_quantity + quantity) > current_capacity) {
				
				//if so change the quantity to add so that it just fits into the current trucks capacity.
				quantity = current_capacity - current_quantity;
			}
			
			//add the current item's name to the builder arraylist
			builder.add(item.GetName());
			
			//add the calculated quantity as a string to the builder arraylist
			builder.add(Integer.toString(quantity));
			
			//despite the calculated quantity, add the items raw reorder amount to the current_quantity tracker variable.
			//this will let the if statements at the start of the loop recognise that the truck has gone over capacity.
			current_quantity += item.GetReorderAmount();
			
		}
		
		//the final truck wont be able to build from inside the for loop, because the
		//items are being added after the loop is checking if it's full.
		
		//so we need to make sure the final truck is built
		//first, make sure the builder arrayList actually has content in it.
		if (!builder.isEmpty()) {
			
			//if the builder arraylist isnt empty, that means there is still a truck to build
			//build that truck with the buildTruck method.
			manifest.add(BuildTruck(store, builder, type));
			
			//clear the builder after creating the final truck
			builder.clear();
			
			//but wait! what if that final truck has overflow? We need to make a final final truck
			
			//calculate overflow amount (same equation as within the loop)
			int overflow = current_quantity - current_capacity;
			
			//check if the overflow exists, if so a new truck needs to be created
			if (overflow >=0) {
				
				//use the previousItem reference to see what the last item was (item which has overflow)
				//get the new trucks type
				type = getType(previousItem);
				
				//make builder a new arraylist for this new truck
				//first add the new trucks type at the start of the builder arraylist
				builder.add(">"+type.name());
				
				//grab the name of the last item referenced and add it to the builder
				builder.add(previousItem.GetName());
				
				//add in the overflow amount as the quantity for this final truck
				builder.add(Integer.toString(overflow));
				
				//add the overflow handling truck to the manifest
				manifest.add(BuildTruck(store, builder, type));
			}
		}
		
		//finally, after the optimised manifest has been completed, write the manifest to the given file.
		//this is done by calling the WriteManifest method
		WriteManifest(manifest, file);
	}
}