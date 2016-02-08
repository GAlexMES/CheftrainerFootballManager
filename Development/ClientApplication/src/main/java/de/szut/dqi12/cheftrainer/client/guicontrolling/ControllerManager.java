package de.szut.dqi12.cheftrainer.client.guicontrolling;

import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;

/**
 * The ControllerManager is used to fire Events on the registered controllers. 
 * The main usage is, when a new message from the server receives at the client and a direct GUI response is needed.
 *
 */
public class ControllerManager {

	private static ControllerManager instance = null;
	private Map<String, ControllerInterface> actionList;

	/**
	 * Function for singleton pattern
	 * @return a {@link ControllerManager} instance.
	 */
	public static ControllerManager getInstance() {
		if (instance == null) {
			instance = new ControllerManager();
		}
		return instance;
	}

	/**
	 * Constructor
	 */
	private ControllerManager() {
		actionList = new HashMap<String, ControllerInterface>();
	}

	/**
	 * This function is used to register a new controller in the {@link ControllerManager}.
	 * @param ci the controller, that should be registered
	 * @param onAction the event IDs, on which the controller should be notified
	 * @return
	 */
	public boolean registerController(ControllerInterface ci, String... onAction){
		for (String s : onAction) {
			if (actionList.keySet().contains(onAction)) {
				InstanceAlreadyExistsException iaee =  new InstanceAlreadyExistsException(
						"One or more of your actions are already declared for a other Controller. Please use a other name! ('"+s+"')");
				iaee.printStackTrace();
				return false;
			}
		}
		
		for (String s: onAction){
			actionList.put(s, ci);
		}
		return true;
	}
	
	/**
	 * Should be called, when a controller should be initialized.
	 * The controller has to be registered with the registerController method first.
	 * @param key the controller, which is registered with this key will be notified
	 */
	public void onAction(String key){
		onAction(key,true);
	}
	
	/**
	 * Should be called, when a controller should be initialized.
	 * The controller has to be registered with the registerController method first.
	 * @param key the controller, which is registered with this key will be notified via the messageArrived function
	 * @param flag the flag will be given directly to the controller via the messageArrived function.
	 */
	public void onAction(String key, Boolean flag){
		actionList.get(key).messageArrived(flag);
	}
}
