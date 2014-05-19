import java.util.*;

public class Node implements Runnable {
	
	private String intersectionName;
	// Streets that come from another node to this node
	private Street left;
	private Street right;
	private Street up;
	private Street down;
	
	private int intersectionNumber; // number assigned to this intersection
	private static int totalNumNodes; // total number of Nodes
	private static ArrayList<Node> allNodes= new ArrayList<Node>(); // array of all nodes
	//array of streets that start at this node and go to another node
	private Street[] adjacencies= new Street[4]; // [left, up, right, down]
	
	private long switchTime;
	private boolean upDown=true; // true if the up/down street cars can move into intersection, false if left/right 
	
	public double minDistance = Double.POSITIVE_INFINITY;
	//private static Street[] adjacencies;
	private Node previous; // previous node?
	
	// For GUI
	private int xPosition; // x position
	private int yPosition; // y position
	
	
	
	public Node(int nodeNum/**, int xpos, int ypos */){
		intersectionNumber=nodeNum;
		intersectionName= "" + intersectionNumber;
		intersectionName=nodeNum + "";
		switchTime= (long)(Math.random()*10000 + 10000); //TODO changed this
		/**xPosition=xpos;
		yPosition=ypos; */
		totalNumNodes++;
		Main.record("Intersection " + intersectionName + " has been created.");
	}
	
	//TODO
	public String getIntName(){
		return intersectionName;
	}
	
	//TODO: what does this do?
	/**
	public Node(String name, Street l, Street r, Street u, Street d) {
		intersectionName = name;
		left = l;
		right = r;
		up = u;
		down = d;
		for (int i = 0; i < 4; i++) {
			switch (i) {
				case 1: adjacencies[1] = left;
				case 2: adjacencies[2] = up;
				case 3: adjacencies[3] = right;
				case 4: adjacencies[4] = down;
			}
		}
	} */
	
	public void run(){
		boolean upDown;
		int random= (int)(Math.random()*2);
		if(random==0)
			upDown=true;
		else
			upDown=false;
		while(Vehicle.getVehCounter()>0){
			try {
				upDown=!upDown;  // changes the flow of traffic with each call
				if(upDown){
					if(up!=null)   // has an up
						up.setGo(true);
					if(down!=null)  // has a down
						down.setGo(true);
					if(left!=null)
						left.setGo(false);
					if(right!=null)
						right.setGo(false);
					Main.record("Intersection " + this.getName()+ " --- Greenlight: up/down, Redlight: left/right");
				}
				else{
					if(left!=null) 
						left.setGo(true);
					if(right!=null)
						right.setGo(true);
					if(up!=null)
						up.setGo(false);
					if(down!=null)
						down.setGo(false);
					Main.record("Intersection " + this.getName()+ " --- Redlight: up/down, Greenlight: left/right");
				}
				Thread.sleep(switchTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	
	
	public static int getNumNodes(){
		return totalNumNodes;
	}

	public boolean isUpDown() {
		return upDown;
	}
	
	// Input name of Node (a number) and returns that Node
	public static Node findNode(int intersectionNum){
		return allNodes.get(intersectionNum);
	}
	
	public int getIntersectionNumber(){
		return intersectionNumber;
	}
	
	public int getXPos(){
		return xPosition;
	}
	
	//TODO must be deleted before handing in
	public void setXPos(int n){
		xPosition= n;
	}
	
	public int getYPos(){
		return yPosition;
	}
	
	//TODO must be deleted before handing in
	public void setYPos(int n){
		yPosition= n;
	}	
	
	public static ArrayList<Node> getNodeArray(){
		return allNodes;
	}
	
	public Street getInLeft(){
		return left;
	}
	
	public Street getInRight(){
		return right;
	}
	
	public void setInLeft(Street left){
		this.left= left;
	}
	
	public void setInRight(Street right){
		this.right= right;
	}
	
	public void setInUp(Street up){
		this.up= up;
	}
	
	public void setInDown(Street down){
		this.down= down;
	}
	
	public void setOutLeft(Street left){
		adjacencies[0]= left;
	}
	
	public void setOutRight(Street right){
		adjacencies[2]= right;
	}
	
	public void setOutUp(Street up){
		adjacencies[1]= up;
	}
	
	public void setOutDown(Street down){
		adjacencies[3]= down;
	}
	
	public String getName() {
		return intersectionName;
	}
	
	public Street[] getAdjacencies() {
		return adjacencies;
	}
	
	public void moveCars() {
		down.getFront();
	}
	
	public int numStreets() {
		int m=0;
		for(int k=0; k<adjacencies.length; k++){
			if(adjacencies[k]!=null)
				m++;
		}
		return m;
	}
}
