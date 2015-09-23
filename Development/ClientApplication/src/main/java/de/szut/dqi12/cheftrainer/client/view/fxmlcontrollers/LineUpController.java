package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class LineUpController {
	@FXML
	private GridPane lineUpFrame;

	public LineUpController() {

	}

	public javafx.scene.layout.GridPane getFrame() {
		return lineUpFrame;
	}

	public void changeFormation(String path) {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			FXMLLoader currentContentLoader = new FXMLLoader();
			URL fxmlFile = classLoader.getResource("sourcesFXML/" + path);
			currentContentLoader.setLocation(fxmlFile);
			GridPane newContentPane = (GridPane) currentContentLoader.load();
			((FormationController) currentContentLoader.getController())
					.setClickedListener();
			;

			for (Node node : newContentPane.getChildren()) {
				System.out.println(((Label) node).getText());
			}
			lineUpFrame.add(newContentPane, 0, 0);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveButtonClicked() {

	}

	public void formationButtonClicked() {

	}

}
