package de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.dialogcontrollers;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import de.szut.dqi12.cheftrainer.client.guicontrolling.AlertDialog;
import de.szut.dqi12.cheftrainer.client.view.utils.DialogUtils;

public class CreateCommunityController {

	@FXML
	TextField communityNameField;
	@FXML
	PasswordField passwordField;
	@FXML
	PasswordField passwordConfirmationField;
	
	@FXML
	public void cancle(){
		Stage stage = (Stage)communityNameField.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	public void create(){
		TextField[] inputFields = {communityNameField,passwordField,passwordConfirmationField};
		List<String> errorList = DialogUtils.checkInputs(inputFields);
		
		
		if (!(passwordField.getText().equals(passwordConfirmationField
				.getText()))) {
			passwordField.setText("");
			passwordConfirmationField.setText("");
			passwordField.setStyle("-fx-text-box-border: red;");
			passwordConfirmationField.setStyle("-fx-text-box-border: red;");
			errorList.add("Passwords are not the same");
		}
		
		if (errorList.size() == 0) {
			
		} else {
			String errorMessage = AlertDialog.WRONG_INPUTS;
			for (String s : errorList) {
				errorMessage += "\n " + s;
			}
			DialogUtils.showError("Creation failed",
					"Something went wrong during the community creation",
					errorMessage);
		}
	}
}
