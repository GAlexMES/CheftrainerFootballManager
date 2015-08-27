package de.szut.dqi12.cheftrainer.connectorlib.clientside;

import java.net.URL;

/**
 * The ClientProperties class is used to save properties, which are required to generate a client socket connection to a server socket.
 * @author Alexander Brennecke
 *
 */
public class ClientProperties {
	private String server_ip;
	private int port;
	private URL pathToCallableDir;
	private String packagePathToCallableDir;
	
	// GETTER AND SETTER
	public String getServer_ip() {
		return server_ip;
	}
	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public URL getPathToCallableDir() {
		return pathToCallableDir;
	}
	public void setPathToCallableDir(URL pathToCallableDir) {
		this.pathToCallableDir = pathToCallableDir;
	}
	public String getPackagePathToCallableDir() {
		return packagePathToCallableDir;
	}
	public void setPackagePathToCallableDir(String packagePathToCallableDir) {
		this.packagePathToCallableDir = packagePathToCallableDir;
	}
}
