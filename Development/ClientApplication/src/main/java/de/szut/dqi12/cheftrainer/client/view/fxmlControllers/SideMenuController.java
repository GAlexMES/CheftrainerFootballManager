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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guiControlling.GUIController;
import de.szut.dqi12.cheftrainer.client.guiControlling.GUIInitialator;

public class SideMenuController {

	@FXML
	private VBox sideMenu;

	private GUIInitialator mainApp;
	private boolean sideMenuFlag = true;

	private double expandedWidth = 200.0;
	private double collapsedWidth = 100.0;

	private GridPane rLayout = null;

	private List<String> sideMenuButtonTitles = new ArrayList<String>();

	public void setMainApp(GUIInitialator mainApp) {
		this.mainApp = mainApp;
		GridPane rLayout = mainApp.getRootlayout();
		Image imageDecline = new Image(
				MainApp.class
						.getResourceAsStream("../../../../../images/dummyMenuIcon.png"));

		for (Node n : ((VBox) rLayout.lookup("#sideMenu")).getChildren()) {
			((Button) n).setGraphic(new ImageView(imageDecline));
			((Button) n).setAlignment(Pos.BASELINE_LEFT);
			((Button) n).setPrefWidth(expandedWidth);
		}
	}

	@FXML
	public void buttonPressed() {
		String fileName = "CommunitiesFrame.fxml";
		GUIController.getInstance().setContentFrameByName(fileName);
	}

	@FXML
	public void triggerSideMenu() {
		rLayout = mainApp.getRootlayout();
		ObservableList<Node> buttonList = ((VBox) rLayout.lookup("#sideMenu"))
				.getChildren();
		if (sideMenuFlag) {
			collaps(buttonList);
		} else {
			expands(buttonList);

		}
	}

	private void collaps(ObservableList<Node> buttonList) {
		((VBox) rLayout.lookup("#sideMenu")).setPrefWidth(collapsedWidth);
		((VBox) rLayout.lookup("#sideMenu")).setMaxWidth(collapsedWidth);
		((VBox) rLayout.lookup("#sideMenu")).setMinWidth(collapsedWidth);
		for (Node n : buttonList) {
			sideMenuButtonTitles.add(((Button) n).getText());
			((Button) n).setText("");
			((Button) n).setPrefWidth(collapsedWidth);
		}
		sideMenuFlag = false;
		
		ColumnConstraints menuColoum = rLayout.getColumnConstraints().get(0);
		menuColoum.setMaxWidth(collapsedWidth);
		
		ColumnConstraints contentColoum = rLayout.getColumnConstraints().get(1);
		contentColoum.setPrefWidth(rLayout.getWidth()-((VBox)rLayout.lookup("#sideMenu")).getWidth());
		contentColoum.setMinWidth(rLayout.getWidth()-((VBox)rLayout.lookup("#sideMenu")).getWidth());
	}

	private void expands(ObservableList<Node> buttonList) {
		((VBox) rLayout.lookup("#sideMenu")).setPrefWidth(expandedWidth);
		((VBox) rLayout.lookup("#sideMenu")).setMaxWidth(expandedWidth);
		((VBox) rLayout.lookup("#sideMenu")).setMinWidth(expandedWidth);
		for (int i = 0; i < buttonList.size(); i++) {
			((Button) buttonList.get(i)).setText(sideMenuButtonTitles.get(i));
			((Button) buttonList.get(i)).setPrefWidth(expandedWidth);
		}
		sideMenuFlag = true;	
		

		ColumnConstraints menuColoum = rLayout.getColumnConstraints().get(0);
		menuColoum.setMaxWidth(expandedWidth);
		
		ColumnConstraints contentColoum = rLayout.getColumnConstraints().get(1);
		contentColoum.setPrefWidth(rLayout.getWidth()-expandedWidth-100);
		contentColoum.setMinWidth(rLayout.getWidth()-expandedWidth-100);
	}

	public List<String> getSideMenuButtonTitles() {
		return sideMenuButtonTitles;
	}
}