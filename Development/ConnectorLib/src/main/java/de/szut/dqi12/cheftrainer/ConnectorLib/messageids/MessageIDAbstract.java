package de.szut.dqi12.cheftrainer.connectorlib.messageids;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Every class, which contains IDs for  ID<->CallableAbstract Mapping needs to extends this class.
 * @author Alexander Brennecke
 *
 */
public abstract class MessageIDAbstract {
	
	/**
	 * 
	 * @return a List with all Strings in this class
	 */
	public List<String> getIDs(){
		Field[] fields = ServerToClient_MessageIDs.class.getFields();
		List<Field> fieldList = Arrays.asList(fields);
		List<String> idList = new ArrayList<String>();
		for(Field f : fieldList){
			try {
				idList.add(((String)f.get(this)));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return idList;
	}

}
