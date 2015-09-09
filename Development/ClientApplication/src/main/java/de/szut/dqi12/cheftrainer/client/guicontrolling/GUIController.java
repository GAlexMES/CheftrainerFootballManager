package de.szut.dqi12.cheftrainer.client.guicontrolling;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.MainApp;

/**
 * The GUIController controlles the GUIInitialator to replacement GUI components.
 * @author Alexander Brennecke
 *
 */
public class GUIController {

	private static GUIController instance = null;
	private GUIInitialator guiInitialator;
	
	/**
	 * Constructor
	 * @param primaryStage needs a Stage to display GUI Elements onto it.
	 */

	public GUIController(Stage primaryStage) {
		guiInitialator = new GUIInitialator(primaryStage);
	}

	/**
	 * return the instance of this class.
	 */
	public static GUIController getInstance() {
		return instance;
	}

	/**
	 * Should be used to initialize this class for singleton pattern.
	 * @param primaryStage
	 * @return
	 */
	public static GUIController getInstance(Stage primaryStage) {
		if (instance == null) {
			instance = new GUIController(primaryStage);
		}
		return instance;
	}

	/**
	 * Shows the Login Frame
	 */
	public void showLogin() {
		guiInitialator.initLoginLayout();
	}

	/**
	 * Shows the main application, including side menu and content pane
	 */
	public void showMainApplication() {
		Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	guiInitialator.closeLoginDialog();
            	guiInitialator.initRootLayout();
        		guiInitialator.showMenuLayout();
        		setContentFrameByName("CommunitiesFrame.fxml", false);
        		guiInitialator.getSideMenuController().expandColums();
            }
        });
		
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

	public GUIInitialator getGUIInitialator() {
		return guiInitialator;
	}

	public void resetApplication() {
		guiInitialator.closeMainApplication();
		showLogin();
	}
}
