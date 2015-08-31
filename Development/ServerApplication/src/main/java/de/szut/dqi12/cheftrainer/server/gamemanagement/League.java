package de.szut.dqi12.cheftrainer.server.gamemanagement;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Robin
 *
 */
public class League {

	private List<User> users;
	private Market market;
	
	public League(Market market){
		users = new ArrayList<User>();
		this.market = market;
	}
	
	public void addUser(User user){
		this.users.add(user);
	}

	public List<User> getUsers() {
		return users;
	}

	public Market getMarket() {
		return market;
	}
	
	
	


	
}
