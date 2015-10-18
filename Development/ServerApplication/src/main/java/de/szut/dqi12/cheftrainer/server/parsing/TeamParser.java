package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.RealTeam;
import de.szut.dqi12.cheftrainer.server.utils.ParserUtils;

/**
 * The team parses class is used to parse all bundesliga teams from ran.de
 * @author Alexander Brennecke
 *
 */
public class TeamParser {
	public static String rootURL = "http://www.ran.de/datenbank/fussball";
	
	/**
	 * This method uses other methods in this class and in the PlayerParser class to create a List of RealTeams
	 * @return a List of all Teams in the bundesliga. Each RealTeam object contains a List of players, playing n this team.
	 * @throws IOException
	 */
	public static List<RealTeam> getTeams() throws IOException {
		try {
			List<RealTeam> teamList = getTeamlist();
			PlayerParser pp = new PlayerParser();
			for (RealTeam t : teamList) {
				URL teamURL;
				try {
					teamURL = new URL(rootURL + t.getTeamUrl());
					t.setPlayerList(pp.getPlayers(teamURL));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			return teamList;
		} catch (IOException e) {
			throw e;
		}
	}
	
	
	/**
	 * This method ueses the rootURL to parse the ran.de webside and collect all information to create teams.
	 * @return a List of RealTeam objects
	 * @throws IOException
	 */
	private static List<RealTeam> getTeamlist() throws IOException{
		URL teamsURL;
		List<RealTeam> teamList = new ArrayList<RealTeam>();
		try {
			teamsURL = new URL(
					rootURL + "/bundesliga/mannschaften/");
			String pageContent = ParserUtils.getPage(teamsURL);
			String teamsTable = ParserUtils.getTableOfHTML(pageContent);
			List<Element> rootChilds = ParserUtils.parseXmlTableString(teamsTable);
			teamList = parseNodeList(rootChilds);
		} catch (IOException e ) {
			throw e;
		}
		return teamList;
	}
	
	/**
	 * This method tries to parse the given Element List and creates a RealTeam object for each element in the list.
	 * @param nodeList a List of created RealTeam objects
	 * @return
	 */
	private static List<RealTeam> parseNodeList(List<Element> nodeList){
		List<RealTeam> teamList = new ArrayList<RealTeam>();
		for(Element e : nodeList){
			Element trTag = e.getChildren().get(1);
			Element aTag = trTag.getChild("a");
			RealTeam tempTeam = new RealTeam();
			tempTeam.setTeamName(aTag.getText());
			tempTeam.setTeamUrl(aTag.getAttributeValue("href"));
			teamList.add(tempTeam);
		}
		return teamList;
	}

}