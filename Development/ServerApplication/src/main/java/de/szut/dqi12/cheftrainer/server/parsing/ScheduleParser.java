package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.server.utils.ParserUtils;

public class ScheduleParser {

	private String scheduleRoot = "http://www.sportal.de/fussball/bundesliga/spielplan/spielplan-spieltag-";
	private final static String tableID = "moduleResultContentResultateList";
	private int matchday;
	private int season;

	private Map<String,List<String>> gamedayInformation;

	public List<Match> createSchedule(int matchday, int season)
			throws MalformedURLException {
		this.matchday = matchday;
		this.season = season;
		URL scheduleURL = new URL(scheduleRoot + matchday + "-saison-" + season
				+ "-" + (season + 1));
		gamedayInformation = new HashMap<>();
		List<Match> matches = new ArrayList<>();
		try {
			String pageContent = ParserUtils.getPage(scheduleURL);

			List<Element> nodeList = new ArrayList<Element>();

			gamedayInformation.put("date",findeTagsInHTML(
					("<span class=\"date\">(.*?)</span>"), pageContent, 0, 7));
			gamedayInformation.put("time",findeTagsInHTML(
					("<span class=\"time\">(.*?)</span>"), pageContent, 0, 7));
			gamedayInformation.put("home",findeTagsInHTML(
					("<li class=\"heim\">(.*?)</li>"), pageContent, 1, 8));
			gamedayInformation.put("score",findeTagsInHTML(
					("<li class=\"score\">(.*?)</li>"), pageContent, 1, 8));
			gamedayInformation.put("guest",findeTagsInHTML(
					("<li class=\"auswaerts\">(.*?)</li>"), pageContent, 1, 8));

			for (int i = 0; i < 7; i++) {
				matches.add(createMatchForIndex(i));
			}
		} catch (IOException e) {
		}
		return matches;
	}

	private Match createMatchForIndex(int i) {
		Match retval = new Match();
		if (matchday < 18) {
			retval.setDate(gamedayInformation.get("date").get(i) + season);
		} else {
			retval.setDate(gamedayInformation.get("date").get(i) + (season + 1));
		}
		retval.setTime(gamedayInformation.get("time").get(i));
		retval.setHome(getTeamName(gamedayInformation.get("home").get(i)));
		retval.setGuest(getTeamName(gamedayInformation.get("guest").get(i)));
		retval.setDetailURL(getDetailURL(gamedayInformation.get("score").get(i)));
		String score = getScore(gamedayInformation.get("score").get(i));
		retval.setGoalsHome(Integer.valueOf(score.split(":")[0]));
		retval.setGoalsGuest(Integer.valueOf(score.split(":")[1]));
		return retval;
	}
	
	private String getScore(String htmlPhrase) {
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document doc = saxBuilder.build(new StringReader("<html>/n"+htmlPhrase+"/n</html>"));
			String teamName = doc.getRootElement().getChildren("a").get(0).getText();
			return teamName;
		} catch (JDOMException | IOException e) {
				 e.printStackTrace();
		}
		return null;
	}

	private String getDetailURL(String htmlPhrase) {
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document doc = saxBuilder.build(new StringReader("<html>/n"+htmlPhrase+"/n</html>"));
			String teamName = doc.getRootElement().getChildren("a").get(0).getAttribute("href").getValue();
			return teamName;
		} catch (JDOMException | IOException e) {
				 e.printStackTrace();
		}
		return null;
	}

	private String getTeamName(String htmlPhrase){
		 try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document doc = saxBuilder.build(new StringReader("<html>/n"+htmlPhrase+"/n</html>"));
			String teamName = doc.getRootElement().getChild("a").getAttribute("title").getValue();
			return teamName;
		} catch (JDOMException | IOException e) {
				 e.printStackTrace();
		}
		 return null;
	}

	private List<String> findeTagsInHTML(String pattern, String source,
			int beginIndex, int endIndex) {
		List<String> retval = new ArrayList<>();
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(source);

		for (int i = 0; i < endIndex; i++) {
			if (m.find()) {
				if (i >= beginIndex) {
					retval.add(m.group(1));
				}
			}
		}
		return retval;
	}
}
