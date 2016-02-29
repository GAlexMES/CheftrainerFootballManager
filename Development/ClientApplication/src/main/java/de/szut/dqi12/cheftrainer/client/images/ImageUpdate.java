package de.szut.dqi12.cheftrainer.client.images;

import javafx.scene.image.Image;

/**
 * An Image which is updateable with an new Image.
 * This could be used for An Image which is required but needs time to load.
 */
public interface ImageUpdate {
	/**
	 * Updates The Image of an Player with an new loaded Image
	 * @param image The new Image
	 * @param id ID of a Player
	 */
	public void updateImage(Image image, int id);
}
