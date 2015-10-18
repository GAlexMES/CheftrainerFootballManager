package de.brennecke.alexander.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import Parsers.PlayerParser;
import Parsers.TeamParser;

/**
 * Hello world!
 *
 */
public class App {
	
	private static String rootURL = "http://www.ran.de/datenbank/fussball";
	
	public static void main(String[] args) {
		TeamParser tp = new TeamParser();
		List<Team> teamList = tp.getTeamlist(rootURL);
		PlayerParser pp = new PlayerParser();
		for(Team t : teamList){
			URL teamURL;
			try {
				teamURL = new URL(rootURL + t.getTeamUrl());
				t.setPlayerList(pp.getPlayers(teamURL));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}


}