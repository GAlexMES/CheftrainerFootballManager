package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

public class Formation {
	
	private int defenders;
	private int middfielders;
	private int offensives;
	private String name;
	
	public Formation(String name, int defenders, int middfielders, int offensives){
		this.name = name;
		this.defenders = defenders;
		this.middfielders = middfielders;
		this.offensives = offensives;
	}
	
	public int getDefenders() {
		return defenders;
	}
	public void setDefenders(int defenders) {
		this.defenders = defenders;
	}
	public int getMiddfielders() {
		return middfielders;
	}
	public void setMiddfielders(int middfielders) {
		this.middfielders = middfielders;
	}
	public int getOffensives() {
		return offensives;
	}
	public void setOffensives(int offensives) {
		this.offensives = offensives;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
