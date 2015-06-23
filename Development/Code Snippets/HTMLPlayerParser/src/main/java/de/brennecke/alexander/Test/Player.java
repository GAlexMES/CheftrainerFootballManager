package de.brennecke.alexander.Test;

public class Player {

	private String name;
	private Team team;
	private String position;
	private int number; 
	
	
	public Player(){
		
	}
	
	public Player (Team t){
		this.team=t;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	
	
}
