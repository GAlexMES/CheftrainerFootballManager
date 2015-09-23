package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class FormationController {
	@FXML
	private GridPane formationFrame;

	public FormationController() {
		
	}
	
	public GridPane getFrame() {
		return formationFrame;
	}


public ArrayList getPlayers() {
	ArrayList<Label> players = new ArrayList<Label>();
	try {
		for (Node n : formationFrame.getChildren()) {
			players.add(((Label) n));
		}
		return players;
	} catch (NullPointerException n) {
		return null;
	}
}

public void setClickedListener() {
	for (Node n : formationFrame.getChildren()) {
		((Label) n).setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				// ADD CODE HERE

				System.out.println(((Label) n).getText());

			}
		});
	}
}
}
