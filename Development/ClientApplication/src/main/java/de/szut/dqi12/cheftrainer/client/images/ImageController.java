package de.szut.dqi12.cheftrainer.client.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import de.szut.dqi12.cheftrainer.client.ClientApplication;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;

/**
 * Loads an required Image for an ImageUpdate.
 */
public class ImageController {

	private final static String DIR_PATH = ClientApplication.class.getResource(
			"../../../../../images/").toString().substring(6);
	private final static String LOADED_IMAGE_DIR = DIR_PATH + "LoadedImages/";
	private final static String DUMMY_IMG = DIR_PATH + "dummy.png";
	private ImageUpdate imageUpdate;
	
	/**
	 * @param iu ImageUpdate, which needs an Picture to load.
	 */
	public ImageController(ImageUpdate iu){
		this.imageUpdate = iu;
	}
	/**
	 * Loads an Picture of an Player
	 * @param p Player, whos Picture is needed.
	 * @return A Picture of the Player
	 */
	public Image getPicture(Player p) {
		String path = getPath(p);
		File imageFile = new File(path);
		return new Image(imageFile.toURI().toString());
	}

	/**
	 * Loads the Path of an Picture for an Player.
	 * @param p The Player, which Path of the Picture is needed.
	 * @return The Path of the Picture.
	 */
	private String getPath(Player p) {
		String url = p.getAbsolutePictureURL();
		String picturePath = getPicturePath(url);

		File imageFile = new File(picturePath);
		if (imageFile.exists() && !imageFile.isDirectory()) {
			return picturePath;
		} else {
			try {
				imageFile.getParentFile().mkdirs();
				Thread t = new Thread(new ImageLoader(url, picturePath, p.getSportalID()));
				t.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return DUMMY_IMG;
	}

	/**
	 * Loads an Picture
	 * @param path Path of the Picture
	 * @return Picture of the Path
	 */
	private String getPicturePath(String path) {
		String[] splittedPath = path.split("/");
		String fileName = splittedPath[splittedPath.length - 1];
		return LOADED_IMAGE_DIR + fileName;
	}

	/**
	 * Loads an Image
	 */
	private class ImageLoader implements Runnable {

		private URL imageURL;
		private int id;
		private String destinationFile;
		
		/**
		 * @param url URL of the Picture
		 * @param destinationFile File to save the Picture in
		 * @param id Id of the Player
		 * @throws MalformedURLException
		 */
		public ImageLoader(String url, String destinationFile, int id)
				throws MalformedURLException {
			imageURL = new URL(url);
			this.destinationFile = destinationFile;
			this.id =id;
		}

		@Override
		public void run() {
			try {
	            BufferedImage image = ImageIO.read(imageURL);
	            ImageIO.write(image, "jpg",new File(destinationFile));
	            
	            File imageFile = new File(destinationFile);
	    		Image updateImage = new Image(imageFile.toURI().toString());
				imageUpdate.updateImage(updateImage, id);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

			}
		}
	}
}
