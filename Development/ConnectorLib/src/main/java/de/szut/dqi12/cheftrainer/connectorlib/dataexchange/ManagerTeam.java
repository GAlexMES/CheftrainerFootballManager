package de.szut.dqi12.cheftrainer.connectorlib.dataexchange;

import java.text.NumberFormat;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ManagerTeam {
	private final StringProperty communityName;
	private final StringProperty wertDesTeams;
	private final StringProperty plazierung;

	public ManagerTeam(String communityName, double wertDesTeams,
			String plazierung) {
		super();
		this.communityName =  new SimpleStringProperty(communityName);
		this.wertDesTeams =  new SimpleStringProperty(formatDouble(wertDesTeams)+"€");
		this.plazierung =  new SimpleStringProperty(plazierung);
	}
	
	

	public StringProperty getCommunityName() {
		return communityName;
	}

	public StringProperty getWertDesTeams() {
		return wertDesTeams;
	}

	public StringProperty getPlazierung() {
		return plazierung;
	}

	private String formatDouble(Double d){
		NumberFormat f = NumberFormat.getInstance();
		f.setGroupingUsed(false);
		return f.format(d);
	}
}