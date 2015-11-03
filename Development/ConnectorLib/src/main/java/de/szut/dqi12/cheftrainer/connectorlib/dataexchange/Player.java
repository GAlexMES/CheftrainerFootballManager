package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import org.json.JSONObject;


/**
 * 
 * @author Robin
 *
 */
public class Player {
	private int worth;
	private String name;
	private int points;
	private int number;
	private String position;
	private int ID;
	private int goals;
	private boolean redCard;
	private boolean yellowRedCard;
	private String teamName;
	private boolean plays;
	private PlayerLabel label;
		
	
	public PlayerLabel getLabel() {
		return label;
	}

	public void setLabel(PlayerLabel label) {
		this.label = label;
	}

	public boolean isPlays() {
		return plays;
	}

	public Player(){
	}
	
	public Player(JSONObject playerJSON){
		getPlayerFromJSON(playerJSON);
	}
	
	public Player(int worth, String name, int points, String position) {
		this.worth = worth;
		this.name = name;
		this.points = points;
		goals = 0;
		redCard = false;
		yellowRedCard = false;
	}
	
	public Player(String name, String teamName, int points) {
		this.name = name;
		this.points = points;
		this.teamName = teamName;
	}
	
	public Player(String name, int points) {
		this.name = name;
		this.points = points;
	}
	
	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public int getGoals() {
		return goals;
	}

	public void setGoals(int goals) {
		this.goals = goals;
	}

	public boolean isRedCard() {
		return redCard;
	}

	public void setRedCard(boolean redCard) {
		this.redCard = redCard;
	}

	public boolean isYellowRedCard() {
		return yellowRedCard;
	}

	public void setYellowRedCard(boolean yellowRedCard) {
		this.yellowRedCard = yellowRedCard;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getWorth() {
		return worth;
	}
	
	public String getPosition() {
		return position;
	}

	public void setWorth(int worth) {
		this.worth = worth;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}

	public void setPlays(boolean plays) {
		this.plays = plays;
	}
	
	public boolean plays(){
		return this.plays;
	}
	
	public void setPosition(String position){
		this.position = position;
	}
	
	public JSONObject getJSONFromPlayer() {
		JSONObject retval = new JSONObject();
		retval.put("name", this.getName());
		retval.put("id", this.getID());
		retval.put("number", this.getNumber());
		retval.put("points", this.getPoints());
		retval.put("worth", this.getWorth());
		retval.put("position", this.getPosition());
		retval.put("team", this.getTeamName());
		retval.put("plays", this.plays());
		return retval;
	}
	
	public void getPlayerFromJSON(JSONObject playerJSON) {
		this.setName(playerJSON.getString("name"));
		this.setID(playerJSON.getInt("id"));
		this.setNumber(playerJSON.getInt("number"));
		this.setPoints(playerJSON.getInt("points"));
		this.setWorth(playerJSON.getInt("worth"));
		this.setPosition(playerJSON.getString("position"));
		this.setTeamName(playerJSON.getString("team"));
		this.setPlays(playerJSON.getBoolean("plays"));
	}
	

}
