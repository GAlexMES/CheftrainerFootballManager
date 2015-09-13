package de.szut.dqi12.cheftrainer.utils;

import java.util.HashMap;

import org.json.JSONObject;

public class JSONUtils {

	
	public static JSONObject mapToJSON(HashMap<String,Boolean> map) {
        JSONObject retval = new JSONObject();
        
        for(String key : map.keySet() ){
            boolean value = map.get(key); 
            retval.put(key, value);

        }

        System.out.println(random(5,7));
        return retval;
    }
	 public static <T> T random( T m, T n )
	  {
	    return Math.random() > 0.5 ? m : n;
	  }
}
