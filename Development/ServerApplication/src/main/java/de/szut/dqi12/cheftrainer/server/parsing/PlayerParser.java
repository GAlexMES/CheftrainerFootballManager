package de.szut.dqi12.cheftrainer.server.parsing;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom2.Element;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;
import de.szut.dqi12.cheftrainer.server.utils.ParserUtils;

public class PlayerParser {
	
	private String[] validPositions = {"Torwart", "Abwehr", "Mittelfeld", "Sturm"};
	private List<String> validPositionList = new ArrayList<String>(Arrays.asList(validPositions));


	public List<Player> getPlayers(URL teamURL) {
		List<Player> playerList = new ArrayList<Player>();
		String pageContent = ParserUtils.getPage(teamURL);
		String playersTable = ParserUtils.getTableOfHTML(pageContent);
		List<Element> rootChilds = ParserUtils.parseXmlTableString(playersTable);
		playerList = createTeamsPlayersList(rootChilds);

		return playerList;
	}

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