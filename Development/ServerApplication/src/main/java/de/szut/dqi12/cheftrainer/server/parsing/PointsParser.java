package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.util.HashMap;
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

	public static final String TEAM_HOME = "Home";
	public static final String TEAM_GUEST = "Away";
	private static final int HEIGHST_GRADE_POINT = 12;
	private static final int DIF_BETWEEN_HALF_GRADES = 2;

	public Map<String, Map<String, Player>> getPlayerPoints(int season,
			int matchday, int matchID) {
		Map<String, Map<String, Player>> retval = new HashMap<>();
		String compactformURL = getURL(season, matchday, matchID);

		try {
			Document doc = Jsoup.connect(compactformURL).get();

			String homeTeamName = getTeamName(TEAM_HOME, doc);
			Map<String, Player> homeTeam = getPlayersForTeam(TEAM_HOME, doc);
			homeTeam = addTeamToPlayers(homeTeam, homeTeamName);

			String guestTeamName = getTeamName(TEAM_GUEST, doc);
			Map<String, Player> guestTeam = getPlayersForTeam(TEAM_GUEST, doc);
			guestTeam = addTeamToPlayers(guestTeam, guestTeamName);

			retval.put(homeTeamName, homeTeam);
			retval.put(guestTeamName, guestTeam);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return retval;
	}

	private Map<String, Player> addTeamToPlayers(Map<String, Player> team,
			String teamName) {
		for (String s : team.keySet()) {
			team.get(s).setTeamName(teamName);
		}
		return team;
	}

	private Map<String, Player> getPlayersForTeam(String team, Document doc) {
		Element teamOnPitch = doc.select("div[class=spielfeld" + team + "]")
				.first();
		Elements beginnerPlayers = teamOnPitch
				.select("div[class=spielinfoSpielfeldPlayer]");

		Map<String, Player> players = getPlayerOnPitch(beginnerPlayers);
		players.putAll(getReplacementPlayer(team, doc));

		players = addAditionalInformationToPlayers(players, team, doc);

		return players;
	}

	private Map<String, Player> addAditionalInformationToPlayers(
			Map<String, Player> players, String team, Document doc) {

		players = mapAdditionalInformationToPlayer(players,
				getRedCardPlayers(team, doc), "RedCard");
		players = mapAdditionalInformationToPlayer(players,
				getYellowRedCardPlayers(team, doc), "YellowRedCard");
		players = mapAdditionalInformationToPlayer(players,
				getGoalPlayers(team, doc), "Goals");

		return players;
	}

	private Map<String, Player> mapAdditionalInformationToPlayer(
			Map<String, Player> players, Map<String, Integer> information,
			String mapping) {
		for (String s : information.keySet()) {
			if (!players.keySet().contains(s)) {
				players.put(s, new Player(s, 0));
			}
			switch (mapping) {
			case "RedCard":
				players.get(s).setRedCard(true);
				break;
			case "YellowRedCard":
				players.get(s).setYellowRedCard(true);
				break;
			case "Goals":
				players.get(s).setGoals(information.get(s));
				break;
			default:
				System.err.println("Invalid mapping parameter");
			}
		}
		return players;
	}

	private Map<String, Player> getPlayerOnPitch(Elements players) {
		Map<String, Player> retval = new HashMap<>();

		for (Element e : players) {
			Player p = new Player();
			p.setName(e.select("a[class=spielfeld_spielinfo_pos_item_link]")
					.text());
			String gradeAsString = e.select("div[class=note_zahl]").text();
			if (gradeAsString.contains(",")) {
				gradeAsString = gradeAsString.replace(",", ".");
			}
			
			int grade = getGrade(gradeAsString);
			p.setPoints(grade);
			retval.put(p.getName(), p);
		}
		return retval;
	}
	
	private int getGrade(String gradeAsString){
		try {
			Double grade = Double.valueOf(gradeAsString);
			if (gradeAsString.equals("1")) {
				return HEIGHST_GRADE_POINT;
			}
			else{
				
				return(int) (HEIGHST_GRADE_POINT - (((grade - 1)/0.5) * DIF_BETWEEN_HALF_GRADES));
			}
			
		} catch (NumberFormatException exc) {
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	private Map<String, Player> getReplacementPlayer(String team, Document doc) {
		Map<String, Player> retval = new HashMap<>();

		Elements changes = getRowData(doc, team, "Einwechslung");

		for (Element e : changes) {
			String eleText = e.text();
			String playerName = eleText.split("für")[0];
			if (playerName.contains("(")) {
				String gradeAsString = playerName.split(Pattern.quote("("))[1];
				gradeAsString = gradeAsString
						.substring(0, gradeAsString.length() - 2);
				gradeAsString = gradeAsString.replace(",", ".");
				int grade = getGrade(gradeAsString);
				
				playerName = playerName.split(Pattern.quote("("))[0];
				String[] playerNameParts = playerName.split(Pattern.quote("."));
				playerName = "";
				for (int i = 1; i < playerNameParts.length; i++) {
					playerName = playerName + playerNameParts[i];
				}
				playerName = playerName.substring(1, playerName.length() - 1);

				
				Player p = new Player(playerName,grade);
				retval.put(playerName,p);
			}
		}

		return retval;
	}

	private String getTeamName(String team, Document doc) {
		Element teamLogo = doc.select("div[class=logo" + team + "]").first();
		return teamLogo.select("a").attr("title");
	}

	private Map<String, Integer> getGoalPlayers(String team, Document doc) {
		Map<String, Integer> retval = new HashMap<>();
		Elements goals = getRowData(doc, team, "Tore");
		for (Element e : goals) {
			String playerName = e.text().split(Pattern.quote("("))[0];
			playerName = playerName.substring(0, playerName.length() - 1);
			if (retval.keySet().contains(playerName)) {
				int currentGoals = retval.get(playerName);
				retval.put(playerName, currentGoals + 1);
			} else {
				retval.put(playerName, 1);
			}
		}
		return retval;
	}

	private Map<String, Integer> getYellowRedCardPlayers(String team,
			Document doc) {
		Map<String, Integer> retval = new HashMap<>();
		Elements yellowRedCards = getRowData(doc, team, "Gelb-Rote Karten");
		for (Element e : yellowRedCards) {
			String playerName = e.text().split(Pattern.quote("("))[0];
			retval.put(playerName, 0);
		}
		return retval;
	}

	private Map<String, Integer> getRedCardPlayers(String team, Document doc) {
		Map<String, Integer> retval = new HashMap<>();
		Elements redCards = getRowData(doc, team, "Rote Karten");
		for (Element e : redCards) {
			String playerName = e.text().split(Pattern.quote("("))[0];
			retval.put(playerName, 0);
		}
		return retval;
	}

	private Elements getRowData(Document doc, String team, String headerText) {
		String teamAB = "";
		if (team.equals(TEAM_GUEST)) {
			teamAB = "B";
		} else if (team.equals(TEAM_HOME)) {
			teamAB = "A";
		}

		Element table = doc
				.select("li[class=first]:contains(" + headerText + ")").first()
				.parent();
		Element teams = table.nextElementSibling();
		Element data = teams.nextElementSibling();
		Element rowData = data.select("div[class=team" + teamAB + "]").first();
		Elements retval = rowData.select("div[class=headDataRowLiDiv2]");
		return retval;
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
