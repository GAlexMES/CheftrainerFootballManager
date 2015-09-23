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
	
	
	public Player(int worth, String name, int points) {
		super();
		this.worth = worth;
		this.name = name;
		this.points = points;
	}
	
	public Player(){
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
