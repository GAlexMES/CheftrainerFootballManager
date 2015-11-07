package de.szut.dqi12.cheftrainer.client.images;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.scene.image.Image;
import de.szut.dqi12.cheftrainer.client.ClientApplication;
import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;

public class ImageController {

	private final static String DIR_PATH = ClientApplication.class.getResource(
			"../../../../../images/LoadedImages/").toString();
	private final static String LOADED_IMAGE_DIR = DIR_PATH + "LoadedImages/";
	private final static String DUMMY_IMG = DIR_PATH + "dummy.png";

	public Image getPicture(Player p) {
		String path = getPath(p);
		return new Image(path);
	}

	private String getPath(Player p) {
		String url = p.getAbsolutePictureURL();
		String picturePath = getPicturePath(url);

		File imageFile = new File(picturePath);
		if (imageFile.exists() && !imageFile.isDirectory()) {
			return picturePath;
		} else {
			try {
				Thread t = new Thread(new ImageLoader(url, picturePath));
				t.run();
			} catch (MalformedURLException mue) {
				System.out.println("invalid url");
			}
		}
		return DUMMY_IMG;
	}

	private String getPicturePath(String path) {
		String[] splittedPath = path.split("/");
		String fileName = splittedPath[splittedPath.length];
		return LOADED_IMAGE_DIR + fileName;
	}

	private class ImageLoader implements Runnable {

		private URL imageURL;
		private File destinationFile;

		public ImageLoader(String url, File destinationFile)
				throws MalformedURLException {
			imageURL = new URL(url);
			this.destinationFile = destinationFile;
		}

		@Override
		public void run() {
			InputStream is;
			OutputStream os;
			try {
				is = imageURL.openStream();
				os = new FileOutputStream(destinationFile);

				byte[] b = new byte[2048];
				int length;

				while ((length = is.read(b)) != -1) {
					os.write(b, 0, length);
				}
				is.close();
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				
			}

		}

	}

}
