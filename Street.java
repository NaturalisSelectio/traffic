import java.util.*;



/** A one-way street */
public class Street extends Thread implements Runnable{
	
	private String streetName; // Name of street
	private Node start; // Node where the street starts
	private Node end; // Node where street ends
    private long timeDelay; // amount of delay for vehicle to move on this street
    private static ArrayList<Street> allStreets= new ArrayList<Street>();
    public static int numStreets=0;
    private LinkedList<Vehicle> vehicleList; // List of vehicles currently on this street
    
    private int direction=0;//Direction of the street
    private int maxVehicles=2; // Maximum number of vehicles allowed on this street 
    private int currentNumV=0; //current number of vehicles on this street
    private boolean Go; // if true, then front car can move into the connected intersection
    private boolean isExit; // true if you have reached end destination
    private int weight=0;
    private int orientation;//specifies if the cars in a street go in the positive or negative direction
    private boolean onAlert= false;
    
    // For GUI
    private int xpos; // xposition of street
    private int ypos; // yposition of street
    private int width; // width of street 
    private int height; // height of street
    private String pathDirection;
    //private boolean vertical;
    
 // Synchronize lock objects
	public static Object lockObject = new Object();
	
	private Main myGUI;
    
    public void setGo(boolean go){
    	Go= go;
    }
    
    public String getPathDirection(){
    	return pathDirection;
    }
    
    /** Constructor: initialize everything and set positions for GUI 
     * */
	public Street(Node start, Node end, String name, String pathDir, int n, int x, Main gui) {
		this.start = start;
		this.end = end;
		streetName = name;
		timeDelay= (long)(Math.random()*8000 + 2000); 
		pathDirection= pathDir;
		direction= n;
		vehicleList = new LinkedList<Vehicle>();
		orientation = x;
		myGUI = gui;
		allStreets.add(this); // Gives errors?
		numStreets++; // gives errors?
		setGo(false);
		Main.record(streetName + " " + pathDirection + " Street has been created. ");
	}
	
	/** when activated, should let front person go at its timeDelay rate */
	public void run(){
		while(true){
			try {
				if(Go==true){
					if (vehicleList.size()>0){
						printStreet(this);
						Vehicle toMove = vehicleList.getFirst();
						Street target = toMove.getTargetStreet();
						if	(target == null) {
							vehicleList.removeFirst();
							toMove.stopVehicleThread();
						}
						else if (!target.isMax()) {
						
							vehicleList.removeFirst();
							toMove.moveFrontVehicle(); // move Front vehicle
						}
						printStreet(this);
						printStreet(target);
					}
					//System.out.println("After:");
					//printStreet(this);
					Thread.sleep(timeDelay);
					
				}
				else
					//Main.record("Street " + streetName + " is  inside false");
				Thread.sleep(timeDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // wait a bit according to its timeDelay rate
		}
		// have function that checks if there is an ambulance on alert? ambulance should notify street
	}

	// responds to emergencies
	public void ambulanceAlert(){
		Main.record(this.getStreetName() + " is in an emergency!");
		// for every vehicle except this ambulance
		for(int i = 0; i < vehicleList.size(); i++){
			Vehicle v = vehicleList.get(i);
			// if vehicle not an ambulance
			if (v.getType() != "Ambulance")
					v.setWaiting();  // stop threads
			// if vehicle is an ambulance
			else if(v.getType()=="Ambulance"){
				Ambulance a= (Ambulance)v;
				if(a.getIsEmergency()==false){
						a.setWaiting();
				}

			}	
		}
	}
	
	public void ambulanceNotAlert() {
		Main.record(this.getStreetName() + " is no longer in an emergency!");
		for(int i = 0; i < vehicleList.size(); i++){
			Vehicle v = vehicleList.get(i);
			v.stopWaiting();
		}
	}
		
	public void printStreet(Street s){
		if(s==null)
			return;
		Main.record(s.getStreetName() + " St. : ");
		for(int i = 0; i < s.vehicleList.size(); i++){
			Main.record(" --> " + s.vehicleList.get(i).getName());
		}
		Main.record(" ");
	}	
	
	public void setAlert(){
		onAlert = true;
	}
	
	public boolean getAlert(){
		return onAlert;
	}
	
	public String getStreetName(){
		return streetName;
	}

	
    public static int getNumStreets(){
    	return numStreets;
    }
   
    public static ArrayList<Street> getStreetArray(){
    	return allStreets;
    }
	    
    public int getXpos(){
    	return xpos;
    }
    
    public int getYpos(){
    	return ypos;
    }
    
    public int getWidth(){
    	return width;
    }
    
    public int getHeight(){
    	return height;
    }
	
	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @return the orientation
	 */
	public int getOrientation() {
		return orientation;
	}

	public void setVehicles(LinkedList<Vehicle> p) {
		vehicleList = p;
	}
	
	public LinkedList<Vehicle> getVehicles() {
		return vehicleList;
	}
	
	public int getVehicleListLength() {
		return vehicleList.size();
	}
	
	public Vehicle getFront() {
		return vehicleList.getFirst();
	}
	
	public Vehicle getNext(Vehicle v) {
		int i = vehicleList.indexOf(v);
		if (i < vehicleList.size())
			return vehicleList.get(i + 1);
		else {
			return null;
		}
	}
	
	public void addVehicle(Vehicle v) {
		vehicleList.add(v);
	}
	
	public Vehicle removeVehicle(Vehicle v){
		vehicleList.remove(v);
		return v;
	}
	
	public Node getStart() {
		return start;
	}
	
	public Node getEnd() {
		return end;
	}
	
	public double getTimeDelay() {
		return timeDelay;
	}
	
	public boolean isExit() {
		return isExit;
	}
	
	public boolean isMax() {
		return vehicleList.size() == maxVehicles;
	}
	

	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int w){
		weight= w;
	}
	
	public void moveVehicleToFront(Vehicle v) {
		vehicleList.remove(v);
		vehicleList.addFirst(v);
	}

}
