package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.ClientApplication;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIInitialator;

/**
 * Controller for the side menu
 * 
 * @author Alexander Brennecke
 *
 */
public class SideMenuController {

	// DEFINITIONS
	@FXML
	private VBox sideMenu;

	private GUIInitialator guiInitilator;
	private boolean sideMenuFlag = true;

	public static double expandedWidth = 200.0;
	private double collapsedWidth = 100.0;

	private GridPane rLayout = null;

	private List<String> sideMenuButtonTitles = new ArrayList<String>();
	private List<String> alwaysClickableButtons = new ArrayList<>();

	private Map<String, String> button_FXMLComponent = new HashMap<>();

	/**
	 * 
	 * @param mainApp
	 *            is required for further actions, generates the side menu
	 *            content
	 */
	public void setGUIInitialator(GUIInitialator guiInitilator) {
		this.guiInitilator = guiInitilator;
		GridPane rLayout = guiInitilator.getRootlayout();
		generateButtons(((VBox) rLayout.lookup("#sideMenu")));
	}

	/**
	 * Generates the buttons for the side menu and adds them to the VBox Uses
	 * the options, which are defined in the sideMenuButtons.xml
	 * 
	 * @param box
	 */
	@SuppressWarnings("static-access")
	private void generateButtons(VBox box) {
		try {
			// reads the sideMenuButtons.xml
			Path buttonDefinitionFile = Paths.get(ClientApplication.class
					.getResource("/definitions/sideMenuButtons.xml").toURI());

			List<String> xmlLines = Files.readAllLines(buttonDefinitionFile);
			List<Element> buttonList = parseXMLButtons(xmlLines);
			List<Button> buttons = new ArrayList<Button>();

			// defines buttons out of the xml
			for (Element e : buttonList) {
				Button tempButton = generateButtonFromXML(e);
				buttons.add(tempButton);
			}

			// adds the buttons to the vbox
			box.getChildren().addAll(buttons);

			// sets thr Vgrow priority
			for (Node n : box.getChildren()) {
				box.setVgrow(((Button) n), Priority.ALWAYS);
			}

			// sets the width of the box
			box.setPrefWidth(expandedWidth);
			box.setMaxWidth(expandedWidth);
			box.setMinWidth(expandedWidth);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a button from an xml source
	 * 
	 * @param e
	 *            the xml node element
	 * @return a button Element
	 */
	private Button generateButtonFromXML(Element e) {
		// name of the button
		String buttonName = e.getChildText("text");

		// new button with the name as text
		Button tempButton = new Button(buttonName);
		// sets the button image with the source, defined in the xml
		Image image = new Image(
				ClientApplication.class.getResourceAsStream("/images/"
						+ e.getChildText("imageName")));
		tempButton.setGraphic(new ImageView(image));
		// sets height and other properties of the button
		tempButton.setPrefHeight(250.0);
		tempButton.setMnemonicParsing(false);
		tempButton.setAlignment(Pos.BASELINE_LEFT);
		tempButton.setPrefWidth(expandedWidth);
		tempButton.setStyle("button");

		// sets the button id
		tempButton.setId(buttonName);
		// adds the button id and the fxmlComponent, which is defined in the xml
		// to the map
		button_FXMLComponent.put(buttonName, e.getChildText("fxmlComponent"));

		tempButton = addCorrectListener(tempButton, e);
		tempButton = setClickable(tempButton, e);
		return tempButton;
	}

	private Button setClickable(Button button, Element e) {
		if (e.getChildText("alwaysClickable").equals("true")) {
			button.setDisable(false);
		} else {
			button.setDisable(true);
		}
		return button;
	}

	private Button addCorrectListener(Button button, Element e) {
		// checks if the button is the toggle button(toggles the menu)
		if (e.getChildText("triggerButton").equals("true")) {
			// sets the onAction to the triggerSideMenu function
			button.setOnAction(this::triggerSideMenu);
		} else if (e.getChildText("logoutButton").equals("true")) {
			Controller controller = Controller.getInstance();
			// sets the onAction to the logout function
			button.setOnAction(controller::resetApplication);
		} else {
			// sets the onAction to the buttonPressed function
			button.setOnAction(this::buttonPressed);
		}
		return button;
	}

	/**
	 * parses the xml file
	 * 
	 * @param xmlStringList
	 *            list of the lines of the xml file
	 * @return list of <Button> Nodes out of the xml file
	 */
	private List<Element> parseXMLButtons(List<String> xmlStringList) {
		String xmlString = "";
		for (String s : xmlStringList) {
			xmlString += s + "\t";
		}

		SAXBuilder saxBuilder = new SAXBuilder();
		List<Element> nodeList = new ArrayList<Element>();
		try {
			Document doc = saxBuilder.build(new StringReader(xmlString));
			nodeList = doc.getRootElement().getChildren();
		} catch (JDOMException e) {
		} catch (IOException e) {
		}
		return nodeList;
	}

	/**
	 * is called when a button was pressed (without the trigger side menu
	 * button)
	 * 
	 * @param evt
	 */
	@FXML
	public void buttonPressed(ActionEvent evt) {
		String sourceID = ((Button) evt.getSource()).getId();
		String fxmlComponent = button_FXMLComponent.get(sourceID);
		String fileName = fxmlComponent + ".fxml";
		GUIController.getInstance().setContentFrameByName(fileName, true);
	}

	/**
	 * is called when the triger side menu button was pressed
	 * 
	 * @param evt
	 */
	@FXML
	public void triggerSideMenu(ActionEvent evt) {
		rLayout = guiInitilator.getRootlayout();
		ObservableList<Node> buttonList = ((VBox) rLayout.lookup("#sideMenu"))
				.getChildren();
		if (sideMenuFlag) {
			collaps(buttonList);
		} else {
			expands(buttonList);

		}
	}

	/**
	 * collapses the side menu, so that only the button images will be shown
	 * 
	 * @param buttonList
	 *            List of buttons in the vbox
	 */
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
		collapseColums();
		updateWidthPercentage();
	}

	/**
	 * resize the width of the root grid pane coloums
	 */
	private void collapseColums() {
		ColumnConstraints menuColoum = rLayout.getColumnConstraints().get(0);
		menuColoum.setMaxWidth(collapsedWidth);

		ColumnConstraints contentColoum = rLayout.getColumnConstraints().get(1);
		contentColoum.setPrefWidth(rLayout.getWidth()
				- ((VBox) rLayout.lookup("#sideMenu")).getWidth());
		contentColoum.setMinWidth(rLayout.getWidth()
				- ((VBox) rLayout.lookup("#sideMenu")).getWidth());
	}

	/**
	 * expands the side menu so that the images and the textes will be shown
	 * 
	 * @param buttonList
	 *            List of buttons in the vbox
	 */
	private void expands(ObservableList<Node> buttonList) {
		((VBox) rLayout.lookup("#sideMenu")).setPrefWidth(expandedWidth);
		((VBox) rLayout.lookup("#sideMenu")).setMaxWidth(expandedWidth);
		((VBox) rLayout.lookup("#sideMenu")).setMinWidth(expandedWidth);
		for (int i = 0; i < buttonList.size(); i++) {
			((Button) buttonList.get(i)).setText(sideMenuButtonTitles.get(i));
			((Button) buttonList.get(i)).setPrefWidth(expandedWidth);
		}
		sideMenuFlag = true;
		expandColums();
		updateWidthPercentage();
	}

	public void updateWidthPercentage() {
		double width = 0.0;

		if (sideMenuFlag) {
			width = expandedWidth;
		} else {
			width = collapsedWidth;
		}
		rLayout.getColumnConstraints().get(0).setMinWidth(width);
		rLayout.getColumnConstraints().get(1)
				.setMaxWidth(rLayout.getWidth() - width);
	}

	/**
	 * resize the width of the root grid pane coloums
	 */
	public void expandColums() {
		rLayout = guiInitilator.getRootlayout();
		ColumnConstraints menuColoum = rLayout.getColumnConstraints().get(0);
		menuColoum.setMaxWidth(expandedWidth);

		ColumnConstraints contentColoum = rLayout.getColumnConstraints().get(1);
		contentColoum.setPrefWidth(rLayout.getWidth() - expandedWidth - 100);
		contentColoum.setMinWidth(rLayout.getWidth() - expandedWidth - 100);
	}

	public void triggerButtonClickable(boolean clickable) {
		ObservableList<Node> buttonList = ((VBox) rLayout.lookup("#sideMenu"))
				.getChildren();
		for(Node n : buttonList){
			if(!alwaysClickableButtons.contains(((Button)n).getText())){
				((Button)n).setDisable(!clickable);
			}
		}
	}
	
	// GETTER AND SETTER
	public List<String> getSideMenuButtonTitles() {
		return sideMenuButtonTitles;
	}

}
