package de.szut.dqi12.cheftrainer.server.parsing;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom2.Element;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.utils.ParserUtils;

/**
 * This parser is used to fill a empty database with real players. 
 * The website www.ran.de will be parsed, to create RealTeam and Player objects, that will be added to the database.ö
 * @author Alexander Brennecke
 *
 */
public class PlayerParser {
	
	private String[] validPositions = {"Torwart", "Abwehr", "Mittelfeld", "Sturm"};
	private List<String> validPositionList = new ArrayList<String>(Arrays.asList(validPositions));

	/**
	 * Creates a List of Players out of the given URL
	 * @param teamURL the ran.de URL for the Real team, that should be parsed
	 * @return a List&ltPlayer&gt with all Players for the given team
	 * @throws IOException
	 */

	public List<Player> getPlayers(URL teamURL) throws IOException {
		List<Player> playerList = new ArrayList<Player>();
		String pageContent = ParserUtils.getPage(teamURL);
		String playersTable = ParserUtils.getTableOfHTML(pageContent);
		List<Element> rootChilds = ParserUtils.parseXmlTableString(playersTable);
		playerList = createTeamsPlayersList(rootChilds);

		return playerList;
	}

	/**
	 * 
	 * @param playersTable the table on the ran.de team side, that contains all players
	 * @return a List&ltPlayer&gt with all Players for the given table
	 */
	private List<Player> createTeamsPlayersList(List<Element> playersTable) {
		List<Player> playerList = new ArrayList<Player>();
		boolean validPosition = false;
		String currentPosition = "";
		for (Element e : playersTable) {
			if (e.hasAttributes() && validPosition) {
				playerList.add(createPlayer(e, currentPosition));
			}
			else{
				currentPosition = e.getChildText("th");
				validPosition=validPositionList.contains(currentPosition);
			}
		}

		return playerList;
	}

	/**
	 * Creates a new Player object out of the given Element 
	 * @param playerElement the Element, out of which the player object can be created
	 * @param role the role of the player
	 * @return a new Player object, that was created out of the given parameters
	 */
	private Player createPlayer(Element playerElement, String role) {
		Player player = new Player();
		List<Element> playersAttributes = playerElement.getChildren();
		
		try{
			player.setNumber(Integer.valueOf(playersAttributes.get(0).getText()));
		}
		catch(NumberFormatException nfe){
			System.err.println("No valid player number.");
		}
		
		player.setName(playersAttributes.get(2).getChildText("a"));
		player.setPosition(role);
		return player;
	}

	
}