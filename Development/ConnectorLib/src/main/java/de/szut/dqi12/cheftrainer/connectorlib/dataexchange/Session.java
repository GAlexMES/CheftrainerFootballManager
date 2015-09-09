package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import de.szut.dqi12.cheftrainer.connectorlib.clientside.Client;

public class Session {

	private String loginName;
	private Client clientSocket;
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
