package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Robin
 *
 */
public class Community {

	private int communityID;
	private String name;
	private List<Manager> managers;
	private Market market;
	
	public Community(){
		managers= new ArrayList<>();
	}
	
	public Community(Market market){
		managers = new ArrayList<Manager>();
		this.market = market;
	}
	
	public void addManager(Manager user){
		this.managers.add(user);
	}
	
	public void addManagers(List<Manager> managerList){
		managers.addAll(managerList);
	}

	public List<Manager> getmanagers() {
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
