package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.RealTeam;
import de.szut.dqi12.cheftrainer.server.utils.ParserUtils;


public class TeamParser {
	
	public List<RealTeam> getTeamlist(String rootURL) throws IOException{
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
	
	private List<RealTeam> parseNodeList(List<Element> nodeList){
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