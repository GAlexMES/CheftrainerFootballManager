package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import org.apache.log4j.chainsaw.Main;

import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.charts.BarChartController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.charts.LineChartController;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

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
//		//HIER SOLLTEN NICHT DIE AKTUELLEN PUNKTE DER MANAGER STEHEN SONDERN DER VERLAUF DER PUNKTE
//		Session s = Controller.getInstance().getSession();
//		ArrayList<Manager> managers = (ArrayList<Manager>) s.getCommunities().get(s.getCurrentCommunity()).getManagers();
//		//FALSCHE DATEN
//		HashMap<String, Integer> data = new HashMap<String, Integer>();
//		for(Manager m : managers){
//			data.put(m.getName(), m.getPoints());
//		}
//		//FALSCHE DATEN ENDE
//		
//		lineController.setData(data);
//		stats.getChildren().set(0, lineController.getChart());

	}
	/**
	 * Fills the BarChart with data
	 */
	public void setBarChart() {
//		Session s = Controller.getInstance().getSession();
//		ArrayList<Manager> managers = (ArrayList<Manager>) s.getCommunities().get(s.getCurrentCommunity()).getManagers();
//		
//		HashMap<String, Integer> data = new HashMap<String, Integer>();
//		for(Manager m : managers){
//			data.put(m.getName(), m.getPoints());
//		}
//	
//		stats.getChildren().set(0, barController.getChart());
//		barController.setData(data);

	}

}
