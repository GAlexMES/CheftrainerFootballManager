package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

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
	GridPane lineChart;
	ObservableList<XYChart.Series<String, Integer>> data;
	LineChart<String, Integer> chart;

	public void init() {

		data = FXCollections.observableArrayList();

		chart = (LineChart) lineChart.getChildren().get(0);
	}
	
	public void setTitle(String name){
		chart.setTitle(name);
	}

	public void setData(HashMap<String, Integer> data) {
		Series<String, Integer> series = new Series<String, Integer>();
		for (String key : data.keySet()) {
				
				series.getData().add(new XYChart.Data<String, Integer>(key, data.get(key)));
		}
		
		try{			
			this.data.clear();
		}catch(Exception e){
//			e.printStackTrace();
		}
		this.data.add(series);
		this.chart.getData().clear();
		this.chart.setData(this.data);

	}

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
 * 
 * @param xValue Wert fuer X-Achse
 * @param yValue Wert fuer Y-Achse
 * @param series Die Position des Graphes (0 fuer ersten Graph)
 */
	public void addValue(String xValue, int yValue, int series) {

		try {
			this.chart.getData().get(series).getData()
					.add(new XYChart.Data(xValue, yValue));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
