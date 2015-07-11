package Parsers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import de.brennecke.alexander.Test.Team;

public class TeamParser {
	
	public List<Team> getTeamlist(String rootURL){
		URL teamsURL;
		List<Team> teamList = new ArrayList<Team>();
		try {
			teamsURL = new URL(
					rootURL + "/bundesliga/mannschaften/");
			String pageContent = Parser.getPage(teamsURL);
			String teamsTable = Parser.getTableOfHTML(pageContent);
			List<Element> rootChilds = Parser.parseXmlTableString(teamsTable);
			teamList = parseNodeList(rootChilds);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return teamList;
	}
	
	private List<Team> parseNodeList(List<Element> nodeList){
		List<Team> teamList = new ArrayList<Team>();
		for(Element e : nodeList){
			Element trTag = e.getChildren().get(1);
			Element aTag = trTag.getChild("a");
			Team tempTeam = new Team();
			tempTeam.setTeamName(aTag.getText());
			tempTeam.setTeamUrl(aTag.getAttributeValue("href"));
			teamList.add(tempTeam);
		}
		return teamList;
	}

}