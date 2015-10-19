package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FormationFactory {

	public static final String FOUR_FOUR_TWO = "4-4-2";
	public static final String FOUR_FIVE_ONE = "4-5-1";
	
	private List<String> formationsList;
	
	public FormationFactory(){
		formationsList = new ArrayList<>();
		createFormationList();
	}

	public Formation getFormation(String formation) {
		String[] sF = formation.split("-");
		return new Formation(formation, Integer.valueOf(sF[0]),
				Integer.valueOf(sF[1]), Integer.valueOf(sF[2]));
	}
	
	public Formation getFormation(int defenders, int middfielders,
			int offensives) throws IOException{
		String formationName = defenders+"-"+middfielders+"-"+offensives;
		if(formationsList.contains(formationName)){
			return getFormation(formationName);
		}
		else{
			throw new IOException("Invalid formation '"+formationName+"'!");
		}
	}
	
	public void createFormationList(){
		Field[] formations = FormationFactory.class.getFields();
		for (Field f : formations) {
			String formationName;
			try {
				formationName = (String) f.get(this);
				formationsList.add(formationName);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public List<String> getFormationsList() {
		return formationsList;
	}
	
	public List<Formation> getFormations() {
		List<Formation> retval = new ArrayList<Formation>();
		for (String s : formationsList) {
			retval.add(getFormation(s));
		}
		return retval;
	}
}
