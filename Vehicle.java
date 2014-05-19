
import java.awt.Color;
import java.util.*;
//TODO: make things add to the static arrays when made, have pick number of vehicles in the beginning?
public abstract class Vehicle implements Runnable {

	protected Node exitDirection; // vehicle's final destination Node
	private int priorityLevel;
	protected Street currentStreet; // current street vehicle is on
	protected LinkedList<Street> path;
	private static ArrayList<Vehicle> allVehicles= new ArrayList<Vehicle>();
	private static ArrayList<Ambulance> ambulances = new ArrayList<Ambulance>();
	private Color color;
	private double xPos;
	private double yPos;
	private double vx;
	private double vy;
	private double targetPointerX;
	private double targetPointerY;
	private int width;
	private int height;
	private Main myGUI;
	private static int totalNumVehicles=0;
	private String type;
	private int index;
	protected boolean stop= false;
	private static int vehicleCounter=0;
	private boolean isWaiting;
	
	public static int getVehCounter(){
		return vehicleCounter;
	}
	
	//TODO: changed this
	public Vehicle(int priority, Color col, int width, int height, String type,  Main gui) {
		int node= (int)(Math.random()*Node.getNumNodes());
		exitDirection = Node.getNodeArray().get(node);
		int street= (int)(Math.random()*Street.getNumStreets());
		currentStreet = Street.getStreetArray().get(street); 
		currentStreet.addVehicle(this);
		priorityLevel = priority;
		color= col;
		this.width= width;
		this.height= height;
		index = totalNumVehicles;
		this.type=type;
		totalNumVehicles++;
		myGUI = gui;
		vehicleCounter++;
		computeShortestPath(currentStreet.getEnd(), null);
		Main.record(this.getName() + " has been created on Street " + currentStreet.getStreetName() + " " + currentStreet.getPathDirection() +
				". Destination: Node " + exitDirection.getIntName());
		Main.record(type+" "+index+" has initial PATH: ");
		Main.record(printPath(path));
	}
	
	public static int getNumVehicles(){
		return totalNumVehicles;
	}
	
	// returns the name of the vehicle
	public String getName(){
		return type + " " + index;
	}
	
	public void randomizeDestinations(){
		return;
	}
	
	public void setType(String t){
		type = t; 
	}
	
	public String getType(){
		return type;
	}
	
	public void run() {
		while (!currentStreet.isExit()) {
			//System.out.println(path);
			if (!path.isEmpty() && path.getFirst().isMax() && currentStreet.getEnd().numStreets() > 1) {
				//System.out.println(currentStreet.getEnd().numStreets());
				computeShortestPath(currentStreet.getEnd(), path.getFirst());
				Main.record(printPath(path));
				//System.out.println(exitDirection.getIntName());
			}
			else if (!path.isEmpty() && path.getFirst().isMax()) {
				//printPath(path);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { System.out.println("FAIL"); }
		}
	}
	
	public Street getTargetStreet() {
		if (path.isEmpty())
			return null;
		return path.getFirst();
	}
	


	
	public double getXpos(){
		return xPos;
	}
	
	public double getYpos(){
		return yPos;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public Color getColor(){
		return color;
	}
	
	public double getTargetX() {
		return targetPointerX;
	}
	
	public double getTargetY() {
		return targetPointerY;
	}
	
	public void setTargetX(int t) {
		targetPointerX = t;
	}
	
	public void setTargetY(int t) {
		targetPointerY = t;
	}
	
	public void setTargetPointer(double x, double y){
		targetPointerX=x;
		targetPointerY=y;
	}
	
	public static ArrayList<Vehicle> getVehicleArray(){
		return allVehicles;
	}
	
	/** remove front vehicle from its current street and join the desired street */
	public void moveFrontVehicle() {
		synchronized(this) {
			if (path!=null && path.size() > 0){
				Street previous = currentStreet;	// record previous street
				currentStreet = path.removeFirst();	// find next street
				currentStreet.addVehicle(this);
				Main.record(this.getName() + " has left " + previous.getStreetName() + 
						" St. and has entered " + currentStreet.getStreetName() + " St.");
			} else {
				System.out.println("FAIL");
				stop = true;
				return;
			}
			
		}
	}	
	
	public void stopVehicleThread() {
		Main.record(" ");
		Main.record(this.getName() + " reached destination: Node " + this.exitDirection.getName());
		Main.record(" ");
		stop = true;
		vehicleCounter--;
	}
	
	/** set its target position to the position it wants to go in */
	public void moveVehicle(){
		int n= allVehicles.indexOf(this);
		Vehicle v= allVehicles.get(n-1);
		for(Vehicle f= v; f!=null; f=getNext()){
			double x= f.getXpos();
			double y= f.getTargetY() - f.getHeight() - Main.distBetweenCars;
			f.setTargetPointer(x, y);
		}
	}

	public void calculateVelocity(){
		vx= targetPointerX - xPos;
		vy= targetPointerY - yPos;
		normalize(vx, vy);
	}

	public void move(double newX, double newY){
		setX(xPos + newX);
		setY(yPos + newY);
	}
	

	public void setX(double d){
		xPos= d;
	}
	
	public void setY(double d){
		yPos= d;
	}
	
	public void normalize(double velocityX, double velocityY){
		vx = (velocityX)/(Math.sqrt(Math.pow(velocityX, 2)+Math.pow(velocityY,2)));
		vy = (velocityY)/(Math.sqrt(Math.pow(velocityX, 2)+Math.pow(velocityY,2)));
	}
	
	public Vehicle getNext() {
		int i = allVehicles.indexOf(this);
		if (i < allVehicles.size())
			return allVehicles.get(i + 1);
		else {
			return null;
		}
	}
	
	public LinkedList<Street> getPath() {
		return path;
	}
	
	public String printPath(LinkedList<Street> pa){
		String str = "";
        for (Street s : pa){
        	if(s!=null)
        		str += "    --> St. " + s.getStreetName() + " " + s.getPathDirection();
        	else
        		str += "St. is null\n";
		}	
        return str;
	}
	
	/** Dijkstra's Algorithm */
    // Yo Weili, write comments for this, I don't understand what this does
    public void computeShortestPath(Node n, Street excluded) {
        synchronized(this) {
            Hashtable<Node, Double> minDistances = new Hashtable<Node, Double>();
            Hashtable<Node, Street> previousStreets = new Hashtable<Node, Street>();
            HashSet<Node> pathQueue = new HashSet<Node>();
            HashSet<Node> visited = new HashSet<Node>();
            pathQueue.add(n);
            minDistances.put(n, 0.0);
            boolean destinationReached = false;
            while (!destinationReached) {
                Node u = removeShortest(pathQueue, minDistances);
                if	(u == null)
                	return;
                visited.add(u);
                if    (u == exitDirection) {
                    destinationReached = true;
                }
                else {
                    // Visit each edge exiting u
                	//Main.record("Vehicle " + type + index+ " is thinking");
                    for (Street e : u.getAdjacencies()) {
                        if (e != excluded && e != null) {
                            Node end = e.getEnd();
                            if    (!visited.contains(end)) {
                                double weight = e.getTimeDelay();
                                double currentDistance = minDistances.get(u) + weight;
                                if (!minDistances.contains(end) || currentDistance < minDistances.get(end)) {
                                    pathQueue.add(end);
                                    minDistances.put(end, currentDistance);
                                    previousStreets.put(end, e);
                                }
                            }
                        }
                    }
                }
            }
            path = new LinkedList<Street>();
            Street previous = previousStreets.get(exitDirection);
            while (previous != null) {
                path.addFirst(previous);
                previous = previousStreets.get(previous.getStart());
            }
        }
    }
   
    private Node removeShortest(Set<Node> nodes, Map<Node, Double> minDistances) {
        double shortestDist = Integer.MAX_VALUE;
        Node shortestNode = null;
        for    (Node n : nodes) {
            if    (minDistances.get(n) < shortestDist) {
                shortestDist = minDistances.get(n);
                shortestNode = n;
            }
        }
        nodes.remove(shortestNode);
        return shortestNode;
	}
	
	public boolean isWaiting() {
		return isWaiting;
	}
	
	public void setWaiting() {
		isWaiting = true;
	}
	
	public void stopWaiting() {
		isWaiting = false;
	}
    
	public int getPriorityLevel() {
		return priorityLevel;
	}
	
	public Street getCurrentStreet() {
		return currentStreet;
	}
	
	public void setCurrentStreet(Street s) {
		currentStreet = s;
	}

}
