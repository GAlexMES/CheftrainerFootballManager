package de.szut.dqi12.cheftrainer.server.utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.RealTeam;
import de.szut.dqi12.cheftrainer.server.parsing.PlayerParser;
import de.szut.dqi12.cheftrainer.server.parsing.TeamParser;

public class ParserUtils {

	public static String playerRootURL = "http://www.ran.de/datenbank/fussball";

	public static List<RealTeam> getTeamList(String leagueRootURL){
		TeamParser tp = new TeamParser();
		List<RealTeam> teamList = tp.getTeamlist(leagueRootURL);
		PlayerParser pp = new PlayerParser();
		for(RealTeam t : teamList){
			URL teamURL;
			try {
				teamURL = new URL(leagueRootURL + t.getTeamUrl());
				t.setPlayerList(pp.getPlayers(teamURL));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return teamList;
	}

	public static String getPage(URL url) {
		String content = "";
		try {
			content = new Scanner(url.openStream(), "UTF-8")
					.useDelimiter("\\A").next();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getTableOfHTML(String file) {
		String pattern = "(<table>.*<\\/table>)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(file);

		if (m.find()) {
			return m.group(0);
		}
		return null;
	}

	public static List<Element> parseXmlTableString(String xmlString) {
		SAXBuilder saxBuilder = new SAXBuilder();
		List<Element> nodeList = new ArrayList<Element>();
		try {
			Document doc = saxBuilder.build(new StringReader(xmlString));
			nodeList = doc.getRootElement().getChildren();
		} catch (JDOMException e) {
		} catch (IOException e) {
		}
		return nodeList;
	}
}