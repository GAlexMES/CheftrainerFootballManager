package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;


/**
 * 
 * @author Robin
 *
 */
public class Player {
	private Double worth;
	private String name;
	private int points;
	private Position position;
	
	
	
	public Player(Double worth, String name, int points, Position position) {
		super();
		this.worth = worth;
		this.name = name;
		this.points = points;
		this.position = position;
	}
	
	
	public Position getPosition() {
		return position;
	}


	public Double getWorth() {
		return worth;
	}
	public void setWorth(Double worth) {
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
