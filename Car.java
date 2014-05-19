import java.awt.Color;


public class Car extends Vehicle {
	//TODO a temporary boolean that I used. not necesary
	public boolean temp= false;
	
	public Car(Main GUI) {
		super(1, Color.cyan, 10, 10, "Car", GUI);
	}
	
	public boolean isTemp() {
		return temp;
	}
	
	public void setTemp(boolean temp) {
		this.temp = temp;
	}
}
