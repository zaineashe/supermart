package supermart.backEnd;

import java.util.AbstractList; 
import java.util.ArrayList;

/**
 * @author Harrison Berryman - 09745092
 * 
 * Builds a manifest containing trucks.
 * <p>
 * Builds a manifest that contains both refrigerated and 
 * ordinary trucks. The manifest states the truck's type and 
 * all item's it's carrying along with their quantities.
 */
public class Manifest extends AbstractList<Truck> {

	ArrayList<Truck> manifest;
	
	/**
	 * Constructs a manifest object
	 * <p>
	 * Constructs a manifest and initialises an ArrayList used to
	 * store a list of truck inside the manifest.
	 */
	public Manifest() {
		manifest = new ArrayList<Truck>();
	}
	
	/**
	 * Returns the truck at the specified position in the manifest
	 * 
	 * @param index The position of a truck in the manifest (starts at 0)
	 * @return The truck located at the index number in the manifest
	 */
	public Truck get(int index) {
		return manifest.get(index);
	}

	/**
	 * Returns size of the manifest
	 *  
	 * @return The number of trucks in the manifest
	 */
	public int size() {
		return manifest.size();
	}
	
	/**
	 * Adds a truck to the manifest
	 * <p>
	 * Checks to see if the truck is not null, if it not, the truck
	 * is then added and the method returns true.
	 * If the truck is empty false is returned.
	 * 
	 * @param truck The truck to be added to the manifest
	 * @return True if the truck is successfully added to the manifest, false if otherwise
	 */
	public boolean add(Truck truck) {

		if (truck != null){
			
			return manifest.add(truck);
		}
		else {
			return false;
		}

	}
	
	/**
	 * Checks if the manifest contains a certain truck
	 * <p>
	 * Returns true if the truck parameter is in the manifest.
	 * Returns false if otherwise.
	 * 
	 * @param truck The truck being searched for in the manifest
	 * @return True if the truck is found in the manifest, false if it can't be found
	 */
	public boolean contains(Truck truck) {
		if (manifest.contains(truck) == true) {
			return true;
		}
		else {
			return false;
	
		}
	}
	
	/**
	 * Returns the ArrayList used by the manifest
	 * 
	 * @return The main ArrayList used by the manifest
	 */
	public ArrayList<Truck> toList() {
		return manifest;
	}	
	
	/**
	 * Returns a string of the manifest
	 * <p>
	 * Expected string should be "&gt;Refrigerated
	 * <p>
	 * ItemName, Quantity
	 * <p>
	 * &gt;Ordinary etc."
	 * 
	 * @return A string of each truck in the manifest and it's stock
	 */
	public String toString() {
		
		String string1 = "";
		for(Truck i : manifest) {
			string1 += i.toString() + "\n";
		}
		//Removes the last comma from the string
		String string2 = string1.substring(0, string1.length() - 1);
		return string2;
	}
	
	

}
