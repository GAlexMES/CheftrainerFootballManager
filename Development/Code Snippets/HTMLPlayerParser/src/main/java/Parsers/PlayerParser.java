package Parsers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom2.Element;

import de.brennecke.alexander.Test.Player;

public class PlayerParser {
	
	private String[] validPositions = {"Torwart", "Abwehr", "Mittelfeld", "Sturm"};
	private List<String> validPositionList = new ArrayList<String>(Arrays.asList(validPositions));


	public List<Player> getPlayers(URL teamURL) {
		List<Player> playerList = new ArrayList<Player>();
		String pageContent = Parser.getPage(teamURL);
		String playersTable = Parser.getTableOfHTML(pageContent);
		List<Element> rootChilds = Parser.parseXmlTableString(playersTable);
		playerList = createTeamsPlayersList(rootChilds);

		return playerList;
	}

	private List<Player> createTeamsPlayersList(List<Element> playersTable) {
		List<Player> playerList = new ArrayList<Player>();
		String role = "";
		boolean validPosition = false;
		for (Element e : playersTable) {
			if (e.hasAttributes() && validPosition) {
				playerList.add(createPlayer(e, role));
			}
			else{
				String currentPosition = e.getChildText("th");
				validPosition=validPositionList.contains(currentPosition);
			}
		}

		return playerList;
	}

	private Player createPlayer(Element playerElement, String role) {
		Player player = new Player();
		List<Element> playersAttributes = playerElement.getChildren();
		player.setName(playersAttributes.get(2).getChildText("a"));
		return player;
	}

	
}
