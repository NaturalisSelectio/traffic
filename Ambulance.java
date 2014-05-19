import java.awt.Color;
import java.util.*;


public class Ambulance extends Vehicle {
	private boolean onAlert;

	public Ambulance(Main GUI) {
		super(4, Color.red, 10, 10, "Ambulance", GUI);
	}
	
	public boolean getIsEmergency() {
		return onAlert;
	}
	
	public void stopVehicleThread() {
		super.stopVehicleThread();
		onAlert = false;
		currentStreet.ambulanceNotAlert();
	}
	
	public void setEmergency() {
		onAlert = true;
	}
	
	public void moveFrontVehicle() {
		synchronized(this) {
			if (path!=null && path.size() > 0){
				Street previous = currentStreet;	// record previous street
				currentStreet = path.removeFirst();	// find next street
				currentStreet.addVehicle(this);
				if (getIsEmergency()) {
					currentStreet.ambulanceAlert();
					previous.ambulanceNotAlert();
					currentStreet.moveVehicleToFront(this);
				}
				Main.record(this.getName() + " has left " + previous.getStreetName() + 
						" St. and has entered " + currentStreet.getStreetName() + " St.");
			} else {
				System.out.println("FAIL");
				stop = true;
				return;
			}
			
		}
	}
	
	public void run() {	
		while (!stop) {
			Random generator = new Random();
			double pty = generator.nextDouble();
			
			// declare an emergency
			if (!getIsEmergency() && pty < .02){
				this.setEmergency();
				currentStreet.ambulanceAlert();
				currentStreet.moveVehicleToFront(this);
			}
			//System.out.println(path);
			if (!path.isEmpty() && path.getFirst().isMax() && currentStreet.getEnd().numStreets() > 1 && !getIsEmergency()) {
				computeShortestPath(currentStreet.getEnd(), path.getFirst());
				Main.record(printPath(path));
				//System.out.println(exitDirection.getIntName());
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { System.out.println("FAIL"); }
		}
	}

	
}

