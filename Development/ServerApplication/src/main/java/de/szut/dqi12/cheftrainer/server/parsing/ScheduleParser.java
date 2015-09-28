package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.server.utils.ParserUtils;

public class ScheduleParser {

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
			games.forEach(e -> createMatch(e));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matches;
	}

	private void createMatch(Element e) {
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
		matches.add(m);
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
}
