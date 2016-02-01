package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.log4j.Logger;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Match;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;

/**
 * The PointParser class is used to create a new URL to a sportal webside, from
 * which the points of all players, that realy played, can be parsed.
 * 
 * @author Alexander Brennecke
 *
 */
public class PointsParser {

	private static final String root = "http://www.sportal.de/includes/kompaktformat/index_frame_full.php";
	private static final String appendix = "?league=1bundesliga&page=spielinfo&gameday=%GAMEDAY%&season=%SEASON%&matchid=%MATCHID%";

	public static final String TEAM_HOME = "Home";
	public static final String TEAM_GUEST = "Away";

	private static final int HEIGHST_GRADE_POINT = 12;
	private static final int DIF_BETWEEN_HALF_GRADES = 2;
	public static final int POINTS_RED_CARD = -6;
	public static final int POINTS_YELLOW_RED_CARD = -3;
	public static final int POINTS_GOAL_KEEPER = 6;
	public static final int POINTS_GOAL_DEFENDER = 5;
	public static final int POINTS_GOAL_MIDDFIELDER = 4;
	public static final int POINTS_GOAL_OFFENSIVE = 3;
	public static final int POINTS_GOAL_PENALTY = -6;

	private final static Logger LOGGER = Logger.getLogger(PointsParser.class);

	public static HashMap<String, HashMap<String, Player>> getPlayerPoints(Match m) {
		return getPlayerPoints(m.getSeason(), m.getMatchDay(), m.getSportalMatchID());
	}

	/**
	 * This method is used, to get the points of all players for a specific
	 * match at a specific matchday in a specific season.
	 * 
	 * @param season
	 *            the season, use 2015 for season 2015-2016
	 * @param matchday
	 *            the matchday, use 1-34 for the bundesliga
	 * @param matchID
	 *            tha matchID, is a unique ID, that was created by sportal
	 * @return a Map, that contains a key for each team of the given season.
	 *         Each key has a new Map, where the keys are the Player names and
	 *         the value is a player object.
	 */
	public static HashMap<String, HashMap<String, Player>> getPlayerPoints(int season, int matchday, int matchID) {
		HashMap<String, HashMap<String, Player>> retval = new HashMap<>();
		String compactformURL = getURL(season, matchday, matchID);

		try {
			Document doc = Jsoup.connect(compactformURL).get();

			String homeTeamName = getTeamName(TEAM_HOME, doc);
			HashMap<String, Player> homeTeam = getPlayersForTeam(TEAM_HOME, doc);
			homeTeam = addTeamToPlayers(homeTeam, homeTeamName);

			String guestTeamName = getTeamName(TEAM_GUEST, doc);
			HashMap<String, Player> guestTeam = getPlayersForTeam(TEAM_GUEST, doc);
			guestTeam = addTeamToPlayers(guestTeam, guestTeamName);

			retval.put(homeTeamName, homeTeam);
			retval.put(guestTeamName, guestTeam);

		} catch (SocketTimeoutException ste) {
			LOGGER.error("The following URL was not reachable: " + compactformURL);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return retval;
	}

	/**
	 * Sets the given team name to all players of the given map.
	 * 
	 * @param team
	 *            a Map of players, that plays in the given team
	 * @param teamName
	 *            the name of the team, that will be set to all players in the
	 *            team
	 * @return the team list with modified values
	 */
	private static HashMap<String, Player> addTeamToPlayers(HashMap<String, Player> team, String teamName) {
		for (String s : team.keySet()) {
			team.get(s).setTeamName(teamName);
		}
		return team;
	}

	/**
	 * Creates a map of all Players, that played for the Home/Guest team and
	 * received points
	 * 
	 * @param team
	 *            Should be TEAM_HOME or TEAM_GUEST
	 * @param doc
	 *            the Document of the player grades webside from sportal
	 * @return a map with all players, that received points and plays in the
	 *         given team
	 */
	private static HashMap<String, Player> getPlayersForTeam(String team, Document doc) {
		Element teamOnPitch = doc.select("div[class=spielfeld" + team + "]").first();
		Elements beginnerPlayers = teamOnPitch.select("div[class=spielinfoSpielfeldPlayer]");

		HashMap<String, Player> players = getPlayerOnPitch(beginnerPlayers);
		players.putAll(getReplacementPlayer(team, doc));

		players = addAditionalInformationToPlayers(players, team, doc);

		return players;
	}

	/**
	 * Adds Yellow-Red Card, Red Card and Goals to the player and creates new
	 * Player Object, for players, that have no grade but a additional
	 * information
	 * 
	 * @param players
	 *            a Map of players, that should get the additional data
	 * @param team
	 *            should be TEAM_HOME or TEAM_GUEST
	 * @param doc
	 *            the document or the sportal player grade form
	 * @return the team map with the additional data
	 */
	private static HashMap<String, Player> addAditionalInformationToPlayers(HashMap<String, Player> players, String team, Document doc) {

		players = mapAdditionalInformationToPlayer(players, getRedCardPlayers(team, doc), "RedCard");
		players = mapAdditionalInformationToPlayer(players, getYellowRedCardPlayers(team, doc), "YellowRedCard");
		players = mapAdditionalInformationToPlayer(players, getGoalPlayers(team, doc), "Goals");

		return players;
	}

	/**
	 * Maps the given additional data parameter to a player in the given map
	 * 
	 * @param players
	 *            the map of players, that should receive the additional data
	 * @param information
	 *            the map of the players, that have additional data
	 * @param mapping
	 *            the Parameter, that should be maped. Should be RedCard,
	 *            YellowRedCard or Goals
	 * @return the players map with the additional data for the mapping
	 *         parameter
	 */
	private static HashMap<String, Player> mapAdditionalInformationToPlayer(HashMap<String, Player> players, HashMap<String, Integer> information, String mapping) {
		for (String s : information.keySet()) {
			if (!players.keySet().contains(s)) {
				players.put(s, new Player(s, 0));
			}
			switch (mapping) {
			case "RedCard":
				players.get(s).setRedCard(true);
				players.get(s).setPoints(players.get(s).getPoints() + POINTS_RED_CARD);
				break;
			case "YellowRedCard":
				players.get(s).setYellowRedCard(true);
				players.get(s).setPoints(players.get(s).getPoints() + POINTS_YELLOW_RED_CARD);
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

	/**
	 * This method parsed all players, that played at the beginning of the game,
	 * and received a grade
	 * 
	 * @param players
	 *            a List&lt;Player&gt; of the players, that played at the
	 *            beginning and received a grade
	 * @return
	 */
	private static HashMap<String, Player> getPlayerOnPitch(Elements players) {
		HashMap<String, Player> retval = new HashMap<>();

		for (Element e : players) {
			Player p = new Player();
			p.setName(e.select("a[class=spielfeld_spielinfo_pos_item_link]").text());
			String[] splittedPath = e.select("img").attr("src").split("\\.");
			String[] splittedID = splittedPath[splittedPath.length - 2].split("-");
			int id = Integer.valueOf(splittedID[splittedID.length - 1]);
			p.setSportalID(id);
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

	/**
	 * Calculate the points for the given grade
	 * 
	 * @param gradeAsString
	 *            Should be between 1 and 6 in 0.5 steps
	 * @return the points for the grade
	 */
	private static int getGrade(String gradeAsString) {
		try {
			Double grade = Double.valueOf(gradeAsString);
			if (gradeAsString.equals("1")) {
				return HEIGHST_GRADE_POINT;
			} else {

				return (int) (HEIGHST_GRADE_POINT - (((grade - 1) / 0.5) * DIF_BETWEEN_HALF_GRADES));
			}

		} catch (NumberFormatException exc) {
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	/**
	 * Parses all Players, that replaced a other player during the game
	 * 
	 * @param team
	 *            should be TEAM_HOME or TEAM_GUEST
	 * @param doc
	 *            the Document of the sportal webside
	 * @return a Map with player names as key and a player object as value for
	 *         all players, that replaced a other player during the game
	 */
	private static Map<String, Player> getReplacementPlayer(String team, Document doc) {
		Map<String, Player> retval = new HashMap<>();

		Elements changes = getRowData(doc, team, "Einwechslung");

		for (Element e : changes) {
			String eleText = e.text();
			String playerName = eleText.split("für")[0];
			if (playerName.contains("(")) {
				String gradeAsString = playerName.split(Pattern.quote("("))[1];
				gradeAsString = gradeAsString.substring(0, gradeAsString.length() - 2);
				gradeAsString = gradeAsString.replace(",", ".");
				int grade = getGrade(gradeAsString);

				playerName = playerName.split(Pattern.quote("("))[0];
				String[] playerNameParts = playerName.split(Pattern.quote("."));
				playerName = "";
				for (int i = 1; i < playerNameParts.length; i++) {
					playerName = playerName + playerNameParts[i];
				}
				playerName = playerName.substring(1, playerName.length() - 1);

				Player p = new Player(playerName, grade);
				retval.put(playerName, p);
			}
		}

		return retval;
	}

	/**
	 * Parses the Document to receive the team name of the given team.
	 * 
	 * @param team
	 *            should be TEAM_HOME or TEAM_GUEST
	 * @param doc
	 *            the Document of the sportal webside
	 * @return the team name as string
	 */
	private static String getTeamName(String team, Document doc) {
		Element teamLogo = doc.select("div[class=logo" + team + "]").first();
		return teamLogo.select("a").attr("title");
	}

	/**
	 * Parses all Players, that scored at leas one goal during the game.
	 * 
	 * @param team
	 *            should be TEAM_HOME or TEAM_GUEST
	 * @param doc
	 *            the Document of the sportal webside
	 * @return a Map with the player name as key and a player object as value
	 *         for all players, that plays in the given team and scored at least
	 *         one goal.
	 */
	private static HashMap<String, Integer> getGoalPlayers(String team, Document doc) {
		HashMap<String, Integer> retval = new HashMap<>();
		Elements goals = getRowData(doc, team, "Tore");
		for (Element e : goals) {
			if (!e.text().contains("Eigentor")) {
				String playerName = e.text().split(Pattern.quote("("))[0];
				playerName = playerName.substring(0, playerName.length() - 1);
				if (retval.keySet().contains(playerName)) {
					int currentGoals = retval.get(playerName);
					retval.put(playerName, currentGoals + 1);
				} else {
					retval.put(playerName, 1);
				}
			}
		}
		return retval;
	}

	/**
	 * Parses all Players, that received a YellowRedCard
	 * 
	 * @param team
	 *            should be TEAM_HOME or TEAM_GUEST
	 * @param doc
	 *            the Document of the sportal webside
	 * @return a Map with the player name as key and a player object as value
	 *         for all players, that plays in the given team and received a
	 *         YellowRedCard
	 */
	private static HashMap<String, Integer> getYellowRedCardPlayers(String team, Document doc) {
		HashMap<String, Integer> retval = new HashMap<>();
		Elements yellowRedCards = getRowData(doc, team, "Gelb-Rote Karten");
		for (Element e : yellowRedCards) {
			String playerName = e.text().split(Pattern.quote("("))[0];
			retval.put(playerName, 0);
		}
		return retval;
	}

	/**
	 * Parses all Players, that received a RedCard
	 * 
	 * @param team
	 *            should be TEAM_HOME or TEAM_GUEST
	 * @param doc
	 *            the Document of the sportal webside
	 * @return a Map with the player name as key and a player object as value
	 *         for all players, that plays in the given team and received a
	 *         RedCard
	 */
	private static HashMap<String, Integer> getRedCardPlayers(String team, Document doc) {
		HashMap<String, Integer> retval = new HashMap<>();
		Elements redCards = getRowData(doc, team, "Rote Karten");
		for (Element e : redCards) {
			String playerName = e.text().split(Pattern.quote("("))[0];
			retval.put(playerName, 0);
		}
		return retval;
	}

	/**
	 * Parses the information table.
	 * 
	 * @param doc
	 *            The document of the sportal webside.
	 * @param team
	 *            should be TEAM_HOME or TEAM_GUEST
	 * @param headerText
	 *            the name of the information, that should be parsed
	 * @return All Elements under the given row header
	 */
	private static Elements getRowData(Document doc, String team, String headerText) {
		String teamAB = "";
		if (team.equals(TEAM_GUEST)) {
			teamAB = "B";
		} else if (team.equals(TEAM_HOME)) {
			teamAB = "A";
		}

		Element table = doc.select("li[class=first]:contains(" + headerText + ")").first().parent();
		Element teams = table.nextElementSibling();
		Element data = teams.nextElementSibling();
		Element rowData = data.select("div[class=team" + teamAB + "]").first();
		Elements retval = rowData.select("div[class=headDataRowLiDiv2]");
		return retval;
	}

	/**
	 * Creates the URL for the sportal webside out of the given information
	 * 
	 * @param season
	 *            the season, should be 2015 for 2015-2016
	 * @param matchday
	 *            the matchday, should be 1-34 for bundesliga
	 * @param matchID
	 *            the matchID is a unique sportal ID for a specific match
	 * @return the generated URL as String
	 */
	private static String getURL(int season, int matchday, int matchID) {
		String parsedSeason = String.valueOf(season);
		parsedSeason = parsedSeason.substring(2, parsedSeason.length());
		parsedSeason = parsedSeason + String.valueOf(Integer.valueOf(parsedSeason) + 1);

		String filledAppendix = appendix.replace("%GAMEDAY%", String.valueOf(matchday));
		filledAppendix = filledAppendix.replace("%SEASON%", parsedSeason);
		filledAppendix = filledAppendix.replace("%MATCHID%", String.valueOf(matchID).substring(2));

		return root + filledAppendix;
	}
}
