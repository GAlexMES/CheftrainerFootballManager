package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Robin
 *
 */
public class Community {

	private int communityID;
	private String name;
	private List<Manager> managers;
	private Map<Integer,Manager> managerDictionary;
	private Market market;
	private Manager usersManager;
	
	
	public Community(){
		managers= new ArrayList<>();
		managerDictionary = new HashMap<Integer, Manager>();
	}
	
	public void findeUsersManager(String userName){
		for(Integer i: managerDictionary.keySet()){
			if(managerDictionary.get(i).getName().equals(userName)){
				usersManager = managerDictionary.get(i);
				break;
			}
		}
	}
	
	public Community(Market market){
		managers = new ArrayList<Manager>();
		this.market = market;
	}
	
	public void addManager(Manager manager){
		this.managers.add(manager);
		managerDictionary.put(manager.getID(), manager);
	}
	
	public void addManagers(List<Manager> managerList){
		managers.addAll(managerList);
		for(Manager m : managerList){
			managerDictionary.put(m.getID(), m);
		}
	}
	
	public void setUsersManager(Manager m){
		this.usersManager=m;
	}
	public Manager getUsersManager(){
		return usersManager;
	}
	public Manager getManager(int ID){
		return managerDictionary.get(ID);
	}

	public List<Manager> getManagers() {
		return managers;
	}

	public Market getMarket() {
		return market;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCommunityID() {
		return communityID;
	}

	public void setCommunityID(int communityID) {
		this.communityID = communityID;
	}
	
}
