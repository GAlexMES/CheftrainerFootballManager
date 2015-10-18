package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.server.ServerApplication;

/**
 * This class should be used to parse information, which are required for a matchday, a match or a season
 * @author Alexander Brennecke
 *
 */
public class ScheduleParser {

	private static String sportalBundesligaRoot = "http://www.sportal.de/fussball/bundesliga/";
	private static String sportalRoot = "http://www.sportal.de";
	private String scheduleRoot = sportalRoot
			+ "/fussball/bundesliga/spielplan/spielplan-spieltag-";
	private int matchday;
	private List<Match> matches;

	private final static Logger LOGGER = Logger.getLogger(ScheduleParser.class);
	
	/**
	 * This method parses the information for the given matchday in the given season to Match objects.
	 * @param matchday the matchday (should be 1-34 for bundesliga)
	 * @param season use 2015 for season 2015-2016
	 * @return a List of all Matches, that are part of this matchday
	 * @throws MalformedURLException
	 */
	public List<Match> createSchedule(int matchday, int season)
			throws MalformedURLException {
		this.matchday = matchday;
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
				matches.add(createMatch(e,season));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matches;
	}

	/**
	 * Parses a HTML Element to a Match object
	 * @param e the HTML Element from sportal.de
	 * @return a new Match object
	 */
	private Match createMatch(Element e, int season) {
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
	
	/**
	 * This method returns the ID of a match.
	 * @param url must be a URL to a sportal detail side of a match
	 * @return the id of the match as int
	 */
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
		}catch(NullPointerException npe){
			boolean isPage404 = isPageSportal404(url);
			if(isPage404){
				return -1;
			}
			else{
				boolean wrongContent = hasSportalPageWrongContent(url);
				if(wrongContent){
					return -1;
				}
				npe.printStackTrace();
			}
		}
		return -1;
	}
	
	/**
	 * This function checks, if the sportal webside has the navigation bar, to find the id of the game.
	 * @param url the url of a game detail page on sportal.
	 * @return true = webside has wrong content.
	 */
	private static boolean hasSportalPageWrongContent(String url){
		try{
			Document doc = Jsoup.connect(sportalRoot + url).get();
			Element impressumContent = doc.getElementById("kompaktformat_topnavi");
			if(impressumContent==null){
				LOGGER.error("The page of the given game URL  was invalid! It was: "+url);
				return true;
			}
			return false;
		} catch(NullPointerException npe){
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * This function checks, if the given URL is a sportal 404 page.
	 * @param url the url, that should be checked.
	 * @return true = it is a 404 Error page.
	 */
	private static boolean isPageSportal404(String url){
		try{
			Document doc = Jsoup.connect(sportalRoot + url).get();
			Element impressumContent = doc.getElementById("impressumContent");
			String text = impressumContent.getElementsByTag("h1").text();
			if(text.equals("Ooops, diese Seite…")){
				LOGGER.error("The URL of a game was invalid! It results in a 404. It was: "+url);
				return true;
			}
			return false;
		} catch(NullPointerException npe){
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This method parses the whole schedule for a season-
	 * @param season use 2015 for season 2015-2016
	 * @return a Map, where the key is a matchday and returns a List of Matches for that matchday
	 */
	public Map<Integer,List<Match>> getMatchesForSeason(int season){
		String url =sportalBundesligaRoot+"spielplan/spielplan-chronologisch-saison-";
		url = url + season+"-"+(season+1);
		Map<Integer,List<Match>> retval = new HashMap<>();
		
		try {
			Document doc = Jsoup.connect(url).get();
			Elements matchDays = doc.getElementById("moduleResultContentResultateList").select("ul[class=table_head_spieltag]");
			for(Element matchDay: matchDays){
				int matchDayID = Integer.valueOf(matchDay.child(0).text().split(Pattern.quote("."))[0]);
				retval.put(matchDayID,new ArrayList<>());
				Element currentMatch = matchDay;
				for(int i = 0; i<9;i++){
					Element match = currentMatch.nextElementSibling();
					Match m = createMatch(match, season);
					m.setSeason(season);
					m.setMatchDay(matchDayID);
					retval.get(matchDayID).add(m);
					currentMatch=match;
				}
			}
		}
		catch(IOException e1){
			
		}
		return retval;
	}

	/**
	 * This method parses the sportal.de webside t find the current season.
	 * @return the current season as int. Will return 2015 for season 2015-2016
	 */
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
