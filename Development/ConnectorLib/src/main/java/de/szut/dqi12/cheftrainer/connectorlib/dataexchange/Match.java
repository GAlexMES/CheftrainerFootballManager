package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

public class Match {
	
	private String date;
	private String time;
	private String home;
	private String guest;
	private String detailURL;
	private int goalsHome;
	private int goalsGuest;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public String getGuest() {
		return guest;
	}
	public void setGuest(String guest) {
		this.guest = guest;
	}
	public String getDetailURL() {
		return detailURL;
	}
	public void setDetailURL(String detailURL) {
		this.detailURL = detailURL;
	}
	public int getGoalsHome() {
		return goalsHome;
	}
	public void setGoalsHome(int goalsHome) {
		this.goalsHome = goalsHome;
	}
	public int getGoalsGuest() {
		return goalsGuest;
	}
	public void setGoalsGuest(int goalsGuest) {
		this.goalsGuest = goalsGuest;
	}

	
}
