package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlayerLabel extends Label {
	private int id;
	private Image image;
	private String position;

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getPlayerId() {
		return this.id;
	}

	public void setPlayerId(int id) {
		this.id = id;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.setGraphic(new ImageView(image));
		this.image = image;
	}

}
