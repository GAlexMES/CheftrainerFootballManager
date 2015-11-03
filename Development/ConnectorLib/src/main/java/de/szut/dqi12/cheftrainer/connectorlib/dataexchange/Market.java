package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

/**
 * 
 * @author Robin
 *
 */
public class Market {
	private List<Player> players;

	public Market() {
		this.players = new ArrayList<Player>();
	}

	
	public Market(JSONArray playerList) {
		this.players = new ArrayList<Player>();
		for (int i = 0; i < playerList.length(); i++) {
			Player p = new Player();
			p.getPlayerFromJSON(playerList.getJSONObject(i));
			players.add(p);
		}
	}

	public void addPlayer(Player... players) {
		for (Player p : players) {
			this.players.add(p);
		}
	}

	public List<Player> getPlayers() {
		return this.players;
	}
	
	public JSONArray toJSON(){
		JSONArray playerList = new JSONArray();
		for(Player p: players){
			playerList.put(p.toJSON());
		}
		return playerList;
	}

}
