package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;

public class PointsParser {

	private static final String root = "http://www.sportal.de/includes/kompaktformat/index_frame_full.php";
	private static final String appendix = "?league=1bundesliga&page=spielinfo&gameday=%GAMEDAY%&season=%SEASON%&matchid=%MATCHID%";

	private static final String TEAM_HOME = "Home";
	private static final String TEAM_GUEST = "Away";
	private static Map<Double, Integer> noten;

	public static Map<String, List<Player>> getPlayerPoints(int season,
			int matchday, int matchID) {
		init();
		Map<String, List<Player>> retval = new HashMap<>();
		String compactformURL = getURL(season, matchday, matchID);

		try {
			Document doc = Jsoup.connect(compactformURL).get();

			retval.put(TEAM_HOME, getPlayersForTeam(TEAM_HOME, doc));
			retval.put(TEAM_GUEST, getPlayersForTeam(TEAM_GUEST, doc));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return retval;
	}

	private static List<Player> getPlayersForTeam(String team, Document doc) {
		Element teamOnPitch = doc.select("div[class=spielfeld" + team + "]")
				.first();
		Elements beginnerPlayers = teamOnPitch
				.select("div[class=spielinfoSpielfeldPlayer]");

		Map<String, Player> players = getPlayerOnPitch(beginnerPlayers);
		players.putAll(getReplacementPlayer(team, doc));

		List<Player> retval = new ArrayList<>();
		for (String s : players.keySet()) {
			retval.add(players.get(s));
		}
		return null;
	}

	private static Map<String, Player> getReplacementPlayer(String team,
			Document doc) {
		Map<String, Player> retval = new HashMap<>();
		String teamAB = "";
		if (team.equals(TEAM_GUEST)) {
			teamAB = "B";
		} else if (team.equals(TEAM_HOME)) {
			teamAB = "A";
		}

		Element table = doc.select("li[class=first]:contains(Einwechslung)")
				.first().parent();
		Element teams = table.nextElementSibling();
		Element data = teams.nextElementSibling();
		Element teamChanges = data.select("div[class=team" + teamAB + "]")
				.first();
		Elements changes = teamChanges.select("div[class=headDataRowLiDiv2]");

		for (Element e : changes) {
			String eleText = e.text();
			String playerName = eleText.split("für")[0];
			if (playerName.contains("(")) {
				String grade = playerName.split(Pattern.quote("("))[1];
				grade = grade.substring(0, grade.length() - 2);
				grade = grade.replace(",", ".");

				playerName = playerName.split(Pattern.quote("("))[0];
				String[] playerNameParts = playerName.split(Pattern.quote("."));
				playerName = "";
				for (int i = 1; i < playerNameParts.length; i++) {
					playerName = playerName + playerNameParts[i];
				}
				playerName = playerName.substring(1, playerName.length() - 1);
				System.out.println(playerName);
			}
		}

		return retval;
	}

	private static Map<String, Player> getPlayerOnPitch(Elements players) {
		Map<String, Player> retval = new HashMap<>();

		for (Element e : players) {
			Player p = new Player();
			p.setName(e.select("a[class=spielfeld_spielinfo_pos_item_link]")
					.text());
			String gradeAsString = e.select("div[class=note_zahl]").text();
			if (gradeAsString.contains(",")) {
				gradeAsString = gradeAsString.replace(",", ".");
			}
			try{
				Double grade = Double.valueOf(gradeAsString);
				p.setPoints(noten.get(grade));
				retval.put(p.getName(), p);
			}
			catch(NumberFormatException exc){
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}

		return retval;
	}

	private static void init() {
		if (noten == null) {
			noten = new HashMap<>();
			noten.put(1.0, 12);
			noten.put(1.5, 10);
			noten.put(2.0, 8);
			noten.put(2.5, 6);
			noten.put(3.0, 4);
			noten.put(3.5, 2);
			noten.put(4.0, 0);
			noten.put(4.5, -2);
			noten.put(5.0, -4);
			noten.put(5.5, -6);
			noten.put(6.0, -8);
		}
	}

	private static String getURL(int season, int matchday, int matchID) {
		String parsedSeason = String.valueOf(season);
		parsedSeason = parsedSeason.substring(2, parsedSeason.length());
		parsedSeason = parsedSeason
				+ String.valueOf(Integer.valueOf(parsedSeason) + 1);

		String filledAppendix = appendix.replace("%GAMEDAY%",
				String.valueOf(matchday));
		filledAppendix = filledAppendix.replace("%SEASON%", parsedSeason);
		filledAppendix = filledAppendix.replace("%MATCHID%",
				String.valueOf(matchID).substring(2));

		return root + filledAppendix;
	}
}
