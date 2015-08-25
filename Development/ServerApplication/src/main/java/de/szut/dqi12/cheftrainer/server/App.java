package de.szut.dqi12.cheftrainer.server;

import de.szut.dqi12.cheftrainer.server.UserCommunication.ServerController;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	ServerController conServer = new ServerController();
    	conServer.createServer();
    }
}
