package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FormationFactory {

	public static final String FOUR_FOUR_TWO = "4-4-2";
	public static final String FOUR_FIVE_ONE = "4-5-1";

	public Formation getFormation(String formation) {
		String[] sF = formation.split("-");
		return new Formation(formation, Integer.valueOf(sF[0]),
				Integer.valueOf(sF[1]), Integer.valueOf(sF[2]));
	}
	
	public List<Formation> getFormations() {
		List<Formation> retval = new ArrayList<Formation>();
		Field[] formations = FormationFactory.class.getFields();
		for (Field f : formations) {
			String formationName;
			try {
				formationName = (String) f.get(this);
				retval.add(getFormation(formationName));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return retval;
	}
}
