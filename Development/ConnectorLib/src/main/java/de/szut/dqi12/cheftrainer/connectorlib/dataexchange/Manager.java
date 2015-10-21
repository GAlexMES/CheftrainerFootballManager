package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;

/**
 * 
 * @author Robin
 *
 */
public class Manager {

	private int id;
	private String name;
	private Double money;
	private Integer teamWorth;
	private List<Player> players;
	private List<Player> lineUp;
	private Formation formation;
	private int points;
	private int rang;
	private List<Transaction> transactions;
	
	private final SimpleStringProperty communityNameProperty;
	private final SimpleStringProperty teamWorthProperty;
	private final SimpleStringProperty rangProperty;

    public Manager() {
    	this(null,null,null,null);
    }

	public Manager(String managerName, Integer teamWorth, Integer rang, String communityName){
		communityNameProperty =  new SimpleStringProperty(communityName);
		teamWorthProperty =  new SimpleStringProperty(teamWorth+"€");
		rangProperty =  new SimpleStringProperty(String.valueOf(rang));
		
		this.name =managerName;
		this.teamWorth =teamWorth;
		this.rang= rang;
		this.players = new ArrayList<Player>();
		this.lineUp = new ArrayList<Player>();
	}

	
	private String formatDouble(Double d){
		NumberFormat f = NumberFormat.getInstance();
		f.setGroupingUsed(false);
		return f.format(d);
	}

	public void addTransaction(Transaction transaction) {
		this.transactions.add(transaction);
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public Formation getFormation() {
		return formation;
	}

	public void setFormation(Formation formation) {
		this.formation = formation;
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
		}
	}

	public List<Player> getLineUp() {
		return lineUp;
	}

	public void setLineUp(ArrayList<Player> lineUp) {
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

	public Integer getTeamWorth() {
		return teamWorth;
	}
	
	public void setTeamWorth(Integer teamWorth){
		this.teamWorth = teamWorth;
	}

	public SimpleStringProperty getCommunityNameProperty() {
		return communityNameProperty;
	}

	public SimpleStringProperty getTeamWorthProperty() {
		return teamWorthProperty;
	}

	public SimpleStringProperty getRangProperty() {
		return rangProperty;
	}

}
