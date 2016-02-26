package de.szut.dqi12.cheftrainer.client.guicontrolling;

import javafx.scene.Scene;

/**
 * Interface for FXMLControllers, which are Components for the graphical user interface. 
 *
 */
public interface ControllerInterface {
	/**
	 * Initialisation of graphical components
	 */
	public void init(double width, double height);
	/**
	 * This method is called, when the enterbutton is pressed on the Component.
	 * The main method of the component will call.
	 */
	public void enterPressed();
	/**
	 * This Method is called, when the client arrives an message from the server.
	 * @param flag 
	 */
	public void messageArrived(Boolean flag);
	
	public void initializationFinihed(Scene scene);
	
	public void resize(double sizeDifferent);
}
