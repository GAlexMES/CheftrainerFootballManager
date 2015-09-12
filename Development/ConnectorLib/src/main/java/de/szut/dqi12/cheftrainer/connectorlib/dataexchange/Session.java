package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.HashMap;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ClientHandler;

public class Session {

	private int userID;
	private User user;
	private Client clientSocket;

	private ClientHandler clientHandler;

	private HashMap<Integer, Community> communityMap;

	public Session() {
		communityMap = new HashMap<>();
	}


	public void addCommunity(Community community) {
		communityMap.put(community.getCommunityID(), community);
	}
	
	public void addCommunities(List<Community> communities){
		for(Community c : communities){
			communityMap.put(c.getCommunityID(),c);
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		userID = user.getUserID();
		this.user = user;
	}

	public Client getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Client clientSocket) {
		this.clientSocket = clientSocket;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public HashMap<Integer, Community> getCommunityMap() {
		return communityMap;
	}

	public ClientHandler getClientHandler() {
		return clientHandler;
	}

	public void setClientHandler(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}
}
