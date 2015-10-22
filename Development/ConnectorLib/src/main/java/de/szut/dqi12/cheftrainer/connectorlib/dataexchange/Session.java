package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;
import de.szut.dqi12.cheftrainer.connectorlib.serverside.ClientHandler;

/**
 * This class is used to save session specific attributes. It can be used on
 * server and client side.
 * 
 * @author Alexander Brennecke
 *
 */
public class Session {

	private int userID;
	private User user;
	private Client clientSocket;
	private int currentManagerID;
	private int currentCommunityID;

	private ClientHandler clientHandler;
	
	private ObservableList<Manager> managerTableData= FXCollections.observableArrayList();

	private HashMap<Integer, Community> communityIDMap;
	private HashMap<String, Community> communityNameMap;

	public int getCurrentManagerID() {
		return currentManagerID;
	}
	
	public void setCurrentManager(Manager currentManager) {
		setCurrentManager(currentManager.getID());
		Community currentCommunity = communityNameMap.get(currentManager.getCommunityNameProperty());
		currentCommunityID = currentCommunity.getCommunityID();
	}

	public void setCurrentManager(int currentManager) {
		this.currentManagerID = currentManager;
	}

	public int getCurrentCommunityID() {
		return currentCommunityID;
	}

	public void setCurrentCommunityID(int currentCommunity) {
		this.currentCommunityID = currentCommunity;
	}

	public Session() {
		communityIDMap = new HashMap<>();
		communityNameMap = new HashMap<>();
	}
	
	public Community getCurrentCommunity(){
		return communityIDMap.get(currentCommunityID);
	}
	
	public Community getCommunity(Integer id){
		return communityIDMap.get(id);
	}
	
	public Community getCommunity(String name){
		return communityNameMap.get(name);
	}
	
	public void updateCommunities(List<Community> communities) {
		communityIDMap = new HashMap<>();
		addCommunities(communities);
	}

	public void addCommunity(Community community) {
		communityIDMap.put(community.getCommunityID(), community);
		communityNameMap.put(community.getName(), community);
		managerTableData.add(community.getUsersManager());
	}

	public void addCommunities(List<Community> communities) {
		for (Community c : communities) {
			addCommunity(c);
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
	 * 
	 * @return
	 */
	public Client getClientSocket() {
		return clientSocket;
	}

	/**
	 * Should only be used on the client side.
	 * 
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

	public HashMap<Integer, Community> getCommunityIDMap() {
		return communityIDMap;
	}
	
	public HashMap<String, Community> getCommunityNameMap() {
		return communityNameMap;
	}
	
	public List<Community> getCommunities(){
		List<Community> retval = new ArrayList<Community>();
		for(Integer s : communityIDMap.keySet()){
			retval.add(communityIDMap.get(s));
		}
		return retval;
	}

	public ObservableList<Manager> getManagerObservable() {
		return managerTableData;
	}

	public void setManagerTableData(ObservableList<Manager> managerTableData) {
		this.managerTableData = managerTableData;
	}

	/**
	 * Should only be used on the server side.
	 * 
	 * @return
	 */
	public ClientHandler getClientHandler() {
		return clientHandler;
	}

	/**
	 * Should only be used on the server side.
	 * 
	 * @return
	 */
	public void setClientHandler(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}
}
