package de.szut.dqi12.cheftrainer.connectorlib.callables;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;

/**
 * The CallacleController is used to map IDs to new instances of classes, which extends CallableAbstact.
 * @author Alexander Brennecke
 *
 */
public class CallableController {

	private static URL FILE_PATH;
	private static String packagePath="";
	private static HashMap<String, CallableAbstract> retval;

	/**
	 * Tries to find a .class file in the given director for each String in fieldList. The .class file must have the same name than the String in the filedList. 
	 * @param fieldList List of IDs/Classnames 
	 * @param pathToCallableDir Path to the director in which the classes are saved
	 * @param packagePathToCallableDir package name/path to the classed in the director
	 * @return A HashMap, which maps every ID in fieldList to a new instance of a class, which extends CallableAbstract.
	 */
	public static HashMap<String, CallableAbstract> getInstancesForIDs(
			List<String> fieldList, URL pathToCallableDir,
			String packagePathToCallableDir) {
		//resets the packagePath from last use
		packagePath = "";
		
		FILE_PATH = pathToCallableDir;
		
		//removes the last part of the package path
		String[] packagePathSplitted = packagePathToCallableDir.split("\\.");
		for (int i=0; i<packagePathSplitted.length-1;i++) {
			packagePath += packagePathSplitted[i]+".";
		}
		
		retval = new HashMap<String, CallableAbstract>();
		
		//iterates over the fieldList
		fieldList.forEach(element -> mapClassToID(element));
		return retval;

	}

	
	/**
	 * Calls the generateInstance() method and maps the return value to the given messageID
	 * @param messageID messageID and name of the class, which should be mapped to each other
	 */
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

	/**
	 * Uses a URLClassLoader and generates a new instance of "className".class with it and returns it.
	 * @param path the URL to the folder, which includes the "className".class file
	 * @param className the name of the class, which is located in the path folder
	 * @return a new instance of the "className".class file
	 * @throws CallableMappingException is thrown when the newInstance() function doens't return a valid instance.
	 */
	private static CallableAbstract generateInstance(URL path, String className) throws CallableMappingException{
		// initializes
		CallableAbstract classInstance = null;
		URLClassLoader cl = null;
		try {

			// new URL[] with the given URL inside and a new URLClassLoader instance
			URL[] classLoaderUrls = new URL[] { path };
			cl = new URLClassLoader(classLoaderUrls);

			// creates the fullyQualifiedName and a new Class<CallableAbstact> with the ClassLoader
			String fullQualifiedName = packagePath + className;
			@SuppressWarnings("unchecked")
			Class<CallableAbstract> c = (Class<CallableAbstract>) cl
					.loadClass(fullQualifiedName);
			
			// creates a new instance for the class
			classInstance = c.newInstance();
			if(classInstance == null){
				throw new CallableMappingException("The class: "+className+"\n "
						+ "doesn't return a valid instance of itself. Pleas override the newInstance() function correct!");
			}
		} catch (ClassNotFoundException cne) {
			throw new CallableMappingException("The class: "+className+"  Was not found in: "+path.toString());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		finally{
			// tries to close the ClassLoader
			try {
				cl.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch (NullPointerException npe){
				//nothing happens then
			}
		}
		return classInstance;
	}

}
