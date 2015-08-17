package de.szut.dqi12.cheftrainer.client.guiControlling;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.MainApp;

public class GUIController {

	private static GUIController instance = null;
	private GUIInitialator guiInitialator;

	public GUIController(Stage primaryStage) {
		guiInitialator = new GUIInitialator(primaryStage);
	}

	public static GUIController getInstance() {
		return instance;
	}

	public static GUIController getInstance(Stage primaryStage) {
		if (instance == null) {
			instance = new GUIController(primaryStage);
		}
		return instance;
	}

	public void showLogin() {
		guiInitialator.initLoginLayout();
	}

	public void showMainApplikation() {
		guiInitialator.initRootLayout();
		guiInitialator.showMenuLayout();
		setContentFrameByName("CommunitiesFrame.fxml", false);
		guiInitialator.getSideMenuController().expandColums();
	}

	/**
	 * sets the given .fxml, which must contains an AnchorPane as the content.
	 * 
	 * @param fxmlFileName
	 *            <li>must be a .fxml file</li><li>fxml must be a AnchorPane</li>
	 *            <li>must be lcoated in
	 *            de\szut\dqi12\cheftrainer\client\view\fxmlSources</li>
	 */
	public void setContentFrameByName(String fxmlFileName, boolean update) {
		String path = GUIInitialator.FXML_RESOURCE + fxmlFileName;
		setContentFrameByPath(path,update);
	}

	/**
	 * sets the given .fxml, which must contains an AnchorPane as the content.
	 * 
	 * @param path
	 *            <li>to a .fxml</li><li>fxml must be a AnchorPane</li><li>path
	 *            root at de\szut\dqi12\cheftrainer\client</li>
	 */
	public void setContentFrameByPath(String path, boolean update) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(path));
			GridPane newContentPane = (GridPane) loader.load();
			newContentPane.autosize();
			if (update) {
				GridPane currentContentPane = ((GridPane) guiInitialator
						.getRootlayout());
				Node sideMenu = currentContentPane.getChildren().get(2);
				currentContentPane.getChildren().removeAll(sideMenu);
			}
			guiInitialator.getRootlayout().add(newContentPane, 1, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
