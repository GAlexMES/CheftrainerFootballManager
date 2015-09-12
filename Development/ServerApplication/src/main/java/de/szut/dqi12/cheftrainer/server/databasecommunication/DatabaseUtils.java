package de.szut.dqi12.cheftrainer.server.databasecommunication;

import java.util.HashMap;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Community;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.User;

public class DatabaseUtils {
	
	private static DatabaseUtils INSTANCE = null;
	private SQLConnection sqlCon;
	
	
	//DATABASE MANAGERS
	private static UserManagement userManagement;
	private static CommunityManagement communityManagement;
	
	public static DatabaseUtils getInstance(){
		if(INSTANCE==null){
			INSTANCE = new DatabaseUtils();
		}
		return INSTANCE;
	}
	
	public void setSQLConnection(SQLConnection sqlCon){
		this.sqlCon=sqlCon;
		userManagement = new UserManagement(sqlCon);
		communityManagement = new CommunityManagement(sqlCon);
	}

	
	public static HashMap<String, Boolean> registerNewUser(User newUser){
		return userManagement.register(newUser);
	}
	
	public static HashMap<String, Boolean> loginUser(User user){
		return userManagement.login(user);
	}
	
	public static User getUserData(String userName){
		return userManagement.getUserValues(userName);
	}
	
	public static boolean createNewCommunity(String name, String password, int adminID){
		return communityManagement.createNewCommunity(name,password, adminID);
	}
	
	public static List<Community> getCummunitiesForUser(int userID){
		return communityManagement.getCummunities(userID);
	}
}
