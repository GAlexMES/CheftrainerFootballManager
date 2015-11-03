package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MarketPlayer {
	private final StringProperty playerName;
	private final StringProperty points;
	private final StringProperty werth;
	private final Player player;

	public MarketPlayer(String playerName, String points,
			String price, Player p) {
		super();
		this.playerName =  new SimpleStringProperty(playerName);
		this.points =  new SimpleStringProperty(points);
		this.werth =  new SimpleStringProperty(price);
		this.player=p;
	}

	public StringProperty getPlayerName() {
		return playerName;
	}

	public StringProperty getPoints() {
		return points;
	}

	public StringProperty getWerth() {
		return werth;
	}
	
	public Player getPlayer(){
		return player;
	}


}
