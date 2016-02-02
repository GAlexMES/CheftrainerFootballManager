package de.szut.dqi12.cheftrainer.client.listeners;

import de.szut.dqi12.cheftrainer.client.guicontrolling.ControllerInterface;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Listener for the Enter key.
 */
public class EnterPressedListener implements EventHandler<KeyEvent> {
	
	private ControllerInterface controller;
	
	/**
	 * @param controller Controller of FXML Component
	 */
	public EnterPressedListener(ControllerInterface controller) {
		this.controller = controller;
	}

	@Override
	public void handle(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.ENTER)) {
			controller.enterPressed();
		}
	}
}
