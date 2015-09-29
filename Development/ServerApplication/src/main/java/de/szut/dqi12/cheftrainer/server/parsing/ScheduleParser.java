package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;

public class ScheduleParser {

	private static String sportalBundesligaRoot = "http://www.sportal.de/fussball/bundesliga/";
	private static String sportalRoot = "http://www.sportal.de";
	private String scheduleRoot = sportalRoot
			+ "/fussball/bundesliga/spielplan/spielplan-spieltag-";
	private final static String tableID = "moduleResultContentResultateList";
	private int matchday;
	private int season;
	private List<Match> matches;

	public List<Match> createSchedule(int matchday, int season)
			throws MalformedURLException {
		this.matchday = matchday;
		this.season = season;
		matches = new ArrayList<>();
		URL scheduleURL = new URL(scheduleRoot + matchday + "-saison-" + season
				+ "-" + (season + 1));

		try {
			Document doc = Jsoup.connect(scheduleURL.toString()).get();
			Element scheduleDiv = doc
					.getElementById("moduleResultContentResultateList");
			Elements games = scheduleDiv.getElementsByAttributeValue("class",
					"table_content table_content_wetten");
			for(Element e : games){
				matches.add(createMatch(e));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matches;
	}

	private Match createMatch(Element e) {
		String date = e.select("span[class=date]").text();
		if (matchday < 18) {
			date += String.valueOf(season);
		} else {
			date += String.valueOf(season + 1);
		}
		String time = e.select("span[class=time]").text();
		String home = e.select("li[class=heim]").get(0).select("a")
				.attr("title");
		String guest = e.select("li[class=auswaerts]").get(0).select("a")
				.attr("title");
		String score = e.select("li[class=score]").get(0).select("a").text();
		String detailURL = e.select("li[class=score]").get(0).select("a")
				.attr("href");
		Match m = new Match(date, time, home, guest, score, detailURL);
		return m;
	}
	
	public static int getSportalID(String url) {

		try {
			Document doc = Jsoup.connect(sportalRoot + url).get();
			Element navigation = doc.getElementById("kompaktformat_topnavi");
			Element firstnavigationIcon = navigation.select("a[href]").first();
			String[] hrefParts = firstnavigationIcon.attr("href").split("-");
			String completeID = hrefParts[hrefParts.length - 1];
			return Integer.valueOf(completeID);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	

	public Map<Integer,List<Match>> getMatchesForSeason(int season){
		String url ="http://www.sportal.de/fussball/bundesliga/spielplan/spielplan-chronologisch-saison-";
		url = url + season+"-"+(season+1);
		Map<Integer,List<Match>> retval = new HashMap<>();
		
		try {
			Document doc = Jsoup.connect(sportalBundesligaRoot).get();
			Elements matchDays = doc.getElementById("moduleResultContentResultateList").select("ul[class=table_head_spieltag]");
			for(Element matchDay: matchDays){
				int matchDayID = Integer.valueOf(matchDay.child(0).text().split(Pattern.quote("."))[0]);
				retval.put(matchDayID,new ArrayList<>());
				Element currentMatch = matchDay;
				for(int i = 0; i<9;i++){
					Element match = currentMatch.nextElementSibling();
					Match m = createMatch(match);
					retval.get(matchDayID).add(m);
					currentMatch=match;
				}
			}
		}
		catch(IOException e1){
			
		}
		return retval;
	}

	public int getCurrentSeason() {
		Document doc;
		try {
			doc = Jsoup.connect(sportalBundesligaRoot).get();
			Element navigationBar = doc.getElementById("HeaderMenuBottomSub");
			Elements navigationElements = navigationBar.select("a");
			Element results = null;
			for(Element e : navigationElements){
				if(e.text().equals("Ergebnisse")){
					results = e;
					break;
				}
			}
			String[] splittedHref = results.attr("href").split(Pattern.quote("-"));
			String currentSeason = splittedHref[splittedHref.length-2];
			return Integer.valueOf(currentSeason);
		} catch (IOException e1) {
			return 0;
		}
		
	}
}
