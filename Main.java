import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.*;

// TODO: randomize destination node



public class Main extends Component implements ActionListener{
	private static final long serialVersionUID = 1L;
	public final static int DELAY_TIME = 4000;
	private static JFrame frame;
	private JButton button;
	private JTextField fileInput;
	private static String fileName;
	public static int numIntersections; // number of intersections you want on this map
	public static int startX=100; // the first Node's starting X position
	public static int startY=100; // the first Node's starting Y position
	public static int nodeGap=20; // distance between nodes horizontally and vertically
	public static int nodeLength= 50; // square length of the node
	public static int maxStreets; // maximum number of streets
	public static int maxNodes; // maximum number of Nodes
	public static int numVehicles;//number of vehicles you want on this map
	//public static int maxStreets; // maximum number of streets
	public static int maxVehicles;
	
	public static final int streetWidth= 400; // street rectangle width
	public static final int streetLengths= 800; //street rectangle height
	public static int streetYPosition = 50;
	public static final int distBetweenCars = 20; // y distance between customers
	
	
	//Boxes for different use
	private Box b0 = new Box(BoxLayout.X_AXIS);
	private Box b1 = new Box(BoxLayout.X_AXIS);
	private Box b2 = new Box(BoxLayout.X_AXIS);
	
	/** This is the main method that starts running the program.*/
	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable(){ 
			public void run(){
				createAndShowGUI();
			}
		});
	}
	
	public int getStreetYPosition() {
		return streetYPosition;
	}
	
	/** = Returns how big your square of intersections must be in terms of how many nodes
	 * there will be on the side. 
	 * For example: if you have 4 intersections, will return a value of 2 because will make 
	 * a square with 2 intersections on each side. If you have 5 intersections will still 
	 * return level 2 */
	public static int howBigSquare(int numNodes){
		int level=0;
		if(numNodes==0)
			return level;
		while(numNodes-((level+1)*(level+1))>=0){
			level++;
		}
		return level;
	}
	
	public void createNodes(int numNodes){
		int level= howBigSquare(numNodes);
		//Node.setNodeArray(numIntersections);
		ArrayList<Node> allNodes= Node.getNodeArray();
		//Main.record("NOde array length is " + allNodes.getLength());
		int n=0;
		int column=0;
		while(n!=numNodes){
			int xpos= startX;
			int ypos= startY;
			for(int t=0; t<level; t++){
				xpos= startX + column*nodeGap;
				ypos= startY + t*nodeGap;
				allNodes.add(new Node(n));
				if(t==level-1){
					t= -1;
					column++;
				}
				n++;
				if(n==numNodes){
					Main.record(" ");
					Main.record(" ");
					createStreets(allNodes, numNodes, level, column);
					return;
				}
			}
		} 
		
	}
	
	
	public void createStreets(ArrayList<Node> node, int numNodes, int level, int columns){

        int n=0;
        while(n<numNodes){
            Street left, right, up, down= null;
            for(int r=0; ; r++){
                if(r!=0){
                    // new Street (start node, end node (this node), name of street, orientation, inList?)
                    left= new Street(node.get(n-level), node.get(n), "" + (n-level) + "-" + n, "right", -1, 1, this);
                    node.get(n).setInLeft(left);
                    // Street that starts at this node and goes somewhere else
                    node.get(n-level).setOutRight(left);
                }
                if(/**r!=columns-1 && */(n+level)<numNodes){
                    right= new Street(node.get(n+level), node.get(n), "" + (n+level) + "-" + n, "left", -1, 1, this);
                    node.get(n).setInRight(right);
                    node.get(n+level).setOutLeft(right);
                }
                if(n%level!=0){

                    up= new Street(node.get(n-1), node.get(n), "" + (n-1) + "-" + n, "down", 1, 1, this);
                    node.get(n).setInUp(up);
                    node.get(n-1).setOutDown(up);
                }
                if(n%level!=level-1 && (n+1)<numNodes){

                    down= new Street(node.get(n+1), node.get(n), "" + (n+1) + "-" + n, "up", 1, 1, this);
                    node.get(n).setInDown(down);
                    node.get(n+1).setOutUp(down);
                }
                n++;
                if(n<level)
                    r=-1;
                if(n==numNodes)
                    return;
            }
        }
    }

	
	// create vehicles
	public void createVehicles(int numVehicles){
		ArrayList<Vehicle> v= Vehicle.getVehicleArray();
		for(int k=0; k<numVehicles; k++){
			double number= Math.random()*4;
			if(number>=0 && number<1)
				v.add(new Car(this));
			if(number>=1 && number<2)
				v.add(new Ambulance(this));
			if(number>=2 && number<3)
				v.add(new Bus(this));
			if(number>=3 && number<=4)
				v.add(new Bicycle(this));
		}
	}
	
	public static void createAndShowGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("Selection Menu");
		Main window = new Main();
		Component contents = window.createComponents();
		frame.getContentPane().add(contents, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
        frame.setVisible(true);
	}
	
	public Component createComponents() {
		JPanel pane = new JPanel(new GridLayout(3, 0));
		pane.add(b0, BorderLayout.NORTH);
		pane.add(b1, BorderLayout.CENTER);
		pane.add(b2, BorderLayout.SOUTH);
		JLabel nodePrompt = new JLabel("  Select the number of intersections (nodes) you would like.  ");
		JLabel vehiclePrompt = new JLabel("  Select the number of vehicles you would like.  ");
		b0.add(nodePrompt);
		String[] nodeNumber = {"2", "3", "4", "5", "6", "7", "8", "9", "10"};
		String[] vehicleNumber = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
		JComboBox numberList = new JComboBox(nodeNumber); //Create drop-down menu, select position 0
		numberList.setSelectedIndex(0);
		numIntersections = 2;
		numberList.addActionListener(this);
		numberList.setVisible(true);
		numberList.setActionCommand("nodes");
		b0.add(numberList);
		b0.add(vehiclePrompt);
		JComboBox vehicles = new JComboBox(vehicleNumber);
		vehicles.setSelectedIndex(0);
		maxVehicles = 1;
		vehicles.addActionListener(this);
		vehicles.setVisible(true);
		vehicles.setActionCommand("vehicles");
		b0.add(vehicles);
		JLabel filePrompt = new JLabel("  Type the name of the file you would like to write to.  ");
		b1.add(filePrompt, BorderLayout.WEST);
		fileInput = new JTextField(20);
		fileInput.setActionCommand("file");
		b1.add(fileInput, BorderLayout.EAST);
		button = new JButton("Start!");
		button.addActionListener(this);
		button.setVisible(true);
		b2.add(button);
		return pane;	
	}

	public void printDirections(){
		Main.record("THE CITY");
		Main.record(" by Nora Ng-Quinn, Jacqueline Chien, Weili Shi, and Victor Bautista");
		Main.record(" ");
		Main.record("YOUR INPUT:");
		Main.record("Number of Intersections = " + numIntersections);
		Main.record("Number of Vehicles = " + maxVehicles);
		Main.record(" ");
		Main.record("INFORMATION: ");
		Main.record("Each intersection has 4 streets connected to its: left, right, up, down");
		Main.record("Intersections are given numbers as their identifiers. ");
		Main.record("Example: Intersection 7");
		Main.record("Vehicles are specified by type and number. No two vehicles can have the same number");
		Main.record("Example: Car 3, Ambulance 0, Bike 7");
		Main.record("Streets are named by the Intersection is starts with and ends with, and the direction it travels from the start to end.");
		Main.record("Example: Street 4-6 UP starts at intersection 4 and goes UP to intersection 6");
		Main.record(" ");
		Main.record(" ");
		Main.record(" ");
	}
	
	public void createSecondGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Traffic Map"); // create and setup window frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(50,50);
		frame.setSize(1200,800/**WIDTH, HEIGHT*/); // set width and height
		Container contentPane= frame.getContentPane();
		contentPane.setLayout(new GridLayout(1, 1));
		printDirections();
		createNodes(numIntersections);
		Main.record(" ");
		Main.record(" ");
		createVehicles(maxVehicles);
		Main.record(" ");
		Main.record(" ");
		contentPane.add(this);
		frame.setVisible(true);
		startThreads();
	}
	
	public void startThreads(){
		// Notify file that have started moving threads
		Main.record("START MOVING");
		Main.record(" ");
		Main.record(" ");
		Main.record(" ");
		
		// start Node/Intersection threads
		ArrayList<Node> n= Node.getNodeArray();
		for(int k=0; k<n.size(); k++){
			Node one= n.get(k);
			new Thread(one).start();
		}
		
		// start Street threads
		ArrayList<Street> s= Street.getStreetArray();
		for(int m=0; m<s.size(); m++){
			Street two= s.get(m);
			new Thread(two).start();
		}
		
		// start Vehicle threads
		ArrayList<Vehicle> v= Vehicle.getVehicleArray();
		for(int z=0; z<v.size(); z++){
			Vehicle three= v.get(z);
			new Thread(three).start();
		}
	}
	
	// paint all vehicles, streets, and intersections
	public void paint(Graphics g) {
		// paint intersections/Nodes black
		ArrayList<Node> n= Node.getNodeArray();
		g.setColor(Color.black);
		for(int k=0; k<Node.getNumNodes(); k++){
			g.fillRect(n.get(k).getXPos(), n.get(k).getYPos(), nodeLength, nodeLength);
		}
		
		// paint streets blue
		g.setColor(Color.blue);
		ArrayList<Street> s= Street.getStreetArray();
		for(int m=0; m<Street.getNumStreets(); m++){
			g.fillRect(s.get(m).getXpos(), s.get(m).getYpos(), s.get(m).getWidth(), s.get(m).getHeight());
		}
		
		// paint vehicles
		ArrayList<Vehicle> v= Vehicle.getVehicleArray();
		for(int z=0; z<Vehicle.getNumVehicles(); z++){
			g.setColor(v.get(z).getColor());
			g.fillOval((int)v.get(z).getXpos(), (int)v.get(z).getYpos(), v.get(z).getWidth(), v.get(z).getHeight());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "nodes") {
			JComboBox cb = (JComboBox)e.getSource();
		    String number = (String) cb.getSelectedItem();
		    cb.setSelectedItem(number);
		    numIntersections = Integer.parseInt(number);
		}
		if (e.getActionCommand() == "vehicles") {
			JComboBox cb = (JComboBox)e.getSource();
		    String number = (String) cb.getSelectedItem();
		    cb.setSelectedItem(number);
		    maxVehicles = Integer.parseInt(number);
		}
		if (e.getActionCommand() == "Start!") {
			fileName = fileInput.getText();
			System.out.println(fileName);
			try {
				FileOutputStream writer = new FileOutputStream(fileName); 
		        PrintStream scribble = new PrintStream(writer);
		        writer.close();
			} catch (IOException i) {
				
			}
			frame.setVisible(false);
			createSecondGUI();
		}
	}
	
	
	public synchronized static void record(String message){
		System.out.println(message);
		try{
			FileOutputStream writer = new FileOutputStream(fileName, true); 
	        PrintStream scribble = new PrintStream(writer);
			scribble.println(message);
			writer.close();
			//System.out.print(message);
		}
		catch(IOException e){
		}
	}
}
