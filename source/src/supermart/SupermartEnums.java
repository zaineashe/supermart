package supermart;

/**
 * @author Zaine Ashe - 09469010
 */
public class SupermartEnums {
	
	public static enum CSVType {
		
		//set up regular expressions for each CSVtype
		//these are the regex patterns used to verify whether or not the given csv file
		//is the right CSVType
		MANIFEST("((>Refrigerated|>Ordinary),(.+,[0-9]+,)+)+"), 
		ITEMS( "(.+,(([0-9]+,[0-9]+,[0-9]+,[0-9]+,)|([0-9]+,[0-9]+,[0-9]+,[0-9]+,.*)))+"), 
		SALES("([^>]+,[0-9]+,)+");
		
		private String regex;
		
		CSVType(String regex) {
			this.regex = regex;
		}
		
		public String getRegex() {
			return this.regex;
		}
		
	}
	
	public static enum ItemTestType {
		DRY, COLD
	}
	
	public static enum ToStringType {
		NAME, DETAILS
	}
	
	public static enum TruckType {
		ORDINARY(1000), REFRIGERATED(800);
		
		private int capacity;
		
		TruckType(int capacity) {
			this.capacity = capacity;
		}
		
		public int getCapacity() {
			return this.capacity;
		}
	}
	
}


