package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import org.json.JSONObject;

public class User {
	
	String firstName = "";
	String lastName = "";
	String userName = "";
	String eMail = "";
	String password = "";
	
	int userID;
	
	public void setWithJSON(JSONObject json){
		firstName = json.getString("vorname");
		lastName = json.getString("nachname");
		eMail = json.getString("mail");
		userName = json.getString("login");
		password = json.getString("password");
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getUserID(){
		return userID;
	}
	public void setUserId(int ID){
		this.userID=ID;
	}
	public String getAllForSQL(){
		String retval = "'"+firstName +"', '" + lastName +"', '" + userName +"', '" + eMail +"', '" + password+"'";
		return retval;
	}
	
	

}
