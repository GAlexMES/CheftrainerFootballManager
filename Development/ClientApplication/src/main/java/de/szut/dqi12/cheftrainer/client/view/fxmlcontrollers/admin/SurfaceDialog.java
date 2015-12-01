package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.admin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

public class SurfaceDialog extends TableView<String>{
	
	private ListView<String> list;
	private String leauge;
	
	public SurfaceDialog(){
		super();
		list = new ListView<String>();
		ObservableList<String> data =FXCollections.observableArrayList (
		    "show leauge", "delete leauge", "add Player", "Change Properties");
		this.setItems(data);
		this.addListener();
	}
	
	private void addListener(){
		list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				if(arg2.equals(list.getItems().get(0))){
					//show leauge
					//Open AdminInterdace
				}else if(arg2.equals(list.getItems().get(1))){
					//delte leauge
				}else if(arg2.equals(list.getItems().get(2))){
					//add player to leauge
				}else if(arg2.equals(list.getItems().get(3))){
					//Change Properties
				}
			}
			
		});
	}
	
	public void showDialog(Boolean visible, String leauge){
		this.leauge = leauge;
		
	}
	

}
