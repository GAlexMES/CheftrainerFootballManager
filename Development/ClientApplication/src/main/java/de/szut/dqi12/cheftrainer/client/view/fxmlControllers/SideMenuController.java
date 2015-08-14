package de.szut.dqi12.cheftrainer.client.view.fxmlControllers;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

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
import javafx.event.ActionEvent;
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
		generateButtons(((VBox) rLayout.lookup("#sideMenu")));
	}

	@SuppressWarnings("static-access")
	private void generateButtons(VBox box) {
		try {
			Path buttonDefinitionFile = Paths.get(MainApp.class.getResource(
					"/definitions/sideMenuButtons.xml").toURI());
			
			List<String> xmlLines = Files.readAllLines(buttonDefinitionFile);
			List<Element> buttonList = parseXMLButtons(xmlLines);
			List<Button> buttons = new ArrayList<Button>();
			
			for (Element e : buttonList) {
				Button tempButton = generateButtonFromXML(e);
				buttons.add(tempButton);
			}
			
			box.getChildren().addAll(buttons);

			for (Node n : box.getChildren()) {
				box.setVgrow(((Button) n), Priority.ALWAYS);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private Button generateButtonFromXML(Element e){
		Button tempButton = new Button(e.getChildText("text"));
		Image image = new Image(
				MainApp.class.getResourceAsStream("/images/"
						+ e.getChildText("imageName")));
		tempButton.setGraphic(new ImageView(image));
		tempButton.setPrefHeight(250.0);
		tempButton.setMnemonicParsing(false);
		tempButton.setAlignment(Pos.BASELINE_LEFT);
		tempButton.setPrefWidth(expandedWidth);
		tempButton.setStyle("button");

		if (e.getChildText("triggerButton").equals("true")) {
			tempButton.setOnAction(this::triggerSideMenu);
		}
		else{
			tempButton.setOnAction(this::buttonPressed);
		}
		return tempButton;
	}

	private List<Element> parseXMLButtons(List<String> xmlStringList) {
		String xmlString = "";
		for (String s : xmlStringList) {
			xmlString += s + "\t";
		}

		SAXBuilder saxBuilder = new SAXBuilder();
		List<Element> nodeList = new ArrayList();
		try {
			Document doc = saxBuilder.build(new StringReader(xmlString));
			nodeList = doc.getRootElement().getChildren();
		} catch (JDOMException e) {
		} catch (IOException e) {
		}
		return nodeList;
	}

	@FXML
	public void buttonPressed(ActionEvent evt) {
		String fileName = "CommunitiesFrame.fxml";
		GUIController.getInstance().setContentFrameByName(fileName);
	}

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
		contentColoum.setPrefWidth(rLayout.getWidth()
				- ((VBox) rLayout.lookup("#sideMenu")).getWidth());
		contentColoum.setMinWidth(rLayout.getWidth()
				- ((VBox) rLayout.lookup("#sideMenu")).getWidth());
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
		contentColoum.setPrefWidth(rLayout.getWidth() - expandedWidth - 100);
		contentColoum.setMinWidth(rLayout.getWidth() - expandedWidth - 100);
	}

	public List<String> getSideMenuButtonTitles() {
		return sideMenuButtonTitles;
	}

}