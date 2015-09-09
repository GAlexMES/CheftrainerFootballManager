package de.szut.dqi12.cheftrainer.connectorlib.clientside;

import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;

/**
 * The ClientProperties class is used to save properties, which are required to generate a client socket connection to a server socket.
 * @author Alexander Brennecke
 *
 */
public class ClientProperties {
	private String serverIP;
	private int port;
	private List<IDClass_Path_Mapper> idMappers = new ArrayList<IDClass_Path_Mapper>();
	
	// GETTER AND SETTER
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void addClassPathMapper(IDClass_Path_Mapper idCLassPathMapper){
		idMappers.add(idCLassPathMapper);
	}
	public List<IDClass_Path_Mapper> getIDMappers(){
		return idMappers;
	}
}
