package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.RealTeam;

/**
 * The team parses class is used to parse all bundesliga teams from ran.de
 * @author Alexander Brennecke
 *
 */
public class TeamParser {
	public static String rootURL = "http://www.sportal.de";
	
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
					String teamURL = rootURL + t.getTeamUrl();
					t.setPlayerList(pp.getPlayers(teamURL));
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
		List<RealTeam> teamList = new ArrayList<RealTeam>();
		try {
			String teamsURL = rootURL + "/fussball/bundesliga/vereine/";
			Document doc = Jsoup.connect(teamsURL).get();
			teamList = getTeams(doc);
		} catch (IOException e ) {
			throw e;
		}
		return teamList;
	}
	
	private static List<RealTeam> getTeams(Document doc){
		List<RealTeam> retval = new ArrayList<>();
		Element teamTable = doc.getElementById("subpageContent");
		Elements teams = teamTable.select("div[class=right] > div[class*=vereineRow");
		for(Element e : teams){
			retval.add(getTeam(e));
		}
		return retval;
	}
	
	private static RealTeam getTeam(Element e){
		RealTeam retval = new RealTeam();
		Element infoDiv = e.getElementsByAttributeValue("class", "info").first();
		
		String logoURL = e.getElementsByTag("img").get(0).attr("src");
		retval.setLogoURL(rootURL+logoURL);
		
		
		String xPathToName ="h2 a";
		String teamName = infoDiv.select(xPathToName).text();
		retval.setTeamName(teamName);
		
		String xPathToTeamURL = "a[title^=Kader]";
		String teamURL =infoDiv.select(xPathToTeamURL).attr("href");
		retval.setTeamUrl(teamURL);
		
		return retval;
	}
}