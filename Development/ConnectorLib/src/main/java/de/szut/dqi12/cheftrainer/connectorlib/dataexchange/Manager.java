package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Robin
 *
 */
public class Manager {

	private int id;
	private String name;
	private Double money;
	private List<Player> players;
	private List<String> lineUp;
	private int points;
	private List<Transaction> transactions;
	private int teamWorth;

	public Manager(String name, Double money, int points) {
		this.name = name;
		this.money = money;
		this.points = points;
		this.players = new ArrayList<Player>();
		this.lineUp = new ArrayList<String>();
	}

	public void addTransaction(Transaction transaction) {
		this.transactions.add(transaction);
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public void addPlayer(List<Player> players){
		Player[] playerArray = players.toArray(new Player[players.size()]);
		addPlayer(playerArray);
	}
	
	public void addPlayer(Player... player) {
		for (Player p : player) {
			this.players.add(p);
			teamWorth += p.getWorth();
		}
	}

	public List<String> getLineUp() {
		return lineUp;
	}

	public void setLineUp(ArrayList<String> lineUp) {
		this.lineUp = lineUp;
	}

	public String getName() {
		return name;
	}

	public Double getMoney() {
		return money;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public int getPoints() {
		return points;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getTeamWorth() {
		return teamWorth;
	}
	
	public void setTeamWorth(int teamWorth){
		this.teamWorth = teamWorth;
	}

}
