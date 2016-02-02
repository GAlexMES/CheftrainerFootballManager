package de.szut.dqi12.cheftrainer.client.guicontrolling;

/**
 * Interface for FXMLControllers, which ar Components for the graphical user interface. 
 *
 */
public interface ControllerInterface {
	/**
	 * Initialisation of graphical components
	 */
	public void init();
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
}
