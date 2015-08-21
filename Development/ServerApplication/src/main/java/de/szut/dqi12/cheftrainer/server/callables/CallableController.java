package de.szut.dqi12.cheftrainer.server.callables;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

public class CallableController {

	public CallableController(MessageController messageController, ClientToServer_MessageIDs messageIDs){
		Field[] fields = messageIDs.class.getFields();
		
		String filePath = CallableController.class.getResource(".").getPath();
		
		for(Field field : fields){
			URL url = new URL(filePath+field.toString()+".class");
			URLClassLoader cl = new URLClassLoader(new URL[]{url});
			Class<CallableInterface> class = cl.loadClass(field.toString());
			CallableInterface classInstance = class.newInstance();
		}
	}
}
