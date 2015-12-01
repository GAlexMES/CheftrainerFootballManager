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

public class ImageController {

	private final static String DIR_PATH = ClientApplication.class.getResource(
			"../../../../../images/").toString().substring(6);
	private final static String LOADED_IMAGE_DIR = DIR_PATH + "LoadedImages/";
	private final static String DUMMY_IMG = DIR_PATH + "dummy.png";
	private ImageUpdate imageUpdate;
	
	public ImageController(ImageUpdate iu){
		this.imageUpdate = iu;
	}

	public Image getPicture(Player p) {
		String path = getPath(p);
		File imageFile = new File(path);
		return new Image(imageFile.toURI().toString());
	}

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

	private String getPicturePath(String path) {
		String[] splittedPath = path.split("/");
		String fileName = splittedPath[splittedPath.length - 1];
		return LOADED_IMAGE_DIR + fileName;
	}

	private class ImageLoader implements Runnable {

		private URL imageURL;
		private int id;
		private String destinationFile;
		

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
