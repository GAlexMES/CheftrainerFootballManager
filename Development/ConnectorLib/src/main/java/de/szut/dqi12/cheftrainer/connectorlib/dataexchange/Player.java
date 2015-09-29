package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;


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
	
	
	public Player(int worth, String name, int points) {
		super();
		this.worth = worth;
		this.name = name;
		this.points = points;
		goals = 0;
		redCard = false;
		yellowRedCard = false;
	}
	
	public Player(){
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

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
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
	
	

}
