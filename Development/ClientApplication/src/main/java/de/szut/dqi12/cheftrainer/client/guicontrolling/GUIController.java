package de.szut.dqi12.cheftrainer.client.guicontrolling;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.CommunitiesController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.ControllerInterface;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.ManagerTeam;

/**
 * The GUIController controlles the GUIInitialator to replacement GUI
 * components.
 * 
 * @author Alexander Brennecke
 *
 */
public class GUIController {

	private static GUIController instance = null;
	private GUIInitialator guiInitialator;
	private FXMLLoader currentContentLoader;
	private Stage currentDialogStage;
	private ClassLoader classLoader;
	private URL fxmlFile;

	/**
	 * Constructor
	 * 
	 * @param primaryStage
	 *            needs a Stage to display GUI Elements onto it.
	 */

	public GUIController(Stage primaryStage) {
		guiInitialator = new GUIInitialator(primaryStage);
		classLoader = getClass().getClassLoader();
	}

	/**
	 * return the instance of this class.
	 */
	public static GUIController getInstance() {
		return instance;
	}

	/**
	 * Should be used to initialize this class for singleton pattern.
	 * 
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
		setContentFrameByPath(fxmlFileName, update);
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
			currentContentLoader = new FXMLLoader();
			fxmlFile = classLoader.getResource("sourcesFXML/" + path);
			currentContentLoader.setLocation(fxmlFile);
			GridPane newContentPane = (GridPane) currentContentLoader.load();

			try {
				((ControllerInterface) currentContentLoader.getController())
						.init();
			} catch (Exception e) {

			}

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

	public void initController() {
	}

	public GUIInitialator getGUIInitialator() {
		return guiInitialator;
	}

	public void resetApplication() {
		guiInitialator.closeMainApplication();
		showLogin();
	}

	public FXMLLoader getCurrentContentLoader() {
		return currentContentLoader;
	}

	public void setCurrentDialogStage(Stage dialogStage) {
		this.currentDialogStage = dialogStage;
	}

	public Stage getCurrentDialogStage() {
		return this.currentDialogStage;
	}

	public void closeCurrentDialog() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				currentDialogStage.close();
			}
		});
	}
}
