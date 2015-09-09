package de.szut.dqi12.cheftrainer.server.gamemanagement;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Robin
 *
 */
public class Market {
	private List<Player> players;
	
	public Market(){
		this.players = new ArrayList<Player>();
	}
	
	public void addPlayer(Player player){
		this.players.add(player);
	}
	
	public List<Player> getPlayers(){
		return this.players;
	}

}
