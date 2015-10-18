package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

public class Match {
	
	private int matchDay;
	private int season;
	private String date;
	private String time;
	private String home;
	private String guest;
	private String detailURL;
	private int goalsHome;
	private int goalsGuest;
	private int sportalMatchID;
	
	public Match(String date, String time, String home, String guest,
			String score, String detailURL) {
		this.date = date;
		this.time=time;
		this.home=home;
		this.guest=guest;
		this.detailURL=detailURL;
		try{
		this.goalsHome = Integer.valueOf(score.split(":")[0]);
		this.goalsGuest = Integer.valueOf(score.split(":")[1]);
		}
		catch(Exception e){
			this.goalsHome=-1;
			this.goalsGuest=-1;
		}
	}
	public int getSportalMatchID() {
		return sportalMatchID;
	}
	public void setSportalMatchID(int sportalMatchID) {
		this.sportalMatchID = sportalMatchID;
	}
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
	public int getMatchDay() {
		return matchDay;
	}
	public void setMatchDay(int matchDay) {
		this.matchDay = matchDay;
	}
	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
	}

	
}
