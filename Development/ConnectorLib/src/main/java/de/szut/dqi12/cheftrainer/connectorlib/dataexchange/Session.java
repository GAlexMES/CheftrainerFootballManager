package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ClientHandler;

/**
 * This class is used to save session specific attributes. It can be used on server and client side.
 * @author Alexander Brennecke
 *
 */
public class Session {

	private int userID;
	private User user;
	private Client clientSocket;

	private ClientHandler clientHandler;

	private HashMap<Integer, Community> communityMap;

	public Session() {
		communityMap = new HashMap<>();
	}

	public void updateCommunities(List<Community> communities){
		communityMap = new HashMap<>();
		addCommunities(communities);
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

	/**
	 * Should only be used on the client side.
	 * @return
	 */
	public Client getClientSocket() {
		return clientSocket;
	}

	/**
	 * Should only be used on the client side.
	 * @return
	 */
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
	
	public List<Community> getCommunities(){
		List<Community> retval = new ArrayList<Community>();
		for(Integer s : communityMap.keySet()){
			retval.add(communityMap.get(s));
		}
		return retval;
	}
	

	/**
	 * Should only be used on the server side.
	 * @return
	 */
	public ClientHandler getClientHandler() {
		return clientHandler;
	}


	/**
	 * Should only be used on the server side.
	 * @return
	 */
	public void setClientHandler(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}
}
