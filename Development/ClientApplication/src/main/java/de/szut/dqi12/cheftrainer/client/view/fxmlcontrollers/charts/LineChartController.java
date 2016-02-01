package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.charts;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.GridPane;

public class LineChartController<Y, X> {
	final NumberAxis xAxis = new NumberAxis();
	final NumberAxis yAxis = new NumberAxis();
	@FXML
	private GridPane lineChart;
	private ObservableList<XYChart.Series<String, Integer>> data;
	private LineChart<String, Integer> chart;

	public void init() {

		data = FXCollections.observableArrayList();

		chart = (LineChart) lineChart.getChildren().get(0);
	}

	public LineChart<String, Integer> getChart() {
		return chart;
	}

	public void setTitle(String name) {
		chart.setTitle(name);
	}

	public void setData(HashMap<String, Integer> data) {
		this.chart.getData().clear();
		Series<String, Integer> series = new Series<String, Integer>();
		for (String key : data.keySet()) {

			series.getData().add(new XYChart.Data<String, Integer>(key, data.get(key)));
		}

		try {
			this.data.clear();
		} catch (Exception e) {
		}
		this.data.add(series);
		this.chart.setData(this.data);

	}
	/**
	 * Generates an graph and adds him to the global data.
	 * @param data data of the graph.
	 */
	public void addSeries(HashMap<String, Integer> data) {
		Series<String, Integer> series = new Series<String, Integer>();
		for (String key : data.keySet()) {
			series.getData().add(new XYChart.Data(key, data.get(key)));
		}
		this.data.add(series);
		this.chart.getData().clear();
		this.chart.setData(this.data);

	}

	/**
	 * Adds an value to an graph
	 * @param xValue value for x-axis
	 * @param yValue value for y-axis
	 * @param series Position of Series in ArrayList
	 */
	public void addValue(String xValue, int yValue, int series) {

		try {
			if (this.chart.getData().size() == 0) {
				this.chart.getData().add(new Series<String, Integer>());
			}
			this.chart.getData().get(series).getData().add(new XYChart.Data(xValue, yValue));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}