package de.szut.dqi12.cheftrainer.connectorlib.callables;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;

public class CallableController {

	private static URL FILE_PATH;
	private static String packagePath="";
	private static HashMap<String, CallableAbstract> retval;

	public static HashMap<String, CallableAbstract> getInstancesForIDs(
			List<String> fieldList, URL pathToCallableDir,
			String packagePathToCallableDir) {
		packagePath = "";
		FILE_PATH = pathToCallableDir;
		String[] packagePathSplitted = packagePathToCallableDir.split("\\.");
		for (int i=0; i<packagePathSplitted.length-1;i++) {
			packagePath += packagePathSplitted[i]+".";
		}
		retval = new HashMap<String, CallableAbstract>();
		fieldList.forEach(element -> mapClassToID(element));
		return retval;

	}

	private static void mapClassToID(String messageID) {
		try {
			URL urlPath = new URL(FILE_PATH, messageID + ".class");
			CallableAbstract tempCallable = generateInstance(urlPath, messageID);
			retval.put(messageID, tempCallable);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (CallableMappingException e) {
			e.printStackTrace();
		}
	}

	private static CallableAbstract generateInstance(URL path, String className) throws CallableMappingException{
		CallableAbstract classInstance = null;
		try {

			URL[] classLoaderUrls = new URL[] { path };
			URLClassLoader cl = new URLClassLoader(classLoaderUrls);

			String fullQualifiedName = packagePath + className;
			@SuppressWarnings("unchecked")
			Class<CallableAbstract> c = (Class<CallableAbstract>) cl
					.loadClass(fullQualifiedName);
			classInstance = c.newInstance();
			cl.close();
		} catch (ClassNotFoundException cne) {
			throw new CallableMappingException("The class: "+className+"  Was not found in: "+path.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return classInstance;
	}

}
