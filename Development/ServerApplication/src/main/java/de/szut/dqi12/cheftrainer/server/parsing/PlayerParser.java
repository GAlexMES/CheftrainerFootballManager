package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;

/**
 * This parser is used to fill a empty database with real players. 
 * The website www.ran.de will be parsed, to create RealTeam and Player objects, that will be added to the database.ö
 * @author Alexander Brennecke
 *
 */
public class PlayerParser {
	
	/**
	 * Creates a List of Players out of the given URL
	 * @param teamURL the ran.de URL for the Real team, that should be parsed
	 * @return a List&ltPlayer&gt with all Players for the given team
	 * @throws IOException
	 */

	public List<Player> getPlayers(String teamURL) throws IOException {
		List<Player> playerList = new ArrayList<Player>();
		Document doc = Jsoup.connect(teamURL).get();
		Elements rootChilds = doc.getElementById("moduleListContent").select("ul[class=listBig]");
		playerList = createTeamsPlayersList(rootChilds);

		return playerList;
	}

	/**
	 * 
	 * @param playersTable the table on the ran.de team side, that contains all players
	 * @return a List&ltPlayer&gt with all Players for the given table
	 */
	private List<Player> createTeamsPlayersList(Elements playersTable) {
		List<Player> playerList = new ArrayList<Player>();
		for (Element e : playersTable) {
			playerList.add(createPlayer(e));
		}

		return playerList;
	}

	/**
	 * Creates a new Player object out of the given Element 
	 * @param playerElement the Element, out of which the player object can be created
	 * @param role the role of the player
	 * @return a new Player object, that was created out of the given parameters
	 */
	private Player createPlayer(Element e) {
		Player player = new Player();
		
		String position = e.select("li[class=torjagerPosition]").text();
		String number = e.select("li[class=torjagerPlatz]").text();
		String birthday = e.select("li[class=torjagerGeburtstag]").text();
		String pictureURL = parseURL(e.select("img").attr("src"));
		String name = e.select("li[class=torjagerSpieler] > a").text();
		int sportalID = getSportalID(pictureURL);
		
		try{
			player.setNumber(Integer.valueOf(number));
		}
		catch(NumberFormatException nfe){
			System.err.println("No valid player number.");
		}
		
		player.setSportalID(sportalID);
		player.setAbsolutePictureURL(pictureURL);
		player.setBirthdate(birthday);
		player.setName(name);
		player.setPosition(position);
		return player;
	}
	
	private String parseURL(String url){
		return TeamParser.rootURL + url.replace("34x41", "150x180");
	}
	
	private int getSportalID(String imagePath){
		String[] splittedPath = imagePath.split("\\.");
		String[] splittedID = splittedPath[splittedPath.length-2].split("-");
		String id = splittedID[splittedID.length-1];
		return Integer.valueOf(id);
	}

	
}