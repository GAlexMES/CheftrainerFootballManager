package de.szut.dqi12.cheftrainer.server.database;

import java.util.Map;

public interface DatabaseInterface {

	
	
	
	public Map<String, Float> getNewPoints();
	
	public boolean updateMoney(String player, String group, Double change);
	
	public boolean updatePlayerPoints(Map<String, Float> values);
	
	public boolean updateUserPonts(Map<String, Float> values);
	
	public boolean updateLineUp(String user, Map<String, String> players);
	
	public Map<String, Map> initGame();
	
	public Map<String, Double> getMarktwert(String player);
}
