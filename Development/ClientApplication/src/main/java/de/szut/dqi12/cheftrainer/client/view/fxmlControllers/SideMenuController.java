package de.szut.dqi12.cheftrainer.client.view.fxmlControllers;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guiControlling.GUIInitialator;

public class SideMenuController {

	private GUIInitialator mainApp;
	private boolean sideMenuFlag = true;

	private double expandedWidth = 200.0;
	private double collapsedWidth = 0.0;

	private BorderPane rLayout = null;


	private List<String> sideMenuButtonTitles = new ArrayList<String>();

	public void setMainApp(GUIInitialator mainApp) {
		this.mainApp = mainApp;
		BorderPane rLayout = mainApp.getRootlayout();
		Image imageDecline = new Image(
				MainApp.class
						.getResourceAsStream("../../../../../images/dummyMenuIcon.png"));
		for (Node n : ((VBox) rLayout.getLeft()).getChildren()) {
			((Button)n).setGraphic(new ImageView(imageDecline));
			((Button)n).setAlignment(Pos.BASELINE_LEFT);
			((Button)n).setPrefWidth(expandedWidth);
		}
	}

	@FXML
	public void triggerSideMenu() {
		rLayout = mainApp.getRootlayout();
		ObservableList<Node> buttonList = ((VBox) rLayout.getLeft())
				.getChildren();
		if (sideMenuFlag) {
			collaps(buttonList);
		} else {
			expands(buttonList);

		}
	}

	private void collaps(ObservableList<Node> buttonList) {
		((VBox) rLayout.getLeft()).setPrefWidth(collapsedWidth);
		for (Node n : buttonList) {
			sideMenuButtonTitles.add(((Button) n).getText());
			((Button) n).setText("");
		}
		sideMenuFlag = false;
	}

	private void expands(ObservableList<Node> buttonList) {
		((VBox) rLayout.getLeft()).setPrefWidth(expandedWidth);
		for (int i = 0; i < buttonList.size(); i++) {
			((Button) buttonList.get(i)).setText(sideMenuButtonTitles.get(i));
		}
		sideMenuFlag = true;
	}
	
	public List<String> getSideMenuButtonTitles(){
		return sideMenuButtonTitles;
	}
}