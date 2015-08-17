package de.szut.dqi12.cheftrainer.client.view.fxmlControllers;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guiControlling.GUIController;
import de.szut.dqi12.cheftrainer.client.guiControlling.GUIInitialator;

/**
 * Controller for the registration dialog
 * 
 * @author Alexander Brennecke
 *
 */
public class RegistrationController {

	private Stage dialogStage;

	@FXML
	private AnchorPane serverDetailsPane;
	@FXML
	private AnchorPane buttonPane;
	@FXML
	private AnchorPane registrationPane;
	@FXML
	private CheckBox showDetailsCheck;
	
	private double mainPaneMaxSize;
	private double buttonPane_YLayout;
	private double serverDetailsPane_YLayout;
	private double severDetailsPane_Height;
	
	
	/**
	 * initialized a few variables
	 */
	public void initialize() {
		mainPaneMaxSize = registrationPane.getPrefHeight();
		buttonPane_YLayout = buttonPane.layoutYProperty().getValue();
		serverDetailsPane_YLayout = serverDetailsPane.layoutYProperty().get()+200;
		severDetailsPane_Height = serverDetailsPane.getPrefHeight();

		buttonPane.layoutYProperty().set(serverDetailsPane_YLayout);
		registrationPane.setPrefHeight(mainPaneMaxSize - severDetailsPane_Height);

		serverDetailsPane.visibleProperty().bind(showDetailsCheck.selectedProperty());
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * triggers the frame size, to display the additional server information
	 */
	@FXML
	public void triggerFrameSize() {
		if (serverDetailsPane.visibleProperty().getValue()) {
			buttonPane.layoutYProperty().set(buttonPane_YLayout);
			registrationPane.setMinHeight(mainPaneMaxSize);
			dialogStage.setMaxHeight(serverDetailsPane_YLayout+1000);
			
		} else {
			buttonPane.layoutYProperty().set(serverDetailsPane_YLayout);
			registrationPane.setPrefHeight(mainPaneMaxSize - severDetailsPane_Height);
			dialogStage.setMaxHeight(serverDetailsPane_YLayout+100);
		}
		dialogStage.sizeToScene();
	}
}