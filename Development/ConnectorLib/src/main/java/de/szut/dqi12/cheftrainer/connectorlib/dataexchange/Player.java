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
	private String positionString;
	private Position position;
	private int ID;
	private int goals;
	private boolean redCard;
	private boolean yellowRedCard;
	private String teamName;
	private boolean plays;
		
	
	public Player(){
	}
	
	public Player(JSONObject playerJSON){
		getPlayerFromJSON(playerJSON);
	}
	
	public Player(int worth, String name, int points, Position position) {
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

	public String getPositionString() {
		return positionString;
	}

	public void setPosition(String position) {
		this.positionString = position;
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
	
	public Position getPosition() {
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
		return this.plays();
	}
	
	public JSONObject getJSONFromPlayer() {
		JSONObject retval = new JSONObject();
		retval.put("name", this.getName());
		retval.put("id", this.getID());
		retval.put("number", this.getNumber());
		retval.put("points", this.getPoints());
		retval.put("worth", this.getWorth());
		retval.put("position", this.getPositionString());
		retval.put("team", this.getTeamName());
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
	}
	

}
