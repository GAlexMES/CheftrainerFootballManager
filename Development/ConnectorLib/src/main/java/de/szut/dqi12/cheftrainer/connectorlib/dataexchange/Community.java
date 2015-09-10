package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Robin
 *
 */
public class Community {

	private String name;
	private List<Manager> users;
	private Market market;
	
	public Community(){
		
	}
	
	public Community(Market market){
		users = new ArrayList<Manager>();
		this.market = market;
	}
	
	public void addUser(Manager user){
		this.users.add(user);
	}

	public List<Manager> getUsers() {
		return users;
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
	
}
