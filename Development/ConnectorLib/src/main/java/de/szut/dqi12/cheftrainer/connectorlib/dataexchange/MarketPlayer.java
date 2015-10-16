package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class MarketPlayer {
	private final StringProperty playerName;
	private final StringProperty points;
	private final StringProperty werth;

	public MarketPlayer(String playerName, String points,
			String price) {
		super();
		this.playerName =  new SimpleStringProperty(playerName);
		this.points =  new SimpleStringProperty(points);
		this.werth =  new SimpleStringProperty(price);
		
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


}
