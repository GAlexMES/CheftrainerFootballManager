package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import org.apache.log4j.chainsaw.Main;

import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.charts.BarChartController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.charts.LineChartController;

/**
 * This is the controller for the different charts.
 * @author Robin
 *
 */
public class StatisticsController {

	private BarChartController barController;
	private LineChartController<String, Integer> lineController;

	@FXML
	private GridPane stats;

	/**
	 * This method have to be called before all other methods
	 * Initialization of gui-components
	 */
	public void init() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("stats/BarChart.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		barController = loader.getController();
		barController.init();
		loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("stats/LineChart.fxml"));
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lineController = loader.getController();
		lineController.init();
	}

	/**
	 * Fills the LineChart with data
	 */
	public void setLineChart() {

		// Muell Anfang
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("Robin", 234);
		map.put("Alex", 224);
		map.put("Hans", 214);
		map.put("Dieter", 14);

		// Muell ende

		//getData()
		//Daten parsen und in eine map schmeisen
		
		lineController.setData(map);
		stats.getChildren().set(0, lineController.getChart());

	}
	/**
	 * Fills the BarChart with data
	 */
	public void setBarChart() {
		// Muell Anfang
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("Robin", 234);
		map.put("Alex", 224);
		map.put("Hans", 214);
		map.put("Dieter", 14);

		// Muell ende
		
		
		
		//getData()
		//Daten parsen und in eine map schmeisen
		stats.getChildren().set(0, barController.getChart());
		barController.setData(map);

	}

}
