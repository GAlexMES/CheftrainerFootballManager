package de.szut.dqi12.cheftrainer.connectorlib.serverside;

import java.net.URL;

public class ServerProperties {
	private int port;
	private URL pathToCallableDir;
	private String packagePathToCallableDir;
	
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
