package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Robin
 *
 */
public class Manager {

	private String name;
	private Double money;
	private List<Player> players;
	private List<String> lineUp;
	private int points;
	private List<Transaction> transactions;
	
	public Manager(String name, Double money, int points){
		this.name = name;
		this.money = money;
		this.points = points;
		this.players = new ArrayList<Player>();
		this.lineUp = new ArrayList<String>();
	}
	
	public void addTransaction(Transaction transaction){
		this.transactions.add(transaction);
	}
		
	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public void addPlayer(Player player){
		this.players.add(player);
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
	
	
}
