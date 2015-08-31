package de.szut.dqi12.cheftrainer.connectorlib.serverside;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.messages.IDClass_Path_Mapper;

/**
 * The ServerProperties class is used to save properties, which are required to generate a server socket.
 * @author Alexander Brennecke
 *
 */
public class ServerProperties {
	private int port;
	private List<IDClass_Path_Mapper> idMappers = new ArrayList<IDClass_Path_Mapper>();
	
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
