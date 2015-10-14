package de.szut.dqi12.cheftrainer.server.utils;

import java.util.HashMap;

import org.json.JSONObject;

import de.szut.dqi12.cheftrainer.connectorlib.dataexchange.Player;

/**
 * This class provides a few JSON method, which could be used to transform JSONObject etc. to something else. Or other way round.
 * @author Alexander Brennecke
 *
 */
public class JSONUtils {

	/**
	 * This class created a JSONObject out of the given HashMap.
	 * @param map the Map, that should be transformed to a JSON.
	 * @return a new JSONObject with the map information inside.
	 */
	public static JSONObject mapToJSON(HashMap<String,Boolean> map) {
        JSONObject retval = new JSONObject();
        
        for(String key : map.keySet() ){
            boolean value = map.get(key); 
            retval.put(key, value);

        }
        return retval;
    }
}
