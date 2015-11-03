package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;

public class ControllerManager {

	private static ControllerManager instance = null;
	private Map<String, ControllerInterface> actionList;

	public static ControllerManager getInstance() {
		if (instance == null) {
			instance = new ControllerManager();
		}
		return instance;
	}

	private ControllerManager() {
		actionList = new HashMap<String, ControllerInterface>();
	}

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
	
	public void onAction(String key){
		actionList.get(key).messageArrived();
	}
}
