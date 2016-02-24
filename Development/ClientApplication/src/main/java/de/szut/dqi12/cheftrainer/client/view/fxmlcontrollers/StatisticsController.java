package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import de.szut.dqi12.cheftrainer.client.Controller;
import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.charts.BarChartController;
import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.charts.LineChartController;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Manager;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Session;

/**
 * This is the controller for the different charts.
 * 
 * @author Robin
 *
 */
public class StatisticsController implements ControllerInterface {

	private BarChartController barController;
	private LineChartController<String, Integer> lineController;

	@FXML
	private GridPane stats;

	/**
	 * This method have to be called before all other methods Initialization of
	 * gui-components
	 */
	@Override
	public void init() {
		FXMLLoader loader = new FXMLLoader();
		ClassLoader classLoader = getClass().getClassLoader();
		loader.setLocation(classLoader.getResource("sourcesFXML/BarChart.fxml"));
		try {
			loader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		barController = loader.getController();
		barController.init();
		loader = new FXMLLoader();
		loader.setLocation(classLoader.getResource("sourcesFXML/LineChart.fxml"));
		try {
			loader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		lineController = loader.getController();
		lineController.init();
		setBarChart();
	}

	/**
	 * Fills the LineChart with data. The data contains the history of the points matchday day of the current manager.
	 */
	@FXML
	public void setLineChart() {
		Session s = Controller.getInstance().getSession();
		HashMap<String, Integer> data = new HashMap<String, Integer>();

		try{
		HashMap<Integer, Integer> history = (HashMap<Integer, Integer>) s.getCurrentManager().getHistory();
		for (Integer day : history.keySet()) {
			data.put(String.valueOf(day), history.get(day));
		}
		}catch(NullPointerException e){
			
		}

		lineController.setData(data);
		stats.getChildren().set(0, lineController.getChart());

	}

	/**
	 * Fills the BarChart with data. The data contains the points of every manager in the current cummunity.
	 */
	@FXML
	public void setBarChart() {
		Session s = Controller.getInstance().getSession();
		ArrayList<Manager> managers = (ArrayList<Manager>) s.getCurrentCommunity().getManagers();

		HashMap<String, Integer> data = new HashMap<String, Integer>();
		for (Manager m : managers) {
			data.put(m.getName(), m.getPoints());
		}
		
		barController.setData(data);
		stats.getChildren().set(0, barController.getChart());

	}

	@Override
	public void enterPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(Boolean flag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializationFinihed(Scene scene) {
		// TODO Auto-generated method stub
		
	}

}
