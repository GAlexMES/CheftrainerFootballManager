package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.Calendar;
import java.util.Date;

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
	private String absolutePictureURL;
	private int sportalID;
	private Date birthdate;
		

	private MarketPlayer marketPlayer;

	
	public Player(){
	}
	
	public Player(JSONObject playerJSON){
		getPlayerFromJSON(playerJSON);
		marketPlayer = new MarketPlayer(name, String.valueOf(points), String.valueOf(worth),this);
	}
	
	public Player(int worth, String name, int points, String position) {
		this.worth = worth;
		this.name = name;
		this.points = points;
		
		marketPlayer = new MarketPlayer(name, String.valueOf(points), String.valueOf(worth),this);
		
		goals = 0;
		redCard = false;
		yellowRedCard = false;
	}
	
	public Player(String name, String teamName, int points) {
		this.name = name;
		this.points = points;
		this.teamName = teamName;
		
		marketPlayer = new MarketPlayer(name, String.valueOf(points), "no information available",this);
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
	
	public String getAbsolutePictureURL() {
		return absolutePictureURL;
	}

	public void setAbsolutePictureURL(String absolutePictureURL) {
		this.absolutePictureURL = absolutePictureURL;
	}

	public int getSportalID() {
		return sportalID;
	}

	public void setSportalID(int sportalID) {
		this.sportalID = sportalID;
	}
	
	
	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	
	public void setBirthdate(String birthday) {
		String[] splittedBirthday = birthday.split("\\.");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.valueOf(splittedBirthday[2]));
		cal.set(Calendar.MONTH, Integer.valueOf(splittedBirthday[1]));
		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(splittedBirthday[0]));
		this.birthdate = cal.getTime();
	}


	public JSONObject toJSON() {
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
		marketPlayer = new MarketPlayer(name, String.valueOf(points), "no information available",this);
	}
	
	public PlayerLabel getLabel() {
		return label;
	}

	public void setLabel(PlayerLabel label) {
		this.label = label;
	}

	public boolean isPlays() {
		return plays;
	}
	
	public MarketPlayer getMarketPlayer(){
		return marketPlayer;
	}
}
