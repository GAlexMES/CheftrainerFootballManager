package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;

public class BarChartController {
	@FXML
	GridPane barChart;
	BarChart<String, Integer> chart;
	XYChart.Series<String, Integer> series;
	
	public BarChartController(){
		series = new XYChart.Series<>();	
	}
	
	public void init() {

		chart = (BarChart) barChart.getChildren().get(0);
		ObservableList<XYChart.Series<String, Integer>> data = FXCollections.observableArrayList();
		data.add(series);
		chart.setData(data);
	}
	
	public void setData(HashMap<String, Integer> data){
		try{
			series.getData().clear();
		}catch(NullPointerException e){
		}
		for(String key : data.keySet()){
			series.getData().add(new XYChart.Data<>(key, data.get(key)));
		}
	}

}
