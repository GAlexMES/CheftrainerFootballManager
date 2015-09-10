package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.HashMap;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ClientHandler;

public class Session {

	private long userID;
	private User user;
	private Client clientSocket;

	private ClientHandler clientHandler;

	private HashMap<String, Community> communityMap;

	public Session() {
		communityMap = new HashMap<>();
	}

	public void setCommunitiesName(List<String> names) {
		for (String s : names) {
			communityMap.put(s, new Community());
		}
	}

	public void addCommunity(Community community) {
		communityMap.put(community.getName(), community);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Client getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Client clientSocket) {
		this.clientSocket = clientSocket;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public HashMap<String, Community> getCommunityMap() {
		return communityMap;
	}

	public ClientHandler getClientHandler() {
		return clientHandler;
	}

	public void setClientHandler(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}
}
