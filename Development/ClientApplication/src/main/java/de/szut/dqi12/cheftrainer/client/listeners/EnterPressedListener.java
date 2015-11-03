package de.szut.dqi12.cheftrainer.client.listeners;

import de.szut.dqi12.cheftrainer.client.view.fxmlcontrollers.ControllerInterface;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class EnterPressedListener implements EventHandler<KeyEvent> {
	
	private ControllerInterface controller;
	
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
