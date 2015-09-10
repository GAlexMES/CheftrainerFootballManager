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
	
	
	public Player(Double worth, String name, int points) {
		super();
		this.worth = worth;
		this.name = name;
		this.points = points;
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
