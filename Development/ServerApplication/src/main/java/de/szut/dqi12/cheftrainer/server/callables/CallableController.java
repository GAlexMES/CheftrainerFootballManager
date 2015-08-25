package de.szut.dqi12.cheftrainer.server.callables;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import de.szut.dqi12.cheftrainer.connectorlib.messages.CallableAbstract;
import de.szut.dqi12.cheftrainer.connectorlib.messages.ClientToServer_MessageIDs;
import de.szut.dqi12.cheftrainer.connectorlib.messages.MessageController;


public class CallableController {

	private URL FILE_PATH;
	private ClientToServer_MessageIDs messageIDs;
	private MessageController mesController;

	public CallableController(ClientToServer_MessageIDs messageIDs, MessageController mesController) {
		this.messageIDs = messageIDs;
		this.mesController = mesController;
		try {
			FILE_PATH = CallableController.class.getResource(".").toURI()
					.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Field[] fields = this.messageIDs.getClass().getFields();

		// String filePath =
		// CallableController.class.getResource(".").getPath();
		List<Field> fieldList = Arrays.asList(fields);

		fieldList.forEach((Field element) -> mapClassToID(element));
	}

	private void mapClassToID(Field field) {
		try {
			String className = (String) field.get(messageIDs);
			URL urlPath = new URL(FILE_PATH, className + ".class");
			CallableAbstract tempCallable = generateInstance(field, urlPath, className);
			mesController.registerCallable(className, tempCallable);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private CallableAbstract generateInstance(Field field, URL path, String className) {
		CallableAbstract classInstance = null;
		try {

			URL[] classLoaderUrls = new URL[] { path };
			URLClassLoader cl = new URLClassLoader(classLoaderUrls);

			String packagePath = this.getClass().getName();
			packagePath = packagePath.replace(this.getClass().getSimpleName(),
					className);
			@SuppressWarnings("unchecked")
			Class<CallableAbstract> c = (Class<CallableAbstract>) cl
					.loadClass(packagePath);
			classInstance = c.newInstance();
			return classInstance;
		} catch (ClassNotFoundException
				| InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return classInstance;
	}
}
