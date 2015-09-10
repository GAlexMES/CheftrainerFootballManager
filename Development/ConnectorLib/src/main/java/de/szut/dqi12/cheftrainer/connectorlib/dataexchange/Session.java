package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.util.HashMap;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;

public class Session {

	private String loginName;
	private Client clientSocket;
	private HashMap<String,Community> communityMap;
	
	public Session(){
		communityMap = new HashMap<>();
	}
	
	public void setCommunitiesName(List<String> names){
		for(String s : names){
			communityMap.put(s, new Community());
		}
	}
	
	public void addCommunity(Community community){
		communityMap.put(community.getName(), community);
	}
	
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public Client getClientSocket() {
		return clientSocket;
	}
	public void setClientSocket(Client clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	
}
