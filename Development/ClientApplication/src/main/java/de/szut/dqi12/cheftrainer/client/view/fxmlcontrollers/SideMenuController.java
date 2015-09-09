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

import de.szut.dqi12.cheftrainer.client.MainApp;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIController;
import de.szut.dqi12.cheftrainer.client.guicontrolling.GUIInitialator;
import de.szut.dqi12.cheftrainer.client.servercommunication.ServerConnection;

/**
 * Controller for the side menu
 * @author Alexander Brennecke
 *
 */
public class SideMenuController {

	//DEFINITIONS
	@FXML
	private VBox sideMenu;

	private GUIInitialator mainApp;
	private boolean sideMenuFlag = true;

	private double expandedWidth = 200.0;
	private double collapsedWidth = 100.0;

	private GridPane rLayout = null;

	private List<String> sideMenuButtonTitles = new ArrayList<String>();
	
	private Map<String,String> button_FXMLComponent =  new HashMap<>();

	/**
	 * 
	 * @param mainApp is required for further actions, generates the side menu content
	 */
	public void setMainApp(GUIInitialator mainApp) {
		this.mainApp = mainApp;
		GridPane rLayout = mainApp.getRootlayout();
		generateButtons(((VBox) rLayout.lookup("#sideMenu")));
	}

	/**
	 * Generates the buttons for the side menu and adds them to the VBox
	 * Uses the options, which are defined in the sideMenuButtons.xml
	 * @param box
	 */
	@SuppressWarnings("static-access")
	private void generateButtons(VBox box) {
		try {
			//reads the sideMenuButtons.xml
			Path buttonDefinitionFile = Paths.get(MainApp.class.getResource(
					"/definitions/sideMenuButtons.xml").toURI());
			
			List<String> xmlLines = Files.readAllLines(buttonDefinitionFile);
			List<Element> buttonList = parseXMLButtons(xmlLines);
			List<Button> buttons = new ArrayList<Button>();
			
			//defines buttons out of the xml
			for (Element e : buttonList) {
				Button tempButton = generateButtonFromXML(e);
				buttons.add(tempButton);
			}
			
			//adds the buttons to the vbox
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
	 * @param e the xml node element
	 * @return a button Element
	 */
	private Button generateButtonFromXML(Element e){
		// name of the button
		String buttonName = e.getChildText("text");
		
		// new button with the name as text
		Button tempButton = new Button(buttonName);
		// sets the button image with the source, defined in the xml
		Image image = new Image(
				MainApp.class.getResourceAsStream("/images/"
						+ e.getChildText("imageName")));
		tempButton.setGraphic(new ImageView(image));
		//sets height and other properties of the button
		tempButton.setPrefHeight(250.0);
		tempButton.setMnemonicParsing(false);
		tempButton.setAlignment(Pos.BASELINE_LEFT);
		tempButton.setPrefWidth(expandedWidth);
		tempButton.setStyle("button");
		
		//sets the button id
		tempButton.setId(buttonName);
		//adds the button id and the fxmlComponent, which is defined in the xml to the map
		button_FXMLComponent.put(buttonName,e.getChildText("fxmlComponent"));

		// checks if the button is the toggle button(toggles the menu)
		if (e.getChildText("triggerButton").equals("true")) {
			//sets the onAction to the triggerSideMenu function
			tempButton.setOnAction(this::triggerSideMenu);
		}
		else if(e.getChildText("logoutButton").equals("true")){
			ServerConnection serverCon = mainApp.getLoginController().getServerConnection();
			//sets the onAction to the logout function
			tempButton.setOnAction(serverCon::logout);
		}
		else{
			//sets the onAction to the buttonPressed function
			tempButton.setOnAction(this::buttonPressed);
		}
		return tempButton;
	}

	/**
	 * parses the xml file 
	 * @param xmlStringList list of the lines of the xml file
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
	 * is called when a button was pressed (without the trigger side menu button)
	 * @param evt
	 */
	@FXML
	public void buttonPressed(ActionEvent evt) {
		String sourceID = ((Button)evt.getSource()).getId();
		String fxmlComponent = button_FXMLComponent.get(sourceID);
		String fileName = fxmlComponent+".fxml";
		GUIController.getInstance().setContentFrameByName(fileName,true);
	}

	/**
	 * is called when the triger side menu button was pressed
	 * @param evt
	 */
	@FXML
	public void triggerSideMenu(ActionEvent evt) {
		rLayout = mainApp.getRootlayout();
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
	 * @param buttonList List of buttons in the vbox
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
	}
	
	/**
	 * resize the width of the root grid pane coloums
	 */
	private void collapseColums(){
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
	 * @param buttonList List of buttons in the vbox
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
		
	}
	
	/**
	 * resize the width of the root grid pane coloums
	 */
	public void expandColums(){
		rLayout = mainApp.getRootlayout();
		ColumnConstraints menuColoum = rLayout.getColumnConstraints().get(0);
		menuColoum.setMaxWidth(expandedWidth);

		ColumnConstraints contentColoum = rLayout.getColumnConstraints().get(1);
		contentColoum.setPrefWidth(rLayout.getWidth() - expandedWidth - 100);
		contentColoum.setMinWidth(rLayout.getWidth() - expandedWidth - 100);
	}
	
	
	//GETTER AND SETTER
	public List<String> getSideMenuButtonTitles() {
		return sideMenuButtonTitles;
	}

}
