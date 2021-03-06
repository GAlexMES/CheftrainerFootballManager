package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.charts;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;

/**
 * Controller for the FXML-Component of an BarChart.
 * @author Robin
 *
 */
public class BarChartController {
	@FXML
	private GridPane barChart;
	private BarChart<String, Integer> chart;
	private XYChart.Series<String, Integer> series;

	public BarChartController() {
		series = new XYChart.Series<>();
	}

	public BarChart<String, Integer> getChart() {
		return chart;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init() {

		chart = (BarChart) barChart.getChildren().get(0);
		ObservableList<XYChart.Series<String, Integer>> data = FXCollections.observableArrayList();
		data.add(series);
		chart.setData(data);
	}

	/**
	 * Fills the Chart with Data.
	 * @param data Data for the Chart.
	 */
	public void setData(HashMap<String, Integer> data) {
		try {
			series.getData().clear();
		} catch (NullPointerException e) {
		}
		for (String key : data.keySet()) {
			series.getData().add(new XYChart.Data<>(key, data.get(key)));
		}
	}

}
